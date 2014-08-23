package org.quizreader.textmaker.uima;

import org.quizreader.textmaker.wiktionary.model.Entry;

public interface WiktionaryResource {
	
	public Entry getEntry(String word);
	
}
