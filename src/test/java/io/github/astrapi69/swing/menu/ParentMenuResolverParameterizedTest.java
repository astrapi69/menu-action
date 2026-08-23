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
package io.github.astrapi69.swing.menu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToolBar;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

/**
 * The parameterized unit test class for the class {@link ParentMenuResolver}
 */
class ParentMenuResolverParameterizedTest
{

	/** The menu bar of the fixture with the menus 'File' &gt; 'Foo' &gt; 'Bar' */
	private JMenuBar menuBar;

	/** The top level menu 'File' of the fixture */
	private JMenu file;

	/** The sub menu 'Foo' of the fixture */
	private JMenu foo;

	/** The sub sub menu 'Bar' of the fixture */
	private JMenu bar;

	@BeforeEach
	void setUp()
	{
		menuBar = new JMenuBar();
		file = new JMenu("File");
		menuBar.add(file);
		foo = new JMenu("Foo");
		file.add(foo);
		bar = new JMenu("Bar");
		foo.add(bar);
	}

	/**
	 * Parameterized test for {@link ParentMenuResolver#getMenuElementType(MenuElement)}
	 */
	@ParameterizedTest(name = "[{index}] the menu element type of a {0} is {2}")
	@MethodSource("menuElements")
	void getMenuElementType(String description, MenuElement menuElement,
		Optional<Class<?>> expected)
	{
		assertEquals(expected, ParentMenuResolver.getMenuElementType(menuElement), description);
	}

	static Stream<Arguments> menuElements()
	{
		return Stream.of(Arguments.of("JMenu", new JMenu("File"), Optional.of(JMenu.class)),
			Arguments.of("JMenuBar", new JMenuBar(), Optional.of(JMenuBar.class)),
			Arguments.of("JMenuItem", new JMenuItem("Exit"), Optional.of(JMenuItem.class)),
			Arguments.of("JCheckBoxMenuItem", new JCheckBoxMenuItem("Visible"),
				Optional.of(JCheckBoxMenuItem.class)),
			Arguments.of("JRadioButtonMenuItem", new JRadioButtonMenuItem("Metal"),
				Optional.of(JRadioButtonMenuItem.class)),
			Arguments.of("JPopupMenu", new JPopupMenu(), Optional.of(JPopupMenu.class)),
			Arguments.of("menu element with a foreign component", new StubMenuElement(new JPanel()),
				Optional.empty()),
			Arguments.of("menu element without a component", new StubMenuElement(null),
				Optional.empty()));
	}

	/**
	 * Parameterized test for {@link ParentMenuResolver#getRoot(JMenuItem)}
	 */
	@ParameterizedTest(name = "[{index}] the root of the {0} is {2}")
	@MethodSource("rootFixtures")
	void getRoot(String description, JMenuItem menuItem, String expectedName)
	{
		Optional<Container> root = ParentMenuResolver.getRoot(menuItem);

		assertEquals(expectedName, nameOf(root.orElse(null)), description);
	}

	static Stream<Arguments> rootFixtures()
	{
		Hierarchy hierarchy = new Hierarchy();
		return Stream
			.of(Arguments.of("item in a menu in a menu bar", hierarchy.exitMenuItem, "menuBar"),
				Arguments.of("menu in a menu bar", hierarchy.fileMenu, "menuBar"),
				Arguments.of("item in nested menus in a menu bar", hierarchy.nestedMenuItem,
					"nestedMenuBar"),
				Arguments.of("item in nested menus without a menu bar",
					hierarchy.orphanNestedMenuItem, "orphanTopMenu"),
				Arguments.of("menu in a tool bar", hierarchy.toolBarMenu, "toolBar"),
				Arguments.of("item in a menu in a tool bar", hierarchy.toolBarMenuItem, "toolBar"),
				Arguments.of("orphan item", hierarchy.orphanMenuItem, "orphanMenuItem"),
				Arguments.of("orphan menu", hierarchy.orphanMenu, "orphanMenu"), Arguments
					.of("item in a popup menu without an invoker", hierarchy.popupMenuItem, null),
				Arguments.of("menu in a plain panel", hierarchy.panelMenu, null));
	}

