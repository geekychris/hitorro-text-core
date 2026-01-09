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

import com.hitorro.util.core.Console;

import java.io.IOException;


public class PhraseEmitter {
    protected StringBuilder m_builder = new StringBuilder();
    protected int maxDepth;
    protected String m_buffer[];
    protected int m_fill = 0;
    protected int currRead = 0;


    protected int minSize = 1;

    public PhraseEmitter(int maxDepth, int minLength) {
        this.maxDepth = maxDepth;
        m_buffer = new String[maxDepth];
        minSize = minLength - 1;
    }

    public final void addToken(final String tok) throws IOException {
        add(tok);
        emit();
    }

    public final void emit() throws IOException {
        int maxLength = Math.min(maxDepth, m_fill - currRead);
        if (maxLength >= maxDepth) {
            emitAux(maxLength);
        }
    }

    public final void close() throws IOException {
        while (m_fill > currRead) {
            int maxLength = Math.min(maxDepth, m_fill - currRead);

            emitAux(maxLength);
        }
    }

    protected void emitAux(final int maxLength) throws IOException {
        m_builder.setLength(0);
        for (int i = 0; i < maxLength; i++) {
            if (i > 0) {
                m_builder.append(" ");
            }
            m_builder.append(get(i + currRead));
            if (i >= minSize) {
                emitToConsumer(m_builder.toString());
            }
        }
        // right place for this?
        currRead++;
    }

    protected void emitToConsumer(final String s) throws IOException {
        Console.println(s);
    }

    protected final String get(final int i) {
        return m_buffer[i % maxDepth];
    }

    protected void add(final String tok) {
        m_buffer[m_fill % maxDepth] = tok;
        m_fill++;
    }
}
