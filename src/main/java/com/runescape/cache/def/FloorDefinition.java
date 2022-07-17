package com.runescape.cache.def;

import com.runescape.cache.FileArchive;
import com.runescape.io.Buffer;

public class FloorDefinition {

    public static FloorDefinition[] overlays;
    public static FloorDefinition[] underlays;
    public int texture;
    public int rgb;
    public boolean occlude;
    public int anotherRgb;
    public int hue;
    public int saturation;
    public int textureResolution;
    public boolean blockShadow;

    public int hue2;
    public int ambient;
    public int hueDivisor;


    private FloorDefinition() {
        texture = -1;
        occlude = true;
        textureResolution = 512;
        anotherRgb = -1;
        rgb = -1;
    }

    public static void init(FileArchive archive) {
        Buffer buffer = new Buffer(archive.readFile("flo.dat"));
        int underlayAmount = buffer.readShort();

        System.out.println("Underlays Read -> " + underlayAmount);
        underlays = new FloorDefinition[underlayAmount];

        for (int index = 0; index != underlayAmount; ++index) {
            if (underlays[index] == null) {
                underlays[index] = new FloorDefinition();
            }
            underlays[index].decodeUnderlay(buffer);

        }

        Buffer bufferOverlay = new Buffer(archive.readFile("flo2.dat"));
        int overlayAmount = bufferOverlay.readShort();
        System.out.println("Overlays Read -> " + overlayAmount);
        overlays = new FloorDefinition[overlayAmount];
        for (int index = 0; index < overlayAmount; index++) {
            if (overlays[index] == null) {
                overlays[index] = new FloorDefinition();
            }
            overlays[index].decodeOverlay(bufferOverlay);
        }
    }

    private void decodeUnderlay(Buffer buffer) {
        do {
            int opcode = buffer.readUnsignedByte();
            if (opcode == 0)
                return;
            else if (opcode == 1) {
                rgb = rgbToHsl(buffer.readTriByte());
            } else if (opcode == 2) {
                texture = buffer.readUShort();
                if (texture == Integer.MAX_VALUE) {
                    texture = -1;
                }
                if (texture == 594)
                {
                    texture = 1;
                    rgb = rgbToHsl(0x5d7397);
                }
                else if (texture == 512)
                {
                    texture = 505;
                    rgb = rgbToHsl(0x897b5f);
                }
            } else if (opcode == 3) {
                textureResolution = buffer.readUShort();
            } else if (opcode == 4) {
                blockShadow = false;
            } else if (opcode == 5) {
                occlude = false;
            } else {
                System.out.println("Error unrecognised underlay code: " + opcode);
            }
        } while (true);
    }

    private void decodeOverlay(Buffer buffer) {
        for (; ; ) {
            int opcode = buffer.readUnsignedByte();
            if (opcode == 0) {
                break;
            } else if (opcode == 1) {
                rgb = rgbToHsl(buffer.readTriByte());
            } else if (opcode == 2) {
                texture = buffer.readShort();
                if (texture == Integer.MAX_VALUE) {
                    texture = -1;
                }
            } else if (opcode == 3) {
                texture = buffer.readShort();
            } else if (opcode == 5) {
                occlude = false;
            } else if (opcode == 7) {
                anotherRgb = rgbToHsl(buffer.readTriByte());
            } else if (opcode == 9) {
                textureResolution = buffer.readShort();
            } else if (opcode == 10) {
                boolean aBoolean3638 = false;
            } else if (opcode == 11) {
                int anInt3633 = buffer.readUnsignedByte();
            } else if (opcode == 12) {
                boolean aBoolean3643 = true;
            } else if (opcode == 13) {
                int anInt3646 = buffer.readTriByte();
            } else if (opcode == 14) {
                int int_14 = buffer.readShort();
            } else if (opcode == 16) {
                int anInt3641 = buffer.readUnsignedByte();
            } else {
                System.out.println("Error unrecognised overlay code: " + opcode);
            }
        }
    }

    private int rgbToHsl(int color)
    {
        return color != 0xff00ff ? convertToHSL(color) : -1;
    }

    private int convertToHSL(int i)
    {
        double d = (double) (i >> 16 & 0xff) / 256D;
        double d1 = (double) (i >> 8 & 0xff) / 256D;
        double d2 = (double) (i & 0xff) / 256D;

        double d3 = d;
        if (d1 < d3)
            d3 = d1;

        if (d2 < d3)
            d3 = d2;

        double d4 = d;
        if (d1 > d4)
            d4 = d1;

        if (d2 > d4)
            d4 = d2;

        double d5 = 0.0D;
        double d6 = 0.0D;
        double d7 = (d3 + d4) / 2D;
        if (d3 != d4)
        {
            if (d7 < 0.5D)
                d6 = (d4 - d3) / (d4 + d3);

            if (d7 >= 0.5D)
                d6 = (d4 - d3) / (2D - d4 - d3);

            if (d == d4)
                d5 = (d1 - d2) / (d4 - d3);
            else if (d1 == d4)
                d5 = 2D + (d2 - d) / (d4 - d3);
            else if (d2 == d4)
                d5 = 4D + (d - d1) / (d4 - d3);

        }
        d5 /= 6D;
        hue2 = (int) (d5 * 256D);
        saturation = (int) (d6 * 256D);
        ambient = (int) (d7 * 256D);
        if (saturation < 0)
            saturation = 0;
        else if (saturation > 255)
            saturation = 255;

        if (ambient < 0)
            ambient = 0;
        else if (ambient > 255)
            ambient = 255;

        if (d7 > 0.5D)
            hueDivisor = (int) ((1.0D - d7) * d6 * 512D);
        else
            hueDivisor = (int) (d7 * d6 * 512D);

        if (hueDivisor < 1)
            hueDivisor = 1;

        hue = (int) (d5 * (double) hueDivisor);
        int k = hue2;
        if (k < 0)
            k = 0;
        else if (k > 255)
            k = 255;

        int l = saturation;
        if (l < 0)
            l = 0;
        else if (l > 255)
            l = 255;

        int i1 = ambient;
        if (i1 < 0)
            i1 = 0;
        else if (i1 > 255)
            i1 = 255;

        return hsl24to16(k, l, i1);
    }

    private final static int hsl24to16(int h, int s, int l) {
        if (l > 179) {
            s /= 2;
        }
        if (l > 192) {
            s /= 2;
        }
        if (l > 217) {
            s /= 2;
        }
        if (l > 243) {
            s /= 2;
        }
        return (h / 4 << 10) + (s / 32 << 7) + l / 2;
    }


}
