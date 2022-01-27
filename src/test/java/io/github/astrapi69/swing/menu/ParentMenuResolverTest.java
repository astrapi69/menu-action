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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Container;
import java.util.Optional;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;

import org.junit.jupiter.api.Test;

class ParentMenuResolverTest
{

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
		Optional<Class> parentType;

		// new scenario parent is JMenuBar object ...
		menuBar = MenuFactory.newJMenuBar();
		menu = MenuFactory.newJMenu("File", 'f');
		menuBar.add(menu);

		root = ParentMenuResolver.getRootJMenu(menu);

		assertTrue(root.isPresent());
		actual = root.get();
		expected = menu;
		assertEquals(expected, actual);

		foo = MenuFactory.newJMenu("Foo");
		menu.add(foo);

		root = ParentMenuResolver.getRootJMenu(foo);

		assertTrue(root.isPresent());
		actual = root.get();
		expected = menu;
		assertEquals(expected, actual);

		// new scenario parent is JMenu object ...
		menu = MenuFactory.newJMenu("File", 'f');
		foo = MenuFactory.newJMenu("Foo");
		menu.add(foo);

		root = ParentMenuResolver.getRootJMenu(foo);
		assertTrue(root.isPresent());
		actual = root.get();
		expected = menu;
		assertEquals(expected, actual);

		// new scenario JMenu object has no parent ...
		menu = MenuFactory.newJMenu("File", 'f');

		root = ParentMenuResolver.getRootJMenu(menu);
		assertTrue(root.isPresent());
		actual = root.get();
		expected = menu;
		assertEquals(expected, actual);
		// new scenario parent is JToolBar object ...
		toolBar = MenuFactory.newJToolBar();
		toolBar.add(menu);

		root = ParentMenuResolver.getRootJMenu(menu);
		assertTrue(root.isPresent());
		actual = root.get();
		expected = menu;
		assertEquals(expected, actual);
		foo = MenuFactory.newJMenu("Foo");
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
		Optional<Class> parentType;

		// new scenario parent is JMenuBar object ...
		menuBar = MenuFactory.newJMenuBar();
		menu = MenuFactory.newJMenu("File", 'f');
		menuBar.add(menu);

		root = ParentMenuResolver.getRoot(menu);

		assertTrue(root.isPresent());
		actual = root.get();
		expected = menuBar;
		assertEquals(expected, actual);

		foo = MenuFactory.newJMenu("Foo");
		menu.add(foo);

		root = ParentMenuResolver.getRoot(foo);

		assertTrue(root.isPresent());
		actual = root.get();
		expected = menuBar;
		assertEquals(expected, actual);

		// new scenario parent is JMenu object ...
		menu = MenuFactory.newJMenu("File", 'f');
		foo = MenuFactory.newJMenu("Foo");
		menu.add(foo);

		root = ParentMenuResolver.getRoot(foo);
		assertTrue(root.isPresent());
		actual = root.get();
		expected = menu;
		assertEquals(expected, actual);

		// new scenario JMenu object has no parent ...
		menu = MenuFactory.newJMenu("File", 'f');

		root = ParentMenuResolver.getRoot(menu);
		assertTrue(root.isPresent());
		actual = root.get();
		expected = menu;
		assertEquals(expected, actual);
		// new scenario parent is JToolBar object ...
		toolBar = MenuFactory.newJToolBar();
		toolBar.add(menu);

		root = ParentMenuResolver.getRoot(menu);
		assertTrue(root.isPresent());
		actual = root.get();
		expected = toolBar;
		assertEquals(expected, actual);
		foo = MenuFactory.newJMenu("Foo");
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
	void getParentFromJMenu()
	{
		JMenuBar menuBar;
		JMenu menu;
		JMenu foo;
		JMenu actual;
		JMenu expected;
		Optional<JMenu> parent;
		Optional<Class> parentType;
		// new scenario menu has no JMenu as parent so empty Optional is expected ...
		menuBar = MenuFactory.newJMenuBar();
		menu = MenuFactory.newJMenu("File", 'f');
		menuBar.add(menu);

		parentType = ParentMenuResolver.getParentType(menu);

		assertTrue(parentType.isPresent());
		assertTrue(parentType.get().equals(JMenuBar.class));

		parent = ParentMenuResolver.getParentMenu(menu);
		assertFalse(parent.isPresent());
		// new scenario test if foo menu has as parent menu
		foo = MenuFactory.newJMenu("Foo");
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
