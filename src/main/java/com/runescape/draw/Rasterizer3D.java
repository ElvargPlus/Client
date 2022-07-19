package com.runescape.draw;

import com.runescape.Client;
import com.runescape.cache.def.texture.TextureDefinition;
import com.runescape.cache.def.texture.TextureLoader;
import com.runescape.cache.graphics.textures.Texture;


public final class Rasterizer3D extends Rasterizer2D {

    public static int fieldOfView = 512;
    public static double brightness = 1.0F;
    public static boolean world = true;
    public static boolean renderOnGpu = false;

    public static void clear() {
        anIntArray1468 = null;
        anIntArray1468 = null;
        anIntArray1470 = null;
        COSINE = null;
        scanOffsets = null;
        textures = null;
        textureIsTransparant = null;
        averageTextureColours = null;
        texelArrayPool = null;
        texelCache = null;
        textureLastUsed = null;
        hslToRgb = null;
        currentPalette = null;
    }

    public static void useViewport() {
        scanOffsets = new int[Rasterizer2D.height];

        for (int j = 0; j < Rasterizer2D.height; j++) {
            scanOffsets[j] = Rasterizer2D.width * j;
        }

        originViewX = Rasterizer2D.width / 2;
        originViewY = Rasterizer2D.height / 2;
    }

    public static void reposition(int width, int length) {
        scanOffsets = new int[length];
        for (int x = 0; x < length; x++) {
            scanOffsets[x] = width * x;
        }
        originViewX = width / 2;
        originViewY = length / 2;
    }

    public static void resetTextures() {
        if (texelArrayPool == null) {
            textureTexelPoolPointer = 50;

            texelArrayPool = new int[textureTexelPoolPointer][0x10000];

            for (int i = 0; i < loadedTextureCount; i++) {
                texelCache[i] = null;
            }
        }
    }

    public static void initTextures(int count)
    {
        loadedTextureCount = count;
        textureLastUsed = new int[count];
        texelCache = new int[count][];
    }

    public static void resetTexture(int textureId) {
        if (texelCache[textureId] == null) {
            return;
        }
        texelArrayPool[textureTexelPoolPointer++] = texelCache[textureId];
        texelCache[textureId] = null;
    }

    public static int[] getTexturePixels(int textureId) {
        TextureLoader texture = TextureLoader.get(textureId);
        if (texture == null)
            return null;

        textureLastUsed[textureId] = textureGetCount++;
        if (texelCache[textureId] != null)
            return texelCache[textureId];

        int texels[];
        //Start of mem management code
        if (textureTexelPoolPointer > 0) {	//Freed texture data arrays available
            texels = texelArrayPool[--textureTexelPoolPointer];
            texelArrayPool[textureTexelPoolPointer] = null;
        } else {   //No freed texture data arrays available, recycle least used texture's array
            int lastUsed = 0;
            int target = -1;
            for (int i = 0; i < loadedTextureCount; i++) {
                if (texelCache[i] != null && (textureLastUsed[i] < lastUsed || target == -1)) {
                    lastUsed = textureLastUsed[i];
                    target = i;
                }
            }

            texels = texelCache[target];
            texelCache[target] = null;
        }
        texelCache[textureId] = texels;
        //End of mem management code
        if (texture.width == 64)
            for (int y = 0; y < 128; y++)
                for (int x = 0; x < 128; x++)
                    texels[x + (y << 7)] = texture.getPixel((x >> 1) + ((y >> 1) << 6));


        else
            for (int texelPtr = 0; texelPtr < 16384; texelPtr++)
                texels[texelPtr] = texture.getPixel(texelPtr);


        TextureDefinition def = textureId >= 0 && textureId < TextureDefinition.textures.length ? TextureDefinition.textures[textureId]:null;
        int blendType = def != null ? def.anInt1226 : 0;
        if (blendType != 1 && blendType != 2)
            blendType = 0;

        for (int texelPtr = 0; texelPtr < 16384; texelPtr++) {
            int texel = texels[texelPtr];
            int alpha;
            if (blendType == 2)
                alpha = texel >>> 24;
            else if (blendType == 1)
                alpha = texel != 0 ? 0xff:0;

            else
            {
                alpha = texel >>> 24;
                if (def != null && !def.aBoolean1223)
                    alpha /= 5;

            }
            texel &= 0xffffff;
            texel = adjustBrightnessLinear(texel, 179);
            texel = adjustBrightness(texel, brightness);
            texel &= 0xf8f8ff;
            texels[texelPtr] = texel | (alpha << 24);
            texels[16384 + texelPtr] = ((texel - (texel >>> 3)) & 0xf8f8ff) | (alpha << 24);
            texels[32768 + texelPtr] = ((texel - (texel >>> 2)) & 0xf8f8ff) | (alpha << 24);
            texels[49152 + texelPtr] = ((texel - (texel >>> 2) - (texel >>> 3)) & 0xf8f8ff) | (alpha << 24);
        }

        return texels;
    }

    public static void calculatePalette(float brightness1)
    {
        brightness1 += Math.random() * 0.03F - 0.015F;
        //if (Rasterizer.brightness == brightness)
        //	return;

        brightness = brightness1;
        int hsl = 0;
        for (int k = 0; k < 512; k++) {
            float d1 = (float) (k / 8) / 64F + 0.0078125F;
            float d2 = (float) (k & 7) / 8F + 0.0625F;
            for (int k1 = 0; k1 < 128; k1++) {
                float d3 = (float) k1 / 128F;
                float r = d3;
                float g = d3;
                float b = d3;
                if (d2 != 0.0F) {
                    float d7;
                    if (d3 < 0.5F) {
                        d7 = d3 * (1.0F + d2);
                    } else {
                        d7 = (d3 + d2) - d3 * d2;
                    }
                    float d8 = 2F * d3 - d7;
                    float d9 = d1 + 1F / 3F;
                    if (d9 > 1.0F) {
                        d9--;
                    }
                    float d10 = d1;
                    float d11 = d1 - 1F / 3F;
                    if (d11 < 0.0F) {
                        d11++;
                    }
                    if (6F * d9 < 1.0F) {
                        r = d8 + (d7 - d8) * 6F * d9;
                    } else if (2F * d9 < 1.0F) {
                        r = d7;
                    } else if (3F * d9 < 2F) {
                        r = d8 + (d7 - d8) * ((2F / 3F) - d9) * 6F;
                    } else {
                        r = d8;
                    }
                    if (6F * d10 < 1.0F) {
                        g = d8 + (d7 - d8) * 6F * d10;
                    } else if (2F * d10 < 1.0F) {
                        g = d7;
                    } else if (3F * d10 < 2F) {
                        g = d8 + (d7 - d8) * ((2F / 3F) - d10) * 6F;
                    } else {
                        g = d8;
                    }
                    if (6F * d11 < 1.0F) {
                        b = d8 + (d7 - d8) * 6F * d11;
                    } else if (2F * d11 < 1.0F) {
                        b = d7;
                    } else if (3F * d11 < 2F) {
                        b = d8 + (d7 - d8) * ((2F / 3F) - d11) * 6F;
                    } else {
                        b = d8;
                    }
                }
                int rgb = ((int) ((float) Math.pow((double) r, (double) brightness) * 256F) << 16) + ((int) ((float) Math.pow((double) g, (double) brightness) * 256F) << 8) + (int) ((float) Math.pow((double) b, (double) brightness) * 256F);
                if (rgb == 0)
                    rgb = 1;

                hslToRgb[hsl++] = rgb;
            }

        }

        for (int textureId = 0; textureId != loadedTextureCount; ++textureId)
            resetTexture(textureId);

    }

    private static int adjustBrightness(int rgb, double intensity) {
        double r = (rgb >> 16) / 256D;
        double g = (rgb >> 8 & 0xff) / 256D;
        double b = (rgb & 0xff) / 256D;
        r = Math.pow(r, intensity);
        g = Math.pow(g, intensity);
        b = Math.pow(b, intensity);
        int r_byte = (int) (r * 256D);
        int g_byte = (int) (g * 256D);
        int b_byte = (int) (b * 256D);
        return (r_byte << 16) + (g_byte << 8) + b_byte;
    }

    public static Texture[] getTextures() {
        return textures;
    }

    private static int adjustBrightness(int rgb, float brightness) {
        return ((int) ((float) Math.pow((double) ((float) (rgb >>> 16) / 256.0F), (double) brightness) * 256.0F) << 16) |
                ((int) ((float) Math.pow((double) ((float) ((rgb >>> 8) & 0xff) / 256.0F), (double) brightness) * 256.0F) << 8) |
                (int) ((float) Math.pow((double) ((float) (rgb & 0xff) / 256.0F), (double) brightness) * 256.0F);
    }

    private static int adjustBrightnessLinear(int rgb, int factor)
    {
        return ((((rgb >>> 16) * factor) & 0xff00) << 8) |
                ((((rgb >>> 8) & 0xff) * factor) & 0xff00) |
                (((rgb & 0xff) * factor) >> 8);
    }

