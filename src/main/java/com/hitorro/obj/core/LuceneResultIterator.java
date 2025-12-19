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

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;
import java.util.Iterator;

/**
 * Copyright (c) 2003 - present HiTorro All rights reserved.
 * <p/>
 * User: chris
 * <p/>
 * Iterate over a series of result from lucene.
 */
public class LuceneResultIterator<E> implements Iterator<E> {
    private TopDocs m_hits;
    private ScoreDoc sd[];
    private long m_totalCount;
    private int m_maxResults;
    private long m_max;
    private int m_index = 0;
    private float m_minRank;
    private LuceneResultIteratorAdapter<E> m_adapter;
    private E m_nextVal = null;
    private IndexSearcher searcher;

    public LuceneResultIterator(LuceneResultIteratorAdapter<E> adapter,
                                int maxResults, float minRank, IndexSearcher searcher,
                                Query query)
            throws IOException {
        this.searcher = searcher;
        m_minRank = minRank;
        query = query.rewrite(searcher.getIndexReader());


        m_hits = searcher.search(query, 100000);
        m_totalCount = m_hits.totalHits.value;
        sd = m_hits.scoreDocs;
        m_maxResults = maxResults;
        Log.search.debug("Query: %s with hits: %s", query, m_totalCount);
        if (maxResults > 0) {
            m_max = Math.min(m_totalCount, m_maxResults);
        } else {
            m_max = m_totalCount;
        }
        m_adapter = adapter;
        m_nextVal = getAux();
    }

    private E getAux() throws IOException {
        if (m_index >= m_max) {
            return null;
        }
        Document doc = searcher.doc(sd[m_index].doc);
        float score = sd[m_index].score;
        m_index++;
        if (score < m_minRank) {
            // didnt meet min rank.
            return null;
        }
        return m_adapter.map(score, doc);
    }

    public boolean hasNext() {
        return m_nextVal != null;
    }

    public E next() {
        E prev = m_nextVal;
        try {
            m_nextVal = getAux();
        } catch (IOException ioe) {
            m_nextVal = null;
        }

        return prev;
    }

    public void remove() {
        // Remove not implemented
    }
}
