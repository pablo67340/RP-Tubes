package com.rptubes.client;

//? if <1.20.1 {
/*import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
*///?} else if <1.21.11 {
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
//?} else if <26.1.2 {
/*import net.minecraft.client.gui.GuiGraphics;
*///?} else {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?}
import com.rptubes.RPTubes;
import com.rptubes.blockentity.BatteryBoxBlockEntity;
import com.rptubes.menu.BatteryBoxMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
//? if <1.21.11 {
import net.minecraft.client.renderer.GameRenderer;
//?}
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * OG GuiBatteryBox. The left bar showed network voltage; with FE it shows the larger of
 * inflow and outflow on a square-root scale so a single solar panel still registers.
 */
public class BatteryBoxScreen extends AbstractContainerScreen<BatteryBoxMenu> {
    private static final ResourceLocation TEXTURE =
            //? if <1.21.1 {
            /*new ResourceLocation(RPTubes.MOD_ID, "textures/gui/battery_box.png");
            *///?} else {
            ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, "textures/gui/battery_box.png");
            //?}

    public BatteryBoxScreen(BatteryBoxMenu menu, Inventory inv, Component title) {
        //? if <26.1.2 {
        super(menu, inv, title);
        this.imageHeight = 170;
        //?} else {
        /*super(menu, inv, title, 176, 170);
        *///?}
        this.titleLabelX = 60;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    //? if <1.20.1 {
    /*protected void renderBg(PoseStack graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, TEXTURE);
    *///?} else if <1.21.11 {
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    //?} else if <26.1.2 {
    /*protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
    *///?} else {
    /*public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
    *///?}
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        this.draw(graphics, x, y, 0, 0, this.imageWidth, this.imageHeight);

        int in = this.menu.getInRate();
        int out = this.menu.getOutRate();
        int storage = this.menu.getStorage();
        int max = BatteryBoxMenu.MAX_STORAGE;
        double flow = Math.max(in, out) / (double) BatteryBoxBlockEntity.MAX_IO;
        int s = (int) Math.min(48, Math.round(48 * Math.sqrt(flow)));
        if (s > 0) {
            this.draw(graphics, x + 71, y + 73 - s, 176, 48 - s, 5, s);
        }
        if (in > 0 || out > 0) {
            this.draw(graphics, x + 72, y + 17, 197, 16, 3, 6);
        }
        if (in > 0 && storage < max) {
            this.draw(graphics, x + 82, y + 37, 197, 0, 10, 8);
        }
        if (out > 0 && storage > 0) {
            this.draw(graphics, x + 82, y + 55, 197, 8, 10, 8);
        }
        s = Math.min(48, storage * 48 / max);
        if (s > 0) {
            this.draw(graphics, x + 98, y + 73 - s, 181, 48 - s, 16, s);
        }
        if (storage >= max) {
            this.draw(graphics, x + 103, y + 17, 200, 16, 6, 6);
        }
    }

    //? if <1.20.1 {
    /*private void draw(PoseStack graphics, int x, int y, int u, int v, int w, int h) {
        this.blit(graphics, x, y, u, v, w, h);
    *///?} else if <1.21.11 {
    private void draw(GuiGraphics graphics, int x, int y, int u, int v, int w, int h) {
        graphics.blit(TEXTURE, x, y, u, v, w, h);
    //?} else if <26.1.2 {
    /*private void draw(GuiGraphics graphics, int x, int y, int u, int v, int w, int h) {
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, (float) u, (float) v, w, h, 256, 256);
    *///?} else {
    /*private void draw(GuiGraphicsExtractor graphics, int x, int y, int u, int v, int w, int h) {
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, (float) u, (float) v, w, h, 256, 256);
    *///?}
    }

    @Override
    //? if <1.20.1 {
    /*public void render(PoseStack pose, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(pose);
        super.render(pose, mouseX, mouseY, partialTick);
        this.renderTooltip(pose, mouseX, mouseY);
    *///?} else if <1.20.6 {
    /*public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    *///?} else if <26.1.2 {
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    //?} else {
    /*public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    *///?}
    }
}
