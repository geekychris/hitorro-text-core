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
package com.hitorro.basetext.dirfileprocessor.file;

import com.hitorro.basetext.dirfileprocessor.core.ContentQueue;
import com.hitorro.basetext.dirfileprocessor.core.Crawler;

import java.io.File;
import java.io.FilenameFilter;

/**
 */
public class DirectoryCrawler implements Crawler {
    private File rootDirectory = null;
    private FilenameFilter m_filter = null;

    public DirectoryCrawler(File directory, FilenameFilter filter) {
        rootDirectory = directory;
        m_filter = filter;
    }

    public void crawl(ContentQueue queue) {
        if (rootDirectory.isDirectory()) {
            crawl(queue, rootDirectory);
        } else {
            // not a directory
        }

    }

    private void crawl(ContentQueue queue, File directory) {
        File[] files = directory.listFiles(m_filter);
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isDirectory()) {
                crawl(queue, file);
            } else {
                queue.addContent(file);
            }
        }
    }
}
