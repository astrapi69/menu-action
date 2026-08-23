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
package io.github.astrapi69.swing.menu.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.ActionListener;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuBar;
import javax.swing.KeyStroke;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import io.github.astrapi69.swing.menu.MenuExtensions;
import io.github.astrapi69.swing.menu.enumeration.Anchor;
import io.github.astrapi69.swing.menu.enumeration.MenuType;

/**
 * The parameterized unit test class for the class {@link MenuItemInfo}
 */
class MenuItemInfoParameterizedTest
{

	private static final ActionListener NO_ACTION = event -> {
	};

	private static final ActionListener OTHER_ACTION = event -> {
	};

	private static final Icon ICON = new ImageIcon();

	private static final Icon OTHER_ICON = new ImageIcon();

	private static MenuItemInfo.MenuItemInfoBuilder base()
	{
		return MenuItemInfo.builder().name("global.menu.file.exit").text("Exit")
			.toolTip("Exit the application").mnemonic(MenuExtensions.toMnemonic('X'))
			.keyStrokeInfo(KeyStrokeInfo.toKeyStrokeInfo(KeyStroke.getKeyStroke("alt F4")))
			.type(MenuType.MENU_ITEM).anchor(Anchor.BEFORE)
			.relativeToMenuId("global.menu.file.open").actionCommand("exit")
			.actionListener(NO_ACTION).enabled(Boolean.FALSE).visible(Boolean.FALSE)
			.selected(Boolean.TRUE).icon(ICON).accessibleName("Exit the application")
			.accessibleDescription("Closes all windows");
	}

	static Stream<Arguments> singleFieldMutators()
	{
		return Stream.of(
			Arguments.of("name",
				(UnaryOperator<MenuItemInfo.MenuItemInfoBuilder>)builder -> builder
					.name("global.menu.file.quit")),
			Arguments.of("text",
				(UnaryOperator<MenuItemInfo.MenuItemInfoBuilder>)builder -> builder.text("Quit")),
			Arguments.of("toolTip",
				(UnaryOperator<MenuItemInfo.MenuItemInfoBuilder>)builder -> builder
					.toolTip("Quit the application")),
			Arguments.of("mnemonic",
				(UnaryOperator<MenuItemInfo.MenuItemInfoBuilder>)builder -> builder
					.mnemonic(MenuExtensions.toMnemonic('Q'))),
			Arguments.of("keyStrokeInfo",
				(UnaryOperator<MenuItemInfo.MenuItemInfoBuilder>)builder -> builder.keyStrokeInfo(
					KeyStrokeInfo.toKeyStrokeInfo(KeyStroke.getKeyStroke("ctrl Q")))),
			Arguments.of("type",
				(UnaryOperator<MenuItemInfo.MenuItemInfoBuilder>)builder -> builder
					.type(MenuType.CHECK_BOX_MENU_ITEM)),
			Arguments.of("anchor",
				(UnaryOperator<MenuItemInfo.MenuItemInfoBuilder>)builder -> builder
					.anchor(Anchor.AFTER)),
			Arguments.of("relativeToMenuId",
				(UnaryOperator<MenuItemInfo.MenuItemInfoBuilder>)builder -> builder
					.relativeToMenuId("global.menu.file.new")),
			Arguments.of("actionCommand",
				(UnaryOperator<MenuItemInfo.MenuItemInfoBuilder>)builder -> builder
					.actionCommand("quit")),
			Arguments.of("actionListener",
				(UnaryOperator<MenuItemInfo.MenuItemInfoBuilder>)builder -> builder
					.actionListener(OTHER_ACTION)),
			Arguments.of("enabled",
				(UnaryOperator<MenuItemInfo.MenuItemInfoBuilder>)builder -> builder.enabled(null)),
			Arguments.of("visible",
				(UnaryOperator<MenuItemInfo.MenuItemInfoBuilder>)builder -> builder.visible(null)),
			Arguments.of("selected",
				(UnaryOperator<MenuItemInfo.MenuItemInfoBuilder>)builder -> builder
					.selected(Boolean.FALSE)),
			Arguments.of("icon",
				(UnaryOperator<MenuItemInfo.MenuItemInfoBuilder>)builder -> builder
					.icon(OTHER_ICON)),
			Arguments.of("accessibleName",
				(UnaryOperator<MenuItemInfo.MenuItemInfoBuilder>)builder -> builder
					.accessibleName("Quit the application")),
			Arguments.of("accessibleDescription",
				(UnaryOperator<MenuItemInfo.MenuItemInfoBuilder>)builder -> builder
					.accessibleDescription("Closes the main window")));
	}

