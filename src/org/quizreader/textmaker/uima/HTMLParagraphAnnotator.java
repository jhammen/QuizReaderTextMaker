package org.quizreader.textmaker.uima;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.quizreader.textmaker.uima.types.HTMLAnnotation;
import org.quizreader.textmaker.uima.types.ParagraphAnnotation;

public class HTMLParagraphAnnotator extends JCasAnnotator_ImplBase {
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		AnnotationIndex<Annotation> markupIndex = jcas.getAnnotationIndex(HTMLAnnotation.type);
		for (Annotation anno : markupIndex) {
			HTMLAnnotation markupAnno = (HTMLAnnotation) anno;
			if("p".equalsIgnoreCase(markupAnno.getName())) {
				ParagraphAnnotation paraAnno = new ParagraphAnnotation(jcas);
				paraAnno.setBegin(markupAnno.getBegin());
				paraAnno.setEnd(markupAnno.getEnd());
				paraAnno.addToIndexes();
			}
		}
	}
}