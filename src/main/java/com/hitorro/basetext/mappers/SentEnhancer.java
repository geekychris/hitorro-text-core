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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hitorro.language.IsoLanguage;
import com.hitorro.util.core.iterator.JsonValueSource;
import com.hitorro.util.core.iterator.mappers.BaseMapper;

/**
 *
 */
public class SentEnhancer extends BaseMapper<JsonValueSource, JsonValueSource> {
    private ExtractPOS epos;
    private Json2Sentences sents;
    private IsoLanguage lang;
    private String arr[][];

    public SentEnhancer(IsoLanguage lang, String arr[][]) {
        epos = new ExtractPOS(lang, null);
        sents = new Json2Sentences(lang, null);
        this.lang = lang;
        this.arr = arr;
    }

    public boolean isThreadSafe() {
        return false;
    }

    public BaseMapper getCopy() {
        return new SentEnhancer(lang, arr);
    }

    public Class inputType() {
        return JsonValueSource.class;
    }

    public Class outputType() {
        return JsonValueSource.class;
    }

    @Override
    public JsonValueSource apply(final JsonValueSource js) {
        for (String a[] : arr) {
            populateSent(js, sents, epos, a[0], a[1], a[2], a[3]);
        }
        return js;
    }

    private void populateSent(JsonValueSource js, Json2Sentences sents, ExtractPOS epos, String from, String sentsString, String posString, String dependString) {
        JsonNode jn = (JsonNode) js.getValue(js, from);
        if (jn != null) {
            ArrayNode an = sents.apply(jn);
            if (an != null) {
                ObjectNode on = epos.apply(an);

                js.setValue(js, sentsString, an);

                js.setValue(js, posString, on);
                //js.setValue(js, "posts.detail_chris[1]", on);
            }

        }
    }
}

