package com.runescape.cache.def;

import com.runescape.Client;
import com.runescape.cache.FileArchive;
import com.runescape.cache.anim.Animation;
import com.runescape.cache.anim.Frame;
import com.runescape.cache.config.VariableBits;
import com.runescape.collection.ReferenceCache;
import com.runescape.entity.model.Model;
import com.runescape.io.Buffer;
import net.runelite.api.IterableHashTable;
import net.runelite.api.Node;
import net.runelite.api.ObjectComposition;

import java.io.IOException;
import java.util.Hashtable;

public final class ObjectDefinition implements ObjectComposition {

    public boolean interactType;
    public byte[] description;
    public boolean hasActions;

    public int anInt746;
    public int mapscene;
    public int animation;
    public boolean contouredGround;
    public boolean aBoolean764;
    public boolean aBoolean757;

    public void setDefaults() {
        interactType = true;
        aBoolean757 = true;
        hasActions = false;
        contouredGround = false;
        aBoolean764 = false;
        anInt746 = -1;
        mapscene = -1;
        animation = -1;

        anInt3030 = 0;
        aBoolean2961 = false;
        aByte2974 = 0;
        aByte3045 = 0;
        aByte3052 = 0;
        modelTypes = null;
        aByteArray2996 = null;
        textureReplace = null;
        recolorToReplace = null;
        textureFind = null;
        recolorToFind = null;
        anIntArray2981 = null;
        configs = null;
        anIntArray3036 = null;
        objectModels = null;
        actions = null;
        params = null;

        aBoolean2972 = true;
        modelSizeY = 128;
        anInt2963 = 0;
        aByte2960 = (byte) 0;
        anInt2983 = 0;
        anInt2971 = 0;
        sizeY = 1;
        anInt2987 = -1;
        anInt2975 = 0;
        anInt2964 = 0;
        modelSizeX = 128;
        name = "null";
        translateZ = 0;
        aBoolean3002 = false;
        supportItems = -1;
        areaId = -1;
        occludes = false;
        anInt3012 = 0;
        ambientSoundId = -1;
        varbitId = -1;
        removeClipping = false;
        aBoolean3007 = false;
        modelSizeZ = 128;
        anInt3018 = 0;
        anInt3024 = 0xff;
        anInt2958 = 0;
        anIntArray3019 = null;
        anInt3023 = -1;
        anInt2989 = 0;
        anInt3008 = -1;
        anInt3032 = 960;
        randomizeAnimStart = true;
        decorDisplacement = 64;
        anIntArray2995 = null;
        aByte3027 = (byte) 0;
        anInt3020 = 256;
        anInt3038 = -1;
        translateX = 0;
        ambient = 0;
        anInt3010 = 2;
        aBoolean2993 = false;
        aBoolean2998 = false;
        translateY = 0;
        surroundings = 0;
        anInt3050 = 256;
        contrast = 0;
        obstructsGround = false;
        aBoolean2990 = false;
        blocksProjectile = true;
        aBoolean2992 = false;
        mergeNormals = false;
        sizeX = 1;
        anInt2962 = 0;
        anInt3006 = -1;
        varbitID = -1;
        anInt3013 = -1;
        castsShadow = true;
        aBoolean3056 = false;
        isRotated = false;
        isInteractive = false;
    }

    public int type;
    public static boolean lowMemory;
    private static Buffer buffer;
    private static int[] streamIndices;
    public static Client clientInstance;
    private static int cacheIndex;
    public static ReferenceCache models = new ReferenceCache(30);
    public static ReferenceCache baseModels = new ReferenceCache(500);
    public static ObjectDefinition[] cache;
    public static int TOTAL_OBJECTS;

