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

import com.hitorro.language.IsoLanguage;
import com.hitorro.util.core.EnumContext;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.classic.ClassicTokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizer;

import java.io.Reader;

/**
 * Created by chris on 2/20/16.
 */
public enum TokenizerEnum {
    WHITESPACE("WHITESPACE") {
        public Tokenizer get(Reader reader, TSInterceptorContext context, GenericAnalyzer.Mode mode, IsoLanguage
                language, final String field, FilterContext fc) {
            //TokenStream ret = new HTTokenizer(Version.LUCENE_CURRENT, reader);
            WhitespaceTokenizer wt = new WhitespaceTokenizer();
            return wt;
        }
    },
    CLASSIC("CLASSIC") {
        public Tokenizer get(Reader reader, TSInterceptorContext context, GenericAnalyzer.Mode mode, IsoLanguage
                language, final String field, FilterContext fc) {
            //TokenStream ret = new HTTokenizer(Version.LUCENE_CURRENT, reader);
            ClassicTokenizer ct = new ClassicTokenizer();
            return ct;
        }
    },
    STANDARD("STANDARD") {
        public Tokenizer get(Reader reader, TSInterceptorContext context, GenericAnalyzer.Mode mode, IsoLanguage
                language, final String field, FilterContext fc) {
            StandardTokenizer st = new StandardTokenizer();
            return st;
        }

    };
    public static EnumContext<TokenizerEnum> tokenizerContext;
    private String m_name;

    TokenizerEnum(String name) {
        m_name = name.toLowerCase();
        setMapEntry(this);
    }

    public static Tokenizer tokenizer(String fieldName, Reader reader, TSInterceptorContext context, TokenizerEnum tokenizer, GenericAnalyzer.Mode mode, IsoLanguage language) {
        return tokenizer(fieldName, reader, context, tokenizer, mode, language, null);
    }

    public static Tokenizer tokenizer(String fieldName, Reader reader, TSInterceptorContext context, TokenizerEnum tokenizer, GenericAnalyzer.Mode mode, IsoLanguage language, FilterContext fc) {
        return tokenizer.get(reader, context, mode, language, fieldName, fc);
    }

    private static void setMapEntry(TokenizerEnum e) {
        if (tokenizerContext == null) {
            tokenizerContext = new EnumContext<>("tokenizerenum");
        }
        tokenizerContext.setNames(e, e.getName());
    }

    public abstract Tokenizer get(Reader reader, TSInterceptorContext context, GenericAnalyzer.Mode mode, IsoLanguage language, final String field, FilterContext fc);

    public String getName() {
        return m_name;
    }
}
