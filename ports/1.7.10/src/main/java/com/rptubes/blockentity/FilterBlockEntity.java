package com.rptubes.blockentity;

import com.rptubes.menu.FilterMenu;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.tube.MachineLib;
import com.rptubes.tube.TubeItem;
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
import com.rptubes.util.inv.IItemHandler;
import com.rptubes.util.inv.SidedInvWrapper;

/**
 * Port of TileFilter — stack extract + optional filter slots + paint color.
 */
public class FilterBlockEntity extends TransposerBlockEntity implements ISidedInventory, InvDefaults, com.rptubes.util.inv.Handlers.IItemHandlerProvider {
    private ItemStack[] filterSlots = new ItemStack[9];
    private MachineLib.FilterMap filterMap;
    public byte color = 0;
    private final IItemHandler handler = new SidedInvWrapper(this, ForgeDirection.NORTH);

    public FilterBlockEntity() {
        super();
    }

    private void regenFilterMap() {
        ItemStack[] arr = this.filterSlots;
        this.filterMap = MachineLib.makeFilterMap(arr);
    }

    private MachineLib.FilterMap map() {
        if (this.filterMap == null) {
            this.regenFilterMap();
        }
        return this.filterMap;
    }

    @Override
    protected void addToBuffer(ItemStack stack) {
        this.buffer.addNewColor(stack, this.color & 0xFF);
    }

    @Override
    protected ItemStack extractFrom(BlockPos src, ForgeDirection extractSide) {
        MachineLib.FilterMap map = this.map();
        if (map.size() == 0) {
            return MachineLib.collectOneStack(this.getWorld(), src, extractSide, null);
        }
        int slot = MachineLib.matchAnyStack(map, this.getWorld(), src, extractSide);
        if (slot < 0) {
            return null;
        }
        return MachineLib.collectOneStack(this.getWorld(), src, extractSide, this.filterSlots[slot].copy());
    }

    @Override
    protected boolean suckFilter(ItemStack stack) {
        MachineLib.FilterMap map = this.map();
        return map.size() == 0 || map.containsKey(stack);
    }

    @Override
    public boolean tubeItemEnter(int side, int state, TubeItem ti) {
        ForgeDirection back = this.getFacing().getOpposite();
        if (side == back.ordinal() && state == 1) {
            MachineLib.FilterMap map = this.map();
            if (map.size() > 0 && !map.containsKey(ti.item)) {
                return false;
            }
        }
        return super.tubeItemEnter(side, state, ti);
    }

    @Override
    public boolean tubeItemCanEnter(int side, int state, TubeItem ti) {
        ForgeDirection back = this.getFacing().getOpposite();
        if (side == back.ordinal() && state == 1) {
            MachineLib.FilterMap map = this.map();
            if (map.size() > 0 && !map.containsKey(ti.item)) {
                return false;
            }
        }
        return super.tubeItemCanEnter(side, state, ti);
    }

    public boolean applyColor(int dyeColor) {
        // 1-16 dye colors; 0 = unpainted
        byte next = (byte) ((dyeColor % 16) + 1);
        if (this.color == next) {
            return false;
        }
        this.color = next;
        this.markDirty();
        return true;
    }

    @Override
    public int getSizeInventory() {
        return 9;
    }

        public boolean isEmpty() {
        for (ItemStack stack : this.filterSlots) {
            if (!com.rptubes.util.Stacks.isEmpty(stack)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.filterSlots[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        ItemStack result = com.rptubes.util.Stacks.getAndSplit(this.filterSlots, slot, amount);
        if (!com.rptubes.util.Stacks.isEmpty(result)) {
            this.filterMap = null;
            this.markDirty();
        }
        return result;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        this.filterMap = null;
        return com.rptubes.util.Stacks.getAndRemove(this.filterSlots, slot);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        this.filterSlots[slot] = stack;
        this.filterMap = null;
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
        java.util.Arrays.fill(this.filterSlots, null);
        this.filterMap = null;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        ForgeDirection face = this.getFacing();
        if (side == face.ordinal() || side == face.getOpposite().ordinal()) {
            return new int[0];
        }
        return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, int side) {
        return true;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, int side) {
        return true;
    }

        public IChatComponent getDisplayName() {
        return new ChatComponentTranslation("block.rptubes.filter");
    }

        public Container createMenu(InventoryPlayer inv) {
        return new FilterMenu(inv, this);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        com.rptubes.util.Stacks.saveAllItems(tag, this.filterSlots);
        tag.setByte("color", this.color);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.filterSlots = new ItemStack[9];
        com.rptubes.util.Stacks.loadAllItems(tag, this.filterSlots);
        this.color = tag.getByte("color");
        this.filterMap = null;
    }
    public com.rptubes.util.inv.IItemHandler getItemHandler(ForgeDirection side) {
        return this.handler;
    }

    public void dropContents() {
        for (ItemStack stack : this.filterSlots) {
            if (!com.rptubes.util.Stacks.isEmpty(stack) && this.getWorld() != null) {
                com.rptubes.util.Stacks.spawnItemStack(this.getWorld(),
                        this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), stack);
            }
        }
    }
}
