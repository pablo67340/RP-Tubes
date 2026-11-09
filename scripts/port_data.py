#!/usr/bin/env python3
"""Keep the data files (recipes, tags, worldgen) of every port in the format its
loader and Minecraft version actually read.

The ports share the same recipes and tags, but each loader/version pair spells
them differently: recipe conditions, the common tag namespace (forge: or c:),
ingredient and result syntax, folder names, and where the tin ore biome
modifier lives. A file that is valid for one port is often silently ignored by
another, so hand-copying between ports is how the 1.3.1 recipe and worldgen
bugs happened. This script does the translation instead.

Usage, from the project root:
  python scripts/port_data.py check                 # report problems, change nothing
  python scripts/port_data.py fix                   # rewrite every port into its own format
  python scripts/port_data.py fix 1.20.6-neoforge   # only some ports
  python scripts/port_data.py copy rptubes/recipe/filter.json --from 1.21.1-neoforge
  python scripts/port_data.py copy "rptubes/recipe/*.json" --from 1.21.1-neoforge --dry-run

`copy` takes paths relative to the source port's src/main/resources/data and
writes the translated file into every other port (or only those in --to).
Edit a recipe or tag once, in whichever port you are testing, then copy it out.
"""

import argparse
import copy as copylib
import fnmatch
import json
import re
import sys
from dataclasses import dataclass
from pathlib import Path

PROJECT_ROOT = Path(__file__).resolve().parent.parent
PORTS_DIR = PROJECT_ROOT / "ports"
MOD_ID = "rptubes"

# Mod ids as Fabric spells them in fabric:all_mods_loaded.
FABRIC_MOD_IDS = {"projectred_core": "projectred-core"}

# Common tags RP Tubes fills in itself when the loader does not ship them:
# path -> (vanilla item, first Minecraft version that has the item).
VANILLA_COMMON_TAGS = {
    "ingots/iron": ("minecraft:iron_ingot", (1, 0)),
    "ingots/gold": ("minecraft:gold_ingot", (1, 0)),
    "ingots/copper": ("minecraft:copper_ingot", (1, 17)),
}
# Common tags that only exist when ProjectRed is installed; RP Tubes declares
# them with an optional entry so recipes referencing them always parse.
OPTIONAL_COMMON_TAGS = {
    "ingots/red_alloy": "projectred_core:red_ingot",
}

COOKING_TYPES = {"minecraft:smelting", "minecraft:blasting", "minecraft:smoking",
                 "minecraft:campfire_cooking"}
CONDITION_KEYS = ("conditions", "forge:condition", "neoforge:conditions",
                  "fabric:load_conditions")

# data/<ns>/<folder> names that lost their plural in 1.21.
PLURAL_FOLDERS = {
    "recipes": "recipe", "loot_tables": "loot_table", "advancements": "advancement",
    "structures": "structure", "functions": "function", "predicates": "predicate",
    "item_modifiers": "item_modifier",
}
PLURAL_TAG_FOLDERS = {"items": "item", "blocks": "block", "fluids": "fluid",
                      "entity_types": "entity_type", "functions": "function",
                      "game_events": "game_event"}


@dataclass(frozen=True)
class Port:
    name: str
    mc: tuple
    loader: str  # forge, neoforge, fabric

    @property
    def data_dir(self) -> Path:
        return PORTS_DIR / self.name / "src" / "main" / "resources" / "data"

    @property
    def resources_dir(self) -> Path:
        return PORTS_DIR / self.name / "src" / "main" / "resources"

    @property
    def neo_modern(self) -> bool:
        """NeoForge split from Forge's formats in 20.2; 1.20.1 NeoForge is still Forge."""
        return self.loader == "neoforge" and self.mc >= (1, 20, 2)

    @property
    def conditions(self):
        if self.loader == "fabric":
            return "fabric" if self.mc >= (1, 18) else None
        if self.neo_modern:
            return "neoforge"
        return "forge_single" if self.mc >= (1, 20, 5) else "forge_list"

    @property
    def common_ns(self) -> str:
        if self.loader == "fabric" or self.neo_modern:
            return "c"
        return "c" if self.mc >= (26,) else "forge"

    @property
    def loader_ships_vanilla_tags(self) -> bool:
        """Fabric only ships c:ingots/iron style tags from 1.20.5 on."""
        return self.loader != "fabric" or self.mc >= (1, 20, 5)

    @property
    def biome_modifier(self):
        """(folder under data/rptubes, type) or None where ore is added in Java."""
        if self.loader == "fabric" or self.mc < (1, 19):
            return None
        if self.neo_modern:
            return "neoforge", "neoforge:add_features"
        return "forge", "forge:add_features"

    @property
    def singular_folders(self) -> bool:
        return self.mc >= (1, 21)

    @property
    def string_ingredients(self) -> bool:
        return self.mc >= (1, 21, 2)

    @property
    def components(self) -> bool:
        """1.20.5 renamed result "item" to "id" and nested alloy ingredients."""
        return self.mc >= (1, 20, 5)

    def folder(self, name: str) -> str:
        singular = PLURAL_FOLDERS.get(name, name)
        if self.singular_folders:
            return singular
        plural = {v: k for k, v in PLURAL_FOLDERS.items()}
        return plural.get(singular, singular)

    def tag_folder(self, name: str) -> str:
        singular = PLURAL_TAG_FOLDERS.get(name, name)
        if self.singular_folders:
            return singular
        plural = {v: k for k, v in PLURAL_TAG_FOLDERS.items()}
        return plural.get(singular, singular)

    def tag(self, tag: str) -> str:
        ns, _, path = tag.partition(":")
        return f"{self.common_ns}:{path}" if ns in ("forge", "c") else tag


