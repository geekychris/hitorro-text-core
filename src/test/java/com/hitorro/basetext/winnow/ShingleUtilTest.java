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
package com.hitorro.basetext.winnow;

import com.hitorro.basetext.winnow.streaming.ShingleUtil;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test cases for ShingleUtil similarity computation.
 */
public class ShingleUtilTest {

    @Test
    public void testComputeSimilarityIdenticalArrays() {
        int[] arr1 = {1, 2, 3, 4, 5};
        int[] arr2 = {1, 2, 3, 4, 5};
        
        int similarity = ShingleUtil.computeSimilarity(arr1, arr2);
        assertEquals("Identical arrays should have similarity equal to array length", 5, similarity);
    }

    @Test
    public void testComputeSimilarityNoOverlap() {
        int[] arr1 = {1, 2, 3};
        int[] arr2 = {4, 5, 6};
        
        int similarity = ShingleUtil.computeSimilarity(arr1, arr2);
        assertEquals("Non-overlapping arrays should have similarity of 0", 0, similarity);
    }

    @Test
    public void testComputeSimilarityPartialOverlap() {
        int[] arr1 = {1, 3, 5, 7, 9};
        int[] arr2 = {2, 3, 5, 8, 9};
        
        int similarity = ShingleUtil.computeSimilarity(arr1, arr2);
        assertEquals("Arrays with partial overlap should count common elements", 3, similarity);
    }

    @Test
    public void testComputeSimilarityDifferentSizes() {
        int[] arr1 = {1, 2, 3, 4, 5, 6, 7, 8};
        int[] arr2 = {2, 4, 6};
        
        int similarity = ShingleUtil.computeSimilarity(arr1, arr2);
        assertEquals("Different sized arrays should count intersections correctly", 3, similarity);
    }

    @Test
    public void testComputeSimilarityEmptyArrays() {
        int[] arr1 = {};
        int[] arr2 = {};
        
        int similarity = ShingleUtil.computeSimilarity(arr1, arr2);
        assertEquals("Empty arrays should have similarity of 0", 0, similarity);
    }

    @Test
    public void testComputeSimilarityOneEmptyArray() {
        int[] arr1 = {1, 2, 3};
        int[] arr2 = {};
        
        int similarity = ShingleUtil.computeSimilarity(arr1, arr2);
        assertEquals("Array with empty array should have similarity of 0", 0, similarity);
    }

    @Test
    public void testComputeSimilaritySingleElement() {
        int[] arr1 = {5};
        int[] arr2 = {5};
        
        int similarity = ShingleUtil.computeSimilarity(arr1, arr2);
        assertEquals("Single element arrays with same value should have similarity of 1", 1, similarity);
        
        arr2 = new int[]{3};
        similarity = ShingleUtil.computeSimilarity(arr1, arr2);
        assertEquals("Single element arrays with different values should have similarity of 0", 0, similarity);
    }

    @Test
    public void testComputeSimilarityDuplicateHandling() {
        // Note: Arrays should be sorted ascending for the algorithm to work correctly
        int[] arr1 = {1, 2, 2, 3, 4};
        int[] arr2 = {2, 2, 3, 5, 6};
        
        // The algorithm counts each match once as it progresses through arrays
        int similarity = ShingleUtil.computeSimilarity(arr1, arr2);
        assertTrue("Should handle duplicates in sorted arrays", similarity >= 2);
    }

    @Test
    public void testComputeSimilarityLargeArrays() {
        // Create arrays where half the elements overlap
        // arr1: 0, 2, 4, 6, 8, ..., 998 (500 even numbers from 0-998)
        // arr2: 0, 2, 4, 6, 8, ..., 998 (same 500 even numbers)
        int[] arr1 = new int[500];
        int[] arr2 = new int[500];
        
        for (int i = 0; i < 500; i++) {
            arr1[i] = i * 2;  // 0, 2, 4, 6, ...
            arr2[i] = i * 2;  // 0, 2, 4, 6, ...
        }
        
        int similarity = ShingleUtil.computeSimilarity(arr1, arr2);
        assertEquals("Arrays with identical even numbers should have full overlap", 500, similarity);
    }

    @Test
    public void testComputeSimilarityFirstArrayLarger() {
        int[] arr1 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int[] arr2 = {3, 5, 7};
        
        int similarity = ShingleUtil.computeSimilarity(arr1, arr2);
        assertEquals("Should find all elements from smaller array in larger", 3, similarity);
    }

    @Test
    public void testComputeSimilaritySecondArrayLarger() {
        int[] arr1 = {3, 5, 7};
        int[] arr2 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        
        int similarity = ShingleUtil.computeSimilarity(arr1, arr2);
        assertEquals("Should find all elements from smaller array in larger", 3, similarity);
    }

    @Test
    public void testComputeSimilaritySequentialNumbers() {
        int[] arr1 = {10, 20, 30, 40, 50};
        int[] arr2 = {15, 20, 35, 40, 55};
        
        int similarity = ShingleUtil.computeSimilarity(arr1, arr2);
        assertEquals("Should correctly identify common elements", 2, similarity);
    }
}
