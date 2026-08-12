# Datapack Recipes & Tags

Everything a modpack or datapack author can hook into without writing Java.

## Alloy Smelting recipes (`rptubes:alloy_smelting`)

Alloy Furnace recipes are plain JSON files under
`data/<namespace>/recipes/`. Ingredients are consumed **by count** from anywhere in
the 3×3 input grid — order and placement don't matter. They appear in JEI's Alloy
Smelting category automatically.

```json
{
  "type": "rptubes:alloy_smelting",
  "ingredients": [
    { "tag": "forge:ingots/copper", "count": 3 },
    { "tag": "forge:ingots/tin", "count": 1 }
  ],
  "result": {
    "item": "rptubes:brass_ingot",
    "count": 4
  },
  "cookTime": 200
}
```

- `ingredients` — array of vanilla `Ingredient` JSON (`item` or `tag`), each with an
  optional `count` (default `1`).
- `result` — Forge item stack JSON (`item`, optional `count` and `nbt`).
- `cookTime` — ticks per smelt, optional, default `200` (10 seconds).
- Standard Forge `conditions` work. RP Tubes itself uses one to only register the
  Red Alloy recipe when ProjectRed Core is loaded:

```json
"conditions": [
  { "type": "forge:mod_loaded", "modid": "projectred_core" }
]
```

## Tags RP Tubes provides

| Tag | Type | Contents |
|---|---|---|
| `forge:ingots/tin` | item | Tin Ingot |
| `forge:ingots/brass` | item | Brass Ingot |
| `forge:ores/tin` | item + block | Tin Ore |
| `rptubes:act_pipes` | item | Pipes accepted in the Automatic Crafting Table Mk II recipe: Pneumatic Tube, plus BuildCraft CE's Wooden Pipe (optional entry) |

## Tags RP Tubes consumes

All material inputs in RP Tubes recipes are tag-based, so pack makers can substitute
freely:

- `forge:ingots/tin` — brass alloying. Any mod's tin satisfies it.
- `forge:ingots/copper`, `forge:ingots/iron`, etc. — standard Forge tags throughout.
- `rptubes:act_pipes` — add your favorite transport mod's pipe here to let it build
  the ACT Mk II:

```json
// data/rptubes/tags/items/act_pipes.json (in your datapack)
{
  "replace": false,
  "values": [
    { "id": "somepipemod:some_pipe", "required": false }
  ]
}
```

## Worldgen

Tin Ore placement is data-driven and can be overridden by datapacks:

- Configured/placed features: `rptubes:tin_ore` under
  `data/rptubes/worldgen/configured_feature/` and `placed_feature/`
- Biome modifier: `data/rptubes/forge/biome_modifier/add_tin_ore.json`

Default distribution matches original RedPower 2: 10 veins per chunk, below y48.
