package com.rptubes.blockentity;

import com.rptubes.block.MachineBlock;
import com.rptubes.menu.ItemDetectorMenu;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.tube.ITubeConnectable;
import com.rptubes.tube.MachineLib;
import com.rptubes.tube.TubeBuffer;
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
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;

/** Port of TileItemDetect — pass-through tube machine that emits redstone when items pass. */
public class ItemDetectorBlockEntity extends CompatTileEntity implements ITubeConnectable, ISidedInventory, InvDefaults {
    private final TubeBuffer buffer = new TubeBuffer();
    private ItemStack[] filterSlots = new ItemStack[9];
    private MachineLib.FilterMap filterMap;
    private boolean active;
    private boolean emitting;
    /** OG mode: 0 = pulse per item, 1 = pulse per stack, 2 = powered while plugged. */
    public byte mode;
    /** Pending pulses (OG count). */
    private int count;
    /** OG Delay — spaces the pulse train into 2 ticks on / 2 ticks off. */
    private boolean delay;

    public ItemDetectorBlockEntity() {
        super();
    }

    public static void serverTick(World world, BlockPos pos, ItemDetectorBlockEntity be) {
        if (!be.buffer.isEmpty()) {
            be.drainBuffer();
        }
        // OG n_(): emit one 2-tick pulse per counted unit, 2-tick gap between pulses
        if (be.mode != 2) {
            if (be.emitting) {
                if (be.delay) {
                    be.delay = false;
                } else {
                    if (be.count > 0) {
                        be.delay = true;
                    }
                    be.setEmitting(false);
                }
            } else if (be.count > 0) {
                if (be.delay) {
                    be.delay = false;
                } else {
                    --be.count;
                    be.delay = true;
                    be.setEmitting(true);
                }
            }
        }
        if (be.buffer.isEmpty() && be.active && !be.emitting && be.count == 0) {
            be.setActive(false);
        }
    }

    private void regenFilterMap() {
        this.filterMap = MachineLib.makeFilterMap(this.filterSlots);
    }

    private MachineLib.FilterMap map() {
        if (this.filterMap == null) {
            this.regenFilterMap();
        }
        return this.filterMap;
    }

    private boolean matchesFilter(ItemStack stack) {
        MachineLib.FilterMap map = this.map();
        return map.size() == 0 || map.containsKey(stack);
    }

    /** OG counting: mode 0 adds the stack size, mode 1 adds one pulse per stack. */
    private void onItemPassed(ItemStack stack) {
        if (this.matchesFilter(stack)) {
            if (this.mode == 0) {
                this.count += stack.stackSize;
            } else if (this.mode == 1) {
                ++this.count;
            }
            this.markDirty();
        }
    }

    public boolean isEmitting() {
        return this.emitting;
    }

    private void setEmitting(boolean value) {
        if (this.emitting == value) {
            return;
        }
        this.emitting = value;
        if (this.getWorld() != null) {
            net.minecraft.block.Block block = this.getWorld().getBlock(this.xCoord, this.yCoord, this.zCoord);
            this.getWorld().notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, block);
            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                BlockPos np = this.getPos().offset(dir);
                this.getWorld().notifyBlocksOfNeighborChange(np.getX(), np.getY(), np.getZ(), block);
            }
        }
        this.markDirty();
    }

    private void drainBuffer() {
        while (!this.buffer.isEmpty()) {
            TubeItem ti = this.buffer.getLast();
            if (!MachineLib.handleItem(this.getWorld(), ti, this.getPos(), this.getFacing())) {
                this.buffer.plugged = true;
                this.onPlugged();
                return;
            }
            this.buffer.pop();
            if (this.buffer.plugged) {
                this.onPlugged();
                return;
            }
        }
        // OG mode 2: signal follows the plugged state — clear once the buffer empties
        if (this.mode == 2 && this.emitting) {
            this.setEmitting(false);
        }
    }

    /** OG mode 2: latch the signal on while an item is stuck inside. */
    private void onPlugged() {
        if (this.mode == 2 && !this.emitting) {
            this.delay = false;
            this.count = 0;
            this.setEmitting(true);
        }
    }

    public ForgeDirection getFacing() {
        return MachineBlock.getFacing(this.getWorld().getBlockMetadata(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ()));
    }

    private void setActive(boolean value) {
        this.active = value;
        if (this.getWorld() != null) {
            MachineBlock.setActive(this.getWorld(), this.xCoord, this.yCoord, this.zCoord, value);
        }
        this.markDirty();
    }

    @Override
    public int getTubeConnectableSides() {
        ForgeDirection face = this.getFacing();
        return (1 << face.ordinal()) | (1 << face.getOpposite().ordinal());
    }

    @Override
    public int getTubeConClass() {
        return 0;
    }

    @Override
    public boolean canRouteItems() {
        return false;
    }

    @Override
    public boolean tubeItemEnter(int side, int state, TubeItem ti) {
        ForgeDirection face = this.getFacing();
        if (side == face.ordinal() && state == 2) {
            this.buffer.addBounce(ti);
            this.setActive(true);
            return true;
        }
        if (side == face.getOpposite().ordinal() && state == 1) {
            if (!this.buffer.isEmpty()) {
                return false;
            }
            this.onItemPassed(ti.item);
            this.buffer.add(ti);
            this.setActive(true);
            this.drainBuffer();
            return true;
        }
        return false;
    }

    @Override
    public boolean tubeItemCanEnter(int side, int state, TubeItem ti) {
        ForgeDirection face = this.getFacing();
        if (side == face.ordinal() && state == 2) {
            return true;
        }
        if (side == face.getOpposite().ordinal() && state == 1) {
            return this.buffer.isEmpty();
        }
        return false;
    }

    @Override
    public int tubeWeight(int side, int state) {
        ForgeDirection face = this.getFacing();
        if (side == face.ordinal() && state == 2) {
            return this.buffer.size();
        }
        return 0;
    }

        public IChatComponent getDisplayName() {
        return new ChatComponentTranslation("block.rptubes.item_detector");
    }

        public Container createMenu(InventoryPlayer inv) {
        return new ItemDetectorMenu(inv, this);
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

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        com.rptubes.util.Stacks.saveAllItems(tag, this.filterSlots);
        this.buffer.writeToNBT(tag);
        tag.setBoolean("Active", this.active);
        tag.setBoolean("Emitting", this.emitting);
        tag.setByte("mode", this.mode);
        tag.setInteger("cnt", this.count);
        tag.setBoolean("Delay", this.delay);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.filterSlots = new ItemStack[9];
        com.rptubes.util.Stacks.loadAllItems(tag, this.filterSlots);
        this.buffer.readFromNBT(tag);
        this.active = tag.getBoolean("Active");
        this.emitting = tag.getBoolean("Emitting");
        this.mode = tag.getByte("mode");
        this.count = tag.getInteger("cnt");
        this.delay = tag.getBoolean("Delay");
        this.filterMap = null;
    }

    public void dropContents() {
        this.buffer.onRemove(this);
        for (ItemStack stack : this.filterSlots) {
            if (!com.rptubes.util.Stacks.isEmpty(stack) && this.getWorld() != null) {
                com.rptubes.util.Stacks.spawnItemStack(this.getWorld(),
                        this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), stack);
            }
        }
    }

    @Override
    public void updateEntity() {
        if (this.getWorld() == null || this.getWorld().isRemote) {
            return;
        }
        serverTick(this.getWorld(), this.getPos(), this);
    }
}
