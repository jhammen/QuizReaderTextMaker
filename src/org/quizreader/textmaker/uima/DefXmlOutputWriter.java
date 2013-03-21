/*
This file is part of QuizReader.

QuizReader is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

QuizReader is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with QuizReader. If not, see <http://www.gnu.org/licenses/>.
 */

package org.quizreader.textmaker.uima;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;
import org.quizreader.textmaker.DefinitionFile;
import org.quizreader.textmaker.uima.types.FileAnnotation;
import org.quizreader.textmaker.uima.types.HTMLAnnotation;
import org.quizreader.textmaker.wiktionary.Definition;
import org.quizreader.textmaker.wiktionary.Entry;
import org.quizreader.textmaker.wiktionary.WiktionaryManager;

public class DefXmlOutputWriter extends CasConsumer_ImplBase {

	private static final String CONFIG_PARAM_OUPUT_DIR = "outputFolder";
	private static final String CONFIG_PARAM_WIKTIONARY_XML = "wiktionaryXml";

	private WiktionaryManager wiktionary = new WiktionaryManager();
	private Map<String, Integer> fileCount;
	private Map<String, DefinitionFile> definitionFiles;
	private String outputDir;

	@Override
	public void initialize() throws ResourceInitializationException {
		super.initialize();
		try {
			outputDir = (String) getConfigParameterValue(CONFIG_PARAM_OUPUT_DIR);
			String wiktionaryXml = (String) getConfigParameterValue(CONFIG_PARAM_WIKTIONARY_XML);
			wiktionary.loadXML(wiktionaryXml);
			definitionFiles = new HashMap<String, DefinitionFile>();
			fileCount = new HashMap<String, Integer>();
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

	private void process(CAS cas) throws CASException, FileNotFoundException, JAXBException {

		JCas jcas = cas.getJCas();
		String inFileName = getInputFileName(jcas);

		Map<String, Entry> entries = new HashMap<String, Entry>();
		FSIndex<Annotation> htmlIndex = jcas.getAnnotationIndex(HTMLAnnotation.type);

		for (Annotation anno : htmlIndex) {
			HTMLAnnotation htmlAnno = (HTMLAnnotation) anno;
			if ("A".equals(htmlAnno.getName().toUpperCase())) {
				String word = anno.getCoveredText();
				// if we haven't seen this word yet for this file
				if (!entries.containsKey(word)) {
					// find entry
					Entry entry = wiktionary.getEntry(word);
					entries.put(word, entry);
					// increment count of this word
					Integer count = fileCount.get(word);
					fileCount.put(word, count == null ? 1 : count + 1);
					// check for roots
					for (Definition definition : entry.getDefinitions()) {
						String roots = definition.getRoot();
						if (roots != null && roots.length() > 0) {
							for (String root : roots.split(",")) {
								Entry rootEntry = wiktionary.getEntry(root);
								if (rootEntry != null) {
									entries.put(root, rootEntry);
								}
								else {
									System.err.println("missing root entry: " + root);
								}
							}
						}
					}
				}
			}
		}
		DefinitionFile definitionFile = new DefinitionFile();
		definitionFile.setEntries(new ArrayList<Entry>(entries.values()));
		definitionFiles.put(outputFileName(inFileName), definitionFile);
	}

	private String outputFileName(String inFileName) {
		int dotIndex = inFileName.lastIndexOf(".html");
		return inFileName.substring(0, dotIndex) + ".def.xml";
	}

	private String getInputFileName(JCas jcas) {
		FSIterator<Annotation> it = jcas.getAnnotationIndex(FileAnnotation.type).iterator();
		if (it.hasNext()) {
			return ((FileAnnotation) it.next()).getFileName();
		}
		return null;
	}

	@Override
	public void collectionProcessComplete(ProcessTrace arg0) throws ResourceProcessException, IOException {
		try {
			// make a list of words common to multiple files
			List<String> commonWords = new ArrayList<String>();
			for (String word : fileCount.keySet()) {
				if (fileCount.get(word) > 1) {
					commonWords.add(word);
				}
			}
			System.out.println("found " + commonWords.size() + " common words out of " + fileCount.size());
			// for each definition file we've created in memory
			for (String filename : definitionFiles.keySet()) {
				DefinitionFile definitionFile = definitionFiles.get(filename);
				// remove common entries
				List<Entry> commonEntries = new ArrayList<Entry>();
				for (Entry entry : definitionFile.getEntries()) {
					if (commonWords.contains(entry.getWord())) {
						commonEntries.add(entry);
					}
				}
				definitionFile.getEntries().removeAll(commonEntries);
				// save to file
				marshalXML(filename, definitionFiles.get(filename));
			}
			// create and save the common definitions file
			DefinitionFile commonFile = new DefinitionFile();
			List<Entry> entries = new ArrayList<Entry>();
			for (String word : commonWords) {
				entries.add(wiktionary.getEntry(word));
			}
			commonFile.setEntries(entries);
			marshalXML("common.xml", commonFile);

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ResourceProcessException(ex);
		}
		super.collectionProcessComplete(arg0);
	}

	private void marshalXML(String filename, DefinitionFile definitionFile) throws JAXBException, PropertyException {
		Collections.sort(definitionFile.getEntries());
		JAXBContext jaxbContext = JAXBContext.newInstance(DefinitionFile.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		File file = new File(outputDir, filename);
		jaxbMarshaller.marshal(definitionFile, file);
	}

}
