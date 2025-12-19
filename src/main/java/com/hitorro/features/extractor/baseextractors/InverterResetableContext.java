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
package com.hitorro.features.extractor.baseextractors;

import com.hitorro.basetext.inverter.TermTupleSet;
import com.hitorro.basetext.inverter.TermTupleSetGroup;
import com.hitorro.features.extractor.ExtractionContext;
import com.hitorro.features.extractor.ResetableContext;
import com.hitorro.language.Iso639Table;
import com.hitorro.language.IsoLanguage;
import com.hitorro.util.core.Log;
import com.hitorro.util.typesystem.TypeFieldIntf;
import com.hitorro.util.typesystem.TypeIntf;
import com.hitorro.util.typesystem.valuesource.ValueSourceForClass;

import java.io.IOException;

/**
 *
 */
public class InverterResetableContext implements ResetableContext<InverterResetableContext> {
    private TermTupleSetGroup group;
    private String fields[];
    private boolean valuesSet = false;
    private String indexField;

    public InverterResetableContext(String fields[], String indexField) {
        this.fields = fields;
        this.indexField = indexField;
        assignGroup();
    }

    private void assignGroup() {
        //new TFIDFTermMeasureFunction();
        group = new TermTupleSetGroup(indexField, null, TermTupleSet.s_HashAscend, null);
    }

    public InverterResetableContext getInstance(final ExtractionContext ec) {
        return new InverterResetableContext(fields, "body");
    }

    public void setDocument(ValueSourceForClass vf) {
        if (valuesSet) {
            return;
        }
        IsoLanguage language = getLanguage(vf);

        valuesSet = true;
        for (String field : fields) {
            Object o = vf.getValue(vf, field);
            String txt;
            if (o == null) {

                txt = "";
            } else {
                txt = o.toString();
            }

            try {
                group.add(field, txt, language);
            } catch (IOException e) {
                Log.extractors.error("Unable to put text to group %s", field);
            }

        }
    }

    private IsoLanguage getLanguage(final ValueSourceForClass vf) {
        TypeIntf type = vf.getType();
        TypeFieldIntf langField = type.getLanguageField();
        IsoLanguage language = null;
        if (langField != null) {
            Object l = vf.getValue(vf, langField.getName());
            if (l != null) {
                language = Iso639Table.getInstance().getRow(l.toString());
            }
        }
        if (language == null) {
            language = Iso639Table.english;
        }
        return language;
    }

    public TermTupleSet getTupleSetFor(String name) {
        return group.getByName(name);
    }

    public void reset() {
        valuesSet = false;
        group.clear();
    }

    public void deinit() {
        // gc cleanup should be sufficient.
    }

}
