# RP Tubes — RedPower 2 Pneumatic Tubes, Revived

**The best item transport system Minecraft ever had is back.**

RP Tubes is a faithful revival of the pneumatic tube system and item machines from **RedPower 2** (the Tekkit Classic era, pr5b2) for modern **Forge 1.19.2** — rebuilt from the ground up with the original behaviors, textures, GUIs, and routing logic. If you ever ran a sorting hall off Filters, Transposers, and a wall of Sorting Machines, this is that — exactly that.

---

## ✦ Pneumatic Tubes

- **Pneumatic Tube** — the classic brass-railed glass tube. Items visibly travel through the network with smooth animation, and always route to the **nearest valid destination**.
- **Redstone Tube** — carries items *and* a redstone signal. The red stripes glow brighter with signal strength, just like the original.
- **Restriction Tube** — massively increases path weight so items only take this route when there is no other option.

Items that can't be delivered anywhere **bounce back** to their source machine and wait in its internal buffer until a path opens up — no items ever spill on the floor from a full chest.

## ✦ The Machines

All the RedPower 2 item machinery, with authentic textures, facings, and dedicated GUIs:

- **Transposer** — pulls one item per redstone pulse (or vacuums dropped items) into the tube network.
- **Filter** — like the Transposer, but with a 3×3 whitelist and a paint color for routed items.
- **Sorting Machine** — the legend itself. 8 color-coded filter columns × 5 rows, all **five original sorting modes** (anystack/allstacks sequential, random allstacks, anystack, any-item with default color), per-column channel buffers drained round-robin.
- **Ejector** — pushes buffered items out into the tube network.
- **Relay** — accepts painted/unpainted items and forwards them; doubles as a tube-accessible chest.
- **Retriever** — the reverse Filter: *pulls* matching items from across the tube network to itself, with sequential or any-match modes.
- **Regulator** — keeps a destination inventory stocked to an exact template, with dump and regulate modes.
- **Item Detector** — emits redstone pulses as items pass through: per item, per stack, or latched while plugged.
- **Buffer** — a 20-slot chest that tubes and machines can freely push into and pull from on every side.
- **Block Breaker** — breaks the block in front of it and sends it into the network.
- **Deployer** — places blocks and uses items, exactly like a robotic player hand.
- **Igniter** — lights fires (and TNT, and nether portals) on redstone signal, clears them when it turns off.

## ✦ Materials & Crafting

- **Alloy Furnace** — the classic 3×3 alloying furnace with its original GUI. Smelts **Brass** (3 copper + 1 tin) and more.
- **Automatic Crafting Table** — the beloved BuildCraft AutoWorkbench, with its original textures and behavior: real items in the grid, one item always left behind to hold the recipe, pulls missing ingredients from adjacent chests, and one tube pull = one full craft (4 planks per log, just like the wooden-pipe days).
- **Automatic Crafting Table Mk II** — immibis's Tubestuff upgrade, faithfully recreated: a ghost recipe template, 45 slots of internal ingredient storage, and continuous hands-free crafting. Improved over the original with a translucent result preview so you can see the recipe resolved before feeding it materials.
- **Tin Ore** — generates in the overworld with the original RedPower distribution (10 veins per chunk below y48). Fully tag-based: if another mod already adds tin (ProjectRed, Thermal, Mekanism…), all of it just works together.
- **Screwdriver** — right-click any RP Tubes machine to rotate it through all six facings. 128 uses.

## ✦ Compatibility & Soft Dependencies

RP Tubes has **no required dependencies**. Everything below is detected automatically when present:

- **JEI** — full recipe support, including a dedicated Alloy Smelting category with recipe transfer.
- **ProjectRed** — recipes prefer ProjectRed's red alloy and tin when installed, with vanilla fallbacks otherwise; the Alloy Furnace gains the classic Red Alloy recipe.
- **ProjectE** — EMC values for everything, tuned to Tekkit-era ratios.
- **BuildCraft Community Edition** — its Wooden Pipe is accepted alongside our Pneumatic Tube in the ACT Mk II recipe.
- **Any tin mod** (Thermal, Mekanism, ProjectRed…) — all tin usage is tag-based (`forge:ingots/tin`), so every mod's tin interoperates with ours.
- Works with **any inventory from any mod** — all item I/O uses Forge capabilities.

## ✦ For Modders & Pack Makers

RP Tubes ships a small stable Java API (`com.rptubes.api.RPTubesApi`): inject items into the tube network like an Ejector, or implement `ITubeConnectable` to make your own blocks part of the network. Pack makers get data-driven hooks too: the `rptubes:alloy_smelting` recipe JSON type, the `rptubes:act_pipes` tag, and datapack-overridable tin worldgen. Full documentation is on the wiki.

## ✦ Requirements

- Minecraft **1.19.2**
- Forge **43+**
- No required dependencies. JEI strongly recommended.

## ✦ Running a Large Modpack Server? Read This

Minecraft 1.19.2 itself has a vanilla bug ([MC-258163](https://bugs.mojang.com/browse/MC-258163)): in packs with a very large number of total blockstates (~90+ mods), blocks whose IDs fall above 524,288 get **visually corrupted on multiplayer** — they render as entirely different blocks after batched updates, while singleplayer works fine. Any late-registered mod can be affected; it is not specific to RP Tubes.

**The fix:** install [Block Limit Fix](https://modrinth.com/mod/block_limit_fix) on the **server only** (no config, no client install). It backports Mojang's official 1.19.4 patch. If tubes or machines ever look like the wrong block on your server, this is why.

---

## ✦ Credits

- **Eloraam** — creator of **RedPower 2**. The tube system, machines, textures, GUIs, and every design idea in this mod are her original work. RP Tubes is a tribute: a recreation of the pr5b2 transport system for players who never stopped missing it. All original art and design credit belongs to her.
- **SpaceToad & the BuildCraft team** — creators of **BuildCraft** and the original **Automatic Crafting Table** (AutoWorkbench). Its behavior and textures here are recreated from BuildCraft 2.2.14, the version every Tekkit Classic player remembers. BuildCraft is open source under the MMPL.
- **immibis** — creator of **Tubestuff** and the **Automatic Crafting Table Mk II**, recreated here from Tubestuff 49.1.2.
- **MrTJP & the ProjectRed team** — for faithfully carrying RedPower's wiring, logic gates, and world content forward on modern Minecraft for over a decade. RP Tubes is designed to sit alongside ProjectRed and complete the picture with the transport system it no longer includes — when ProjectRed is installed, recipes automatically use its red alloy and tin.

*The best transportation system Minecraft ever had, back where it belongs.*
