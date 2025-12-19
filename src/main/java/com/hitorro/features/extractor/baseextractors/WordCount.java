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

import com.fasterxml.jackson.databind.JsonNode;
import com.hitorro.basetext.inverter.TermTupleSet;
import com.hitorro.features.Feature;
import com.hitorro.features.FeatureValue;
import com.hitorro.features.extractor.ExtractionContext;
import com.hitorro.features.extractor.ExtractorDefinition;
import com.hitorro.features.extractor.FeatureExtractor;
import com.hitorro.features.extractor.Results;
import com.hitorro.util.json.keys.StringProperty;
import com.hitorro.util.typesystem.BaseFields;
import com.hitorro.util.typesystem.valuesource.ValueSourceForClass;

import java.util.HashMap;

/**
 *
 */
public class WordCount extends FeatureExtractor<ValueSourceForClass> {
    public static final StringProperty SectionKey = new StringProperty("extractor.field", "", Section.Body.getSection());

    public static final StringProperty ComputeKey = new StringProperty("compute", "", Compute.UniqueWords.getCompute());
    public static InverterResetableContext key = new InverterResetableContext(new String[]{"title", "body"}, "body");

    private Section section;
    private Compute compute;

    public boolean init(final JsonNode props, ExtractorDefinition ed, String featureName, Feature feature) {
        super.init(props, ed, featureName, feature);
        section = Section.getFilterByName(SectionKey.apply(props));
        compute = Compute.getFilterByName(ComputeKey.apply(props));
        return false;
    }

    public int extract(final ValueSourceForClass v, final Results results, final ExtractionContext ec) {
        InverterResetableContext context = (InverterResetableContext) ec.getLifetimeCacheValue(key);
        context.setDocument(v);
        BaseFields bf = ec.getBaseFields();
        TermTupleSet tts = context.getTupleSetFor(section.getSection());

        int val = compute.getComputed(tts);
        FeatureValue fv = FeatureValue.getFeature(feature, bf.getId(), bf.getCreated(), new Integer(val));
        results.add(this, fv);
        return 1;
    }

    public enum Section {
        Title("title"), Body("body"), All("all");

        private static HashMap<String, Section> s_byShortName;
        private String section;

        Section(String sec) {
            section = sec;
            setMapEntry(this);
        }

        public static Section getFilterByName(String name) {
            return s_byShortName.get(name.toLowerCase());
        }

        private static void setMapEntry(Section filter) {
            if (s_byShortName == null) {
                s_byShortName = new HashMap<String, Section>();
            }
            s_byShortName.put(filter.getSection(), filter);
        }

        public String getSection() {
            return section;
        }

    }

    public enum Compute {
        WordCount("wordcount") {
            public int getComputed(TermTupleSet tts) {
                return tts.getWordCount();
            }
        },
        UniqueWords("uniquewords") {
            public int getComputed(TermTupleSet tts) {
                return tts.getTuplesList().size();
            }
        };

        private static HashMap<String, Compute> s_byShortName;
        private String section;

        Compute(String sec) {
            section = sec;
            setMapEntry(this);
        }

        public static Compute getFilterByName(String name) {
            return s_byShortName.get(name.toLowerCase());
        }

        private static void setMapEntry(Compute filter) {
            if (s_byShortName == null) {
                s_byShortName = new HashMap<String, Compute>();
            }
            s_byShortName.put(filter.getCompute(), filter);
        }

        public abstract int getComputed(TermTupleSet tts);

        public String getCompute() {
            return section;
        }

    }
}
