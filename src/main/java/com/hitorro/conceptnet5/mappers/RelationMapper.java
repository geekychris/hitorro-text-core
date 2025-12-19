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

import com.hitorro.conceptnet5.Relation;
import com.hitorro.util.core.iterator.AbstractIterator;
import com.hitorro.util.core.iterator.database.JDBCIterator;
import com.hitorro.util.core.iterator.mappers.BaseMapper;
import com.hitorro.util.core.string.Fmt;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RelationMapper extends BaseMapper<ResultSet, Relation> {
    public static RelationMapper me = new RelationMapper();

    private static String forward = "select relation_id, concept1_id, concept2_id, best_surface1_id, best_frame_id from conceptnet_assertion where  %s= %s";

    private static String forwardKey = "concept1_id";

    private static String backwardKey = "concept2_id";


    public static AbstractIterator<Relation> getIterator(Connection c, boolean forward, long id) {
        String dir;
        if (forward) {
            dir = forwardKey;
        } else {
            dir = backwardKey;
        }
        return new JDBCIterator(Fmt.S(RelationMapper.forward, dir, id), RelationMapper.me, c, null);
    }

    @Override
    public Relation apply(ResultSet rs) {
        try {
            long relationId = rs.getLong("relation_id");
            long concept1Id = rs.getLong("concept1_id");
            long concept2Id = rs.getLong("concept2_id");
            //int concept1Id, int bestSurfaceForm, int bestFrameId, int concept2Id, int relationId
            return new Relation(concept1Id, concept2Id, relationId);
        } catch (SQLException e) {
            return null;
        }
    }
}

