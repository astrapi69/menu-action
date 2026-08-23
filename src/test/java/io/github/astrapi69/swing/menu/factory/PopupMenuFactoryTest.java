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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.awt.GraphicsEnvironment;
import java.awt.PopupMenu;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.astrapi69.swing.menu.model.MenuItemInfo;

/**
 * The unit test class for the class {@link PopupMenuFactory}
 */
class PopupMenuFactoryTest
{

	@Test
	void newPopupMenu()
	{
		// the awt PopupMenu is a heavyweight component and needs a display
		assumeFalse(GraphicsEnvironment.isHeadless());
		ActionListener listener = e -> {
		};
		List<MenuItemInfo> infos = List.of(
			MenuItemInfo.builder().name("tray.show").text("Show").mnemonic(KeyEvent.VK_S)
				.actionListener(listener).build(),
			MenuItemInfo.builder().name("tray.exit").text("Exit").enabled(false).build());

		PopupMenu popupMenu = PopupMenuFactory.newPopupMenu(infos);

		assertEquals(2, popupMenu.getItemCount());
		assertEquals("Show", popupMenu.getItem(0).getLabel());
		assertEquals("tray.show", popupMenu.getItem(0).getName());
		assertEquals(KeyEvent.VK_S, popupMenu.getItem(0).getShortcut().getKey());
		assertSame(listener, popupMenu.getItem(0).getActionListeners()[0]);
		assertEquals("Exit", popupMenu.getItem(1).getLabel());
		assertFalse(popupMenu.getItem(1).isEnabled());
		// every call creates a new instance
		assertNotSame(popupMenu, PopupMenuFactory.newPopupMenu(infos));

		PopupMenu empty = PopupMenuFactory.newPopupMenu(List.of());
		assertEquals(0, empty.getItemCount());
	}

	@Test
	void newPopupMenuWithNullList()
	{
		assumeFalse(GraphicsEnvironment.isHeadless());
		assertThrows(NullPointerException.class, () -> PopupMenuFactory.newPopupMenu(null));
	}
}
