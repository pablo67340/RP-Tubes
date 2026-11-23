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
import com.rptubes.menu.SortingMachineMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
//? if <1.21.11 {
import net.minecraft.client.renderer.GameRenderer;
//?}
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

/** OG GuiSorter — sortmachine.png 176x222, column highlight, 8 color swatches, mode button. */
public class SortingMachineScreen extends AbstractContainerScreen<SortingMachineMenu> {
    private static final ResourceLocation TEXTURE =
            //? if <1.21.1 {
            /*new ResourceLocation(RPTubes.MOD_ID, "textures/gui/sorting_machine.png");
            *///?} else {
            ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, "textures/gui/sorting_machine.png");
            //?}

    private static final int[] PAINT_COLORS = {
            0xFFFFFF, 0xFF8000, 0xFF00FF, 0x6C83FF, 0xFFFF00, 0x00FF00, 0xFF6480, 0x535353,
            0x939393, 0x00FFFF, 0x8000FF, 0x0000FF, 0x4F2700, 0x008000, 0xFF0000, 0x1F1F1F};

    public SortingMachineScreen(SortingMachineMenu menu, Inventory inv, Component title) {
        //? if <26.1.2 {
        super(menu, inv, title);
        // OG GuiSorter: 176x222, title at x=50
        this.imageHeight = 222;
        //?} else {
        /*// OG GuiSorter: 176x222, title at x=50
        super(menu, inv, title, 176, 222);
        *///?}
        this.titleLabelX = 50;
        this.inventoryLabelY = this.imageHeight - 94;
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

        int mode = this.menu.getMode();
        // Sequential modes highlight the current working column
        if (mode < 2) {
            //? if <1.20.1 {
            /*this.blit(pose, x + 24 + 18 * this.menu.getColumn(), y + 16, 176, 0, 20, 92);
            *///?} else if <1.21.11 {
            graphics.blit(TEXTURE, x + 24 + 18 * this.menu.getColumn(), y + 16, 176, 0, 20, 92);
            //?} else {
            /*graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE, x + 24 + 18 * this.menu.getColumn(), y + 16, (float) (176), (float) (0), 20, 92, 256, 256);
            *///?}
        }
        for (int n = 0; n < 8; n++) {
            int color = this.menu.getColor(n);
            if (color > 0) {
                //? if <1.20.1 {
                /*fill(pose, x + 32 + n * 18, y + 114, x + 36 + n * 18, y + 118,
                *///?} else {
                graphics.fill(x + 32 + n * 18, y + 114, x + 36 + n * 18, y + 118,
                //?}
                        0xFF000000 | PAINT_COLORS[color - 1]);
            } else {
                //? if <1.20.1 {
                /*this.blit(pose, x + 32 + n * 18, y + 114, 187, 92, 4, 4);
                *///?} else if <1.21.11 {
                graphics.blit(TEXTURE, x + 32 + n * 18, y + 114, 187, 92, 4, 4);
                //?} else {
                /*graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE, x + 32 + n * 18, y + 114, (float) (187), (float) (92), 4, 4, 256, 256);
                *///?}
            }
        }
        //? if <1.20.1 {
        /*this.blit(pose, x + 7, y + 91, 196, 14 * mode, 14, 14);
        *///?} else if <1.21.11 {
        graphics.blit(TEXTURE, x + 7, y + 91, 196, 14 * mode, 14, 14);
        //?} else {
        /*graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE, x + 7, y + 91, (float) (196), (float) (14 * mode), 14, 14, 256, 256);
        *///?}
        if (mode == 4) {
            // OG draws the default-color button by copying a slot frame from the background
            //? if <1.20.1 {
            /*this.blit(pose, x + 7, y + 109, 27, 109, 14, 14);
            *///?} else if <1.21.11 {
            graphics.blit(TEXTURE, x + 7, y + 109, 27, 109, 14, 14);
            //?} else {
            /*graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE, x + 7, y + 109, (float) (27), (float) (109), 14, 14, 256, 256);
            *///?}
            int defColor = this.menu.getDefColor();
            if (defColor > 0) {
                //? if <1.20.1 {
                /*fill(pose, x + 12, y + 114, x + 16, y + 118, 0xFF000000 | PAINT_COLORS[defColor - 1]);
            } else {
                this.blit(pose, x + 12, y + 114, 187, 92, 4, 4);
                *///?} else if <1.21.11 {
                graphics.fill(x + 12, y + 114, x + 16, y + 118, 0xFF000000 | PAINT_COLORS[defColor - 1]);
            } else {
                graphics.blit(TEXTURE, x + 12, y + 114, 187, 92, 4, 4);
                //?} else {
                /*graphics.fill(x + 12, y + 114, x + 16, y + 118, 0xFF000000 | PAINT_COLORS[defColor - 1]);
            } else {
                graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE, x + 12, y + 114, (float) (187), (float) (92), 4, 4, 256, 256);
                *///?}
            }
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
        if (button == 0 || button == 1) {
            // OG GuiSorter click regions
            if (rx >= 7 && rx <= 21 && ry >= 91 && ry <= 105) {
                this.sendButton(button == 0 ? 0 : 1);
                return true;
            }
            if (ry >= 110 && ry <= 121) {
                for (int n = 0; n < 8; n++) {
                    if (rx >= 28 + n * 18 && rx <= 39 + n * 18) {
                        this.sendButton((button == 0 ? 10 : 20) + n);
                        return true;
                    }
                }
                if (this.menu.getMode() == 4 && rx >= 7 && rx <= 21) {
                    this.sendButton(button == 0 ? 30 : 31);
                    return true;
                }
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
