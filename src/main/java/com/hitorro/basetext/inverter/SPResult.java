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

import com.hitorro.util.core.math.SparseVector;
import com.hitorro.util.core.math.SparseVectorCosineDistance;

/**
 *
 */
public class SPResult implements Comparable<SPResult> {
    public static final SparseVectorCosineDistance distMeasure = new SparseVectorCosineDistance();
    public double distance;
    public SparseVector<TermTupleSet> l;
    public SparseVector<TermTupleSet> r;

    public SPResult(SparseVector<TermTupleSet> lIn, SparseVector<TermTupleSet> rIn) {
        compute(lIn, rIn);
    }

    public void compute(SparseVector<TermTupleSet> lIn, SparseVector<TermTupleSet> rIn) {
        l = lIn;
        r = rIn;
        distance = distMeasure.calculateEuclidDistanceSansSqrt(l, r);
    }

    public int compareTo(final SPResult o) {
        if (distance > o.distance) {
            return 1;
        }
        if (distance < o.distance) {
            return -1;
        }
        return 0;
    }
}
