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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.KeyEvent;
import java.util.List;
import java.util.stream.Stream;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.astrapi69.swing.menu.build.MenuBuilder;
import io.github.astrapi69.swing.menu.build.MissingActionPolicy;
import io.github.astrapi69.swing.menu.enumeration.Anchor;
import io.github.astrapi69.swing.menu.enumeration.MenuType;
import io.github.astrapi69.swing.menu.model.MenuInfo;

/**
 * The parameterized unit test class for the classes {@link MenuXmlReader} and {@link MenuXmlWriter}
 * that covers the read write read round trip of the menu xml format
 */
class MenuXmlRoundTripParameterizedTest
{

	/** The full set of attributes that the reader and the writer support */
	private static final String FULL_ATTRIBUTES = " id=\"full.id\" text=\"Save\""
		+ " textKey=\"menu.save.text\" toolTip=\"Saves the file\" toolTipKey=\"menu.save.tip\""
		+ " mnemonic=\"S\" accelerator=\"ctrl S\" action=\"saveFile\" actionCommand=\"SAVE\""
		+ " enabled=\"false\" visible=\"true\" selected=\"true\" group=\"view.mode\""
		+ " model=\"statusbar\" value=\"DESKTOP\" showText=\"true\" floatable=\"false\""
		+ " rollover=\"true\" anchor=\"AFTER\" relativeTo=\"other.id\" icon=\"icons/save.png\""
		+ " accessibleName=\"Save\" accessibleDescription=\"Saves the current file\"";

	static Stream<Arguments> fullAttributeSnippets()
	{
		return Stream.of(Arguments.of("menubar", MenuType.MENU_BAR, snippet("menubar", true)),
			Arguments.of("menu", MenuType.MENU, snippet("menu", true)),
			Arguments.of("item", MenuType.MENU_ITEM, snippet("item", false)),
			Arguments.of("checkbox", MenuType.CHECK_BOX_MENU_ITEM, snippet("checkbox", false)),
			Arguments.of("radio", MenuType.RADIO_BUTTON_MENU_ITEM, snippet("radio", false)),
			Arguments.of("separator", MenuType.SEPARATOR, snippet("separator", false)),
			Arguments.of("popup", MenuType.POPUP, snippet("popup", true)),
			Arguments.of("toolbar", MenuType.TOOL_BAR, snippet("toolbar", true)),
			Arguments.of("tray", MenuType.SYSTEM_TRAY, snippet("tray", true)));
	}

	private static String snippet(final String elementName, final boolean withChildren)
	{
		if (withChildren)
		{
			return "<" + elementName + FULL_ATTRIBUTES
				+ "><item id=\"child.item\" text=\"Child\" mnemonic=\"C\" accelerator=\"alt F4\"/>"
				+ "<separator id=\"child.separator\"/></" + elementName + ">";
		}
		return "<" + elementName + FULL_ATTRIBUTES + "/>";
	}

	@ParameterizedTest(name = "[{index}] the element <{0}> survives a read write read round trip")
	@MethodSource("fullAttributeSnippets")
	void fullAttributeSetSurvivesTheRoundTrip(final String elementName, final MenuType menuType,
		final String xml)
	{
		MenuInfo first = MenuXmlReader.fromXml(xml);

		assertEquals(menuType, first.getType());
		assertEquals("full.id", first.getName());
		assertEquals("Save", first.getText());
		assertEquals("menu.save.text", first.getTextKey());
		assertEquals("Saves the file", first.getToolTip());
		assertEquals("menu.save.tip", first.getToolTipKey());
		assertEquals(KeyEvent.VK_S, first.getMnemonic());
		assertEquals(KeyStroke.getKeyStroke("ctrl S"), first.getKeyStrokeInfo().toKeyStroke());
		assertEquals("saveFile", first.getActionId());
		assertEquals("SAVE", first.getActionCommand());
		assertEquals(Boolean.FALSE, first.getEnabled());
		assertEquals(Boolean.TRUE, first.getVisible());
		assertEquals(Boolean.TRUE, first.getSelected());
		assertEquals("view.mode", first.getGroup());
		assertEquals("statusbar", first.getModel());
		assertEquals("DESKTOP", first.getValue());
		assertEquals(Boolean.TRUE, first.getShowText());
		assertEquals(Boolean.FALSE, first.getFloatable());
		assertEquals(Boolean.TRUE, first.getRollover());
		assertEquals(Anchor.AFTER, first.getAnchor());
		assertEquals("other.id", first.getRelativeToMenuId());
		assertEquals("icons/save.png", first.getIcon());
		assertEquals("Save", first.getAccessibleName());
		assertEquals("Saves the current file", first.getAccessibleDescription());

		String written = MenuXmlWriter.toXml(first);
		assertTrue(written.contains("<" + elementName + " "),
			"the written xml has no <" + elementName + "> element: " + written);

		MenuInfo second = MenuXmlReader.fromXml(written);
		assertEquals(first, second);
		assertEquals(first.getChildren().size(), second.getChildren().size());
		assertEquals(MenuXmlWriter.toXml(second), written);
	}

