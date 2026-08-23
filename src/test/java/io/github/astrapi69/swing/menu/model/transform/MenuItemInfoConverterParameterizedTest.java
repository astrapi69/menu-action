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
package io.github.astrapi69.swing.menu.model.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.astrapi69.swing.menu.MenuExtensions;
import io.github.astrapi69.swing.menu.enumeration.MenuType;
import io.github.astrapi69.swing.menu.model.KeyStrokeInfo;
import io.github.astrapi69.swing.menu.model.MenuInfo;
import io.github.astrapi69.swing.menu.model.MenuItemInfo;

/**
 * The parameterized unit test class for the class {@link MenuItemInfoConverter}
 */
class MenuItemInfoConverterParameterizedTest
{

	private static final ActionListener NO_ACTION = e -> {
	};

	private static AbstractAction newAction(final String name, final String shortDescription)
	{
		AbstractAction action = new AbstractAction(name)
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
			}
		};
		if (shortDescription != null)
		{
			action.putValue(AbstractAction.SHORT_DESCRIPTION, shortDescription);
		}
		return action;
	}

	private static void assertOpenFields(final AbstractButton button)
	{
		assertEquals("menu.id.open", button.getName());
		assertEquals("Open", button.getText());
		assertEquals("Opens a file", button.getToolTipText());
		assertEquals(MenuExtensions.toMnemonic('O'), button.getMnemonic());
		assertEquals("open", button.getActionCommand());
		assertTrue(button.isEnabled());
	}

	/**
	 * Parameterized test for all factory methods of the class {@link MenuItemInfoConverter} with
	 * every value of the enum {@link MenuType}
	 */
	@ParameterizedTest(name = "[{index}] a menu item info of the type {0} fills every component")
	@EnumSource(MenuType.class)
	void everyMenuTypeFillsTheProducedComponents(MenuType type)
	{
		KeyStroke accelerator = KeyStroke.getKeyStroke("ctrl pressed O");
		MenuItemInfo menuItemInfo = MenuItemInfo.builder().type(type).name("menu.id.open")
			.text("Open").toolTip("Opens a file").mnemonic(MenuExtensions.toMnemonic('O'))
			.actionCommand("open").keyStrokeInfo(KeyStrokeInfo.toKeyStrokeInfo(accelerator))
			.build();

		JMenuItem menuItem = MenuItemInfoConverter.toJMenuItem(menuItemInfo);
		assertOpenFields(menuItem);
		assertEquals(accelerator, menuItem.getAccelerator());

		JCheckBoxMenuItem checkBoxMenuItem = MenuItemInfoConverter
			.toJCheckBoxMenuItem(menuItemInfo);
		assertOpenFields(checkBoxMenuItem);
		assertEquals(accelerator, checkBoxMenuItem.getAccelerator());
		assertFalse(checkBoxMenuItem.isSelected());

		JRadioButtonMenuItem radioButtonMenuItem = MenuItemInfoConverter
			.toJRadioButtonMenuItem(menuItemInfo);
		assertOpenFields(radioButtonMenuItem);
		assertEquals(accelerator, radioButtonMenuItem.getAccelerator());

		JMenu menu = MenuItemInfoConverter.toJMenu(menuItemInfo);
		assertOpenFields(menu);
		// a menu never gets the accelerator
		assertNull(menu.getAccelerator());

		JMenuBar menuBar = MenuItemInfoConverter.toJMenuBar(menuItemInfo);
		// a menu bar only gets the name, the tool tip, the enabled and the visible state
		assertEquals("menu.id.open", menuBar.getName());
		assertEquals("Opens a file", menuBar.getToolTipText());
		assertTrue(menuBar.isEnabled());
		assertTrue(menuBar.isVisible());
	}

	static Stream<Arguments> roundTripFixtures()
	{
		return Stream.of(
			Arguments.of(MenuType.MENU, "global.menu.edit", "Edit", "Edit the content",
				MenuExtensions.toMnemonic('E'), "edit", null),
			Arguments.of(MenuType.MENU, "global.menu.file", "File", null,
				MenuExtensions.toMnemonic('F'), "file", null),
			Arguments.of(MenuType.MENU_ITEM, "global.menu.file.open", "Open", "Opens a file",
				MenuExtensions.toMnemonic('O'), "open", "ctrl pressed O"),
			Arguments.of(MenuType.MENU_ITEM, "global.menu.file.exit", "Exit", null,
				MenuExtensions.toMnemonic('X'), "exit", null),
			Arguments.of(MenuType.CHECK_BOX_MENU_ITEM, "global.menu.view.grid", "Grid",
				"Shows the grid", MenuExtensions.toMnemonic('G'), "grid", "ctrl pressed G"),
			Arguments.of(MenuType.RADIO_BUTTON_MENU_ITEM, "global.menu.view.desktop", "Desktop",
				null, MenuExtensions.toMnemonic('D'), "desktop", "shift ctrl pressed D"));
	}

	/**
	 * Parameterized test for the round trip of a {@link MenuItemInfo} object over the swing
	 * component back to a {@link MenuItemInfo} object
	 */
	@ParameterizedTest(name = "[{index}] the round trip of the {0} {1} keeps all fields")
	@MethodSource("roundTripFixtures")
	void roundTripOverTheSwingComponent(MenuType type, String name, String text, String toolTip,
		int mnemonic, String actionCommand, String accelerator)
	{
		if (MenuType.MENU.equals(type))
		{
			MenuInfo expected = MenuInfo.builder().type(type).name(name).text(text).toolTip(toolTip)
				.mnemonic(mnemonic).actionCommand(actionCommand).build();
			JMenu menu = MenuItemInfoConverter.toMenuItemInfo(expected, NO_ACTION).toJMenu();
			assertEquals(expected, MenuItemInfoConverter.fromJMenu(menu));
			return;
		}
		KeyStrokeInfo keyStrokeInfo = accelerator != null
			? KeyStrokeInfo.toKeyStrokeInfo(KeyStroke.getKeyStroke(accelerator))
			: null;
		MenuItemInfo expected = MenuItemInfo.builder().type(type).name(name).text(text)
			.toolTip(toolTip).mnemonic(mnemonic).actionCommand(actionCommand)
			.keyStrokeInfo(keyStrokeInfo).build();
		MenuItemInfo withListener = MenuItemInfoConverter.toMenuItemInfo(expected, NO_ACTION);
		MenuItemInfo actual = switch (type)
		{
			case CHECK_BOX_MENU_ITEM -> MenuItemInfoConverter
				.fromJCheckBoxMenuItem(withListener.toJCheckBoxMenuItem());
			case RADIO_BUTTON_MENU_ITEM -> MenuItemInfoConverter
				.fromJRadioButtonMenuItem(withListener.toJRadioButtonMenuItem());
			default -> MenuItemInfoConverter.fromJMenuItem(withListener.toJMenuItem());
		};
		// the action listener is not readable from the component and is not part of the round trip
		assertNull(actual.getActionListener());
		assertEquals(expected, actual);
	}

	/**
	 * Parameterized test that shows that the action command of a {@link MenuItemInfo} object
	 * without an explicit action command survives the round trip as {@code null} (the swing-derived
	 * fall back of the action command to the button text is filtered out and no longer leaks into
	 * the round trip); the round trip is still not the full identity because a button without an
	 * explicit mnemonic always reports the mnemonic {@code 0}, never {@code null}
	 */
	@ParameterizedTest(name = "[{index}] a menu item info with only the text {0} keeps the action command null on the round trip")
	@ValueSource(strings = { "Open", "Exit", "Auto save" })
	void roundTripOfAMenuItemInfoWithoutAnActionCommand(String text)
	{
		MenuItemInfo sparse = MenuItemInfo.builder().type(MenuType.MENU_ITEM).name("menu.id.sparse")
			.text(text).build();

		MenuItemInfo readBack = MenuItemInfoConverter.fromJMenuItem(sparse.toJMenuItem());

		assertEquals(sparse.getName(), readBack.getName());
		assertEquals(sparse.getText(), readBack.getText());
		// the swing-derived fall back of the action command to the text is filtered out
		assertNull(sparse.getActionCommand());
		assertNull(readBack.getActionCommand());
		// a button without a mnemonic still reports the mnemonic 0, not null, so the round trip
		// is not the full identity
		assertNull(sparse.getMnemonic());
		assertEquals(0, readBack.getMnemonic().intValue());
		assertNotEquals(sparse, readBack);
	}

	/**
	 * Parameterized test for the method
	 * {@link MenuItemInfoConverter#setFields(MenuItemInfo, AbstractButton)} with the matrix of all
	 * optional fields
	 */
	@ParameterizedTest(name = "[{index}] text={0}, toolTip={1}, mnemonic={2}, actionCommand={3}, "
		+ "enabled={4}, visible={5}, selected={6}")
	@CsvSource(nullValues = "null", value = {
			"Open, Opens a file, 79, open, null, null, null, null, null",
			"Save, null, 83, save, false, null, null, Save the file, Writes the file to disk",
			"null, Only a tool tip, null, null, true, false, true, null, null",
			"Grid, null, null, grid, true, true, true, Grid, Shows the grid",
			"Exit, Quit the app, 88, exit, false, false, false, Exit, Closes all windows" })
	void setFieldsSetsOnlyTheGivenFields(String text, String toolTip, Integer mnemonic,
		String actionCommand, Boolean enabled, Boolean visible, Boolean selected,
		String accessibleName, String accessibleDescription)
	{
		MenuItemInfo menuItemInfo = MenuItemInfo.builder().text(text).toolTip(toolTip)
			.mnemonic(mnemonic).actionCommand(actionCommand).enabled(enabled).visible(visible)
			.selected(selected).accessibleName(accessibleName)
			.accessibleDescription(accessibleDescription).build();
		JMenuItem untouched = new JMenuItem();
		JMenuItem menuItem = MenuItemInfoConverter.toJMenuItem(menuItemInfo);

		assertEquals(text != null ? text : untouched.getText(), menuItem.getText());
		assertEquals(toolTip, menuItem.getToolTipText());
		assertEquals(mnemonic != null ? mnemonic.intValue() : untouched.getMnemonic(),
			menuItem.getMnemonic());
		// without an action command the text is the action command
		assertEquals(actionCommand != null ? actionCommand : menuItem.getText(),
			menuItem.getActionCommand());
		assertEquals(enabled == null || enabled, menuItem.isEnabled());
		assertEquals(visible == null || visible, menuItem.isVisible());
		assertEquals(selected != null && selected, menuItem.isSelected());
		// swing derives the accessible name from the text and the description from the tool tip
		assertEquals(accessibleName != null ? accessibleName : menuItem.getText(),
			menuItem.getAccessibleContext().getAccessibleName());
		assertEquals(accessibleDescription != null ? accessibleDescription : toolTip,
			menuItem.getAccessibleContext().getAccessibleDescription());
		// no keystroke info means no accelerator
		assertNull(menuItem.getAccelerator());
	}

	static Stream<Arguments> actionFixtures()
	{
		return Stream.of(
			Arguments.of("the text and the tool tip of the action", null, null, "Action name",
				"Action tip"),
			Arguments.of("the explicit text", "Explicit", null, "Explicit", "Action tip"),
			Arguments.of("the explicit tool tip", null, "Explicit tip", "Action name",
				"Explicit tip"),
			Arguments.of("both explicit values", "Explicit", "Explicit tip", "Explicit",
				"Explicit tip"));
	}

	/**
	 * Parameterized test for the method
	 * {@link MenuItemInfoConverter#setFields(MenuItemInfo, AbstractButton)} with a bound
	 * {@link javax.swing.Action} object
	 */
	@ParameterizedTest(name = "[{index}] a bound action and {0} result in the text {3}")
	@MethodSource("actionFixtures")
	void explicitFieldsWinOverTheBoundAction(String label, String text, String toolTip,
		String expectedText, String expectedToolTip)
	{
		AbstractAction action = newAction("Action name", "Action tip");
		MenuItemInfo menuItemInfo = MenuItemInfo.builder().actionListener(action).text(text)
			.toolTip(toolTip).build();

		JMenuItem menuItem = MenuItemInfoConverter.toJMenuItem(menuItemInfo);

		assertEquals(expectedText, menuItem.getText());
		assertEquals(expectedToolTip, menuItem.getToolTipText());
		assertSame(action, menuItem.getAction());
		// the action is not added a second time as plain action listener
		assertEquals(1, menuItem.getActionListeners().length);
	}

	/**
	 * Parameterized test for the enabled state of a {@link MenuItemInfo} object with a bound
	 * {@link javax.swing.Action} object
	 */
	@ParameterizedTest(name = "[{index}] the info enabled={0} and the action enabled={1} result in the button enabled={2}")
	@CsvSource(nullValues = "null", value = { "null, true, true", "null, false, false",
			"true, false, true", "false, true, false" })
	void theEnabledStateOfTheInfoWinsButTheActionKeepsItInSync(Boolean infoEnabled,
		boolean actionEnabled, boolean expected)
	{
		AbstractAction action = newAction("Action name", null);
		action.setEnabled(actionEnabled);
		MenuItemInfo menuItemInfo = MenuItemInfo.builder().actionListener(action)
			.enabled(infoEnabled).build();

		JMenuItem menuItem = MenuItemInfoConverter.toJMenuItem(menuItemInfo);
		assertEquals(expected, menuItem.isEnabled());

		// a later change of the enabled state of the action always propagates to the button
		action.setEnabled(!actionEnabled);
		assertEquals(!actionEnabled, menuItem.isEnabled());
	}

	static Stream<Arguments> buttonFactories()
	{
		return Stream.of(
			Arguments.of("toJMenuItem",
				(Function<MenuItemInfo, AbstractButton>)MenuItemInfoConverter::toJMenuItem),
			Arguments.of("toJMenu",
				(Function<MenuItemInfo, AbstractButton>)MenuItemInfoConverter::toJMenu),
			Arguments.of("toJCheckBoxMenuItem",
				(Function<MenuItemInfo, AbstractButton>)MenuItemInfoConverter::toJCheckBoxMenuItem),
			Arguments.of("toJRadioButtonMenuItem",
				(Function<MenuItemInfo, AbstractButton>)MenuItemInfoConverter::toJRadioButtonMenuItem));
	}

	/**
	 * Parameterized test that a plain {@link ActionListener} object is added only once to the
	 * produced component
	 */
	@ParameterizedTest(name = "[{index}] {0} adds a plain action listener exactly once")
	@MethodSource("buttonFactories")
	void plainActionListenerIsAddedOnce(String label,
		Function<MenuItemInfo, AbstractButton> factory)
	{
		int[] performed = { 0 };
		ActionListener actionListener = event -> performed[0]++;
		AbstractButton button = factory
			.apply(MenuItemInfo.builder().text("Click").actionListener(actionListener).build());

		assertEquals(1, button.getActionListeners().length);
		// a plain action listener is no javax.swing.Action object
		assertNull(button.getAction());
		button.getActionListeners()[0]
			.actionPerformed(new ActionEvent(button, ActionEvent.ACTION_PERFORMED, "Click"));
		assertEquals(1, performed[0]);
	}

	static Stream<Arguments> nullRejectingFactories()
	{
		return Stream.of(
			Arguments.of("toJMenuItem",
				(Function<MenuItemInfo, Object>)MenuItemInfoConverter::toJMenuItem),
			Arguments.of("toJMenu", (Function<MenuItemInfo, Object>)MenuItemInfoConverter::toJMenu),
			Arguments.of("toJMenuBar",
				(Function<MenuItemInfo, Object>)MenuItemInfoConverter::toJMenuBar),
			Arguments.of("toJCheckBoxMenuItem",
				(Function<MenuItemInfo, Object>)MenuItemInfoConverter::toJCheckBoxMenuItem),
			Arguments.of("toJRadioButtonMenuItem",
				(Function<MenuItemInfo, Object>)MenuItemInfoConverter::toJRadioButtonMenuItem),
			Arguments.of("toMenuItem",
				(Function<MenuItemInfo, Object>)MenuItemInfoConverter::toMenuItem));
	}

	/**
	 * Parameterized test that all factory methods reject a null {@link MenuItemInfo} object
	 */
	@ParameterizedTest(name = "[{index}] {0} throws a NullPointerException for a null menu item info")
	@MethodSource("nullRejectingFactories")
	void factoryMethodsRejectNull(String label, Function<MenuItemInfo, Object> factory)
	{
		NullPointerException exception = assertThrows(NullPointerException.class,
			() -> factory.apply(null));
		assertEquals("menuItemInfo is marked non-null but is null", exception.getMessage());
	}

	/**
	 * Parameterized test that the method
	 * {@link MenuItemInfoConverter#setFields(MenuItemInfo, AbstractButton)} rejects null arguments
	 */
	@ParameterizedTest(name = "[{index}] setFields throws a NullPointerException for a null {0}")
	@ValueSource(strings = { "menuItemInfo", "button" })
	void setFieldsRejectsNullArguments(String parameter)
	{
		Executable call = "menuItemInfo".equals(parameter)
			? () -> MenuItemInfoConverter.setFields(null, new JMenuItem())
			: () -> MenuItemInfoConverter.setFields(MenuItemInfo.builder().build(), null);
		NullPointerException exception = assertThrows(NullPointerException.class, call);
		assertEquals(parameter + " is marked non-null but is null", exception.getMessage());
	}

	/**
	 * Parameterized test for the method {@link MenuItemInfoConverter#resolveIcon(String)} with icon
	 * paths that can not be resolved
	 */
	@ParameterizedTest(name = "[{index}] the icon path {0} can not be resolved")
	@NullSource
	@EmptySource
	@ValueSource(strings = { " ", "   ", "icons/missing.png", "/icons/missing.png",
			"not/a/path.png" })
	void resolveIconReturnsNullForUnresolvablePaths(String iconPath)
	{
		assertNull(MenuItemInfoConverter.resolveIcon(iconPath));
	}

	/**
	 * Parameterized test for the method {@link MenuItemInfoConverter#resolveIcon(String)} with icon
	 * paths that are resolved from the classpath
	 */
	@ParameterizedTest(name = "[{index}] the icon path {0} is resolved from the classpath")
	@ValueSource(strings = { "icons/dot.png", "/icons/dot.png" })
	void resolveIconResolvesClasspathResources(String iconPath)
	{
		Icon icon = MenuItemInfoConverter.resolveIcon(iconPath);
		assertNotNull(icon);
		assertEquals(2, icon.getIconWidth());
	}

	/**
	 * Parameterized test for the method {@link MenuItemInfoConverter#toMenuItem(MenuItemInfo)}
	 */
	@ParameterizedTest(name = "[{index}] the awt menu item of {0} keeps the label and the shortcut")
	@CsvSource(nullValues = "null", value = { "Exit, 88, exit, false", "Open, null, null, true" })
	void toAwtMenuItem(String text, Integer mnemonic, String actionCommand, boolean enabled)
	{
		// the awt MenuItem is a heavyweight component and needs a display
		assumeFalse(GraphicsEnvironment.isHeadless());
		MenuItemInfo menuItemInfo = MenuItemInfo.builder().name("menu.id.awt").text(text)
			.mnemonic(mnemonic).actionCommand(actionCommand).enabled(enabled)
			.actionListener(NO_ACTION).build();

		java.awt.MenuItem menuItem = MenuItemInfoConverter.toMenuItem(menuItemInfo);

		assertEquals(text, menuItem.getLabel());
		assertEquals("menu.id.awt", menuItem.getName());
		assertEquals(enabled, menuItem.isEnabled());
		assertEquals(1, menuItem.getActionListeners().length);
		if (mnemonic == null)
		{
			assertNull(menuItem.getShortcut());
			assertEquals(text, menuItem.getActionCommand());
		}
		else
		{
			assertEquals(mnemonic.intValue(), menuItem.getShortcut().getKey());
			assertEquals(actionCommand, menuItem.getActionCommand());
		}
	}
}
