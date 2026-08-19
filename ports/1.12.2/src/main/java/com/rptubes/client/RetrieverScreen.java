package com.rptubes.client;

import com.rptubes.RPTubes;
import com.rptubes.menu.RetrieverMenu;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraft.init.SoundEvents;

/** OG GuiRetriever — retriever.png, 3x3 grid, paint color selector, mode button, slot highlight. */
public class RetrieverScreen extends GuiContainer {
    private final RetrieverMenu menu;
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(RPTubes.MOD_ID, "textures/gui/retriever.png");

    private static final int[] PAINT_COLORS = {
            0xFFFFFF, 0xFF8000, 0xFF00FF, 0x6C83FF, 0xFFFF00, 0x00FF00, 0xFF6480, 0x535353,
            0x939393, 0x00FFFF, 0x8000FF, 0x0000FF, 0x4F2700, 0x008000, 0xFF0000, 0x1F1F1F};

    public RetrieverScreen(RetrieverMenu menu) {
        super(menu);
        this.menu = menu;
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
            drawRect(x + 122, y + 59, x + 126, y + 63, 0xFF000000 | PAINT_COLORS[color - 1]);
        } else {
            this.drawTexturedModalRect(x + 122, y + 59, 187, 0, 4, 4);
        }

        int mode = this.menu.getMode();
        this.drawTexturedModalRect(x + 45, y + 54, 211, 14 * mode, 14, 14);
        if (mode == 0) {
            int select = this.menu.getSelect();
            this.drawTexturedModalRect(x + 60 + 18 * (select % 3), y + 15 + 18 * (select / 3), 191, 0, 20, 20);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws java.io.IOException {
        double rx = mouseX - this.guiLeft;
        double ry = mouseY - this.guiTop;
        if (ry >= 55 && ry <= 66 && (button == 0 || button == 1)) {
            // OG GuiRetriever: color button at (118, 55), mode button at (45, 55)
            if (rx >= 118 && rx <= 129) {
                this.sendButton(button == 0 ? 0 : 1);
                return;
            }
            if (rx >= 45 && rx <= 58) {
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
