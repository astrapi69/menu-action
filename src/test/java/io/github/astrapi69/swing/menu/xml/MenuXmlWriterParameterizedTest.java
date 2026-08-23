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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import javax.swing.KeyStroke;

import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import io.github.astrapi69.swing.menu.enumeration.Anchor;
import io.github.astrapi69.swing.menu.enumeration.MenuType;
import io.github.astrapi69.swing.menu.model.KeyStrokeInfo;
import io.github.astrapi69.swing.menu.model.MenuInfo;

/**
 * The parameterized unit test class for the class {@link MenuXmlWriter}
 */
class MenuXmlWriterParameterizedTest
{

	@ParameterizedTest(name = "[{index}] the menu type {0} is written as its xml element")
	@EnumSource(MenuType.class)
	void everyMenuTypeIsWrittenAsItsElement(final MenuType menuType)
	{
		MenuInfo menuInfo = MenuInfo.builder().type(menuType).name("the.id").text("The text")
			.build();
		switch (menuType)
		{
			case UNKNOWN -> {
				IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
					() -> MenuXmlWriter.toXml(menuInfo));
				assertTrue(exception.getMessage().contains("The menu type UNKNOWN"));
				assertTrue(exception.getMessage().contains("the.id"));
				assertTrue(exception.getMessage().contains("can not be represented in xml"));
			}
			default -> {
				String elementName = MenuXmlElements.toElementName(menuType).orElseThrow();
				String xml = MenuXmlWriter.toXml(menuInfo);
				assertTrue(xml.contains("<" + elementName + " "),
					"the written xml has no <" + elementName + "> element: " + xml);
				assertTrue(xml.contains("id=\"the.id\""));
				assertEquals(menuInfo, MenuXmlReader.fromXml(xml));
			}
		}
	}

	@ParameterizedTest(name = "[{index}] the menu type {0} is written as the element <{1}>")
	@CsvSource({ "SYSTEM_TRAY, tray", "MENU_BAR, menubar", "TOOL_BAR, toolbar", "MENU, menu",
			"MENU_ITEM, item", "CHECK_BOX_MENU_ITEM, checkbox", "RADIO_BUTTON_MENU_ITEM, radio",
			"POPUP, popup", "SEPARATOR, separator" })
	void theMenuTypeIsWrittenAsTheExpectedElementName(final MenuType menuType,
		final String elementName)
	{
		String xml = MenuXmlWriter.toXml(MenuInfo.builder().type(menuType).name("the.id").build());
		assertTrue(xml.contains("<" + elementName + " "),
			"the written xml has no <" + elementName + "> element: " + xml);
	}

	@ParameterizedTest(name = "[{index}] a menu info without a writable type is rejected")
	@NullSource
	void aMenuInfoWithoutATypeIsRejected(final MenuType menuType)
	{
		MenuInfo menuInfo = MenuInfo.builder().type(menuType).name("no.type").build();
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
			() -> MenuXmlWriter.toXml(menuInfo));
		assertTrue(exception.getMessage().contains("can not be represented in xml"));
		assertThrows(IllegalArgumentException.class, () -> MenuXmlWriter.toXml(List.of(menuInfo)));
	}

	static Stream<Arguments> writtenTrees()
	{
		return Stream.of(
			Arguments.of("a menu bar with a menu and an item",
				MenuInfo.builder().type(MenuType.MENU_BAR).name("bar").build()
					.addChild(MenuInfo.builder().type(MenuType.MENU).name("file").text("File")
						.mnemonic((int)'F').build()
						.addChild(MenuInfo.builder().type(MenuType.MENU_ITEM).name("file.exit")
							.text("Exit")
							.keyStrokeInfo(
								KeyStrokeInfo.toKeyStrokeInfo(KeyStroke.getKeyStroke("alt F4")))
							.actionId("exit").build())),
				1),
			Arguments.of("a popup with a separator and a check box",
				MenuInfo.builder().type(MenuType.POPUP).name("popup").build()
					.addChild(MenuInfo.builder().type(MenuType.SEPARATOR).name("popup.sep").build())
					.addChild(MenuInfo.builder().type(MenuType.CHECK_BOX_MENU_ITEM).name("popup.c")
						.selected(true).model("statusbar").build()),
				2),
			Arguments.of("a tool bar with the tool bar flags",
				MenuInfo.builder().type(MenuType.TOOL_BAR).name("tb").floatable(false)
					.rollover(true).showText(false).accessibleName("Bar")
					.accessibleDescription("The bar").build()
					.addChild(MenuInfo.builder().type(MenuType.MENU_ITEM).name("tb.i").text("I")
						.showText(true).build()),
				1),
			Arguments.of("a tray with an anchored item",
				MenuInfo.builder().type(MenuType.SYSTEM_TRAY).name("tray").build()
					.addChild(MenuInfo.builder().type(MenuType.MENU_ITEM).name("tray.quit")
						.text("Quit").anchor(Anchor.LAST).relativeToMenuId("tray.show")
						.actionCommand("QUIT").enabled(false).visible(true).build()),
				1),
			Arguments.of("a radio group",
				MenuInfo.builder().type(MenuType.MENU).name("view").text("View").build()
					.addChild(MenuInfo.builder().type(MenuType.RADIO_BUTTON_MENU_ITEM)
						.name("view.a").group("g").model("mode").value("A").selected(true).build())
					.addChild(MenuInfo.builder().type(MenuType.RADIO_BUTTON_MENU_ITEM)
						.name("view.b").group("g").model("mode").value("B").build()),
				2));
	}

	@ParameterizedTest(name = "[{index}] {0} is written and read back equally")
	@MethodSource("writtenTrees")
	void writtenTreeIsReadBackEqually(final String description, final MenuInfo menuInfo,
		final int expectedChildren)
	{
		assertFalse(description.isBlank());
		String xml = MenuXmlWriter.toXml(menuInfo);
		MenuInfo actual = MenuXmlReader.fromXml(xml);
		assertEquals(menuInfo, actual);
		assertEquals(expectedChildren, actual.getChildren().size());
		assertEquals(xml, MenuXmlWriter.toXml(actual));
	}

	@ParameterizedTest(name = "[{index}] {0} is written to a file and read back equally")
	@MethodSource("writtenTrees")
	void writtenTreeIsWrittenToAFileAndReadBackEqually(final String description,
		final MenuInfo menuInfo, final int expectedChildren, @TempDir final Path tempDir)
		throws IOException
	{
		Path file = tempDir.resolve("menu.xml");
		MenuXmlWriter.write(menuInfo, file);
		assertTrue(Files.size(file) > 0);
		assertEquals(menuInfo, MenuXmlReader.read(file));
		assertEquals(expectedChildren, MenuXmlReader.read(file).getChildren().size());
		assertFalse(description.isBlank());

		StringWriter stringWriter = new StringWriter();
		MenuXmlWriter.write(menuInfo, stringWriter);
		assertEquals(menuInfo, MenuXmlReader.fromXml(stringWriter.toString()));
	}

	@ParameterizedTest(name = "[{index}] the several roots of {0} are wrapped in a <menus> element")
	@MethodSource("writtenTrees")
	void severalRootsAreWrappedInTheMenusElement(final String description, final MenuInfo menuInfo,
		final int expectedChildren)
	{
		assertFalse(description.isBlank());
		assertEquals(expectedChildren, menuInfo.getChildren().size());
		List<MenuInfo> menuInfos = List.of(menuInfo, menuInfo);
		String xml = MenuXmlWriter.toXml(menuInfos);
		assertTrue(xml.contains("<" + MenuXmlElements.MENUS + ">"));
		assertEquals(menuInfos, MenuXmlReader.readAll(xml));
	}
}
