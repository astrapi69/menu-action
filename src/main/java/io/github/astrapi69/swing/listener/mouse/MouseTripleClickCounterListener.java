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
package io.github.astrapi69.swing.listener.mouse;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Timer;

/**
 * The listener class {@link MouseTripleClickCounterListener} for listen for double and triple click
 */
public class MouseTripleClickCounterListener extends MouseAdapter implements ActionListener
{

	public static final String DESKTOP_PROPERTY_AWT_MULTI_CLICK_INTERVAL_KEY = "awt.multiClickInterval";
	MouseEvent lastEvent;
	Timer timer;
	int delay;

	protected MouseClickedType mouseClickedType = MouseClickedType.SINGLE;
	protected MouseButton mouseButton;

	public MouseTripleClickCounterListener()
	{
		this((Integer)Toolkit.getDefaultToolkit()
			.getDesktopProperty(DESKTOP_PROPERTY_AWT_MULTI_CLICK_INTERVAL_KEY));
	}

	public MouseTripleClickCounterListener(int delay)
	{
		this.delay = delay;
		this.timer = new Timer(delay, this);
	}

	public void mouseClicked(MouseEvent mouseEvent)
	{
		setMouseButton(mouseEvent);

		if (mouseEvent.getClickCount() > 3)
		{
			timer.stop();
			mouseClickedType = MouseClickedType.TRIPLE;
			tripleClick(lastEvent);
		}
		lastEvent = mouseEvent;
		if (mouseEvent.getClickCount() > 2)
			return;

		if (timer.isRunning())
		{
			timer.stop();
			mouseClickedType = MouseClickedType.DOUBLE;
			doubleClick(lastEvent);
		}
		else
		{
			mouseClickedType = MouseClickedType.SINGLE;
			timer.restart();
		}
	}

	private void setMouseButton(MouseEvent mouseEvent)
	{
		if (mouseEvent.getButton() == MouseEvent.BUTTON1)
		{
			mouseButton = MouseButton.LEFT;
		}
		if (mouseEvent.getButton() == MouseEvent.BUTTON2)
		{
			mouseButton = MouseButton.MIDDLE;
		}
		if (mouseEvent.getButton() == MouseEvent.BUTTON3)
		{
			mouseButton = MouseButton.LEFT;
		}
	}

	public void actionPerformed(ActionEvent e)
	{
		timer.stop();
		singleClick(lastEvent);
	}

	public void singleClick(MouseEvent mouseEvent)
	{
	}

	public void doubleClick(MouseEvent mouseEvent)
	{
	}

	public void tripleClick(MouseEvent mouseEvent)
	{
	}
}
