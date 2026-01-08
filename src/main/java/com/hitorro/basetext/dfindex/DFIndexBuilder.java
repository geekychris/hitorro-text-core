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
package com.hitorro.basetext.dfindex;

import gnu.trove.map.hash.TLongIntHashMap;
import com.hitorro.basetext.inverter.TermTupleSet;
import com.hitorro.util.basefile.fs.BaseFile;

import java.io.File;
import java.io.IOException;

/**
 */
public class DFIndexBuilder {
    private DFIndex m_index = new DFIndex(0);
    private TLongIntHashMap m_currentSet = new TLongIntHashMap(10000000);
    private ApplyHashProcedure m_proc = new ApplyHashProcedure();

    public void setDescription(String desc, String query) {
        m_index.setDetails(desc, query);
    }

    public void addDocument(TermTupleSet... sets) {
        m_currentSet.clear();
        int wordCount = 0;
        for (TermTupleSet set : sets) {
            set.fillHitMap(m_currentSet);
            wordCount += set.getWordCount();
        }
        m_proc.set(m_index);
        m_currentSet.retainEntries(m_proc);
        m_index.incrementAccumulativeDocLength(wordCount);
        m_index.incrementDocFrequency();
    }

    public boolean save(File f) throws IOException {
        return m_index.save(f);
    }

    public boolean save(BaseFile f) throws IOException {
        return m_index.save(f);
    }
}
