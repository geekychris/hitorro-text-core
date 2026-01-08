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

import gnu.trove.map.hash.TLongIntHashMap;
import com.hitorro.basetext.dfindex.DFIndex;
import com.hitorro.util.core.ListUtil;
import com.hitorro.util.core.math.SparseVector;
import com.hitorro.util.core.opers.HTPredicate;
import com.hitorro.util.io.StoreException;
import com.hitorro.util.typesystem.HTObjectInputStream;
import com.hitorro.util.typesystem.HTObjectOutputStream;
import com.hitorro.util.typesystem.HTSerializable;
import com.hitorro.util.typesystem.annotation.TypeClassMetaInfo;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

/**
 */
@TypeClassMetaInfo(shortTypeName = TypeClassMetaInfo.TermTupleSet,
        isView = false,
        isPersisted = false,
        schemaVersion = TermTuple.SerializableVersion)

public class TermTupleSet<E extends TermTuple> implements HTSerializable {
    public static final int SerializableVersion = 1;
    public static HashComparitor s_HashAscend = new HashComparitor();
    public static TermComparitor s_TermComparitor = new TermComparitor();
    public static MeasureDescendComparitor s_MeasureDescendComparitor = new MeasureDescendComparitor();
    public static TermIdComparator termIdComparator = new TermIdComparator();

    private int m_wordCount = 0;
    private String m_sectionName;

    private List<E> m_termTuples = new ArrayList<E>();


    private Comparator m_comparatorUsed = null;
    private TermMeasureFunction m_previousFunc = null;

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Visit all the terms and collect those that test the constraint, also returns a count of terms added.
     *
     * @param collection
     * @param constraint
     * @return
     */
    public int visit(TermVisitor collection, Predicate<E> constraint) {
        int counter = 0;
        for (E t : m_termTuples) {
            if (constraint.test(t)) {
                counter++;
                collection.add(t);
            }
        }
        return counter;
    }

