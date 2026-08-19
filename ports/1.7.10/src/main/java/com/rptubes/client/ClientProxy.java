package com.rptubes.client;

import com.rptubes.CommonProxy;
import com.rptubes.block.PneumaticTubeBlock;
import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit() {
        PneumaticTubeBlock.renderId = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new TubeRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(PneumaticTubeBlockEntity.class, new TubeItemRenderer());
    }
}
