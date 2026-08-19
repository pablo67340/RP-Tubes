package com.rptubes.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
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
        // OG GuiBufferChest: 176x186, title at x=70
        super(menu, inv, title, 176, 186);
        this.titleLabelX = 70;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, (float) (0), (float) (0), this.imageWidth, this.imageHeight, 256, 256);
    }
}
