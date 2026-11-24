package com.rptubes.compat.cbm;

import codechicken.multipart.api.MultipartClientRegistry;
import codechicken.multipart.api.part.render.PartBakedModelRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/** Tube parts draw the tube block's own model, then the items in flight with the tube renderer. */
final class TubePartsClient {
    private static final PartBakedModelRenderer<TubePart> RENDERER = new PartBakedModelRenderer<TubePart>() {
        @Override
        public void renderDynamic(TubePart part, MatrixStack poseStack, IRenderTypeBuffer buffers, int packedLight, int packedOverlay, float partialTicks) {
            PneumaticTubeBlockEntity tube = part.tube();
            TileEntityRenderer<PneumaticTubeBlockEntity> renderer = TileEntityRendererDispatcher.instance.getRenderer(tube);
            if (renderer != null) {
                renderer.render(tube, partialTicks, poseStack, buffers, packedLight, packedOverlay);
            }
        }
    };

    private TubePartsClient() {
    }

    static void init(IEventBus modBus) {
        modBus.addListener(TubePartsClient::onClientSetup);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        for (TubeParts.Kind kind : TubeParts.Kind.values()) {
            MultipartClientRegistry.register(kind.type, RENDERER);
        }
    }
}
