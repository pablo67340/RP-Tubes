package com.rptubes.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import com.rptubes.RPTubes;
import com.rptubes.menu.FilterMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

/** OG GuiFilter — filter9.png background, 3x3 grid, paint color selector. */
public class FilterScreen extends AbstractContainerScreen<FilterMenu> {
    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(RPTubes.MOD_ID, "textures/gui/filter9.png");

    private static final int[] PAINT_COLORS = {
            0xFFFFFF, 0xFF8000, 0xFF00FF, 0x6C83FF, 0xFFFF00, 0x00FF00, 0xFF6480, 0x535353,
            0x939393, 0x00FFFF, 0x8000FF, 0x0000FF, 0x4F2700, 0x008000, 0xFF0000, 0x1F1F1F};

    public FilterScreen(FilterMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 60;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, (float) (0), (float) (0), this.imageWidth, this.imageHeight, 256, 256);

        int color = this.menu.getColor();
        if (color > 0) {
            graphics.fill(x + 122, y + 59, x + 126, y + 63, 0xFF000000 | PAINT_COLORS[color - 1]);
        } else {
            graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE, x + 122, y + 59, (float) (176), (float) (0), 4, 4, 256, 256);
        }
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
        double rx = mouseX - this.leftPos;
        double ry = mouseY - this.topPos;
        // OG GuiFilter: 12x12 color button at (118, 55); left = next, right = previous
        if (rx >= 118 && rx <= 129 && ry >= 55 && ry <= 66 && (button == 0 || button == 1)) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, button == 0 ? 0 : 1);
            this.minecraft.getSoundManager().play(
                    SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }
}
