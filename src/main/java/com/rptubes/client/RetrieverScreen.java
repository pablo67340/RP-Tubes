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
import com.rptubes.menu.RetrieverMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
//? if <1.21.11 {
import net.minecraft.client.renderer.GameRenderer;
//?}
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

/** OG GuiRetriever — retriever.png, 3x3 grid, paint color selector, mode button, slot highlight. */
public class RetrieverScreen extends AbstractContainerScreen<RetrieverMenu> {
    private static final ResourceLocation TEXTURE =
            //? if <1.21.1 {
            /*new ResourceLocation(RPTubes.MOD_ID, "textures/gui/retriever.png");
            *///?} else {
            ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, "textures/gui/retriever.png");
            //?}

    private static final int[] PAINT_COLORS = {
            0xFFFFFF, 0xFF8000, 0xFF00FF, 0x6C83FF, 0xFFFF00, 0x00FF00, 0xFF6480, 0x535353,
            0x939393, 0x00FFFF, 0x8000FF, 0x0000FF, 0x4F2700, 0x008000, 0xFF0000, 0x1F1F1F};

    public RetrieverScreen(RetrieverMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 65;
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

        int color = this.menu.getColor();
        if (color > 0) {
            //? if <1.20.1 {
            /*fill(pose, x + 122, y + 59, x + 126, y + 63, 0xFF000000 | PAINT_COLORS[color - 1]);
        } else {
            this.blit(pose, x + 122, y + 59, 187, 0, 4, 4);
            *///?} else if <1.21.11 {
            graphics.fill(x + 122, y + 59, x + 126, y + 63, 0xFF000000 | PAINT_COLORS[color - 1]);
        } else {
            graphics.blit(TEXTURE, x + 122, y + 59, 187, 0, 4, 4);
            //?} else {
            /*graphics.fill(x + 122, y + 59, x + 126, y + 63, 0xFF000000 | PAINT_COLORS[color - 1]);
        } else {
            graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE, x + 122, y + 59, (float) (187), (float) (0), 4, 4, 256, 256);
            *///?}
        }

        int mode = this.menu.getMode();
        //? if <1.20.1 {
        /*this.blit(pose, x + 45, y + 54, 211, 14 * mode, 14, 14);
        *///?} else if <1.21.11 {
        graphics.blit(TEXTURE, x + 45, y + 54, 211, 14 * mode, 14, 14);
        //?} else {
        /*graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE, x + 45, y + 54, (float) (211), (float) (14 * mode), 14, 14, 256, 256);
        *///?}
        if (mode == 0) {
            int select = this.menu.getSelect();
            //? if <1.20.1 {
            /*this.blit(pose, x + 60 + 18 * (select % 3), y + 15 + 18 * (select / 3), 191, 0, 20, 20);
            *///?} else if <1.21.11 {
            graphics.blit(TEXTURE, x + 60 + 18 * (select % 3), y + 15 + 18 * (select / 3), 191, 0, 20, 20);
            //?} else {
            /*graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE, x + 60 + 18 * (select % 3), y + 15 + 18 * (select / 3), (float) (191), (float) (0), 20, 20, 256, 256);
            *///?}
        }
    }

    @Override
    //? if <1.21.11 {
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
    //?} else {
    /*public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
    *///?}
        double rx = mouseX - this.leftPos;
        double ry = mouseY - this.topPos;
        if (ry >= 55 && ry <= 66 && (button == 0 || button == 1)) {
            // OG GuiRetriever: color button at (118, 55), mode button at (45, 55)
            if (rx >= 118 && rx <= 129) {
                this.sendButton(button == 0 ? 0 : 1);
                return true;
            }
            if (rx >= 45 && rx <= 58) {
                this.sendButton(button == 0 ? 2 : 3);
                return true;
            }
        }
        //? if <1.21.11 {
        return super.mouseClicked(mouseX, mouseY, button);
        //?} else {
        /*return super.mouseClicked(event, doubleClick);
        *///?}
    }

    private void sendButton(int id) {
        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, id);
        this.minecraft.getSoundManager().play(
                SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
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
