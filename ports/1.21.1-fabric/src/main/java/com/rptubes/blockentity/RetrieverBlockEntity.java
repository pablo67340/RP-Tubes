package com.rptubes.blockentity;

import com.rptubes.menu.RetrieverMenu;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.tube.ITubeConnectable;
import com.rptubes.tube.ITubeFlow;
import com.rptubes.tube.MachineLib;
import com.rptubes.tube.TubeItem;
import com.rptubes.tube.TubeLib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Port of TileRetriever — BFS through tubes for matching items, no blutricity required.
 */
public class RetrieverBlockEntity extends FilterBlockEntity {
    /** OG mode: 0 = sequential (one filter slot at a time), 1 = any match. */
    public byte mode;
    /** Currently selected filter slot in sequential mode (OG select). */
    public byte select;

    public RetrieverBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RETRIEVER, pos, state);
    }

    private MachineLib.FilterMap map() {
        ItemStack[] arr = new ItemStack[9];
        for (int i = 0; i < 9; i++) {
            arr[i] = this.getItem(i);
        }
        return MachineLib.makeFilterMap(arr);
    }

    private boolean gridEmpty() {
        for (int i = 0; i < 9; i++) {
            if (!this.getItem(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /** OG stepSelect — advance to the next non-empty filter slot. */
    private void stepSelect() {
        for (int i = 0; i < 9; ++i) {
            this.select = (byte) (this.select + 1);
            if (this.select > 8) {
                this.select = 0;
            }
            if (!this.getItem(this.select).isEmpty()) {
                this.setChanged();
                return;
            }
        }
        this.select = 0;
        this.setChanged();
    }

    @Override
    protected boolean handleExtract(BlockPos src, Direction extractSide) {
        BlockEntity backBe = this.level.getBlockEntity(src);
        if (!(backBe instanceof ITubeConnectable tube) || !tube.canRouteItems()) {
            return super.handleExtract(src, extractSide);
        }

        boolean sequential = this.mode == 0 && !this.gridEmpty();
        if (sequential && this.getItem(this.select).isEmpty()) {
            this.stepSelect();
        }
        MachineLib.FilterMap filterMap;
        if (sequential) {
            // OG setSubFilt: only the selected slot participates in matching
            ItemStack[] arr = new ItemStack[9];
            for (int i = 0; i < 9; i++) {
                arr[i] = i == this.select ? this.getItem(i) : ItemStack.EMPTY;
            }
            filterMap = MachineLib.makeFilterMap(arr);
        } else {
            filterMap = this.map();
        }
        TubeLib.InventorySource found = TubeLib.findInventorySource(
                this.level, this.worldPosition, this.getFacing().getOpposite(), filterMap);
        if (found == null) {
            return false;
        }

        ItemStack match = filterMap.size() == 0 ? ItemStack.EMPTY : this.getItem(found.filterSlot()).copy();
        ItemStack extracted = MachineLib.collectOneStack(this.level, found.invPos(), found.invSide(), match);
        if (extracted.isEmpty()) {
            return false;
        }

        BlockPos tubePos = found.invPos().relative(found.invSide());
        BlockEntity tubeBe = this.level.getBlockEntity(tubePos);
        if (!(tubeBe instanceof ITubeFlow flow)) {
            this.addToBuffer(extracted);
            this.drainBuffer();
            if (sequential) {
                this.stepSelect();
            }
            return true;
        }

        TubeItem ti = new TubeItem(found.invSide().ordinal(), extracted);
        ti.mode = 3;
        flow.addTubeItem(ti);
        if (sequential) {
            this.stepSelect();
        }
        return true;
    }

    @Override
    public boolean tubeItemEnter(int side, int state, TubeItem ti) {
        Direction back = this.getFacing().getOpposite();
        if (side == back.ordinal() && state == 3) {
            if (!this.buffer.isEmpty()) {
                return false;
            }
            MachineLib.FilterMap filterMap = this.map();
            if (filterMap.size() > 0 && !filterMap.containsKey(ti.item)) {
                return false;
            }
            this.addToBuffer(ti.item);
            this.setActive(true);
            this.tickCooldown = 5;
            this.drainBuffer();
            return true;
        }
        return super.tubeItemEnter(side, state, ti);
    }

    @Override
    public boolean tubeItemCanEnter(int side, int state, TubeItem ti) {
        Direction back = this.getFacing().getOpposite();
        if (side == back.ordinal() && state == 3) {
            if (!this.buffer.isEmpty()) {
                return false;
            }
            MachineLib.FilterMap filterMap = this.map();
            return filterMap.size() == 0 || filterMap.containsKey(ti.item);
        }
        return super.tubeItemCanEnter(side, state, ti);
    }

    @Override
    protected void saveAdditional(net.minecraft.nbt.CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putByte("mode", this.mode);
        tag.putByte("sel", this.select);
    }

    @Override
    protected void loadAdditional(net.minecraft.nbt.CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.mode = tag.getByte("mode");
        this.select = tag.getByte("sel");
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.rptubes.retriever");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new RetrieverMenu(id, inv, this);
    }
}
