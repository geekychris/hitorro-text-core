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
package com.hitorro.basetext.filter;

import com.hitorro.basetext.filter.core.FilterFactory;
import com.hitorro.basetext.filter.core.TextFilter;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test cases for FilterFactory to verify filter registration and retrieval.
 */
public class FilterFactoryTest {

    private FilterFactory factory;

    @Before
    public void setUp() {
        factory = new FilterFactory();
    }

    @Test
    public void testRegisterAndRetrieveFilter() {
        // Create a mock text filter
        TextFilter mockFilter = new MockTextFilter(new String[]{"txt", "text"});
        
        // Register the filter
        factory.registerFilter(mockFilter);
        
        // Verify retrieval by file name
        TextFilter retrievedFilter = factory.getFilterByFileName("test.txt");
        assertNotNull("Filter should be retrievable by txt extension", retrievedFilter);
        assertEquals("Retrieved filter should be the same as registered", mockFilter, retrievedFilter);
        
        // Test with different extension
        retrievedFilter = factory.getFilterByFileName("document.text");
        assertNotNull("Filter should be retrievable by text extension", retrievedFilter);
        assertEquals("Retrieved filter should be the same as registered", mockFilter, retrievedFilter);
    }

    @Test
    public void testRetrieveFilterByFile() {
        TextFilter mockFilter = new MockTextFilter(new String[]{"pdf"});
        factory.registerFilter(mockFilter);
        
        File testFile = new File("document.pdf");
        TextFilter retrievedFilter = factory.getFilterByFileName(testFile);
        assertNotNull("Filter should be retrievable by File object", retrievedFilter);
        assertEquals("Retrieved filter should be the same as registered", mockFilter, retrievedFilter);
    }

    @Test
    public void testMultipleFilters() {
        TextFilter txtFilter = new MockTextFilter(new String[]{"txt"});
        TextFilter pdfFilter = new MockTextFilter(new String[]{"pdf"});
        TextFilter docFilter = new MockTextFilter(new String[]{"doc", "docx"});
        
        factory.registerFilter(txtFilter);
        factory.registerFilter(pdfFilter);
        factory.registerFilter(docFilter);
        
        assertEquals("Should retrieve txt filter", txtFilter, factory.getFilterByFileName("file.txt"));
        assertEquals("Should retrieve pdf filter", pdfFilter, factory.getFilterByFileName("file.pdf"));
        assertEquals("Should retrieve doc filter", docFilter, factory.getFilterByFileName("file.doc"));
        assertEquals("Should retrieve doc filter for docx", docFilter, factory.getFilterByFileName("file.docx"));
    }

    @Test
    public void testNonExistentExtension() {
        TextFilter mockFilter = new MockTextFilter(new String[]{"txt"});
        factory.registerFilter(mockFilter);
        
        TextFilter retrievedFilter = factory.getFilterByFileName("file.xyz");
        assertNull("Should return null for non-registered extension", retrievedFilter);
    }

    @Test
    public void testNoExtension() {
        TextFilter mockFilter = new MockTextFilter(new String[]{"txt"});
        factory.registerFilter(mockFilter);
        
        TextFilter retrievedFilter = factory.getFilterByFileName("fileWithoutExtension");
        assertNull("Should return null for file without extension", retrievedFilter);
    }

    @Test
    public void testExtensionsList() {
        TextFilter filter1 = new MockTextFilter(new String[]{"txt", "text"});
        TextFilter filter2 = new MockTextFilter(new String[]{"pdf"});
        
        factory.registerFilter(filter1);
        factory.registerFilter(filter2);
        
        List extensions = factory.extensions();
        assertNotNull("Extensions list should not be null", extensions);
        assertEquals("Should have 3 extensions registered", 3, extensions.size());
        assertTrue("Should contain txt extension", extensions.contains("txt"));
        assertTrue("Should contain text extension", extensions.contains("text"));
        assertTrue("Should contain pdf extension", extensions.contains("pdf"));
    }

    @Test
    public void testInitFactory() {
        List<TextFilter> filters = new ArrayList<>();
        filters.add(new MockTextFilter(new String[]{"txt"}));
        filters.add(new MockTextFilter(new String[]{"pdf"}));
        
        FilterFactory.initFactory(filters);
        FilterFactory retrievedFactory = FilterFactory.getFactory();
        
        assertNotNull("Factory should be initialized", retrievedFactory);
        assertNotNull("Should retrieve txt filter", retrievedFactory.getFilterByFileName("test.txt"));
        assertNotNull("Should retrieve pdf filter", retrievedFactory.getFilterByFileName("test.pdf"));
    }

    /**
     * Mock implementation of TextFilter for testing purposes.
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
            // Mock implementation - just return the output file
            return out;
        }
    }
}
