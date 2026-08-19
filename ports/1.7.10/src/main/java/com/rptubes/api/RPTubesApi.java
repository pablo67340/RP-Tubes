package com.rptubes.api;

import com.rptubes.tube.ITubeConnectable;
import com.rptubes.tube.TubeItem;
import com.rptubes.tube.TubeLib;
import com.rptubes.util.BlockPos;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Public entry point for mods that want to interact with the RP Tubes network.
 *
 * <p>Stability: everything in {@code com.rptubes.api}, plus
 * {@link com.rptubes.tube.ITubeConnectable} and {@link com.rptubes.tube.TubeItem},
 * is considered public API and will not break within a major version.
 *
 * <p>The two integration levels, simplest first:
 * <ol>
 *   <li><b>Expose a Forge {@code IItemHandler} capability.</b> Tubes connect to and
 *       deliver into any block entity that exposes {@code CapabilityItemHandler.ITEM_HANDLER_CAPABILITY}
 *       on the touching face. Most mods need nothing else.</li>
 *   <li><b>Implement {@link ITubeConnectable} on your TileEntity</b> to become part of
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
    public static boolean isTubeConnectable(World world, BlockPos pos) {
        return world.getTileEntity(pos.getX(), pos.getY(), pos.getZ()) instanceof ITubeConnectable;
    }

    /**
     * True if the tube/machine at {@code pos} would connect toward {@code side}
     * (same check used for tube model connections and routing).
     */
    public static boolean canConnect(World world, BlockPos pos, ForgeDirection side) {
        return TubeLib.canConnect(world, pos, side);
    }

    /** Inject an unpainted item. See {@link #injectItem(World, BlockPos, ForgeDirection, ItemStack, Integer)}. */
    public static boolean injectItem(World world, BlockPos sourcePos, ForgeDirection toward, ItemStack stack) {
        return injectItem(world, sourcePos, toward, stack, null);
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
     * @param paint     optional wool color 0-15 for Filter/Sorting Machine color routing
     * @return true if the whole stack was accepted into the network
     */
    public static boolean injectItem(World world, BlockPos sourcePos, ForgeDirection toward,
                                     ItemStack stack, Integer paint) {
        if (world.isRemote || com.rptubes.util.Stacks.isEmpty(stack)) {
            return false;
        }
        BlockPos tubePos = sourcePos.offset(toward);
        ForgeDirection enterSide = toward.getOpposite();
        TubeItem ti = new TubeItem(enterSide.ordinal(), stack.copy());
        ti.color = paintValue(paint);
        return TubeLib.addToTubeRoute(world, ti, sourcePos, tubePos, enterSide);
    }

    /** Convert a wool color (0-15) to the tube paint value: 0 = unpainted, 1-16 = color + 1. */
    public static byte paintValue(Integer color) {
        return color == null ? 0 : (byte) (color.intValue() + 1);
    }
}
