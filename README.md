# RP Tubes

RedPower 2 pneumatic tubes and related machines, revived for **Forge** on
**Minecraft 1.7.10, 1.12.2, 1.16.5, 1.19.2, 1.20.1, 1.20.6, 1.21.1, 1.21.11,
26.1.2 and 26.2**.

A faithful recreation of the RedPower 2 pr5b2 (Tekkit Classic era) tube transport system:
original behaviors, textures, GUIs, and routing logic, rebuilt on modern Forge.

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

## Build / run

Requires **JDK 17** (`gradle.properties` points at `G:/AstroCore-Labs/Tools/java/17`).

```bat
gradlew.bat genEclipseRuns
gradlew.bat runClient
```

### Changing recipes or tags across ports

Every port under `ports/` ships the same recipes and tags, but each loader and
Minecraft version spells them differently (condition format, `forge:` or `c:`
tags, ingredient syntax, folder names). Edit the file in one port, then let
`scripts/port_data.py` translate it into the rest:

```bat
python scripts\port_data.py copy rptubes/recipe/filter.json --from 1.21.1-neoforge --dry-run
python scripts\port_data.py copy rptubes/recipe/filter.json --from 1.21.1-neoforge
python scripts\port_data.py check
```

`check` lists anything in a format its loader would ignore and exits non-zero;
`fix` rewrites those files in place. Run `check` before building a release.

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
