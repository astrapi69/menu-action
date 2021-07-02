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
package io.github.astrapi69.swing.actions;

import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.*;

/**
 * The class {@link ToggleFullScreenAction}
 */
public class ToggleFullScreenAction extends AbstractAction
{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The frame. */
	final JFrame frame;

	/**
	 * Instantiates a new {@link ToggleFullScreenAction} object.
	 *
	 * @param name
	 *            the name
	 * @param frame
	 *            the frame
	 */
	public ToggleFullScreenAction(final String name, JFrame frame)
	{
		super(name);
		this.frame = frame;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void actionPerformed(final ActionEvent actionEvent)
	{
		onToggleFullScreen(actionEvent);
	}

	/**
	 * Callback method to interact on show the dialog
	 *
	 * @param actionEvent
	 *            the action event
	 */
	protected void onToggleFullScreen(final ActionEvent actionEvent)
	{
		GraphicsDevice device = frame.getGraphicsConfiguration().getDevice();
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		if (frame.equals(device.getFullScreenWindow()))
		{
			device.setFullScreenWindow(null);
		}
		else
		{
			frame.setVisible(true);
			device.setFullScreenWindow(frame);
		}
	}

}