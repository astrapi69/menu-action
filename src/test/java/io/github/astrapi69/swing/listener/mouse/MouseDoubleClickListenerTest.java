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
package io.github.astrapi69.swing.listener.mouse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BooleanSupplier;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.Test;

/**
 * The unit test class for the class {@link MouseDoubleClickListener}
 */
class MouseDoubleClickListenerTest
{

	private static final int DELAY = 50;

	private final List<MouseEvent> singleClicks = new CopyOnWriteArrayList<>();
	private final List<MouseEvent> doubleClicks = new CopyOnWriteArrayList<>();
	private final JPanel component = new JPanel();

	static void await(final BooleanSupplier condition) throws InterruptedException
	{
		final long end = System.currentTimeMillis() + 5000;
		while (!condition.getAsBoolean() && System.currentTimeMillis() < end)
		{
			Thread.sleep(10);
		}
		assertTrue(condition.getAsBoolean());
	}

	private MouseDoubleClickListener newListener(final int delay)
	{
		return new MouseDoubleClickListener(delay)
		{
			@Override
			public void onSingleClick(final MouseEvent mouseEvent)
			{
				singleClicks.add(mouseEvent);
			}

			@Override
			public void onDoubleClick(final MouseEvent mouseEvent)
			{
				doubleClicks.add(mouseEvent);
			}
		};
	}

	private MouseEvent newClick(final int clickCount)
	{
		return new MouseEvent(component, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, 1,
			1, clickCount, false, MouseEvent.BUTTON1);
	}

	@Test
	void singleClickIsDeferredUntilTheMultiClickIntervalElapsed() throws Exception
	{
		final MouseDoubleClickListener listener = newListener(DELAY);
		final MouseEvent event = newClick(1);

		SwingUtilities.invokeAndWait(() -> {
			listener.mouseClicked(event);
			// the single click is deferred as long as a second click may follow
			assertTrue(singleClicks.isEmpty());
		});

		await(() -> !singleClicks.isEmpty());
		assertEquals(List.of(event), singleClicks);
		assertTrue(doubleClicks.isEmpty());
	}

	@Test
	void doubleClickCancelsThePendingSingleClick() throws Exception
	{
		final MouseDoubleClickListener listener = newListener(DELAY);
		final MouseEvent first = newClick(1);
		final MouseEvent second = newClick(2);

		SwingUtilities.invokeAndWait(() -> {
			listener.mouseClicked(first);
			listener.mouseClicked(second);
		});

		// the double click is executed at once with the last mouse event
		assertEquals(List.of(second), doubleClicks);
		Thread.sleep(DELAY * 4);
		assertTrue(singleClicks.isEmpty());
		assertEquals(1, doubleClicks.size());
	}

	@Test
	void moreThanTwoClicksAreIgnored() throws Exception
	{
		final MouseDoubleClickListener listener = newListener(DELAY);

		SwingUtilities.invokeAndWait(() -> listener.mouseClicked(newClick(3)));

		Thread.sleep(DELAY * 4);
		assertTrue(singleClicks.isEmpty());
		assertTrue(doubleClicks.isEmpty());
	}

	@Test
	void defaultConstructorWorksAlsoInHeadlessMode() throws Exception
	{
		final MouseDoubleClickListener listener = new MouseDoubleClickListener()
		{
			@Override
			public void onSingleClick(final MouseEvent mouseEvent)
			{
				singleClicks.add(mouseEvent);
			}

			@Override
			public void onDoubleClick(final MouseEvent mouseEvent)
			{
				doubleClicks.add(mouseEvent);
			}
		};
		final MouseEvent event = newClick(1);

		SwingUtilities.invokeAndWait(() -> listener.mouseClicked(event));

		await(() -> !singleClicks.isEmpty());
		assertEquals(List.of(event), singleClicks);
	}

	@Test
	void multiClickIntervalIsResolvedFromTheDesktopPropertyWithFallback()
	{
		final int interval = MouseDoubleClickListener.resolveMultiClickInterval();
		final Object desktopProperty = Toolkit.getDefaultToolkit()
			.getDesktopProperty("awt.multiClickInterval");
		final int expected = desktopProperty instanceof Integer
			? (Integer)desktopProperty
			: MouseDoubleClickListener.DEFAULT_MULTI_CLICK_INTERVAL;
		assertEquals(expected, interval);
		assertTrue(0 < interval);
	}
}
