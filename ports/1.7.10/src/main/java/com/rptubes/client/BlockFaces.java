package com.rptubes.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Works out, for a full-cube block model turned by a blockstate rotation, which model face ends
 * up on each world side and how its texture has to be turned so that RenderBlocks' fixed UVs
 * show it the way the newer versions' baked models do. Sides are ForgeDirection ordinals.
 */
@SideOnly(Side.CLIENT)
public final class BlockFaces {
    private static final int[][] NORMALS = {{0, -1, 0}, {0, 1, 0}, {0, 0, -1}, {0, 0, 1}, {-1, 0, 0}, {1, 0, 0}};
    /** Blockstate x and y rotation that turns a model's north face toward each facing. */
    private static final int[][] FACING_ROTATION = {{90, 0}, {270, 0}, {0, 0}, {0, 180}, {0, 270}, {0, 90}};

    private BlockFaces() {
    }

    /**
     * For a model facing north turned to {@code facing}, returns the model face shown on world
     * {@code side} and the {@link TransformedSprite} transform for its texture.
     *
     * @param faceRotation texture rotation of each model face in degrees, as in the model JSON
     */
    public static int[] map(int facing, int side, int[] faceRotation) {
        int rx = FACING_ROTATION[facing][0];
        int ry = FACING_ROTATION[facing][1];
        int[] n = NORMALS[side];
        double[] mn = unrotate(new double[]{n[0], n[1], n[2]}, rx, ry);
        int modelFace = 0;
        for (int f = 0; f < 6; f++) {
            if (NORMALS[f][0] == Math.round(mn[0]) && NORMALS[f][1] == Math.round(mn[1]) && NORMALS[f][2] == Math.round(mn[2])) {
                modelFace = f;
            }
        }
        double[] t00 = texel(side, modelFace, faceRotation[modelFace], rx, ry, 0, 0);
        double[] t10 = texel(side, modelFace, faceRotation[modelFace], rx, ry, 16, 0);
        boolean swap = Math.round(t10[0] - t00[0]) == 0;
        int transform = swap ? TransformedSprite.SWAP : 0;
        if (Math.round(t00[0]) == 16) transform |= TransformedSprite.FLIP_U;
        if (Math.round(t00[1]) == 16) transform |= TransformedSprite.FLIP_V;
        return new int[]{modelFace, transform};
    }

    /** Texture coordinate the model shows where RenderBlocks would sample (a, b) on world side. */
    private static double[] texel(int side, int modelFace, int rotation, int rx, int ry, double a, double b) {
        double[] p = vanillaPoint(side, a, b);
        double[] q = unrotate(new double[]{p[0] - 8, p[1] - 8, p[2] - 8}, rx, ry);
        double[] uv = modelUv(modelFace, q[0] + 8, q[1] + 8, q[2] + 8);
        double u = uv[0];
        double v = uv[1];
        switch (rotation) {
            case 90: return new double[]{v, 16 - u};
            case 180: return new double[]{16 - u, 16 - v};
            case 270: return new double[]{16 - v, u};
            default: return new double[]{u, v};
        }
    }

    /** The point on a full block's world side where RenderBlocks samples texture coordinate (a, b). */
    private static double[] vanillaPoint(int side, double a, double b) {
        switch (side) {
            case 0: return new double[]{a, 0, b};
            case 1: return new double[]{a, 16, b};
            case 2: return new double[]{16 - a, 16 - b, 0};
            case 3: return new double[]{a, 16 - b, 16};
            case 4: return new double[]{0, 16 - b, a};
            default: return new double[]{16, 16 - b, 16 - a};
        }
    }

    /** Default UV of a point on a model face, as Minecraft's block model baker assigns it. */
    private static double[] modelUv(int face, double x, double y, double z) {
        switch (face) {
            case 0: return new double[]{x, 16 - z};
            case 1: return new double[]{x, z};
            case 2: return new double[]{16 - x, 16 - y};
            case 3: return new double[]{x, 16 - y};
            case 4: return new double[]{z, 16 - y};
            default: return new double[]{16 - z, 16 - y};
        }
    }

    /** Undoes a blockstate rotation (x applied first, then y) on a vector about the block center. */
    private static double[] unrotate(double[] v, int rx, int ry) {
        for (int i = 0; i < (360 - ry) % 360 / 90; i++) {
            v = new double[]{-v[2], v[1], v[0]};
        }
        for (int i = 0; i < (360 - rx) % 360 / 90; i++) {
            v = new double[]{v[0], v[2], -v[1]};
        }
        return v;
    }
}
