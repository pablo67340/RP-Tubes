package com.rptubes.client;

import com.rptubes.RPTubes;
import com.rptubes.menu.BufferMenu;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class BufferScreen extends GuiContainer {
    private final BufferMenu menu;
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(RPTubes.MOD_ID, "textures/gui/buffer.png");

    public BufferScreen(BufferMenu menu) {
        super(menu);
        this.menu = menu;
        // OG GuiBufferChest: 176x186, title at x=70
        this.ySize = 186;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
        GlStateManager.color(1f, 1f, 1f, 1f);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTick);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
}
