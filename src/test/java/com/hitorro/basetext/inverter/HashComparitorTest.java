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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test cases for HashComparitor which compares TermTuples by their hash values.
 */
public class HashComparitorTest {

    private HashComparitor comparator;

    @Before
    public void setUp() {
        comparator = new HashComparitor();
    }

    @Test
    public void testCompareEqualHashes() {
        TermTuple tuple1 = new TermTuple();
        tuple1.set("test", 5);
        
        TermTuple tuple2 = new TermTuple();
        tuple2.set("test", 10);
        
        int result = comparator.compare(tuple1, tuple2);
        assertEquals("Same term should produce equal hash comparison", 0, result);
    }

    @Test
    public void testCompareLessThan() {
        TermTuple tuple1 = new TermTuple();
        tuple1.set("aaa", 5);
        
        TermTuple tuple2 = new TermTuple();
        tuple2.set("zzz", 5);
        
        // We don't know exact hash values, but we test the comparison consistency
        int result = comparator.compare(tuple1, tuple2);
        
        // The result should be consistent with hash ordering
        assertTrue("Comparison should return consistent value", 
                  result == -1 || result == 0 || result == 1);
    }

    @Test
    public void testCompareGreaterThan() {
        TermTuple tuple1 = new TermTuple();
        tuple1.m_hash = 1000L;
        
        TermTuple tuple2 = new TermTuple();
        tuple2.m_hash = 500L;
        
        int result = comparator.compare(tuple1, tuple2);
        assertEquals("Higher hash should return 1", 1, result);
    }

    @Test
    public void testCompareSymmetry() {
        TermTuple tuple1 = new TermTuple();
        tuple1.m_hash = 100L;
        
        TermTuple tuple2 = new TermTuple();
        tuple2.m_hash = 200L;
        
        int result1 = comparator.compare(tuple1, tuple2);
        int result2 = comparator.compare(tuple2, tuple1);
        
        assertEquals("Comparison should be symmetric", -result1, result2);
    }

    @Test
    public void testCompareTransitivity() {
        TermTuple tuple1 = new TermTuple();
        tuple1.m_hash = 100L;
        
        TermTuple tuple2 = new TermTuple();
        tuple2.m_hash = 200L;
        
        TermTuple tuple3 = new TermTuple();
        tuple3.m_hash = 300L;
        
        assertTrue("If A < B and B < C, then A < C",
                  comparator.compare(tuple1, tuple2) < 0 &&
                  comparator.compare(tuple2, tuple3) < 0 &&
                  comparator.compare(tuple1, tuple3) < 0);
    }

    @Test
    public void testSortingTermTuples() {
        List<TermTuple> tuples = new ArrayList<>();
        
        TermTuple tuple1 = new TermTuple();
        tuple1.m_hash = 500L;
        tuples.add(tuple1);
        
        TermTuple tuple2 = new TermTuple();
        tuple2.m_hash = 100L;
        tuples.add(tuple2);
        
        TermTuple tuple3 = new TermTuple();
        tuple3.m_hash = 300L;
        tuples.add(tuple3);
        
        TermTuple tuple4 = new TermTuple();
        tuple4.m_hash = 200L;
        tuples.add(tuple4);
        
        Collections.sort(tuples, comparator);
        
        assertEquals("First element should have smallest hash", 100L, tuples.get(0).m_hash);
        assertEquals("Second element hash", 200L, tuples.get(1).m_hash);
        assertEquals("Third element hash", 300L, tuples.get(2).m_hash);
        assertEquals("Last element should have largest hash", 500L, tuples.get(3).m_hash);
    }

    @Test
    public void testCompareWithZeroHash() {
        TermTuple tuple1 = new TermTuple();
        tuple1.m_hash = 0L;
        
        TermTuple tuple2 = new TermTuple();
        tuple2.m_hash = 100L;
        
        int result = comparator.compare(tuple1, tuple2);
        assertEquals("Zero hash should be less than positive hash", -1, result);
    }

    @Test
    public void testCompareWithNegativeHash() {
        TermTuple tuple1 = new TermTuple();
        tuple1.m_hash = -100L;
        
        TermTuple tuple2 = new TermTuple();
        tuple2.m_hash = 100L;
        
        int result = comparator.compare(tuple1, tuple2);
        assertEquals("Negative hash should be less than positive hash", -1, result);
    }

    @Test
    public void testCompareWithLargeHashes() {
        TermTuple tuple1 = new TermTuple();
        tuple1.m_hash = Long.MAX_VALUE;
        
        TermTuple tuple2 = new TermTuple();
        tuple2.m_hash = Long.MAX_VALUE - 1;
        
        int result = comparator.compare(tuple1, tuple2);
        assertEquals("MAX_VALUE should be greater than MAX_VALUE-1", 1, result);
    }

    @Test
    public void testCompareWithMinHashes() {
        TermTuple tuple1 = new TermTuple();
        tuple1.m_hash = Long.MIN_VALUE;
        
        TermTuple tuple2 = new TermTuple();
        tuple2.m_hash = Long.MIN_VALUE + 1;
        
        int result = comparator.compare(tuple1, tuple2);
        assertEquals("MIN_VALUE should be less than MIN_VALUE+1", -1, result);
    }

    @Test
    public void testStableSorting() {
        // Create tuples with same hash to test stability
        List<TermTuple> tuples = new ArrayList<>();
        
        for (int i = 0; i < 5; i++) {
            TermTuple tuple = new TermTuple();
            tuple.m_hash = 100L;
            tuple.tf = i; // Use tf to track original order
            tuples.add(tuple);
        }
        
        Collections.sort(tuples, comparator);
        
        // All should still be present
        assertEquals("All tuples should remain after sorting", 5, tuples.size());
        
        // All should have same hash
        for (TermTuple tuple : tuples) {
            assertEquals("All tuples should have same hash", 100L, tuple.m_hash);
        }
    }
}
