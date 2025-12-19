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
package com.hitorro.basetext.wiki;

import com.hitorro.util.core.string.StringUtil;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class WIkiXMLHandler extends DefaultHandler {
    /**
     * When you see the end tag, print it out and decrease indentation level by 2.
     */
    private boolean inField = false;
    private StringBuilder builder = new StringBuilder();
    private Map<String, String> fields = new HashMap();

    /**
     * When you see a start tag, print it out and then increase indentation by two spaces. If the element has
     * attributes, place them in parens after the element name.
     */
    public void startElement(String namespaceUri,
                             String localName,
                             String qualifiedName,
                             Attributes attributes) {

        inField = true;
        builder.setLength(0);

    }

    public void endElement(String namespaceUri,
                           String localName,
                           String qualifiedName) {

        qualifiedName = qualifiedName.toLowerCase();


        if (inField) {
            if (builder.length() != 0) {
                String s = builder.toString().trim();

                if (!StringUtil.nullOrEmptyString(s)) {
                    // do something
                }
                builder.setLength(0);
            }
            inField = false;
            return;
        }
    }


    public void characters(char[] chars,
                           int startIndex,
                           int endIndex) {
        builder.append(chars, startIndex, endIndex);
    }
}
