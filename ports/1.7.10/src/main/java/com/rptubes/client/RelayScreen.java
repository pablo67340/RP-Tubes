package com.rptubes.client;

import com.rptubes.menu.RelayMenu;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;

/** Like the ejector, the OG relay GUI is the vanilla dispenser-style 3x3 grid. */
public class RelayScreen extends GuiContainer {
    private final RelayMenu menu;
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("textures/gui/container/dispenser.png");

    public RelayScreen(RelayMenu menu) {
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
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTick);
    }
}
