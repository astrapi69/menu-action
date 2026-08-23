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
package io.github.astrapi69.swing.action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JButton;

import org.junit.jupiter.api.Test;

import io.github.astrapi69.swing.menu.enumeration.BaseMenuId;

/**
 * The unit test class for the class {@link ToggleFullScreenAction}
 */
class ToggleFullScreenActionTest
{

	private static ActionEvent newEvent()
	{
		return new ActionEvent(new JButton(), ActionEvent.ACTION_PERFORMED, "toggle");
	}

	@Test
	void defaultConstructorUsesToggleFullscreenMenuId()
	{
		ToggleFullScreenAction action = new ToggleFullScreenAction();

		assertEquals(BaseMenuId.TOGGLE_FULLSCREEN.propertiesKey(), action.getValue(Action.NAME));
		assertEquals("global.menu.file.toggle.fullscreen", action.getValue(Action.NAME));
		assertNull(action.getFrame());
	}

	@Test
	void toggleFullScreenRequiresFrame()
	{
		assertThrows(NullPointerException.class,
			() -> ToggleFullScreenAction.toggleFullScreen(null));
	}

	@Test
	void actionPerformedWithoutFrameThrows()
	{
		ToggleFullScreenAction action = new ToggleFullScreenAction();

		assertThrows(NullPointerException.class, () -> action.actionPerformed(newEvent()));
	}

	@Test
	void actionPerformedDelegatesToOnToggleFullScreen()
	{
		final int[] toggled = { 0 };
		ToggleFullScreenAction action = new ToggleFullScreenAction("Toggle", null)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onToggleFullScreen()
			{
				toggled[0]++;
			}
		};
		assertEquals("Toggle", action.getValue(Action.NAME));

		action.actionPerformed(newEvent());

		assertEquals(1, toggled[0]);
	}

	@Test
	void toggleFullScreenTogglesTheFullScreenWindow()
	{
		assumeFalse(GraphicsEnvironment.isHeadless());
		Frame frame = new Frame("fullscreen");
		GraphicsDevice device = null;
		try
		{
			ToggleFullScreenAction action = new ToggleFullScreenAction("Toggle", frame);
			assertSame(frame, action.getFrame());
			device = frame.getGraphicsConfiguration().getDevice();

			action.actionPerformed(newEvent());

			assertEquals(Frame.MAXIMIZED_BOTH, frame.getExtendedState() & Frame.MAXIMIZED_BOTH);
			assertSame(frame, device.getFullScreenWindow());
			assertTrue(frame.isVisible());

			// the second toggle leaves the full screen mode
			ToggleFullScreenAction.toggleFullScreen(frame);

			assertNull(device.getFullScreenWindow());

			Frame otherFrame = new Frame("other");
			ToggleFullScreenAction otherAction = new ToggleFullScreenAction();
			otherAction.setFrame(otherFrame);
			assertSame(otherFrame, otherAction.getFrame());
			otherFrame.dispose();
		}
		finally
		{
			if (device != null)
			{
				device.setFullScreenWindow(null);
			}
			frame.dispose();
		}
	}
}
