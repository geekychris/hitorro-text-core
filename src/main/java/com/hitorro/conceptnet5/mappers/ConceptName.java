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

import com.hitorro.util.core.iterator.AbstractIterator;
import com.hitorro.util.core.iterator.database.JDBCIterator;
import com.hitorro.util.core.iterator.mappers.BaseMapper;
import com.hitorro.util.core.string.Fmt;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConceptName extends BaseMapper<ResultSet, String> {
    public static String relationSql = "select text from conceptnet_concept where num_assertions > 0";
    public static String relationWithIdSql = "select text,id from conceptnet_concept where num_assertions > 0";
    public static ConceptName me = new ConceptName();

    public static AbstractIterator<String> getIterator(Connection c, String language) {
        return new JDBCIterator(Fmt.S(ConceptName.relationSql, language), ConceptName.me, c, null);
    }

    public static AbstractIterator<String> getIteratorWithId(Connection c, String language) {
        return new JDBCIterator(Fmt.S(ConceptName.relationWithIdSql, language), ConceptName.me, c, null);
    }


    @Override
    public String apply(ResultSet rs) {
        try {
            return rs.getString("text");
        } catch (SQLException e) {
            return null;
        }
    }
}

