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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.quizreader.textmaker.dictionary.model.Definition;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * Model object for a single json file in a definition store
 *
 */
public class DefinitionStoreEntry extends HashMap<String, List<Definition>> {

	@JsonIgnore
	public List<Definition> getAll() {
		List<Definition> ret = new ArrayList<Definition>();
		for (String key : keySet()) {
			ret.addAll(get(key));
		}
		return ret;
	}
}
