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

import com.hitorro.util.core.Constants;
import com.hitorro.util.core.hash.FPHash64;
import com.hitorro.util.core.iterator.queue.AbstractEnqueue;
import com.hitorro.util.core.string.StringUtil;
import com.hitorro.util.core.thread.EnhancedThreadGroup;
import com.hitorro.util.core.thread.farm.Farm;
import com.hitorro.util.core.thread.farm.FarmSink;
import com.hitorro.util.io.FileUtil;
import com.hitorro.util.xml.RDFHandler;

import java.io.File;

/**
 */
public class ODPRdfHandler extends RDFHandler {
    private File m_root;

    private AbstractEnqueue<ODPFetchElement> m_queueIn = AbstractEnqueue.arrayBlocking(100);

    private AbstractEnqueue<ODPFetchElement> m_queueOut = AbstractEnqueue.arrayBlocking(100);
    private EnhancedThreadGroup m_threadGroup = new EnhancedThreadGroup("ODPFetcher");
    private Farm m_farm = new Farm("odpFetcher", m_threadGroup, m_queueIn, m_queueOut, new ODPFarmCommand(), 40);
    private FarmSink m_sink = new FarmSink("ODPSInk", m_threadGroup, m_queueOut, new ODPSink());

    public ODPRdfHandler(File root) {
        m_root = root;
        // every 10 seconds check that all the threads are still alive.
        m_farm.useKeepAliveThread(Constants.MillisInSecond * 10);
        m_farm.start();
        m_sink.start();
    }

    public void set(String title, String description, String topic, String url) {
        long fp = FPHash64.getFP(url);
        topic = StringUtil.replace(topic, "\n", "");
        topic = topic.trim();
        String path = FileUtil.idToHexPath(fp);
        path = StringUtil.strcat(path, ".html");
        ODPFetchElement elem = new ODPFetchElement();
        elem.set(url, topic, new File(m_root, path));
        try {
            m_queueIn.put(elem);
        } catch (InterruptedException e) {
        }

    }

}
