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

import com.hitorro.util.basefile.fs.BaseFile;
import com.hitorro.util.basefile.tools.BaseFileUtil;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

public class AnnoConf {
    private Set<String> entities = new HashSet<>();

    private BaseFile dir;

    public AnnoConf(BaseFile dir) {
        this.dir = dir;
    }

    public void addEntity(String e) {
        entities.add(e);
    }

    public void write() {
        PrintWriter pw = BaseFileUtil.bf2utf8printwriter.apply(dir.getChild("annotation.conf"));
        pw.println("[entities]");
        for (String e : entities) {
            pw.println(e);
        }

        pw.println("[relations]");
        pw.println("[events]");
        pw.println("[attributes]");
        pw.flush();
        pw.close();
    }
}