    /**
     * See if any term matches the constraint.
     *
     * @param constraint
     * @return
     */
    public boolean match(HTPredicate<E> constraint) {
        for (E t : m_termTuples) {
            if (constraint.test(t)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return a sparse vector where the position is the hash and value is the
     *
     * @return
     */
    public SparseVector<TermTupleSet> getSparseVectorWithHash() {
        int size = m_termTuples.size();
        TermTuple tt;
        SparseVector<TermTupleSet> v = new SparseVector<TermTupleSet>(size, -1);
        v.setReferrer(this);
        for (int i = 0; i < size; i++) {
            tt = m_termTuples.get(i);
            v.setNextElement(tt.m_hash, tt.m_termMeasure);
        }
        return v;
    }

    public String getSectionName() {
        return m_sectionName;
    }

    public void setSectionName(String name) {
        m_sectionName = name;
    }


    /**
     * Prune the set to those terms that meet the min metric bar
     *
     * @param minMeasure
     * @param maxTerms
     */
    public void prune(double minMeasure, int maxTerms) {

        int startIndex = m_termTuples.size() - 1;
        for (int i = startIndex; i > 0; i--) {
            E e = m_termTuples.get(i);
            if (!e.isGood() || e.getMeasure() < minMeasure) {
                m_termTuples.remove(i);
            }
        }

        ListUtil.pruneListToLength(m_termTuples, maxTerms);

    }

    /**
     * Get the amount of words in this section of text.
     *
     * @return
     */
    public int getWordCount() {
        return m_wordCount;
    }

    public void setWordCount(int size) {
        m_wordCount = size;
    }

    public void add(E tt) {
        m_termTuples.add(tt);
    }

    public void sortByHashAscend() {
        sortByIfNotAlreadySorted(s_HashAscend);
    }

    public void sortByTermAscend() {
        sortByIfNotAlreadySorted(s_TermComparitor);
    }

    public void sortByMeasureDescend() {
        sortByIfNotAlreadySorted(s_MeasureDescendComparitor);
    }

    public void sortByID() {
        sortByIfNotAlreadySorted(termIdComparator);
    }


    public boolean sortByIfNotAlreadySorted(Comparator<TermTuple> sortFunc) {
        if (sortFunc == m_comparatorUsed) {
            return false;
        }
        sort(sortFunc);
        return true;
    }

    public void sort(Comparator<TermTuple> sortFunc) {

        m_comparatorUsed = sortFunc;
        Collections.sort(m_termTuples, sortFunc);
    }

    public List<E> getTuplesList() {
        return m_termTuples;
    }

    public boolean computeTermMeasureIfNotAlready(TermMeasureFunction func) {
        if (func == m_previousFunc) {
            return false;
        }
        computeTermMeasure(func);
        return true;
    }

    public void computeTermMeasure(TermMeasureFunction func) {
        m_previousFunc = func;
        for (TermTuple tt : m_termTuples) {
            func.compute(tt, this);
        }
    }

    /**
     * Merge two sets together making a new one.  The
     *
     * @param b
     * @param sectionName
     * @return
     */
    public TermTupleSet mergeSet(TermTupleSet b, String sectionName) {
        // ensure we are ordered by hash for merging purposes.
        b.sortByIfNotAlreadySorted(s_HashAscend);
        sortByIfNotAlreadySorted(s_HashAscend);
        TermTupleSet set = new TermTupleSet();
        set.setSectionName(sectionName);
        Iterator<E> iter = m_termTuples.iterator();
        Iterator<E> otherIter = b.getTuplesList().iterator();
        TermTuple tuple = null;
        if (iter.hasNext()) {
            tuple = iter.next();
        }
        TermTuple otherTuple = null;
        if (otherIter.hasNext()) {
            otherTuple = otherIter.next();
        }
        int wordCount = this.m_wordCount + b.m_wordCount;
        set.setWordCount(wordCount);
        while (otherTuple != null && tuple != null) {
            if (tuple.m_hash == otherTuple.m_hash) {
                TermTuple tt = new TermTuple();
                tt.set(tuple.m_term, tuple.tf + otherTuple.tf);
                tt.normalizeTF(wordCount);
                set.add(tt);
                otherTuple = next(otherIter);
                tuple = next(iter);

            } else if (tuple.m_hash < otherTuple.m_hash) {

                tuple = consumeToken(tuple, set, iter, wordCount);
            } else {

                otherTuple = consumeToken(otherTuple, set, otherIter, wordCount);
            }
        }
        while (otherTuple != null) {
            otherTuple = consumeToken(otherTuple, set, otherIter, wordCount);
        }

        while (tuple != null) {
            tuple = consumeToken(tuple, set, iter, wordCount);
        }

        // set the new aggregated word count for the two sections merged together.

        return set;
    }

    private TermTuple consumeToken(TermTuple tuple, TermTupleSet set, Iterator<E> iter, int wordCount) {
        TermTuple tt = new TermTuple();
        tt.set(tuple.m_term, tuple.tf);
        tt.normalizeTF(wordCount);
        set.add(tt);
        tuple = next(iter);
        return tuple;
    }

    public void visit(TermTupleSetVisitor visitor) {
        sortByIfNotAlreadySorted(s_HashAscend);
        Iterator<E> iter = m_termTuples.iterator();
        while (iter.hasNext()) {
            visitor.visit(iter.next(), null, TermTupleSetVisitor.Mode.LeftAvailable);
        }
    }

    /**
     * enumerate through the tuples within this listFiles and the peer set.  Any tuples that are in common call the visitor.
     *
     * @param visitor
     * @param setIn
     */
    public void matchByHash(TermTupleSetVisitor visitor, TermTupleSet<E> setIn) {
        setIn.sortByIfNotAlreadySorted(s_HashAscend);
        sortByIfNotAlreadySorted(s_HashAscend);
        List<E> l = setIn.getTuplesList();
        Iterator<E> iter = m_termTuples.iterator();
        Iterator<E> otherIter = l.iterator();
        TermTuple tuple = null;
        TermTuple otherTuple = null;
        if (iter.hasNext() && otherIter.hasNext()) {
            tuple = iter.next();
            otherTuple = otherIter.next();
        }
        while (otherTuple != null && tuple != null) {
            if (tuple.m_hash == otherTuple.m_hash) {
                visitor.visit(tuple, otherTuple, TermTupleSetVisitor.Mode.BothAvailable);

                otherTuple = next(otherIter);
                tuple = next(iter);
            } else if (tuple.m_hash < otherTuple.m_hash) {
                tuple = next(iter);
            } else {
                otherTuple = next(otherIter);
            }
        }
    }

    /**
     * assumes that the TTS was sorted by hash order so we can efficiently go through and
     *
     * @param hash
     * @return
     */
    public SparseVector<TermTupleSet> getSparseVector(long hash[], String terms[]) {
        /*
        to put, diff through the entries for matching hashes, count.
        construct sv from size.
        do same filling sv
        return
         */
        int count = 0;
        int size = m_termTuples.size();
        int hashSize = hash.length;
        if (size == 0) {
            // return the empty vector
            SparseVector<TermTupleSet> s = new SparseVector(0, hashSize);
            s.setReferrer(this);
        }

        int ind = 0;
        long termHash = m_termTuples.get(0).m_hash;
        for (int i = 0; i < size; i++) {
            termHash = m_termTuples.get(i).m_hash;
            if (hash[ind] < termHash) {
                // advance the hash array
                while (ind < hashSize && hash[ind] < termHash) {
                    ind++;
                }
            }
            if (hash[ind] == termHash) {
                count++;
            }
        }

        SparseVector<TermTupleSet> sv = new SparseVector(count, hashSize);
        sv.setReferrer(this);
        if (count == 0) {
            // we have no terms in common with the vector
            return sv;
        }

        // second pass to fill the vector
        ind = 0;

        count = 0;
        for (int i = 0; i < size; i++) {
            termHash = m_termTuples.get(i).m_hash;
            if (hash[ind] < termHash) {
                // advance the hash array
                while (ind < hashSize && hash[ind] < termHash) {
                    ind++;
                }
            }
            if (hash[ind] == termHash) {
                sv.setNextElement(ind, m_termTuples.get(i).m_termMeasure);
                if (terms != null) {
                    terms[ind] = m_termTuples.get(i).m_term;
                }
                count++;
            }
        }

        // now we have computed the length
        return sv;
    }

    /**
     * enumerate through the tuples within this listFiles and the peer set.  Any tuples that are in commen call the visitor.
     *
     * @param visitor
     * @param setIn
     */
    public void diffByHash(TermTupleSetVisitor visitor, TermTupleSet<E> setIn) {
        setIn.sortByIfNotAlreadySorted(s_HashAscend);
        sortByIfNotAlreadySorted(s_HashAscend);
        List<E> l = setIn.getTuplesList();
        Iterator<E> iter = m_termTuples.iterator();
        Iterator<E> otherIter = l.iterator();
        TermTuple tuple = iter.next();
        TermTuple otherTuple = otherIter.next();
        while (otherTuple != null && tuple != null) {
            if (tuple.m_hash == otherTuple.m_hash) {
                visitor.visit(tuple, otherTuple, TermTupleSetVisitor.Mode.BothAvailable);

                otherTuple = next(otherIter);
                tuple = next(iter);
            } else if (tuple.m_hash < otherTuple.m_hash) {
                visitor.visit(tuple, null, TermTupleSetVisitor.Mode.LeftAvailable);
                tuple = next(iter);
            } else {
                visitor.visit(null, otherTuple, TermTupleSetVisitor.Mode.RightAvailable);
                otherTuple = next(otherIter);
            }
        }
    }

    private TermTuple next(Iterator<E> iter) {
        if (iter.hasNext()) {
            return iter.next();
        }
        return null;
    }


    /**
     * Add this set to a df index.
     *
     * @param index
     */
    public void addDocumentToDF(DFIndex index) {
        for (TermTuple tt : m_termTuples) {
            index.incrementFrequency(tt.m_hash);
        }
        index.incrementAccumulativeDocLength(getWordCount());
        index.incrementDocFrequency();
    }

    /**
     * Fill in an identity apply of what terms are found in this section.  This hashmap will be past to each section found
     * in a document before being applied to the df index.
     *
     * @param currentSet
     */
    public void fillHitMap(TLongIntHashMap currentSet) {
        for (TermTuple tt : m_termTuples) {
            if (!currentSet.contains(tt.m_hash)) {
                currentSet.put(tt.m_hash, 1);
            }
        }
    }

    public void serialize(HTObjectOutputStream os) throws IOException, StoreException {
        os.writeInt(getSerializationVersion());
        os.writeString(m_sectionName);
        os.writeInt(m_wordCount);
        os.writeListOfHTSerializable(this.m_termTuples);
    }

    public void deserialize(HTObjectInputStream is) throws IOException, ClassNotFoundException, StoreException {
        int version = is.readInt();
        m_sectionName = is.readString();
        m_wordCount = is.readInt();
        is.readListOfHTSerializable(m_termTuples);
    }

    public int getSerializationVersion() {
        return SerializableVersion;
    }

    public boolean isPersisted() {
        return false;
    }

    public boolean hasGuid() {
        return false;
    }

    public boolean hasSoftGuid() {
        return false;
    }
}
