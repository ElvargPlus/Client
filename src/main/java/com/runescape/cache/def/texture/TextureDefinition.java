package com.runescape.cache.def.texture;


import com.runescape.cache.FileArchive;
import com.runescape.io.Buffer;

public final class TextureDefinition {

	public static void unpackConfig(FileArchive configArchive) {
		Buffer buffer = new Buffer(configArchive.readFile("texture.dat"));
		int count = buffer.readUShort();
		textures = new TextureDefinition[count];
		System.out.println("Textures Read -> " + count);

		for (int i = 0; i != count; ++i) {
			if (buffer.readUnsignedByte() == 1) {
				textures[i] = new TextureDefinition();
				textures[i].aBoolean1223 = buffer.readUnsignedByte() == 1;
				textures[i].aBoolean1204 = buffer.readUnsignedByte() == 1;
				textures[i].aBoolean1205 = buffer.readUnsignedByte() == 1;
				textures[i].aByte1217 = buffer.readSignedByte();
				textures[i].aByte1225 = buffer.readSignedByte();
				textures[i].aByte1214 = buffer.readSignedByte();
				textures[i].aByte1213 = buffer.readSignedByte();
				textures[i].aShort1221 = (short) buffer.readUShort();
				textures[i].aByte1211 = buffer.readSignedByte();
				textures[i].aByte1203 = buffer.readSignedByte();
				textures[i].aBoolean1222 = buffer.readUnsignedByte() == 1;
				textures[i].aBoolean1216 = buffer.readUnsignedByte() == 1;
				textures[i].aByte1207 = buffer.readSignedByte();
				textures[i].aBoolean1212 = buffer.readUnsignedByte() == 1;
				textures[i].aBoolean1210 = buffer.readUnsignedByte() == 1;
				textures[i].aBoolean1215 = buffer.readUnsignedByte() == 1;
				textures[i].anInt1202 = buffer.readUnsignedByte();
				textures[i].anInt1206 = buffer.readInt();
				textures[i].anInt1226 = buffer.readUnsignedByte();
			}
		}

	}

	public static void clear() {
		textures = null;
	}

	public boolean aBoolean1223;
	public boolean aBoolean1204;
	public boolean aBoolean1205;
	public byte aByte1217;
	public byte aByte1225;
	public byte aByte1214;
	public byte aByte1213;
	public short aShort1221;
	public byte aByte1211;
	public byte aByte1203;
	public boolean aBoolean1222;
	public boolean aBoolean1216;
	public byte aByte1207;
	public boolean aBoolean1212;
	public boolean aBoolean1210;
	public boolean aBoolean1215;
	public int anInt1202;
	public int anInt1206;
	public int anInt1226;
	public static TextureDefinition[] textures;
}
