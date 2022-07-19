package com.runescape.cache.def;

import com.runescape.Client;
import com.runescape.cache.FileArchive;
import com.runescape.cache.anim.Frame;
import com.runescape.cache.config.VariableBits;
import com.runescape.collection.ReferenceCache;
import com.runescape.entity.model.Model;
import com.runescape.io.Buffer;
import net.runelite.api.HeadIcon;
import net.runelite.api.IterableHashTable;
import net.runelite.rs.api.RSIterableNodeHashTable;
import net.runelite.rs.api.RSNPCComposition;

import java.util.Hashtable;
import java.util.Map;

public final class NpcDefinition implements RSNPCComposition {


	public static int anInt56;
	public static Buffer dataBuf;
	public static int[] offsets;
	public static NpcDefinition[] cache;
	public static Client clientInstance;
	public static ReferenceCache modelCache = new ReferenceCache(30);
	public final int anInt64;
	public int rotate90CCWAnimIndex;
	public int varbitId;
	public int rotate180AnimIndex;
	public int varpIndex;
	public int combatLevel;
	public String name;
	public String actions[];
	public int walkingAnimation;
	public int size;
	public int runAnimIndex;
	public int anInt67;
	public int anInt58;
	public int anInt2803;
	public boolean isInteractable;
	public int anInt2804;
	public int anInt2809;
	public int anInt2810;
	public int anInt2812;
	public int anInt2814;
	public int anInt2815;
	public byte aByte2816;
	public boolean aBoolean2825;
	public int anInt2826;
	public int anInt2828;
	public int anInt2831;
	public int anInt2833;
	public byte aByte2836;
	public int anInt2837;
	public byte aByte2839;
	public boolean aBoolean2843;
	public int anInt2844;
	public int anInt2849;
	public int anInt2852;
	public byte aByte2853;
	public byte aByte2855;
	public int anInt2856;
	public byte aByte2857;
	public int anInt2859;
	public int idleAnimation;
	public int anInt2860;
	public int anInt2862;
	public short aShort2863;
	public int anInt2864;
	public byte aByte2868;
	public short aShort2871;
	public byte aByte2873;
	public byte aByte2877;
	public int anInt2878;
	public boolean aBoolean2883;
	public int anInt2886;
	public byte[] aByteArray2820;
	public int[] anIntArray2832;
	public int[][] anIntArrayArray2842;
	public int[] recolorToReplace;
	public int[] chatheadModels;
	public int headIcon;
	public int[] recolorToFind;
	public int standingAnimation;
	public long interfaceType;
	public int rotationSpeed;
	public boolean isPet = false;
	public int rotate90CWAnimIndex;
	public boolean clickable;
	public int ambient;
	public int heightScale;
	public boolean isMinimapVisible;
	public int configs[];
	public boolean rotationFlag = true;
	public int rotateLeftAnimation = -1;
	public int rotateRightAnimation = -1;
	private int category;
	public short[] textureReplace;
	public short[] textureFind;
	public String description;
	public int widthScale;
	public int contrast;
	public boolean priorityRender;
	public int[] models;
	public int id;
	private Map<Integer, Object> params = null;

	public NpcDefinition() {
		rotate90CCWAnimIndex = -1;
		varbitId = -1;
		rotate180AnimIndex = -1;
		varpIndex = -1;
		combatLevel = -1;
		anInt64 = 1834;
		walkingAnimation = -1;
		size = 1;
		headIcon = -1;
		standingAnimation = -1;
		interfaceType = -1L;
		rotationSpeed = 32;
		rotate90CWAnimIndex = -1;
		clickable = true;
		heightScale = 128;
		isMinimapVisible = true;
		widthScale = 128;
		priorityRender = false;

		description = null;
		anInt58 = -1;
		anInt67 = -1;

		params = null;
		aByte2836 = 0;
		aByte2853 = 0;
		aByte2857 = 0;
		aBoolean2883 = false;

		anInt2814 = -1;
		aByteArray2820 = null;
		anIntArray2832 = null;

		anIntArrayArray2842 = null;
		models = null;
		anInt2809 = -1;
		anInt2803 = -1;
		anInt2804 = -1;
		aBoolean2843 = false;
		anInt2833 = -1;
		aBoolean2825 = false;
		anInt2826 = -1;
		aByte2855 = (byte) -1;
		aByte2816 = (byte) 0;
		aByte2839 = (byte) 0;
		anInt2812 = -1;
		anInt2844 = 256;
		isInteractable = true;
		anInt2810 = -1;
		anInt2852 = 256;
		anInt2860 = -1;
		anInt2859 = -1;
		anInt2831 = 0;
		anInt2837 = -1;
		anInt2862 = 0;
		aShort2863 = (short) 0;
		anInt2864 = 0;
		aByte2868 = (byte) -16;
		ambient = 0;
		anInt2849 = -1;
		anInt2856 = -1;
		aShort2871 = (short) 0;
		anInt2828 = 255;
		aByte2877 = (byte) -96;
		anInt2878 = -1;
		aByte2873 = (byte) 4;
		anInt2815 = -1;
		anInt2886 = -1;
		runAnimIndex = -1;

	}

