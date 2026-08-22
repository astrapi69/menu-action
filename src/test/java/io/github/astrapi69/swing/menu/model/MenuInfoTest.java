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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.junit.jupiter.api.Test;

import io.github.astrapi69.swing.menu.MenuExtensions;
import io.github.astrapi69.swing.menu.enumeration.BaseMenuId;
import io.github.astrapi69.swing.menu.enumeration.MenuType;
import io.github.astrapi69.swing.menu.xml.MenuXmlReader;
import io.github.astrapi69.swing.menu.xml.MenuXmlWriter;

/**
 * The unit test class for the class {@link MenuInfo}
 */
class MenuInfoTest
{

	@Test
	void xmlRoundTrip()
	{
		MenuInfo expected = MenuInfo.builder().type(MenuType.MENU_ITEM)
			.mnemonic(MenuExtensions.toMnemonic('E'))
			.keyStrokeInfo(KeyStrokeInfo.toKeyStrokeInfo(KeyStroke.getKeyStroke("alt F4")))
			.text("Exit").name(BaseMenuId.EXIT.propertiesKey()).build();
		String xml = MenuXmlWriter.toXml(expected);
		assertNotNull(xml);
		assertEquals(expected, MenuXmlReader.fromXml(xml));
	}

	@Test
	void childrenAndConversion()
	{
		MenuInfo menu = MenuInfo.builder().type(MenuType.MENU).name("m").build();
		assertFalse(menu.hasChildren());
		menu.setChildren(null);
		assertFalse(menu.hasChildren());
		menu.addChild(MenuInfo.builder().type(MenuType.MENU_ITEM).name("i").text("I").build());
		assertTrue(menu.hasChildren());

		boolean[] clicked = { false };
		MenuItemInfo itemInfo = menu.getChildren().get(0).toMenuItemInfo(e -> clicked[0] = true);
		JMenuItem item = itemInfo.toJMenuItem();
		assertEquals("i", item.getName());
		item.doClick();
		assertTrue(clicked[0]);

		MenuInfo copy = menu.toBuilder().build();
		assertEquals(menu, copy);
	}
}
