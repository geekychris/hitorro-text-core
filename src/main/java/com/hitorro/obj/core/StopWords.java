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
package com.hitorro.obj.core;

import com.hitorro.language.IsoLanguage;
import com.hitorro.util.basefile.fs.BaseFile;
import com.hitorro.util.core.string.Fmt;
import com.hitorro.util.json.keys.BasefileProperty;
import org.apache.lucene.analysis.CharArraySet;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class StopWords {
    public static final BasefileProperty root = new BasefileProperty("analysis.stopwords.rootdir", "", "file://${ht_data}/stopwords/");
    private BaseFile langDir;
    private Map<String, CharArraySet> casMap = new HashMap();

    public StopWords(IsoLanguage lang) {
        langDir = root.apply().getChild(lang.getTwo());
    }

    public synchronized CharArraySet getStopWordsSet(String field) {
        CharArraySet cas = casMap.get(field);
        if (cas != null) {
            return cas;
        }
        BaseFile file = langDir.getChild(Fmt.S("%s.txt", field));
        if (file.exists()) {
            cas = getCommonWordsFromFile(file);
        } else {
            cas = new CharArraySet(0, true);
        }
        casMap.put(field, cas);
        return cas;
    }

    private CharArraySet getCommonWordsFromFile(BaseFile fileName) {
        boolean ignoreCase = true;
        CharArraySet commonWords = new CharArraySet(0, ignoreCase);

        if (!fileName.exists()) {
            Log.stop.warn("Could not find common words file %s", fileName);
            return commonWords;
        }

        BufferedReader r = null;
        try {
            r = new BufferedReader(fileName.getReader());
            String line;
            while ((line = r.readLine()) != null) {
                line = line.trim().toLowerCase();
                if (line.startsWith("#") || line.length() == 0) {
                    continue;
                }
                commonWords.add(line);
            }
        } catch (IOException e) {
            Log.stop.error("Unable to get stop words for file %s", fileName);
            return null;
        }
        return commonWords;
    }

}
