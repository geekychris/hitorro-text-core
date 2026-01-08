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
package com.hitorro.basetext.phrase;

import com.hitorro.util.core.Log;
import com.hitorro.util.core.events.cache.SingletonCache;
import com.hitorro.util.core.iterator.mappers.BaseMapper;
import com.hitorro.util.io.resourcecache.file.FileResourcePropertyKey;

import java.io.File;
import java.io.IOException;


public class PhraseIndexSingletonCache extends BaseMapper<Object, PhraseIndex> {
    public static final String DFIndexSingletonCacheEventKey = "PhraseIndexSingletonCache";
    public static final FileResourcePropertyKey PropKey =
            new FileResourcePropertyKey(PhraseIndex.ResourceName, PhraseIndex.VersionQuery, PhraseIndex.DictFile,
                    PhraseIndex.DictFile, "", false);

    private static final SingletonCache<PhraseIndex> s_singleton = new SingletonCache<PhraseIndex>(DFIndexSingletonCacheEventKey, new PhraseIndexSingletonCache());

    public PhraseIndexSingletonCache() {
    }

    public static final SingletonCache<PhraseIndex> getSingleton() {
        return s_singleton;
    }

    public PhraseIndex apply(Object voidArg) {
        File f = PropKey.apply();
        PhraseIndex pi = new PhraseIndex(0);
        try {
            pi.read(f);
        } catch (IOException e) {
            Log.util.error("Unable to load phrase index file %s, %s %e", f.getAbsolutePath(), e, e);
            return null;
        }
        return pi;
    }
}