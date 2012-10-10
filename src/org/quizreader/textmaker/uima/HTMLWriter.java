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
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

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
import org.apache.uima.tika.MarkupAnnotation;
import org.apache.uima.util.ProcessTrace;
import org.quizreader.textmaker.uima.types.DefinitionAnnotation;
import org.quizreader.textmaker.uima.types.OutputFileAnnotation;

public class HTMLWriter extends CasConsumer_ImplBase {

	private static final String CONFIG_PARAM_PASS_TAGS = "passTags";

	private Set<String> passTags;

	@Override
	public void initialize() throws ResourceInitializationException {
		super.initialize();
		try {
			passTags = new HashSet<String>();
			for (String tag : (String[]) getConfigParameterValue(CONFIG_PARAM_PASS_TAGS)) {
				passTags.add(tag.toLowerCase());
			}
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
		String documentText = cas.getDocumentText();

		AnnotationIndex<Annotation> fileIndex = jcas.getAnnotationIndex(OutputFileAnnotation.type);
		AnnotationIndex<Annotation> defIndex = jcas.getAnnotationIndex(DefinitionAnnotation.type);
		AnnotationIndex<Annotation> markupIndex = jcas.getAnnotationIndex(MarkupAnnotation.type);

		Set<String> suppressTags = new HashSet<String>();

		for (Annotation anno : fileIndex) {

			OutputFileAnnotation fileAnno = (OutputFileAnnotation) anno;

			FSIterator<Annotation> defAnnoIterator = defIndex.subiterator(fileAnno);
			Annotation defAnno = defAnnoIterator.hasNext() ? defAnnoIterator.next() : null;

			FSIterator<Annotation> markupAnnoIterator = markupIndex.subiterator(fileAnno);
			Annotation markupAnno = markupAnnoIterator.next();

			// start new file
			OutputStream nullStream = new OutputStream() {
				public void write(int b) {
				}
			};
			PrintStream htmlStream = new PrintStream(nullStream);
			if (fileAnno.getFilePath().endsWith("33.html")) {
				htmlStream = new PrintStream(fileAnno.getFilePath());
			}
			htmlStream.print("<html><body>");

			Map<Integer, Stack<String>> endTags = new HashMap<Integer, Stack<String>>();

			for (int i = fileAnno.getBegin(); i <= fileAnno.getEnd(); i++) {
				// print out end tags at this location
				Stack<String> stack = endTags.get(i);
				if (stack != null) {
					while (!stack.empty()) {
						htmlStream.print("</" + stack.pop() + ">");
					}
				}
				// print out markup tags at this location (pass tags only)
				while (markupAnno != null && markupAnno.getBegin() == i) {
					String tag = ((MarkupAnnotation) markupAnno).getName();
					if (passTags.contains(tag.toLowerCase())) {
						htmlStream.print("<" + tag + ">");
						addEndTag(endTags, tag, markupAnno.getEnd());
					}
					else {
						suppressTags.add(tag);
					}
					markupAnno = markupAnnoIterator.hasNext() ? markupAnnoIterator.next() : null;
				}
				// print out definition tags
				while (defAnno != null && defAnno.getBegin() == i) {
					htmlStream.print("<a>");
					addEndTag(endTags, "a", defAnno.getEnd());
					defAnno = defAnnoIterator.hasNext() ? defAnnoIterator.next() : null;
				}
				htmlStream.print(documentText.charAt(i));
			}

			htmlStream.println("</body></html>");
			htmlStream.close();
		}

		for (String suppressedTag : suppressTags) {
			System.out.println("suppressed tag: " + suppressedTag);
		}
	}

	private void addEndTag(Map<Integer, Stack<String>> endTags, String string, int end) {
		Stack<String> stack = endTags.get(end);
		if (stack == null) {
			stack = new Stack<String>();
			endTags.put(end, stack);
		}
		stack.push(string);
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
