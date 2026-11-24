//? if cbm {
package com.rptubes.compat.cbm;

import codechicken.multipart.api.MultipartType;
import codechicken.multipart.api.PartConverter;
import codechicken.multipart.api.SimpleMultipartType;
import codechicken.multipart.api.part.MultiPart;
import codechicken.multipart.block.TileMultipart;
import codechicken.multipart.util.MultipartPlaceContext;
import com.rptubes.RPTubes;
import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.registry.ModBlocks;
import com.rptubes.tube.ITubeConnectable;
import com.rptubes.tube.TubeAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
//? if forge {
/*import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
*///?} else {
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
//?}

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * CB Multipart support: pneumatic, redstone, restriction and mag tubes as center parts, so they
 * share a block space with covers, hollow covers and wires. Only loaded when CB Multipart is.
 */
public final class TubeParts {
    static final DeferredRegister<MultipartType<?>> TYPES = DeferredRegister.create(MultipartType.MULTIPART_TYPES, RPTubes.MOD_ID);
    //? if <1.21.11
    private static final DeferredRegister<PartConverter> CONVERTERS = DeferredRegister.create(PartConverter.PART_CONVERTERS, RPTubes.MOD_ID);

    private TubeParts() {
    }

    public enum Kind {
        PNEUMATIC("pneumatic_tube", ModBlocks.PNEUMATIC_TUBE::get, false),
        REDSTONE("redstone_tube", ModBlocks.REDSTONE_TUBE::get, true),
        RESTRICTION("restriction_tube", ModBlocks.RESTRICTION_TUBE::get, false),
        MAG("mag_tube", ModBlocks.MAG_TUBE::get, false);

        final Supplier<Block> block;
        //? if forge {
        /*final RegistryObject<MultipartType<TubePart>> type;
        *///?} else {
        final DeferredHolder<MultipartType<?>, MultipartType<TubePart>> type;
        //?}

        Kind(String name, Supplier<Block> block, boolean redstone) {
            this.block = block;
            this.type = TYPES.register(name, () -> new SimpleMultipartType<>(client -> redstone ? new RedstoneTubePart(this) : new TubePart(this)));
        }

        TubePart create() {
            return this.type.get().createPartServer(null);
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

    public static void init(IEventBus modBus) {
        Kind.values();
        TYPES.register(modBus);
        //? if <1.21.11 {
        CONVERTERS.register("tubes", Converter::new);
        CONVERTERS.register(modBus);
        //?} else {
        /*modBus.addListener((codechicken.multipart.api.RegisterPartConvertersEvent event) -> event.register(new Converter()));
        *///?}
        TubeAccess.setResolver(TubeParts::find);
        //? if <1.21.11 {
        if (FMLEnvironment.dist == Dist.CLIENT) {
        //?} else {
        /*if (FMLEnvironment.getDist() == Dist.CLIENT) {
        *///?}
            TubePartsClient.init(modBus);
        }
    }

    private static ITubeConnectable find(BlockEntity be) {
        return be instanceof TileMultipart tile && tile.getSlottedPart(6) instanceof TubePart part ? part.tubeOrNull() : null;
    }

    private static final class Converter extends PartConverter {
        @Override
        public ConversionResult<Collection<MultiPart>> convert(LevelAccessor level, BlockPos pos, BlockState state) {
            Kind kind = Kind.of(state.getBlock());
            if (kind == null || !(level.getBlockEntity(pos) instanceof PneumaticTubeBlockEntity tube)) {
                return emptyResultList();
            }
            TubePart part = kind.create();
            part.convertFrom(tube, state);
            return ConversionResult.success(List.of(part));
        }

        @Override
        public ConversionResult<MultiPart> convert(MultipartPlaceContext context) {
            if (!(context.getItemInHand().getItem() instanceof BlockItem item)) {
                return emptyResult();
            }
            Kind kind = Kind.of(item.getBlock());
            if (kind == null) {
                return emptyResult();
            }
            TubePart part = kind.create();
            part.placedFrom(context.getItemInHand());
            return ConversionResult.success(part);
        }
    }
}
//?}
