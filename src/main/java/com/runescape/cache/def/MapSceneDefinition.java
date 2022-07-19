package com.runescape.cache.def;

import com.runescape.cache.FileArchive;
import com.runescape.io.Buffer;

import java.util.HashMap;

public final class MapSceneDefinition {

    public static int size;

    public int id;
    public int spriteId = -1;
    public int spritesubindex = -1;
    private static int cacheIndex;

    private MapSceneDefinition() {
        spriteId = -1;
        spritesubindex = -1;
    }

    public static MapSceneDefinition[] cache;
    private static Buffer scene_data;
    private static int[] indices;

    public void clear() {
        cache = null;
        scene_data = null;
    }

    public static void init(FileArchive archive) {
        scene_data = new Buffer(archive.readFile("mapscene.dat"));
        Buffer stream = new Buffer(archive.readFile("mapscene.idx"));

        size = stream.readUShort();

        indices = new int[size];
        int offset = 2;

        for (int _ctr = 0; _ctr < size; _ctr++) {
            indices[_ctr] = offset;
            offset += stream.readUShort();
        }

        cache = new MapSceneDefinition[10];

        for (int _ctr = 0; _ctr < 10; _ctr++) {
            cache[_ctr] = new MapSceneDefinition();
        }

        System.out.println("Map Scenes read -> " + size);

    }

    public static MapSceneDefinition lookup(int area) {
        for (int count = 0; count < 10; count++) {
            if (cache[count].id == area) {
                return cache[count];
            }
        }
        cacheIndex = (cacheIndex + 1) % 10;
        MapSceneDefinition data = cache[cacheIndex];
        if (area >= 0) {
            scene_data.currentPosition = indices[area];
            data.decode(scene_data);
        }
        return data;
    }

    private void decode(Buffer buffer) {
        do {
            int opcode = buffer.readUnsignedByte();

            if (opcode == 0) {
                return;
            }

            if (opcode == 1) {
                spriteId = buffer.readUShort();
            } else if (opcode == 2) {
                spritesubindex = buffer.readTriByte();
            } else {
                System.out.println("Error unrecognised MapScene opcode: " + opcode);
            }
        } while (true);
    }


}