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
package com.hitorro.basetext.winnow.streaming;

import com.hitorro.util.core.Console;
import com.hitorro.util.core.Log;
import com.hitorro.util.core.string.StringUtil;
import com.hitorro.util.io.ResetableStringReader;
import com.hitorro.util.testframework.EnhancedTestCase;

import java.io.File;
import java.io.IOException;

/**
 *
 */
//@HTTest(runlevel = RunLevel.Smoke,
//        email = "chris@hitorro.com",
//        description = "Winnowing hash function tests")
public class TestHash extends EnhancedTestCase {
    public void test() {
        String sent = null;
        String sent2 = null;
        StringBuilder sb = new StringBuilder();
        try {
            StringUtil.readFileIntoBuilder(sb, new File("/foo.txt"));
            sent = sb.toString();
            StringUtil.readFileIntoBuilder(sb, new File("/foo2.txt"));
            sent2 = sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        ResetableStringReader read = new ResetableStringReader(sent);
        FilteringReader fr = new FilteringReader();
        fr.setReader(read);


        FixedShingleWindowHash hb = new FixedShingleWindowHash(50);
        FixedShingleWindowHash hb2 = new FixedShingleWindowHash(50);
        Winnower w = new Winnower(15, hb);
        Winnower w2 = new Winnower(15, hb2);
        HashBase h = new HashBase(12, w);

        HashBase h2 = new HashBase(12, w2);
        try {
            h.read(fr);

            int arr1[] = hb.toSortedArray();
            read.set(sent2);
            Console.println("=======================");
            h2.read(fr);


            int arr2[] = hb2.toSortedArray();
            hb2.dump(arr1);
            hb.dump(arr2);
            int dist = ShingleUtil.computeSimilarity(arr1, arr2);
            Console.println("similarity: %s", dist);
        } catch (IOException e) {
            Log.test.error("Unable to parse content %s %e", e, e);
        }
    }
}
