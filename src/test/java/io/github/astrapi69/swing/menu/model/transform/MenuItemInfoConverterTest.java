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
package io.github.astrapi69.swing.menu.model.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import org.junit.jupiter.api.Test;

import io.github.astrapi69.swing.menu.MenuExtensions;
import io.github.astrapi69.swing.menu.enumeration.BaseMenuId;
import io.github.astrapi69.swing.menu.enumeration.MenuType;
import io.github.astrapi69.swing.menu.model.KeyStrokeInfo;
import io.github.astrapi69.swing.menu.model.MenuInfo;
import io.github.astrapi69.swing.menu.model.MenuItemInfo;

/**
 * The unit test class for the class {@link MenuItemInfoConverter}
 */
class MenuItemInfoConverterTest
{

	private static final ActionListener NO_ACTION = e -> {
	};

	@Test
	void fromJMenu()
	{
		MenuInfo editMenuInfo = MenuInfo.builder().type(MenuType.MENU)
			.mnemonic(MenuExtensions.toMnemonic('E')).actionCommand("Edit").text("Edit")
			.name(BaseMenuId.EDIT.propertiesKey()).build();
		JMenu menu = MenuItemInfoConverter.toMenuItemInfo(editMenuInfo, NO_ACTION).toJMenu();
		MenuInfo menuInfo = MenuItemInfoConverter.fromJMenu(menu);
		assertEquals(editMenuInfo, menuInfo);
	}

	@Test
	void fromJMenuBar()
	{
		MenuInfo menuInfo = MenuItemInfoConverter.fromJMenuBar();
		assertEquals(MenuType.MENU_BAR, menuInfo.getType());
		assertEquals(BaseMenuId.MENU_BAR.propertiesKey(), menuInfo.getName());
		assertNull(menuInfo.getActionId());

		JMenuBar menuBar = new JMenuBar();
		assertEquals(BaseMenuId.MENU_BAR.propertiesKey(),
			MenuItemInfoConverter.fromJMenuBar(menuBar).getName());
		menuBar.setName("custom.bar");
		assertEquals("custom.bar", MenuItemInfoConverter.fromJMenuBar(menuBar).getName());
	}

	@Test
	void fromJMenuItem()
	{
		MenuItemInfo helpContentMenuInfo = MenuItemInfo.builder().type(MenuType.MENU_ITEM)
			.mnemonic(MenuExtensions.toMnemonic('C'))
			.keyStrokeInfo(
				KeyStrokeInfo.toKeyStrokeInfo(KeyStroke.getKeyStroke("ctrl alt pressed H")))
			.actionCommand("Help Content").text("Help Content")
			.name(BaseMenuId.HELP_CONTENT.propertiesKey()).build();
		JMenuItem menu = MenuItemInfoConverter.toMenuItemInfo(helpContentMenuInfo, NO_ACTION)
			.toJMenuItem();
		assertEquals(1, menu.getActionListeners().length);
		MenuItemInfo menuInfo = MenuItemInfoConverter.fromJMenuItem(menu);
		assertEquals(helpContentMenuInfo, menuInfo);
	}

	@Test
	void fromJCheckBoxMenuItem()
	{
		MenuItemInfo donateMenuInfo = MenuItemInfo.builder().type(MenuType.CHECK_BOX_MENU_ITEM)
			.mnemonic(MenuExtensions.toMnemonic('Y'))
			.keyStrokeInfo(KeyStrokeInfo.toKeyStrokeInfo(KeyStroke.getKeyStroke("ctrl pressed Y")))
			.actionCommand("Yes").text("Yes").name(BaseMenuId.HELP_DONATE.propertiesKey()).build();
		JCheckBoxMenuItem menu = MenuItemInfoConverter.toMenuItemInfo(donateMenuInfo, NO_ACTION)
			.toJCheckBoxMenuItem();
		assertEquals(KeyStroke.getKeyStroke("ctrl pressed Y"), menu.getAccelerator());
		MenuItemInfo menuInfo = MenuItemInfoConverter.fromJCheckBoxMenuItem(menu);
		assertEquals(donateMenuInfo, menuInfo);
	}

	@Test
	void fromJRadioButtonMenuItem()
	{
		MenuItemInfo donateMenuInfo = MenuItemInfo.builder().type(MenuType.RADIO_BUTTON_MENU_ITEM)
			.mnemonic(MenuExtensions.toMnemonic('R'))
			.keyStrokeInfo(KeyStrokeInfo.toKeyStrokeInfo(KeyStroke.getKeyStroke("ctrl pressed R")))
			.actionCommand("Donate").text("Donate").name(BaseMenuId.HELP_DONATE.propertiesKey())
			.build();
		JRadioButtonMenuItem menu = MenuItemInfoConverter.toMenuItemInfo(donateMenuInfo, NO_ACTION)
			.toJRadioButtonMenuItem();
		MenuItemInfo menuInfo = MenuItemInfoConverter.fromJRadioButtonMenuItem(menu);
		assertEquals(donateMenuInfo, menuInfo);
	}

	@Test
	void toMenuItemInfoCopiesAllFields()
	{
		MenuInfo menuInfo = MenuInfo.builder().type(MenuType.MENU_ITEM).name("n").text("t")
			.toolTip("tt").mnemonic((int)'N').actionCommand("ac").enabled(false).selected(true)
			.icon("icons/missing.png").build();
		MenuItemInfo itemInfo = menuInfo.toMenuItemInfo(NO_ACTION);
		assertEquals("n", itemInfo.getName());
		assertEquals("t", itemInfo.getText());
		assertEquals("tt", itemInfo.getToolTip());
		assertEquals((int)'N', itemInfo.getMnemonic());
		assertEquals("ac", itemInfo.getActionCommand());
		assertEquals(Boolean.FALSE, itemInfo.getEnabled());
		assertEquals(Boolean.TRUE, itemInfo.getSelected());
		assertNull(itemInfo.getIcon());
		assertEquals(NO_ACTION, itemInfo.getActionListener());

		MenuItemInfo withOtherListener = MenuItemInfoConverter.toMenuItemInfo(itemInfo, e -> {
		});
		assertNotNull(withOtherListener.getActionListener());
		assertEquals("n", withOtherListener.getName());
	}

	@Test
	void resolveIcon()
	{
		assertNull(MenuItemInfoConverter.resolveIcon(null));
		assertNull(MenuItemInfoConverter.resolveIcon(" "));
		assertNull(MenuItemInfoConverter.resolveIcon("icons/missing.png"));
		Icon icon = MenuItemInfoConverter.resolveIcon("/icons/dot.png");
		assertNotNull(icon);
		assertEquals(2, icon.getIconWidth());
	}
}
