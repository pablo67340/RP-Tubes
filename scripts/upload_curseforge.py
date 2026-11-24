#!/usr/bin/env python3
"""Upload RP-Tubes jars to CurseForge with the right Minecraft version,
modloader, Java version, and Client+Server environment tags.

Prerequisites:
  1. pip install -r requirements.txt          (just `requests`)
  2. An API token from https://authors.curseforge.com/account/api-tokens
     -> put it in the CURSEFORGE_TOKEN environment variable (or --token)
  3. Jars built: `gradlew build collectJar` puts the 1.19.2+ jars in
     build/libs/<mod_version>/ as rptubes-<mc>-<loader>-<mod_version>.jar, and the
     legacy ports (1.7.10, 1.12.2, 1.16.5) build into ports/<port>/build/libs/.
     `gradlew -p universal build` then packs them all into
     universal/build/libs/rptubes-<mod_version>.jar.

By default the one universal jar is uploaded, tagged with every version, loader
and Java in PORTS. --per-version uploads the separate jars instead, one file per
port. Only the newest section of the changelog file is sent.

The project ID defaults to RP-Tubes' own (1650052); pass --project-id only
if you are uploading somewhere else.

Usage, from the project root:
  python scripts/upload_curseforge.py
  python scripts/upload_curseforge.py --dry-run
  python scripts/upload_curseforge.py --per-version --loaders neoforge,fabric
  python scripts/upload_curseforge.py --per-version --only 1.20.1-neoforge,1.20.1-fabric
"""

import argparse
import json
import os
import re
import sys
from pathlib import Path

import requests

PROJECT_ROOT = Path(__file__).resolve().parent.parent
UPLOAD_API = "https://minecraft.curseforge.com/api"

# Baked in rather than read from CURSEFORGE_PROJECT_ID, because that variable is
# shared with the other mod projects and the wrong value uploads to the wrong page.
DEFAULT_PROJECT_ID = "1650052"

# Minecraft version + loader + Java the jar targets. Client + server for all.
# `loader` must match the CurseForge Modloader tag name exactly; `also` lists other
# loaders the same jar runs on, and `also_mc` the other Minecraft versions in its range.
PORTS = [
    {"mc": "1.7.10",  "loader": "Forge",    "java": "Java 8"},
    {"mc": "1.12.2",  "loader": "Forge",    "java": "Java 8"},
    {"mc": "1.16.5",  "loader": "Forge",    "java": "Java 8", "also_mc": ["1.16.4"]},
    {"mc": "1.16.5",  "loader": "Fabric",   "java": "Java 17", "also_mc": ["1.16.4"]},
    {"mc": "1.19.2",  "loader": "Forge",    "java": "Java 17", "also_mc": ["1.19.1"]},
    # NeoForge 1.20.1 forked from Forge 47.1 and loads Forge 1.20.1 mods.
    {"mc": "1.20.1",  "loader": "Forge",    "java": "Java 17", "also": ["NeoForge"], "also_mc": ["1.20"]},
    {"mc": "1.20.1",  "loader": "Fabric",   "java": "Java 17", "also_mc": ["1.20"]},
    {"mc": "1.20.6",  "loader": "Forge",    "java": "Java 21"},
    {"mc": "1.20.6",  "loader": "NeoForge", "java": "Java 21"},
    {"mc": "1.20.6",  "loader": "Fabric",   "java": "Java 21", "also_mc": ["1.20.5"]},
    {"mc": "1.21.1",  "loader": "Forge",    "java": "Java 21", "also_mc": ["1.21"]},
    {"mc": "1.21.1",  "loader": "NeoForge", "java": "Java 21", "also_mc": ["1.21"]},
    {"mc": "1.21.1",  "loader": "Fabric",   "java": "Java 21", "also_mc": ["1.21"]},
    {"mc": "1.21.11", "loader": "Forge",    "java": "Java 21"},
    {"mc": "1.21.11", "loader": "NeoForge", "java": "Java 21"},
    {"mc": "1.21.11", "loader": "Fabric",   "java": "Java 21"},
    {"mc": "26.1.2",  "loader": "Forge",    "java": "Java 25", "also_mc": ["26.1", "26.1.1"]},
    {"mc": "26.1.2",  "loader": "NeoForge", "java": "Java 25"},
    {"mc": "26.1.2",  "loader": "Fabric",   "java": "Java 25", "also_mc": ["26.1", "26.1.1"]},
    {"mc": "26.2",    "loader": "Forge",    "java": "Java 25"},
    {"mc": "26.2",    "loader": "NeoForge", "java": "Java 25"},
    {"mc": "26.2",    "loader": "Fabric",   "java": "Java 25"},
]

