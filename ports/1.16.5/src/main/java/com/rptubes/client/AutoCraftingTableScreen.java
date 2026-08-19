package com.rptubes.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.rptubes.menu.AutoCraftingTableMenu;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.PlayerInventory;

/** OG GuiAutoCrafting drew the vanilla crafting GUI with the title "Crafting" at 28,6. */
public class AutoCraftingTableScreen extends ContainerScreen<AutoCraftingTableMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("textures/gui/container/crafting_table.png");

    public AutoCraftingTableScreen(AutoCraftingTableMenu menu, PlayerInventory inv, ITextComponent title) {
        super(menu, inv, title);
        this.titleLabelX = 28;
    }

    @Override
    protected void renderBg(MatrixStack pose, float partialTick, int mouseX, int mouseY) {
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        this.minecraft.getTextureManager().bind(TEXTURE);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        this.blit(pose, x, y, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(MatrixStack pose, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(pose);
        super.render(pose, mouseX, mouseY, partialTick);
        this.renderTooltip(pose, mouseX, mouseY);
    }
}
