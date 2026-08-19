package com.rptubes.tube;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Port of eloraam.core.TubeLib — connection checks + closest-destination routing.
 */
public final class TubeLib {
    private static final Set<Long> COMPAT = new HashSet<>();

    static {
        addCompatibleMapping(0, 17);
        addCompatibleMapping(17, 18);
        for (int i = 0; i < 16; ++i) {
            addCompatibleMapping(0, 1 + i);
            addCompatibleMapping(17, 1 + i);
            addCompatibleMapping(17, 19 + i);
            addCompatibleMapping(18, 19 + i);
        }
    }

    private TubeLib() {
    }

    public static void addCompatibleMapping(int a, int b) {
        COMPAT.add(pack(a, b));
        COMPAT.add(pack(b, a));
    }

    public static boolean isCompatible(int a, int b) {
        return a == b || COMPAT.contains(pack(a, b));
    }

    private static long pack(int a, int b) {
        return (((long) a) << 32) | (b & 0xFFFFFFFFL);
    }

    public static boolean canConnect(World world, BlockPos pos, EnumFacing dir) {
        BlockPos neighbor = pos.offset(dir);
        TileEntity self = world.getTileEntity(pos);
        int selfClass = 0;
        if (self instanceof ITubeConnectable) {
            ITubeConnectable tube = (ITubeConnectable) self;
            if ((tube.getTubeConnectableSides() & (1 << dir.ordinal())) == 0) {
                return false;
            }
            selfClass = tube.getTubeConClass();
        }

        TileEntity other = world.getTileEntity(neighbor);
        EnumFacing opposite = dir.getOpposite();

        if (other instanceof ITubeConnectable) {
            ITubeConnectable tube = (ITubeConnectable) other;
            if (!isCompatible(selfClass, tube.getTubeConClass())) {
                return false;
            }
            return (tube.getTubeConnectableSides() & (1 << opposite.ordinal())) != 0;
        }

        return isCompatible(selfClass, 0) && hasItemHandler(world, neighbor, opposite);
    }

    public static boolean hasItemHandler(World world, BlockPos pos, EnumFacing side) {
        TileEntity be = world.getTileEntity(pos);
        if (be == null) {
            return false;
        }
        net.minecraftforge.items.IItemHandler handler = be.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
        return handler != null && handler.getSlots() > 0;
    }

    public static int getConnections(World world, BlockPos pos) {
        TileEntity be = world.getTileEntity(pos);
        if (!(be instanceof ITubeConnectable)) {
            return 0;
        }
        ITubeConnectable tube = (ITubeConnectable) be;
        int mask = 0;
        int sides = tube.getTubeConnectableSides();
        for (EnumFacing dir : EnumFacing.values()) {
            int bit = 1 << dir.ordinal();
            if ((sides & bit) != 0 && canConnect(world, pos, dir)) {
                mask |= bit;
            }
        }
        return mask;
    }

    /**
     * Find the best exit side from {@code pos} toward a valid inventory / sink.
     * @return EnumFacing ordinal, or -1 if none.
     */
    public static int findRoute(World world, BlockPos pos, TubeItem item, int sideMask, int state, int startDir) {
        PriorityQueue<Node> queue = new PriorityQueue<>();
        Set<BlockPos> visited = new HashSet<>();
        visited.add(pos);

        for (EnumFacing dir : EnumFacing.values()) {
            int bit = 1 << dir.ordinal();
            if ((sideMask & bit) == 0) {
                continue;
            }
            BlockPos next = pos.offset(dir);
            int weight = dir.ordinal() == startDir ? 0 : 1;
            queue.add(new Node(next, dir.ordinal(), dir.getOpposite().ordinal(), weight));
        }

        while (!queue.isEmpty()) {
            Node node = queue.poll();
            if (visited.contains(node.pos)) {
                continue;
            }
            visited.add(node.pos);

            if (state != 3 && MachineLib.canInsert(world, node.pos, EnumFacing.byIndex(node.enterSide), item.item)) {
                return node.startSide;
            }

            TileEntity be = world.getTileEntity(node.pos);
            if (!(be instanceof ITubeConnectable)) {
                continue;
            }
            ITubeConnectable tube = (ITubeConnectable) be;

            if (tube.tubeItemCanEnter(node.enterSide, state, item)) {
                return node.startSide;
            }
            if (!tube.tubeItemCanEnter(node.enterSide, 0, item) || !tube.canRouteItems()) {
                continue;
            }

            int cons = getConnections(world, node.pos) & ~(1 << node.enterSide);
            for (EnumFacing dir : EnumFacing.values()) {
                int bit = 1 << dir.ordinal();
                if ((cons & bit) == 0) {
                    continue;
                }
                BlockPos next = node.pos.offset(dir);
                if (visited.contains(next)) {
                    continue;
                }
                int w = node.weight + 1 + tube.tubeWeight(dir.ordinal(), state);
                queue.add(new Node(next, node.startSide, dir.getOpposite().ordinal(), w));
            }
        }
        return -1;
    }

