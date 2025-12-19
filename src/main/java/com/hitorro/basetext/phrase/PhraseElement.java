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

import com.hitorro.util.core.ArrayUtil;
import com.hitorro.util.core.string.StringUtil;
import com.hitorro.util.typesystem.HTObjectInputStream;
import com.hitorro.util.typesystem.HTObjectOutputStream;
import com.hitorro.util.typesystem.HTSerializable;
import com.hitorro.util.typesystem.annotation.TypeClassMetaInfo;

import java.io.IOException;

/**
 * Copyright (c) 2003 - present HiTorro All rights reserved.
 * <p/>
 * User: chris
 */
@TypeClassMetaInfo(shortTypeName = TypeClassMetaInfo.PhraseElement,
        isView = false,
        isPersisted = false,
        schemaVersion = PhraseElement.SerializationVersion)
public class PhraseElement implements HTSerializable, Comparable<PhraseElement> {
    public static final int SerializationVersion = 1;
    protected String phrase;
    protected int hashcode;
    protected int frequency;

    protected String phraseTerms[] = null;

    public int hashCode() {
        return hashcode;
    }

    public String toString() {
        return phrase;
    }

    public String[] getTerms() {
        if (phraseTerms == null) {
            phraseTerms = StringUtil.tokenizeFromSingleChar(phrase, " ");
        }
        return phraseTerms;
    }

    public boolean equals(Object o) {
        return phrase.equals(((PhraseElement) o).phrase);
    }

    public void serialize(HTObjectOutputStream os) throws IOException {
        os.writeInt(getSerializationVersion());
        os.writeString(phrase);
        os.writeInt(frequency);
    }

    public void deserialize(HTObjectInputStream os) throws IOException, ClassNotFoundException {
        int version = os.readInt();
        switch (version) {
            case 1:
                phrase = os.readString();
                frequency = os.readInt();
        }
        hashcode = phrase.hashCode();
    }

    public int getSerializationVersion() {
        return SerializationVersion;
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

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
        hashcode = phrase.hashCode();
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int compareTo(PhraseElement phraseElement) {
        if (phraseElement.phrase == null) {
            return -1;
        }
        return phraseElement.phrase.compareTo(phrase) * -1;
    }

    /**
     * Determine if the supplied element is a child of the current element.
     *
     * @param peIn
     * @return
     */
    public boolean isChild(PhraseElement peIn) {
        String parts[] = this.getTerms();
        int common = ArrayUtil.compareArrays(parts, peIn.getTerms());
        return (common == parts.length);
    }

    /**
     * Determine if the provided element is an immediate child.  That is:
     * <p>
     * "a b".isImmediateChild("a b c") == true
     * <p>
     * "a b".isImmediateChild("a b c e") == false
     *
     * @param peIn
     * @return
     */
    public boolean isImmediateChild(PhraseElement peIn) {
        return getTerms().length + 1 == peIn.getTerms().length;
    }
}
