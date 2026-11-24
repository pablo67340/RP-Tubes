//? if cbm {
package com.rptubes.compat.cbm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rptubes.blockentity.PneumaticTubeBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
//? if <1.21.11 {
import codechicken.multipart.api.MultipartClientRegistry;
import codechicken.multipart.api.part.render.PartBakedModelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
//?} else {
/*import codechicken.lib.vec.Cuboid6;
import codechicken.multipart.api.MultipartType;
import codechicken.multipart.api.RegisterMultipartRenderersEvent;
import codechicken.multipart.api.part.render.BlockStatePartBakedModelRenderer;
import codechicken.multipart.api.part.render.DynamicPartRenderer;
import codechicken.multipart.api.part.render.StandardPartParticleHandler;
import codechicken.multipart.util.PartRayTraceResult;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

import java.util.List;
*///?}
//? if forge {
/*import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
*///?} else {
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
//?}

/** Tube parts draw the tube block's own model, then the items in flight with the tube renderer. */
final class TubePartsClient {
    private TubePartsClient() {
    }

    //? if <1.21.11 {
    private static final PartBakedModelRenderer<TubePart> RENDERER = new PartBakedModelRenderer<>() {
        @Override
        public void renderDynamic(TubePart part, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay, float partialTicks) {
            PneumaticTubeBlockEntity tube = part.tube();
            BlockEntityRenderer<PneumaticTubeBlockEntity> renderer = Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(tube);
            if (renderer != null) {
                renderer.render(tube, partialTicks, poseStack, buffers, packedLight, packedOverlay);
            }
        }
    };

    static void init(IEventBus modBus) {
        modBus.addListener(TubePartsClient::onClientSetup);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        for (TubeParts.Kind kind : TubeParts.Kind.values()) {
            MultipartClientRegistry.register(kind.type.get(), RENDERER);
        }
    }
    //?} else {
    /*private static final StandardPartParticleHandler<TubePart> PARTICLES = new StandardPartParticleHandler<>() {
        @Override
        public Cuboid6 getBounds(TubePart part) {
            return new Cuboid6(part.getShape(CollisionContext.empty()).bounds());
        }

        @Override
        public TextureAtlasSprite getParticleTexture(TubePart part, PartRayTraceResult hit) {
            return sprite(part);
        }

        @Override
        public List<TextureAtlasSprite> getBrokenTextures(TubePart part) {
            return List.of(sprite(part));
        }

        private TextureAtlasSprite sprite(TubePart part) {
            return Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getBlockModel(part.getCurrentState()).particleIcon();
        }
    };

    static void init(IEventBus modBus) {
        modBus.addListener(TubePartsClient::onRegisterRenderers);
    }

    private static void onRegisterRenderers(RegisterMultipartRenderersEvent event) {
        for (TubeParts.Kind kind : TubeParts.Kind.values()) {
            MultipartType<TubePart> type = kind.type.get();
            event.registerStaticPartRenderer(type, BlockStatePartBakedModelRenderer::new);
            event.registerDynamicPartRenderer(type, Items::new);
            event.registerParticleHandler(type, PARTICLES);
        }
    }

    // Forwards extraction and submission to the tube block's own renderer and render state.
    private static final class Items implements DynamicPartRenderer<TubePart, Items.State> {
        static final class State {
            BlockEntityRenderer<PneumaticTubeBlockEntity, BlockEntityRenderState> renderer;
            BlockEntityRenderState tube;
        }

        @Override
        public State createDynamicState() {
            return new State();
        }

        @Override
        @SuppressWarnings("unchecked")
        public void extractDynamicState(BEState beState, State state, TubePart part, float partialTick, Vec3 camera, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
            PneumaticTubeBlockEntity tube = part.tube();
            if (state.renderer == null) {
                state.renderer = (BlockEntityRenderer<PneumaticTubeBlockEntity, BlockEntityRenderState>) (BlockEntityRenderer<?, ?>)
                        Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(tube);
                if (state.renderer == null) {
                    return;
                }
                state.tube = state.renderer.createRenderState();
            }
            state.renderer.extractRenderState(tube, state.tube, partialTick, camera, breakProgress);
        }

        @Override
        public void submitDynamic(BEState beState, State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
            if (state.renderer != null) {
                state.renderer.submit(state.tube, poseStack, collector, cameraState);
            }
        }
    }
    *///?}
}
//?}
