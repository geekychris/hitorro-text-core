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
package com.hitorro.basetext.phrase;

import com.hitorro.base.BaseBaseFileUtil;
import com.hitorro.obj.core.Log;
import com.hitorro.util.basefile.filters.FileExtension;
import com.hitorro.util.basefile.fs.BaseFile;
import com.hitorro.util.core.ArrayUtil;
import com.hitorro.util.core.Console;
import com.hitorro.util.core.iterator.AbstractIterator;
import com.hitorro.util.core.iterator.mappers.BaseMapper;
import com.hitorro.util.core.iterator.mappers.DummyBaseMapper;
import com.hitorro.util.core.iterator.sinks.Sink;
import com.hitorro.util.io.largedata.iterator.BaseFileSelectTreeController;
import com.hitorro.util.io.resourcecache.basefile.BaseFileResourcePropertyKey;
import com.hitorro.util.typesystem.HTSerializable;
import com.hitorro.util.typesystem.HTSerializableUtil;

import java.io.IOException;

public class PhraseUtilBasic {

	public static final BaseMapper<BaseFile, AbstractIterator<PhraseElement>> bf2phraseelementiter = BaseBaseFileUtil.bf2htser.combine(new DummyBaseMapper<AbstractIterator<? extends HTSerializable>, AbstractIterator<PhraseElement>>());
	public static final BaseMapper<BaseFile, Sink<PhraseElement>> bf2phraselementsink = HTSerializableUtil.bf2htser.combine(new DummyBaseMapper<Sink<? extends HTSerializable>, Sink<PhraseElement>>());
	public static final String ResourceName = "phraselist";
	public static final long MajorVersion = 1;
	public static final long MinorVersion = 0;
	public static final long PatchVersion = 0;
	public static final String VersionQuery = "1.0+";

	public static final String AlphaNumeric = "phrases_alphaNumeric.phrase";
	public static final String PhraseFreq = "phrases_alphaNumeric.phraseFreq";
	public static final String DictFile = "phrases.dict";

	public static final BaseFileResourcePropertyKey AlphaSortedPhraseListKey =
			new BaseFileResourcePropertyKey(PhraseUtilBasic.ResourceName, PhraseUtilBasic.VersionQuery, PhraseUtilBasic.AlphaNumeric,
					PhraseUtilBasic.AlphaNumeric, "");

	public static final boolean generatePhraseIndexFromPhrasesInResourceCache(int min, int max, String desc, String query, boolean writeDumpText) throws IOException {
		BaseFile f = AlphaSortedPhraseListKey.apply();
		if (BaseFile.notNullAndExists(f)) {
			PhraseIndex.createIndexInResourceCache(f, min, max, desc, query, writeDumpText);
			return true;
		}
		return false;
	}

	public static BaseFile mergePhraseElementByFrequency(BaseFile inputFile,
														 BaseFile dir,
														 String fileExtension,
														 int maxFiles, boolean deleteInput)
			throws Exception {
		PhraseFactory factory = new PhraseFactory();

		BaseFile files[] = inputFile.listFiles(new FileExtension(fileExtension, true));

		if (!ArrayUtil.nullOrEmpty(files) && files.length == 1) {
			// nothing todo.
			return files[0];
		}

		// this is merging to the same directory
		BaseFileSelectTreeController controller = new BaseFileSelectTreeController(dir, files, maxFiles, factory, false, fileExtension, true);
		BaseFile ret = controller.merge();
		if (BaseFile.notNullAndExistsAndContainsData(ret) && deleteInput) {
			for (BaseFile file : files) {
				file.delete();
			}
		}
		return ret;
	}


	/**
	 * Given a set of phrase frequency files that are already sorted, apply them together.
	 *
	 * @param inputFile
	 * @param dir
	 * @param fileExtension
	 * @param maxFiles
	 * @return
	 * @throws IOException
	 */

	public static void dumpPhraseElementFile(BaseFile f, int minVal, int maxVal) {
		AbstractIterator<PhraseElement> iter = PhraseUtilBasic.bf2phraseelementiter.apply(f);
		while (iter.hasNext()) {
			PhraseElement pe = iter.next();
			if (pe.getFrequency() >= minVal && pe.getFrequency() <= maxVal) {
				Console.println("%s : %s", pe.getPhrase(), pe.getFrequency());
			}
		}
		Console.println();
	}



	/**
	 * Given an iterator of text chew it up into phrases and spew it out as tuples of text and frequency.
	 *
	 * @param textIter
	 * @param dir
	 * @param fileExtension
	 * @param phraseDepth
	 * @param analyzers
	 * @param phrasesPerBucket
	 * @param mergeFilesLimit
	 * @return
	 * @throws IOException
	 */
	public static final BaseFile writePhrases(AbstractIterator<String> textIter,
											  BaseFile dir,
											  String fileExtension,
											  int phraseDepth,
											  int minDepth,
											  String analyzers,
											  int phrasesPerBucket,
											  int mergeFilesLimit) throws Exception {


		com.hitorro.basetext.phrase.TextToPhraseSink sink = new TextToPhraseSink(analyzers, phraseDepth, minDepth, phrasesPerBucket, dir, fileExtension);
		int count = textIter.sink(sink);

		Log.phraseextractor.info("Wrote %s phrase out", count);

		return mergePhraseElementByFrequency(dir, dir, fileExtension, mergeFilesLimit, true);
	}
}

