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

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

/**
 * Copyright (c) 2003 - present HiTorro All rights reserved. User: chris Date: Oct 5, 2003 Time: 7:09:34 PM
 */
public class CrawlerFilenameFilter implements FilenameFilter {
    private String m_extensions[] = {"doc", "xls", "txt"};

    public CrawlerFilenameFilter(List extensions) {
        Object[] tempArray = extensions.toArray();
        m_extensions = new String[tempArray.length];

        for (int i = 0; i < tempArray.length; i++) {
            m_extensions[i] = (String) tempArray[i];
        }
    }

    public boolean accept(File test, String name) {
        if (test.isDirectory()) {
            // always true for a directory
            return true;
        }
        return isIndexableFile(test);
    }

    /*
        Not very efficient or accurate file determining tool
    */
    private boolean isIndexableFile(File test) {
        String file = test.getName();
        for (int i = 0; i < m_extensions.length; i++) {
            String extension = m_extensions[i];
            if (file.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
}