    public static boolean addToTubeRoute(World world, ItemStack stack, BlockPos src, BlockPos tubePos, EnumFacing enterSide) {
        return addToTubeRoute(world, new TubeItem(enterSide.ordinal(), stack), src, tubePos, enterSide);
    }

    public static boolean addToTubeRoute(World world, TubeItem ti, BlockPos src, BlockPos tubePos, EnumFacing enterSide) {
        TileEntity be = world.getTileEntity(tubePos);
        if (!(be instanceof ITubeConnectable)) {
            return false;
        }
        ITubeConnectable tube = (ITubeConnectable) be;
        ti.mode = 1;
        int side = findRoute(world, src, ti, 1 << enterSide.getOpposite().ordinal(), 1, 0);
        if (side < 0) {
            return false;
        }
        return tube.tubeItemEnter(enterSide.ordinal(), 0, ti);
    }

    /**
     * BFS through routing tubes for an inventory containing items matching {@code map}.
     * {@code startDir} is the first direction to leave {@code start} (usually facing opposite).
     */
    public static InventorySource findInventorySource(World world, BlockPos start, EnumFacing startDir,
                                                     MachineLib.FilterMap map) {
        PriorityQueue<SrcNode> queue = new PriorityQueue<>();
        Set<BlockPos> visited = new HashSet<>();
        visited.add(start);
        queue.add(new SrcNode(start.offset(startDir), startDir.getOpposite(), 0));

        while (!queue.isEmpty()) {
            SrcNode node = queue.poll();
            if (!visited.add(node.pos)) {
                continue;
            }

            TileEntity be = world.getTileEntity(node.pos);
            boolean routing = be instanceof ITubeConnectable && ((ITubeConnectable) be).canRouteItems();
            IItemHandler handler = MachineLib.getHandler(world, node.pos, node.enterSide);

            if (handler != null && !routing) {
                if (map == null || map.size() == 0) {
                    for (int i = 0; i < handler.getSlots(); i++) {
                        if (!handler.getStackInSlot(i).isEmpty()) {
                            return new InventorySource(node.pos, node.enterSide, 0);
                        }
                    }
                } else {
                    int slot = MachineLib.matchAnyStack(map, world, node.pos, node.enterSide);
                    if (slot >= 0) {
                        return new InventorySource(node.pos, node.enterSide, slot);
                    }
                }
            }

            if (!routing) {
                continue;
            }

            int cons = getConnections(world, node.pos) & ~(1 << node.enterSide.ordinal());
            for (EnumFacing dir : EnumFacing.values()) {
                int bit = 1 << dir.ordinal();
                if ((cons & bit) == 0) {
                    continue;
                }
                BlockPos next = node.pos.offset(dir);
                if (visited.contains(next)) {
                    continue;
                }
                queue.add(new SrcNode(next, dir.getOpposite(), node.weight + 1));
            }
        }
        return null;
    }

    public static final class InventorySource {
        private final BlockPos invPos;
        private final EnumFacing invSide;
        private final int filterSlot;

        public InventorySource(BlockPos invPos, EnumFacing invSide, int filterSlot) {
            this.invPos = invPos;
            this.invSide = invSide;
            this.filterSlot = filterSlot;
        }

        public BlockPos invPos() {
            return this.invPos;
        }

        public EnumFacing invSide() {
            return this.invSide;
        }

        public int filterSlot() {
            return this.filterSlot;
        }
    }

    private static final class SrcNode implements Comparable<SrcNode> {
        final BlockPos pos;
        final EnumFacing enterSide;
        final int weight;

        SrcNode(BlockPos pos, EnumFacing enterSide, int weight) {
            this.pos = pos;
            this.enterSide = enterSide;
            this.weight = weight;
        }

        @Override
        public int compareTo(SrcNode o) {
            return Integer.compare(this.weight, o.weight);
        }
    }

    private static final class Node implements Comparable<Node> {
        final BlockPos pos;
        final int startSide;
        final int enterSide;
        final int weight;

        Node(BlockPos pos, int startSide, int enterSide, int weight) {
            this.pos = pos;
            this.startSide = startSide;
            this.enterSide = enterSide;
            this.weight = weight;
        }

        @Override
        public int compareTo(Node o) {
            return Integer.compare(this.weight, o.weight);
        }
    }
}
