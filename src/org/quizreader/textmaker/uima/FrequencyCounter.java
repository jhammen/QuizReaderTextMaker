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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.quizreader.textmaker.wiktionary.model.Definition;
import org.quizreader.textmaker.wiktionary.model.Entry;

public class FrequencyCounter extends JCasAnnotator_ImplBase {

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

	private List<Definition> getDefinitions(String word, String pos) {
		final List<Definition> ret = new ArrayList<Definition>();
		final Entry entry = wiktionary.getEntry(word);
		if (entry != null && entry.getDefinitions() != null) {
			for (Definition def : entry.getDefinitions()) {
				if (def.getType().equalsIgnoreCase(pos)) {
					ret.add(def);
				}
				else {
					System.out.println(def.getType() + "!=" + pos);
				}
			}
		}
		return ret;
	}

	public void process(JCas aJCas) throws AnalysisEngineProcessException {

		Logger logger = getContext().getLogger();
		// String docText = aJCas.getDocumentText();
		AnnotationIndex<Annotation> tokenIndex = aJCas.getAnnotationIndex(Token.type);
		if (tokenIndex.size() == 0) {
			logger.log(Level.SEVERE, "No token annotations found! Was the document tokenised?");
			System.err.println("No token annotations found! Was the document tokenised?");
			return;
		}
		final Map<String, Integer> countMap = new HashMap<String, Integer>();
		for (Annotation tok : tokenIndex) {
			Token anno = (Token) tok;
			String word = anno.getCoveredText();

			// skip punctuation
			if (word.matches("\\p{Punct}+") || word.matches("[\r\n]+")) {
				continue;
			}

			// find word
			final String pos = anno.getPos();
			if (!word.equals(word.toLowerCase())) {
				if (!pos.equals("NOUN")) {
					System.out.println("converting " + pos + ": " + word + "->" + word.toLowerCase());
					word = word.toLowerCase();
				}
			}

			List<Definition> defList = getDefinitions(word, pos);

			String key = word + "\t" + pos;
			int count = countMap.get(key) != null ? countMap.get(key) : 0;
			countMap.put(key, count + 1);
		}
		List<String> keys = new ArrayList<String>(countMap.keySet());
		Collections.sort(keys, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return countMap.get(o2) - countMap.get(o1);
			}
		});
		for (String key : keys) {
			System.out.println(key + "\t" + countMap.get(key));
		}
	}

}
