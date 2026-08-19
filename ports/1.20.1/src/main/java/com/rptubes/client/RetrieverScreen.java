package com.rptubes.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import com.rptubes.RPTubes;
import com.rptubes.menu.RetrieverMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

/** OG GuiRetriever â€” retriever.png, 3x3 grid, paint color selector, mode button, slot highlight. */
public class RetrieverScreen extends AbstractContainerScreen<RetrieverMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(RPTubes.MOD_ID, "textures/gui/retriever.png");

    private static final int[] PAINT_COLORS = {
            0xFFFFFF, 0xFF8000, 0xFF00FF, 0x6C83FF, 0xFFFF00, 0x00FF00, 0xFF6480, 0x535353,
            0x939393, 0x00FFFF, 0x8000FF, 0x0000FF, 0x4F2700, 0x008000, 0xFF0000, 0x1F1F1F};

    public RetrieverScreen(RetrieverMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 65;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        int color = this.menu.getColor();
        if (color > 0) {
            graphics.fill(x + 122, y + 59, x + 126, y + 63, 0xFF000000 | PAINT_COLORS[color - 1]);
        } else {
            graphics.blit(TEXTURE, x + 122, y + 59, 187, 0, 4, 4);
        }

        int mode = this.menu.getMode();
        graphics.blit(TEXTURE, x + 45, y + 54, 211, 14 * mode, 14, 14);
        if (mode == 0) {
            int select = this.menu.getSelect();
            graphics.blit(TEXTURE, x + 60 + 18 * (select % 3), y + 15 + 18 * (select / 3), 191, 0, 20, 20);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        double rx = mouseX - this.leftPos;
        double ry = mouseY - this.topPos;
        if (ry >= 55 && ry <= 66 && (button == 0 || button == 1)) {
            // OG GuiRetriever: color button at (118, 55), mode button at (45, 55)
            if (rx >= 118 && rx <= 129) {
                this.sendButton(button == 0 ? 0 : 1);
                return true;
            }
            if (rx >= 45 && rx <= 58) {
                this.sendButton(button == 0 ? 2 : 3);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void sendButton(int id) {
        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, id);
        this.minecraft.getSoundManager().play(
                SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}
