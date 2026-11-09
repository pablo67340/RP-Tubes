# RP-Tubes 1.3.1

## New (all 1.12.2+ ports, all loaders)

- **Dyeable tubes.** Right-click a Pneumatic, Redstone, or Restriction Tube with
  dye to paint it. Sneak + empty hand clears the paint. Painted items from the
  Sorting Machine / Filter / Retriever only travel through unpainted tubes or
  tubes of the same color — the original RedPower color-routing setup.
- **Energy on Filter, Retriever, and Sorting Machine.**
  Forge / NeoForge speak Forge Energy (RF). Fabric uses Team Reborn Energy.
  While they have energy they extract on their own; a redstone pulse still
  works without a generator. IC2 EU and BuildCraft MJ need a converter.

## Fixed

- **The NeoForge 1.20.6 build loads again.** It asked for a newer mod loader
  than NeoForge 20.6 ships, so NeoForge refused to start with it installed.
- **The Fabric 1.16.5 build loads again.** It required Fabric API under its
  newer name, `fabric-api`; on 1.16.5 Fabric API is still called `fabric`, so
  the loader reported it missing even when installed.
- **Filter, Item Detector, Regulator, Relay, Retriever, and Sorting Machine
  can be crafted without ProjectRed (Forge 1.20.6 and newer).** Their
  redstone recipes, used when ProjectRed is not installed, wrote the "not"
  condition in a form Forge rejects, so Forge dropped them without a word.
- **Tin ore now generates on NeoForge 1.20.6, 1.21.1, 1.21.11, and 26.1.2.**
  The world generation file was in the old Forge location, so NeoForge never
  loaded it and no tin spawned. Without tin there was no brass, which most tube
  recipes need.
- **Restriction Tube, Retriever, and Sorting Machine can be crafted on
  NeoForge 1.20.6, 1.21.1, and 1.21.11, and on Fabric 1.16.5 and 1.20.1.**
  Their recipes asked for an iron ingot tag those loaders do not have. NeoForge
  recipes now use the shared `c:` tags, and the Fabric builds declare the tag
  themselves. RP Tubes' tin and brass ingots are tagged under `c:` on NeoForge
  so other mods' recipes accept them.
- **Only one version of each recipe loads on NeoForge 1.20.6 to 26.1.2.** The
  "use red alloy when ProjectRed is installed, redstone otherwise" switch was
  written in the Forge format and ignored, so both versions showed up in JEI.

# RP-Tubes 1.2.1 — NeoForge & Fabric

RP Tubes now ships on **Forge, NeoForge, and Fabric**. Pick the jar that
matches your Minecraft version and loader.

## NeoForge

- 1.20.1, 1.20.6, 1.21.1, 1.21.11, 26.1.2, 26.2
- There is no NeoForge for 1.19.2 or earlier (the loader starts at 1.20.1).

## Fabric

- 1.16.5, 1.20.1, 1.20.6, 1.21.1, 1.21.11, 26.1.2, 26.2

## Also

- **Forge 1.19.2** source tree is in the repo now (`ports/1.19.2`). NeoForge
  does not exist for that version; use Forge 43.5.2.

Jar names: `rptubes-<minecraft>-<loader>-1.2.1.jar`

# RP-Tubes 1.3.0 (1.12.2 only)

## Fixed

- **Tubes now visually connect to each other and to inventories.** The
  connection state was never computed during rendering, so every tube
  drew as an unconnected core. Selection boxes now match the arms too.
- **Missing textures on Tin Ore and most machines.** 1.12.2 requires
  blockstate variant keys with properties in alphabetical order
  (`active=...,facing=...`) and `"normal"` instead of `""` — all 15
  affected blockstates were rewritten.
- **Alloy Furnace no longer eats its fuel and ingots.** Every tile entity
  was being destroyed and recreated whenever its block toggled state
  (lit/active/redstone glow), wiping the contents. All tile entities now
  survive state changes.
- **Recipes load again.** Recipes using stone were silently dropped
  (1.12.2 requires an explicit `data` value for items with subtypes),
  and the Alloy Furnace recipe referenced `minecraft:bricks`, which is
  the 1.13+ name (`minecraft:brick_block` on 1.12.2).

## New

- **Copper Ore and Copper Ingot (1.12.2 only).** Vanilla 1.12.2 has no
  copper, which made brass — and therefore every tube — uncraftable
  without another mod. Copper generates in the overworld and smelts to
  ingots.
- **Worldgen config** (`config/rptubes.cfg`): `generateTinOre` and
  `generateCopperOre` toggles for packs that provide their own ores.
  All recipes accept any ore-dictionary tin/copper (`ingotTin`,
  `oreTin`, `ingotCopper`), including entries added via CraftTweaker.

# RP-Tubes 1.2.1

## Fixed

- **Ejector input side now connects properly.** Tubes can connect to the
  back (input) side of the Ejector, and a chest placed directly behind it
  is drained as expected.
- **Relay no longer drains adjacent inventories.** The Relay only pushes
  items from its own internal buffer.

## New

- **1.20.1 and 1.21.1 ports**, by request. RP-Tubes is now available for
  Minecraft 1.7.10, 1.12.2, 1.16.5, 1.19.2, 1.20.1, 1.20.6, 1.21.1,
  1.21.11, 26.1.2 and 26.2 (Forge, client + server).
