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

import com.hitorro.util.core.hash.FPHash64;
import com.hitorro.util.core.string.Fmt;
import com.hitorro.util.typesystem.HTObjectInputStream;
import com.hitorro.util.typesystem.HTObjectOutputStream;
import com.hitorro.util.typesystem.HTSerializable;
import com.hitorro.util.typesystem.annotation.TypeClassMetaInfo;

import java.io.IOException;

/**
 */
@TypeClassMetaInfo(shortTypeName = TypeClassMetaInfo.TermTuple,
        isView = false,
        isPersisted = false,
        schemaVersion = TermTuple.SerializableVersion)
public class TermTuple implements HTSerializable {
    public static final double TFMultFactor = 1000.0;
    public static final double DFMultiFactor = 1000.0;
    public static final int SerializableVersion = 1;
    public long m_hash;
    public int tf;
    public int id;
    String m_term;
    double m_normTf;
    // Measure could be tfIdf or some other measure of relative worth.
    boolean m_isTermMeasure = false;
    double m_termMeasure;
    // token frequency in the corpus.
    double m_df;
    double m_normDf;
    boolean m_isGoodValue = true;

    public String toString() {
        return Fmt.S("t: %s isGood: %s, measure:%s", m_term, m_isGoodValue, m_termMeasure);
    }

    public double getNormalizedDF() {
        return m_normDf;
    }

    public double getMeasure() {
        return m_termMeasure;
    }

    public boolean isGood() {
        return m_isGoodValue && m_termMeasure != Double.NEGATIVE_INFINITY;
    }

    public String getTerm() {
        return m_term;
    }

    public void normalizeTF(int documentSize) {
        m_normTf = ((double) tf / documentSize) * TFMultFactor;
    }

    public double computeNormalizedDF(int dfCount, double corpusDocSize) {
        m_normDf = ((double) dfCount / corpusDocSize) * DFMultiFactor;
        return m_normDf;
    }


    public double getNormalizedTF() {
        return m_normTf;
    }

    public void setTermMeasure(double termMeasure) {
        m_isTermMeasure = true;
        m_termMeasure = termMeasure;
    }

    public void setDF(double df) {
        m_df = df;
    }

    public void set(String term, int tf) {
        m_term = term;
        m_hash = FPHash64.getFP(term);
        this.tf = tf;
    }

    public void serialize(HTObjectOutputStream os) throws IOException {
        os.writeInt(getSerializationVersion());
        os.writeString(m_term);
        os.writeLong(m_hash);
        os.writeInt(tf);
        os.writeDouble(m_normTf);
        os.writeBoolean(m_isTermMeasure);
        if (m_isTermMeasure) {
            os.writeBoolean(m_isGoodValue);
            os.writeDouble(m_termMeasure);
            os.writeDouble(m_df);
            os.writeDouble(m_normDf);
        }
    }

    public void deserialize(HTObjectInputStream is)
            throws IOException, ClassNotFoundException {
        int version = is.readInt();
        m_term = is.readString();
        m_hash = is.readLong();
        tf = is.readInt();
        m_normTf = is.readDouble();
        m_isTermMeasure = is.readBoolean();
        if (m_isTermMeasure) {
            m_isGoodValue = is.readBoolean();
            m_termMeasure = is.readDouble();
            m_df = is.readDouble();
            m_normDf = is.readDouble();
        }
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
