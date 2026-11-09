package com.rptubes.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.tube.PaintColors;
import com.rptubes.tube.TubeItem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;

/**
 * Port of eloraam.machine.TileTubeRenderer — draws items traveling through tubes.
 * Position: tube center + side vector * d, where d = progress/128 * 0.5,
 * inverted while the item is moving inward (not yet scheduled).
 */
public class TubeItemRenderer<T extends PneumaticTubeBlockEntity> implements BlockEntityRenderer<T> {
    private final ItemRenderer itemRenderer;

    public TubeItemRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(T be, float partialTick, PoseStack poseStack, MultiBufferSource buffers,
                       int packedLight, int packedOverlay) {
        if (be.getPaintColor() > 0) {
            this.renderPaintOverlay(poseStack, buffers, be.getPaintColor());
        }
        var contents = be.getTubeFlow().contents;
        if (contents.isEmpty()) {
            return;
        }
        for (TubeItem ti : contents) {
            if (ti.item.isEmpty()) {
                continue;
            }
            double x = 0.5;
            double y = 0.5;
            double z = 0.5;
            if (ti.side >= 0) {
                // True interpolation between last tick and current — never overshoots
                float progress = ti.lastProgress + (ti.progress - ti.lastProgress) * partialTick;
                double d = progress / 128.0 * 0.5;
                if (!ti.scheduled) {
                    d = 0.5 - d;
                }
                Direction dir = Direction.from3DDataValue(ti.side);
                x += dir.getStepX() * d;
                y += dir.getStepY() * d;
                z += dir.getStepZ() * d;
            }
            // else: awaiting the server's routing decision — hold at the tube center
            poseStack.pushPose();
            poseStack.translate(x, y, z);
            // Point-billboard, yaw only: each item turns horizontally to face
            // the viewer's position (never tilts vertically). Client-side, so
            // every player sees items facing themselves on their own screen.
            var cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            double wx = be.getBlockPos().getX() + x;
            double wz = be.getBlockPos().getZ() + z;
            float yaw = (float) Math.toDegrees(Math.atan2(cam.x - wx, cam.z - wz));
            poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
            // FIXED renders ~2x larger than GROUND; halve the scale to keep size
            poseStack.scale(0.35f, 0.35f, 0.35f);
            this.itemRenderer.renderStatic(ti.item, net.minecraft.world.item.ItemDisplayContext.FIXED,
                    packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffers, be.getLevel(), 0);
            poseStack.popPose();
        }
    }

    private void renderPaintOverlay(PoseStack poseStack, MultiBufferSource buffers, int paint) {
        int rgb = PaintColors.rgb(paint);
        float r = ((rgb >> 16) & 0xFF) / 255f;
        float g = ((rgb >> 8) & 0xFF) / 255f;
        float b = (rgb & 0xFF) / 255f;
        float a = 0.35f;
        Matrix4f mat = poseStack.last().pose();
        VertexConsumer buf = buffers.getBuffer(RenderType.lightning());
        float n = 0.26f;
        float x = 0.74f;
        quad(buf, mat, n, n, n, x, n, n, x, x, n, n, x, n, r, g, b, a);
        quad(buf, mat, n, n, x, n, x, x, x, x, x, x, n, x, r, g, b, a);
        quad(buf, mat, n, n, n, n, n, x, x, n, x, x, n, n, r, g, b, a);
        quad(buf, mat, n, x, n, x, x, n, x, x, x, n, x, x, r, g, b, a);
        quad(buf, mat, n, n, n, n, x, n, n, x, x, n, n, x, r, g, b, a);
        quad(buf, mat, x, n, n, x, n, x, x, x, x, x, x, n, r, g, b, a);
    }

    private static void quad(VertexConsumer buf, Matrix4f mat,
                             float x1, float y1, float z1, float x2, float y2, float z2,
                             float x3, float y3, float z3, float x4, float y4, float z4,
                             float r, float g, float b, float a) {
        buf.vertex(mat, x1, y1, z1).color(r, g, b, a).endVertex();
        buf.vertex(mat, x2, y2, z2).color(r, g, b, a).endVertex();
        buf.vertex(mat, x3, y3, z3).color(r, g, b, a).endVertex();
        buf.vertex(mat, x4, y4, z4).color(r, g, b, a).endVertex();
    }
}
