package com.runescape.cache.def;

import com.runescape.cache.FileArchive;
import com.runescape.io.Buffer;

import java.util.HashMap;
import java.util.Hashtable;

public final class AreaDefinition {

    public static int size;
    public static HashMap<Integer, AreaDefinition> definitions = new HashMap<>();
    public int id;
    public int spriteId;
    public int iconKey;
    public String description;
    public int fontColor;
    public int opcode19;
    public String[] options;
    public String type;
    public int fontSize;
    public int varbitSecondary;
    public int varSecondary;
    public int varMinSecondary;
    public int varMaxSecondary;
    public int varp;
    public int varbit;
    public int varValueMin;
    public int varValueMax;
    public int opcode21;
    public int flags;
    public int opcode22;
    public int opcode39;
    public int opcode8;
    public HashMap<Integer,Object> params;
    private static int cacheIndex;
    
    private AreaDefinition() {
        spriteId = -1;
        iconKey = -1;
        description = "";
        fontColor = 0;
        opcode19 = -1;
        type = "";
        fontSize = 0;
        varbitSecondary = -1;
        varSecondary = -1;
        varMinSecondary = -1;
        varMaxSecondary = -1;
        varp = -1;
        varbit = -1;
        varValueMin = -1;
        varValueMax = -1;
        opcode21 = -1;
        flags = -1;
        opcode22 = -1;
        opcode39 = -1;
        opcode8 = -1;
    }

    public static AreaDefinition[] cache;
    private static Buffer area_data;
    private static int[] indices;

    public void clear() {
        cache = null;
        area_data = null;
    }

    public static void init(FileArchive archive) {
        area_data = new Buffer(
                archive.readFile("areas.dat")
        );
        Buffer stream = new Buffer(
                archive.readFile("areas.idx")
        );

        size = stream.readUShort();

        indices = new int[size];
        int offset = 2;

        for (int _ctr = 0; _ctr < size; _ctr++) {
            indices[_ctr] = offset;
            offset += stream.readUShort();
        }

        cache = new AreaDefinition[10];

        for (int _ctr = 0; _ctr < 10; _ctr++) {
            cache[_ctr] = new AreaDefinition();
        }

        System.out.println("Areas read -> " + size);

    }

    public static AreaDefinition lookup(int area) {
        for (int count = 0; count < 10; count++) {
            if (cache[count].id == area) {
                return cache[count];
            }
        }
        cacheIndex = (cacheIndex + 1) % 10;
        AreaDefinition data = cache[cacheIndex];
        if (area >= 0) {
            area_data.currentPosition = indices[area];
            data.decode(area_data);
        }
        return data;
    }

    public static AreaDefinition get(int id) {
        return definitions.get(id);
    }

    private void decode(Buffer buffer) {
        do {
            int opcode = buffer.readUnsignedByte();
            if (opcode == 0)
                return;
            else if (opcode == 1) {
                spriteId = buffer.readUShort();
            } else if (opcode == 2) {
                iconKey = buffer.readUShort();
            } else {
                System.out.println("Error unrecognised area code: " + opcode);
            }
        } while (true);
    }


}