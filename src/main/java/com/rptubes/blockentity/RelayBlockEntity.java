package com.rptubes.blockentity;

import com.rptubes.menu.RelayMenu;
import com.rptubes.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** Port of TileRelay — Ejector that auto-extracts without a redstone pulse. */
public class RelayBlockEntity extends EjectorBlockEntity {
    private int autoCooldown;

    public RelayBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RELAY.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, RelayBlockEntity be) {
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
    public Component getDisplayName() {
        return Component.translatable("block.rptubes.relay");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new RelayMenu(id, inv, this);
    }
}
