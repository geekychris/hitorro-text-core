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
package com.hitorro.jsontypesystem.dynamic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.hitorro.jsontypesystem.JVS;
import com.hitorro.language.mappers.String2SegmentedSentences;
import com.hitorro.util.json.keys.propaccess.Propaccess;
import opennlp.tools.util.Span;


public class SentenceSegmenter extends com.hitorro.jsontypesystem.dynamic.DynamicFieldMapper {
    private static String2SegmentedSentences mapper = new String2SegmentedSentences();

    public static Span[] getSpans(JsonNode n, String text) {
        int i = 0;
        Span spans[] = new Span[n.size()];
        if (n != null && n.isArray()) {
            ArrayNode ret = JsonNodeFactory.instance.arrayNode();
            for (JsonNode row : n) {
                int s = row.get(0).intValue();
                int e = row.get(1).intValue();
                spans[i++] = new Span(s, e, text);
            }
        }
        return spans;
    }

    @Override
    public JsonNode map(final JVS jvs, final Propaccess pa, final int depth) {
        JsonNode arr[] = getValues(jvs, pa, depth);
        if (arr.length > 1) {
            if (arr[0] == null) {
                return null;
            }
            JsonNode n = arr[0];
            String text = arr[1].textValue();
            if (n != null && n.isArray()) {
                ArrayNode ret = JsonNodeFactory.instance.arrayNode();
                for (JsonNode row : n) {
                    int s = row.get(0).intValue();
                    int e = row.get(1).intValue();
                    ret.add(text.substring(s, e));
                }
                return ret;
            }
            return null;
        }
        return null;
    }
}
