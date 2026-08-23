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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import javax.swing.KeyStroke;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.astrapi69.swing.menu.MenuExtensions;
import io.github.astrapi69.swing.menu.enumeration.Anchor;
import io.github.astrapi69.swing.menu.enumeration.MenuType;

/**
 * The parameterized unit test class for the class {@link MenuInfo}
 */
class MenuInfoParameterizedTest
{

	private static MenuInfo.MenuInfoBuilder base()
	{
		return MenuInfo.builder().name("global.menu.file.exit").text("Exit")
			.textKey("menu.file.exit.text").toolTip("Exit the application")
			.toolTipKey("menu.file.exit.tooltip").mnemonic(MenuExtensions.toMnemonic('X'))
			.keyStrokeInfo(KeyStrokeInfo.toKeyStrokeInfo(KeyStroke.getKeyStroke("alt F4")))
			.type(MenuType.MENU_ITEM).anchor(Anchor.BEFORE)
			.relativeToMenuId("global.menu.file.open").actionCommand("exit").actionId("exitAction")
			.enabled(Boolean.FALSE).visible(Boolean.FALSE).selected(Boolean.TRUE).group("group1")
			.icon("icons/dot.png").model("exitModel").value("exit").showText(Boolean.TRUE)
			.floatable(Boolean.FALSE).rollover(Boolean.TRUE).accessibleName("Exit the application")
			.accessibleDescription("Closes all windows");
	}

	static Stream<Arguments> singleFieldMutators()
	{
		return Stream.of(
			Arguments.of("name",
				(UnaryOperator<MenuInfo.MenuInfoBuilder>)builder -> builder
					.name("global.menu.file.quit")),
			Arguments.of("text",
				(UnaryOperator<MenuInfo.MenuInfoBuilder>)builder -> builder.text("Quit")),
			Arguments.of("textKey",
				(UnaryOperator<MenuInfo.MenuInfoBuilder>)builder -> builder
					.textKey("menu.file.quit.text")),
			Arguments.of("toolTip",
				(UnaryOperator<MenuInfo.MenuInfoBuilder>)builder -> builder
					.toolTip("Quit the application")),
			Arguments.of("toolTipKey",
				(UnaryOperator<MenuInfo.MenuInfoBuilder>)builder -> builder
					.toolTipKey("menu.file.quit.tooltip")),
			Arguments.of("mnemonic",
				(UnaryOperator<MenuInfo.MenuInfoBuilder>)builder -> builder
					.mnemonic(MenuExtensions.toMnemonic('Q'))),
			Arguments.of("keyStrokeInfo",
				(UnaryOperator<MenuInfo.MenuInfoBuilder>)builder -> builder.keyStrokeInfo(
					KeyStrokeInfo.toKeyStrokeInfo(KeyStroke.getKeyStroke("ctrl Q")))),
			Arguments.of("type",
				(UnaryOperator<MenuInfo.MenuInfoBuilder>)builder -> builder.type(MenuType.MENU)),
			Arguments.of("anchor",
				(UnaryOperator<MenuInfo.MenuInfoBuilder>)builder -> builder.anchor(Anchor.AFTER)),
			Arguments.of("relativeToMenuId",
				(UnaryOperator<MenuInfo.MenuInfoBuilder>)builder -> builder
					.relativeToMenuId("global.menu.file.new")),
			Arguments.of("actionCommand",
				(UnaryOperator<MenuInfo.MenuInfoBuilder>)builder -> builder.actionCommand("quit")),
			Arguments.of("actionId",
				(UnaryOperator<MenuInfo.MenuInfoBuilder>)builder -> builder.actionId("quitAction")),
			Arguments.of("enabled",
				(UnaryOperator<MenuInfo.MenuInfoBuilder>)builder -> builder.enabled(null)),
			Arguments.of("visible",
				(UnaryOperator<MenuInfo.MenuInfoBuilder>)builder -> builder.visible(null)),
			Arguments.of("selected",
				(UnaryOperator<MenuInfo.MenuInfoBuilder>)builder -> builder
					.selected(Boolean.FALSE)),
			Arguments.of("group",
				(UnaryOperator<MenuInfo.MenuInfoBuilder>)builder -> builder.group("group2")),
			Arguments.of("icon",
				(UnaryOperator<MenuInfo.MenuInfoBuilder>)builder -> builder
					.icon("icons/other.png")),
			Arguments.of("model",
				(UnaryOperator<MenuInfo.MenuInfoBuilder>)builder -> builder.model("quitModel")),
			Arguments.of("value",
				(UnaryOperator<MenuInfo.MenuInfoBuilder>)builder -> builder.value("quit")),
			Arguments.of("showText",
				(UnaryOperator<MenuInfo.MenuInfoBuilder>)builder -> builder
					.showText(Boolean.FALSE)),
			Arguments.of("floatable",
				(UnaryOperator<MenuInfo.MenuInfoBuilder>)builder -> builder
					.floatable(Boolean.TRUE)),
			Arguments.of("rollover",
				(UnaryOperator<MenuInfo.MenuInfoBuilder>)builder -> builder.rollover(null)),
			Arguments.of("accessibleName",
				(UnaryOperator<MenuInfo.MenuInfoBuilder>)builder -> builder
					.accessibleName("Quit the application")),
			Arguments.of("accessibleDescription",
				(UnaryOperator<MenuInfo.MenuInfoBuilder>)builder -> builder
					.accessibleDescription("Closes the main window")),
			Arguments.of("children",
				(UnaryOperator<MenuInfo.MenuInfoBuilder>)builder -> builder
					.children(new ArrayList<>(
						List.of(MenuInfo.builder().type(MenuType.SEPARATOR).build())))));
	}

