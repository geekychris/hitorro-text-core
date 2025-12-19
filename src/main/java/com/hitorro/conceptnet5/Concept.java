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
package com.hitorro.conceptnet5;

import com.hitorro.conceptnet5.mappers.RelationMapper;
import com.hitorro.util.core.hash.FPHash64;
import com.hitorro.util.core.iterator.AbstractIterator;
import com.hitorro.util.core.string.Fmt;
import com.hitorro.util.core.string.StringUtil;
import com.hitorro.util.io.csv.CSVFormattedWriter;
import com.hitorro.util.io.largedata.CompressedStreamIO;
import com.hitorro.util.io.largedata.compressedstreams.CInputStream;
import com.hitorro.util.io.largedata.compressedstreams.COutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Concept implements CompressedStreamIO {
    protected String term;
    protected String lang;
    protected String pos;
    protected long id;
    private List<Relation> forwardAsserts = null;
    private List<Relation> backwardAsserts = null;

    public Concept() {

    }

    public Concept(String term, String lang, long id) {
        this.term = term;
        this.lang = lang;
        this.id = id;
    }

    public void initID() {
        id = FPHash64.getFP(term, lang, pos);
    }

    @Override
    public void write(final COutputStream os) throws IOException {
        os.writeString(term);
        os.writeString(lang);
        os.writeString(pos);
    }

    @Override
    public boolean read(final CInputStream is) throws IOException {
        term = is.readString();
        if (term == null) {
            return false;
        }
        lang = is.readString();
        pos = is.readString();
        return true;
    }


    public void setFromFileElement(String elem) {
        String parts[] = elem.split("/");
        if (parts.length == 5) {
            pos = parts[4];
        } else {
            pos = null;
        }
        setTerm(parts[3]);
        setLang(parts[2]);
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term.toLowerCase();
    }

    public String getLanguage() {
        return lang;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTermHash() {
        return FPHash64.getFP(term);
    }

    public long getTermLangHash() {
        return FPHash64.getFP(lang, term);
    }

    public long getTermLangPosHash() {
        return FPHash64.getFP(lang, term, pos);
    }

    public String toString() {
        return Fmt.S("Concept: %s, id: %s lang: %s", term, id, lang);
    }

    public List<Relation> getForwardAsserts(ConceptNet cn) throws Exception {
        if (forwardAsserts == null) {
            AbstractIterator<Relation> iter = RelationMapper.getIterator(cn.getConnection(), true, id);
            forwardAsserts = new ArrayList();
            iter.toCollection(forwardAsserts);
            iter.close();
        }
        return forwardAsserts;
    }

    public List<Relation> getbackwardAsserts(ConceptNet cn) throws Exception {
        if (backwardAsserts == null) {
            AbstractIterator<Relation> iter = RelationMapper.getIterator(cn.getConnection(), false, id);
            backwardAsserts = new ArrayList();
            iter.toCollection(backwardAsserts);
            iter.close();
        }
        return backwardAsserts;
    }

    @Override
    public boolean close(final COutputStream os) throws IOException {
        os.writeString(null);
        return true;
    }

    @Override
    public void writeCSVRow(final CSVFormattedWriter formatter) throws ArrayIndexOutOfBoundsException {

    }

    @Override
    public long getSize() {
        return StringUtil.size(term, lang, pos);
    }

    public static final class ConceptComparator implements Comparator<Concept> {
        static ConceptComparator c = new ConceptComparator();

        @Override
        public int compare(final Concept o1, final Concept o2) {
            int c = o1.lang.compareTo(o2.lang);

            if (c < 0) {
                return -1;
            }
            if (c > 0) {
                return 1;
            }

            c = o1.term.compareTo(o2.term);
            if (c < 0) {
                return -1;
            }
            if (c > 0) {
                return 1;
            }

            return 0;
        }
    }
}

