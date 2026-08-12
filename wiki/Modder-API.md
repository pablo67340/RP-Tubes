# Modder API

RP Tubes exposes a small, stable Java API for mod developers. Everything in
`com.rptubes.api`, plus the `com.rptubes.tube.ITubeConnectable` interface and the
`com.rptubes.tube.TubeItem` class, is public API and will not break within a major
version. Anything else is internal.

Add the RP Tubes jar as a `compileOnly` dependency (e.g. via
[CurseMaven](https://cursemaven.com/)) and guard your integration behind
`ModList.get().isLoaded("rptubes")`.

## Level 0 — you probably need nothing

Tubes connect to and deliver into **any block entity that exposes the Forge
`IItemHandler` capability** (`ForgeCapabilities.ITEM_HANDLER`) on the touching face.
Transposers, Filters, Retrievers, and Regulators pull from the same capability.
If your machine already works with hoppers and pipes from other mods, it already
works with RP Tubes — no code required.

## Level 1 — `RPTubesApi` (send items into the network)

`com.rptubes.api.RPTubesApi` is a static facade:

```java
import com.rptubes.api.RPTubesApi;

// From your block at `pos`, push a stack into the tube touching its NORTH face.
// Routing picks the nearest valid destination automatically.
boolean accepted = RPTubesApi.injectItem(level, pos, Direction.NORTH, stack);
if (accepted) {
    stack.setCount(0); // a copy is now traveling; consume your original
}

// Same, but painted lime — Filters and Sorting Machines route it by color.
RPTubesApi.injectItem(level, pos, Direction.NORTH, stack, DyeColor.LIME);
```

| Method | Purpose |
|---|---|
| `injectItem(level, sourcePos, toward, stack)` | Send a stack from your block into an adjacent tube, unpainted. Returns `false` (nothing consumed) if there is no tube or no reachable destination with room. |
| `injectItem(level, sourcePos, toward, stack, DyeColor)` | Same, with a paint color for color routing. |
| `isTubeConnectable(level, pos)` | True if the block entity at `pos` is part of the tube network. |
| `canConnect(level, pos, side)` | True if the tube/machine at `pos` connects toward `side` — the same check used for models and routing. |
| `paintValue(DyeColor)` | Dye → tube paint byte (`0` unpainted, `1–16` = `DyeColor.getId() + 1`). |

All injection is server-side only; items never spill — if the network can't take the
stack, `injectItem` returns `false` and your inventory is untouched. This is exactly
the behavior of the Ejector block.

## Level 2 — `ITubeConnectable` (become part of the network)

Implement `com.rptubes.tube.ITubeConnectable` on your `BlockEntity` to make tubes
physically connect to your block and hand you traveling items:

```java
public class MyMachineBlockEntity extends BlockEntity implements ITubeConnectable {
    @Override
    public int getTubeConnectableSides() {
        // Bitmask by Direction ordinal: bit 0 = DOWN ... bit 5 = EAST.
        return 1 << Direction.UP.ordinal(); // tubes connect on top only
    }

    @Override
    public int getTubeConClass() {
        return 0; // 0 = standard pneumatic tube class; always use 0
    }

    @Override
    public boolean canRouteItems() {
        return false; // true only for actual tubes that pass items through
    }

    @Override
    public boolean tubeItemCanEnter(int side, int state, TubeItem item) {
        // Would you accept this item arriving on `side` right now?
        return side == Direction.UP.ordinal() && hasRoomFor(item.item);
    }

    @Override
    public boolean tubeItemEnter(int side, int state, TubeItem item) {
        // Take ownership of the item. Return false to reject (it bounces back).
        return acceptDelivery(item.item);
    }

    @Override
    public int tubeWeight(int side, int state) {
        return 0; // extra path cost; Restriction Tubes return a huge number here
    }
}
```

Notes:

- `TubeItem` carries the `ItemStack` (`item`), the paint color (`color`, `0` = none),
  and transit state. Treat other fields as read-only.
- The `state` parameter mirrors original RedPower semantics: `1` is a normal delivery
  pass; `3` is the bounce/return pass looking for a machine willing to take back an
  undeliverable item. Machines with an internal overflow buffer (like the Transposer)
  accept on state `3`.
- Endpoint machines (not tubes) should return `false` from `canRouteItems()`; the
  router then never paths *through* your block.
- If you implement `ITubeConnectable` **and** expose an `IItemHandler` on the same
  face, the tube-aware path wins for tube deliveries.

## Alloy Furnace recipes from code

The Alloy Furnace uses a standard Forge recipe type, `rptubes:alloy_smelting`
(`com.rptubes.recipe.AlloyRecipe`). Prefer adding recipes with datapack JSON — see
[Datapack Recipes & Tags](Datapack-Recipes-and-Tags) — they show up in JEI
automatically.
