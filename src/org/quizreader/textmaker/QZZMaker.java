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

package org.quizreader.textmaker;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.quizreader.textmaker.wiktionary.Entry;
import org.quizreader.textmaker.wiktionary.Wiktionary;

public class QZZMaker {

	static final int BUFFER_SIZE = 2048;

	public static void main(String argv[]) throws Exception {
		// new QZZMaker().countXml();
		zipFolder();
	}

	void countXml() throws JAXBException {
		File dir = new File(".");
		FileFilter fileFilter = new FileFilter() {
			public boolean accept(File file) {
				return file.getName().endsWith(".txt.xml");
			}
		};
		File[] files = dir.listFiles(fileFilter);
		Map<String, Integer> countMap = new HashMap<String, Integer>();
		JAXBContext jaxbContext = JAXBContext.newInstance(Wiktionary.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		for (File file : files) {
			Wiktionary wikt = (Wiktionary) jaxbUnmarshaller.unmarshal(file);
			for (Entry entry : wikt.getEntries()) {
				String word = entry.getWord();
				if (countMap.get(word) == null) {
					countMap.put(word, 1);
				}
				else {
					countMap.put(word, countMap.get(word) + 1);
				}
			}
		}
		for (String word : countMap.keySet()) {
			if (countMap.get(word) > 1) {
				System.out.println(word + ": " + countMap.get(word));
			}
		}
	}

	private static void zipFolder() {
		try {
			FileOutputStream dest = new FileOutputStream("foo.qzz");
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
			// out.setMethod(ZipOutputStream.DEFLATED);
			// get a list of files from current directory
			zipFile(out, "meta.xml");
			zipFile(out, "common.xml");
			zipFile(out, "lreln_chap1.txt.html");
			zipFile(out, "lreln_chap1.txt.xml");
			zipFile(out, "lreln_chap2.txt.html");
			zipFile(out, "lreln_chap2.txt.xml");
			zipFile(out, "lreln_chap28.txt.html");
			zipFile(out, "lreln_chap28.txt.xml");

			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void zipFile(ZipOutputStream out, String filename) throws FileNotFoundException, IOException {
		byte data[] = new byte[BUFFER_SIZE];
		File file = new File(filename);
		FileInputStream fi = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(fi, BUFFER_SIZE);
		ZipEntry entry = new ZipEntry(filename);
		out.putNextEntry(entry);
		int count;
		while ((count = bis.read(data, 0, BUFFER_SIZE)) != -1) {
			out.write(data, 0, count);
		}
		bis.close();
	}
}
