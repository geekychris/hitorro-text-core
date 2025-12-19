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
package com.hitorro.obj.core.wikimedia;

import com.hitorro.jsontypesystem.JVS;
import com.hitorro.jsontypesystem.Type;
import com.hitorro.util.core.iterator.mappers.BaseMapper;
import com.hitorro.util.json.keys.propaccess.PropaccessError;
import com.hitorro.util.xml.XE;

import java.util.Date;

public class WikiXML2JVSMapper extends BaseMapper<XE, JVS> {
    private WikimediaMapper mediaMapper = new WikimediaMapper();
    private String lang = "en";
    private WikimediaMapper wikiMapper = new WikimediaMapper();
    private Type type;
    private String domain;
    private Date now = new Date();

    public WikiXML2JVSMapper(String lang, String domain, Type type) {
        this.lang = lang;
        this.type = type;
        this.domain = domain;
    }

    @Override
    public JVS apply(final XE node) {
        try {
            String title = node.get("title").getValue();
            String id = node.get("id").getValue();
            String text = node.get("revision.text").getValue();
            String body = wikiMapper.parseText(text, title, lang);
            JVS jvs = new JVS();
            jvs.setType(type);
            jvs.addLangTextTemporaryReLook(JVS.titleKey, title, lang);
            if (body.length() > 200) {
                int i = 1;
            }
            jvs.addLangTextTemporaryReLook
                    (JVS.bodyKey, body, lang);
            jvs.setDates(now, now);
            jvs.setId(domain, id);
            return jvs;

        } catch (PropaccessError e) {
            return null;
        }

    }
}
