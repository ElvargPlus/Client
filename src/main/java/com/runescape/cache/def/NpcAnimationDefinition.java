package com.runescape.cache.def;

import com.runescape.cache.FileArchive;
import com.runescape.io.Buffer;

public final class NpcAnimationDefinition {
	private NpcAnimationDefinition() {
		anInt3275 = -1;
		anInt3253 = -1;
		runAnimIndex = -1;
		anInt3282 = -1;
		anInt3287 = -1;
		walkingAnimation = -1;
		anInt3260 = -1;
		anInt3286 = -1;
		aBoolean3267 = true;
		anInt3290 = -1;
		anInt3256 = -1;
		anInt3295 = -1;
		anInt3292 = -1;
		rotate90CCWAnimIndex = -1;
		idleAnimation = -1;
		anInt3299 = -1;
		anInt3298 = -1;
		anInt3270 = -1;
		anInt3274 = -1;
		rotate90CWAnimIndex = -1;
		rotate180AnimIndex = -1;
		anInt3301 = -1;
		anInt3293 = -1;
		anInt3305 = -1;
		anInt3303 = -1;
		anInt3304 = -1;
		anInt3271 = -1;
	}

	public int[][] anIntArrayArray3249;
	public int anInt3250;
	public int runAnimIndex;
	public int anInt3253;
	public int[] anIntArray3255;
	public int anInt3256;
	public int anInt3258;
	public int idleAnimation;
	public int anInt3260;
	public int anInt3261;
	public int rotate180AnimIndex;
	public int anInt3263;
	public int anInt3266;
	public boolean aBoolean3267;
	public int rotate90CCWAnimIndex;
	public int anInt3270;
	public int anInt3271;
	public int anInt3272;
	public int[][] anIntArrayArray3273;
	public int anInt3274;
	public int anInt3275;
	public int[] anIntArray3276;
	public int walkingAnimation;
	public int anInt3278;
	public int anInt3281;
	public int anInt3282;
	public int anInt3283;
	public int anInt3284;
	public int anInt3285;
	public int anInt3286;
	public int anInt3287;
	public int anInt3289;
	public int anInt3290;
	public int anInt3291;
	public int anInt3292;
	public int anInt3293;
	public int[] anIntArray3294;
	public int anInt3295;
	public int rotate90CWAnimIndex;
	public int anInt3298;
	public int anInt3299;
	public int anInt3301;
	public int[] anIntArray3302;
	public int anInt3303;
	public int anInt3304;
	public int anInt3305;

