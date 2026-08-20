package com.rptubes.tube;

import com.rptubes.blockentity.AutoCraftingTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Modern inventory helpers replacing eloraam.core.MachineLib.
 * Uses Forge ITEM_HANDLER for universal mod compatibility.
 */
public final class MachineLib {
    private MachineLib() {
    }

    public static IItemHandler getHandler(Level level, BlockPos pos, Direction side) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be == null) {
            return null;
        }
        return level.getCapability(Capabilities.ItemHandler.BLOCK, pos, side);
    }

    public static boolean canInsert(Level level, BlockPos pos, Direction side, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        IItemHandler handler = getHandler(level, pos, side);
        if (handler == null) {
            return false;
        }
        ItemStack leftover = ItemHandlerHelper.insertItem(handler, stack.copy(), true);
        return leftover.getCount() < stack.getCount();
    }

    public static ItemStack insert(Level level, BlockPos pos, Direction side, ItemStack stack, boolean simulate) {
        IItemHandler handler = getHandler(level, pos, side);
        if (handler == null) {
            return stack;
        }
        return ItemHandlerHelper.insertItem(handler, stack, simulate);
    }

    public static boolean insertAll(Level level, BlockPos pos, Direction side, ItemStack stack) {
        ItemStack leftover = insert(level, pos, side, stack, false);
        return leftover.isEmpty();
    }

    public static void ejectItem(Level level, BlockPos pos, ItemStack stack, Direction dir) {
        if (stack.isEmpty()) {
            return;
        }
        double x = pos.getX() + 0.5 + dir.getStepX() * 0.6;
        double y = pos.getY() + 0.5 + dir.getStepY() * 0.6;
        double z = pos.getZ() + 0.5 + dir.getStepZ() * 0.6;
        ItemEntity entity = new ItemEntity(level, x, y, z, stack.copy());
        entity.setDeltaMovement(dir.getStepX() * 0.2, dir.getStepY() * 0.2, dir.getStepZ() * 0.2);
        level.addFreshEntity(entity);
    }

    /** Extract one item (count 1) from the first non-empty slot. */
    public static ItemStack extractOne(Level level, BlockPos pos, Direction side) {
        // Automatic Crafting Table: one pull = one atomic craft (may be >1 item,
        // e.g. 4 planks), matching the OG wooden pipe behavior.
        if (level.getBlockEntity(pos) instanceof AutoCraftingTableBlockEntity act) {
            return act.craftOne(true, false);
        }
        IItemHandler handler = getHandler(level, pos, side);
        if (handler == null) {
            return ItemStack.EMPTY;
        }
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack extracted = handler.extractItem(i, 1, false);
            if (!extracted.isEmpty()) {
                return extracted;
            }
        }
        return ItemStack.EMPTY;
    }

    /**
     * Collect up to {@code match.getCount()} items matching {@code match}.
     * If match is empty, pull one full stack from the first slot.
     */
    public static ItemStack collectOneStack(Level level, BlockPos pos, Direction side, ItemStack match) {
        // Automatic Crafting Table: filters pull whole crafts of the matching result
        if (level.getBlockEntity(pos) instanceof AutoCraftingTableBlockEntity act) {
            ItemStack preview = act.findRecipe();
            if (preview.isEmpty()) {
                return ItemStack.EMPTY;
            }
            if (match.isEmpty() || ItemStack.isSameItemSameComponents(preview, match)) {
                return act.craftOne(true, false);
            }
            return ItemStack.EMPTY;
        }
        IItemHandler handler = getHandler(level, pos, side);
        if (handler == null) {
            return ItemStack.EMPTY;
        }
        if (match.isEmpty()) {
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    return handler.extractItem(i, stack.getCount(), false);
                }
            }
            return ItemStack.EMPTY;
        }
        int need = match.getCount();
        ItemStack result = ItemStack.EMPTY;
        for (int i = 0; i < handler.getSlots() && need > 0; i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (stack.isEmpty() || !ItemStack.isSameItemSameComponents(match, stack)) {
                continue;
            }
            ItemStack took = handler.extractItem(i, need, false);
            if (took.isEmpty()) {
                continue;
            }
            if (result.isEmpty()) {
                result = took;
            } else {
                result.grow(took.getCount());
            }
            need -= took.getCount();
        }
        return result;
    }

    public static int matchAnyStack(FilterMap map, Level level, BlockPos pos, Direction side) {
        IItemHandler handler = getHandler(level, pos, side);
        if (handler == null || map.size() == 0) {
            return -1;
        }
        int[] counts = new int[map.filter.length];
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (stack.isEmpty()) {
                continue;
            }
            List<Integer> slots = map.getSlots(stack);
            if (slots == null) {
                continue;
            }
            for (int idx : slots) {
                counts[idx] += stack.getCount();
                if (counts[idx] >= map.filter[idx].getCount()) {
                    return idx;
                }
            }
        }
        return -1;
    }

    /** OG matchAnyStackCol — like matchAnyStack but only filter slots in the given column (slot & 7). */
    public static int matchAnyStackCol(FilterMap map, Level level, BlockPos pos, Direction side, int column) {
        IItemHandler handler = getHandler(level, pos, side);
        if (handler == null || map.size() == 0) {
            return -1;
        }
        int[] counts = new int[map.filter.length];
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (stack.isEmpty()) {
                continue;
            }
            List<Integer> slots = map.getSlots(stack);
            if (slots == null) {
                continue;
            }
            for (int idx : slots) {
                if ((idx & 7) != column) {
                    continue;
                }
                counts[idx] += stack.getCount();
                if (counts[idx] >= map.filter[idx].getCount()) {
                    return idx;
                }
            }
        }
        return -1;
    }

    /** OG matchAllCol — true if the inventory holds every (non-empty) filter stack of the column. */
    public static boolean matchAllCol(FilterMap map, Level level, BlockPos pos, Direction side, int column) {
        IItemHandler handler = getHandler(level, pos, side);
        if (handler == null) {
            return false;
        }
        boolean any = false;
        for (int row = 0; row * 8 + column < map.filter.length; row++) {
            ItemStack want = map.filter[row * 8 + column];
            if (want == null || want.isEmpty()) {
                continue;
            }
            any = true;
            int have = 0;
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (!stack.isEmpty() && ItemStack.isSameItemSameComponents(want, stack)) {
                    have += stack.getCount();
                }
            }
            if (have < want.getCount()) {
                return false;
            }
        }
        return any;
    }

    /**
     * Push an item out the machine's facing: into a tube, into an inventory, or eject into world.
     */
    public static boolean handleItem(Level level, TubeItem ti, BlockPos pos, Direction facing) {
        if (ti.item.isEmpty()) {
            return true;
        }
        BlockPos dest = pos.relative(facing);
        Direction enter = facing.getOpposite();

        if (TubeLib.addToTubeRoute(level, ti, pos, dest, enter)) {
            return true;
        }

        ItemStack leftover = insert(level, dest, enter, ti.item.copy(), false);
        if (leftover.isEmpty()) {
            ti.item = ItemStack.EMPTY;
            return true;
        }
        if (leftover.getCount() < ti.item.getCount()) {
            ti.item = leftover;
            return false;
        }

        BlockEntity be = level.getBlockEntity(dest);
        if (be instanceof ITubeConnectable || getHandler(level, dest, enter) != null) {
            return false;
        }
        BlockState state = level.getBlockState(dest);
        if (state.isFaceSturdy(level, dest, enter)) {
            return false;
        }
        ejectItem(level, pos, ti.item, facing);
        ti.item = ItemStack.EMPTY;
        return true;
    }

    public static FilterMap makeFilterMap(ItemStack[] filter) {
        return new FilterMap(filter);
    }

    public static FilterMap makeFilterMap(ItemStack[] filter, int start, int len) {
        ItemStack[] slice = new ItemStack[len];
        System.arraycopy(filter, start, slice, 0, len);
        return new FilterMap(slice);
    }

    public static final class FilterMap {
        private final Map<String, List<Integer>> map = new HashMap<>();
        final ItemStack[] filter;

        public FilterMap(ItemStack[] filter) {
            this.filter = filter;
            for (int i = 0; i < filter.length; i++) {
                ItemStack stack = filter[i];
                if (stack == null || stack.isEmpty()) {
                    continue;
                }
                String key = keyOf(stack);
                this.map.computeIfAbsent(key, k -> new ArrayList<>()).add(i);
            }
        }

        public int size() {
            return this.map.size();
        }

        public boolean containsKey(ItemStack stack) {
            return !stack.isEmpty() && this.map.containsKey(keyOf(stack));
        }

        public List<Integer> getSlots(ItemStack stack) {
            return this.map.get(keyOf(stack));
        }

        public int firstMatch(ItemStack stack) {
            List<Integer> slots = getSlots(stack);
            if (slots == null || slots.isEmpty()) {
                return -1;
            }
            return slots.get(0);
        }

        public ItemStack[] getFilter() {
            return this.filter;
        }

        private static String keyOf(ItemStack stack) {
            return stack.getItem().toString() + "|" + stack.getComponentsPatch().toString();
        }
    }
}
