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
import com.hitorro.basetext.dfindex.DFIndexSingletonMapper;
import com.hitorro.language.IsoLanguage;
import com.hitorro.util.io.StoreException;
import com.hitorro.util.typesystem.HTObjectInputStream;
import com.hitorro.util.typesystem.HTObjectOutputStream;
import com.hitorro.util.typesystem.HTSerializable;
import com.hitorro.util.typesystem.annotation.TypeClassMetaInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * <p/>
 * Group of tuple sets that represent a document
 */
@TypeClassMetaInfo(shortTypeName = TypeClassMetaInfo.TermTupleGroup,
        isView = false,
        isPersisted = false,
        schemaVersion = TermTupleSetGroup.SerializableVersion)
public class TermTupleSetGroup implements HTSerializable {
    public static final int SerializableVersion = 1;

    private String m_id;
    private List<TermTupleSet> m_sets = new ArrayList<TermTupleSet>();

    private Comparator<TermTuple> defaultSort;
    private TermMeasureFunction defaultFunc;
    private DocumentInverter documentInverter;

    public TermTupleSetGroup(String field, String filters, Comparator<TermTuple> sortFunction, TermMeasureFunction func) {
        defaultSort = sortFunction;
        defaultFunc = func;
        documentInverter = new DocumentInverter(field, filters, func, sortFunction);
    }

    public void setSortFunction(Comparator<TermTuple> sortFunc) {
        defaultSort = sortFunc;
    }

    public String getId() {
        return m_id;
    }

    public void setId(String id) {
        m_id = id;

        for (TermTupleSet set : m_sets) {
            set.setId(id);
        }
    }


    public void setGroup(String text[], String sectionName[], IsoLanguage language) throws IOException {

        for (int i = 0; i < text.length; i++) {
            String t = text[i];
            String name = sectionName[i];
            add(name, t, language);
        }
    }

    public void add(String section, String text, IsoLanguage language) throws IOException {
        this.addTupleSet(documentInverter.setText(section, text, language));
    }

    /**
     * @param sectionA
     * @param sectionB
     * @param targetSectionName
     * @return
     */
    public boolean merge(String sectionA, String sectionB, String targetSectionName) {
        TermTupleSet a = getByNameAux(sectionA);
        TermTupleSet b = getByNameAux(sectionB);
        if (a == null || b == null) {
            return false;
        }
        addTupleSet(a.mergeSet(b, targetSectionName));
        return true;
    }

    public void clear() {
        m_sets.clear();
    }

    public void addTupleSet(TermTupleSet set) {
        m_sets.add(set);
    }

    /**
     * Get the set by name and computeFromSubjectArea the measure and sort if required.
     *
     * @param setName
     * @param sortFunction
     * @param func
     * @return
     */
    public TermTupleSet getByName(String setName,
                                  Comparator<TermTuple> sortFunction,
                                  TermMeasureFunction func,
                                  boolean alwaysRecompute) {
        return getByName(setName, sortFunction, func, alwaysRecompute, null);
    }

    public TermTupleSet getByName(String setName,
                                  Comparator<TermTuple> sortFunction,
                                  TermMeasureFunction func,
                                  boolean alwaysRecompute,
                                  DFIndex dfIndex) {
        TermTupleSet set = getByNameAux(setName);
        if (set == null) {
            return null;
        }
        if (func != null) {
            if (alwaysRecompute) {
                set.computeTermMeasure(func);
            } else {
                if (dfIndex == null && func.getNeedsDFIndex()) {
                    // no point loading it unless the function really wants it
                    dfIndex = DFIndexSingletonMapper.getSingleton().get();
                }
                func.setDFIndex(dfIndex);
                set.computeTermMeasureIfNotAlready(func);
            }
        }

        if (sortFunction != null) {
            if (alwaysRecompute) {
                set.sort(sortFunction);
            } else {
                set.sortByIfNotAlreadySorted(sortFunction);
            }
        }
        return set;
    }

    /**
     * Get the set by name,  if the set needs to have its measure computation carried out and sorted it will
     *
     * @param setName
     * @return
     */
    public TermTupleSet getByName(String setName) {
        return getByName(setName, defaultSort, defaultFunc, false);
    }

    public TermTupleSet getByName(String setName, DFIndex index) {
        return getByName(setName, defaultSort, defaultFunc, false, index);
    }

    protected TermTupleSet getByNameAux(String setName) {
        for (TermTupleSet s : m_sets) {
            if (setName.equals(s.getSectionName())) {
                return s;
            }
        }
        return null;
    }

    public void serialize(HTObjectOutputStream os) throws IOException, StoreException {
        os.writeInt(getSerializationVersion());
        os.writeString(m_id);
        os.writeListOfHTSerializable(m_sets);
    }

    public void deserialize(HTObjectInputStream os) throws IOException, ClassNotFoundException, StoreException {
        int version = os.readInt();
        m_id = os.readString();
        os.readListOfHTSerializable(m_sets);
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
