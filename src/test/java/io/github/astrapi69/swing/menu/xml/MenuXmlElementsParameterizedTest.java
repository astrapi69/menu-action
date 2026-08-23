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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.astrapi69.swing.menu.enumeration.MenuType;

/**
 * The parameterized unit test class for the class {@link MenuXmlElements}
 */
class MenuXmlElementsParameterizedTest
{

	@ParameterizedTest(name = "[{index}] the element <{0}> is the menu type {1}")
	@CsvSource({ "menubar, MENU_BAR", "menu, MENU", "item, MENU_ITEM",
			"checkbox, CHECK_BOX_MENU_ITEM", "radio, RADIO_BUTTON_MENU_ITEM",
			"separator, SEPARATOR", "popup, POPUP", "toolbar, TOOL_BAR", "tray, SYSTEM_TRAY" })
	void elementNameAndMenuTypeAreMappedInBothDirections(final String elementName,
		final MenuType menuType)
	{
		assertEquals(Optional.of(menuType), MenuXmlElements.toMenuType(elementName));
		assertEquals(Optional.of(elementName), MenuXmlElements.toElementName(menuType));
		assertEquals(menuType, MenuXmlReader.fromXml("<" + elementName + " id=\"x\"/>").getType());
	}

	@ParameterizedTest(name = "[{index}] the unknown element name ''{0}'' has no menu type")
	@EmptySource
	@ValueSource(strings = { "foo", "menus", "MENU", "Menu", "menuBar", "menu bar", "toolBar",
			"MENU_BAR", "items", " menu", "menu ", "SEPARATOR" })
	void toMenuTypeOfAnUnknownElementNameIsEmpty(final String elementName)
	{
		assertTrue(MenuXmlElements.toMenuType(elementName).isEmpty());
	}

	/**
	 * A null element name now correctly gives an empty {@link Optional} as the javadoc of the
	 * method promises, instead of throwing a {@link NullPointerException} because the backing map
	 * was created with {@code Map.of} which rejects a null key
	 */
	@ParameterizedTest(name = "[{index}] a null element name gives an empty optional")
	@NullSource
	void toMenuTypeOfNullIsEmpty(final String elementName)
	{
		assertTrue(MenuXmlElements.toMenuType(elementName).isEmpty());
	}

	@ParameterizedTest(name = "[{index}] the menu type {0} maps to an element name")
	@EnumSource(MenuType.class)
	void toElementNameOfEveryMenuType(final MenuType menuType)
	{
		Optional<String> elementName = MenuXmlElements.toElementName(menuType);
		switch (menuType)
		{
			case UNKNOWN -> assertTrue(elementName.isEmpty());
			default -> {
				assertTrue(elementName.isPresent());
				assertFalse(elementName.get().isBlank());
				assertEquals(Optional.of(menuType), MenuXmlElements.toMenuType(elementName.get()));
			}
		}
	}

	@ParameterizedTest(name = "[{index}] a null menu type has no element name")
	@NullSource
	void toElementNameOfNullIsEmpty(final MenuType menuType)
	{
		assertTrue(MenuXmlElements.toElementName(menuType).isEmpty());
	}

	@ParameterizedTest(name = "[{index}] the root element ''{0}'' holds several menu definitions")
	@ValueSource(strings = { "menus" })
	void theMenusElementIsNoMenuType(final String elementName)
	{
		assertEquals(MenuXmlElements.MENUS, elementName);
		assertTrue(MenuXmlElements.toMenuType(elementName).isEmpty());
		assertEquals(2,
			MenuXmlReader
				.readAll(
					"<" + elementName + "><menu id=\"a\"/><popup id=\"b\"/></" + elementName + ">")
				.size());
	}
}
