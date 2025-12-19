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
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;

/**
 * Copyright (c) 2003 - present HiTorro All rights reserved.
 * <p/>
 * User: chris
 */
public class PhraseFilter extends TokenFilter {
    String buf[];
    private BaseDFIndexInterface baseIndex;
    private FPPhraseEmitter emitter;
    private int size;
    private char tokens[][];
    private int currTokenLength[];
    private int maxToken = 0;
    private int currToken = 0;
    private CharTermAttribute termAttLocal;

    public PhraseFilter(TokenStream in, BaseDFIndexInterface baseIndex, int maxDepth, int minDepth) {
        super(in);
        this.baseIndex = baseIndex;

        tokens = new char[maxDepth][];
        currTokenLength = new int[maxDepth];

        emitter = new FPPhraseEmitter(maxDepth, minDepth, baseIndex);

        buf = emitter.getOutputBuffer();
        termAttLocal = addAttribute(CharTermAttribute.class);
    }

    public final boolean incrementToken() throws IOException {
        if (currToken < maxToken) {
            termAttLocal.copyBuffer(tokens[currToken], 0, currTokenLength[currToken++]);
            return true;
        }
        return nextAux();
    }

    protected final boolean nextAux() throws IOException {
        if (input.incrementToken()) {
            String t = termAttLocal.toString();
            emitter.addToken(t);
            grab();
            return true;
        } else {
            // flush the pipeline
            emitter.close();
            grab();
            if (currToken < maxToken) {
                termAttLocal.copyBuffer(tokens[currToken], 0, currTokenLength[currToken++]);
                return true;
            }
        }
        return maxToken != 0;
    }

    private final void grab() {
        size = emitter.getOutputSize();
        if (size > 0) {
            maxToken++;
            currToken = 0;
            for (int i = 0; i < size; i++) {
                tokens[i] = emitter.getRow().toCharArray();
                this.currTokenLength[i] = emitter.getRowLength();
                emitter.decrementRow();
            }
        } else {
            maxToken = 0;
        }
    }
}