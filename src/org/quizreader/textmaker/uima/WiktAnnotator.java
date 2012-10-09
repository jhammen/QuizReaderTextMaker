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

import org.apache.uima.TokenAnnotation;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;
import org.quizreader.textmaker.uima.types.WiktAnnotation;
import org.quizreader.textmaker.wiktionary.Entry;
import org.quizreader.textmaker.wiktionary.WiktionaryManager;

public class WiktAnnotator extends JCasAnnotator_ImplBase {

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

	public void process(JCas aJCas) {

		Logger logger = getContext().getLogger();
		// String docText = aJCas.getDocumentText();
		FSIndex<Annotation> tokenIndex = aJCas.getAnnotationIndex(TokenAnnotation.type);
		if (tokenIndex.size() == 0) {
			logger.log(Level.SEVERE, "No token annotations found! Was the document tokenised?");
			System.err.println("No token annotations found! Was the document tokenised?");
			return;
		}
		for (Annotation tok : tokenIndex) {
			// TokenAnnotation anno = (TokenAnnotation) tok;
			String coveredText = tok.getCoveredText();

			Entry entry = wiktionary.getEntry(coveredText);
			if (entry != null) {
				WiktAnnotation wikiAnno = new WiktAnnotation(aJCas);
				wikiAnno.setBegin(tok.getBegin());
				wikiAnno.setEnd(tok.getEnd());
				if (entry.getDefinitions() != null) {
					wikiAnno.setExcerpt(entry.getDefinitions().get(0).getText());
				}
				wikiAnno.addToIndexes();
			}
		}

	}
}