	/**
	 * Parameterized test for {@link ParentMenuResolver#getRootJMenu(JMenuItem)}
	 */
	@ParameterizedTest(name = "[{index}] the root menu of the {0} is {2}")
	@MethodSource("rootJMenuFixtures")
	void getRootJMenu(String description, JMenuItem menuItem, String expectedName)
	{
		Optional<Container> rootJMenu = ParentMenuResolver.getRootJMenu(menuItem);

		assertEquals(expectedName, nameOf(rootJMenu.orElse(null)), description);
	}

	static Stream<Arguments> rootJMenuFixtures()
	{
		Hierarchy hierarchy = new Hierarchy();
		return Stream
			.of(Arguments.of("item in a menu in a menu bar", hierarchy.exitMenuItem, "fileMenu"),
				Arguments.of("menu in a menu bar", hierarchy.fileMenu, "fileMenu"),
				Arguments.of("item in nested menus in a menu bar", hierarchy.nestedMenuItem,
					"nestedTopMenu"),
				Arguments.of("item in nested menus without a menu bar",
					hierarchy.orphanNestedMenuItem, "orphanTopMenu"),
				Arguments.of("menu in a tool bar", hierarchy.toolBarMenu, "toolBarMenu"),
				Arguments.of("item in a menu in a tool bar", hierarchy.toolBarMenuItem,
					"toolBarMenu"),
				Arguments.of("orphan item", hierarchy.orphanMenuItem, "orphanMenuItem"),
				Arguments.of("orphan menu", hierarchy.orphanMenu, "orphanMenu"), Arguments
					.of("item in a popup menu without an invoker", hierarchy.popupMenuItem, null),
				Arguments.of("menu in a plain panel", hierarchy.panelMenu, null));
	}

	/**
	 * Parameterized test for {@link ParentMenuResolver#getRootType(JMenuItem)}
	 */
	@ParameterizedTest(name = "[{index}] the root type of the {0} is {2}")
	@MethodSource("rootTypeFixtures")
	void getRootType(String description, JMenuItem menuItem, Optional<Class<?>> expected)
	{
		assertEquals(expected, ParentMenuResolver.getRootType(menuItem), description);
	}

	static Stream<Arguments> rootTypeFixtures()
	{
		Hierarchy hierarchy = new Hierarchy();
		return Stream.of(
			Arguments.of("item in a menu in a menu bar", hierarchy.exitMenuItem,
				Optional.of(JMenuBar.class)),
			Arguments.of("menu in a menu bar", hierarchy.fileMenu, Optional.of(JMenuBar.class)),
			Arguments.of("item in nested menus in a menu bar", hierarchy.nestedMenuItem,
				Optional.of(JMenuBar.class)),
			Arguments.of("sub menu in a menu bar", hierarchy.nestedSubMenu,
				Optional.of(JMenuBar.class)),
			Arguments.of("item in nested menus without a menu bar", hierarchy.orphanNestedMenuItem,
				Optional.of(JMenu.class)),
			Arguments.of("menu in a tool bar", hierarchy.toolBarMenu, Optional.of(JToolBar.class)),
			Arguments.of("item in a menu in a tool bar", hierarchy.toolBarMenuItem,
				Optional.of(JToolBar.class)),
			Arguments.of("orphan item", hierarchy.orphanMenuItem, Optional.of(JMenuItem.class)),
			Arguments.of("orphan menu", hierarchy.orphanMenu, Optional.of(JMenu.class)),
			Arguments.of("item in a popup menu without an invoker", hierarchy.popupMenuItem,
				Optional.empty()),
			Arguments.of("menu in a plain panel", hierarchy.panelMenu, Optional.empty()));
	}

	/**
	 * Parameterized test for {@link ParentMenuResolver#getParentType(JMenu)}
	 */
	@ParameterizedTest(name = "[{index}] the parent type of the {0} is {2}")
	@MethodSource("parentTypeFixtures")
	void getParentType(String description, JMenu menu, Optional<Class<?>> expected)
	{
		assertEquals(expected, ParentMenuResolver.getParentType(menu), description);
	}

