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

/**
 *
 */
public class Winnower extends SetHash {
    private int winnowSize;
    private int lastEmmited = 0;
    private int currMinHash = Integer.MAX_VALUE;
    private SetHash next;

    public Winnower(int winnowSize, SetHash next) {
        this.next = next;
        this.winnowSize = winnowSize;
    }

    public final void setHash(int hash) {
        if (hash < currMinHash) {
            next.setHash(hash);
            currMinHash = hash;
            lastEmmited++;
            return;
        }
        if (lastEmmited >= winnowSize) {
            next.setHash(hash);
            lastEmmited = 0;
            currMinHash = Integer.MAX_VALUE;
        } else {
            lastEmmited++;
        }
    }

    public void reset() {
        lastEmmited = 0;
        currMinHash = Integer.MAX_VALUE;
        next.reset();
    }

}
