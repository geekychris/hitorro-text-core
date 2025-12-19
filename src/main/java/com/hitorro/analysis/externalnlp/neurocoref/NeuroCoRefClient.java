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
package com.hitorro.analysis.externalnlp.neurocoref;

import com.fasterxml.jackson.databind.JsonNode;
import com.hitorro.jsontypesystem.JVS;
import com.hitorro.jsontypesystem.Json2JVSMapper;
import com.hitorro.jsontypesystem.predicates.ForEach;
import com.hitorro.util.core.Console;
import com.hitorro.util.core.http.JSONHTTPIterableClient;
import com.hitorro.util.core.iterator.AbstractIterator;
import com.hitorro.util.core.string.Fmt;
import com.hitorro.util.json.keys.StringProperty;
import org.apache.http.HttpException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * start a local docker
 * <p>
 * docs
 * https://github.com/huggingface/neuralcoref
 * <p>
 * <p>
 * docker run -d -it -p 9999:8080 artpar/languagecrunch
 * <p>
 * <p>
 * sentence
 * <p>
 * http://localhost:9999/nlp/parse?sentence=The%20lady%20sat%20on%20a%20toad%20stool
 * <p>
 * http://localhost:9999/nlp/parse?sentence=I%20had%20a%20telephone%20call%20with%20Barack%20Obama%20on%20december%2025th%20in%20london
 * <p>
 * <p>
 * coref
 * <p>
 * curl -H "Accept: application/json" "http://localhost:9999/nlp/coref?sentence=The%20lady%20sat%20on%20a%20toad%20stool"
 */
public class NeuroCoRefClient {
    private String host;
    private int port;

    public NeuroCoRefClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String args[]) {
        try {

            JVS jvs = new NeuroCoRefClient("localhost", 9999).get("I had a telephone call with Barack Obama on december 25th in london.");
            System.out.println();
            StringProperty sent = new StringProperty("sentences", "", null);
            StringProperty pos = new StringProperty("pos", "", null);
            ForEach fe = new ForEach(sent).forEach(pos);
            List<JsonNode> l = new ArrayList();
            fe.visit(jvs.getJsonNode(), l);
            Console.println();
        } catch (Exception e) {
            System.out.println();
        }
    }

    public JVS get(String sentence) throws IOException, HttpException {
        JSONHTTPIterableClient client = new JSONHTTPIterableClient();
        String url = Fmt.S("http://%s:%s/nlp/parse?sentence=%r",
                host, port,
                sentence);

        AbstractIterator<JsonNode> iter = client.getStream(url);
        return iter.map(Json2JVSMapper.me).getFirstItem();
    }


}
