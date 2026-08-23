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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.function.Executable;
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
 * The parameterized unit test class for the class {@link MenuInfoExtensions}
 */
class MenuInfoExtensionsParameterizedTest
{

	private static MenuInfo item(final String name)
	{
		return MenuInfo.builder().type(MenuType.MENU_ITEM).name(name).build();
	}

	private static MenuInfo item(final String name, final Anchor anchor)
	{
		return MenuInfo.builder().type(MenuType.MENU_ITEM).name(name).anchor(anchor).build();
	}

	private static MenuInfo item(final String name, final Anchor anchor,
		final String relativeToMenuId)
	{
		return MenuInfo.builder().type(MenuType.MENU_ITEM).name(name).anchor(anchor)
			.relativeToMenuId(relativeToMenuId).build();
	}

	private static MenuInfo menu(final String name, final MenuInfo... children)
	{
		MenuInfo menuInfo = MenuInfo.builder().type(MenuType.MENU).name(name)
			.children(new ArrayList<>()).build();
		for (MenuInfo child : children)
		{
			menuInfo.addChild(child);
		}
		return menuInfo;
	}

	private static List<String> names(final List<MenuInfo> menuInfos)
	{
		return menuInfos.stream().map(MenuInfo::getName).toList();
	}

	private static List<String> flattenedNames(final MenuInfo root)
	{
		return names(MenuInfoExtensions.flatten(root));
	}

	private static List<String> siblings(final String pipeSeparatedNames)
	{
		return pipeSeparatedNames == null ? List.of() : List.of(pipeSeparatedNames.split("\\|"));
	}

	static Stream<Arguments> orderByAnchorPlacesTheChildren()
	{
		return Stream.of(
			Arguments.of("an empty list stays empty", List.<MenuInfo> of(), List.<String> of()),
			Arguments.of("children without an anchor keep the document order",
				List.of(item("a"), item("b"), item("c")), List.of("a", "b", "c")),
			Arguments.of("the anchor LAST keeps the document order",
				List.of(item("a", Anchor.LAST), item("b"), item("c", Anchor.LAST)),
				List.of("a", "b", "c")),
			Arguments.of("the anchor UNKNOWN keeps the document order",
				List.of(item("a", Anchor.UNKNOWN), item("b"), item("c")), List.of("a", "b", "c")),
			Arguments.of("the anchor FIRST moves the children to the front in document order",
				List.of(item("a"), item("first1", Anchor.FIRST), item("b"),
					item("first2", Anchor.FIRST)),
				List.of("first1", "first2", "a", "b")),
			Arguments.of("the anchors BEFORE and AFTER place the child relative to its target",
				List.of(item("a"), item("b"), item("c"), item("beforeB", Anchor.BEFORE, "b"),
					item("afterB", Anchor.AFTER, "b")),
				List.of("a", "beforeB", "b", "afterB", "c")),
			Arguments.of("a chain of relative children is resolved in any document order",
				List.of(item("c", Anchor.AFTER, "b"), item("b", Anchor.AFTER, "a"), item("a")),
				List.of("a", "b", "c")),
			Arguments.of("an unknown relative menu id appends the child",
				List.of(item("a"), item("b"), item("orphan", Anchor.BEFORE, "missing")),
				List.of("a", "b", "orphan")),
			Arguments.of("a cycle appends the children in document order",
				List.of(item("x"), item("cycle1", Anchor.AFTER, "cycle2"),
					item("cycle2", Anchor.AFTER, "cycle1")),
				List.of("x", "cycle1", "cycle2")),
			Arguments.of("a chain that ends in an unknown relative menu id is appended",
				List.of(item("a"), item("x", Anchor.AFTER, "missing"),
					item("y", Anchor.AFTER, "x")),
				List.of("a", "x", "y")),
			Arguments.of("the anchor BEFORE without a relative menu id keeps the document order",
				List.of(item("a"), item("b", Anchor.BEFORE, null), item("c")),
				List.of("a", "b", "c")),
			Arguments.of("the anchor AFTER without a relative menu id keeps the document order",
				List.of(item("a"), item("b", Anchor.AFTER, null), item("c")),
				List.of("a", "b", "c")),
			Arguments.of("a relative child can reference a child with the anchor FIRST",
				List.of(item("afterFirst", Anchor.AFTER, "first"), item("plain"),
					item("first", Anchor.FIRST)),
				List.of("first", "afterFirst", "plain")),
			Arguments.of("several children BEFORE the same target keep the document order",
				List.of(item("a"), item("b"), item("p", Anchor.BEFORE, "b"),
					item("q", Anchor.BEFORE, "b")),
				List.of("a", "p", "q", "b")),
			Arguments.of("several children AFTER the same target keep their declaration order",
				List.of(item("a"), item("b"), item("p", Anchor.AFTER, "a"),
					item("q", Anchor.AFTER, "a")),
				List.of("a", "p", "q", "b")),
			Arguments.of("the anchor FIRST and the relative anchors are combined",
				List.of(item("a"), item("afterB", Anchor.AFTER, "b"), item("b"),
					item("last", Anchor.LAST), item("first", Anchor.FIRST),
					item("beforeA", Anchor.BEFORE, "a"), item("orphan", Anchor.BEFORE, "missing"),
					item("c")),
				List.of("first", "beforeA", "a", "b", "afterB", "last", "c", "orphan")));
	}

