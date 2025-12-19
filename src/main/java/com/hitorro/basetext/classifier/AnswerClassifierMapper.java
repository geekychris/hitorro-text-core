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
package com.hitorro.basetext.classifier;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.hitorro.basetext.maxentclassifier.AnswerTypeClassifier;
import com.hitorro.basetext.maxentclassifier.ChunkParser;
import com.hitorro.util.core.events.cache.PoolContainer;
import com.hitorro.util.core.iterator.JsonValueSource;
import com.hitorro.util.core.iterator.mappers.BaseMapper;
import com.hitorro.util.core.string.Fmt;
import com.hitorro.util.core.string.StringUtil;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.parser.Parse;

/**
 *
 */
public class AnswerClassifierMapper extends BaseMapper<JsonValueSource, String> {
    private static String[][] bestMatch = {{"posts.subject_class_sents", "posts.subject_sents", "Q"},
            {"posts.detail_class_sents", "posts.detail_sents", "Q"}};
    public int counter = 0;
    ChunkParser parser;
    PoolContainer<com.hitorro.language.IsoLanguage, com.hitorro.language.PartOfSpeech> pool = null;
    com.hitorro.language.PartOfSpeech pos = null;

    DoccatModel answerTypeModel;
    double answerProbs[];
    AnswerTypeClassifier answerTypeClassifier;
    com.hitorro.language.IsoLanguage lang = com.hitorro.language.Iso639Table.english;
    private boolean oldFields = false;


    public AnswerClassifierMapper() {
        pool = com.hitorro.language.PartOfSpeechSingletonMapper.singleton.get(lang);
        pos = pool.get();
        parser = new ChunkParser(new ChunkerME(pos.getChunker()), pos.getPOSTagger());

        answerTypeModel = com.hitorro.language.DoccatModelSingletonMapper.answertypeclassifier.get(lang);

        answerProbs = new double[answerTypeModel.getMaxentModel().getNumOutcomes()];
        com.hitorro.language.IsoLanguage lang = com.hitorro.language.Iso639Table.english;

        answerTypeClassifier = new AnswerTypeClassifier(answerTypeModel, lang);
    }

    public boolean isThreadSafe() {
        return false;
    }

    public BaseMapper getCopy() {
        return new AnswerClassifierMapper();
    }

    public void close() {
        if (pool != null) {
            pool.returnIt(pos);
        }
    }

    @Override
    public String apply(final JsonValueSource e) {
        JsonValueSource res = new JsonValueSource();

        res.setValue(null, "docid", Integer.valueOf(counter));

        String question = selectBest(e);
        if (StringUtil.nullOrEmptyString(question)) {

            return Fmt.S("Unknown %s", question);
        }
        String cat = processCategory(question);
        return Fmt.S("%s %s", cat, question);
    }

    private String processCategory(String sentence) {
        Parse parse = ParserTool.parseLine(sentence, parser, 1)[0];
        return answerTypeClassifier.getClassification(parse);
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

