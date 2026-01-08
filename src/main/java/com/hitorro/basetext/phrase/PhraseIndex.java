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
package com.hitorro.basetext.phrase;


import gnu.trove.iterator.TLongIntIterator;
import gnu.trove.map.hash.TLongIntHashMap;
import com.hitorro.basetext.dfindex.BaseDFIndex;
import com.hitorro.util.basefile.fs.BaseFile;
import com.hitorro.util.core.Console;
import com.hitorro.util.core.hash.FPHash64;
import com.hitorro.util.core.iterator.AbstractIterator;
import com.hitorro.util.core.params.HTProperties;
import com.hitorro.util.core.string.StringUtil;
import com.hitorro.util.io.FileUtil;
import com.hitorro.util.io.resourcecache.basefile.BaseFileResourceCache;
import com.hitorro.util.io.resourcecache.basefile.BaseFileResourceContext;

import java.io.*;
import java.util.Date;
import java.util.Iterator;


public class PhraseIndex extends BaseDFIndex {
    public static final String DictTextFile = "phrases.dicttext";
    public static final String DictFile = "phrases.dict";

    public static final String ResourceName = "phraseindex";

    public static final long MajorVersion = 1;
    public static final long MinorVersion = 0;
    public static final long PatchVersion = 0;

    public static final String VersionQuery = "1.0+";

    public static final short Version = 1;
    public static final String FileName = "phraseindex.ind";

    private String m_description;
    private String m_creationQuery;
    private String m_creationDate = new Date().toString();
    private PrintWriter pw = null;
    private BaseFile dir;

    public PhraseIndex(int layer) {
        super(layer);
    }

    public static final boolean createIndexInResourceCache(BaseFile source, int minFreq,
                                                           int maxFreq, String desc,
                                                           String query, boolean writeTextDump) throws IOException {
        AbstractIterator<PhraseElement> iterIn = PhraseUtilBasic.bf2phraseelementiter.apply(source);
        AbstractIterator<PhraseElement> iter = iterIn.filter(new PhraseBetweenLogicalOperator(minFreq, maxFreq));
        boolean wrote = false;
        BaseFileResourceContext c = null;
        try {
            c = BaseFileResourceCache.getCache().getTempResourceContext(PhraseIndex.ResourceName,
                    PhraseIndex.MajorVersion,
                    PhraseIndex.MinorVersion,
                    PhraseIndex.PatchVersion);

            BaseFile dir = c.getPath();
            PhraseIndex index = new PhraseIndex(0);
            index.createIndex(iter, dir, writeTextDump, desc, query, minFreq, maxFreq);
            wrote = true;
        } finally {
            if (wrote == true) {
                c.commit();
                return true;
            } else {
                c.rollback();
            }

        }
        return false;


    }

    public void setDetails(String description, String queryString) {
        m_description = description;
        m_creationQuery = queryString;
    }

    public String getDescription() {
        return m_description;
    }

    public String getQueryString() {
        return m_creationQuery;
    }

    public String getCreationgDate() {
        return m_creationDate;
    }

    public boolean createIndex(Iterator<PhraseElement> iter,
                               BaseFile dir,
                               boolean textCopy,
                               String description,
                               String queryString,
                               int min, int max) throws IOException {
        BaseFile headerFile = dir.getChild("info.txt");
        StringBuilder sb = new StringBuilder();
        Console.bprintln(sb, "StartTime = %s", System.currentTimeMillis());

        this.setDetails(description, queryString);
        if (textCopy) {
            BaseFile dictTextFile = dir.getChild(DictTextFile);
            pw = dictTextFile.getPrintWriter();
        } else {
            pw = null;
        }

        this.dir = dir;
        int count = 0;
        while (iter.hasNext()) {
            count++;
            PhraseElement pe = iter.next();
            String phrase = pe.getPhrase();
            String parts[] = StringUtil.tokenizeFromSingleChar(phrase, " ");
            long fp = 0;
            for (String part : parts) {
                long curr = FPHash64.getFP(part);
                if (fp == 0) {
                    fp = curr;
                } else {
                    fp = FPHash64.combineFingerPrints(fp, curr);
                }
            }
            if (pw != null) {
                pw.print(phrase);
                pw.print(", ");
                pw.print(parts.length);
                pw.print(", ");
                pw.println(fp);
            }
            incrementFrequency(fp);

        }
        BaseFile dictFile = dir.getChild(DictFile);
        save(dictFile);
        HTProperties props = new HTProperties();
        props.put("rowcount", count);
        props.put("min", min);
        props.put("max", max);
        headerFile.writeProperties(props);
        if (pw != null) {
            pw.flush();
            pw.close();
        }
        return true;
    }

    public void incrementFrequency(long hash) {
        int f = m_map.get(hash);
        if (f == 0) {
            m_map.put(hash, 1);
        } else {
            m_map.increment(hash);
        }
    }

    /**
     * Save the df index to a file.
     *
     * @param f
     * @return
     * @throws java.io.IOException
     */
    public boolean save(BaseFile f) throws IOException {
        DataOutputStream dos = f.getDataOutputStream();
        save(dos);
        dos.close();
        return true;
    }

    /**
     * Save this df index to an output stream (could be over the wire).
     *
     * @param dos
     * @throws IOException
     */
    public void save(DataOutputStream dos) throws IOException {
        dos.writeShort(Version);
        dos.writeUTF(m_description);
        dos.writeUTF(m_creationQuery);
        dos.writeUTF(m_creationDate);
        dos.writeInt(m_map.size());
        for (TLongIntIterator it = m_map.iterator(); it.hasNext(); ) {
            it.advance();
            dos.writeLong(it.key());
            dos.writeInt(it.value());
        }
        dos.flush();
    }

    /**
     * @param f
     * @return
     * @throws IOException
     */
    public boolean read(File f) throws IOException {
        DataInputStream dis = FileUtil.getDataInputStreamForFile(f);
        if (read(dis)) {
            return false;
        }
        dis.close();
        return true;
    }

    /**
     * read the index from some data input stream, could be a network connection.
     *
     * @param dis
     * @return
     * @throws IOException
     */
    private boolean read(DataInputStream dis) throws IOException {
        short ver = dis.readShort();
        if (ver != Version) {
            return true;
        }
        this.m_description = dis.readUTF();
        this.m_creationQuery = dis.readUTF();
        this.m_creationDate = dis.readUTF();

        int size = dis.readInt();
        m_map = new TLongIntHashMap(size);
        long hash;
        int freq;
        for (int i = 0; i < size; i++) {
            hash = dis.readLong();
            freq = dis.readInt();
            m_map.put(hash, freq);
        }
        return false;
    }
}

