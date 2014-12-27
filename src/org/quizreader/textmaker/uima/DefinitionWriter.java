package org.quizreader.textmaker.uima;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.quizreader.textmaker.uima.types.DefinitionAnnotation;
import org.quizreader.textmaker.uima.types.FileAnnotation;
import org.quizreader.textmaker.wiktionary.model.Entry;

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
		AnnotationIndex<Annotation> fileIndex = aJCas.getAnnotationIndex(FileAnnotation.type);

		for (Annotation fanno : fileIndex) {

			FileAnnotation fileAnno = (FileAnnotation) fanno;

			if (!fileAnno.getOutput() || !fileAnno.getWithinRange()) {
				continue;
			}

			FSIterator<Annotation> defAnnoIterator = defIndex.subiterator(fileAnno);
			Set<String> entries = new HashSet<String>();

			while (defAnnoIterator.hasNext()) {
				Annotation anno = defAnnoIterator.next();
				String word = ((DefinitionAnnotation) anno).getWord();
				if (word == null) {
					word = anno.getCoveredText();
				}
				if (!entries.contains(word)) {
					Entry entry = wiktionary.getEntry(word);
					try {
						writeDefinition(word, entry);
						entries.add(word);
						String root = getRoot(entry);
						if (root != null && !entries.contains(root)) {
							Entry rootEntry = wiktionary.getEntry(root);
							writeDefinition(root, rootEntry);
							entries.add(root);
						}
					} catch (IOException e) {
						throw new AnalysisEngineProcessException(e);
					}
				}
			}
		}
	}

	private String getRoot(Entry entry) {
		if (entry == null || entry.getDefinitions().size() == 0) {
			return null;
		}
		String root = entry.getDefinitions().get(0).getRoot();
		if (root == null || root.length() == 0) {
			return null;
		}
		return root;
	}

	private String path(String word) {
		return word.length() == 1 ? word : word.substring(0, 2);
	}

	// TODO: write definitions only
	private void writeDefinition(String word, Entry entry) throws IOException {
		File file = new File(definitionPath + path(word), word + ".json");
		if (!file.exists()) {
			System.out.println("writing file: " + file.getName());
			final ObjectWriter w = objectMapper.writer();
			if (entry == null) {
				entry = new Entry();
				entry.setWord(word);
			}
			file.getParentFile().mkdirs();
			w.writeValue(file, entry);
		}
	}
}
