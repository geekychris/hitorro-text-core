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
import com.hitorro.language.HTJWNLDictionary;
import opennlp.tools.parser.Parse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 *
 */
public abstract class AbstractQuestionContextGenerator implements GeneratorInterface {
    protected static final Pattern falseHeadsPattern = Pattern.compile("^(name|type|kind|sort|form|one|breed|names|variety)$");
    protected static final Pattern copulaPattern = Pattern.compile("^(is|are|'s|were|was|will)$");
    protected static final Pattern queryWordPattern = Pattern.compile("^(who|what|when|where|why|how|whom|which|name)$");
    protected static final Pattern useFocusNounPattern = Pattern.compile("^(who|what|which|name)$");
    protected static final Pattern howModifierTagPattern = Pattern.compile("^(JJ|RB)");
    protected HTJWNLDictionary dict = HTJWNLDictionary.me.get();

    public abstract String[] getContext(Parse query);

    protected boolean isQueryWord(String word) {
        return (queryWordPattern.matcher(word).matches());
    }

    protected int movePastPrep(int i, Parse[] toks) {
        if (i < toks.length && (toks[i].toString().equals("of") || toks[i].toString().equals("for"))) {
            i++;
        }
        return (i);
    }

    protected int movePastOf(int i, Parse[] toks) {
        if (i < toks.length && toks[i].toString().equals("of")) {
            i++;
        }
        return (i);
    }

    protected int movePastCopula(int i, Parse[] toks) {
        if (i < toks.length && toks[i].getType().startsWith("V")) {
            if (copulaPattern.matcher(toks[i].toString()).matches()) {
                i++;
            }
        }
        return (i);
    }


    protected Parse[] getNounPhrases(Parse parse) {
        List<Parse> nps = new ArrayList<Parse>(10);
        List<Parse> parts = new ArrayList<Parse>();
        parts.add(parse);
        while (parts.size() > 0) {
            List<Parse> newParts = new ArrayList<Parse>();
            for (int pi = 0, pn = parts.size(); pi < pn; pi++) {
                Parse cp = parts.get(pi);
                if (cp.getType().equals("NP") && cp.isFlat()) {
                    nps.add(cp);
                } else if (!cp.isPosTag()) {
                    newParts.addAll(Arrays.asList(cp.getChildren()));
                }
            }
            parts = newParts;
        }
        return nps.toArray(new Parse[nps.size()]);
    }

    protected Parse getContainingNounPhrase(Parse token) {
        Parse parent = token.getParent();
        if (parent.getType().equals("NP")) {
            return parent;
        }
        return null;
    }

    protected int getTokenIndexFollowingPhrase(Parse p, Parse[] toks) {
        Parse[] ptok = p.getTagNodes();
        Parse lastToken = ptok[ptok.length - 1];
        for (int ti = 0, tl = toks.length; ti < tl; ti++) {
            if (toks[ti] == lastToken) {
                return (ti + 1);
            }
        }
        return (toks.length);
    }


    protected Parse findFocusNounPhrase(String queryWord, int qwi, Parse[] toks) {
        if (queryWord.equals("who")) {
            int npStart = movePastCopula(qwi + 1, toks);
            if (npStart >= toks.length) {
                return null;
            }
            if (npStart > qwi + 1) { // check to ensure there is a copula
                Parse np = getContainingNounPhrase(toks[npStart]);
                if (np != null) {
                    return (np);
                }
            }
        } else if (queryWord.equals("what")) {
            int npStart = movePastCopula(qwi + 1, toks);
            if (npStart >= toks.length) {
                return null;
            }
            Parse np = getContainingNounPhrase(toks[npStart]);
            //removed copula case
            if (np != null) {
                Parse head = np.getHead();
                if (falseHeadsPattern.matcher(head.toString()).matches()) {
                    npStart += np.getChildren().length;
                    int np2Start = movePastPrep(npStart, toks);
                    if (np2Start > npStart) {
                        Parse snp = getContainingNounPhrase(toks[np2Start]);
                        if (snp != null) {
                            return (snp);
                        }
                    }
                }
                return (np);
            }
        } else if (queryWord.equals("which")) {
            //check for false query words like which VBD
            int npStart = movePastCopula(qwi + 1, toks);
            if (npStart >= toks.length) {
                return null;
            }
            if (npStart > qwi + 1) {
                return (getContainingNounPhrase(toks[npStart]));
            } else {
                npStart = movePastOf(qwi + 1, toks);
                return (getContainingNounPhrase(toks[npStart]));
            }
        } else if (queryWord.equals("how")) {
            if (qwi + 1 < toks.length) {
                return (getContainingNounPhrase(toks[qwi + 1]));
            }
        } else if (qwi == 0 && queryWord.equals("name")) {
            int npStart = qwi + 1;
            if (npStart >= toks.length) {
                return null;
            }
            Parse np = getContainingNounPhrase(toks[npStart]);
            if (np != null) {
                Parse head = np.getHead();
                if (falseHeadsPattern.matcher(head.toString()).matches()) {
                    npStart += np.getChildren().length;
                    int np2Start = movePastPrep(npStart, toks);
                    if (np2Start > npStart) {
                        Parse snp = getContainingNounPhrase(toks[np2Start]);
                        if (snp != null) {
                            return (snp);
                        }
                    }
                }
                return (np);
            }
        }
        return (null);
    }

