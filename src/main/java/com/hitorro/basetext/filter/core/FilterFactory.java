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
package com.hitorro.basetext.filter.core;

import com.hitorro.util.core.string.StringUtil;
import com.hitorro.util.io.FileUtil;

import java.io.File;
import java.util.*;

/**
 * Copyright (c) 2003 - present HiTorro All rights reserved. User: chris Date: Oct 5, 2003 Time: 8:28:38 PM To
 */
public class FilterFactory {
    private static FilterFactory s_factory;
    private Map m_filters = new Hashtable();
    private List m_extensions = new Vector();


    public FilterFactory() {

    }

    public static FilterFactory getFactory() {
        return s_factory;
    }

    public static void initFactory(List filters) {
        Iterator iterator = filters.iterator();
        FilterFactory ff = new FilterFactory();
        while (iterator.hasNext()) {
            TextFilter tf = (TextFilter) iterator.next();
            ff.registerFilter(tf);
        }
        s_factory = ff;
    }

    public void registerFilter(TextFilter textFilter) {
        String[] extensions = textFilter.extensions();
        for (int i = 0; i < extensions.length; i++) {
            String extension = extensions[i];
            m_filters.put(extension, textFilter);
            m_extensions.add(extension);
        }
    }

    public List extensions() {
        return m_extensions;
    }

    public TextFilter getFilterByFileName(File file) {
        String fileName = file.getName();
        return getFilterByFileName(fileName);
    }

    public TextFilter getFilterByFileName(String file) {
        String ext = FileUtil.getFileExtension(file);
        if (!StringUtil.nullOrEmptyString(ext)) {
            return (TextFilter) m_filters.get(ext);
        }
        return null;
    }
}
