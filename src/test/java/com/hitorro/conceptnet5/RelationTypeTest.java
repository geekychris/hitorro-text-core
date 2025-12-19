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

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test cases for RelationType class which represents semantic relations in ConceptNet.
 */
public class RelationTypeTest {

    @Test
    public void testRelationTypeCreation() {
        int id = 1;
        String name = "IsA";
        String description = "X is a type of Y";
        
        RelationType relationType = new RelationType(id, name, description);
        
        assertEquals("ID should be set correctly", id, relationType.getId());
        assertEquals("Name should be set correctly", name, relationType.getName());
        assertEquals("Description should be set correctly", description, relationType.getDescription());
    }

    @Test
    public void testRelationTypeToString() {
        RelationType relationType = new RelationType(42, "PartOf", "X is a part of Y");
        
        String result = relationType.toString();
        
        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain ID", result.contains("42"));
        assertTrue("toString should contain name", result.contains("PartOf"));
        assertTrue("toString should contain description", result.contains("X is a part of Y"));
    }

    @Test
    public void testMultipleRelationTypes() {
        RelationType isA = new RelationType(1, "IsA", "X is a type of Y");
        RelationType partOf = new RelationType(2, "PartOf", "X is a part of Y");
        RelationType usedFor = new RelationType(3, "UsedFor", "X is used for Y");
        
        assertNotEquals("Different relation types should have different IDs", 
                       isA.getId(), partOf.getId());
        assertNotEquals("Different relation types should have different names", 
                       isA.getName(), usedFor.getName());
    }

    @Test
    public void testRelationTypeWithZeroId() {
        RelationType relationType = new RelationType(0, "Unknown", "Unknown relation");
        
        assertEquals("Should allow zero as ID", 0, relationType.getId());
        assertEquals("Name should be set correctly", "Unknown", relationType.getName());
    }

    @Test
    public void testRelationTypeWithNegativeId() {
        RelationType relationType = new RelationType(-1, "Invalid", "Invalid relation");
        
        assertEquals("Should allow negative ID", -1, relationType.getId());
    }

    @Test
    public void testRelationTypeWithEmptyStrings() {
        RelationType relationType = new RelationType(1, "", "");
        
        assertEquals("Should allow empty name", "", relationType.getName());
        assertEquals("Should allow empty description", "", relationType.getDescription());
    }

    @Test
    public void testRelationTypeWithLongDescription() {
        String longDescription = "This is a very long description that explains in great detail " +
                                "the semantic relationship between two concepts in the knowledge graph. " +
                                "It provides comprehensive information about the nature of the connection.";
        
        RelationType relationType = new RelationType(100, "DetailedRelation", longDescription);
        
        assertEquals("Should handle long descriptions", longDescription, relationType.getDescription());
    }

    @Test
    public void testCommonConceptNetRelationTypes() {
        // Test some common ConceptNet relation types
        RelationType[] commonRelations = {
            new RelationType(1, "RelatedTo", "X is related to Y"),
            new RelationType(2, "IsA", "X is a type of Y"),
            new RelationType(3, "PartOf", "X is a part of Y"),
            new RelationType(4, "UsedFor", "X is used for Y"),
            new RelationType(5, "CapableOf", "X is capable of Y"),
            new RelationType(6, "AtLocation", "X is typically found at Y"),
            new RelationType(7, "Causes", "X causes Y"),
            new RelationType(8, "HasA", "X has Y"),
            new RelationType(9, "MadeOf", "X is made of Y"),
            new RelationType(10, "Desires", "X wants Y")
        };
        
        assertEquals("Should have 10 relation types", 10, commonRelations.length);
        
        for (RelationType rt : commonRelations) {
            assertNotNull("Name should not be null", rt.getName());
            assertNotNull("Description should not be null", rt.getDescription());
            assertTrue("ID should be positive", rt.getId() > 0);
        }
    }

    @Test
    public void testRelationTypeEquality() {
        RelationType rt1 = new RelationType(5, "IsA", "X is a type of Y");
        RelationType rt2 = new RelationType(5, "IsA", "X is a type of Y");
        
        // Note: RelationType doesn't override equals(), so this tests object identity
        assertNotSame("Different instances should not be same object", rt1, rt2);
        assertEquals("Same ID values should be equal", rt1.getId(), rt2.getId());
        assertEquals("Same name values should be equal", rt1.getName(), rt2.getName());
    }

    @Test
    public void testRelationTypeImmutability() {
        RelationType relationType = new RelationType(1, "IsA", "X is a type of Y");
        
        int originalId = relationType.getId();
        String originalName = relationType.getName();
        String originalDescription = relationType.getDescription();
        
        // Access the values multiple times
        relationType.getId();
        relationType.getName();
        relationType.getDescription();
        
        // Verify values haven't changed
        assertEquals("ID should remain constant", originalId, relationType.getId());
        assertEquals("Name should remain constant", originalName, relationType.getName());
        assertEquals("Description should remain constant", originalDescription, relationType.getDescription());
    }

    @Test
    public void testRelationTypeWithSpecialCharacters() {
        RelationType relationType = new RelationType(99, "Special-Relation_123", 
                                                     "Description with special chars: @#$%^&*()");
        
        assertEquals("Should handle special characters in name", 
                    "Special-Relation_123", relationType.getName());
        assertTrue("Should handle special characters in description", 
                  relationType.getDescription().contains("@#$%^&*()"));
    }
}
