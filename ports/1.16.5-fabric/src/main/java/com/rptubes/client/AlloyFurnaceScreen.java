package com.rptubes.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.rptubes.RPTubes;
import com.rptubes.menu.AlloyFurnaceMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AlloyFurnaceScreen extends AbstractContainerScreen<AlloyFurnaceMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(RPTubes.MOD_ID, "textures/gui/alloy_furnace.png");

    public AlloyFurnaceScreen(AlloyFurnaceMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        // OG GuiAlloyFurnace: title at x=60
        this.titleLabelX = 60;
    }

    @Override
    protected void renderBg(PoseStack pose, float partialTick, int mouseX, int mouseY) {
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        this.minecraft.getTextureManager().bind(TEXTURE);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        this.blit(pose, x, y, 0, 0, this.imageWidth, this.imageHeight);

        // OG GuiAlloyFurnace flame + arrow positions
        if (this.menu.isLit()) {
            int burn = this.menu.getBurnProgress();
            this.blit(pose, x + 17, y + 25 + 12 - burn, 176, 12 - burn, 14, burn + 2);
        }
        int cook = this.menu.getCookProgress();
        this.blit(pose, x + 107, y + 34, 176, 14, cook + 1, 16);
    }

    @Override
    public void render(PoseStack pose, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(pose);
        super.render(pose, mouseX, mouseY, partialTick);
        this.renderTooltip(pose, mouseX, mouseY);
    }
}
