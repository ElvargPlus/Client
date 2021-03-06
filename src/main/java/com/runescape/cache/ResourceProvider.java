package com.runescape.cache;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.zip.CRC32;
import java.util.zip.GZIPInputStream;

import com.runescape.Client;
import com.runescape.collection.Deque;
import com.runescape.collection.Queue;
import com.runescape.io.Buffer;
import com.runescape.sign.SignLink;

public final class ResourceProvider implements Runnable {

    private final Deque requested;
    private final byte[] payload;
    private final byte[][] fileStatus;
    private final Deque extras;
    private final Deque complete;
    private final byte[] gzipInputBuffer;
    private final Queue requests;
    private final int[][] versions;
    private final Deque unrequested;
    private final Deque mandatoryRequests;
    private final CRC32 crc32;
    private final String crcNames[] = {"model_crc", "anim_crc", "midi_crc", "map_crc"};
    private final int[][] crcs = new int[crcNames.length][];
    public String loadingMessage;
    public int tick;
    public int errors;
    public String currentDownload = "";
    public int[] file_amounts = new int[4];

    private int totalFiles;
    private int maximumPriority;
    private int deadTime;
    private long lastRequestTime;
    private int[] landscapes;
    private Client clientInstance;
    private int completedSize;
    private int remainingData;
    private int[] musicPriorities;
    private int[] mapFiles;
    private int filesLoaded;
    private boolean running;
    private OutputStream outputStream;
    private int[] membersArea;
    private boolean expectingData;
    private int[] anIntArray1360;
    private InputStream inputStream;
    private Socket socket;
    private int uncompletedCount;
    private int completedCount;
    private Resource current;
    private int[] areas;
    private byte[] modelIndices;
    private int idleTime;

    public ResourceProvider() {
        requested = new Deque();
        loadingMessage = "";
        payload = new byte[500];
        fileStatus = new byte[4][];
        extras = new Deque();
        running = true;
        expectingData = false;
        complete = new Deque();
        gzipInputBuffer = new byte[0x71868];
        requests = new Queue();
        versions = new int[5][];
        unrequested = new Deque();
        mandatoryRequests = new Deque();
        crc32 = new CRC32();
    }

    private String forId(int type) {
        switch (type) {
            case 1:
                return "Model";
            case 2:
                return "Animation";
            case 3:
                return "Sound";
            case 4:
                return "Map";
        }
        return "";
    }

    private void respond() {
        try {
            int available = inputStream.available();
            if (remainingData == 0 && available >= 10) {
                expectingData = true;
                for (int skip = 0; skip < 10; skip += inputStream.read(payload, skip, 10 - skip))
                    ;
                int type = payload[0] & 0xff;
                int file = ((payload[1] & 0xff) << 16) + ((payload[2] & 0xff) << 8) + (payload[3] & 0xff);
                int length = ((payload[4] & 0xff) << 32) + ((payload[5] & 0xff) << 16) + ((payload[6] & 0xff) << 8) + (payload[7] & 0xff);
                int sector = ((payload[8] & 0xff) << 8) + (payload[9] & 0xff);
                current = null;
                for (Resource resource = (Resource) requested.reverseGetFirst(); resource != null; resource = (Resource) requested.reverseGetNext()) {
                    if (resource.dataType == CacheArchive.values()[type] && resource.ID == file)
                        current = resource;
                    if (current != null)
                        resource.loopCycle = 0;
                }

                if (current != null) {
                    currentDownload = "Downloading " + forId(current.dataType.getIndex()) + " " + current.ID + "";
                    idleTime = 0;
                    if (length == 0) {
                        System.out.println("Rej: " + type + "," + file);
                        current.buffer = null;
                        if (current.incomplete)
                            synchronized (complete) {
                                complete.insertHead(current);
                            }
                        else {
                            current.unlink();
                        }
                        current = null;
                    } else {
                        if (current.buffer == null && sector == 0)
                            current.buffer = new byte[length];
                        if (current.buffer == null && sector != 0)
                            throw new IOException("missing start of file");
                    }
                }
                completedSize = sector * 500;
                remainingData = 500;
                if (remainingData > length - sector * 500)
                    remainingData = length - sector * 500;
            }
            if (remainingData > 0 && available >= remainingData) {
                expectingData = true;
                byte data[] = payload;
                int read = 0;
                if (current != null) {
                    data = current.buffer;
                    read = completedSize;
                }
                for (int skip = 0; skip < remainingData; skip += inputStream.read(data, skip + read, remainingData - skip))
                    ;
                if (remainingData + completedSize >= data.length && current != null) {
                    if (clientInstance.indices[0] != null)
                        clientInstance.indices[current.dataType.getIndex()].writeFile(data.length, data, current.ID);

                    if (current.incomplete)
                        synchronized (complete) {
                            complete.insertHead(current);
                        }
                    else {
                        current.unlink();
                    }
                }
                remainingData = 0;
            }
        } catch (IOException ex) {
            try {
                socket.close();
            } catch (Exception _ex) {
                _ex.printStackTrace();
            }
            socket = null;
            inputStream = null;
            outputStream = null;
            remainingData = 0;
        }
    }

