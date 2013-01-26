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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
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
import org.apache.uima.util.ProcessTrace;
import org.htmlparser.Parser;
import org.htmlparser.util.ParserException;
import org.quizreader.textmaker.uima.types.DefinitionAnnotation;
import org.quizreader.textmaker.uima.types.FileAnnotation;
import org.quizreader.textmaker.uima.types.HTMLAnnotation;

public class HTMLOutputWriter extends CasConsumer_ImplBase {

	private static final String CONFIG_PARAM_PASS_TAGS = "passTags";
	private static final String CONFIG_PARAM_OUPUT_DIR = "outputFolder";

	private Set<String> passTags;
	private String outputDir;

	@Override
	public void initialize() throws ResourceInitializationException {
		super.initialize();
		try {
			passTags = new HashSet<String>();
			for (String tag : (String[]) getConfigParameterValue(CONFIG_PARAM_PASS_TAGS)) {
				passTags.add(tag.toLowerCase());
			}
			outputDir = (String) getConfigParameterValue(CONFIG_PARAM_OUPUT_DIR);
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	public void processCas(CAS cas) throws ResourceProcessException {
		try {
			process(cas);
		} catch (Exception ex) {
			throw new ResourceProcessException(ex);
		}
	}

	private void process(CAS cas) throws CASException, FileNotFoundException, JAXBException, ParserException, UnsupportedEncodingException {

		JCas jcas = cas.getJCas();
		String documentText = cas.getDocumentText();

		AnnotationIndex<Annotation> fileIndex = jcas.getAnnotationIndex(FileAnnotation.type);
		AnnotationIndex<Annotation> defIndex = jcas.getAnnotationIndex(DefinitionAnnotation.type);
		AnnotationIndex<Annotation> markupIndex = jcas.getAnnotationIndex(HTMLAnnotation.type);

		Set<String> suppressTags = new HashSet<String>();

		for (Annotation anno : fileIndex) {

			FileAnnotation fileAnno = (FileAnnotation) anno;

			if (!fileAnno.getOutput()) {
				continue;
			}

			FSIterator<Annotation> defAnnoIterator = defIndex.subiterator(fileAnno);
			Annotation defAnno = defAnnoIterator.hasNext() ? defAnnoIterator.next() : null;

			FSIterator<Annotation> htmlAnnoIterator = markupIndex.subiterator(fileAnno);
			Annotation markupAnno = htmlAnnoIterator.next();

			StringBuilder htmlBuilder = new StringBuilder();
			htmlBuilder.append("<html><head>");
			htmlBuilder.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>");
			htmlBuilder.append("</head><body>");

			Map<Integer, Stack<String>> endTags = new HashMap<Integer, Stack<String>>();

			for (int i = fileAnno.getBegin(); i <= fileAnno.getEnd(); i++) {
				// print out end tags at this location
				Stack<String> stack = endTags.get(i);
				if (stack != null) {
					while (!stack.empty()) {
						htmlBuilder.append("</" + stack.pop() + ">");
					}
				}
				// print out markup tags at this location (pass tags only)
				while (markupAnno != null && markupAnno.getBegin() == i) {
					String tag = ((HTMLAnnotation) markupAnno).getName();
					String lowerTag = tag.toLowerCase();
					if (passTags.contains(lowerTag)) {
						htmlBuilder.append("<" + lowerTag + ">");
						addEndTag(endTags, lowerTag, markupAnno.getEnd());
					}
					else {
						suppressTags.add(tag);
					}
					markupAnno = htmlAnnoIterator.hasNext() ? htmlAnnoIterator.next() : null;
				}
				// print out definition tags
				while (defAnno != null && defAnno.getBegin() == i) {
					htmlBuilder.append("<a>");
					addEndTag(endTags, "a", defAnno.getEnd());
					defAnno = defAnnoIterator.hasNext() ? defAnnoIterator.next() : null;
				}
				htmlBuilder.append(documentText.charAt(i));
			}

			htmlBuilder.append("</body></html>");

			Parser parser = new Parser();
			parser.setInputHTML(htmlBuilder.toString());
			String html = parser.parse(null).toHtml();
			// print to file: file name from annotation, folder from param
			File outputFile = new File(outputDir, fileAnno.getFileName());
			PrintStream printStream = new PrintStream(outputFile, "UTF-8");
			printStream.print(html);
			printStream.close();
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
