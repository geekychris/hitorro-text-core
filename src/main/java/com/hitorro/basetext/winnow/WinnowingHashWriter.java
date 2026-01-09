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
package com.hitorro.basetext.winnow;

import com.hitorro.util.core.Log;

/**
 */
public class WinnowingHashWriter implements HashWriter {
    int m_initialSize;
    int m_hash[];
    int currPtr = 0;
    int windowSize = 11;
    int hashCount = 0;
    private HashWriter m_writer;

    public WinnowingHashWriter(HashWriter writer, int initialSize, int windowSize) {
        m_writer = writer;
        m_initialSize = initialSize;
        m_hash = new int[initialSize];
        this.windowSize = windowSize;
    }

    public boolean close() {
        int i, j;
        int min = Integer.MAX_VALUE;
        int pos = 0;
        int temp_pos = 0;
        int prev_pos = 0;


        long time = System.currentTimeMillis();
        //fastWinnow(prev_pos, min, pos);
        slowWinnow(min, pos, prev_pos);
        long time2 = System.currentTimeMillis();
        Long val = new Long(time2 - time);
        Long count = new Long(hashCount);
        Log.util.debug("winnow: %s count: %s", val.toString(), count);
        return m_writer.close();
    }

    private final void slowWinnow(int min, int pos, int prev_pos) {
        int i;
        int j;
        int temp_pos;
        for (i = 0; i < currPtr - windowSize + 1; i++) {
            for (j = 0; j < windowSize; j++) {
                temp_pos = i + j;
                if (m_hash[temp_pos] <= min) {

                    pos = temp_pos;
                    min = m_hash[pos];
                }
            }
            // Slide forward and only write out hash once
            if (pos != prev_pos) {
                hashCount++;
                m_writer.write(m_hash[pos], pos);
                prev_pos = pos;
            }
            min = Integer.MAX_VALUE;
        }
    }

    private final void fastWinnow(int prev_pos, int min, int pos) {
        int i;
        int j;
        int temp_pos;
        for (i = 0; i < currPtr - windowSize + 1; i++) {
            int tempWindowSize = windowSize + i;
            for (j = prev_pos; j < tempWindowSize; j++) {
                temp_pos = j;
                if (m_hash[temp_pos] <= min) {

                    pos = temp_pos;
                    min = m_hash[pos];
                }
            }
            // Slide forward and only write out hash once
            if (pos != prev_pos) {
                hashCount++;
                m_writer.write(m_hash[pos], pos);
                prev_pos = pos;
            }
            min = Integer.MAX_VALUE;
        }
    }

    public boolean write(int hash, int position) {

        if (currPtr >= m_initialSize) {
            return false;
        }
        m_hash[currPtr] = hash;
        currPtr++;
        return true;
    }
}
