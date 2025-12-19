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
package com.hitorro.analysis.wordnet;

import com.hitorro.obj.core.Log;
import com.hitorro.util.core.events.cache.SingletonCache;
import com.hitorro.util.core.iterator.mappers.BaseMapper;
import com.hitorro.util.json.keys.FileProperty;

import java.io.IOException;

/**
 *
 */
public class WordnetSingleton extends BaseMapper<Object, WordNetContext> {
    public static String EventName = "wordnetsingleton";

    public static FileProperty WNLocation = new FileProperty("wordnet.dir", "Path of the wordnet repo", "${ht_data}/WordNet-3.0/dict/");

    public static SingletonCache<WordNetContext> instance = new SingletonCache(EventName, new WordnetSingleton());

    public WordnetSingleton() {
    }

    public WordNetContext apply(Object voidObject) {
        try {
            return new WordNetContext(WNLocation.apply());
        } catch (IOException e) {
            Log.util.error("Unable to load wordnet dictionary %s %e", e, e);
        } catch (InterruptedException e) {
            Log.util.error("Unable to load wordnet dictionary %s %e", e, e);
        }
        return null;
    }
}
