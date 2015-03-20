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
package org.quizreader.textmaker.dictionary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.SharedResourceObject;
import org.quizreader.textmaker.dictionary.model.Definition;
import org.quizreader.textmaker.dictionary.model.Entry;
import org.quizreader.textmaker.uima.DictionaryResource;

public class DictionaryResourceImpl implements DictionaryResource, SharedResourceObject {

	private Map<String, Entry> entries;

	@Override
	public void load(DataResource aData) throws ResourceInitializationException {
		try {
			load(aData.getInputStream());
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	public void load(InputStream is) throws IOException {
		entries = new HashMap<String, Entry>();
		BufferedReader bis = new BufferedReader(new InputStreamReader(is));
		String line = bis.readLine();
		while (line != null) {
			if (line.startsWith("#") || line.length() < 2) {
				line = bis.readLine();
				continue;
			}
			String[] tok = line.split("\\t+");
			Entry entry = entries.get(tok[0]);
			if (entry == null) {
				entry = new Entry();
				entry.setWord(tok[0]);
				entries.put(tok[0], entry);
			}
			Definition def = new Definition();
			if (tok.length == 2) {
				def.setText(tok[1]);
			}
			else if (tok.length == 3) {
				def.setType(tok[1]);
				def.setText(tok[2]);
			}
			entry.getDefinitions().add(def);
			line = bis.readLine();
		}
	}

	@Override
	public Entry getEntry(String word) {
		return entries.get(word);
	}

}
