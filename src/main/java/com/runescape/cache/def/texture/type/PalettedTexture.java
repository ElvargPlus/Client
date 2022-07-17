package com.runescape.cache.def.texture.type;


import com.runescape.cache.def.texture.TextureLoader;
import com.runescape.io.Buffer;

public class PalettedTexture extends TextureLoader
{
	public PalettedTexture(int width, int height, Buffer buffer, boolean alpha)
	{
		super(width, height);
		int paletteSize = buffer.readUnsignedByte();
		int[] palette = this.palette = new int[1 + paletteSize];
		for (int i = 0; i != paletteSize; ++i) {
			int pixel = buffer.readMedium();
			if (!alpha) {
				pixel |= 0xff000000;
			}
			palette[1 + i] = pixel;
		}

		int count = width * height;
		byte[] indices = this.indices = new byte[count];
		for (int i = 0; i != count; ++i) {
			indices[i] = buffer.readSignedByte();
			if (!alpha && indices[i] == 0) {
				opaque = false;
			}
		}

	}

	public int getPixel(int i)
	{
		return palette[indices[i] & 0xff];
	}

	public String getType()
	{
		return "0";
	}

	public String toString()
	{
		return getType() + "	" + super.toString() + "	" + (palette.length - 1);
	}

	public final int[] palette;
	public final byte[] indices;
}