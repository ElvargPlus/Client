package com.runescape.cache.def;

import java.nio.ByteBuffer;

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
    public int luminance;
    public int anotherHue;
    public int anotherSaturation;
    public int anotherLuminance;
    public int blendHue;
    public int blendHueMultiplier;
    public int hsl16;

    private FloorDefinition() {
        texture = -1;
        occlude = true;
    }

    public static void init(FileArchive archive) {
        Buffer buffer = new Buffer(archive.readFile("flo.dat"));
        int underlayAmount = buffer.readShort();
        System.out.println("Loaded: " + underlayAmount + " underlays");
        underlays = new FloorDefinition[underlayAmount];
        for (int i = 0; i < underlayAmount; i++) {
            if (underlays[i] == null) {
                underlays[i] = new FloorDefinition();
            }
            underlays[i].readValuesUnderlay(buffer);
        }

        ByteBuffer buffer1 = ByteBuffer.wrap(archive.readFile("flo2.dat"));
        int overlayAmount = buffer1.getShort();
        System.out.println("Loaded: " + overlayAmount + " overlays");
        overlays = new FloorDefinition[overlayAmount];
        for (int i = 0; i < overlayAmount; i++) {
            if (overlays[i] == null) {
                overlays[i] = new FloorDefinition();
            }
            overlays[i].readValuesOverlay(buffer1);
        }
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

    private void readValuesUnderlay(Buffer buffer) {
        do {
            int opcode = buffer.readUnsignedByte();
            if (opcode == 0)
                return;
            else if (opcode == 1) {
                rgb = buffer.readTriByte();
                rgbToHsl(rgb);
            } else if (opcode == 2)
                texture = buffer.readUnsignedByte();
            else if (opcode == 3) {
            } else if (opcode == 5)
                occlude = false;
            else if (opcode == 6)
                buffer.readString();
            else if (opcode == 7) {
                int j = hue;
                int k = saturation;
                int l = luminance;
                int i1 = blendHue;
                int j1 = buffer.readTriByte();
                rgbToHsl(j1);
                hue = j;
                saturation = k;
                luminance = l;
                blendHue = i1;
                blendHueMultiplier = i1;
            } else {
                System.out.println("Error unrecognised underlay code: " + opcode);
            }
        } while (true);
    }

    private void readValuesOverlay(ByteBuffer buffer) {
        for (; ; ) {
            int opcode = buffer.get();
            if (opcode == 0) {
                break;
            } else if (opcode == 1) {
                rgb = ((buffer.get() & 0xff) << 16) + ((buffer.get() & 0xff) << 8) + (buffer.get() & 0xff);
                rgbToHsl(rgb);
            } else if (opcode == 2) {
                texture = buffer.get() & 0xff;
            } else if (opcode == 3) {
                texture = buffer.getShort() & 0xffff;
                if (texture == 65535) {
                    texture = -1;
                }
            } else if (opcode == 4) {

            } else if (opcode == 5) {
                occlude = false;
            } else if (opcode == 6) {

            } else if (opcode == 7) {
                anotherRgb = ((buffer.get() & 0xff) << 16) + ((buffer.get() & 0xff) << 8) + (buffer.get() & 0xff);
            } else if (opcode == 8) {

            } else if (opcode == 9) {
                int int_9 = buffer.getShort() & 0xffff;
            } else if (opcode == 10) {
                boolean boolean_10 = false;
            } else if (opcode == 11) {
                int int_11 = buffer.get() & 0xff;
            } else if (opcode == 12) {
                boolean boolean_12 = true;
            } else if (opcode == 13) {
                int int_13 = ((buffer.get() & 0xff) << 16) + ((buffer.get() & 0xff) << 8) + (buffer.get() & 0xff);
            } else if (opcode == 14) {
                int int_14 = buffer.get() & 0xff;
            } else if (opcode == 15) {
                int int_15 = buffer.getShort() & 0xffff;
                if (int_15 == 65535) {
                    int_15 = -1;
                }
            } else if (opcode == 16) {
                int int_16 = buffer.get() & 0xff;
            } else {
                System.out.println("Error unrecognised overlay code: " + opcode);
            }
        }
    }
    private void rgbToHsl(int rgb) {
        double r = (rgb >> 16 & 0xff) / 256.0;
        double g = (rgb >> 8 & 0xff) / 256.0;
        double b = (rgb & 0xff) / 256.0;
        double min = r;
        if (g < min) {
            min = g;
        }
        if (b < min) {
            min = b;
        }
        double max = r;
        if (g > max) {
            max = g;
        }
        if (b > max) {
            max = b;
        }
        double h = 0.0;
        double s = 0.0;
        double l = (min + max) / 2.0;
        if (min != max) {
            if (l < 0.5) {
                s = (max - min) / (max + min);
            }
            if (l >= 0.5) {
                s = (max - min) / (2.0 - max - min);
            }
            if (r == max) {
                h = (g - b) / (max - min);
            } else if (g == max) {
                h = 2.0 + (b - r) / (max - min);
            } else if (b == max) {
                h = 4.0 + (r - g) / (max - min);
            }
        }
        h /= 6.0;
        hue = (int) (h * 256.0);
        saturation = (int) (s * 256.0);
        luminance = (int) (l * 256.0);
        if (saturation < 0) {
            saturation = 0;
        } else if (saturation > 255) {
            saturation = 255;
        }
        if (luminance < 0) {
            luminance = 0;
        } else if (luminance > 255) {
            luminance = 255;
        }
        if (l > 0.5) {
            blendHueMultiplier = (int) ((1.0 - l) * s * 512.0);
        } else {
            blendHueMultiplier = (int) (l * s * 512.0);
        }
        if (blendHueMultiplier < 1) {
            blendHueMultiplier = 1;
        }
        blendHue = (int) (h * blendHueMultiplier);
        hsl16 = hsl24to16(hue, saturation, luminance);
    }
}