	/**
	 * Parameterized test for {@link MenuInfoExtensions#orderByAnchor(List)}
	 */
	@ParameterizedTest(name = "[{index}] {0}")
	@MethodSource
	void orderByAnchorPlacesTheChildren(final String caseName, final List<MenuInfo> children,
		final List<String> expectedNames)
	{
		List<MenuInfo> ordered = MenuInfoExtensions.orderByAnchor(children);
		assertEquals(expectedNames, names(ordered), caseName);
		assertEquals(children.size(), ordered.size(), caseName);
		assertTrue(ordered.containsAll(children), caseName);
	}

	/**
	 * Parameterized test for {@link MenuInfoExtensions#orderByAnchor(List)} with a single anchored
	 * child without a relative menu id
	 */
	@ParameterizedTest(name = "[{index}] a single child with the anchor {0} and no relative id")
	@EnumSource(Anchor.class)
	void orderByAnchorWithASingleAnchoredChild(final Anchor anchor)
	{
		List<MenuInfo> ordered = MenuInfoExtensions
			.orderByAnchor(List.of(item("a"), item("b", anchor), item("c")));
		List<String> expected = anchor == Anchor.FIRST
			? List.of("b", "a", "c")
			: List.of("a", "b", "c");
		assertEquals(expected, names(ordered));
	}

	/**
	 * Parameterized test for {@link MenuInfoExtensions#insertIndex(List, MenuInfo)}
	 */
	@ParameterizedTest(name = "[{index}] insertIndex in [{0}] with the anchor {1} relative to {2}")
	@CsvSource({ "file|edit|help, , , 3", "file|edit|help, LAST, , 3",
			"file|edit|help, UNKNOWN, , 3", "file|edit|help, FIRST, , 0",
			"file|edit|help, FIRST, edit, 0", "file|edit|help, BEFORE, file, 0",
			"file|edit|help, BEFORE, edit, 1", "file|edit|help, BEFORE, help, 2",
			"file|edit|help, AFTER, file, 1", "file|edit|help, AFTER, edit, 2",
			"file|edit|help, AFTER, help, 3", "file|edit|help, BEFORE, nope, 3",
			"file|edit|help, AFTER, nope, 3", "file|edit|help, BEFORE, , 3",
			"file|edit|help, AFTER, , 3", "file|edit|help, LAST, edit, 3",
			"file|edit|help, UNKNOWN, edit, 3", "file, BEFORE, file, 0", "file, AFTER, file, 1",
			", FIRST, , 0", ", LAST, , 0", ", BEFORE, file, 0", ", , , 0" })
	void insertIndexReturnsThePositionForTheAnchor(final String pipeSeparatedSiblings,
		final Anchor anchor, final String relativeToMenuId, final int expectedIndex)
	{
		List<String> siblingNames = siblings(pipeSeparatedSiblings);
		MenuInfo child = item("child", anchor, relativeToMenuId);
		assertEquals(expectedIndex, MenuInfoExtensions.insertIndex(siblingNames, child));
	}

