package com.rptubes.client;

import com.rptubes.RPTubes;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * A block texture turned or mirrored while the atlas loads. 1.7.10 cannot rotate a face's
 * texture reliably (RenderBlocks' uvRotate mirrors some faces), so faces that the newer
 * versions' models rotate get a pre-turned copy instead.
 *
 * Transform bits: SWAP exchanges the axes, then FLIP_U and FLIP_V mirror them.
 */
@SideOnly(Side.CLIENT)
public class TransformedSprite extends TextureAtlasSprite {
    public static final int SWAP = 1;
    public static final int FLIP_U = 2;
    public static final int FLIP_V = 4;

    private final ResourceLocation source;
    private final int transform;

    private TransformedSprite(String name, String texture, int transform) {
        super(name);
        int colon = texture.indexOf(':');
        this.source = new ResourceLocation(texture.substring(0, colon),
                "textures/blocks/" + texture.substring(colon + 1) + ".png");
        this.transform = transform;
    }

    /** The icon for texture ("modid:name") with the given transform; 0 is the plain texture. */
    public static IIcon register(IIconRegister reg, String texture, int transform) {
        if (transform == 0 || !(reg instanceof TextureMap)) {
            return reg.registerIcon(texture);
        }
        TextureMap map = (TextureMap) reg;
        String name = texture + "_t" + transform;
        TextureAtlasSprite existing = map.getTextureExtry(name);
        if (existing != null) {
            return existing;
        }
        TransformedSprite sprite = new TransformedSprite(name, texture, transform);
        map.setTextureEntry(name, sprite);
        return sprite;
    }

    @Override
    public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location) {
        return true;
    }

    /** Returns false when loaded: TextureMap only stitches custom sprites whose load returned false. */
    @Override
    public boolean load(IResourceManager manager, ResourceLocation location) {
        BufferedImage in;
        try {
            in = ImageIO.read(manager.getResource(this.source).getInputStream());
        } catch (IOException e) {
            RPTubes.LOGGER.error("Could not load {} for {}", this.source, this.getIconName(), e);
            return true;
        }
        int size = in.getWidth();
        BufferedImage out = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        for (int a = 0; a < size; a++) {
            for (int b = 0; b < size; b++) {
                int u = (this.transform & SWAP) != 0 ? b : a;
                int v = (this.transform & SWAP) != 0 ? a : b;
                if ((this.transform & FLIP_U) != 0) u = size - 1 - u;
                if ((this.transform & FLIP_V) != 0) v = size - 1 - v;
                out.setRGB(a, b, in.getRGB(u, v));
            }
        }
        BufferedImage[] mips = new BufferedImage[1 + Minecraft.getMinecraft().gameSettings.mipmapLevels];
        mips[0] = out;
        this.loadSprite(mips, null, Minecraft.getMinecraft().gameSettings.anisotropicFiltering > 1);
        return false;
    }
}