    public int anInt2958;
    public int anInt2962;
    public int anInt2963;
    public int anInt2964;
    public int translateX;
    public int modelSizeX;
    public int supportItems;
    public int anInt2971;
    public int modelSizeY;
    public int anInt2975;
    public boolean occludes;
    public int anInt2983;
    public int translateZ;
    public int sizeY;
    public int anInt2987;
    public int anInt2989;
    public int areaId;
    public int anInt3006;
    public int anInt3008;
    public int modelSizeZ;
    public int anInt3010;
    public int decorDisplacement;
    public int anInt3012;
    public int anInt3013;
    public int ambientSoundId;
    public int varbitId;
    public int anInt3018;
    public int anInt3020;
    public int anInt3023;
    public int anInt3024;
    public int contrast;
    public int anInt3030;
    public int anInt3032;
    public int varbitID;
    public int translateY;
    public int anInt3038;
    public int surroundings;
    public int ambient;
    public int anInt3050;
    public int sizeX;
    public boolean isInteractive;
    public boolean aBoolean2961;
    public boolean aBoolean2972;
    public boolean aBoolean2990;
    public boolean aBoolean2992;
    public boolean aBoolean2993;
    public boolean aBoolean2998;
    public boolean randomizeAnimStart;
    public boolean aBoolean3002;
    public boolean aBoolean3007;
    public boolean removeClipping;
    public boolean castsShadow;
    public boolean blocksProjectile;
    public boolean isRotated;
    public boolean obstructsGround;
    public boolean mergeNormals;
    public boolean aBoolean3056;
    public byte aByte2960;
    public byte aByte2974;
    public byte aByte3027;
    public byte aByte3045;
    public byte aByte3052;
    public int[] modelTypes;
    public byte[] aByteArray2996;
    public short[] textureReplace;
    public short[] recolorToReplace;
    public short[] textureFind;
    public short[] recolorToFind;
    public int[] anIntArray2981;
    public int[] configs;
    public int[] anIntArray2995;
    public int[] anIntArray3019;
    public int[] anIntArray3036;
    public int[][] objectModels;
    public String[] actions;
    public String name;
    public Hashtable<Integer, Object> params;

    public static ObjectDefinition lookup(int id) {
        for (int j = 0; j < 50; j++) {
            if (cache[j].type == id) {
                return cache[j];
            }
        }
        cacheIndex = (cacheIndex + 1) % 50;
        ObjectDefinition object = cache[cacheIndex];
        object.type = id;
        object.setDefaults();
        if (id >= 0 && id < TOTAL_OBJECTS && streamIndices[id] != -1) {
            buffer.currentPosition = streamIndices[id];
            object.decode(buffer);
        }

        return object;
    }

    public static void clear() {
        baseModels = null;
        models = null;
        modelBuffer1 = null;
        modelBuffer2 = null;
        streamIndices = null;
        cache = null;
        buffer = null;
    }


    public static void init(FileArchive archive) throws IOException {
        buffer = new Buffer(archive.readFile("object.dat"));
        Buffer stream = new Buffer(archive.readFile("object.idx"));
        TOTAL_OBJECTS = archive.readFile("object.idx").length / 2;
        streamIndices = new int[TOTAL_OBJECTS];
        int index = 0;

        for (int i = 0; i != TOTAL_OBJECTS; ++i) {
            int size = stream.readUShort();
            streamIndices[i] = size != 0 ? index : -1;
            index += size;
        }

        System.out.println("Objects Read -> " + TOTAL_OBJECTS);

        cache = new ObjectDefinition[50];
        for (int k = 0; k < 50; k++) {
            cache[k] = new ObjectDefinition();
        }
    }

    public boolean method577(int type) {
        if (modelTypes == null) {
            if (objectModels == null) {
                return true;
            }

            if (type != 10) {
                return true;
            }
            boolean flag = true;
            int count = objectModels.length;
            for (int i = 0; i != count; ++i) {
                int childCount = objectModels[i].length;
                for (int i1 = 0; i1 != childCount; ++i1) {
                    flag &= Model.isCached(objectModels[i][i1] & 0xffff);
                }

            }

            return flag;
        }
        boolean flag = true;
        int count = modelTypes.length;

        for (int i = 0; i != count; ++i) {
            if (modelTypes[i] == type) {
                int childCount = objectModels[i].length;
                for (int i1 = 0; i1 != childCount; ++i1) {
                    flag &= Model.isCached(objectModels[i][i1] & 0xffff);
                }
                break;
            }
        }
        return flag;
    }

