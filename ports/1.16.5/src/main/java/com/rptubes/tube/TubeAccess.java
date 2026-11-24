package com.rptubes.tube;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import java.util.function.Function;

/**
 * Finds the tube at a position. Usually that is the tile entity itself, but a tube can also live
 * inside another mod's container tile entity (a CB Multipart block space shared with covers),
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

    public static ITubeConnectable connectable(IBlockReader level, BlockPos pos) {
        return connectable(level.getBlockEntity(pos));
    }

    public static ITubeFlow flow(IBlockReader level, BlockPos pos) {
        ITubeConnectable tube = connectable(level, pos);
        return tube instanceof ITubeFlow ? (ITubeFlow) tube : null;
    }
}
