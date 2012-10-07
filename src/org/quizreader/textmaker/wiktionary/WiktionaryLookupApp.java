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

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class WiktionaryLookupApp {
	WiktionaryManager wiktionary = new WiktionaryManager();

	public static void main(String[] args) throws Exception {
		new WiktionaryLookupApp().run(args[0]);
	}

	public void run(String path) throws Exception {

		wiktionary.loadXML(path);

		final JFrame jFrame = new JFrame();
		jFrame.setLayout(new GridBagLayout());

		final JTextArea jTextArea = new JTextArea();
		jTextArea.setPreferredSize(new Dimension(350, 400));
		jFrame.add(jTextArea, new GBC(0, 1).setSpan(2, 1).setFill(GBC.BOTH));

		final JTextField lookupField = new JTextField();
		jFrame.add(lookupField, new GBC(0, 0).setWeight(1.0, 0.0).setFill(GBC.HORIZONTAL));
		JButton lookupButton = new JButton("Lookup");
		lookupButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String word = lookupField.getText();
				jTextArea.setText("");
				Entry entry = wiktionary.getEntry(word);
				if (entry == null) {
					jTextArea.setText("No definitions found");
				}
				else {
					jTextArea.setText(jTextArea.getText() + "\n* " + entry);
				}
				jFrame.pack();
			}
		});
		jFrame.add(lookupButton, new GBC(1, 0));
		jFrame.pack();
		jFrame.setVisible(true);

	}

}
