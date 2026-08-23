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

import javax.swing.ButtonGroup;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import org.junit.jupiter.api.Test;

import io.github.astrapi69.swing.menu.model.KeyStrokeInfo;
import io.github.astrapi69.swing.menu.model.MenuItemInfo;

/**
 * The unit test class for the class {@link JRadioButtonMenuItemFactory}
 */
class JRadioButtonMenuItemFactoryTest
{

	@Test
	void newJRadioButtonMenuItem()
	{
		List<String> fired = new ArrayList<>();
		KeyStroke ctrlD = KeyStroke.getKeyStroke("ctrl D");
		MenuItemInfo info = MenuItemInfo.builder().name("view.mode.desktop").text("Desktop")
			.toolTip("Desktop mode").mnemonic(KeyEvent.VK_D)
			.keyStrokeInfo(KeyStrokeInfo.toKeyStrokeInfo(ctrlD)).actionCommand("desktopMode")
			.actionListener(e -> fired.add(e.getActionCommand())).selected(true).build();

		JRadioButtonMenuItem menuItem = JRadioButtonMenuItemFactory.newJRadioButtonMenuItem(info);

		assertEquals("view.mode.desktop", menuItem.getName());
		assertEquals("Desktop", menuItem.getText());
		assertEquals("Desktop mode", menuItem.getToolTipText());
		assertEquals(KeyEvent.VK_D, menuItem.getMnemonic());
		assertEquals(ctrlD, menuItem.getAccelerator());
		assertEquals("desktopMode", menuItem.getActionCommand());
		assertTrue(menuItem.isSelected());
		assertTrue(menuItem.isEnabled());
		menuItem.doClick();
		assertEquals(List.of("desktopMode"), fired);
		// every call creates a new instance
		assertNotSame(menuItem, JRadioButtonMenuItemFactory.newJRadioButtonMenuItem(info));
	}

	@Test
	void newJRadioButtonMenuItemInButtonGroup()
	{
		JRadioButtonMenuItem desktop = JRadioButtonMenuItemFactory
			.newJRadioButtonMenuItem(MenuItemInfo.builder().text("Desktop").selected(true).build());
		JRadioButtonMenuItem panel = JRadioButtonMenuItemFactory
			.newJRadioButtonMenuItem(MenuItemInfo.builder().text("Panel").build());
		assertFalse(panel.isSelected());
		assertEquals(0, panel.getActionListeners().length);

		ButtonGroup group = new ButtonGroup();
		group.add(desktop);
		group.add(panel);
		panel.doClick();
		assertTrue(panel.isSelected());
		assertFalse(desktop.isSelected());

		JRadioButtonMenuItem disabled = JRadioButtonMenuItemFactory
			.newJRadioButtonMenuItem(MenuItemInfo.builder().text("Off").enabled(false).build());
		assertFalse(disabled.isEnabled());
		assertThrows(NullPointerException.class,
			() -> JRadioButtonMenuItemFactory.newJRadioButtonMenuItem(null));
	}
}
