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
package io.github.astrapi69.swing.menu;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import org.junit.jupiter.api.Test;

class KeyStrokeExtensionsTest
{

	@Test
	void getKeyStroke()
	{
		String actual;
		String expected;
		KeyStroke keyStroke;
		KeyStroke otherKeyStroke;
		// new scenario ...
		expected = "ctrl pressed Z";
		keyStroke = KeyStrokeExtensions.getKeyStroke(expected);
		actual = keyStroke.toString();
		assertEquals(expected, actual);
		// new scenario ...
		expected = "ctrl pressed D";
		keyStroke = KeyStrokeExtensions.getKeyStroke(expected);
		actual = keyStroke.toString();
		assertEquals(expected, actual);
		// new scenario ...
		expected = "ctrl pressed S";
		keyStroke = KeyStrokeExtensions.getKeyStroke(expected);
		actual = keyStroke.toString();
		assertEquals(expected, actual);
		// new scenario ...
		expected = "ctrl pressed K";
		keyStroke = KeyStrokeExtensions.getKeyStroke(expected);
		actual = keyStroke.toString();
		assertEquals(expected, actual);
		// new scenario ...
		expected = "ctrl pressed P";
		keyStroke = KeyStrokeExtensions.getKeyStroke(expected);
		actual = keyStroke.toString();
		assertEquals(expected, actual);
		// new scenario ...
		expected = "ctrl pressed Y";
		keyStroke = KeyStrokeExtensions.getKeyStroke(expected);
		actual = keyStroke.toString();
		assertEquals(expected, actual);
		// new scenario ...
		expected = "ctrl pressed N";
		keyStroke = KeyStrokeExtensions.getKeyStroke(expected);
		actual = keyStroke.toString();
		assertEquals(expected, actual);
		// new scenario ...
		expected = "ctrl pressed C";
		keyStroke = KeyStrokeExtensions.getKeyStroke(expected);
		actual = keyStroke.toString();
		assertEquals(expected, actual);
		// new scenario ...
		expected = "ctrl pressed I";
		keyStroke = KeyStrokeExtensions.getKeyStroke(expected);
		actual = keyStroke.toString();
		assertEquals(expected, actual);
		// new scenario ...
		expected = "alt pressed F11";
		keyStroke = KeyStrokeExtensions.getKeyStroke(expected);
		actual = keyStroke.toString();
		assertEquals(expected, actual);
		// new scenario ...
		expected = "alt pressed F4";
		keyStroke = KeyStrokeExtensions.getKeyStroke(expected);
		actual = keyStroke.toString();
		assertEquals(expected, actual);
		otherKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK);
		assertEquals(keyStroke, otherKeyStroke);
		// new scenario ...
		expected = "alt pressed L";
		keyStroke = KeyStrokeExtensions.getKeyStroke(expected);
		actual = keyStroke.toString();
		assertEquals(expected, actual);
		// new scenario ...
		expected = "ctrl alt pressed D";
		keyStroke = KeyStrokeExtensions.getKeyStroke(expected);
		actual = keyStroke.toString();
		assertEquals(expected, actual);
		otherKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_D,
			InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK);
		assertEquals(keyStroke, otherKeyStroke);
	}
}
