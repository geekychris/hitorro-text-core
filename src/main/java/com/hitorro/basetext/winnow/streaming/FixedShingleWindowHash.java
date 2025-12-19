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

import com.hitorro.util.core.Console;

/**
 *
 */
public class FixedShingleWindowHash extends SetHash {
    int counter = 0;
    private IntPriorityQueue queue;

    public FixedShingleWindowHash(int fixedSize) {
        queue = new IntPriorityQueue(fixedSize);
    }

    public void setHash(int hash) {
        counter++;
        queue.insert(hash);
    }

    public void reset() {
        counter = 0;
        queue.clear();
    }

    public void dump(int[] hashes) {
        for (int i = 0; i < hashes.length; i++) {
            Console.println("QueueValue: %s", hashes[i]);
        }
    }

    public int[] toSortedArray() {
        int arr[] = new int[queue.size()];
        for (int i = queue.size() - 1; i >= 0; i--) {
            arr[i] = queue.pop();
        }
        return arr;
    }
}
