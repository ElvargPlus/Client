package com.runescape.cache.def.texture.type;


import com.runescape.cache.def.texture.TextureLoader;
import com.runescape.io.Buffer;

public final class RGBTexture extends TextureLoader {
	public RGBTexture(int width, int height, Buffer buffer) {
		super(width, height);
		int count = width * height;
		int[] pixels = this.pixels = new int[count];
		for (int i = 0; i != count; ++i) {
			int pixel = buffer.readMedium();
			if (pixel != 0) {
				pixel |= 0xff000000;
			} else {
				opaque = false;
			}
			pixels[i] = pixel;
		}

	}

	public int getPixel(int i)
	{
		return pixels[i];
	}

	public String toString()
	{
		return "1	" + super.toString();
	}

	public final int[] pixels;
}
