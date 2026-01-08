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
package com.hitorro.basetext.inverter;

import com.hitorro.basetext.dfindex.DFIndex;
import com.hitorro.language.IsoLanguage;
import com.hitorro.util.core.math.SparseVector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Collection of documents to be inverted
 * <p/>
 */
public class DocumentCollection {
    private List<TermTupleSetGroup> docs = new ArrayList<TermTupleSetGroup>();
    private String filters;

    /**
     * Provide the filters to use in the tokenization. One can pass null which will use the default filters
     *
     * @param filters
     */
    public DocumentCollection(String filters) {
        this.filters = filters;
    }

    public void setSortFunction(Comparator<TermTuple> sorter) {
        for (TermTupleSetGroup doc : docs) {
            doc.setSortFunction(sorter);
        }
    }

    public TermTupleSetGroup addDocument(String id, String sectionName, String sectionContent, String indexField, IsoLanguage language) throws IOException {
        TermTupleSetGroup set = InverterUtils.getTupleSetFromTextWithFilter(filters, sectionName, sectionContent, indexField, language);
        set.setId(id);
        docs.add(set);
        return set;
    }

    /**
     * For two sections of text, maybe title and body.  Merge provides the option of combining the two into an "all"
     * field.
     * <p/>
     * If you want to create a df index from groups that are sets then you need to run this guy in "merged" mode to
     * generate an "all" set. If you want to get a df for all document parts.
     */
    public TermTupleSetGroup addDocument(String id, boolean merge, String sn1, String sc1, String sn2, String sc2, String indexField, IsoLanguage language) throws IOException {
        TermTupleSetGroup set = InverterUtils.getTupleSetWithFilter(filters, merge, sn1, sc1, sn2, sc2, indexField, language);
        set.setId(id);
        docs.add(set);
        return set;
    }

    /**
     * Compute a df index from the provided section.  Set the max allowable tf frequency as a percentage of corpus size.
     * If you dont want to hit a max, set this to 100%
     *
     * @param sectionName
     * @param frequencyMax
     * @return
     */
    public DFIndex getDFIndexFrom(String sectionName, double frequencyMax) {
        DFIndex index = new DFIndex(0);
        index.setDetails("Doc Collection DFIndex", "Constrained by Document Collection");
        TermTupleSet ts;
        for (TermTupleSetGroup g : docs) {
            ts = g.getByNameAux(sectionName);
            if (ts != null) {

                ts.addDocumentToDF(index);
            }
        }
        index.setFrequencyMaxByPercentage(frequencyMax);
        return index;
    }


    public void sort(String section, Comparator<TermTuple> sortFunc) {

        TermTupleSet ts;
        for (TermTupleSetGroup g : docs) {
            ts = g.getByNameAux(section);
            ts.sortByIfNotAlreadySorted(sortFunc);
        }
    }

    /**
     * Apply the TM measure function.
     *
     * @param section
     * @param func
     * @return
     */
    public void computeTermMeasureIfNotAlready(String section, TermMeasureFunction func) {
        TermTupleSet ts;
        for (TermTupleSetGroup g : docs) {
            ts = g.getByNameAux(section);
            ts.computeTermMeasureIfNotAlready(func);
        }
    }

    /**
     * Get the vector, we sort by has, but we can optionally apply the term measure function if provided.
     *
     * @param section
     * @param func
     * @param normalize
     * @return
     */
    public SparseVector<TermTupleSet>[] getSparseVectors(String section, TermMeasureFunction func, boolean normalize) {
        setSortFunction(TermTupleSet.s_HashAscend);
        int size = docs.size();
        SparseVector<TermTupleSet> arr[] = new SparseVector[size];
        TermTupleSet ts;
        TermTupleSetGroup g;
        for (int i = 0; i < size; i++) {
            g = docs.get(i);
            ts = g.getByName(section, TermTupleSet.s_HashAscend, func, true);
            arr[i] = ts.getSparseVectorWithHash();
            if (normalize) {
                arr[i].calculateEuclidNormalize();
            }
        }
        return arr;
    }

    /**
     * Compute a set of sparse vectors for all the docs for a specific section, you can optionally provide a terms array
     * to fill the terms that were associated with the hash (so you can render back to meaningfull speak).
     *
     * @param section
     * @param hash
     * @param terms
     * @return
     */
    public List<SparseVector> getSPForTuples(String section, long hash[], String terms[]) {
        setSortFunction(TermTupleSet.s_HashAscend);
        TermTupleSet ts;
        List<SparseVector> spv = new ArrayList();
        for (TermTupleSetGroup g : docs) {
            ts = g.getByName(section);
            spv.add(ts.getSparseVector(hash, terms));
        }
        return spv;
    }


}
