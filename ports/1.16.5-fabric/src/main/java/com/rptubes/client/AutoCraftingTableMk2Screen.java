package com.rptubes.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.rptubes.menu.AutoCraftingTableMk2Menu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

/** Port of Tubestuff's GuiCraftingTable — 186x249 custom background, no labels. */
public class AutoCraftingTableMk2Screen extends AbstractContainerScreen<AutoCraftingTableMk2Menu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("rptubes", "textures/gui/act_mk2.png");
    private static final int OUTPUT_X = 134;
    private static final int OUTPUT_Y = 124;

    public AutoCraftingTableMk2Screen(AutoCraftingTableMk2Menu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 186;
        this.imageHeight = 249;
    }

    @Override
    protected void renderBg(PoseStack pose, float partialTick, int mouseX, int mouseY) {
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        this.minecraft.getTextureManager().bind(TEXTURE);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        this.blit(pose, x, y, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
        // The original drew no labels; the background art has no room for them
    }

    @Override
    public void render(PoseStack pose, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(pose);
        super.render(pose, mouseX, mouseY, partialTick);
        ItemStack preview = this.renderResultPreview(pose);
        this.renderTooltip(pose, mouseX, mouseY);
        // Hovering the empty output slot explains what the template will make
        if (!preview.isEmpty() && this.hoveredSlot != null
                && this.hoveredSlot.index == 0 && !this.hoveredSlot.hasItem()) {
            this.renderTooltip(pose, preview, mouseX, mouseY);
        }
    }

    /**
     * Improvement over the original: while the output slot is empty, show what
     * the ghost template resolves to, washed out so it reads as "not yet real".
     */
    private ItemStack renderResultPreview(PoseStack pose) {
        if (this.menu.getSlot(0).hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack preview = this.menu.getResultPreview();
        if (preview.isEmpty()) {
            return ItemStack.EMPTY;
        }
        int x = this.leftPos + OUTPUT_X;
        int y = this.topPos + OUTPUT_Y;
        this.itemRenderer.renderAndDecorateItem(preview, x, y);
        // Wash the sprite out with the slot's background gray at ~55% alpha...
        pose.pushPose();
        pose.translate(0, 0, 250);
        fill(pose, x, y, x + 16, y + 16, 0x8C8B8B8B);
        pose.popPose();
        // ...but draw the count above the wash so the craft yield stays readable
        this.itemRenderer.blitOffset = 100f;
        this.itemRenderer.renderGuiItemDecorations(this.font, preview, x, y);
        this.itemRenderer.blitOffset = 0f;
        return preview;
    }
}
