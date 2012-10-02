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

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Logger;
import org.quizreader.textmaker.uima.types.ParagraphAnnotation;

public class ParagraphAnnotator extends JCasAnnotator_ImplBase {

	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
	}

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		Logger logger = getContext().getLogger();
		char[] txt = aJCas.getDocumentText().toCharArray();

		ParagraphAnnotation anno = null;

		int i = 1;
		while (i < txt.length) {
			if (txt[i] == '\r') {
				throw new AnalysisEngineProcessException(new IllegalArgumentException("'\r' character found in view: " + aJCas.getViewName()));
			}
			if (txt[i] == '\n' && txt[i - 1] == '\n') {
				anno.setEnd(i - 2);
				anno.addToIndexes();
				anno = null;
			}
			else if (anno == null && !Character.isWhitespace(txt[i])) {
				anno = new ParagraphAnnotation(aJCas);
				anno.setBegin(i);
			}
			i++;
		}
		// wrap up last anno
		if (anno != null) {
			i--;
			while (Character.isWhitespace(txt[i])) {
				i--;
			}
			anno.setEnd(i);
			anno.addToIndexes();
		}
	}
}
