package com.rptubes.client;

import com.mojang.blaze3d.vertex.PoseStack;
//? if <1.20.1 {
/*import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.tube.PaintColors;
import com.rptubes.tube.TubeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
*///?} else if fabric && >=1.20.6 && <1.21.11 {
/*import com.mojang.math.Axis;
import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.tube.PaintColors;
import com.rptubes.tube.TubeItem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
*///?} else if <1.21.11 {
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.tube.PaintColors;
import com.rptubes.tube.TubeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
//?} else if !neoforge {
/*import com.mojang.math.Axis;
import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.tube.TubeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
*///?} else {
/*import com.mojang.math.Axis;
import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.tube.PaintColors;
import com.rptubes.tube.TubeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.SubmitNodeCollector;
*///?}
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
//? if <1.21.11 {
import net.minecraft.client.renderer.entity.ItemRenderer;
//?} else if <26.1.2 {
/*import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
*///?} else {
/*import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
*///?}
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
//? if >=1.21.11 {
/*import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/^*
 * Port of eloraam.machine.TileTubeRenderer — draws items traveling through tubes.
 * Position: tube center + side vector * d, where d = progress/128 * 0.5,
 * inverted while the item is moving inward (not yet scheduled).
 *
 * 1.21.9+ renderer: per-frame state is extracted from the block entity into a
 * render state, then submitted to the ordered node collector.
 ^/
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
*///?} else if (fabric && >=1.20.6) || <1.20.1 {

/*/^*
 * Port of eloraam.machine.TileTubeRenderer — draws items traveling through tubes.
 * Position: tube center + side vector * d, where d = progress/128 * 0.5,
 * inverted while the item is moving inward (not yet scheduled).
 ^/
public class TubeItemRenderer<T extends PneumaticTubeBlockEntity> implements BlockEntityRenderer<T> {
    private final ItemRenderer itemRenderer;
*///?} else {
import org.joml.Matrix4f;

/**
 * Port of eloraam.machine.TileTubeRenderer — draws items traveling through tubes.
 * Position: tube center + side vector * d, where d = progress/128 * 0.5,
 * inverted while the item is moving inward (not yet scheduled).
 */
public class TubeItemRenderer<T extends PneumaticTubeBlockEntity> implements BlockEntityRenderer<T> {
    private final ItemRenderer itemRenderer;
//?}

    public TubeItemRenderer(BlockEntityRendererProvider.Context context) {
        //? if <1.21.11 {
        this.itemRenderer = context.getItemRenderer();
        //?}
    }

