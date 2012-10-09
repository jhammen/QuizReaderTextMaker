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
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.tika.MarkupAnnotation;
import org.quizreader.textmaker.uima.types.OutputFileAnnotation;

public class OutputFileAnnotator extends JCasAnnotator_ImplBase {

	private int fileCounter;

	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
		fileCounter = 1;
	}

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		// Logger logger = getContext().getLogger();

		OutputFileAnnotation sectionAnno = null;
		AnnotationIndex<Annotation> markupIndex = aJCas.getAnnotationIndex(MarkupAnnotation.type);
		// create break list
		for (Annotation anno : markupIndex) {
			MarkupAnnotation markup = (MarkupAnnotation) anno;
			if ("h2".equalsIgnoreCase(markup.getName())) { // TODO: parameterize split tag
				if (sectionAnno != null) {
					sectionAnno.setEnd(anno.getBegin() - 1);
					sectionAnno.addToIndexes();
				}
				sectionAnno = new OutputFileAnnotation(aJCas);
				sectionAnno.setBegin(anno.getBegin());
				sectionAnno.setFilePath("part" + fileCounter++ + ".html");
			}
		}
		if (sectionAnno != null) {
			DocumentAnnotation docAnno = (DocumentAnnotation) aJCas.getDocumentAnnotationFs();
			sectionAnno.setEnd(docAnno.getEnd());
			sectionAnno.addToIndexes();
		}
	}

}