    public static void drawShadedTriangle(int y1, int y2, int y3, int x1, int x2, int x3, int hsl1, int hsl2, int hsl3) {
        if (Client.processGpuPlugin() && !renderOnGpu) {
            return;
        }

        int var9 = x2 - x1;
        int var10 = y2 - y1;
        int var11 = x3 - x1;
        int var12 = y3 - y1;
        int var13 = hsl2 - hsl1;
        int var14 = hsl3 - hsl1;
        int var15;
        if (y3 != y2) {
            var15 = (x3 - x2 << 14) / (y3 - y2);
        } else {
            var15 = 0;
        }

        int var16;
        if (y1 != y2) {
            var16 = (var9 << 14) / var10;
        } else {
            var16 = 0;
        }

        int var17;
        if (y1 != y3) {
            var17 = (var11 << 14) / var12;
        } else {
            var17 = 0;
        }

        int var18 = var9 * var12 - var11 * var10;
        if (var18 != 0) {
            int var19 = (var13 * var12 - var14 * var10 << 8) / var18;
            int var20 = (var14 * var9 - var13 * var11 << 8) / var18;
            if (y1 <= y2 && y1 <= y3) {
                if (y1 < Rasterizer2D.bottomY) {
                    if (y2 > Rasterizer2D.bottomY) {
                        y2 = Rasterizer2D.bottomY;
                    }

                    if (y3 > Rasterizer2D.bottomY) {
                        y3 = Rasterizer2D.bottomY;
                    }

                    hsl1 = var19 + ((hsl1 << 8) - x1 * var19);
                    if (y2 < y3) {
                        x3 = x1 <<= 14;
                        if (y1 < 0) {
                            x3 -= y1 * var17;
                            x1 -= y1 * var16;
                            hsl1 -= y1 * var20;
                            y1 = 0;
                        }

                        x2 <<= 14;
                        if (y2 < 0) {
                            x2 -= var15 * y2;
                            y2 = 0;
                        }

                        if ((y1 == y2 || var17 >= var16) && (y1 != y2 || var17 <= var15)) {
                            y3 -= y2;
                            y2 -= y1;
                            y1 = scanOffsets[y1];

                            while (true) {
                                --y2;
                                if (y2 < 0) {
                                    while (true) {
                                        --y3;
                                        if (y3 < 0) {
                                            return;
                                        }

                                        drawGouraudScanline(Rasterizer2D.pixels, y1, 0, 0, x2 >> 14, x3 >> 14, hsl1, var19);
                                        x3 += var17;
                                        x2 += var15;
                                        hsl1 += var20;
                                        y1 += Rasterizer2D.width;
                                    }
                                }

                                drawGouraudScanline(Rasterizer2D.pixels, y1, 0, 0, x1 >> 14, x3 >> 14, hsl1, var19);
                                x3 += var17;
                                x1 += var16;
                                hsl1 += var20;
                                y1 += Rasterizer2D.width;
                            }
                        } else {
                            y3 -= y2;
                            y2 -= y1;
                            y1 = scanOffsets[y1];

                            while (true) {
                                --y2;
                                if (y2 < 0) {
                                    while (true) {
                                        --y3;
                                        if (y3 < 0) {
                                            return;
                                        }

                                        drawGouraudScanline(Rasterizer2D.pixels, y1, 0, 0, x3 >> 14, x2 >> 14, hsl1, var19);
                                        x3 += var17;
                                        x2 += var15;
                                        hsl1 += var20;
                                        y1 += Rasterizer2D.width;
                                    }
                                }

                                drawGouraudScanline(Rasterizer2D.pixels, y1, 0, 0, x3 >> 14, x1 >> 14, hsl1, var19);
                                x3 += var17;
                                x1 += var16;
                                hsl1 += var20;
                                y1 += Rasterizer2D.width;
                            }
                        }
                    } else {
                        x2 = x1 <<= 14;
                        if (y1 < 0) {
                            x2 -= y1 * var17;
                            x1 -= y1 * var16;
                            hsl1 -= y1 * var20;
                            y1 = 0;
                        }

                        x3 <<= 14;
                        if (y3 < 0) {
                            x3 -= var15 * y3;
                            y3 = 0;
                        }

                        if (y1 != y3 && var17 < var16 || y1 == y3 && var15 > var16) {
                            y2 -= y3;
                            y3 -= y1;
                            y1 = scanOffsets[y1];

                            while (true) {
                                --y3;
                                if (y3 < 0) {
                                    while (true) {
                                        --y2;
                                        if (y2 < 0) {
                                            return;
                                        }

                                        drawGouraudScanline(Rasterizer2D.pixels, y1, 0, 0, x3 >> 14, x1 >> 14, hsl1, var19);
                                        x3 += var15;
                                        x1 += var16;
                                        hsl1 += var20;
                                        y1 += Rasterizer2D.width;
                                    }
                                }

                                drawGouraudScanline(Rasterizer2D.pixels, y1, 0, 0, x2 >> 14, x1 >> 14, hsl1, var19);
                                x2 += var17;
                                x1 += var16;
                                hsl1 += var20;
                                y1 += Rasterizer2D.width;
                            }
                        } else {
                            y2 -= y3;
                            y3 -= y1;
                            y1 = scanOffsets[y1];

                            while (true) {
                                --y3;
                                if (y3 < 0) {
                                    while (true) {
                                        --y2;
                                        if (y2 < 0) {
                                            return;
                                        }

                                        drawGouraudScanline(Rasterizer2D.pixels, y1, 0, 0, x1 >> 14, x3 >> 14, hsl1, var19);
                                        x3 += var15;
                                        x1 += var16;
                                        hsl1 += var20;
                                        y1 += Rasterizer2D.width;
                                    }
                                }

                                drawGouraudScanline(Rasterizer2D.pixels, y1, 0, 0, x1 >> 14, x2 >> 14, hsl1, var19);
                                x2 += var17;
                                x1 += var16;
                                hsl1 += var20;
                                y1 += Rasterizer2D.width;
                            }
                        }
                    }
                }
            } else if (y2 <= y3) {
                if (y2 < Rasterizer2D.bottomY) {
                    if (y3 > Rasterizer2D.bottomY) {
                        y3 = Rasterizer2D.bottomY;
                    }

                    if (y1 > Rasterizer2D.bottomY) {
                        y1 = Rasterizer2D.bottomY;
                    }

                    hsl2 = var19 + ((hsl2 << 8) - var19 * x2);
                    if (y3 < y1) {
                        x1 = x2 <<= 14;
                        if (y2 < 0) {
                            x1 -= var16 * y2;
                            x2 -= var15 * y2;
                            hsl2 -= var20 * y2;
                            y2 = 0;
                        }

                        x3 <<= 14;
                        if (y3 < 0) {
                            x3 -= var17 * y3;
                            y3 = 0;
                        }

                        if ((y3 == y2 || var16 >= var15) && (y3 != y2 || var16 <= var17)) {
                            y1 -= y3;
                            y3 -= y2;
                            y2 = scanOffsets[y2];

                            while (true) {
                                --y3;
                                if (y3 < 0) {
                                    while (true) {
                                        --y1;
                                        if (y1 < 0) {
                                            return;
                                        }

                                        drawGouraudScanline(Rasterizer2D.pixels, y2, 0, 0, x3 >> 14, x1 >> 14, hsl2, var19);
                                        x1 += var16;
                                        x3 += var17;
                                        hsl2 += var20;
                                        y2 += Rasterizer2D.width;
                                    }
                                }

                                drawGouraudScanline(Rasterizer2D.pixels, y2, 0, 0, x2 >> 14, x1 >> 14, hsl2, var19);
                                x1 += var16;
                                x2 += var15;
                                hsl2 += var20;
                                y2 += Rasterizer2D.width;
                            }
                        } else {
                            y1 -= y3;
                            y3 -= y2;
                            y2 = scanOffsets[y2];

                            while (true) {
                                --y3;
                                if (y3 < 0) {
                                    while (true) {
                                        --y1;
                                        if (y1 < 0) {
                                            return;
                                        }

                                        drawGouraudScanline(Rasterizer2D.pixels, y2, 0, 0, x1 >> 14, x3 >> 14, hsl2, var19);
                                        x1 += var16;
                                        x3 += var17;
                                        hsl2 += var20;
                                        y2 += Rasterizer2D.width;
                                    }
                                }

                                drawGouraudScanline(Rasterizer2D.pixels, y2, 0, 0, x1 >> 14, x2 >> 14, hsl2, var19);
                                x1 += var16;
                                x2 += var15;
                                hsl2 += var20;
                                y2 += Rasterizer2D.width;
                            }
                        }
                    } else {
                        x3 = x2 <<= 14;
                        if (y2 < 0) {
                            x3 -= var16 * y2;
                            x2 -= var15 * y2;
                            hsl2 -= var20 * y2;
                            y2 = 0;
                        }

                        x1 <<= 14;
                        if (y1 < 0) {
                            x1 -= y1 * var17;
                            y1 = 0;
                        }

                        if (var16 < var15) {
                            y3 -= y1;
                            y1 -= y2;
                            y2 = scanOffsets[y2];

                            while (true) {
                                --y1;
                                if (y1 < 0) {
                                    while (true) {
                                        --y3;
                                        if (y3 < 0) {
                                            return;
                                        }

                                        drawGouraudScanline(Rasterizer2D.pixels, y2, 0, 0, x1 >> 14, x2 >> 14, hsl2, var19);
                                        x1 += var17;
                                        x2 += var15;
                                        hsl2 += var20;
                                        y2 += Rasterizer2D.width;
                                    }
                                }

                                drawGouraudScanline(Rasterizer2D.pixels, y2, 0, 0, x3 >> 14, x2 >> 14, hsl2, var19);
                                x3 += var16;
                                x2 += var15;
                                hsl2 += var20;
                                y2 += Rasterizer2D.width;
                            }
                        } else {
                            y3 -= y1;
                            y1 -= y2;
                            y2 = scanOffsets[y2];

                            while (true) {
                                --y1;
                                if (y1 < 0) {
                                    while (true) {
                                        --y3;
                                        if (y3 < 0) {
                                            return;
                                        }

                                        drawGouraudScanline(Rasterizer2D.pixels, y2, 0, 0, x2 >> 14, x1 >> 14, hsl2, var19);
                                        x1 += var17;
                                        x2 += var15;
                                        hsl2 += var20;
                                        y2 += Rasterizer2D.width;
                                    }
                                }

                                drawGouraudScanline(Rasterizer2D.pixels, y2, 0, 0, x2 >> 14, x3 >> 14, hsl2, var19);
                                x3 += var16;
                                x2 += var15;
                                hsl2 += var20;
                                y2 += Rasterizer2D.width;
                            }
                        }
                    }
                }
            } else if (y3 < Rasterizer2D.bottomY) {
                if (y1 > Rasterizer2D.bottomY) {
                    y1 = Rasterizer2D.bottomY;
                }

                if (y2 > Rasterizer2D.bottomY) {
                    y2 = Rasterizer2D.bottomY;
                }

                hsl3 = var19 + ((hsl3 << 8) - x3 * var19);
                if (y1 < y2) {
                    x2 = x3 <<= 14;
                    if (y3 < 0) {
                        x2 -= var15 * y3;
                        x3 -= var17 * y3;
                        hsl3 -= var20 * y3;
                        y3 = 0;
                    }

                    x1 <<= 14;
                    if (y1 < 0) {
                        x1 -= y1 * var16;
                        y1 = 0;
                    }

                    if (var15 < var17) {
                        y2 -= y1;
                        y1 -= y3;
                        y3 = scanOffsets[y3];

                        while (true) {
                            --y1;
                            if (y1 < 0) {
                                while (true) {
                                    --y2;
                                    if (y2 < 0) {
                                        return;
                                    }

                                    drawGouraudScanline(Rasterizer2D.pixels, y3, 0, 0, x2 >> 14, x1 >> 14, hsl3, var19);
                                    x2 += var15;
                                    x1 += var16;
                                    hsl3 += var20;
                                    y3 += Rasterizer2D.width;
                                }
                            }

                            drawGouraudScanline(Rasterizer2D.pixels, y3, 0, 0, x2 >> 14, x3 >> 14, hsl3, var19);
                            x2 += var15;
                            x3 += var17;
                            hsl3 += var20;
                            y3 += Rasterizer2D.width;
                        }
                    } else {
                        y2 -= y1;
                        y1 -= y3;
                        y3 = scanOffsets[y3];

                        while (true) {
                            --y1;
                            if (y1 < 0) {
                                while (true) {
                                    --y2;
                                    if (y2 < 0) {
                                        return;
                                    }

                                    drawGouraudScanline(Rasterizer2D.pixels, y3, 0, 0, x1 >> 14, x2 >> 14, hsl3, var19);
                                    x2 += var15;
                                    x1 += var16;
                                    hsl3 += var20;
                                    y3 += Rasterizer2D.width;
                                }
                            }

                            drawGouraudScanline(Rasterizer2D.pixels, y3, 0, 0, x3 >> 14, x2 >> 14, hsl3, var19);
                            x2 += var15;
                            x3 += var17;
                            hsl3 += var20;
                            y3 += Rasterizer2D.width;
                        }
                    }
                } else {
                    x1 = x3 <<= 14;
                    if (y3 < 0) {
                        x1 -= var15 * y3;
                        x3 -= var17 * y3;
                        hsl3 -= var20 * y3;
                        y3 = 0;
                    }

                    x2 <<= 14;
                    if (y2 < 0) {
                        x2 -= var16 * y2;
                        y2 = 0;
                    }

                    if (var15 < var17) {
                        y1 -= y2;
                        y2 -= y3;
                        y3 = scanOffsets[y3];

                        while (true) {
                            --y2;
                            if (y2 < 0) {
                                while (true) {
                                    --y1;
                                    if (y1 < 0) {
                                        return;
                                    }

                                    drawGouraudScanline(Rasterizer2D.pixels, y3, 0, 0, x2 >> 14, x3 >> 14, hsl3, var19);
                                    x2 += var16;
                                    x3 += var17;
                                    hsl3 += var20;
                                    y3 += Rasterizer2D.width;
                                }
                            }

                            drawGouraudScanline(Rasterizer2D.pixels, y3, 0, 0, x1 >> 14, x3 >> 14, hsl3, var19);
                            x1 += var15;
                            x3 += var17;
                            hsl3 += var20;
                            y3 += Rasterizer2D.width;
                        }
                    } else {
                        y1 -= y2;
                        y2 -= y3;
                        y3 = scanOffsets[y3];

                        while (true) {
                            --y2;
                            if (y2 < 0) {
                                while (true) {
                                    --y1;
                                    if (y1 < 0) {
                                        return;
                                    }

                                    drawGouraudScanline(Rasterizer2D.pixels, y3, 0, 0, x3 >> 14, x2 >> 14, hsl3, var19);
                                    x2 += var16;
                                    x3 += var17;
                                    hsl3 += var20;
                                    y3 += Rasterizer2D.width;
                                }
                            }

                            drawGouraudScanline(Rasterizer2D.pixels, y3, 0, 0, x3 >> 14, x1 >> 14, hsl3, var19);
                            x1 += var15;
                            x3 += var17;
                            hsl3 += var20;
                            y3 += Rasterizer2D.width;
                        }
                    }
                }
            }
        }
    }

