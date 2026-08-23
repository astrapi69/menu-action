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
package io.github.astrapi69.swing.menu.build;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.function.Function;
import java.util.stream.Stream;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import io.github.astrapi69.swing.menu.enumeration.MenuType;
import io.github.astrapi69.swing.menu.model.MenuInfo;

/**
 * The parameterized unit test class for the text and tool tip resolution of the class
 * {@link MenuBuilder}
 */
class MenuBuilderTextParameterizedTest
{

	/** The resource bundle key for the text of the built menu components */
	private static final String TEXT_KEY = "menu.file";
	/** The resource bundle key for the tool tip of the built menu components */
	private static final String TOOL_TIP_KEY = "menu.file.tip";

	private static MenuBuilder newBuilder(final String resolvedText, final String resolvedToolTip)
	{
		return new MenuBuilder().withMissingActionPolicy(MissingActionPolicy.IGNORE)
			.withTextResolver(key -> switch (key)
			{
			case TEXT_KEY -> resolvedText;
			case TOOL_TIP_KEY -> resolvedToolTip;
			default -> null;
			});
	}

	/**
	 * Parameterized test for the text resolution and the ampersand mnemonic marker of a menu item
	 */
	@ParameterizedTest(name = "the text {0} with the key {1} that resolves to {2} gives the text {4}")
	@CsvSource(nullValues = "NULL", value = {
			"File,     NULL,      NULL,           NULL, File,        0",
			"NULL,     menu.file, Datei,          NULL, Datei,       0",
			"File,     menu.file, Datei,          NULL, Datei,       0",
			"File,     menu.file, NULL,           NULL, File,        0",
			"NULL,     menu.file, NULL,           NULL, menu.file,   0",
			"NULL,     NULL,      NULL,           NULL, '',          0",
			"NULL,     menu.file, '&Datei',       NULL, Datei,       68",
			"&New,     NULL,      NULL,           NULL, New,         78",
			"Ne&w,     NULL,      NULL,           NULL, New,         87",
			"&New,     NULL,      NULL,           88,   New,         88",
			"NULL,     menu.file, 'Save && Exit', NULL, 'Save & Exit', 0",
			"'&& Raw', NULL,      NULL,           NULL, '& Raw',     0",
			"NULL,     menu.file, '& Spaced',     NULL, '& Spaced',  0" })
	void textResolutionOfAMenuItem(final String text, final String textKey,
		final String resolvedText, final Integer mnemonic, final String expectedText,
		final int expectedMnemonic)
	{
		MenuBuilder builder = newBuilder(resolvedText, null);

		JMenuItem menuItem = builder.buildMenuComponent(MenuInfo.builder().type(MenuType.MENU_ITEM)
			.name("file").text(text).textKey(textKey).mnemonic(mnemonic).build());

		assertEquals(expectedText, menuItem.getText());
		assertEquals(expectedMnemonic, menuItem.getMnemonic());
		assertNull(menuItem.getToolTipText());
	}

	/**
	 * Parameterized test for the text resolution without a text resolver
	 */
	@ParameterizedTest(name = "without a text resolver the text {0} with the key {1} gives {2}")
	@CsvSource(nullValues = "NULL", value = { "File, menu.file, File", "NULL, menu.file, menu.file",
			"File, NULL,      File", "NULL, NULL,      ''" })
	void textResolutionWithoutATextResolver(final String text, final String textKey,
		final String expectedText)
	{
		MenuBuilder builder = new MenuBuilder().withMissingActionPolicy(MissingActionPolicy.IGNORE);

		JMenuItem menuItem = builder.buildMenuComponent(MenuInfo.builder().type(MenuType.MENU_ITEM)
			.name("file").text(text).textKey(textKey).build());

		assertEquals(expectedText, menuItem.getText());
	}

