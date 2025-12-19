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
package com.hitorro.analysis.brat.annotation;

import com.hitorro.util.core.iterator.StringTokenizingIterator;
import com.hitorro.util.core.iterator.mappers.BaseFunction;
import com.hitorro.util.core.iterator.mappers.String2Integer;
import com.hitorro.util.core.string.StringRange;

import java.util.Map;


public class TagAnnotation extends BaseAnnotation {

    public static BaseFunction<BaseAnnotation, String> mapTagAnnotationText = new BaseFunction<BaseAnnotation, String>() {
        @Override
        public String apply(BaseAnnotation elem) {

            return ((TagAnnotation) elem).extractedText;
        }
    };
    protected String tagType;
    protected int start;
    protected int end;
    protected String extractedText;
    protected StringRange sentence;

    public TagAnnotation() {

    }

    public BaseAnnotation.AnnoType getAnnoType() {
        return AnnoType.Tag;
    }

    @Override
    public void fixupGraph(Map<String, BaseAnnotation> map) {
    }

    @Override
    public int getFirstPositionInBuffer() {
        return start;
    }

    @Override
    public String toFileLine() {
        return String.format("%s\t%s %d %d\t%s", name, tagType, start, end, extractedText);
    }

    public StringRange getSentenceSpan() {
        return this.sentence;
    }

    public void setSentenceSpan(StringRange sentence) {
        this.sentence = sentence;
    }

    public String toString() {
        return String.format("TAG name: {%s} type: {%s} start: {%d} end: {%d}", name, tagType, start, end);
    }

    public String getExtractedText() {
        return extractedText;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int i) {
        start = i;
    }

    public void setText(String s) {
        extractedText = s;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int i) {
        end = i;
    }

    public String getTagType() {
        return tagType;
    }

    public void setTagType(String s) {
        this.tagType = s;
    }

    public boolean constructRemainder(StringTokenizingIterator sti) {
        extractLabelIndexParts(sti.getIfHasNext());
        extractedText = sti.getIfHasNext();
        return true;
    }

    private void extractLabelIndexParts(String nameLocation) {
        StringTokenizingIterator nameParts = new StringTokenizingIterator(nameLocation, " ");
        tagType = nameParts.getIfHasNext("");

        start = nameParts.getIfHasNextMap(String2Integer.me, "0");
        end = nameParts.getIfHasNextMap(String2Integer.me, "0");
    }
}
