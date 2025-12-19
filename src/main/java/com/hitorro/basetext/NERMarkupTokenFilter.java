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
package com.hitorro.basetext;

import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Take a set of tokens such as "bla bla [{chris&&NE_Person]} and convert that into a new token stream that places the
 * term and the markup at the same positionals.
 */
public class NERMarkupTokenFilter extends TokenFilter {
    protected final Tokenizer tokenizer;


    protected final KeywordAttribute keywordAtt = addAttribute(KeywordAttribute.class);
    protected final PositionIncrementAttribute posIncrAtt = addAttribute(PositionIncrementAttribute.class);
    protected final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    protected final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
    protected final Queue<State> tokenQueue =
            new LinkedList<State>();
    //protected String text;
    protected int baseOffset = 0;
    protected StringBuilder sb = new StringBuilder();
    protected int length;

    public NERMarkupTokenFilter(TokenStream in) {
        super(in);
        this.tokenizer = SimpleTokenizer.INSTANCE;
    }

    public boolean incrementToken() throws IOException {
        // if there's nothing in the queue.
        if (tokenQueue.peek() == null) {
            if (!input.incrementToken()) {
                return false;
            }
            if (termAtt.length() > 2 &&
                    termAtt.charAt(0) == '[' &&
                    termAtt.charAt(1) == '{') {
                bundleUp();
            } else {
                length = termAtt.length();
                posIncrAtt.setPositionIncrement(1);
                offsetAtt.setOffset(
                        baseOffset,
                        baseOffset + length
                );

                baseOffset += 1 + length;
                return true;
            }
        }
        // process each extra incarnation of token at this position (NE_*)
        State state = tokenQueue.poll();
        restoreState(state);

        return true;
    }

    private void bundleUp() {
        // start
        String tmp = termAtt.toString();
        clearAttributes();
        int endPos = tmp.length() - 2;
        keywordAtt.setKeyword(false);
        posIncrAtt.setPositionIncrement(1);
        boolean first = true;
        for (int i = 2; i < endPos; i++) {
            char c = tmp.charAt(i);
            if (c == '&') {
                first = bundle(first);
            } else {
                sb.append(c);
            }
        }
        // do end of
        bundle(false);
    }

    private boolean bundle(boolean first) {
        if (sb.length() > 0) {
            if (first) {
                keywordAtt.setKeyword(false);
                length = sb.length();
                posIncrAtt.setPositionIncrement(1);
                offsetAtt.setOffset(
                        baseOffset,
                        baseOffset + length
                );
                baseOffset += 1 + length;

                termAtt.setEmpty().append(sb);
                tokenQueue.add(captureState());
            } else {
                keywordAtt.setKeyword(true);
                posIncrAtt.setPositionIncrement(0);
                termAtt.setEmpty().append(sb);
                tokenQueue.add(captureState());
            }
            sb.setLength(0);
        }
        return false;
    }

    @Override
    public void close() throws IOException {
        super.close();
        resetCounters();
    }

    @Override
    public void end() throws IOException {
        super.end();
        resetCounters();
    }

    private void resetCounters() {
        baseOffset = 0;
        length = 0;
    }
}
