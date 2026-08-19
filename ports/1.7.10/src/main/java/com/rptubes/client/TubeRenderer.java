package com.rptubes.client;

import com.rptubes.block.PneumaticTubeBlock;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import static com.rptubes.block.PneumaticTubeBlock.MAX;
import static com.rptubes.block.PneumaticTubeBlock.MIN;

/**
 * ISBRH drawing tubes as a core box plus one arm box per connection,
 * standing in for the multipart blockstate models used on newer versions.
 */
public class TubeRenderer implements ISimpleBlockRenderingHandler {

    @Override
    public void renderInventoryBlock(Block block, int meta, int modelId, RenderBlocks renderer) {
        // straight north-south piece in inventory
        renderer.setRenderBounds(MIN, MIN, 0.0, MAX, MAX, 1.0);
        drawInventoryBox(block, meta, renderer);
        renderer.setRenderBoundsFromBlock(block);
    }

    private static void drawInventoryBox(Block block, int meta, RenderBlocks renderer) {
        Tessellator tes = Tessellator.instance;
        for (int side = 0; side < 6; side++) {
            tes.startDrawingQuads();
            switch (side) {
                case 0: tes.setNormal(0.0F, -1.0F, 0.0F); break;
                case 1: tes.setNormal(0.0F, 1.0F, 0.0F); break;
                case 2: tes.setNormal(0.0F, 0.0F, -1.0F); break;
                case 3: tes.setNormal(0.0F, 0.0F, 1.0F); break;
                case 4: tes.setNormal(-1.0F, 0.0F, 0.0F); break;
                default: tes.setNormal(1.0F, 0.0F, 0.0F); break;
            }
            switch (side) {
                case 0: renderer.renderFaceYNeg(block, -0.5, -0.5, -0.5, block.getIcon(0, meta)); break;
                case 1: renderer.renderFaceYPos(block, -0.5, -0.5, -0.5, block.getIcon(1, meta)); break;
                case 2: renderer.renderFaceZNeg(block, -0.5, -0.5, -0.5, block.getIcon(2, meta)); break;
                case 3: renderer.renderFaceZPos(block, -0.5, -0.5, -0.5, block.getIcon(3, meta)); break;
                case 4: renderer.renderFaceXNeg(block, -0.5, -0.5, -0.5, block.getIcon(4, meta)); break;
                default: renderer.renderFaceXPos(block, -0.5, -0.5, -0.5, block.getIcon(5, meta)); break;
            }
            tes.draw();
        }
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess access, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        if (!(block instanceof PneumaticTubeBlock)) {
            return false;
        }
        World world = renderer.blockAccess instanceof World ? (World) renderer.blockAccess
                : net.minecraft.client.Minecraft.getMinecraft().theWorld;
        int cons = PneumaticTubeBlock.getConnectionMask(world, x, y, z);

        renderer.setRenderBounds(MIN, MIN, MIN, MAX, MAX, MAX);
        renderer.renderStandardBlock(block, x, y, z);

        if ((cons & (1 << 0)) != 0) { renderer.setRenderBounds(MIN, 0.0, MIN, MAX, MIN, MAX); renderer.renderStandardBlock(block, x, y, z); }
        if ((cons & (1 << 1)) != 0) { renderer.setRenderBounds(MIN, MAX, MIN, MAX, 1.0, MAX); renderer.renderStandardBlock(block, x, y, z); }
        if ((cons & (1 << 2)) != 0) { renderer.setRenderBounds(MIN, MIN, 0.0, MAX, MAX, MIN); renderer.renderStandardBlock(block, x, y, z); }
        if ((cons & (1 << 3)) != 0) { renderer.setRenderBounds(MIN, MIN, MAX, MAX, MAX, 1.0); renderer.renderStandardBlock(block, x, y, z); }
        if ((cons & (1 << 4)) != 0) { renderer.setRenderBounds(0.0, MIN, MIN, MIN, MAX, MAX); renderer.renderStandardBlock(block, x, y, z); }
        if ((cons & (1 << 5)) != 0) { renderer.setRenderBounds(MAX, MIN, MIN, 1.0, MAX, MAX); renderer.renderStandardBlock(block, x, y, z); }

        renderer.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return PneumaticTubeBlock.renderId;
    }
}
