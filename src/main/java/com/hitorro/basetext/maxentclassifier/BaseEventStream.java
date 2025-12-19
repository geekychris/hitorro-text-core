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
package com.hitorro.basetext.maxentclassifier;

import com.hitorro.basetext.classifier.GeneratorInterface;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.ml.model.Event;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.util.ObjectStream;

import java.io.*;

/**
 *
 */
public class BaseEventStream implements ObjectStream<Event> {
    protected BufferedReader reader;
    protected String line;
    protected GeneratorInterface atcg;
    protected Parser parser;

    public BaseEventStream(String fileName, String encoding, GeneratorInterface atcg, Parser parser) throws
            IOException {
        if (encoding == null) {
            reader = new BufferedReader(new FileReader(fileName));
        } else {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), encoding));
        }
        this.atcg = atcg;
        this.parser = parser;
    }

    public BaseEventStream(String fileName, GeneratorInterface atcg, Parser parser) throws IOException {
        this(fileName, null, atcg, parser);
    }

    /**
     * Creates a new file event stream from the specified file.
     *
     * @param file the file containing the events.
     * @throws IOException When the specified file can not be read.
     */
    public BaseEventStream(File file) throws IOException {
        reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
    }

    /**
     * Generates a string representing the specified event.
     *
     * @param event The event for which a string representation is needed.
     * @return A string representing the specified event.
     */
    public static String toLine(Event event) {
        StringBuffer sb = new StringBuffer();
        sb.append(event.getOutcome());
        String[] context = event.getContext();
        for (int ci = 0, cl = context.length; ci < cl; ci++) {
            sb.append(" " + context[ci]);
        }
        sb.append(System.getProperty("line.separator"));
        return sb.toString();
    }

    @Override
    public Event read() throws IOException {
        if (null != (line = reader.readLine())) {
            int split = line.indexOf(' ');
            String outcome = line.substring(0, split);
            String question = line.substring(split + 1);
            Parse query = ParserTool.parseLine(question, parser, 1)[0];
            return (new Event(outcome, atcg.getContext(query)));
        }
        return null;
    }


}
