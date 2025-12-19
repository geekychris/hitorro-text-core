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
package com.hitorro.analysis.sentences;

import com.hitorro.base.docprocessing.EnqueueClient;
import com.hitorro.base.docprocessing.blockqueue.QueueService;
import com.hitorro.util.core.Console;
import com.hitorro.util.core.events.cache.PoolContainer;
import com.hitorro.util.core.string.StringUtil;
import com.hitorro.util.io.StoreException;
import com.hitorro.util.testframework.EnhancedTestCase;
import com.hitorro.util.testframework.HTTest;
import com.hitorro.util.testframework.RunLevel;
import com.hitorro.util.typesystem.Bag;
import com.hitorro.util.typesystem.HTSerializable;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.util.Span;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@HTTest(runlevel = RunLevel.Full,
        email = "chris@hitorro.com",
        description = "Test sentence segmentation")
public class TestSentenceDetector extends EnhancedTestCase {


    /**
     * drive the chunker to construct a parse tree of the sentence.
     */
    public void testChunker() throws Exception {
        com.hitorro.language.IsoLanguage eng = com.hitorro.language.Iso639Table.english;
        PoolContainer<com.hitorro.language.IsoLanguage, com.hitorro.language.PartOfSpeech> posPool = com.hitorro.language.PartOfSpeechSingletonMapper.singleton.get(eng);
        try (com.hitorro.language.PartOfSpeech pos = posPool.get()) {
            String tstString = "I love my ipod and my wife loves her steve jobs.";
            com.hitorro.language.POS p = pos.getPOS(tstString);
            String toks[] = p.getTokenizedText();
            List<String>[] tags = p.getTags();
            p.dumpInEnglish(0);
            for (int i = 0; i < tags.length; i++) {
                for (int j = 0; j < tags[i].size(); j++) {
                    Console.print("%s(%s)", toks[j], tags[i].get(j));
                }
                Console.println();
            }
            Parser parser = pos.getParser();

            printParseTree(ParserTool.parseLine("I love my ipod and my wife loves her steve jobs.", parser, 1)[0]);
            Console.println();
            printParseTree(ParserTool.parseLine("I really had fun last nigh, joe came too.", parser, 1)[0]);
        } finally {

        }

        Console.println();
    }

    private void printParseTree(Parse p) {
        ArrayList<ArrayList<String>> matrix = new ArrayList();
        printParseTreeAux(p, 0, 0, matrix);
        for (int i = 0; i < matrix.size(); i++) {

            ArrayList<String> row = matrix.get(i);
            for (int j = 0; j < row.size(); j++) {
                if (j != 0) {
                    Console.print("________");
                }
                Console.print(row.get(j));

            }
            Console.println();
        }


    }

    private void printParseTreeAux(Parse p, int level, int part, ArrayList<ArrayList<String>> matrix) {
        Span s = p.getSpan();
        if (matrix.size() < level + 1) {
            matrix.add(level, new ArrayList());
        }
        ArrayList<String> row = matrix.get(level);
        if (row == null) {
            row = new ArrayList();
            matrix.set(level, row);
        }
        row.add(p.getText().substring(s.getStart(), s.getEnd()));
        int index = 0;
        for (Parse pChild : p.getChildren()) {
            printParseTreeAux(pChild, level + 1, index++, matrix);
        }
    }

    public void tsest() throws IOException, StoreException, ClassNotFoundException {
        EnqueueClient ec = new EnqueueClient(QueueService.EnqueueKey, "docs");
        Bag b = Bag.getBagForType("webpage");
        b.setValue(b, "url", new String("http://www.cnn.com/foo/bar/default.txt"));
        b.setValue(b, "created", new Long(System.currentTimeMillis()));
        b.setValue(b, "title", new String("title of this epic yarn"));
        b.setValue(b, "body", new String("this is the body text of this baggy poohs"));
        List<HTSerializable> list = new ArrayList();
        list.add(b);
        ec.send(list, true);
    }

    public void testLanguageDetectionOnShortStrings() {
        com.hitorro.language.IsoLanguage el = com.hitorro.language.Iso639Table.getInstance().getLanguageFromContent("die dampfschiffcapitainaufvierwalderstettesee");
        assertEquals(el.getTwo(), "de");
        el = com.hitorro.language.Iso639Table.getInstance().getLanguageFromContent("Esta actividad volcánica fue descubierta");
        assertEquals(el.getTwo(), "es");
        el = com.hitorro.language.Iso639Table.getInstance().getLanguageFromContent("This is a test of english");
        assertEquals(el.getTwo(), "en");
    }

    public void testEnglishAndGerman() throws IOException {
        tLang(this.getInputFileRelative("german1.txt"));
        tLang(this.getInputFileRelative("english1.txt"));
    }

    public void testNameFinder() throws Exception {
        String tstString = "Chris Collins was seen with president Barack Obama and Jennifer Lee, he purchased an Apple iPad and a blackberry";
        com.hitorro.language.IsoLanguage lang = com.hitorro.language.Iso639Table.getInstance().getLanguageFromContent(tstString);
        PoolContainer<com.hitorro.language.IsoLanguage, com.hitorro.language.PartOfSpeech> posPool = com.hitorro.language.PartOfSpeechSingletonMapper.singleton.get(lang);
        try (com.hitorro.language.PartOfSpeech pos = posPool.get()) {
            com.hitorro.language.POS p = pos.getPOS(tstString);
            String toks[] = p.getTokenizedText();
            p.dumpInEnglish(0);
            com.hitorro.language.NameFinder nf = p.getNameFinder(com.hitorro.language.IsoLanguage.NameFinderIntent.Person);

            String names[] = nf.getNames();
            if (names != null) {
                for (int i = 0; i < names.length; i++) {
                    Console.println("%s", names[i]);
                }
            }
        } finally {

        }
    }

    public void tLang(File textFile) throws IOException {
        this.assertFileExistsWithExplanation(textFile);
        String content = StringUtil.readFileIntoString(textFile);

        com.hitorro.language.IsoLanguage lang = com.hitorro.language.Iso639Table.getInstance().getLanguageFromContent(content);
        PoolContainer<com.hitorro.language.IsoLanguage, com.hitorro.language.SentenceSegmenter> pool = com.hitorro.language.SentenceDetectorSingleton.singleton.get(lang);
        try (com.hitorro.language.SentenceSegmenter ss = pool.get()) {
            com.hitorro.language.Sentences s = ss.getSentenceOffsets(content);
            Span offsets[] = s.getOffsets();
            List<String> sentences = s.getSentences();

            for (String sent : sentences) {
                Console.println("%s", sent);
            }

            for (String sent : s.getSentencesDirect()) {
                Console.println("%s", sent);
            }
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {

        }


    }
}