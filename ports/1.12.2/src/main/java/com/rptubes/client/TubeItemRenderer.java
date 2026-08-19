package com.rptubes.client;

import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.tube.TubeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;

import java.util.List;

/**
 * Port of eloraam.machine.TileTubeRenderer — draws items traveling through tubes.
 * Position: tube center + side vector * d, where d = progress/128 * 0.5,
 * inverted while the item is moving inward (not yet scheduled).
 */
public class TubeItemRenderer<T extends PneumaticTubeBlockEntity> extends TileEntitySpecialRenderer<T> {

    @Override
    public void render(T be, double rx, double ry, double rz, float partialTick, int destroyStage, float alpha) {
        List<TubeItem> contents = be.getTubeFlow().contents;
        if (contents.isEmpty()) {
            return;
        }
        RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
        // Light the items with the tube block's own light value
        int light = be.getWorld().getCombinedLight(be.getPos(), 0);
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
                EnumFacing dir = EnumFacing.byIndex(ti.side);
                x += dir.getXOffset() * d;
                y += dir.getYOffset() * d;
                z += dir.getZOffset() * d;
            }
            // else: awaiting the server's routing decision — hold at the tube center
            GlStateManager.pushMatrix();
            GlStateManager.translate(rx + x, ry + y, rz + z);
            // Point-billboard, yaw only: each item turns horizontally to face
            // the viewer's position (never tilts vertically). Client-side, so
            // every player sees items facing themselves on their own screen.
            Entity view = Minecraft.getMinecraft().getRenderViewEntity();
            if (view != null) {
                double wx = be.getPos().getX() + x;
                double wz = be.getPos().getZ() + z;
                float yaw = (float) Math.toDegrees(Math.atan2(view.posX - wx, view.posZ - wz));
                GlStateManager.rotate(yaw, 0.0F, 1.0F, 0.0F);
            }
            // FIXED renders ~2x larger than GROUND; halve the scale to keep size
            GlStateManager.scale(0.35F, 0.35F, 0.35F);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, light % 65536, (float) (light / 65536));
            GlStateManager.enableRescaleNormal();
            RenderHelper.enableStandardItemLighting();
            itemRenderer.renderItem(ti.item, ItemCameraTransforms.TransformType.FIXED);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.popMatrix();
        }
    }
}
