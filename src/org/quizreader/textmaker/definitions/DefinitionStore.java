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

import org.quizreader.textmaker.dictionary.model.Definition;

import com.fasterxml.jackson.annotation.JsonInclude;
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

	private List<String> getFolderWords(File folder) throws IOException {
		List<String> ret = new ArrayList<String>();
		File[] entryFiles = folder.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.isFile();
			}
		});
		for (File entryFile : entryFiles) {
			final String name = entryFile.getName();
			final String word = name.substring(0, name.indexOf('.'));
			ret.add(word);
		}
		File[] childFolders = folder.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.isDirectory();
			}
		});
		for (File childFolder : childFolders) {
			ret.addAll(getFolderWords(childFolder));
		}
		return ret;
	}

	public List<String> getAll() throws IOException {
		return getFolderWords(new File(basePath));
	}

	public List<Definition> getDefinitions(String sourceId, String word) throws IOException {
		final DefinitionStoreEntry entry = getEntry(word);
		return entry == null ? null : entry.get(sourceId);
	}

	public List<Definition> getDefinitions(String word) throws IOException {
		final DefinitionStoreEntry entry = getEntry(word);
		return entry == null ? null : entry.getAll();
	}

	public boolean hasEntry(String word) {
		return wordFile(word).exists();
	}

	public void touch(String word) throws IOException {
		File file = wordFile(word);
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			objectWriter.writeValue(file, new DefinitionStoreEntry());
		}
	}

	public void writeEntry(String word, String source, List<Definition> definitions) throws IOException {
		File file = wordFile(word);
		System.out.println("writing file: " + file.getName());
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			DefinitionStoreEntry entry = new DefinitionStoreEntry();
			entry.put(source, definitions);
			objectWriter.writeValue(file, entry);
		}
		else {
			DefinitionStoreEntry entry = getEntry(word);
			entry.put(source, definitions);
			objectWriter.writeValue(file, entry);
		}
	}

	public String listToString(List<Definition> definitions) throws IOException {
		return objectWriter.writeValueAsString(definitions);
	}

	private DefinitionStoreEntry getEntry(String word) throws IOException {
		final File file = wordFile(word);
		if (!file.exists()) {
			return null;
		}
		return objectMapper.readValue(file, DefinitionStoreEntry.class);
	}
}