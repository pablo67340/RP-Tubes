package com.rptubes.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import com.rptubes.RPTubes;
import com.rptubes.menu.AlloyFurnaceMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AlloyFurnaceScreen extends AbstractContainerScreen<AlloyFurnaceMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(RPTubes.MOD_ID, "textures/gui/alloy_furnace.png");

    public AlloyFurnaceScreen(AlloyFurnaceMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        // OG GuiAlloyFurnace: title at x=60
        this.titleLabelX = 60;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        // OG GuiAlloyFurnace flame + arrow positions
        if (this.menu.isLit()) {
            int burn = this.menu.getBurnProgress();
            graphics.blit(TEXTURE, x + 17, y + 25 + 12 - burn, 176, 12 - burn, 14, burn + 2);
        }
        int cook = this.menu.getCookProgress();
        graphics.blit(TEXTURE, x + 107, y + 34, 176, 14, cook + 1, 16);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}