def all_ports() -> list:
    ports = []
    for d in sorted(PORTS_DIR.iterdir()):
        m = re.fullmatch(r"(\d+(?:\.\d+)*)(?:-(fabric|neoforge))?", d.name)
        if not m or not (d / "src" / "main" / "resources" / "data").is_dir():
            continue
        ports.append(Port(d.name, tuple(int(x) for x in m.group(1).split(".")),
                          m.group(2) or "forge"))
    return ports


def read_json(path: Path):
    return json.loads(path.read_text(encoding="utf-8-sig"))


def dump_json(data) -> str:
    return json.dumps(data, indent=2, ensure_ascii=False) + "\n"


# ---------------------------------------------------------------- recipes

def parse_condition(c: dict) -> dict:
    kind = (c.get("type") or c.get("condition", "")).split(":")[-1]
    if kind == "mod_loaded":
        return {"mod": c["modid"]}
    if kind == "all_mods_loaded":
        if len(c["values"]) != 1:
            raise ValueError("only single-mod conditions are supported")
        fabric_to_mod = {v: k for k, v in FABRIC_MOD_IDS.items()}
        return {"mod": fabric_to_mod.get(c["values"][0], c["values"][0])}
    if kind == "not":
        # Forge's NotCondition reads "value"; "child" is only its Java field name.
        return {"not": parse_condition(c["value"] if "value" in c else c["child"])}
    raise ValueError(f"unsupported condition {c}")


def emit_condition(port: Port, c: dict) -> dict:
    style = port.conditions
    if style == "fabric":
        if "mod" in c:
            return {"condition": "fabric:all_mods_loaded",
                    "values": [FABRIC_MOD_IDS.get(c["mod"], c["mod"])]}
        return {"condition": "fabric:not", "value": emit_condition(port, c["not"])}
    prefix = "neoforge" if style == "neoforge" else "forge"
    if "mod" in c:
        return {"type": f"{prefix}:mod_loaded", "modid": c["mod"]}
    return {"type": f"{prefix}:not", "value": emit_condition(port, c["not"])}


def parse_ingredient(x) -> list:
    if isinstance(x, str):
        return [("tag", x[1:])] if x.startswith("#") else [("item", x)]
    if isinstance(x, list):
        return [alt for y in x for alt in parse_ingredient(y)]
    if "tag" in x:
        return [("tag", x["tag"])]
    if "item" in x:
        return [("item", x["item"])]
    raise ValueError(f"unsupported ingredient {x}")


def emit_ingredient(port: Port, alts: list):
    alts = [(k, port.tag(v) if k == "tag" else v) for k, v in alts]
    if port.string_ingredients:
        if len(alts) == 1:
            kind, value = alts[0]
            return "#" + value if kind == "tag" else value
        if any(kind == "tag" for kind, _ in alts):
            raise ValueError("1.21.2+ ingredients cannot list a tag next to items; use a tag")
        return [value for _, value in alts]
    objs = [{"tag": v} if k == "tag" else {"item": v} for k, v in alts]
    return objs[0] if len(objs) == 1 else objs


def parse_result(r) -> dict:
    if isinstance(r, str):
        return {"id": r}
    out = {"id": r.get("id", r.get("item"))}
    if "count" in r:
        out["count"] = r["count"]
    return out


