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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.KeyStroke;

import org.junit.jupiter.api.Test;

import io.github.astrapi69.swing.menu.enumeration.Anchor;
import io.github.astrapi69.swing.menu.enumeration.MenuType;
import io.github.astrapi69.swing.menu.model.MenuInfo;

/**
 * The unit test class for the class {@link MenuXmlReader}
 */
class MenuXmlReaderTest
{

	@Test
	void readResourceMenuBar()
	{
		MenuInfo menuBar = MenuXmlReader.readResource("menubar.xml");

		assertEquals(MenuType.MENU_BAR, menuBar.getType());
		assertEquals("global.menu.bar", menuBar.getName());
		assertEquals(4, menuBar.getChildren().size());

		MenuInfo file = menuBar.getChildren().get(0);
		assertEquals(MenuType.MENU, file.getType());
		assertEquals("File", file.getText());
		assertEquals(KeyEvent.VK_F, file.getMnemonic());
		assertEquals(5, file.getChildren().size());

		MenuInfo newItem = file.getChildren().get(0);
		assertEquals(MenuType.MENU_ITEM, newItem.getType());
		assertEquals("global.menu.file.new", newItem.getName());
		assertEquals("newFile", newItem.getActionId());
		assertEquals("icons/new.png", newItem.getIcon());
		assertEquals(KeyStroke.getKeyStroke("ctrl N"), newItem.getKeyStrokeInfo().toKeyStroke());

		assertEquals(MenuType.SEPARATOR, file.getChildren().get(2).getType());

		MenuInfo exit = file.getChildren().get(4);
		assertEquals("Exit the application", exit.getToolTip());
		assertEquals(KeyStroke.getKeyStroke("alt F4"), exit.getKeyStrokeInfo().toKeyStroke());

		MenuInfo edit = menuBar.getChildren().get(1);
		assertEquals(Boolean.FALSE, edit.getEnabled());
		assertEquals("menu.edit.copy", edit.getChildren().get(0).getTextKey());
		assertNull(edit.getChildren().get(0).getText());

		MenuInfo view = menuBar.getChildren().get(2);
		MenuInfo statusbar = view.getChildren().get(0);
		assertEquals(MenuType.CHECK_BOX_MENU_ITEM, statusbar.getType());
		assertEquals(Boolean.TRUE, statusbar.getSelected());
		MenuInfo desktopMode = view.getChildren().get(2);
		assertEquals(MenuType.RADIO_BUTTON_MENU_ITEM, desktopMode.getType());
		assertEquals("view.mode", desktopMode.getGroup());

		MenuInfo help = menuBar.getChildren().get(3);
		assertEquals(Anchor.LAST, help.getAnchor());
	}

	@Test
	void readResourcePopupAndToolBar()
	{
		MenuInfo popup = MenuXmlReader.readResource("/popup.xml");
		assertEquals(MenuType.POPUP, popup.getType());
		assertEquals(4, popup.getChildren().size());
		assertEquals(MenuType.MENU, popup.getChildren().get(3).getType());

		MenuInfo toolBar = MenuXmlReader.readResource("toolbar.xml");
		assertEquals(MenuType.TOOL_BAR, toolBar.getType());
		assertEquals(4, toolBar.getChildren().size());
	}

	@Test
	void readAll()
	{
		List<MenuInfo> menus = MenuXmlReader
			.readAll(MenuXmlReaderTest.class.getResourceAsStream("/menus.xml"));
		assertEquals(2, menus.size());
		assertEquals(MenuType.MENU_BAR, menus.get(0).getType());
		assertEquals(MenuType.POPUP, menus.get(1).getType());

		List<MenuInfo> single = MenuXmlReader.readAll("<menu id=\"foo\"/>");
		assertEquals(1, single.size());
		assertFalse(single.get(0).hasChildren());
	}

	@Test
	void fromXmlWithMnemonicAsKeyCode()
	{
		MenuInfo menu = MenuXmlReader
			.fromXml("<menu id=\"foo\" text=\"Foo\" mnemonic=\"" + KeyEvent.VK_F1 + "\"/>");
		assertEquals(KeyEvent.VK_F1, menu.getMnemonic());
		assertNotNull(menu.getChildren());
		assertTrue(menu.getChildren().isEmpty());
	}

	@Test
	void invalidDocuments()
	{
		IllegalArgumentException unknownElement = assertThrows(IllegalArgumentException.class,
			() -> MenuXmlReader.fromXml("<foo id=\"x\"/>"));
		assertTrue(unknownElement.getMessage().contains("<foo>"));

		IllegalArgumentException invalidAccelerator = assertThrows(IllegalArgumentException.class,
			() -> MenuXmlReader.fromXml("<item id=\"x\" accelerator=\"not a keystroke\"/>"));
		assertTrue(invalidAccelerator.getMessage().contains("id=\"x\""));

		assertThrows(IllegalArgumentException.class,
			() -> MenuXmlReader.fromXml("<item id=\"x\" anchor=\"MIDDLE\"/>"));
		assertThrows(IllegalArgumentException.class,
			() -> MenuXmlReader.fromXml("<item id=\"x\" mnemonic=\"abc\"/>"));
		assertThrows(IllegalArgumentException.class,
			() -> MenuXmlReader.fromXml("<menu id=\"x\">"));
		assertThrows(IllegalArgumentException.class,
			() -> MenuXmlReader.readResource("missing.xml"));
	}

