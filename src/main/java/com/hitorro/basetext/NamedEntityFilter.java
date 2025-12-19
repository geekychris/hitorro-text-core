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

import com.hitorro.util.core.Log;
import com.hitorro.language.IsoLanguage;
import com.hitorro.language.NameFinder;
import com.hitorro.language.PartOfSpeech;
import com.hitorro.language.PartOfSpeechSingletonMapper;
import com.hitorro.util.core.events.cache.PoolContainer;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.Span;
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
 *
 */
public class NamedEntityFilter extends TokenFilter {
    public static final String NE_PREFIX = "NE_";

    protected final Tokenizer tokenizer;
    protected final KeywordAttribute keywordAtt = addAttribute(KeywordAttribute.class);
    protected final PositionIncrementAttribute posIncrAtt = addAttribute(PositionIncrementAttribute.class);
    protected final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    protected final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
    protected final Queue<State> tokenQueue =
            new LinkedList<State>();
    protected String[] tokenTypeNames = null;
    protected NameFinder[] finders;
    protected String text;
    protected int baseOffset;
    protected Span[] spans;
    protected String[] tokens;
    protected Span[][] foundNames;
    protected boolean[][] tokenTypes;
    protected int spanOffset = 0;
    protected PoolContainer<IsoLanguage, PartOfSpeech> posPool;
    private IsoLanguage language;

    public NamedEntityFilter(TokenStream in, IsoLanguage language) {
        super(in);
        this.tokenizer = SimpleTokenizer.INSTANCE;
        this.language = language;

        posPool = PartOfSpeechSingletonMapper.singleton.get(language);
        try (PartOfSpeech pos = posPool.get()) {
            String modelNames[] = pos.getModelNames();
            finders = pos.getAllNameFinders();

            this.tokenTypeNames = new String[modelNames.length];
            for (int i = 0; i < modelNames.length; i++) {
                tokenTypeNames[i] = NE_PREFIX + modelNames[i];
            }
        } catch (Exception e) {
            Log.util.error("Unable to close %s %e", e, e);
        } finally {

        }
    }

    /**
     * consume tokens from the upstream tokenizer and buffer them in a StringBuilder whose contents will get passed to
     * opennlp.
     */
    protected boolean fillSpans() {
        posPool = PartOfSpeechSingletonMapper.singleton.get(language);
        try (PartOfSpeech pos = posPool.get()) {
            finders = pos.getAllNameFinders();

            if (!input.incrementToken()) {
                return false;
            }

            // process the next sentence from the upstream tokenizer
            text = input.getAttribute(CharTermAttribute.class).toString();
            baseOffset = input.getAttribute(OffsetAttribute.class).startOffset();

            spans = tokenizer.tokenizePos(text);
            tokens = Span.spansToStrings(spans, text);
            foundNames = new Span[finders.length][];
            for (int i = 0; i < finders.length; i++) {
                finders[i].setToks(tokens);
                foundNames[i] = finders[i].getSpans();
            }

            //TODO: make this a bitset that is tokens.length * finders.length
            // in size.
            tokenTypes = new boolean[tokens.length][finders.length];

            for (int i = 0; i < finders.length; i++) {
                Span[] spans = foundNames[i];
                for (int j = 0; j < spans.length; j++) {
                    int start = spans[j].getStart();
                    int end = spans[j].getEnd();
                    for (int k = start; k < end; k++) {
                        tokenTypes[k][i] = true;
                    }
                }
            }

            spanOffset = 0;

            return true;
        } catch (Exception e) {
            Log.util.error("Unable to close %s %e", e, e);
        } finally {

        }
        return false;
    }

    public boolean incrementToken() throws IOException {
        // if there's nothing in the queue.
        if (tokenQueue.peek() == null) {
            // no spans or spans consumed
            if (spans == null || spanOffset >= spans.length) {
                // no more data to fill spans
                if (!fillSpans()) {
                    return false;
                }
            }

            if (spanOffset >= spans.length) {
                return false;
            }

            // copy the token and any types.
            clearAttributes();
            generateAttributes();

            spanOffset++;
            return true;
        }
        // process each extra incarnation of token at this position (NE_*)
        State state = tokenQueue.poll();
        restoreState(state);

        return true;
    }

    public void generateAttributes() {
        keywordAtt.setKeyword(false);
        posIncrAtt.setPositionIncrement(1);
        offsetAtt.setOffset(
                baseOffset + spans[spanOffset].getStart(),
                baseOffset + spans[spanOffset].getEnd()
        );
        termAtt.setEmpty().append(tokens[spanOffset]);

        // determine of the current token is of a named entity type, if so
        // push the current state into the queue and put a token reflecting
        // any matching entity types.
        boolean[] types = tokenTypes[spanOffset];
        for (int i = 0; i < finders.length; i++) {
            if (types[i]) {
                keywordAtt.setKeyword(true);
                posIncrAtt.setPositionIncrement(0);
                tokenQueue.add(captureState());

                posIncrAtt.setPositionIncrement(1);
                termAtt.setEmpty().append(tokenTypeNames[i]);
            }
        }
    }

    @Override
    public void close() throws IOException {
        super.close();
        resetState();
    }

    @Override
    public void end() throws IOException {
        super.end();
        resetState();
    }

    private void resetState() {
        this.spanOffset = 0;
        this.spans = null;
    }
}
