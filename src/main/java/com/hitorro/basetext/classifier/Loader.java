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
package com.hitorro.basetext.classifier;

import com.hitorro.util.core.string.StringUtil;
import com.hitorro.util.io.filefilters.FileStartsEndsWith;
import com.hitorro.util.io.filefilters.NotFilter;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Stack;

/**
 * Copyright (c) 2003 - present HiTorro All rights reserved.
 * <p/>
 * User: chris
 * <p/>
 * Load a set of documents in and pass them to either the isQuestionClassifier or to the trainer.
 */
public class Loader {
    private static final FilenameFilter s_noDots = new NotFilter(new FileStartsEndsWith(".", false, false));

    public Loader() {

    }

    public boolean load(File dir, LoaderCallback callback) {
        Stack<String> list = new Stack<String>();
        return loadAux(dir, list, callback);
    }

    private boolean loadAux(File dir, Stack<String> category, LoaderCallback callback) {
        File files[] = dir.listFiles(s_noDots);
        if (files == null) {
            return false;
        }
        for (File f : files) {
            if (f.isFile()) {
                callback.document(f, StringUtil.toArray(category));
            }
            category.push(f.getName());
            loadAux(f, category, callback);
            category.pop();
        }
        return true;
    }


}