	private NpcAnimationDefinition(Buffer buffer) {
		this();
		while (true)
		{
			int opcode = buffer.readUnsignedByte();
			if (opcode == 0)
				break;

			if (opcode == 1)
			{
				idleAnimation = buffer.readUShort();
				walkingAnimation = buffer.readUShort();
				if (idleAnimation == -1)
					idleAnimation = -1;

				if (walkingAnimation == -1)
					walkingAnimation = -1;

			}
			else if (opcode == 2)
				rotate180AnimIndex = buffer.readUShort();
			else if (opcode == 3)
				rotate90CWAnimIndex = buffer.readUShort();
			else if (opcode == 4)
				rotate90CCWAnimIndex = buffer.readUShort();
			else if (opcode == 5)
				anInt3304 = buffer.readUShort();
			else if (opcode == 6)
				runAnimIndex = buffer.readUShort();
			else if (opcode == 7)
				anInt3271 = buffer.readUShort();
			else if (opcode == 8)
				anInt3270 = buffer.readUShort();
			else if (opcode == 9)
				anInt3293 = buffer.readUShort();

			else if (opcode == 26)
			{
				anInt3261 = (short) (4 * buffer.readUnsignedByte());
				anInt3266 = (short) (4 * buffer.readUnsignedByte());
			}
			else if (opcode == 27)
			{
				if (anIntArrayArray3273 == null)
					anIntArrayArray3273 = new int[256][];

				int index = buffer.readUnsignedByte();
				anIntArrayArray3273[index] = new int[6];
				for (int i = 0; i != 6; ++i)
					anIntArrayArray3273[index][i] = buffer.readShort();

			}
			else if (opcode == 28)
			{
				int count = buffer.readUShort();
				anIntArray3276 = new int[count];
				for (int i = 0; i != count; ++i)
				{
					anIntArray3276[i] = buffer.readUnsignedByte();
					if (anIntArray3276[i] == 0xff)
						anIntArray3276[i] = -1;

				}

			}
			else if (opcode == 29)
				anInt3258 = buffer.readUnsignedByte();
			else if (opcode == 30)
				anInt3283 = buffer.readUShort();
			else if (opcode == 31)
				anInt3278 = buffer.readUnsignedByte();
			else if (opcode == 32)
				anInt3284 = buffer.readUShort();
			else if (opcode == 33)
				anInt3250 = buffer.readShort();
			else if (opcode == 35)
				anInt3289 = buffer.readUShort();
			else if (opcode == 36)
				anInt3285 = buffer.readShort();
			else if (opcode == 37)
				anInt3256 = buffer.readUnsignedByte();
			else if (opcode == 38)
				anInt3299 = buffer.readUShort();
			else if (opcode == 39)
				anInt3274 = buffer.readUShort();
			else if (opcode == 40)
				anInt3286 = buffer.readUShort();
			else if (opcode == 41)
				anInt3301 = buffer.readUShort();
			else if (opcode == 42)
				anInt3287 = buffer.readUShort();
			else if (opcode == 43)
				anInt3290 = buffer.readUShort();
			else if (opcode == 44)
				anInt3292 = buffer.readUShort();
			else if (opcode == 45)
				anInt3303 = buffer.readUShort();
			else if (opcode == 46)
				anInt3275 = buffer.readUShort();
			else if (opcode == 47)
				anInt3260 = buffer.readUShort();
			else if (opcode == 48)
				anInt3282 = buffer.readUShort();
			else if (opcode == 49)
				anInt3253 = buffer.readUShort();
			else if (opcode == 50)
				anInt3298 = buffer.readUShort();
			else if (opcode == 51)
				anInt3305 = buffer.readUShort();

			else if (opcode == 52)
			{
				int count = buffer.readUnsignedByte();
				anIntArray3294 = new int[count];
				anIntArray3302 = new int[count];
				for (int i = 0; i != count; ++i)
				{
					anIntArray3294[i] = buffer.readUShort();
					int alpha = buffer.readUnsignedByte();
					anIntArray3302[i] = alpha;
					anInt3281 += alpha;
				}

			}
			else if (opcode == 53)
				aBoolean3267 = false;

			else if (opcode == 54)
			{
				anInt3263 = buffer.readUnsignedByte() << 6;
				anInt3291 = buffer.readUnsignedByte() << 6;
			}
			else if (opcode == 55)
			{
				if (anIntArray3255 == null)
					anIntArray3255 = new int[256];

				int index = buffer.readUnsignedByte();
				anIntArray3255[index] = buffer.readUShort();
			}
			else if (opcode == 56)
			{
				if (anIntArrayArray3249 == null)
					anIntArrayArray3249 = new int[256][];

				int index = buffer.readUnsignedByte();
				anIntArrayArray3249[index] = new int[3];
				for (int i = 0; i != 3; ++i)
					anIntArrayArray3249[index][i] = buffer.readShort();

			}
			else
			{
				System.out.println("[NPCAnimDef] Unknown opcode: " + opcode);
				break;
			}
		}
	}

	public static void init(FileArchive configArchive)
	{
		Buffer buffer = new Buffer(configArchive.readFile("npcanim.dat"));
		int count = buffer.readUShort();
		defs = new NpcAnimationDefinition[count];
		int pos = buffer.currentPosition;
		for (int i = 0; i != count; ++i) {
			buffer.currentPosition = pos;
			int size = buffer.readUShort();
			pos += 2 + size;
			if (size != 0) {
				defs[i] = new NpcAnimationDefinition(buffer);
			}

		}

	}

	public static void clear()
	{
		defs = null;
	}

	public static NpcAnimationDefinition[] defs;
}