	@ParameterizedTest(name = "[{index}] the attribute {0}=\"{1}\" is read as {2}")
	@CsvSource({ "id, global.menu.file.save, global.menu.file.save", "text, Save, Save",
			"textKey, menu.file.save.text, menu.file.save.text",
			"toolTip, Saves the file, Saves the file",
			"toolTipKey, menu.file.save.tip, menu.file.save.tip", "mnemonic, S, 83",
			"mnemonic, s, 83", "mnemonic, 112, 112", "accelerator, ctrl S, ctrl pressed S",
			"accelerator, alt F4, alt pressed F4", "action, saveFile, saveFile",
			"actionCommand, SAVE, SAVE", "enabled, false, false", "enabled, true, true",
			"visible, false, false", "visible, true, true", "selected, true, true",
			"selected, false, false", "group, view.mode, view.mode", "model, statusbar, statusbar",
			"value, DESKTOP, DESKTOP", "showText, true, true", "floatable, false, false",
			"rollover, true, true", "anchor, FIRST, FIRST", "anchor, before, BEFORE",
			"relativeTo, global.menu.file.new, global.menu.file.new",
			"icon, icons/save.png, icons/save.png", "accessibleName, Save, Save",
			"accessibleDescription, Saves the current file, Saves the current file" })
	void attributeIsReadIntoTheMatchingProperty(final String attributeName, final String xmlValue,
		final String expected)
	{
		MenuInfo menuInfo = MenuXmlReader.fromXml(itemWith(attributeName, xmlValue));
		assertEquals(expected, propertyValue(menuInfo, attributeName));
	}

	@ParameterizedTest(name = "[{index}] the attribute {0}=\"{1}\" is written back as \"{2}\"")
	@CsvSource({ "id, global.menu.file.save, global.menu.file.save", "text, Save, Save",
			"textKey, menu.file.save.text, menu.file.save.text",
			"toolTip, Saves the file, Saves the file",
			"toolTipKey, menu.file.save.tip, menu.file.save.tip", "mnemonic, S, S",
			"mnemonic, s, S", "mnemonic, 112, 112", "accelerator, ctrl S, ctrl pressed S",
			"accelerator, alt F4, alt pressed F4", "action, saveFile, saveFile",
			"actionCommand, SAVE, SAVE", "enabled, false, false", "enabled, true, true",
			"visible, false, false", "selected, true, true", "group, view.mode, view.mode",
			"model, statusbar, statusbar", "value, DESKTOP, DESKTOP", "showText, true, true",
			"floatable, false, false", "rollover, true, true", "anchor, FIRST, FIRST",
			"anchor, before, BEFORE", "relativeTo, global.menu.file.new, global.menu.file.new",
			"icon, icons/save.png, icons/save.png", "accessibleName, Save, Save",
			"accessibleDescription, Saves the current file, Saves the current file" })
	void attributeIsWrittenBackAndReReadEqually(final String attributeName, final String xmlValue,
		final String expectedWritten)
	{
		MenuInfo first = MenuXmlReader.fromXml(itemWith(attributeName, xmlValue));
		String written = MenuXmlWriter.toXml(first);
		assertTrue(written.contains(attributeName + "=\"" + expectedWritten + "\""),
			"the written xml has no " + attributeName + "=\"" + expectedWritten + "\": " + written);
		assertEquals(first, MenuXmlReader.fromXml(written));
	}

