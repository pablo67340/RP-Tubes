package com.rptubes.client;

import net.minecraft.client.gui.GuiGraphics;
import com.rptubes.menu.AutoCraftingTableMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

/** OG GuiAutoCrafting drew the vanilla crafting GUI with the title "Crafting" at 28,6. */
public class AutoCraftingTableScreen extends AbstractContainerScreen<AutoCraftingTableMenu> {
    private static final Identifier TEXTURE =
            Identifier.withDefaultNamespace("textures/gui/container/crafting_table.png");

    public AutoCraftingTableScreen(AutoCraftingTableMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.titleLabelX = 28;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, (float) (0), (float) (0), this.imageWidth, this.imageHeight, 256, 256);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}
