package com.rptubes.tube;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.function.Function;

/**
 * Finds the tube at a position. Usually that is the tile entity itself, but a tube can also live
 * inside another mod's container tile entity (a Forge Multipart block space shared with covers),
 * which registers a resolver here.
 */
public final class TubeAccess {
    private static volatile Function<TileEntity, ITubeConnectable> resolver;

    private TubeAccess() {
    }

    public static void setResolver(Function<TileEntity, ITubeConnectable> resolver) {
        TubeAccess.resolver = resolver;
    }

    public static ITubeConnectable connectable(TileEntity te) {
        if (te instanceof ITubeConnectable) {
            return (ITubeConnectable) te;
        }
        Function<TileEntity, ITubeConnectable> r = resolver;
        return te != null && r != null ? r.apply(te) : null;
    }

    public static ITubeConnectable connectable(IBlockAccess world, BlockPos pos) {
        return connectable(world.getTileEntity(pos));
    }

    public static ITubeFlow flow(IBlockAccess world, BlockPos pos) {
        ITubeConnectable tube = connectable(world, pos);
        return tube instanceof ITubeFlow ? (ITubeFlow) tube : null;
    }
}
