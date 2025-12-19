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
import com.hitorro.basetext.mappers.NERMarkup;
import com.hitorro.jsontypesystem.JVS;
import com.hitorro.language.Iso639Table;
import com.hitorro.language.IsoLanguage;
import com.hitorro.util.json.keys.propaccess.Propaccess;

import java.io.IOException;

public class NERMarkupMapper extends com.hitorro.jsontypesystem.dynamic.DynamicFieldMapper {
    @Override
    public JsonNode map(final JVS jvs, final Propaccess pa, final int depth) {
        JsonNode arr[] = getValues(jvs, pa, depth);
        if (arr.length > 1) {
            String langText = arr[0].textValue();
            IsoLanguage lang = Iso639Table.getInstance().getRow(langText);
            JsonNode valIn = arr[1];
            return apply(valIn, lang);

        }
        return null;
    }

    public ArrayNode apply(JsonNode arrayNode, IsoLanguage lang) {
        NERMarkup markup = new NERMarkup("SENTENCE,NAMEDENTITYMARKUP", lang);


        ArrayNode ret = JsonNodeFactory.instance.arrayNode();
        for (JsonNode elem : arrayNode) {
            try {
                String p = markup.process(elem.textValue());
                ret.add(JsonNodeFactory.instance.textNode(p));
            } catch (IOException e) {
                return null;
            }

        }
        return ret;

    }
}