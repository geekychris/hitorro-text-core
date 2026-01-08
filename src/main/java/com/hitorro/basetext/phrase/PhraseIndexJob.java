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
package com.hitorro.basetext.phrase;

import com.hitorro.obj.core.Log;
import com.hitorro.util.job.Job;
import com.hitorro.util.job.JobExecutionResult;
import com.hitorro.util.job.JobParameters;
import org.apache.log4j.Level;

import java.io.IOException;


public class PhraseIndexJob extends Job {
    public static final String Name = "PhraseIndexJob";

    public String getName() {
        return Name;
    }

    public boolean needsSession() {
        return false;
    }

    public JobExecutionResult doAction(JobParameters parameters) {
        com.hitorro.basetext.phrase.PhraseIndexJobParameters p = null;
        if (parameters instanceof com.hitorro.basetext.phrase.PhraseIndexJobParameters) {
            p = (PhraseIndexJobParameters) parameters;
        } else {
            return new JobExecutionResult(Level.ERROR, "PhraseIndexJob was not passed the appropriate job parameters, it was given %s", parameters.getClass());
        }
        try {
            boolean resultPhraseIndexCreate = PhraseUtilBasic.generatePhraseIndexFromPhrasesInResourceCache(p.getMinFrequency(),
                    p.getMaxFrequency(),
                    p.getPhraseDescription(),
                    p.getGeneratingQuery(),
                    p.isWriteTextRepresentation());
            return new JobExecutionResult(Level.INFO, "PhraseIndex generated");
        } catch (IOException e) {
            Log.util.error("Unable to generate index %s %e", e, e);
            return new JobExecutionResult(Level.ERROR, "Unable to generate phrase index %s %e", e, e);
        }

    }
}
