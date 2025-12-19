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

import com.hitorro.util.io.StoreException;
import com.hitorro.util.job.JobParameters;
import com.hitorro.util.typesystem.HTObjectInputStream;
import com.hitorro.util.typesystem.HTObjectOutputStream;
import com.hitorro.util.typesystem.annotation.TypeClassMetaInfo;
import com.hitorro.util.typesystem.annotation.UiProperties;
import com.hitorro.util.typesystem.annotation.UiTypeProperties;
import com.hitorro.util.typesystem.annotation.ViewClassReference;

import java.io.IOException;

/**
 * Copyright (c) 2003 - present HiTorro All rights reserved.
 * <p/>
 * User: chris
 */
@TypeClassMetaInfo(shortTypeName = "PhraseIndexJobParameters",
        isView = false,
        isPersisted = true,
        schemaVersion = 1)
@UiTypeProperties(name = "PhraseIndex Parameters", views =
        {@ViewClassReference(name = ViewClassReference.ListView, viewClass = PhraseIndexJobParameters.PhraseIndexJobParametersListView.class),
                @ViewClassReference(name = ViewClassReference.EditView, viewClass = PhraseIndexJobParameters.PhraseIndexJobParametersEditView.class)
        })
public class PhraseIndexJobParameters extends JobParameters {
    public static final int SerializationVersion = 1;
    private int minFrequency;
    private int maxFrequency;
    private String phraseDescription;
    private String generatingQuery;
    private boolean writeTextRepresentation;

    public void serialize(HTObjectOutputStream os) throws IOException, StoreException {
        os.writeInt(getSerializationVersion());
        super.serialize(os);
        os.writeInt(minFrequency);
        os.writeInt(maxFrequency);
        os.writeString(phraseDescription);
        os.writeString(generatingQuery);
        os.writeBoolean(writeTextRepresentation);
    }

    public void deserialize(HTObjectInputStream os) throws IOException, ClassNotFoundException, StoreException {
        int version = os.readInt();
        super.deserialize(os);
        switch (version) {
            case 1:
                minFrequency = os.readInt();
                maxFrequency = os.readInt();
                phraseDescription = os.readString();
                generatingQuery = os.readString();
                writeTextRepresentation = os.readBoolean();
        }
    }

    public String getJobName() {
        return PhraseIndexJob.Name;
    }

    public int getSerializationVersion() {
        return PhraseIndexJobParameters.SerializationVersion;
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

    @UiProperties(displayName = "Minimum phrase frequency", displayType = UiProperties.IntFieldDisplay, order = 50)
    public int getMinFrequency() {
        return minFrequency;
    }

    public void setMinFrequency(int minFrequency) {
        this.minFrequency = minFrequency;
    }


    @UiProperties(displayName = "Maximum phrase frequency", displayType = UiProperties.IntFieldDisplay, order = 60)
    public int getMaxFrequency() {
        return maxFrequency;
    }

    public void setMaxFrequency(int maxFrequency) {
        this.maxFrequency = maxFrequency;
    }

    @UiProperties(displayName = "Phrase Description", displayType = UiProperties.TextAreaDisplay, order = 30)

    public String getPhraseDescription() {
        return phraseDescription;
    }

    public void setPhraseDescription(String phraseDescription) {
        this.phraseDescription = phraseDescription;
    }

    @UiProperties(displayName = "Phrase query used in listFiles generation query", displayType = UiProperties.TextAreaDisplay, order = 40)

    public String getGeneratingQuery() {
        return generatingQuery;
    }


    public void setGeneratingQuery(String generatingQuery) {
        this.generatingQuery = generatingQuery;
    }

    public boolean isWriteTextRepresentation() {
        return writeTextRepresentation;
    }

    public void setWriteTextRepresentation(boolean writeTextRepresentation) {
        this.writeTextRepresentation = writeTextRepresentation;
    }


    /**
     * View class enumerating which fields to show when listing.
     */
    @TypeClassMetaInfo(shortTypeName = "PhraseIndexJobParametersListView",
            isView = true,
            isPersisted = false,
            schemaVersion = SerializationVersion)
    public abstract static class PhraseIndexJobParametersListView {

        public abstract String getPhraseDescription();

        public abstract String getGeneratingQuery();
    }

    /**
     * View class enumerating which fields to show when editing.
     */
    @TypeClassMetaInfo(shortTypeName = "PhraseIndexJobParametersEditView",
            isView = true,
            isPersisted = false,
            schemaVersion = SerializationVersion)
    public abstract static class PhraseIndexJobParametersEditView {

        public abstract String getPhraseDescription();

        public abstract String getGeneratingQuery();

        public abstract int getMinFrequency();

        public abstract int getMaxFrequency();
    }

}
