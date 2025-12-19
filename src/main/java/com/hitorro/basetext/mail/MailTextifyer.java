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
package com.hitorro.basetext.mail;

import com.hitorro.basetext.filter.core.FilterFactory;
import com.hitorro.basetext.filter.core.TextFilter;
import com.hitorro.util.core.GenericKeyValue;
import com.hitorro.util.core.Log;
import com.hitorro.util.core.string.StringUtil;
import com.hitorro.util.io.FileUtil;
import com.hitorro.util.mail.MailDecomposer;

import java.io.File;

/**
 * Copyright (c) HiTorro 2003-2008, Inc.
 * <p/>
 * User: chris Date: Jul 14, 2004 Time: 4:52:55 PM
 * <p/>
 * Description:
 */
public class MailTextifyer extends MailDecomposer {
    public MailTextifyer(File mimeFile, File outputDir) {
        super(mimeFile, outputDir);
    }

    /*
        Take files and create textual versions of files.
    */

    public boolean convertContent() {
        for (int i = 0; i < m_files.size(); i++) {
            GenericKeyValue<String, String> pair = (GenericKeyValue) m_files.get(i);
            File f = convertFileToText(pair.getValue());
            if (f != null) {
                pair.setValue(f.toString());
            } else {
                // dont want to process it.
                pair.setValue(null);
            }
        }
        return true;
    }

    /*
    Attempt to convertToPdf a file to a textual representation.  If it
    cannot be converted then null is returned.
    */

    private File convertFileToText(String f) {
        TextFilter filter = FilterFactory.getFactory().getFilterByFileName(f);
        if (filter == null) {
            return null;
        }
        File inFile = new File(StringUtil.strcat(m_outputDir.toString(), "/", f));
        File fileOut = getTextFileOut(inFile);
        Class foo = filter.getClass();
        Log.util.debug("converting %s to %s using filter %s", inFile, fileOut, foo.toString());
        return filter.convert(inFile, fileOut);
    }

    private File getTextFileOut(File f) {
        return FileUtil.getUniqueFileName(f.getParentFile(), "txt");
    }
}
