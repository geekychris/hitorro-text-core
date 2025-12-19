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


import com.fasterxml.jackson.databind.JsonNode;
import com.hitorro.basetext.indexer.Log;
import com.hitorro.language.Iso639Table;
import com.hitorro.obj.core.GenericAnalyzer;
import com.hitorro.util.basefile.fs.BaseFile;
import com.hitorro.util.core.Timer;
import com.hitorro.util.core.iterator.sinks.Sink;
import com.hitorro.util.io.ResetableStringReader;
import com.hitorro.util.io.largedata.buckets.BaseFileBucketWriter;
import com.hitorro.util.json.keys.BasefileProperty;
import com.hitorro.util.json.keys.IntegerProperty;
import com.hitorro.util.json.keys.StringProperty;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;

/**
 * Copyright (c) 2003 - present HiTorro All rights reserved.
 * <p/>
 * User: chris
 */
public class TextToPhraseSink implements Sink<String> {
    public static final StringProperty AnalyzersKey = new StringProperty("analyzers", "analyzers for tokenization", "NUMBER, PORTER, LOWERCASE");

    public static final BasefileProperty DirKey = new BasefileProperty("dir", "output directory using basefile syntax");
    public static final IntegerProperty MinDepthKey = new IntegerProperty("phrasemindepth", "phrase minimum depth (how many terms to a phrase max", 1);
    public static final IntegerProperty PhraseDepthKey = new IntegerProperty("phrasedepth", "phrase depth (how many terms to a phrase max", 4);

    public static final IntegerProperty PhrasePerBucketKey = new IntegerProperty("phraseperbucket", "how many phrases per initial bucket file", 10000000);
    public static final StringProperty FileExtensionKey = new StringProperty("extension", "file extension", "phrase");
    private BaseFileBucketWriter<PhraseElement> m_writer;
    private PhraseElementPhraseEmitter m_emitter = null;
    private GenericAnalyzer analyzer;
    private ResetableStringReader m_reader = new ResetableStringReader(null);
    private int phraseDepth;
    private int minDepth;
    private boolean stopped = false;
    private int count = 0;
    private Timer timer = new Timer();

    public TextToPhraseSink(String analyzers, int phraseDepth, int minDepth, int phrasesPerBucket, BaseFile dir, String fileExtension) throws IOException {
        init(analyzers, phraseDepth, minDepth, phrasesPerBucket, dir, fileExtension);
    }

    public TextToPhraseSink() {

    }

    private void init(final String analyzers, final int phraseDepth, final int minDepth, final int phrasesPerBucket, final BaseFile dir, final String fileExtension) throws IOException {
        dir.deleteContentOfDir(true);
        dir.mkdir();
        PhraseFactory factory = new PhraseFactory();
        // XXX TODO: this should be localized.
        analyzer = new GenericAnalyzer(analyzers, Iso639Table.english, GenericAnalyzer.Mode.Index);
        BaseFileBucketWriter<PhraseElement> bw = new BaseFileBucketWriter(phrasesPerBucket, dir, fileExtension, factory);
        this.phraseDepth = phraseDepth;
        setWriter(bw);
    }

    public void setWriter(BaseFileBucketWriter<PhraseElement> writer) {
        m_writer = writer;
        m_emitter = new PhraseElementPhraseEmitter(phraseDepth, minDepth, writer);
    }

    @Override
    public boolean init(JsonNode node) {
        try {
            init(AnalyzersKey.apply(node),
                    PhraseDepthKey.apply(node),
                    MinDepthKey.apply(node),
                    PhrasePerBucketKey.apply(node),
                    DirKey.apply(node),
                    FileExtensionKey.apply(node));
        } catch (IOException e) {
            return false;
        }
        return false;
    }

    @Override
    public boolean start() {
        return true;
    }

    @Override
    public boolean add(final String text) throws IOException {
        if (count % 10000 == 0) {
            Log.indexer.info("Processed %s content files in the phrase generator, took %s ms", count, timer.stop());
            timer.reset();
        }
        m_reader.set(text);
        TokenStream ts = analyzer.tokenStream("", m_reader);
        CharTermAttribute attr = ts.getAttribute(CharTermAttribute.class);
        while (ts.incrementToken()) {
            m_emitter.addToken(new String(attr.buffer(), 0, attr.length()));
        }
        ts.close();
        m_emitter.close();
        count++;
        return true;
    }

    @Override
    public boolean stop() throws IOException {
        if (stopped) {
            return true;
        }
        stopped = true;
        m_writer.stop();
        return true;
    }

    @Override
    public void close() throws IOException {
        stop();
    }
}

class PhraseElementPhraseEmitter extends PhraseEmitter {
    private BaseFileBucketWriter<PhraseElement> m_writer;

    public PhraseElementPhraseEmitter(int maxDepth, int minDepth, BaseFileBucketWriter<PhraseElement> writer) {
        super(maxDepth, minDepth);
        m_writer = writer;
    }

    protected void emitToConsumer(String s) throws IOException {
        PhraseElement pe = new PhraseElement();
        pe.setPhrase(s);
        pe.setFrequency(1);
        m_writer.add(pe);
    }

}
