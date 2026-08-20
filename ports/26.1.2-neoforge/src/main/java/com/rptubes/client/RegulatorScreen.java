package com.rptubes.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import com.rptubes.RPTubes;
import com.rptubes.menu.RegulatorMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

/** OG GuiRegulator — 211x167 regulator.png, three 3x3 grids, color selector, mode button. */
public class RegulatorScreen extends AbstractContainerScreen<RegulatorMenu> {
    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(RPTubes.MOD_ID, "textures/gui/regulator.png");

    private static final int[] PAINT_COLORS = {
            0xFFFFFF, 0xFF8000, 0xFF00FF, 0x6C83FF, 0xFFFF00, 0x00FF00, 0xFF6480, 0x535353,
            0x939393, 0x00FFFF, 0x8000FF, 0x0000FF, 0x4F2700, 0x008000, 0xFF0000, 0x1F1F1F};

    public RegulatorScreen(RegulatorMenu menu, Inventory inv, Component title) {
        super(menu, inv, title, 211, 167);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 79;
        this.inventoryLabelX = 25;
        this.inventoryLabelY = this.imageHeight - 93;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, (float) (0), (float) (0), this.imageWidth, this.imageHeight, 256, 256);

        int color = this.menu.getColor();
        if (color > 0) {
            graphics.fill(x + 140, y + 60, x + 144, y + 64, 0xFF000000 | PAINT_COLORS[color - 1]);
        } else {
            graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE, x + 140, y + 60, (float) (212), (float) (0), 4, 4, 256, 256);
        }
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE, x + 135, y + 19, (float) (216), (float) (14 * this.menu.getMode()), 14, 14, 256, 256);
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
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
        return super.mouseClicked(event, doubleClick);
    }

    private void sendButton(int id) {
        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, id);
        this.minecraft.getSoundManager().play(
                SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
}