    public Model modelAt(int type, int j, int k, int l, int i1, int j1, int k1) {
        Animation animDef = null;
        Model model = model(type, k1, j, animDef);
        if (model == null)
            return null;
        if (contouredGround || mergeNormals)
            model = new Model(contouredGround, mergeNormals, model, type);
        //model = new Model(contouredGround, mergeNormals, model);//todo
        if (contouredGround) {
            int l1 = (k + l + i1 + j1) >> 2;
            for (int i2 = 0; i2 < model.numVertices; i2++) {
                int j2 = model.vertexX[i2] >> 2;
                int k2 = model.vertexZ[i2] >> 2;
                int l2 = k + (((l - k) * (j2 + 64)) >> 7);
                int i3 = j1 + (((i1 - j1) * (j2 + 64)) >> 7);
                int j3 = l2 + (((i3 - l2) * (k2 + 64)) >> 7);
                model.vertexY[i2] += (j3 - l1) << 2;
            }

            model.calc_diagonals();
        }
        return model;
    }

    public boolean method579() {
        if (objectModels == null) {
            return true;
        }

        boolean flag = true;
        for (int i = 0; i != objectModels.length; ++i) {
            int childCount = objectModels[i].length;
            for (int i1 = 0; i1 != childCount; ++i1) {
                flag &= Model.isCached(objectModels[i][i1] & 0xffff);
            }
        }

        return flag;
    }

    public ObjectDefinition method580() {
        int id = -1;
        if (varbitId != -1) {
            VariableBits variableBit = VariableBits.varbits[varbitId];
            int j = variableBit.getSetting();
            int k = variableBit.getLow();
            int l = variableBit.getHigh();
            int i1 = Client.BIT_MASKS[l - k];
            id = clientInstance.settings[j] >> k & i1;
        } else if (varbitID != -1)
            id = clientInstance.settings[varbitID];
        if (id < 0 || id >= configs.length || configs[id] == -1)
            return null;
        else
            return lookup(configs[id]);
    }

    private static Model[] modelBuffer1 = new Model[256];
    private static Model[] modelBuffer2 = new Model[256];

    private Model model(int j, int k, int l, Animation animDef) {
        Model model = null;
        long l1;
        if (modelTypes == null) {
            if (j != 0) {
                return null;
            }

            l1 = (long) ((type << 8) + l) + ((long) (k + 1) << 32);
            Model model_1 = (Model) models.get(l1);
            //Model model_1 = null;
            if (model_1 != null) {
                return model_1;
            }

            if (objectModels == null) {
                return null;
            }
            boolean flag = isRotated ^ (l > 3);
            int count = objectModels.length;
            for (int i = 0; i != count; ++i) {
                int childCount = objectModels[i].length;
                for (int i1 = 0; i1 != childCount; ++i1) {
                    int id = objectModels[i][i1];
                    if (flag)
                        id += 0x10000;

                    model = null;
                    if (model == null) {
                        model = Model.getModel(id & 0xffff);
                        if (model == null)
                            return null;

                        if (flag)
                            model.invert();

                        baseModels.put(model, id);
                    }
                    if (childCount > 1)
                        modelBuffer2[i1] = model;

                }

                if (childCount > 1)
                    model = new Model(childCount, modelBuffer2);

                if (count > 1)
                    modelBuffer1[i] = model;

            }

            if (count > 1)
                model = new Model(count, modelBuffer1);

        } else {
            int i1 = -1;
            int count = modelTypes.length;
            for (int j1 = 0; j1 < count; j1++) {
                if (modelTypes[j1] != j)
                    continue;

                i1 = j1;
                break;
            }

            if (i1 == -1)
                return null;

            l1 = (long) ((type << 6) + (i1 << 3) + l) + ((long) (k + 1) << 32);
            //model = (Model) mruNodes2.get(l1);
            model = null;
            if (objectModels == null)
                return null;

            boolean flag = isRotated ^ (l > 3);
            int childCount = objectModels[i1].length;
            for (int i = 0; i != childCount; ++i) {
                int id = objectModels[i1][i];
                if (flag)
                    id += 0x10000;

                //model = (Model) mruNodes1.get(id);
                model = null;
                if (model == null) {
                    model = Model.getModel(id & 0xffff);
                    if (model == null)
                        return null;

                    if (flag)
                        model.invert();

                    baseModels.put(model, id);
                }
                if (childCount > 1)
                    modelBuffer1[i] = model;

            }

            if (childCount > 1)
                model = new Model(childCount, modelBuffer1);

        }
        boolean flag;
        flag = modelSizeX != 128 || modelSizeY != 128 || modelSizeZ != 128;
        boolean flag2;
        flag2 = translateX != 0 || translateY != 0 || translateZ != 0;
        int flags = 0;
        if (k != -1)
            flags |= Model.getFlags(k, animDef);

        if (flag2 || flag)
            flags |= 0x4;

        if (recolorToFind != null)
            flags |= 0x2;

        if (textureFind != null)
            flags |= 0x8;

        if (l > 0)
            flags |= 0x4;

        Model model_3 = new Model(recolorToFind == null,
                Frame.noAnimationInProgress(k), l == 0 && k == -1 && !flag
                && !flag2, textureFind == null, model);
        //Model model_3 = new Model(flags, model);//todo
        if (k != -1) {
            model_3.skin();
            model_3.applyTransform(k, animDef);
            model_3.faceGroups = null;
            model_3.vertexGroups = null;
        }
        while (l-- > 0)
            model_3.rotate90Degrees();

        if (recolorToFind != null)
            for (int k2 = 0; k2 < recolorToFind.length; k2++)
                model_3.recolor(recolorToFind[k2], recolorToReplace[k2]);


        if (textureFind != null)
            for (int k2 = 0; k2 < textureFind.length; k2++)
                model_3.retexture(textureFind[k2], textureReplace[k2]);


        if (flag)
            model_3.scale(modelSizeX, modelSizeZ, modelSizeY);//+2???

        if (flag2)
            model_3.translate(translateX, translateY, translateZ);

        model_3.light(64 + ambient, 768 + contrast, -50, -10, -50, !mergeNormals, false);
        if (supportItems == 1)
            model_3.itemDropHeight = model_3.modelBaseY;

        models.put(model_3, l1);
        return model_3;
    }

