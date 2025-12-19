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
package com.hitorro.basetext.winnow;

/**
 * Copyright (c) 2003 - present HiTorro All rights reserved. User: chris Date: Oct 5, 2003 Time: 6:11:04 PM To change this
 * template use Options | File Templates.
 */
public class WinnowingTestDriver {

    public static void main(String args[]) {

    }

    public static void hashTest() {
        int subStringLength = 23;
        int windowSize = 23;
        long time = System.currentTimeMillis();
        //long result = Hash.hash("C:/rover/code/work/bin/tuft.txt", "C:/rover/code/work/bin/tuft.fp.chris",  substringlength, windowSize);
        String unixBase = "/home/chris/dosstuff/cdrom/rover/code/work/bin/";
        Hash.hashAndWinnowToFile(unixBase + "tuft.txt", unixBase + "tuft.fp.chris", subStringLength, windowSize);
        long time2 = System.currentTimeMillis();
        Long val = new Long(time2 - time);
        System.out.println("Took: " + val.toString());
        //Hash.hashToFile ("C:/rover/code/work/bin/tuft.txt", "C:/rover/code/work/bin/tuft.hash.chris", subStringLength, windowSize);

        /*rover.filter.filters.Initializer.init();

        ht.rover.util.logging.Log.util.fine("Starting test");
        Crawler dirfileprocessor = new DirectoryCrawler(new File(TestDirectory),
                                               new CrawlerFilenameFilter(rover.filter.core.FilterFactory.getFactory().extensions()));
        ContentQueue queue = new ContentQueue();
        dirfileprocessor.crawl(queue); */
    }
}
