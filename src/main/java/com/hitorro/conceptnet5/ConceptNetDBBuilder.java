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
package com.hitorro.conceptnet5;


import gnu.trove.set.hash.TLongHashSet;
import com.hitorro.util.basefile.filters.FileExtension;
import com.hitorro.util.basefile.fs.BaseFile;
import com.hitorro.util.core.Console;
import com.hitorro.util.core.Env;
import com.hitorro.util.core.iterator.AbstractIterator;
import com.hitorro.util.core.iterator.Mapper;
import com.hitorro.util.core.iterator.sinks.Sink;
import com.hitorro.util.io.csv.CSVIteratorImpl;
import com.hitorro.util.io.largedata.BaseFileAccessingObjectFactory;
import com.hitorro.util.io.largedata.CompressedIOIteratorMapper;
import com.hitorro.util.io.largedata.CompressedIOSinkMapper;
import com.hitorro.util.io.largedata.buckets.CompressedStreamFileBucketWriter;
import com.hitorro.util.io.largedata.iterator.BaseFileSelectTreeController;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class ConceptNetDBBuilder {
    static BaseFileAccessingObjectFactory<Concept> factory = new BaseFileAccessingObjectFactory() {

        @Override
        public Mapper<BaseFile, Sink> getBaseFileToSinkMapper() {
            return new CompressedIOSinkMapper(this);
        }

        @Override
        public Mapper<BaseFile, AbstractIterator> getBaseFileToChainingMapper() {
            return new CompressedIOIteratorMapper(this);
        }

        @Override
        public Object getObject() {
            return new Concept();
        }


        @Override
        public Comparator getDefaultComparitor() {
            return Concept.ConceptComparator.c;
        }

        @Override
        public Object[] getArray(final int i) {
            return new Concept[i];
        }

        @Override
        public boolean preferTreeBucket() {
            return false;
        }
    };
    TLongHashSet seen = new TLongHashSet();
    Set<String> rel = new HashSet();

    public void read(File file) throws IOException {
        //dumpFinal();
        //int maxLength, LikeRowMerger<T> merger, Comparator<T> comp, File directory, String extension
        int maxLength = 100000;

        BaseFile dir = Env.getHomeBaseFile().getChild("concept");
        dir.mkdir();

        CompressedStreamFileBucketWriter<Concept> writer =
                new CompressedStreamFileBucketWriter(maxLength, dir, "bin", factory);
        BaseFile csvFile = Env.getBaseFile(file);
        generate(writer, csvFile);

        BaseFile res = merge(dir, factory);
        res.renameTo(res.getParent().getChild("final.bin"));
        int i = 1;
    }

    private void dumpFinal() {
        BaseFile f = Env.getHomeBaseFile().getChild("concept/final.bin");
        AbstractIterator<Concept> it = factory.getBaseFileToChainingMapper().apply(f);
        while (it.hasNext()) {
            Concept c = it.next();
            if (c.lang.equals("en")) {
                Console.println();
            }
        }
    }

    private void generate(final CompressedStreamFileBucketWriter<Concept> writer, final BaseFile csvFile) throws IOException {
        try (CSVIteratorImpl iterator = new CSVIteratorImpl(csvFile, '\t')) {
            while (iterator.hasNext()) {
                Concept from = new Concept();
                Concept to = new Concept();
                String row[] = iterator.next();
                String relation = row[1];
                rel.add(relation);
                from.setFromFileElement(row[2]);

                to.setFromFileElement(row[3]);
                addConcept(from, seen, writer);
                addConcept(to, seen, writer);
                storeRelation(from, to, relation);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        writer.close();
    }

    public void storeRelation(Concept from, Concept to, String relation) {
        long fromId = from.getId();
        long toId = to.getId();
    }

    private BaseFile merge(final BaseFile dir, final BaseFileAccessingObjectFactory<Concept> factory) throws IOException {
        boolean deleteInput = true;
        BaseFile files[] = dir.listFiles(new FileExtension("bin", true));
        BaseFileSelectTreeController controller = new BaseFileSelectTreeController(dir, files, 10, factory, true, "bin", true);
        BaseFile ret = null;
        try {
            ret = controller.merge();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (BaseFile.notNullAndExistsAndContainsData(ret) && deleteInput) {
            for (BaseFile f : files) {
                f.delete();
            }
        }
        return ret;
    }

    public void addConcept(Concept c, TLongHashSet seen, CompressedStreamFileBucketWriter<Concept> writer) throws IOException {
        long hash = c.getTermLangPosHash();
        if (!seen.contains(hash)) {
            seen.add(hash);
            c.initID();
            writer.add(c);
        }
    }
}

