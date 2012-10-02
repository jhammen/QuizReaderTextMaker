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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.examples.SourceDocumentInformation;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;
import org.quizreader.textmaker.DefinitionFile;
import org.quizreader.textmaker.Paragraph;
import org.quizreader.textmaker.uima.types.DefinitionAnnotation;
import org.quizreader.textmaker.uima.types.ParagraphAnnotation;
import org.quizreader.textmaker.uima.types.WiktAnnotation;
import org.quizreader.textmaker.wiktionary.Entry;
import org.quizreader.textmaker.wiktionary.WiktionaryManager;

public class QRXWriter extends CasConsumer_ImplBase {

	private WiktionaryManager wiktionary = new WiktionaryManager();
	private Map<String, Integer> defCount;
	private Map<String, DefinitionFile> definitionFiles;

	@Override
	public void initialize() throws ResourceInitializationException {
		super.initialize();
		try {
			wiktionary.loadXML();
			definitionFiles = new HashMap<String, DefinitionFile>();
			defCount = new HashMap<String, Integer>();
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
		File inFile = getInputFile(jcas);

		FSIterator<Annotation> defIndex = jcas.getAnnotationIndex(DefinitionAnnotation.type).iterator();
		FSIndex<Annotation> paraIndex = jcas.getAnnotationIndex(ParagraphAnnotation.type);

		Annotation nextDef = defIndex.hasNext() ? defIndex.next() : null;

		List<Paragraph> paragraphs = new ArrayList<Paragraph>();
		for (Annotation nextPara : paraIndex) {
			List<Entry> entries = new ArrayList<Entry>();
			while (nextDef != null && nextDef.getEnd() <= nextPara.getEnd()) {
				String word = nextDef.getCoveredText();
				incrementDefCount(word);
				Entry entry = wiktionary.getEntry(word);
				if (entry != null) {
					entries.add(entry);
				}
				nextDef = defIndex.hasNext() ? defIndex.next() : null;
			}
			Paragraph paragraph = new Paragraph();
			paragraph.setEntries(entries);
			paragraphs.add(paragraph);
		}
		DefinitionFile definitionFile = new DefinitionFile();
		definitionFile.setParagraphs(paragraphs);

		definitionFiles.put(inFile.getName() + ".xml", definitionFile);

		File htmlFile = new File(inFile.getName() + ".html");
		// PrintStream htmlStream = new PrintStream(htmlFile);
		PrintStream htmlStream = new PrintStream(System.out);

		htmlStream.print("<html><body>");

		FSIterator<Annotation> wiktAnnoIterator = jcas.getAnnotationIndex(WiktAnnotation.type).iterator();
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

	private void incrementDefCount(String word) {
		Integer count = defCount.get(word);
		if (count == null) {
			defCount.put(word, 1);
		}
		else {
			defCount.put(word, count + 1);
		}
	}

	@Override
	public void collectionProcessComplete(ProcessTrace arg0) throws ResourceProcessException, IOException {
		try {
			List<String> commonWords = new ArrayList<String>();
			int halfOrSo = definitionFiles.size() / 2;
			for (String word : defCount.keySet()) {
				if (defCount.get(word) > halfOrSo) {
					commonWords.add(word);
				}
			}
			System.err.println("found " + commonWords.size() + " common words out of " + defCount.size());
			for (String filename : definitionFiles.keySet()) {
				removeCommonWords(definitionFiles.get(filename), commonWords);
				marshalXML(filename, definitionFiles.get(filename));
			}
			DefinitionFile commonFile = new DefinitionFile();
			List<Entry> entries = new ArrayList<Entry>();
			for (String word : commonWords) {
				entries.add(wiktionary.getEntry(word));
			}
			Paragraph paragraph = new Paragraph();
			paragraph.setEntries(entries);
			List<Paragraph> paragraphList = new ArrayList<Paragraph>();
			paragraphList.add(paragraph);
			commonFile.setParagraphs(paragraphList);
			marshalXML("common.xml", commonFile);

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ResourceProcessException(ex);
		}
		super.collectionProcessComplete(arg0);
	}

	private void removeCommonWords(DefinitionFile definitionFile, List<String> commonWords) {
		for (Paragraph paragraph : definitionFile.getParagraphs()) {
			List<Entry> commonEntries = new ArrayList<Entry>();
			for (Entry entry : paragraph.getEntries()) {
				if (commonWords.contains(entry.getWord())) {
					commonEntries.add(entry);
				}
			}
			paragraph.getEntries().removeAll(commonEntries);
		}
	}

	private void marshalXML(String filename, DefinitionFile definitionFile) throws JAXBException, PropertyException {
		JAXBContext jaxbContext = JAXBContext.newInstance(DefinitionFile.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		jaxbMarshaller.marshal(definitionFile, new File(filename));
	}

	private File getInputFile(JCas jcas) {

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
