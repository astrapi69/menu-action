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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.Test;

/**
 * The unit test class for the class {@link MouseTripleClickCounterListener}
 */
class MouseTripleClickCounterListenerTest
{

	private static final int DELAY = 50;

	private final JPanel component = new JPanel();

	private MouseEvent newClick(final int clickCount, final int button)
	{
		return new MouseEvent(component, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, 1,
			1, clickCount, false, button);
	}

	@Test
	void singleClickIsDeferredUntilTheMultiClickIntervalElapsed() throws Exception
	{
		final RecordingListener listener = new RecordingListener(DELAY);
		final MouseEvent event = newClick(1, MouseEvent.BUTTON1);

		SwingUtilities.invokeAndWait(() -> {
			listener.mouseClicked(event);
			// the single click is deferred as long as another click may follow
			assertTrue(listener.singleClicks.isEmpty());
			assertEquals(MouseClickedType.SINGLE, listener.mouseClickedType);
		});

		MouseDoubleClickListenerTest.await(() -> !listener.singleClicks.isEmpty());
		assertEquals(List.of(event), listener.singleClicks);
		assertEquals(MouseButton.LEFT, listener.mouseButton);
		assertTrue(listener.doubleClicks.isEmpty());
		assertTrue(listener.tripleClicks.isEmpty());
	}

	@Test
	void doubleClickCancelsThePendingSingleClick() throws Exception
	{
		final RecordingListener listener = new RecordingListener(DELAY);
		final MouseEvent first = newClick(1, MouseEvent.BUTTON1);
		final MouseEvent second = newClick(2, MouseEvent.BUTTON1);

		SwingUtilities.invokeAndWait(() -> {
			listener.mouseClicked(first);
			listener.mouseClicked(second);
		});

		// the double click is executed at once with the last mouse event
		assertEquals(List.of(second), listener.doubleClicks);
		assertEquals(MouseClickedType.DOUBLE, listener.mouseClickedType);
		Thread.sleep(DELAY * 4);
		assertTrue(listener.singleClicks.isEmpty());
		assertTrue(listener.tripleClicks.isEmpty());
	}

	@Test
	void tripleClickIsExecutedWithTheThirdMouseEvent() throws Exception
	{
		final RecordingListener listener = new RecordingListener(DELAY);
		final MouseEvent first = newClick(1, MouseEvent.BUTTON1);
		final MouseEvent second = newClick(2, MouseEvent.BUTTON1);
		final MouseEvent third = newClick(3, MouseEvent.BUTTON1);

		SwingUtilities.invokeAndWait(() -> {
			listener.mouseClicked(first);
			listener.mouseClicked(second);
			listener.mouseClicked(third);
		});

		// the double click fires before the triple click, the triple click gets the third event
		assertEquals(List.of(second), listener.doubleClicks);
		assertEquals(List.of(third), listener.tripleClicks);
		assertEquals(MouseClickedType.TRIPLE, listener.mouseClickedType);
		Thread.sleep(DELAY * 4);
		assertTrue(listener.singleClicks.isEmpty());

		// every further click of the same click sequence counts as triple click as well
		final MouseEvent fourth = newClick(4, MouseEvent.BUTTON1);
		SwingUtilities.invokeAndWait(() -> listener.mouseClicked(fourth));
		assertEquals(List.of(third, fourth), listener.tripleClicks);
	}

	@Test
	void singleClickStopsTheTimerSoItDoesNotFireASecondTime() throws Exception
	{
		final RecordingListener listener = new RecordingListener(DELAY);
		final MouseEvent event = newClick(1, MouseEvent.BUTTON1);

		SwingUtilities.invokeAndWait(() -> listener.mouseClicked(event));
		MouseDoubleClickListenerTest.await(() -> !listener.singleClicks.isEmpty());

		// javax.swing.Timer repeats by default; without actionPerformed calling timer.stop() the
		// same single click would fire again after another delay
		assertFalse(listener.timer.isRunning());
		Thread.sleep(DELAY * 4);
		assertEquals(List.of(event), listener.singleClicks);
	}

	@Test
	void tripleClickStopsAPendingTimerFromAPriorSingleClick() throws Exception
	{
		final RecordingListener listener = new RecordingListener(DELAY);
		final MouseEvent first = newClick(1, MouseEvent.BUTTON1);
		final MouseEvent third = newClick(3, MouseEvent.BUTTON1);

		SwingUtilities.invokeAndWait(() -> {
			listener.mouseClicked(first);
			assertTrue(listener.timer.isRunning());
			// a third click event arrives directly, without an intervening double click
			listener.mouseClicked(third);
		});

		assertFalse(listener.timer.isRunning());
		assertEquals(List.of(third), listener.tripleClicks);
		Thread.sleep(DELAY * 4);
		assertTrue(listener.singleClicks.isEmpty());
	}

	@Test
	void mouseButtonIsResolvedFromTheMouseEvent() throws Exception
	{
		final RecordingListener listener = new RecordingListener(DELAY);
		assertNull(listener.mouseButton);

		// click count three avoids that a timer is started
		SwingUtilities.invokeAndWait(() -> {
			listener.mouseClicked(newClick(3, MouseEvent.BUTTON1));
			assertEquals(MouseButton.LEFT, listener.mouseButton);
			listener.mouseClicked(newClick(3, MouseEvent.BUTTON2));
			assertEquals(MouseButton.MIDDLE, listener.mouseButton);
			listener.mouseClicked(newClick(3, MouseEvent.BUTTON3));
			assertEquals(MouseButton.RIGHT, listener.mouseButton);
			// an event without a button keeps the last resolved button
			listener.mouseClicked(newClick(3, MouseEvent.NOBUTTON));
			assertEquals(MouseButton.RIGHT, listener.mouseButton);
		});
	}

	@Test
	void defaultConstructorWorksAlsoInHeadlessMode()
	{
		final RecordingListener listener = new RecordingListener();
		assertEquals(MouseDoubleClickListener.resolveMultiClickInterval(), listener.delay);
		assertEquals(listener.delay, listener.timer.getDelay());
	}

	private static class RecordingListener extends MouseTripleClickCounterListener
	{

		final List<MouseEvent> singleClicks = new CopyOnWriteArrayList<>();
		final List<MouseEvent> doubleClicks = new CopyOnWriteArrayList<>();
		final List<MouseEvent> tripleClicks = new CopyOnWriteArrayList<>();

		RecordingListener()
		{
		}

		RecordingListener(final int delay)
		{
			super(delay);
		}

		@Override
		public void singleClick(final MouseEvent mouseEvent)
		{
			singleClicks.add(mouseEvent);
		}

		@Override
		public void doubleClick(final MouseEvent mouseEvent)
		{
			doubleClicks.add(mouseEvent);
		}

		@Override
		public void tripleClick(final MouseEvent mouseEvent)
		{
			tripleClicks.add(mouseEvent);
		}
	}
}
