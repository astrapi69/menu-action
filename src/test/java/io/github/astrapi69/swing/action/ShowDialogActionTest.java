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
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;

import org.junit.jupiter.api.Test;

/**
 * The unit test class for the class {@link ShowDialogAction}
 */
class ShowDialogActionTest
{

	/**
	 * Test subclass that records the created dialogs and never shows a real dialog
	 */
	private static class RecordingShowDialogAction extends ShowDialogAction
	{
		private static final long serialVersionUID = 1L;
		Frame newJDialogOwner;
		String newJDialogTitle;
		boolean dialogVisible;

		RecordingShowDialogAction(final String name)
		{
			super(name);
		}

		RecordingShowDialogAction(final String name, final Frame owner, final String title)
		{
			super(name, owner, title);
		}

		@Override
		protected JDialog newJDialog(final Frame owner, final String title)
		{
			newJDialogOwner = owner;
			newJDialogTitle = title;
			return new JDialog(owner, title)
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void setVisible(final boolean visible)
				{
					// record instead of showing a real dialog
					dialogVisible = visible;
				}
			};
		}
	}

	private static ActionEvent newEvent()
	{
		return new ActionEvent(new JButton(), ActionEvent.ACTION_PERFORMED, "show");
	}

	@Test
	void nameConstructor()
	{
		RecordingShowDialogAction action = new RecordingShowDialogAction("Show");

		assertEquals("Show", action.getValue(Action.NAME));
		assertNull(action.getOwner());
		assertNull(action.getTitle());
	}

	@Test
	void constructorRequiresOwner()
	{
		assertThrows(NullPointerException.class,
			() -> new RecordingShowDialogAction("Show", null, "Title"));
	}

	@Test
	void setters()
	{
		RecordingShowDialogAction action = new RecordingShowDialogAction("Show");
		action.setTitle("New title");

		assertEquals("New title", action.getTitle());
	}

	@Test
	void actionPerformedDelegatesToOnShowDialog()
	{
		List<ActionEvent> shown = new ArrayList<>();
		ShowDialogAction action = new ShowDialogAction("Show")
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onShowDialog(final ActionEvent actionEvent)
			{
				shown.add(actionEvent);
			}

			@Override
			protected JDialog newJDialog(final Frame owner, final String title)
			{
				throw new AssertionError("must not be called");
			}
		};
		ActionEvent event = newEvent();

		action.actionPerformed(event);

		assertEquals(List.of(event), shown);
	}

	@Test
	void onShowDialogCreatesAndShowsTheDialog()
	{
		assumeFalse(GraphicsEnvironment.isHeadless());
		Frame owner = new Frame("owner");
		try
		{
			RecordingShowDialogAction action = new RecordingShowDialogAction("Show", owner,
				"Info dialog");
			assertSame(owner, action.getOwner());
			assertEquals("Info dialog", action.getTitle());

			action.actionPerformed(newEvent());

			assertSame(owner, action.newJDialogOwner);
			assertEquals("Info dialog", action.newJDialogTitle);
			assertTrue(action.dialogVisible);

			assertThrows(NullPointerException.class,
				() -> new RecordingShowDialogAction("Show", owner, null));
		}
		finally
		{
			owner.dispose();
		}
	}
}
