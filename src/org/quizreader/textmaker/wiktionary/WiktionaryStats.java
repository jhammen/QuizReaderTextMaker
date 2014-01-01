package org.quizreader.textmaker.wiktionary;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class WiktionaryStats {

	public void runStats(String path) throws IOException, SAXException, ParserConfigurationException {
		// final PrintStream out = new PrintStream("stats.txt"); // System.out;
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();

		final Stack<Integer> startStack = new Stack<Integer>();

		DefaultHandler handler = new DefaultHandler() {
			private Locator locator;

			@Override
			public void setDocumentLocator(Locator locator) {
				this.locator = locator;
			}

			@Override
			public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
				if ("page".equals(qName)) {
					startStack.push(locator.getLineNumber());
				}
			}

			@Override
			public void endElement(String uri, String localName, String qName) {
				if ("page".equals(qName)) {
					int start = startStack.pop();
					int end = locator.getLineNumber();
					if ((end - start) > 0) {
						System.out.println(qName + " covers lines: " + start + " to " + end);
					}
				}
			}
		};
		FileInputStream fis = new FileInputStream(path);
		parser.parse(fis, handler);
		fis.close();
		// out.close();
	}

	public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
		new WiktionaryStats().runStats(args[0]);
	}

}
