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
package com.hitorro.basetext.winnow.streaming;

import java.io.IOException;
import java.io.Reader;

/**
 *
 */
public class HashBase {
    private int buffSize = 1024;
    private int[] buff = new int[buffSize];
    private int curr = 0;
    private int hashingWindowSize;


    // pow(2, window - ith position) for purposes of generating hashes.
    private int mult[];

    private SetHash nextStep;

    public HashBase(int windowSize, SetHash nextStep) {
        this.hashingWindowSize = windowSize;
        this.nextStep = nextStep;
        mult = new int[windowSize];
        for (int i = 0; i < windowSize; i++) {
            mult[i] = 1 << (windowSize - i - 1);
        }
    }

    public void reset() {
        curr = 0;
        nextStep.reset();
    }

    public void read(Reader r) throws IOException {
        reset();
        char buf[] = new char[10];
        int i = r.read(buf);
        StringBuilder sb = new StringBuilder();
        while (i != -1) {
            for (int j = 0; j < i; j++) {
                set(buf[j]);
            }
            i = r.read(buf);
        }
    }

    private final void set(int v) {
        buff[curr++ % buffSize] = v;
        if (curr >= hashingWindowSize) {
            int hash = 0;
            int wStart = curr - hashingWindowSize;
            for (int i = 0; i < hashingWindowSize; i++) {
                hash += buff[(wStart + i) % buffSize] * mult[i];
            }
            nextStep.setHash(hash);

        }
    }
}
