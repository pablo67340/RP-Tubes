package com.rptubes.client;

import com.rptubes.RPTubes;
import com.rptubes.menu.RegulatorMenu;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraft.init.SoundEvents;

/** OG GuiRegulator — 211x167 regulator.png, three 3x3 grids, color selector, mode button. */
public class RegulatorScreen extends GuiContainer {
    private final RegulatorMenu menu;
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(RPTubes.MOD_ID, "textures/gui/regulator.png");

    private static final int[] PAINT_COLORS = {
            0xFFFFFF, 0xFF8000, 0xFF00FF, 0x6C83FF, 0xFFFF00, 0x00FF00, 0xFF6480, 0x535353,
            0x939393, 0x00FFFF, 0x8000FF, 0x0000FF, 0x4F2700, 0x008000, 0xFF0000, 0x1F1F1F};

    public RegulatorScreen(RegulatorMenu menu) {
        super(menu);
        this.menu = menu;
        this.xSize = 211;
        this.ySize = 167;
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
        GlStateManager.color(1f, 1f, 1f, 1f);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);

        int color = this.menu.getColor();
        if (color > 0) {
            drawRect(x + 140, y + 60, x + 144, y + 64, 0xFF000000 | PAINT_COLORS[color - 1]);
        } else {
            this.drawTexturedModalRect(x + 140, y + 60, 212, 0, 4, 4);
        }
        this.drawTexturedModalRect(x + 135, y + 19, 216, 14 * this.menu.getMode(), 14, 14);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws java.io.IOException {
        double rx = mouseX - this.guiLeft;
        double ry = mouseY - this.guiTop;
        if (rx >= 136 && rx <= 147 && (button == 0 || button == 1)) {
            // OG GuiRegulator: color button at (136, 56), mode button at (136, 19)
            if (ry >= 56 && ry <= 67) {
                this.sendButton(button == 0 ? 0 : 1);
                return;
            }
            if (ry >= 19 && ry <= 32) {
                this.sendButton(button == 0 ? 2 : 3);
                return;
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