    public static void drawGouraudScanline(int var0[], int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
        if (Client.instance.frameMode == Client.ScreenMode.FIXED
                && world && var1 <= 259086) { //(512+4)+(334+4)*765
            var1 += 3064; //4+4*765
        }

        if (textureOutOfDrawingBounds) {
            if (var5 > lastX) {
                var5 = lastX;
            }

            if (var4 < 0) {
                var4 = 0;
            }
        }

        if (var4 < var5) {
            var1 += var4;
            var6 += var4 * var7;
            int var8;
            int var9;
            int var10;
            if (aBoolean1464) {
                var3 = var5 - var4 >> 2;
                var7 <<= 2;
                if (alpha == 0) {
                    if (var3 > 0) {
                        do {
                            var2 = hslToRgb[var6 >> 8];
                            var6 += var7;
                            drawAlpha(var0, var1++, var2, 255);
                            drawAlpha(var0, var1++, var2, 255);
                            drawAlpha(var0, var1++, var2, 255);
                            drawAlpha(var0, var1++, var2, 255);
                            --var3;
                        } while(var3 > 0);
                    }

                    var3 = var5 - var4 & 3;
                    if (var3 > 0) {
                        var2 = hslToRgb[var6 >> 8];

                        do {
                            drawAlpha(var0, var1++, var2, 255);
                            --var3;
                        } while(var3 > 0);
                    }
                } else {
                    var8 = alpha;
                    var9 = 256 - alpha;
                    if (var3 > 0) {
                        do {
                            var2 = hslToRgb[var6 >> 8];
                            var6 += var7;
                            var2 = (var9 * (var2 & 65280) >> 8 & 65280) + (var9 * (var2 & 16711935) >> 8 & 16711935);
                            var10 = var0[var1];
                            drawAlpha(var0, var1++, ((var10 & 16711935) * var8 >> 8 & 16711935) + var2 + (var8 * (var10 & 65280) >> 8 & 65280), 255);
                            var10 = var0[var1];
                            drawAlpha(var0, var1++, ((var10 & 16711935) * var8 >> 8 & 16711935) + var2 + (var8 * (var10 & 65280) >> 8 & 65280), 255);
                            var10 = var0[var1];
                            drawAlpha(var0, var1++, ((var10 & 16711935) * var8 >> 8 & 16711935) + var2 + (var8 * (var10 & 65280) >> 8 & 65280), 255);
                            var10 = var0[var1];
                            drawAlpha(var0, var1++, ((var10 & 16711935) * var8 >> 8 & 16711935) + var2 + (var8 * (var10 & 65280) >> 8 & 65280), 255);
                            --var3;
                        } while(var3 > 0);
                    }

                    var3 = var5 - var4 & 3;
                    if (var3 > 0) {
                        var2 = hslToRgb[var6 >> 8];
                        var2 = (var9 * (var2 & 65280) >> 8 & 65280) + (var9 * (var2 & 16711935) >> 8 & 16711935);

                        do {
                            var10 = var0[var1];
                            drawAlpha(var0, var1++, ((var10 & 16711935) * var8 >> 8 & 16711935) + var2 + (var8 * (var10 & 65280) >> 8 & 65280), 255);
                            --var3;
                        } while(var3 > 0);
                    }
                }

            } else {
                var3 = var5 - var4;
                if (alpha == 0) {
                    do {
                        drawAlpha(var0, var1++, hslToRgb[var6 >> 8], 255);
                        var6 += var7;
                        --var3;
                    } while(var3 > 0);
                } else {
                    var8 = alpha;
                    var9 = 256 - alpha;

                    do {
                        var2 = hslToRgb[var6 >> 8];
                        var6 += var7;
                        var2 = (var9 * (var2 & 65280) >> 8 & 65280) + (var9 * (var2 & 16711935) >> 8 & 16711935);
                        var10 = var0[var1];
                        drawAlpha(var0, var1++, ((var10 & 16711935) * var8 >> 8 & 16711935) + var2 + (var8 * (var10 & 65280) >> 8 & 65280), 255);
                        --var3;
                    } while(var3 > 0);
                }

            }
        }
    }

