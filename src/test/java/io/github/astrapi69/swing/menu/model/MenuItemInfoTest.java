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
package io.github.astrapi69.swing.menu.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.MenuItem;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import org.junit.jupiter.api.Test;

import io.github.astrapi69.swing.menu.MenuExtensions;
import io.github.astrapi69.swing.menu.enumeration.Anchor;
import io.github.astrapi69.swing.menu.enumeration.BaseMenuId;
import io.github.astrapi69.swing.menu.enumeration.MenuType;

/**
 * The unit test class for the class {@link MenuItemInfo}
 */
class MenuItemInfoTest
{

	private static final Icon ICON = new Icon()
	{
		@Override
		public void paintIcon(Component c, Graphics g, int x, int y)
		{
		}

		@Override
		public int getIconWidth()
		{
			return 4;
		}

		@Override
		public int getIconHeight()
		{
			return 4;
		}
	};

	private static final ActionListener LISTENER = e -> {
	};

	private static MenuItemInfo.MenuItemInfoBuilder fullBuilder()
	{
		return MenuItemInfo.builder().name("id").text("Text").toolTip("tip").mnemonic(KeyEvent.VK_T)
			.keyStrokeInfo(KeyStrokeInfo.builder().keystrokeAsString("ctrl pressed T").build())
			.type(MenuType.MENU_ITEM).anchor(Anchor.AFTER).relativeToMenuId("other")
			.actionCommand("cmd").actionListener(LISTENER).enabled(true).visible(true)
			.selected(false).icon(ICON).accessibleName("acc-name")
			.accessibleDescription("acc-desc");
	}

	private static MenuItemInfo newExitInfo()
	{
		return MenuItemInfo.builder().mnemonic(MenuExtensions.toMnemonic('E'))
			.keyStrokeInfo(
				KeyStrokeInfo.builder().keyCode(KeyEvent.VK_F4).modifiers(InputEvent.ALT_DOWN_MASK)
					.keystrokeAsString("alt pressed F4").onKeyRelease(false).build())
			.text("Exit").toolTip("Exit the app").name(BaseMenuId.EXIT.propertiesKey())
			.actionCommand("foo-action-command").enabled(false).build();
	}

	@Test
	void equalsAndHashCode()
	{
		assertEquals(newExitInfo(), newExitInfo());
		assertEquals(newExitInfo().hashCode(), newExitInfo().hashCode());
		assertNotEquals(newExitInfo(), MenuItemInfo.builder().text("Other").build());
	}

	@Test
	void toSwingComponents()
	{
		MenuItemInfo info = newExitInfo();

		JMenuItem menuItem = info.toJMenuItem();
		assertEquals("Exit", menuItem.getText());
		assertEquals("Exit the app", menuItem.getToolTipText());
		assertEquals(KeyEvent.VK_E, menuItem.getMnemonic());
		assertEquals(KeyStroke.getKeyStroke("alt F4"), menuItem.getAccelerator());
		assertEquals(BaseMenuId.EXIT.propertiesKey(), menuItem.getName());
		assertEquals("foo-action-command", menuItem.getActionCommand());
		assertFalse(menuItem.isEnabled());

		JMenu menu = info.toJMenu();
		assertEquals("Exit", menu.getText());
		assertNull(menu.getAccelerator());

		JCheckBoxMenuItem checkBox = MenuItemInfo.builder().text("c").selected(true).build()
			.toJCheckBoxMenuItem();
		assertTrue(checkBox.isSelected());
		JRadioButtonMenuItem radio = MenuItemInfo.builder().text("r").selected(true).build()
			.toJRadioButtonMenuItem();
		assertTrue(radio.isSelected());

		JMenuBar menuBar = info.toJMenuBar();
		assertEquals(BaseMenuId.EXIT.propertiesKey(), menuBar.getName());
	}

	@Test
	void equalsAndHashCodeWithAllFields()
	{
		MenuItemInfo first = fullBuilder().build();
		MenuItemInfo second = fullBuilder().build();
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, fullBuilder().visible(false).build());
		assertNotEquals(first, fullBuilder().visible(null).build());
		assertNotEquals(first, fullBuilder().selected(true).build());
		assertNotEquals(first, fullBuilder().enabled(false).build());
		assertNotEquals(first, fullBuilder().accessibleName("other-name").build());
		assertNotEquals(first, fullBuilder().accessibleDescription("other-desc").build());
		assertNotEquals(first, fullBuilder().icon(null).build());
		assertNotEquals(first, fullBuilder().anchor(Anchor.BEFORE).build());
		assertNotEquals(first, fullBuilder().type(MenuType.MENU).build());
		assertNotEquals(first, fullBuilder().relativeToMenuId("another").build());
		assertNotEquals(first, fullBuilder().actionListener(e -> {
		}).build());
		assertNotEquals(first, new MenuItemInfo());
		assertEquals(new MenuItemInfo(), MenuItemInfo.builder().build());
		assertEquals(new MenuItemInfo().hashCode(), MenuItemInfo.builder().build().hashCode());
	}

	@Test
	void toStringWithAllFields()
	{
		String string = fullBuilder().build().toString();
		assertTrue(string.startsWith("MenuItemInfo("), string);
		assertTrue(string.contains("name=id"), string);
		assertTrue(string.contains("text=Text"), string);
		assertTrue(string.contains("toolTip=tip"), string);
		assertTrue(string.contains("type=MENU_ITEM"), string);
		assertTrue(string.contains("anchor=AFTER"), string);
		assertTrue(string.contains("relativeToMenuId=other"), string);
		assertTrue(string.contains("actionCommand=cmd"), string);
		assertTrue(string.contains("enabled=true"), string);
		assertTrue(string.contains("visible=true"), string);
		assertTrue(string.contains("selected=false"), string);
		assertTrue(string.contains("accessibleName=acc-name"), string);
		assertTrue(string.contains("accessibleDescription=acc-desc"), string);
	}

	@Test
	void visibleAndAccessibleValuesAreApplied()
	{
		JMenuItem menuItem = fullBuilder().visible(false).build().toJMenuItem();
		assertFalse(menuItem.isVisible());
		assertEquals("acc-name", menuItem.getAccessibleContext().getAccessibleName());
		assertEquals("acc-desc", menuItem.getAccessibleContext().getAccessibleDescription());
		// a null visible value keeps the component visible
		assertTrue(fullBuilder().visible(null).build().toJMenuItem().isVisible());
	}

	@Test
	void toAwtMenuItem()
	{
		// the awt MenuItem is a heavyweight component and needs a display
		assumeFalse(GraphicsEnvironment.isHeadless());
		MenuItem awtItem = newExitInfo().toMenuItem();
		assertEquals("Exit", awtItem.getLabel());
		assertFalse(awtItem.isEnabled());
	}
}
