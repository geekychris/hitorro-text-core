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
package com.hitorro.features.extractor.baseextractors;

import com.hitorro.features.extractor.ExtractionContext;
import com.hitorro.features.extractor.ResetableContext;
import com.hitorro.language.Iso639Table;
import com.hitorro.language.IsoLanguage;
import com.hitorro.obj.core.GenericAnalyzer;
import com.hitorro.util.core.Log;
import com.hitorro.util.io.ResetableStringReader;
import com.hitorro.util.typesystem.TypeFieldIntf;
import com.hitorro.util.typesystem.TypeIntf;
import com.hitorro.util.typesystem.valuesource.ValueSourceForClass;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import java.io.IOException;

/**
 *
 */
public class TokenTypeCounterContext implements ResetableContext<TokenTypeCounterContext> {
    private GenericAnalyzer analyzer = new GenericAnalyzer("HTSTANDARD", Iso639Table.english, GenericAnalyzer.Mode.Index);
    private ResetableStringReader reader = new ResetableStringReader(null);

    private String fields[];
    private boolean valuesSet = false;
    private int hashTagCount;
    private int responseCount;
    private int wordCount;
    private int uriCount;
    private int refCount;
    private int getFollowingCount;
    private int getFriendsCount;
    private int getStatusCount;

    public TokenTypeCounterContext(String fields[], String indexField) {
        this.fields = fields;
    }


    public TokenTypeCounterContext getInstance(final ExtractionContext ec) {
        return new TokenTypeCounterContext(fields, "body");
    }

    public void setDocument(ValueSourceForClass vf) {
        if (valuesSet) {
            return;
        }
        IsoLanguage language = getLanguage(vf);

        valuesSet = true;
        for (String field : fields) {
            Object o = vf.getValue(vf, field);
            String txt;
            if (o == null) {

                txt = "";
            } else {
                txt = o.toString();
            }
            reader.set(txt);
            TokenStream ts = analyzer.tokenStream("body", reader);
            CharTermAttribute termAttribute = ts.getAttribute(CharTermAttribute.class);
            TypeAttribute typeAttribute = ts.addAttribute(TypeAttribute.class);
            int tokPos = 0;

            try {
                while (ts.incrementToken()) {
                    String type = typeAttribute.type();
                    char buff[] = termAttribute.buffer();
                    //TODO Update removed the original HTTokenizer needs rebuilt)

                    /*if (type == HTTokenizer.TOKEN_TYPES[HTTokenizer.SENTENCE])
                    {
                        // sentence ignore.
                    }
                    else if (type == HTTokenizer.TOKEN_TYPES[HTTokenizer.HOST])
                    {
                        // uri
                        uriCount++;
                    }
                    else if (type == HTTokenizer.TOKEN_TYPES[HTTokenizer.MIXED])
                    {
                        // sentence ignore.
                        switch (buff[0])
                        {
                            case '@':
                                // if its in the first position, they are responding, else they are
                                // referencing someone
                                if (tokPos == 0)
                                {
                                    this.responseCount++;
                                }
                                else
                                {
                                    this.refCount++;
                                }
                                break;
                            case '#':
                                this.hashTagCount++;
                                break;
                        }
                    }
                    else
                    {
                        wordCount++;
                    }
                    */
                    tokPos++;
                }
                ts.close();
            } catch (IOException e) {
                Log.util.error("Unable to parse text");
            }
        }
    }


    private IsoLanguage getLanguage(final ValueSourceForClass vf) {
        TypeIntf type = vf.getType();
        TypeFieldIntf langField = type.getLanguageField();
        IsoLanguage language = null;
        if (langField != null) {
            Object l = vf.getValue(vf, langField.getName());
            if (l != null) {
                language = Iso639Table.getInstance().getRow(l.toString());
            }
        }
        if (language == null) {
            language = Iso639Table.english;
        }
        return language;
    }

    public void reset() {
        valuesSet = false;
        hashTagCount = 0;
        responseCount = 0;
        wordCount = 0;
        uriCount = 0;
        refCount = 0;
    }

    public void deinit() {
        // gc cleanup should be sufficient.
    }

    public int getHashTagCount() {
        return hashTagCount;
    }

    public int getResponseCount() {
        return responseCount;
    }

    public int getWordCount() {
        return wordCount;
    }

    public int getUriCount() {
        return uriCount;
    }

    public int getRefCount() {
        return refCount;
    }
}