	/**
	 * Parameterized test for {@link MenuInfoExtensions#insertIndex(List, MenuInfo)} without a
	 * relative menu id, only the anchor {@link Anchor#FIRST} does not append
	 */
	@ParameterizedTest(name = "[{index}] insertIndex with the anchor {0} and no relative menu id")
	@EnumSource(Anchor.class)
	void insertIndexWithoutARelativeMenuId(final Anchor anchor)
	{
		List<String> siblingNames = List.of("file", "edit", "help");
		int expectedIndex = anchor == Anchor.FIRST ? 0 : siblingNames.size();
		assertEquals(expectedIndex,
			MenuInfoExtensions.insertIndex(siblingNames, item("child", anchor, null)));
		assertEquals(expectedIndex,
			MenuInfoExtensions.insertIndex(siblingNames, item("child", anchor, "nope")));
	}

	/**
	 * Parameterized test for {@link MenuInfoExtensions#indexOf(List, String)}
	 */
	@ParameterizedTest(name = "[{index}] indexOf(''{0}'') is {1}")
	@CsvSource({ "file, 0", "edit, 1", "help, 2", "nope, -1", "FILE, -1", "'', -1", ", -1" })
	void indexOfReturnsThePositionOfTheChild(final String name, final int expectedIndex)
	{
		List<MenuInfo> children = List.of(item("file"), item("edit"), item("help"));
		assertEquals(expectedIndex, MenuInfoExtensions.indexOf(children, name));
	}

	/**
	 * Parameterized test for {@link MenuInfoExtensions#find(MenuInfo, String)}
	 */
	@ParameterizedTest(name = "[{index}] find(''{0}'') in the menu bar tree")
	@NullSource
	@EmptySource
	@ValueSource(strings = { "bar", "file", "new", "exit", "edit", "nope", "FILE" })
	void findReturnsTheNodeWithTheGivenName(final String name)
	{
		MenuInfo root = menu("bar", menu("file", item("new"), item("exit")), menu("edit"));
		boolean expected = name != null
			&& List.of("bar", "file", "new", "exit", "edit").contains(name);
		assertEquals(expected, MenuInfoExtensions.find(root, name).isPresent());
		if (expected)
		{
			assertEquals(name, MenuInfoExtensions.find(root, name).orElseThrow().getName());
		}
	}

	static Stream<Arguments> flattenReturnsTheTreeInDepthFirstOrder()
	{
		return Stream.of(Arguments.of("a single node", menu("bar"), List.of("bar")),
			Arguments.of("a node with a leaf child", menu("bar", item("file")),
				List.of("bar", "file")),
			Arguments.of("two levels",
				menu("bar", menu("file", item("new"), item("exit")), menu("edit", item("copy"))),
				List.of("bar", "file", "new", "exit", "edit", "copy")),
			Arguments.of("a leaf without children", item("lonely"), List.of("lonely")));
	}

	/**
	 * Parameterized test for {@link MenuInfoExtensions#flatten(MenuInfo)}
	 */
	@ParameterizedTest(name = "[{index}] flatten of {0}")
	@MethodSource
	void flattenReturnsTheTreeInDepthFirstOrder(final String caseName, final MenuInfo root,
		final List<String> expectedNames)
	{
		List<MenuInfo> flattened = MenuInfoExtensions.flatten(root);
		assertSame(root, flattened.get(0), caseName);
		assertEquals(expectedNames, names(flattened), caseName);
	}

