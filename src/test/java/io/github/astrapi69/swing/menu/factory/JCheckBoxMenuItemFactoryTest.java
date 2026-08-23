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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.KeyStroke;

import org.junit.jupiter.api.Test;

import io.github.astrapi69.swing.menu.model.KeyStrokeInfo;
import io.github.astrapi69.swing.menu.model.MenuItemInfo;

/**
 * The unit test class for the class {@link JCheckBoxMenuItemFactory}
 */
class JCheckBoxMenuItemFactoryTest
{

	@Test
	void newJCheckBoxMenuItem()
	{
		List<String> fired = new ArrayList<>();
		KeyStroke ctrlB = KeyStroke.getKeyStroke("ctrl B");
		MenuItemInfo info = MenuItemInfo.builder().name("view.statusbar").text("Statusbar")
			.toolTip("Toggle the status bar").mnemonic(KeyEvent.VK_S)
			.keyStrokeInfo(KeyStrokeInfo.toKeyStrokeInfo(ctrlB)).actionCommand("toggleStatusbar")
			.actionListener(e -> fired.add(e.getActionCommand())).selected(true).build();

		JCheckBoxMenuItem menuItem = JCheckBoxMenuItemFactory.newJCheckBoxMenuItem(info);

		assertEquals("view.statusbar", menuItem.getName());
		assertEquals("Statusbar", menuItem.getText());
		assertEquals("Toggle the status bar", menuItem.getToolTipText());
		assertEquals(KeyEvent.VK_S, menuItem.getMnemonic());
		assertEquals(ctrlB, menuItem.getAccelerator());
		assertEquals("toggleStatusbar", menuItem.getActionCommand());
		assertTrue(menuItem.isSelected());
		assertTrue(menuItem.isEnabled());
		// a click toggles the selection and notifies the listener
		menuItem.doClick();
		assertFalse(menuItem.isSelected());
		assertEquals(List.of("toggleStatusbar"), fired);
		// every call creates a new instance
		assertNotSame(menuItem, JCheckBoxMenuItemFactory.newJCheckBoxMenuItem(info));
	}

	@Test
	void newJCheckBoxMenuItemDefaults()
	{
		JCheckBoxMenuItem menuItem = JCheckBoxMenuItemFactory
			.newJCheckBoxMenuItem(MenuItemInfo.builder().text("Mute").build());

		assertEquals("Mute", menuItem.getText());
		assertFalse(menuItem.isSelected());
		assertTrue(menuItem.isEnabled());
		assertEquals(0, menuItem.getActionListeners().length);
		JCheckBoxMenuItem disabled = JCheckBoxMenuItemFactory
			.newJCheckBoxMenuItem(MenuItemInfo.builder().text("Mute").enabled(false).build());
		assertFalse(disabled.isEnabled());
		assertThrows(NullPointerException.class,
			() -> JCheckBoxMenuItemFactory.newJCheckBoxMenuItem(null));
	}
}
