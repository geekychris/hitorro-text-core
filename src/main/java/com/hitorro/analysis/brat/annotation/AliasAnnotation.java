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

public class AliasAnnotation extends BaseAnnotation {
    private String rest;
    private List<Alias> list;

    @Override
    public BaseAnnotation.AnnoType getAnnoType() {
        return AnnoType.Alias;
    }

    public List<Alias> getAliases() {
        return list;
    }

    @Override
    public String toFileLine() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append("\t");
        sb.append("Alias ");
        int counter = 0;
        for (Alias a : list) {
            if (counter > 0) {
                sb.append(" ");
            }
            counter++;
            sb.append(a.name);
        }
        return sb.toString();
    }


    public String toString() {
        return String.format("ALIAS: %s", toFileLine());
    }

    @Override
    public boolean constructRemainder(StringTokenizingIterator sti) {
        rest = sti.getRemainder();
        StringTokenizingIterator sti2 = new StringTokenizingIterator(rest, " ");
        list = (List<Alias>) sti2.skipNTakeM(1, -1, true).
                map(Alias.aliasMapper).
                reduce(ListReducer.me);
        return true;
    }

    @Override
    public void fixupGraph(Map<String, BaseAnnotation> map) {
        for (Alias alias : list) {
            alias.anno = map.get(alias.name);
        }
    }

    @Override
    public int getFirstPositionInBuffer() {
        return -1;
    }

}

