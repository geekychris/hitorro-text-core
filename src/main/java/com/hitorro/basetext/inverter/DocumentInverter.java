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

import com.hitorro.basetext.dfindex.DFIndex;
import com.hitorro.basetext.dfindex.DFIndexSingletonMapper;
import com.hitorro.language.IsoLanguage;
import com.hitorro.obj.core.GenericAnalyzer;
import com.hitorro.util.core.events.cache.SingletonCache;
import com.hitorro.util.core.string.StringUtil;
import com.hitorro.util.io.ResetableStringReader;
import org.apache.lucene.analysis.TokenStream;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 */
public class DocumentInverter {
    private SingletonCache<DFIndex> m_cache = DFIndexSingletonMapper.getSingleton();
    private ResetableStringReader m_reader = new ResetableStringReader(null);
    private FrequencyTokenizerCollector m_col = new FrequencyTokenizerCollector();
    private TermMeasureFunction m_func;
    private Map<IsoLanguage, GenericAnalyzer> analyzers = new HashMap();
    private String field;
    private String filters;
    private Comparator<TermTuple> m_sortFunction;

    public DocumentInverter(String field, String filters, TermMeasureFunction func, Comparator<TermTuple> sortFunc) {
        this.field = field;
        this.filters = filters;

        setFunc(func, sortFunc);
    }

    public void setFunc(TermMeasureFunction func, Comparator<TermTuple> sortFunc) {
        m_func = func;
        m_sortFunction = sortFunc;
    }

    /**
     * Invert content and potentially computeFromSubjectArea a term measure and sort using the defined criteria.
     *
     * @param section
     * @param text
     * @return
     * @throws IOException
     */
    public TermTupleSet setText(String section, String text, IsoLanguage language) throws IOException {
        m_reader.set(text);
        return setText(section, language);
    }

    private TermTupleSet setText(String section, IsoLanguage language) throws IOException {
        TokenStream ts = getAnalyzer(language).tokenStream(field, m_reader);
        int wordCount = m_col.collect(ts);
        TermTupleSet set = new TermTupleSet();
        m_col.getTuples(section, set, wordCount);
        set.setWordCount(wordCount);
        set.setSectionName(section);
        return set;
    }

    private GenericAnalyzer getAnalyzer(IsoLanguage language) {
        GenericAnalyzer ga = analyzers.get(language);
        if (ga == null) {
            if (StringUtil.nullOrEmptyString(filters)) {
                ga = new GenericAnalyzer(GenericAnalyzer.Standard.apply(), language, GenericAnalyzer.Mode.Index);
            } else {
                ga = new GenericAnalyzer(filters, language, GenericAnalyzer.Mode.Index);
            }
            analyzers.put(language, ga);
        }
        return ga;
    }

}
