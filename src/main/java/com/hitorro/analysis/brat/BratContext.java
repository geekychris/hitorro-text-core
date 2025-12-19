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
import com.hitorro.analysis.brat.annotation.TagAnnotation;
import com.hitorro.analysis.brat.annotation.filter.AnnotationTypeFilter;
import com.hitorro.analysis.brat.mappers.AddSpanToAnnotation;
import com.hitorro.analysis.brat.mappers.FixupReferences;
import com.hitorro.analysis.brat.mappers.GetTextFromTagPosition;
import com.hitorro.analysis.brat.mappers.Line2BaseAnnotation;
import com.hitorro.util.basefile.fs.BaseFile;
import com.hitorro.util.basefile.tools.BaseFileUtil;
import com.hitorro.util.core.GenericKeyValue;
import com.hitorro.util.core.iterator.AbstractIterator;
import com.hitorro.util.core.iterator.ArrayIterator;
import com.hitorro.util.core.iterator.CollectionIterator;
import com.hitorro.util.core.iterator.reducers.ListReducer;
import com.hitorro.util.core.iterator.reducers.MapReducer;
import com.hitorro.util.core.iterator.reducers.NullReducer;
import com.hitorro.util.core.iterator.reducers.TupleComparatorReducer;
import com.hitorro.util.core.string.StringComparator;
import com.hitorro.util.core.string.StringRange;
import com.hitorro.util.core.string.StringSpanTokenizingIterator;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class BratContext {

    private BaseFile dir;
    private String textName;
    private AnnotationLog annoLog;
    private String textBuffer;
    private String annoName;
    private StringRange<StringRangePayload> span;
    private Map<String, BaseAnnotation> annotationMap;

    public BratContext(BaseFile dir, String name) throws IOException {
        this(dir, name, name);
        annoLog = new AnnotationLog();
    }

    public BratContext(BaseFile dir, String textName, String annoName) throws IOException {
        this.dir = dir;
        this.textName = textName;
        this.annoName = annoName;
    }

    public static BratContext read(BaseFile dir, String textName, String annoName) throws IOException {
        return new BratContext(dir, textName, annoName).initWithRead();
    }

    public AbstractIterator<StringRange<StringRangePayload>> getSpans() {
        return new ArrayIterator<>(span.getChildren());
    }

    public StringRange<StringRangePayload> getRootSpan() {
        return span;
    }

    public void writeLog(BaseFile dir, String name) {
        BaseFile f = dir.getChild("%s.ann", name);
        annoLog.writeLog(f);
    }

    public void addAnnotation(BaseAnnotation ba) {
        annoLog.add(ba);
    }

    private BratContext initWithRead() throws IOException {
        this.annoLog = getAnnotations(getAnnoFile());
        this.textBuffer = IOUtils.toString(BaseFileUtil.bf2reader.apply(getTextFile()));
        span = new StringRange(textBuffer);

        // build spans
        StringSpanTokenizingIterator sti = new StringSpanTokenizingIterator(textBuffer, "\n\n");
        span.setChildren((List<StringRange>) sti.reduce(ListReducer.me));

        // Find the correct sentence and assign it to the TagAnnotation
        new CollectionIterator<>(annoLog.getAnnos()).
                filter(AnnotationTypeFilter.tagType).map(new AddSpanToAnnotation(span)).reduce(NullReducer.me);

        // build a hashmap by the annotation name
        annotationMap = (Map<String, BaseAnnotation>) new CollectionIterator<>(annoLog.getAnnos()).
                reduce(new MapReducer(BaseAnnotation.annotationNameMapper));

        // fixup object references
        new CollectionIterator<>(annoLog.getAnnos()).
                map(new FixupReferences(annotationMap, span)).
                reduce(NullReducer.me);

        return this;
    }

    public BaseFile getAnnoFile() {
        return dir.getChild("%s.ann", annoName);
    }

    public BaseFile getTextFile() {
        return dir.getChild("%s.txt", annoName);
    }

    public GenericKeyValue<Integer, Integer> verifyTagAlignment() {
        return (GenericKeyValue<Integer, Integer>) new CollectionIterator<>(annoLog.getAnnos()).
                filter(AnnotationTypeFilter.tagType).
                map(new GetTextFromTagPosition(textBuffer).toTuple(TagAnnotation.mapTagAnnotationText)).
                reduce(new TupleComparatorReducer(StringComparator.me));
    }

    public AnnotationLog getAnnotations(BaseFile file) throws IOException {
        return (AnnotationLog) file.getLineReaderIteratorFromFile().map(new Line2BaseAnnotation()).reduce(new AnnotationLog());
    }
}





