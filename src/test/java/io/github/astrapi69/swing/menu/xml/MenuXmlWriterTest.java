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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.swing.KeyStroke;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import io.github.astrapi69.swing.menu.enumeration.Anchor;
import io.github.astrapi69.swing.menu.enumeration.MenuType;
import io.github.astrapi69.swing.menu.model.KeyStrokeInfo;
import io.github.astrapi69.swing.menu.model.MenuInfo;

/**
 * The unit test class for the class {@link MenuXmlWriter}
 */
class MenuXmlWriterTest
{

	@Test
	void roundTripOfResource()
	{
		MenuInfo expected = MenuXmlReader.readResource("menubar.xml");
		String xml = MenuXmlWriter.toXml(expected);
		assertTrue(xml.contains("<menubar id=\"global.menu.bar\">"));
		assertTrue(xml.contains("accelerator=\"alt pressed F4\""));
		assertTrue(xml.contains("mnemonic=\"F\""));
		MenuInfo actual = MenuXmlReader.fromXml(xml);
		assertEquals(expected, actual);
	}

	@Test
	void roundTripOfBuiltTree()
	{
		MenuInfo expected = MenuInfo.builder().type(MenuType.POPUP).name("popup").text("Popup")
			.build()
			.addChild(MenuInfo.builder().type(MenuType.MENU_ITEM).name("popup.add").text("Add")
				.textKey("popup.add.text").toolTip("Adds").mnemonic((int)'A')
				.keyStrokeInfo(KeyStrokeInfo.toKeyStrokeInfo(KeyStroke.getKeyStroke("ctrl A")))
				.actionId("add").actionCommand("ADD").enabled(false).icon("icons/add.png")
				.anchor(Anchor.FIRST).build())
			.addChild(MenuInfo.builder().type(MenuType.SEPARATOR).build())
			.addChild(MenuInfo.builder().type(MenuType.RADIO_BUTTON_MENU_ITEM).name("popup.r")
				.selected(true).group("g").anchor(Anchor.AFTER).relativeToMenuId("popup.add")
				.build());
		MenuInfo actual = MenuXmlReader.fromXml(MenuXmlWriter.toXml(expected));
		assertEquals(expected, actual);
	}

	@Test
	void writeAndReadFile(@TempDir Path tempDir) throws IOException
	{
		MenuInfo expected = MenuXmlReader.readResource("popup.xml");
		Path file = tempDir.resolve("popup.xml");
		MenuXmlWriter.write(expected, file);
		assertTrue(Files.size(file) > 0);
		assertEquals(expected, MenuXmlReader.read(file));
	}

	@Test
	void writeSeveralRoots()
	{
		List<MenuInfo> expected = MenuXmlReader
			.readAll(MenuXmlWriterTest.class.getResourceAsStream("/menus.xml"));
		String xml = MenuXmlWriter.toXml(expected);
		assertTrue(xml.contains("<menus>"));
		assertEquals(expected, MenuXmlReader.readAll(xml));
	}

	@Test
	void unsupportedTypeIsRejected()
	{
		MenuInfo systemTray = MenuInfo.builder().type(MenuType.SYSTEM_TRAY).name("tray").build();
		assertThrows(IllegalArgumentException.class, () -> MenuXmlWriter.toXml(systemTray));
	}
}
