package org.quizreader.textmaker;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.quizreader.textmaker.wiktionary.Entry;
import org.quizreader.textmaker.wiktionary.WiktionaryManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class DefinitionSource {


	private static final String SITE_BASE = "../QuizReaderEnglish/";
	private WiktionaryManager wiktionary = new WiktionaryManager();
	private ObjectMapper objectMapper = new ObjectMapper();
	private Map<String, Entry> entries;
	private String definitionPath;

	public DefinitionSource(String language, String resourceFilePath) throws JAXBException {
		entries = new HashMap<String, Entry>();
		wiktionary.loadXML(resourceFilePath);
		definitionPath = SITE_BASE + '/' + language + "/def";
	}

	public Entry getEntry(String word) throws IOException {
		if (!entries.containsKey(word)) {
			Entry entry = readEntry(word);
			if (entry == null) {
				entry = wiktionary.getEntry(word);
			}
			entries.put(word, entry);
		}
		return entries.get(word);
	}

	private Entry readEntry(String word) throws IOException {
		File defFile = new File(definitionPath + '/' + path(word) + '/' + word);
		if (!defFile.exists()) {
			return null;
		}
		return objectMapper.readValue(defFile, Entry.class);
	}

	public String path(String word) {
		return word.length() == 1 ? word : word.substring(0, 2);
	}

	public void writeUpdatedEntries() throws IOException {
		final ObjectWriter w = objectMapper.writer();
		for (String word : entries.keySet()) {
			Entry entry = entries.get(word);
			if (entry != null) {
				File folder = new File(definitionPath + '/' + path(word));
				// folder.mkdirs();
				// w.writeValue(new File(folder, word), entry);
			}
		}
		System.out.println("total entries count: " + entries.values().size());
	}
}