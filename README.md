# RP Tubes

RedPower 2 pneumatic tubes and related machines, revived for **Forge, NeoForge and
Fabric** on **Minecraft 1.7.10, 1.12.2, 1.16.4-1.16.5, 1.19.1-1.19.2, 1.20-1.20.1,
1.20.5-1.20.6, 1.21-1.21.1, 1.21.11 and 26.1-26.2**. See `CHANGELOG.md` for the exact
versions per loader.

A faithful recreation of the RedPower 2 pr5b2 (Tekkit Classic era) tube transport system:
original behaviors, textures, GUIs, and routing logic, rebuilt on modern Forge.

## What's in 1.5

- **CB Multipart support.** Pneumatic, Redstone, Restriction and Mag Tubes fit in the
  same block as covers, panels and hollow covers. Solid covers close a tube's side;
  hollow covers let it pass. Optional, on Forge 1.7.10, 1.12.2, 1.16.5, 1.19.2 and
  1.20.1 and NeoForge 1.21.1 and 1.21.11.
- **MagTube** keeps the speed of items passing through it, and plain tubes now slow
  sped up items back down, as in RedPower 2
- **Accelerator** spends power to bring passing items up to full speed
- **Retrievulator** (Tubestuff) keeps an inventory stocked to set counts by pulsing a
  Retriever for whatever is missing
- Solar Panel, Thermopile, Battery Box and Battery on 1.7.10, 1.12.2 and 1.16.5 too
  (1.7.10 also talks to RF mods), plus crafting recipes and copper on 1.7.10
- 1.5.1: the same jar also runs on the hotfix releases (Forge 1.16.4, 1.19.1, 1.20,
  1.21, 26.1, 26.1.1; NeoForge 1.21; Fabric 1.16.4, 1.20, 1.20.5, 1.21, 26.1, 26.1.1)
- 1.5.2: tube and machine textures on 1.7.10 match the newer versions, with or
  without Angelica

## What's in 1.4

- **One jar for every version.** A single `rptubes-<version>.jar` loads the right build
  for your Minecraft version and loader.
- **Power blocks:** Solar Panel, Thermopile (melts nearby ice and snow like the
  original), Battery Box and Battery. They feed the Sorting Machine, Filter and
  Retriever and any mod using Forge Energy (Team Reborn Energy on Fabric).
- The Alloy Furnace accepts more than the exact ingredient counts, as in RedPower 2
- Tin, copper and brass from other mods work in recipes; ProjectE EMC for tin, brass
  and the machines
- 1.4.1: fixed item duplication when a chunk unloads, and contents now drop when a
  machine is broken on 1.7.10 and 1.12.2

## What's in 1.3

- **Dyeable tubes.** Paint a tube with dye (sneak with an empty hand clears it). Painted
  items only travel through tubes of the same color or unpainted ones, the original
  color routing.
- **Energy** on the Filter, Retriever and Sorting Machine: with power they extract on
  their own, and a redstone pulse still works without it
- NeoForge and Fabric builds next to Forge
- 1.12.2: connected tube rendering, Copper Ore and Ingot, and worldgen toggles in
  `config/rptubes.cfg`

## What's in 1.2.0

- **Automatic Crafting Table** (BuildCraft 2.2.14 AutoWorkbench) — real items in the
  grid, keeps the last item as the recipe template, pulls missing ingredients from
  adjacent inventories, one tube pull = one full craft
- **Automatic Crafting Table Mk II** (Tubestuff 49.1.2) — ghost recipe template,
  45-slot internal ingredient storage, continuous crafting; adds a translucent
  result preview the original never had
- **Public modder API** — `com.rptubes.api.RPTubesApi` (inject items into the
  network, connection queries) + stable `ITubeConnectable`/`TubeItem` contracts;
  documented in `wiki/`
- Wiki docs for soft dependencies, datapack recipes/tags, and the API

## What's in 1.1.0

- **Multiplayer-hardened item sync** — the client fully simulates tube flow and the server
  sends small item-event packets (network entries, junction routes, bounces) over a
  version-checked channel. Smooth on dedicated servers, including Bukkit hybrids (Arclight).
- Unique per-item identity so identical stacks in flight never merge or ghost
- Version handshake: client and server must run the same RP Tubes version to connect

## What's in 1.0.0

- **Pneumatic / Redstone / Restriction Tubes** — nearest-destination routing, visible item
  transit, bounce-back buffering, glowing redstone tube stripes
- **Machines** — Transposer, Filter, Sorting Machine (all 5 OG modes), Ejector, Relay,
  Retriever, Regulator, Item Detector, Buffer, Block Breaker, Deployer, Igniter —
  each with its original GUI and texture set
- **Alloy Furnace** — OG GUI, brass smelting (3 copper + 1 tin → 4 brass), custom alloy recipes
- **Tin Ore / Tin Ingot** — OG textures and worldgen (10 veins/chunk below y48), fully
  tag-based (`forge:ingots/tin`) so any other mod's tin interoperates