    public static void drawFlatTriangle(int y_a, int y_b, int y_c, int x_a, int x_b, int x_c, int k1) {
        if (Client.processGpuPlugin() && !renderOnGpu) {
            return;
        }

        int a_to_b = 0;
        if (y_b != y_a) {
            a_to_b = (x_b - x_a << 16) / (y_b - y_a);
        }
        int b_to_c = 0;
        if (y_c != y_b) {
            b_to_c = (x_c - x_b << 16) / (y_c - y_b);
        }
        int c_to_a = 0;
        if (y_c != y_a) {
            c_to_a = (x_a - x_c << 16) / (y_a - y_c);
        }
        float b_aX = x_b - x_a;
        float b_aY = y_b - y_a;
        float c_aX = x_c - x_a;
        float c_aY = y_c - y_a;

        if (y_a <= y_b && y_a <= y_c) {
            if (y_a >= Rasterizer2D.bottomY)
                return;
            if (y_b > Rasterizer2D.bottomY)
                y_b = Rasterizer2D.bottomY;
            if (y_c > Rasterizer2D.bottomY)
                y_c = Rasterizer2D.bottomY;
            if (y_b < y_c) {
                x_c = x_a <<= 16;
                if (y_a < 0) {
                    x_c -= c_to_a * y_a;
                    x_a -= a_to_b * y_a;
                    y_a = 0;
                }
                x_b <<= 16;
                if (y_b < 0) {
                    x_b -= b_to_c * y_b;
                    y_b = 0;
                }
                if (y_a != y_b && c_to_a < a_to_b || y_a == y_b && c_to_a > b_to_c) {
                    y_c -= y_b;
                    y_b -= y_a;
                    for (y_a = scanOffsets[y_a]; --y_b >= 0; y_a += Rasterizer2D.width) {
                        drawFlatTexturedScanline(Rasterizer2D.pixels, y_a, k1, x_c >> 16, x_a >> 16);
                        x_c += c_to_a;
                        x_a += a_to_b;
                    }

                    while (--y_c >= 0) {
                        drawFlatTexturedScanline(Rasterizer2D.pixels, y_a, k1, x_c >> 16, x_b >> 16);
                        x_c += c_to_a;
                        x_b += b_to_c;
                        y_a += Rasterizer2D.width;
                    }
                    return;
                }
                y_c -= y_b;
                y_b -= y_a;
                for (y_a = scanOffsets[y_a]; --y_b >= 0; y_a += Rasterizer2D.width) {
                    drawFlatTexturedScanline(Rasterizer2D.pixels, y_a, k1, x_a >> 16, x_c >> 16);
                    x_c += c_to_a;
                    x_a += a_to_b;
                }

                while (--y_c >= 0) {
                    drawFlatTexturedScanline(Rasterizer2D.pixels, y_a, k1, x_b >> 16, x_c >> 16);
                    x_c += c_to_a;
                    x_b += b_to_c;
                    y_a += Rasterizer2D.width;
                }
                return;
            }
            x_b = x_a <<= 16;
            if (y_a < 0) {
                x_b -= c_to_a * y_a;
                x_a -= a_to_b * y_a;
                y_a = 0;

            }
            x_c <<= 16;
            if (y_c < 0) {
                x_c -= b_to_c * y_c;
                y_c = 0;
            }
            if (y_a != y_c && c_to_a < a_to_b || y_a == y_c && b_to_c > a_to_b) {
                y_b -= y_c;
                y_c -= y_a;
                for (y_a = scanOffsets[y_a]; --y_c >= 0; y_a += Rasterizer2D.width) {
                    drawFlatTexturedScanline(Rasterizer2D.pixels, y_a, k1, x_b >> 16, x_a >> 16);
                    x_b += c_to_a;
                    x_a += a_to_b;
                }

                while (--y_b >= 0) {
                    drawFlatTexturedScanline(Rasterizer2D.pixels, y_a, k1, x_c >> 16, x_a >> 16);
                    x_c += b_to_c;
                    x_a += a_to_b;
                    y_a += Rasterizer2D.width;
                }
                return;
            }
            y_b -= y_c;
            y_c -= y_a;
            for (y_a = scanOffsets[y_a]; --y_c >= 0; y_a += Rasterizer2D.width) {
                drawFlatTexturedScanline(Rasterizer2D.pixels, y_a, k1, x_a >> 16, x_b >> 16);
                x_b += c_to_a;
                x_a += a_to_b;
            }

            while (--y_b >= 0) {
                drawFlatTexturedScanline(Rasterizer2D.pixels, y_a, k1, x_a >> 16, x_c >> 16);
                x_c += b_to_c;
                x_a += a_to_b;
                y_a += Rasterizer2D.width;
            }
            return;
        }
        if (y_b <= y_c) {
            if (y_b >= Rasterizer2D.bottomY)
                return;
            if (y_c > Rasterizer2D.bottomY)
                y_c = Rasterizer2D.bottomY;
            if (y_a > Rasterizer2D.bottomY)
                y_a = Rasterizer2D.bottomY;
            if (y_c < y_a) {
                x_a = x_b <<= 16;
                if (y_b < 0) {
                    x_a -= a_to_b * y_b;
                    x_b -= b_to_c * y_b;
                    y_b = 0;
                }
                x_c <<= 16;
                if (y_c < 0) {
                    x_c -= c_to_a * y_c;
                    y_c = 0;
                }
                if (y_b != y_c && a_to_b < b_to_c || y_b == y_c && a_to_b > c_to_a) {
                    y_a -= y_c;
                    y_c -= y_b;
                    for (y_b = scanOffsets[y_b]; --y_c >= 0; y_b += Rasterizer2D.width) {
                        drawFlatTexturedScanline(Rasterizer2D.pixels, y_b, k1, x_a >> 16, x_b >> 16);
                        x_a += a_to_b;
                        x_b += b_to_c;
                    }

                    while (--y_a >= 0) {
                        drawFlatTexturedScanline(Rasterizer2D.pixels, y_b, k1, x_a >> 16, x_c >> 16);
                        x_a += a_to_b;
                        x_c += c_to_a;
                        y_b += Rasterizer2D.width;
                    }
                    return;
                }
                y_a -= y_c;
                y_c -= y_b;
                for (y_b = scanOffsets[y_b]; --y_c >= 0; y_b += Rasterizer2D.width) {
                    drawFlatTexturedScanline(Rasterizer2D.pixels, y_b, k1, x_b >> 16, x_a >> 16);
                    x_a += a_to_b;
                    x_b += b_to_c;
                }

                while (--y_a >= 0) {
                    drawFlatTexturedScanline(Rasterizer2D.pixels, y_b, k1, x_c >> 16, x_a >> 16);
                    x_a += a_to_b;
                    x_c += c_to_a;
                    y_b += Rasterizer2D.width;
                }
                return;
            }
            x_c = x_b <<= 16;
            if (y_b < 0) {
                x_c -= a_to_b * y_b;
                x_b -= b_to_c * y_b;
                y_b = 0;
            }
            x_a <<= 16;
            if (y_a < 0) {
                x_a -= c_to_a * y_a;
                y_a = 0;
            }
            if (a_to_b < b_to_c) {
                y_c -= y_a;
                y_a -= y_b;
                for (y_b = scanOffsets[y_b]; --y_a >= 0; y_b += Rasterizer2D.width) {
                    drawFlatTexturedScanline(Rasterizer2D.pixels, y_b, k1, x_c >> 16, x_b >> 16);
                    x_c += a_to_b;
                    x_b += b_to_c;
                }

                while (--y_c >= 0) {
                    drawFlatTexturedScanline(Rasterizer2D.pixels, y_b, k1, x_a >> 16, x_b >> 16);
                    x_a += c_to_a;
                    x_b += b_to_c;
                    y_b += Rasterizer2D.width;
                }
                return;
            }
            y_c -= y_a;
            y_a -= y_b;
            for (y_b = scanOffsets[y_b]; --y_a >= 0; y_b += Rasterizer2D.width) {
                drawFlatTexturedScanline(Rasterizer2D.pixels, y_b, k1, x_b >> 16, x_c >> 16);
                x_c += a_to_b;
                x_b += b_to_c;
            }

            while (--y_c >= 0) {
                drawFlatTexturedScanline(Rasterizer2D.pixels, y_b, k1, x_b >> 16, x_a >> 16);
                x_a += c_to_a;
                x_b += b_to_c;
                y_b += Rasterizer2D.width;
            }
            return;
        }
        if (y_c >= Rasterizer2D.bottomY)
            return;
        if (y_a > Rasterizer2D.bottomY)
            y_a = Rasterizer2D.bottomY;
        if (y_b > Rasterizer2D.bottomY)
            y_b = Rasterizer2D.bottomY;
        if (y_a < y_b) {
            x_b = x_c <<= 16;
            if (y_c < 0) {
                x_b -= b_to_c * y_c;
                x_c -= c_to_a * y_c;
                y_c = 0;
            }
            x_a <<= 16;
            if (y_a < 0) {
                x_a -= a_to_b * y_a;
                y_a = 0;
            }
            if (b_to_c < c_to_a) {
                y_b -= y_a;
                y_a -= y_c;
                for (y_c = scanOffsets[y_c]; --y_a >= 0; y_c += Rasterizer2D.width) {
                    drawFlatTexturedScanline(Rasterizer2D.pixels, y_c, k1, x_b >> 16, x_c >> 16);
                    x_b += b_to_c;
                    x_c += c_to_a;
                }

                while (--y_b >= 0) {
                    drawFlatTexturedScanline(Rasterizer2D.pixels, y_c, k1, x_b >> 16, x_a >> 16);
                    x_b += b_to_c;
                    x_a += a_to_b;
                    y_c += Rasterizer2D.width;
                }
                return;
            }
            y_b -= y_a;
            y_a -= y_c;
            for (y_c = scanOffsets[y_c]; --y_a >= 0; y_c += Rasterizer2D.width) {
                drawFlatTexturedScanline(Rasterizer2D.pixels, y_c, k1, x_c >> 16, x_b >> 16);
                x_b += b_to_c;
                x_c += c_to_a;
            }

            while (--y_b >= 0) {
                drawFlatTexturedScanline(Rasterizer2D.pixels, y_c, k1, x_a >> 16, x_b >> 16);
                x_b += b_to_c;
                x_a += a_to_b;
                y_c += Rasterizer2D.width;
            }
            return;
        }
        x_a = x_c <<= 16;
        if (y_c < 0) {
            x_a -= b_to_c * y_c;
            x_c -= c_to_a * y_c;
            y_c = 0;
        }
        x_b <<= 16;
        if (y_b < 0) {
            x_b -= a_to_b * y_b;
            y_b = 0;
        }
        if (b_to_c < c_to_a) {
            y_a -= y_b;
            y_b -= y_c;
            for (y_c = scanOffsets[y_c]; --y_b >= 0; y_c += Rasterizer2D.width) {
                drawFlatTexturedScanline(Rasterizer2D.pixels, y_c, k1, x_a >> 16, x_c >> 16);
                x_a += b_to_c;
                x_c += c_to_a;
            }

            while (--y_a >= 0) {
                drawFlatTexturedScanline(Rasterizer2D.pixels, y_c, k1, x_b >> 16, x_c >> 16);
                x_b += a_to_b;
                x_c += c_to_a;
                y_c += Rasterizer2D.width;
            }
            return;
        }
        y_a -= y_b;
        y_b -= y_c;
        for (y_c = scanOffsets[y_c]; --y_b >= 0; y_c += Rasterizer2D.width) {
            drawFlatTexturedScanline(Rasterizer2D.pixels, y_c, k1, x_c >> 16, x_a >> 16);
            x_a += b_to_c;
            x_c += c_to_a;
        }

        while (--y_a >= 0) {
            drawFlatTexturedScanline(Rasterizer2D.pixels, y_c, k1, x_c >> 16, x_b >> 16);
            x_b += a_to_b;
            x_c += c_to_a;
            y_c += Rasterizer2D.width;
        }
    }

