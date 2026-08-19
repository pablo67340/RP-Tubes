package com.rptubes.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import com.rptubes.menu.AutoCraftingTableMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/** OG GuiAutoCrafting drew the vanilla crafting GUI with the title "Crafting" at 28,6. */
public class AutoCraftingTableScreen extends AbstractContainerScreen<AutoCraftingTableMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("textures/gui/container/crafting_table.png");

    public AutoCraftingTableScreen(AutoCraftingTableMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.titleLabelX = 28;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}
