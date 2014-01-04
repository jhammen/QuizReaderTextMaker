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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.SentenceAnnotation;
import org.apache.uima.TokenAnnotation;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;
import org.quizreader.textmaker.uima.types.DefinitionAnnotation;
import org.quizreader.textmaker.wiktionary.Definition;
import org.quizreader.textmaker.wiktionary.Entry;
import org.quizreader.textmaker.wiktionary.WiktionaryManager;

public class DefinitionAnnotator extends JCasAnnotator_ImplBase {

	private static final String WIKTIONARY_XML_PATH_KEY = "wiktionaryXml";

	WiktionaryManager wiktionary = new WiktionaryManager();

	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
		try {
			String resourceFilePath = (String) aContext.getConfigParameterValue(WIKTIONARY_XML_PATH_KEY);
			wiktionary.loadXML(resourceFilePath);
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
	}

	private boolean isWord(String word) {
		return word.matches("[a-zA-ZÀ-ÿœ]+");
	}

	public void process(JCas aJCas) {

		Logger logger = getContext().getLogger();
		// String docText = aJCas.getDocumentText();
		AnnotationIndex<Annotation> tokenIndex = aJCas.getAnnotationIndex(TokenAnnotation.type);
		if (tokenIndex.size() == 0) {
			logger.log(Level.SEVERE, "No token annotations found! Was the document tokenised?");
			System.err.println("No token annotations found! Was the document tokenised?");
			return;
		}

		// map sentences by their start location
		AnnotationIndex<Annotation> sentenceIndex = aJCas.getAnnotationIndex(SentenceAnnotation.type);
		// Map<Integer, Annotation> sentenceStart = new HashMap<Integer, Annotation>();
		Set<Integer> sentenceStarts = new HashSet<Integer>();
		for (Annotation sentence : sentenceIndex) {
			FSIterator<Annotation> subiterator = tokenIndex.subiterator(sentence);
			if (subiterator.hasNext()) {
				Annotation tok = subiterator.next();
				while (subiterator.hasNext() && !isWord(tok.getCoveredText())) {
					tok = subiterator.next();
				}
				sentenceStarts.add(tok.getBegin());
			}
		}

		Map<String, Integer> missingWords = new HashMap<String, Integer>();

		for (Annotation tok : tokenIndex) {

			// TokenAnnotation anno = (TokenAnnotation) tok;
			String word = tok.getCoveredText();

			// attempt straight lookup
			Entry entry = wiktionary.getEntry(word);

			// sentence beginning: try lower case
			if (entry == null && sentenceStarts.contains(tok.getBegin())) {
				entry = wiktionary.getEntry(word.toLowerCase());
			}

			// take note of missing words
			if (entry == null && isWord(word)) {
				Integer count = missingWords.get(word);
				missingWords.put(word, count == null ? 1 : count + 1);
			}

			if (entry != null) {
				DefinitionAnnotation defAnno = new DefinitionAnnotation(aJCas);
				defAnno.setBegin(tok.getBegin());
				defAnno.setEnd(tok.getEnd());

				if (entry != null) {
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
