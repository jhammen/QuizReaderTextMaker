/*
 This file is part of QuizReader.

 QuizReader is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 QuizReader is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with QuizReader.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.quizreader.textmaker.uima;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import opennlp.uima.Token;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;
import org.quizreader.textmaker.dictionary.model.Definition;
import org.quizreader.textmaker.dictionary.model.Entry;
import org.quizreader.textmaker.uima.types.DefinitionAnnotation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * creates a wordlist from a text + simple list, wordlist is sorted by appearance in the text
 *
 */
public class AppearanceSorter extends JCasAnnotator_ImplBase {

	private DictionaryResource wiktionary;
	private ObjectMapper objectMapper = new ObjectMapper();
	private static final String CONFIG_PARAM_INPUT_LIST = "inputList";
	private final Set<String> wordSet = new HashSet<String>();
	final Map<String, Integer> appearanceMap = new HashMap<String, Integer>();

	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		try {
			wiktionary = (DictionaryResource) getContext().getResourceObject("Wiktionary");
			String inputListPath = (String) aContext.getConfigParameterValue(CONFIG_PARAM_INPUT_LIST);
			// FileReader fr = new FileReader(inputListPath);
			FileReader fr = new FileReader("./samplelist.txt");
			Scanner scan = new Scanner(fr);
			scan.useDelimiter(System.lineSeparator());
			while (scan.hasNext()) {
				wordSet.add(scan.next());
			}
			scan.close();
			fr.close();
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
	}

	public void process(JCas aJCas) throws AnalysisEngineProcessException {

		Logger logger = getContext().getLogger();
		AnnotationIndex<Annotation> tokenIndex = aJCas.getAnnotationIndex(Token.type);
		if (tokenIndex.size() == 0) {
			logger.log(Level.SEVERE, "No token annotations found! Was the document tokenised?");
			return;
		}

		AnnotationIndex<Annotation> defIndex = aJCas.getAnnotationIndex(DefinitionAnnotation.type);
		if (defIndex.size() == 0) {
			logger.log(Level.SEVERE, "No definition annotations found! Was the document tokenised?");
			return;
		}

		// reset map
		appearanceMap.clear();

		// loop tokens for direct appearances
		for (Annotation tok : tokenIndex) {
			Token anno = (Token) tok;
			String word = anno.getCoveredText();
			if (wordSet.contains(word)) {
				addAppearance(word, anno.getBegin());
			}
		}

		// loop definitions
		for (Annotation anno : defIndex) {
			DefinitionAnnotation defAnno = (DefinitionAnnotation) anno;
			String word = defAnno.getWord() == null ? defAnno.getCoveredText() : defAnno.getWord();
			if (wordSet.contains(word)) {
				addAppearance(word, anno.getBegin());
			}
			else {
				Entry entry = wiktionary.getEntry(word);
				if (entry != null) {
					boolean found = false;
					for (Definition definition : entry.getDefinitions()) {
						final String root = definition.getRoot();
						if (wordSet.contains(root)) {
							addAppearance(root, anno.getBegin());
							System.out.println("!found word via def: " + anno.getCoveredText() + "->" + root);
							found = true;
						}
					}
					if (!found) {
						System.out.println("not found: " + anno.getCoveredText());
					}
				}
			}
		}

		// sort
		List<String> keys = new ArrayList<String>(appearanceMap.keySet());
		Collections.sort(keys, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return appearanceMap.get(o1) - appearanceMap.get(o2);
			}
		});

		// print
		System.out.println("[");
		for (String key : keys) {
			System.out.println("\"" + key + "\",");
		}
		System.out.println("]");

		// show missing
		int missing = 0;
		for (String word : wordSet) {
			if (appearanceMap.get(word) == null) {
				System.err.println("missing: " + word + "[" + ++missing + "]");
			}
		}
	}

	private void addAppearance(String word, int location) {
		final Integer existing = appearanceMap.get(word);
		if (existing == null || existing > location) {
			appearanceMap.put(word, location);
		}
	}
}