	/**
	 * Lookup an NpcDefinition by its id
	 *
	 * @param id
	 */
	public static NpcDefinition lookup(int id) {
		for (int index = 0; index < 50; index++)
			if (cache[index].interfaceType == (long) id)
				return cache[index];

		anInt56 = (anInt56 + 1) % 50;
		NpcDefinition definition = cache[anInt56] = new NpcDefinition();
		if (id >= 0 && id < TOTAL_NPCS && offsets[id] != -1) {
			dataBuf.currentPosition = offsets[id];
			definition.interfaceType = id;
			definition.id = id;
			definition.decode(dataBuf);
		}

		return definition;
	}

	public static int TOTAL_NPCS;

	public static void init(FileArchive archive) {
        dataBuf = new Buffer(archive.readFile("npc.dat"));
        Buffer idxBuf = new Buffer(archive.readFile("npc.idx"));

		int size = archive.readFile("npc.idx").length / 2;
		TOTAL_NPCS = size;

		System.out.println("Npcs Read -> " + TOTAL_NPCS);

		offsets = new int[size];

		int offset = 2;

		for (int count = 0; count < size; count++) {
			offsets[count] = offset;
			offset += idxBuf.readUShort();
		}

		cache = new NpcDefinition[50];

		for (int count = 0; count < 50; count++) {
			cache[count] = new NpcDefinition();
		}

		System.out.println("Mobs Read -> " + size);
	}

	public static void clear() {
		modelCache = null;
		offsets = null;
		cache = null;
		dataBuf = null;
	}
	public Model model() {
		if (configs != null) {
			NpcDefinition entityDef = morph();
			if (entityDef == null)
				return null;
			else
				return entityDef.model();
		}
		if (chatheadModels == null)
			return null;
		boolean flag1 = false;
		for (int index = 0; index < chatheadModels.length; index++)
			if (!Model.isCached(chatheadModels[index]))
				flag1 = true;

		if (flag1)
			return null;
		Model models[] = new Model[chatheadModels.length];
		for (int index = 0; index < chatheadModels.length; index++)
			models[index] = Model.getModel(chatheadModels[index]);

		Model model;
		if (models.length == 1)
			model = models[0];
		else
			model = new Model(models.length, models,true);
		if (recolorToFind != null) {
			for (int index = 0; index < recolorToFind.length; index++)
				model.recolor(recolorToFind[index], recolorToReplace[index]);

		}
		return model;
	}

	public NpcDefinition morph() {
		int child = -1;
		if (varbitId != -1) {
			VariableBits varBit = VariableBits.varbits[varbitId];
			int variable = varBit.getSetting();
			int low = varBit.getLow();
			int high = varBit.getHigh();
			int mask = Client.BIT_MASKS[high - low];
			child = clientInstance.settings[variable] >> low & mask;
		} else if (varpIndex != -1)
			child = clientInstance.settings[varpIndex];
		if (child < 0 || child >= configs.length || configs[child] == -1)
			return null;
		else
			return lookup(configs[child]);
	}

