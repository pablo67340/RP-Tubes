package com.rptubes.block;

import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.tube.TubeLib;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Thin pneumatic tube. Connection mask mirrors RedPower TubeLib side bits.
 * On 1.12 connections are computed via getActualState instead of stored,
 * because six booleans do not fit in four bits of metadata.
 */
public class PneumaticTubeBlock extends Block {
    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool EAST = PropertyBool.create("east");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool WEST = PropertyBool.create("west");
    public static final PropertyBool UP = PropertyBool.create("up");
    public static final PropertyBool DOWN = PropertyBool.create("down");

    protected static final Map<EnumFacing, PropertyBool> PROPERTY_BY_DIRECTION =
            new EnumMap<EnumFacing, PropertyBool>(EnumFacing.class);

    static {
        PROPERTY_BY_DIRECTION.put(EnumFacing.NORTH, NORTH);
        PROPERTY_BY_DIRECTION.put(EnumFacing.EAST, EAST);
        PROPERTY_BY_DIRECTION.put(EnumFacing.SOUTH, SOUTH);
        PROPERTY_BY_DIRECTION.put(EnumFacing.WEST, WEST);
        PROPERTY_BY_DIRECTION.put(EnumFacing.UP, UP);
        PROPERTY_BY_DIRECTION.put(EnumFacing.DOWN, DOWN);
    }

    // Match OG RenderTube doubleBox bounds: 0.25..0.75 => 4..12
    private static final AxisAlignedBB CORE = box(4, 4, 4, 12, 12, 12);
    private static final AxisAlignedBB ARM_N = box(4, 4, 0, 12, 12, 4);
    private static final AxisAlignedBB ARM_S = box(4, 4, 12, 12, 12, 16);
    private static final AxisAlignedBB ARM_W = box(0, 4, 4, 4, 12, 12);
    private static final AxisAlignedBB ARM_E = box(12, 4, 4, 16, 12, 12);
    private static final AxisAlignedBB ARM_D = box(4, 0, 4, 12, 4, 12);
    private static final AxisAlignedBB ARM_U = box(4, 12, 4, 12, 16, 12);

    private static AxisAlignedBB box(double x1, double y1, double z1, double x2, double y2, double z2) {
        return new AxisAlignedBB(x1 / 16, y1 / 16, z1 / 16, x2 / 16, y2 / 16, z2 / 16);
    }

    private final Supplier<? extends TileEntity> beFactory;

    public PneumaticTubeBlock() {
        this(PneumaticTubeBlockEntity::new);
    }

    public PneumaticTubeBlock(Supplier<? extends TileEntity> beFactory) {
        super(Material.IRON);
        this.beFactory = beFactory;
        this.setDefaultState(this.blockState.getBaseState()
                .withProperty(NORTH, false).withProperty(EAST, false).withProperty(SOUTH, false)
                .withProperty(WEST, false).withProperty(UP, false).withProperty(DOWN, false));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (!(world instanceof World)) {
            return state;
        }
        World realWorld = (World) world;
        for (EnumFacing dir : EnumFacing.values()) {
            state = state.withProperty(PROPERTY_BY_DIRECTION.get(dir), TubeLib.canConnect(realWorld, pos, dir));
        }
        return state;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        AxisAlignedBB bounds = CORE;
        if (world instanceof World) {
            IBlockState actual = this.getActualState(state, world, pos);
            if (actual.getValue(NORTH)) bounds = bounds.union(ARM_N);
            if (actual.getValue(SOUTH)) bounds = bounds.union(ARM_S);
            if (actual.getValue(WEST)) bounds = bounds.union(ARM_W);
            if (actual.getValue(EAST)) bounds = bounds.union(ARM_E);
            if (actual.getValue(DOWN)) bounds = bounds.union(ARM_D);
            if (actual.getValue(UP)) bounds = bounds.union(ARM_U);
        }
        return bounds;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox,
                                      List<AxisAlignedBB> collidingBoxes, net.minecraft.entity.Entity entity,
                                      boolean isActualState) {
        IBlockState actual = isActualState ? state : this.getActualState(state, world, pos);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, CORE);
        if (actual.getValue(NORTH)) addCollisionBoxToList(pos, entityBox, collidingBoxes, ARM_N);
        if (actual.getValue(SOUTH)) addCollisionBoxToList(pos, entityBox, collidingBoxes, ARM_S);
        if (actual.getValue(WEST)) addCollisionBoxToList(pos, entityBox, collidingBoxes, ARM_W);
        if (actual.getValue(EAST)) addCollisionBoxToList(pos, entityBox, collidingBoxes, ARM_E);
        if (actual.getValue(DOWN)) addCollisionBoxToList(pos, entityBox, collidingBoxes, ARM_D);
        if (actual.getValue(UP)) addCollisionBoxToList(pos, entityBox, collidingBoxes, ARM_U);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        if (!world.isRemote) {
            // connections are actual-state; just re-render and notify the tube tile
            world.notifyBlockUpdate(pos, state, state, 3);
        }
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return this.beFactory.get();
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity be = world.getTileEntity(pos);
        if (be instanceof com.rptubes.blockentity.PneumaticTubeBlockEntity) {
            ((com.rptubes.blockentity.PneumaticTubeBlockEntity) be).dropContents();
        }
        super.breakBlock(world, pos, state);
    }
}
