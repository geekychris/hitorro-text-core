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
package com.hitorro.analysis.wordnet;

import edu.mit.jwi.IRAMDictionary;
import edu.mit.jwi.RAMDictionary;
import edu.mit.jwi.data.ILoadPolicy;
import edu.mit.jwi.item.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class WordNetContext {
    IRAMDictionary dictMe;

    public WordNetContext(File dir) throws IOException, InterruptedException {
        // construct the dictionary object and open it
        dictMe = new RAMDictionary(dir, ILoadPolicy.NO_LOAD);
        dictMe.open();
        dictMe.load(true);
    }

    public List<String> getSynonyms(String wordIn, POS pos) {
        IIndexWord idxWord = getDict().getIndexWord(wordIn, pos);
        IWordID wordID = idxWord.getWordIDs().get(0);
        IWord word = getDict().getWord(wordID);
        ISynset synset = word.getSynset();
        List<String> list = new ArrayList();
        for (IWord w : synset.getWords()) {
            list.add(w.getLemma());
        }
        return list;
    }

    public boolean exists(String wordIn, POS pos) {
        IIndexWord idxWord = getDict().getIndexWord(wordIn, pos);
        return (idxWord != null);
    }

    public void getHypernyms(String wordIn, POS pos, Pointer p) {
        // get the synset
        IIndexWord idxWord = getDict().getIndexWord(wordIn, pos);
        IWordID wordID = idxWord.getWordIDs().get(0);
        IWord word = getDict().getWord(wordID);
        ISynset synset = word.getSynset();
        // get the hypernyms
        List<ISynsetID> hypernyms = synset.getRelatedSynsets(p);

        // print out each hypernyms id and synonyms
        List<IWord> words;
        for (ISynsetID sid : hypernyms) {
            words = getDict().getSynset(sid).getWords();
            System.out.print(sid + " {");
            for (Iterator<IWord> i = words.iterator(); i.hasNext(); ) {
                System.out.print(i.next().getLemma());
                if (i.hasNext()) {
                    System.out.print(", ");

                }
            }
            System.out.println("}");
        }
    }

    public IRAMDictionary getDict() {
        return dictMe;
    }

    /*public void trek (IDictionary dict)
    {
        int tickNext = 0;
        int tickSize = 20000;
        int seen = 0;
        System.out.print("Treking across Wordnet");
        long t = System.currentTimeMillis();
        for (POS pos : POS.values())
        {
            for (Iterator<IIndexWord> i = dict.getIndexWordIterator(pos); i.hasNext(); )
            {
                for (IWordID wid : i.next().getWordIDs())
                {
                    seen += dict.getWord(wid).getSynset().getWords().size();
                    if (seen > tickNext)
                    {

                        tickNext = seen + tickSize;
                    }
                }
            }
        }
        System.out.printf("done (%1d msec)\n", System.currentTimeMillis() - t);
        System.out.println("In my trek I saw " + seen + " words");
    }
    */
}