    private static void drawFlatTexturedScanline(int dest[], int dest_off, int loops, int start_x, int end_x) {
        if (Client.instance.frameMode == Client.ScreenMode.FIXED && world && dest_off <= 259086) {
            dest_off += 3064; //4+4*765
        }

        int rgb;
        if (textureOutOfDrawingBounds) {
            if (end_x > Rasterizer2D.lastX)
                end_x = Rasterizer2D.lastX;
            if (start_x < 0)
                start_x = 0;
        }
        if (start_x >= end_x)
            return;
        dest_off += start_x;
        rgb = end_x - start_x >> 2;
        if (alpha == 0) {
            while (--rgb >= 0) {
                for (int i = 0; i < 4; i++) {
                    drawAlpha(dest, dest_off, loops, 255);
                    dest_off++;
                }
            }
            for (rgb = end_x - start_x & 3; --rgb >= 0;) {
                drawAlpha(dest, dest_off, loops, 255);
                dest_off++;
            }
            return;
        }
        int dest_alpha = alpha;
        int src_alpha = 256 - alpha;
        loops = ((loops & 0xff00ff) * src_alpha >> 8 & 0xff00ff) + ((loops & 0xff00) * src_alpha >> 8 & 0xff00);
        while (--rgb >= 0) {
            for (int i = 0; i < 4; i++) {
                drawAlpha(dest, dest_off, loops + ((dest[dest_off] & 0xff00ff) * dest_alpha >> 8 & 0xff00ff) + ((dest[dest_off] & 0xff00) * dest_alpha >> 8 & 0xff00), 255);
                dest_off++;
            }
        }
        for (rgb = end_x - start_x & 3; --rgb >= 0;) {
            drawAlpha(dest, dest_off, loops + ((dest[dest_off] & 0xff00ff) * dest_alpha >> 8 & 0xff00ff) + ((dest[dest_off] & 0xff00) * dest_alpha >> 8 & 0xff00), 255);
            dest_off++;
        }
    }

