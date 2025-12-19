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
package com.hitorro.basetext.inverter.classifier.fphashmap;

import gnu.trove.map.hash.TLongLongHashMap;
import com.hitorro.basetext.inverter.TermCollection;
import com.hitorro.basetext.inverter.TermTuple;
import com.hitorro.basetext.inverter.classifier.ClassTestResult;
import com.hitorro.util.core.map.FPHashLongMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Rather than just collecting, we apply a simple barrage of tests, the first test that it succeeds at it gets placed
 * into that bucket.
 * <p/>
 * This code is optimized to reduce the amount of lookups in the fphash table, retrieving the value for the term only
 * once.  Tests are not run if the hash is not contained in the fphash table.  Tests can be setup to continue even if
 * they found a test, thus allowing them to be multi classified.  If your not using multi classification, be carefull
 * to ask your questions in the correct order!!!
 */
public class FPHashLongMapClassifyingTermCollection<E extends TermTuple> extends TermCollection<E> {
    private List<FPHashLongMapClassTest> tests = new ArrayList();
    private FPHashLongMap hashMap;
    private TLongLongHashMap map;

    public FPHashLongMapClassifyingTermCollection(FPHashLongMap map) {
        this.hashMap = map;
        this.map = map.getMap(0);
    }

    public void addTest(FPHashLongMapClassTest test) {
        tests.add(test);
    }

    public void add(E t) {
        if (map.contains(t.m_hash)) {
            // we have an entry
            long val = map.get(t.m_hash);
            ClassTestResult res;

            for (FPHashLongMapClassTest test : tests) {
                res = test.add(t, val);
                if (res == ClassTestResult.MatchNoContinue) {
                    return;
                }
            }
        }
    }
}


