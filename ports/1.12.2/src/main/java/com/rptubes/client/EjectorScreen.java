package com.rptubes.client;

import com.rptubes.menu.EjectorMenu;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

/** OG GuiEject reused the vanilla dispenser layout — plain 3x3 grid, no extras. */
public class EjectorScreen extends GuiContainer {
    private final EjectorMenu menu;
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("textures/gui/container/dispenser.png");

    public EjectorScreen(EjectorMenu menu) {
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
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTick);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
}