	static Stream<Arguments> parentTypeFixtures()
	{
		Hierarchy hierarchy = new Hierarchy();
		return Stream.of(
			Arguments.of("menu in a menu bar", hierarchy.fileMenu, Optional.of(JMenuBar.class)),
			Arguments.of("top level menu in a menu bar", hierarchy.nestedTopMenu,
				Optional.of(JMenuBar.class)),
			Arguments.of("sub menu in a menu", hierarchy.nestedSubMenu, Optional.of(JMenu.class)),
			Arguments.of("sub menu in a menu without a menu bar", hierarchy.orphanSubMenu,
				Optional.of(JMenu.class)),
			Arguments.of("menu in a tool bar", hierarchy.toolBarMenu, Optional.of(JToolBar.class)),
			Arguments.of("orphan menu", hierarchy.orphanMenu, Optional.empty()),
			Arguments.of("menu in a popup menu without an invoker", hierarchy.popupSubMenu,
				Optional.empty()),
			Arguments.of("menu in a plain panel", hierarchy.panelMenu, Optional.empty()));
	}

	/**
	 * Parameterized test for {@link ParentMenuResolver#getParentMenu(JMenuItem)}
	 */
	@ParameterizedTest(name = "[{index}] the parent menu of the {0} is {2}")
	@MethodSource("parentMenuFixtures")
	void getParentMenu(String description, JMenuItem menuItem, String expectedName)
	{
		Optional<JMenu> parentMenu = ParentMenuResolver.getParentMenu(menuItem);

		assertEquals(expectedName, nameOf(parentMenu.orElse(null)), description);
	}

	static Stream<Arguments> parentMenuFixtures()
	{
		Hierarchy hierarchy = new Hierarchy();
		return Stream.of(
			Arguments.of("item in a menu in a menu bar", hierarchy.exitMenuItem, "fileMenu"),
			Arguments.of("menu in a menu bar", hierarchy.fileMenu, null),
			Arguments.of("item in nested menus in a menu bar", hierarchy.nestedMenuItem,
				"nestedSubMenu"),
			Arguments.of("sub menu in a menu", hierarchy.nestedSubMenu, "nestedTopMenu"),
			Arguments.of("item in a menu in a tool bar", hierarchy.toolBarMenuItem, "toolBarMenu"),
			Arguments.of("menu in a tool bar", hierarchy.toolBarMenu, null),
			Arguments.of("orphan item", hierarchy.orphanMenuItem, null),
			Arguments.of("item in a popup menu without an invoker", hierarchy.popupMenuItem, null));
	}

	/**
	 * Parameterized test for {@link ParentMenuResolver#getMenuAncestors(JMenuItem)}
	 */
	@ParameterizedTest(name = "[{index}] the menu ancestors of the {0} are {2}")
	@MethodSource("menuAncestorFixtures")
	void getMenuAncestors(String description, JMenuItem menuItem, List<String> expectedNames)
	{
		List<Container> ancestors = ParentMenuResolver.getMenuAncestors(menuItem);

		assertEquals(expectedNames,
			ancestors.stream().map(ParentMenuResolverParameterizedTest::nameOf).toList(),
			description);
	}

	static Stream<Arguments> menuAncestorFixtures()
	{
		Hierarchy hierarchy = new Hierarchy();
		return Stream.of(
			Arguments.of("item in a menu in a menu bar", hierarchy.exitMenuItem,
				List.of("fileMenu", "menuBar")),
			Arguments.of("menu in a menu bar", hierarchy.fileMenu, List.of("menuBar")),
			Arguments.of("item in nested menus in a menu bar", hierarchy.nestedMenuItem,
				List.of("nestedSubMenu", "nestedTopMenu", "nestedMenuBar")),
			Arguments.of("item in nested menus without a menu bar", hierarchy.orphanNestedMenuItem,
				List.of("orphanSubMenu", "orphanTopMenu")),
			Arguments.of("item in a menu in a tool bar", hierarchy.toolBarMenuItem,
				List.of("toolBarMenu", "toolBar")),
			Arguments.of("menu in a tool bar", hierarchy.toolBarMenu, List.of("toolBar")),
			Arguments.of("orphan item", hierarchy.orphanMenuItem, List.of()), Arguments
				.of("item in a popup menu without an invoker", hierarchy.popupMenuItem, List.of()),
			Arguments.of("menu in a plain panel", hierarchy.panelMenu, List.of()));
	}

