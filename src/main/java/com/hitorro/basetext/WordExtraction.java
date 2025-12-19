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
package com.hitorro.basetext;

import com.hitorro.util.core.Console;
import com.hitorro.util.core.string.Fmt;
import com.hitorro.util.io.FileUtil;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.model.DocumentProperties;
import org.apache.poi.hwpf.model.ListTables;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Loads a doc from the provided directory and saves in Documents directory html:
 * <p/>
 * tell application "Microsoft Word" activate open ":jen_target:AFKHAMI, REZA .doc" save as active document file name
 * "bla.html" file format format HTML close active document end tell
 * <p/>
 * pdf to a specified directory
 * <p/>
 * tell application "Microsoft Word" activate open ":jen_target:AFKHAMI, REZA .doc" change file open directory path
 * ":pdf:" save as active document file name "testchris.pdf" file format format PDF close active document end tell
 * <p/>
 * The online docs from Microsoft are here: http://www.microsoft.com/mac/developers/default.mspx?CTT=PageView&clr=99-21-0&target=c228e25f-f798-40f4-ad50-5033cd1b76371033&srcid=a96edcd4-ee10-49e2-b836-ce785291c6ea1033&ep=7
 * <p/>
 * but they are SHIT.
 * <p/>
 * Instead load the Applescript Editor and you can open a "dictionary".  Find the MS Word dictionary and it has lots of
 * docs
 * <p/>
 * for iworks: http://macscripter.net/viewtopic.php?id=22175
 */
public class WordExtraction {
    private ScriptEngineManager mgr = new ScriptEngineManager();
    private ScriptEngine engine = mgr.getEngineByName("AppleScript");

    public void convertFileToPDF(File file, File outputDir) throws ScriptException {
        convertTo(file, outputDir, "PDF");
    }

    public void convertFileToHtml(File file, File outputDir) throws ScriptException {
        convertTo(file, outputDir, "HTML");
    }

    public void convertTo(File file, File outputPath, String outFormat) throws ScriptException {
        String input = toAppleScriptFormat(file);
        FileUtil.ensureDirectoryExists(outputPath);
        if (!outputPath.exists()) {
            return;
        }
        String pdfPath = toAppleScriptFormat(outputPath);
        String pdfOutput = FileUtil.getFilePeerWithExtension(file, outFormat).getName();
        String script = Fmt.S("tell application \"Microsoft Word\"\n" +
                "activate\n" +
                "open \"%s\"\n" +
                "change file open directory path \"%s\"\n" +
                "save as active document file name \"%s\" file format format %s\n" +
                "close active document saving no\n" +
                "end tell", input, pdfPath, pdfOutput, outFormat);

        Console.println("%s", script);
        Object o = engine.eval(script);
        Console.println("%s", o);

    }

    private String toAppleScriptFormat(File f) {
        String s = f.getAbsolutePath();
        return s.replaceAll("/", ":");
    }

    public void convertToPdf(File root, File outDir) throws ScriptException {
        File files[] = root.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                convertToPdf(file, outDir);
            } else {
                if (file.getName().toLowerCase().endsWith("doc")) {
                    convertFileToPDF(file, outDir);
                }
            }
        }
    }


    public void convertToHTML(File root, File outDir) throws ScriptException {
        File files[] = root.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                convertToHTML(file, outDir);
            } else {
                if (file.getName().toLowerCase().endsWith("doc")) {
                    convertFileToHtml(file, outDir);
                }
            }
        }
    }

    public void setFileOrig(File file) throws IOException {
        InputStream is = FileUtil.getBufferedFileInputStream(file);
        HWPFDocument doc = new HWPFDocument(is);
        ListTables lt = doc.getListTables();
        DocumentProperties dp = doc.getDocProperties();
        WordExtractor we = new WordExtractor(doc);
        String text[] = we.getParagraphText();
        String header = we.getHeaderText();
        String footer = we.getFooterText();
        for (String t : text) {
            Console.println("%s", t);
        }
    }


}