    public static boolean drawTexturedTriangle(int y_a, int y_b, int y_c, int x_a, int x_b, int x_c, int grad_a, int grad_b, int grad_c, int Px, int Mx, int Nx, int Pz, int Mz, int Nz, int Py, int My, int Ny, int t_id, int color, boolean force) {
        if (t_id < 0 || t_id >= TextureDefinition.textures.length) {
            drawShadedTriangle(y_a, y_b, y_c, x_a, x_b, x_c, grad_a, grad_b, grad_c);
            return true;
        }
        TextureDefinition def = TextureDefinition.textures[t_id];
        if (def == null || (!force && !def.aBoolean1223 && lowMem)) {
            drawShadedTriangle(y_a, y_b, y_c, x_a, x_b, x_c, grad_a, grad_b, grad_c);
            return true;
        }
        int texture[] = getTexturePixels(t_id);
        if (texture == null) {
            drawShadedTriangle(y_a, y_b, y_c, x_a, x_b, x_c, grad_a, grad_b, grad_c);
            return false;
        }
        if (color >= 0xffff) {
            color = -1;
        }

        if (color >= 0) {
            color = hslToRgb[color];
        }

        Mx = Px - Mx;
        Mz = Pz - Mz;
        My = Py - My;
        Nx -= Px;
        Nz -= Pz;
        Ny -= Py;
        int Oa = Nx * Pz - Nz * Px << 14;
        int Ha = Nz * Py - Ny * Pz << 8;
        int Va = Ny * Px - Nx * Py << 5;
        int Ob = Mx * Pz - Mz * Px << 14;
        int Hb = Mz * Py - My * Pz << 8;
        int Vb = My * Px - Mx * Py << 5;
        int Oc = Mz * Nx - Mx * Nz << 14;
        int Hc = My * Nz - Mz * Ny << 8;
        int Vc = Mx * Ny - My * Nx << 5;
        int x_a_off = 0;
        int grad_a_off = 0;
        if (y_b != y_a) {
            x_a_off = (x_b - x_a << 16) / (y_b - y_a);
            grad_a_off = (grad_b - grad_a << 16) / (y_b - y_a);
        }
        int x_b_off = 0;
        int grad_b_off = 0;
        if (y_c != y_b) {
            x_b_off = (x_c - x_b << 16) / (y_c - y_b);
            grad_b_off = (grad_c - grad_b << 16) / (y_c - y_b);
        }
        int x_c_off = 0;
        int grad_c_off = 0;
        if (y_c != y_a) {
            x_c_off = (x_a - x_c << 16) / (y_a - y_c);
            grad_c_off = (grad_a - grad_c << 16) / (y_a - y_c);
        }
        if (y_a <= y_b && y_a <= y_c) {
            if (y_a >= Rasterizer2D.bottomY) {
                return true;
            }
            if (y_b > Rasterizer2D.bottomY) {
                y_b = Rasterizer2D.bottomY;
            }
            if (y_c > Rasterizer2D.bottomY) {
                y_c = Rasterizer2D.bottomY;
            }
            if (y_b < y_c) {
                x_c = x_a <<= 16;
                grad_c = grad_a <<= 16;
                if (y_a < 0) {
                    x_c -= x_c_off * y_a;
                    x_a -= x_a_off * y_a;
                    grad_c -= grad_c_off * y_a;
                    grad_a -= grad_a_off * y_a;
                    y_a = 0;
                }
                x_b <<= 16;
                grad_b <<= 16;
                if (y_b < 0) {
                    x_b -= x_b_off * y_b;
                    grad_b -= grad_b_off * y_b;
                    y_b = 0;
                }
                int jA = y_a - originViewY;
                Oa += Va * jA;
                Ob += Vb * jA;
                Oc += Vc * jA;

                y_c -= y_b;
                y_b -= y_a;
                y_a = scanOffsets[y_a];
                while (--y_b >= 0) {
                    drawTexturedLine(pixels, texture, y_a, x_a >> 16, x_c >> 16, grad_a >> 8, grad_c >> 8, Oa, Ob, Oc, Ha, Hb, Hc, color, force);
                    x_c += x_c_off;
                    x_a += x_a_off;
                    grad_c += grad_c_off;
                    grad_a += grad_a_off;
                    y_a += width;
                    Oa += Va;
                    Ob += Vb;
                    Oc += Vc;
                }
                while (--y_c >= 0) {
                    drawTexturedLine(pixels, texture, y_a, x_b >> 16, x_c >> 16, grad_b >> 8, grad_c >> 8, Oa, Ob, Oc, Ha, Hb, Hc, color, force);
                    x_c += x_c_off;
                    x_b += x_b_off;
                    grad_c += grad_c_off;
                    grad_b += grad_b_off;
                    y_a += width;
                    Oa += Va;
                    Ob += Vb;
                    Oc += Vc;
                }
                return true;
            }
            x_b = x_a <<= 16;
            grad_b = grad_a <<= 16;
            if (y_a < 0) {
                x_b -= x_c_off * y_a;
                x_a -= x_a_off * y_a;
                grad_b -= grad_c_off * y_a;
                grad_a -= grad_a_off * y_a;
                y_a = 0;
            }
            x_c <<= 16;
            grad_c <<= 16;
            if (y_c < 0) {
                x_c -= x_b_off * y_c;
                grad_c -= grad_b_off * y_c;
                y_c = 0;
            }
            int l8 = y_a - originViewY;
            Oa += Va * l8;
            Ob += Vb * l8;
            Oc += Vc * l8;

            y_b -= y_c;
            y_c -= y_a;
            y_a = scanOffsets[y_a];
            while (--y_c >= 0) {
                drawTexturedLine(pixels, texture, y_a, x_a >> 16, x_b >> 16, grad_a >> 8, grad_b >> 8, Oa, Ob, Oc, Ha, Hb, Hc, color, force);
                x_b += x_c_off;
                x_a += x_a_off;
                grad_b += grad_c_off;
                grad_a += grad_a_off;
                y_a += width;
                Oa += Va;
                Ob += Vb;
                Oc += Vc;
            }
            while (--y_b >= 0) {
                drawTexturedLine(pixels, texture, y_a, x_a >> 16, x_c >> 16, grad_a >> 8, grad_c >> 8, Oa, Ob, Oc, Ha, Hb, Hc, color, force);
                x_c += x_b_off;
                x_a += x_a_off;
                grad_c += grad_b_off;
                grad_a += grad_a_off;
                y_a += width;
                Oa += Va;
                Ob += Vb;
                Oc += Vc;
            }
            return true;
        }
        if (y_b <= y_c) {
            if (y_b >= Rasterizer2D.bottomY) {
                return true;
            }
            if (y_c > Rasterizer2D.bottomY) {
                y_c = Rasterizer2D.bottomY;
            }
            if (y_a > Rasterizer2D.bottomY) {
                y_a = Rasterizer2D.bottomY;
            }
            if (y_c < y_a) {
                x_a = x_b <<= 16;
                grad_a = grad_b <<= 16;
                if (y_b < 0) {
                    x_a -= x_a_off * y_b;
                    x_b -= x_b_off * y_b;
                    grad_a -= grad_a_off * y_b;
                    grad_b -= grad_b_off * y_b;
                    y_b = 0;
                }
                x_c <<= 16;
                grad_c <<= 16;
                if (y_c < 0) {
                    x_c -= x_c_off * y_c;
                    grad_c -= grad_c_off * y_c;
                    y_c = 0;
                }
                int i9 = y_b - originViewY;
                Oa += Va * i9;
                Ob += Vb * i9;
                Oc += Vc * i9;

                y_a -= y_c;
                y_c -= y_b;
                y_b = scanOffsets[y_b];
                //not these
                while (--y_c >= 0) {
                    drawTexturedLine(pixels, texture, y_b, x_b >> 16, x_a >> 16, grad_b >> 8, grad_a >> 8, Oa, Ob, Oc, Ha, Hb, Hc, color, force);
                    x_a += x_a_off;
                    x_b += x_b_off;
                    grad_a += grad_a_off;
                    grad_b += grad_b_off;
                    y_b += width;
                    Oa += Va;
                    Ob += Vb;
                    Oc += Vc;
                }
                while (--y_a >= 0) {
                    drawTexturedLine(pixels, texture, y_b, x_c >> 16, x_a >> 16, grad_c >> 8, grad_a >> 8, Oa, Ob, Oc, Ha, Hb, Hc, color, force);
                    x_a += x_a_off;
                    x_c += x_c_off;
                    grad_a += grad_a_off;
                    grad_c += grad_c_off;
                    y_b += width;
                    Oa += Va;
                    Ob += Vb;
                    Oc += Vc;
                }
                return true;
            }
            x_c = x_b <<= 16;
            grad_c = grad_b <<= 16;
            if (y_b < 0) {
                x_c -= x_a_off * y_b;
                x_b -= x_b_off * y_b;
                grad_c -= grad_a_off * y_b;
                grad_b -= grad_b_off * y_b;
                y_b = 0;
            }
            x_a <<= 16;
            grad_a <<= 16;
            if (y_a < 0) {
                x_a -= x_c_off * y_a;
                grad_a -= grad_c_off * y_a;
                y_a = 0;
            }
            int j9 = y_b - originViewY;
            Oa += Va * j9;
            Ob += Vb * j9;
            Oc += Vc * j9;

            y_c -= y_a;
            y_a -= y_b;
            y_b = scanOffsets[y_b];
            //not these
            while (--y_a >= 0) {
                drawTexturedLine(pixels, texture, y_b, x_b >> 16, x_c >> 16, grad_b >> 8, grad_c >> 8, Oa, Ob, Oc, Ha, Hb, Hc, color, force);
                x_c += x_a_off;
                x_b += x_b_off;
                grad_c += grad_a_off;
                grad_b += grad_b_off;
                y_b += width;
                Oa += Va;
                Ob += Vb;
                Oc += Vc;
            }
            while (--y_c >= 0) {
                drawTexturedLine(pixels, texture, y_b, x_b >> 16, x_a >> 16, grad_b >> 8, grad_a >> 8, Oa, Ob, Oc, Ha, Hb, Hc, color, force);
                x_a += x_c_off;
                x_b += x_b_off;
                grad_a += grad_c_off;
                grad_b += grad_b_off;
                y_b += width;
                Oa += Va;
                Ob += Vb;
                Oc += Vc;
            }
            return true;
        }
        if (y_c >= Rasterizer2D.bottomY) {
            return true;
        }
        if (y_a > Rasterizer2D.bottomY) {
            y_a = Rasterizer2D.bottomY;
        }
        if (y_b > Rasterizer2D.bottomY) {
            y_b = Rasterizer2D.bottomY;
        }
        if (y_a < y_b) {
            x_b = x_c <<= 16;
            grad_b = grad_c <<= 16;
            if (y_c < 0) {
                x_b -= x_b_off * y_c;
                x_c -= x_c_off * y_c;
                grad_b -= grad_b_off * y_c;
                grad_c -= grad_c_off * y_c;
                y_c = 0;
            }
            x_a <<= 16;
            grad_a <<= 16;
            if (y_a < 0) {
                x_a -= x_a_off * y_a;
                grad_a -= grad_a_off * y_a;
                y_a = 0;
            }
            int k9 = y_c - originViewY;
            Oa += Va * k9;
            Ob += Vb * k9;
            Oc += Vc * k9;

            y_b -= y_a;
            y_a -= y_c;
            y_c = scanOffsets[y_c];
            //not these
            while (--y_a >= 0) {
                drawTexturedLine(pixels, texture, y_c, x_c >> 16, x_b >> 16, grad_c >> 8, grad_b >> 8, Oa, Ob, Oc, Ha, Hb, Hc, color, force);
                x_b += x_b_off;
                x_c += x_c_off;
                grad_b += grad_b_off;
                grad_c += grad_c_off;
                y_c += width;
                Oa += Va;
                Ob += Vb;
                Oc += Vc;
            }
            while (--y_b >= 0) {
                drawTexturedLine(pixels, texture, y_c, x_a >> 16, x_b >> 16, grad_a >> 8, grad_b >> 8, Oa, Ob, Oc, Ha, Hb, Hc, color, force);
                x_b += x_b_off;
                x_a += x_a_off;
                grad_b += grad_b_off;
                grad_a += grad_a_off;
                y_c += width;
                Oa += Va;
                Ob += Vb;
                Oc += Vc;
            }
            return true;
        }
        x_a = x_c <<= 16;
        grad_a = grad_c <<= 16;
        if (y_c < 0) {
            x_a -= x_b_off * y_c;
            x_c -= x_c_off * y_c;
            grad_a -= grad_b_off * y_c;
            grad_c -= grad_c_off * y_c;
            y_c = 0;
        }
        x_b <<= 16;
        grad_b <<= 16;
        if (y_b < 0) {
            x_b -= x_a_off * y_b;
            grad_b -= grad_a_off * y_b;
            y_b = 0;
        }
        int l9 = y_c - originViewY;
        Oa += Va * l9;
        Ob += Vb * l9;
        Oc += Vc * l9;

        y_a -= y_b;
        y_b -= y_c;
        y_c = scanOffsets[y_c];
        //not these
        while (--y_b >= 0) {
            drawTexturedLine(pixels, texture, y_c, x_c >> 16, x_a >> 16, grad_c >> 8, grad_a >> 8, Oa, Ob, Oc, Ha, Hb, Hc, color, force);
            x_a += x_b_off;
            x_c += x_c_off;
            grad_a += grad_b_off;
            grad_c += grad_c_off;
            y_c += width;
            Oa += Va;
            Ob += Vb;
            Oc += Vc;
        }
        while (--y_a >= 0) {
            drawTexturedLine(pixels, texture, y_c, x_c >> 16, x_b >> 16, grad_c >> 8, grad_b >> 8, Oa, Ob, Oc, Ha, Hb, Hc, color, force);
            x_b += x_a_off;
            x_c += x_c_off;
            grad_b += grad_a_off;
            grad_c += grad_c_off;
            y_c += width;
            Oa += Va;
            Ob += Vb;
            Oc += Vc;
        }
        return true;
    }


