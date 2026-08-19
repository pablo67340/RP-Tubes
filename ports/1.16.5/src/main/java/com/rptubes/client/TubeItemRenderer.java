package com.rptubes.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.tube.TubeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import java.util.List;

/**
 * Port of eloraam.machine.TileTubeRenderer — draws items traveling through tubes.
 * Position: tube center + side vector * d, where d = progress/128 * 0.5,
 * inverted while the item is moving inward (not yet scheduled).
 */
public class TubeItemRenderer<T extends PneumaticTubeBlockEntity> extends TileEntityRenderer<T> {

    public TubeItemRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(T be, float partialTick, MatrixStack poseStack, IRenderTypeBuffer buffers,
                       int packedLight, int packedOverlay) {
        List<TubeItem> contents = be.getTubeFlow().contents;
        if (contents.isEmpty()) {
            return;
        }
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
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
            Vector3d cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            double wx = be.getBlockPos().getX() + x;
            double wz = be.getBlockPos().getZ() + z;
            float yaw = (float) Math.toDegrees(Math.atan2(cam.x - wx, cam.z - wz));
            poseStack.mulPose(Vector3f.YP.rotationDegrees(yaw));
            // FIXED renders ~2x larger than GROUND; halve the scale to keep size
            poseStack.scale(0.35f, 0.35f, 0.35f);
            itemRenderer.renderStatic(ti.item, ItemCameraTransforms.TransformType.FIXED,
                    packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffers);
            poseStack.popPose();
        }
    }
}
