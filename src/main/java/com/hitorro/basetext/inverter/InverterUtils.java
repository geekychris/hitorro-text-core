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
package com.hitorro.basetext.inverter;

import com.hitorro.language.Iso639Table;
import com.hitorro.language.IsoLanguage;
import com.hitorro.util.core.Log;
import com.hitorro.util.html.HTMLPage;
import com.hitorro.util.html.HTMLPageFetcher;
import com.hitorro.util.html.HTMLParser;
import org.w3c.dom.Document;

import java.io.IOException;


public class InverterUtils {
    public static TermTupleSetGroup getMergedTupleSetFromPage(HTMLParser parser) throws IOException {
        return getMergedTupleSetFromPage(parser, "body", Iso639Table.english);
    }

    public static TermTupleSetGroup getMergedTupleSetFromPage(HTMLParser parser, String indexField, IsoLanguage language) throws IOException {
        return getTupleSet("title", parser.getTitleText(), "body", parser.getBodyText(), indexField, language);
    }

    public static TermTupleSetGroup getTupleSet(String p1Section, String p1Text, String p2Section, String p2Text) throws IOException {
        return getTupleSetWithFilter(null, true, p1Section, p1Text, p2Section, p2Text, "body", Iso639Table.english);
    }

    public static TermTupleSetGroup getTupleSet(String p1Section, String p1Text, String p2Section, String p2Text, String indexField, IsoLanguage language) throws IOException {
        return getTupleSetWithFilter(null, true, p1Section, p1Text, p2Section, p2Text, indexField, language);
    }

    public static TermTupleSetGroup getTupleSetWithFilter(String filter, boolean merge, String p1Section, String p1Text, String p2Section, String p2Text, String indexField, IsoLanguage language) throws IOException {
        TermTupleSetGroup group = new TermTupleSetGroup(indexField, filter, TermTupleSet.s_MeasureDescendComparitor, new TFIDFTermMeasureFunction());
        group.add(p1Section, p1Text, language);
        group.add(p2Section, p2Text, language);
        if (merge) {
            group.merge(p1Section, p2Section, "all");
        }
        return group;
    }

    public static TermTupleSetGroup getTupleSetFromText(String section, String text) throws IOException {
        return getTupleSetFromTextWithFilter(null, section, text, "body", Iso639Table.english);
    }

    public static TermTupleSetGroup getTupleSetFromText(String section, String text, String indexField, IsoLanguage language) throws IOException {
        return getTupleSetFromTextWithFilter(null, section, text, indexField, language);
    }

    public static TermTupleSetGroup getTupleSetFromTextWithFilter(String filter, String section, String text, String indexField, IsoLanguage language) throws IOException {
        TermTupleSetGroup group = new TermTupleSetGroup(indexField, filter, TermTupleSet.s_MeasureDescendComparitor, new TFIDFTermMeasureFunction());
        group.add(section, text, language);
        return group;
    }

    public static TermTupleSetGroup getTupleSetFromTextTF(String section, String text) throws IOException {
        return getTupleSetFromTextWithFilterTF(null, section, text, "body", Iso639Table.english);
    }

    public static TermTupleSetGroup getTupleSetFromTextWithFilterTF(String filter, String section, String text, String indexField, IsoLanguage language) throws IOException {
        TermTupleSetGroup group = new TermTupleSetGroup(indexField, filter, TermTupleSet.s_MeasureDescendComparitor, new TermMeasureSetterFunction(10.0));
        group.add(section, text, language);
        return group;
    }

    public static HTMLPage fetchHTMLPage(String url, long timeout) {
        HTMLPageFetcher fetcher = new HTMLPageFetcher();
        fetcher.setHttpTimeout(10000);
        HTMLPage page = fetcher.fetchPage(url);
        if (page == null || page.getSource() == null) {
            return null;
        }
        return page;
    }

    public static Document getPage(String source) throws IOException {
        HTMLParser parser = new HTMLParser();
        parser.setHtmlPage(source);
        return parser.getDocument();
    }

    public static final TermTupleSetGroup getTupleSetGroupFromUrl(String query, long time) {
        return getTupleSetGroupFromUrl(query, time, "body", Iso639Table.english);
    }

    /**
     * Fetch a page and get the TSG for it.
     *
     * @param query
     * @param time
     * @return
     */
    public static final TermTupleSetGroup getTupleSetGroupFromUrl(String query, long time, String indexField, IsoLanguage language) {
        HTMLPage page = InverterUtils.fetchHTMLPage(query, time);
        if (page == null) {
            return null;
        }
        try {
            return InverterUtils.getMergedTupleSetFromPage(page.getParser(), indexField, language);
        } catch (IOException e) {
            Log.util.error("Unable to get the tuple set %s %e", e, e);
            return null;
        }
    }
}
