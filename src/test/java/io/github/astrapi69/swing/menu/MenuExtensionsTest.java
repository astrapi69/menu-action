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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.junit.jupiter.api.Test;

/**
 * The unit test class for the class {@link MenuExtensions}
 */
class MenuExtensionsTest
{

	@Test
	void parseMnemonic()
	{
		MenuExtensions.TextWithMnemonic parsed = MenuExtensions.parseMnemonic("&File");
		assertEquals("File", parsed.text());
		assertEquals(KeyEvent.VK_F, parsed.mnemonic());

		parsed = MenuExtensions.parseMnemonic("Save &As...");
		assertEquals("Save As...", parsed.text());
		assertEquals(KeyEvent.VK_A, parsed.mnemonic());

		parsed = MenuExtensions.parseMnemonic("Tom && &Jerry");
		assertEquals("Tom & Jerry", parsed.text());
		assertEquals(KeyEvent.VK_J, parsed.mnemonic());

		parsed = MenuExtensions.parseMnemonic("No marker");
		assertEquals("No marker", parsed.text());
		assertNull(parsed.mnemonic());

		parsed = MenuExtensions.parseMnemonic("trailing &");
		assertEquals("trailing &", parsed.text());
		assertNull(parsed.mnemonic());

		parsed = MenuExtensions.parseMnemonic("& space");
		assertEquals("& space", parsed.text());
		assertNull(parsed.mnemonic());

		parsed = MenuExtensions.parseMnemonic(null);
		assertNull(parsed.text());
		assertNull(parsed.mnemonic());
	}

	@Test
	void toMnemonic()
	{
		assertEquals(KeyEvent.VK_F, MenuExtensions.toMnemonic('f'));
		assertEquals(KeyEvent.VK_F, MenuExtensions.toMnemonic('F'));
		assertEquals((int)'1', MenuExtensions.toMnemonic('1'));
	}

	@Test
	void toMnemonicOnTheExactLowerCaseBoundaries()
	{
		// 'a' and 'z' are the inclusive boundaries of the shifted range, '`' and '{' are the
		// characters immediately outside it and must be returned unchanged
		assertEquals((int)'A', MenuExtensions.toMnemonic('a'));
		assertEquals((int)'Z', MenuExtensions.toMnemonic('z'));
		assertEquals((int)'`', MenuExtensions.toMnemonic('`'));
		assertEquals((int)'{', MenuExtensions.toMnemonic('{'));
	}

	@Test
	void setAcceleratorWithKeyChar()
	{
		JMenuItem menuItem = new JMenuItem("Save");

		MenuExtensions.setAccelerator(menuItem, 'S');

		assertEquals(KeyStroke.getKeyStroke('S'), menuItem.getAccelerator());
		assertEquals('S', menuItem.getAccelerator().getKeyChar());
		assertEquals(KeyEvent.VK_UNDEFINED, menuItem.getAccelerator().getKeyCode());
	}

	@Test
	void setAcceleratorWithCharacterAndModifiers()
	{
		JMenuItem menuItem = new JMenuItem("Save");

		MenuExtensions.setAccelerator(menuItem, Character.valueOf('s'), InputEvent.CTRL_DOWN_MASK);

		assertEquals(KeyStroke.getKeyStroke(Character.valueOf('s'), InputEvent.CTRL_DOWN_MASK),
			menuItem.getAccelerator());
		assertEquals('s', menuItem.getAccelerator().getKeyChar());
		assertEquals(InputEvent.CTRL_DOWN_MASK,
			menuItem.getAccelerator().getModifiers() & InputEvent.CTRL_DOWN_MASK);
	}

	@Test
	void setAcceleratorWithKeyCodeAndModifiers()
	{
		JMenuItem menuItem = new JMenuItem("Save");

		MenuExtensions.setAccelerator(menuItem, KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK);

		assertEquals(KeyStroke.getKeyStroke("ctrl S"), menuItem.getAccelerator());
		assertEquals(KeyEvent.VK_S, menuItem.getAccelerator().getKeyCode());
	}

	@Test
	void setAcceleratorWithKeyCodeModifiersAndOnKeyRelease()
	{
		JMenuItem menuItem = new JMenuItem("Save");

		MenuExtensions.setAccelerator(menuItem, KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK, true);

		assertEquals(KeyStroke.getKeyStroke("ctrl released S"), menuItem.getAccelerator());
		assertEquals(true, menuItem.getAccelerator().isOnKeyRelease());

		MenuExtensions.setAccelerator(menuItem, KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK, false);
		assertEquals(false, menuItem.getAccelerator().isOnKeyRelease());
	}

	@Test
	void setAcceleratorWithKeyStroke()
	{
		JMenuItem menuItem = new JMenuItem("Exit");
		KeyStroke altF4 = KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK);

		MenuExtensions.setAccelerator(menuItem, altF4);

