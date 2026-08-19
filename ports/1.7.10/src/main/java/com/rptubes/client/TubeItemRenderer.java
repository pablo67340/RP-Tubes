package com.rptubes.client;

import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import com.rptubes.tube.TubeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * Port of eloraam.machine.TileTubeRenderer — draws items traveling through tubes.
 * Position: tube center + side vector * d, where d = progress/128 * 0.5,
 * inverted while the item is moving inward (not yet scheduled).
 * On 1.7.10 the item is drawn via a dummy EntityItem with renderInFrame set,
 * the standard trick before ItemRenderer.renderStatic existed.
 */
public class TubeItemRenderer extends TileEntitySpecialRenderer {

    private EntityItem dummy;

    @Override
    public void renderTileEntityAt(TileEntity te, double rx, double ry, double rz, float partialTick) {
        if (!(te instanceof PneumaticTubeBlockEntity)) {
            return;
        }
        PneumaticTubeBlockEntity be = (PneumaticTubeBlockEntity) te;
        List<TubeItem> contents = be.getTubeFlow().contents;
        if (contents.isEmpty()) {
            return;
        }
        if (this.dummy == null || this.dummy.worldObj != be.getWorldObj()) {
            this.dummy = new EntityItem(be.getWorldObj());
            this.dummy.hoverStart = 0.0F;
        }
        int light = be.getWorldObj().getLightBrightnessForSkyBlocks(te.xCoord, te.yCoord, te.zCoord, 0);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, light % 65536, (float) (light / 65536));

        boolean oldInFrame = RenderItem.renderInFrame;
        RenderItem.renderInFrame = true;
        for (TubeItem ti : contents) {
            if (ti.item == null) {
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
                ForgeDirection dir = ForgeDirection.getOrientation(ti.side);
                x += dir.offsetX * d;
                y += dir.offsetY * d;
                z += dir.offsetZ * d;
            }
            // else: awaiting the server's routing decision — hold at the tube center

            GL11.glPushMatrix();
            GL11.glTranslated(rx + x, ry + y - 0.25, rz + z);
            // Point-billboard, yaw only: each item turns horizontally to face
            // the viewer's position (never tilts vertically). Client-side, so
            // every player sees items facing themselves on their own screen.
            Entity view = Minecraft.getMinecraft().renderViewEntity;
            float yaw = 0.0F;
            if (view != null) {
                double wx = te.xCoord + x;
                double wz = te.zCoord + z;
                yaw = (float) Math.toDegrees(Math.atan2(view.posX - wx, view.posZ - wz));
            }
            GL11.glScalef(0.7F, 0.7F, 0.7F);
            this.dummy.setEntityItemStack(ti.item);
            this.dummy.hoverStart = 0.0F;
            RenderManager.instance.renderEntityWithPosYaw(this.dummy, 0.0D, 0.0D, 0.0D, yaw, 0.0F);
            GL11.glPopMatrix();
        }
        RenderItem.renderInFrame = oldInFrame;
    }
}