	private static MenuBuilder newMenuBuilder()
	{
		return new MenuBuilder().withMissingActionPolicy(MissingActionPolicy.IGNORE);
	}

	/**
	 * Parameterized test that {@link MenuXmlReader} keeps the ampersand mnemonic marker of the text
	 * attribute raw and unresolved; only {@link MenuBuilder}, which is the sole place that resolves
	 * the marker (see {@code MenuBuilder.toMenuItemInfo}), turns it into the plain text and the
	 * mnemonic
	 */
	@ParameterizedTest(name = "[{index}] MenuBuilder parses the text ''{0}'' to the text ''{1}'' with the mnemonic {2}")
	@CsvSource(delimiter = '|', nullValues = "NIL", value = { "&File | File | 70",
			"&file | file | 70", "Sa&ve | Save | 86", "&& | & | NIL", "A&&B | A&B | NIL",
			"File& | File& | NIL", "& File | & File | NIL", "No marker | No marker | NIL",
			"S&ave &More | Save &More | 65", "&1 | 1 | 49", "&_x | _x | 95", "&Über | Über | 220" })
	void ampersandMarksTheMnemonicInTheText(final String rawText, final String expectedText,
		final Integer expectedMnemonic)
	{
		MenuInfo menuInfo = MenuXmlReader.fromXml(itemWith("text", escape(rawText)));
		// the reader keeps the raw text with the marker verbatim
		assertEquals(rawText, menuInfo.getText());
		assertNull(menuInfo.getMnemonic());

		JMenuItem built = newMenuBuilder().buildMenuComponent(menuInfo);
		assertEquals(expectedText, built.getText());
		assertEquals(expectedMnemonic == null ? 0 : expectedMnemonic.intValue(),
			built.getMnemonic());
	}

	/**
	 * Parameterized test that an explicit {@code mnemonic} xml attribute is read by
	 * {@link MenuXmlReader} regardless of a marker in the text, and that {@link MenuBuilder} lets
	 * that explicit mnemonic win over the marker that it resolves from the text (see
	 * {@code MenuBuilder.toMenuItemInfo})
	 */
	@ParameterizedTest(name = "[{index}] the mnemonic attribute \"{1}\" wins over the marker in ''{0}''")
	@CsvSource(delimiter = '|', value = { "&File | X | File | 88", "&File | 112 | File | 112",
			"Sa&ve | s | Save | 83", "No marker | Q | No marker | 81",
			"&&Literal | Z | &Literal | 90" })
	void theExplicitMnemonicAttributeWinsOverTheMarker(final String rawText,
		final String mnemonicAttribute, final String expectedText, final Integer expectedMnemonic)
	{
		String xml = "<item id=\"i\" text=\"" + escape(rawText) + "\" mnemonic=\""
			+ mnemonicAttribute + "\"/>";
		MenuInfo menuInfo = MenuXmlReader.fromXml(xml);
		// the reader keeps the raw text with the marker verbatim, only the explicit attribute is
		// parsed
		assertEquals(rawText, menuInfo.getText());
		assertNotNull(menuInfo.getMnemonic());

		JMenuItem built = newMenuBuilder().buildMenuComponent(menuInfo);
		assertEquals(expectedText, built.getText());
		assertEquals(expectedMnemonic.intValue(), built.getMnemonic());
	}

	/**
	 * Parameterized test that a literal ampersand in the text always survives a read write read
	 * round trip, whether or not it is followed by whitespace: the reader stores the text attribute
	 * verbatim without resolving the mnemonic marker, so the writer never has to re-escape it
	 * either
	 */
	@ParameterizedTest(name = "[{index}] the literal ampersand text ''{0}'' survives the round trip")
	@ValueSource(strings = { "A&&B", "&&Start", "1&&2", "Tray && more", "A & B", "Ends with &&" })
	void literalAmpersandSurvivesTheRoundTrip(final String rawText)
	{
		MenuInfo first = MenuXmlReader.fromXml(itemWith("text", escape(rawText)));
		assertEquals(rawText, first.getText());
		assertNull(first.getMnemonic());

		String written = MenuXmlWriter.toXml(first);
		MenuInfo second = MenuXmlReader.fromXml(written);
		assertEquals(rawText, second.getText());
		assertEquals(first, second);
	}

