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
package com.hitorro.basetext.indexer.collector.meta;

import com.hitorro.util.core.Console;

/*
 */
public class ResultPayload {
    public int publicationDate;
    public int partitionDate;
    public float tfidf;
    public float score;
    public int docId;
    long ids[];

    ResultPayload() {
        ids = new long[Ids.values().length];
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Console.bprint(sb, "partitiondate:%s tfidf: %s, docId: %s", partitionDate, tfidf, docId);
        for (int i = 0; i < ids.length; i++) {
            if (ids[i] != 0) {
                Console.bprint(sb, ", ids[0]=%s", Long.toString(ids[i]));
            }
        }
        return sb.toString();
    }
}
