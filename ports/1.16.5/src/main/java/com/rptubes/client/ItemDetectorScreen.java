package com.rptubes.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.rptubes.RPTubes;
import com.rptubes.menu.ItemDetectorMenu;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.entity.player.PlayerInventory;

/** OG GuiItemDetect — itemdet.png, 3x3 grid, 3-state mode button. */
public class ItemDetectorScreen extends ContainerScreen<ItemDetectorMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(RPTubes.MOD_ID, "textures/gui/itemdet.png");

    public ItemDetectorScreen(ItemDetectorMenu menu, PlayerInventory inv, ITextComponent title) {
        super(menu, inv, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 60;
    }

    @Override
    protected void renderBg(MatrixStack pose, float partialTick, int mouseX, int mouseY) {
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        this.minecraft.getTextureManager().bind(TEXTURE);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        this.blit(pose, x, y, 0, 0, this.imageWidth, this.imageHeight);
        this.blit(pose, x + 117, y + 54, 176, 14 * this.menu.getMode(), 14, 14);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        double rx = mouseX - this.leftPos;
        double ry = mouseY - this.topPos;
        // OG GuiItemDetect: mode button at (117, 54), 14x14; left = next, right = previous
        if (rx >= 117 && rx <= 131 && ry >= 54 && ry <= 68 && (button == 0 || button == 1)) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, button == 0 ? 0 : 1);
            this.minecraft.getSoundManager().play(
                    SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(MatrixStack pose, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(pose);
        super.render(pose, mouseX, mouseY, partialTick);
        this.renderTooltip(pose, mouseX, mouseY);
    }
}
