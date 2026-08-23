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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import org.junit.jupiter.api.Test;

/**
 * The unit test class for the class {@link JToolBarFactory}
 */
class JToolBarFactoryTest
{

	@Test
	void newJToolBar()
	{
		JToolBar toolBar = JToolBarFactory.newJToolBar();

		assertNotNull(toolBar);
		assertEquals(0, toolBar.getComponentCount());
		assertEquals(SwingConstants.HORIZONTAL, toolBar.getOrientation());
		assertTrue(toolBar.isFloatable());
		assertNull(toolBar.getName());
		// the tool bar is a plain usable tool bar
		JButton button = toolBar.add(new javax.swing.AbstractAction("New")
		{
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e)
			{
			}
		});
		assertEquals(1, toolBar.getComponentCount());
		assertInstanceOf(JButton.class, toolBar.getComponent(0));
		assertEquals("New", button.getAction().getValue(javax.swing.Action.NAME));
		// every call creates a new instance
		assertNotSame(toolBar, JToolBarFactory.newJToolBar());
		assertEquals(0, JToolBarFactory.newJToolBar().getComponentCount());
	}
}
