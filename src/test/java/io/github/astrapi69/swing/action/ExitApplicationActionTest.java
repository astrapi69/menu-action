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

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JButton;

import org.junit.jupiter.api.Test;

import io.github.astrapi69.swing.menu.enumeration.BaseMenuId;

/**
 * The unit test class for the class {@link ExitApplicationAction}
 */
class ExitApplicationActionTest
{

	/**
	 * Test subclass that records the exit callback instead of terminating the JVM
	 */
	private static class RecordingExitApplicationAction extends ExitApplicationAction
	{
		private static final long serialVersionUID = 1L;
		final List<ActionEvent> exits = new ArrayList<>();

		RecordingExitApplicationAction()
		{
			super();
		}

		RecordingExitApplicationAction(final String name)
		{
			super(name);
		}

		@Override
		protected void onExit(final ActionEvent actionEvent)
		{
			exits.add(actionEvent);
		}
	}

	@Test
	void defaultConstructorUsesExitMenuId()
	{
		ExitApplicationAction action = new RecordingExitApplicationAction();

		assertEquals(BaseMenuId.EXIT.propertiesKey(), action.getValue(Action.NAME));
		assertEquals("global.menu.file.exit", action.getValue(Action.NAME));
	}

	@Test
	void namedConstructor()
	{
		ExitApplicationAction action = new RecordingExitApplicationAction("Quit");

		assertEquals("Quit", action.getValue(Action.NAME));
	}

	@Test
	void nullNameIsAllowed()
	{
		ExitApplicationAction action = new RecordingExitApplicationAction(null);

		assertNull(action.getValue(Action.NAME));
	}

	@Test
	void actionPerformedDelegatesToOnExit()
	{
		RecordingExitApplicationAction action = new RecordingExitApplicationAction();
		ActionEvent event = new ActionEvent(new JButton(), ActionEvent.ACTION_PERFORMED, "exit");

		action.actionPerformed(event);

		assertEquals(1, action.exits.size());
		assertSame(event, action.exits.get(0));

		action.actionPerformed(event);
		assertEquals(2, action.exits.size());
	}

	@Test
	void actionPerformedPassesNullEventThrough()
	{
		RecordingExitApplicationAction action = new RecordingExitApplicationAction();

		action.actionPerformed(null);

		assertEquals(1, action.exits.size());
		assertNull(action.exits.get(0));
	}
}
