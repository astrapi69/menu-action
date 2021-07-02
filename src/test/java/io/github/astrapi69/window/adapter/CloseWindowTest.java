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
package io.github.astrapi69.window.adapter;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.awt.*;
import java.awt.event.WindowEvent;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The unit test class for the class CloseWindow.
 *
 * @version 1.0
 * @author Asterios Raptis
 */
public class CloseWindowTest
{
	boolean actual;

	/** The dispose window. */
	DisposeWindow disposeWindow = null;

	/** The frame. */
	Frame frame = null;

	@BeforeEach
	protected void setUp()
	{
	}

	@AfterEach
	protected void tearDown()
	{
		this.frame.removeAll();
		this.frame = null;
		this.disposeWindow = null;
	}

	/**
	 * Test method for.
	 *
	 * {@link io.github.astrapi69.window.adapter.CloseWindow#windowClosed(java.awt.event.WindowEvent)}
	 */
	@Test
	public void testWindowClosedWindowEvent()
	{
		this.disposeWindow = new DisposeWindow();
		this.frame = new Frame("testWindowClosedWindowEvent");
		this.frame.addWindowListener(this.disposeWindow);
		final Color color = new Color(0, 180, 0);
		final DrawMessage message = new DrawMessage("testWindowClosedWindowEvent", color);
		this.frame.add("Center", message);
		this.frame.pack();
		this.frame.setSize(new Dimension(400, 160));

		this.frame.setVisible(true);
		this.frame.repaint();
		try
		{
			Thread.sleep(1000);
		}
		catch (final InterruptedException e1)
		{
			e1.printStackTrace();
		}
		final WindowEvent we = new WindowEvent(this.frame, this.frame.getY());

		this.disposeWindow.windowClosed(we);
		actual = this.frame.isVisible();
		assertFalse(actual);

		actual = this.frame.isShowing();
		assertFalse(actual);

		actual = this.frame.isActive();
		assertFalse(actual);
	}

	/**
	 * Test method for
	 * {@link io.github.astrapi69.window.adapter.CloseWindow#windowClosing(java.awt.event.WindowEvent)}
	 */
	@Test
	public void testWindowClosingWindowEvent()
	{
		this.disposeWindow = new DisposeWindow();
		this.frame = new Frame("testWindowClosedWindowEvent");
		this.frame.addWindowListener(this.disposeWindow);
		final Color color = new Color(0, 180, 0);
		final DrawMessage message = new DrawMessage("testWindowClosingWindowEvent", color);
		this.frame.add("Center", message);
		this.frame.pack();
		this.frame.setSize(new Dimension(400, 60));
		this.frame.setVisible(true);
		try
		{
			Thread.sleep(1000);
		}
		catch (final InterruptedException e1)
		{
			e1.printStackTrace();
		}
		final WindowEvent we = new WindowEvent(this.frame, this.frame.getX());

		this.disposeWindow.windowClosing(we);
		actual = this.frame.isVisible();
		assertFalse(actual);

		actual = this.frame.isShowing();
		assertFalse(actual);

	}

}