    private static void drawTexturedLine(int dest[], int texture[], int dest_off, int start_x, int end_x, int shadeValue, int gradient, int arg7, int arg8, int arg9, int arg10, int arg11, int arg12, int color, boolean force) {
        //shadeValue = 500;//lol makes textures ultra bright and makes triangles visible - slightly wrong name.. meh
        int rgb = 0;
        int loops = 0;
        if (start_x >= end_x) {
            return;
        }
        int j3;
        int k3;
        if (textureOutOfDrawingBounds) {
            j3 = (gradient - shadeValue) / (end_x - start_x);
            if (end_x > Rasterizer2D.lastX) {
                end_x = Rasterizer2D.lastX;
            }
            if (start_x < 0) {
                shadeValue -= start_x * j3;
                start_x = 0;
            }
            if (start_x >= end_x) {
                return;
            }
            k3 = end_x - start_x >> 3;
            j3 <<= 12;
            shadeValue <<= 9;
        } else {
            if (end_x - start_x > 7) {
                k3 = end_x - start_x >> 3;
                j3 = (gradient - shadeValue) * anIntArray1468[k3] >> 6;
            } else {
                k3 = 0;
                j3 = 0;
            }
            shadeValue <<= 9;
        }
        dest_off += start_x;
        int j4 = 0;
        int l4 = 0;
        int l6 = start_x - originViewX;
        arg7 += (arg10 >> 3) * l6;
        arg8 += (arg11 >> 3) * l6;
        arg9 += (arg12 >> 3) * l6;
        int l5 = arg9 >> 14;
        if (l5 != 0) {
            rgb = arg7 / l5;
            loops = arg8 / l5;
            if (rgb < 0) {
                rgb = 0;
            } else if (rgb > 16256) {
                rgb = 16256;
            }
        }
        arg7 += arg10;
        arg8 += arg11;
        arg9 += arg12;
        l5 = arg9 >> 14;
        if (l5 != 0) {
            j4 = arg7 / l5;
            l4 = arg8 / l5;
            if (j4 < 7) {
                j4 = 7;
            } else if (j4 > 16256) {
                j4 = 16256;
            }
        }
        int j7 = j4 - rgb >> 3;
        int l7 = l4 - loops >> 3;
        rgb += shadeValue & 0x600000;
        int glb_alpha = alpha;
        if (glb_alpha < 0 || glb_alpha >= 0xff)
            glb_alpha = 0;

        int src;
        int src_alpha;
        int src_delta;
        int dst;
        while (k3-- > 0)
        {
            src = texture[(loops & 0x3f80) + (rgb >> 7)];
            src_alpha = src >>> 24;
            if (src_alpha != 0 || force)
            {
                if (src_alpha != 0xff && color >= 0)
                {
                    if (src_alpha == 0)
                        src = color;

                    else
                    {
                        src_delta = 0xff - src_alpha;
                        src = ((0xff00ff00 & (0xff00ff & src) * src_alpha | 0xff0000 & (src & 0xff00) * src_alpha) >>> 8) + (((0xff0000 & src_delta * (color & 0xff00) | src_delta * (color & 0xff00ff) & 0xff00ff00) >>> 8));
                    }
                    src_alpha = 0xff;
                }
                if (glb_alpha > 0)
                    src_alpha = (src_alpha * (glb_alpha + 1)) >>> 8;

                if (src_alpha != 0)
                {
                    if (src_alpha == 0xff)
                        dest[dest_off] = src & 0xffffff;

                    else
                    {
                        dst = dest[dest_off];
                        src_delta = 0xff - src_alpha;
                        dest[dest_off] = (((0xff00ff00 & (0xff00ff & src) * src_alpha | 0xff0000 & (src & 0xff00) * src_alpha) >>> 8) + (((0xff0000 & src_delta * (dst & 0xff00) | src_delta * (dst & 0xff00ff) & 0xff00ff00) >>> 8))) & 0xffffff;
                    }
                }
            }
            dest_off++;
            rgb += j7;
            loops += l7;

            src = texture[(loops & 0x3f80) + (rgb >> 7)];
            src_alpha = src >>> 24;
            if (src_alpha != 0 || force)
            {
                if (src_alpha != 0xff && color >= 0)
                {
                    if (src_alpha == 0)
                        src = color;

                    else
                    {
                        src_delta = 0xff - src_alpha;
                        src = ((0xff00ff00 & (0xff00ff & src) * src_alpha | 0xff0000 & (src & 0xff00) * src_alpha) >>> 8) + (((0xff0000 & src_delta * (color & 0xff00) | src_delta * (color & 0xff00ff) & 0xff00ff00) >>> 8));
                    }
                    src_alpha = 0xff;
                }
                if (glb_alpha > 0)
                    src_alpha = (src_alpha * (glb_alpha + 1)) >>> 8;

                if (src_alpha != 0)
                {
                    if (src_alpha == 0xff)
                        dest[dest_off] = src & 0xffffff;

                    else
                    {
                        dst = dest[dest_off];
                        src_delta = 0xff - src_alpha;
                        dest[dest_off] = (((0xff00ff00 & (0xff00ff & src) * src_alpha | 0xff0000 & (src & 0xff00) * src_alpha) >>> 8) + (((0xff0000 & src_delta * (dst & 0xff00) | src_delta * (dst & 0xff00ff) & 0xff00ff00) >>> 8))) & 0xffffff;
                    }
                }
            }
            dest_off++;
            rgb += j7;
            loops += l7;

            src = texture[(loops & 0x3f80) + (rgb >> 7)];
            src_alpha = src >>> 24;
            if (src_alpha != 0 || force)
            {
                if (src_alpha != 0xff && color >= 0)
                {
                    if (src_alpha == 0)
                        src = color;

                    else
                    {
                        src_delta = 0xff - src_alpha;
                        src = ((0xff00ff00 & (0xff00ff & src) * src_alpha | 0xff0000 & (src & 0xff00) * src_alpha) >>> 8) + (((0xff0000 & src_delta * (color & 0xff00) | src_delta * (color & 0xff00ff) & 0xff00ff00) >>> 8));
                    }
                    src_alpha = 0xff;
                }
                if (glb_alpha > 0)
                    src_alpha = (src_alpha * (glb_alpha + 1)) >>> 8;

                if (src_alpha != 0)
                {
                    if (src_alpha == 0xff)
                        dest[dest_off] = src & 0xffffff;

                    else
                    {
                        dst = dest[dest_off];
                        src_delta = 0xff - src_alpha;
                        dest[dest_off] = (((0xff00ff00 & (0xff00ff & src) * src_alpha | 0xff0000 & (src & 0xff00) * src_alpha) >>> 8) + (((0xff0000 & src_delta * (dst & 0xff00) | src_delta * (dst & 0xff00ff) & 0xff00ff00) >>> 8))) & 0xffffff;
                    }
                }
            }
            dest_off++;
            rgb += j7;
            loops += l7;

            src = texture[(loops & 0x3f80) + (rgb >> 7)];
            src_alpha = src >>> 24;
            if (src_alpha != 0 || force)
            {
                if (src_alpha != 0xff && color >= 0)
                {
                    if (src_alpha == 0)
                        src = color;

                    else
                    {
                        src_delta = 0xff - src_alpha;
                        src = ((0xff00ff00 & (0xff00ff & src) * src_alpha | 0xff0000 & (src & 0xff00) * src_alpha) >>> 8) + (((0xff0000 & src_delta * (color & 0xff00) | src_delta * (color & 0xff00ff) & 0xff00ff00) >>> 8));
                    }
                    src_alpha = 0xff;
                }
                if (glb_alpha > 0)
                    src_alpha = (src_alpha * (glb_alpha + 1)) >>> 8;

                if (src_alpha != 0)
                {
                    if (src_alpha == 0xff)
                        dest[dest_off] = src & 0xffffff;

                    else
                    {
                        dst = dest[dest_off];
                        src_delta = 0xff - src_alpha;
                        dest[dest_off] = (((0xff00ff00 & (0xff00ff & src) * src_alpha | 0xff0000 & (src & 0xff00) * src_alpha) >>> 8) + (((0xff0000 & src_delta * (dst & 0xff00) | src_delta * (dst & 0xff00ff) & 0xff00ff00) >>> 8))) & 0xffffff;
                    }
                }
            }
            dest_off++;
            rgb += j7;
            loops += l7;

            src = texture[(loops & 0x3f80) + (rgb >> 7)];
            src_alpha = src >>> 24;
            if (src_alpha != 0 || force)
            {
                if (src_alpha != 0xff && color >= 0)
                {
                    if (src_alpha == 0)
                        src = color;

                    else
                    {
                        src_delta = 0xff - src_alpha;
                        src = ((0xff00ff00 & (0xff00ff & src) * src_alpha | 0xff0000 & (src & 0xff00) * src_alpha) >>> 8) + (((0xff0000 & src_delta * (color & 0xff00) | src_delta * (color & 0xff00ff) & 0xff00ff00) >>> 8));
                    }
                    src_alpha = 0xff;
                }
                if (glb_alpha > 0)
                    src_alpha = (src_alpha * (glb_alpha + 1)) >>> 8;

                if (src_alpha != 0)
                {
                    if (src_alpha == 0xff)
                        dest[dest_off] = src & 0xffffff;

                    else
                    {
                        dst = dest[dest_off];
                        src_delta = 0xff - src_alpha;
                        dest[dest_off] = (((0xff00ff00 & (0xff00ff & src) * src_alpha | 0xff0000 & (src & 0xff00) * src_alpha) >>> 8) + (((0xff0000 & src_delta * (dst & 0xff00) | src_delta * (dst & 0xff00ff) & 0xff00ff00) >>> 8))) & 0xffffff;
                    }
                }
            }
            dest_off++;
            rgb += j7;
            loops += l7;

            src = texture[(loops & 0x3f80) + (rgb >> 7)];
            src_alpha = src >>> 24;
            if (src_alpha != 0 || force)
            {
                if (src_alpha != 0xff && color >= 0)
                {
                    if (src_alpha == 0)
                        src = color;

                    else
                    {
                        src_delta = 0xff - src_alpha;
                        src = ((0xff00ff00 & (0xff00ff & src) * src_alpha | 0xff0000 & (src & 0xff00) * src_alpha) >>> 8) + (((0xff0000 & src_delta * (color & 0xff00) | src_delta * (color & 0xff00ff) & 0xff00ff00) >>> 8));
                    }
                    src_alpha = 0xff;
                }
                if (glb_alpha > 0)
                    src_alpha = (src_alpha * (glb_alpha + 1)) >>> 8;

                if (src_alpha != 0)
                {
                    if (src_alpha == 0xff)
                        dest[dest_off] = src & 0xffffff;

                    else
                    {
                        dst = dest[dest_off];
                        src_delta = 0xff - src_alpha;
                        dest[dest_off] = (((0xff00ff00 & (0xff00ff & src) * src_alpha | 0xff0000 & (src & 0xff00) * src_alpha) >>> 8) + (((0xff0000 & src_delta * (dst & 0xff00) | src_delta * (dst & 0xff00ff) & 0xff00ff00) >>> 8))) & 0xffffff;
                    }
                }
            }
            dest_off++;
            rgb += j7;
            loops += l7;

            src = texture[(loops & 0x3f80) + (rgb >> 7)];
            src_alpha = src >>> 24;
            if (src_alpha != 0 || force)
            {
                if (src_alpha != 0xff && color >= 0)
                {
                    if (src_alpha == 0)
                        src = color;

                    else
                    {
                        src_delta = 0xff - src_alpha;
                        src = ((0xff00ff00 & (0xff00ff & src) * src_alpha | 0xff0000 & (src & 0xff00) * src_alpha) >>> 8) + (((0xff0000 & src_delta * (color & 0xff00) | src_delta * (color & 0xff00ff) & 0xff00ff00) >>> 8));
                    }
                    src_alpha = 0xff;
                }
                if (glb_alpha > 0)
                    src_alpha = (src_alpha * (glb_alpha + 1)) >>> 8;

                if (src_alpha != 0)
                {
                    if (src_alpha == 0xff)
                        dest[dest_off] = src & 0xffffff;

                    else
                    {
                        dst = dest[dest_off];
                        src_delta = 0xff - src_alpha;
                        dest[dest_off] = (((0xff00ff00 & (0xff00ff & src) * src_alpha | 0xff0000 & (src & 0xff00) * src_alpha) >>> 8) + (((0xff0000 & src_delta * (dst & 0xff00) | src_delta * (dst & 0xff00ff) & 0xff00ff00) >>> 8))) & 0xffffff;
                    }
                }
            }
            dest_off++;
            rgb += j7;
            loops += l7;

            src = texture[(loops & 0x3f80) + (rgb >> 7)];
            src_alpha = src >>> 24;
            if (src_alpha != 0 || force)
            {
                if (src_alpha != 0xff && color >= 0)
                {
                    if (src_alpha == 0)
                        src = color;

                    else
                    {
                        src_delta = 0xff - src_alpha;
                        src = ((0xff00ff00 & (0xff00ff & src) * src_alpha | 0xff0000 & (src & 0xff00) * src_alpha) >>> 8) + (((0xff0000 & src_delta * (color & 0xff00) | src_delta * (color & 0xff00ff) & 0xff00ff00) >>> 8));
                    }
                    src_alpha = 0xff;
                }
                if (glb_alpha > 0)
                    src_alpha = (src_alpha * (glb_alpha + 1)) >>> 8;

                if (src_alpha != 0)
                {
                    if (src_alpha == 0xff)
                        dest[dest_off] = src & 0xffffff;

                    else
                    {
                        dst = dest[dest_off];
                        src_delta = 0xff - src_alpha;
                        dest[dest_off] = (((0xff00ff00 & (0xff00ff & src) * src_alpha | 0xff0000 & (src & 0xff00) * src_alpha) >>> 8) + (((0xff0000 & src_delta * (dst & 0xff00) | src_delta * (dst & 0xff00ff) & 0xff00ff00) >>> 8))) & 0xffffff;
                    }
                }
            }
            dest_off++;
            //rgb += j7;
            //loops += l7;

            rgb = j4;
            loops = l4;
            arg7 += arg10;
            arg8 += arg11;
            arg9 += arg12;
            int i6 = arg9 >> 14;
            if (i6 != 0) {
                j4 = arg7 / i6;
                l4 = arg8 / i6;
                if (j4 < 7) {
                    j4 = 7;
                } else if (j4 > 16256) {
                    j4 = 16256;
                }
            }
            j7 = j4 - rgb >> 3;
            l7 = l4 - loops >> 3;
            shadeValue += j3;
            rgb += shadeValue & 0x600000;
        }
        for (k3 = end_x - start_x & 7; k3-- > 0; ) {
            src = texture[(loops & 0x3f80) + (rgb >> 7)];
            src_alpha = src >>> 24;
            if (src_alpha != 0 || force)
            {
                if (src_alpha != 0xff && color >= 0)
                {
                    if (src_alpha == 0)
                        src = color;

                    else
                    {
                        src_delta = 0xff - src_alpha;
                        src = ((0xff00ff00 & (0xff00ff & src) * src_alpha | 0xff0000 & (src & 0xff00) * src_alpha) >>> 8) + (((0xff0000 & src_delta * (color & 0xff00) | src_delta * (color & 0xff00ff) & 0xff00ff00) >>> 8));
                    }
                    src_alpha = 0xff;
                }
                if (glb_alpha > 0)
                    src_alpha = (src_alpha * (glb_alpha + 1)) >>> 8;

                if (src_alpha != 0)
                {
                    if (src_alpha == 0xff)
                        dest[dest_off] = src & 0xffffff;

                    else
                    {
                        dst = dest[dest_off];
                        src_delta = 0xff - src_alpha;
                        dest[dest_off] = (((0xff00ff00 & (0xff00ff & src) * src_alpha | 0xff0000 & (src & 0xff00) * src_alpha) >>> 8) + (((0xff0000 & src_delta * (dst & 0xff00) | src_delta * (dst & 0xff00ff) & 0xff00ff00) >>> 8))) & 0xffffff;
                    }
                }
            }
            dest_off++;
            rgb += j7;
            loops += l7;
        }

    }