ENV_TAGS = [("environment", "Client"), ("environment", "Server")]


def port_key(port: dict) -> str:
    return f"{port['mc']}-{port['loader'].lower()}"


def find_jar(port: dict, mod_version: str) -> Path | None:
    loader = port["loader"].lower()
    names = [f"rptubes-{port['mc']}-{loader}-{mod_version}.jar"]
    if port["loader"] == "Forge":
        names.append(f"rptubes-{port['mc']}-{mod_version}.jar")
    for folder in (PROJECT_ROOT / "build" / "libs" / mod_version, PROJECT_ROOT / "releases" / mod_version):
        for name in names:
            if (folder / name).is_file():
                return folder / name
    return None


def latest_section(changelog: str) -> str:
    """The changelog up to the second top-level heading."""
    parts = re.split(r"^(?=# )", changelog, flags=re.M)
    sections = [p for p in parts if p.startswith("# ")]
    return sections[0].strip() + "\n" if sections else changelog


def find_port_jar(port: dict, mod_version: str) -> Path | None:
    """Legacy ports build into ports/<mc>[-fabric]/build/libs/."""
    folder = port["mc"] + ("-fabric" if port["loader"] == "Fabric" else "")
    libs = PROJECT_ROOT / "ports" / folder / "build" / "libs"
    for name in (f"rptubes-{folder}-{mod_version}.jar", f"rptubes-{port['mc']}-{mod_version}.jar"):
        if (libs / name).is_file():
            return libs / name
    return None


def read_mod_version() -> str:
    text = (PROJECT_ROOT / "stonecutter.properties.toml").read_text(encoding="utf-8")
    match = re.search(r'^mod\.version\s*=\s*"([^"]+)"', text, re.M)
    if not match:
        sys.exit("mod.version not found in stonecutter.properties.toml")
    return match.group(1)


def fetch_version_ids(token: str) -> dict:
    """Map (category, display name) to numeric game-version IDs.

    The same display name can exist under several version types (e.g.
    "1.19.2" as a Minecraft version and under an addon category), and the
    upload API rejects IDs from the wrong type, so the type must be part of
    the lookup key.
    """
    headers = {"X-Api-Token": token}
    types_resp = requests.get(f"{UPLOAD_API}/game/version-types",
                              headers=headers, timeout=30)
    types_resp.raise_for_status()
    type_category = {}
    for vt in types_resp.json():
        name = vt["name"]
        # Minecraft version types are usually "Minecraft 1.19", but some are
        # bare version numbers like "26.2"
        if name.startswith("Minecraft") or re.fullmatch(r"\d+(\.\d+)*", name):
            type_category[vt["id"]] = "minecraft"
        elif name == "Java":
            type_category[vt["id"]] = "java"
        elif name in ("Modloader", "ModLoader"):
            type_category[vt["id"]] = "modloader"
        elif name == "Environment":
            type_category[vt["id"]] = "environment"

    resp = requests.get(f"{UPLOAD_API}/game/versions", headers=headers, timeout=30)
    resp.raise_for_status()
    ids = {}
    for entry in resp.json():
        category = type_category.get(entry["gameVersionTypeID"])
        if category is not None:
            ids[(category, entry["name"])] = entry["id"]
    return ids


def resolve_tags(tags: list, version_ids: dict) -> list:
    """tags is a list of (category, name) pairs."""
    resolved = []
    for category, name in tags:
        key = (category, name)
        if key not in version_ids:
            close = sorted(n for c, n in version_ids
                           if c == category and name.split()[0] in n)[:10]
            sys.exit(f"CurseForge has no {category} version named {name!r}.\n"
                     f"Closest available names in that category: {close}")
        resolved.append(version_ids[key])
    return resolved


def upload(args, jar: Path, display_name: str, tags: list, version_ids: dict,
           changelog: str) -> int | None:
    game_versions = resolve_tags(tags, version_ids)
    metadata = {
        "changelog": changelog,
        "changelogType": "markdown",
        "displayName": display_name,
        "gameVersions": game_versions,
        "releaseType": args.release_type,
    }
    print(f"\n{jar.name}  ({display_name})")
    print(f"  tags: {', '.join(n for _, n in tags)}  ->  ids {game_versions}")
    if args.dry_run:
        return None
    with open(jar, "rb") as fh:
        resp = requests.post(
            f"{UPLOAD_API}/projects/{args.project_id}/upload-file",
            headers={"X-Api-Token": args.token},
            data={"metadata": json.dumps(metadata)},
            files={"file": (jar.name, fh, "application/java-archive")},
            timeout=300,
        )
    if resp.status_code != 200:
        sys.exit(f"  UPLOAD FAILED ({resp.status_code}): {resp.text}")
    file_id = resp.json()["id"]
    print(f"  uploaded -> file id {file_id}")
    return file_id


