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
package com.hitorro.basetext.maxentclassifier;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.util.Span;

/**
 *
 */
public class ChunkParser implements Parser {
    private ChunkerME chunker;
    private POSTaggerME tagger;

    public ChunkParser(ChunkerME chunker, POSTaggerME tagger) {
        this.chunker = chunker;
        this.tagger = tagger;
    }

    @Override
    public Parse parse(Parse tokens) {
        Parse[] children = tokens.getChildren();
        String[] words = new String[children.length];
        double[] probs = new double[words.length];
        for (int i = 0, il = children.length; i < il; i++) {
            words[i] = children[i].toString();
        }
        String[] tags = tagger.tag(words);
        tagger.probs(probs);
        for (int j = 0; j < words.length; j++) {
            Parse word = children[j];
            double prob = probs[j];
            tokens.insert(new Parse(word.getText(), word.getSpan(), tags[j], prob, j));
            tokens.addProb(Math.log(prob));
        }

        String[] chunks = chunker.chunk(words, tags);
        chunker.probs(probs);
        int chunkStart = -1;
        String chunkType = null;
        double logProb = 0;
        for (int ci = 0, cn = chunks.length; ci < cn; ci++) {
            if (chunkStart != -1 && ci > 0 && !chunks[ci].startsWith("I-") && !chunks[ci - 1].equals("O")) {
                Span span = new Span(children[chunkStart].getSpan().getStart(), children[ci - 1].getSpan().getEnd());
                tokens.insert(new Parse(tokens.getText(), span, chunkType, logProb, children[ci - 1]));
                logProb = 0;
            }
            if (chunks[ci].startsWith("B-")) {
                chunkStart = ci;
                chunkType = chunks[ci].substring(2);
            }
            logProb += Math.log(probs[ci]);
        }
        if (chunkStart != -1 && !chunks[chunks.length - 1].equals("O")) {
            int ci = chunks.length;
            Span span = new Span(children[chunkStart].getSpan().getStart(), children[ci - 1].getSpan().getEnd());
            tokens.insert(new Parse(tokens.getText(), span, chunkType, logProb, children[ci - 1]));
        }
        return tokens;
    }

    @Override
    public Parse[] parse(Parse tokens, int numParses) {
        return new Parse[]{parse(tokens)};
    }
}

