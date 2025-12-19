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
import com.hitorro.util.core.iterator.reducers.ListReducer;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class EventAnnotation extends BaseAnnotation {

    public static Function<String, EventElem> eventMapper = new Function<String, EventElem>() {
        @Override
        public EventElem apply(String elem) {
            String parts[] = elem.split(":");
            if (parts.length == 2) {
                return new EventElem(parts[0], parts[1]);
            }
            return null;
        }
    };
    private List<EventElem> list;

    public BaseAnnotation.AnnoType getAnnoType() {
        return AnnoType.Event;
    }

    @Override
    public void fixupGraph(Map<String, BaseAnnotation> map) {
        for (EventElem elem : list) {
            elem.setAnno(map.get(elem.tag));
        }
    }

    public String toString() {
        return String.format("EVENT: ", toFileLine());
    }

    @Override
    public String toFileLine() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append("\t");
        int count = 0;
        for (EventElem elem : list) {
            if (count > 0) {
                sb.append(" ");
            }
            sb.append(elem.toString());
        }
        return sb.toString();
    }

    @Override
    public int getFirstPositionInBuffer() {
        if (list.size() > 0) {
            list.get(0).anno.getFirstPositionInBuffer();
        }
        return -1;
    }

    public List<EventElem> getElems() {
        return list;
    }

    @Override
    public boolean constructRemainder(StringTokenizingIterator sti) {
        String rem = sti.getRemainder();
        list = (List<EventElem>) new StringTokenizingIterator(rem, " ").map(eventMapper).reduce(ListReducer.me);

        return false;
    }
}