		assertEquals(altF4, menuItem.getAccelerator());
	}

	@Test
	void setAcceleratorWithParsableKeystrokeString()
	{
		JMenuItem menuItem = new JMenuItem("Save");

		MenuExtensions.setAccelerator(menuItem, "ctrl S");
		assertEquals(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK),
			menuItem.getAccelerator());

		// an unparsable keystroke string removes the accelerator
		MenuExtensions.setAccelerator(menuItem, "not a keystroke");
		assertNull(menuItem.getAccelerator());
	}

	@Test
	void setAltAccelerator()
	{
		JMenuItem menuItem = new JMenuItem("Exit");

		MenuExtensions.setAltAccelerator(menuItem, 'E');

		assertEquals(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.ALT_DOWN_MASK),
			menuItem.getAccelerator());
	}

	@Test
	void setCtrlAccelerator()
	{
		JMenuItem menuItem = new JMenuItem("Save");

		MenuExtensions.setCtrlAccelerator(menuItem, 'S');

		assertEquals(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK),
			menuItem.getAccelerator());
	}

	@Test
	void setMnemonicOverloads()
	{
		JMenu menu = new JMenu("File");
		MenuExtensions.setMnemonic(menu, 'f');
		// the lower case key char is converted to the key code
		assertEquals(KeyEvent.VK_F, menu.getMnemonic());
		MenuExtensions.setMnemonic(menu, KeyEvent.VK_I);
		assertEquals(KeyEvent.VK_I, menu.getMnemonic());

		JMenuItem menuItem = new JMenuItem("Save");
		MenuExtensions.setMnemonic(menuItem, 's');
		assertEquals(KeyEvent.VK_S, menuItem.getMnemonic());
		MenuExtensions.setMnemonic(menuItem, KeyEvent.VK_A);
		assertEquals(KeyEvent.VK_A, menuItem.getMnemonic());
	}

	@Test
	void addActionListener()
	{
		List<String> fired = new ArrayList<>();
		JMenuItem menuItem = new JMenuItem("Save");
		ActionListener listener = e -> fired.add(e.getActionCommand());

		MenuExtensions.addActionListener(menuItem, listener);

		assertSame(listener, menuItem.getActionListeners()[0]);
		menuItem.doClick();
		assertEquals(List.of("Save"), fired);
		assertThrows(NullPointerException.class,
			() -> MenuExtensions.addActionListener(menuItem, null));
		assertThrows(NullPointerException.class,
			() -> MenuExtensions.addActionListener(null, listener));
	}

	@Test
	void addMenuItem()
	{
		JMenu menu = new JMenu("File");
		JMenuItem menuItem = new JMenuItem("Save");

		JMenuItem added = MenuExtensions.addMenuItem(menu, menuItem);

		assertSame(menuItem, added);
		assertEquals(1, menu.getItemCount());
		assertSame(menuItem, menu.getItem(0));
		assertThrows(NullPointerException.class, () -> MenuExtensions.addMenuItem(menu, null));
		assertThrows(NullPointerException.class, () -> MenuExtensions.addMenuItem(null, menuItem));
	}

	@Test
	void addMenuItemWithActionListener()
	{
		List<String> fired = new ArrayList<>();
		JMenu menu = new JMenu("File");
		JMenuItem menuItem = new JMenuItem("Open");
		ActionListener listener = e -> fired.add(e.getActionCommand());

		JMenuItem added = MenuExtensions.addMenuItem(menu, menuItem, listener);

		assertSame(menuItem, added);
		assertSame(menuItem, menu.getItem(0));
		assertSame(listener, added.getActionListeners()[0]);
		added.doClick();
		assertEquals(List.of("Open"), fired);
		assertThrows(NullPointerException.class,
			() -> MenuExtensions.addMenuItem(menu, menuItem, (ActionListener)null));
	}

	@Test
	void addMenuItemWithKeyStrokeAndActionListener()
	{
		List<String> fired = new ArrayList<>();
		JMenu menu = new JMenu("File");
		JMenuItem menuItem = new JMenuItem("Save");
		KeyStroke ctrlS = KeyStroke.getKeyStroke("ctrl S");
		ActionListener listener = e -> fired.add(e.getActionCommand());

		JMenuItem added = MenuExtensions.addMenuItem(menu, menuItem, ctrlS, listener);

		assertSame(menuItem, added);
		assertSame(menuItem, menu.getItem(0));
		assertEquals(ctrlS, added.getAccelerator());
		added.doClick();
		assertEquals(List.of("Save"), fired);
		assertThrows(NullPointerException.class,
			() -> MenuExtensions.addMenuItem(menu, menuItem, null, listener));
		assertThrows(NullPointerException.class,
			() -> MenuExtensions.addMenuItem(menu, menuItem, ctrlS, null));
	}
}
