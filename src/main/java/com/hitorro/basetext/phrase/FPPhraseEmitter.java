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

import com.hitorro.basetext.dfindex.BaseDFIndexInterface;
import com.hitorro.util.core.hash.FPHash64;

/**
 * Copyright (c) 2003 - present HiTorro All rights reserved.
 * <p/>
 * User: chris
 * <p>
 * This guy and PhraseFilter needs to be re-written.  Given "the quick brown fox jumped over the lazy dogs" we get:
 * <p>
 * the
 * quick
 * brown
 * the quick
 * fox
 * jumped
 * over
 * the
 * jumped over the
 * lazy
 * dogs
 * lazy dogs
 * <p>
 * Also the logic seems a little complex and convoluted for the management of a sliding window of words.
 */
public class FPPhraseEmitter extends PhraseEmitter {
    protected BaseDFIndexInterface index;
    protected long fps[];
    protected String buf[];

    protected int queueSize = 0;
    protected int addPtr = 0;

    public FPPhraseEmitter(int maxDepth, int minDepth, BaseDFIndexInterface index) {
        super(maxDepth, minDepth);
        fps = new long[maxDepth];
        this.index = index;
        buf = new String[maxDepth];
    }

    protected final void add(String tok) {
        int pos = m_fill % m_maxDepth;
        m_buffer[pos] = tok;
        fps[pos] = FPHash64.getFP(tok);
        m_fill++;
    }

    public final int getOutputSize() {
        return queueSize;
    }

    public String getRow() {
        int ind = (addPtr - queueSize) % m_maxDepth;
        return buf[ind];
    }

    public int getRowLength() {
        int ind = (addPtr - queueSize) % m_maxDepth;
        return this.buf[ind].length();
    }

    public void decrementRow() {
        queueSize--;
    }

    public String[] getOutputBuffer() {
        return buf;
    }

    protected final String getOutput(final int depth) {
        String root = get(m_currRead);
        if (depth == 1) {
            return root;
        }
        m_builder.setLength(0);
        m_builder.append(root);

        for (int i = 1; i < depth; i++) {
            m_builder.append(" ");
            m_builder.append(get(m_currRead + i));
        }
        return m_builder.toString();
    }

    protected final long getHash(int i) {
        return fps[i % m_maxDepth];
    }

    protected void emitAux(int maxLength) {
        long fp = getHash(m_currRead);
        int layer;
        for (int i = 1; i < maxLength; i++) {
            fp = FPHash64.combineFingerPrints(fp, getHash(m_currRead + i));
            layer = index.hasPhrase(fp);
            if (layer != -1) {
                buf[addPtr++ % m_maxDepth] = getOutput(i + 1);
                this.queueSize++;
            }
        }
        m_currRead++;
    }
}
