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

import com.hitorro.language.IsoLanguage;
import com.hitorro.obj.core.GenericAnalyzer;
import com.hitorro.util.io.ResetableStringReader;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;

/**
 *
 */
public class NERMarkup {
    private GenericAnalyzer ga;
    private ResetableStringReader rsr = new ResetableStringReader("");
    private StringBuilder sb = new StringBuilder();

    public NERMarkup(String filters, IsoLanguage lang) {
        ga = new GenericAnalyzer(filters, lang, GenericAnalyzer.Mode.Query);
    }

    public String process(String s) throws IOException {
        rsr.set(s);
        TokenStream ts = ga.tokenStream("foo", rsr);
        CharTermAttribute termAttribute = ts.getAttribute(CharTermAttribute.class);
        // PositionIncrementAttribute pia = ts.getAttribute(PositionIncrementAttribute.class);
        sb.setLength(0);

        while (ts.incrementToken()) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(termAttribute.toString());
        }
        ts.close();
        return sb.toString();
    }
}


