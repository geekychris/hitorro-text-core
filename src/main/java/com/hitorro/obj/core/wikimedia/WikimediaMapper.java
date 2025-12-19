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
package com.hitorro.obj.core.wikimedia;

import com.hitorro.util.core.string.Fmt;
import com.hitorro.util.core.string.StringUtil;
import com.hitorro.util.xml.BaseStaxXmlParser;
import com.hitorro.util.xml.XE;
import org.sweble.wikitext.engine.*;
import org.sweble.wikitext.engine.config.WikiConfig;
import org.sweble.wikitext.engine.nodes.EngProcessedPage;
import org.sweble.wikitext.engine.output.HtmlRendererCallback;
import org.sweble.wikitext.engine.output.MediaInfo;
import org.sweble.wikitext.engine.utils.DefaultConfigEnWp;
import org.sweble.wikitext.parser.nodes.WtUrl;
import org.sweble.wikitext.parser.parser.LinkTargetException;

import javax.xml.stream.XMLStreamException;

public class WikimediaMapper {
    StringBuilder sb = new StringBuilder();
    private WikiConfig config;
    // Instantiate Sweble parser
    private WtEngine engine;
    private BaseStaxXmlParser bsParser = new BaseStaxXmlParser();

    public WikimediaMapper() {
        config = DefaultConfigEnWp.generate();

        // Instantiate Sweble parser
        engine = new WtEngineImpl(config);
    }

    public String parseText(String wikitext, String pagetitle, String language) {
        try {
            PageTitle pageTitle = PageTitle.make(config, pagetitle);
            PageId pageId = new PageId(pageTitle, -1);
            // Parse Wikitext into AST
            if (StringUtil.nullOrEmptyString(wikitext, pagetitle)) {
                return "";
            }
            EngProcessedPage cp = engine.postprocess(pageId, wikitext, null);

            // Render AST to XML
            String uri = language + ".wikipedia.org/wiki/";
            String wikiXML = XMLRenderer.print(new MyRendererCallback(), config, pageTitle, cp.getPage(), uri);
            sb.setLength(0);
            Fmt.f(sb, "<xml>%s</xml>", wikiXML);
            return sb.toString();
        } catch (Exception e) {
            //The wiki parser throws all kinds of exceptions.  Sometimes its non trivial to prevent them exploding.
            // for now lets skip documents that dont behave.

            return "";
        }
    }

    public XE parseToXML(String wikitext, String pagetitle, String language) throws LinkTargetException, EngineException, XMLStreamException {
        StringBuilder sb = new StringBuilder();
        String txt = parseText(wikitext, pagetitle, language);
        Fmt.f(sb, "<xml>%s</xml>", txt);
        bsParser.setInput(sb.toString(), "xml");
        return bsParser.renderObject();
    }

    private static final class MyRendererCallback
            implements
            HtmlRendererCallback {
        @Override
        public boolean resourceExists(PageTitle target) {
            return false;
        }

        @Override
        public String makeUrl(final PageTitle linkTarget) {
            return null;
        }

        @Override
        public String makeUrl(final WtUrl target) {
            return null;
        }

        @Override
        public String makeUrlMissingTarget(final String path) {
            return null;
        }

        @Override
        public MediaInfo getMediaInfo(
                String title,
                int width,
                int height) {
            return null;
        }
    }
}
