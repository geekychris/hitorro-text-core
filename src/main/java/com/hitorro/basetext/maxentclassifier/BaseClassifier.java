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
package com.hitorro.basetext.maxentclassifier;

import com.hitorro.basetext.classifier.GeneratorInterface;
import com.hitorro.language.Iso639Table;
import com.hitorro.language.IsoLanguage;
import com.hitorro.util.core.events.cache.PoolContainer;
import com.hitorro.util.core.events.cache.PooledObjectIntf;
import com.hitorro.util.core.string.Fmt;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.ml.AbstractTrainer;
import opennlp.tools.ml.maxent.GISTrainer;
import opennlp.tools.ml.model.AbstractDataIndexer;
import opennlp.tools.ml.model.AbstractModel;
import opennlp.tools.ml.model.TwoPassDataIndexer;
import opennlp.tools.ml.model.UniformPrior;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.util.TrainingParameters;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public abstract class BaseClassifier<T extends Enum> implements PooledObjectIntf<IsoLanguage> {
    protected DoccatModel model;
    protected double[] probs;
    protected GeneratorInterface atcg;
    protected Map<String, String> shortToLongName = new HashMap();
    protected IsoLanguage lang;
    protected String name;
    private int id;
    private PoolContainer pc;

    public BaseClassifier(DoccatModel model,
                          GeneratorInterface atcg,
                          IsoLanguage lang,
                          String name,
                          String shortNames[], String longNames[]) {
        if (model == null) {
            probs = new double[0];
        } else {
            probs = new double[model.getMaxentModel().getNumOutcomes()];
        }
        this.model = model;
        this.atcg = atcg;
        this.name = name;
        this.lang = lang;

        for (int i = 0; i < shortNames.length; i++) {
            shortToLongName.put(shortNames[i], longNames[i]);
        }
    }

    public static void train(File trainingFile, File outFile, File modelsDir, GeneratorInterface atcg, IsoLanguage lang) throws IOException {

        Parser parser = getParser(modelsDir);

        // new
        TrainingParameters trainingParameters = new TrainingParameters();
        //trainingParameters.put(AbstractTrainer.CUTOFF_PARAM, 1);
        trainingParameters.put(AbstractDataIndexer.SORT_PARAM, false);
        TwoPassDataIndexer indexer = new TwoPassDataIndexer();
        indexer.init(trainingParameters, new HashMap<>());

        BaseEventStream es = new BaseEventStream(trainingFile.getAbsolutePath(), atcg, parser);

        indexer.index(es);
        AbstractModel model =
                new GISTrainer().trainModel(100,
                        indexer,
                        new UniformPrior(), 1);

        Map<String, String> manifestInfoEntries = new HashMap<>();
        new DoccatModel("en", model, manifestInfoEntries, new DoccatFactory()).serialize(new FileOutputStream(outFile));
    }

    private static Parser getParser(final File modelsDir) throws IOException {
        InputStream chunkerStream = new FileInputStream(
                new File(modelsDir, "en-chunker.bin"));
        ChunkerModel chunkerModel = new ChunkerModel(chunkerStream);
        ChunkerME chunker = new ChunkerME(chunkerModel);
        InputStream posStream = new FileInputStream(
                new File(modelsDir, "en-pos-maxent.bin"));
        POSModel posModel = new POSModel(posStream);
        POSTaggerME tagger = new POSTaggerME(posModel);
        return new ChunkParser(chunker, tagger);
    }

    public String getLongNameFromShort(String shortName) {
        return shortToLongName.get(shortName);
    }

    public String getClassification(Parse question) {
        double[] probs = computeAnswerTypeProbs(question);
        return model.getMaxentModel().getBestOutcome(probs);
    }

    public double[] computeAnswerTypeProbs(Parse question) {
        String[] context = atcg.getContext(question);
        return model.getMaxentModel().eval(context, probs);
    }

    public void trainDefault(File trainFile) throws IOException {
        File modelsDir = IsoLanguage.OpenNLPRootPath.apply();
        File outFile = new File(modelsDir, getModelName());
        train(trainFile, outFile, modelsDir, atcg, Iso639Table.english);
    }

    public String getModelName() {
        return Fmt.S("%s-answertype.bin", lang.getTwo());
    }

    @Override
    public int getGenerationId() {
        return id;
    }

    @Override
    public void setGenerationId(final int id) {
        this.id = id;
    }

    @Override
    public void passivate() {

    }

    @Override
    public void activate() {

    }

    @Override
    public void setPoolContainer(final PoolContainer pc) {
        this.pc = pc;
    }

    @Override
    public void close() {

    }

}
