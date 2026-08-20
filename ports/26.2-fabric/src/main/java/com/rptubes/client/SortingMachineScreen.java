package com.rptubes.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import com.rptubes.RPTubes;
import com.rptubes.menu.SortingMachineMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

/** OG GuiSorter — sortmachine.png 176x222, column highlight, 8 color swatches, mode button. */
public class SortingMachineScreen extends AbstractContainerScreen<SortingMachineMenu> {
    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(RPTubes.MOD_ID, "textures/gui/sorting_machine.png");

    private static final int[] PAINT_COLORS = {
            0xFFFFFF, 0xFF8000, 0xFF00FF, 0x6C83FF, 0xFFFF00, 0x00FF00, 0xFF6480, 0x535353,
            0x939393, 0x00FFFF, 0x8000FF, 0x0000FF, 0x4F2700, 0x008000, 0xFF0000, 0x1F1F1F};

    public SortingMachineScreen(SortingMachineMenu menu, Inventory inv, Component title) {
        // OG GuiSorter: 176x222, title at x=50
        super(menu, inv, title, 176, 222);
        this.titleLabelX = 50;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, (float) (0), (float) (0), this.imageWidth, this.imageHeight, 256, 256);

        int mode = this.menu.getMode();
        // Sequential modes highlight the current working column
        if (mode < 2) {
            graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE, x + 24 + 18 * this.menu.getColumn(), y + 16, (float) (176), (float) (0), 20, 92, 256, 256);
        }
        for (int n = 0; n < 8; n++) {
            int color = this.menu.getColor(n);
            if (color > 0) {
                graphics.fill(x + 32 + n * 18, y + 114, x + 36 + n * 18, y + 118,
                        0xFF000000 | PAINT_COLORS[color - 1]);
            } else {
                graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE, x + 32 + n * 18, y + 114, (float) (187), (float) (92), 4, 4, 256, 256);
            }
        }
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE, x + 7, y + 91, (float) (196), (float) (14 * mode), 14, 14, 256, 256);
        if (mode == 4) {
            // OG draws the default-color button by copying a slot frame from the background
            graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE, x + 7, y + 109, (float) (27), (float) (109), 14, 14, 256, 256);
            int defColor = this.menu.getDefColor();
            if (defColor > 0) {
                graphics.fill(x + 12, y + 114, x + 16, y + 118, 0xFF000000 | PAINT_COLORS[defColor - 1]);
            } else {
                graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE, x + 12, y + 114, (float) (187), (float) (92), 4, 4, 256, 256);
            }
        }
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
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
        return super.mouseClicked(event, doubleClick);
    }

    private void sendButton(int id) {
        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, id);
        this.minecraft.getSoundManager().play(
                SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
}
