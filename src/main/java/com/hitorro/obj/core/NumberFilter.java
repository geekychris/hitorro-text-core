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

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;

/**
 */
public class NumberFilter extends TokenFilter {
    private CharTermAttribute termAtt;

    public NumberFilter(TokenStream in) {
        super(in);
        termAtt = addAttribute(CharTermAttribute.class);
    }

    public static final boolean isNumber(char buff[], int length) {
        boolean haveDigit = false;
        char c;
        for (int i = 0; i < length; i++) {
            c = buff[i];
            if (Character.isDigit(c)) {
                haveDigit = true;
            } else if (c == ',' || c == '/' || c == '.' || c == '-') {
                // do nothing its a number related special char...still could be what we consider to be a number
            } else {
                // we found something that isnt a digit
                return false;
            }
        }
        return haveDigit;
    }

    public final boolean incrementToken() throws IOException {
        // return the first non-stop word found
        while (input.incrementToken()) {
            if (!isNumber(termAtt.buffer(), termAtt.length())) {
                return true;
            }

        }
        input.close();
        // reached EOS -- return null
        return false;
    }

}