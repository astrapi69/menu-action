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
package io.github.astrapi69.swing.menu.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;

import org.junit.jupiter.api.Test;

import io.github.astrapi69.swing.menu.model.MenuItemInfo;

/**
 * The unit test class for the class {@link JMenuFactory}
 */
class JMenuFactoryTest
{

	private static ActionListener recording(final List<String> fired)
	{
		return e -> fired.add(e.getActionCommand());
	}

	private static void fire(final JMenu menu)
	{
		ActionEvent event = new ActionEvent(menu, ActionEvent.ACTION_PERFORMED,
			menu.getActionCommand());
		for (ActionListener listener : menu.getActionListeners())
		{
			listener.actionPerformed(event);
		}
	}

	@Test
	void newJMenuWithText()
	{
		JMenu menu = JMenuFactory.newJMenu("File");

		assertEquals("File", menu.getText());
		assertEquals(0, menu.getMnemonic());
		assertEquals(0, menu.getItemCount());
		assertEquals(0, menu.getActionListeners().length);
		assertNull(menu.getName());
		assertNotSame(menu, JMenuFactory.newJMenu("File"));
		assertThrows(NullPointerException.class, () -> JMenuFactory.newJMenu((String)null));
	}

	@Test
	void newJMenuFromMenuItemInfo()
	{
		List<String> fired = new ArrayList<>();
		MenuItemInfo info = MenuItemInfo.builder().name("global.menu.file").text("File")
			.toolTip("The file menu").mnemonic(KeyEvent.VK_F).actionCommand("file")
			.actionListener(recording(fired)).enabled(false).build();

		JMenu menu = JMenuFactory.newJMenu(info);

		assertEquals("global.menu.file", menu.getName());
		assertEquals("File", menu.getText());
		assertEquals("The file menu", menu.getToolTipText());
		assertEquals(KeyEvent.VK_F, menu.getMnemonic());
		assertEquals("file", menu.getActionCommand());
		assertFalse(menu.isEnabled());
		fire(menu);
		assertEquals(List.of("file"), fired);
		assertThrows(NullPointerException.class, () -> JMenuFactory.newJMenu((MenuItemInfo)null));
	}

	@Test
	void newJMenuWithTextAndIntMnemonic()
	{
		JMenu menu = JMenuFactory.newJMenu("Edit", KeyEvent.VK_E);

		assertEquals("Edit", menu.getText());
		assertEquals(KeyEvent.VK_E, menu.getMnemonic());
		assertEquals(0, menu.getActionListeners().length);
		assertThrows(NullPointerException.class, () -> JMenuFactory.newJMenu(null, KeyEvent.VK_E));
	}

	@Test
	void newJMenuWithTextIntMnemonicAndActionListener()
	{
		List<String> fired = new ArrayList<>();
		ActionListener listener = recording(fired);

		JMenu menu = JMenuFactory.newJMenu("Edit", KeyEvent.VK_E, listener);

		assertEquals("Edit", menu.getText());
		assertEquals(KeyEvent.VK_E, menu.getMnemonic());
		assertSame(listener, menu.getActionListeners()[0]);
		fire(menu);
		// the action command falls back to the text
		assertEquals(List.of("Edit"), fired);
		assertThrows(NullPointerException.class,
			() -> JMenuFactory.newJMenu("Edit", KeyEvent.VK_E, null));
		assertThrows(NullPointerException.class,
			() -> JMenuFactory.newJMenu(null, KeyEvent.VK_E, listener));
	}

	@Test
	void newJMenuWithTextAndCharMnemonic()
	{
		JMenu menu = JMenuFactory.newJMenu("View", 'v');

		assertEquals("View", menu.getText());
		// the lower case mnemonic is converted to the key code
		assertEquals(KeyEvent.VK_V, menu.getMnemonic());
		assertEquals(KeyEvent.VK_V, JMenuFactory.newJMenu("View", 'V').getMnemonic());
		assertThrows(NullPointerException.class, () -> JMenuFactory.newJMenu(null, 'v'));
	}

	@Test
	void newJMenuWithTextCharMnemonicAndActionListener()
	{
		List<String> fired = new ArrayList<>();
		ActionListener listener = recording(fired);

		JMenu menu = JMenuFactory.newJMenu("Help", 'h', listener);

		assertEquals("Help", menu.getText());
		assertEquals(KeyEvent.VK_H, menu.getMnemonic());
		assertSame(listener, menu.getActionListeners()[0]);
		fire(menu);
		assertEquals(List.of("Help"), fired);
		assertThrows(NullPointerException.class, () -> JMenuFactory.newJMenu("Help", 'h', null));
		assertThrows(NullPointerException.class, () -> JMenuFactory.newJMenu(null, 'h', listener));
	}
}
