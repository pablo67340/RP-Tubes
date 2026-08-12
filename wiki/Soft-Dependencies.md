# Soft Dependencies

RP Tubes has **no required dependencies** — Minecraft 1.19.2 and Forge 43+ is all it
needs. Everything below is detected at runtime and integrated automatically when
present. Nothing crashes or changes behavior when absent.

## JEI (Just Enough Items)

- All crafting recipes are browsable.
- A dedicated **Alloy Smelting** recipe category shows Alloy Furnace recipes
  (brass, red alloy, and any datapack-added alloys) with the Alloy Furnace as catalyst.
- Recipe-transfer (the `+` button) and click-areas work in the Alloy Furnace GUI.
- Tested against JEI `11.6.0.1024`.

## ProjectRed

- If **ProjectRed Core** (`projectred_core`) is loaded, the Alloy Furnace gains the
  classic **Red Alloy Ingot** recipe (4 redstone + 1 iron), producing ProjectRed's
  own `projectred_core:red_ingot`.
- Machine recipes that call for red alloy use an ingredient that accepts ProjectRed's
  ingot, with a redstone fallback when ProjectRed is absent.
- Tin is tag-based (see below), so ProjectRed Exploration's tin is used interchangeably
  with ours.

## ProjectE

- If ProjectE (`projecte`) is loaded, EMC values tuned to Tekkit-era ratios are
  registered for every RP Tubes item via the ProjectE API (reflection — no compile
  dependency).

## BuildCraft Community Edition

- The **Automatic Crafting Table Mk II** recipe accepts either our Pneumatic Tube
  *or* BuildCraft CE's Wooden Pipe (`buildcrafttransport:wood_item`) in the pipe
  slots, via the `rptubes:act_pipes` item tag. The tag entry is optional, so nothing
  breaks without BuildCraft installed.

## Any mod that adds Tin (Thermal, Mekanism, ProjectRed, …)

- RP Tubes adds its own **Tin Ore** (worldgen: 10 veins/chunk below y48, OG RedPower
  distribution) and **Tin Ingot**, published under `forge:ores/tin` and
  `forge:ingots/tin`.
- Every recipe that consumes tin uses the **tag**, never our item directly — so any
  other mod's tin works in the Alloy Furnace, and our tin works in theirs.

## Dedicated servers in large packs (informational)

Minecraft 1.19.2 has a vanilla bug ([MC-258163](https://bugs.mojang.com/browse/MC-258163))
that visually corrupts blocks with high state IDs in packs of roughly 90+ mods on
multiplayer. If tubes or machines render as the wrong block on your server, install
[Block Limit Fix](https://modrinth.com/mod/block_limit_fix) on the **server only** —
it backports Mojang's official 1.19.4 patch. This is not specific to RP Tubes.
