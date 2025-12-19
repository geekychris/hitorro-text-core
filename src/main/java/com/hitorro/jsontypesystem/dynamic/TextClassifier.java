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
import com.hitorro.basetext.maxentclassifier.BaseClassifier;
import com.hitorro.basetext.maxentclassifier.ChunkParser;
import com.hitorro.basetext.maxentclassifier.OpenNLPClosures;
import com.hitorro.jsontypesystem.JVS;
import com.hitorro.language.Iso639Table;
import com.hitorro.language.IsoLanguage;
import com.hitorro.language.Models;
import com.hitorro.util.json.keys.propaccess.Propaccess;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;

import java.util.function.Function;

public class TextClassifier extends com.hitorro.jsontypesystem.dynamic.DynamicFieldMapper {
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
        Function<ChunkParser, ArrayNode> function = (parser) -> {
            BaseClassifier classifier = null;
            try {
                classifier = Models.answerTypeSingleton.get(lang);
                ArrayNode ret = JsonNodeFactory.instance.arrayNode();
                for (JsonNode elem : arrayNode) {
                    Parse parse = ParserTool.parseLine(elem.textValue(), parser, 1)[0];
                    String p = classifier.getClassification(parse);
                    ret.add(p);
                }
                return ret;
            } finally {
                Models.answerTypeSingleton.returnIt(lang, classifier);
            }
        };

        return OpenNLPClosures.chunkParser(lang, function);
    }
}