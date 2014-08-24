package org.quizreader.textmaker.wiktionary;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.quizreader.textmaker.wiktionary.model.Definition;
import org.quizreader.textmaker.wiktionary.model.Entry;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class DefinitionUpdater {

	private WiktionaryResourceImpl dictionary = new WiktionaryResourceImpl();
	private ObjectMapper objectMapper = new ObjectMapper();
	private Set<String> missingWords;
	int changedWords;

	public static void main(String[] args) throws JAXBException, IOException {
		new DefinitionUpdater().run(args[0], args[1], args.length > 2);
		System.out.println("done.");
	}

	private void run(String definitionPath, String dictionaryPath, boolean write) throws JAXBException, IOException {
		if(write) {
			System.out.println("WARNING: set to overwrite files! cancel now if you don't want that");
		}
		changedWords = 0;
		missingWords = new HashSet<String>();
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		FileInputStream fis = new FileInputStream(dictionaryPath);
		dictionary.load(fis);
		fis.close();
		checkFolder(new File(definitionPath), write);
		System.out.println(changedWords + " changed words");
		// show missing words
		System.out.println(missingWords.size() + " missing words:");
		for(String word: missingWords) {
			System.out.println(word);
		}
	}

	private void checkFolder(File folder, boolean write) {
		File[] childFolders = folder.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.isDirectory();
			}
		});
		for (File childFolder : childFolders) {
			checkFolder(childFolder, write);
		}
		File[] entryFiles = folder.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.isFile();
			}
		});
		final ObjectWriter w = objectMapper.writer();
		for (File entry : entryFiles) {
			try {
				final Entry fileEntry = objectMapper.readValue(entry, Entry.class);
				final Entry dictEntry = dictionary.getEntry(fileEntry.getWord());
				if (dictEntry == null) {
					missingWords.add(fileEntry.getWord());
				}
				else if (differs(fileEntry, dictEntry)) {
					changedWords++;
					System.out.println("********************************* " + fileEntry.getWord());
					System.out.println(w.writeValueAsString(fileEntry));
					System.out.println(w.writeValueAsString(dictEntry));
					if(write) {
						w.writeValue(entry, dictEntry);						
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private boolean differs(Entry fileEntry, Entry wiktEntry) {
		// System.out.println("checking entry: " + entry.getName());
		if (wiktEntry == null) {
			return true;
		}
		if (fileEntry.getDefinitions().size() != wiktEntry.getDefinitions().size()) {
			return true;
		}
		int index = 0;
		for (Definition def : fileEntry.getDefinitions()) {
			// checkForTemplate(fileEntry, def);
			if (!def.getText().equals(wiktEntry.getDefinitions().get(index++).getText())) {
				return true;
			}
		}
		return false;
	}

	private void checkForTemplate(final Entry fileEntry, Definition def) {
		if (def.getText().matches(".*\\[template\\|.*")) {
			System.out.println(fileEntry.getWord() + " has template: " + def.getText());
		}
	}

}
