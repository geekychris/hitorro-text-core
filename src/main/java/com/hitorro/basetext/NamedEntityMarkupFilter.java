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

import com.hitorro.language.IsoLanguage;
import org.apache.lucene.analysis.TokenStream;

/**
 *
 */
public class NamedEntityMarkupFilter extends NamedEntityFilter {
    private StringBuilder sb = new StringBuilder();

    public NamedEntityMarkupFilter(TokenStream in, IsoLanguage language) {
        super(in, language);
    }

    public void generateAttributes() {
        keywordAtt.setKeyword(false);
        posIncrAtt.setPositionIncrement(1);
        offsetAtt.setOffset(
                baseOffset + spans[spanOffset].getStart(),
                baseOffset + spans[spanOffset].getEnd()
        );


        // determine of the current token is of a named entity type, if so
        // push the current state into the queue and put a token reflecting
        // any matching entity types.
        boolean[] types = tokenTypes[spanOffset];
        boolean virgin = true;
        for (int i = 0; i < finders.length; i++) {
            if (types[i]) {
                if (virgin == true) {
                    virgin = false;
                    sb.setLength(0);
                    sb.append("[{").append(tokens[spanOffset]).append("&&");
                } else {
                    sb.append("&&");
                }
                sb.append(tokenTypeNames[i]);
            }
        }
        if (virgin == true) {
            termAtt.setEmpty().append(tokens[spanOffset]);
        } else {
            sb.append("}]");
            termAtt.setEmpty().append(sb.toString());
        }
    }
}
