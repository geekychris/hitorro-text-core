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
package com.hitorro.basetext.maxentclassifier;

import com.hitorro.language.IsoLanguage;
import opennlp.tools.parser.Parse;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class AnswerTypeContextGenerator extends AbstractQuestionContextGenerator {
    private IsoLanguage lang;

    /**
     * @param lang
     */
    public AnswerTypeContextGenerator(IsoLanguage lang) {
        this.lang = lang;
    }

    public String[] getContext(Parse query) {
        Parse focalNoun = null;
        String queryWord = null;
        List<String> features = new ArrayList<String>();
        features.add("def");
        Parse[] nps = getNounPhrases(query);
        Parse[] toks = query.getTagNodes();
        getQueryWordAndFocalNoun(focalNoun, queryWord, features, nps, toks);
        return (features.toArray(new String[features.size()]));
    }
}
