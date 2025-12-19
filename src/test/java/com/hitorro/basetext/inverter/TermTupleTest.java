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
package com.hitorro.basetext.inverter;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test cases for TermTuple class which represents term frequency and measure data.
 */
public class TermTupleTest {

    private TermTuple termTuple;

    @Before
    public void setUp() {
        termTuple = new TermTuple();
    }

    @Test
    public void testSetAndGetTerm() {
        String term = "example";
        int tf = 5;
        
        termTuple.set(term, tf);
        
        assertEquals("Term should be set correctly", term, termTuple.getTerm());
        assertEquals("Term frequency should be set correctly", tf, termTuple.tf);
        assertTrue("Hash should be non-zero after setting term", termTuple.m_hash != 0);
    }

    @Test
    public void testNormalizeTF() {
        termTuple.set("test", 10);
        int documentSize = 100;
        
        termTuple.normalizeTF(documentSize);
        
        double expectedNormTf = (10.0 / 100.0) * TermTuple.TFMultFactor;
        assertEquals("Normalized TF should be calculated correctly", 
                     expectedNormTf, termTuple.getNormalizedTF(), 0.001);
    }

    @Test
    public void testComputeNormalizedDF() {
        termTuple.set("test", 5);
        int dfCount = 50;
        double corpusDocSize = 1000.0;
        
        double normDf = termTuple.computeNormalizedDF(dfCount, corpusDocSize);
        
        double expectedNormDf = (50.0 / 1000.0) * TermTuple.DFMultiFactor;
        assertEquals("Normalized DF should be calculated correctly", 
                     expectedNormDf, normDf, 0.001);
        assertEquals("getNormalizedDF should return same value", 
                     expectedNormDf, termTuple.getNormalizedDF(), 0.001);
    }

    @Test
    public void testSetAndGetTermMeasure() {
        termTuple.set("test", 5);
        double measure = 3.14;
        
        termTuple.setTermMeasure(measure);
        
        assertEquals("Term measure should be set correctly", measure, termTuple.getMeasure(), 0.001);
        assertTrue("isGood should return true after setting valid measure", termTuple.isGood());
    }

    @Test
    public void testIsGoodWithNegativeInfinity() {
        termTuple.set("test", 5);
        termTuple.setTermMeasure(Double.NEGATIVE_INFINITY);
        
        assertFalse("isGood should return false for NEGATIVE_INFINITY", termTuple.isGood());
    }

    @Test
    public void testSetDF() {
        double df = 42.0;
        termTuple.setDF(df);
        
        assertEquals("DF should be set correctly", df, termTuple.m_df, 0.001);
    }

    @Test
    public void testToString() {
        termTuple.set("example", 10);
        termTuple.setTermMeasure(2.5);
        
        String result = termTuple.toString();
        
        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain term", result.contains("example"));
        assertTrue("toString should contain measure info", result.contains("2.5"));
    }

    @Test
    public void testMultipleTermsWithSameTF() {
        TermTuple tuple1 = new TermTuple();
        TermTuple tuple2 = new TermTuple();
        
        tuple1.set("hello", 5);
        tuple2.set("world", 5);
        
        assertEquals("Both should have same TF", tuple1.tf, tuple2.tf);
        assertNotEquals("Hashes should be different for different terms", 
                       tuple1.m_hash, tuple2.m_hash);
    }

    @Test
    public void testNormalizationEdgeCases() {
        termTuple.set("rare", 1);
        termTuple.normalizeTF(1000);
        
        // Very low frequency term
        assertTrue("Normalized TF should be positive", termTuple.getNormalizedTF() > 0);
        assertTrue("Normalized TF should be small", termTuple.getNormalizedTF() < 10);
        
        TermTuple commonTerm = new TermTuple();
        commonTerm.set("common", 500);
        commonTerm.normalizeTF(1000);
        
        // Very high frequency term
        assertTrue("Common term normalized TF should be larger", 
                  commonTerm.getNormalizedTF() > termTuple.getNormalizedTF());
    }

    @Test
    public void testDFNormalizationEdgeCases() {
        termTuple.set("rare", 1);
        
        // Rare term (appears in 1 out of 10000 documents)
        double rareNormDf = termTuple.computeNormalizedDF(1, 10000.0);
        assertTrue("Rare term should have low normalized DF", rareNormDf < 1);
        
        TermTuple commonTerm = new TermTuple();
        commonTerm.set("common", 1);
        
        // Common term (appears in 5000 out of 10000 documents)
        double commonNormDf = commonTerm.computeNormalizedDF(5000, 10000.0);
        assertTrue("Common term should have higher normalized DF", commonNormDf > rareNormDf);
    }

    @Test
    public void testSerializationMetadata() {
        assertEquals("Serialization version should be 1", 
                    1, termTuple.getSerializationVersion());
        assertFalse("TermTuple should not be persisted", termTuple.isPersisted());
        assertFalse("TermTuple should not have GUID", termTuple.hasGuid());
        assertFalse("TermTuple should not have soft GUID", termTuple.hasSoftGuid());
    }

    @Test
    public void testZeroFrequency() {
        termTuple.set("zero", 0);
        termTuple.normalizeTF(100);
        
        assertEquals("Normalized TF should be 0 for zero frequency", 
                    0.0, termTuple.getNormalizedTF(), 0.001);
    }

    @Test
    public void testHashConsistency() {
        String term = "consistent";
        
        TermTuple tuple1 = new TermTuple();
        tuple1.set(term, 5);
        
        TermTuple tuple2 = new TermTuple();
        tuple2.set(term, 10);
        
        assertEquals("Same term should produce same hash", tuple1.m_hash, tuple2.m_hash);
    }
}
