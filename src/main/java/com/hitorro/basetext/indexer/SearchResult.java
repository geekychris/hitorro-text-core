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
package com.hitorro.basetext.indexer;

import com.hitorro.util.core.string.StringUtil;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class SearchResult {
    private String m_guid;
    private float m_score;
    private Map<String, String> m_map = new HashMap<String, String>();

    public void setDocument(IndexSearcher searcher, ScoreDoc hit, String[] fieldsToExtract) throws IOException {
        /*
        TopDocs td = s.search(q, 100000);

    int totalCount = td.totalHits;
    ScoreDoc[] sd = td.scoreDocs;
    response.setResponseShape(shape);
    for (int i = 0; i < totalCount; i++)
    {
        Document doc = s.doc(sd[i].doc);
        */
        m_map.clear();
        Document doc = searcher.doc(hit.doc);
        m_score = hit.score;
        m_guid = doc.get("guid");
        m_map.put("guid", m_guid);

        for (String field : fieldsToExtract) {
            String val = doc.get(field);
            if (!StringUtil.nullOrEmptyString(val)) {
                m_map.put(field, val);
            }
        }
    }

    public List<String> getRow() {
        List<String> l = new ArrayList<String>();
        l.add(m_guid);
        return l;
    }

    public String getGuid() {
        return m_guid;
    }

    public float getScore() {
        return m_score;
    }

    public String getFieldValue(String field) {
        return m_map.get(field);
    }
}
