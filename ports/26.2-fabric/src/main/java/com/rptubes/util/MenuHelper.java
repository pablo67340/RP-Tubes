package com.rptubes.util;

import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public final class MenuHelper {
    private MenuHelper() {
    }

    public static void open(ServerPlayer player, MenuProvider provider, BlockPos pos) {
        player.openMenu(new ExtendedMenuProvider<BlockPos>() {
            @Override
            public BlockPos getScreenOpeningData(ServerPlayer p) {
                return pos;
            }

            @Override
            public Component getDisplayName() {
                return provider.getDisplayName();
            }

            @Override
            public AbstractContainerMenu createMenu(int syncId, Inventory inventory, Player p) {
                return provider.createMenu(syncId, inventory, p);
            }
        });
    }
}
