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
package io.github.astrapi69.swing.plaf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.awt.Component;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Stream;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.astrapi69.swing.menu.build.ActionRegistry;
import io.github.astrapi69.swing.menu.build.MenuBuilder;
import io.github.astrapi69.swing.menu.enumeration.BaseMenuId;
import io.github.astrapi69.swing.menu.enumeration.MenuType;
import io.github.astrapi69.swing.menu.model.MenuInfo;

/**
 * The parameterized unit test class for the class {@link LookAndFeelMenuFactory}
 */
class LookAndFeelMenuFactoryParameterizedTest
{

	/** The look and feel that was installed before a test method has changed it */
	private LookAndFeel previousLookAndFeel;

	/** The metal theme that was current before a test method has changed it */
	private MetalTheme previousMetalTheme;

	/** The component whose tree is updated by the registered actions */
	private JLabel component;

	@BeforeEach
	void setUp() throws Exception
	{
		previousLookAndFeel = UIManager.getLookAndFeel();
		previousMetalTheme = MetalLookAndFeel.getCurrentTheme();
		component = new JLabel("look and feel");
		// the nimbus look and feel is installed and supported in a headless environment, so the
		// selected menu item is deterministic
		LookAndFeels.setLookAndFeel(LookAndFeels.NIMBUS);
	}

	@AfterEach
	void tearDown() throws Exception
	{
		if (previousMetalTheme != null)
		{
			MetalLookAndFeel.setCurrentTheme(previousMetalTheme);
		}
		if (previousLookAndFeel != null)
		{
			UIManager.setLookAndFeel(previousLookAndFeel);
		}
	}

	static Stream<String> lookAndFeelIds()
	{
		return LookAndFeelMenuFactory.lookAndFeelIds().stream();
	}

	/**
	 * Parameterized test for {@link LookAndFeelMenuFactory#lookAndFeelIds()} and
	 * {@link LookAndFeelMenuFactory#registerActions(ActionRegistry, Component)}. Every id has a
	 * registered action
	 */
	@ParameterizedTest(name = "[{index}] the look and feel id ''{0}'' has a registered action")
	@MethodSource("lookAndFeelIds")
	void registerActionsRegistersAnActionForEveryId(String lookAndFeelId)
	{
		ActionRegistry registry = ActionRegistry.empty();

		assertSame(registry, LookAndFeelMenuFactory.registerActions(registry, component));

		assertTrue(lookAndFeelId.startsWith(BaseMenuId.LOOK_AND_FEEL_KEY), () -> "the id '"
			+ lookAndFeelId + "' should start with '" + BaseMenuId.LOOK_AND_FEEL_KEY + "'");
		assertTrue(registry.contains(lookAndFeelId));
		assertTrue(registry.find(lookAndFeelId).isPresent());
		assertEquals(LookAndFeelMenuFactory.lookAndFeelIds().size(), registry.size());
	}

	/**
	 * Parameterized test for {@link LookAndFeelMenuFactory#newLookAndFeelMenuInfo()}. Every id of
	 * {@link LookAndFeelMenuFactory#lookAndFeelIds()} has a radio button menu item child that
	 * belongs to the group {@link LookAndFeelMenuFactory#GROUP}
	 */
	@ParameterizedTest(name = "[{index}] the look and feel id ''{0}'' has a radio button menu item")
	@MethodSource("lookAndFeelIds")
	void newLookAndFeelMenuInfoContainsAnItemForEveryId(String lookAndFeelId)
	{
		MenuInfo menuInfo = LookAndFeelMenuFactory.newLookAndFeelMenuInfo();

		assertEquals(MenuType.MENU, menuInfo.getType());
		assertEquals(BaseMenuId.LOOK_AND_FEEL_KEY, menuInfo.getName());
		assertEquals("Look and Feel", menuInfo.getText());

		MenuInfo child = menuInfo.getChildren().stream()
			.filter(menuChild -> lookAndFeelId.equals(menuChild.getName())).findFirst()
			.orElseThrow();
		assertEquals(MenuType.RADIO_BUTTON_MENU_ITEM, child.getType());
		assertEquals(LookAndFeelMenuFactory.GROUP, child.getGroup());
		assertNotNull(child.getText());
		assertEquals(LookAndFeelMenuFactory.lookAndFeelIds().size(), menuInfo.getChildren().size());
		long selected = menuInfo.getChildren().stream()
			.filter(menuChild -> Boolean.TRUE.equals(menuChild.getSelected())).count();
		assertEquals(1, selected);
	}

	/**
	 * Parameterized test for the menu that is built from
	 * {@link LookAndFeelMenuFactory#newLookAndFeelMenuInfo()}. Every id has a
	 * {@link JRadioButtonMenuItem} object in the built menu and all items share the same
	 * {@link ButtonGroup} object
	 */
	@ParameterizedTest(name = "[{index}] the built menu has a grouped item for the id ''{0}''")
	@MethodSource("lookAndFeelIds")
	void buildMenuContainsAGroupedItemForEveryId(String lookAndFeelId)
	{
		List<String> lookAndFeelIds = LookAndFeelMenuFactory.lookAndFeelIds();
		MenuInfo menuInfo = LookAndFeelMenuFactory.newLookAndFeelMenuInfo("&Look and Feel");
		ActionRegistry registry = LookAndFeelMenuFactory.registerActions(ActionRegistry.empty(),
			component);
		MenuBuilder menuBuilder = new MenuBuilder(registry);

		JMenu menu = menuBuilder.buildMenu(menuInfo);

		assertEquals("Look and Feel", menu.getText());
		assertEquals(lookAndFeelIds.size(), menu.getItemCount());

		JRadioButtonMenuItem menuItem = menuBuilder
			.getComponent(lookAndFeelId, JRadioButtonMenuItem.class).orElseThrow();
		ButtonGroup buttonGroup = menuBuilder.getButtonGroup(LookAndFeelMenuFactory.GROUP)
			.orElseThrow();
		assertEquals(lookAndFeelIds.size(), buttonGroup.getButtonCount());
		assertTrue(Collections.list(buttonGroup.getElements()).contains(menuItem));
		assertEquals(1, selectedButtonCount(buttonGroup));
	}

