package com.rptubes.compat.cbm;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import codechicken.microblock.ISidedHollowConnect;
import codechicken.multipart.IconHitEffects;
import codechicken.multipart.JIconHitEffects;
import codechicken.multipart.JNormalOcclusion;
import codechicken.multipart.NormalOcclusionTest;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.TSlottedPart;
import codechicken.multipart.TileMultipart;
import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.client.TubeRenderer;
import com.rptubes.tube.TubeItem;
import com.rptubes.tube.TubeLib;
import com.rptubes.util.BlockPos;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A tube in the center slot of a Forge Multipart block space. The tube's behaviour is not
 * reimplemented: the part hosts an ordinary tube tile entity that is never placed in the world,
 * and TubeAccess hands that out wherever the network looks for a tube at this position.
 *
 * The Scala traits here are plain interfaces to javac, so every trait method with a body that
 * TMultiPart also defines (occlusionTest, the hit effects) has to be overridden explicitly.
 */
public class TubePart extends TMultiPart implements TSlottedPart, JNormalOcclusion, JIconHitEffects, ISidedHollowConnect {
    private static final int CENTER_SLOT = 6;
    private static final Cuboid6 CORE = box(4, 4, 4, 12, 12, 12);
    /** Indexed by ForgeDirection ordinal. */
    private static final Cuboid6[] ARMS = {
            box(4, 0, 4, 12, 4, 12),
            box(4, 12, 4, 12, 16, 12),
            box(4, 4, 0, 12, 12, 4),
            box(4, 4, 12, 12, 12, 16),
            box(0, 4, 4, 4, 12, 12),
            box(12, 4, 4, 16, 12, 12),
    };
    private static final ArmProbe[] ARM_PROBES = new ArmProbe[6];

    static {
        for (int side = 0; side < 6; side++) {
            ARM_PROBES[side] = new ArmProbe(ARMS[side]);
        }
    }

    private static Cuboid6 box(double x1, double y1, double z1, double x2, double y2, double z2) {
        return new Cuboid6(x1 / 16, y1 / 16, z1 / 16, x2 / 16, y2 / 16, z2 / 16);
    }

    protected final TubeParts.Kind kind;
    private PneumaticTubeBlockEntity tube;
    /** Tube data read before the part had a world position to build its tile entity at. */
    private NBTTagCompound pending;
    /** The in-world tube this part was converted from, emptied once the conversion goes through. */
    private PneumaticTubeBlockEntity convertedFrom;
    private int connections;
    private int blockedSides;
    /** Loaded parts get no onAdded, so they re-read covers and neighbours on their first tick. */
    private boolean refreshOnTick;

    public TubePart(TubeParts.Kind kind) {
        this.kind = kind;
    }

    @Override
    public String getType() {
        return this.kind.id;
    }

    protected BlockPos pos() {
        return new BlockPos(this.x(), this.y(), this.z());
    }

    public PneumaticTubeBlockEntity tube() {
        if (this.tube == null) {
            this.tube = (PneumaticTubeBlockEntity) this.kind.block().createTileEntity(this.world(), 0);
            this.tube.setWorldObj(this.world());
            this.tube.readFromNBT(this.pending != null ? this.pending : new NBTTagCompound());
            this.pending = null;
            // readFromNBT also reads the position from the tag, so it has to come after
            this.tube.xCoord = this.x();
            this.tube.yCoord = this.y();
            this.tube.zCoord = this.z();
            this.tube.setBlockedSides(this.blockedSides);
        }
        return this.tube;
    }

    PneumaticTubeBlockEntity tubeOrNull() {
        return this.tile() != null && this.world() != null ? this.tube() : null;
    }

    void convertFrom(PneumaticTubeBlockEntity source, int connections) {
        this.pending = new NBTTagCompound();
        source.writeToNBT(this.pending);
        this.convertedFrom = source;
        this.connections = connections;
    }

    @Override
    public void invalidateConvertedTile() {
        if (this.convertedFrom != null) {
            // The block is about to be replaced; its contents now live in this part.
            this.convertedFrom.getTubeFlow().contents.clear();
            this.convertedFrom = null;
        }
    }

