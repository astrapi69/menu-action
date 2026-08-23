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
package io.github.astrapi69.swing.menu.xml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import io.github.astrapi69.swing.menu.enumeration.MenuType;

/**
 * The unit test class for the class {@link MenuXmlElements}
 */
class MenuXmlElementsTest
{

	@Test
	void toMenuTypeOfEveryElement()
	{
		assertEquals(Optional.of(MenuType.MENU_BAR),
			MenuXmlElements.toMenuType(MenuXmlElements.MENU_BAR));
		assertEquals(Optional.of(MenuType.MENU), MenuXmlElements.toMenuType(MenuXmlElements.MENU));
		assertEquals(Optional.of(MenuType.MENU_ITEM),
			MenuXmlElements.toMenuType(MenuXmlElements.ITEM));
		assertEquals(Optional.of(MenuType.CHECK_BOX_MENU_ITEM),
			MenuXmlElements.toMenuType(MenuXmlElements.CHECKBOX));
		assertEquals(Optional.of(MenuType.RADIO_BUTTON_MENU_ITEM),
			MenuXmlElements.toMenuType(MenuXmlElements.RADIO));
		assertEquals(Optional.of(MenuType.SEPARATOR),
			MenuXmlElements.toMenuType(MenuXmlElements.SEPARATOR));
		assertEquals(Optional.of(MenuType.POPUP),
			MenuXmlElements.toMenuType(MenuXmlElements.POPUP));
		assertEquals(Optional.of(MenuType.TOOL_BAR),
			MenuXmlElements.toMenuType(MenuXmlElements.TOOL_BAR));
		assertEquals(Optional.of(MenuType.SYSTEM_TRAY),
			MenuXmlElements.toMenuType(MenuXmlElements.TRAY));
	}

	@Test
	void toMenuTypeOfUnknownAndNull()
	{
		// the root element is a container and has no menu type
		assertEquals(Optional.empty(), MenuXmlElements.toMenuType(MenuXmlElements.MENUS));
		assertEquals(Optional.empty(), MenuXmlElements.toMenuType("unknown"));
		assertEquals(Optional.empty(), MenuXmlElements.toMenuType(""));
		// element names are case sensitive
		assertEquals(Optional.empty(), MenuXmlElements.toMenuType("MENU"));
		assertEquals(Optional.empty(), MenuXmlElements.toMenuType("Item"));
		assertEquals(Optional.empty(), MenuXmlElements.toMenuType(null));
	}

	@Test
	void toElementNameOfEveryType()
	{
		assertEquals(Optional.of(MenuXmlElements.MENU_BAR),
			MenuXmlElements.toElementName(MenuType.MENU_BAR));
		assertEquals(Optional.of(MenuXmlElements.MENU),
			MenuXmlElements.toElementName(MenuType.MENU));
		assertEquals(Optional.of(MenuXmlElements.ITEM),
			MenuXmlElements.toElementName(MenuType.MENU_ITEM));
		assertEquals(Optional.of(MenuXmlElements.CHECKBOX),
			MenuXmlElements.toElementName(MenuType.CHECK_BOX_MENU_ITEM));
		assertEquals(Optional.of(MenuXmlElements.RADIO),
			MenuXmlElements.toElementName(MenuType.RADIO_BUTTON_MENU_ITEM));
		assertEquals(Optional.of(MenuXmlElements.SEPARATOR),
			MenuXmlElements.toElementName(MenuType.SEPARATOR));
		assertEquals(Optional.of(MenuXmlElements.POPUP),
			MenuXmlElements.toElementName(MenuType.POPUP));
		assertEquals(Optional.of(MenuXmlElements.TOOL_BAR),
			MenuXmlElements.toElementName(MenuType.TOOL_BAR));
		assertEquals(Optional.of(MenuXmlElements.TRAY),
			MenuXmlElements.toElementName(MenuType.SYSTEM_TRAY));
	}

