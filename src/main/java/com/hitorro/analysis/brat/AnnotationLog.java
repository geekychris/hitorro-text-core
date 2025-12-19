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
package com.hitorro.analysis.brat;

import com.hitorro.analysis.brat.annotation.BaseAnnotation;
import com.hitorro.analysis.brat.mappers.Anno2Line;
import com.hitorro.util.basefile.fs.BaseFile;
import com.hitorro.util.basefile.tools.BaseFileUtil;
import com.hitorro.util.core.iterator.AbstractIterator;
import com.hitorro.util.core.iterator.BaseReducer;
import com.hitorro.util.core.iterator.CollectionIterator;
import com.hitorro.util.core.iterator.reducers.ListReducer;
import com.hitorro.util.core.iterator.reducers.ToPrintWriter;

import java.util.ArrayList;

public class AnnotationLog implements BaseReducer<BaseAnnotation, AnnotationLog> {
    private ArrayList<BaseAnnotation> annos = new ArrayList<>();

    @Override
    public AnnotationLog reduce(AbstractIterator<BaseAnnotation> iter) {
        annos = (ArrayList<BaseAnnotation>) iter.reduce(ListReducer.me);
        return this;
    }

    public ArrayList<BaseAnnotation> getAnnos() {
        return annos;
    }

    public void add(BaseAnnotation ba) {
        annos.add(ba);
    }

    public int writeLog(BaseFile file) {
        return new CollectionIterator<>(annos).
                map(Anno2Line.me).
                reduce(new ToPrintWriter(BaseFileUtil.bf2utf8printwriter.apply(file), true));
    }
}
