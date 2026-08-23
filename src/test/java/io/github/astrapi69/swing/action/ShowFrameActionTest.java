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

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JButton;

import org.junit.jupiter.api.Test;

/**
 * The unit test class for the class {@link ShowFrameAction}
 */
class ShowFrameActionTest
{

	/**
	 * Test subclass that records the callback instead of showing a real frame
	 */
	private static class RecordingShowFrameAction extends ShowFrameAction
	{
		private static final long serialVersionUID = 1L;
		final List<ActionEvent> shown = new ArrayList<>();

		RecordingShowFrameAction()
		{
			super();
		}

		RecordingShowFrameAction(final String name)
		{
			super(name);
		}

		RecordingShowFrameAction(final String name, final String title)
		{
			super(name, title);
		}

		@Override
		protected void onShowFrame(final ActionEvent e)
		{
			shown.add(e);
		}
	}

	@Test
	void defaultConstructor()
	{
		RecordingShowFrameAction action = new RecordingShowFrameAction();

		assertEquals("", action.getValue(Action.NAME));
		assertEquals("", action.getTitle());
	}

	@Test
	void nameConstructorUsesTheNameAsTitle()
	{
		RecordingShowFrameAction action = new RecordingShowFrameAction("Editor");

		assertEquals("Editor", action.getValue(Action.NAME));
		assertEquals("Editor", action.getTitle());
	}

	@Test
	void nameConstructorRequiresName()
	{
		assertThrows(NullPointerException.class, () -> new RecordingShowFrameAction((String)null));
	}

	@Test
	void nameAndTitleConstructor()
	{
		RecordingShowFrameAction action = new RecordingShowFrameAction("Editor", "The editor");

		assertEquals("Editor", action.getValue(Action.NAME));
		assertEquals("The editor", action.getTitle());
	}

	@Test
	void nameAndTitleConstructorRequiresTitleButAllowsNullName()
	{
		assertThrows(NullPointerException.class,
			() -> new RecordingShowFrameAction("Editor", null));

		RecordingShowFrameAction action = new RecordingShowFrameAction(null, "The editor");
		assertNull(action.getValue(Action.NAME));
		assertEquals("The editor", action.getTitle());
	}

	@Test
	void actionPerformedDelegatesToOnShowFrame()
	{
		RecordingShowFrameAction action = new RecordingShowFrameAction("Editor");
		ActionEvent event = new ActionEvent(new JButton(), ActionEvent.ACTION_PERFORMED, "show");

		action.actionPerformed(event);

		assertEquals(1, action.shown.size());
		assertSame(event, action.shown.get(0));
	}
}
