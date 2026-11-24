package com.rptubes.compat.cbm;

import codechicken.multipart.MultiPartRegistry;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;
import codechicken.multipart.api.IPartConverter;
import codechicken.multipart.api.IPlacementConverter;
import com.rptubes.RPTubes;
import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.registry.ModBlocks;
import com.rptubes.tube.ITubeConnectable;
import com.rptubes.tube.TubeAccess;
import com.rptubes.tube.TubeLib;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Forge Multipart support: pneumatic, redstone, restriction and mag tubes as center parts, so they
 * share a block space with covers, hollow covers and wires. Only loaded when Forge Multipart is.
 */
public final class TubeParts {
    private TubeParts() {
    }

    public enum Kind {
        PNEUMATIC("pneumatic_tube", ModBlocks.PNEUMATIC_TUBE::get, false),
        REDSTONE("redstone_tube", ModBlocks.REDSTONE_TUBE::get, true),
        RESTRICTION("restriction_tube", ModBlocks.RESTRICTION_TUBE::get, false),
        MAG("mag_tube", ModBlocks.MAG_TUBE::get, false);

        final ResourceLocation id;
        final Supplier<Block> block;
        final boolean redstone;

        Kind(String name, Supplier<Block> block, boolean redstone) {
            this.id = new ResourceLocation(RPTubes.MOD_ID, name);
            this.block = block;
            this.redstone = redstone;
        }

        TubePart create() {
            return this.redstone ? new RedstoneTubePart(this) : new TubePart(this);
        }

        static Kind of(Block block) {
            for (Kind kind : values()) {
                if (kind.block.get() == block) {
                    return kind;
                }
            }
            return null;
        }

        static Kind of(ResourceLocation id) {
            for (Kind kind : values()) {
                if (kind.id.equals(id)) {
                    return kind;
                }
            }
            return null;
        }
    }

    /** Called from init, while this mod is the active container, as Forge Multipart requires. */
    public static void init() {
        List<ResourceLocation> ids = new ArrayList<>();
        for (Kind kind : Kind.values()) {
            ids.add(kind.id);
        }
        MultiPartRegistry.registerParts((id, client) -> Kind.of(id).create(), ids);
        MultiPartRegistry.registerConverter(new Converter());
        MultiPartRegistry.registerPlacementConverter(new Placement());
        TubeAccess.setResolver(TubeParts::find);
    }

    private static ITubeConnectable find(TileEntity te) {
        if (te instanceof TileMultipart) {
            TMultiPart part = ((TileMultipart) te).partMap(6);
            if (part instanceof TubePart) {
                return ((TubePart) part).tubeOrNull();
            }
        }
        return null;
    }

    /** Turns a placed tube block into a part when a cover is added to its space. */
    private static final class Converter implements IPartConverter {
        @Override
        public boolean canConvert(World world, BlockPos pos, IBlockState state) {
            return Kind.of(state.getBlock()) != null && world.getTileEntity(pos) instanceof PneumaticTubeBlockEntity;
        }

        @Override
        public TMultiPart convert(World world, BlockPos pos, IBlockState state) {
            TubePart part = Kind.of(state.getBlock()).create();
            part.convertFrom((PneumaticTubeBlockEntity) world.getTileEntity(pos), TubeLib.getConnections(world, pos));
            return part;
        }
    }

    /** Places a tube item as a part when the target space already holds multiparts. */
    private static final class Placement implements IPlacementConverter {
        @Override
        public boolean canConvert(ItemStack stack) {
            return stack.getItem() instanceof ItemBlock && Kind.of(((ItemBlock) stack.getItem()).getBlock()) != null;
        }

        @Override
        public TMultiPart convert(ItemStack stack, World world, BlockPos pos, EnumFacing sideHit, Vec3d hitVec,
                                  EntityLivingBase placer, EnumHand hand) {
            TubePart part = Kind.of(((ItemBlock) stack.getItem()).getBlock()).create();
            part.placedFrom(stack);
            return part;
        }
    }
}
