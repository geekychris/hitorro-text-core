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

import gnu.trove.iterator.TLongIntIterator;
import gnu.trove.map.hash.TLongIntHashMap;
import com.hitorro.util.basefile.fs.BaseFile;
import com.hitorro.util.core.hash.FPHash64;
import com.hitorro.util.io.FileUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

/**
 * of terms - > frequency
 * <p/>
 * The df corpus has a defined doc Frequency (how many documents). When asking for the term frequency, we will cutoff
 * frequencies
 */
public class DFIndex extends BaseDFIndex {
    public static final String ResourceName = "dfindex";
    public static final long MajorVersion = 1;
    public static final long MinorVersion = 0;
    public static final long PatchVersion = 0;

    public static final String VersionQuery = "1.0+";

    public static final short Version = 1;
    public static final String FileName = "dfindex.ind";


    private int m_docFrequency = 0;

    private int m_freqMax = 0;

    private long m_accumDocLength = 0;

    private String m_description;
    private String m_creationQuery;
    private String m_creationDate = new Date().toString();

    public DFIndex(int layer) {
        super(layer);
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

    public String getCreationDate() {
        return m_creationDate;
    }

    public void incrementAccumulativeDocLength(int length) {
        m_accumDocLength += length;
    }

    public int getFrequencyMaxAbsolute() {
        return m_freqMax;
    }

    public void setFrequencyMaxAbsolute(int max) {
        m_freqMax = max;
    }

    /**
     * Calculate the average document length that is required by such things as Ocapi.
     *
     * @return
     */
    public double getAverageDocLength() {
        double l = m_accumDocLength;
        double f = m_docFrequency;
        return l / f;
    }

    /**
     * How many documents exist in the corpus that was used to generate this index.
     *
     * @return
     */
    public int getDocFrequency() {
        return m_docFrequency;
    }

    public void setDocFrequency(int freq) {
        m_docFrequency = freq;
    }

    public void incrementDocFrequency() {
        m_docFrequency++;
    }

    /**
     * given a document frequency of 1M, we may want a cutoff of 30%, that is we do not want to consider terms that
     * occur in more than 30% of the documents because they are considered to common that they are useless.
     * <p/>
     * To state a percentage of 30% max, pass 30.0
     *
     * @param perc
     */
    public void setFrequencyMaxByPercentage(double perc) {
        double d = m_docFrequency;
        m_freqMax = (int) (d * (perc / 100));
    }

    public void incrementFrequency(long hash) {
        int f = m_map.get(hash);
        if (f == 0) {
            m_map.put(hash, 1);
        } else {
            m_map.increment(hash);
        }
    }

    public long[] getHashArray(int minDF, int countKnown, boolean sort) {
        int count;
        if (countKnown >= 0) {
            count = countKnown;
        } else {
            count = getCountAboveDF(minDF);
        }

        long arr[] = new long[count];
        int i = 0;
        int val;
        for (TLongIntIterator it = m_map.iterator(); it.hasNext(); ) {
            it.advance();
            val = it.value();
            if (val >= minDF) {
                if (i <= count) {
                    arr[i++] = it.key();
                } else {
                    break;
                }
            }
        }
        if (sort) {
            Arrays.sort(arr);
        }
        return arr;

    }

    private int getCountAboveDF(final int minDF) {
        int count = 0;
        for (TLongIntIterator it = m_map.iterator(); it.hasNext(); ) {
            it.advance();
            if (it.value() >= minDF) {
                count++;
            }
        }
        return count;
    }

    /**
     * Save the df index to a file.
     *
     * @param f
     * @return
     * @throws IOException
     */
    public boolean save(File f) throws IOException {
        DataOutputStream dos = FileUtil.getDataOutputStreamForFile(f);
        save(dos);
        dos.close();
        return true;
    }

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
        dos.writeInt(m_docFrequency);
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
        m_docFrequency = dis.readInt();
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

    public int getFrequency(String token) {
        return getFrequency(FPHash64.getFP(token));
    }

    public int getFrequency(long hash) {
        int f = m_map.get(hash);
        if (f > this.m_freqMax) {
            // hit max
            return -1;
        }
        return f;
    }

    public int getFrequencyRaw(String token) {
        return getFrequency(FPHash64.getFP(token));
    }

    public int getFrequencyRaw(long hash) {
        int f = m_map.get(hash);

        return f;
    }
}
