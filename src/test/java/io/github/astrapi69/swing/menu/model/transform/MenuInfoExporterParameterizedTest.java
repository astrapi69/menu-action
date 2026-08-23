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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import io.github.astrapi69.swing.menu.enumeration.MenuType;
import io.github.astrapi69.swing.menu.model.MenuInfo;

/**
 * The parameterized unit test class for the class {@link MenuInfoExporter}
 */
class MenuInfoExporterParameterizedTest
{

	static class ExitAction extends AbstractAction
	{
		ExitAction()
		{
			super("Exit");
		}

		@Override
		public void actionPerformed(ActionEvent event)
		{
		}
	}

	private static JMenuBar newMenuBar(final String name, final int menuCount)
	{
		JMenuBar menuBar = new JMenuBar();
		menuBar.setName(name);
		for (int i = 0; i < menuCount; i++)
		{
			JMenu menu = new JMenu("Menu " + i);
			menu.add(new JMenuItem("Item " + i));
			menuBar.add(menu);
		}
		return menuBar;
	}

	private static JMenu newFileMenu()
	{
		JMenu menu = new JMenu("File");
		menu.setName("file");
		JMenuItem open = new JMenuItem("Open File...");
		open.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
		menu.add(open);
		menu.addSeparator();
		JMenu recent = new JMenu("Recent");
		recent.add(new JMenuItem("a.txt"));
		menu.add(recent);
		return menu;
	}

	private static JPopupMenu newPopupMenu(final String name, final int itemCount)
	{
		JPopupMenu popupMenu = new JPopupMenu("Tree");
		popupMenu.setName(name);
		for (int i = 0; i < itemCount; i++)
		{
			popupMenu.add(new JMenuItem("Item " + i));
		}
		return popupMenu;
	}

	private static JToolBar newToolBar(final String name)
	{
		JToolBar toolBar = new JToolBar();
		toolBar.setName(name);
		toolBar.add(new JButton("New"));
		toolBar.addSeparator();
		toolBar.add(new JToggleButton("Status", true));
		// a component that is no button is not exported
		toolBar.add(new JLabel("ignored"));
		return toolBar;
	}

