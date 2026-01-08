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

/**
 * <p/>
 * Classic TFIDF calculation.
 */
public class TFIDFTermMeasureFunction extends TermMeasureFunction {
    // if we can set a low water mark, if the work is too rare we dont want to consider it either.

    private double m_dfMinTollerance = 0.30;


    public TFIDFTermMeasureFunction() {

    }

    /**
     * @param normalizedTF Fraction of term occurrences in section set
     * @param normalizedDF Fraction of document the term occurs in TP DF corpus
     * @return TFIDF: The importance of this term in the document
     */
    public static final double computeClassicTFIDF(double normalizedTF, double normalizedDF) {
        return normalizedTF / normalizedDF * TermTuple.DFMultiFactor;
    }

    public boolean getNeedsDFIndex() {
        return true;
    }

    public void setDFIndex(DFIndex index) {
        super.setDFIndex(index);
        //should calculate mindf as a % of docs or absolute....like at least 2 docs.

    }

    public boolean compute(TermTuple tt, TermTupleSet set) {
        if (m_dfIndex == null) {
            return false;
        }
        int df = m_dfIndex.getFrequency(tt.m_hash);
        tt.m_df = df;
        tt.m_isTermMeasure = true;
        double dfD = tt.computeNormalizedDF(df, m_corpusSize);
        if (dfD < m_dfMinTollerance) {
            // We hit a df level that is not acceptable
            tt.m_termMeasure = -Double.MAX_VALUE;
            tt.m_isGoodValue = false;
        } else {
            tt.m_termMeasure = computeClassicTFIDF(tt.m_normTf, dfD);
        }
        return true;
    }

}
