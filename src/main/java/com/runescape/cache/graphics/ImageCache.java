package com.runescape.cache.graphics;

import com.runescape.Client;
import com.runescape.cache.CacheArchive;
import com.runescape.cache.assets.AssetLoader;
import com.runescape.cache.graphics.sprite.Sprite;
import net.runelite.client.RuneLite;

import java.io.*;
import java.net.MalformedURLException;
import java.util.HashMap;

public final class ImageCache {

    public final static HashMap<Integer,Sprite> imageCache = new HashMap<Integer, Sprite>();

    private final static Sprite nulledImage = new Sprite(0, 0);

    public static Sprite lookup(int id) {
        ImageCache.get(id,true);
        return get(id);
    }

    public static Sprite get(int id) {
        if(id == -1) {
            return Sprite.EMPTY_SPRITE;
        }
        return get(id, true);
    }

    public static Sprite get(int id, boolean urgent) {
        if(RuneLite.devMode) {
            try {
                if(!imageCache.containsKey(id)) {
                    imageCache.put(id,new Sprite(new File(AssetLoader.INSTANCE.getClientFolder(),  "/index6/" + id + ".png").toURL()));
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return imageCache.get(id);
        }
        if(!imageCache.containsKey(id)) {
            if(Client.instance.resourceProvider != null) {
                Client.instance.resourceProvider.provide(CacheArchive.SPRITES_RUNESCAPE, id);
                if(urgent) {
                    Client.instance.processOnDemandQueue();
                }
            }
            return nulledImage;
        }

        return imageCache.get(id);
    }


    public static synchronized void setImage(Sprite image, int id) {
        imageCache.put(id, image);
    }


    public static synchronized void clear() {
        imageCache.clear();
    }


}