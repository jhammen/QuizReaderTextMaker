package org.quizreader.textmaker.uima;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.uima.cas.FSIterator;
import org.apache.uima.examples.SourceDocumentInformation;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

public class UimaUtil {

	public File getInputFile(JCas jcas) {
		FSIterator<Annotation> it = jcas.getAnnotationIndex(SourceDocumentInformation.type).iterator();
		if (it.hasNext()) {
			SourceDocumentInformation fileLoc = (SourceDocumentInformation) it.next();
			try {
				return new File(new URL(fileLoc.getUri()).getPath());

			} catch (MalformedURLException e1) {
				return null;
			}
		}
		return null;
	}
}
