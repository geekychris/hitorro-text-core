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
package com.hitorro.basetext.commands;

import com.hitorro.jsontypesystem.JVS;
import com.hitorro.util.commandandcontrol.Command;
import com.hitorro.util.commandandcontrol.CommandSession;
import com.hitorro.util.commandandcontrol.Response;
import com.hitorro.util.commandandcontrol.ano.CommandArgument;
import com.hitorro.util.commandandcontrol.ano.CommandDef;
import com.hitorro.util.html.HTMLEncoder;
import com.hitorro.util.html.HTMLPage;
import com.hitorro.util.html.HTMLPageFetcher;
import com.hitorro.util.json.keys.StringProperty;

/**
 * <p/>
 * Copyright (c) 2003 - present HiTorro All rights reserved. User: chris Date: Oct 11, 2007
 * http://feeds.feedburner.com/~r/chrisbrogandotcom/~3/167895307/ <http://feeds.feedburner.com/%7Er/chrisbrogandotcom/%7E3/167895307/
 * <p/>
 * Time: 1:51:56 PM
 */

@CommandDef(command = "text.fetchpage", description = "Fetch a html page")
public class FetchPageCommand extends Command {
    @CommandArgument(required = true)
    private StringProperty Url = new StringProperty("url", "Reason given for exit", null);

    public boolean execute(String rawValue, JVS args, Response response, CommandSession session) throws Exception {
        HTMLPageFetcher fetcher = new HTMLPageFetcher();
        String url = Url.apply(args);
        HTMLPage page = fetcher.fetchPage(HTMLEncoder.decodeHtml(url));
        if (page == null) {
            this.writeSimpleError(response, "unable to fetch page");
        } else {
            this.writeSuccess(response, "got page with real url of: %s, response code %s", page.getDestinationUrl(), fetcher.getCode());
        }
        return true;
    }
}
