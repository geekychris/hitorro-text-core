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
package com.hitorro.conceptnet5.mappers;

import com.hitorro.conceptnet5.Concept;
import com.hitorro.conceptnet5.ConceptNet;
import com.hitorro.util.core.iterator.AbstractIterator;
import com.hitorro.util.core.iterator.database.JDBCIterator;
import com.hitorro.util.core.iterator.mappers.BaseMapper;
import com.hitorro.util.core.string.Fmt;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ConceptMapper extends BaseMapper<ResultSet, Concept> {
    public static String conceptById = "select id, language_id, text from conceptnet_concept where id=%s";
    public static String conceptByTerm = "select id, language_id, text from conceptnet_concept where text='%s' and language_id='%s'";

    public static ConceptMapper me = new ConceptMapper();

    public AbstractIterator<Concept> getIterator(ConceptNet cn, int id) {
        return new JDBCIterator(Fmt.S(ConceptMapper.conceptById, id), this, cn.getConnection(), null);
    }

    public AbstractIterator<Concept> getIteratorForTerm(ConceptNet cn, String term, String language) {
        return new JDBCIterator(Fmt.S(ConceptMapper.conceptByTerm, term, language), this, cn.getConnection(), null);
    }

    @Override
    public Concept apply(ResultSet rs) {
        try {
            int id = rs.getInt("id");
            String lang = rs.getString("language_id");
            String text = rs.getString("text");
            return new Concept(text, lang, id);
        } catch (SQLException e) {
            return null;
        }
    }
}

