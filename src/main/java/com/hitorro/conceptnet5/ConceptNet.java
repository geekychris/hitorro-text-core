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
package com.hitorro.conceptnet5;

import com.hitorro.conceptnet5.mappers.ConceptMapper;
import com.hitorro.conceptnet5.mappers.ConceptName;
import com.hitorro.conceptnet5.mappers.ConceptNameWithId;
import com.hitorro.conceptnet5.mappers.RelationTypeMapper;
import com.hitorro.conceptnet5.mappers.SurfaceFormMapper;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import com.hitorro.util.core.GenericKeyValue;
import com.hitorro.util.core.hash.FPHash64;
import com.hitorro.util.core.iterator.AbstractIterator;
import com.hitorro.util.json.keys.StringProperty;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConceptNet {
    public static final StringProperty urlKey = new StringProperty("", "conceptnet.jdbcuri", "jdbc:sqlite:/Users/chris/staged_dir/ConceptNet.db");
    private TLongObjectHashMap<RelationType> relationType = new TLongObjectHashMap<>();
    private TLongObjectHashMap<Concept> concepts = new TLongObjectHashMap();
    private TLongObjectHashMap<Concept> conceptsByTerm = new TLongObjectHashMap();
    private TIntObjectHashMap<SurfaceForm> surfaceForm = new TIntObjectHashMap();
    private Connection c;

    private ConceptNet() {

    }

    public static ConceptNet getNet() throws Exception {
        ConceptNet cn = new ConceptNet();
        cn.init();
        return cn;
    }

    private boolean init() throws Exception {
        Class.forName("org.sqlite.JDBC");
        c = DriverManager.getConnection(urlKey.apply());
        c.setAutoCommit(false);

        loadRelations();
        return true;
    }

    public AbstractIterator<String> getConcepts(String languageId) {
        return ConceptName.getIterator(getConnection(), languageId);
    }

    public AbstractIterator<GenericKeyValue<String, Integer>> getConceptsWithId(String languageId) {
        return ConceptNameWithId.getIterator(getConnection(), languageId);
    }

    public RelationType getRelationType(long id) {
        return relationType.get(id);
    }

    public SurfaceForm getSurfaceForm(int id) throws Exception {
        SurfaceForm c = surfaceForm.get(id);
        if (c != null) {
            return c;
        }
        AbstractIterator<SurfaceForm> iter = SurfaceFormMapper.getIterator(this.getConnection(), id);
        c = iter.next();
        iter.close();
        if (c != null) {
            surfaceForm.put(id, c);
        }
        return c;
    }

    public Connection getConnection() {
        return c;
    }

    private void loadRelations() throws Exception {
        AbstractIterator<RelationType> iter = RelationTypeMapper.getIterator(c);
        while (iter.hasNext()) {
            RelationType rt = iter.next();
            if (rt != null) {
                relationType.put(rt.getId(), rt);
            }
        }
        iter.close();
    }

    public Concept getConcept(String term, String language) throws Exception {
        long hash = FPHash64.getFP(term);
        Concept c = conceptsByTerm.get(hash);
        if (c == null) {
            AbstractIterator<Concept> iter = ConceptMapper.me.getIteratorForTerm(this, term, language);
            c = iter.next();
            if (c != null) {
                conceptsByTerm.put(hash, c);
                concepts.put(c.getId(), c);
            }
            iter.close();
        }
        return c;
    }

    public Concept getConcept(int id) throws Exception {
        Concept c = concepts.get(id);
        if (c != null) {
            return c;
        }
        AbstractIterator<Concept> iter = ConceptMapper.me.getIterator(this, id);
        c = iter.next();
        iter.close();
        if (c != null) {
            concepts.put(id, c);
            conceptsByTerm.put(c.getTermHash(), c);
        }
        return c;
    }
}