    public void initialize(FileArchive archive, Client client) {
        String ver[] = {
                "model_version", "anim_version", "midi_version", "map_version", "texture_version"
        };
        for(int type = 0; type < 5; type++) {
            byte data[] = archive.readFile(ver[type]);
            int total = data.length / 2;
            Buffer buffer = new Buffer(data);
            versions[type] = new int[65536];
            for(int id = 0; id < total; id++) {
                versions[type][id] = buffer.readShort();
            }
        }

        for (int i = 0; i < crcNames.length; i++) {
            byte[] crc_file = archive.readFile(crcNames[i]);
            int length = 0;

            if (crc_file != null) {
                length = crc_file.length / 4;
                Buffer crcStream = new Buffer(crc_file);
                crcs[i] = new int[length];
                fileStatus[i] = new byte[length];
                for (int ptr = 0; ptr < length; ptr++) {
                    crcs[i][ptr] = crcStream.readInt();
                }
            }
        }


        byte[] data = archive.readFile("map_index");
        Buffer stream = new Buffer(data);
        int j1 = stream.readUShort();//data.length / 6;
        areas = new int[j1];
        mapFiles = new int[j1];
        landscapes = new int[j1];
        file_amounts[3] = j1;
        for (int i2 = 0; i2 < j1; i2++) {
            areas[i2] = stream.readUShort();
            mapFiles[i2] = stream.readUShort();
            landscapes[i2] = stream.readUShort();
        }

        System.out.println("Maps Read -> " + file_amounts[3]);

        data = archive.readFile("midi_index");
        stream = new Buffer(data);
        j1 = data.length;
        file_amounts[2] = j1;
        musicPriorities = new int[j1];
        for (int k2 = 0; k2 < j1; k2++)
            musicPriorities[k2] = stream.readUnsignedByte();
        System.out.println("Sounds Read -> " + file_amounts[2]);

        //For some reason, model_index = anim_index and vice versa
        data = archive.readFile("model_index");
        file_amounts[1] = data.length;

        data = archive.readFile("anim_index");
        file_amounts[0] = data.length;

        System.out.println("Models Read -> " + file_amounts[0]);

        clientInstance = client;
        running = true;
        clientInstance.startRunnable(this, 2);
    }

    public int remaining() {
        synchronized (requests) {
            return requests.size();
        }
    }

    public void disable() {
        running = false;
    }

    public void preloadMaps(boolean members) {
        for (int area = 0; area < areas.length; area++) {
            if (members || membersArea[area] != 0) {
                requestExtra((byte) 2, 3, landscapes[area]);
                requestExtra((byte) 2, 3, mapFiles[area]);
            }
        }
    }

    public int getVersionCount(int index) {
        return versions[index].length;
    }