    public static int loadedTextureCount;
    public static boolean lowMem = false;
    public static boolean textureOutOfDrawingBounds;
    private static boolean aBoolean1463;
    public static boolean aBoolean1464 = true;
    public static int alpha;
    public static int originViewX;
    public static int originViewY;
    private static int[] anIntArray1468;
    public static final int[] anIntArray1469;
    public static int anIntArray1470[];
    public static int COSINE[];
    public static int scanOffsets[];
    private static int textureCount;
    public static Texture textures[] = new Texture[loadedTextureCount];
    private static boolean[] textureIsTransparant = new boolean[loadedTextureCount];
    private static int[] averageTextureColours = new int[loadedTextureCount];
    private static int textureTexelPoolPointer;
    private static int[][] texelArrayPool;
    private static int[][] texelCache = new int[loadedTextureCount][];
    public static int textureLastUsed[] = new int[loadedTextureCount];
    public static int textureGetCount;
    public static int hslToRgb[] = new int[0x10000];
    private static int[][] currentPalette = new int[loadedTextureCount][];

    static {
        anIntArray1468 = new int[512];
        anIntArray1469 = new int[2048];
        anIntArray1470 = new int[2048];
        COSINE = new int[2048];
        for (int i = 1; i < 512; i++) {
            anIntArray1468[i] = 32768 / i;
        }
        for (int j = 1; j < 2048; j++) {
            anIntArray1469[j] = 0x10000 / j;
        }
        for (int k = 0; k < 2048; k++) {
            anIntArray1470[k] = (int) (65536D * Math.sin((double) k * 0.0030679614999999999D));
            COSINE[k] = (int) (65536D * Math.cos((double) k * 0.0030679614999999999D));
        }
    }
}