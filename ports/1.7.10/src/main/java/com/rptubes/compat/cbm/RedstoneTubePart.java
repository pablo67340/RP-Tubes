package com.rptubes.compat.cbm;

import codechicken.multipart.IRedstonePart;
import com.rptubes.block.RedstoneTubeBlock;
import com.rptubes.blockentity.RedstoneTubeBlockEntity;

/** Redstone tube part: conducts redstone along tubes exactly like the block, through the shared power logic. */
public class RedstoneTubePart extends TubePart implements IRedstonePart {
    private int glow;

    public RedstoneTubePart(TubeParts.Kind kind) {
        super(kind);
    }

    @Override
    protected void refresh() {
        super.refresh();
        if (this.world().isRemote) {
            return;
        }
        RedstoneTubeBlockEntity tube = (RedstoneTubeBlockEntity) this.tube();
        int target = RedstoneTubeBlock.calculateTargetPower(this.world(), this.x(), this.y(), this.z());
        if (tube.getPower() == target) {
            return;
        }
        tube.setPower(target);
        int glow = target == 0 ? 0 : Math.min(3, (target + 4) / 5);
        if (glow != this.glow) {
            this.glow = glow;
            this.sendState();
        }
        this.tile().notifyPartChange(this);
    }

    @Override
    public void onRemoved() {
        super.onRemoved();
        if (!this.world().isRemote) {
            this.world().notifyBlocksOfNeighborChange(this.x(), this.y(), this.z(), this.world().getBlock(this.x(), this.y(), this.z()));
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
}