	public Model method164(int j, int frame, int ai[]) {
		if (configs != null) {
			NpcDefinition entityDef = morph();
			if (entityDef == null)
				return null;
			else
				return entityDef.method164(j, frame, ai);
		}
		Model model = (Model) modelCache.get(interfaceType);
		if (model == null) {
			boolean flag = false;
			for (int i1 = 0; i1 < models.length; i1++)
				if (!Model.isCached(models[i1]))
					flag = true;

			if (flag)
				return null;
			Model models[] = new Model[this.models.length];
			for (int j1 = 0; j1 < this.models.length; j1++)
				models[j1] = Model.getModel(this.models[j1]);

			if (models.length == 1)
				model = models[0];
			else
				model = new Model(models.length, models,true);
			if (recolorToFind != null) {
				for (int k1 = 0; k1 < recolorToFind.length; k1++)
					model.recolor(recolorToFind[k1], recolorToReplace[k1]);

			}
			model.skin();
			model.scale(132, 132, 132);
			model.light(84 + ambient, 1000 + contrast, -90, -580, -90, true);
			modelCache.put(model, interfaceType);
		}
		Model empty = Model.EMPTY_MODEL;

		empty.replace(model, Frame.noAnimationInProgress(frame) & Frame.noAnimationInProgress(j));
		if (frame != -1 && j != -1)
			empty.mix(ai, j, frame);
		else if (frame != -1)
			empty.applyTransform(frame);
		if (widthScale != 128 || heightScale != 128)
			empty.scale(widthScale, widthScale, heightScale);
		empty.calc_diagonals();
		empty.faceGroups = null;
		empty.vertexGroups = null;
		if (size == 1)
			empty.fits_on_single_square = true;
		return empty;
	}