	/**
	 * Parameterized test for the getAllMenuElements method of the class {@link ParentMenuResolver}
	 * over the matrix of the flags 'withoutPopupMenu' and 'recursive'
	 */
	@ParameterizedTest(name = "[{index}] getAllMenuElements({0}, withoutPopupMenu={1}, recursive={2}) returns {3} elements")
	@CsvSource({ "menuBar, false, true, 5", "menuBar, true, true, 3", "menuBar, false, false, 1",
			"menuBar, true, false, 1", "file, false, true, 4", "file, true, true, 2",
			"file, false, false, 1", "file, true, false, 0", "foo, false, true, 2",
			"foo, true, true, 1", "bar, false, true, 0", "bar, true, true, 0" })
	void getAllMenuElements(String fixture, boolean withoutPopupMenu, boolean recursive,
		int expectedSize)
	{
		List<MenuElement> menuElements = ParentMenuResolver.getAllMenuElements(resolve(fixture),
			withoutPopupMenu, recursive);

		assertEquals(expectedSize, menuElements.size());
		if (withoutPopupMenu)
		{
			assertTrue(
				menuElements.stream().noneMatch(menuElement -> menuElement instanceof JPopupMenu));
		}
	}

	/**
	 * Parameterized test for {@link ParentMenuResolver#getChildMenuElements(MenuElement)}
	 */
	@ParameterizedTest(name = "[{index}] getChildMenuElements({0}) returns {1} direct children")
	@CsvSource({ "menuBar, 1, File", "file, 1, Foo", "foo, 1, Bar", "bar, 0, ''" })
	void getChildMenuElements(String fixture, int expectedSize, String expectedText)
	{
		List<MenuElement> childMenuElements = ParentMenuResolver
			.getChildMenuElements(resolve(fixture));

		assertEquals(expectedSize, childMenuElements.size());
		if (expectedText.isEmpty())
		{
			assertTrue(childMenuElements.isEmpty());
			return;
		}
		assertEquals(expectedText, ((JMenuItem)childMenuElements.get(0).getComponent()).getText());
	}

	/**
	 * Parameterized test for {@link ParentMenuResolver#getMenuElementType(MenuElement)} with a null
	 * argument
	 */
	@ParameterizedTest(name = "[{index}] getMenuElementType(null) throws a NullPointerException")
	@NullSource
	void getMenuElementTypeWithNull(MenuElement menuElement)
	{
		NullPointerException exception = assertThrows(NullPointerException.class,
			() -> ParentMenuResolver.getMenuElementType(menuElement));
		assertNotNull(exception.getMessage());
		assertTrue(exception.getMessage().contains("menuElement is marked non-null but is null"));
	}

	/**
	 * Parameterized test for the null checks of the methods of the class {@link ParentMenuResolver}
	 */
	@ParameterizedTest(name = "[{index}] {0} throws a NullPointerException")
	@MethodSource("nullArguments")
	void methodsWithNullArgument(String description, String expectedMessagePart,
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
		return Stream.of(
			Arguments.of("getChildMenuElements(null)", "parent is marked non-null but is null",
				(Executable)() -> ParentMenuResolver.getChildMenuElements(null)),
			Arguments.of("getAllMenuElements(null)", "parent is marked non-null but is null",
				(Executable)() -> ParentMenuResolver.getAllMenuElements(null)),
			Arguments.of("getAllMenuElements(null, true)", "parent is marked non-null but is null",
				(Executable)() -> ParentMenuResolver.getAllMenuElements(null, true)),
			Arguments.of("getAllMenuElements(null, true, true)",
				"parent is marked non-null but is null",
				(Executable)() -> ParentMenuResolver.getAllMenuElements(null, true, true)),
			Arguments.of("getMenuAncestors(null)", "menu is marked non-null but is null",
				(Executable)() -> ParentMenuResolver.getMenuAncestors(null)),
			Arguments.of("getRootJMenu(null)", "menu is marked non-null but is null",
				(Executable)() -> ParentMenuResolver.getRootJMenu(null)),
			Arguments.of("getRoot(null)", "menu is marked non-null but is null",
				(Executable)() -> ParentMenuResolver.getRoot(null)),
			Arguments.of("getRootType((JMenuItem)null)", "menu is marked non-null but is null",
				(Executable)() -> ParentMenuResolver.getRootType((JMenuItem)null)),
			Arguments.of("getRootType((JMenu)null)", "menu is marked non-null but is null",
				(Executable)() -> ParentMenuResolver.getRootType((JMenu)null)),
			Arguments.of("getParentType(null)", "menu is marked non-null but is null",
				(Executable)() -> ParentMenuResolver.getParentType(null)),
			Arguments.of("getParentMenu(null)", "menu is marked non-null but is null",
				(Executable)() -> ParentMenuResolver.getParentMenu(null)));
	}

