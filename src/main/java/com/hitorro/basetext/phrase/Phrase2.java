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

import com.hitorro.obj.core.FilterContext;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PackedTokenAttributeImpl;

import java.io.IOException;

/**
 *
 */
public class Phrase2 extends TokenFilter {
    private FPPhraseContext emitter;
    private CharTermAttribute termAtt;

    public Phrase2(TokenStream in, FilterContext fc) {
        super(in);

        // one that uses spaces in the tokens to hash (not as efficient but gives parity with whats in the df index.
        emitter = fc.fpPhraseContext;
        emitter.reset();

        termAtt = addAttribute(CharTermAttribute.class);
    }

    public final boolean incrementToken() throws IOException {
        if (input.incrementToken()) {
            PackedTokenAttributeImpl i = (PackedTokenAttributeImpl) termAtt;

            emitter.add(termAtt.buffer(), termAtt.length(), i.startOffset(), i.endOffset());
            return true;
        }
        emitter.end();
        return false;
    }
}