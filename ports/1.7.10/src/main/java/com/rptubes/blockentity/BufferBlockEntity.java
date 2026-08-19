package com.rptubes.blockentity;

import com.rptubes.menu.BufferMenu;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.util.BlockPos;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import com.rptubes.util.inv.IItemHandler;
import com.rptubes.util.inv.InvWrapper;

/** Port of TileBufferChest — 20-slot storage accessible from all sides. */
public class BufferBlockEntity extends CompatTileEntity implements ISidedInventory, InvDefaults, com.rptubes.util.inv.Handlers.IItemHandlerProvider {
    private ItemStack[] items = new ItemStack[20];
    private final IItemHandler handler = new InvWrapper(this);
    private static final int[] ALL_SLOTS = new int[20];

    static {
        for (int i = 0; i < 20; i++) {
            ALL_SLOTS[i] = i;
        }
    }

    public BufferBlockEntity() {
        super();
    }

        public IChatComponent getDisplayName() {
        return new ChatComponentTranslation("block.rptubes.buffer");
    }

        public Container createMenu(InventoryPlayer inv) {
        return new BufferMenu(inv, this);
    }

    @Override
    public int getSizeInventory() {
        return 20;
    }

        public boolean isEmpty() {
        for (ItemStack stack : this.items) {
            if (!com.rptubes.util.Stacks.isEmpty(stack)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.items[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        return com.rptubes.util.Stacks.getAndSplit(this.items, slot, amount);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return com.rptubes.util.Stacks.getAndRemove(this.items, slot);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        this.items[slot] = stack;
        this.markDirty();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.getWorld() != null
                && this.getWorld().getTileEntity(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ()) == this
                && player.getDistanceSq(this.getPos().getX() + 0.5, this.getPos().getY() + 0.5,
                this.getPos().getZ() + 0.5) <= 64.0;
    }

        public void clear() {
        java.util.Arrays.fill(this.items, null);
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return ALL_SLOTS;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, int side) {
        return true;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, int side) {
        return true;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        com.rptubes.util.Stacks.saveAllItems(tag, this.items);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.items = new ItemStack[20];
        com.rptubes.util.Stacks.loadAllItems(tag, this.items);
    }
    public com.rptubes.util.inv.IItemHandler getItemHandler(ForgeDirection side) {
        return this.handler;
    }

    public void dropContents() {
        if (this.getWorld() != null) {
            com.rptubes.util.Stacks.dropInventory(this.getWorld(), this.xCoord, this.yCoord, this.zCoord, this);
        }
    }
}
