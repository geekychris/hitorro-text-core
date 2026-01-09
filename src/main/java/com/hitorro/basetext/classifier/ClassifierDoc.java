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
package com.hitorro.basetext.classifier;

import com.hitorro.basetext.inverter.InverterUtils;
import com.hitorro.basetext.inverter.TermTuple;
import com.hitorro.basetext.inverter.TermTupleSet;
import com.hitorro.basetext.inverter.TermTupleSetGroup;
import com.hitorro.language.Iso639Table;
import com.hitorro.util.core.map.HashToIdAllocatingMap;
import com.hitorro.util.core.math.SparseVector;
import com.hitorro.util.core.string.StringBuilderUtil;
import com.hitorro.util.core.string.StringUtil;
import com.hitorro.util.html.HTMLPage;
import com.hitorro.util.html.HTMLParser;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class ClassifierDoc {
    public static final String CategoryJoinToken = ":";
    private File m_file;
    private String[] m_category;
    private String categoryString;
    private TermTupleSetGroup tupleGroup = null;
    private TermTupleSet m_set = null;
    private double m_minMeasure = 1000;
    private int maxTuples = 140;

    public ClassifierDoc(File f, String[] category) {
        m_file = f;
        m_category = category;
        categoryString = StringUtil.mergeWithJoinToken(m_category, CategoryJoinToken);
    }

    /**
     * Get a sparse vector for the test document, we need to find sufficient candidates out of the sea of crap.
     *
     * @param idMap
     * @param normalize
     * @param set
     * @return
     */
    public static SparseVector getSparseVectorAssumingNonAssignmentOfTerm(HashToIdAllocatingMap idMap,
                                                                          boolean normalize,
                                                                          TermTupleSet set, int maxTerms) {
        List<TermTuple> list = set.getTuplesList();

        int count = 0;
        for (TermTuple tt : list) {
            if (tt.isGood()) {
                // NOTE getHash does NOT work if in non translating mode
                if (idMap.getHashExists(tt.m_hash)) {
                    count++;
                }
            }
        }
        maxTerms = Math.min(maxTerms, count);
        if (maxTerms == 0) {
            // we didnt find any terms, not much point running the model
            return null;
        }
        SparseVector sv = new SparseVector(maxTerms, idMap.getSize());
        for (TermTuple tt : list) {
            if (maxTerms == 0) {
                break;
            }
            if (tt.isGood()) {
                if (idMap.getHashExists(tt.m_hash)) {
                    sv.setNextElement(idMap.getDocId(tt.m_hash), tt.getMeasure());
                    maxTerms--;
                }
            }
        }
        sv.sortByElemPosition();
        if (normalize) {
            sv.calculateEuclidNormalize();
        }
        return sv;
    }

    public File getFile() {
        return m_file;
    }

    public String getCategory() {
        return categoryString;
    }

    public HTMLPage getHtmlPage() throws IOException {
        HTMLPage page = new HTMLPage();
        String src = getDocumentRaw();
        if (StringUtil.nullOrEmptyString(src)) {
            return null;
        }
        page.setSource(src);
        return page;
    }

    public String getDocumentRaw() throws IOException {
        if (m_file.exists()) {
            StringBuilder builder = new StringBuilder();
            StringBuilderUtil.readFileIntoBuilder(builder, m_file);
            return builder.toString();
        }
        return null;
    }

    public TermTupleSetGroup getTupleSetGroup() throws IOException {
        if (tupleGroup == null) {
            HTMLPage page = getHtmlPage();
            HTMLParser parser = page.getParser();
            tupleGroup = InverterUtils.getMergedTupleSetFromPage(parser, "body", Iso639Table.english);
            tupleGroup.merge("title", "body", "all");
        }
        return tupleGroup;
    }

    public TermTupleSet getAll() throws IOException {
        if (m_set == null) {
            TermTupleSetGroup g = getTupleSetGroup();
            if (g != null) {
                m_set = g.getByName("all");
                m_set.prune(m_minMeasure, maxTuples);
            }
        }

        return m_set;
    }

    public boolean getTermSpace(HashToIdAllocatingMap idMap) throws IOException {
        TermTupleSet set = getAll();
        if (set == null) {
            return false;
        }
        int count = 0;
        List<TermTuple> list = set.getTuplesList();
        for (TermTuple tt : list) {
            idMap.getDocIdWithIDAllocation(tt.m_hash);
            count++;
        }
        return count > 0;
    }

    /**
     * Assumes that the term space in idMap has been precomputed by calling: getTermSpace
     *
     * @param idMap
     * @return
     * @throws IOException
     */
    public SparseVector getSparseVector(HashToIdAllocatingMap idMap, boolean normalize) throws IOException {
        TermTupleSet set = getAll();
        if (set == null) {
            return null;
        }
        List<TermTuple> list = set.getTuplesList();
        SparseVector sv = new SparseVector(list.size(), idMap.getSize());
        for (TermTuple tt : list) {
            sv.setNextElement(idMap.getDocId(tt.m_hash), tt.getMeasure());
        }
        sv.sortByElemPosition();
        if (normalize) {
            sv.calculateEuclidNormalize();
        }
        return sv;
    }
}
