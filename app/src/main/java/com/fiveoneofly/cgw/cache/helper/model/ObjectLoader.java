package com.fiveoneofly.cgw.cache.helper.model;

import android.content.Context;

import com.fiveoneofly.cgw.cache.disk.DiskLruCache;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.fiveoneofly.cgw.cache.helper.Util.readListStream;
import static com.fiveoneofly.cgw.cache.helper.Util.readStream;
import static com.fiveoneofly.cgw.cache.helper.Util.writeToStream;

/**
 * Created by Fussen on 2017/12/19.
 */

public class ObjectLoader<D> extends BaseLoader<Object> {


    public ObjectLoader(String cachePath, Context context) {
        super(cachePath, context);
    }


    @Override
    public boolean saveCache(String key, Object data) {
        try {
            DiskLruCache.Editor edit = mDiskLruCache.edit(getKey(key));

            OutputStream fos = edit.newOutputStream(0);

            if (writeToStream(fos, data)) {
                edit.commit();
            } else {
                edit.abort();
            }
            mDiskLruCache.flush();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public <D> D getObjCache(String key, Class<D> cls) {

        try {
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(getKey(key));

            if (snapshot != null) {
                InputStream inputStream = snapshot.getInputStream(0);
                return readStream(inputStream, cls);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <D> List<D> getCacheList(String key, Class<D> cls) {

        try {
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(getKey(key));

            if (snapshot != null) {
                InputStream inputStream = snapshot.getInputStream(0);
                return readListStream(inputStream, cls);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

}