    // ---- shape and occlusion

    @Override
    public int getSlotMask() {
        return 1 << CENTER_SLOT;
    }

    @Override
    public int getHollowSize(int side) {
        return 8;
    }

    @Override
    public Iterable<Cuboid6> getOcclusionBoxes() {
        return Collections.singletonList(CORE);
    }

    @Override
    public boolean occlusionTest(TMultiPart npart) {
        return NormalOcclusionTest.apply(this, npart);
    }

    private List<Cuboid6> boxes() {
        List<Cuboid6> boxes = new ArrayList<>();
        boxes.add(CORE);
        for (int side = 0; side < 6; side++) {
            if ((this.connections & (1 << side)) != 0) {
                boxes.add(ARMS[side]);
            }
        }
        return boxes;
    }

    @Override
    public Iterable<Cuboid6> getCollisionBoxes() {
        return this.boxes();
    }

    @Override
    public Iterable<IndexedCuboid6> getSubParts() {
        List<IndexedCuboid6> parts = new ArrayList<>();
        for (Cuboid6 box : this.boxes()) {
            parts.add(new IndexedCuboid6(0, box));
        }
        return parts;
    }

    @Override
    public Cuboid6 getBounds() {
        Cuboid6 bounds = CORE.copy();
        for (Cuboid6 box : this.boxes()) {
            bounds.enclose(box);
        }
        return bounds;
    }

    private int computeBlockedSides() {
        int mask = 0;
        for (int side = 0; side < 6; side++) {
            if (!this.tile().canReplacePart(this, ARM_PROBES[side])) {
                mask |= 1 << side;
            }
        }
        return mask;
    }

    /**
     * Stand-in for NormallyOccludedPart: a Java subclass picks up TMultiPart's always-true
     * occlusionTest rather than the trait's, so it has to be stated here.
     */
    private static final class ArmProbe extends TMultiPart implements JNormalOcclusion {
        private final Cuboid6 box;

        ArmProbe(Cuboid6 box) {
            this.box = box;
        }

        @Override
        public String getType() {
            return null;
        }

        @Override
        public Iterable<Cuboid6> getOcclusionBoxes() {
            return Collections.singletonList(this.box);
        }

        @Override
        public boolean occlusionTest(TMultiPart npart) {
            return NormalOcclusionTest.apply(this, npart);
        }
    }

    // ---- state

    /** Server side: re-read covers and neighbours, and tell the client if the visible tube changed. */
    protected void refresh() {
        if (this.world().isRemote) {
            return;
        }
        int blocked = this.computeBlockedSides();
        boolean blockedChanged = blocked != this.blockedSides;
        this.blockedSides = blocked;
        this.tube().setBlockedSides(blocked);
        int conns = TubeLib.getConnections(this.world(), this.pos());
        if (conns != this.connections) {
            this.connections = conns;
            this.sendState();
        }
        if (blockedChanged) {
            // Neighbouring tube blocks work out their arms on the fly and only need a re-render
            this.world().notifyBlocksOfNeighborChange(this.x(), this.y(), this.z(), this.world().getBlock(this.x(), this.y(), this.z()));
        }
    }

    protected int glow() {
        return 0;
    }

    protected void setGlow(int glow) {
    }

    protected void sendState() {
        this.getWriteStream().writeByte(this.connections).writeByte(this.glow());
    }

    @Override
    public void read(MCDataInput packet) {
        this.connections = packet.readUByte() & 0x3F;
        this.setGlow(packet.readUByte());
        this.tile().markRender();
    }

    @Override
    public void onAdded() {
        super.onAdded();
        this.refresh();
    }

    @Override
    public void onNeighborChanged() {
        this.refresh();
    }

    @Override
    public void onPartChanged(TMultiPart part) {
        this.refresh();
    }

    @Override
    public void update() {
        if (this.refreshOnTick) {
            this.refreshOnTick = false;
            this.refresh();
        }
        PneumaticTubeBlockEntity.tick(this.world(), this.pos(), this.tube());
    }

    // ---- persistence and sync

