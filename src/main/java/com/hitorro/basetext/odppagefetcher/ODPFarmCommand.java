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
package com.hitorro.basetext.odppagefetcher;

import com.hitorro.util.core.Log;
import com.hitorro.util.core.string.StringUtil;
import com.hitorro.util.core.thread.farm.FarmCommand;
import com.hitorro.util.html.HTMLPage;
import com.hitorro.util.html.HTMLPageFetcher;
import com.hitorro.util.urlparser.UrlCursor;

/**
 * template use File | Settings | File Templates.
 */
class ODPFarmCommand extends FarmCommand<ODPFetchElement, ODPFetchElement, Object> {
    private ThreadLocal<HTMLPageFetcher> fetcherTL = new ThreadLocal<HTMLPageFetcher>();
    private ThreadLocal<UrlCursor> urlCursor = new ThreadLocal<UrlCursor>();

    public ODPFetchElement apply(ODPFetchElement inElement) {
        HTMLPageFetcher fetcher = getFetcher();


        try {
            if (inElement.m_path.exists()) {
                // we already fetched it.
                return inElement;
            }
            String url = inElement.m_url;
            UrlCursor cur = getCursor();
            if (cur.setUrl(url)) {
                // only fetch if it looks like a valid url.
                HTMLPage page = fetcher.fetchPage(url);
                if (page != null && !StringUtil.nullOrEmptyString(page.getSource())) {
                    inElement.m_page = page;
                }
            }
        } catch (IllegalStateException ise) {

        } catch (Exception e) {
            Log.util.error("Didnt catch in ODPRdfFetcher %s %e", e, e);
        }


        return inElement;
    }

    private UrlCursor getCursor() {
        UrlCursor c = urlCursor.get();
        if (c == null) {
            c = new UrlCursor();
            urlCursor.set(c);
        }

        return c;
    }

    private HTMLPageFetcher getFetcher() {
        HTMLPageFetcher f = fetcherTL.get();

        if (f == null) {
            f = new HTMLPageFetcher();
            f.setHttpTimeout(1000);
            fetcherTL.set(f);
        }
        return f;
    }
}
