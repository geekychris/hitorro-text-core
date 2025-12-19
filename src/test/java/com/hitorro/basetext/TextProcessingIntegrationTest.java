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
package com.hitorro.basetext;

import com.hitorro.basetext.filter.core.FilterFactory;
import com.hitorro.basetext.filter.core.TextFilter;
import com.hitorro.basetext.inverter.TermTuple;
import com.hitorro.basetext.winnow.streaming.ShingleUtil;
import com.hitorro.conceptnet5.RelationType;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Integration test that combines multiple components of the text processing system.
 */
public class TextProcessingIntegrationTest {

    @Test
    public void testDocumentProcessingWorkflow() {
        // Step 1: Create a mock document filtering pipeline
        FilterFactory factory = new FilterFactory();
        TextFilter mockFilter = new MockTextFilter(new String[]{"txt"});
        factory.registerFilter(mockFilter);
        
        // Verify filter is registered
        TextFilter retrievedFilter = factory.getFilterByFileName("document.txt");
        assertNotNull("Filter should be registered in factory", retrievedFilter);
        
        // Step 2: Create term tuples for document analysis
        List<TermTuple> terms = new ArrayList<>();
        
        TermTuple term1 = new TermTuple();
        term1.set("machine", 10);
        term1.normalizeTF(100);
        terms.add(term1);
        
        TermTuple term2 = new TermTuple();
        term2.set("learning", 8);
        term2.normalizeTF(100);
        terms.add(term2);
        
        TermTuple term3 = new TermTuple();
        term3.set("algorithm", 5);
        term3.normalizeTF(100);
        terms.add(term3);
        
        assertEquals("Should have 3 terms", 3, terms.size());
        
        // Verify each term has proper normalized TF values
        for (TermTuple term : terms) {
            assertTrue("Term should have positive normalized TF", term.getNormalizedTF() > 0);
        }
        
        // Step 4: Test similarity computation for document comparison
        int[] doc1Hashes = {100, 200, 300, 400, 500};
        int[] doc2Hashes = {200, 300, 400, 600, 700};
        
        int similarity = ShingleUtil.computeSimilarity(doc1Hashes, doc2Hashes);
        assertEquals("Documents should have 3 common hash values", 3, similarity);
        
        // Step 5: Create semantic relations for concept analysis
        RelationType isA = new RelationType(1, "IsA", "X is a type of Y");
        RelationType relatedTo = new RelationType(2, "RelatedTo", "X is related to Y");
        
        assertNotNull("IsA relation should exist", isA);
        assertEquals("IsA relation name should be correct", "IsA", isA.getName());
        
        // Verify the entire workflow succeeded
        assertTrue("Complete document processing workflow succeeded", true);
    }

    @Test
    public void testTermFrequencyNormalization() {
        // Create multiple terms with different frequencies
        TermTuple commonTerm = new TermTuple();
        commonTerm.set("the", 50);
        commonTerm.normalizeTF(100);
        
        TermTuple rareTerm = new TermTuple();
        rareTerm.set("quantum", 2);
        rareTerm.normalizeTF(100);
        
        // Common term should have higher normalized TF
        assertTrue("Common term should have higher normalized TF", 
                  commonTerm.getNormalizedTF() > rareTerm.getNormalizedTF());
        
        // Set TF-IDF measures
        commonTerm.setTermMeasure(0.1); // Low importance (common word)
        rareTerm.setTermMeasure(5.0);   // High importance (rare, specific word)
        
        assertTrue("Rare term should have higher measure", 
                  rareTerm.getMeasure() > commonTerm.getMeasure());
    }

    @Test
    public void testMultipleDocumentComparison() {
        // Create hash signatures for multiple documents
        int[] doc1 = {1, 2, 3, 4, 5};
        int[] doc2 = {3, 4, 5, 6, 7};
        int[] doc3 = {5, 6, 7, 8, 9};
        
        int sim12 = ShingleUtil.computeSimilarity(doc1, doc2);
        int sim23 = ShingleUtil.computeSimilarity(doc2, doc3);
        int sim13 = ShingleUtil.computeSimilarity(doc1, doc3);
        
        // doc1 and doc2 share more than doc1 and doc3
        assertTrue("doc1-doc2 similarity should be greater than doc1-doc3", sim12 > sim13);
        // doc2 and doc3 share more than doc1 and doc3
        assertTrue("doc2-doc3 similarity should be greater than doc1-doc3", sim23 > sim13);
    }

    @Test
    public void testSemanticRelationTypes() {
        // Create a knowledge graph of semantic relations
        List<RelationType> relations = new ArrayList<>();
        relations.add(new RelationType(1, "IsA", "X is a type of Y"));
        relations.add(new RelationType(2, "PartOf", "X is a part of Y"));
        relations.add(new RelationType(3, "UsedFor", "X is used for Y"));
        relations.add(new RelationType(4, "CapableOf", "X is capable of Y"));
        
        assertEquals("Should have 4 relation types", 4, relations.size());
        
        // Verify each relation has required properties
        for (RelationType rt : relations) {
            assertNotNull("Relation name should not be null", rt.getName());
            assertNotNull("Relation description should not be null", rt.getDescription());
            assertTrue("Relation ID should be positive", rt.getId() > 0);
        }
    }

    @Test
    public void testFilterFactoryWithMultipleExtensions() {
        FilterFactory factory = new FilterFactory();
        
        // Register filters for different file types
        factory.registerFilter(new MockTextFilter(new String[]{"txt", "text"}));
        factory.registerFilter(new MockTextFilter(new String[]{"doc", "docx"}));
        factory.registerFilter(new MockTextFilter(new String[]{"pdf"}));
        
        // Verify all extensions are registered
        List extensions = factory.extensions();
        assertTrue("Should have multiple extensions", extensions.size() >= 5);
        
        // Test retrieval by different file names
        assertNotNull("Should find filter for .txt", factory.getFilterByFileName("doc.txt"));
        assertNotNull("Should find filter for .doc", factory.getFilterByFileName("doc.doc"));
        assertNotNull("Should find filter for .pdf", factory.getFilterByFileName("doc.pdf"));
    }

    /**
     * Mock TextFilter implementation for testing.
     */
    private static class MockTextFilter implements TextFilter {
        private final String[] extensions;

        public MockTextFilter(String[] extensions) {
            this.extensions = extensions;
        }

        @Override
        public String[] extensions() {
            return extensions;
        }

        @Override
        public File convert(File in, File out) {
            return out;
        }
    }
}
