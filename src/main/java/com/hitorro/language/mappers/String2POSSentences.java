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
package com.hitorro.language.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hitorro.basetext.maxentclassifier.OpenNLPClosures;
import com.hitorro.language.Iso639Table;
import com.hitorro.language.IsoLanguage;
import com.hitorro.language.POS;
import com.hitorro.language.PartOfSpeech;
import com.hitorro.util.core.iterator.mappers.BaseMapper;

import java.util.function.Function;

public class String2POSSentences extends BaseMapper<JsonNode, ArrayNode> {
    private IsoLanguage defaultLang;
    private BaseMapper<String, String> intermediateMapper;

    public String2POSSentences(IsoLanguage lang, BaseMapper<String, String> intermediateMapper) {
        this.defaultLang = lang;
        this.intermediateMapper = intermediateMapper;
    }

    public String2POSSentences() {
        defaultLang = Iso639Table.english;
    }

    public ArrayNode apply(JsonNode txt) {
        return apply(txt, Iso639Table.english);
    }


    public ArrayNode apply(JsonNode txtNode, IsoLanguage lang) {
        Function<PartOfSpeech, ArrayNode> function = (pos) -> {
            String sentence = txtNode.textValue();

            POS p = pos.getPOS(sentence);
            String text[] = p.getTokenizedText();
            String pe0[] = p.getTagsEnglish(0);
            ArrayNode row = JsonNodeFactory.instance.arrayNode();
            for (int i = 0; i < text.length; i++) {
                ObjectNode node = JsonNodeFactory.instance.objectNode();
                node.set(text[i], JsonNodeFactory.instance.textNode(pe0[i]));
                row.add(node);
            }
            return row;
        };

        return OpenNLPClosures.sentencePOS(lang, function);
    }
}

