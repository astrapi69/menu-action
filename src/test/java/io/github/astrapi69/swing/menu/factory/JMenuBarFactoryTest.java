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
package io.github.astrapi69.swing.menu.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import org.junit.jupiter.api.Test;

/**
 * The unit test class for the class {@link JMenuBarFactory}
 */
class JMenuBarFactoryTest
{

	@Test
	void newJMenuBar()
	{
		JMenuBar menuBar = JMenuBarFactory.newJMenuBar();

		assertNotNull(menuBar);
		assertEquals(0, menuBar.getMenuCount());
		assertNull(menuBar.getName());
		// the menu bar is a plain usable menu bar
		JMenu menu = JMenuFactory.newJMenu("File");
		assertSame(menu, menuBar.add(menu));
		assertEquals(1, menuBar.getMenuCount());
		assertSame(menu, menuBar.getMenu(0));
		// every call creates a new instance
		assertNotSame(menuBar, JMenuBarFactory.newJMenuBar());
		assertEquals(0, JMenuBarFactory.newJMenuBar().getMenuCount());
	}
}