def upload_universal(args, mod_version: str, changelog: str) -> None:
    jar = PROJECT_ROOT / "universal" / "build" / "libs" / f"rptubes-{mod_version}.jar"
    if not jar.is_file():
        sys.exit(f"Missing {jar}\nRun `gradlew -p universal build` first.")
    tags = []
    for port in PORTS:
        for tag in [("minecraft", v) for v in [port["mc"]] + port.get("also_mc", [])] + [("java", port["java"])] + \
                   [("modloader", l) for l in [port["loader"]] + port.get("also", [])]:
            if tag not in tags:
                tags.append(tag)
    tags += ENV_TAGS
    print("Resolving CurseForge game version IDs...")
    version_ids = fetch_version_ids(args.token)
    upload(args, jar, f"RP-Tubes {mod_version}", tags, version_ids, changelog)
    print("\nDry run complete - nothing uploaded." if args.dry_run else
          "\nDone. The file appears once CurseForge approval finishes.")


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    parser.add_argument("--project-id", default=DEFAULT_PROJECT_ID,
                        help=f"numeric CurseForge project id (default {DEFAULT_PROJECT_ID})")
    parser.add_argument("--token", default=os.environ.get("CURSEFORGE_TOKEN"),
                        help="CurseForge author API token")
    parser.add_argument("--mod-version", default=None,
                        help="defaults to mod.version from stonecutter.properties.toml")
    parser.add_argument("--changelog-file", default=str(PROJECT_ROOT / "CHANGELOG.md"))
    parser.add_argument("--release-type", default="release",
                        choices=["release", "beta", "alpha"])
    parser.add_argument("--per-version", action="store_true",
                        help="upload one jar per port instead of the universal jar")
    parser.add_argument("--only", default=None,
                        help="with --per-version: comma-separated keys (1.20.1 or 1.20.1-neoforge)")
    parser.add_argument("--loaders", default=None,
                        help="with --per-version: comma-separated loaders (forge,neoforge,fabric)")
    parser.add_argument("--dry-run", action="store_true",
                        help="resolve everything and show what would be uploaded")
    args = parser.parse_args()

    if not args.token:
        sys.exit("No API token: set CURSEFORGE_TOKEN or pass --token")
    if not args.project_id and not args.dry_run:
        sys.exit("No project id: set CURSEFORGE_PROJECT_ID or pass --project-id")

    mod_version = args.mod_version or read_mod_version()
    changelog = latest_section(Path(args.changelog_file).read_text(encoding="utf-8"))
    only = {s.strip().lower() for s in args.only.split(",")} if args.only else None
    loaders = ({s.strip().lower() for s in args.loaders.split(",")}
               if args.loaders else None)

    if not args.per_version:
        upload_universal(args, mod_version, changelog)
        return

    ports = []
    for port in PORTS:
        if loaders is not None and port["loader"].lower() not in loaders:
            continue
        key = port_key(port)
        if only is not None and port["mc"] not in only and key not in only:
            continue
        ports.append(port)

    if not ports:
        sys.exit("No ports matched --only / --loaders")

    # Verify all jars exist before touching the network
    for port in ports:
        port["jar"] = find_jar(port, mod_version) or find_port_jar(port, mod_version)
        if port["jar"] is None:
            sys.exit(f"Missing jar for {port_key(port)} {mod_version}\n"
                     f"Build and collect the release jars first.")

    print(f"Resolving CurseForge game version IDs...")
    version_ids = fetch_version_ids(args.token)

    uploaded = []
    for port in ports:
        tags = [("minecraft", v) for v in [port["mc"]] + port.get("also_mc", [])]
        tags += [("java", port["java"])]
        tags += [("modloader", l) for l in [port["loader"]] + port.get("also", [])]
        tags += ENV_TAGS
        file_id = upload(args, port["jar"], f"RP-Tubes {mod_version} (MC {port['mc']} {port['loader']})",
                         tags, version_ids, changelog)
        if file_id is not None:
            uploaded.append((port_key(port), file_id))

    if args.dry_run:
        print("\nDry run complete - nothing uploaded.")
    else:
        print(f"\nDone: {len(uploaded)} files uploaded. "
              f"They'll appear once CurseForge approval finishes.")


if __name__ == "__main__":
    main()
