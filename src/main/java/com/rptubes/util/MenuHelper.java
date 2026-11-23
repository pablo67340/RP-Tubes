//? if fabric {
/*package com.rptubes.util;

//? if <1.20.6 {
/^import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
^///?} else if <26.1.2 {
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
//?} else {
/^import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.minecraft.core.BlockPos;
^///?}
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
        //? if <1.20.6 {
        /^player.openMenu(new ExtendedScreenHandlerFactory() {
            @Override
            public void writeScreenOpeningData(ServerPlayer p, FriendlyByteBuf buf) {
                buf.writeBlockPos(pos);
        ^///?} else if <26.1.2 {
        player.openMenu(new ExtendedScreenHandlerFactory<BlockPos>() {
            @Override
            public BlockPos getScreenOpeningData(ServerPlayer p) {
                return pos;
        //?} else {
        /^player.openMenu(new ExtendedMenuProvider<BlockPos>() {
            @Override
            public BlockPos getScreenOpeningData(ServerPlayer p) {
                return pos;
        ^///?}
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
*///?}
