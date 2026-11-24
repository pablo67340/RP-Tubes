package com.rptubes.client;

import com.rptubes.RPTubes;
import com.rptubes.blockentity.BatteryBoxBlockEntity;
import com.rptubes.menu.BatteryBoxMenu;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

/**
 * OG GuiBatteryBox. The left bar showed network voltage; with RF it shows the larger of
 * inflow and outflow on a square-root scale so a single solar panel still registers.
 */
public class BatteryBoxScreen extends GuiContainer {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(RPTubes.MOD_ID, "textures/gui/battery_box.png");
    private final BatteryBoxMenu menu;

    public BatteryBoxScreen(BatteryBoxMenu menu) {
        super(menu);
        this.menu = menu;
        this.xSize = 176;
        this.ySize = 170;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRendererObj.drawString(StatCollector.translateToLocal("block.rptubes.battery_box"), 60, 6, 0x404040);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 94, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
        org.lwjgl.opengl.GL11.glColor4f(1f, 1f, 1f, 1f);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);

        int in = this.menu.getInRate();
        int out = this.menu.getOutRate();
        int storage = this.menu.getStorage();
        int max = BatteryBoxMenu.MAX_STORAGE;
        double flow = Math.max(in, out) / (double) BatteryBoxBlockEntity.MAX_IO;
        int s = (int) Math.min(48, Math.round(48 * Math.sqrt(flow)));
        if (s > 0) {
            this.drawTexturedModalRect(x + 71, y + 73 - s, 176, 48 - s, 5, s);
        }
        if (in > 0 || out > 0) {
            this.drawTexturedModalRect(x + 72, y + 17, 197, 16, 3, 6);
        }
        if (in > 0 && storage < max) {
            this.drawTexturedModalRect(x + 82, y + 37, 197, 0, 10, 8);
        }
        if (out > 0 && storage > 0) {
            this.drawTexturedModalRect(x + 82, y + 55, 197, 8, 10, 8);
        }
        s = Math.min(48, storage * 48 / max);
        if (s > 0) {
            this.drawTexturedModalRect(x + 98, y + 73 - s, 181, 48 - s, 16, s);
        }
        if (storage >= max) {
            this.drawTexturedModalRect(x + 103, y + 17, 200, 16, 6, 6);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTick);
    }
}
