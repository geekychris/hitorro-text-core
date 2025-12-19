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
package com.hitorro.basetext.dfindex;

import com.hitorro.jsontypesystem.JVS;
import com.hitorro.util.commandandcontrol.Command;
import com.hitorro.util.commandandcontrol.CommandSession;
import com.hitorro.util.commandandcontrol.Response;
import com.hitorro.util.commandandcontrol.ResponseShape;
import com.hitorro.util.commandandcontrol.ano.CommandArgument;
import com.hitorro.util.commandandcontrol.ano.CommandDef;
import com.hitorro.util.commandandcontrol.ano.RespColumn;
import com.hitorro.util.commandandcontrol.ano.ResponseDefinition;
import com.hitorro.util.core.events.cache.SingletonCache;
import com.hitorro.util.json.keys.StringProperty;

/**
 * Copyright (c) 2003 - present HiTorro All rights reserved. User: chris Date: Jan 17, 2005 Time: 3:48:45 PM
 */
@CommandDef(command = "text.dumpdfterm", description = "Dump the frequency of a term")
public class DumpDF extends Command {
    @CommandArgument(required = true)
    private static final StringProperty TermKey = new StringProperty("term", "term", null);

    @ResponseDefinition(command = "dumpdf",
            rowname = "term",
            columns = {@RespColumn(name = "Term", lName = "term"),
                    @RespColumn(name = "DF", lName = "df", type = Integer.class),
                    @RespColumn(name = "DFRaw", lName = "dfraw", type = Integer.class),
                    @RespColumn(name = "Document #", lName = "docno", type = Integer.class),
                    @RespColumn(name = "Average doc length", lName = "avgdoclength", type = Double.class),
                    @RespColumn(name = "Frequency Max", lName = "freqmax", type = Integer.class)})
    private ResponseShape shape = new ResponseShape();

    public boolean execute(String rawValue, JVS args, Response response, CommandSession session) {
        String term = TermKey.apply(args);
        SingletonCache<DFIndex> cache = DFIndexSingletonMapper.getSingleton();
        DFIndex index = cache.get();
        if (index == null) {
            this.writeSimpleError(response, "Unable to load df index");
            return false;
        }
        response.setResponseShape(shape);
        response.addRow(term, index.getFrequency(term), index.getFrequencyRaw(term),
                index.getDocFrequency(), index.getAverageDocLength(), index.getFrequencyMaxAbsolute());

        return false;
    }
}
