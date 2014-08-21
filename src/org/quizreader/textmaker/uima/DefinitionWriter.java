package org.quizreader.textmaker.uima;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.quizreader.textmaker.uima.types.DefinitionAnnotation;
import org.quizreader.textmaker.wiktionary.Entry;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class DefinitionWriter extends JCasAnnotator_ImplBase {

	private static final String CONFIG_PARAM_DEF_PATH = "definitionPath";
	
	private ObjectMapper objectMapper = new ObjectMapper();
	private WiktionaryResource wiktionary;
	private String definitionPath;

	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		try {
			wiktionary = (WiktionaryResource) getContext().getResourceObject("Wiktionary");
			definitionPath = (String) aContext.getConfigParameterValue(CONFIG_PARAM_DEF_PATH);		
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		AnnotationIndex<Annotation> defIndex = aJCas.getAnnotationIndex(DefinitionAnnotation.type);
		Set<String> entries = new HashSet<String>();		
		try {
			for (Annotation anno : defIndex) {
				String word = ((DefinitionAnnotation) anno).getWord();
				if (word == null) {
					word = anno.getCoveredText();
				}
				if (!entries.contains(word)) {
					writeDefinition(word);
					entries.add(word);
				}
			}
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

	private String path(String word) {
		return word.length() == 1 ? word : word.substring(0, 2);
	}

	// TODO: write definitions only
	private void writeDefinition(String word) throws IOException {
		File file = new File(definitionPath + path(word), word + ".json");
		if (!file.exists()) {
			final ObjectWriter w = objectMapper.writer();
			Entry entry = wiktionary.getEntry(word);
			if(entry == null) {
				entry = new Entry();
				entry.setWord(word);
			}
			file.getParentFile().mkdirs();
			w.writeValue(file, entry);
		}		
	}
}
