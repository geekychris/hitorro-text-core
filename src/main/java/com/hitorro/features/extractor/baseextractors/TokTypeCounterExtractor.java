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
public class TokTypeCounterExtractor extends FeatureExtractor<ValueSourceForClass> {
    public static final StringProperty SectionKey = new StringProperty("compute", "", null);
    public static TokenTypeCounterContext key = new TokenTypeCounterContext(new String[]{"body"}, "body");
    private Type type;

    public boolean init(final JsonNode props, ExtractorDefinition ed, String featureName, Feature feature) {
        super.init(props, ed, featureName, feature);
        type = Type.getFilterByName(SectionKey.apply(props));
        return false;
    }

    public int extract(final ValueSourceForClass v, final Results results, final ExtractionContext ec) {
        TokenTypeCounterContext context = (TokenTypeCounterContext) ec.getLifetimeCacheValue(key);
        context.setDocument(v);
        BaseFields bf = ec.getBaseFields();

        int val = 0;
        switch (type) {
            case Word:
                val = context.getWordCount();
                break;
            case Hash:
                val = context.getHashTagCount();
                break;
            case Response:
                val = context.getResponseCount();
                break;
            case Uri:
                val = context.getUriCount();
                break;
            case Ref:
                val = context.getRefCount();
                break;
        }
        FeatureValue fv = FeatureValue.getFeature(feature, bf.getId(), bf.getCreated(), new Integer(val));
        results.add(this, fv);
        return 1;
    }

    public enum Type {
        Word("word"), Hash("hash"), Response("response"), Uri("uri"), Ref("ref");

        private static HashMap<String, Type> s_byShortName;
        private String section;

        Type(String sec) {
            section = sec;
            setMapEntry(this);
        }

        public static Type getFilterByName(String name) {
            return s_byShortName.get(name.toLowerCase());
        }

        private static void setMapEntry(Type filter) {
            if (s_byShortName == null) {
                s_byShortName = new HashMap<String, Type>();
            }
            s_byShortName.put(filter.getSection(), filter);
        }

        public String getSection() {
            return section;
        }

    }
}
