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
package com.hitorro.obj.core;

import com.hitorro.language.IsoLanguage;
import com.hitorro.util.typesystem.TypeField;
import com.hitorro.util.typesystem.TypeFieldIntf;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class AnalyzerPerLanguage {
    private List<TypeFieldIntf> fields;
    private GenericAnalyzer.Mode mode;
    private Map<IsoLanguage, Analyzer> analyzers = new HashMap();

    public AnalyzerPerLanguage(List<TypeFieldIntf> fields, GenericAnalyzer.Mode mode) {
        this.fields = fields;
        this.mode = mode;
    }

    public synchronized Analyzer getForLanguage(IsoLanguage language) {
        Analyzer a = analyzers.get(language);
        if (a != null) {
            return a;
        }
        Map<String, Analyzer> map = new HashMap();
        for (TypeFieldIntf tf : fields) {
            if (mode == GenericAnalyzer.Mode.Query) {
                map.put(tf.getFullTextMeta().luceneFieldName(), ((TypeField) tf).getSearchAnalyzer(language));
            } else {
                map.put(tf.getFullTextMeta().luceneFieldName(), ((TypeField) tf).getIndexingAnalyzer(language));
            }

        }
        PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), map);

        analyzers.put(language, analyzer);
        return analyzer;
    }
}