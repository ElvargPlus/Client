package com.runescape.entity.model;

import com.runescape.cache.FileArchive;
import com.runescape.io.Buffer;

public final class IdentityKit {

    public static int length;
    public static IdentityKit kits[];
    private short[] srcColors;
    private short[] dstColors;
    private short[] aShortArray3356;
    private short[] aShortArray3353;
    private int[] headModels = {-1, -1, -1, -1, -1};
    public int bodyPartId;
    public boolean validStyle;
    private int[] bodyModels;

    private IdentityKit() {
        bodyPartId = -1;

    }

    public static void init(FileArchive archive) {
        Buffer buffer = new Buffer(archive.readFile("idk.dat"));

        length = buffer.readUShort();
        kits = new IdentityKit[length];

        for (int id = 0; id < length; id++) {

            if (kits[id] == null) {
                kits[id] = new IdentityKit();
            }
            kits[id].decode(buffer);
        }

    }

    private void decode(Buffer buffer) {        
        while(true) {
            final int opcode = buffer.readUnsignedByte();

            if (opcode == 0) {
                break;
            }

            if (opcode == 1) {
                bodyPartId = buffer.readUnsignedByte();
            } else if (opcode == 2) {
                final int length = buffer.readUnsignedByte();
                bodyModels = new int[length];
                for (int i = 0; i < length; i++) {
                    bodyModels[i] = buffer.readUShort();
                }
            } else if (opcode == 3) {
                validStyle = true;
            } else if (opcode == 40) {
                 int count = buffer.readUnsignedByte();
                 srcColors = new short[count];
                 dstColors = new short[count];
                 for (int i = 0; i != count; ++i) {
                     srcColors[i] = (short) buffer.readUShort();
                     dstColors[i] = (short) buffer.readUShort();
                 }
            } else if (opcode == 41) {
                int count = buffer.readUnsignedByte();
                aShortArray3356 = new short[count];
                aShortArray3353 = new short[count];
                for (int i = 0; i != count; ++i) {
                    aShortArray3356[i] = (short) buffer.readUShort();
                    aShortArray3353[i] = (short) buffer.readUShort();
                }
            } else if (opcode == 60) {
                int count = buffer.readUnsignedByte();
                headModels = new int[count];
                for (int i = 0; i != count; ++i) {
                    headModels[i] = buffer.readUShort();
                }
            } else {
                System.out.println("Error unrecognised config code: " + opcode);
            }
        }
    }

    public boolean bodyLoaded() {
        if (bodyModels == null)
            return true;
        boolean ready = true;
        for (int part = 0; part < bodyModels.length; part++)
            if (!Model.isCached(bodyModels[part]))
                ready = false;

        return ready;
    }

    public Model bodyModel() {
        if (bodyModels == null) {
            return null;
        }

        Model models[] = new Model[bodyModels.length];
        for (int part = 0; part < bodyModels.length; part++) {
            models[part] = Model.getModel(bodyModels[part]);
        }

        Model model;
        if (models.length == 1) {
            model = models[0];
        } else {
            model = new Model(models.length, models,true);
        }

        return model;
    }

    public boolean headLoaded() {
        boolean ready = true;
        for (int part = 0; part < 5; part++) {
            if (headModels[part] != -1 && !Model.isCached(headModels[part])) {
                ready = false;
            }
        }
        return ready;
    }

    public Model headModel() {
        Model models[] = new Model[5];
        int count = 0;
        for (int part = 0; part < 5; part++) {
            if (headModels[part] != -1) {
                models[count++] = Model.getModel(headModels[part]);
            }
        }

        Model model = new Model(count, models,true);

        return model;
    }
}
