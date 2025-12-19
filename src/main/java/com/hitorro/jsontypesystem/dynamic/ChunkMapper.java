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
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hitorro.basetext.maxentclassifier.ChunkParser;
import com.hitorro.basetext.maxentclassifier.OpenNLPClosures;
import com.hitorro.jsontypesystem.JVS;
import com.hitorro.language.Iso639Table;
import com.hitorro.language.IsoLanguage;
import com.hitorro.util.core.iterator.mappers.BaseMapper;
import com.hitorro.util.json.keys.propaccess.Propaccess;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;

import java.util.function.Function;

public class ChunkMapper extends com.hitorro.jsontypesystem.dynamic.DynamicFieldMapper {
    private static ChunkRowMapper mapper = new ChunkRowMapper();

    @Override
    public JsonNode map(final JVS jvs, final Propaccess pa, final int depth) {
        JsonNode arr[] = getValues(jvs, pa, depth);
        if (arr.length > 1) {
            String langText = arr[0].textValue();
            IsoLanguage lang = Iso639Table.getInstance().getRow(langText);
            JsonNode valIn = arr[1];
            return mapper.apply(valIn, lang);

        }
        return null;
    }
}

class ChunkRowMapper extends BaseMapper<JsonNode, ArrayNode> {
    private IsoLanguage defaultLang;

    public ChunkRowMapper(IsoLanguage lang) {
        this.defaultLang = lang;
    }

    public ChunkRowMapper() {
        defaultLang = Iso639Table.english;
    }

    public ArrayNode apply(JsonNode txt) {
        return apply(txt, Iso639Table.english);
    }

    public ArrayNode apply(JsonNode arrayNode, IsoLanguage lang) {
        Function<ChunkParser, ArrayNode> function = (parser) -> {
            ArrayNode ret = JsonNodeFactory.instance.arrayNode();
            for (JsonNode elem : arrayNode) {
                Parse parse = ParserTool.parseLine(elem.textValue(), parser, 1)[0];
                ret.add(getParse(parse));
            }
            return ret;
        };

        return OpenNLPClosures.chunkParser(lang, function);
    }

    private JsonNode getParse(Parse p) {
        String type = p.getType();
        Parse children[] = p.getChildren();
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("type", type);
        node.put("ss", p.getSpan().getStart());
        node.put("se", p.getSpan().getEnd());
        ArrayNode c = JsonNodeFactory.instance.arrayNode();
        node.set("c", c);
        node.set("t", JsonNodeFactory.instance.textNode(p.getText().substring(p.getSpan().getStart(), p.getSpan().getEnd())));
        for (Parse pChild : children) {
            c.add(getParse(pChild));
        }
        return node;
    }
}

