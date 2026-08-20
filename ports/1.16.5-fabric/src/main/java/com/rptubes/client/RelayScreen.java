package com.rptubes.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.rptubes.menu.RelayMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/** Like the ejector, the OG relay GUI is the vanilla dispenser-style 3x3 grid. */
public class RelayScreen extends AbstractContainerScreen<RelayMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("textures/gui/container/dispenser.png");

    public RelayScreen(RelayMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
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
    public void render(PoseStack pose, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(pose);
        super.render(pose, mouseX, mouseY, partialTick);
        this.renderTooltip(pose, mouseX, mouseY);
    }
}
