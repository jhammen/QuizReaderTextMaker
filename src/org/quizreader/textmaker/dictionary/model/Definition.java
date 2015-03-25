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

package org.quizreader.textmaker.dictionary.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Definition {

	private String type;
	private String text;
	private String root;

	@XmlAttribute
	@JsonProperty("t")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@XmlAttribute
	@JsonProperty("r")
	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	@XmlValue
	@JsonProperty("x")
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
