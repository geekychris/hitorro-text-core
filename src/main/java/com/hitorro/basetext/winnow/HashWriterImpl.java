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
package com.hitorro.basetext.winnow;

import java.io.*;

/**
 * Copyright (c) 2003 - present HiTorro All rights reserved. User: chris Date: Jan 21, 2004 Time: 10:23:03 AM
 */
public class HashWriterImpl
        implements HashWriter {
    BufferedWriter writer;
    PrintWriter pw;

    public boolean open(File file) {
        try {

            FileWriter fr = new FileWriter(file);
            writer = new BufferedWriter(fr);
            pw = new PrintWriter(writer);
            return true;
        } catch (IOException ioe) {
            return false;
        }
    }

    public boolean write(int hash, int position) {
        pw.print(hash);
        pw.print(position);
        return true;
    }

    public boolean close() {
        try {
            writer.close();
            return true;
        } catch (IOException ioe) {
        }
        return false;
    }
}