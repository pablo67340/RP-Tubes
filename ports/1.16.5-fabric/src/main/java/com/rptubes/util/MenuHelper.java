package com.rptubes.util;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public final class MenuHelper {
    private MenuHelper() {
    }

    public static void open(ServerPlayer player, MenuProvider provider, BlockPos pos) {
        player.openMenu(new ExtendedScreenHandlerFactory() {
            @Override
            public void writeScreenOpeningData(ServerPlayer p, FriendlyByteBuf buf) {
                buf.writeBlockPos(pos);
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
