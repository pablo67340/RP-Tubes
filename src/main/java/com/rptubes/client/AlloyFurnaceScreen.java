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
import com.rptubes.menu.AlloyFurnaceMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
//? if <1.21.11 {
import net.minecraft.client.renderer.GameRenderer;
//?}
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AlloyFurnaceScreen extends AbstractContainerScreen<AlloyFurnaceMenu> {
    private static final ResourceLocation TEXTURE =
            //? if <1.21.1 {
            /*new ResourceLocation(RPTubes.MOD_ID, "textures/gui/alloy_furnace.png");
            *///?} else {
            ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, "textures/gui/alloy_furnace.png");
            //?}

    public AlloyFurnaceScreen(AlloyFurnaceMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        // OG GuiAlloyFurnace: title at x=60
        this.titleLabelX = 60;
    }

    @Override
    //? if <1.20.1 {
    /*protected void renderBg(PoseStack pose, float partialTick, int mouseX, int mouseY) {
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
        //? if <1.20.1 {
        /*this.blit(pose, x, y, 0, 0, this.imageWidth, this.imageHeight);
        *///?} else if <1.21.11 {
        graphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
        //?} else {
        /*graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, (float) (0), (float) (0), this.imageWidth, this.imageHeight, 256, 256);
        *///?}

        // OG GuiAlloyFurnace flame + arrow positions
        if (this.menu.isLit()) {
            int burn = this.menu.getBurnProgress();
            //? if <1.20.1 {
            /*this.blit(pose, x + 17, y + 25 + 12 - burn, 176, 12 - burn, 14, burn + 2);
            *///?} else if <1.21.11 {
            graphics.blit(TEXTURE, x + 17, y + 25 + 12 - burn, 176, 12 - burn, 14, burn + 2);
            //?} else {
            /*graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE, x + 17, y + 25 + 12 - burn, (float) (176), (float) (12 - burn), 14, burn + 2, 256, 256);
            *///?}
        }
        int cook = this.menu.getCookProgress();
        //? if <1.20.1 {
        /*this.blit(pose, x + 107, y + 34, 176, 14, cook + 1, 16);
        *///?} else if <1.21.11 {
        graphics.blit(TEXTURE, x + 107, y + 34, 176, 14, cook + 1, 16);
        //?} else {
        /*graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE, x + 107, y + 34, (float) (176), (float) (14), cook + 1, 16, 256, 256);
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
