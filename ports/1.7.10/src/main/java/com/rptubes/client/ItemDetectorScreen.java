package com.rptubes.client;

import com.rptubes.RPTubes;
import com.rptubes.menu.ItemDetectorMenu;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

/** OG GuiItemDetect — itemdet.png, 3x3 grid, 3-state mode button. */
public class ItemDetectorScreen extends GuiContainer {
    private final ItemDetectorMenu menu;
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(RPTubes.MOD_ID, "textures/gui/itemdet.png");

    public ItemDetectorScreen(ItemDetectorMenu menu) {
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
        this.drawTexturedModalRect(x + 117, y + 54, 176, 14 * this.menu.getMode(), 14, 14);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        double rx = mouseX - this.guiLeft;
        double ry = mouseY - this.guiTop;
        // OG GuiItemDetect: mode button at (117, 54), 14x14; left = next, right = previous
        if (rx >= 117 && rx <= 131 && ry >= 54 && ry <= 68 && (button == 0 || button == 1)) {
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
