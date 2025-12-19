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
package com.hitorro.basetext;

import com.hitorro.util.io.FileUtil;
import com.hitorro.util.io.StringInputStream;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.model.DocumentProperties;
import org.apache.poi.hwpf.model.ListTables;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utility methods to hide the reading a microsoft file.
 */
public class MSWordTextStreamUtil {
    public static InputStream getInputStreamFromMSFile(File file) throws IOException {
        InputStream is = FileUtil.getBufferedFileInputStream(file);
        HWPFDocument doc = new HWPFDocument(is);
        ListTables lt = doc.getListTables();
        DocumentProperties dp = doc.getDocProperties();
        WordExtractor we = new WordExtractor(doc);
        String text[] = we.getParagraphText();
        StringBuilder sb = new StringBuilder();

        for (String t : text) {
            sb.append(t);
        }
        return new StringInputStream(sb.toString());
    }

}
