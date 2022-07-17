package com.runescape.cache.def.texture;


import com.runescape.Client;
import com.runescape.cache.ResourceProvider;
import com.runescape.cache.def.texture.type.ARGBTexture;
import com.runescape.cache.def.texture.type.AlphaPalettedTexture;
import com.runescape.cache.def.texture.type.PalettedTexture;
import com.runescape.cache.def.texture.type.RGBTexture;
import com.runescape.draw.Rasterizer3D;
import com.runescape.io.Buffer;

public class TextureLoader {

	public TextureLoader(int width, int height) {
		this.width = width;
		this.height = height;
		opaque = true;
	}

	public int getPixel(int i)
	{
		return 0xffffffff;
	}


	public static TextureLoader get(int id) {
		if (id < 0 || id >= textures.length) {
			return null;
		}

		if (loaded[id]) {
			return textures[id];
		}

		Client.instance.resourceProvider.loadMandatory(4, id);
		return null;
	}

	public static void init() {
		textures = new TextureLoader[1430];
		loaded = new boolean[1430];
		Rasterizer3D.initTextures(1430);
	}

	public static void load(int id, byte[] data) {
		loaded[id] = true;
		if (data != null && data.length >= 5) {
			Buffer buffer = new Buffer(data);
			int type = buffer.readUnsignedByte();
			int width = buffer.readUShort();
			int height = buffer.readUShort();
			switch(type) {
				case 0:
					textures[id] = new PalettedTexture(width, height, buffer, false);
				case 1:
					textures[id] = new RGBTexture(width, height, buffer);
				case 2:
					textures[id] = new AlphaPalettedTexture(width, height, buffer);
				case 3:
					textures[id] = new ARGBTexture(width, height, buffer);
			}
		}
	}

	public String toString() {
		return width + " X " + height + "	" + (opaque ? "+opaque":"-opaque") + "	" + (hasAlpha ? "+alpha":"-alpha");
	}

	public static void clear() {
		loaded = null;
		textures = null;
	}

	public boolean opaque;
	public boolean hasAlpha;
	public final int width;
	public final int height;
	private static boolean[] loaded;
	private static TextureLoader[] textures;


}
