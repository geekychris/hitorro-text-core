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
package com.hitorro.language;

import com.hitorro.basetext.maxentclassifier.AnswerTypeClassifier;
import com.hitorro.basetext.maxentclassifier.BaseClassifier;
import com.hitorro.util.core.Log;
import com.hitorro.util.core.events.cache.HashCache;
import com.hitorro.util.core.events.cache.PooledObjectCache;
import com.hitorro.util.core.iterator.Mapper;
import com.hitorro.util.core.iterator.mappers.BaseMapper;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerModel;

import java.io.IOException;

public class Models {
    public static final HashCache<com.hitorro.language.IsoLanguage, TokenNameFinderModel> person = new HashCache(new NameFinderMapper("person"));
    public static final HashCache<com.hitorro.language.IsoLanguage, TokenNameFinderModel> organization = new HashCache(new NameFinderMapper("organization"));
    public static final HashCache<com.hitorro.language.IsoLanguage, TokenNameFinderModel> location = new HashCache(new NameFinderMapper("location"));
    public static final HashCache<com.hitorro.language.IsoLanguage, TokenNameFinderModel> date = new HashCache(new NameFinderMapper("date"));
    public static final HashCache<com.hitorro.language.IsoLanguage, TokenNameFinderModel> money = new HashCache(new NameFinderMapper("money"));
    /**
     * Document Categorization singletons
     */

    public static final PooledObjectCache<com.hitorro.language.IsoLanguage, com.hitorro.language.SentenceSegmenter> sentenceDetectorSingleton = new PooledObjectCache(10, false, null, new SentenceDetectorMapper());
    public static final PooledObjectCache<com.hitorro.language.IsoLanguage, com.hitorro.language.PartOfSpeech> posPoolSingleton = new PooledObjectCacheWrapper(10, false, null, new POSPoolMapper());
    public static final HashCache<com.hitorro.language.IsoLanguage, DoccatModel> answertypeclassifier = new HashCache(new DocumentCatMapper("answertype"));
    // Though we pass in a language currently we only have an english wordnet wrapper.
    public static final HashCache<com.hitorro.language.IsoLanguage, com.hitorro.language.WordnetPool> wordnetWrapper = new HashCache(new WordNetWrapperMapper());
    public static final HashCache<com.hitorro.language.IsoLanguage, POSModel> posModel = new HashCache<com.hitorro.language.IsoLanguage, POSModel>(new PosModelMapper()) {
        public POSModel get(com.hitorro.language.IsoLanguage key) {
            return super.get(key);
        }
    };
    public static final PooledObjectCache<com.hitorro.language.IsoLanguage, BaseClassifier> answerTypeSingleton = new PooledObjectCache(10, false, null, new AnswerTypeMapper());
    public static BaseMapper<com.hitorro.language.IsoLanguage, TokenizerModel> tokenizerMapper = new BaseMapper<com.hitorro.language.IsoLanguage, TokenizerModel>() {
        public TokenizerModel apply(com.hitorro.language.IsoLanguage key) {
            return key.getTokenizerModel();
        }
    };
    /*
     *
     */
    public static final HashCache<com.hitorro.language.IsoLanguage, TokenizerModel> tokenizerSingleton = new HashCache(tokenizerMapper);
    public static BaseMapper<com.hitorro.language.IsoLanguage, SentenceModel> sentenceModelMapper = new BaseMapper<com.hitorro.language.IsoLanguage, SentenceModel>() {
        public SentenceModel apply(com.hitorro.language.IsoLanguage key) {
            return key.getSentenceModel();
        }
    };
    public static final HashCache<com.hitorro.language.IsoLanguage, SentenceModel> sentenceModelSingleton = new HashCache(sentenceModelMapper);
    public static BaseMapper<com.hitorro.language.IsoLanguage, ParserModel> parserModelMapper = new BaseMapper<com.hitorro.language.IsoLanguage, ParserModel>() {
        public ParserModel apply(com.hitorro.language.IsoLanguage key) {
            return key.getParserModel();
        }
    };
    public static final HashCache<com.hitorro.language.IsoLanguage, ParserModel> parserSingleton = new HashCache(parserModelMapper);
    /**
     * name finders
     */
    public static NameFinderMapper personMapper = new NameFinderMapper("person");

    public static class NameFinderMapper extends BaseMapper<com.hitorro.language.IsoLanguage, TokenNameFinderModel> {
        private String intent;

        public NameFinderMapper(String intent) {
            this.intent = intent;
        }

        public TokenNameFinderModel apply(com.hitorro.language.IsoLanguage key) {
            return key.getTokenNameFinderModel(intent);
        }
    }

    public static class DocumentCatMapper extends BaseMapper<com.hitorro.language.IsoLanguage, DoccatModel> {
        private String cat;

        public DocumentCatMapper(String cat) {
            this.cat = cat;
        }

        public DoccatModel apply(com.hitorro.language.IsoLanguage key) {
            return key.getDocumentCatModel(cat);

        }
    }

    public static class PosModelMapper extends BaseMapper<com.hitorro.language.IsoLanguage, POSModel> {

        public POSModel apply(com.hitorro.language.IsoLanguage key) {
            return key.getPOSModel();
        }
    }

    public static class SentenceDetectorMapper extends BaseMapper<com.hitorro.language.IsoLanguage, com.hitorro.language.SentenceSegmenter> {
        public com.hitorro.language.SentenceSegmenter apply(com.hitorro.language.IsoLanguage key) {
            com.hitorro.language.SentenceSegmenter ss = new com.hitorro.language.SentenceSegmenter();
            try {
                ss.init(key);
            } catch (IOException e) {
                Log.sentence.error("Unable to initialize sentence segmenter %s %s %e", key, e, e);
                return null;
            }
            return ss;
        }
    }

    public static class POSPoolMapper extends BaseMapper<com.hitorro.language.IsoLanguage, com.hitorro.language.PartOfSpeech> {
        public com.hitorro.language.PartOfSpeech apply(com.hitorro.language.IsoLanguage key) {
            com.hitorro.language.PartOfSpeech ss = new com.hitorro.language.PartOfSpeech(key);
            try {
                ss.init(key);
                return ss;
            } catch (IOException e) {
                Log.sentence.error("Unable to load PartOfSpeech with error %s %e", e, e);
            }
            return null;
        }
    }

    public static class WordNetWrapperMapper extends BaseMapper<com.hitorro.language.IsoLanguage, com.hitorro.language.WordnetPool> {
        public WordNetWrapperMapper() {
        }

        public com.hitorro.language.WordnetPool apply(com.hitorro.language.IsoLanguage key) {
            // XXX only english
            return new com.hitorro.language.WordnetPool();
        }
    }

    public static class PooledObjectCacheWrapper extends PooledObjectCache<com.hitorro.language.IsoLanguage, com.hitorro.language.PartOfSpeech> {

        public PooledObjectCacheWrapper(int maxElements, boolean demandBasedCacheing, String eventName, Mapper<com.hitorro.language.IsoLanguage, com.hitorro.language.PartOfSpeech> mapper) {
            super(maxElements, demandBasedCacheing, eventName, mapper);
        }

        public com.hitorro.language.PartOfSpeech get(com.hitorro.language.IsoLanguage key) {
            return super.get(key);
        }
    }

    public static final class AnswerTypeMapper extends BaseMapper<com.hitorro.language.IsoLanguage, BaseClassifier> {
        public BaseClassifier apply(com.hitorro.language.IsoLanguage key) {
            return new AnswerTypeClassifier(Models.answertypeclassifier.get(key), key);
        }
    }


}
