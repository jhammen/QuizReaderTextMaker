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

import java.util.HashSet;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.quizreader.textmaker.uima.types.HTMLAnnotation;
import org.quizreader.textmaker.uima.types.OutputFileAnnotation;

public class OutputFileAnnotator extends JCasAnnotator_ImplBase {

	private static final String CONFIG_PARAM_SPLIT_TAGS = "splitTags";
	private int fileCounter;
	private Set<String> splitTags;

	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
		splitTags = new HashSet<String>();
		for (String tag : (String[]) aContext.getConfigParameterValue(CONFIG_PARAM_SPLIT_TAGS)) {
			splitTags.add(tag.toLowerCase());
		}
		fileCounter = 1;
	}

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		// Logger logger = getContext().getLogger();

		OutputFileAnnotation sectionAnno = null;
		AnnotationIndex<Annotation> markupIndex = aJCas.getAnnotationIndex(HTMLAnnotation.type);
		// create break list
		for (Annotation anno : markupIndex) {
			HTMLAnnotation markup = (HTMLAnnotation) anno;
			if (splitTags.contains(markup.getName().toLowerCase())) {
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
			sectionAnno.setEnd(docAnno.getEnd() - 1);
			sectionAnno.addToIndexes();
		}
	}

}
