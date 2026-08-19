package com.rptubes.util;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import java.util.Random;

/**
 * ItemStack helpers papering over 1.7.10's nullable stacks — stands in for
 * ItemStack.isEmpty/ItemStackHelper/InventoryHelper from newer versions.
 */
public final class Stacks {
    private static final Random RANDOM = new Random();

    public static boolean isEmpty(ItemStack stack) {
        return stack == null || stack.getItem() == null || stack.stackSize <= 0;
    }

    /** Normalizes zero-size stacks to null after a shrink. */
    public static ItemStack orNull(ItemStack stack) {
        return isEmpty(stack) ? null : stack;
    }

    public static int size(ItemStack stack) {
        return isEmpty(stack) ? 0 : stack.stackSize;
    }

    /** ItemStackHelper.getAndSplit */
    public static ItemStack getAndSplit(ItemStack[] items, int slot, int count) {
        if (slot < 0 || slot >= items.length || isEmpty(items[slot]) || count <= 0) {
            return null;
        }
        ItemStack taken = items[slot].splitStack(count);
        if (items[slot].stackSize <= 0) {
            items[slot] = null;
        }
        return taken;
    }

    /** ItemStackHelper.getAndRemove */
    public static ItemStack getAndRemove(ItemStack[] items, int slot) {
        if (slot < 0 || slot >= items.length) {
            return null;
        }
        ItemStack taken = items[slot];
        items[slot] = null;
        return taken;
    }

    public static void saveAllItems(NBTTagCompound tag, ItemStack[] items) {
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < items.length; i++) {
            if (!isEmpty(items[i])) {
                NBTTagCompound entry = new NBTTagCompound();
                entry.setByte("Slot", (byte) i);
                items[i].writeToNBT(entry);
                list.appendTag(entry);
            }
        }
        tag.setTag("Items", list);
    }

    public static void loadAllItems(NBTTagCompound tag, ItemStack[] items) {
        for (int i = 0; i < items.length; i++) {
            items[i] = null;
        }
        NBTTagList list = tag.getTagList("Items", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound entry = list.getCompoundTagAt(i);
            int slot = entry.getByte("Slot") & 255;
            if (slot < items.length) {
                items[slot] = ItemStack.loadItemStackFromNBT(entry);
            }
        }
    }

    /** InventoryHelper.spawnItemStack */
    public static void spawnItemStack(World world, double x, double y, double z, ItemStack stack) {
        if (isEmpty(stack)) {
            return;
        }
        EntityItem entity = new EntityItem(world, x, y, z, stack);
        entity.motionX = RANDOM.nextGaussian() * 0.05;
        entity.motionY = RANDOM.nextGaussian() * 0.05 + 0.2;
        entity.motionZ = RANDOM.nextGaussian() * 0.05;
        world.spawnEntityInWorld(entity);
    }

    /** InventoryHelper.dropContents over a raw slot array */
    public static void dropAll(World world, int x, int y, int z, ItemStack[] items) {
        for (int i = 0; i < items.length; i++) {
            spawnItemStack(world, x + 0.5, y + 0.5, z + 0.5, items[i]);
            items[i] = null;
        }
    }

    /** InventoryHelper.dropInventoryItems over an IInventory */
    public static void dropInventory(World world, int x, int y, int z, net.minecraft.inventory.IInventory inv) {
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            spawnItemStack(world, x + 0.5, y + 0.5, z + 0.5, inv.getStackInSlot(i));
            inv.setInventorySlotContents(i, null);
        }
    }

    private Stacks() {
    }
}
