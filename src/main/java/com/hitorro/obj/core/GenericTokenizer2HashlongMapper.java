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
import com.hitorro.util.core.hash.FPHash64;
import com.hitorro.util.core.iterator.mappers.BaseMapper;
import com.hitorro.util.io.ResetableStringReader;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;

/**
 * Mapper that is used to take a string and convert to a fp64 hash.  It uses the generic analyzer chain todo this
 * and honors word boundaries.  This can be used for computing phrase hashes that can be used is subsequent genericanalyzer
 * "emitphrase" tokenization chains.
 */
public class GenericTokenizer2HashlongMapper extends BaseMapper<String, Long> {
    private GenericAnalyzer analyzer;
    private ResetableStringReader reader = new ResetableStringReader(null);

    private IsoLanguage lang;

    public GenericTokenizer2HashlongMapper(String analyzerString, IsoLanguage lang) {
        this.lang = lang;
        analyzer = new GenericAnalyzer(analyzerString, lang, GenericAnalyzer.Mode.Index);
    }

    @Override
    public Long apply(final String string) {
        reader.set(string);
        TokenStream ts = analyzer.tokenStream("body", reader);
		try {
			ts.reset();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		CharTermAttribute termAttLocal = ts.addAttribute(CharTermAttribute.class);
        long last = 0;

        try {
            while (ts.incrementToken()) {

                String tok = termAttLocal.toString();
                if (last == 0) {
                    last = FPHash64.getFPViaChars(tok);
                } else {
                    last = FPHash64.combineFingerPrints(last, FPHash64.getFPViaChars(tok));
                }
            }
            ts.close();
        } catch (IOException e) {
            return new Long(0);
        }
        return last;
    }
}