	@Test
	void ampersandMnemonicVisibleAndTray()
	{
		MenuInfo tray = MenuXmlReader
			.fromXml("<tray id=\"tray\" text=\"&amp;Tray &amp;&amp; more\">"
				+ "<item id=\"tray.show\" text=\"&amp;Show\" visible=\"false\"/>"
				+ "<item id=\"tray.quit\" text=\"&amp;Quit\" mnemonic=\"X\"/></tray>");
		assertEquals(MenuType.SYSTEM_TRAY, tray.getType());
		assertEquals("Tray & more", tray.getText());
		assertEquals(KeyEvent.VK_T, tray.getMnemonic());
		MenuInfo show = tray.getChildren().get(0);
		assertEquals("Show", show.getText());
		assertEquals(KeyEvent.VK_S, show.getMnemonic());
		assertEquals(Boolean.FALSE, show.getVisible());
		// an explicit mnemonic attribute wins over the marker
		assertEquals(KeyEvent.VK_X, tray.getChildren().get(1).getMnemonic());
		assertEquals("Quit", tray.getChildren().get(1).getText());
		String xml = MenuXmlWriter.toXml(tray);
		assertTrue(xml.contains("<tray "));
		assertTrue(xml.contains("visible=\"false\""));
		assertEquals(tray, MenuXmlReader.fromXml(xml));
	}

	@Test
	void modelToolBarAndAccessibilityAttributes()
	{
		String xml = "<toolbar id=\"tb\" toolTipKey=\"tip.bar\" floatable=\"false\" rollover=\"true\""
			+ " showText=\"false\" accessibleName=\"Bar\" accessibleDescription=\"The bar\">"
			+ "<checkbox id=\"c\" text=\"C\" model=\"statusbar\" showText=\"true\"/>"
			+ "<item id=\"i\" text=\"I\" toolTipKey=\"tip.i\" accessibleName=\"Item\"/></toolbar>";
		assertTrue(MenuXmlReader.validate(xml).isEmpty(), MenuXmlReader.validate(xml).toString());
		MenuInfo toolBar = MenuXmlReader.readValidated(xml);
		assertEquals("tip.bar", toolBar.getToolTipKey());
		assertEquals(Boolean.FALSE, toolBar.getFloatable());
		assertEquals(Boolean.TRUE, toolBar.getRollover());
		assertEquals(Boolean.FALSE, toolBar.getShowText());
		assertEquals("Bar", toolBar.getAccessibleName());
		assertEquals("The bar", toolBar.getAccessibleDescription());
		assertEquals("statusbar", toolBar.getChildren().get(0).getModel());
		assertEquals(Boolean.TRUE, toolBar.getChildren().get(0).getShowText());
		assertEquals("tip.i", toolBar.getChildren().get(1).getToolTipKey());
		assertEquals("Item", toolBar.getChildren().get(1).getAccessibleName());
		assertEquals(toolBar, MenuXmlReader.fromXml(MenuXmlWriter.toXml(toolBar)));

		String radios = "<menu id=\"m\"><radio id=\"a\" group=\"g\" model=\"mode\" value=\"A\"/></menu>";
		assertTrue(MenuXmlReader.validate(radios).isEmpty());
		MenuInfo radio = MenuXmlReader.fromXml(radios).getChildren().get(0);
		assertEquals("mode", radio.getModel());
		assertEquals("A", radio.getValue());
		assertEquals(radio, MenuXmlReader.fromXml(MenuXmlWriter.toXml(radio)));
	}

	@Test
	void validate()
	{
		for (String resource : List.of("menubar.xml", "popup.xml", "toolbar.xml",
			"plugin-contribution.xml", "menus.xml"))
		{
			List<String> errors = MenuXmlReader
				.validate(MenuXmlReaderTest.class.getResourceAsStream("/" + resource));
			assertTrue(errors.isEmpty(), resource + ": " + errors);
		}
		assertTrue(MenuXmlReader.validate("<menu id=\"m\"><item id=\"i\"/></menu>").isEmpty());

		List<String> errors = MenuXmlReader.validate(
			"<menubar id=\"b\"><menu text=\"no id\"><foo/></menu><item id=\"x\" anchor=\"MIDDLE\"/></menubar>");
		assertFalse(errors.isEmpty());
		assertTrue(errors.stream().anyMatch(error -> error.matches("\\d+:\\d+: .*")));
		assertTrue(errors.stream().anyMatch(error -> error.contains("foo")));
		assertFalse(MenuXmlReader.validate("<menu id=\"m\">").isEmpty());

		assertNotNull(MenuXmlReader.readValidated("<menu id=\"m\"/>"));
		assertNotNull(MenuXmlReader.readValidatedResource("popup.xml"));
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
			() -> MenuXmlReader.readValidated("<menu><foo/></menu>"));
		assertTrue(exception.getMessage().contains("foo"));
	}

	@Test
	void doctypeIsRejected()
	{
		String xml = "<!DOCTYPE menu [<!ENTITY xxe SYSTEM \"file:///etc/passwd\">]>"
			+ "<menu id=\"x\" text=\"&xxe;\"/>";
		assertThrows(IllegalArgumentException.class, () -> MenuXmlReader.fromXml(xml));
	}
}
