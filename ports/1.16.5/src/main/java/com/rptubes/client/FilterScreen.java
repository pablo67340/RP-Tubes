package com.rptubes.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.rptubes.RPTubes;
import com.rptubes.menu.FilterMenu;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.entity.player.PlayerInventory;

/** OG GuiFilter — filter9.png background, 3x3 grid, paint color selector. */
public class FilterScreen extends ContainerScreen<FilterMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(RPTubes.MOD_ID, "textures/gui/filter9.png");

    private static final int[] PAINT_COLORS = {
            0xFFFFFF, 0xFF8000, 0xFF00FF, 0x6C83FF, 0xFFFF00, 0x00FF00, 0xFF6480, 0x535353,
            0x939393, 0x00FFFF, 0x8000FF, 0x0000FF, 0x4F2700, 0x008000, 0xFF0000, 0x1F1F1F};

    public FilterScreen(FilterMenu menu, PlayerInventory inv, ITextComponent title) {
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

        int color = this.menu.getColor();
        if (color > 0) {
            fill(pose, x + 122, y + 59, x + 126, y + 63, 0xFF000000 | PAINT_COLORS[color - 1]);
        } else {
            this.blit(pose, x + 122, y + 59, 176, 0, 4, 4);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        double rx = mouseX - this.leftPos;
        double ry = mouseY - this.topPos;
        // OG GuiFilter: 12x12 color button at (118, 55); left = next, right = previous
        if (rx >= 118 && rx <= 129 && ry >= 55 && ry <= 66 && (button == 0 || button == 1)) {
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
