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

import com.hitorro.util.core.CharArrayWrapper;
import com.hitorro.util.io.FileUtil;

import java.io.File;

/**
 * Copyright (c) 2003 - present HiTorro All rights reserved. User: chris Date: Jan 18, 2004 Time: 12:27:50 PM
 */
public class Hash {
    public static int HASHBASE = 2;
    public static int SUBSTRINGLENGTH = 17;
    public static int MINDIGITLENGTH = 6;

    public static long hashToFile(String inFile, String outFile, int subStringLength, int windowSize) {
        CharArrayWrapper wrapper = FileUtil.getCharArrayFromASCIIFile(new File(inFile));
        HashWriterImpl writer = new HashWriterImpl();
        writer.open(new File(outFile));
        return hashToWriter(wrapper, writer, subStringLength, windowSize);
    }

    public static long hashAndWinnowToFile(String inFile, String outFile, int subStringLength, int windowSize) {
        CharArrayWrapper wrapper = FileUtil.getCharArrayFromASCIIFile(new File(inFile));
        HashWriterImpl realWriter = new HashWriterImpl();
        realWriter.open(new File(outFile));
        int initialSize = wrapper.getSize();
        WinnowingHashWriter hashWriter = new WinnowingHashWriter(realWriter, initialSize, windowSize);
        return hashToWriter(wrapper, hashWriter, subStringLength, windowSize);
    }

    public static long hashToWriter(CharArrayWrapper wrapper, HashWriter writer, int subStringLength, int windowSize) {
        int nFileSize = wrapper.getSize();
        if (nFileSize < subStringLength) {
            subStringLength = nFileSize / 3;
            windowSize = subStringLength - 1;
        }
        return hash(wrapper, writer, subStringLength);
    }

    public static long hash(CharArrayWrapper wrapper, HashWriter writer, int sl) {
        int i, counter, stoppingPoint;
        int key, keysWritten = 0;
        int substringLength = sl;
        boolean stringOfDigits = false;
        char[] buffer = wrapper.getArray();
        int fileSize = wrapper.getSize();

        stoppingPoint = fileSize - substringLength + 1;
        for (counter = 0; counter < stoppingPoint; counter++) {

            if (Character.isDigit(buffer[counter])) {
                for (i = 0; Character.isDigit(buffer[counter + i]); i++) {
                    substringLength = i;
                }

                if (substringLength < MINDIGITLENGTH) {
                    substringLength = sl;
                } else {
                    substringLength++;
                    stringOfDigits = true;
                }
            }

            key = hashSubstring(buffer, counter, substringLength);
            writer.write(key, counter);
            keysWritten++;

            if (stringOfDigits) {
                stringOfDigits = false;
                counter += substringLength - 1;
                substringLength = sl;
            }
        }
        writer.close();
        return keysWritten;
    }

    public static int hashSubstring(char[] inString, int startIndex, int inStringLength) {
        int i, j;
        int hashkey = 0;

        for (i = 0, j = inStringLength; i < inStringLength; i++, j--) {
            // TODO write intPow so that were not converting to doubles!
            hashkey += inString[startIndex + i] * Math.pow(HASHBASE, j - 1);
        }
        return hashkey;
    }

}