package io.github.astrapi69;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.*;

import io.github.astrapi69.swing.menu.MenuExtensions;
import io.github.astrapi69.swing.menu.MenuFactory;

public class MenuFactoryAndExtensionsTest extends JFrame
{
	public MenuFactoryAndExtensionsTest()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JMenuBar bar = MenuFactory.newJMenuBar();
		JMenu menu = MenuFactory.newJMenu("File", 'f');
		bar.add(menu);

		MenuExtensions.addMenuItem(menu, MenuFactory.newJMenuItem("Exit", 'x'),
			e -> System.exit(0));

		menu = MenuFactory.newJMenu("Edit", 'e');
		bar.add(menu);
		ActionListener printToSop = e -> System.out.println(e.getActionCommand());
		MenuExtensions.addMenuItem(menu, new JMenuItem("Cut", 't'),
			KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.CTRL_MASK), printToSop);
		MenuExtensions.addMenuItem(menu, new JMenuItem("Copy", 'c'),
			KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK), printToSop);
		MenuExtensions.addMenuItem(menu, new JMenuItem("Paste", 'p'),
			KeyStroke.getKeyStroke(KeyEvent.VK_V, Event.CTRL_MASK), printToSop);

		setJMenuBar(bar);
		getContentPane().add(new JLabel("A label"));

		pack();
		setSize(400, 400);
		setVisible(true);
	}

	public static void main(String arg[])
	{
		new MenuFactoryAndExtensionsTest();
	}
}