	public Model getAnimatedModel(int primaryFrame, int secondaryFrame, int interleaveOrder[]) {
		if (configs != null) {
			NpcDefinition definition = morph();
			if (definition == null)
				return null;
			else
				return definition.getAnimatedModel(primaryFrame, secondaryFrame, interleaveOrder);
		}
		Model model = (Model) modelCache.get(interfaceType);
		if (model == null) {
			boolean flag = false;
			for (int index = 0; index < models.length; index++)
				if (!Model.isCached(models[index]))
					flag = true;
			if (flag) {
				return null;
			}
			Model models[] = new Model[this.models.length];
			for (int index = 0; index < this.models.length; index++)
				models[index] = Model.getModel(this.models[index]);

			if (models.length == 1)
				model = models[0];
			else
				model = new Model(models.length, models,true);
			if (recolorToFind != null) {
				for (int index = 0; index < recolorToFind.length; index++)
					model.recolor(recolorToFind[index], recolorToReplace[index]);

			}
			model.skin();
			model.light(64 + ambient, 850 + contrast, -30, -50, -30, true);
			modelCache.put(model, interfaceType);
		}
		Model model_1 = Model.EMPTY_MODEL;
		model_1.replace(model,
				Frame.noAnimationInProgress(secondaryFrame) & Frame.noAnimationInProgress(primaryFrame));
		if (secondaryFrame != -1 && primaryFrame != -1)
			model_1.mix(interleaveOrder, primaryFrame, secondaryFrame);
		else if (secondaryFrame != -1)
			model_1.applyTransform(secondaryFrame);
		if (widthScale != 128 || heightScale != 128)
			model_1.scale(widthScale, widthScale, heightScale);
		model_1.calc_diagonals();
		model_1.faceGroups = null;
		model_1.vertexGroups = null;
		if (size == 1)
			model_1.fits_on_single_square = true;
		return model_1;
	}
	private void decode(Buffer buffer)
	{
		while (true)
		{
			int opcode = buffer.readUnsignedByte();
			if (opcode == 0)
				break;

			if (opcode == 1)
			{
				int count = buffer.readUnsignedByte();
				models = new int[count];
				for (int i = 0; i != count; ++i)
				{
					models[i] = buffer.readUShort();
					if (models[i] == 65535)
						models[i] = -1;

				}

			}
			else if (opcode == 2)
				name = buffer.readStringJagex();
			else if (opcode == 3)
				description = buffer.readStringJagex();
			else if (opcode == 12)
				size = buffer.readSignedByte();
			else if (opcode >= 30 && opcode < 40)
				actions[opcode - 30] = buffer.readStringJagex();

			else if (opcode == 40)
			{
				int count = buffer.readUnsignedByte();
				recolorToFind = new int[count];
				recolorToReplace = new int[count];
				for (int i = 0; i != count; ++i)
				{
					recolorToFind[i] = (short) buffer.readUShort();
					recolorToReplace[i] = (short) buffer.readUShort();
				}

			}
			else if (opcode == 41)
			{
				int count = buffer.readUnsignedByte();
				textureFind = new short[count];
				textureReplace = new short[count];
				for (int i = 0; i != count; ++i)
				{
					textureFind[i] = (short) buffer.readUShort();
					textureReplace[i] = (short) buffer.readUShort();
				}

			}
			else if (opcode == 42)
			{
				int count = buffer.readUnsignedByte();
				aByteArray2820 = new byte[count];
				for (int i = 0; i != count; ++i)
					aByteArray2820[i] = (byte) count;

			}
			else if (opcode == 60)
			{
				int count = buffer.readUnsignedByte();
				chatheadModels = new int[count];
				for (int i = 0; i != count; ++i)
					chatheadModels[i] = buffer.readUShort();

			}
			else if (opcode == 93)
				isMinimapVisible = false;
			else if (opcode == 95)
				combatLevel = buffer.readUShort();
			else if (opcode == 97)
				widthScale = buffer.readUShort();
			else if (opcode == 98)
				heightScale = buffer.readUShort();
			else if (opcode == 99)
				priorityRender = true;
			else if (opcode == 100)
				ambient = buffer.readSignedByte();
			else if (opcode == 101)
				contrast = 5 * buffer.readSignedByte();
			else if (opcode == 102)
				headIcon = buffer.readUShort();
			else if (opcode == 103)
				rotationSpeed = buffer.readUShort();

			else if (opcode == 106 || opcode == 118)
			{
				varbitId = buffer.readUShort();
				if (varbitId == 65535)
					varbitId = -1;

				varpIndex = buffer.readUShort();
				if (varpIndex == 65535)
					varpIndex = -1;

				int n1 = -1;
				if (opcode == 118)
				{
					n1 = buffer.readUShort();
					if (n1 == 65535)
						n1 = -1;

				}
				int n2 = buffer.readUnsignedByte();
				configs = new int[n2 + 2];
				for (int i = 0; i <= n2; ++i)
				{
					configs[i] = buffer.readUShort();
					if (configs[i] == 65535)
						configs[i] = -1;

				}

				configs[n2 + 1] = n1;
			}
			else if (opcode == 107)
				isInteractable = false;
			else if (opcode == 109)
				rotationFlag = false;
			else if (opcode == 111)
				isPet = false;

			else if (opcode == 113)
			{
				aShort2863 = (short) buffer.readUShort();
				aShort2871 = (short) buffer.readUShort();
			}
			else if (opcode == 114)
			{
				aByte2877 = buffer.readSignedByte();
				aByte2868 = buffer.readSignedByte();
			}
			else if (opcode == 119)
				aByte2816 = buffer.readSignedByte();

			else if (opcode == 121)
			{
				anIntArrayArray2842 = new int[models.length][];
				int count = buffer.readUnsignedByte();
				for (int i = 0; i != count; ++i)
				{
					int index = buffer.readUnsignedByte();
					int[] out = anIntArrayArray2842[index] = new int[3];
					out[0] = buffer.readUnsignedByte();
					out[1] = buffer.readUnsignedByte();
					out[2] = buffer.readUnsignedByte();
				}

			}
			else if (opcode == 122)
				anInt2878 = buffer.readUShort();
			else if (opcode == 123)
				anInt2804 = buffer.readUShort();
			else if (opcode == 125)
				aByte2873 = buffer.readSignedByte();
			else if (opcode == 127)//anim index.
				anInt2837 = buffer.readUShort();
			else if (opcode == 128)
				buffer.readSignedByte();

			else if (opcode == 134)
			{
				anInt2812 = buffer.readUShort();
				if (anInt2812 == 65535)
					anInt2812 = -1;

				anInt2833 = buffer.readUShort();
				if (anInt2833 == 65535)
					anInt2833 = -1;

				anInt2809 = buffer.readUShort();
				if (anInt2809 == 65535)
					anInt2809 = -1;

				anInt2810 = buffer.readUShort();
				if (anInt2810 == 65535)
					anInt2810 = -1;

				anInt2864 = buffer.readUnsignedByte();
			}
			else if (opcode == 135)
			{
				anInt2815 = buffer.readUnsignedByte();
				anInt2859 = buffer.readUShort();
			}
			else if (opcode == 136)
			{
				anInt2856 = buffer.readUnsignedByte();
				anInt2886 = buffer.readUShort();
			}
			else if (opcode == 137)
				anInt2860 = buffer.readUShort();
			else if (opcode == 138)
				anInt2814 = buffer.readUShort();
			else if (opcode == 139)
				anInt2826 = buffer.readUShort();
			else if (opcode == 140)
				anInt2828 = buffer.readUnsignedByte();
			else if (opcode == 141)
				aBoolean2843 = true;
			else if (opcode == 142)
				anInt2849 = buffer.readUShort();
			else if (opcode == 143)
				aBoolean2825 = true;

			else if (opcode >= 150 && opcode < 155)
			{
				actions[opcode - 150] = buffer.readString();

			}
			else if (opcode == 155)
			{
				aByte2836 = buffer.readSignedByte();
				aByte2853 = buffer.readSignedByte();
				aByte2857 = buffer.readSignedByte();
				aByte2839 = buffer.readSignedByte();
			}
			else if (opcode == 158)
				aByte2855 = (byte) 1;
			else if (opcode == 159)
				aByte2855 = (byte) 0;

			else if (opcode == 160)
			{
				int count = buffer.readUnsignedByte();
				anIntArray2832 = new int[count];
				for (int i = 0; i != count; ++i)
					anIntArray2832[i] = buffer.readUShort();

			}
			else if (opcode == 162)
				aBoolean2883 = true;
			else if (opcode == 163)
				anInt2803 = buffer.readUnsignedByte();

			else if (opcode == 164)
			{
				anInt2844 = buffer.readUShort();
				anInt2852 = buffer.readUShort();
			}
			else if (opcode == 165)
				anInt2831 = buffer.readUnsignedByte();
			else if (opcode == 168)
				anInt2862 = buffer.readUnsignedByte();

			else if (opcode == 249)
			{
				int count = buffer.readUnsignedByte();
				if (params == null)
					params = new Hashtable<>();

				for (int i = 0; i != count; ++i)
				{
					boolean string = buffer.readUnsignedByte() == 1;
					int key = buffer.readMedium();
					Object value = string ? buffer.readString():Integer.valueOf(buffer.readInt());
					params.put(key, value);
				}

			}
			else
			{
				System.out.println("[NPCDef] Unknown opcode: " + opcode);
				break;
			}
		}
		if (anInt2837 >= 0 && anInt2837 < NpcAnimationDefinition.defs.length)
		{
			NpcAnimationDefinition def = NpcAnimationDefinition.defs[anInt2837];
			if (def != null) {
				idleAnimation = def.idleAnimation;
				walkingAnimation = def.walkingAnimation;
				rotate180AnimIndex = def.rotate180AnimIndex;
				rotate90CWAnimIndex = def.rotate90CWAnimIndex;
				rotate90CCWAnimIndex = def.rotate90CCWAnimIndex;
				runAnimIndex = def.runAnimIndex;
			}
		}
		else if (anInt2837 >= 0)
			System.out.println("Inavlid NPC Anim Def: " + anInt2837);

	}

