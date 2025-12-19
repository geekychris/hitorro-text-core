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


import com.hitorro.basetext.filter.core.TextFilter;
import com.hitorro.util.core.Log;
import com.hitorro.util.core.string.StringUtil;
import com.hitorro.util.json.keys.StringProperty;

import java.io.File;
import java.io.IOException;

/**
 * Copyright (c) 2003 - present HiTorro All rights reserved. User: chris Date: Oct 5, 2003 Time: 8:32:19 PM To
 * <p/>
 * Description:
 */
public abstract class CommandLineTextFilter
        implements TextFilter {
    public static final StringProperty filterDir = new StringProperty("filters.directory", "", null);
    private String m_command;

    public CommandLineTextFilter() {
        setCommandInFilterDir(filterCommandName());
    }

    public abstract String filterCommandName();

    protected boolean execute(String command) {
        Runtime rt = Runtime.getRuntime();
        try {
            Process p = rt.exec(command);
            p.waitFor();
        } catch (IOException ioe) {
            Log.util.warn("IO Exception executing command %s", ioe);
            return false;
        } catch (InterruptedException ie) {

            Log.util.warn("Interrupted Exception executing command %s", ie);
            return false;
        }
        return true;
    }

    protected File execute(File in, File out) {
        StringBuffer sb = new StringBuffer();
        sb.append(m_command);
        sb.append(" ");
        sb.append(in.toString());
        sb.append(" ");
        sb.append(out.toString());

        if (execute(sb.toString())) {
            return out;
        } else {
            return null;
        }
    }

    protected void setCommandInFilterDir(String name) {
        String dir = filterDir.apply();
        setCommand(new File(StringUtil.strcat(dir, "/", name)));
    }

    protected void setCommand(File command) {

        if (command.exists()) {
            m_command = command.toString();
        } else {
            Log.util.warn("Command not found %s", command.toString());
        }
    }
}
