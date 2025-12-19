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
 * Created with IntelliJ IDEA. User: chris Date: 7/25/12 Time: 10:41 PM To change this template use File | Settings |
 * File Templates.
 */
public class MoneyToAliasFilter extends TokenFilter {
    public static final String Tok = "moneyornumber";
    private char[] TokBuff;
    private CharTermAttribute termAtt;

    public MoneyToAliasFilter(TokenStream in) {
        super(in);
        termAtt = addAttribute(CharTermAttribute.class);
        TokBuff = new char[Tok.length()];
        Tok.getChars(0, Tok.length(), TokBuff, 0);

    }

    public static final boolean isMoneyOrNumber(char buff[], int length) {
        boolean haveDigit = false;
        char c;
        boolean looksLikeMoney = false;
        for (int i = 0; i < length; i++) {
            c = buff[i];
            if (i == 0 && c == '$') {
                looksLikeMoney = true;
                continue;
            }

            if (Character.isDigit(c)) {
                haveDigit = true;
            } else if (c == ',' || c == '/' || c == '.' || c == '-' || ((c == 'k' || c == '+') && looksLikeMoney)) {
                // do nothing its a number related special char...still could be what we consider to be a number
            } else {
                // we found something that isnt a digit
                return false;
            }
        }
        return haveDigit;
    }

    /*

    public final boolean incrementToken() throws IOException {
    if (input.incrementToken()) {
      final char[] buffer = termAtt.buffer();
      final int length = termAtt.length();
      for (int i = 0; i < length;) {
       i += Character.toChars(
               Character.toLowerCase(
                   charUtils.codePointAt(buffer, i)), buffer, i);
      }
      return true;
    } else
      return false;
  }
     */
    public final boolean incrementToken() throws IOException {
        if (input.incrementToken()) {
            if (isMoneyOrNumber(termAtt.buffer(), termAtt.length())) {
                termAtt.copyBuffer(TokBuff, 0, TokBuff.length);
            }
            return true;
        }
        return false;
    }

}