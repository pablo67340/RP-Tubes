package com.rptubes.tube;

import com.rptubes.blockentity.AutoCraftingTableBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

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

    public static IItemHandler getHandler(World world, BlockPos pos, EnumFacing side) {
        TileEntity be = world.getTileEntity(pos);
        if (be == null) {
            return null;
        }
        return be.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
    }

    public static boolean canInsert(World world, BlockPos pos, EnumFacing side, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        IItemHandler handler = getHandler(world, pos, side);
        if (handler == null) {
            return false;
        }
        ItemStack leftover = ItemHandlerHelper.insertItem(handler, stack.copy(), true);
        return leftover.getCount() < stack.getCount();
    }

    public static ItemStack insert(World world, BlockPos pos, EnumFacing side, ItemStack stack, boolean simulate) {
        IItemHandler handler = getHandler(world, pos, side);
        if (handler == null) {
            return stack;
        }
        return ItemHandlerHelper.insertItem(handler, stack, simulate);
    }

    public static boolean insertAll(World world, BlockPos pos, EnumFacing side, ItemStack stack) {
        ItemStack leftover = insert(world, pos, side, stack, false);
        return leftover.isEmpty();
    }

    public static void ejectItem(World world, BlockPos pos, ItemStack stack, EnumFacing dir) {
        if (stack.isEmpty()) {
            return;
        }
        double x = pos.getX() + 0.5 + dir.getXOffset() * 0.6;
        double y = pos.getY() + 0.5 + dir.getYOffset() * 0.6;
        double z = pos.getZ() + 0.5 + dir.getZOffset() * 0.6;
        EntityItem entity = new EntityItem(world, x, y, z, stack.copy());
        entity.motionX = dir.getXOffset() * 0.2;
        entity.motionY = dir.getYOffset() * 0.2;
        entity.motionZ = dir.getZOffset() * 0.2;
        world.spawnEntity(entity);
    }

    /** Extract one item (count 1) from the first non-empty slot. */
    public static ItemStack extractOne(World world, BlockPos pos, EnumFacing side) {
        // Automatic Crafting Table: one pull = one atomic craft (may be >1 item,
        // e.g. 4 planks), matching the OG wooden pipe behavior.
        if (world.getTileEntity(pos) instanceof AutoCraftingTableBlockEntity) {
            AutoCraftingTableBlockEntity act = (AutoCraftingTableBlockEntity) world.getTileEntity(pos);
            return act.craftOne(true, false);
        }
        IItemHandler handler = getHandler(world, pos, side);
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
    public static ItemStack collectOneStack(World world, BlockPos pos, EnumFacing side, ItemStack match) {
        // Automatic Crafting Table: filters pull whole crafts of the matching result
        if (world.getTileEntity(pos) instanceof AutoCraftingTableBlockEntity) {
            AutoCraftingTableBlockEntity act = (AutoCraftingTableBlockEntity) world.getTileEntity(pos);
            ItemStack preview = act.findRecipe();
            if (preview.isEmpty()) {
                return ItemStack.EMPTY;
            }
            if (match.isEmpty() || (preview.isItemEqual(match) && ItemStack.areItemStackTagsEqual(preview, match))) {
                return act.craftOne(true, false);
            }
            return ItemStack.EMPTY;
        }
        IItemHandler handler = getHandler(world, pos, side);
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
            if (stack.isEmpty() || !(match.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(match, stack))) {
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

    public static int matchAnyStack(FilterMap map, World world, BlockPos pos, EnumFacing side) {
        IItemHandler handler = getHandler(world, pos, side);
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
    public static int matchAnyStackCol(FilterMap map, World world, BlockPos pos, EnumFacing side, int column) {
        IItemHandler handler = getHandler(world, pos, side);
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
    public static boolean matchAllCol(FilterMap map, World world, BlockPos pos, EnumFacing side, int column) {
        IItemHandler handler = getHandler(world, pos, side);
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
                if (!stack.isEmpty() && (want.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(want, stack))) {
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
    public static boolean handleItem(World world, TubeItem ti, BlockPos pos, EnumFacing facing) {
        if (ti.item.isEmpty()) {
            return true;
        }
        BlockPos dest = pos.offset(facing);
        EnumFacing enter = facing.getOpposite();

        if (TubeLib.addToTubeRoute(world, ti, pos, dest, enter)) {
            return true;
        }

        ItemStack leftover = insert(world, dest, enter, ti.item.copy(), false);
        if (leftover.isEmpty()) {
            ti.item = ItemStack.EMPTY;
            return true;
        }
        if (leftover.getCount() < ti.item.getCount()) {
            ti.item = leftover;
            return false;
        }

        TileEntity be = world.getTileEntity(dest);
        if (be instanceof ITubeConnectable || getHandler(world, dest, enter) != null) {
            return false;
        }
        IBlockState state = world.getBlockState(dest);
        if (state.isSideSolid(world, dest, enter)) {
            return false;
        }
        ejectItem(world, pos, ti.item, facing);
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
            return stack.getItem().toString() + "|" + (stack.hasTagCompound() ? stack.getTagCompound().toString() : "");
        }
    }
}
