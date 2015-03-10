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

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.quizreader.textmaker.wiktionary.model.Definition;
import org.quizreader.textmaker.wiktionary.model.Entry;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class DefinitionStore {

	private String basePath;
	private ObjectMapper objectMapper = new ObjectMapper();
	private ObjectWriter objectWriter;

	public DefinitionStore(String basePath) {
		this.basePath = basePath;
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		objectWriter = objectMapper.writer();
	}

	private String path(String word) {
		return word.length() == 1 ? word : word.substring(0, 2);
	}

	private File wordFile(String word) {
		return new File(basePath + path(word), word + ".json");
	}

	private List<Entry> getFolderEntries(File folder) throws IOException {
		List<Entry> ret = new ArrayList<Entry>();
		File[] entryFiles = folder.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.isFile();
			}
		});
		for (File entryFile : entryFiles) {
			final Entry entry = objectMapper.readValue(entryFile, Entry.class);
			final String name = entryFile.getName();
			final String word = name.substring(0, name.indexOf('.'));
			entry.setWord(word);
			ret.add(entry);
		}
		File[] childFolders = folder.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.isDirectory();
			}
		});
		for (File childFolder : childFolders) {
			ret.addAll(getFolderEntries(childFolder));
		}
		return ret;
	}

	public List<Entry> getAll() throws IOException {
		return getFolderEntries(new File(basePath));
	}

	public Entry getEntry(String word) throws IOException {
		final File file = wordFile(word);
		if (!file.exists()) {
			return null;
		}
		final Entry entry = objectMapper.readValue(file, Entry.class);
		entry.setWord(word);
		return entry;
	}

	public void writeEntry(Entry entry) throws IOException {
		File file = wordFile(entry.getWord());
		if (!file.exists()) {
			file.getParentFile().mkdirs();
		}
		objectWriter.writeValue(file, entry);
		System.out.println("writing file: " + file.getName());
	}

	public boolean differs(Entry fileEntry, Entry wiktEntry) {
		if (wiktEntry == null) {
			return true;
		}
		if (fileEntry.getDefinitions().size() != wiktEntry.getDefinitions().size()) {
			return true;
		}
		int index = 0;
		for (Definition def : fileEntry.getDefinitions()) {
			if (!def.getText().equals(wiktEntry.getDefinitions().get(index++).getText())) {
				return true;
			}
		}
		return false;
	}

	public String entryToString(Entry entry) throws JsonProcessingException {
		return objectWriter.writeValueAsString(entry);
	}
}