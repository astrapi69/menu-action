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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JButton;

import org.junit.jupiter.api.Test;

/**
 * The unit test class for the class {@link OpenBrowserAction}
 */
class OpenBrowserActionTest
{

	/**
	 * Test subclass that records the urls instead of opening a real browser
	 */
	private static class RecordingOpenBrowserAction extends OpenBrowserAction
	{
		private static final long serialVersionUID = 1L;
		final List<String> displayed = new ArrayList<>();

		RecordingOpenBrowserAction()
		{
			super();
		}

		RecordingOpenBrowserAction(final String name, final String url)
		{
			super(name, url);
		}

		@Override
		protected void onDisplayURLonStandardBrowser(final String url)
		{
			displayed.add(url);
		}
	}

	private static ActionEvent newEvent()
	{
		return new ActionEvent(new JButton(), ActionEvent.ACTION_PERFORMED, "browse");
	}

	@Test
	void defaultConstructor()
	{
		RecordingOpenBrowserAction action = new RecordingOpenBrowserAction();

		assertEquals("", action.getValue(Action.NAME));
		assertNull(action.getUrl());
	}

	@Test
	void constructorWithNameAndUrl()
	{
		RecordingOpenBrowserAction action = new RecordingOpenBrowserAction("Homepage",
			"https://github.com/astrapi69/menu-action");

		assertEquals("Homepage", action.getValue(Action.NAME));
		assertEquals("https://github.com/astrapi69/menu-action", action.getUrl());
	}

	@Test
	void actionPerformedPassesUrlToCallback()
	{
		RecordingOpenBrowserAction action = new RecordingOpenBrowserAction("Homepage",
			"https://example.com/");

		action.actionPerformed(newEvent());

		assertEquals(List.of("https://example.com/"), action.displayed);
	}

	@Test
	void setUrlChangesTheUrlPassedToCallback()
	{
		RecordingOpenBrowserAction action = new RecordingOpenBrowserAction("Homepage",
			"https://example.com/");
		action.setUrl("https://example.org/");
		assertEquals("https://example.org/", action.getUrl());

		action.actionPerformed(newEvent());
		action.actionPerformed(null);

		assertEquals(List.of("https://example.org/", "https://example.org/"), action.displayed);
	}

	@Test
	void actionPerformedWithoutUrlPassesNull()
	{
		RecordingOpenBrowserAction action = new RecordingOpenBrowserAction();

		action.actionPerformed(newEvent());

		assertEquals(1, action.displayed.size());
		assertTrue(action.displayed.contains(null));
	}
}
