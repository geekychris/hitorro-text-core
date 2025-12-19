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

import com.hitorro.basetext.inverter.TermTuple;
import com.hitorro.basetext.inverter.classifier.BaseContainerClassTest;
import com.hitorro.basetext.inverter.classifier.ClassTestResult;
import com.hitorro.util.core.longword.opers.LongOperator;

/**
 * For all terms that test this constraint we retain a reference to that term.
 */
public class ContainerFPHashLongMapClassTest<E extends TermTuple> extends BaseContainerClassTest<E> {
    LongOperator oper;

    public ContainerFPHashLongMapClassTest(LongOperator oper, boolean continueAfterMatch) {
        this.oper = oper;
        this.continueAfterMatch = continueAfterMatch;
    }

    public ClassTestResult test(E t, long val) {
        if (oper.match(val)) {
            container.add(t);
            if (continueAfterMatch) {
                return ClassTestResult.MatchContinue;
            } else {
                return ClassTestResult.MatchNoContinue;
            }
        }
        return ClassTestResult.NoMatch;
    }
}
