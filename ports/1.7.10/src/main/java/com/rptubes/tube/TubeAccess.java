package com.rptubes.tube;

import com.rptubes.util.BlockPos;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

/**
 * Finds the tube at a position. Usually that is the tile entity itself, but a tube can also live
 * inside another mod's container tile entity (a Forge Multipart block space shared with covers),
 * which registers a resolver here.
 */
public final class TubeAccess {
    public interface Resolver {
        ITubeConnectable find(TileEntity te);
    }

    private static volatile Resolver resolver;

    private TubeAccess() {
    }

    public static void setResolver(Resolver resolver) {
        TubeAccess.resolver = resolver;
    }

    public static ITubeConnectable connectable(TileEntity te) {
        if (te instanceof ITubeConnectable) {
            return (ITubeConnectable) te;
        }
        Resolver r = resolver;
        return te != null && r != null ? r.find(te) : null;
    }

    public static ITubeConnectable connectable(IBlockAccess world, int x, int y, int z) {
        return connectable(world.getTileEntity(x, y, z));
    }

    public static ITubeConnectable connectable(IBlockAccess world, BlockPos pos) {
        return connectable(world.getTileEntity(pos.getX(), pos.getY(), pos.getZ()));
    }

    public static ITubeFlow flow(IBlockAccess world, BlockPos pos) {
        ITubeConnectable tube = connectable(world, pos);
        return tube instanceof ITubeFlow ? (ITubeFlow) tube : null;
    }
}