	/**
	 * Parameterized test for the tool tip resolution of a menu item
	 */
	@ParameterizedTest(name = "the tool tip {0} with the key {1} that resolves to {2} gives {3}")
	@CsvSource(nullValues = "NULL", value = {
			"Exit the application, NULL,          NULL,    Exit the application",
			"NULL,                 menu.file.tip, Beenden, Beenden",
			"Exit,                 menu.file.tip, Beenden, Beenden",
			"Exit,                 menu.file.tip, NULL,    Exit",
			"NULL,                 menu.file.tip, NULL,    NULL",
			"NULL,                 NULL,          NULL,    NULL",
			"'&Exit',              NULL,          NULL,    '&Exit'" })
	void toolTipResolutionOfAMenuItem(final String toolTip, final String toolTipKey,
		final String resolvedToolTip, final String expectedToolTip)
	{
		MenuBuilder builder = newBuilder(null, resolvedToolTip);

		JMenuItem menuItem = builder.buildMenuComponent(MenuInfo.builder().type(MenuType.MENU_ITEM)
			.name("file").text("File").toolTip(toolTip).toolTipKey(toolTipKey).build());

		assertEquals(expectedToolTip, menuItem.getToolTipText());
		assertEquals("File", menuItem.getText());
	}

	private static Stream<Arguments> componentTypesWithTextExtractors()
	{
		return Stream.of(
			Arguments.of(MenuType.MENU,
				(Function<JComponent, String>)component -> ((JMenu)component).getText()),
			Arguments.of(MenuType.MENU_ITEM,
				(Function<JComponent, String>)component -> ((JMenuItem)component).getText()),
			Arguments.of(MenuType.CHECK_BOX_MENU_ITEM,
				(Function<JComponent, String>)component -> ((JMenuItem)component).getText()),
			Arguments.of(MenuType.RADIO_BUTTON_MENU_ITEM,
				(Function<JComponent, String>)component -> ((JMenuItem)component).getText()),
			Arguments.of(MenuType.POPUP,
				(Function<JComponent, String>)component -> ((JPopupMenu)component).getLabel()));
	}

	/**
	 * Parameterized test for the text resolution of every menu component type that shows a text
	 */
	@ParameterizedTest(name = "the resolved text of a component of the type {0}")
	@MethodSource("componentTypesWithTextExtractors")
	void textKeyIsResolvedForEveryComponentType(final MenuType type,
		final Function<JComponent, String> textExtractor)
	{
		MenuBuilder builder = newBuilder("&Datei", null);

		JComponent component = builder.build(MenuInfo.builder().type(type).name("component")
			.text("Fallback").textKey(TEXT_KEY).build());

		assertEquals("Datei", textExtractor.apply(component));
		if (component instanceof AbstractButton button)
		{
			assertEquals('D', button.getMnemonic());
		}
	}

	/**
	 * Parameterized test for the tool tip resolution of every menu component type
	 */
	@ParameterizedTest(name = "the resolved tool tip of a component of the type {0}")
	@EnumSource(value = MenuType.class, names = { "MENU_BAR", "POPUP", "TOOL_BAR", "MENU",
			"MENU_ITEM", "CHECK_BOX_MENU_ITEM", "RADIO_BUTTON_MENU_ITEM" })
	void toolTipKeyIsResolvedForEveryComponentType(final MenuType type)
	{
		MenuBuilder builder = newBuilder(null, "The tool tip");

		JComponent component = builder.build(MenuInfo.builder().type(type).name("component")
			.text("Text").toolTipKey(TOOL_TIP_KEY).build());

		assertEquals("The tool tip", component.getToolTipText());
	}

	/**
	 * Parameterized test that shows that the text of a tool bar is only kept as its name if the
	 * tool bar has no name of its own
	 */
	@ParameterizedTest(name = "the tool bar with the name {0} and the text {1} has the name {2}")
	@CsvSource(nullValues = "NULL", value = { "tool.bar, Main,    tool.bar",
			"NULL,     Main,    Main", "NULL,     '&Main', Main", "NULL,     NULL,    NULL" })
	void toolBarTextIsOnlyUsedAsNameWithoutAnOwnName(final String name, final String text,
		final String expectedName)
	{
		JToolBar toolBar = newBuilder(null, null)
			.buildToolBar(MenuInfo.builder().type(MenuType.TOOL_BAR).name(name).text(text).build());

		assertEquals(expectedName, toolBar.getName());
	}
}
