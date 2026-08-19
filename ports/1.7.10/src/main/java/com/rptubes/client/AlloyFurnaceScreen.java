package com.rptubes.client;

import com.rptubes.RPTubes;
import com.rptubes.menu.AlloyFurnaceMenu;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;

public class AlloyFurnaceScreen extends GuiContainer {
    private final AlloyFurnaceMenu menu;
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(RPTubes.MOD_ID, "textures/gui/alloy_furnace.png");

    public AlloyFurnaceScreen(AlloyFurnaceMenu menu) {
        super(menu);
        this.menu = menu;
        // OG GuiAlloyFurnace: title at x=60
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
        org.lwjgl.opengl.GL11.glColor4f(1f, 1f, 1f, 1f);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);

        // OG GuiAlloyFurnace flame + arrow positions
        if (this.menu.isLit()) {
            int burn = this.menu.getBurnProgress();
            this.drawTexturedModalRect(x + 17, y + 25 + 12 - burn, 176, 12 - burn, 14, burn + 2);
        }
        int cook = this.menu.getCookProgress();
        this.drawTexturedModalRect(x + 107, y + 34, 176, 14, cook + 1, 16);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTick);
    }
}