def emit_result(port: Port, recipe_type: str, r: dict):
    if port.components:
        return dict(r)
    if recipe_type in COOKING_TYPES and "count" not in r:
        return r["id"]
    out = {"item": r["id"]}
    if "count" in r:
        out["count"] = r["count"]
    return out


def translate_recipe(port: Port, src: dict):
    """Returns the recipe in `port`'s format, or None if the port cannot express
    its conditions (pre-1.18 Fabric has no conditional recipes)."""
    rtype = src["type"]
    conds = []
    for key in CONDITION_KEYS:
        if key in src:
            raw = src[key]
            conds += [parse_condition(c) for c in (raw if isinstance(raw, list) else [raw])]

    body = {k: copylib.deepcopy(v) for k, v in src.items()
            if k != "type" and k not in CONDITION_KEYS}

    if rtype == "minecraft:crafting_shaped":
        body["key"] = {k: emit_ingredient(port, parse_ingredient(v))
                       for k, v in body["key"].items()}
    elif rtype == "minecraft:crafting_shapeless":
        body["ingredients"] = [emit_ingredient(port, parse_ingredient(v))
                               for v in body["ingredients"]]
    elif rtype in COOKING_TYPES or rtype == "minecraft:stonecutting":
        body["ingredient"] = emit_ingredient(port, parse_ingredient(body["ingredient"]))
    elif rtype == f"{MOD_ID}:alloy_smelting":
        entries = []
        for e in body["ingredients"]:
            alts = parse_ingredient(e["ingredient"] if "ingredient" in e
                                    else {k: v for k, v in e.items() if k in ("item", "tag")})
            ing = emit_ingredient(port, alts)
            count = e.get("count", 1)
            if port.components:
                entries.append({"ingredient": ing, "count": count})
            else:
                if isinstance(ing, list):
                    raise ValueError("pre-1.20.5 alloy entries take a single item or tag")
                entries.append({**ing, "count": count})
        body["ingredients"] = entries
    else:
        raise ValueError(f"unsupported recipe type {rtype}")

    if "result" in body:
        body["result"] = emit_result(port, rtype, parse_result(body["result"]))

    out = {"type": rtype}
    if conds:
        style = port.conditions
        if style is None:
            if any("mod" in c for c in conds):
                return None
            conds = []
        elif style == "forge_single":
            if len(conds) > 1:
                raise ValueError("Forge 1.20.5+ ports take a single condition per recipe")
            out["forge:condition"] = emit_condition(port, conds[0])
        else:
            key = {"forge_list": "conditions", "neoforge": "neoforge:conditions",
                   "fabric": "fabric:load_conditions"}[style]
            out[key] = [emit_condition(port, c) for c in conds]
    out.update(body)
    return out


def translate_tag(port: Port, src: dict) -> dict:
    def fix(v):
        if isinstance(v, str) and v.startswith("#"):
            return "#" + port.tag(v[1:])
        if isinstance(v, dict) and str(v.get("id", "")).startswith("#"):
            return {**v, "id": "#" + port.tag(v["id"][1:])}
        return v
    return {**src, "values": [fix(v) for v in src.get("values", [])]}


# ---------------------------------------------------------------- paths

def translate_path(port: Port, rel: Path) -> Path:
    """Map a path under data/ (as written for any port) to `port`'s layout."""
    parts = list(rel.parts)
    if parts[0] in ("forge", "c") and len(parts) > 1 and parts[1] == "tags":
        parts[0] = port.common_ns
    if len(parts) > 1 and parts[1] == "tags" and len(parts) > 2:
        parts[2] = port.tag_folder(parts[2])
    elif len(parts) > 1:
        parts[1] = port.folder(parts[1])
    return Path(*parts)


def translate_file(port: Port, rel: Path, src):
    """(new relative path, new json or None to skip)."""
    new_rel = translate_path(port, rel)
    parts = new_rel.parts
    if len(parts) > 2 and parts[1] == port.folder("recipes"):
        return new_rel, translate_recipe(port, src)
    if len(parts) > 1 and parts[1] == "tags":
        return new_rel, translate_tag(port, src)
    return new_rel, src


# ---------------------------------------------------------------- fix / check

class Report:
    def __init__(self, dry_run: bool):
        self.dry_run = dry_run
        self.lines = {}

    def add(self, port: Port, msg: str):
        self.lines.setdefault(port.name, []).append(msg)

    def write(self, path: Path, data):
        if not self.dry_run:
            path.parent.mkdir(parents=True, exist_ok=True)
            path.write_text(dump_json(data), encoding="utf-8")

    def remove(self, path: Path):
        if not self.dry_run:
            path.unlink()
            for parent in path.parents:
                if parent == PORTS_DIR or any(parent.iterdir()):
                    break
                parent.rmdir()


