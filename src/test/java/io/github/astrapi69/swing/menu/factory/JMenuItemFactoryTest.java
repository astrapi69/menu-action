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

import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.junit.jupiter.api.Test;

import io.github.astrapi69.swing.menu.model.KeyStrokeInfo;
import io.github.astrapi69.swing.menu.model.MenuItemInfo;

/**
 * The unit test class for the class {@link JMenuItemFactory}
 */
class JMenuItemFactoryTest
{

	private static final KeyStroke CTRL_S = KeyStroke.getKeyStroke("ctrl S");

	private static ActionListener recording(final List<String> fired)
	{
		return e -> fired.add(e.getActionCommand());
	}

	@Test
	void newJMenuItemFromMenuItemInfo()
	{
		List<String> fired = new ArrayList<>();
		MenuItemInfo info = MenuItemInfo.builder().name("file.save").text("Save")
			.toolTip("Save the file").mnemonic(KeyEvent.VK_S)
			.keyStrokeInfo(KeyStrokeInfo.toKeyStrokeInfo(CTRL_S)).actionCommand("save")
			.actionListener(recording(fired)).enabled(false).build();

		JMenuItem menuItem = JMenuItemFactory.newJMenuItem(info);

		assertEquals("file.save", menuItem.getName());
		assertEquals("Save", menuItem.getText());
		assertEquals("Save the file", menuItem.getToolTipText());
		assertEquals(KeyEvent.VK_S, menuItem.getMnemonic());
		assertEquals(CTRL_S, menuItem.getAccelerator());
		assertEquals("save", menuItem.getActionCommand());
		assertFalse(menuItem.isEnabled());
		assertEquals(1, menuItem.getActionListeners().length);
		// every call creates a new instance
		assertNotSame(menuItem, JMenuItemFactory.newJMenuItem(info));
		assertThrows(NullPointerException.class,
			() -> JMenuItemFactory.newJMenuItem((MenuItemInfo)null));
	}

	@Test
	void newJMenuItemWithText()
	{
		JMenuItem menuItem = JMenuItemFactory.newJMenuItem("Open");

		assertEquals("Open", menuItem.getText());
		assertEquals(0, menuItem.getMnemonic());
		assertNull(menuItem.getAccelerator());
		assertNull(menuItem.getName());
		assertEquals(0, menuItem.getActionListeners().length);
		assertThrows(NullPointerException.class, () -> JMenuItemFactory.newJMenuItem((String)null));
	}

	@Test
	void newJMenuItemWithTextAndActionListener()
	{
		List<String> fired = new ArrayList<>();
		ActionListener listener = recording(fired);

		JMenuItem menuItem = JMenuItemFactory.newJMenuItem("Open", listener);

		assertEquals("Open", menuItem.getText());
		assertSame(listener, menuItem.getActionListeners()[0]);
		menuItem.doClick();
		// the action command falls back to the text
		assertEquals(List.of("Open"), fired);
		assertThrows(NullPointerException.class,
			() -> JMenuItemFactory.newJMenuItem("Open", (ActionListener)null));
		assertThrows(NullPointerException.class,
			() -> JMenuItemFactory.newJMenuItem(null, listener));
	}

	@Test
	void newJMenuItemWithTextAndIntMnemonic()
	{
		JMenuItem menuItem = JMenuItemFactory.newJMenuItem("File", KeyEvent.VK_F);

		assertEquals("File", menuItem.getText());
		assertEquals(KeyEvent.VK_F, menuItem.getMnemonic());
		assertNull(menuItem.getAccelerator());
		assertThrows(NullPointerException.class,
			() -> JMenuItemFactory.newJMenuItem(null, KeyEvent.VK_F));
	}

	@Test
	void newJMenuItemWithIntMnemonicAndCharAccelerator()
	{
		JMenuItem menuItem = JMenuItemFactory.newJMenuItem("Save", KeyEvent.VK_S, 'S');

		assertEquals("Save", menuItem.getText());
		assertEquals(KeyEvent.VK_S, menuItem.getMnemonic());
		// the accelerator is combined with CTRL
		assertEquals(CTRL_S, menuItem.getAccelerator());
		assertEquals(KeyEvent.VK_S, menuItem.getAccelerator().getKeyCode());
		assertEquals(InputEvent.CTRL_DOWN_MASK,
			menuItem.getAccelerator().getModifiers() & InputEvent.CTRL_DOWN_MASK);
		assertEquals(0, menuItem.getActionListeners().length);
		assertThrows(NullPointerException.class,
			() -> JMenuItemFactory.newJMenuItem(null, KeyEvent.VK_S, 'S'));
	}