    public void provide(CacheArchive type, int file) {
        if (file < 0)
            return;
        synchronized (requests) {
            for (Resource resource = (Resource) requests.reverseGetFirst(); resource != null; resource = (Resource) requests.reverseGetNext())
                if (resource.dataType == type && resource.ID == file) {
                    return;
                }

            Resource resource = new Resource();
            resource.dataType = type;
            resource.ID = file;
            resource.incomplete = true;
            synchronized (mandatoryRequests) {
                mandatoryRequests.insertHead(resource);
            }
            requests.insertHead(resource);
        }
    }

    public int getModelIndex(int i) {
        return modelIndices[i] & 0xff;
    }

    public void run() {
        try {
            while (running) {
                tick++;
                int sleepTime = 20;
                if (maximumPriority == 0 && clientInstance.indices[0] != null)
                    sleepTime = 50;
                try {
                    Thread.sleep(sleepTime);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                expectingData = true;
                for (int index = 0; index < 100; index++) {
                    if (!expectingData)
                        break;
                    expectingData = false;
                    loadMandatory();
                    requestMandatory();
                    if (uncompletedCount == 0 && index >= 5)
                        break;
                    loadExtra();
                    if (inputStream != null)
                        respond();
                }

                boolean idle = false;
                for (Resource resource = (Resource) requested.reverseGetFirst(); resource != null; resource = (Resource) requested.reverseGetNext())
                    if (resource.incomplete) {
                        idle = true;
                        resource.loopCycle++;
                        if (resource.loopCycle > 50) {
                            resource.loopCycle = 0;
                        }
                    }

                if (!idle) {
                    for (Resource resource = (Resource) requested.reverseGetFirst(); resource != null; resource = (Resource) requested.reverseGetNext()) {
                        idle = true;
                        resource.loopCycle++;
                        if (resource.loopCycle > 50) {
                            resource.loopCycle = 0;
                        }
                    }

                }
                if (idle) {
                    idleTime++;
                    if (idleTime > 750) {
                        try {
                            socket.close();
                        } catch (Exception _ex) {
                        }
                        socket = null;
                        inputStream = null;
                        outputStream = null;
                        remainingData = 0;
                    }
                } else {
                    idleTime = 0;
                    loadingMessage = "";
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            System.out.println("od_ex " + exception.getMessage());
        }
    }

    public void loadExtra(CacheArchive type, int file) {
        if (clientInstance.indices[0] == null) {
            return;
        } else if (maximumPriority == 0) {
            return;
        }
        Resource resource = new Resource();
        resource.dataType = type;
        resource.ID = file;
        resource.incomplete = false;
        synchronized (extras) {
            extras.insertHead(resource);
        }
    }

    public Resource next() {
        Resource resource;
        synchronized (complete) {
            resource = (Resource) complete.popHead();
        }
        if (resource == null)
            return null;
        synchronized (requests) {
            resource.unlinkCacheable();
        }
        if (resource.buffer == null)
            return resource;
        int read = 0;
        try {
            GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(resource.buffer));
            do {
                if (read == gzipInputBuffer.length)
                    throw new RuntimeException("buffer overflow!");
                int in = gis.read(gzipInputBuffer, read, gzipInputBuffer.length - read);
                if (in == -1)
                    break;
                read += in;
            } while (true);
        } catch (IOException _ex) {
            System.out.println("Failed to unzip model [" + resource.ID + "] type = " + resource.dataType);
            _ex.printStackTrace();
            return null;
        }
        resource.buffer = new byte[read];
        System.arraycopy(gzipInputBuffer, 0, resource.buffer, 0, read);

        return resource;
    }

    public int resolve(int regionX, int regionY, int type) {
        int code = (type << 8) + regionY;
        for (int area = 0; area < areas.length; area++) {
            if (areas[area] == code) {
                if (regionX == 0) {
                    return mapFiles[area];
                } else {
                    return landscapes[area];
                }
            }
        }
        return -1;
    }

    public void requestExtra(byte priority, int type, int file) {
        if (clientInstance.indices[0] == null)
            return;

        fileStatus[type][file] = priority;
        if (priority > maximumPriority)
            maximumPriority = priority;
        totalFiles++;
    }

    public boolean landscapePresent(int landscape) {
        for (int index = 0; index < areas.length; index++)
            if (landscapes[index] == landscape)
                return true;
        return false;
    }

    private void requestMandatory() {
        uncompletedCount = 0;
        completedCount = 0;
        for (Resource resource = (Resource) requested.reverseGetFirst(); resource != null; resource = (Resource) requested.reverseGetNext())
            if (resource.incomplete)
                uncompletedCount++;
            else
                completedCount++;

        while (uncompletedCount < 10) {
            Resource resource = (Resource) unrequested.popHead();
            if (resource == null)
                break;
            if (fileStatus[resource.dataType.getIndex()][resource.ID] != 0) {
                filesLoaded++;
            }
            fileStatus[resource.dataType.getIndex()][resource.ID] = 0;
            requested.insertHead(resource);
            uncompletedCount++;
        }
    }



    public void clearExtras() {
        synchronized (extras) {
            extras.clear();
        }
    }

    public void loadMandatory(CacheArchive type, int id) {
        if (id < 0) {
            return;
        }
        synchronized (mandatoryRequests) {
            for (Resource resource = (Resource) mandatoryRequests.reverseGetFirst(); resource != null; resource = (Resource) mandatoryRequests.reverseGetNext())
                if (resource.dataType == type && resource.ID == id)
                    return;

            Resource resource = new Resource();
            resource.dataType = type;
            resource.ID = id;
            resource.incomplete = true;
            synchronized (complete) {
                complete.insertHead(resource);
            }
            mandatoryRequests.insertHead(resource);
        }
    }
    public void loadMandatory() {
        Resource resource;
        synchronized (mandatoryRequests) {
            resource = (Resource) mandatoryRequests.popHead();
        }
        while (resource != null) {
            expectingData = true;
            byte data[] = null;

            if (clientInstance.indices[0] != null)
                data = clientInstance.indices[resource.dataType.getIndex()].decompress(resource.ID);



            synchronized (mandatoryRequests) {
                if (data == null) {
                    unrequested.insertHead(resource);
                } else {
                    resource.buffer = data;
                    synchronized (complete) {
                        complete.insertHead(resource);
                    }
                }
                resource = (Resource) mandatoryRequests.popHead();
            }
        }
    }


    private void loadExtra() {
        while (uncompletedCount == 0 && completedCount < 10) {
            if (maximumPriority == 0)
                break;
            Resource resource;
            synchronized (extras) {
                resource = (Resource) extras.popHead();
            }
            while (resource != null) {
                if (fileStatus[resource.dataType.getIndex()][resource.ID] != 0) {
                    fileStatus[resource.dataType.getIndex()][resource.ID] = 0;
                    requested.insertHead(resource);
                    expectingData = true;
                    if (filesLoaded < totalFiles)
                        filesLoaded++;
                    loadingMessage = "Loading extra files - " + (filesLoaded * 100) / totalFiles + "%";
                    completedCount++;
                    if (completedCount == 10)
                        return;
                }
                synchronized (extras) {
                    resource = (Resource) extras.popHead();
                }
            }
            for (int type = 0; type < 4; type++) {
                byte data[] = fileStatus[type];
                int size = data.length;
                for (int file = 0; file < size; file++)
                    if (data[file] == maximumPriority) {
                        data[file] = 0;
                        Resource newResource = new Resource();
                        newResource.dataType = CacheArchive.values()[type];
                        newResource.ID = file;
                        newResource.incomplete = false;
                        requested.insertHead(newResource);
                        expectingData = true;
                        if (filesLoaded < totalFiles)
                            filesLoaded++;
                        loadingMessage = "Loading extra files - " + (filesLoaded * 100) / totalFiles + "%";
                        completedCount++;
                        if (completedCount == 10)
                            return;
                    }
            }
            maximumPriority--;
        }
    }

}