	/**
	 * Parameterized test that two {@link MenuItemInfo} objects that differ in exactly one field are
	 * not equal and have a different hash code
	 */
	@ParameterizedTest(name = "[{index}] two menu item infos that differ in the field {0} are not equal")
	@MethodSource("singleFieldMutators")
	void objectsThatDifferInOneFieldAreNotEqual(String field,
		UnaryOperator<MenuItemInfo.MenuItemInfoBuilder> mutator)
	{
		MenuItemInfo menuItemInfo = base().build();
		MenuItemInfo other = mutator.apply(base()).build();

		assertNotEquals(menuItemInfo, other);
		assertNotEquals(other, menuItemInfo);
		assertNotEquals(menuItemInfo.hashCode(), other.hashCode());
	}

	/**
	 * Parameterized test that identical copies of a {@link MenuItemInfo} object are equal
	 */
	@ParameterizedTest(name = "[{index}] an identical copy with the field {0} is equal")
	@MethodSource("singleFieldMutators")
	void identicalCopiesAreEqual(String field,
		UnaryOperator<MenuItemInfo.MenuItemInfoBuilder> mutator)
	{
		MenuItemInfo menuItemInfo = mutator.apply(base()).build();

		MenuItemInfo copy = mutator.apply(base()).build();
		assertEquals(menuItemInfo, copy);
		assertEquals(menuItemInfo.hashCode(), copy.hashCode());
		assertEquals(menuItemInfo.toString(), copy.toString());
		assertNotEquals(null, menuItemInfo);
	}

	static Stream<Arguments> buttonFactories()
	{
		return Stream.of(
			Arguments.of("toJMenuItem",
				(Function<MenuItemInfo, AbstractButton>)MenuItemInfo::toJMenuItem, true),
			Arguments.of("toJMenu", (Function<MenuItemInfo, AbstractButton>)MenuItemInfo::toJMenu,
				false),
			Arguments.of("toJCheckBoxMenuItem",
				(Function<MenuItemInfo, AbstractButton>)MenuItemInfo::toJCheckBoxMenuItem, true),
			Arguments.of("toJRadioButtonMenuItem",
				(Function<MenuItemInfo, AbstractButton>)MenuItemInfo::toJRadioButtonMenuItem,
				true));
	}

	/**
	 * Parameterized test for the factory methods of the class {@link MenuItemInfo} that create
	 * swing components
	 */
	@ParameterizedTest(name = "[{index}] {0} creates a component with all fields")
	@MethodSource("buttonFactories")
	void factoryMethodsCreateFilledComponents(String label,
		Function<MenuItemInfo, AbstractButton> factory, boolean withAccelerator)
	{
		MenuItemInfo menuItemInfo = base().build();

		AbstractButton button = factory.apply(menuItemInfo);

		assertEquals("global.menu.file.exit", button.getName());
		assertEquals("Exit", button.getText());
		assertEquals("Exit the application", button.getToolTipText());
		assertEquals(MenuExtensions.toMnemonic('X'), button.getMnemonic());
		assertEquals("exit", button.getActionCommand());
		assertFalse(button.isEnabled());
		assertFalse(button.isVisible());
		assertTrue(button.isSelected());
		assertEquals(ICON, button.getIcon());
		assertEquals("Exit the application", button.getAccessibleContext().getAccessibleName());
		assertEquals("Closes all windows",
			button.getAccessibleContext().getAccessibleDescription());
		javax.swing.JMenuItem menuItem = (javax.swing.JMenuItem)button;
		if (withAccelerator)
		{
			assertEquals(KeyStroke.getKeyStroke("alt F4"), menuItem.getAccelerator());
		}
		else
		{
			// a menu never gets an accelerator
			assertNull(menuItem.getAccelerator());
		}
		// the menu bar only gets the name, the tool tip, the enabled and the visible state
		JMenuBar menuBar = menuItemInfo.toJMenuBar();
		assertEquals("global.menu.file.exit", menuBar.getName());
		assertEquals("Exit the application", menuBar.getToolTipText());
		assertFalse(menuBar.isEnabled());
		assertFalse(menuBar.isVisible());
	}
}
