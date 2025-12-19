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
package com.hitorro.basetext.mappers;

import com.hitorro.language.IsoLanguage;
import com.hitorro.language.SentenceDetectorSingleton;
import com.hitorro.language.SentenceSegmenter;
import com.hitorro.language.Sentences;
import com.hitorro.util.core.events.cache.PoolContainer;
import com.hitorro.util.core.iterator.mappers.BaseMapper;
import com.hitorro.util.core.string.StringUtil;
import com.hitorro.util.typesystem.Bag;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Bag2Sentences extends BaseMapper<Bag, List<String>> {
    private IsoLanguage lang;
    private String fields[];
    private PoolContainer<IsoLanguage, SentenceSegmenter> pool = null;
    private SentenceSegmenter ss;
    private BaseMapper<String, String> intermediateMapper;

    public Bag2Sentences(IsoLanguage lang, String fields[], BaseMapper<String, String> intermediateMapper) {
        this.lang = lang;
        this.fields = fields;
        pool = SentenceDetectorSingleton.singleton.get(lang);
        ss = pool.get();
        if (intermediateMapper != null && !intermediateMapper.isThreadSafe()) {
            this.intermediateMapper = intermediateMapper.getCopy();
        } else {
            this.intermediateMapper = intermediateMapper;
        }
    }

    public boolean isThreadSafe() {
        return false;
    }

    public BaseMapper getCopy() {
        return new Bag2Sentences(lang, fields, intermediateMapper);
    }

    @Override
    public List<String> apply(final Bag e) {
        ArrayList<String> ret = new ArrayList<String>();
        for (String field : fields) {
            String txt = e.getValueAsString(field);
            if (intermediateMapper != null) {
                txt = intermediateMapper.apply(txt);
            }
            if (StringUtil.nullOrEmptyString(txt)) {
                continue;
            }
            Sentences sentences = ss.getSentenceOffsets(txt);
            ret.addAll(sentences.getSentences());
        }
        return ret;
    }


    public void finalize() {
        if (ss != null) {
            pool.returnIt(ss);
        }
    }

}
