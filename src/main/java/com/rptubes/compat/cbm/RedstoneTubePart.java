//? if cbm {
package com.rptubes.compat.cbm;

import codechicken.multipart.api.part.redstone.RedstonePart;
import com.rptubes.block.RedstoneTubeBlock;
import com.rptubes.blockentity.RedstoneTubeBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Redstone tube part: conducts redstone along tubes exactly like the block, through the shared power logic. */
public class RedstoneTubePart extends TubePart implements RedstonePart {
    private int glow;

    public RedstoneTubePart(TubeParts.Kind kind) {
        super(kind);
    }

    @Override
    protected void refresh() {
        super.refresh();
        if (this.level().isClientSide()) {
            return;
        }
        RedstoneTubeBlockEntity tube = (RedstoneTubeBlockEntity) this.tube();
        int target = RedstoneTubeBlock.calculateTargetPower(this.level(), this.pos());
        if (tube.getPower() == target) {
            return;
        }
        tube.setPower(target);
        int glow = target == 0 ? 0 : Math.min(3, (target + 4) / 5);
        if (glow != this.glow) {
            this.glow = glow;
            this.sendState();
        }
        this.level().updateNeighborsAt(this.pos(), this.tile().getBlockState().getBlock());
    }

    @Override
    public void onRemoved() {
        super.onRemoved();
        if (!this.level().isClientSide()) {
            this.level().updateNeighborsAt(this.pos(), this.tile().getBlockState().getBlock());
        }
    }

    @Override
    protected int glow() {
        return this.glow;
    }

    @Override
    protected void setGlow(int glow) {
        this.glow = Math.max(0, Math.min(3, glow));
    }

    @Override
    public int strongPowerLevel(int side) {
        return 0;
    }

    @Override
    public int weakPowerLevel(int side) {
        if (!RedstoneTubeBlock.isSignalling() || this.tubeOrNull() == null) {
            return 0;
        }
        return ((RedstoneTubeBlockEntity) this.tube()).getPower();
    }

    @Override
    public boolean canConnectRedstone(int side) {
        return true;
    }

    @Override
    public BlockState getCurrentState() {
        return super.getCurrentState().setValue(RedstoneTubeBlock.POWER, this.glow);
    }
}
//?}
