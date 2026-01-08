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
package com.hitorro.obj.core;

import com.hitorro.basetext.NERMarkupTokenFilter;
import com.hitorro.basetext.NamedEntityFilter;
import com.hitorro.basetext.NamedEntityMarkupFilter;
import com.hitorro.basetext.dfindex.BaseDFIndexInterface;
import com.hitorro.basetext.phrase.Phrase2;
import com.hitorro.basetext.phrase.PhraseFilter;
import com.hitorro.language.IsoLanguage;
import com.hitorro.util.core.EnumContext;
import org.apache.commons.codec.language.*;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.LengthFilter;
import org.apache.lucene.analysis.phonetic.PhoneticFilter;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * <p/>
 * Enumeration of available Filters
 */
public enum FilterEnum {

    INTERCEPTOR("INTERCEPTOR") {
        public TokenStream get(Reader reader, TokenStream ts, TSInterceptorContext context, GenericAnalyzer.Mode mode, IsoLanguage language, final String field, FilterContext fc) {
            if (context != null) {
                InterceptorTokenFilter itf = new InterceptorTokenFilter(ts);
                context.interceptor = itf;
                return itf;
            }
            return ts;
        }
    },
    LOWERCASE("CASE") {
        public TokenStream get(Reader reader, TokenStream ts, TSInterceptorContext context, GenericAnalyzer.Mode mode, IsoLanguage language, final String field, FilterContext fc) {
            return new LowerCaseFilter(ts);
        }
    },
    STOP("STOP") {
        public TokenStream get(Reader reader, TokenStream ts, TSInterceptorContext context, GenericAnalyzer.Mode mode, IsoLanguage language, final String field, FilterContext fc) {
            CharArraySet stopWords;

            stopWords = getStops(language);

            StopFilter stopFilter = new StopFilter(ts, stopWords);
            return stopFilter;
        }
    },
    SOUNDEX("SOUNDEX") {
        public TokenStream get(Reader reader, TokenStream ts, TSInterceptorContext context, GenericAnalyzer.Mode mode, IsoLanguage language, final String field, FilterContext fc) {
            return new PhoneticFilter(ts, new Soundex(), true);
        }
    },
    METAPHONE("METAPHONE") {
        public TokenStream get(Reader reader, TokenStream ts, TSInterceptorContext context, GenericAnalyzer.Mode mode, IsoLanguage language, final String field, FilterContext fc) {
            return new PhoneticFilter(ts, new Metaphone(), true);
        }
    },
    DOUBLEMETAPHONE("DOUBLEMETAPHONE") {
        public TokenStream get(Reader reader, TokenStream ts, TSInterceptorContext context, GenericAnalyzer.Mode mode, IsoLanguage language, final String field, FilterContext fc) {
            return new PhoneticFilter(ts, new DoubleMetaphone(), true);
        }
    },
    CAVERPHONE("CAVERPHONE") {
        public TokenStream get(Reader reader, TokenStream ts, TSInterceptorContext context, GenericAnalyzer.Mode mode, IsoLanguage language, final String field, FilterContext fc) {
            return new PhoneticFilter(ts, new Caverphone2(), true);
        }
    },
    COLOGNEPHONETIC("COLOGNEPHONETIC") {
        public TokenStream get(Reader reader, TokenStream ts, TSInterceptorContext context, GenericAnalyzer.Mode mode, IsoLanguage language, final String field, FilterContext fc) {
            return new PhoneticFilter(ts, new ColognePhonetic(), true);
        }
    },
    NYSIIS("NYSIIS") {
        public TokenStream get(Reader reader, TokenStream ts, TSInterceptorContext context, GenericAnalyzer.Mode mode, IsoLanguage language, final String field, FilterContext fc) {
            return new PhoneticFilter(ts, new Nysiis(), true);
        }
    },
    REFINEDSOUNDEX("REFINEDSOUNDEX") {
        public TokenStream get(Reader reader, TokenStream ts, TSInterceptorContext context, GenericAnalyzer.Mode mode, IsoLanguage language, final String field, FilterContext fc) {
            return new PhoneticFilter(ts, new RefinedSoundex(), true);
        }
    },
    PORTER_STEM("PORTERSTEM") {
        public TokenStream get(Reader reader, TokenStream ts, TSInterceptorContext context, GenericAnalyzer.Mode mode, IsoLanguage language, final String field, FilterContext fc) {
            return new PorterStemFilter(ts);
        }
    },
    LENGTH("LENGTH") {
        public TokenStream get(Reader reader, TokenStream ts, TSInterceptorContext context, GenericAnalyzer.Mode mode, IsoLanguage language, final String field, FilterContext fc) {
            return new LengthFilter(ts, GenericAnalyzer.MinLengthProperty.apply(), GenericAnalyzer.MaxLengthProperty.apply());
        }
    },
    NUMBER("NUMBER") {
        public TokenStream get(Reader reader, TokenStream ts, TSInterceptorContext context, GenericAnalyzer.Mode mode, IsoLanguage language, final String field, FilterContext fc) {
            return new NumberFilter(ts);
        }
    },
    MONEYNUMBERALIAS("MONEYNUMBERALIAS") {
        public TokenStream get(Reader reader, TokenStream ts, TSInterceptorContext context, GenericAnalyzer.Mode mode, IsoLanguage language, final String field, FilterContext fc) {
            return new MoneyToAliasFilter(ts);
        }

    },
    NAMEDENTITY("NAMEDENTITY") {
        public TokenStream get(Reader reader, TokenStream ts, TSInterceptorContext context, GenericAnalyzer.Mode mode, IsoLanguage language, final String field, FilterContext fc) {

            return new NamedEntityFilter(ts, language);
        }

    },
    NAMEDENTITYMARKUP("NAMEDENTITYMARKUP") {
        public TokenStream get(Reader reader, TokenStream ts, TSInterceptorContext context, GenericAnalyzer.Mode mode, IsoLanguage language, final String field, FilterContext fc) {

            return new NamedEntityMarkupFilter(ts, language);
        }

    },

