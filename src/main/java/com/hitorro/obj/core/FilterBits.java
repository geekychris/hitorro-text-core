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
package com.hitorro.obj.core;

import com.hitorro.util.core.string.StringUtil;

import java.util.BitSet;

/**
 * <p/>
 * NamedBitsOfLong oriented mapping of the filters associated with a filter pipeline
 */

public class FilterBits {
    public static final String DefaultFilters = "STANDARD,CASE,PORTERSTEM";
    public static final String StandardFilters = "STANDARD,CASE";
    private BitSet m_set;

    public FilterBits() {
        initBitSet();
    }

    public FilterBits(String filtersByName[]) {
        init(filtersByName);
    }

    public FilterBits(String names, String seperator) {
        init(StringUtil.tokenizeFromSingleChar(names, seperator, true));
    }

    public void setByName(String name) {
        FilterEnum filter = FilterEnum.filterContext.getByShortName(name);
        if (filter != null) {
            m_set.set(filter.ordinal());
        } else {
        }
    }

    public void set(FilterEnum filter) {
        m_set.set(filter.ordinal());
    }

    public void clear(FilterEnum filter) {
        m_set.clear(filter.ordinal());
    }

    public boolean isSet(String name) {
        FilterEnum filter = FilterEnum.filterContext.getByShortName(name);
        if (filter != null) {
            return isSet(filter);
        } else {
            return false;
        }
    }

    public boolean isSet(FilterEnum filter) {
        return m_set.get(filter.ordinal());
    }

    /**
     * Dump the filters used by this mask
     */
    public String toString() {
        StringBuilder buff = new StringBuilder();
        for (FilterEnum filter : FilterEnum.values()) {
            if (isSet(filter)) {
                if (buff.length() > 0) {
                    buff.append(", ");
                }
                buff.append(filter.getName());
            }
        }

        return buff.toString();
    }

    /**
     * set the bit positions by
     *
     * @param names
     */
    private void init(String names[]) {
        initBitSet();
        for (String name : names) {
            setByName(name);
        }
    }

    private void initBitSet() {
        m_set = new BitSet(FilterEnum.filterContext.size());
    }
}