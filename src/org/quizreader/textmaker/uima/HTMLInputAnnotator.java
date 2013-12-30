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

package org.quizreader.textmaker.uima;

import java.io.File;
import java.util.Stack;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.TextExtractingVisitor;
import org.quizreader.textmaker.uima.types.FileAnnotation;
import org.quizreader.textmaker.uima.types.HTMLAnnotation;

public class HTMLInputAnnotator extends JCasAnnotator_ImplBase {

	public void process(final JCas aJCas) {
		Logger logger = getContext().getLogger();
		try {
			// multi-view component - must explicitly grab initial view
			JCas view = aJCas.getView("_InitialView");
			// parse html and create new plainTextView			
			String documentText = view.getDocumentText();
			Parser parser = new Parser(documentText);
			JCas plainTextView = aJCas.createView("textView");
			TextExtractingVisitor visitor = new HTMLVisitor(plainTextView);
			parser.visitAllNodesWith(visitor);
			String textInPage = visitor.getExtractedText();
			plainTextView.setDocumentText(textInPage);
			// add a file annotation to the new view
			File inputFile = UimaUtil.getInputFile(view);
			FileAnnotation fileAnno = new FileAnnotation(plainTextView);
			fileAnno.setFileName(inputFile.getName());
			fileAnno.setOutput(false);
			fileAnno.addToIndexes();
		} catch (ParserException e) {
			logger.log(Level.SEVERE, e.toString());
			e.printStackTrace();
		} catch (CASException e) {
			logger.log(Level.SEVERE, e.toString());
			e.printStackTrace();
		}
	}

	class HTMLVisitor extends TextExtractingVisitor {

		private JCas aJCas;
		private Stack<HTMLAnnotation> annoStack;

		public HTMLVisitor(JCas aJCas) {
			this.aJCas = aJCas;
			annoStack = new Stack<HTMLAnnotation>();
		}

		public void visitTag(Tag tag) {
			HTMLAnnotation htmlAnno = new HTMLAnnotation(aJCas);
			htmlAnno.setName(tag.getTagName());
			int length = getExtractedText().length();
			htmlAnno.setBegin(length);
			annoStack.push(htmlAnno);
		}

		public void visitEndTag(Tag tag) {
			HTMLAnnotation htmlAnno = annoStack.pop();
			while (!tag.getTagName().equals(htmlAnno.getName())) {
				if (annoStack.empty()) {
					System.err.println("error: no matching start tag for tag: " + tag);
					return;
				}
				htmlAnno = annoStack.pop();
			}
			htmlAnno.setEnd(getExtractedText().length());
			htmlAnno.addToIndexes();
		}
	}
}
