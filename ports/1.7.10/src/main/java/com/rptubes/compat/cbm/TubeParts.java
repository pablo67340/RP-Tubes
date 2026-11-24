package com.rptubes.compat.cbm;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.raytracer.RayTracer;
import codechicken.lib.vec.BlockCoord;
import codechicken.multipart.MultiPartRegistry;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;
import com.rptubes.RPTubes;
import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.registry.ModBlocks;
import com.rptubes.tube.ITubeConnectable;
import com.rptubes.tube.TubeAccess;
import com.rptubes.tube.TubeLib;
import com.rptubes.util.BlockPos;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

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
        PNEUMATIC("pneumatic_tube", () -> ModBlocks.PNEUMATIC_TUBE.get(), false),
        REDSTONE("redstone_tube", () -> ModBlocks.REDSTONE_TUBE.get(), true),
        RESTRICTION("restriction_tube", () -> ModBlocks.RESTRICTION_TUBE.get(), false),
        MAG("mag_tube", () -> ModBlocks.MAG_TUBE.get(), false);

        final String id;
        private final Supplier<Block> block;
        final boolean redstone;

        Kind(String name, Supplier<Block> block, boolean redstone) {
            this.id = RPTubes.MOD_ID + ":" + name;
            this.block = block;
            this.redstone = redstone;
        }

        Block block() {
            return this.block.get();
        }

        TubePart create() {
            return this.redstone ? new RedstoneTubePart(this) : new TubePart(this);
        }

        static Kind of(Block block) {
            for (Kind kind : values()) {
                if (kind.block() == block) {
                    return kind;
                }
            }
            return null;
        }

        static Kind of(String id) {
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
        String[] ids = new String[Kind.values().length];
        for (Kind kind : Kind.values()) {
            ids[kind.ordinal()] = kind.id;
        }
        MultiPartRegistry.registerParts(new Factory(), ids);
        MultiPartRegistry.registerConverter(new Converter());
        MinecraftForge.EVENT_BUS.register(new Placement());
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

    private static final class Factory implements MultiPartRegistry.IPartFactory2 {
        @Override
        public TMultiPart createPart(String name, NBTTagCompound nbt) {
            Kind kind = Kind.of(name);
            return kind == null ? null : kind.create();
        }

        @Override
        public TMultiPart createPart(String name, MCDataInput packet) {
            Kind kind = Kind.of(name);
            return kind == null ? null : kind.create();
        }
    }

    /** Turns a placed tube block into a part when a cover is added to its space. */
    private static final class Converter implements MultiPartRegistry.IPartConverter {
        @Override
        public Iterable<Block> blockTypes() {
            List<Block> blocks = new ArrayList<>();
            for (Kind kind : Kind.values()) {
                blocks.add(kind.block());
            }
            return blocks;
        }

        @Override
        public TMultiPart convert(World world, BlockCoord pos) {
            Kind kind = Kind.of(world.getBlock(pos.x, pos.y, pos.z));
            TileEntity te = world.getTileEntity(pos.x, pos.y, pos.z);
            if (kind == null || !(te instanceof PneumaticTubeBlockEntity)) {
                return null;
            }
            TubePart part = kind.create();
            part.convertFrom((PneumaticTubeBlockEntity) te, TubeLib.getConnections(world, new BlockPos(pos.x, pos.y, pos.z)));
            return part;
        }
    }

    /**
     * Places a tube item as a part when the target space already holds multiparts. Forge Multipart
     * 1.7.10 has no placement converter, so this does what its own part items do: a click that lands
     * inside a block space tries that space first, then the space beside the clicked face.
     */
    public static final class Placement {
        @SubscribeEvent
        public void onInteract(PlayerInteractEvent event) {
            if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK || event.world.isRemote) {
                return;
            }
            EntityPlayer player = event.entityPlayer;
            ItemStack held = player.getHeldItem();
            Kind kind = held == null ? null : Kind.of(Block.getBlockFromItem(held.getItem()));
            if (kind == null) {
                return;
            }
            BlockCoord pos = new BlockCoord(event.x, event.y, event.z);
            if ((this.hitInside(event, player) && this.place(event.world, pos, kind, player, held))
                    || this.place(event.world, pos.offset(event.face), kind, player, held)) {
                event.setCanceled(true);
            }
        }

        private boolean hitInside(PlayerInteractEvent event, EntityPlayer player) {
            MovingObjectPosition hit = RayTracer.retraceBlock(event.world, player, event.x, event.y, event.z);
            if (hit == null || hit.hitVec == null) {
                return false;
            }
            double along;
            switch (event.face >> 1) {
                case 0: along = hit.hitVec.yCoord - event.y; break;
                case 1: along = hit.hitVec.zCoord - event.z; break;
                default: along = hit.hitVec.xCoord - event.x; break;
            }
            double depth = (event.face & 1) == 0 ? 1 - along : along;
            return depth < 0.999;
        }

        private boolean place(World world, BlockCoord pos, Kind kind, EntityPlayer player, ItemStack held) {
            TileEntity te = world.getTileEntity(pos.x, pos.y, pos.z);
            if (!(te instanceof TileMultipart)) {
                return false;
            }
            TubePart part = kind.create();
            if (!TileMultipart.canPlacePart(world, pos, part)) {
                return false;
            }
            TileMultipart.addPart(world, pos, part);
            Block block = kind.block();
            world.playSoundEffect(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, block.stepSound.func_150496_b(),
                    (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
            if (!player.capabilities.isCreativeMode) {
                held.stackSize--;
                if (held.stackSize <= 0) {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                }
            }
            return true;
        }
    }
}
