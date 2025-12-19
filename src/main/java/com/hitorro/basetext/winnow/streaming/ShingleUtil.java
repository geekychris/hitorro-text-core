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
package com.hitorro.basetext.winnow.streaming;

/**
 *
 */
public class ShingleUtil {
    /**
     * Calculates the intersection count between two arrays or ascending sorted integers.
     *
     * @param arr1
     * @param arr2
     * @return
     */
    public static final int computeSimilarity(int arr1[], int arr2[]) {
        int common = 0;
        int arr1Length = arr1.length;
        int arr2Length = arr2.length;
        int a1I = 0;
        int a2I = 0;
        while (a1I < arr1Length && a2I < arr2Length) {
            if (arr1[a1I] == arr2[a2I]) {
                common++;
                a1I++;
                a2I++;
            } else if (arr1[a1I] > arr2[a2I]) {
                a2I++;
            } else {
                a1I++;
            }

        }
        return common;
    }
}
