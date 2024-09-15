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
package io.github.astrapi69.swing.action;

import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.event.ActionEvent;
import java.io.Serial;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

import io.github.astrapi69.swing.menu.enumeration.BaseMenuId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * The class {@link ToggleFullScreenAction}
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ToggleFullScreenAction extends AbstractAction
{

	/** The Constant serialVersionUID. */
	@Serial
	private static final long serialVersionUID = 1L;

	/** The frame. */
	@Getter
	@Setter
	Frame frame;

	/**
	 * Instantiates a new {@link ToggleFullScreenAction} object
	 */
	public ToggleFullScreenAction()
	{
		super(BaseMenuId.TOGGLE_FULLSCREEN.propertiesKey());
	}

	/**
	 * Instantiates a new {@link ToggleFullScreenAction} object.
	 *
	 * @param name
	 *            the name
	 * @param frame
	 *            the frame
	 */
	public ToggleFullScreenAction(final String name, Frame frame)
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
		onToggleFullScreen();
	}

	/**
	 * Callback method to interact on toggle to full screen
	 */
	protected void onToggleFullScreen()
	{
		ToggleFullScreenAction.toggleFullScreen(frame);
	}

	/**
	 * Toggle to full screen and back
	 * 
	 * @param frame
	 *            the {@link Frame} object
	 */
	public static void toggleFullScreen(final @NonNull Frame frame)
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
