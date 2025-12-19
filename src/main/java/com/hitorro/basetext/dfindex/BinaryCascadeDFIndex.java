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
package com.hitorro.basetext.dfindex;

/**
 * uses recursion as the typical depth is probably 1 or 2 deep and this does not require the setup of a loop best case
 */
public class BinaryCascadeDFIndex implements BaseDFIndexInterface {
    private BaseDFIndexInterface arr[];
    private int size;
    private BaseDFIndexInterface second;

    public BinaryCascadeDFIndex(BaseDFIndexInterface elems[]) {
        arr = elems;
        size = elems.length;
    }

    @Override
    public int hasPhrase(final long fp) {
        if (arr[0].hasPhrase(fp) != -1) {
            return 0;
        }
        return hasPhrasePrivate(fp, 1);
    }

    public int hasPhrasePrivate(final long fp, int depth) {
        if (depth < size) {
            if (arr[depth].hasPhrase(fp) != -1) {
                return depth;
            }
            return hasPhrasePrivate(fp, depth + 1);
        }
        return -1;
    }

    @Override
    public long getValue(final long fp, final int layer) {
        return arr[layer].getValue(fp, layer);
    }
}
