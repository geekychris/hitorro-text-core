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

import com.hitorro.util.core.string.Fmt;
import com.hitorro.util.io.csv.CSVFormattedWriter;
import com.hitorro.util.io.largedata.CompressedStreamIO;
import com.hitorro.util.io.largedata.compressedstreams.CInputStream;
import com.hitorro.util.io.largedata.compressedstreams.COutputStream;

import java.io.IOException;

public class Relation implements CompressedStreamIO {
    public long concept1Id;
    public long concept2Id;
    public long relationId;

    public Relation(long concept1Id, long concept2Id, long relationId) {
        this.concept1Id = concept1Id;
        this.concept2Id = concept2Id;
        this.relationId = relationId;
    }

    public String toString() {
        return Fmt.S("relation: c1: %s c2: %s relationId: %s", concept1Id, concept2Id, relationId);
    }

    public RelationType getRelationType(ConceptNet cn) {
        return cn.getRelationType(relationId);
    }

    @Override
    public void write(final COutputStream os) throws IOException {
        os.writeVLong(concept1Id);
        os.writeVLong(concept2Id);
        os.writeVLong(relationId);
    }

    @Override
    public boolean read(final CInputStream is) throws IOException {
        concept1Id = is.readVLong();
        if (concept1Id == 0) {
            return false;
        }

        concept2Id = is.readVLong();

        relationId = is.readVLong();
        return false;
    }

    @Override
    public boolean close(final COutputStream os) throws IOException {
        os.writeVLong(0);
        return true;
    }

    @Override
    public void writeCSVRow(final CSVFormattedWriter formatter) throws ArrayIndexOutOfBoundsException {

    }

    @Override
    public long getSize() {
        return 0;
    }
}