    public void decode(Buffer buffer) {
        while (true) {
            int opcode = buffer.readUnsignedByte();
            if (opcode == 0)
                break;

            if (opcode == 1 || opcode == 5) {
                if (opcode == 5 && lowMemory) {
                    skip(buffer);
                }
                int count = buffer.readUnsignedByte();
                modelTypes = new int[count];
                objectModels = new int[count][];
                for (int i = 0; i != count; ++i) {
                    modelTypes[i] = buffer.readUnsignedByte();
                    int childCount = buffer.readUnsignedByte();
                    objectModels[i] = new int[childCount];
                    for (int i1 = 0; i1 != childCount; ++i1)
                        objectModels[i][i1] = buffer.readUShort();

                }

                if (opcode == 5 && !lowMemory)
                    skip(buffer);

            } else if (opcode == 2)
                name = buffer.readStringJagex();
            else if (opcode == 14)
                sizeX = buffer.readUnsignedByte();
            else if (opcode == 15)
                sizeY = buffer.readUnsignedByte();
            else if (opcode == 17) {
                blocksProjectile = false;
                anInt3010 = 0;
                interactType = false;
            } else if (opcode == 18) {
                blocksProjectile = false;
                aBoolean757 = false;
            } else if (opcode == 19)
                isInteractive = (buffer.readUnsignedByte() == 1);
            else if (opcode == 21) {
                aByte3027 = (byte) 1;
                contouredGround = true;
            } else if (opcode == 22)
                mergeNormals = true;
            else if (opcode == 23)
                occludes = true;
            else if (opcode == 24) {
                int index = buffer.readUShort();
                if (index != 0xffff) {
                    anIntArray3019 = new int[]{index};
                    animation = index;
                }
            } else if (opcode == 27)
                anInt3010 = 1;
            else if (opcode == 28)
                decorDisplacement = buffer.readUnsignedByte();
            else if (opcode == 29)
                ambient = buffer.readSignedByte();
            else if (opcode == 39)
                contrast = 5 * buffer.readSignedByte();

            else if (opcode >= 30 && opcode < 35) {
                if (actions == null)
                    actions = new String[5];

                actions[opcode - 30] = buffer.readStringJagex();
            } else if (opcode == 40) {
                int count = buffer.readUnsignedByte();
                recolorToFind = new short[count];
                recolorToReplace = new short[count];
                for (int i = 0; i != count; ++i) {
                    recolorToFind[i] = (short) buffer.readUShort();
                    recolorToReplace[i] = (short) buffer.readUShort();
                }

            } else if (opcode == 41) {
                int count = buffer.readUnsignedByte();
                textureFind = new short[count];
                textureReplace = new short[count];
                for (int i = 0; i != count; ++i) {
                    textureFind[i] = (short) buffer.readUShort();
                    textureReplace[i] = (short) buffer.readUShort();
                }

            } else if (opcode == 42) {
                int count = buffer.readUnsignedByte();
                aByteArray2996 = new byte[count];
                for (int i = 0; i != count; ++i)
                    aByteArray2996[i] = buffer.readSignedByte();

            } else if (opcode == 62)
                isRotated = true;
            else if (opcode == 64)
                castsShadow = false;
            else if (opcode == 65)
                modelSizeX = buffer.readUShort();
            else if (opcode == 66)
                modelSizeY = buffer.readUShort();
            else if (opcode == 67)
                modelSizeZ = buffer.readUShort();
            else if (opcode == 69)
                surroundings = buffer.readUnsignedByte();
            else if (opcode == 70)
                translateX = buffer.readShort();
            else if (opcode == 71)
                translateY = buffer.readShort();
            else if (opcode == 72)
                translateZ = buffer.readShort();
            else if (opcode == 73)
                obstructsGround = true;
            else if (opcode == 74)
                removeClipping = true;
            else if (opcode == 75)
                supportItems = buffer.readUnsignedByte();

            else if (opcode == 77 || opcode == 92) {
                varbitId = buffer.readUShort();
                if (varbitId == 0xffff)
                    varbitId = -1;

                varbitID = buffer.readUShort();
                if (varbitID == 0xffff)
                    varbitID = -1;

                int ending = -1;
                if (opcode == 92) {
                    ending = buffer.readUShort();
                    if (ending == 0xffff)
                        ending = -1;

                }
                int count = buffer.readUnsignedByte();
                configs = new int[count + 2];
                for (int i = 0; i <= count; ++i) {
                    configs[i] = buffer.readUShort();
                    if (configs[i] == 0xffff)
                        configs[i] = -1;

                }

                configs[count + 1] = ending;
            } else if (opcode == 78) {
                ambientSoundId = buffer.readUShort();
                anInt3012 = buffer.readUnsignedByte();
            } else if (opcode == 79) {
                anInt2989 = buffer.readUShort();
                anInt2971 = buffer.readUShort();
                anInt3012 = buffer.readUnsignedByte();
                int count = buffer.readUnsignedByte();
                anIntArray3036 = new int[count];
                for (int i = 0; i != count; ++i)
                    anIntArray3036[i] = buffer.readUShort();

            } else if (opcode == 81) {
                aByte3027 = (byte) 2;
                anInt3023 = buffer.readUnsignedByte() * 256;
            } else if (opcode == 82)
                aBoolean2990 = true;
            else if (opcode == 88)
                aBoolean2972 = false;
            else if (opcode == 89)
                randomizeAnimStart = false;
            else if (opcode == 91)
                aBoolean3002 = true;
            else if (opcode == 93) {
                aByte3027 = (byte) 3;
                anInt3023 = buffer.readUShort();
            } else if (opcode == 94)
                aByte3027 = (byte) 4;

            else if (opcode == 95) {
                aByte3027 = (byte) 5;
                anInt3023 = buffer.readShort();
            } else if (opcode == 97)
                aBoolean3056 = true;
            else if (opcode == 98)
                aBoolean2998 = true;

            else if (opcode == 99) {
                anInt2987 = buffer.readUnsignedByte();
                anInt3008 = buffer.readUShort();
            } else if (opcode == 100) {
                anInt3038 = buffer.readUnsignedByte();
                anInt3013 = buffer.readUShort();
            } else if (opcode == 101)
                anInt2958 = buffer.readUnsignedByte();
            else if (opcode == 102) {
                mapscene = buffer.readUShort();
            } else if (opcode == 103)
                occludes = false;
            else if (opcode == 104)
                anInt3024 = buffer.readUnsignedByte();
            else if (opcode == 105)
                aBoolean3007 = true;

            else if (opcode == 106) {
                int count = buffer.readUnsignedByte();
                int total = 0;
                anIntArray3019 = new int[count];
                anIntArray2995 = new int[count];
                for (int i = 0; i != count; ++i) {
                    anIntArray3019[i] = buffer.readUShort();
                    if (anIntArray3019[i] == 0xffff)
                        anIntArray3019[i] = -1;

                    if (i == 0)
                        animation = anIntArray3019[i];

                    total += anIntArray2995[i] = buffer.readUnsignedByte();
                }

                for (int i = 0; i != count; ++i)
                    anIntArray2995[i] = anIntArray2995[i] * 0xffff / total;

            } else if (opcode == 107)
                areaId = buffer.readUShort();

            else if (opcode >= 150 && opcode < 155) {
                if (actions == null)
                    actions = new String[5];

                actions[opcode - 150] = buffer.readStringJagex();
                //if (!aClass112_3028.aBoolean1431)
                //	actions[opcode - 150] = null;

            } else if (opcode == 160) {
                int count = buffer.readUnsignedByte();
                anIntArray2981 = new int[count];
                for (int i = 0; i != count; ++i)
                    anIntArray2981[i] = buffer.readUShort();

            } else if (opcode == 162) {
                aByte3027 = (byte) 3;
                anInt3023 = buffer.readInt();
            } else if (opcode == 163) {
                aByte2974 = buffer.readSignedByte();
                aByte3045 = buffer.readSignedByte();
                aByte3052 = buffer.readSignedByte();
                aByte2960 = buffer.readSignedByte();
            } else if (opcode == 164)
                anInt2964 = buffer.readShort();
            else if (opcode == 165)
                anInt2963 = buffer.readShort();
            else if (opcode == 166)
                anInt3018 = buffer.readShort();
            else if (opcode == 167)
                anInt2983 = buffer.readUShort();
            else if (opcode == 168)
                aBoolean2961 = true;
            else if (opcode == 169)
                aBoolean2993 = true;
            else if (opcode == 170)
                anInt3032 = buffer.readUSmart();
            else if (opcode == 171)
                anInt2962 = buffer.readUSmart();

            else if (opcode == 173) {
                anInt3050 = buffer.readUShort();
                anInt3020 = buffer.readUShort();
            } else if (opcode == 177)
                aBoolean2992 = true;
            else if (opcode == 178)
                anInt2975 = buffer.readUnsignedByte();

            else if (opcode == 249) {
                int count = buffer.readUnsignedByte();
                if (params == null)
                    params = new Hashtable<>();

                for (int i = 0; i != count; ++i) {
                    boolean string = buffer.readUnsignedByte() == 1;
                    int key = buffer.readMedium();
                    Object value = string ? buffer.readStringJagex() : buffer.readInt();
                    params.put(key, value);
                }

            } else {
                System.out.println("[ObjectDef] Unknown opcode: " + opcode);
                break;
            }
            post();
        }

    }

