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
package com.hitorro.basetext.phrase;

import com.hitorro.basetext.dfindex.FPHashDict;
import com.hitorro.util.core.Console;
import com.hitorro.util.core.hash.FPHash64;

/**
 * Responsible for managing a sliding window of n tokens from a lucene tokenizer.  The tokens are hashed independently
 * and then a window of 1->n tokens are combined.  Each hash is looked up in the provided hash dictionary.  If that hash
 * is found then the ngram phrase was found in the dictionary and the phraseMatch method is invoked.
 * <p/>
 * The intent is to find phrases (anything from 1 term to n terms) in a dictionary and emit that test. Because an
 * FPHashDict is used anything that can be attributed to that phrase that fits within a few bits of a long can be
 * expressed.  If not, you can use nbits of the long to represent some kind of external id.
 * <p/>
 * Cost: dictionary lookups ================== for w words we perform O(w*k) lookups.  At the end of the tokenization we
 * perform something like 1/2 k*k lookups.
 * <p/>
 * hash computations ================= Each individual token is hashed using the FPHash64 function once or O(w) for raw
 * hashes. for each window k we combine O(k-1) so combines for a w is O((k-1)*w))
 */
public class FPPhraseContext {
    protected StringBuilder m_builder = new StringBuilder();
    protected int maxDepth;
    protected char m_buffer[][];
    protected int bufferL[];

    protected long fpbuffer[];
    protected int m_fill = 0;
    protected int currRead = 0;
    protected boolean recordStrings = true;
    protected FPHashDict baseIndex;
    int counter = 0;
    private int charStartOffset;
    private int charEndOffset;

    public FPPhraseContext(int maxDepth, FPHashDict baseIndex, boolean recordStrings) {
        this.maxDepth = maxDepth;
        m_buffer = new char[maxDepth][];
        bufferL = new int[maxDepth];
        fpbuffer = new long[maxDepth];
        this.recordStrings = recordStrings;
        if (recordStrings) {
            for (int i = 0; i < maxDepth; i++) {
                m_buffer[i] = new char[20];
            }
        }
        this.baseIndex = baseIndex;
    }

    public void reset() {
        m_fill = 0;
        currRead = 0;
    }

    public void end() {
        while (m_fill > currRead) {
            getHashSlidingWindow(m_fill - currRead);
        }
    }

    protected String getSlidingWindowText(final int maxLength) {
        m_builder.setLength(0);
        for (int i = 0; i < maxLength; i++) {
            if (i > 0) {
                m_builder.append(" ");
            }
            m_builder.append(get(i + currRead));
        }
        return m_builder.toString();
    }

    protected final void getHashSlidingWindow(int depth) {
        if (this.recordStrings) {
            long fp = fpbuffer[currRead % maxDepth];
            // single word
            phraseEmit(fp, currRead, 1, getSlidingWindowText(1), charStartOffset, charEndOffset);
            int ind;
            for (int i = 1; i < depth; i++) {
                ind = i + currRead;
                fp = FPHash64.combineFingerPrints(fp, fpbuffer[ind % maxDepth]);
                phraseEmit(fp, currRead, i + 1, getSlidingWindowText(i + 1), charStartOffset, charEndOffset);
            }
            currRead++;
        } else {
            long fp = fpbuffer[currRead % maxDepth];
            // single word
            phraseEmit(fp, currRead, 1, null, charStartOffset, charEndOffset);
            int ind;
            for (int i = 1; i < depth; i++) {
                ind = i + currRead;
                fp = FPHash64.combineFingerPrints(fp, fpbuffer[ind % maxDepth]);
                phraseEmit(fp, currRead, i + 1, null, charStartOffset, charEndOffset);
            }
            currRead++;
        }
    }


    /**
     * Overide this method to receive notifications of matching fingerprints
     *
     * @param fingerPrint
     * @param startPosition
     * @param size
     * @param txt
     */
    public void phraseEmit(long fingerPrint, int startPosition, int size, String txt, int charStartOffset, int charEndOffset) {
        int layer = baseIndex.hasPhrase(fingerPrint);
        if (layer != -1) {
            long val = baseIndex.getValue(fingerPrint, layer);
            phraseMatch(fingerPrint, startPosition, size, txt, val, layer, charStartOffset, charEndOffset);
        }
    }

    public void phraseMatch(long fingerPrint, int startPosition, int size, String txt,
                            long val, int layer, int charStartOffset, int charEndOffset) {
        Console.println(">>> %s %s %s %s val:%s layer:%s", fingerPrint, startPosition, size, txt, val, layer);
    }

    protected final String get(final int i) {
        int ind = i % maxDepth;
        return new String(m_buffer[ind], 0, bufferL[ind]);
    }

    protected void add(final char buff[], int length, int charStartOffset, int charEndOffset) {
        this.charStartOffset = charStartOffset;
        this.charEndOffset = charEndOffset;
        int ind = m_fill % maxDepth;
        if (recordStrings) {
            if (m_buffer[ind].length < length) {
                m_buffer[ind] = new char[length];
            }
            System.arraycopy(buff, 0, m_buffer[ind], 0, length);
            bufferL[ind] = length;
        }
        fpbuffer[ind] = FPHash64.getFingerprint(buff, length);

        m_fill++;
        if (m_fill >= maxDepth) {
            getHashSlidingWindow(maxDepth);
        }
        // XXX TEST
        //has = true;
    }
}
