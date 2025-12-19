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
import com.hitorro.util.core.iterator.JsonValueSource;
import com.hitorro.util.core.iterator.mappers.BaseMapper;
import com.hitorro.util.core.string.StringUtil;

/**
 * Created with IntelliJ IDEA. User: chris Date: 8/8/12 Time: 6:21 PM To change this template use File | Settings | File
 * Templates.
 */
public class NullLVMapping extends BaseMapper<JsonValueSource, JsonValueSource> {
    private static String[][] bestMatch = {{"posts.subject_class_sents", "posts.subject_sents", "Q"},
            {"posts.detail_class_sents", "posts.detail_sents", "Q"}};
    public int counter = 0;
    private boolean oldFields = false;

    @Override
    public JsonValueSource apply(final JsonValueSource e) {
        JsonValueSource res = new JsonValueSource();
        if (oldFields) {
            res.setValue(null, "postsubject", "postsubject");
            res.setValue(null, "postdetails", "postdetails");
            res.setValue(null, "reply", "reply");
        }

        res.setValue(null, "docid", "1");

        String question = selectBest(e);
        if (!StringUtil.nullOrEmptyString(question)) {
            res.setValue(null, "question", "my question");
        }

        //addArrayIfNotNull(e, res, "details", "posts.detail_ner_sents", "posts.detail_sents");
        //addArrayIfNotNull(e, res, "subject", "posts.subject_ner_sents", "posts.subject_sents");
        //addArrayIfNotNull(e, res, "reply", "replies.reply_ner_sents", "replies.reply_sents");

        ObjectNode node = JsonNodeFactory.instance.objectNode();
        ObjectNode addNode = JsonNodeFactory.instance.objectNode();
        node.set("add", addNode);
        addNode.set("doc", res.getNode());


        return res;
    }

    private void addArrayIfNotNull(JsonValueSource in, JsonValueSource out, String targetPath, String... paths) {
        for (String path : paths) {
            JsonNode jn = (JsonNode) in.getValue(in, path);
            if (jn != null) {
                out.setValue(out, targetPath, jn);
                return;
            }
        }
    }

    private String selectBest(JsonValueSource vs) {
        for (String parts[] : bestMatch) {
            String s = selectBestQuestionAux(vs, parts[0], parts[1], parts[2]);
            if (!StringUtil.nullOrEmptyOrBlankString(s)) {
                return s;
            }
        }
        return null;
    }

    private String selectBestQuestionAux(JsonValueSource vs, String classField, String sentField, String matchMe) {
        ArrayNode an = (ArrayNode) vs.getValue(vs, classField);
        if (an == null) {
            return null;
        }
        for (int i = 0; i < an.size(); i++) {
            String f = an.get(i).textValue();
            if (matchMe.equalsIgnoreCase(f)) {
                ArrayNode sent = (ArrayNode) vs.getValue(vs, sentField);
                if (sent != null) {
                    return sent.get(i).textValue();
                }
            }
        }
        return null;
    }

}

