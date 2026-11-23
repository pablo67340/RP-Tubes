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
import com.rptubes.menu.RegulatorMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
//? if <1.21.11 {
import net.minecraft.client.renderer.GameRenderer;
//?}
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

/** OG GuiRegulator — 211x167 regulator.png, three 3x3 grids, color selector, mode button. */
public class RegulatorScreen extends AbstractContainerScreen<RegulatorMenu> {
    private static final ResourceLocation TEXTURE =
            //? if <1.21.1 {
            /*new ResourceLocation(RPTubes.MOD_ID, "textures/gui/regulator.png");
            *///?} else {
            ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, "textures/gui/regulator.png");
            //?}

    private static final int[] PAINT_COLORS = {
            0xFFFFFF, 0xFF8000, 0xFF00FF, 0x6C83FF, 0xFFFF00, 0x00FF00, 0xFF6480, 0x535353,
            0x939393, 0x00FFFF, 0x8000FF, 0x0000FF, 0x4F2700, 0x008000, 0xFF0000, 0x1F1F1F};

    public RegulatorScreen(RegulatorMenu menu, Inventory inv, Component title) {
        //? if <26.1.2 {
        super(menu, inv, title);
        this.imageWidth = 211;
        this.imageHeight = 167;
        //?} else {
        /*super(menu, inv, title, 211, 167);
        *///?}
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 79;
        this.inventoryLabelX = 25;
        this.inventoryLabelY = this.imageHeight - 93;
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
            /*fill(pose, x + 140, y + 60, x + 144, y + 64, 0xFF000000 | PAINT_COLORS[color - 1]);
        } else {
            this.blit(pose, x + 140, y + 60, 212, 0, 4, 4);
        }
        this.blit(pose, x + 135, y + 19, 216, 14 * this.menu.getMode(), 14, 14);
            *///?} else if <1.21.11 {
            graphics.fill(x + 140, y + 60, x + 144, y + 64, 0xFF000000 | PAINT_COLORS[color - 1]);
        } else {
            graphics.blit(TEXTURE, x + 140, y + 60, 212, 0, 4, 4);
        }
        graphics.blit(TEXTURE, x + 135, y + 19, 216, 14 * this.menu.getMode(), 14, 14);
            //?} else {
            /*graphics.fill(x + 140, y + 60, x + 144, y + 64, 0xFF000000 | PAINT_COLORS[color - 1]);
        } else {
            graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE, x + 140, y + 60, (float) (212), (float) (0), 4, 4, 256, 256);
        }
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE, x + 135, y + 19, (float) (216), (float) (14 * this.menu.getMode()), 14, 14, 256, 256);
            *///?}
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
        if (rx >= 136 && rx <= 147 && (button == 0 || button == 1)) {
            // OG GuiRegulator: color button at (136, 56), mode button at (136, 19)
            if (ry >= 56 && ry <= 67) {
                this.sendButton(button == 0 ? 0 : 1);
                return true;
            }
            if (ry >= 19 && ry <= 32) {
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
    //? if <1.20.1 {

    /*@Override
    public void render(PoseStack pose, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(pose);
        super.render(pose, mouseX, mouseY, partialTick);
        this.renderTooltip(pose, mouseX, mouseY);
    }
    *///?} else if <1.20.6 {

    /*@Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
    *///?} else if <26.1.2 {

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
    //?}
}
