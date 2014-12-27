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

import javax.swing.JOptionPane;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.quizreader.textmaker.uima.types.FileAnnotation;
import org.quizreader.textmaker.uima.types.HTMLAnnotation;

public class OutputFileAnnotator extends JCasAnnotator_ImplBase {

	private static final String CONFIG_PARAM_SPLIT_TAGS = "splitTags";
	private static final String CONFIG_PARAM_PAGE_RANGE = "pageRange";
	private int fileCounter;
	private Set<String> splitTags;
	private int startPage;
	private int endPage;

	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
		fileCounter = 1;
		// split tags
		splitTags = new HashSet<String>();
		for (String tag : (String[]) aContext.getConfigParameterValue(CONFIG_PARAM_SPLIT_TAGS)) {
			splitTags.add(tag.toLowerCase());
		}
		// page range
		Integer pageRange = (Integer) aContext.getConfigParameterValue(CONFIG_PARAM_PAGE_RANGE);
		// pop up dialog if not set
		if (pageRange == null) {
			String input = JOptionPane.showInputDialog("Please enter page range:");
			pageRange = Integer.parseInt(input);
		}
		startPage = endPage = pageRange;
	}

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		// Logger logger = getContext().getLogger();

		FileAnnotation outputFileAnno = null;
		AnnotationIndex<Annotation> markupIndex = aJCas.getAnnotationIndex(HTMLAnnotation.type);
		// create break list
		for (Annotation anno : markupIndex) {
			HTMLAnnotation markup = (HTMLAnnotation) anno;
			if (splitTags.contains(markup.getName().toLowerCase())) {
				// close any open anno
				if (outputFileAnno != null) {
					outputFileAnno.setEnd(anno.getBegin() - 1);
					outputFileAnno.addToIndexes();
				}
				// start new output file anno
				outputFileAnno = new FileAnnotation(aJCas);
				outputFileAnno.setOutput(true);
				outputFileAnno.setBegin(anno.getBegin());
				String countSuffix = String.format("%03d", fileCounter);
				outputFileAnno.setFileName("t" + countSuffix + ".html");
				outputFileAnno.setWithinRange(fileCounter >= startPage && fileCounter <= endPage);
				fileCounter++;
			}
		}
		// finish the last open anno
		if (outputFileAnno != null) {
			DocumentAnnotation docAnno = (DocumentAnnotation) aJCas.getDocumentAnnotationFs();
			outputFileAnno.setEnd(docAnno.getEnd() - 1);
			outputFileAnno.addToIndexes();
		}
	}

}
