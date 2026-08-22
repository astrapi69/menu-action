/**
 * The MIT License
 *
 * Copyright (C) 2026 Asterios Raptis
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
package io.github.astrapi69.swing.menu;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;

import io.github.astrapi69.swing.action.ToggleFullScreenAction;
import io.github.astrapi69.swing.menu.build.ActionRegistry;
import io.github.astrapi69.swing.menu.build.MenuBuilder;
import io.github.astrapi69.swing.menu.build.MissingActionPolicy;
import io.github.astrapi69.swing.menu.xml.MenuXmlReader;

/**
 * Demo that loads the menu bar from the xml resource menubar.xml
 */
public class MenuXmlDemo extends JFrame
{

	private static final long serialVersionUID = 1L;

	public MenuXmlDemo()
	{
		super("menu-action xml demo");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ActionRegistry actions = ActionRegistry.empty().register("exit", e -> System.exit(0))
			.register("toggleFullscreen", new ToggleFullScreenAction("Toggle Fullscreen", this))
			.register("newFile", e -> System.out.println("new file"))
			.register("openFile", e -> System.out.println("open file"));
		MenuBuilder menuBuilder = new MenuBuilder(actions)
			.withMissingActionPolicy(MissingActionPolicy.DISABLE);
		JMenuBar menuBar = menuBuilder.buildMenuBar(MenuXmlReader.readResource("menubar.xml"));
		setJMenuBar(menuBar);
		getContentPane().add(new JLabel("Menu loaded from menubar.xml", JLabel.CENTER));
		setSize(500, 300);
		setLocationRelativeTo(null);
	}

	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(() -> new MenuXmlDemo().setVisible(true));
	}
}
