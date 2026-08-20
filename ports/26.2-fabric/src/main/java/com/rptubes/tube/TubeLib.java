package com.rptubes.tube;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.rptubes.inventory.IItemHandler;

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

    public static boolean canConnect(Level level, BlockPos pos, Direction dir) {
        BlockPos neighbor = pos.relative(dir);
        BlockEntity self = level.getBlockEntity(pos);
        int selfClass = 0;
        if (self instanceof ITubeConnectable tube) {
            if ((tube.getTubeConnectableSides() & (1 << dir.ordinal())) == 0) {
                return false;
            }
            selfClass = tube.getTubeConClass();
        }

        BlockEntity other = level.getBlockEntity(neighbor);
        Direction opposite = dir.getOpposite();

        if (other instanceof ITubeConnectable tube) {
            if (!isCompatible(selfClass, tube.getTubeConClass())) {
                return false;
            }
            return (tube.getTubeConnectableSides() & (1 << opposite.ordinal())) != 0;
        }

        return isCompatible(selfClass, 0) && hasItemHandler(level, neighbor, opposite);
    }

    public static boolean hasItemHandler(Level level, BlockPos pos, Direction side) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be == null) {
            return false;
        }
        IItemHandler handler = com.rptubes.tube.MachineLib.getHandler(level, pos, side);
        return handler != null && handler.getSlots() > 0;
    }

    public static int getConnections(Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof ITubeConnectable tube)) {
            return 0;
        }
        int mask = 0;
        int sides = tube.getTubeConnectableSides();
        for (Direction dir : Direction.values()) {
            int bit = 1 << dir.ordinal();
            if ((sides & bit) != 0 && canConnect(level, pos, dir)) {
                mask |= bit;
            }
        }
        return mask;
    }

    /**
     * Find the best exit side from {@code pos} toward a valid inventory / sink.
     * @return Direction ordinal, or -1 if none.
     */
    public static int findRoute(Level level, BlockPos pos, TubeItem item, int sideMask, int state, int startDir) {
        PriorityQueue<Node> queue = new PriorityQueue<>();
        Set<BlockPos> visited = new HashSet<>();
        visited.add(pos);

        for (Direction dir : Direction.values()) {
            int bit = 1 << dir.ordinal();
            if ((sideMask & bit) == 0) {
                continue;
            }
            BlockPos next = pos.relative(dir);
            int weight = dir.ordinal() == startDir ? 0 : 1;
            queue.add(new Node(next, dir.ordinal(), dir.getOpposite().ordinal(), weight));
        }

        while (!queue.isEmpty()) {
            Node node = queue.poll();
            if (visited.contains(node.pos)) {
                continue;
            }
            visited.add(node.pos);

            if (state != 3 && MachineLib.canInsert(level, node.pos, Direction.from3DDataValue(node.enterSide), item.item)) {
                return node.startSide;
            }

            BlockEntity be = level.getBlockEntity(node.pos);
            if (!(be instanceof ITubeConnectable tube)) {
                continue;
            }

            if (tube.tubeItemCanEnter(node.enterSide, state, item)) {
                return node.startSide;
            }
            if (!tube.tubeItemCanEnter(node.enterSide, 0, item) || !tube.canRouteItems()) {
                continue;
            }

            int cons = getConnections(level, node.pos) & ~(1 << node.enterSide);
            for (Direction dir : Direction.values()) {
                int bit = 1 << dir.ordinal();
                if ((cons & bit) == 0) {
                    continue;
                }
                BlockPos next = node.pos.relative(dir);
                if (visited.contains(next)) {
                    continue;
                }
                int w = node.weight + 1 + tube.tubeWeight(dir.ordinal(), state);
                queue.add(new Node(next, node.startSide, dir.getOpposite().ordinal(), w));
            }
        }
        return -1;
    }

    public static boolean addToTubeRoute(Level level, ItemStack stack, BlockPos src, BlockPos tubePos, Direction enterSide) {
        return addToTubeRoute(level, new TubeItem(enterSide.ordinal(), stack), src, tubePos, enterSide);
    }

    public static boolean addToTubeRoute(Level level, TubeItem ti, BlockPos src, BlockPos tubePos, Direction enterSide) {
        BlockEntity be = level.getBlockEntity(tubePos);
        if (!(be instanceof ITubeConnectable tube)) {
            return false;
        }
        ti.mode = 1;
        int side = findRoute(level, src, ti, 1 << enterSide.getOpposite().ordinal(), 1, 0);
        if (side < 0) {
            return false;
        }
        return tube.tubeItemEnter(enterSide.ordinal(), 0, ti);
    }

    /**
     * BFS through routing tubes for an inventory containing items matching {@code map}.
     * {@code startDir} is the first direction to leave {@code start} (usually facing opposite).
     */
    public static InventorySource findInventorySource(Level level, BlockPos start, Direction startDir,
                                                     MachineLib.FilterMap map) {
        PriorityQueue<SrcNode> queue = new PriorityQueue<>();
        Set<BlockPos> visited = new HashSet<>();
        visited.add(start);
        queue.add(new SrcNode(start.relative(startDir), startDir.getOpposite(), 0));

        while (!queue.isEmpty()) {
            SrcNode node = queue.poll();
            if (!visited.add(node.pos)) {
                continue;
            }

            BlockEntity be = level.getBlockEntity(node.pos);
            boolean routing = be instanceof ITubeConnectable tube && tube.canRouteItems();
            IItemHandler handler = MachineLib.getHandler(level, node.pos, node.enterSide);

            if (handler != null && !routing) {
                if (map == null || map.size() == 0) {
                    for (int i = 0; i < handler.getSlots(); i++) {
                        if (!handler.getStackInSlot(i).isEmpty()) {
                            return new InventorySource(node.pos, node.enterSide, 0);
                        }
                    }
                } else {
                    int slot = MachineLib.matchAnyStack(map, level, node.pos, node.enterSide);
                    if (slot >= 0) {
                        return new InventorySource(node.pos, node.enterSide, slot);
                    }
                }
            }

            if (!routing) {
                continue;
            }

            int cons = getConnections(level, node.pos) & ~(1 << node.enterSide.ordinal());
            for (Direction dir : Direction.values()) {
                int bit = 1 << dir.ordinal();
                if ((cons & bit) == 0) {
                    continue;
                }
                BlockPos next = node.pos.relative(dir);
                if (visited.contains(next)) {
                    continue;
                }
                queue.add(new SrcNode(next, dir.getOpposite(), node.weight + 1));
            }
        }
        return null;
    }

    public record InventorySource(BlockPos invPos, Direction invSide, int filterSlot) {
    }

    private static final class SrcNode implements Comparable<SrcNode> {
        final BlockPos pos;
        final Direction enterSide;
        final int weight;

        SrcNode(BlockPos pos, Direction enterSide, int weight) {
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
