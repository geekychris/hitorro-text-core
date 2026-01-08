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


import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.TObjectIntHashMap;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;

/**
 */
public class FrequencyTokenizerCollector {
    private TObjectIntHashMap m_map = new TObjectIntHashMap();

    /**
     * Read all of the stream, accumulating the frequencies of the terms found into a hashtable
     *
     * @param ts
     * @return number of words in document section
     * @throws IOException
     */
    public int collect(TokenStream ts) throws IOException {
        int count = 0;
        //XXX RESET???
        ts.reset();
        CharTermAttribute termAttribute = ts.getAttribute(CharTermAttribute.class);
        m_map.clear();
        while (ts.incrementToken()) {
            count++;
            String term = termAttribute.toString();
            int i = m_map.get(term);
            if (i == 0) {
                m_map.put(term, 1);
            } else {
                m_map.increment(term);
            }
        }
        ts.close();
        return count;
    }


    public void getTuples(String section, TermTupleSet set, int sectionSize) {
        for (TObjectIntIterator it = m_map.iterator(); it.hasNext(); ) {
            it.advance();
            TermTuple tt = new TermTuple();
            tt.set((String) it.key(), it.value());
            tt.normalizeTF(sectionSize);
            set.add(tt);
        }
    }

    public void debugValuesDump() {
        TObjectIntIterator iter = m_map.iterator();
        for (TObjectIntIterator it = m_map.iterator(); it.hasNext(); ) {
            it.advance();
            System.out.println(it.key() + " : " + it.value());
        }
    }

}