	/**
	 * Parameterized test that two {@link MenuInfo} objects that differ in exactly one field are not
	 * equal and have a different hash code
	 */
	@ParameterizedTest(name = "[{index}] two menu infos that differ in the field {0} are not equal")
	@MethodSource("singleFieldMutators")
	void objectsThatDifferInOneFieldAreNotEqual(String field,
		UnaryOperator<MenuInfo.MenuInfoBuilder> mutator)
	{
		MenuInfo menuInfo = base().build();
		MenuInfo other = mutator.apply(base()).build();

		assertNotEquals(menuInfo, other);
		assertNotEquals(other, menuInfo);
		assertNotEquals(menuInfo.hashCode(), other.hashCode());
		assertNotEquals(menuInfo.toString(), other.toString());
	}

	/**
	 * Parameterized test that identical copies of a {@link MenuInfo} object are equal
	 */
	@ParameterizedTest(name = "[{index}] an identical copy with the field {0} is equal")
	@MethodSource("singleFieldMutators")
	void identicalCopiesAreEqual(String field, UnaryOperator<MenuInfo.MenuInfoBuilder> mutator)
	{
		MenuInfo menuInfo = mutator.apply(base()).build();

		MenuInfo copy = menuInfo.toBuilder().build();
		assertEquals(menuInfo, copy);
		assertEquals(menuInfo.hashCode(), copy.hashCode());
		assertEquals(menuInfo.toString(), copy.toString());
		// the same builder produces the same object
		assertEquals(menuInfo, mutator.apply(base()).build());
		assertEquals(menuInfo.hashCode(), mutator.apply(base()).build().hashCode());
	}

	/**
	 * Parameterized test for the methods {@link MenuInfo#addChild(MenuInfo)} and
	 * {@link MenuInfo#hasChildren()}
	 */
	@ParameterizedTest(name = "[{index}] a menu with {0} children")
	@ValueSource(ints = { 0, 1, 3, 7 })
	void addChildAndHasChildren(int childCount)
	{
		MenuInfo menuInfo = MenuInfo.builder().type(MenuType.MENU).name("global.menu.file").build();
		assertFalse(menuInfo.hasChildren());
		// a null list of children is handled like an empty list
		menuInfo.setChildren(null);
		assertFalse(menuInfo.hasChildren());

		for (int i = 0; i < childCount; i++)
		{
			MenuInfo child = MenuInfo.builder().type(MenuType.MENU_ITEM).name("item" + i).build();
			// the method returns this object for method chaining
			assertSame(menuInfo, menuInfo.addChild(child));
		}

		if (childCount == 0)
		{
			// the list is only created when the first child is added
			assertNull(menuInfo.getChildren());
		}
		else
		{
			assertEquals(childCount, menuInfo.getChildren().size());
		}
		assertEquals(0 < childCount, menuInfo.hasChildren());
		NullPointerException exception = assertThrows(NullPointerException.class,
			() -> menuInfo.addChild(null));
		assertEquals("child is marked non-null but is null", exception.getMessage());
	}

	/**
	 * Parameterized test for the method
	 * {@link MenuInfo#toMenuItemInfo(java.awt.event.ActionListener)} with every value of the enum
	 * {@link MenuType}
	 */
	@ParameterizedTest(name = "[{index}] a menu info of the type {0} is converted to a menu item info")
	@EnumSource(MenuType.class)
	void toMenuItemInfoKeepsAllFields(MenuType type)
	{
		MenuInfo menuInfo = base().type(type).build();

		MenuItemInfo menuItemInfo = menuInfo.toMenuItemInfo(null);

		assertEquals(type, menuItemInfo.getType());
		assertEquals(menuInfo.getName(), menuItemInfo.getName());
		assertEquals(menuInfo.getText(), menuItemInfo.getText());
		assertEquals(menuInfo.getToolTip(), menuItemInfo.getToolTip());
		assertEquals(menuInfo.getMnemonic(), menuItemInfo.getMnemonic());
		assertEquals(menuInfo.getKeyStrokeInfo(), menuItemInfo.getKeyStrokeInfo());
		assertEquals(menuInfo.getAnchor(), menuItemInfo.getAnchor());
		assertEquals(menuInfo.getRelativeToMenuId(), menuItemInfo.getRelativeToMenuId());
		assertEquals(menuInfo.getActionCommand(), menuItemInfo.getActionCommand());
		assertEquals(menuInfo.getEnabled(), menuItemInfo.getEnabled());
		assertEquals(menuInfo.getVisible(), menuItemInfo.getVisible());
		assertEquals(menuInfo.getSelected(), menuItemInfo.getSelected());
		assertEquals(menuInfo.getAccessibleName(), menuItemInfo.getAccessibleName());
		assertEquals(menuInfo.getAccessibleDescription(), menuItemInfo.getAccessibleDescription());
		// the icon path is resolved to an icon object
		assertNull(menuItemInfo.getActionListener());
		assertTrue(0 < menuItemInfo.getIcon().getIconWidth());
	}
}
