package com.rptubes.client;

import com.rptubes.block.MagTubeBlock;
import com.rptubes.block.PneumaticTubeBlock;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * ISBRH drawing tubes the way the multipart models on newer versions do: four double-sided
 * walls cut from the middle of the side texture with its rails running along the tube, end
 * caps on the open sides of bends and junctions, and coils at both ends of a straight MagTube.
 * Coordinates are in pixels (0..16) within the block.
 */
public class TubeRenderer implements ISimpleBlockRenderingHandler {
    /** Vanilla face shading, by ForgeDirection ordinal. */
    private static final float[] SHADE = {0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F};
    /** Axis (0 x, 1 y, 2 z) of each ForgeDirection ordinal. */
    private static final int[] AXIS = {1, 1, 2, 2, 0, 0};
    private static final int FRONT = 1;
    private static final int BACK = 2;
    private static final int BOTH = FRONT | BACK;

    @Override
    public void renderInventoryBlock(Block block, int meta, int modelId, RenderBlocks renderer) {
        if (!(block instanceof PneumaticTubeBlock)) {
            return;
        }
        Tessellator tes = Tessellator.instance;
        tes.startDrawingQuads();
        // vertical straight piece, like the item model on newer versions
        new Quads(tes, -0.5, -0.5, -0.5, true, null).tube((PneumaticTubeBlock) block, meta, 0b000011);
        tes.draw();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess access, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        if (!(block instanceof PneumaticTubeBlock)) {
            return false;
        }
        World world = renderer.blockAccess instanceof World ? (World) renderer.blockAccess
                : net.minecraft.client.Minecraft.getMinecraft().theWorld;
        renderTube(renderer, block, x, y, z, PneumaticTubeBlock.getConnectionMask(world, x, y, z));
        return true;
    }

    /** Draws the tube with the given connections (bits are ForgeDirection ordinals). */
    public static void renderTube(RenderBlocks renderer, Block block, int x, int y, int z, int cons) {
        if (!(block instanceof PneumaticTubeBlock)) {
            return;
        }
        IBlockAccess access = renderer.blockAccess;
        Quads q = new Quads(Tessellator.instance, x, y, z, false,
                renderer.hasOverrideBlockTexture() ? renderer.overrideBlockTexture : null);
        int meta = 0;
        if (access != null) {
            meta = access.getBlockMetadata(x, y, z);
            q.brightness = block.getMixedBrightnessForBlock(access, x, y, z);
            int color = block.colorMultiplier(access, x, y, z);
            q.r = (color >> 16 & 255) / 255.0F;
            q.g = (color >> 8 & 255) / 255.0F;
            q.b = (color & 255) / 255.0F;
        }
        q.tube((PneumaticTubeBlock) block, meta, cons);
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return PneumaticTubeBlock.renderId;
    }

    private static final class Quads {
        private final Tessellator tes;
        private final double ox;
        private final double oy;
        private final double oz;
        private final boolean inventory;
        private final IIcon override;
        int brightness;
        float r = 1.0F;
        float g = 1.0F;
        float b = 1.0F;

        Quads(Tessellator tes, double ox, double oy, double oz, boolean inventory, IIcon override) {
            this.tes = tes;
            this.ox = ox;
            this.oy = oy;
            this.oz = oz;
            this.inventory = inventory;
            this.override = override;
        }

        void tube(PneumaticTubeBlock block, int meta, int cons) {
            IIcon side = block.getSideIcon(meta);
            int axis = straightAxis(cons);
            if (axis >= 0) {
                this.walls(axis, 0, 16, side);
                if (block instanceof MagTubeBlock) {
                    MagTubeBlock mag = (MagTubeBlock) block;
                    this.coil(axis, 2, 6, mag.getCoilIcon(), mag.getCoilEndIcon());
                    this.coil(axis, 10, 14, mag.getCoilIcon(), mag.getCoilEndIcon());
                }
                return;
            }
            IIcon end = block.getEndIcon(meta);
            for (int dir = 0; dir < 6; dir++) {
                int a = AXIS[dir];
                boolean positive = (dir & 1) == 1;
                if ((cons & (1 << dir)) != 0) {
                    this.walls(a, positive ? 12 : 0, positive ? 16 : 4, side);
                } else {
                    this.rect(a, positive ? 12 : 4, (a + 1) % 3, 4, 12, (a + 2) % 3, 4, 12, end, BOTH);
                }
            }
        }