	static Stream<Arguments> mergeAddsAndOrdersTheContributedChildren()
	{
		return Stream.of(
			Arguments.of("a new child is appended", menu("bar", item("file"), item("edit")),
				menu("bar", item("help")), List.of("bar", "file", "edit", "help")),
			Arguments.of("a new child with the anchor FIRST is placed at the front",
				menu("bar", item("file"), item("edit")), menu("bar", item("help", Anchor.FIRST)),
				List.of("bar", "help", "file", "edit")),
			Arguments.of("a new child with the anchor BEFORE is placed before its target",
				menu("bar", item("file"), item("edit")),
				menu("bar", item("view", Anchor.BEFORE, "edit")),
				List.of("bar", "file", "view", "edit")),
			Arguments.of("a new child with the anchor AFTER is placed after its target",
				menu("bar", item("file"), item("edit")),
				menu("bar", item("view", Anchor.AFTER, "file")),
				List.of("bar", "file", "view", "edit")),
			Arguments.of("an existing child is merged recursively",
				menu("bar", menu("file", item("new"), item("exit"))),
				menu("bar", menu("file", item("export", Anchor.BEFORE, "exit"))),
				List.of("bar", "file", "new", "export", "exit")),
			Arguments.of("an empty contribution leaves the base unchanged",
				menu("bar", item("file"), item("edit")), menu("bar"),
				List.of("bar", "file", "edit")),
			Arguments.of("an existing child without contributed children is not duplicated",
				menu("bar", item("file"), item("edit")), menu("bar", item("file")),
				List.of("bar", "file", "edit")),
			Arguments.of("several contributed children are placed in one pass",
				menu("bar", item("file"), item("help")),
				menu("bar", item("edit", Anchor.BEFORE, "help"),
					item("view", Anchor.AFTER, "file")),
				List.of("bar", "file", "view", "edit", "help")),
			Arguments.of("a whole sub tree that does not exist is added", menu("bar", item("file")),
				menu("bar", menu("plugin", item("a"), item("b"))),
				List.of("bar", "file", "plugin", "a", "b")),
			Arguments.of("a contribution into an empty base adds all children", menu("bar"),
				menu("bar", item("file"), item("edit")), List.of("bar", "file", "edit")),
			Arguments.of("a contributed child without a name is always added",
				menu("bar", item(null)), menu("bar", item(null)),
				Arrays.asList("bar", null, null)));
	}

	/**
	 * Parameterized test for {@link MenuInfoExtensions#merge(MenuInfo, MenuInfo)}
	 */
	@ParameterizedTest(name = "[{index}] {0}")
	@MethodSource
	void mergeAddsAndOrdersTheContributedChildren(final String caseName, final MenuInfo base,
		final MenuInfo contribution, final List<String> expectedNames)
	{
		MenuInfo merged = MenuInfoExtensions.merge(base, contribution);
		assertSame(base, merged, caseName);
		assertEquals(expectedNames, flattenedNames(merged), caseName);
	}

	static Stream<Arguments> mergeKeepsTheAttributesOfTheBase()
	{
		return Stream.of(
			Arguments.of("text", (Function<MenuInfo, Object>)MenuInfo::getText, "File",
				"Plugin File"),
			Arguments.of("toolTip", (Function<MenuInfo, Object>)MenuInfo::getToolTip,
				"The file menu", "The plugin menu"),
			Arguments.of("actionId", (Function<MenuInfo, Object>)MenuInfo::getActionId,
				"file.action", "plugin.action"),
			Arguments.of("mnemonic", (Function<MenuInfo, Object>)MenuInfo::getMnemonic, 70, 80),
			Arguments.of("type", (Function<MenuInfo, Object>)MenuInfo::getType, MenuType.MENU,
				MenuType.MENU_ITEM));
	}

