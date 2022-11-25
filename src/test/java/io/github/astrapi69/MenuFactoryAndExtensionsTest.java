/**
 * The MIT License
 *
 * Copyright (C) 2021 Asterios Raptis
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.github.astrapi69;

import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import io.github.astrapi69.swing.menu.MenuExtensions;
import io.github.astrapi69.swing.menu.MenuFactory;

public class MenuFactoryAndExtensionsTest extends JFrame
{
	public MenuFactoryAndExtensionsTest()
	{
		JMenuBar bar;
		JMenu menu;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		bar = MenuFactory.newJMenuBar();
		menu = MenuFactory.newJMenu("File", 'f');
		bar.add(menu);

		MenuExtensions.addMenuItem(menu, MenuFactory.newJMenuItem("Print to console"),
			e -> System.out.println("foo bar"));
		MenuExtensions.addMenuItem(menu, MenuFactory.newJMenuItem("Exit", 'x'),
			e -> System.exit(0));

		menu = MenuFactory.newJMenu("Edit", 'e');
		bar.add(menu);
		ActionListener printToSop = e -> System.out.println(e.getActionCommand());
		MenuExtensions.addMenuItem(menu, new JMenuItem("Cut", 't'),
			KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK), printToSop);
		MenuExtensions.addMenuItem(menu, new JMenuItem("Copy", 'c'),
			KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK), printToSop);
		JMenuItem pasteMenu = new JMenuItem("Paste", 'p');

		MenuExtensions.addMenuItem(menu, pasteMenu,
			KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK), printToSop);

		setJMenuBar(bar);
		getContentPane().add(new JLabel("A label"));

		pack();
		setSize(400, 400);
		setVisible(true);
	}

	public static void main(String[] arg)
	{
		new MenuFactoryAndExtensionsTest();
	}
}
