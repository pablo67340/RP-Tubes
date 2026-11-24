package com.rptubes.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.rptubes.RPTubes;
import com.rptubes.blockentity.BatteryBoxBlockEntity;
import com.rptubes.menu.BatteryBoxMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

/**
 * OG GuiBatteryBox. The left bar showed network voltage; with FE it shows the larger of
 * inflow and outflow on a square-root scale so a single solar panel still registers.
 */
public class BatteryBoxScreen extends AbstractContainerScreen<BatteryBoxMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(RPTubes.MOD_ID, "textures/gui/battery_box.png");

    public BatteryBoxScreen(BatteryBoxMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageHeight = 170;
        this.titleLabelX = 60;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(PoseStack pose, float partialTick, int mouseX, int mouseY) {
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        this.minecraft.getTextureManager().bind(TEXTURE);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        this.blit(pose, x, y, 0, 0, this.imageWidth, this.imageHeight);

        int in = this.menu.getInRate();
        int out = this.menu.getOutRate();
        int storage = this.menu.getStorage();
        int max = BatteryBoxMenu.MAX_STORAGE;
        double flow = Math.max(in, out) / (double) BatteryBoxBlockEntity.MAX_IO;
        int s = (int) Math.min(48, Math.round(48 * Math.sqrt(flow)));
        if (s > 0) {
            this.blit(pose, x + 71, y + 73 - s, 176, 48 - s, 5, s);
        }
        if (in > 0 || out > 0) {
            this.blit(pose, x + 72, y + 17, 197, 16, 3, 6);
        }
        if (in > 0 && storage < max) {
            this.blit(pose, x + 82, y + 37, 197, 0, 10, 8);
        }
        if (out > 0 && storage > 0) {
            this.blit(pose, x + 82, y + 55, 197, 8, 10, 8);
        }
        s = Math.min(48, storage * 48 / max);
        if (s > 0) {
            this.blit(pose, x + 98, y + 73 - s, 181, 48 - s, 16, s);
        }
        if (storage >= max) {
            this.blit(pose, x + 103, y + 17, 200, 16, 6, 6);
        }
    }

    @Override
    public void render(PoseStack pose, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(pose);
        super.render(pose, mouseX, mouseY, partialTick);
        this.renderTooltip(pose, mouseX, mouseY);
    }
}
