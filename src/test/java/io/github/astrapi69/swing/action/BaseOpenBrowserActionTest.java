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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JButton;

import org.junit.jupiter.api.Test;

/**
 * The unit test class for the class {@link BaseOpenBrowserAction}
 */
class BaseOpenBrowserActionTest
{

	/**
	 * An url with a whitespace that is rejected by {@link java.net.URI} before any browser starts
	 */
	private static final String INVALID_URL = "http://exa mple.com";

	private static ActionEvent newEvent()
	{
		return new ActionEvent(new JButton(), ActionEvent.ACTION_PERFORMED, "browse");
	}

	@Test
	void defaultConstructor()
	{
		BaseOpenBrowserAction action = new BaseOpenBrowserAction();

		assertEquals("", action.getValue(Action.NAME));
		assertNull(action.getUrl());
	}

	@Test
	void constructorWithNameAndUrl()
	{
		BaseOpenBrowserAction action = new BaseOpenBrowserAction("Homepage",
			"https://github.com/astrapi69/menu-action");

		assertEquals("Homepage", action.getValue(Action.NAME));
		assertEquals("https://github.com/astrapi69/menu-action", action.getUrl());
	}

	@Test
	void of()
	{
		BaseOpenBrowserAction action = BaseOpenBrowserAction.of("Homepage",
			"https://github.com/astrapi69/menu-action");

		assertNotNull(action);
		assertInstanceOf(OpenBrowserAction.class, action);
		assertEquals("Homepage", action.getValue(Action.NAME));
		assertEquals("https://github.com/astrapi69/menu-action", action.getUrl());
		assertEquals(BaseOpenBrowserAction.class, action.getClass());
	}

	@Test
	void actionPerformedDelegatesToCallback()
	{
		List<String> displayed = new ArrayList<>();
		BaseOpenBrowserAction action = new BaseOpenBrowserAction("Homepage", "https://example.com/")
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onDisplayURLonStandardBrowser(final String url)
			{
				displayed.add(url);
			}
		};

		action.actionPerformed(newEvent());

		assertEquals(List.of("https://example.com/"), displayed);
	}

	@Test
	void actionPerformedWithInvalidUrlDoesNotThrow()
	{
		// the invalid url is rejected before any browser is started
		BaseOpenBrowserAction action = BaseOpenBrowserAction.of("Homepage", INVALID_URL);

		assertDoesNotThrow(() -> action.actionPerformed(newEvent()));
	}

	@Test
	void actionPerformedWithoutUrlThrows()
	{
		BaseOpenBrowserAction action = new BaseOpenBrowserAction();

		assertThrows(NullPointerException.class, () -> action.actionPerformed(newEvent()));
	}
}
