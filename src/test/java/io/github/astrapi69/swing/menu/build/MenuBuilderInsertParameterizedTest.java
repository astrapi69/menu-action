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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.astrapi69.swing.menu.enumeration.Anchor;
import io.github.astrapi69.swing.menu.enumeration.MenuType;
import io.github.astrapi69.swing.menu.model.MenuInfo;

/**
 * The parameterized unit test class for the anchor placement and the runtime insertion of the class
 * {@link MenuBuilder}
 */
class MenuBuilderInsertParameterizedTest
{

	private static MenuBuilder newBuilder()
	{
		return new MenuBuilder().withMissingActionPolicy(MissingActionPolicy.IGNORE);
	}

	private static MenuInfo newItem(final String name)
	{
		return MenuInfo.builder().type(MenuType.MENU_ITEM).name(name).text(name).build();
	}

	private static MenuInfo newAnchoredItem(final String name, final Anchor anchor,
		final String relativeToMenuId)
	{
		return MenuInfo.builder().type(MenuType.MENU_ITEM).name(name).text(name).anchor(anchor)
			.relativeToMenuId(relativeToMenuId).build();
	}

	private static MenuInfo newMenuWithTwoItems()
	{
		return MenuInfo.builder().type(MenuType.MENU).name("parent").text("Parent").build()
			.addChild(newItem("b")).addChild(newItem("c"));
	}

	private static List<String> childNames(final JComponent parent)
	{
		List<String> names = new ArrayList<>();
		Component[] children = parent instanceof JMenu menu
			? menu.getMenuComponents()
			: parent.getComponents();
		for (Component child : children)
		{
			names.add(child.getName());
		}
		return names;
	}

	private static Component childAt(final JComponent parent, final int index)
	{
		return parent instanceof JMenu menu
			? menu.getMenuComponent(index)
			: parent.getComponent(index);
	}

	/**
	 * Parameterized test for the placement of a child with every {@link Anchor} that is relative to
	 * the first of its two siblings in {@link MenuBuilder#buildMenu(MenuInfo)}
	 */
	@ParameterizedTest(name = "the anchor {0} relative to the first sibling")
	@EnumSource(Anchor.class)
	void anchorRelativeToTheFirstSiblingInBuildMenu(final Anchor anchor)
	{
		int expectedIndex = switch (anchor)
		{
			case BEFORE, FIRST -> 0;
			case AFTER -> 1;
			case LAST, UNKNOWN -> 2;
		};
		MenuInfo menuInfo = newMenuWithTwoItems().addChild(newAnchoredItem("x", anchor, "b"));

		JMenu menu = newBuilder().buildMenu(menuInfo);

		assertEquals(3, menu.getMenuComponentCount());
		assertEquals("x", menu.getMenuComponent(expectedIndex).getName());
		assertEquals(expectedIndex, childNames(menu).indexOf("x"));
	}

	/**
	 * Parameterized test for the placement of a child in {@link MenuBuilder#buildMenu(MenuInfo)}
	 * between the siblings 'b' and 'c'
	 */
	@ParameterizedTest(name = "the anchor {0} relative to {1} places the child at the index {2}")
	@CsvSource(nullValues = "NULL", value = { "FIRST,   NULL, 0", "FIRST,   b,    0",
			"BEFORE,  b,    0", "BEFORE,  c,    1", "AFTER,   b,    1", "AFTER,   c,    2",
			"LAST,    NULL, 2", "UNKNOWN, NULL, 2", "UNKNOWN, b,    2", "NULL,    NULL, 2",
			"NULL,    b,    2", "BEFORE,  NULL, 2", "AFTER,   NULL, 2", "BEFORE,  nope, 2",
			"AFTER,   nope, 2" })
	void anchorPlacementInBuildMenu(final Anchor anchor, final String relativeToMenuId,
		final int expectedIndex)
	{
		MenuInfo menuInfo = newMenuWithTwoItems()
			.addChild(newAnchoredItem("x", anchor, relativeToMenuId));

		JMenu menu = newBuilder().buildMenu(menuInfo);

		assertEquals(3, menu.getMenuComponentCount());
		assertEquals(expectedIndex, childNames(menu).indexOf("x"));
	}

