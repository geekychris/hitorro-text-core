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

import com.fasterxml.jackson.databind.JsonNode;
import com.hitorro.util.core.Console;
import com.hitorro.util.core.Log;
import com.hitorro.util.core.Timer;
import com.hitorro.util.core.iterator.sinks.Sink;
import com.hitorro.util.core.string.StringUtil;
import com.hitorro.util.html.HTMLPage;
import com.hitorro.util.html.HTMLParser;
import com.hitorro.util.io.FileUtil;
import com.hitorro.util.io.StoreException;
import com.hitorro.util.typesystem.BaseSession;
import com.hitorro.util.typesystem.BaseSessionFactory;

import java.io.File;
import java.io.IOException;

/**
 *
 */
public class ODPSink implements Sink<ODPFetchElement> {
    private int m_counterSuccess = 0;
    private int m_fails = 0;
    private Timer m_timer = new Timer();
    private StringBuilder m_builder = new StringBuilder();

    private void writeToBlog(HTMLPage page) {
        BaseSession session = BaseSessionFactory.getFactory().getSession();
        boolean committed = false;
        try {
            HTMLParser parser = page.getParser();
            String body = parser.getBodyText();
            String title = parser.getTitleText();
           /*
              need to substitute in a way to persist these things without all the dms references
           Post item = new Post();
            item.setBody(body);
            if (!StringUtil.nullOrEmptyOrBlankString(title)) {
                if (title.length() > 20) {
                    title = title.substring(0, 20);
                }
            }
            item.setTitle(title);
            session.persist(item);

            */
			BaseSessionFactory.getFactory().commitAndClose(session);
            committed = true;
        } catch (Exception err) {
            Log.util.info("blew up processing %s %e", err, err);
        } finally {
            if (!committed) {
                BaseSessionFactory.getFactory().rollbackClose(session);
            }
        }

    }

    @Override
    public boolean init(final JsonNode node) {
        return false;
    }

    @Override
    public boolean start() throws IOException {
        return false;
    }

    public void accept(ODPFetchElement e) {
        if (e.m_page != null && e.m_page.getSource() != null) {
            m_counterSuccess++;
            if (m_counterSuccess % 1000 == 0) {
                m_timer.stop();
                long time = m_timer.getTime();

                m_timer.reset();
                Console.println("wrote %s avg time %s, failures %s", m_counterSuccess, time / 1000, m_fails);


            }
            FileUtil.ensureParentDirectories(e.m_path, true);
            try {
                FileUtil.writeToFile(e.m_path, e.m_page.getSource());
                File txtFile = new File(FileUtil.getFileNameFromPeer(e.m_path.getAbsolutePath(), "txt"));
                m_builder.setLength(0);
                Console.bprintln(m_builder, "url=%s", e.m_url);
                Console.bprintln(m_builder, "topic=%s", e.m_topic);
                FileUtil.writeToFile(txtFile, m_builder);

                writeToBlog(e.m_page);
            } catch (IOException e1) {
                Log.util.error("%s %e", e1, e1);
            }
            try {
                Log.util.debug("Wrote %s to %s", e.m_url, e.m_path.getCanonicalPath());
            } catch (IOException e1) {
                Log.util.error("%s %e", e1, e1);
            } catch (Exception e5) {
                Log.util.error("Spewed up on: %s %e", e5, e5);
            }
        } else {
            m_fails++;
            Log.util.debug("Unable to write %s as no content was fetched", e.m_url);
        }
    }

    @Override
    public boolean add(final ODPFetchElement o) throws IOException, StoreException {
        return false;
    }

    @Override
    public boolean stop() throws IOException {
        return false;
    }

    public void close() {
    }
}