def defined_item_tags(port: Port) -> set:
    """Item tags the port defines, counting common tags still waiting to be moved
    into the loader's namespace."""
    tags = set()
    for ns_dir in port.data_dir.iterdir():
        folder = ns_dir / "tags" / port.tag_folder("items")
        for f in folder.rglob("*.json"):
            tags.add(port.tag(f"{ns_dir.name}:{f.relative_to(folder).with_suffix('').as_posix()}"))
    return tags


def used_common_tags(port: Port) -> set:
    used = set()
    for f in port.data_dir.rglob("*.json"):
        text = f.read_text(encoding="utf-8-sig")
        for t in re.findall(r'"tag"\s*:\s*"([a-z_]+:[a-z0-9_/]+)"', text) + \
                re.findall(r'"#([a-z_]+:[a-z0-9_/]+)"', text):
            if t.split(":")[0] in ("forge", "c"):
                used.add(port.tag(t))
    return used


def fix_port(port: Port, report: Report):
    data = port.data_dir

    for f in sorted(port.resources_dir.rglob("*.json")):
        raw = f.read_bytes()
        if raw.startswith(b"\xef\xbb\xbf"):
            report.add(port, f"strip byte-order mark: {f.relative_to(port.resources_dir).as_posix()}")
            if not report.dry_run:
                f.write_bytes(raw[3:])

    # Common tag files in the namespace this port's loader uses.
    for wrong_ns in ("forge", "c"):
        if wrong_ns == port.common_ns or not (data / wrong_ns / "tags").is_dir():
            continue
        for f in sorted((data / wrong_ns / "tags").rglob("*.json")):
            rel = f.relative_to(data)
            dest = data / translate_path(port, rel)
            src = translate_tag(port, read_json(f))
            if dest.is_file():
                merged = read_json(dest)
                for v in src["values"]:
                    if v not in merged["values"]:
                        merged["values"].append(v)
                src = merged
            report.add(port, f"move tag {rel.as_posix()} -> {dest.relative_to(data).as_posix()}")
            report.write(dest, src)
            report.remove(f)

    for f in sorted(data.rglob("*.json")):
        rel = f.relative_to(data)
        parts = rel.parts
        if parts[0] in ("forge", "c") and parts[0] != port.common_ns:
            continue  # moved into the right namespace above
        try:
            src = read_json(f)
            if len(parts) > 2 and parts[1] in ("recipes", "recipe"):
                new = translate_recipe(port, src)
            elif len(parts) > 2 and parts[1] == "tags":
                new = translate_tag(port, src)
            else:
                continue
        except ValueError as e:
            report.add(port, f"CANNOT TRANSLATE {rel.as_posix()}: {e}")
            continue
        if new is not None and new != src:
            changed = sorted(k for k in set(src) | set(new) if src.get(k) != new.get(k))
            report.add(port, f"rewrite {rel.as_posix()} ({', '.join(changed)})")
            report.write(f, new)

    used = used_common_tags(port)
    defined = defined_item_tags(port)
    tag_dir = data / port.common_ns / "tags" / port.tag_folder("items")
    for path, (item, since) in VANILLA_COMMON_TAGS.items():
        tag = f"{port.common_ns}:{path}"
        if (tag in used and tag not in defined and port.mc >= since
                and not port.loader_ships_vanilla_tags):
            report.add(port, f"add tag {tag} (the loader does not ship it)")
            report.write(tag_dir / f"{path}.json", {"replace": False, "values": [item]})
    for path, item in OPTIONAL_COMMON_TAGS.items():
        tag = f"{port.common_ns}:{path}"
        if tag in used and tag not in defined:
            report.add(port, f"add tag {tag} (optional, filled by ProjectRed)")
            report.write(tag_dir / f"{path}.json",
                         {"replace": False, "values": [{"id": item, "required": False}]})

    wanted = port.biome_modifier
    mod_data = data / MOD_ID
    for folder in ("forge", "neoforge"):
        bm_dir = mod_data / folder / "biome_modifier"
        if not bm_dir.is_dir():
            continue
        for f in sorted(bm_dir.glob("*.json")):
            src = read_json(f)
            rel = f.relative_to(data).as_posix()
            if wanted is None:
                report.add(port, f"remove {rel} (this loader adds ore in code)")
                report.remove(f)
                continue
            want_folder, want_type = wanted
            dest = mod_data / want_folder / "biome_modifier" / f.name
            new = {**src, "type": want_type}
            if dest != f:
                report.add(port, f"move {rel} -> {dest.relative_to(data).as_posix()}")
                if not dest.is_file():
                    report.write(dest, new)
                report.remove(f)
            elif new != src:
                report.add(port, f"rewrite {rel} (type)")
                report.write(f, new)


