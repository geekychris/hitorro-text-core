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
package com.hitorro.analysis.brat;

import com.hitorro.util.core.error.Errors;
import com.hitorro.util.core.string.Fmt;
import opennlp.tools.cmdline.*;
import opennlp.tools.cmdline.chunker.*;
import opennlp.tools.cmdline.dictionary.DictionaryBuilderTool;
import opennlp.tools.cmdline.doccat.*;
import opennlp.tools.cmdline.entitylinker.EntityLinkerTool;
import opennlp.tools.cmdline.langdetect.*;
import opennlp.tools.cmdline.languagemodel.NGramLanguageModelTool;
import opennlp.tools.cmdline.lemmatizer.LemmatizerEvaluatorTool;
import opennlp.tools.cmdline.lemmatizer.LemmatizerMETool;
import opennlp.tools.cmdline.lemmatizer.LemmatizerTrainerTool;
import opennlp.tools.cmdline.namefind.*;
import opennlp.tools.cmdline.parser.*;
import opennlp.tools.cmdline.postag.POSTaggerConverterTool;
import opennlp.tools.cmdline.postag.POSTaggerCrossValidatorTool;
import opennlp.tools.cmdline.postag.POSTaggerEvaluatorTool;
import opennlp.tools.cmdline.postag.POSTaggerTrainerTool;
import opennlp.tools.cmdline.sentdetect.*;
import opennlp.tools.cmdline.tokenizer.*;

import java.io.File;
import java.util.*;

import static com.hitorro.language.IsoLanguage.OpenNLPRootPath;

/**
 * https://stackoverflow.com/questions/39877434/creating-and-training-a-model-for-opennlp-using-brat
 * <p>
 * opennlp.tools.cmdline.CLI
 * <p>
 * TokenNameFinderTrainer.brat
 * -resources conf/resources
 * -featuregen /Users/i500845/src/brat-v1.3_Crunchy_Frog/data/t//en-foo.xml
 * -nameTypes ca_AttributeValue
 * -params /Users/i500845/src/brat-v1.3_Crunchy_Frog/data/t/TrainerParams.txt
 * -lang en
 * -model /Users/i500845/opennlp/en-foo.bin
 * -ruleBasedTokenizer simple
 * -annotationConfig /Users/i500845/src/brat-v1.3_Crunchy_Frog/data/t/annotation.conf
 * -bratDataDir /Users/i500845/src/brat-v1.3_Crunchy_Frog/data/t/
 * -recursive true
 * -sentenceDetectorModel /Users/i500845/opennlp/en-sent.bin
 */
public class OpenNLPBratTrainer {

    /*
    dir =
    attribute = "ca_AttributeValue"
    featureConfig = en-foo.xml
    lang = en
    modelName = en-foo
    modelsDir = <where opennlp dir>
     */
    public static String[] get(String dir, String attribute, String featureConfig,
                               String lang, String modelName, String modelsDir) {
        ArrayList<String> list = new ArrayList<>();

        list.add("TokenNameFinderTrainer.brat");
        ///Users/i500845/src/brat-v1.3_Crunchy_Frog/data/t//en-foo.xml
        list.add("-featuregen");
        list.add(Fmt.S("%s/%s", dir, featureConfig));

        list.add("-nameTypes");
        list.add(Fmt.S("%s", attribute));

        list.add("-params");
        list.add(Fmt.S("%s/TrainerParams.txt", dir));

        list.add("-lang");
        list.add(Fmt.S("%s", lang));

        list.add("-model");
        list.add(Fmt.S("%s/%s", dir, modelName));

        list.add("-ruleBasedTokenizer");
        list.add(Fmt.S("simple"));

        list.add("-annotationConfig");
        list.add(Fmt.S("%s/annotation.conf", dir));

        list.add("-bratDataDir");
        list.add(Fmt.S("%s", dir));

        list.add("-recursive");
        list.add(Fmt.S("true"));

        list.add("-bratDataDir");
        list.add(Fmt.S("%s/%s-sent.bin", modelsDir, lang));

        return list.toArray(new String[list.size()]);
    }

    public static void tst(String argsIn[]) {
        File f = OpenNLPRootPath.apply();
        String modelsDir = f.getAbsolutePath();
        String dataDir = "/Users/chris/1811results";
        String attribute = "ca_AttributeValue";
        String featureConfig = "en-foo.xml";
        String lang = "en";
        String modelName = "foo";
        String args[] = get(dataDir, attribute, featureConfig, lang, modelName, modelsDir);

        Errors errors = new Errors();
        int returnCode = HTCLI.exec(args, errors);
    }

}

/**
 * copy of CLI so that we can avoid the exit codes if fails to parse
 */
class HTCLI {

    public static final String CMD = "opennlp";

    private static Map<String, CmdLineTool> toolLookupMap;

