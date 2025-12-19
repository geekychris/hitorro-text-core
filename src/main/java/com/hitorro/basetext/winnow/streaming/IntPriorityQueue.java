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
public class IntPriorityQueue {
    private int[] heap;

    private int size;

    private int maxSize;

    /**
     * Create a priority queue with a particular comparator and max size
     *
     * @param maxQueueSize
     */
    public IntPriorityQueue(int maxQueueSize) {
        initialize(maxQueueSize);
    }

    /**
     * Subclass constructors must call this.
     */
    @SuppressWarnings("unchecked")
    protected final void initialize(int maxQueueSize) {
        size = 0;
        int heapSize = maxQueueSize + 1;
        heap = new int[heapSize];
        this.maxSize = maxQueueSize;
    }

    /**
     * Adds an Object to a PriorityQueue in log(size) time. If one tries to put more objects than maxSize from
     * initialize a RuntimeException (ArrayIndexOutOfBound) is thrown.
     */
    public final void put(int element) {
        size++;
        heap[size] = element;
        upHeap();
    }

    /**
     * Adds element to the PriorityQueue in log(size) time if either the PriorityQueue is not full, or not
     * lessThan(element, top()).
     *
     * @param element
     * @return element that is removed as a result of an insert. null means that nothing was removed from the queue
     * could mean returning the element itself means that nothing was added to the queue
     */
    public int insert(int element) {
        if (size < maxSize) {
            put(element);
            return Integer.MIN_VALUE;
        } else if (size > 0 && element <= heap[1]) {
            int elementToRemove = heap[1];
            heap[1] = element;
            downHeap();
            return elementToRemove;
        }

        return element;
    }

    /**
     * Returns the least element of the PriorityQueue in constant time.
     */
    public final int top() {
        if (size > 0) {
            return heap[1];
        }

        return Integer.MIN_VALUE;
    }

    /**
     * Removes and returns the least element of the PriorityQueue in log(size) time.
     */
    public final int pop() {
        if (size > 0) {
            int result = heap[1]; // save first value
            heap[1] = heap[size]; // move last to first
            size--;
            downHeap(); // adjust heap
            return result;
        }
        return Integer.MIN_VALUE;
    }

    /**
     * Should be called when the Object at top changes values. Still log(n) worst case, but it's at least twice as fast
     * to
     * <p/>
     * <pre>
     * {
     * 	pq.top().change();
     * 	pq.adjustTop();
     * }
     * </pre>
     * <p/>
     * instead of
     * <p/>
     * <pre>
     * {
     * 	o = pq.pop();
     * 	o.change();
     * 	pq.push(o);
     * }
     * </pre>
     */
    public final void adjustTop() {
        downHeap();
    }

    /**
     * Returns the number of elements currently stored in the PriorityQueue.
     */
    public final int size() {
        return size;
    }

    /**
     * Maximum number of entries this queue can take.
     */
    public final int maxSize() {
        return maxSize;
    }

    /**
     * Removes all entries from the PriorityQueue.
     */
    public final void clear() {
        for (int i = 0; i <= size; i++) {
            heap[i] = Integer.MIN_VALUE;
        }
        size = 0;
    }

    private final void upHeap() {
        int i = size;
        int node = heap[i]; // save bottom node
        int j = i >>> 1;

        /** less than is -ve gt = +ve */
        while (j > 0 && node > heap[j]) {
            heap[i] = heap[j]; // shift parents down
            i = j;
            j = j >>> 1;
        }
        heap[i] = node; // install saved node
    }

    private final void downHeap() {
        int i = 1;
        int node = heap[i]; // save top node
        int j = i << 1; // find smaller child
        int k = j + 1;
        if (k <= size && heap[k] > heap[j]) {
            j = k;
        }
        while (j <= size && heap[j] > node) {
            heap[i] = heap[j]; // shift up child
            i = j;
            j = i << 1;
            k = j + 1;
            if (k <= size && heap[k] > heap[j]) {
                j = k;
            }
        }
        heap[i] = node; // install saved node
    }


    /**
     * This method returns underlying elements. It is useful when priority queue insertion is finished and quick access
     * to unsorted elements is desired
     * <p/>
     * Keep in mind that the first element is unused and always null
     *
     * @return unsorted elements
     */
    public final int[] getRawHeap() {
        return heap;
    }
}