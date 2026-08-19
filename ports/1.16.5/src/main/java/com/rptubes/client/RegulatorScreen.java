package com.rptubes.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.rptubes.RPTubes;
import com.rptubes.menu.RegulatorMenu;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.entity.player.PlayerInventory;

/** OG GuiRegulator — 211x167 regulator.png, three 3x3 grids, color selector, mode button. */
public class RegulatorScreen extends ContainerScreen<RegulatorMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(RPTubes.MOD_ID, "textures/gui/regulator.png");

    private static final int[] PAINT_COLORS = {
            0xFFFFFF, 0xFF8000, 0xFF00FF, 0x6C83FF, 0xFFFF00, 0x00FF00, 0xFF6480, 0x535353,
            0x939393, 0x00FFFF, 0x8000FF, 0x0000FF, 0x4F2700, 0x008000, 0xFF0000, 0x1F1F1F};

    public RegulatorScreen(RegulatorMenu menu, PlayerInventory inv, ITextComponent title) {
        super(menu, inv, title);
        this.imageWidth = 211;
        this.imageHeight = 167;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 79;
        this.inventoryLabelX = 25;
        this.inventoryLabelY = this.imageHeight - 93;
    }

    @Override
    protected void renderBg(MatrixStack pose, float partialTick, int mouseX, int mouseY) {
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        this.minecraft.getTextureManager().bind(TEXTURE);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        this.blit(pose, x, y, 0, 0, this.imageWidth, this.imageHeight);

        int color = this.menu.getColor();
        if (color > 0) {
            fill(pose, x + 140, y + 60, x + 144, y + 64, 0xFF000000 | PAINT_COLORS[color - 1]);
        } else {
            this.blit(pose, x + 140, y + 60, 212, 0, 4, 4);
        }
        this.blit(pose, x + 135, y + 19, 216, 14 * this.menu.getMode(), 14, 14);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        double rx = mouseX - this.leftPos;
        double ry = mouseY - this.topPos;
        if (rx >= 136 && rx <= 147 && (button == 0 || button == 1)) {
            // OG GuiRegulator: color button at (136, 56), mode button at (136, 19)
            if (ry >= 56 && ry <= 67) {
                this.sendButton(button == 0 ? 0 : 1);
                return true;
            }
            if (ry >= 19 && ry <= 32) {
                this.sendButton(button == 0 ? 2 : 3);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void sendButton(int id) {
        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, id);
        this.minecraft.getSoundManager().play(
                SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    public void render(MatrixStack pose, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(pose);
        super.render(pose, mouseX, mouseY, partialTick);
        this.renderTooltip(pose, mouseX, mouseY);
    }
}