	/**
	 * Parameterized test that {@link MenuInfoExtensions#merge(MenuInfo, MenuInfo)} keeps the
	 * attributes of the existing nodes of the base tree
	 */
	@ParameterizedTest(name = "[{index}] merge keeps the {0} of the existing child")
	@MethodSource
	void mergeKeepsTheAttributesOfTheBase(final String attributeName,
		final Function<MenuInfo, Object> getter, final Object baseValue,
		final Object contributedValue)
	{
		MenuInfo baseFile = MenuInfo.builder().type(MenuType.MENU).name("file").text("File")
			.toolTip("The file menu").actionId("file.action").mnemonic(70).build();
		MenuInfo contributedFile = MenuInfo.builder().type(MenuType.MENU_ITEM).name("file")
			.text("Plugin File").toolTip("The plugin menu").actionId("plugin.action").mnemonic(80)
			.build();
		contributedFile.addChild(item("plugin.export"));
		MenuInfo base = menu("bar", baseFile);
		MenuInfo contribution = menu("bar", contributedFile);

		MenuInfo merged = MenuInfoExtensions.merge(base, contribution);

		MenuInfo mergedFile = merged.getChildren().get(0);
		assertSame(baseFile, mergedFile, attributeName);
		assertEquals(baseValue, getter.apply(mergedFile), attributeName);
		assertNotEquals(contributedValue, getter.apply(mergedFile), attributeName);
		assertEquals(List.of("plugin.export"), names(mergedFile.getChildren()), attributeName);
	}

	static Stream<Arguments> nullArgumentsAreRejected()
	{
		MenuInfo root = menu("bar", item("file"));
		return Stream.of(
			Arguments.of("find with a null root",
				(Executable)() -> MenuInfoExtensions.find(null, "file"),
				"root is marked non-null but is null"),
			Arguments.of("flatten with a null root",
				(Executable)() -> MenuInfoExtensions.flatten(null),
				"root is marked non-null but is null"),
			Arguments.of("orderByAnchor with a null list",
				(Executable)() -> MenuInfoExtensions.orderByAnchor(null),
				"children is marked non-null but is null"),
			Arguments.of("insertIndex with null sibling names",
				(Executable)() -> MenuInfoExtensions.insertIndex(null, item("child")),
				"siblingNames is marked non-null but is null"),
			Arguments.of("insertIndex with a null child",
				(Executable)() -> MenuInfoExtensions.insertIndex(List.of("file"), null),
				"child is marked non-null but is null"),
			Arguments.of("indexOf with a null list",
				(Executable)() -> MenuInfoExtensions.indexOf(null, "file"),
				"children is marked non-null but is null"),
			Arguments.of("merge with a null base",
				(Executable)() -> MenuInfoExtensions.merge(null, root),
				"base is marked non-null but is null"),
			Arguments.of("merge with a null contribution",
				(Executable)() -> MenuInfoExtensions.merge(root, null),
				"contribution is marked non-null but is null"));
	}

	/**
	 * Parameterized test for the null argument cases of the {@link MenuInfoExtensions} methods
	 */
	@ParameterizedTest(name = "[{index}] {0} is rejected")
	@MethodSource
	void nullArgumentsAreRejected(final String caseName, final Executable executable,
		final String expectedMessageFragment)
	{
		NullPointerException exception = assertThrows(NullPointerException.class, executable,
			caseName);
		assertTrue(exception.getMessage().contains(expectedMessageFragment), "the message '"
			+ exception.getMessage() + "' must contain '" + expectedMessageFragment + "'");
	}

	/**
	 * Parameterized test that {@link MenuInfoExtensions#find(MenuInfo, String)} does not find a
	 * name in a tree that does not contain it
	 */
	@ParameterizedTest(name = "[{index}] a leaf without children does not contain ''{0}''")
	@NullSource
	@EmptySource
	@ValueSource(strings = { "file", "leaf " })
	void findInALeafWithoutChildren(final String name)
	{
		MenuInfo leaf = item("leaf");
		assertFalse(MenuInfoExtensions.find(leaf, name).isPresent());
		assertTrue(MenuInfoExtensions.find(leaf, "leaf").isPresent());
	}
}
