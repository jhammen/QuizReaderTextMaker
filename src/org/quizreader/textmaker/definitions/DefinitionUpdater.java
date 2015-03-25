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
package org.quizreader.textmaker.definitions;

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

import org.quizreader.textmaker.dictionary.DictionaryResourceImpl;
import org.quizreader.textmaker.dictionary.model.Definition;
import org.quizreader.textmaker.dictionary.model.Entry;
import org.quizreader.textmaker.uima.DictionaryResource;

public class DefinitionUpdater {

	private final Pattern templatePattern = Pattern.compile("(\\[template\\|[^\\|]+\\|)");
	private final DictionaryResource dictionary = new DictionaryResourceImpl();

	public static void main(String[] args) throws JAXBException, IOException {
		new DefinitionUpdater().run(args[0], args[1], args.length > 2);
		System.out.println("done.");
	}

	private void run(String definitionPath, String dictionaryPath, boolean write) throws JAXBException, IOException {
		if (write) {
			System.out.println("WARNING: set to overwrite files! cancel now if you don't want that");
		}
		else {
			System.out.println("Running in simulation mode: will not overwrite files");
		}

		// load dictionary
		final FileInputStream fis = new FileInputStream(dictionaryPath);
		dictionary.load(fis);
		fis.close();
		final String sourceId = dictionary.getSourceId();

		// definition store
		DefinitionStore defStore = new DefinitionStore(definitionPath);

		// loop over all definions
		int changedWords = 0;
		int totalWords = 0;
		final Set<String> templates = new HashSet<String>();
		final List<String> missingWords = new ArrayList<String>();

		for (String word : defStore.getAll()) {
			final Entry dictEntry = dictionary.getEntry(word);
			final List<Definition> defList = defStore.getDefinitions(sourceId, word);
			if (dictEntry == null) {
				missingWords.add(word);
			}
			else if (defList == null || differs(defList, dictEntry)) {
				changedWords++;
				System.out.println("********************************* " + word);
				System.out.println(defStore.listToString(defList));
				System.out.println(defStore.listToString(dictEntry.getDefinitions()));
				if (write) {
					defStore.writeEntry(word, sourceId, dictEntry.getDefinitions());
				}
			}

			final List<String> fileTemplates = defList == null ? null : checkForTemplates(defList);
			if (fileTemplates != null) {
				templates.addAll(fileTemplates);
			}
			totalWords++;
		}

		// show summary
		System.out.println(changedWords + " changed words");
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

	public boolean differs(List<Definition> defList, Entry dictEntry) {
		if (defList.size() != dictEntry.getDefinitions().size()) {
			return true;
		}
		int index = 0;
		for (Definition def : defList) {
			if (!def.getText().equals(dictEntry.getDefinitions().get(index++).getText())) {
				return true;
			}
		}
		return false;
	}

	private List<String> checkForTemplates(final List<Definition> defList) {
		List<String> templates = null;
		for (Definition def : defList) {
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