    @Override
    //? if <1.21.11 {
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
    //?} else if fabric {
    /*public TubeRenderState createRenderState() {
        return new TubeRenderState();
    }

    @Override
    public void extractRenderState(T be, TubeRenderState state, float partialTick, Vec3 cameraPos,
                                   ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(be, state, partialTick, cameraPos, breakProgress);
        state.items.clear();
        state.paintColor = be.getPaintColor();
        ItemModelResolver resolver = Minecraft.getInstance().getItemModelResolver();
        for (TubeItem ti : be.getTubeFlow().contents) {
    *///?} else {
    /*public TubeRenderState createRenderState() {
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
    *///?}
            if (ti.item.isEmpty()) {
                continue;
            }
            //? if >=1.21.11 {
            /*Entry entry = new Entry();
            *///?}
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
            //? if <1.21.11 {
            poseStack.pushPose();
            poseStack.translate(x, y, z);
            //?} else {
            /*entry.x = x;
            entry.y = y;
            entry.z = z;
            *///?}
            // Point-billboard, yaw only: each item turns horizontally to face
            // the viewer's position (never tilts vertically). Client-side, so
            // every player sees items facing themselves on their own screen.
            //? if <1.21.11 {
            var cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            //?}
            double wx = be.getBlockPos().getX() + x;
            double wz = be.getBlockPos().getZ() + z;
            //? if <1.20.1 {
            /*float yaw = (float) Math.toDegrees(Math.atan2(cam.x - wx, cam.z - wz));
            poseStack.mulPose(Vector3f.YP.rotationDegrees(yaw));
            *///?} else if <1.21.11 {
            float yaw = (float) Math.toDegrees(Math.atan2(cam.x - wx, cam.z - wz));
            poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
            //?} else if !neoforge {
            /*entry.yaw = (float) Math.toDegrees(Math.atan2(cameraPos.x - wx, cameraPos.z - wz));
            resolver.updateForTopItem(entry.itemState, ti.item, ItemDisplayContext.FIXED, be.getLevel(), null, 0);
            state.items.add(entry);
        }
    }

    @Override
    public void submit(TubeRenderState state, PoseStack poseStack, SubmitNodeCollector collector,
                       CameraRenderState cameraState) {
        for (Entry entry : state.items) {
            poseStack.pushPose();
            poseStack.translate(entry.x, entry.y, entry.z);
            poseStack.mulPose(Axis.YP.rotationDegrees(entry.yaw));
            *///?} else {
            /*entry.yaw = (float) Math.toDegrees(Math.atan2(cameraPos.x - wx, cameraPos.z - wz));
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
            *///?}
            // FIXED renders ~2x larger than GROUND; halve the scale to keep size
            poseStack.scale(0.35f, 0.35f, 0.35f);
            //? if <1.20.1 {
            /*this.itemRenderer.renderStatic(ti.item, net.minecraft.client.renderer.block.model.ItemTransforms.TransformType.FIXED,
                    packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffers, 0);
            *///?} else if <1.21.11 {
            this.itemRenderer.renderStatic(ti.item, net.minecraft.world.item.ItemDisplayContext.FIXED,
                    packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffers, be.getLevel(), 0);
            //?} else {
            /*entry.itemState.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            *///?}
            poseStack.popPose();
        }
    }
    //? if fabric && <1.20.6 {

    /*/^* Thin colored glass around the tube so dyed pipes are visible in-world. ^/
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
    *///?} else if fabric && <1.21.1 {

    /*private void renderPaintOverlay(PoseStack poseStack, MultiBufferSource buffers, int paint) {
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
    *///?} else if neoforge && <1.21.1 {

    /*/^* Thin colored glass around the tube so dyed pipes are visible in-world. ^/
    private void renderPaintOverlay(PoseStack poseStack, MultiBufferSource buffers, int paint) {
        int rgb = PaintColors.rgb(paint);
        float r = ((rgb >> 16) & 0xFF) / 255f;
        float g = ((rgb >> 8) & 0xFF) / 255f;
        float b = (rgb & 0xFF) / 255f;
        float a = 0.35f;
        Matrix4f mat = poseStack.last().pose();
        VertexConsumer vc = buffers.getBuffer(RenderType.lightning());
        float n = 0.26f;
        float x = 0.74f;
        quad(vc, mat, n, n, n, x, n, n, x, x, n, n, x, n, r, g, b, a);
        quad(vc, mat, n, n, x, n, x, x, x, x, x, x, n, x, r, g, b, a);
        quad(vc, mat, n, n, n, n, n, x, x, n, x, x, n, n, r, g, b, a);
        quad(vc, mat, n, x, n, x, x, n, x, x, x, n, x, x, r, g, b, a);
        quad(vc, mat, n, n, n, n, x, n, n, x, x, n, n, x, r, g, b, a);
        quad(vc, mat, x, n, n, x, n, x, x, x, x, x, x, n, r, g, b, a);
    }

    private static void quad(VertexConsumer vc, Matrix4f mat,
                             float x1, float y1, float z1, float x2, float y2, float z2,
                             float x3, float y3, float z3, float x4, float y4, float z4,
                             float r, float g, float b, float a) {
        vc.vertex(mat, x1, y1, z1).color(r, g, b, a).endVertex();
        vc.vertex(mat, x2, y2, z2).color(r, g, b, a).endVertex();
        vc.vertex(mat, x3, y3, z3).color(r, g, b, a).endVertex();
        vc.vertex(mat, x4, y4, z4).color(r, g, b, a).endVertex();
    }
    *///?} else if fabric && <1.21.11 {

    /*private void renderPaintOverlay(PoseStack poseStack, MultiBufferSource buffers, int paint) {
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
        buf.addVertex(mat, x1, y1, z1).setColor(r, g, b, a);
        buf.addVertex(mat, x2, y2, z2).setColor(r, g, b, a);
        buf.addVertex(mat, x3, y3, z3).setColor(r, g, b, a);
        buf.addVertex(mat, x4, y4, z4).setColor(r, g, b, a);
    }
    *///?} else if forge && >=1.21.1 && <1.21.11 {

    /*/^* Thin colored glass around the tube so dyed pipes are visible in-world. ^/
    private void renderPaintOverlay(PoseStack poseStack, MultiBufferSource buffers, int paint) {
        int rgb = PaintColors.rgb(paint);
        float r = ((rgb >> 16) & 0xFF) / 255f;
        float g = ((rgb >> 8) & 0xFF) / 255f;
        float b = (rgb & 0xFF) / 255f;
        VertexConsumer buf = buffers.getBuffer(RenderType.lightning());
        Matrix4f mat = poseStack.last().pose();
        float n = 0.26f;
        float x = 0.74f;
        quad(buf, mat, n, n, n, x, n, n, x, x, n, n, x, n, r, g, b);
        quad(buf, mat, n, n, x, n, x, x, x, x, x, x, n, x, r, g, b);
        quad(buf, mat, n, n, n, n, n, x, x, n, x, x, n, n, r, g, b);
        quad(buf, mat, n, x, n, x, x, n, x, x, x, n, x, x, r, g, b);
        quad(buf, mat, n, n, n, n, x, n, n, x, x, n, n, x, r, g, b);
        quad(buf, mat, x, n, n, x, n, x, x, x, x, x, x, n, r, g, b);
    }

    private static void quad(VertexConsumer buf, Matrix4f mat,
                             float x1, float y1, float z1, float x2, float y2, float z2,
                             float x3, float y3, float z3, float x4, float y4, float z4,
                             float r, float g, float b) {
        buf.addVertex(mat, x1, y1, z1).setColor(r, g, b, 0.35f);
        buf.addVertex(mat, x2, y2, z2).setColor(r, g, b, 0.35f);
        buf.addVertex(mat, x3, y3, z3).setColor(r, g, b, 0.35f);
        buf.addVertex(mat, x4, y4, z4).setColor(r, g, b, 0.35f);
    }
    *///?} else if >=1.21.1 && <1.21.11 {

    /** Thin colored glass around the tube so dyed pipes are visible in-world. */
    private void renderPaintOverlay(PoseStack poseStack, MultiBufferSource buffers, int paint) {
        int rgb = PaintColors.rgb(paint);
        float r = ((rgb >> 16) & 0xFF) / 255f;
        float g = ((rgb >> 8) & 0xFF) / 255f;
        float b = (rgb & 0xFF) / 255f;
        float a = 0.35f;
        Matrix4f mat = poseStack.last().pose();
        VertexConsumer vc = buffers.getBuffer(RenderType.lightning());
        float n = 0.26f;
        float x = 0.74f;
        quad(vc, mat, n, n, n, x, n, n, x, x, n, n, x, n, r, g, b, a);
        quad(vc, mat, n, n, x, n, x, x, x, x, x, x, n, x, r, g, b, a);
        quad(vc, mat, n, n, n, n, n, x, x, n, x, x, n, n, r, g, b, a);
        quad(vc, mat, n, x, n, x, x, n, x, x, x, n, x, x, r, g, b, a);
        quad(vc, mat, n, n, n, n, x, n, n, x, x, n, n, x, r, g, b, a);
        quad(vc, mat, x, n, n, x, n, x, x, x, x, x, x, n, r, g, b, a);
    }

    private static void quad(VertexConsumer vc, Matrix4f mat,
                             float x1, float y1, float z1, float x2, float y2, float z2,
                             float x3, float y3, float z3, float x4, float y4, float z4,
                             float r, float g, float b, float a) {
        vc.addVertex(mat, x1, y1, z1).setColor(r, g, b, a);
        vc.addVertex(mat, x2, y2, z2).setColor(r, g, b, a);
        vc.addVertex(mat, x3, y3, z3).setColor(r, g, b, a);
        vc.addVertex(mat, x4, y4, z4).setColor(r, g, b, a);
    }
    //?} else if <1.21.1 {

    /*/^* Thin colored glass around the tube so dyed pipes are visible in-world. ^/
    private void renderPaintOverlay(PoseStack poseStack, MultiBufferSource buffers, int paint) {
        int rgb = PaintColors.rgb(paint);
        float r = ((rgb >> 16) & 0xFF) / 255f;
        float g = ((rgb >> 8) & 0xFF) / 255f;
        float b = (rgb & 0xFF) / 255f;
        VertexConsumer buf = buffers.getBuffer(RenderType.lightning());
        Matrix4f mat = poseStack.last().pose();
        float n = 0.26f;
        float x = 0.74f;
        quad(buf, mat, n, n, n, x, n, n, x, x, n, n, x, n, r, g, b);
        quad(buf, mat, n, n, x, n, x, x, x, x, x, x, n, x, r, g, b);
        quad(buf, mat, n, n, n, n, n, x, x, n, x, x, n, n, r, g, b);
        quad(buf, mat, n, x, n, x, x, n, x, x, x, n, x, x, r, g, b);
        quad(buf, mat, n, n, n, n, x, n, n, x, x, n, n, x, r, g, b);
        quad(buf, mat, x, n, n, x, n, x, x, x, x, x, x, n, r, g, b);
    }

    private static void quad(VertexConsumer buf, Matrix4f mat,
                             float x1, float y1, float z1, float x2, float y2, float z2,
                             float x3, float y3, float z3, float x4, float y4, float z4,
                             float r, float g, float b) {
        buf.vertex(mat, x1, y1, z1).color(r, g, b, 0.35f).endVertex();
        buf.vertex(mat, x2, y2, z2).color(r, g, b, 0.35f).endVertex();
        buf.vertex(mat, x3, y3, z3).color(r, g, b, 0.35f).endVertex();
        buf.vertex(mat, x4, y4, z4).color(r, g, b, 0.35f).endVertex();
    }
    *///?} else if neoforge {

    /*private static void quad(com.mojang.blaze3d.vertex.VertexConsumer vc, org.joml.Matrix4f mat,
                             float x1, float y1, float z1, float x2, float y2, float z2,
                             float x3, float y3, float z3, float x4, float y4, float z4,
                             float r, float g, float b, float a) {
        vc.addVertex(mat, x1, y1, z1).setColor(r, g, b, a);
        vc.addVertex(mat, x2, y2, z2).setColor(r, g, b, a);
        vc.addVertex(mat, x3, y3, z3).setColor(r, g, b, a);
        vc.addVertex(mat, x4, y4, z4).setColor(r, g, b, a);
    }
    *///?}
}