- **Screwdriver** — rotates RP Tubes machines through all six facings, 128 uses
- **JEI** — full recipe support incl. a dedicated Alloy Smelting category
- **ProjectE** — soft EMC registration with Tekkit-era ratios
- **ProjectRed** — soft compat: recipes prefer PR red alloy/tin when installed
- Forge `IItemHandler` inventory bridge — works with chests and machines from any mod

## Recipes

| Result | Recipe |
|---|---|
| Alloy Furnace | 8× Bricks hollow square |
| Brass ×4 | Alloy: 3× Copper + 1× Tin (tag) |
| Red Alloy (PR item) | Alloy furnace alternate: 4× Redstone + 1× Iron (if ProjectRed Core loaded) |
| Tin Ingot | Smelt/blast Tin Ore |
| Screwdriver | Iron Ingot + Stick (diagonal) |
| Pneumatic Tube ×8 | Brass / Glass / Brass |
| Redstone Tube | Pneumatic Tube + Redstone |
| Restriction Tube | Pneumatic Tube + Iron |
| MagTube ×8 | 8× Pneumatic Tube around a Copper Ingot |
| Accelerator | Brass corners / Copper top and bottom / MagTube sides / Redstone Block center |
| Transposer | Stone / Planks / Piston / Redstone |
| Filter | Stone / Gold / Piston / Red Alloy (or Redstone) |
| Ejector | Planks / Chest / Transposer / Stone / Redstone |
| Block Breaker | Stone / Iron Pickaxe / Piston / Redstone |
| Deployer | Stone / Chest / Piston / Redstone |
| Buffer | Iron Bars / Planks |
| Item Detector | Brass / Tube / Red Alloy / Pressure Plate |
| Relay | Like Ejector (auto-feed) |
| Retriever | Brass / Ender Pearl / Filter / Iron |
| Regulator | Brass / Detector / Buffer |
| Sorting Machine | Iron / Filter / Red Alloy |
| Igniter | Netherrack / Flint & Steel / Deployer / Stone / Redstone |
| Automatic Crafting Table | 4× Brass around a Crafting Table |
| ACT Mk II | Planks corners / 4× Pipe (`rptubes:act_pipes` tag: Pneumatic Tube or BC CE Wooden Pipe) / ACT center / Chest bottom |
| Retrievulator | Brass / Comparator / Redstone / Hopper center / Brass bottom row |
| Solar Panel | Glass top / Lapis, Daylight Detector, Lapis / Copper bottom (ProjectRed: Electrotine Silicon around an Electrotine Ingot) |
| Thermopile | Copper corners / Iron top and bottom / Lapis sides / Redstone center (ProjectRed: Electrotine Silicon sides, Electrotine Ingot center) |
| Battery | Lapis sides / Copper top and bottom / Tin center (ProjectRed: Electrotine Dust sides) |
| Battery Box | Batteries / Planks top / Iron / Lapis Block bottom center (ProjectRed: Electrotine Ingot) |

## Build / run

Minecraft 1.19.2 through 26.2 on Forge, NeoForge and Fabric build from one source tree
with [Stonecutter](https://stonecutter.kikugie.dev/). Each Minecraft version and loader
is a node in `versions/{version}-{loader}`; version and loader differences are marked
in the Java source with `//? if` comments. Gradle runs on JDK 25 and fetches the JDK
each node compiles with.

```bat
gradlew.bat build collectJar
gradlew.bat :1.20.1-fabric:runClient
gradlew.bat "Set active project to 26.2-neoforge"
```

`collectJar` copies every release jar into `build/libs/{mod version}/`. The active
project is the version the IDE sees; switch back to `1.21.1-neoforge` before committing.

Resources live in `src/main/resources` in the 26.2 NeoForge format. The build rewrites
recipes, tags, conditions, item models and folder names into the format each version
and loader reads, so a recipe or tag is written once. Loader metadata templates are in
`src/overlays/{loader}`, and per-version dependencies in `stonecutter.properties.toml`.

The 1.7.10, 1.12.2 and 1.16.5 ports are separate Gradle projects under `ports/`.
`scripts/port_data.py` translates data files between those.

## Reference

Decompiled RedPower 2.0pr5b2 sources live under `reference/decompiled/` (CFR).
Original jars under `reference/jars/`.

## Credits

- **Eloraam** — creator of **RedPower 2**, the original tube system, machines, textures,
  and GUIs this mod recreates. All original art and design is hers; this project exists
  as a tribute to that work.
- **SpaceToad** and the **BuildCraft** team — the original Automatic Crafting Table
  (AutoWorkbench) and its textures, recreated from BuildCraft 2.2.14 (MMPL).
- **immibis** — Tubestuff's Automatic Crafting Table Mk II, recreated from
  Tubestuff 49.1.2.
- **MrTJP** and the **ProjectRed** team — for keeping the wiring, logic, and exploration
  side of RedPower alive on modern Minecraft for over a decade. RP Tubes is built to sit
  alongside ProjectRed and fill in the transport system it doesn't cover.
- **CFR** decompiler — used to study the original pr5b2 / BC 2.2.14 / Tubestuff 49.1.2
  behavior for accuracy.
