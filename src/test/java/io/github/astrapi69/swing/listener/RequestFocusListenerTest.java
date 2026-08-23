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
package io.github.astrapi69.swing.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.WindowFocusListener;

import javax.swing.JTextField;

import org.junit.jupiter.api.Test;

/**
 * The unit test class for the class {@link RequestFocusListener}
 */
class RequestFocusListenerTest
{

	/**
	 * A {@link JTextField} that records the focus requests instead of relying on a display
	 */
	private static class FocusRecordingTextField extends JTextField
	{
		int requestFocusCalls;
		int requestFocusInWindowCalls;

		@Override
		public void requestFocus()
		{
			requestFocusCalls++;
			super.requestFocus();
		}

		@Override
		public boolean requestFocusInWindow()
		{
			requestFocusInWindowCalls++;
			return super.requestFocusInWindow();
		}
	}

	@Test
	void constructorRejectsNullComponent()
	{
		assertThrows(NullPointerException.class, () -> new RequestFocusListener(null));
	}

	@Test
	void windowGainedFocusEnablesAndRequestsFocus()
	{
		FocusRecordingTextField textField = new FocusRecordingTextField();
		textField.setFocusable(false);
		textField.setRequestFocusEnabled(false);
		RequestFocusListener listener = new RequestFocusListener(textField);
		assertSame(textField, listener.getComponent());

		// the event is not used by the listener, so a synthetic null event is sufficient
		listener.windowGainedFocus(null);

		assertTrue(textField.isFocusable());
		assertTrue(textField.isRequestFocusEnabled());
		assertEquals(1, textField.requestFocusCalls);
		assertEquals(1, textField.requestFocusInWindowCalls);

		listener.windowGainedFocus(null);
		assertEquals(2, textField.requestFocusCalls);
		assertEquals(2, textField.requestFocusInWindowCalls);
	}

	@Test
	void windowLostFocusDoesNothing()
	{
		FocusRecordingTextField textField = new FocusRecordingTextField();
		textField.setFocusable(false);
		textField.setRequestFocusEnabled(false);
		WindowFocusListener listener = new RequestFocusListener(textField);

		listener.windowLostFocus(null);

		assertFalse(textField.isFocusable());
		assertFalse(textField.isRequestFocusEnabled());
		assertEquals(0, textField.requestFocusCalls);
		assertEquals(0, textField.requestFocusInWindowCalls);
	}

	@Test
	void equalsHashCodeAndToString()
	{
		JTextField textField = new JTextField();
		RequestFocusListener first = new RequestFocusListener(textField);
		RequestFocusListener second = new RequestFocusListener(textField);
		RequestFocusListener other = new RequestFocusListener(new JTextField());

		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, other);
		assertNotEquals(first, null);
		assertTrue(first.toString().startsWith("RequestFocusListener(component="));
	}
}