        /** Axis of a straight piece (exactly two opposite connections), otherwise -1. */
        private static int straightAxis(int cons) {
            switch (cons) {
                case 0b000011: return 1;
                case 0b001100: return 2;
                case 0b110000: return 0;
                default: return -1;
            }
        }

        /** The four walls of a tube section along axis, from a0 to a1. */
        private void walls(int axis, double a0, double a1, IIcon icon) {
            for (int n = 0; n < 3; n++) {
                if (n == axis) {
                    continue;
                }
                int across = 3 - axis - n;
                this.rect(n, 4, across, 4, 12, axis, a0, a1, icon, BOTH);
                this.rect(n, 12, across, 4, 12, axis, a0, a1, icon, BOTH);
            }
        }

        /** A MagTube coil: a box 12 pixels across, from a0 to a1 along axis. */
        private void coil(int axis, double a0, double a1, IIcon coil, IIcon coilEnd) {
            int o1 = (axis + 1) % 3;
            int o2 = (axis + 2) % 3;
            this.rect(axis, a0, o1, 2, 14, o2, 2, 14, coilEnd, BACK);
            this.rect(axis, a1, o1, 2, 14, o2, 2, 14, coilEnd, FRONT);
            for (int n = 0; n < 3; n++) {
                if (n == axis) {
                    continue;
                }
                int across = 3 - axis - n;
                this.rect(n, 2, across, 2, 14, axis, a0, a1, coil, BACK);
                this.rect(n, 14, across, 2, 14, axis, a0, a1, coil, FRONT);
            }
        }

        /**
         * A rectangle on the plane where axis n equals plane, spanning u0..u1 on axis ua and
         * v0..v1 on axis va, textured with those same pixel coordinates. FRONT faces +n, BACK -n.
         */
        private void rect(int n, double plane, int ua, double u0, double u1, int va, double v0, double v1,
                          IIcon icon, int sides) {
            if (this.override != null) {
                icon = this.override;
            }
            // (ua, va, n) is an even permutation exactly when va follows ua; then u x v points along +n
            boolean even = (ua + 1) % 3 == va;
            if ((sides & FRONT) != 0) {
                this.face(n, true, plane, ua, u0, u1, va, v0, v1, icon, !even);
            }
            if ((sides & BACK) != 0) {
                this.face(n, false, plane, ua, u0, u1, va, v0, v1, icon, even);
            }
        }

        private void face(int n, boolean positive, double plane, int ua, double u0, double u1, int va,
                          double v0, double v1, IIcon icon, boolean reversed) {
            int dir = n == 0 ? (positive ? 5 : 4) : n == 1 ? (positive ? 1 : 0) : (positive ? 3 : 2);
            if (this.inventory) {
                float s = positive ? 1.0F : -1.0F;
                this.tes.setNormal(n == 0 ? s : 0.0F, n == 1 ? s : 0.0F, n == 2 ? s : 0.0F);
            } else {
                float shade = SHADE[dir];
                this.tes.setBrightness(this.brightness);
                this.tes.setColorOpaque_F(this.r * shade, this.g * shade, this.b * shade);
            }
            double[] us = reversed ? new double[]{u0, u0, u1, u1} : new double[]{u0, u1, u1, u0};
            double[] vs = reversed ? new double[]{v0, v1, v1, v0} : new double[]{v0, v0, v1, v1};
            double[] p = new double[3];
            p[n] = plane;
            for (int i = 0; i < 4; i++) {
                p[ua] = us[i];
                p[va] = vs[i];
                this.tes.addVertexWithUV(this.ox + p[0] / 16.0, this.oy + p[1] / 16.0, this.oz + p[2] / 16.0,
                        icon.getInterpolatedU(us[i]), icon.getInterpolatedV(vs[i]));
            }
        }
    }
}
