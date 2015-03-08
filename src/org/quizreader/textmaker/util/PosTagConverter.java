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

package org.quizreader.textmaker.util;

public class PosTagConverter {

	public static String universal2wiktionary(String tag) {
		switch (tag) {
		case "ADJ":
			return "adjective";
		case "ADP":
			return "preposition";
		case "ADV":
			return "adverb";
		case "CONJ":
			return "conjunction";
		case "DET":
			return "article";
		case "NOUN":
			return "noun";
		case "PRN":
			return "pronoun";
		case "PRON":
			return "pronoun";
		}
		return null;
	}

	public static String spanish2wiktionary(String tag) {
		switch (tag) {
		case "VAI":
			return "verb";
		case "VMI":
			return "verb";
		case "VMN":
			return "verb";
		case "VMP":
			return "verb";
		case "VMS":
			return "verb";
		case "VSI":
			return "verb";
		case "VSN":
			return "verb";
		}
		return null;
	}
}
