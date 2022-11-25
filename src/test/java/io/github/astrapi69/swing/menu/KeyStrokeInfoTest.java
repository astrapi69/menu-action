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

import org.junit.jupiter.api.Test;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KeyStrokeInfoTest
{
	@Test
	public void test()
	{
		KeyStroke actual;
		KeyStroke expected;
		KeyStrokeInfo keyStrokeInfo;

		keyStrokeInfo = KeyStrokeInfo.builder().keystrokeAsString("alt pressed F4").build();
		actual = keyStrokeInfo.toKeyStroke();
		expected = KeyStroke.getKeyStroke("alt pressed F4");
		assertEquals(actual, expected);

		keyStrokeInfo = KeyStrokeInfo.toKeyStrokeInfo(KeyStroke.getKeyStroke("alt pressed F5"));

		actual = keyStrokeInfo.toKeyStroke();
		expected = KeyStroke.getKeyStroke("alt pressed F5");
		assertEquals(actual, expected);


		keyStrokeInfo.set(KeyStroke.getKeyStroke("ctrl pressed D"));

		actual = keyStrokeInfo.toKeyStroke();
		expected = KeyStroke.getKeyStroke("ctrl pressed D");
		assertEquals(actual, expected);

	}
}
