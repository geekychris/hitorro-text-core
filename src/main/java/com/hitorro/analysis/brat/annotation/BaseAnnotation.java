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

import java.util.Map;
import java.util.function.Function;

public abstract class BaseAnnotation {

    public static Function<BaseAnnotation, String> annotationNameMapper = new Function<BaseAnnotation, String>() {
        @Override
        public String apply(BaseAnnotation elem) {
            return elem.name;
        }
    };
    protected String name;

    public static BaseAnnotation fromString(String s) {
        StringTokenizingIterator sti = new StringTokenizingIterator(s, "\t");
        BaseAnnotation ba = null;
        if (sti.hasNext()) {
            String first = sti.next();
            char c = first.charAt(0);

            switch (c) {
                case 'T':
                    ba = new TagAnnotation();
                    break;
                case 'E':
                    ba = new EventAnnotation();
                    break;
                case 'R':
                    ba = new RelationAnnotation();
                    break;
                case '#':
                    ba = new AnnotationAnnotation();
                    break;
                case '*':
                    ba = new AliasAnnotation();
                    break;
                default:
                    return null;
            }
            ba.name = first;
            ba.constructRemainder(sti);
        }
        return ba;
    }

    public abstract AnnoType getAnnoType();

    public String getName() {
        return name;
    }

    public void setName(String s) {
        this.name = s;
    }

    public abstract String toFileLine();

    public abstract boolean constructRemainder(StringTokenizingIterator sti);

    public abstract void fixupGraph(Map<String, BaseAnnotation> map);

    public abstract int getFirstPositionInBuffer();

    public enum AnnoType {Tag, Relation, Event, Comment, Alias}
}
