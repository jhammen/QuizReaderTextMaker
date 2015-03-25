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

package org.quizreader.textmaker.wiktionary;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.SharedResourceObject;
import org.quizreader.textmaker.dictionary.model.Entry;
import org.quizreader.textmaker.dictionary.model.Wiktionary;
import org.quizreader.textmaker.uima.DictionaryResource;

public class WiktionaryResourceImpl implements DictionaryResource, SharedResourceObject {

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
	public String getSourceId() {
		return "wiktionary.org";
	}

	public void load(InputStream inputStream) throws IOException {
		entries = new HashMap<String, Entry>();
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Wiktionary.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			Wiktionary wikt = (Wiktionary) jaxbUnmarshaller.unmarshal(inputStream);
			for (Entry entry : wikt.getEntries()) {
				entry.setSource(wikt.getSource());
				entries.put(entry.getWord(), entry);
			}
			System.out.println(entries.size() + " wiktionary entries loaded");
		} catch (JAXBException e) {
			throw new IOException(e);
		}
	}

	@Override
	public Entry getEntry(String word) {
		return entries.get(word);
	}
}
