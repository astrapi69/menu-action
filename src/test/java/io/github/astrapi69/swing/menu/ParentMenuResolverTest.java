/**
 * The MIT License
 *
 * Copyright (C) 2021 Asterios Raptis
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Container;
import java.util.List;
import java.util.Optional;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToolBar;
import javax.swing.MenuElement;

import org.junit.jupiter.api.Test;

import io.github.astrapi69.swing.menu.factory.JMenuBarFactory;
import io.github.astrapi69.swing.menu.factory.JMenuFactory;
import io.github.astrapi69.swing.menu.factory.JPopupMenuFactory;
import io.github.astrapi69.swing.menu.factory.JToolBarFactory;
import io.github.astrapi69.swing.menu.model.MenuInfo;
import io.github.astrapi69.swing.menu.model.MenuItemInfo;

class ParentMenuResolverTest
{

	@Test
	void getMenuElementType()
	{
		Optional<Class<?>> actual;
		Optional<Class<?>> expected;
		JMenuBar menuBar;
		JMenu menu;
		JMenuItem menuItem;
		JPopupMenu popupMenu;
		JRadioButtonMenuItem radioButtonMenuItem;
		JCheckBoxMenuItem checkBoxMenuItem;
		// new scenario ...
		checkBoxMenuItem = MenuItemInfo.builder().menuInfo(MenuInfo.builder().build()).build()
			.toJCheckBoxMenuItem();
		actual = ParentMenuResolver.getMenuElementType(checkBoxMenuItem);
		expected = Optional.of(JCheckBoxMenuItem.class);
		assertEquals(expected, actual);
		// new scenario ...
		radioButtonMenuItem = MenuItemInfo.builder().menuInfo(MenuInfo.builder().build()).build()
			.toJRadioButtonMenuItem();
		actual = ParentMenuResolver.getMenuElementType(radioButtonMenuItem);
		expected = Optional.of(JRadioButtonMenuItem.class);
		assertEquals(expected, actual);
		// new scenario ...
		menu = MenuItemInfo.builder().menuInfo(MenuInfo.builder().build()).build().toJMenu();
		actual = ParentMenuResolver.getMenuElementType(menu);
		expected = Optional.of(JMenu.class);
		assertEquals(expected, actual);
		// new scenario ...
		menuBar = JMenuBarFactory.newJMenuBar();
		actual = ParentMenuResolver.getMenuElementType(menuBar);
		expected = Optional.of(JMenuBar.class);
		assertEquals(expected, actual);
		// new scenario ...
		menuItem = MenuItemInfo.builder().menuInfo(MenuInfo.builder().build()).build()
			.toJMenuItem();
		actual = ParentMenuResolver.getMenuElementType(menuItem);
		expected = Optional.of(JMenuItem.class);
		assertEquals(expected, actual);
		// new scenario ...
		popupMenu = JPopupMenuFactory.newJPopupMenu();
		actual = ParentMenuResolver.getMenuElementType(popupMenu);
		expected = Optional.of(JPopupMenu.class);
		assertEquals(expected, actual);
	}

	@Test
	void getRootJMenu()
	{
		JToolBar toolBar;
		JMenuBar menuBar;
		JMenu menu;
		JMenu foo;
		Container actual;
		Container expected;
		Optional<Container> root;
		Optional<Class<?>> parentType;

		// new scenario parent is JMenuBar object ...
		menuBar = JMenuBarFactory.newJMenuBar();
		menu = JMenuFactory.newJMenu("File", 'f');
		menuBar.add(menu);

		root = ParentMenuResolver.getRootJMenu(menu);

		assertTrue(root.isPresent());
		actual = root.get();
		expected = menu;
		assertEquals(expected, actual);

		foo = JMenuFactory.newJMenu("Foo");
		menu.add(foo);

		root = ParentMenuResolver.getRootJMenu(foo);

		assertTrue(root.isPresent());
		actual = root.get();
		expected = menu;
		assertEquals(expected, actual);

		// new scenario parent is JMenu object ...
		menu = JMenuFactory.newJMenu("File", 'f');
		foo = JMenuFactory.newJMenu("Foo");
		menu.add(foo);

		root = ParentMenuResolver.getRootJMenu(foo);
		assertTrue(root.isPresent());
		actual = root.get();
		expected = menu;
		assertEquals(expected, actual);

		// new scenario JMenu object has no parent ...
		menu = JMenuFactory.newJMenu("File", 'f');

		root = ParentMenuResolver.getRootJMenu(menu);
		assertTrue(root.isPresent());
		actual = root.get();
		expected = menu;
		assertEquals(expected, actual);
		// new scenario parent is JToolBar object ...
		toolBar = JToolBarFactory.newJToolBar();
		toolBar.add(menu);

		root = ParentMenuResolver.getRootJMenu(menu);
		assertTrue(root.isPresent());
		actual = root.get();
		expected = menu;
		assertEquals(expected, actual);
		foo = JMenuFactory.newJMenu("Foo");
		menu.add(foo);

		root = ParentMenuResolver.getRootJMenu(foo);
		assertTrue(root.isPresent());
		actual = root.get();
		expected = menu;
		assertEquals(expected, actual);

		parentType = ParentMenuResolver.getParentType(menu);

		assertTrue(parentType.isPresent());
		assertTrue(parentType.get().equals(JToolBar.class));
	}

	@Test
	void getRoot()
	{
		JToolBar toolBar;
		JMenuBar menuBar;
		JMenu menu;
		JMenu foo;
		Container actual;
		Container expected;
		Optional<Container> root;
		Optional<Class<?>> parentType;

		// new scenario parent is JMenuBar object ...
		menuBar = JMenuBarFactory.newJMenuBar();
		menu = JMenuFactory.newJMenu("File", 'f');
		menuBar.add(menu);

		root = ParentMenuResolver.getRoot(menu);

		assertTrue(root.isPresent());
		actual = root.get();
		expected = menuBar;
		assertEquals(expected, actual);

		foo = JMenuFactory.newJMenu("Foo");
		menu.add(foo);

		root = ParentMenuResolver.getRoot(foo);

		assertTrue(root.isPresent());
		actual = root.get();
		expected = menuBar;
		assertEquals(expected, actual);

		// new scenario parent is JMenu object ...
		menu = JMenuFactory.newJMenu("File", 'f');
		foo = JMenuFactory.newJMenu("Foo");
		menu.add(foo);

		root = ParentMenuResolver.getRoot(foo);
		assertTrue(root.isPresent());
		actual = root.get();
		expected = menu;
		assertEquals(expected, actual);

		// new scenario JMenu object has no parent ...
		menu = JMenuFactory.newJMenu("File", 'f');

		root = ParentMenuResolver.getRoot(menu);
		assertTrue(root.isPresent());
		actual = root.get();
		expected = menu;
		assertEquals(expected, actual);
		// new scenario parent is JToolBar object ...
		toolBar = JToolBarFactory.newJToolBar();
		toolBar.add(menu);

		root = ParentMenuResolver.getRoot(menu);
		assertTrue(root.isPresent());
		actual = root.get();
		expected = toolBar;
		assertEquals(expected, actual);
		foo = JMenuFactory.newJMenu("Foo");
		menu.add(foo);

		root = ParentMenuResolver.getRoot(foo);
		assertTrue(root.isPresent());
		actual = root.get();
		expected = toolBar;
		assertEquals(expected, actual);

		parentType = ParentMenuResolver.getParentType(menu);

		assertTrue(parentType.isPresent());
		assertTrue(parentType.get().equals(JToolBar.class));
	}

	@Test
	void getAllMenuElements()
	{
		JMenuBar menuBar;
		JMenu menu;
		JMenu foo;
		JMenu bar;
		List<MenuElement> allMenuElements;

		// new scenario menu has no JMenu as parent so empty Optional is expected ...
		menuBar = JMenuBarFactory.newJMenuBar();
		menu = JMenuFactory.newJMenu("File", 'f');
		menuBar.add(menu);
		foo = JMenuFactory.newJMenu("Foo");
		menu.add(foo);
		bar = JMenuFactory.newJMenu("bar");
		foo.add(bar);
		allMenuElements = ParentMenuResolver.getAllMenuElements(menuBar, true);
		assertNotNull(allMenuElements);
		assertEquals(allMenuElements.size(), 3);
		allMenuElements = ParentMenuResolver.getAllMenuElements(menuBar, false);
		assertNotNull(allMenuElements);
		assertEquals(allMenuElements.size(), 5);
	}

	@Test
	void getParentFromJMenu()
	{
		JMenuBar menuBar;
		JMenu menu;
		JMenu foo;
		JMenu actual;
		JMenu expected;
		Optional<JMenu> parent;
		Optional<Class<?>> parentType;
		// new scenario menu has no JMenu as parent so empty Optional is expected ...
		menuBar = JMenuBarFactory.newJMenuBar();
		menu = JMenuFactory.newJMenu("File", 'f');
		menuBar.add(menu);

		parentType = ParentMenuResolver.getParentType(menu);

		assertTrue(parentType.isPresent());
		assertTrue(parentType.get().equals(JMenuBar.class));

		parent = ParentMenuResolver.getParentMenu(menu);
		assertFalse(parent.isPresent());
		// new scenario test if foo menu has as parent menu
		foo = JMenuFactory.newJMenu("Foo");
		menu.add(foo);
		parent = ParentMenuResolver.getParentMenu(foo);
		assertTrue(parent.isPresent());
		actual = parent.get();
		expected = menu;
		assertEquals(expected, actual);

		parentType = ParentMenuResolver.getParentType(foo);

		assertTrue(parentType.isPresent());
		assertTrue(parentType.get().equals(JMenu.class));
	}

}
