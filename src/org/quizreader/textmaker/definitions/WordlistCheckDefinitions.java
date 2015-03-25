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
		new WordlistCheckDefinitions().run(argv[0], argv[1], argv[2], argv.length > 3);
	}

	private void run(String wordlistPath, String definitionPath, String sourceId, boolean write) throws FileNotFoundException,
			JAXBException, IOException, JsonParseException, JsonMappingException {

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

			if (!defStore.hasEntry(word)) {
				System.err.println(word + " has no definition file!");
				if (write) {
					System.err.println("writing stub...");
					defStore.touch(word);
				}
				continue;
			}
			// check for any defrinitions
			final List<Definition> allDefs = defStore.getDefinitions(word);
			if (allDefs == null || allDefs.size() == 0) {
				System.err.println(word + " has no definitions at all");
				continue;
			}
			// check for any defrinitions from given sourceId
			final List<Definition> definitions = defStore.getDefinitions(sourceId, word);
			if (definitions == null || definitions.size() == 0) {
				System.err.println(word + " has no definitions from source " + sourceId);
				continue;
			}

			if (listEntry.getType() != null) {
				if (!hasType(allDefs, listEntry)) {
					System.err.println(word + " has definition but no matching part of speech: " + listEntry.getType());
					continue;
				}
				if (!hasType(allDefs, listEntry)) {
					System.err.println(word + " has definition but no matching part of speech: " + listEntry.getType()
							+ " from sourceId " + sourceId);
					continue;
				}
			}
			count++;
		}
		fr.close();
		System.out.println(entries.size() + " entries checked, " + count + " had no issues");
		System.out.println("done.");
	}

	private boolean hasType(List<Definition> defList, WordlistEntry entry) {
		for (Definition def : defList) {
			final String type = def.getType();
			if (type != null && type.equals(entry.getType())) {
				return true;
			}
		}
		return false;
	}

}
