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
package com.hitorro.analysis.brat.mappers;

import com.hitorro.analysis.brat.StringRangePayload;
import com.hitorro.analysis.brat.annotation.BaseAnnotation;
import com.hitorro.util.core.string.StringRange;

import java.util.Map;
import java.util.function.Function;

public class FixupReferences implements Function<BaseAnnotation, BaseAnnotation> {
    private Map<String, BaseAnnotation> map;
    private StringRange<StringRangePayload> buffer;

    public FixupReferences(Map<String, BaseAnnotation> map, StringRange<StringRangePayload> buffer) {
        this.map = map;
        this.buffer = buffer;
    }

    @Override
    public BaseAnnotation apply(BaseAnnotation elem) {
        elem.fixupGraph(map);
        addAnnotationToSentence(elem);
        return elem;
    }

    private void addAnnotationToSentence(BaseAnnotation elem) {
        int offset = elem.getFirstPositionInBuffer();
        if (offset != -1) {
            StringRange<StringRangePayload> sent = buffer.getSpanContaining(offset);
            if (sent != null) {
                StringRangePayload sp = sent.getPayload();
                if (sp == null) {
                    sp = new StringRangePayload();
                    sent.setPayload(sp);
                }
                sp.sentenceAnnotation.add(elem);
            }
        }
    }
}