	/**
	 * Parameterized test for the placement of a child in
	 * {@link MenuBuilder#insert(String, MenuInfo)} between the siblings 'b' and 'c'
	 */
	@ParameterizedTest(name = "insert with the anchor {0} relative to {1} at the index {2}")
	@CsvSource(nullValues = "NULL", value = { "FIRST,   NULL, 0", "FIRST,   b,    0",
			"BEFORE,  b,    0", "BEFORE,  c,    1", "AFTER,   b,    1", "AFTER,   c,    2",
			"LAST,    NULL, 2", "UNKNOWN, NULL, 2", "UNKNOWN, b,    2", "NULL,    NULL, 2",
			"NULL,    b,    2", "BEFORE,  NULL, 2", "AFTER,   NULL, 2", "BEFORE,  nope, 2",
			"AFTER,   nope, 2" })
	void anchorPlacementInInsert(final Anchor anchor, final String relativeToMenuId,
		final int expectedIndex)
	{
		MenuBuilder builder = newBuilder();
		JMenu menu = builder.buildMenu(newMenuWithTwoItems());

		JComponent inserted = builder.insert("parent",
			newAnchoredItem("x", anchor, relativeToMenuId));

		assertEquals(3, menu.getMenuComponentCount());
		assertEquals(expectedIndex, childNames(menu).indexOf("x"));
		assertSame(inserted, menu.getMenuComponent(expectedIndex));
		assertSame(inserted, builder.getComponent("x").orElseThrow());
	}

	private static MenuInfo newParentInfo(final MenuType parentType)
	{
		MenuType childType = parentType == MenuType.MENU_BAR ? MenuType.MENU : MenuType.MENU_ITEM;
		return MenuInfo.builder().type(parentType).name("parent").text("Parent").build()
			.addChild(MenuInfo.builder().type(childType).name("b").text("b").build())
			.addChild(MenuInfo.builder().type(childType).name("c").text("c").build());
	}

	private static Class<? extends Component> expectedInsertedClass(final MenuType parentType,
		final MenuType childType)
	{
		if (parentType == MenuType.TOOL_BAR)
		{
			return switch (childType)
			{
				case SEPARATOR -> JToolBar.Separator.class;
				case CHECK_BOX_MENU_ITEM, RADIO_BUTTON_MENU_ITEM -> JToggleButton.class;
				default -> JButton.class;
			};
		}
		return switch (childType)
		{
			// a separator can not be built as menu component of a menu bar
			case SEPARATOR -> parentType == MenuType.MENU_BAR ? null : JPopupMenu.Separator.class;
			case MENU -> JMenu.class;
			case CHECK_BOX_MENU_ITEM -> JCheckBoxMenuItem.class;
			case RADIO_BUTTON_MENU_ITEM -> JRadioButtonMenuItem.class;
			default -> JMenuItem.class;
		};
	}

	private static Stream<Arguments> parentsAndChildren()
	{
		List<Arguments> arguments = new ArrayList<>();
		for (MenuType parentType : List.of(MenuType.MENU_BAR, MenuType.MENU, MenuType.POPUP,
			MenuType.TOOL_BAR))
		{
			for (MenuType childType : List.of(MenuType.MENU, MenuType.MENU_ITEM,
				MenuType.CHECK_BOX_MENU_ITEM, MenuType.RADIO_BUTTON_MENU_ITEM, MenuType.SEPARATOR))
			{
				arguments.add(Arguments.of(parentType, childType,
					expectedInsertedClass(parentType, childType)));
			}
		}
		return arguments.stream();
	}

