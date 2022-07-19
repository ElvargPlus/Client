package com.runescape.cache.config;

import com.runescape.cache.FileArchive;
import com.runescape.io.Buffer;

public final class VariableBits {

    public static VariableBits varbits[];
    public int setting;
    public int low;
    public int high;
    private boolean aBoolean651;

    private VariableBits() {
        aBoolean651 = false;
    }

    public static void init(FileArchive archive) {
        Buffer buffer = new Buffer(archive.readFile("varbit.dat"));
        int count = buffer.readUShort();

        varbits = new VariableBits[count];

        for (int index = 0; index < count; index++) {

            if (varbits[index] == null) {
                varbits[index] = new VariableBits();
            }

            varbits[index].decode(buffer);

        }

    }

    private void decode(Buffer buffer) {
        int opcode = buffer.readUnsignedByte();

        if (opcode == 0) {
            return;
        } else if (opcode == 1) {
            setting = buffer.readUShort();
            low = buffer.readUnsignedByte();
            high = buffer.readUnsignedByte();
        } else {
            System.out.println("Invalid varbit opcode: " + opcode);
        }
    }

    public int getSetting() {
        return setting;
    }

    public int getLow() {
        return low;
    }

    public int getHigh() {
        return high;
    }

}
