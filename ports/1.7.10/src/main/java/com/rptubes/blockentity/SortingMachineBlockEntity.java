package com.rptubes.blockentity;

import com.rptubes.menu.SortingMachineMenu;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.tube.MachineLib;
import com.rptubes.tube.TubeBuffer;
import com.rptubes.tube.TubeItem;
import com.rptubes.util.BlockPos;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

/**
 * Port of TileSorter — 8×5 filter columns, per-column paint colors, 5 modes,
 * 8 channel buffers drained round-robin. No blutricity in this port.
 *
 * Modes (OG): 0 = anystack sequential, 1 = allstacks sequential,
 * 2 = random allstacks, 3 = anystack any column, 4 = any item (default color).
 */
public class SortingMachineBlockEntity extends TransposerBlockEntity implements ISidedInventory, InvDefaults {
    public static final int FILTER_SIZE = 40;
    private ItemStack[] filterSlots = new ItemStack[FILTER_SIZE];
    public final byte[] colors = new byte[8];
    public byte mode;
    public byte defcolor;
    public byte column;
    private byte draining = -1;
    private final TubeBuffer[] channelBuffers = new TubeBuffer[8];
    private MachineLib.FilterMap filterMap;

    public SortingMachineBlockEntity() {
        super();
        for (int i = 0; i < 8; i++) {
            this.channelBuffers[i] = new TubeBuffer();
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

    /** @return column 0-7, -2 if filter empty, -1 if no match (OG getColumnMatch). */
    private int getColumnMatch(ItemStack stack) {
        MachineLib.FilterMap map = this.map();
        if (map.size() == 0) {
            return -2;
        }
        int idx = map.firstMatch(stack);
        if (idx < 0) {
            return idx;
        }
        return idx & 7;
    }

    /** OG: matched items go to their column's channel buffer in modes 2-4. */
    private TubeBuffer bufferFor(int columnMatch) {
        if (columnMatch >= 0 && this.mode > 1) {
            return this.channelBuffers[columnMatch];
        }
        return this.buffer;
    }

    @Override
    protected void addToBuffer(ItemStack stack) {
        int cm = this.getColumnMatch(stack);
        TubeBuffer buf = this.bufferFor(cm);
        if (cm < 0) {
            buf.addNewColor(stack, this.mode == 4 ? this.defcolor & 0xFF : 0);
            return;
        }
        buf.addNewColor(stack, this.colors[cm] & 0xFF);
    }

    private boolean tryDrainBuffer(TubeBuffer buf) {
        if (buf.isEmpty()) {
            return false;
        }
        while (!buf.isEmpty()) {
            TubeItem ti = buf.getLast();
            if (!MachineLib.handleItem(this.getWorld(), ti, this.getPos(), this.getFacing())) {
                buf.plugged = true;
                return true;
            }
            buf.pop();
            if (buf.plugged) {
                return true;
            }
        }
        return true;
    }

    /** OG tryDrainBuffer — round-robin over the 8 channels plus the main buffer. */
    @Override
    public void drainBuffer() {
        for (int i = 0; i < 9; i++) {
            TubeBuffer buf;
            this.draining = (byte) (this.draining + 1);
            if (this.draining > 7) {
                this.draining = -1;
                buf = this.buffer;
            } else {
                buf = this.channelBuffers[this.draining];
            }
            if (this.tryDrainBuffer(buf)) {
                return;
            }
        }
    }

    @Override
    protected boolean hasBuffered() {
        if (!this.buffer.isEmpty()) {
            return true;
        }
        for (int i = 0; i < 8; i++) {
            if (!this.channelBuffers[i].isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void stepColumn() {
        for (int i = 0; i < 8; i++) {
            this.column = (byte) ((this.column + 1) % 8);
            for (int row = 0; row < 5; row++) {
                if (!com.rptubes.util.Stacks.isEmpty(this.filterSlots[row * 8 + this.column])) {
                    return;
                }
            }
        }
        this.column = 0;
    }

    private void checkColumn() {
        for (int row = 0; row < 5; row++) {
            if (!com.rptubes.util.Stacks.isEmpty(this.filterSlots[row * 8 + this.column])) {
                return;
            }
        }
        this.stepColumn();
        this.markDirty();
    }

    /** OG handleExtract — the five sorting modes. */
    @Override
    protected boolean handleExtract(BlockPos src, ForgeDirection extractSide) {
        MachineLib.FilterMap map = this.map();
        if (map.size() == 0) {
            ItemStack ist = MachineLib.collectOneStack(this.getWorld(), src, extractSide, null);
            if (com.rptubes.util.Stacks.isEmpty(ist)) {
                return false;
            }
            if (this.mode == 4) {
                this.buffer.addNewColor(ist, this.defcolor & 0xFF);
            } else {
                this.buffer.addNew(ist);
            }
            this.drainBuffer();
            return true;
        }
        switch (this.mode) {
            case 0: {
                this.checkColumn();
                int sm = MachineLib.matchAnyStackCol(map, this.getWorld(), src, extractSide, this.column);
                if (sm < 0) {
                    return false;
                }
                ItemStack coll = MachineLib.collectOneStack(this.getWorld(), src, extractSide,
                        this.filterSlots[sm].copy());
                this.buffer.addNewColor(coll, this.colors[sm & 7] & 0xFF);
                this.stepColumn();
                this.drainBuffer();
                return true;
            }
            case 1: {
                this.checkColumn();
                if (!MachineLib.matchAllCol(map, this.getWorld(), src, extractSide, this.column)) {
                    return false;
                }
                for (int row = 0; row < 5; row++) {
                    ItemStack match = this.filterSlots[row * 8 + this.column];
                    if (com.rptubes.util.Stacks.isEmpty(match)) {
                        continue;
                    }
                    ItemStack coll = MachineLib.collectOneStack(this.getWorld(), src, extractSide, match.copy());
                    this.buffer.addNewColor(coll, this.colors[this.column] & 0xFF);
                }
                this.stepColumn();
                this.drainBuffer();
                return true;
            }
            case 2: {
                int sm;
                for (sm = 0; sm < 8 && !MachineLib.matchAllCol(map, this.getWorld(), src, extractSide, sm); sm++) {
                }
                if (sm == 8) {
                    return false;
                }
                for (int row = 0; row < 5; row++) {
                    ItemStack match = this.filterSlots[row * 8 + sm];
                    if (com.rptubes.util.Stacks.isEmpty(match)) {
                        continue;
                    }
                    ItemStack coll = MachineLib.collectOneStack(this.getWorld(), src, extractSide, match.copy());
                    this.channelBuffers[sm].addNewColor(coll, this.colors[sm] & 0xFF);
                }
                this.stepColumn();
                this.drainBuffer();
                return true;
            }
            case 3: {
                int sm = MachineLib.matchAnyStack(map, this.getWorld(), src, extractSide);
                if (sm < 0) {
                    return false;
                }
                ItemStack coll = MachineLib.collectOneStack(this.getWorld(), src, extractSide,
                        this.filterSlots[sm].copy());
                this.channelBuffers[sm & 7].addNewColor(coll, this.colors[sm & 7] & 0xFF);
                this.drainBuffer();
                return true;
            }
            case 4: {
                int sm = MachineLib.matchAnyStack(map, this.getWorld(), src, extractSide);
                ItemStack coll;
                if (sm < 0) {
                    coll = MachineLib.collectOneStack(this.getWorld(), src, extractSide, null);
                    if (com.rptubes.util.Stacks.isEmpty(coll)) {
                        return false;
                    }
                    this.buffer.addNewColor(coll, this.defcolor & 0xFF);
                } else {
                    coll = MachineLib.collectOneStack(this.getWorld(), src, extractSide,
                            this.filterSlots[sm].copy());
                    this.channelBuffers[sm & 7].addNewColor(coll, this.colors[sm & 7] & 0xFF);
                }
                this.drainBuffer();
                return true;
            }
            default:
                return false;
        }
    }

    @Override
    protected boolean suckFilter(ItemStack stack) {
        int cm = this.getColumnMatch(stack);
        TubeBuffer buf = this.bufferFor(cm);
        if (buf.plugged) {
            return false;
        }
        if (cm < 0) {
            return this.mode == 4 || cm == -2;
        }
        return true;
    }

    @Override
    public boolean tubeItemEnter(int side, int state, TubeItem ti) {
        ForgeDirection face = this.getFacing();
        // Bounced items return through the output and rejoin their channel buffer
        if (side == face.ordinal() && state == 2) {
            int cm = this.getColumnMatch(ti.item);
            this.bufferFor(cm).addBounce(ti);
            this.setActive(true);
            this.tickCooldown = 5;
            return true;
        }
        if (side == face.getOpposite().ordinal() && state == 1) {
            if (this.powered) {
                return false;
            }
            int cm = this.getColumnMatch(ti.item);
            TubeBuffer buf = this.bufferFor(cm);
            if (!buf.isEmpty()) {
                return false;
            }
            if (cm < 0) {
                if (this.mode == 4) {
                    buf.addNewColor(ti.item, this.defcolor & 0xFF);
                } else if (cm == -2) {
                    buf.addNewColor(ti.item, 0);
                } else {
                    return false;
                }
            } else {
                buf.addNewColor(ti.item, this.colors[cm] & 0xFF);
            }
            this.setActive(true);
            this.tickCooldown = 5;
            this.tryDrainBuffer(buf);
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
            if (this.powered) {
                return false;
            }
            int cm = this.getColumnMatch(ti.item);
            if (!this.bufferFor(cm).isEmpty()) {
                return false;
            }
            if (cm < 0) {
                return this.mode == 4 || cm == -2;
            }
            return true;
        }
        return false;
    }

        public IChatComponent getDisplayName() {
        return new ChatComponentTranslation("block.rptubes.sorting_machine");
    }

        public Container createMenu(InventoryPlayer inv) {
        return new SortingMachineMenu(inv, this);
    }

    @Override
    public int getSizeInventory() {
        return FILTER_SIZE;
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
        return new int[0];
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, int side) {
        return false;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, int side) {
        return false;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        com.rptubes.util.Stacks.saveAllItems(tag, this.filterSlots);
        tag.setByteArray("colors", this.colors.clone());
        tag.setByte("column", this.column);
        tag.setByte("mode", this.mode);
        tag.setByte("drain", this.draining);
        tag.setByte("defc", this.defcolor);
        NBTTagList bufs = new NBTTagList();
        for (int i = 0; i < 8; i++) {
            NBTTagCompound buf = new NBTTagCompound();
            this.channelBuffers[i].writeToNBT(buf);
            bufs.appendTag(buf);
        }
        tag.setTag("buffers", bufs);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.filterSlots = new ItemStack[FILTER_SIZE];
        com.rptubes.util.Stacks.loadAllItems(tag, this.filterSlots);
        byte[] loaded = tag.getByteArray("colors");
        for (int i = 0; i < 8; i++) {
            this.colors[i] = i < loaded.length ? loaded[i] : 0;
        }
        this.column = tag.getByte("column");
        this.mode = tag.getByte("mode");
        this.draining = tag.getByte("drain");
        this.defcolor = tag.getByte("defc");
        NBTTagList bufs = tag.getTagList("buffers", 10);
        for (int i = 0; i < bufs.tagCount() && i < 8; i++) {
            this.channelBuffers[i].readFromNBT(bufs.getCompoundTagAt(i));
        }
        this.filterMap = null;
    }

    public void dropContents() {
        for (int i = 0; i < 8; i++) {
            this.channelBuffers[i].onRemove(this);
        }
        for (ItemStack stack : this.filterSlots) {
            if (!com.rptubes.util.Stacks.isEmpty(stack) && this.getWorld() != null) {
                com.rptubes.util.Stacks.spawnItemStack(this.getWorld(),
                        this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), stack);
            }
        }
    }
}
