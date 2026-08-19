package com.rptubes.client;

import com.rptubes.menu.AutoCraftingTableMenu;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

/** OG GuiAutoCrafting drew the vanilla crafting GUI with the title "Crafting" at 28,6. */
public class AutoCraftingTableScreen extends GuiContainer {
    private final AutoCraftingTableMenu menu;
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("textures/gui/container/crafting_table.png");

    public AutoCraftingTableScreen(AutoCraftingTableMenu menu) {
        super(menu);
        this.menu = menu;
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
