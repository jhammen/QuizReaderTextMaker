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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;

public class WiktionaryManager {

	private Map<String, Entry> entries;

	public static void main(String[] args) throws XMLStreamException, IOException, JAXBException {
		new WiktionaryManager().loadXML();
	}

	public void loadXML() throws JAXBException {
		entries = new HashMap<String, Entry>();
		File xmlFile = new File("txt/fr20111016.xml");
		JAXBContext jaxbContext = JAXBContext.newInstance(Wiktionary.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		Wiktionary wikt = (Wiktionary) jaxbUnmarshaller.unmarshal(xmlFile);
		for (Entry entry : wikt.getEntries()) {
			entries.put(entry.getWord(), entry);
		}
		System.out.println(entries.size() + " wiktionary entries loaded");
	}

	public Entry getEntry(String word) {
		return entries.get(word);
	}

}