	@Override
	public HeadIcon getOverheadIcon() {
		return null;
	}

	@Override
	public int getIntValue(int paramID) {
		return 0;
	}

	@Override
	public void setValue(int paramID, int value) {

	}

	@Override
	public String getStringValue(int paramID) {
		return null;
	}

	@Override
	public void setValue(int paramID, String value) {

	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public int[] getModels() {
		return new int[0];
	}

	@Override
	public String[] getActions() {
		return new String[0];
	}

	@Override
	public boolean isClickable() {
		return false;
	}

	@Override
	public boolean isFollower() {
		return false;
	}

	@Override
	public boolean isInteractible() {
		return false;
	}

	@Override
	public boolean isMinimapVisible() {
		return false;
	}

	@Override
	public boolean isVisible() {
		return false;
	}

	@Override
	public int getId() {
		return 0;
	}

	@Override
	public int getCombatLevel() {
		return 0;
	}

	@Override
	public int[] getConfigs() {
		return new int[0];
	}

	@Override
	public RSNPCComposition transform() {
		return null;
	}

	@Override
	public int getSize() {
		return 0;
	}

	@Override
	public int getRsOverheadIcon() {
		return 0;
	}

	@Override
	public RSIterableNodeHashTable getParams() {
		return null;
	}

	@Override
	public void setParams(IterableHashTable params) {

	}

	@Override
	public void setParams(RSIterableNodeHashTable params) {

	}
}
