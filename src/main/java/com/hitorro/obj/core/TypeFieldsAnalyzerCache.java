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

import com.hitorro.basetext.indexer.TypeFieldsCache;
import com.hitorro.util.core.events.cache.HashCache;
import com.hitorro.util.core.iterator.mappers.BaseMapper;
import com.hitorro.util.typesystem.TypeIntf;
import com.hitorro.util.typesystem.TypeManager;

import java.util.ArrayList;

/**
 *
 */
public class TypeFieldsAnalyzerCache extends BaseMapper<TypeIntf, AnalyzerPerLanguage> {
    public static final String EventName = "TypeFieldsCache";
    public static final HashCache<TypeIntf, AnalyzerPerLanguage> query = new HashCache(EventName, new AnalyzerPerLanguage(new ArrayList(), GenericAnalyzer.Mode.Query), new TypeFieldsAnalyzerCache(GenericAnalyzer.Mode.Query));
    public static final HashCache<TypeIntf, AnalyzerPerLanguage> index = new HashCache(EventName, new AnalyzerPerLanguage(new ArrayList(), GenericAnalyzer.Mode.Index), new TypeFieldsAnalyzerCache(GenericAnalyzer.Mode.Index));


    TypeManager m_tm = TypeManager.getTypeManager();

    private GenericAnalyzer.Mode mode;

    public TypeFieldsAnalyzerCache(GenericAnalyzer.Mode mode) {
        this.mode = mode;
    }

    public AnalyzerPerLanguage apply(TypeIntf key) {
        return new AnalyzerPerLanguage(TypeFieldsCache.getCache().get(key), mode);
    }
}