	@ParameterizedTest(name = "[{index}] the resource {0} survives the round trip")
	@ValueSource(strings = { "menubar.xml", "popup.xml", "toolbar.xml", "plugin-contribution.xml" })
	void resourceSurvivesTheRoundTrip(final String resource)
	{
		MenuInfo expected = MenuXmlReader.readResource(resource);
		assertNotNull(expected.getType());
		String xml = MenuXmlWriter.toXml(expected);
		assertEquals(expected, MenuXmlReader.fromXml(xml));
		assertTrue(MenuXmlReader.validate(xml).isEmpty(), MenuXmlReader.validate(xml).toString());
		assertFalse(expected.getChildren().isEmpty());
	}

	@ParameterizedTest(name = "[{index}] the document with several roots {0} survives the round trip")
	@ValueSource(strings = { "menus.xml" })
	void severalRootsSurviveTheRoundTrip(final String resource)
	{
		List<MenuInfo> expected = MenuXmlReader
			.readAll(MenuXmlRoundTripParameterizedTest.class.getResourceAsStream("/" + resource));
		String xml = MenuXmlWriter.toXml(expected);
		assertTrue(xml.contains("<menus>"));
		assertEquals(expected, MenuXmlReader.readAll(xml));
	}

	private static String itemWith(final String attributeName, final String value)
	{
		if (MenuXmlElements.ATTR_ID.equals(attributeName))
		{
			return "<item " + attributeName + "=\"" + value + "\"/>";
		}
		return "<item id=\"i\" " + attributeName + "=\"" + value + "\"/>";
	}

	private static String escape(final String text)
	{
		return text.replace("&", "&amp;").replace("<", "&lt;");
	}

	private static String propertyValue(final MenuInfo menuInfo, final String attributeName)
	{
		Object value = switch (attributeName)
		{
			case MenuXmlElements.ATTR_ID -> menuInfo.getName();
			case MenuXmlElements.ATTR_TEXT -> menuInfo.getText();
			case MenuXmlElements.ATTR_TEXT_KEY -> menuInfo.getTextKey();
			case MenuXmlElements.ATTR_TOOL_TIP -> menuInfo.getToolTip();
			case MenuXmlElements.ATTR_TOOL_TIP_KEY -> menuInfo.getToolTipKey();
			case MenuXmlElements.ATTR_MNEMONIC -> menuInfo.getMnemonic();
			case MenuXmlElements.ATTR_ACCELERATOR -> menuInfo.getKeyStrokeInfo() == null
				? null
				: menuInfo.getKeyStrokeInfo().getKeystrokeAsString();
			case MenuXmlElements.ATTR_ACTION -> menuInfo.getActionId();
			case MenuXmlElements.ATTR_ACTION_COMMAND -> menuInfo.getActionCommand();
			case MenuXmlElements.ATTR_ENABLED -> menuInfo.getEnabled();
			case MenuXmlElements.ATTR_VISIBLE -> menuInfo.getVisible();
			case MenuXmlElements.ATTR_SELECTED -> menuInfo.getSelected();
			case MenuXmlElements.ATTR_GROUP -> menuInfo.getGroup();
			case MenuXmlElements.ATTR_MODEL -> menuInfo.getModel();
			case MenuXmlElements.ATTR_VALUE -> menuInfo.getValue();
			case MenuXmlElements.ATTR_SHOW_TEXT -> menuInfo.getShowText();
			case MenuXmlElements.ATTR_FLOATABLE -> menuInfo.getFloatable();
			case MenuXmlElements.ATTR_ROLLOVER -> menuInfo.getRollover();
			case MenuXmlElements.ATTR_ANCHOR -> menuInfo.getAnchor();
			case MenuXmlElements.ATTR_RELATIVE_TO -> menuInfo.getRelativeToMenuId();
			case MenuXmlElements.ATTR_ICON -> menuInfo.getIcon();
			case MenuXmlElements.ATTR_ACCESSIBLE_NAME -> menuInfo.getAccessibleName();
			case MenuXmlElements.ATTR_ACCESSIBLE_DESCRIPTION -> menuInfo.getAccessibleDescription();
			default -> throw new IllegalArgumentException("Unknown attribute " + attributeName);
		};
		return value == null ? null : String.valueOf(value);
	}
}
