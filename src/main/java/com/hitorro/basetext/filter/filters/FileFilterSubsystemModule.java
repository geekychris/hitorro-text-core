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
package com.hitorro.basetext.filter.filters;

import com.hitorro.basetext.filter.core.FilterFactory;
import com.hitorro.util.startupframework.phases.ServiceDefinition;

import java.util.List;
import java.util.Vector;

/**
 * Copyright (c) 2003 - present HiTorro All rights reserved. User: chris Date: Oct 5, 2003 Time: 6:25:58 PM To
 */

@ServiceDefinition(dependentService = {},
        shortName = "filefilter",
        description = "File filter service",
        debugCommands = {},
        typeManagedClasses = {},
        uiDirectories = {},
        dependentServiceInterfaces = {})
public class FileFilterSubsystemModule {
    public static final String ClassName = "FileFilterSubsystemModule";
    // parameters
    private static final String ModuleName = "FiltersSubsystemModule";

    /*
    Called to initialize susbsystem
    */
    private boolean m_valid = true;

    public boolean init() {
        List filters = new Vector();
        filters.add(new XlsTextFilter());
        filters.add(new WordTextFilter());
        filters.add(new PdfTextFilter());
        filters.add(new HtmlTextFilter());
        filters.add(new TextTextFilter());
        FilterFactory.initFactory(filters);
        return isValid();
    }

    /*
    Allow debugging info to be sent to the log per request of engineer
    */

    public String getName() {
        return ModuleName;
    }

    /*
    Called if the system is being orderly shutdown.
    */

    public void dumpDebugState() {
        // nothing yet, probabaly should dump the directory constants out.
    }

    public boolean deinit() {
        // nothing todo
        return true;
    }

    public boolean isValid() {
        return m_valid;
    }


    public String init(boolean dbInit, final boolean upgrading, final long currentVersion, final long targetVersion) {
        return null;
    }

    public String start(boolean dbInit) {
        return null;
    }

    public String deInit() {
        return null;
    }
}
