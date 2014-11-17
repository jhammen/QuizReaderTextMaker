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

import org.apache.uima.SentenceAnnotation;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.quizreader.textmaker.uima.types.HTMLAnnotation;

public class HTMLSentenceAnnotator extends JCasAnnotator_ImplBase {
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		AnnotationIndex<Annotation> markupIndex = jcas.getAnnotationIndex(HTMLAnnotation.type);
		for (Annotation anno : markupIndex) {
			HTMLAnnotation markupAnno = (HTMLAnnotation) anno;
			if("span".equalsIgnoreCase(markupAnno.getName())) {
				SentenceAnnotation sentenceAnno = new SentenceAnnotation(jcas);
				sentenceAnno.setBegin(markupAnno.getBegin());
				sentenceAnno.setEnd(markupAnno.getEnd());
				sentenceAnno.addToIndexes();
			}
		}
	}
}