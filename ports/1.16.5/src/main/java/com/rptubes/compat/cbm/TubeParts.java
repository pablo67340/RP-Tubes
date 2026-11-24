package com.rptubes.compat.cbm;

import codechicken.multipart.api.MultiPartType;
import codechicken.multipart.api.PartConverter;
import codechicken.multipart.api.SimpleMultiPartType;
import codechicken.multipart.api.part.TMultiPart;
import codechicken.multipart.block.TileMultiPart;
import com.rptubes.RPTubes;
import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.registry.ModBlocks;
import com.rptubes.tube.ITubeConnectable;
import com.rptubes.tube.TubeAccess;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * CB Multipart support: pneumatic, redstone, restriction and mag tubes as center parts, so they
 * share a block space with covers, hollow covers and wires. Only loaded when CB Multipart is.
 */
public final class TubeParts {
    private TubeParts() {
    }

    public enum Kind {
        PNEUMATIC("pneumatic_tube", ModBlocks.PNEUMATIC_TUBE::get, false),
        REDSTONE("redstone_tube", ModBlocks.REDSTONE_TUBE::get, true),
        RESTRICTION("restriction_tube", ModBlocks.RESTRICTION_TUBE::get, false),
        MAG("mag_tube", ModBlocks.MAG_TUBE::get, false);

        final String name;
        final Supplier<Block> block;
        final boolean redstone;
        MultiPartType<TubePart> type;

        Kind(String name, Supplier<Block> block, boolean redstone) {
            this.name = name;
            this.block = block;
            this.redstone = redstone;
        }

        TubePart create() {
            return this.type.createPartServer(null);
        }

        static Kind of(Block block) {
            for (Kind kind : values()) {
                if (kind.block.get() == block) {
                    return kind;
                }
            }
            return null;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void init(IEventBus modBus) {
        modBus.addGenericListener((Class) MultiPartType.class, (Consumer<RegistryEvent.Register<MultiPartType<?>>>) TubeParts::registerTypes);
        modBus.addGenericListener(PartConverter.class, TubeParts::registerConverters);
        TubeAccess.setResolver(TubeParts::find);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> TubePartsClient.init(modBus));
    }

    private static void registerTypes(RegistryEvent.Register<MultiPartType<?>> event) {
        for (Kind kind : Kind.values()) {
            kind.type = new SimpleMultiPartType<>(client -> kind.redstone ? new RedstoneTubePart(kind) : new TubePart(kind));
            kind.type.setRegistryName(RPTubes.MOD_ID, kind.name);
            event.getRegistry().register(kind.type);
        }
    }

    private static void registerConverters(RegistryEvent.Register<PartConverter> event) {
        event.getRegistry().register(new Converter().setRegistryName(RPTubes.MOD_ID, "tubes"));
    }

    private static ITubeConnectable find(TileEntity te) {
        if (te instanceof TileMultiPart) {
            TMultiPart part = ((TileMultiPart) te).getSlottedPart(6);
            if (part instanceof TubePart) {
                return ((TubePart) part).tubeOrNull();
            }
        }
        return null;
    }

    private static final class Converter extends PartConverter {
        @Override
        public ActionResult<Collection<TMultiPart>> convert(IWorld world, BlockPos pos, BlockState state) {
            Kind kind = Kind.of(state.getBlock());
            TileEntity te = world.getBlockEntity(pos);
            if (kind == null || !(te instanceof PneumaticTubeBlockEntity)) {
                return emptyResultList();
            }
            TubePart part = kind.create();
            part.convertFrom((PneumaticTubeBlockEntity) te, state);
            return ActionResult.success(Collections.singletonList(part));
        }

        @Override
        public ActionResult<TMultiPart> convert(ItemUseContext context) {
            ItemStack stack = context.getItemInHand();
            if (!(stack.getItem() instanceof BlockItem)) {
                return emptyResult();
            }
            Kind kind = Kind.of(((BlockItem) stack.getItem()).getBlock());
            if (kind == null) {
                return emptyResult();
            }
            TubePart part = kind.create();
            part.placedFrom(stack);
            return ActionResult.success(part);
        }
    }
}
