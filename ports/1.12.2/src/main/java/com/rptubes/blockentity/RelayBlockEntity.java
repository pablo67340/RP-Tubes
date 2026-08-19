package com.rptubes.blockentity;

import com.rptubes.menu.RelayMenu;
import com.rptubes.registry.ModBlockEntities;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;
import net.minecraft.block.state.IBlockState;

/** Port of TileRelay — Ejector that auto-extracts without a redstone pulse. */
public class RelayBlockEntity extends EjectorBlockEntity {
    private int autoCooldown;

    public RelayBlockEntity() {
        super();
    }

    public static void serverTick(World world, BlockPos pos, IBlockState state, RelayBlockEntity be) {
        if (be.tickCooldown > 0) {
            --be.tickCooldown;
        }
        if (be.autoCooldown > 0) {
            --be.autoCooldown;
        }
        if (!be.buffer.isEmpty()) {
            be.drainBuffer();
            be.tickCooldown = be.buffer.isEmpty() ? 5 : 10;
            return;
        }
        if (be.active && be.tickCooldown <= 0) {
            be.setActive(false);
        }
        if (be.autoCooldown <= 0 && !be.active) {
            be.autoCooldown = 10;
            if (be.handleExtract()) {
                be.setActive(true);
                be.tickCooldown = 5;
            }
        }
    }

    @Override
    public void onNeighborChange() {
        // Relay ignores redstone; continuous extract only.
    }

    @Override
    protected boolean handleExtract() {
        // Unlike the Ejector, the Relay never pulls from the inventory behind it -
        // auto-extracting every 10 ticks would silently drain any adjacent chest.
        return this.extractInternal();
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentTranslation("block.rptubes.relay");
    }

        public Container createMenu(InventoryPlayer inv) {
        return new RelayMenu(inv, this);
    }

    @Override
    public void update() {
        if (this.world == null || this.world.isRemote) {
            return;
        }
        serverTick(this.world, this.pos, this.world.getBlockState(this.pos), this);
    }
}
