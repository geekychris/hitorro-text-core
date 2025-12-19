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

import com.hitorro.language.Iso639Table;
import com.hitorro.language.IsoLanguage;
import com.hitorro.util.GenericAnalyzerBase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;

import java.io.IOException;
import java.io.Reader;

/**
 * Copyright (c) 2003 - present HiTorro All rights reserved. User: chris Date: Oct 17, 2006 Time: 4:59:30 PM Analyzer used
 * by lucene and any other text processing where we need to breakdown a stream into tokens.
 * <p/>
 * Since filters do not necessessarily make sense to be created in random order, this analyzer takes a bitmask of
 * available filters and constructs a series of filters based upon that mask
 */
public class GenericAnalyzer extends GenericAnalyzerBase {

    private TokenizerEnum tokenizer;
    private FilterEnum filters[];
    private IsoLanguage language;
    private Mode mode;
    private FilterContext fc;
    private TSInterceptorContext tsInterceptor;

    public GenericAnalyzer() {
    }


    public GenericAnalyzer(String names, IsoLanguage language, Mode mode) {
        //super(Analyzer.PER_FIELD_REUSE_STRATEGY);
        tokenizer = TokenizerEnum.tokenizerContext.getByShortName(names);
        if (tokenizer == null) {
            // default to standard if not defined.
            tokenizer = TokenizerEnum.STANDARD;
        }
        filters = FilterEnum.filterContext.getEnumsFromShortNames(names);
        this.language = language;
        this.mode = mode;
    }

    /**
     * Construct the analyzer with a bitmask based upon filter names, such as: STANDARD, ETC
     *
     * @param names
     */
    private GenericAnalyzer(String names) {
        this(names, Iso639Table.english, Mode.Index);
    }


    private GenericAnalyzer(String names, Mode mode) {
        this(names, Iso639Table.english, Mode.Query);
    }

    public void setFilterContext(FilterContext fc) {
        this.fc = fc;

    }

    public void setTSInterceptorContext(TSInterceptorContext tsInterceptor) {
        this.tsInterceptor = tsInterceptor;
    }

    @Override
    protected TokenStreamComponents createComponents(final String fieldName) {
        //TODO Update (not sure this is right.  New function not seen before.  Using Null for now.
        return createComponents(fieldName, null);
    }

    TokenStreamComponents createComponents(String fieldName, Reader reader, Mode mode, IsoLanguage language, TSInterceptorContext context, FilterContext fc) {
        Tokenizer tok = tokenizerAux(fieldName, reader, context, mode, language);

        TokenStream ts = tokenStreamAux(tok, fieldName, reader, context, mode, language, fc);
        if (ts == null) {
            return new TokenStreamComponents(tok);
        }
        return new TokenStreamComponents(tok, ts);
    }

    public TokenStream stream(String fieldName, Reader reader, Mode mode, IsoLanguage language) {
        TokenStreamComponents tsc = createComponents(fieldName, reader, mode, language, tsInterceptor, fc);
        return tsc.getTokenStream();
    }

    public TokenStream stream(String fieldName, Reader reader, Mode mode, IsoLanguage language, TSInterceptorContext context, FilterContext fc) {
        TokenStreamComponents tsc = createComponents(fieldName, reader, mode, language, context, fc);
        return tsc.getTokenStream();
    }

    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        Tokenizer tok = tokenizerAux(fieldName, reader, null, mode, language);
        try {
            tok.reset();
        } catch (IOException e) {
            Log.text.error("Unable to reset Tokenizer %s %e", e, e);
            return null;
        }
        TokenStream ts = tokenStreamAux(tok, fieldName, reader, null, mode, language, fc);
        if (ts == null) {
            ts = tok;
        }

        return new TokenStreamComponents(tok, ts);
        /* XXX TODO .....used to have a set reader but no longer can overide!
        {
            @Override
            public void setReader (final Reader reader)
            {
                super.setReader(reader);
                // dont do reset, analyzer will be at the end of the stream
                try
                {
                    source.reset();
                }
                catch (IOException e)
                {
                    Log.text.error("Unable to construct reader %s %e", e, e);
                    return;
                }
            }
        };
        */

    }


    public final Tokenizer tokenizerAux(String fieldName, Reader reader) {
        return tokenizerAux(fieldName, reader, null, mode, language);
    }

    public final Tokenizer tokenizerAux(String fieldName, Reader reader, TSInterceptorContext context, final Mode mode, final IsoLanguage language) {
        return TokenizerEnum.tokenizer(fieldName, reader, context, tokenizer, mode, language);
    }

    public final Tokenizer tokenizerAux(String fieldName, Reader reader, TSInterceptorContext context, final Mode mode, final IsoLanguage language, FilterContext fc) {
        return TokenizerEnum.tokenizer(fieldName, reader, context, tokenizer, mode, language, fc);
    }

    public final TokenStream tokenStreamAux(TokenStream ts, String fieldName, Reader reader) {
        return tokenStreamAux(ts, fieldName, reader, null, mode, language, null);
    }


    public final TokenStream tokenStreamAux(TokenStream ts, String fieldName, Reader reader, TSInterceptorContext context, final Mode mode, final IsoLanguage language, FilterContext fc) {
        if (tokenizer == null) {
            return null;
        }
        return FilterEnum.tokenStream(ts, fieldName, reader, context, filters, mode, language, fc);
    }
}