	private static JMenuBar newMenuBarWithActions()
	{
		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("File");
		file.add(new JMenuItem(new ExitAction()));
		file.add(new JMenuItem("Plain"));
		file.add(new JMenuItem(new AbstractAction("Anonymous")
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
			}
		}));
		menuBar.add(file);
		return menuBar;
	}

	static Stream<Arguments> componentFixtures()
	{
		return Stream.of(
			Arguments.of("an unnamed menu bar", new JMenuBar(), MenuType.MENU_BAR,
				"global.menu.bar", 0),
			Arguments.of("a menu bar with two menus", newMenuBar(null, 2), MenuType.MENU_BAR,
				"global.menu.bar", 2),
			Arguments.of("a named menu bar", newMenuBar("app.bar", 1), MenuType.MENU_BAR, "app.bar",
				1),
			Arguments.of("a menu with an item, a separator and a sub menu", newFileMenu(),
				MenuType.MENU, "file", 3),
			Arguments.of("an unnamed popup menu", newPopupMenu(null, 2), MenuType.POPUP, "popup",
				2),
			Arguments.of("a named popup menu", newPopupMenu("tree.popup", 3), MenuType.POPUP,
				"tree.popup", 3),
			Arguments.of("an unnamed tool bar", newToolBar(null), MenuType.TOOL_BAR,
				"global.tool.bar", 3),
			Arguments.of("a named tool bar", newToolBar("editor.tool.bar"), MenuType.TOOL_BAR,
				"editor.tool.bar", 3),
			Arguments.of("a single menu item", new JMenuItem("Open File..."), MenuType.MENU_ITEM,
				"open.file", 0),
			Arguments.of("a check box menu item", new JCheckBoxMenuItem("Auto save"),
				MenuType.CHECK_BOX_MENU_ITEM, "auto.save", 0),
			Arguments.of("a radio button menu item", new JRadioButtonMenuItem("One"),
				MenuType.RADIO_BUTTON_MENU_ITEM, "one", 0),
			Arguments.of("a separator", new JSeparator(), MenuType.SEPARATOR, null, 0),
			Arguments.of("a label", new JLabel("no menu component"), null, null, 0));
	}

	/**
	 * Parameterized test for the method {@link MenuInfoExporter#fromComponent(Component)} with all
	 * supported menu components
	 */
	@ParameterizedTest(name = "[{index}] {0} is exported as {2} with the id {3}")
	@MethodSource("componentFixtures")
	void exportedTypeAndGeneratedId(String label, Component component, MenuType expectedType,
		String expectedName, int expectedChildren)
	{
		MenuInfo exported = MenuInfoExporter.fromComponent(component);

		if (expectedType == null)
		{
			// a component that is no menu component is not exported
			assertNull(exported);
			return;
		}
		assertNotNull(exported);
		assertEquals(expectedType, exported.getType());
		assertEquals(expectedName, exported.getName());
		assertEquals(expectedChildren, exported.getChildren().size());
		// the exporter with a strategy that resolves no action id exports the same tree
		assertEquals(exported, MenuInfoExporter.withActionIds(button -> null).export(component));
	}

	/**
	 * Parameterized test for the generated ids of components without a name
	 */
	@ParameterizedTest(name = "[{index}] the text {0} generates the id {1}")
	@CsvSource(nullValues = "null", value = { "Open File..., parent.open.file", "New, parent.new",
			"a.txt, parent.a.txt", "Zoom 100%, parent.zoom.100", "Save As, parent.save.as",
			"'', parent.item", "'...', parent.item", "' Trim me ', parent.trim.me",
			"null, parent.item" })
	void generatedIdsFromTheText(String text, String expectedId)
	{
		JMenu parent = new JMenu("Parent");
		parent.setName("parent");
		parent.add(new JMenuItem(text));

		MenuInfo exported = MenuInfoExporter.fromComponent(parent);

		assertEquals(expectedId, exported.getChildren().get(0).getName());
		// a component with a name keeps its name
		JMenu named = new JMenu("Parent");
		named.setName("parent");
		JMenuItem item = new JMenuItem(text);
		item.setName("explicit.id");
		named.add(item);
		assertEquals("explicit.id",
			MenuInfoExporter.fromComponent(named).getChildren().get(0).getName());
	}

	/**
	 * Parameterized test that shows that the generated ids of two sibling components whose texts
	 * would otherwise collide (they differ only in their not alphanumeric characters, in case, or
	 * in a diacritic that the unicode-aware slug now keeps) stay unique: the first occurrence keeps
	 * the plain slug and every following collision gets an incrementing numeric suffix
	 */
	@ParameterizedTest(name = "[{index}] the texts {0} and {1} generate the distinct ids {2} and {3}")
	@CsvSource({ "Neu!, Neu?, parent.neu, parent.neu.2",
			"Save As, save as, parent.save.as, parent.save.as.2",
			"Ärger, ärger, parent.ärger, parent.ärger.2" })
	void differentTextsGenerateDistinctIds(String first, String second, String expectedFirstId,
		String expectedSecondId)
	{
		JMenu parent = new JMenu("Parent");
		parent.setName("parent");
		parent.add(new JMenuItem(first));
		parent.add(new JMenuItem(second));

		MenuInfo exported = MenuInfoExporter.fromComponent(parent);

		// the generated slug of the colliding second text gets a numeric suffix, so both names
		// stay unique
		assertEquals(expectedFirstId, exported.getChildren().get(0).getName());
		assertEquals(expectedSecondId, exported.getChildren().get(1).getName());
		assertNotEquals(expectedFirstId, expectedSecondId);
	}

	static Stream<Arguments> actionIdStrategies()
	{
		return Stream.of(
			Arguments.of("the strategy with the action class name",
				MenuInfoExporter.actionIdFromActionClass(), "exitAction", null, null),
			Arguments.of("the strategy with the action name",
				MenuInfoExporter.actionIdFromActionName(), "Exit", null, "Anonymous"),
			Arguments.of("a custom strategy",
				(Function<AbstractButton, String>)button -> "custom."
					+ button.getText().toLowerCase(),
				"custom.exit", "custom.plain", "custom.anonymous"),
			Arguments.of("a strategy without action ids",
				(Function<AbstractButton, String>)button -> null, null, null, null));
	}

	/**
	 * Parameterized test for the method {@link MenuInfoExporter#withActionIds(Function)} with the
	 * two provided strategies and a custom strategy
	 */
	@ParameterizedTest(name = "[{index}] {0} exports the action ids {2}, {3} and {4}")
	@MethodSource("actionIdStrategies")
	void actionIdStrategies(String label, Function<AbstractButton, String> strategy,
		String expectedForAction, String expectedForPlain, String expectedForAnonymous)
	{
		JMenuBar menuBar = newMenuBarWithActions();

		MenuInfo exported = MenuInfoExporter.withActionIds(strategy).export(menuBar);

		List<MenuInfo> items = exported.getChildren().get(0).getChildren();
		assertEquals(expectedForAction, items.get(0).getActionId());
		assertEquals(expectedForPlain, items.get(1).getActionId());
		// the simple class name of an anonymous action class is empty
		assertEquals(expectedForAnonymous, items.get(2).getActionId());
		// the exporter without a strategy exports no action ids
		assertNull(MenuInfoExporter.fromJMenuBar(menuBar).getChildren().get(0).getChildren().get(0)
			.getActionId());
	}

	/**
	 * Parameterized test for the exported options of a {@link JToolBar} object
	 */
	@ParameterizedTest(name = "[{index}] a tool bar with floatable={0}, rollover={1} and enabled={2} exports {3}, {4} and {5}")
	@CsvSource(nullValues = "null", value = { "true, false, true, null, null, null",
			"false, false, true, false, null, null", "true, true, true, null, true, null",
			"false, true, false, false, true, false" })
	void exportedToolBarOptions(boolean floatable, boolean rollover, boolean enabled,
		Boolean expectedFloatable, Boolean expectedRollover, Boolean expectedEnabled)
	{
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(floatable);
		toolBar.setRollover(rollover);
		toolBar.setEnabled(enabled);

		MenuInfo exported = MenuInfoExporter.fromJToolBar(toolBar);

		assertEquals(MenuType.TOOL_BAR, exported.getType());
		// only the values that differ from the swing default are exported
		assertEquals(expectedFloatable, exported.getFloatable());
		assertEquals(expectedRollover, exported.getRollover());
		assertEquals(expectedEnabled, exported.getEnabled());
	}

	/**
	 * Parameterized test for the exported state of a {@link JMenuItem} object
	 */
	@ParameterizedTest(name = "[{index}] a menu item with enabled={0}, visible={1} and the mnemonic {2} exports {3}, {4} and {5}")
	@CsvSource(nullValues = "null", value = { "true, true, 0, null, null, null",
			"false, true, 69, false, null, 69", "true, false, 0, null, false, null",
			"false, false, 88, false, false, 88" })
	void exportedStateOfAMenuItem(boolean enabled, boolean visible, int mnemonic,
		Boolean expectedEnabled, Boolean expectedVisible, Integer expectedMnemonic)
	{
		JMenuItem menuItem = new JMenuItem("Item");
		menuItem.setEnabled(enabled);
		menuItem.setVisible(visible);
		menuItem.setMnemonic(mnemonic);

		MenuInfo exported = MenuInfoExporter.fromComponent(menuItem);

		assertEquals(expectedEnabled, exported.getEnabled());
		assertEquals(expectedVisible, exported.getVisible());
		assertEquals(expectedMnemonic, exported.getMnemonic());
		// an action command that is equal to the text is not exported
		assertNull(exported.getActionCommand());
	}

	/**
	 * Parameterized test for the exported selected state and the exported button group
	 */
	@ParameterizedTest(name = "[{index}] a {0} with selected={1} in a button group={2} exports selected={3} and group={4}")
	@CsvSource(nullValues = "null", value = { "CHECK_BOX_MENU_ITEM, true, false, true, null",
			"CHECK_BOX_MENU_ITEM, false, false, null, null",
			"CHECK_BOX_MENU_ITEM, true, true, true, group1",
			"RADIO_BUTTON_MENU_ITEM, true, true, true, group1",
			"RADIO_BUTTON_MENU_ITEM, false, true, null, group1",
			"RADIO_BUTTON_MENU_ITEM, false, false, null, null",
			"MENU_ITEM, false, false, null, null" })
	void exportedSelectedStateAndGroup(MenuType type, boolean selected, boolean grouped,
		Boolean expectedSelected, String expectedGroup)
	{
		AbstractButton button = switch (type)
		{
			case CHECK_BOX_MENU_ITEM -> new JCheckBoxMenuItem("Item", selected);
			case RADIO_BUTTON_MENU_ITEM -> new JRadioButtonMenuItem("Item", selected);
			default -> new JMenuItem("Item");
		};
		if (grouped)
		{
			new ButtonGroup().add(button);
		}

		MenuInfo exported = MenuInfoExporter.fromComponent(button);

		assertEquals(type, exported.getType());
		assertEquals(expectedSelected, exported.getSelected());
		assertEquals(expectedGroup, exported.getGroup());
	}

	/**
	 * Parameterized test for the exported accessible name of a {@link JMenuItem} object
	 */
	@ParameterizedTest(name = "[{index}] the text {0} with the accessible name {1} exports {2}")
	@CsvSource(nullValues = "null", value = { "Exit, null, null",
			"Exit, Exit the application, Exit the application", "Exit, Exit, null" })
	void exportedAccessibleName(String text, String accessibleName, String expected)
	{
		JMenuItem menuItem = new JMenuItem(text);
		if (accessibleName != null)
		{
			menuItem.getAccessibleContext().setAccessibleName(accessibleName);
		}

		MenuInfo exported = MenuInfoExporter.fromComponent(menuItem);

		// swing derives the accessible name from the text, only an explicit name is exported
		assertEquals(expected, exported.getAccessibleName());
		assertEquals(text, exported.getText());
	}

	/**
	 * Parameterized test for the exported accessible description of a {@link JMenuItem} object
	 */
	@ParameterizedTest(name = "[{index}] the tool tip {0} with the accessible description {1} exports {2}")
	@CsvSource(nullValues = "null", value = { "null, null, null", "A tool tip, null, null",
			"A tool tip, A description, A description", "null, A description, A description" })
	void exportedAccessibleDescription(String toolTip, String accessibleDescription,
		String expected)
	{
		JMenuItem menuItem = new JMenuItem("Exit");
		menuItem.setToolTipText(toolTip);
		if (accessibleDescription != null)
		{
			menuItem.getAccessibleContext().setAccessibleDescription(accessibleDescription);
		}

		MenuInfo exported = MenuInfoExporter.fromComponent(menuItem);

		// swing derives the accessible description from the tool tip, only an explicit
		// description (one that differs from the tool tip fallback) is exported
		assertEquals(expected, exported.getAccessibleDescription());
		assertEquals(toolTip, exported.getToolTip());
	}

	static Stream<Arguments> nullRejectingCalls()
	{
		return Stream.of(
			Arguments.of("fromJMenuBar", "menuBar",
				(Executable)() -> MenuInfoExporter.fromJMenuBar(null)),
			Arguments.of("fromJMenu", "menu", (Executable)() -> MenuInfoExporter.fromJMenu(null)),
			Arguments.of("fromJPopupMenu", "popupMenu",
				(Executable)() -> MenuInfoExporter.fromJPopupMenu(null)),
			Arguments.of("fromJToolBar", "toolBar",
				(Executable)() -> MenuInfoExporter.fromJToolBar(null)),
			Arguments.of("fromComponent", "component",
				(Executable)() -> MenuInfoExporter.fromComponent(null)),
			Arguments.of("withActionIds", "actionIdResolver",
				(Executable)() -> MenuInfoExporter.withActionIds(null)),
			Arguments.of("export", "component",
				(Executable)() -> MenuInfoExporter.withActionIds(button -> null).export(null)));
	}

	/**
	 * Parameterized test that the exporter rejects null arguments
	 */
	@ParameterizedTest(name = "[{index}] {0} throws a NullPointerException for a null {1}")
	@MethodSource("nullRejectingCalls")
	void nullArgumentsAreRejected(String label, String parameter, Executable call)
	{
		NullPointerException exception = assertThrows(NullPointerException.class, call);
		assertEquals(parameter + " is marked non-null but is null", exception.getMessage());
	}
}
