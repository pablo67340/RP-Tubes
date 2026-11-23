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
import com.rptubes.menu.AutoCraftingTableMk2Menu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
//? if <1.21.11 {
import net.minecraft.client.renderer.GameRenderer;
//?}
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

/** Port of Tubestuff's GuiCraftingTable — 186x249 custom background, no labels. */
public class AutoCraftingTableMk2Screen extends AbstractContainerScreen<AutoCraftingTableMk2Menu> {
    private static final ResourceLocation TEXTURE =
            //? if <1.21.1 {
            /*new ResourceLocation("rptubes", "textures/gui/act_mk2.png");
            *///?} else {
            ResourceLocation.fromNamespaceAndPath("rptubes", "textures/gui/act_mk2.png");
            //?}
    private static final int OUTPUT_X = 134;
    private static final int OUTPUT_Y = 124;

    public AutoCraftingTableMk2Screen(AutoCraftingTableMk2Menu menu, Inventory inv, Component title) {
        //? if <26.1.2 {
        super(menu, inv, title);
        this.imageWidth = 186;
        this.imageHeight = 249;
        //?} else {
        /*// 26.1: imageWidth/imageHeight are final, must go through the ctor
        super(menu, inv, title, 186, 249);
        *///?}
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
    }

    @Override
    //? if <1.20.1 {
    /*protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
    *///?} else if <26.1.2 {
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
    //?} else {
    /*protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
    *///?}
        // The original drew no labels; the background art has no room for them
    }

    @Override
    //? if <1.20.1 {
    /*public void render(PoseStack pose, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(pose);
        super.render(pose, mouseX, mouseY, partialTick);
        ItemStack preview = this.renderResultPreview(pose);
        this.renderTooltip(pose, mouseX, mouseY);
    *///?} else if <1.20.6 {
    /*public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        ItemStack preview = this.renderResultPreview(graphics);
        this.renderTooltip(graphics, mouseX, mouseY);
    *///?} else if <26.1.2 {
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        ItemStack preview = this.renderResultPreview(graphics);
        this.renderTooltip(graphics, mouseX, mouseY);
    //?} else {
    /*public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        ItemStack preview = this.renderResultPreview(graphics);
    *///?}
        // Hovering the empty output slot explains what the template will make
        if (!preview.isEmpty() && this.hoveredSlot != null
                && this.hoveredSlot.index == 0 && !this.hoveredSlot.hasItem()) {
            //? if <1.20.1 {
            /*this.renderTooltip(pose, preview, mouseX, mouseY);
            *///?} else if <1.21.11 {
            graphics.renderTooltip(this.font, preview, mouseX, mouseY);
            //?} else {
            /*graphics.setTooltipForNextFrame(this.font, preview, mouseX, mouseY);
            *///?}
        }
    }

    /**
     * Improvement over the original: while the output slot is empty, show what
     * the ghost template resolves to, washed out so it reads as "not yet real".
     */
    //? if <1.20.1 {
    /*private ItemStack renderResultPreview(PoseStack pose) {
    *///?} else if <26.1.2 {
    private ItemStack renderResultPreview(GuiGraphics graphics) {
    //?} else {
    /*private ItemStack renderResultPreview(GuiGraphicsExtractor graphics) {
    *///?}
        if (this.menu.getSlot(0).hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack preview = this.menu.getResultPreview();
        if (preview.isEmpty()) {
            return ItemStack.EMPTY;
        }
        int x = this.leftPos + OUTPUT_X;
        int y = this.topPos + OUTPUT_Y;
        //? if <1.20.1 {
        /*this.itemRenderer.renderGuiItem(preview, x, y);
        // Wash the sprite out with the slot's background gray at ~55% alpha...
        pose.pushPose();
        pose.translate(0, 0, 250);
        fill(pose, x, y, x + 16, y + 16, 0x8C8B8B8B);
        pose.popPose();
        // ...but draw the count above the wash so the craft yield stays readable
        pose.pushPose();
        pose.translate(0, 0, 300);
        this.itemRenderer.renderGuiItemDecorations(this.font, preview, x, y);
        pose.popPose();
        *///?} else if <1.21.11 {
        graphics.renderItem(preview, x, y);
        // Wash the sprite out with the slot's background gray at ~55% alpha...
        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 250);
        graphics.fill(x, y, x + 16, y + 16, 0x8C8B8B8B);
        graphics.pose().popPose();
        // ...but draw the count above the wash so the craft yield stays readable
        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 300);
        graphics.renderItemDecorations(this.font, preview, x, y);
        graphics.pose().popPose();
        //?} else if <26.1.2 {
        /*// 1.21.6+ GUI renders in submission order, so later draws layer on top:
        // sprite, then the gray wash, then the count above the wash
        graphics.renderItem(preview, x, y);
        graphics.fill(x, y, x + 16, y + 16, 0x8C8B8B8B);
        graphics.renderItemDecorations(this.font, preview, x, y);
        *///?} else {
        /*// 1.21.6+ GUI renders in submission order, so later draws layer on top:
        // sprite, then the gray wash, then the count above the wash
        graphics.item(preview, x, y);
        graphics.fill(x, y, x + 16, y + 16, 0x8C8B8B8B);
        graphics.itemDecorations(this.font, preview, x, y);
        *///?}
        return preview;
    }
}
