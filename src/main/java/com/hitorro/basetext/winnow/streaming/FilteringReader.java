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
 * Reader that lower cases and removes anything that isnt a character or a digit.
 */
public class FilteringReader extends Reader {
    private Reader r;
    private char[] charsInt = new char[1024];
    private int count;

    public void setReader(Reader r) {
        this.r = r;
    }

    public int read(char[] chars, int i, int i1) throws IOException {
        ensureBigEnough(i1);
        int l = r.read(charsInt, 0, i1);
        count = 0;
        if (l == -1) {
            return l;
        }
        char c;
        for (int s = 0; s < l; s++) {
            c = charsInt[s];
            if (Character.isDigit(c)) {
                chars[i + count++] = c;
            } else {
                if (Character.isLetter(c)) {
                    chars[i + count++] = Character.toLowerCase(c);
                }
            }
        }
        return count;
    }

    public void close() throws IOException {

    }

    private final void ensureBigEnough(int length) {
        if (length < charsInt.length) {
            charsInt = new char[length];
        }
    }
}
