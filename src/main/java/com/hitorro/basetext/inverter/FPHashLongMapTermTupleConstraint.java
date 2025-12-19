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
import com.hitorro.util.core.HTAssert;
import com.hitorro.util.core.longword.opers.LongOperator;
import com.hitorro.util.core.map.FPHashLongMap;
import com.hitorro.util.core.opers.HTPredicate;

/**
 * Visit each TermTuple and see if via a the long apply lookup, has a test with the logical constraint.  That is:
 * <p/>
 * lookup TermTuple->hash in the FPHashLongMap.  If it exists, take its longword and see if it matches the long word
 * constraint.
 * <p/>
 * For example, lets say we have a FPHash that represents a dictionary and the longword had a porn bit at position "0".
 * We could lookup the word and if it has bit 0 set, its a test
 */
public class FPHashLongMapTermTupleConstraint<E extends TermTuple> implements HTPredicate<E> {
    private FPHashLongMap map;
    private LongOperator constraint;

    public FPHashLongMapTermTupleConstraint(FPHashLongMap m, LongOperator c) {
        map = m;
        constraint = c;
    }

    public boolean initFromMap(final JsonNode map) {
        // MUST IMPLEMENT
        HTAssert.assertThat(false, "FPHashLongMapTermTupleConstraint.initFromMap not implemented");
        return false;
    }

    public void initForPass() {

    }

    public boolean test(final E e) {
        long fp = e.m_hash;
        return map.match(fp, constraint, 0) != -1;
    }
}