    /** The tube's data, without building the tile entity: new parts are described before they have a tile. */
    private NBTTagCompound tubeData() {
        if (this.tube != null) {
            NBTTagCompound tag = new NBTTagCompound();
            this.tube.writeToNBT(tag);
            return tag;
        }
        return this.pending != null ? this.pending : new NBTTagCompound();
    }

    @Override
    public void save(NBTTagCompound tag) {
        tag.setTag("tube", this.tubeData());
        tag.setByte("conn", (byte) this.connections);
        tag.setByte("blocked", (byte) this.blockedSides);
        tag.setByte("glow", (byte) this.glow());
    }

    @Override
    public void load(NBTTagCompound tag) {
        this.pending = tag.getCompoundTag("tube");
        this.connections = tag.getByte("conn") & 0x3F;
        this.blockedSides = tag.getByte("blocked") & 0x3F;
        this.setGlow(tag.getByte("glow"));
        this.tube = null;
        this.refreshOnTick = true;
    }

    @Override
    public void writeDesc(MCDataOutput packet) {
        packet.writeByte(this.connections);
        packet.writeByte(this.glow());
        packet.writeNBTTagCompound(this.tubeData());
    }

    @Override
    public void readDesc(MCDataInput packet) {
        this.connections = packet.readUByte() & 0x3F;
        this.setGlow(packet.readUByte());
        this.pending = packet.readNBTTagCompound();
        this.tube = null;
    }

    // ---- drops

    @Override
    public Iterable<ItemStack> getDrops() {
        return Collections.singletonList(new ItemStack(this.kind.block()));
    }

    @Override
    public ItemStack pickItem(MovingObjectPosition hit) {
        return new ItemStack(this.kind.block());
    }

    @Override
    public float getStrength(MovingObjectPosition hit, EntityPlayer player) {
        // Forge Multipart divides part strength by 30, as McBlockPart allows for
        return this.kind.block().getPlayerRelativeBlockHardness(player, this.world(), this.x(), this.y(), this.z()) * 30;
    }

    @Override
    public void onRemoved() {
        super.onRemoved();
        if (this.world().isRemote || this.tube == null) {
            return;
        }
        Vector3 center = Vector3.fromTileEntityCenter(this.tile());
        for (TubeItem ti : this.tube.getTubeFlow().contents) {
            if (ti != null && !com.rptubes.util.Stacks.isEmpty(ti.item)) {
                TileMultipart.dropItem(ti.item.copy(), this.world(), center);
            }
        }
        this.tube.getTubeFlow().contents.clear();
    }

    // ---- rendering: the block's own box renderer for the tube, its tile renderer for items

    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderStatic(Vector3 pos, int pass) {
        if (pass != 0) {
            return false;
        }
        TubeRenderer.renderTube(new RenderBlocks(new PartAccess(this)), this.kind.block(),
                this.x(), this.y(), this.z(), this.connections);
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawBreaking(RenderBlocks renderBlocks) {
        TubeRenderer.renderTube(renderBlocks, this.kind.block(), this.x(), this.y(), this.z(), this.connections);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderDynamic(Vector3 pos, float frame, int pass) {
        PneumaticTubeBlockEntity tube = this.tubeOrNull();
        if (pass != 0 || tube == null) {
            return;
        }
        TileEntitySpecialRenderer renderer = TileEntityRendererDispatcher.instance.getSpecialRenderer(tube);
        if (renderer != null) {
            renderer.renderTileEntityAt(tube, pos.x, pos.y, pos.z, frame);
        }
    }

    /** Block metadata the tube's icons are picked by, as the in-world block would have it. */
    int renderMeta() {
        return this.glow();
    }

    Block block() {
        return this.kind.block();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getBreakingIcon(Object subPart, int side) {
        return this.getBrokenIcon(side);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getBrokenIcon(int side) {
        return this.kind.block().getIcon(side, this.glow());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addHitEffects(MovingObjectPosition hit, EffectRenderer effectRenderer) {
        IconHitEffects.addHitEffects(this, hit, effectRenderer);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addDestroyEffects(MovingObjectPosition hit, EffectRenderer effectRenderer) {
        IconHitEffects.addDestroyEffects(this, effectRenderer);
    }
}
