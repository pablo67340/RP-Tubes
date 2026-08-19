package com.rptubes.tube;

import com.rptubes.blockentity.AutoCraftingTableBlockEntity;
import com.rptubes.util.BlockPos;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import com.rptubes.util.inv.IItemHandler;
import com.rptubes.util.inv.Handlers;

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

    public static IItemHandler getHandler(World world, BlockPos pos, ForgeDirection side) {
        TileEntity be = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
        if (be == null) {
            return null;
        }
        return com.rptubes.util.inv.Handlers.wrap(be, side);
    }

    public static boolean canInsert(World world, BlockPos pos, ForgeDirection side, ItemStack stack) {
        if (com.rptubes.util.Stacks.isEmpty(stack)) {
            return false;
        }
        IItemHandler handler = getHandler(world, pos, side);
        if (handler == null) {
            return false;
        }
        ItemStack leftover = Handlers.insertItem(handler, stack.copy(), true);
        return leftover.stackSize < stack.stackSize;
    }

    public static ItemStack insert(World world, BlockPos pos, ForgeDirection side, ItemStack stack, boolean simulate) {
        IItemHandler handler = getHandler(world, pos, side);
        if (handler == null) {
            return stack;
        }
        return Handlers.insertItem(handler, stack, simulate);
    }

    public static boolean insertAll(World world, BlockPos pos, ForgeDirection side, ItemStack stack) {
        ItemStack leftover = insert(world, pos, side, stack, false);
        return com.rptubes.util.Stacks.isEmpty(leftover);
    }

    public static void ejectItem(World world, BlockPos pos, ItemStack stack, ForgeDirection dir) {
        if (com.rptubes.util.Stacks.isEmpty(stack)) {
            return;
        }
        double x = pos.getX() + 0.5 + dir.offsetX * 0.6;
        double y = pos.getY() + 0.5 + dir.offsetY * 0.6;
        double z = pos.getZ() + 0.5 + dir.offsetZ * 0.6;
        EntityItem entity = new EntityItem(world, x, y, z, stack.copy());
        entity.motionX = dir.offsetX * 0.2;
        entity.motionY = dir.offsetY * 0.2;
        entity.motionZ = dir.offsetZ * 0.2;
        world.spawnEntityInWorld(entity);
    }

    /** Extract one item (count 1) from the first non-empty slot. */
    public static ItemStack extractOne(World world, BlockPos pos, ForgeDirection side) {
        // Automatic Crafting Table: one pull = one atomic craft (may be >1 item,
        // e.g. 4 planks), matching the OG wooden pipe behavior.
        if (world.getTileEntity(pos.getX(), pos.getY(), pos.getZ()) instanceof AutoCraftingTableBlockEntity) {
            AutoCraftingTableBlockEntity act = (AutoCraftingTableBlockEntity) world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
            return act.craftOne(true, false);
        }
        IItemHandler handler = getHandler(world, pos, side);
        if (handler == null) {
            return null;
        }
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack extracted = handler.extractItem(i, 1, false);
            if (!com.rptubes.util.Stacks.isEmpty(extracted)) {
                return extracted;
            }
        }
        return null;
    }

    /**
     * Collect up to {@code match.stackSize} items matching {@code match}.
     * If match is empty, pull one full stack from the first slot.
     */
    public static ItemStack collectOneStack(World world, BlockPos pos, ForgeDirection side, ItemStack match) {
        // Automatic Crafting Table: filters pull whole crafts of the matching result
        if (world.getTileEntity(pos.getX(), pos.getY(), pos.getZ()) instanceof AutoCraftingTableBlockEntity) {
            AutoCraftingTableBlockEntity act = (AutoCraftingTableBlockEntity) world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
            ItemStack preview = act.findRecipe();
            if (com.rptubes.util.Stacks.isEmpty(preview)) {
                return null;
            }
            if (com.rptubes.util.Stacks.isEmpty(match) || (preview.isItemEqual(match) && ItemStack.areItemStackTagsEqual(preview, match))) {
                return act.craftOne(true, false);
            }
            return null;
        }
        IItemHandler handler = getHandler(world, pos, side);
        if (handler == null) {
            return null;
        }
        if (com.rptubes.util.Stacks.isEmpty(match)) {
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (!com.rptubes.util.Stacks.isEmpty(stack)) {
                    return handler.extractItem(i, stack.stackSize, false);
                }
            }
            return null;
        }
        int need = match.stackSize;
        ItemStack result = null;
        for (int i = 0; i < handler.getSlots() && need > 0; i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (com.rptubes.util.Stacks.isEmpty(stack) || !(match.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(match, stack))) {
                continue;
            }
            ItemStack took = handler.extractItem(i, need, false);
            if (com.rptubes.util.Stacks.isEmpty(took)) {
                continue;
            }
            if (com.rptubes.util.Stacks.isEmpty(result)) {
                result = took;
            } else {
                result.stackSize += took.stackSize;
            }
            need -= took.stackSize;
        }
        return result;
    }

    public static int matchAnyStack(FilterMap map, World world, BlockPos pos, ForgeDirection side) {
        IItemHandler handler = getHandler(world, pos, side);
        if (handler == null || map.size() == 0) {
            return -1;
        }
        int[] counts = new int[map.filter.length];
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (com.rptubes.util.Stacks.isEmpty(stack)) {
                continue;
            }
            List<Integer> slots = map.getSlots(stack);
            if (slots == null) {
                continue;
            }
            for (int idx : slots) {
                counts[idx] += stack.stackSize;
                if (counts[idx] >= map.filter[idx].stackSize) {
                    return idx;
                }
            }
        }
        return -1;
    }

    /** OG matchAnyStackCol — like matchAnyStack but only filter slots in the given column (slot & 7). */
    public static int matchAnyStackCol(FilterMap map, World world, BlockPos pos, ForgeDirection side, int column) {
        IItemHandler handler = getHandler(world, pos, side);
        if (handler == null || map.size() == 0) {
            return -1;
        }
        int[] counts = new int[map.filter.length];
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (com.rptubes.util.Stacks.isEmpty(stack)) {
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
                counts[idx] += stack.stackSize;
                if (counts[idx] >= map.filter[idx].stackSize) {
                    return idx;
                }
            }
        }
        return -1;
    }

    /** OG matchAllCol — true if the inventory holds every (non-empty) filter stack of the column. */
    public static boolean matchAllCol(FilterMap map, World world, BlockPos pos, ForgeDirection side, int column) {
        IItemHandler handler = getHandler(world, pos, side);
        if (handler == null) {
            return false;
        }
        boolean any = false;
        for (int row = 0; row * 8 + column < map.filter.length; row++) {
            ItemStack want = map.filter[row * 8 + column];
            if (want == null || com.rptubes.util.Stacks.isEmpty(want)) {
                continue;
            }
            any = true;
            int have = 0;
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (!com.rptubes.util.Stacks.isEmpty(stack) && (want.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(want, stack))) {
                    have += stack.stackSize;
                }
            }
            if (have < want.stackSize) {
                return false;
            }
        }
        return any;
    }

    /**
     * Push an item out the machine's facing: into a tube, into an inventory, or eject into world.
     */
    public static boolean handleItem(World world, TubeItem ti, BlockPos pos, ForgeDirection facing) {
        if (com.rptubes.util.Stacks.isEmpty(ti.item)) {
            return true;
        }
        BlockPos dest = pos.offset(facing);
        ForgeDirection enter = facing.getOpposite();

        if (TubeLib.addToTubeRoute(world, ti, pos, dest, enter)) {
            return true;
        }

        ItemStack leftover = insert(world, dest, enter, ti.item.copy(), false);
        if (com.rptubes.util.Stacks.isEmpty(leftover)) {
            ti.item = null;
            return true;
        }
        if (leftover.stackSize < ti.item.stackSize) {
            ti.item = leftover;
            return false;
        }

        TileEntity be = world.getTileEntity(dest.getX(), dest.getY(), dest.getZ());
        if (be instanceof ITubeConnectable || getHandler(world, dest, enter) != null) {
            return false;
        }
        if (world.isSideSolid(dest.getX(), dest.getY(), dest.getZ(), enter)) {
            return false;
        }
        ejectItem(world, pos, ti.item, facing);
        ti.item = null;
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
                if (stack == null || com.rptubes.util.Stacks.isEmpty(stack)) {
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
            return !com.rptubes.util.Stacks.isEmpty(stack) && this.map.containsKey(keyOf(stack));
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
