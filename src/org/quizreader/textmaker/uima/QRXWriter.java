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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;
import org.quizreader.textmaker.uima.types.OutputFileAnnotation;
import org.quizreader.textmaker.uima.types.ParagraphAnnotation;
import org.quizreader.textmaker.uima.types.WiktAnnotation;

public class QRXWriter extends CasConsumer_ImplBase {

	@Override
	public void initialize() throws ResourceInitializationException {
		super.initialize();
		try {
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	public void processCas(CAS cas) throws ResourceProcessException {
		try {
			process(cas);
		} catch (IOException ex) {
			throw new ResourceProcessException(ex);
		} catch (CASException ex) {
			throw new ResourceProcessException(ex);
		} catch (JAXBException ex) {
			throw new ResourceProcessException(ex);
		}
	}

	private void process(CAS cas) throws CASException, FileNotFoundException, JAXBException {

		JCas jcas = cas.getJCas();

		AnnotationIndex<Annotation> fileIndex = jcas.getAnnotationIndex(OutputFileAnnotation.type);

		// Annotation nextDef = markupAnnos.hasNext() ? markupAnnos.next() : null;
		// for (Annotation nextPara : paraIndex) {
		// while (nextDef != null && nextDef.getEnd() <= nextPara.getEnd()) {
		// String word = nextDef.getCoveredText();
		// }
		// nextDef = markupAnnos.hasNext() ? markupAnnos.next() : null;
		// }

		// PrintStream htmlStream = new PrintStream(htmlFile);

		for (Annotation anno : fileIndex) {

			PrintStream htmlStream = new PrintStream(System.out);

			htmlStream.print("<html><body>");

			AnnotationIndex<Annotation> wiktIndex = jcas.getAnnotationIndex(WiktAnnotation.type);

			FSIterator<Annotation> wiktAnnoIterator = wiktIndex.iterator();
			String documentText = cas.getDocumentText();
			Map<Integer, Integer> endTags = new HashMap<Integer, Integer>();

			FSIterator<Annotation> paraIterator = jcas.getAnnotationIndex(ParagraphAnnotation.type).iterator();

			while (paraIterator.hasNext() && wiktAnnoIterator.hasNext()) {

				htmlStream.print("<p>");
				Annotation wiktAnno = wiktAnnoIterator.next();

				Annotation paraAnno = paraIterator.next();
				for (int i = paraAnno.getBegin(); i <= paraAnno.getEnd(); i++) {
					while (wiktAnno.getBegin() == i && wiktAnnoIterator.hasNext()) {
						htmlStream.print("<a>");
						int end = wiktAnno.getEnd();
						Integer existingTags = endTags.get(end);
						endTags.put(end, existingTags == null ? 1 : existingTags + 1);
						wiktAnno = wiktAnnoIterator.next();
					}
					Integer numEndTags = endTags.get(i);
					for (int j = 0; numEndTags != null && j < numEndTags; j++) {
						htmlStream.print("</a>");
					}
					htmlStream.print(documentText.charAt(i));
				}
				htmlStream.print("</p>\n\n");
			}
			htmlStream.println("</body></html>");
			htmlStream.close();
		}
	}

	@Override
	public void collectionProcessComplete(ProcessTrace arg0) throws ResourceProcessException, IOException {
		try {
			// TODO: what?
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ResourceProcessException(ex);
		}
		super.collectionProcessComplete(arg0);
	}

}
