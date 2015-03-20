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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.quizreader.textmaker.dictionary.model.Definition;
import org.quizreader.textmaker.dictionary.model.Entry;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * Simple application to check a wordlist against a definition store.
 * 
 * No dictionaries used, stub files will be created for missing definitions.
 *
 */
public class WordlistCheckDefinitions {

	public static void main(String[] argv) throws IOException, JAXBException {
		new WordlistCheckDefinitions().run(argv[0], argv[1], argv.length > 2);
	}

	private void run(String wordlistPath, String definitionPath, boolean write) throws FileNotFoundException, JAXBException,
			IOException, JsonParseException, JsonMappingException {

		System.out.println("will create files: " + write);
		DefinitionStore defStore = new DefinitionStore(definitionPath);
		// load word list
		ObjectMapper objectMapper = new ObjectMapper();
		FileReader fr = new FileReader(wordlistPath);
		final List<WordlistEntry> entries = objectMapper.readValue(fr,
				objectMapper.getTypeFactory().constructCollectionType(List.class, WordlistEntry.class));

		// check def for each entry
		int count = 0;
		for (WordlistEntry listEntry : entries) {
			final String word = listEntry.getWord();
			final Entry jsonEntry = defStore.getEntry(word);

			if (jsonEntry == null) {
				System.err.println(word + " has no definition file!");
				if (write) {
					System.err.println("writing stub...");
					final Entry entry = new Entry();
					entry.setWord(word);
					defStore.writeEntry(entry);
				}
			}
			else if (listEntry.getType() != null && !hasType(jsonEntry, listEntry)) {
				System.err.println(word + " has definition but no matching part of speech: " + listEntry.getType());
			}
			count++;
		}
		fr.close();
		System.out.println(count + " entries checked");
		System.out.println("done.");
	}

	private boolean hasType(Entry dictEntry, WordlistEntry entry) {
		for (Definition def : dictEntry.getDefinitions()) {
			final String type = def.getType();
			if (type != null && type.equals(entry.getType())) {
				return true;
			}
		}
		return false;
	}

}