    DECODEMARKUP("DECODEMARKUP") {
        public TokenStream get(Reader reader, TokenStream ts, TSInterceptorContext context, GenericAnalyzer.Mode mode, IsoLanguage language, final String field, FilterContext fc) {
            return new NERMarkupTokenFilter(ts);
        }

    },

    EMITPHRASE("EMITPHRASE") {
        public TokenStream get(Reader reader, TokenStream ts, TSInterceptorContext context, GenericAnalyzer.Mode mode, IsoLanguage language, final String field, FilterContext fc) {
            return new Phrase2(ts, fc);
        }
    },
    PHRASE("PHRASE") {
        public TokenStream get(Reader reader, TokenStream ts, TSInterceptorContext context, GenericAnalyzer.Mode mode, IsoLanguage language, final String field, FilterContext fc) {
            BaseDFIndexInterface bi = fc.baseIndex;


            if (bi != null) {
                return new PhraseFilter(ts, bi, GenericAnalyzer.PhraseDepthProperty.apply(), GenericAnalyzer.PhraseMinDepthProperty.apply());
            }
            return ts;
        }
    };

    public static EnumContext<FilterEnum> filterContext;
    private static Map<IsoLanguage, CharArraySet> map = new HashMap();
    private String m_name;

    FilterEnum(String name) {
        m_name = name.toLowerCase();
        setMapEntry(this, name);
    }

    public static synchronized CharArraySet getStops(IsoLanguage language) {
        //TODO update
        /*
        CharArraySet c = apply.get(language);
        if (c == null)
        {
            BaseFile dir = Env.getDataResource().getChild("solrmaster/template/conf/lang");
            List<BaseFile> list = new ArrayList();
            BaseFileUtil.addIfNotNullExistsAbsent(list, dir.getChild("stopwords_%s.txt", language.getTwo()));
            BaseFileUtil.addIfNotNullExistsAbsent(list, dir.getChild("stoptags_%s.txt", language.getTwo()));
            boolean ignoreCase = true;

            c = CharArraySetUtil.getStopWords("", Version.LUCENE_47, list, ignoreCase);
            apply.put(language, c);
        }
        return c;
        */
        return null;
    }

    public static TokenStream tokenStream(TokenStream ts, String fieldName, Reader reader, TSInterceptorContext context, FilterEnum filters[], GenericAnalyzer.Mode mode, IsoLanguage language) {
        return tokenStream(ts, fieldName, reader, context, filters, mode, language, null);
    }

    public static TokenStream tokenStream(TokenStream ts, String fieldName, Reader reader, TSInterceptorContext context, FilterEnum filters[], GenericAnalyzer.Mode mode, IsoLanguage language, FilterContext fc) {
        if (null != filters) {
            for (FilterEnum fe : filters) {
                ts = fe.get(reader, ts, context, mode, language, fieldName, fc);
            }
        }
        return ts;
    }

    private static void setMapEntry(FilterEnum filter, String shortName) {
        if (filterContext == null) {
            filterContext = new EnumContext("filters");
        }
        filterContext.setNames(filter, shortName);
    }

    public abstract TokenStream get(Reader reader, TokenStream ts, TSInterceptorContext context, GenericAnalyzer.Mode mode, IsoLanguage language, final String field, FilterContext fc);

    public String getName() {
        return m_name;
    }
}

