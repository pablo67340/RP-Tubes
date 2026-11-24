package com.rptubes.tube;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.Function;

/**
 * Finds the tube at a position. Usually that is the block entity itself, but a tube can also live
 * inside another mod's container block entity (a CB Multipart block space shared with covers),
 * which registers a resolver here.
 */
public final class TubeAccess {
    private static volatile Function<BlockEntity, ITubeConnectable> resolver;

    private TubeAccess() {
    }

    public static void setResolver(Function<BlockEntity, ITubeConnectable> resolver) {
        TubeAccess.resolver = resolver;
    }

    public static ITubeConnectable connectable(BlockEntity be) {
        if (be instanceof ITubeConnectable tube) {
            return tube;
        }
        Function<BlockEntity, ITubeConnectable> r = resolver;
        return be != null && r != null ? r.apply(be) : null;
    }

    public static ITubeConnectable connectable(BlockGetter level, BlockPos pos) {
        return connectable(level.getBlockEntity(pos));
    }

    public static ITubeFlow flow(BlockGetter level, BlockPos pos) {
        return connectable(level, pos) instanceof ITubeFlow flow ? flow : null;
    }
}
