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
import com.hitorro.language.IsoLanguage;
import com.hitorro.obj.core.Log;
import com.hitorro.util.core.Console;
import com.hitorro.util.core.iterator.JsonValueSource;
import com.hitorro.util.core.iterator.mappers.BaseMapper;
import com.hitorro.util.core.string.Fmt;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA. User: chris Date: 8/6/12 Time: 9:24 PM To change this template use File | Settings | File
 * Templates.
 */
public class NERJacksonMarkup extends BaseMapper<JsonValueSource, JsonValueSource> {
    private String srcField[];
    private String targetField[];
    private NERMarkup nerMarkup;
    private String filters;
    private IsoLanguage lang;


    public NERJacksonMarkup(String srcField[], String targetField[], String filters, IsoLanguage lang) {
        this.srcField = srcField;
        this.targetField = targetField;
        nerMarkup = new NERMarkup(filters, lang);
        this.filters = filters;
        this.lang = lang;
    }

    public boolean isThreadSafe() {
        return false;
    }

    public BaseMapper getCopy() {
        return new NERJacksonMarkup(srcField, targetField, filters, lang);
    }

    @Override
    public JsonValueSource apply(final JsonValueSource e) {
        try {
            mapAux(e);
        } catch (IOException e1) {
            Log.util.error("%s %e", e1, e1);
            return e;
        }
        return e;
    }

    private void mapAux(final JsonValueSource e) throws IOException {
        int j = 0;
        for (String field : srcField) {
            JsonNode o = (JsonNode) e.getValue(e, field);
            assignArray(e, targetField[j++], o);
        }
    }

    private void assignArray(final JsonValueSource e, final String targetF, final JsonNode o) throws IOException {
        if (o != null) {
            if (o.isArray()) {
                ArrayNode an = (ArrayNode) o;
                for (int i = 0; i < an.size(); i++) {
                    JsonNode n = an.get(i);
                    String txt = n.textValue();
                    if (txt == null) {
                        Console.println();
                    }
                    String s = nerMarkup.process(txt);
                    String tf = Fmt.S("%s[%s]", targetF, i);
                    e.setValue(e, tf, s);
                }
            } else {
                String s = nerMarkup.process(o.textValue());
                if (s == null) {
                    Console.println();
                }
                e.setValue(e, targetF, s);
            }

        }
    }


}

