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

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;

import org.junit.jupiter.api.Test;

import io.github.astrapi69.swing.menu.enumeration.BaseMenuId;

/**
 * The unit test class for the class {@link ShowInfoDialogAction}
 */
class ShowInfoDialogActionTest
{

	/**
	 * Test subclass that records the created dialogs and never shows a real dialog
	 */
	private static class RecordingShowInfoDialogAction extends ShowInfoDialogAction
	{
		private static final long serialVersionUID = 1L;
		Frame newJDialogOwner;
		String newJDialogTitle;
		boolean dialogVisible;

		RecordingShowInfoDialogAction()
		{
			super();
		}

		RecordingShowInfoDialogAction(final String name, final Frame owner, final String title)
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

	@Test
	void defaultConstructorUsesHelpInfoMenuId()
	{
		RecordingShowInfoDialogAction action = new RecordingShowInfoDialogAction();

		assertEquals(BaseMenuId.HELP_INFO.propertiesKey(), action.getValue(Action.NAME));
		assertEquals("global.menu.help.info", action.getValue(Action.NAME));
		assertNull(action.getOwner());
		assertNull(action.getTitle());
	}

	@Test
	void constructorRequiresOwner()
	{
		assertThrows(NullPointerException.class,
			() -> new RecordingShowInfoDialogAction("Info", null, "Title"));
	}

	@Test
	void actionPerformedCreatesAndShowsTheDialog()
	{
		assumeFalse(GraphicsEnvironment.isHeadless());
		Frame owner = new Frame("owner");
		try
		{
			RecordingShowInfoDialogAction action = new RecordingShowInfoDialogAction("Info", owner,
				"About");
			assertEquals("Info", action.getValue(Action.NAME));
			assertSame(owner, action.getOwner());
			assertEquals("About", action.getTitle());

			action.actionPerformed(
				new ActionEvent(new JButton(), ActionEvent.ACTION_PERFORMED, "info"));

			assertSame(owner, action.newJDialogOwner);
			assertEquals("About", action.newJDialogTitle);
			assertTrue(action.dialogVisible);
		}
		finally
		{
			owner.dispose();
		}
	}
}
