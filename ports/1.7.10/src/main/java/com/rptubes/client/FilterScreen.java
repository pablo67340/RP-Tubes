package com.rptubes.client;

import com.rptubes.RPTubes;
import com.rptubes.menu.FilterMenu;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

/** OG GuiFilter — filter9.png background, 3x3 grid, paint color selector. */
public class FilterScreen extends GuiContainer {
    private final FilterMenu menu;
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(RPTubes.MOD_ID, "textures/gui/filter9.png");

    private static final int[] PAINT_COLORS = {
            0xFFFFFF, 0xFF8000, 0xFF00FF, 0x6C83FF, 0xFFFF00, 0x00FF00, 0xFF6480, 0x535353,
            0x939393, 0x00FFFF, 0x8000FF, 0x0000FF, 0x4F2700, 0x008000, 0xFF0000, 0x1F1F1F};

    public FilterScreen(FilterMenu menu) {
        super(menu);
        this.menu = menu;
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
        org.lwjgl.opengl.GL11.glColor4f(1f, 1f, 1f, 1f);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);

        int color = this.menu.getColor();
        if (color > 0) {
            drawRect(x + 122, y + 59, x + 126, y + 63, 0xFF000000 | PAINT_COLORS[color - 1]);
        } else {
            this.drawTexturedModalRect(x + 122, y + 59, 176, 0, 4, 4);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        double rx = mouseX - this.guiLeft;
        double ry = mouseY - this.guiTop;
        // OG GuiFilter: 12x12 color button at (118, 55); left = next, right = previous
        if (rx >= 118 && rx <= 129 && ry >= 55 && ry <= 66 && (button == 0 || button == 1)) {
            this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, button == 0 ? 0 : 1);
            this.mc.getSoundHandler().playSound(
                    PositionedSoundRecord.func_147674_a(new net.minecraft.util.ResourceLocation("gui.button.press"), 1.0F));
            return;
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTick);
    }
}
