package com.rptubes.api;

import com.rptubes.tube.ITubeConnectable;
import com.rptubes.tube.TubeItem;
import com.rptubes.tube.TubeLib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * Public entry point for mods that want to interact with the RP Tubes network.
 *
 * <p>Stability: everything in {@code com.rptubes.api}, plus
 * {@link com.rptubes.tube.ITubeConnectable} and {@link com.rptubes.tube.TubeItem},
 * is considered public API and will not break within a major version.
 *
 * <p>The two integration levels, simplest first:
 * <ol>
 *   <li><b>Expose a Fabric Transfer API item storage.</b> Tubes connect to and
 *       deliver into any block entity that exposes {@code ItemStorage.SIDED}
 *       on the touching face. Most mods need nothing else.</li>
 *   <li><b>Implement {@link ITubeConnectable} on your BlockEntity</b> to become part of
 *       the network itself: control which sides tubes connect to, add routing weight,
 *       or accept/route traveling {@link TubeItem}s (this is how our own machines work).
 *       Return {@code 0} from {@code getTubeConClass()} to connect to standard tubes.</li>
 * </ol>
 */
public final class RPTubesApi {
    public static final String MOD_ID = "rptubes";

    private RPTubesApi() {
    }

    /** True if the block entity at {@code pos} participates in the tube network. */
    public static boolean isTubeConnectable(Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof ITubeConnectable;
    }

    /**
     * True if the tube/machine at {@code pos} would connect toward {@code side}
     * (same check used for tube model connections and routing).
     */
    public static boolean canConnect(Level level, BlockPos pos, Direction side) {
        return TubeLib.canConnect(level, pos, side);
    }

    /** Inject an unpainted item. See {@link #injectItem(Level, BlockPos, Direction, ItemStack, DyeColor)}. */
    public static boolean injectItem(Level level, BlockPos sourcePos, Direction toward, ItemStack stack) {
        return injectItem(level, sourcePos, toward, stack, null);
    }

    /**
     * Send an item into a tube adjacent to your block, exactly like an Ejector does.
     * The network routes it to the nearest valid destination; if every destination is
     * full or unreachable, injection fails and nothing is consumed.
     *
     * <p>Server side only.
     *
     * @param sourcePos position of <i>your</i> block (the item visually leaves from here)
     * @param toward    side of your block the tube sits on
     * @param stack     the items to send; not mutated — on success a copy travels the
     *                  network, so shrink/clear your own stack when this returns true
     * @param paint     optional paint color for Filter/Sorting Machine color routing
     * @return true if the whole stack was accepted into the network
     */
    public static boolean injectItem(Level level, BlockPos sourcePos, Direction toward,
                                     ItemStack stack, @Nullable DyeColor paint) {
        if (level.isClientSide() || stack.isEmpty()) {
            return false;
        }
        BlockPos tubePos = sourcePos.relative(toward);
        Direction enterSide = toward.getOpposite();
        TubeItem ti = new TubeItem(enterSide.ordinal(), stack.copy());
        ti.color = paintValue(paint);
        return TubeLib.addToTubeRoute(level, ti, sourcePos, tubePos, enterSide);
    }

    /** Convert a dye to the tube paint value: 0 = unpainted, 1-16 = {@code DyeColor.getId() + 1}. */
    public static byte paintValue(@Nullable DyeColor color) {
        return color == null ? 0 : (byte) (color.getId() + 1);
    }
}
