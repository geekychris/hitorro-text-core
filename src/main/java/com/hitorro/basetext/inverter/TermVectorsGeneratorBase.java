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
package com.hitorro.basetext.inverter;

import com.fasterxml.jackson.databind.JsonNode;
import gnu.trove.map.hash.TIntIntHashMap;
import com.hitorro.language.IsoLanguage;
import com.hitorro.util.core.IDObjectMap;
import com.hitorro.util.core.opers.AlwaysTrueOperator;
import com.hitorro.util.core.string.StringUtil;

import java.io.IOException;

public class TermVectorsGeneratorBase {
    protected IDObjectMap<String> termsId = new IDObjectMap();
    protected TIntIntHashMap masterCounts = new TIntIntHashMap();
    protected String analyzer;
    protected IsoLanguage lang;
    protected VectorVisitor visitor;
    protected TFRowVisitor TFRowVisitor;

    public TermVectorsGeneratorBase(String analyzer, IsoLanguage lang) {
        this.analyzer = analyzer;
        this.lang = lang;
        visitor = new VectorVisitor(this);
        TFRowVisitor = new TFRowVisitor();
    }

    public JsonNode add(String... fieldText) throws IOException {
        String txt = StringUtil.mergeWithJoinToken(fieldText, "");


        String name = "default";
        TermTupleSetGroup group = InverterUtils.getTupleSetFromTextWithFilterTF(analyzer, name, txt, "xxx", lang);
        TermTupleSet tts = group.getByName(name);
        visitor.clear();
        tts.visit(visitor, AlwaysTrueOperator.oper);
        tts.sortByID();

        TFRowVisitor.clear();
        tts.visit(TFRowVisitor, AlwaysTrueOperator.oper);
        JsonNode ret = TFRowVisitor.getRow();
        TFRowVisitor.clear();
        return ret;
    }

    public TIntIntHashMap getMasterCounts() {
        return masterCounts;
    }

    public IDObjectMap<String> getIDMap() {
        return termsId;
    }

    public String getAnalyzer() {
        return analyzer;
    }

}
