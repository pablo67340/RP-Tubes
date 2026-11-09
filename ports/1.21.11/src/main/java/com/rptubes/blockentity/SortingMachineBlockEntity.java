package com.rptubes.blockentity;

import com.rptubes.menu.SortingMachineMenu;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.tube.MachineLib;
import com.rptubes.tube.TubeBuffer;
import com.rptubes.tube.TubeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

/**
 * Port of TileSorter — 8×5 filter columns, per-column paint colors, 5 modes,
 * 8 channel buffers drained round-robin. No blutricity in this port.
 *
 * Modes (OG): 0 = anystack sequential, 1 = allstacks sequential,
 * 2 = random allstacks, 3 = anystack any column, 4 = any item (default color).
 */
public class SortingMachineBlockEntity extends TransposerBlockEntity implements WorldlyContainer, MenuProvider {
    public static final int FILTER_SIZE = 40;
    private NonNullList<ItemStack> filterSlots = NonNullList.withSize(FILTER_SIZE, ItemStack.EMPTY);
    public final byte[] colors = new byte[8];
    public byte mode;
    public byte defcolor;
    public byte column;
    private byte draining = -1;
    private final TubeBuffer[] channelBuffers = new TubeBuffer[8];
    private MachineLib.FilterMap filterMap;
    private final MachineEnergy energy = new MachineEnergy();