	/**
	 * Parameterized test for {@link MenuBuilder#insert(String, MenuInfo)} with every parent type
	 * and every child type
	 */
	@ParameterizedTest(name = "insert a child of the type {1} into a parent of the type {0}")
	@MethodSource("parentsAndChildren")
	void insertEveryChildTypeInEveryParentType(final MenuType parentType, final MenuType childType,
		final Class<? extends Component> expectedClass)
	{
		MenuBuilder builder = newBuilder();
		JComponent parent = builder.build(newParentInfo(parentType));
		assertEquals(2, childNames(parent).size());
		MenuInfo child = MenuInfo.builder().type(childType).name("child").text("Child").build();
		if (expectedClass == null)
		{
			IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> builder.insert("parent", child));
			assertEquals("A JMenuBar has no separator, only menus can be inserted into 'parent'",
				exception.getMessage());
			assertEquals(2, childNames(parent).size());
			return;
		}
		JComponent inserted = builder.insert("parent", child);

		assertEquals(expectedClass, inserted.getClass());
		assertEquals(3, childNames(parent).size());
		assertSame(inserted, childAt(parent, 2));
		if (childType == MenuType.SEPARATOR)
		{
			// separators are not registered in the component lookup
			assertTrue(builder.getComponent("child").isEmpty());
		}
		else
		{
			assertSame(inserted, builder.getComponent("child").orElseThrow());
		}
	}

	private static Stream<Arguments> invalidParents()
	{
		return Stream.of(Arguments.of("menu.item", "of the type javax.swing.JMenuItem"),
			Arguments.of("menu.check", "of the type javax.swing.JCheckBoxMenuItem"),
			Arguments.of("tool.bar.button", "of the type javax.swing.JButton"));
	}

	/**
	 * Parameterized test for {@link MenuBuilder#insert(String, MenuInfo)} with a built component
	 * that can not take menu children
	 */
	@ParameterizedTest(name = "insert into the built component {0} is rejected")
	@MethodSource("invalidParents")
	void insertIntoAComponentThatCanNotTakeChildren(final String parentName,
		final String expectedMessagePart)
	{
		MenuBuilder builder = newBuilder();
		builder.buildMenu(MenuInfo.builder().type(MenuType.MENU).name("menu").text("Menu").build()
			.addChild(newItem("menu.item")).addChild(MenuInfo.builder()
				.type(MenuType.CHECK_BOX_MENU_ITEM).name("menu.check").text("Check").build()));
		builder.buildToolBar(MenuInfo.builder().type(MenuType.TOOL_BAR).name("tool.bar")
			.text("Tool bar").build().addChild(newItem("tool.bar.button")));

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
			() -> builder.insert(parentName, newItem("child")));

		assertEquals(
			"The component '" + parentName + "' " + expectedMessagePart
				+ " can not take menu children, expected a JMenuBar, JMenu, JPopupMenu or JToolBar",
			exception.getMessage());
		assertFalse(builder.getComponent("child").isPresent());
	}

	/**
	 * Parameterized test for {@link MenuBuilder#insert(String, MenuInfo)} with a parent name that
	 * was never built
	 */
	@ParameterizedTest(name = "insert into the not built parent [{0}] is rejected")
	@NullSource
	@EmptySource
	@ValueSource(strings = { " ", "unknown.parent", "parent.child" })
	void insertIntoAParentThatIsNotBuilt(final String parentName)
	{
		MenuBuilder builder = newBuilder();
		builder.buildMenu(newMenuWithTwoItems());
		MenuInfo child = newItem("child");
		if (parentName == null)
		{
			NullPointerException exception = assertThrows(NullPointerException.class,
				() -> builder.insert(parentName, child));
			assertTrue(exception.getMessage().contains("parentName"), exception.getMessage());
			return;
		}
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
			() -> builder.insert(parentName, child));
		assertEquals(
			"No built component with the name '" + parentName + "', build the parent menu first",
			exception.getMessage());
		assertFalse(builder.getComponent("child").isPresent());
	}
}