    static {
        toolLookupMap = new LinkedHashMap<>();

        List<CmdLineTool> tools = new LinkedList<>();

        // Document Categorizer
        tools.add(new DoccatTool());
        tools.add(new DoccatTrainerTool());
        tools.add(new DoccatEvaluatorTool());
        tools.add(new DoccatCrossValidatorTool());
        tools.add(new DoccatConverterTool());

        // Language Detector
        tools.add(new LanguageDetectorTool());
        tools.add(new LanguageDetectorTrainerTool());
        tools.add(new LanguageDetectorConverterTool());
        tools.add(new LanguageDetectorCrossValidatorTool());
        tools.add(new LanguageDetectorEvaluatorTool());

        // Dictionary Builder
        tools.add(new DictionaryBuilderTool());

        // Tokenizer
        tools.add(new SimpleTokenizerTool());
        tools.add(new TokenizerMETool());
        tools.add(new TokenizerTrainerTool());
        tools.add(new TokenizerMEEvaluatorTool());
        tools.add(new TokenizerCrossValidatorTool());
        tools.add(new TokenizerConverterTool());
        tools.add(new DictionaryDetokenizerTool());

        // Sentence detector
        tools.add(new SentenceDetectorTool());
        tools.add(new SentenceDetectorTrainerTool());
        tools.add(new SentenceDetectorEvaluatorTool());
        tools.add(new SentenceDetectorCrossValidatorTool());
        tools.add(new SentenceDetectorConverterTool());

        // Name Finder
        tools.add(new TokenNameFinderTool());
        tools.add(new TokenNameFinderTrainerTool());
        tools.add(new TokenNameFinderEvaluatorTool());
        tools.add(new TokenNameFinderCrossValidatorTool());
        tools.add(new TokenNameFinderConverterTool());
        tools.add(new CensusDictionaryCreatorTool());


        // POS Tagger
        tools.add(new opennlp.tools.cmdline.postag.POSTaggerTool());
        tools.add(new POSTaggerTrainerTool());
        tools.add(new POSTaggerEvaluatorTool());
        tools.add(new POSTaggerCrossValidatorTool());
        tools.add(new POSTaggerConverterTool());

        //Lemmatizer
        tools.add(new LemmatizerMETool());
        tools.add(new LemmatizerTrainerTool());
        tools.add(new LemmatizerEvaluatorTool());

        // Chunker
        tools.add(new ChunkerMETool());
        tools.add(new ChunkerTrainerTool());
        tools.add(new ChunkerEvaluatorTool());
        tools.add(new ChunkerCrossValidatorTool());
        tools.add(new ChunkerConverterTool());

        // Parser
        tools.add(new ParserTool());
        tools.add(new ParserTrainerTool()); // trains everything
        tools.add(new ParserEvaluatorTool());
        tools.add(new ParserConverterTool()); // trains everything
        tools.add(new BuildModelUpdaterTool()); // re-trains  build model
        tools.add(new CheckModelUpdaterTool()); // re-trains  build model
        tools.add(new TaggerModelReplacerTool());

        // Entity Linker
        tools.add(new EntityLinkerTool());

        // Language Model
        tools.add(new NGramLanguageModelTool());

        for (CmdLineTool tool : tools) {
            toolLookupMap.put(tool.getName(), tool);
        }

        toolLookupMap = Collections.unmodifiableMap(toolLookupMap);
    }

    /**
     * @return a set which contains all tool names
     */
    public static Set<String> getToolNames() {
        return toolLookupMap.keySet();
    }

    /**
     * @return a read only map with tool names and instances
     */
    public static Map<String, CmdLineTool> getToolLookupMap() {
        return toolLookupMap;
    }

    public static int exec(String[] args, Errors errors) {

        if (args.length == 0) {
            return 0;
        }

        final long startTime = System.currentTimeMillis();
        String[] toolArguments = new String[args.length - 1];
        System.arraycopy(args, 1, toolArguments, 0, toolArguments.length);

        String toolName = args[0];

        //check for format
        String formatName = StreamFactoryRegistry.DEFAULT_FORMAT;
        int idx = toolName.indexOf(".");
        if (-1 < idx) {
            formatName = toolName.substring(idx + 1);
            toolName = toolName.substring(0, idx);
        }
        CmdLineTool tool = toolLookupMap.get(toolName);

        try {
            if (null == tool) {
                throw new TerminateToolException(1, "Tool " + toolName + " is not found.");
            }

            if ((0 == toolArguments.length && tool.hasParams()) ||
                    0 < toolArguments.length && "help".equals(toolArguments[0])) {
                if (tool instanceof TypedCmdLineTool) {
                    System.out.println(((TypedCmdLineTool<?,?>) tool).getHelp(formatName));
                } else if (tool instanceof BasicCmdLineTool) {
                    System.out.println(tool.getHelp());
                }

                return 0;
            }

            if (tool instanceof TypedCmdLineTool) {
                ((TypedCmdLineTool<?,?>) tool).run(formatName, toolArguments);
            } else if (tool instanceof BasicCmdLineTool) {
                if (-1 == idx) {
                    ((BasicCmdLineTool) tool).run(toolArguments);
                } else {
                    throw new TerminateToolException(1, "Tool " + toolName + " does not support formats.");
                }
            } else {
                throw new TerminateToolException(1, "Tool " + toolName + " is not supported.");
            }
        } catch (TerminateToolException e) {

            if (e.getMessage() != null) {
                System.err.println(e.getMessage());
            }

            if (e.getCause() != null) {
                System.err.println(e.getCause().getMessage());
                e.getCause().printStackTrace(System.err);
            }

            return e.getCode();
        }

        final long endTime = System.currentTimeMillis();
        System.err.format("Execution time: %.3f seconds\n", (endTime - startTime) / 1000.0);
        return 1;
    }
}

