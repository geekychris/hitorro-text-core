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

import com.hitorro.util.core.string.Fmt;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.FieldDoc;

/**
 *
 */
public class ExtendedFieldDoc extends FieldDoc {
    public ResultPayload payload;

    public ExtendedFieldDoc(int doc, float score, ResultPayload payload) {
        super(doc, score);
        this.payload = payload;
    }

    public ExtendedFieldDoc(FieldComparator comparators[], int doc, ResultPayload payload, Comparable[] fields) {
        super(doc, payload.tfidf);
        this.payload = payload;
    }

    public ResultPayload getPayload() {
        return payload;
    }

    public void setPayload(ResultPayload payload) {
        this.payload = payload;
    }

    public String toString() {
        return Fmt.S("doc:%s, score:%s, payload: %s", doc, score, payload);
    }
}
