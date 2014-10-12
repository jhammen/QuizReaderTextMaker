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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.TokenAnnotation;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;
import org.quizreader.textmaker.uima.types.DefinitionAnnotation;
import org.quizreader.textmaker.wiktionary.model.Definition;
import org.quizreader.textmaker.wiktionary.model.Entry;

public class DefinitionAnnotator extends JCasAnnotator_ImplBase {

	private WiktionaryResource wiktionary;
	
	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
		try {
			wiktionary = (WiktionaryResource) getContext().getResourceObject("Wiktionary");
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
	}

	private boolean isLowercaseWord(String word) {
		return word.matches("[a-zà-ÿœ]+");
	}

	public void process(JCas aJCas) throws AnalysisEngineProcessException {

		Logger logger = getContext().getLogger();
		// String docText = aJCas.getDocumentText();
		AnnotationIndex<Annotation> tokenIndex = aJCas.getAnnotationIndex(TokenAnnotation.type);
		if (tokenIndex.size() == 0) {
			logger.log(Level.SEVERE, "No token annotations found! Was the document tokenised?");
			System.err.println("No token annotations found! Was the document tokenised?");
			return;
		}

		Map<String, Integer> missingWords = new HashMap<String, Integer>();

		for (Annotation tok : tokenIndex) {
			// TokenAnnotation anno = (TokenAnnotation) tok;
			String word = tok.getCoveredText();

			// skip punctuation
			if (word.matches("\\p{Punct}+") || word.matches("[\r\n]+")) {
				continue;
			}

			// attempt straight lookup
			Entry entry = wiktionary.getEntry(word);

			// try lower case
			if (entry == null) { // && startWords.contains(tok.getBegin())) {
				entry = wiktionary.getEntry(word.toLowerCase());
			}
			
			// if ALL CAPS -> try First Letter Capitalized
			if(entry == null && word.equals(word.toUpperCase())) {
				entry = wiktionary.getEntry(word.charAt(0) + word.toLowerCase().substring(1));				
			}
			
			// if hyphenated, try each side
			if(entry == null && word.contains("-")) {
				// TODO: try each side
			}

			// take note of missing words
			if (entry == null && isLowercaseWord(word)) {
				Integer count = missingWords.get(word);
				missingWords.put(word, count == null ? 1 : count + 1);
				entry = new Entry(); // blank entry
			}

			if (entry != null) {
				DefinitionAnnotation defAnno = new DefinitionAnnotation(aJCas);
				defAnno.setBegin(tok.getBegin());
				defAnno.setEnd(tok.getEnd());

				if (entry != null) {
					// is it the same word?
					if(!word.equals(entry.getWord())) {
						System.out.println("mapping " + word + " -> " + entry.getWord());
						defAnno.setWord(entry.getWord());
					}
					// add entry definitions to annotation
					List<Definition> definitions = entry.getDefinitions();
					if (definitions != null && definitions.size() > 0) {
						defAnno.setExcerpt(definitions.get(0).getText());
					}
				}
				defAnno.addToIndexes();
			}
		}

		List<String> missing = new ArrayList<String>(missingWords.keySet());
		Collections.sort(missing);
		for (String word : missing) {
			if (missingWords.get(word) > 1) {
				System.out.println("missing: " + word + "\t" + missingWords.get(word));
			}
		}
	}
}
