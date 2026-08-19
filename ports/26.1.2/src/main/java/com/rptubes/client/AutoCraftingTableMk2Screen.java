package com.rptubes.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import com.rptubes.menu.AutoCraftingTableMk2Menu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

/** Port of Tubestuff's GuiCraftingTable — 186x249 custom background, no labels. */
public class AutoCraftingTableMk2Screen extends AbstractContainerScreen<AutoCraftingTableMk2Menu> {
    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath("rptubes", "textures/gui/act_mk2.png");
    private static final int OUTPUT_X = 134;
    private static final int OUTPUT_Y = 124;

    public AutoCraftingTableMk2Screen(AutoCraftingTableMk2Menu menu, Inventory inv, Component title) {
        // 26.1: imageWidth/imageHeight are final, must go through the ctor
        super(menu, inv, title, 186, 249);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, (float) (0), (float) (0), this.imageWidth, this.imageHeight, 256, 256);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        // The original drew no labels; the background art has no room for them
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        ItemStack preview = this.renderResultPreview(graphics);
        // Hovering the empty output slot explains what the template will make
        if (!preview.isEmpty() && this.hoveredSlot != null
                && this.hoveredSlot.index == 0 && !this.hoveredSlot.hasItem()) {
            graphics.setTooltipForNextFrame(this.font, preview, mouseX, mouseY);
        }
    }

    /**
     * Improvement over the original: while the output slot is empty, show what
     * the ghost template resolves to, washed out so it reads as "not yet real".
     */
    private ItemStack renderResultPreview(GuiGraphicsExtractor graphics) {
        if (this.menu.getSlot(0).hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack preview = this.menu.getResultPreview();
        if (preview.isEmpty()) {
            return ItemStack.EMPTY;
        }
        int x = this.leftPos + OUTPUT_X;
        int y = this.topPos + OUTPUT_Y;
        // 1.21.6+ GUI renders in submission order, so later draws layer on top:
        // sprite, then the gray wash, then the count above the wash
        graphics.item(preview, x, y);
        graphics.fill(x, y, x + 16, y + 16, 0x8C8B8B8B);
        graphics.itemDecorations(this.font, preview, x, y);
        return preview;
    }
}