    public String[] getLemmas(Parse np) {
        String word = np.getHead().toString().toLowerCase();

        return dict.getLemmas(word, "NN");
    }

    protected void generateWordNetFeatures(Parse focusNoun, List<String> features) {

        Parse[] toks = focusNoun.getTagNodes();
        if (toks[toks.length - 1].getType().startsWith("NNP")) {
            return;
        }
        //check wordnet
        Set<String> synsets = dict.getSynsetSet(focusNoun);

        for (String synset : synsets) {
            features.add("s=" + synset);
        }
    }

    protected void generateWordFeatures(Parse focusNoun, List<String> features) {
        Parse[] toks = focusNoun.getTagNodes();
        int nsi = 0;
        for (; nsi < toks.length - 1; nsi++) {
            features.add("mw=" + toks[nsi]);
            features.add("mt=" + toks[nsi].getType());
        }
        features.add("hw=" + toks[nsi]);
        features.add("ht=" + toks[nsi].getType());
    }


    protected void getQueryWordAndFocalNoun(Parse focalNoun, String queryWord, final List<String> features, final Parse[] nps, final Parse[] toks) {
        int fnEnd = 0;
        int i = 0;
        boolean fnIsLast = false;
        for (; i < toks.length; i++) {
            String tok = toks[i].toString().toLowerCase();
            if (isQueryWord(tok)) {
                queryWord = tok;
                focalNoun = findFocusNounPhrase(queryWord, i, toks);
                if (tok.equals("how") && i + 1 < toks.length) {
                    if (howModifierTagPattern.matcher(toks[i + 1].getType()).find()) {
                        queryWord = tok + "_" + toks[i + 1].toString();
                    }
                }
                if (focalNoun != null) {
                    fnEnd = getTokenIndexFollowingPhrase(focalNoun, toks);
                }
                if (focalNoun != null && focalNoun.equals(nps[nps.length - 1])) {
                    fnIsLast = true;
                }
                break;
            }
        }
        int ri = i + 1;
        if (focalNoun != null) {
            ri = fnEnd + 1;
        }
        for (; ri < toks.length; ri++) {
            features.add("rw=" + toks[ri].toString());
        }
        if (queryWord != null) {
            features.add("qw=" + queryWord);
            String verb = null;
            //skip first verb for some query words like how much
            for (int vi = i + 1; vi < toks.length; vi++) {
                String tag = toks[vi].getType();
                if (tag != null && tag.startsWith("V")) {
                    verb = toks[vi].toString();
                    break;
                }
            }
            if (focalNoun == null) {
                features.add("qw_verb=" + queryWord + "_" + verb);
                features.add("verb=" + verb);
                features.add("fn=null");
            } else if (useFocusNounPattern.matcher(queryWord).matches()) {
                generateWordFeatures(focalNoun, features);
                generateWordNetFeatures(focalNoun, features);
            }
            if (fnIsLast) {
                features.add("fnIsLast=" + fnIsLast);
            }
        }
    }


}
