package com.rptubes.blockentity;

import com.rptubes.menu.RetrieverMenu;
import com.rptubes.registry.ModBlockEntities;
import com.rptubes.tube.ITubeConnectable;
import com.rptubes.tube.ITubeFlow;
import com.rptubes.tube.MachineLib;
import com.rptubes.tube.TubeItem;
import com.rptubes.tube.TubeLib;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.state.IBlockState;

/**
 * Port of TileRetriever — BFS through tubes for matching items, no blutricity required.
 */
public class RetrieverBlockEntity extends FilterBlockEntity {
    /** OG mode: 0 = sequential (one filter slot at a time), 1 = any match. */
    public byte mode;
    /** Currently selected filter slot in sequential mode (OG select). */
    public byte select;

    public RetrieverBlockEntity() {
        super();
    }

    private MachineLib.FilterMap map() {
        ItemStack[] arr = new ItemStack[9];
        for (int i = 0; i < 9; i++) {
            arr[i] = this.getStackInSlot(i);
        }
        return MachineLib.makeFilterMap(arr);
    }

    private boolean gridEmpty() {
        for (int i = 0; i < 9; i++) {
            if (!this.getStackInSlot(i).isEmpty()) {
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
            if (!this.getStackInSlot(this.select).isEmpty()) {
                this.markDirty();
                return;
            }
        }
        this.select = 0;
        this.markDirty();
    }

    @Override
    protected boolean handleExtract(BlockPos src, EnumFacing extractSide) {
        TileEntity backBe = this.world.getTileEntity(src);
        if (!(backBe instanceof ITubeConnectable) || !((ITubeConnectable) backBe).canRouteItems()) {
            return super.handleExtract(src, extractSide);
        }

        boolean sequential = this.mode == 0 && !this.gridEmpty();
        if (sequential && this.getStackInSlot(this.select).isEmpty()) {
            this.stepSelect();
        }
        MachineLib.FilterMap filterMap;
        if (sequential) {
            // OG setSubFilt: only the selected slot participates in matching
            ItemStack[] arr = new ItemStack[9];
            for (int i = 0; i < 9; i++) {
                arr[i] = i == this.select ? this.getStackInSlot(i) : ItemStack.EMPTY;
            }
            filterMap = MachineLib.makeFilterMap(arr);
        } else {
            filterMap = this.map();
        }
        TubeLib.InventorySource found = TubeLib.findInventorySource(
                this.world, this.pos, this.getFacing().getOpposite(), filterMap);
        if (found == null) {
            return false;
        }

        ItemStack match = filterMap.size() == 0 ? ItemStack.EMPTY : this.getStackInSlot(found.filterSlot()).copy();
        ItemStack extracted = MachineLib.collectOneStack(this.world, found.invPos(), found.invSide(), match);
        if (extracted.isEmpty()) {
            return false;
        }

        BlockPos tubePos = found.invPos().offset(found.invSide());
        TileEntity tubeBe = this.world.getTileEntity(tubePos);
        if (!(tubeBe instanceof ITubeFlow)) {
            this.addToBuffer(extracted);
            this.drainBuffer();
            if (sequential) {
                this.stepSelect();
            }
            return true;
        }

        ITubeFlow flow = (ITubeFlow) tubeBe;
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
        EnumFacing back = this.getFacing().getOpposite();
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
        EnumFacing back = this.getFacing().getOpposite();
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
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setByte("mode", this.mode);
        tag.setByte("sel", this.select);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.mode = tag.getByte("mode");
        this.select = tag.getByte("sel");
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentTranslation("block.rptubes.retriever");
    }

        public Container createMenu(InventoryPlayer inv) {
        return new RetrieverMenu(inv, this);
    }
}