	private MenuElement resolve(final String fixture)
	{
		return switch (fixture)
		{
			case "menuBar" -> menuBar;
			case "file" -> file;
			case "foo" -> foo;
			case "bar" -> bar;
			default -> throw new IllegalArgumentException("Unknown fixture " + fixture);
		};
	}

	private static String nameOf(final Container container)
	{
		return container == null ? null : container.getName();
	}

	/**
	 * The hierarchy fixtures of the tests, every container gets a name so the expected values stay
	 * readable in the display names of the tests
	 */
	private static final class Hierarchy
	{
		final JMenuBar menuBar = named(new JMenuBar(), "menuBar");
		final JMenu fileMenu = named(new JMenu("File"), "fileMenu");
		final JMenuItem exitMenuItem = named(new JMenuItem("Exit"), "exitMenuItem");
		final JMenuBar nestedMenuBar = named(new JMenuBar(), "nestedMenuBar");
		final JMenu nestedTopMenu = named(new JMenu("Edit"), "nestedTopMenu");
		final JMenu nestedSubMenu = named(new JMenu("Convert"), "nestedSubMenu");
		final JMenuItem nestedMenuItem = named(new JMenuItem("To upper case"), "nestedMenuItem");
		final JMenu orphanTopMenu = named(new JMenu("New"), "orphanTopMenu");
		final JMenu orphanSubMenu = named(new JMenu("Project"), "orphanSubMenu");
		final JMenuItem orphanNestedMenuItem = named(new JMenuItem("Gradle"),
			"orphanNestedMenuItem");
		final JToolBar toolBar = named(new JToolBar(), "toolBar");
		final JMenu toolBarMenu = named(new JMenu("Tools"), "toolBarMenu");
		final JMenuItem toolBarMenuItem = named(new JMenuItem("Options"), "toolBarMenuItem");
		final JMenuItem orphanMenuItem = named(new JMenuItem("Orphan"), "orphanMenuItem");
		final JMenu orphanMenu = named(new JMenu("Orphan"), "orphanMenu");
		final JPopupMenu popupMenu = named(new JPopupMenu(), "popupMenu");
		final JMenuItem popupMenuItem = named(new JMenuItem("Copy"), "popupMenuItem");
		final JMenu popupSubMenu = named(new JMenu("More"), "popupSubMenu");
		final JPanel panel = named(new JPanel(), "panel");
		final JMenu panelMenu = named(new JMenu("Panel"), "panelMenu");

		Hierarchy()
		{
			menuBar.add(fileMenu);
			fileMenu.add(exitMenuItem);

			nestedMenuBar.add(nestedTopMenu);
			nestedTopMenu.add(nestedSubMenu);
			nestedSubMenu.add(nestedMenuItem);

			orphanTopMenu.add(orphanSubMenu);
			orphanSubMenu.add(orphanNestedMenuItem);

			toolBar.add(toolBarMenu);
			toolBarMenu.add(toolBarMenuItem);

			popupMenu.add(popupMenuItem);
			popupMenu.add(popupSubMenu);

			panel.add(panelMenu);
		}

		private static <T extends Component> T named(final T component, final String name)
		{
			component.setName(name);
			return component;
		}
	}

	/**
	 * A {@link MenuElement} implementation that provides a component that is no menu component
	 */
	private record StubMenuElement(Component component) implements MenuElement {

		@Override
		public void processMouseEvent(MouseEvent event, MenuElement[] path,
			MenuSelectionManager manager)
		{
		}

		@Override
		public void processKeyEvent(KeyEvent event, MenuElement[] path,
			MenuSelectionManager manager)
		{
		}

		@Override
		public void menuSelectionChanged(boolean isIncluded)
		{
		}

		@Override
		public MenuElement[] getSubElements()
		{
			return new MenuElement[0];
		}

		@Override
		public Component getComponent()
		{
			return component;
		}
	}
}
