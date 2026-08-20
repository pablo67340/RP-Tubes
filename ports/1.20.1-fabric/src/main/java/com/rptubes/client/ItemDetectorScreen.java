package com.rptubes.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import com.rptubes.RPTubes;
import com.rptubes.menu.ItemDetectorMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

/** OG GuiItemDetect â€” itemdet.png, 3x3 grid, 3-state mode button. */
public class ItemDetectorScreen extends AbstractContainerScreen<ItemDetectorMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(RPTubes.MOD_ID, "textures/gui/itemdet.png");

    public ItemDetectorScreen(ItemDetectorMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 60;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
        graphics.blit(TEXTURE, x + 117, y + 54, 176, 14 * this.menu.getMode(), 14, 14);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        double rx = mouseX - this.leftPos;
        double ry = mouseY - this.topPos;
        // OG GuiItemDetect: mode button at (117, 54), 14x14; left = next, right = previous
        if (rx >= 117 && rx <= 131 && ry >= 54 && ry <= 68 && (button == 0 || button == 1)) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, button == 0 ? 0 : 1);
            this.minecraft.getSoundManager().play(
                    SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}