	@Test
	void newJMenuItemWithIntMnemonicCharAcceleratorAndActionListener()
	{
		List<String> fired = new ArrayList<>();
		ActionListener listener = recording(fired);

		JMenuItem menuItem = JMenuItemFactory.newJMenuItem("Save", KeyEvent.VK_S, 'S', listener);

		assertEquals("Save", menuItem.getText());
		assertEquals(KeyEvent.VK_S, menuItem.getMnemonic());
		assertEquals(CTRL_S, menuItem.getAccelerator());
		assertSame(listener, menuItem.getActionListeners()[0]);
		menuItem.doClick();
		assertEquals(List.of("Save"), fired);
		assertThrows(NullPointerException.class,
			() -> JMenuItemFactory.newJMenuItem("Save", KeyEvent.VK_S, 'S', null));
	}

	@Test
	void newJMenuItemWithIntMnemonicKeyStrokeAndActionListener()
	{
		List<String> fired = new ArrayList<>();
		ActionListener listener = recording(fired);
		KeyStroke altF4 = KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK);

		JMenuItem menuItem = JMenuItemFactory.newJMenuItem("Exit", KeyEvent.VK_E, altF4, listener);

		assertEquals("Exit", menuItem.getText());
		assertEquals(KeyEvent.VK_E, menuItem.getMnemonic());
		assertEquals(altF4, menuItem.getAccelerator());
		assertNull(menuItem.getName());
		menuItem.doClick();
		assertEquals(List.of("Exit"), fired);
		assertThrows(NullPointerException.class,
			() -> JMenuItemFactory.newJMenuItem("Exit", KeyEvent.VK_E, altF4, null));
		assertThrows(NullPointerException.class,
			() -> JMenuItemFactory.newJMenuItem("Exit", KeyEvent.VK_E, null, listener));
	}

	@Test
	void newJMenuItemWithIntMnemonicKeyStrokeActionListenerAndName()
	{
		List<String> fired = new ArrayList<>();
		ActionListener listener = recording(fired);
		KeyStroke altF4 = KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK);

		JMenuItem menuItem = JMenuItemFactory.newJMenuItem("Exit", KeyEvent.VK_E, altF4, listener,
			"global.menu.file.exit");

		assertEquals("global.menu.file.exit", menuItem.getName());
		assertEquals("Exit", menuItem.getText());
		assertEquals(KeyEvent.VK_E, menuItem.getMnemonic());
		assertEquals(altF4, menuItem.getAccelerator());
		menuItem.doClick();
		assertEquals(List.of("Exit"), fired);

		// the action listener and the name are optional
		JMenuItem withoutListener = JMenuItemFactory.newJMenuItem("Exit", KeyEvent.VK_E, altF4,
			null, null);
		assertEquals(0, withoutListener.getActionListeners().length);
		assertNull(withoutListener.getName());
		assertEquals(altF4, withoutListener.getAccelerator());
		// the keystroke is mandatory
		assertThrows(NullPointerException.class, () -> JMenuItemFactory.newJMenuItem("Exit",
			KeyEvent.VK_E, null, listener, "global.menu.file.exit"));
	}

	@Test
	void newJMenuItemWithCharMnemonicAndCharAccelerator()
	{
		JMenuItem menuItem = JMenuItemFactory.newJMenuItem("Save", 's', 'S');

		assertEquals("Save", menuItem.getText());
		// the lower case mnemonic is converted to the key code
		assertEquals(KeyEvent.VK_S, menuItem.getMnemonic());
		assertEquals(CTRL_S, menuItem.getAccelerator());
		assertEquals(0, menuItem.getActionListeners().length);

		JMenuItem upperCase = JMenuItemFactory.newJMenuItem("Save", 'S', 'S');
		assertEquals(KeyEvent.VK_S, upperCase.getMnemonic());
		assertThrows(NullPointerException.class,
			() -> JMenuItemFactory.newJMenuItem(null, 's', 'S'));
	}

	@Test
	void newJMenuItemWithCharMnemonicCharAcceleratorAndActionListener()
	{
		List<String> fired = new ArrayList<>();
		ActionListener listener = recording(fired);

		JMenuItem menuItem = JMenuItemFactory.newJMenuItem("Save", 's', 'S', listener);

		assertEquals("Save", menuItem.getText());
		assertEquals(KeyEvent.VK_S, menuItem.getMnemonic());
		assertEquals(CTRL_S, menuItem.getAccelerator());
		assertSame(listener, menuItem.getActionListeners()[0]);
		menuItem.doClick();
		assertEquals(List.of("Save"), fired);
		assertThrows(NullPointerException.class,
			() -> JMenuItemFactory.newJMenuItem("Save", 's', 'S', null));
	}
}
