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
package io.github.astrapi69.swing.menu.model.transform;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.swing.KeyStroke;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import io.github.astrapi69.swing.menu.model.KeyStrokeInfo;

/**
 * The class {@link KeyStrokeInfoExtensionsParameterizedTest} provides parameterized tests for the
 * class {@link KeyStrokeInfoExtensions}
 */
public class KeyStrokeInfoExtensionsParameterizedTest
{

	/**
	 * Parameterized test for {@link KeyStrokeInfoExtensions#toKeyStroke(KeyStrokeInfo)}
	 */
	@ParameterizedTest
	@CsvFileSource(resources = "/keyStrokeInfos.csv", numLinesToSkip = 1)
	void testToKeyStrokeWithParameters(String keystrokeString, int keyCode, int modifiers)
	{
		KeyStrokeInfo keyStrokeInfo = new KeyStrokeInfo();
		keyStrokeInfo.setKeystrokeAsString(keystrokeString);
		keyStrokeInfo.setKeyCode(keyCode);
		keyStrokeInfo.setModifiers(modifiers);
		KeyStroke keyStroke = KeyStrokeInfoExtensions.toKeyStroke(keyStrokeInfo);
		assertNotNull(keyStroke);
	}
}
