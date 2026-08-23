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
package io.github.astrapi69.swing.menu.popup.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPopupMenu;

import org.junit.jupiter.api.Test;

/**
 * The unit test class for the class {@link PopupListener}
 */
class PopupListenerTest
{

	/**
	 * A {@link JPopupMenu} that records the show requests instead of displaying itself, so the test
	 * does not need a showing component
	 */
	private static class RecordingPopupMenu extends JPopupMenu
	{
		private static final long serialVersionUID = 1L;
		final List<String> shown = new ArrayList<>();

		@Override
		public void show(final Component invoker, final int x, final int y)
		{
			shown.add(invoker.getName() + "@" + x + "," + y);
		}
	}

	private static MouseEvent event(final Component component, final int id,
		final boolean popupTrigger)
	{
		return new MouseEvent(component, id, System.currentTimeMillis(),
			popupTrigger ? InputEvent.BUTTON3_DOWN_MASK : InputEvent.BUTTON1_DOWN_MASK, 12, 34, 1,
			popupTrigger, popupTrigger ? MouseEvent.BUTTON3 : MouseEvent.BUTTON1);
	}

	@Test
	void mousePressedWithPopupTrigger()
	{
		RecordingPopupMenu popupMenu = new RecordingPopupMenu();
		PopupListener listener = new PopupListener(popupMenu);
		JLabel label = new JLabel();
		label.setName("tree");

		listener.mousePressed(event(label, MouseEvent.MOUSE_PRESSED, true));

		assertEquals(List.of("tree@12,34"), popupMenu.shown);
	}

	@Test
	void mouseReleasedWithPopupTrigger()
	{
		RecordingPopupMenu popupMenu = new RecordingPopupMenu();
		PopupListener listener = new PopupListener(popupMenu);
		JLabel label = new JLabel();
		label.setName("table");

		listener.mouseReleased(event(label, MouseEvent.MOUSE_RELEASED, true));

		assertEquals(List.of("table@12,34"), popupMenu.shown);
	}

	@Test
	void nonPopupTriggerDoesNothing()
	{
		RecordingPopupMenu popupMenu = new RecordingPopupMenu();
		PopupListener listener = new PopupListener(popupMenu);
		JLabel label = new JLabel();

		listener.mousePressed(event(label, MouseEvent.MOUSE_PRESSED, false));
		listener.mouseReleased(event(label, MouseEvent.MOUSE_RELEASED, false));
		listener.mouseClicked(event(label, MouseEvent.MOUSE_CLICKED, true));
		listener.mouseEntered(event(label, MouseEvent.MOUSE_ENTERED, true));
		listener.mouseExited(event(label, MouseEvent.MOUSE_EXITED, true));

		assertTrue(popupMenu.shown.isEmpty());
	}

	@Test
	void onShowPopupIsTheHookForPressedAndReleased()
	{
		List<MouseEvent> received = new ArrayList<>();
		PopupListener listener = new PopupListener(new JPopupMenu())
		{
			@Override
			protected void onShowPopup(final MouseEvent e)
			{
				received.add(e);
			}
		};
		JLabel label = new JLabel();
		MouseEvent pressed = event(label, MouseEvent.MOUSE_PRESSED, false);
		MouseEvent released = event(label, MouseEvent.MOUSE_RELEASED, true);

		listener.mousePressed(pressed);
		listener.mouseReleased(released);

		assertEquals(2, received.size());
		assertSame(pressed, received.get(0));
		assertSame(released, received.get(1));
	}

	@Test
	void registeredOnComponent()
	{
		RecordingPopupMenu popupMenu = new RecordingPopupMenu();
		JLabel label = new JLabel();
		label.setName("list");
		label.addMouseListener(new PopupListener(popupMenu));

		// dispatch the synthetic events through the component
		label.dispatchEvent(event(label, MouseEvent.MOUSE_PRESSED, false));
		assertTrue(popupMenu.shown.isEmpty());
		label.dispatchEvent(event(label, MouseEvent.MOUSE_PRESSED, true));
		assertEquals(List.of("list@12,34"), popupMenu.shown);
	}
}
