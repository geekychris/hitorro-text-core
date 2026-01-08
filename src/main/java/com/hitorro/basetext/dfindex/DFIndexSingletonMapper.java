/*
 * Copyright (c) 2006-2025 Chris Collins
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.hitorro.basetext.dfindex;

import com.hitorro.util.core.Log;
import com.hitorro.util.core.events.cache.Cache;
import com.hitorro.util.core.events.cache.SingletonCache;
import com.hitorro.util.core.iterator.mappers.BaseMapper;
import com.hitorro.util.io.resourcecache.ResourceToPoll;
import com.hitorro.util.io.resourcecache.basefile.BaseFileResourceCache;
import com.hitorro.util.io.resourcecache.file.FileResourcePropertyKey;

import java.io.File;
import java.io.IOException;

/**
 */
public class DFIndexSingletonMapper extends BaseMapper<Object, DFIndex> {
    public static final String DFIndexSingletonCacheEventKey = "DFIndexSingletonCache";
    public static final FileResourcePropertyKey PropKey =
            new FileResourcePropertyKey(DFIndex.ResourceName, DFIndex.VersionQuery, DFIndex.FileName,
                    "index.dfindex", "", false);

    private static final SingletonCache<DFIndex> singleton = new SingletonCache(true, true, "dfindex", new DFIndexSingletonMapper(), null);

    public DFIndexSingletonMapper() {
        ResourceToPoll rtp = new ResourceToPoll(DFIndex.ResourceName, DFIndex.VersionQuery,
                30, DFIndexSingletonCacheEventKey, Cache.FlushCache);

        try {
            BaseFileResourceCache.getCache().add(rtp);
        } catch (IOException e) {
            Log.util.fatal("Unable to put resource poll %s %e", e, e);
        }
    }

    public static final SingletonCache<DFIndex> getSingleton() {
        return singleton;
    }

    public DFIndex apply(Object object) {
        File f = PropKey.apply();
        DFIndex dfi = new DFIndex(0);
        try {
            dfi.read(f);
            dfi.setFrequencyMaxByPercentage(30.0);
        } catch (IOException e) {
            Log.util.error("Unable to load index file %s, %s %e", f.getAbsolutePath(), e, e);
            return null;
        }
        return dfi;
    }
}