	/**
	 * Parameterized test for the selection behaviour of the built look and feel menu. A click on
	 * the item of an id selects exactly this item within the group
	 */
	@ParameterizedTest(name = "[{index}] a click on the item of ''{0}'' selects only this item")
	@MethodSource("lookAndFeelIds")
	void clickOnMenuItemSelectsOnlyThisItem(String lookAndFeelId)
	{
		MenuInfo menuInfo = LookAndFeelMenuFactory.newLookAndFeelMenuInfo();
		ActionRegistry registry = LookAndFeelMenuFactory.registerActions(ActionRegistry.empty(),
			component);
		MenuBuilder menuBuilder = new MenuBuilder(registry);
		menuBuilder.buildMenu(menuInfo);
		ButtonGroup buttonGroup = menuBuilder.getButtonGroup(LookAndFeelMenuFactory.GROUP)
			.orElseThrow();
		JRadioButtonMenuItem menuItem = menuBuilder
			.getComponent(lookAndFeelId, JRadioButtonMenuItem.class).orElseThrow();

		menuItem.doClick();

		assertTrue(menuItem.isSelected());
		assertEquals(1, selectedButtonCount(buttonGroup));
		assertSame(menuItem.getModel(), buttonGroup.getSelection());
	}

	/**
	 * Parameterized test for {@link LookAndFeelMenuFactory#newLookAndFeelMenuInfo(String)} with
	 * different menu texts
	 */
	@ParameterizedTest(name = "[{index}] the menu info keeps the given text ''{0}''")
	@ValueSource(strings = { "Look and Feel", "&Look and Feel", "Aussehen", "  " })
	@EmptySource
	void newLookAndFeelMenuInfoWithText(String text)
	{
		MenuInfo menuInfo = LookAndFeelMenuFactory.newLookAndFeelMenuInfo(text);

		assertEquals(text, menuInfo.getText());
		assertEquals(BaseMenuId.LOOK_AND_FEEL_KEY, menuInfo.getName());
		assertEquals(MenuType.MENU, menuInfo.getType());
		assertEquals(LookAndFeelMenuFactory.lookAndFeelIds().size(), menuInfo.getChildren().size());
	}

	/**
	 * Parameterized test for
	 * {@link LookAndFeelMenuFactory#registerActions(ActionRegistry, Component)} with a null
	 * argument
	 */
	@ParameterizedTest(name = "[{index}] {0} throws a NullPointerException")
	@MethodSource("nullArguments")
	void registerActionsWithNullArgument(String description, String expectedMessagePart,
		Executable executable)
	{
		NullPointerException exception = assertThrows(NullPointerException.class, executable,
			description);
		assertNotNull(exception.getMessage());
		assertTrue(exception.getMessage().contains(expectedMessagePart), () -> "the message '"
			+ exception.getMessage() + "' should contain '" + expectedMessagePart + "'");
	}

	static Stream<Arguments> nullArguments()
	{
		JLabel component = new JLabel("look and feel");
		return Stream.of(
			Arguments.of("registerActions(null, component)",
				"registry is marked non-null but is null",
				(Executable)() -> LookAndFeelMenuFactory.registerActions(null, component)),
			Arguments.of("registerActions(registry, null)",
				"component is marked non-null but is null",
				(Executable)() -> LookAndFeelMenuFactory.registerActions(ActionRegistry.empty(),
					null)),
			Arguments.of("registerActions(null, null)", "registry is marked non-null but is null",
				(Executable)() -> LookAndFeelMenuFactory.registerActions(null, null)));
	}

	/**
	 * Parameterized test for {@link ActionRegistry#contains(String)} with unknown look and feel
	 * ids. No action is registered for them
	 */
	@ParameterizedTest(name = "[{index}] no action is registered for the unknown id ''{0}''")
	@NullSource
	@EmptySource
	@ValueSource(strings = { "global.menu.look.and.feel", "global.menu.file.exit",
			"global.menu.look.and.feel.unknown" })
	void noActionIsRegisteredForUnknownIds(String unknownId)
	{
		assumeFalse(LookAndFeelMenuFactory.lookAndFeelIds().contains(unknownId),
			() -> "the id '" + unknownId + "' is a look and feel id on this machine");
		ActionRegistry registry = LookAndFeelMenuFactory.registerActions(ActionRegistry.empty(),
			component);

		assertFalse(registry.contains(unknownId));
		assertTrue(registry.find(unknownId).isEmpty());
	}

	private static int selectedButtonCount(final ButtonGroup buttonGroup)
	{
		int selected = 0;
		for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons
			.hasMoreElements();)
		{
			if (buttons.nextElement().isSelected())
			{
				selected++;
			}
		}
		return selected;
	}
}
