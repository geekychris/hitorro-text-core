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
package com.hitorro;//CS124 HW6 Wikipedia Relation Extraction
//Alan Joyce (ajoyce)

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Wiki {

    public static void main(String[] args) {
        String wikiFile = "../data/small-wiki.xml";
        String wivesFile = "../data/wives.txt";
        String goldFile = "../data/gold.txt";
        boolean useInfoBox = true;
        Wiki pedia = new Wiki();
        List<String> wives = pedia.addWives(wivesFile);
        List<String> husbands = pedia.processFile(new File(wikiFile), wives, useInfoBox);
        pedia.evaluateAnswers(useInfoBox, husbands, goldFile);
    }

    public List<String> addWives(String fileName) {
        List<String> wives = new ArrayList<String>();
        try {
            BufferedReader input = new BufferedReader(new FileReader(fileName));
            // for each line
            for (String line = input.readLine(); line != null; line = input.readLine()) {
                wives.add(line);
            }
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
        return wives;
    }

    /*
     * read through the wikipedia file and attempts to extract the matching husbands. note that you will need to provide
     * two different implementations based upon the useInfoBox flag.
     */
    public List<String> processFile(File f, List<String> wives, boolean useInfoBox) {

        List<String> husbands = new ArrayList<String>();

        Document dom = parse(f);
        ArrayList<String> nodes = new ArrayList<String>();
        getNodes("text", nodes, dom.getChildNodes());

        HashMap<String, List<String>> husbandsMap = new HashMap<String, List<String>>();
        for (String txt : nodes) {

            ArrayList<String> name = new ArrayList<String>();

            ArrayList<String> spouse = new ArrayList<String>();
            processMention(txt, name, spouse);
            if (name.size() == 0 || spouse.size() != 1) {
                System.out.println("oh no");
            } else {
                for (String wife : spouse) {
                    husbandsMap.put(wife, name);
                }
            }
            System.out.println();

        }
        dom.getChildNodes();
        //TODO:
        // Process the wiki file and fill the husbands Array
        // +1 for correct Answer, 0 for no answer, -1 for wrong answers
        // put 'No Answer' string as the answer when you dont want to answer
        //spouse of form [[Elizabeth Hadley Richardson]]
        // husband of form: name = Arnold xx
        // split on |
        for (String wife : wives) {
            List<String> hl = husbandsMap.get(wife);
            if (hl == null) {
                husbands.add("No Answer");
            } else {
                boolean first = true;
                StringBuilder sb = new StringBuilder();
                for (String h : hl) {
                    if (!first) {
                        sb.append("|");
                    }
                    first = false;

                    sb.append("Who is ");
                    sb.append(h);
                    sb.append("?");


                }

                husbands.add(sb.toString());
            }
        }
        return husbands;
    }

    private void processMention(String txt, ArrayList<String> name, ArrayList<String> spouse) {

        Pattern p = Pattern.compile("(\\[\\[[A-Za-z\\s]+\\]\\])");
        String parts[] = txt.split("\\n");

        for (String part : parts) {
            if (part.toLowerCase().startsWith("|name")) {
                part = part.substring("|name".length(), part.length());
                addPostEqs(name, part);
            } else if (part.toLowerCase().startsWith("|spouse")) {
                Matcher m = p.matcher(part);
                while (m.find()) {
                    int start = m.start();
                    int end = m.end();
                    String group = m.group();
                    String t = group.substring(2, group.length() - 2);
                    spouse.add(t);
                    System.out.println(t);
                }

            }
        }
    }

    private void addPostEqs(ArrayList<String> list, String txt) {
        int ind = txt.indexOf("=");
        String t = txt.substring(ind + 1).trim();
        list.add(t);
    }

    private void getNodes(String name, List<String> nodes, NodeList nodeList) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node n = nodeList.item(i);
            String nn = n.getNodeName();
            if (nn.equals(name)) {
                String txt = n.getTextContent();
                if (txt.contains("Infobox")) {
                    nodes.add(txt);
                }

            }
            if (n.hasChildNodes()) {
                getNodes(name, nodes, n.getChildNodes());
            }
        }

    }

    private Document parse(File f) {
        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();

            //parse using builder to get DOM representation of the XML file
            return db.parse(f);


        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    /*
     * scores the results based upon the aforementioned criteria
     */
    public void evaluateAnswers(boolean useInfoBox, List<String> husbandsLines, String goldFile) {
        int correct = 0;
        int wrong = 0;
        int noAnswers = 0;
        int score = 0;
        try {
            BufferedReader goldData = new BufferedReader(new FileReader(goldFile));
            List<String> goldLines = new ArrayList<String>();
            String line;
            while ((line = goldData.readLine()) != null) {
                goldLines.add(line);
            }
            if (goldLines.size() != husbandsLines.size()) {
                System.err.println("Number of lines in husbands file should be same as number of wives!");
                System.exit(1);
            }
            for (int i = 0; i < goldLines.size(); i++) {
                String husbandLine = husbandsLines.get(i).trim();
                String goldLine = goldLines.get(i).trim();
                boolean exampleWrong = true; // guilty until proven innocent
                if (husbandLine.equals("No Answer")) {
                    exampleWrong = false;
                    noAnswers++;
                } else { // check if correct.
                    String[] golds = goldLine.split("\\|");
                    for (String gold : golds) {
                        if (husbandLine.equals(gold)) {
                            correct++;
                            score++;
                            exampleWrong = false;
                            break;
                        }
                    }
                }
                if (exampleWrong) {
                    wrong++;
                    score--;
                }
            }
            goldData.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("Correct Answers: " + correct);
        System.out.println("No Answers     : " + noAnswers);
        System.out.println("Wrong Answers  : " + wrong);
        System.out.println("Total Score    : " + score);

    }

    /**
     */

}
