package org.quizreader.textmaker.wiktionary;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;

import org.quizreader.textmaker.wiktionary.model.Definition;
import org.quizreader.textmaker.wiktionary.model.Entry;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class DefinitionUpdater {

	private WiktionaryResourceImpl dictionary = new WiktionaryResourceImpl();

	private ObjectMapper objectMapper = new ObjectMapper();
	private List<String> missingWords;
	int changedWords;
	int totalWords;

	private Pattern templatePattern = Pattern.compile("(\\[template\\|[^\\|]+\\|)");
	private Set<String> templates;

	public static void main(String[] args) throws JAXBException, IOException {
		new DefinitionUpdater().run(args[0], args[1], args.length > 2);
		System.out.println("done.");
	}

	private void run(String definitionPath, String dictionaryPath, boolean write) throws JAXBException, IOException {
		if (write) {
			System.out.println("WARNING: set to overwrite files! cancel now if you don't want that");
		} else {
			System.out.println("Running in simulation mode: will not overwrite files");
		}
		changedWords = 0;
		totalWords = 0;
		templates = new HashSet<String>();
		missingWords = new ArrayList<String>();
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		FileInputStream fis = new FileInputStream(dictionaryPath);
		dictionary.load(fis);
		fis.close();
		checkFolder(new File(definitionPath), write);
		System.out.println(changedWords + " changed words");
		// show missing words
		System.out.println(missingWords.size() + " missing words:");
		Collections.sort(missingWords);
		for (String word : missingWords) {
			System.out.println(word);
		}
		System.out.println(templates.size() + " templates found:");
		for (String template : templates) {
			System.out.println(template);
		}
		System.out.println(totalWords + " total definition files");
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
					if (write) {
						w.writeValue(entry, dictEntry);
					}
				}
				final List<String> fileTemplates = checkForTemplates(fileEntry);
				if (fileTemplates != null) {
					templates.addAll(fileTemplates);
				}
				totalWords++;
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

	private List<String> checkForTemplates(final Entry fileEntry) {
		List<String> templates = null;
		for (Definition def : fileEntry.getDefinitions()) {
			Matcher matcher = templatePattern.matcher(def.getText());
			if (matcher.find()) {
				if (templates == null) {
					templates = new ArrayList<String>();
				}
				templates.add(matcher.group());
			}
		}
		return templates;
	}
}
