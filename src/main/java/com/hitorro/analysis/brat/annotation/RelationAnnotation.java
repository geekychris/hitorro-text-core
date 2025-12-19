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
import com.hitorro.util.core.string.StringUtil;

import java.util.Map;

public class RelationAnnotation extends BaseAnnotation {

    private String relationType;
    private String from;
    private BaseAnnotation fromAnno;
    private String to;
    private BaseAnnotation toAnno;


    public BaseAnnotation.AnnoType getAnnoType() {
        return AnnoType.Relation;
    }


    @Override
    public int getFirstPositionInBuffer() {
        return fromAnno.getFirstPositionInBuffer();
    }

    @Override
    public void fixupGraph(Map<String, BaseAnnotation> map) {
        fromAnno = map.get(from);
        toAnno = map.get(to);
    }

    public BaseAnnotation getFromAnno() {
        return fromAnno;
    }

    public BaseAnnotation getToAnno() {
        return toAnno;
    }

    public String toString() {
        return String.format("REL name: {%s} type: {%s} from: {%s} to {%s}", name, relationType, from, to);
    }

    @Override
    public String toFileLine() {
        //R2      Family Arg1:T13 Arg2:T14
        return String.format("%s\t%s Arg1:%s Arg2:%s\t", name, relationType, from, to);
    }

    public String getRelationType() {
        return relationType;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }


    @Override
    public boolean constructRemainder(StringTokenizingIterator sti) {
        extractRel(sti.getIfHasNext(""));
        return true;
    }

    private void extractRel(String text) {
        StringTokenizingIterator nameParts = new StringTokenizingIterator(text, " ");
        relationType = nameParts.getIfHasNext("");
        from = StringUtil.after(nameParts.getIfHasNext(""), ':');
        to = StringUtil.after(nameParts.getIfHasNext(""), ':');
    }
}
