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

import org.sweble.wikitext.engine.config.WikiConfig;
import org.sweble.wikitext.engine.output.SafeLinkTitlePrinter;
import org.sweble.wikitext.parser.nodes.*;

import java.io.Writer;

public class MySafeLinkTitlePrinter extends SafeLinkTitlePrinter {

    public MySafeLinkTitlePrinter(final Writer writer, final WikiConfig wikiConfig) {
        super(writer, wikiConfig);
    }

    @Override
    public void visit(WtUrl n) {
        // TODO: Implement
        //throw new FmtNotYetImplementedError();
    }

    public void visit(WtExternalLink n) {
        // TODO: Implement
        //throw new FmtNotYetImplementedError();
    }

    @Override
    public void visit(WtImageLink n) {
        // TODO: Implement
        // throw new FmtNotYetImplementedError();
    }

    @Override
    public void visit(WtImEndTag n) {
        // Should not happen ...
        //throw new AssertionError();
    }

    @Override
    public void visit(WtImStartTag n) {
        // Should not happen ...
        //throw new AssertionError();
    }

    @Override
    public void visit(WtLinkOptionGarbage n) {
        // Should not happen ...
        //throw new AssertionError();
    }

    @Override
    public void visit(WtLinkOptionKeyword n) {
        // Should not happen ...
        //throw new AssertionError();
    }

    @Override
    public void visit(WtLinkOptionLinkTarget n) {
        // Should not happen ...
        //throw new AssertionError();
    }

    @Override
    public void visit(WtLinkOptionResize n) {
        // Should not happen ...
        //throw new AssertionError();
    }

    @Override
    public void visit(WtLinkOptions n) {
        // Should not happen ...
        //throw new AssertionError();
    }

    @Override
    public void visit(WtPageName n) {
        // Should not happen ...
        //throw new AssertionError();
    }

    @Override
    public void visit(WtRedirect n) {
        // TODO: Implement
        //throw new FmtNotYetImplementedError();
    }

    @Override
    public void visit(WtSignature n) {
        // TODO: Implement
        //throw new FmtNotYetImplementedError();
    }

    @Override
    public void visit(WtTableCaption n) {
        // TODO: Implement
        //throw new FmtNotYetImplementedError();
    }

    @Override
    public void visit(WtTableCell n) {
        // TODO: Implement
        //throw new FmtNotYetImplementedError();
    }

    @Override
    public void visit(WtTableHeader n) {
        // TODO: Implement
        //throw new FmtNotYetImplementedError();
    }

    @Override
    public void visit(WtTableRow n) {
        // TODO: Implement
        //throw new FmtNotYetImplementedError();
    }

    @Override
    public void visit(WtTagExtensionBody n) {
        // Should not happen ...
        //throw new AssertionError();
    }

    @Override
    public void visit(WtTicks n) {
        // Should not happen ...
        //throw new AssertionError();
    }

    @Override
    public void visit(WtXmlAttribute n) {
        // TODO: Implement
        //throw new FmtNotYetImplementedError();
    }

    @Override
    public void visit(WtTable n) {
        // TODO: Implement
        //throw new FmtNotYetImplementedError();
    }


}