    private void post() {
        if (isInteractive) {
            isInteractive = false;
            if (modelTypes != null && modelTypes.length == 1 && modelTypes[0] == 10)
                isInteractive = true;

            if (!isInteractive && actions != null)
                for (int i = 0; i != 5; ++i)
                    if (actions[i] != null) {
                        isInteractive = true;
                        break;
                    }


        }
        if (supportItems == -1)
            supportItems = anInt3010 == 0 ? 0 : 1;

        if (anIntArray3019 != null || aBoolean2998 || configs != null)
            aBoolean2992 = true;

        hasActions = isInteractive;
    }

    private void skip(Buffer buffer) {
        int count = buffer.readUnsignedByte();
        for (int i = 0; i != count; ++i) {
            ++buffer.currentPosition;
            int childCount = buffer.readUnsignedByte();
            buffer.currentPosition += childCount * 2;
        }
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String[] getActions() {
        return new String[0];
    }

    @Override
    public int getMapSceneId() {
        return 0;
    }

    @Override
    public int getMapIconId() {
        return 0;
    }

    @Override
    public int[] getImpostorIds() {
        return new int[0];
    }

    @Override
    public ObjectComposition getImpostor() {
        return null;
    }

    @Override
    public int getAccessBitMask() {
        return 0;
    }

    @Override
    public IterableHashTable<Node> getParams() {
        return null;
    }

    @Override
    public void setParams(IterableHashTable<Node> params) {

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
}