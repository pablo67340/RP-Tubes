package com.rptubes.client;

import com.rptubes.menu.AutoCraftingTableMk2Menu;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;

/** Port of Tubestuff's GuiCraftingTable — 186x249 custom background, no labels. */
public class AutoCraftingTableMk2Screen extends GuiContainer {
    private final AutoCraftingTableMk2Menu menu;
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("rptubes", "textures/gui/act_mk2.png");
    private static final int OUTPUT_X = 134;
    private static final int OUTPUT_Y = 124;

    public AutoCraftingTableMk2Screen(AutoCraftingTableMk2Menu menu) {
        super(menu);
        this.menu = menu;
        this.xSize = 186;
        this.ySize = 249;
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
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        // The original drew no labels; the background art has no room for them
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTick);
        ItemStack preview = this.renderResultPreview();
        this.renderHoveredToolTip(mouseX, mouseY);
        // Hovering the empty output slot explains what the template will make
        if (!preview.isEmpty() && this.getSlotUnderMouse() != null
                && this.getSlotUnderMouse().slotNumber == 0 && !this.getSlotUnderMouse().getHasStack()) {
            this.renderToolTip(preview, mouseX, mouseY);
        }
    }

    /**
     * Improvement over the original: while the output slot is empty, show what
     * the ghost template resolves to, washed out so it reads as "not yet real".
     */
    private ItemStack renderResultPreview() {
        if (this.menu.getSlot(0).getHasStack()) {
            return ItemStack.EMPTY;
        }
        ItemStack preview = this.menu.getResultPreview();
        if (preview.isEmpty()) {
            return ItemStack.EMPTY;
        }
        int x = this.guiLeft + OUTPUT_X;
        int y = this.guiTop + OUTPUT_Y;
        net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
        this.itemRender.renderItemAndEffectIntoGUI(preview, x, y);
        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
        // Wash the sprite out with the slot's background gray at ~55% alpha...
        GlStateManager.pushMatrix();
        GlStateManager.translate(0f, 0f, 250f);
        drawRect(x, y, x + 16, y + 16, 0x8C8B8B8B);
        GlStateManager.popMatrix();
        // ...but draw the count above the wash so the craft yield stays readable
        this.itemRender.zLevel = 300f;
        this.itemRender.renderItemOverlays(this.fontRenderer, preview, x, y);
        this.itemRender.zLevel = 0f;
        return preview;
    }
}
