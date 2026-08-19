package com.rptubes.client;

import com.rptubes.menu.AutoCraftingTableMk2Menu;
import net.minecraft.client.gui.inventory.GuiContainer;
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
        org.lwjgl.opengl.GL11.glColor4f(1f, 1f, 1f, 1f);
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
        super.drawScreen(mouseX, mouseY, partialTick);
        ItemStack preview = this.renderResultPreview();
        // Hovering the empty output slot explains what the template will make
        int sx = this.guiLeft + OUTPUT_X;
        int sy = this.guiTop + OUTPUT_Y;
        boolean overOutput = mouseX >= sx && mouseX < sx + 16 && mouseY >= sy && mouseY < sy + 16;
        if (!com.rptubes.util.Stacks.isEmpty(preview) && overOutput
                && !this.menu.getSlot(0).getHasStack()) {
            this.renderToolTip(preview, mouseX, mouseY);
        }
    }

    /**
     * Improvement over the original: while the output slot is empty, show what
     * the ghost template resolves to, washed out so it reads as "not yet real".
     */
    private ItemStack renderResultPreview() {
        if (this.menu.getSlot(0).getHasStack()) {
            return null;
        }
        ItemStack preview = this.menu.getResultPreview();
        if (com.rptubes.util.Stacks.isEmpty(preview)) {
            return null;
        }
        int x = this.guiLeft + OUTPUT_X;
        int y = this.guiTop + OUTPUT_Y;
        net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
        itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), preview, x, y);
        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
        // Wash the sprite out with the slot's background gray at ~55% alpha...
        org.lwjgl.opengl.GL11.glPushMatrix();
        org.lwjgl.opengl.GL11.glTranslatef(0f, 0f, 250f);
        drawRect(x, y, x + 16, y + 16, 0x8C8B8B8B);
        org.lwjgl.opengl.GL11.glPopMatrix();
        // ...but draw the count above the wash so the craft yield stays readable
        itemRender.zLevel = 300f;
        itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), preview, x, y);
        itemRender.zLevel = 0f;
        return preview;
    }
}
