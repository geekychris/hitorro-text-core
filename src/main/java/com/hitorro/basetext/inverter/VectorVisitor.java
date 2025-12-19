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

import gnu.trove.map.hash.TIntIntHashMap;
import com.hitorro.util.core.IDObjectMap;

public class VectorVisitor implements TermVisitor<TermTuple> {
    private TermVectorsGeneratorBase tvg;
    private IDObjectMap<String> idmap;
    private TIntIntHashMap masterCounts;

    public VectorVisitor(TermVectorsGeneratorBase tvg) {
        this.tvg = tvg;
        idmap = tvg.getIDMap();
        this.masterCounts = tvg.getMasterCounts();
    }

    public void clear() {

    }

    @Override
    public void add(TermTuple t) {
        String term = t.getTerm();
        t.id = idmap.getOrAdd(term, term);
        if (masterCounts.containsKey(t.id)) {
            masterCounts.increment(t.id);
        } else {
            masterCounts.put(t.id, 1);
        }
    }
}
