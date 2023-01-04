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
package io.github.astrapi69.swing.menu.model.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.junit.jupiter.api.Test;

import io.github.astrapi69.swing.action.NoAction;
import io.github.astrapi69.swing.menu.MenuExtensions;
import io.github.astrapi69.swing.menu.enumeration.BaseMenuId;
import io.github.astrapi69.swing.menu.enumeration.MenuType;
import io.github.astrapi69.swing.menu.model.KeyStrokeInfo;
import io.github.astrapi69.swing.menu.model.MenuInfo;
import io.github.astrapi69.swing.menu.model.MenuItemInfo;

class MenuItemInfoConverterTest
{

	@Test
	void fromJMenu()
	{
		MenuInfo editMenuInfo;
		MenuInfo menuInfo;
		MenuItemInfo menuItemInfo;
		JMenu menu;


		editMenuInfo = MenuInfo.builder().type(MenuType.MENU)
			.mnemonic(MenuExtensions.toMnemonic('E'))
			.keyStrokeInfo(KeyStrokeInfo.toKeyStrokeInfo(KeyStroke.getKeyStroke("alt pressed E")))
			.text("Edit").name(BaseMenuId.EDIT.propertiesKey()).build();

		menuItemInfo = editMenuInfo.toMenuItemInfo(new NoAction());

		menu = menuItemInfo.toJMenu();

		menuInfo = MenuItemInfoConverter.fromJMenu(menu);
		assertEquals(menuInfo, editMenuInfo);
	}

	@Test
	void fromJMenuItem()
	{
		MenuInfo helpContentMenuInfo;
		MenuInfo menuInfo;
		MenuItemInfo menuItemInfo;
		JMenuItem menu;


		helpContentMenuInfo = MenuInfo.builder().type(MenuType.MENU_ITEM)
			.mnemonic(MenuExtensions.toMnemonic('C'))
			.keyStrokeInfo(
				KeyStrokeInfo.toKeyStrokeInfo(KeyStroke.getKeyStroke("ctrl alt pressed H")))
			.text("Help Content").name(BaseMenuId.HELP_CONTENT.propertiesKey()).build();

		menuItemInfo = helpContentMenuInfo.toMenuItemInfo(new NoAction());

		menu = menuItemInfo.toJMenuItem();

		menuInfo = MenuItemInfoConverter.fromJMenuItem(menu);
		assertEquals(menuInfo, helpContentMenuInfo);
	}
}
