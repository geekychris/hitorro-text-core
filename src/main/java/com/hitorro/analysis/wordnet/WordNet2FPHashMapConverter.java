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
package com.hitorro.analysis.wordnet;

import com.hitorro.util.core.ArrayUtil;
import com.hitorro.util.core.string.Fmt;
import com.hitorro.util.core.string.StringUtil;
import com.hitorro.util.io.FileUtil;
import com.hitorro.util.io.csv.CSVFileWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Read in a wordnet index.* file and generate a key,value pair for building a FPHashMap bitset
 * <p/>
 * WordNet2FPHashMapConverter.convertAll(new File("/publicsource/opw/WordNet-3.0/dict"), new
 * File("/ht/hitorro/data/wordnet"));
 */
public class WordNet2FPHashMapConverter {
    private static final String[] keys = {"adj", "adv", "noun", "sense", "verb"};

    public static void convert() throws FileNotFoundException {
        convertAll(new File("/publicsource/opw/WordNet-3.0/dict"), new File("/ht/hitorro/data/wordnet"));
    }

    public static void convertAll(File directory, File outputDirectory) throws FileNotFoundException {
        for (String key : keys) {
            File input = new File(directory, Fmt.S("index.%s", key));
            File output = new File(outputDirectory, Fmt.S("%s.csv", key));
            convert(input, output, "key", key, "1");
        }
    }

    public static void convert(File fileIn, File fileOut, String keyName, String targetColumnName, String value) throws FileNotFoundException {
        List<String> cols = new ArrayList();
        cols.add(keyName);
        cols.add(targetColumnName);
        CSVFileWriter writer = new CSVFileWriter(fileOut, cols);
        Iterator<String> iter = FileUtil.getLineReaderIteratorFromFile(fileIn);
        while (iter.hasNext()) {
            String row = iter.next();
            if (row.startsWith(" ")) {
                continue;
            }
            String parts[] = StringUtil.tokenizeFromSingleChar(row, " ");
            if (ArrayUtil.nullOrEmpty(parts)) {
                continue;
            }
            String word = parts[0];
            word = word.replaceAll("_", " ");
            writer.writeRow(new Object[]{word, value});
        }
        writer.close();
    }
}
