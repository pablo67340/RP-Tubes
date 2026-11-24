# RP-Tubes 1.5.1

Replace `rptubes-1.5.0.jar` with `rptubes-1.5.1.jar`.

## More Minecraft versions

The same jar now also runs on the hotfix and earlier releases next to each
supported version:

- Forge: 1.16.4, 1.19.1, 1.20, 1.21, 26.1, 26.1.1
- NeoForge: 1.21
- Fabric: 1.16.4, 1.20, 1.20.5, 1.21, 26.1, 26.1.1

## Fixed

- **Sneaking with an empty hand clears tube paint again on NeoForge 1.21.11
  and 26.x.** The click was ignored on those versions.
- **The 1.12.2 build no longer claims to support 1.12 and 1.12.1.** It
  crashed on startup there, because it needs Forge 14.23.

# RP-Tubes 1.5.0

Replace `rptubes-1.4.1.jar` with `rptubes-1.5.0.jar`. The new blocks and
features are on every supported version and loader unless noted.

## CB Multipart support

- **Tubes share a block space with covers.** Pneumatic, Redstone,
  Restriction and Mag Tubes work as CB Multipart parts, so they fit in the
  same block as covers, panels and hollow covers.
- Add a cover to a placed tube and it turns into a part, keeping any items
  that were in flight. A tube item can also be placed straight into a space
  that already holds covers.
- A solid cover on a tube's face closes that side. Hollow covers leave room
  for the tube to pass through.
- Redstone Tubes keep carrying redstone as parts, and breaking a tube part
  drops the tube and anything inside it while the covers stay.
- Works with CB Multipart on Forge 1.16.5, 1.19.2 and 1.20.1 and NeoForge
  1.21.1 and 1.21.11, and with Forge Multipart on Forge 1.7.10 and 1.12.2.
  It is optional: without it nothing changes.

## New

- **MagTube.** Keeps the speed of the items passing through it. 8 Pneumatic
  Tubes around a copper ingot make 8.
- **Accelerator.** Spends power to bring every item passing through up to
  full speed.
- **Retrievulator.** Sits on a Retriever's output and keeps the inventory on
  its own output stocked to the counts you set, pulsing the Retriever for
  whatever is missing.
- **Tube speed.** Plain tubes now slow sped up items back down within a few
  blocks, as in RedPower 2. MagTubes keep the speed.

## Older versions (1.7.10, 1.12.2 and 1.16.5)

- **Solar Panel, Thermopile, Battery Box and Battery** are now on 1.7.10,
  1.12.2 and 1.16.5 (Forge and Fabric). On 1.7.10 they run on their own and
  also connect to RF mods when one is installed.
- Filter jamming, the extraction counter, per side inventory handling and
  tube speed loss now behave as they do on the newer versions.
- **1.7.10:** crafting recipes for every block, and Copper Ore and Copper
  Ingot for the recipes that need copper.

## Fixed

- **Filter, Retriever, Regulator and Deployer** gave pipes and tubes the
  wrong side's slots.
- **1.7.10:** fixed a crash when a tube route ended in an inventory with
  room.

# RP-Tubes 1.4.1

Bug fix release. Replace `rptubes-1.4.0.jar` with `rptubes-1.4.1.jar`.

## Fixed

- **Item duplication when a chunk unloads.** Machines and tubes dropped
  their contents on the ground whenever their chunk unloaded, even though the
  same items were also saved inside the block. Walking away and coming back
  doubled everything in Ejectors, Buffers, Regulators, Filters, Sorting
  Machines, Item Detectors, Deployers, Auto Crafting Tables and tubes. Now
  contents only drop when the block is actually broken. Affected every
  version from 1.16.5 up.
- **Contents lost when breaking a machine on 1.7.10 and 1.12.2.** Breaking
  an Ejector, Deployer, Regulator, Item Detector, Block Breaker, Auto
  Crafting Table or tube dropped only the block, and the items inside were
  deleted. They now drop like they do on newer versions.
- **Ejectors, Filters, Regulators and Deployers on Fabric 1.16.5.** These
  failed to create their block entity, so they could not be placed with
  contents or used by tubes and hoppers.
- **Sided inventories now follow the block's facing.** On Fabric, a machine
  turned with the Screwdriver kept using its old sides for hoppers and other
  mods' pipes.

# RP-Tubes 1.4.0

## One jar for every version

There is now a single file, `rptubes-1.4.0.jar`, that works on every
supported Minecraft version and loader. Drop it in your mods folder and it
loads the build that matches your game. Remove any older `rptubes-*.jar`
first.

- Forge: 1.7.10, 1.12.2, 1.16.5, 1.19.2, 1.20.1, 1.20.6, 1.21.1, 1.21.11,
  26.1.2, 26.2
- NeoForge: 1.20.1, 1.20.6, 1.21.1, 1.21.11, 26.1.2, 26.2
- Fabric: 1.16.5, 1.20.1, 1.20.6, 1.21.1, 1.21.11, 26.1.2, 26.2 (needs
  Fabric API; 1.16.5 needs Java 17)

On Forge and NeoForge the matching build is unpacked to your system's temp
folder the first time the game starts.

## New (Forge, NeoForge and Fabric, 1.19.2 and newer)

- **Solar Panel.** Makes 20 FE/t in daylight while it can see the sky.
- **Thermopile.** Makes power from the temperature difference between the
  blocks around it, such as lava on one side and water or ice on the other.
  Like the RedPower 2 original, a hot side can melt nearby ice and snow.
- **Battery Box.** Stores 600,000 FE and moves up to 1,000 FE/t. Put a
  Battery in the top slot to charge it or in the bottom slot to empty it into
  the box.
- **Battery.** A portable 150,000 FE cell for the Battery Box.

These feed the Sorting Machine, Filter and Retriever, and any other mod that
uses Forge Energy (Team Reborn Energy on Fabric).

## Fixed

- **The Alloy Furnace works with more than the exact amount.** Each
  ingredient now only needs at least its recipe count, as in RedPower 2, and
  the extra items stay in the grid. Before, a recipe only crafted when the
  stacks matched exactly. (All versions.)
- **Tin, copper and brass from other mods work in RP Tubes recipes.** Forge
  before 26.1 and Fabric 1.20.1 recipes now accept both common tag styles, so
  tin from mods like IC2 is recognised.
- **ProjectE gives EMC to tin, brass and the RP Tubes machines.** Values come
  from a ProjectE conversion file for the Alloy Furnace recipes, which works
  with current ProjectE versions. The 1.7.10 build calls the ProjectE API in
  the form that version expects.
- **Painted tubes show their color in the inventory on Forge 1.21.11 and
  26.x,** and placed painted tubes show it on Forge 26.x.

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