def cmd_fix(args, dry_run: bool) -> int:
    ports = select_ports(args.ports)
    report = Report(dry_run)
    for port in ports:
        fix_port(port, report)

    recipes = {}
    for p in all_ports():
        for f in p.data_dir.glob(f"{MOD_ID}/recipe*/*.json"):
            recipes.setdefault(f.name, {})[p.name] = f
    for port in ports:
        for name, where in sorted(recipes.items()):
            if port.name in where:
                continue
            # Ports that cannot express a recipe's mod condition are meant to lack it.
            if translate_recipe(port, read_json(next(iter(where.values())))) is None:
                continue
            report.add(port, f"no recipe {name} (other ports have it)")

    for name, lines in report.lines.items():
        print(name)
        for line in lines:
            print("  " + line)
    if not report.lines:
        print("All ports are in their loader's format.")
    elif dry_run:
        print("\nRun `python scripts/port_data.py fix` to apply.")
    return 1 if (dry_run and report.lines) else 0


def cmd_copy(args) -> int:
    ports = {p.name: p for p in all_ports()}
    if args.source not in ports:
        sys.exit(f"Unknown port {args.source}")
    source = ports[args.source]
    targets = select_ports(args.to) if args.to else [p for p in ports.values() if p != source]

    files = []
    for pattern in args.paths:
        matches = [f for f in source.data_dir.rglob("*")
                   if f.is_file() and fnmatch.fnmatch(f.relative_to(source.data_dir).as_posix(),
                                                      pattern.replace("\\", "/"))]
        if not matches:
            sys.exit(f"No files in {source.name} match {pattern}")
        files += matches

    for f in files:
        rel = f.relative_to(source.data_dir)
        src = read_json(f) if f.suffix == ".json" else None
        print(rel.as_posix())
        for port in targets:
            try:
                new_rel, new = translate_file(port, rel, src) if src is not None else (
                    translate_path(port, rel), None)
            except ValueError as e:
                print(f"  {port.name:17} CANNOT TRANSLATE: {e}")
                continue
            dest = port.data_dir / new_rel
            if src is not None and new is None:
                print(f"  {port.name:17} skipped (no conditional recipes on this loader)")
                continue
            if src is None:
                same = dest.is_file() and dest.read_bytes() == f.read_bytes()
            else:
                same = dest.is_file() and read_json(dest) == new
            state = "same" if same else ("update" if dest.is_file() else "new")
            print(f"  {port.name:17} {state:6} {new_rel.as_posix()}")
            if same or args.dry_run:
                continue
            dest.parent.mkdir(parents=True, exist_ok=True)
            if src is None:
                dest.write_bytes(f.read_bytes())
            else:
                dest.write_text(dump_json(new), encoding="utf-8")
    return 0


def select_ports(names) -> list:
    ports = {p.name: p for p in all_ports()}
    if not names:
        return list(ports.values())
    wanted = [n.strip() for chunk in names for n in chunk.split(",") if n.strip()]
    unknown = [n for n in wanted if n not in ports]
    if unknown:
        sys.exit(f"Unknown ports: {', '.join(unknown)}. Known: {', '.join(ports)}")
    return [ports[n] for n in wanted]


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    sub = parser.add_subparsers(dest="cmd", required=True)
    p_check = sub.add_parser("check", help="report files in the wrong format")
    p_check.add_argument("ports", nargs="*")
    p_fix = sub.add_parser("fix", help="rewrite files into each port's format")
    p_fix.add_argument("ports", nargs="*")
    p_copy = sub.add_parser("copy", help="translate files from one port into the others")
    p_copy.add_argument("paths", nargs="+", help="paths under data/, globs allowed")
    p_copy.add_argument("--from", dest="source", required=True)
    p_copy.add_argument("--to", action="append", help="target ports (default: all others)")
    p_copy.add_argument("--dry-run", action="store_true")
    args = parser.parse_args()

    if args.cmd == "check":
        return cmd_fix(args, dry_run=True)
    if args.cmd == "fix":
        return cmd_fix(args, dry_run=False)
    return cmd_copy(args)


if __name__ == "__main__":
    sys.exit(main())