	@Test
	void toElementNameOfUnknownAndNull()
	{
		assertEquals(Optional.empty(), MenuXmlElements.toElementName(MenuType.UNKNOWN));
		assertEquals(Optional.empty(), MenuXmlElements.toElementName(null));
	}

	@Test
	void roundTrip()
	{
		for (MenuType menuType : MenuType.values())
		{
			Optional<String> elementName = MenuXmlElements.toElementName(menuType);
			if (menuType == MenuType.UNKNOWN)
			{
				assertFalse(elementName.isPresent());
				continue;
			}
			assertTrue(elementName.isPresent(), menuType.name());
			assertSame(menuType, MenuXmlElements.toMenuType(elementName.get()).orElseThrow());
		}
		for (String elementName : List.of(MenuXmlElements.MENU_BAR, MenuXmlElements.MENU,
			MenuXmlElements.ITEM, MenuXmlElements.CHECKBOX, MenuXmlElements.RADIO,
			MenuXmlElements.SEPARATOR, MenuXmlElements.POPUP, MenuXmlElements.TOOL_BAR,
			MenuXmlElements.TRAY))
		{
			MenuType menuType = MenuXmlElements.toMenuType(elementName).orElseThrow();
			assertEquals(elementName, MenuXmlElements.toElementName(menuType).orElseThrow());
		}
	}

	@Test
	void elementNames()
	{
		assertEquals("menus", MenuXmlElements.MENUS);
		assertEquals("menubar", MenuXmlElements.MENU_BAR);
		assertEquals("menu", MenuXmlElements.MENU);
		assertEquals("item", MenuXmlElements.ITEM);
		assertEquals("checkbox", MenuXmlElements.CHECKBOX);
		assertEquals("radio", MenuXmlElements.RADIO);
		assertEquals("separator", MenuXmlElements.SEPARATOR);
		assertEquals("popup", MenuXmlElements.POPUP);
		assertEquals("toolbar", MenuXmlElements.TOOL_BAR);
		assertEquals("tray", MenuXmlElements.TRAY);
	}

	@Test
	void attributeNames()
	{
		assertEquals("id", MenuXmlElements.ATTR_ID);
		assertEquals("text", MenuXmlElements.ATTR_TEXT);
		assertEquals("textKey", MenuXmlElements.ATTR_TEXT_KEY);
		assertEquals("toolTip", MenuXmlElements.ATTR_TOOL_TIP);
		assertEquals("mnemonic", MenuXmlElements.ATTR_MNEMONIC);
		assertEquals("accelerator", MenuXmlElements.ATTR_ACCELERATOR);
		assertEquals("action", MenuXmlElements.ATTR_ACTION);
		assertEquals("actionCommand", MenuXmlElements.ATTR_ACTION_COMMAND);
		assertEquals("enabled", MenuXmlElements.ATTR_ENABLED);
		assertEquals("visible", MenuXmlElements.ATTR_VISIBLE);
		assertEquals("selected", MenuXmlElements.ATTR_SELECTED);
		assertEquals("group", MenuXmlElements.ATTR_GROUP);
		assertEquals("icon", MenuXmlElements.ATTR_ICON);
		assertEquals("anchor", MenuXmlElements.ATTR_ANCHOR);
		assertEquals("relativeTo", MenuXmlElements.ATTR_RELATIVE_TO);
		assertEquals("toolTipKey", MenuXmlElements.ATTR_TOOL_TIP_KEY);
		assertEquals("model", MenuXmlElements.ATTR_MODEL);
		assertEquals("value", MenuXmlElements.ATTR_VALUE);
		assertEquals("showText", MenuXmlElements.ATTR_SHOW_TEXT);
		assertEquals("floatable", MenuXmlElements.ATTR_FLOATABLE);
		assertEquals("rollover", MenuXmlElements.ATTR_ROLLOVER);
		assertEquals("accessibleName", MenuXmlElements.ATTR_ACCESSIBLE_NAME);
		assertEquals("accessibleDescription", MenuXmlElements.ATTR_ACCESSIBLE_DESCRIPTION);
	}
}
