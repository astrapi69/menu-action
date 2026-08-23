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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.awt.GraphicsEnvironment;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.astrapi69.swing.menu.model.MenuItemInfo;

/**
 * The unit test class for the class {@link MenuItemFactory}
 */
class MenuItemFactoryTest
{

	@Test
	void newMenuItem()
	{
		// the awt MenuItem is a heavyweight component and needs a display
		assumeFalse(GraphicsEnvironment.isHeadless());
		List<String> fired = new ArrayList<>();
		ActionListener listener = e -> fired.add(e.getActionCommand());
		MenuItemInfo info = MenuItemInfo.builder().name("tray.exit").text("Exit")
			.mnemonic(KeyEvent.VK_E).actionCommand("exit").actionListener(listener).enabled(false)
			.build();

		MenuItem menuItem = MenuItemFactory.newMenuItem(info);

		assertEquals("tray.exit", menuItem.getName());
		assertEquals("Exit", menuItem.getLabel());
		assertEquals(KeyEvent.VK_E, menuItem.getShortcut().getKey());
		assertEquals("exit", menuItem.getActionCommand());
		assertFalse(menuItem.isEnabled());
		assertSame(listener, menuItem.getActionListeners()[0]);
		listener.actionPerformed(
			new ActionEvent(menuItem, ActionEvent.ACTION_PERFORMED, menuItem.getActionCommand()));
		assertEquals(List.of("exit"), fired);
		// every call creates a new instance
		assertNotSame(menuItem, MenuItemFactory.newMenuItem(info));

		MenuItem plain = MenuItemFactory.newMenuItem(MenuItemInfo.builder().text("Show").build());
		assertEquals("Show", plain.getLabel());
		assertNull(plain.getShortcut());
		assertTrue(plain.isEnabled());
		assertEquals(0, plain.getActionListeners().length);
	}

	@Test
	void newMenuItemWithNull()
	{
		assertThrows(NullPointerException.class, () -> MenuItemFactory.newMenuItem(null));
	}
}
