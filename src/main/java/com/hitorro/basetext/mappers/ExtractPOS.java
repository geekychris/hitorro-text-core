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
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hitorro.util.core.Console;
import com.hitorro.util.core.events.cache.PoolContainer;
import com.hitorro.util.core.iterator.mappers.BaseMapper;
import com.hitorro.util.core.string.StringUtil;

import java.util.List;

/**
 * Works in the world of Json objects
 */
public class ExtractPOS extends BaseMapper<JsonNode, ObjectNode> {
    private com.hitorro.language.IsoLanguage lang;
    private PoolContainer<com.hitorro.language.IsoLanguage, com.hitorro.language.PartOfSpeech> pool = null;
    private com.hitorro.language.PartOfSpeech ss;
    private BaseMapper<String, String> intermediateMapper;


    private StringBuilder sb = new StringBuilder();

    public ExtractPOS(com.hitorro.language.IsoLanguage lang, BaseMapper<String, String> intermediateMapper) {
        this.lang = lang;
        pool = com.hitorro.language.PartOfSpeechSingletonMapper.singleton.get(lang);
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
        return new ExtractPOS(lang, intermediateMapper);
    }

    @Override
    public ObjectNode apply(final JsonNode e) {
        ObjectNode retM = JsonNodeFactory.instance.objectNode();
        if (e.isArray()) {
            ArrayNode an = (ArrayNode) e;
            for (JsonNode jn : an) {
                getAux(jn, retM);
            }
        } else {
            getAux(e, retM);
        }
        return retM;
    }

    private void getAux(final JsonNode e, ObjectNode retM) {
        String txt = e.textValue();

        if (intermediateMapper != null) {
            txt = intermediateMapper.apply(txt);
        }

        com.hitorro.language.POS pos = ss.getPOS(txt);

        String toks[] = pos.getTokenizedText();

        List<String>[] arr = pos.getTags();
        List<String> tagRow = arr[0];
        String prevTag = null;
        String tok = null;
        String tag = null;
        for (int i = 0; i < toks.length; i++) {
            tok = toks[i];

            tag = tagRow.get(i);

            com.hitorro.language.PATElem pe = lang.getPennAndTreebank().getValue(tag);
            if (pe != null) {
                tag = pe.getParentString();
                if (tag == null || tag.equals("null")) {
                    Console.println();
                }
            }
            if (tag == null || tok == null) {
                Console.println();
            }


            if (prevTag != null && prevTag.equals(tag)) {
                // same tag, extend
                sb.append(" ");
                sb.append(tok);
            } else {
                // first deal with change.
                addToMap(retM, sb.toString(), prevTag);
                sb.setLength(0);
                sb.append(tok);
                prevTag = tag;
            }

        }
        addToMap(retM, sb.toString(), prevTag);
        sb.setLength(0);
    }

    private void addToMap(final ObjectNode retM, String tok, String tag) {
        if (StringUtil.nullOrEmptyString(tok)) {
            return;
        }
        if (StringUtil.nullOrEmptyString(tag)) {
            return;
        }
        tok = tok.toLowerCase();
        ArrayNode tokArr = (ArrayNode) retM.get(tag);
        if (tokArr == null) {
            tokArr = JsonNodeFactory.instance.arrayNode();
            retM.put(tag, tokArr);
        }
        tokArr.add(tok);
    }

    public void finalize() {
        if (ss != null) {
            pool.returnIt(ss);
        }
    }

}

