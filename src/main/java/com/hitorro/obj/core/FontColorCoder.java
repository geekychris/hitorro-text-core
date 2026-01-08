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

import com.hitorro.basetext.dfindex.DFIndex;
import com.hitorro.basetext.dfindex.DFIndexSingletonMapper;
import com.hitorro.basetext.inverter.InverterUtils;
import com.hitorro.basetext.inverter.TermTuple;
import com.hitorro.basetext.inverter.TermTupleSet;
import com.hitorro.basetext.inverter.TermTupleSetGroup;
import com.hitorro.language.Iso639Table;
import com.hitorro.util.core.string.Fmt;
import com.hitorro.util.core.string.StringUtil;
import com.hitorro.util.io.ResetableStringReader;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p/>
 * for html rendering based upon the frequency of text.
 */
public class FontColorCoder {

    private static final String TooFrequenctColor = "#444444";
    private static String[] colors = {"FFFF00", "FF9900", "#DD0000", "#BB0000", "#990000", "#770000", "#550000", "#330000"};
    private static int[] percent = {2, 3, 5, 11, 21, 31, 41, 51};
    private static int[] size = {3, 2, 2, 1, 0, 0, 0, 0};

    /**
     * Get the top n tokens in their original form (not stemmed).
     *
     * @param text to analyse.
     * @param n    - number of terms to return
     * @return list of top n terms
     * @throws IOException
     */
    public static List<String> getTopNWords(String text, int n, boolean lowerCase) throws IOException {
        List<String> words = new ArrayList<String>();
        Map<String, String> tokMap = new HashMap<String, String>();
        TSInterceptorContext context = new TSInterceptorContext();
        GenericAnalyzer analyzer = new GenericAnalyzer(GenericAnalyzer.Standard.apply(), Iso639Table.english, GenericAnalyzer.Mode.Index);
        ResetableStringReader reader = new ResetableStringReader(null);
        reader.set(text);

        //TODO Update (removed all the goodies like language)
        TokenStream ts = analyzer.tokenStream("", reader);
        CharTermAttribute termAttribute = ts.getAttribute(CharTermAttribute.class);
        DFIndex index = DFIndexSingletonMapper.getSingleton().get();

        // get all the tokens first
        while (ts.incrementToken()) {
            String t = termAttribute.toString();
            tokMap.put(t, context.interceptor.getLastToken());
        }
        ts.close();
        TermTupleSetGroup tsg = InverterUtils.getTupleSet("title", text, "body", "");
        TermTupleSet tts = tsg.getByName("title");
        List<TermTuple> l = tts.getTuplesList();
        int cap = Math.min(n, l.size());
        for (int i = 0; i < cap; i++) {
            TermTuple tt = l.get(i);
            String tok = tokMap.get(tt.getTerm());
            if (!StringUtil.nullOrEmptyString(tok)) {
                if (lowerCase) {
                    words.add(tok.toLowerCase());
                } else {
                    words.add(tok);
                }
            }
        }

        return words;
    }

    public static String getColorCodedText(String text) throws IOException {

        TSInterceptorContext context = new TSInterceptorContext();
        GenericAnalyzer analyzer = new GenericAnalyzer(GenericAnalyzer.Standard.apply(), Iso639Table.english, GenericAnalyzer.Mode.Index);
        ResetableStringReader reader = new ResetableStringReader(null);
        reader.set(text);
        TokenStream ts = analyzer.tokenStream("body", reader);
        CharTermAttribute termAttribute = ts.getAttribute(CharTermAttribute.class);
        DFIndex index = DFIndexSingletonMapper.getSingleton().get();
        double docFreq = index.getDocFrequency();
        StringBuilder sb = new StringBuilder();
        while (ts.incrementToken()) {
            String tok = termAttribute.toString();
            double dfCount = index.getFrequency(tok);
            format(sb, context.interceptor.getLastToken(), dfCount, docFreq);
        }
        ts.close();
        return sb.toString();
    }

    public static String getKey() {
        StringBuilder sb = new StringBuilder();
        formatFont(sb, 0, TooFrequenctColor, "Stop Word");
        for (int i = 0; i < percent.length; i++) {
            String token = Fmt.S(" less than %s docs", percent[i]);
            formatFont(sb, size[i], colors[i], token);
        }

        return sb.toString();
    }

    private static void format(StringBuilder sb, String token, double df, double docFreq) {
        int fontSize = 0;

        if (df != -1) {
            double perc = (df / docFreq) * 100;
            int percI = (int) perc;
            for (int i = 0; i < percent.length; i++) {
                if (percent[i] > percI) {
                    formatFont(sb, size[i], colors[i], token);
                    sb.append(" ");
                    return;
                }
            }
        }
        formatFont(sb, fontSize, TooFrequenctColor, token);
    }

    private static void formatFont(StringBuilder sb, int fontSize, String color, String token) {
        sb.append("<FONT size=\"");
        sb.append(fontSize);
        sb.append("\" color=\"");
        sb.append(color);
        sb.append("\">");
        sb.append(token);
        sb.append("</FONT>");
    }
}
