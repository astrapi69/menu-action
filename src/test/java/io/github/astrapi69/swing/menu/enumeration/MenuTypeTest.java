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
package io.github.astrapi69.swing.menu.enumeration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.EnumSet;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * The unit test class for the enum {@link MenuType}
 */
class MenuTypeTest
{

	@Test
	void values()
	{
		assertEquals(List.of(MenuType.SYSTEM_TRAY, MenuType.MENU_BAR, MenuType.TOOL_BAR,
			MenuType.MENU, MenuType.MENU_ITEM, MenuType.CHECK_BOX_MENU_ITEM,
			MenuType.RADIO_BUTTON_MENU_ITEM, MenuType.POPUP, MenuType.SEPARATOR, MenuType.UNKNOWN),
			List.of(MenuType.values()));
		assertEquals(10, EnumSet.allOf(MenuType.class).size());
	}

	@Test
	void valueOf()
	{
		for (MenuType menuType : MenuType.values())
		{
			assertSame(menuType, MenuType.valueOf(menuType.name()));
		}
		assertSame(MenuType.CHECK_BOX_MENU_ITEM, MenuType.valueOf("CHECK_BOX_MENU_ITEM"));
		assertThrows(IllegalArgumentException.class, () -> MenuType.valueOf("menu"));
	}
}