    public SortingMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SORTING_MACHINE.get(), pos, state);
        for (int i = 0; i < 8; i++) {
            this.channelBuffers[i] = new TubeBuffer();
        }
    }

    private void regenFilterMap() {
        this.filterMap = MachineLib.makeFilterMap(this.filterSlots.toArray(ItemStack[]::new));
    }

    @Override
    protected boolean payForExtract(boolean mustHaveEnergy) {
        return this.energy.pay(mustHaveEnergy);
    }

    @Override
    protected void tryEnergyAutoExtract() {
        if (!this.energy.canAutoExtract()) {
            return;
        }
        if (!this.energy.pay(true)) {
            return;
        }
        this.setActive(true);
        this.startExtract();
        this.tickCooldown = 10;
        this.setChanged();
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
            if (!MachineLib.handleItem(this.level, ti, this.worldPosition, this.getFacing())) {
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
                if (!this.filterSlots.get(row * 8 + this.column).isEmpty()) {
                    return;
                }
            }
        }
        this.column = 0;
    }

    private void checkColumn() {
        for (int row = 0; row < 5; row++) {
            if (!this.filterSlots.get(row * 8 + this.column).isEmpty()) {
                return;
            }
        }
        this.stepColumn();
        this.setChanged();
    }

    /** OG handleExtract — the five sorting modes. */
    @Override
    protected boolean handleExtract(BlockPos src, Direction extractSide) {
        MachineLib.FilterMap map = this.map();
        if (map.size() == 0) {
            ItemStack ist = MachineLib.collectOneStack(this.level, src, extractSide, ItemStack.EMPTY);
            if (ist.isEmpty()) {
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
                int sm = MachineLib.matchAnyStackCol(map, this.level, src, extractSide, this.column);
                if (sm < 0) {
                    return false;
                }
                ItemStack coll = MachineLib.collectOneStack(this.level, src, extractSide,
                        this.filterSlots.get(sm).copy());
                this.buffer.addNewColor(coll, this.colors[sm & 7] & 0xFF);
                this.stepColumn();
                this.drainBuffer();
                return true;
            }
            case 1: {
                this.checkColumn();
                if (!MachineLib.matchAllCol(map, this.level, src, extractSide, this.column)) {
                    return false;
                }
                for (int row = 0; row < 5; row++) {
                    ItemStack match = this.filterSlots.get(row * 8 + this.column);
                    if (match.isEmpty()) {
                        continue;
                    }
                    ItemStack coll = MachineLib.collectOneStack(this.level, src, extractSide, match.copy());
                    this.buffer.addNewColor(coll, this.colors[this.column] & 0xFF);
                }
                this.stepColumn();
                this.drainBuffer();
                return true;
            }
            case 2: {
                int sm;
                for (sm = 0; sm < 8 && !MachineLib.matchAllCol(map, this.level, src, extractSide, sm); sm++) {
                }
                if (sm == 8) {
                    return false;
                }
                for (int row = 0; row < 5; row++) {
                    ItemStack match = this.filterSlots.get(row * 8 + sm);
                    if (match.isEmpty()) {
                        continue;
                    }
                    ItemStack coll = MachineLib.collectOneStack(this.level, src, extractSide, match.copy());
                    this.channelBuffers[sm].addNewColor(coll, this.colors[sm] & 0xFF);
                }
                this.stepColumn();
                this.drainBuffer();
                return true;
            }
            case 3: {
                int sm = MachineLib.matchAnyStack(map, this.level, src, extractSide);
                if (sm < 0) {
                    return false;
                }
                ItemStack coll = MachineLib.collectOneStack(this.level, src, extractSide,
                        this.filterSlots.get(sm).copy());
                this.channelBuffers[sm & 7].addNewColor(coll, this.colors[sm & 7] & 0xFF);
                this.drainBuffer();
                return true;
            }
            case 4: {
                int sm = MachineLib.matchAnyStack(map, this.level, src, extractSide);
                ItemStack coll;
                if (sm < 0) {
                    coll = MachineLib.collectOneStack(this.level, src, extractSide, ItemStack.EMPTY);
                    if (coll.isEmpty()) {
                        return false;
                    }
                    this.buffer.addNewColor(coll, this.defcolor & 0xFF);
                } else {
                    coll = MachineLib.collectOneStack(this.level, src, extractSide,
                            this.filterSlots.get(sm).copy());
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
        Direction face = this.getFacing();
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
        Direction face = this.getFacing();
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

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.rptubes.sorting_machine");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new SortingMachineMenu(id, inv, this);
    }

    @Override
    public int getContainerSize() {
        return FILTER_SIZE;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.filterSlots) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.filterSlots.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = ContainerHelper.removeItem(this.filterSlots, slot, amount);
        if (!result.isEmpty()) {
            this.filterMap = null;
            this.setChanged();
        }
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        this.filterMap = null;
        return ContainerHelper.takeItem(this.filterSlots, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.filterSlots.set(slot, stack);
        this.filterMap = null;
        this.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return this.level != null
                && this.level.getBlockEntity(this.worldPosition) == this
                && player.distanceToSqr(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5,
                this.worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void clearContent() {
        this.filterSlots.clear();
        this.filterMap = null;
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[0];
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, Direction direction) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return false;
    }

    @Override
    protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.filterSlots);
        int[] colorsOut = new int[this.colors.length];
        for (int i = 0; i < this.colors.length; i++) {
            colorsOut[i] = this.colors[i];
        }
        tag.putIntArray("colors", colorsOut);
        tag.putByte("column", this.column);
        tag.putByte("mode", this.mode);
        tag.putByte("drain", this.draining);
        tag.putByte("defc", this.defcolor);
        net.minecraft.core.HolderLookup.Provider registries = this.level != null ? this.level.registryAccess() : null;
        net.minecraft.world.level.storage.ValueOutput.TypedOutputList<CompoundTag> bufs = tag.list("buffers", CompoundTag.CODEC);
        for (int i = 0; i < 8; i++) {
            CompoundTag buf = new CompoundTag();
            this.channelBuffers[i].writeToNBT(buf, registries);
            bufs.add(buf);
        }
        this.energy.write(tag);
    }

    @Override
    protected void loadAdditional(net.minecraft.world.level.storage.ValueInput tag) {
        super.loadAdditional(tag);
        this.filterSlots = NonNullList.withSize(FILTER_SIZE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.filterSlots);
        int[] loaded = tag.getIntArray("colors").orElse(new int[0]);
        for (int i = 0; i < 8; i++) {
            this.colors[i] = i < loaded.length ? (byte) loaded[i] : 0;
        }
        this.column = tag.getByteOr("column", (byte) 0);
        this.mode = tag.getByteOr("mode", (byte) 0);
        this.draining = tag.getByteOr("drain", (byte) 0);
        this.defcolor = tag.getByteOr("defc", (byte) 0);
        int i = 0;
        for (CompoundTag buf : tag.listOrEmpty("buffers", CompoundTag.CODEC)) {
            if (i >= 8) {
                break;
            }
            this.channelBuffers[i++].readFromNBT(buf, tag.lookup());
        }
        this.filterMap = null;
        this.energy.read(tag);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (!this.remove && this.energy.isEnergyCap(cap)) {
            return this.energy.cast(cap, side);
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.energy.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        this.energy.revive();
    }

    @Override
    public void setRemoved() {
        for (int i = 0; i < 8; i++) {
            this.channelBuffers[i].onRemove(this);
        }
        // Filter slot drops handled by the default preRemoveSideEffects (Container)
        super.setRemoved();
    }
}
