package com.rptubes.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.tube.PaintColors;
import com.rptubes.tube.TubeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * Port of eloraam.machine.TileTubeRenderer — draws items traveling through tubes.
 * Position: tube center + side vector * d, where d = progress/128 * 0.5,
 * inverted while the item is moving inward (not yet scheduled).
 *
 * 1.21.9+ renderer: per-frame state is extracted from the block entity into a
 * render state, then submitted to the ordered node collector.
 */
public class TubeItemRenderer<T extends PneumaticTubeBlockEntity>
        implements BlockEntityRenderer<T, TubeItemRenderer.TubeRenderState> {

    public static class TubeRenderState extends BlockEntityRenderState {
        final List<Entry> items = new ArrayList<>();
        int paintColor;
    }

    static class Entry {
        final ItemStackRenderState itemState = new ItemStackRenderState();
        double x;
        double y;
        double z;
        float yaw;
    }

    public TubeItemRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public TubeRenderState createRenderState() {
        return new TubeRenderState();
    }

    @Override
    public void extractRenderState(T be, TubeRenderState state, float partialTick, Vec3 cameraPos,
                                   ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(be, state, partialTick, cameraPos, breakProgress);
        state.paintColor = be.getPaintColor();
        state.items.clear();
        ItemModelResolver resolver = Minecraft.getInstance().getItemModelResolver();
        for (TubeItem ti : be.getTubeFlow().contents) {
            if (ti.item.isEmpty()) {
                continue;
            }
            Entry entry = new Entry();
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
            entry.x = x;
            entry.y = y;
            entry.z = z;
            // Point-billboard, yaw only: each item turns horizontally to face
            // the viewer's position (never tilts vertically). Client-side, so
            // every player sees items facing themselves on their own screen.
            double wx = be.getBlockPos().getX() + x;
            double wz = be.getBlockPos().getZ() + z;
            entry.yaw = (float) Math.toDegrees(Math.atan2(cameraPos.x - wx, cameraPos.z - wz));
            resolver.updateForTopItem(entry.itemState, ti.item, ItemDisplayContext.FIXED, be.getLevel(), null, 0);
            state.items.add(entry);
        }
    }

    @Override
    public void submit(TubeRenderState state, PoseStack poseStack, SubmitNodeCollector collector,
                       CameraRenderState cameraState) {
        if (state.paintColor > 0) {
            int rgb = PaintColors.rgb(state.paintColor);
            float r = ((rgb >> 16) & 0xFF) / 255f;
            float g = ((rgb >> 8) & 0xFF) / 255f;
            float b = (rgb & 0xFF) / 255f;
            float a = 0.35f;
            collector.submitCustomGeometry(poseStack, RenderTypes.lightning(), (pose, vc) -> {
                var mat = pose.pose();
                float n = 0.26f;
                float x = 0.74f;
                quad(vc, mat, n, n, n, x, n, n, x, x, n, n, x, n, r, g, b, a);
                quad(vc, mat, n, n, x, n, x, x, x, x, x, x, n, x, r, g, b, a);
                quad(vc, mat, n, n, n, n, n, x, x, n, x, x, n, n, r, g, b, a);
                quad(vc, mat, n, x, n, x, x, n, x, x, x, n, x, x, r, g, b, a);
                quad(vc, mat, n, n, n, n, x, n, n, x, x, n, n, x, r, g, b, a);
                quad(vc, mat, x, n, n, x, n, x, x, x, x, x, x, n, r, g, b, a);
            });
        }
        for (Entry entry : state.items) {
            poseStack.pushPose();
            poseStack.translate(entry.x, entry.y, entry.z);
            poseStack.mulPose(Axis.YP.rotationDegrees(entry.yaw));
            // FIXED renders ~2x larger than GROUND; halve the scale to keep size
            poseStack.scale(0.35f, 0.35f, 0.35f);
            entry.itemState.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
    }

    private static void quad(com.mojang.blaze3d.vertex.VertexConsumer vc, org.joml.Matrix4f mat,
                             float x1, float y1, float z1, float x2, float y2, float z2,
                             float x3, float y3, float z3, float x4, float y4, float z4,
                             float r, float g, float b, float a) {
        vc.addVertex(mat, x1, y1, z1).setColor(r, g, b, a);
        vc.addVertex(mat, x2, y2, z2).setColor(r, g, b, a);
        vc.addVertex(mat, x3, y3, z3).setColor(r, g, b, a);
        vc.addVertex(mat, x4, y4, z4).setColor(r, g, b, a);
    }
}
