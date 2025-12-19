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
import com.hitorro.basetext.inverter.DocumentInverter;
import com.hitorro.basetext.inverter.TFIDFTermMeasureFunction;
import com.hitorro.basetext.inverter.TermTuple;
import com.hitorro.basetext.inverter.TermTupleSet;
import com.hitorro.jsontypesystem.JVS;
import com.hitorro.language.Iso639Table;
import com.hitorro.language.IsoLanguage;
import com.hitorro.util.commandandcontrol.ano.CommandArgument;
import com.hitorro.util.commandandcontrol.ano.CommandDef;
import com.hitorro.util.commandandcontrol.ano.RespColumn;
import com.hitorro.util.commandandcontrol.ano.ResponseDefinition;
import com.hitorro.util.core.events.cache.SingletonCache;
import com.hitorro.util.core.string.Fmt;
import com.hitorro.util.html.HTMLPage;
import com.hitorro.util.html.HTMLPageFetcher;
import com.hitorro.util.html.Link;
import com.hitorro.util.json.keys.StringProperty;

import java.util.List;

/**
 * Copyright (c) 2003 - present HiTorro All rights reserved. User: chris Date: Jan 12, 2005 Time: 8:41:43 AM
 */
@CommandDef(command = "text.inverturl", description = "Fetch the content of the url and invert it")
public class InvertUrlContent extends com.hitorro.util.commandandcontrol.Command {
    @CommandArgument(required = true)
    public static final StringProperty Url = new StringProperty("url", "url to fetch", null);

    @ResponseDefinition(command = "invertdocument",
            rowname = "section",
            columns = {@RespColumn(name = "Section", lName = "section"),
                    @RespColumn(name = "Term", lName = "term"),
                    @RespColumn(name = "isGood", lName = "isgood"),
                    @RespColumn(name = "Norm TF", lName = "normtf"),
                    @RespColumn(name = "Norm DF", lName = "normdf"),
                    @RespColumn(name = "Measure", lName = "measure")})
    private com.hitorro.util.commandandcontrol.ResponseShape shape = new com.hitorro.util.commandandcontrol.ResponseShape();

    public boolean execute(String rawValue, JVS args, com.hitorro.util.commandandcontrol.Response response, com.hitorro.util.commandandcontrol.CommandSession session) throws Exception {
        HTMLPageFetcher fetcher = new HTMLPageFetcher();
        fetcher.setHttpTimeout(1000);
        String url = Url.apply(args);
        HTMLPage page = fetcher.fetchPage(url);
        if (page == null || page.getSource() == null) {
            this.writeSimpleError(response, "unable to fetch url %s", url);
            return false;
        }
        String contentType = page.getContentTypeFromContent();
        List<Link> links = page.getLinks();
        response.addInfo(com.hitorro.util.commandandcontrol.InfoLevel.Info, Fmt.S("content type: %s", contentType));
        for (Link l : links) {
            response.addInfo(com.hitorro.util.commandandcontrol.InfoLevel.Info, Fmt.S("link: %s", l.getUrl()));
        }
        SingletonCache<DFIndex> cache = DFIndexSingletonMapper.getSingleton();
        DFIndex index = cache.get();
        IsoLanguage lang = Iso639Table.english;
        DocumentInverter inverter = new DocumentInverter("body", null, new TFIDFTermMeasureFunction(), TermTupleSet.s_MeasureDescendComparitor);
        TermTupleSet<TermTuple> title = inverter.setText("title", page.getParser().getTitleText(), lang);


        TermTupleSet<TermTuple> body = inverter.setText("body", page.getParser().getBodyText(), lang);
        response.setResponseShape(shape);
        dumpSection(title, response, "title");
        dumpSection(body, response, "body");
        response.end();
        return false;
    }

    private void dumpSection(TermTupleSet<TermTuple> set, com.hitorro.util.commandandcontrol.Response response, String section) {
        for (TermTuple tt : set.getTuplesList()) {
            response.addRow(section, tt.getTerm(), tt.isGood(), tt.getNormalizedTF(), tt.getNormalizedDF(), tt.getMeasure());
        }
    }
}
