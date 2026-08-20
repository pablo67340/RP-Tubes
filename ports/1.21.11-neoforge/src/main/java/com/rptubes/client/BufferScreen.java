package com.rptubes.client;

import net.minecraft.client.gui.GuiGraphics;
import com.rptubes.RPTubes;
import com.rptubes.menu.BufferMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class BufferScreen extends AbstractContainerScreen<BufferMenu> {
    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(RPTubes.MOD_ID, "textures/gui/buffer.png");

    public BufferScreen(BufferMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        // OG GuiBufferChest: 176x186, title at x=70
        this.imageHeight = 186;
        this.titleLabelX = 70;
        this.inventoryLabelY = this.imageHeight - 94;
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
