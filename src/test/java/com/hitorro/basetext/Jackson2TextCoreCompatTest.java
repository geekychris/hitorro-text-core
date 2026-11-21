package com.hitorro.basetext;

import com.hitorro.basetext.inverter.TFRowVisitor;
import com.hitorro.basetext.inverter.TermTuple;
import com.hitorro.basetext.mappers.NullLVMapping;
import com.hitorro.util.core.iterator.JsonValueSource;
import org.junit.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Pins the JSON that hitorro-text-core builds (term-frequency rows, NullLVMapping documents,
 * including class arrays holding numbers, nulls and containers where 2.x {@code textValue()}
 * returned null) to what 3.0.2 produced on Jackson 2.18. The golden file was produced by running
 * the 3.0.2 classes with the same inputs (generator: hitorro-j3 tools/Jackson2TextCoreGolden.java.txt).
 */
public class Jackson2TextCoreCompatTest {
  static final Map<String, String> out = new LinkedHashMap<>();
  static void p(String k, Object v){ out.put(k, String.valueOf(v).replace("\\","\\\\").replace("\n","\\n").replace("\r","\\r")); }
  static boolean jackson(Throwable t){ return t.getClass().getName().startsWith("com.fasterxml.jackson") || t.getClass().getName().startsWith("tools.jackson"); }
  static String tryIt(java.util.concurrent.Callable<Object> c){ try { return String.valueOf(c.call()); } catch(Throwable t){ return "!"+(jackson(t) ? "jackson" : t.getClass().getSimpleName()); } }
  static Map<String, String> compute() throws Exception {
    out.clear();
    ObjectMapper om = com.hitorro.util.json.JsonMappers.json();
    // TFRowVisitor: term-frequency rows (id -> tf), as written by TermVectorsGeneratorBase
    int[][] rows = {{}, {3, 1}, {3, 1, 7, 42, 1, 2}, {-1, 0, 2147483647, 5, 3, 9, 3, 10}};
    for (int r = 0; r < rows.length; r++) {
      final int[] row = rows[r];
      p("tf." + r, tryIt(() -> { TFRowVisitor v = new TFRowVisitor();
        for (int i = 0; i + 1 < row.length; i += 2) { TermTuple t = new TermTuple(); t.id = row[i]; t.tf = row[i + 1]; v.add(t); }
        JsonNode n = v.getRow(); return n + " " + om.writeValueAsString(n) + " " + om.readTree(om.writeValueAsString(n)).equals(n); }));
    }
    p("tf.clear", tryIt(() -> { TFRowVisitor v = new TFRowVisitor(); TermTuple t = new TermTuple(); t.id = 1; t.tf = 2; v.add(t); v.clear(); return v.getRow(); }));
    // NullLVMapping: picks the question sentence by the class arrays (textValue on any node type)
    String[] docs = {
      "{}",
      "{\"posts\":{\"subject_class_sents\":[\"A\",\"q\"],\"subject_sents\":[\"s0\",\"why?\"]}}",
      "{\"posts\":{\"subject_class_sents\":[1,null,{\"x\":1},[\"Q\"],true,\"Q\"],\"subject_sents\":[\"a\",\"b\",\"c\",\"d\",\"e\",\"f\"]}}",
      "{\"posts\":{\"subject_class_sents\":[\"Q\"],\"subject_sents\":[7]}}",
      "{\"posts\":{\"subject_class_sents\":[\"Q\"],\"subject_sents\":[\" \"],\"detail_class_sents\":[\"Q\"],\"detail_sents\":[\"detail?\"]}}",
      "{\"posts\":{\"detail_class_sents\":[\"Q\"],\"detail_sents\":[null]}}",
      "{\"posts\":{\"detail_class_sents\":[\"Q\"]}}",
      "{\"posts\":{\"subject_class_sents\":[\"Q\",\"Q\"],\"subject_sents\":[\"only\"]}}"};
    for (int i = 0; i < docs.length; i++) {
      final String d = docs[i];
      p("nulllv." + i, tryIt(() -> { JsonValueSource r = new NullLVMapping().apply(new JsonValueSource(om.readTree(d)));
        return r.getNode() + " " + om.writeValueAsString(r.getNode()); }));
    }
    return out;
  }

    @Test
    public void outputMatchesJackson2() throws Exception {
        Map<String, String> golden = new LinkedHashMap<>();
        try (InputStream in = getClass().getResourceAsStream("jackson2-text-core.txt")) {
            for (String line : new String(in.readAllBytes(), StandardCharsets.UTF_8).split("\n")) {
                if (line.isEmpty()) continue;
                int eq = line.indexOf('=');
                golden.put(line.substring(0, eq), line.substring(eq + 1));
            }
        }
        Map<String, String> got = compute();
        assertEquals(golden.size(), got.size());
        for (Map.Entry<String, String> e : golden.entrySet()) {
            assertEquals(e.getKey(), e.getValue(), got.get(e.getKey()));
        }
    }
}
