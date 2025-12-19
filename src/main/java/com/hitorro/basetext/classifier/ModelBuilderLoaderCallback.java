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
package com.hitorro.basetext.classifier;

import com.hitorro.basetext.inverter.TermTupleSet;
import com.hitorro.util.core.Console;
import com.hitorro.util.core.GenericKeyValue;
import com.hitorro.util.core.Log;
import com.hitorro.util.core.map.HashToIdAllocatingMap;
import com.hitorro.util.core.map.MapUtil;
import com.hitorro.util.core.math.SparseVector;
import com.hitorro.util.core.math.SparseVectorEuclidDistance;
import com.hitorro.util.core.string.StringUtil;
import com.hitorro.util.io.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Copyright (c) 2003 - present HiTorro All rights reserved.
 * <p/>
 * User: chris
 */
public class ModelBuilderLoaderCallback implements LoaderCallback {
    private static final String Extensions[] = {"txt", "html", "htm"};
    private static final HashSet m_extensionMap = MapUtil.makeHashSet(Extensions);
    private HashToIdAllocatingMap m_idMap = new HashToIdAllocatingMap(true);
    private List<ClassifierDoc> m_docs = new ArrayList<ClassifierDoc>();
    private List<GenericKeyValue<SparseVector, String>> m_sv =
            new ArrayList<GenericKeyValue<SparseVector, String>>();

    public void document(File f, String[] category) {
        String ext = FileUtil.getFileExtension(f);

        if (!StringUtil.nullOrEmptyString(ext)) {
            ext = ext.toLowerCase();
            if (m_extensionMap.contains(ext)) {
                ClassifierDoc cd = new ClassifierDoc(f, category);
                m_docs.add(cd);

                Console.println("File: %s / %s", f, cd.getCategory());
            }
        }

    }

    public void computeModelDocVectors() {
        for (ClassifierDoc doc : m_docs) {
            try {
                doc.getTermSpace(m_idMap);
            } catch (IOException e) {
                Log.util.error("Unable to load document %s, %s %e", doc.getFile(), e, e);
            }
        }
        for (ClassifierDoc doc : m_docs) {
            try {
                SparseVector sv = doc.getSparseVector(m_idMap, true);
                m_sv.add(new GenericKeyValue<SparseVector, String>(sv, doc.getCategory()));
            } catch (IOException e) {
                Log.util.error("Unable to load document %s, %s %e", doc.getFile(), e, e);
            }
        }
    }

    public List<Parts> rank(TermTupleSet set, boolean normalize, int maxTerms) {

        SparseVector sv = ClassifierDoc.getSparseVectorAssumingNonAssignmentOfTerm(m_idMap, normalize, set, maxTerms);
        if (sv == null) {
            Console.println("No vector produced from test document");
            return null;
        }
        SparseVectorEuclidDistance dFunc = new SparseVectorEuclidDistance();
        List<Parts> list = new ArrayList<Parts>();
        for (GenericKeyValue<SparseVector, String> kv : m_sv) {
            String cat = kv.getValue();
            SparseVector c = kv.getKey();
            if (c != null) {
                double dist = dFunc.calculateEuclidDistance(sv, c);
                //Console.println("%s : %s", cat, dist);
                Parts p = new Parts();
                p.m_cat = cat;
                // note we do 1/over so rather than the smaller number being the higher rank, the bigger number
                // is the higher rank.
                // else it gets complicated for the reciprocol rank calculation.
                p.m_accum = 1.0 / dist;
                list.add(p);
            }

        }
        Collections.sort(list);

        List<Parts> result = new ArrayList<Parts>();
        Map<String, Parts> map = new HashMap<String, Parts>();
        double divisor = 1.0;
        for (Parts p : list) {   //Console.println("%s, %s", p.m_cat, p.m_accum);
            p.m_accum = p.m_accum / divisor;
            divisor += 1.0;
            //Console.println(">>>>%s, %s", p.m_cat, p.m_accum);
            Parts targ = map.get(p.m_cat);
            if (targ == null) {
                targ = new Parts();
                targ.m_cat = p.m_cat;
                map.put(p.m_cat, targ);
            }
            targ.m_accum += p.m_accum;
            targ.m_parts++;
        }

        Collection<Parts> vals = map.values();
        for (Parts part : vals) {
            result.add(part);
        }
        Collections.sort(result);
        return result;
    }
}

