package com.rptubes.client;

import com.rptubes.RPTubes;
import com.rptubes.menu.SortingMachineMenu;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraft.init.SoundEvents;

/** OG GuiSorter — sortmachine.png 176x222, column highlight, 8 color swatches, mode button. */
public class SortingMachineScreen extends GuiContainer {
    private final SortingMachineMenu menu;
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(RPTubes.MOD_ID, "textures/gui/sorting_machine.png");

    private static final int[] PAINT_COLORS = {
            0xFFFFFF, 0xFF8000, 0xFF00FF, 0x6C83FF, 0xFFFF00, 0x00FF00, 0xFF6480, 0x535353,
            0x939393, 0x00FFFF, 0x8000FF, 0x0000FF, 0x4F2700, 0x008000, 0xFF0000, 0x1F1F1F};

    public SortingMachineScreen(SortingMachineMenu menu) {
        super(menu);
        this.menu = menu;
        // OG GuiSorter: 176x222, title at x=50
        this.ySize = 222;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
        GlStateManager.color(1f, 1f, 1f, 1f);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);

        int mode = this.menu.getMode();
        // Sequential modes highlight the current working column
        if (mode < 2) {
            this.drawTexturedModalRect(x + 24 + 18 * this.menu.getColumn(), y + 16, 176, 0, 20, 92);
        }
        for (int n = 0; n < 8; n++) {
            int color = this.menu.getColor(n);
            if (color > 0) {
                drawRect(x + 32 + n * 18, y + 114, x + 36 + n * 18, y + 118,
                        0xFF000000 | PAINT_COLORS[color - 1]);
            } else {
                this.drawTexturedModalRect(x + 32 + n * 18, y + 114, 187, 92, 4, 4);
            }
        }
        this.drawTexturedModalRect(x + 7, y + 91, 196, 14 * mode, 14, 14);
        if (mode == 4) {
            // OG draws the default-color button by copying a slot frame from the background
            this.drawTexturedModalRect(x + 7, y + 109, 27, 109, 14, 14);
            int defColor = this.menu.getDefColor();
            if (defColor > 0) {
                drawRect(x + 12, y + 114, x + 16, y + 118, 0xFF000000 | PAINT_COLORS[defColor - 1]);
            } else {
                this.drawTexturedModalRect(x + 12, y + 114, 187, 92, 4, 4);
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws java.io.IOException {
        double rx = mouseX - this.guiLeft;
        double ry = mouseY - this.guiTop;
        if (button == 0 || button == 1) {
            // OG GuiSorter click regions
            if (rx >= 7 && rx <= 21 && ry >= 91 && ry <= 105) {
                this.sendButton(button == 0 ? 0 : 1);
                return;
            }
            if (ry >= 110 && ry <= 121) {
                for (int n = 0; n < 8; n++) {
                    if (rx >= 28 + n * 18 && rx <= 39 + n * 18) {
                        this.sendButton((button == 0 ? 10 : 20) + n);
                        return;
                    }
                }
                if (this.menu.getMode() == 4 && rx >= 7 && rx <= 21) {
                    this.sendButton(button == 0 ? 30 : 31);
                    return;
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    private void sendButton(int id) {
        this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, id);
        this.mc.getSoundHandler().playSound(
                PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTick);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
}
