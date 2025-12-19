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
import com.hitorro.analysis.brat.annotation.TagAnnotation;
import com.hitorro.util.core.string.StringRange;

import java.util.function.Function;

public class AddSpanToAnnotation implements Function<BaseAnnotation, BaseAnnotation> {
    StringRange root;

    public AddSpanToAnnotation(StringRange root) {
        this.root = root;
    }

    //@Override
    public BaseAnnotation applys(BaseAnnotation elem) {
        int start = ((TagAnnotation) elem).getStart();
        ((TagAnnotation) elem).setSentenceSpan(root.getSpanContaining(start));
        return elem;
    }

    public BaseAnnotation apply(BaseAnnotation elem) {
        TagAnnotation ta = (TagAnnotation) elem;
        int start = ta.getStart();
        StringRange span = root.getSpanContaining(start);
        ta.setSentenceSpan(span);
        StringRange tagSpan = new StringRange(root.getBuffer(), ta.getStart(), ta.getEnd());
        StringRangePayload sp = new StringRangePayload();
        sp.sentenceAnnotation.add(ta);
        tagSpan.setPayload(sp);
        appendChild(span, tagSpan);

        return elem;
    }

    private void appendChild(StringRange span, StringRange tagSpan) {
        StringRange children[] = span.getChildren();
        StringRange newChild[];
        if (children == null) {
            newChild = new StringRange[1];
        } else {
            newChild = new StringRange[children.length + 1];
            System.arraycopy(children, 0, newChild, 0, children.length);
        }
        newChild[newChild.length - 1] = tagSpan;
        span.setChildren(newChild);
    }


}