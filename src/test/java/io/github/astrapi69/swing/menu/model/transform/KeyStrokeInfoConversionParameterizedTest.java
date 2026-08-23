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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Stream;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.astrapi69.swing.menu.KeyStrokeExtensions;
import io.github.astrapi69.swing.menu.model.KeyStrokeInfo;

/**
 * The parameterized unit test class for the class {@link KeyStrokeInfoExtensions}
 */
class KeyStrokeInfoConversionParameterizedTest
{

	/**
	 * Parameterized test for the method {@link KeyStrokeInfoExtensions#toKeyStroke(KeyStrokeInfo)}
	 * with all combinations of the keystroke string, the key code, the modifiers and the on key
	 * release flag
	 */
	@ParameterizedTest(name = "[{index}] keystroke={0}, keyCode={1}, modifiers={2}, onKeyRelease={3} -> {4}")
	@CsvSource(nullValues = "null", value = { "ctrl S, null, null, null, ctrl pressed S",
			"ctrl alt pressed H, null, null, null, ctrl alt pressed H",
			"typed a, null, null, null, typed a", "alt F4, 83, 128, false, alt pressed F4",
			"null, 83, 128, false, ctrl pressed S", "null, 83, 128, true, ctrl released S",
			"null, 83, null, null, pressed S", "null, 115, 0, false, pressed F4",
			"'', 83, 128, false, ctrl pressed S", "not a keystroke, 83, 128, false, ctrl pressed S",
			"not a keystroke, null, null, null, null", "null, null, 128, true, null",
			"'', null, null, null, null", "null, null, null, null, null" })
	void toKeyStroke(String keystrokeAsString, Integer keyCode, Integer modifiers,
		Boolean onKeyRelease, String expected)
	{
		KeyStrokeInfo keyStrokeInfo = KeyStrokeInfo.builder().keystrokeAsString(keystrokeAsString)
			.keyCode(keyCode).modifiers(modifiers).onKeyRelease(onKeyRelease).build();

		KeyStroke actual = KeyStrokeInfoExtensions.toKeyStroke(keyStrokeInfo);

		if (expected == null)
		{
			assertNull(actual);
			return;
		}
		assertNotNull(actual);
		assertEquals(expected, actual.toString());
		// the instance method delegates to the extension class
		assertEquals(actual, keyStrokeInfo.toKeyStroke());
	}

	/**
	 * Parameterized test for the round trip of a {@link KeyStroke} object to a
	 * {@link KeyStrokeInfo} object and back
	 */
	@ParameterizedTest(name = "[{index}] the round trip of the keystroke {0} keeps all values")
	@ValueSource(strings = { "ctrl S", "alt F4", "shift ctrl pressed X", "typed a", "released A",
			"meta pressed C" })
	void keyStrokeRoundTrip(String keystrokeAsString)
	{
		KeyStroke keyStroke = KeyStrokeExtensions.getKeyStroke(keystrokeAsString);
		assertNotNull(keyStroke);

		KeyStrokeInfo keyStrokeInfo = KeyStrokeExtensions.toKeyStrokeInfo(keyStroke);

		assertEquals(keyStroke.getKeyCode(), keyStrokeInfo.getKeyCode().intValue());
		assertEquals(keyStroke.getModifiers(), keyStrokeInfo.getModifiers().intValue());
		assertEquals(keyStroke.isOnKeyRelease(), keyStrokeInfo.getOnKeyRelease().booleanValue());
		// the keystroke string is stored in the normalized form of the KeyStroke object
		assertEquals(keyStroke.toString(), keyStrokeInfo.getKeystrokeAsString());
		assertEquals(keyStroke, keyStrokeInfo.toKeyStroke());
		assertEquals(keyStrokeInfo, KeyStrokeInfo.toKeyStrokeInfo(keyStroke));
		// the same values are set to an already existing object
		KeyStrokeInfo target = new KeyStrokeInfo();
		KeyStrokeInfoExtensions.set(target, keyStroke);
		assertEquals(keyStrokeInfo, target);
		assertEquals(keyStrokeInfo, new KeyStrokeInfo().set(keyStroke));
	}

	/**
	 * Parameterized test for the method {@link KeyStrokeExtensions#getKeyStroke(String)} with
	 * strings that are no keystrokes
	 */
	@ParameterizedTest(name = "[{index}] the string {0} is no keystroke")
	@NullSource
	@EmptySource
	@ValueSource(strings = { "   ", "ctrl", "not a keystroke", "pressed", "ctrl pressed" })
	void invalidKeyStrokeStringsResolveToNull(String keystrokeAsString)
	{
		assertNull(KeyStrokeExtensions.getKeyStroke(keystrokeAsString));
		// a key code is the fall back for an invalid keystroke string
		KeyStrokeInfo keyStrokeInfo = KeyStrokeInfo.builder().keystrokeAsString(keystrokeAsString)
			.keyCode(70).modifiers(0).build();
		assertEquals("pressed F", KeyStrokeInfoExtensions.toKeyStroke(keyStrokeInfo).toString());
	}

	/**
	 * Parameterized test for the methods
	 * {@link KeyStrokeInfoExtensions#getKeyStrokeInfos(JComponent)} and
	 * {@link KeyStrokeInfoExtensions#getKeyStrokeInfo(JComponent)}
	 */
	@ParameterizedTest(name = "[{index}] the keystroke {0} is found on the component")
	@ValueSource(strings = { "ctrl S", "alt F4", "shift ctrl pressed X", "meta pressed C" })
	void getKeyStrokeInfosFromComponents(String keystrokeAsString)
	{
		KeyStroke keyStroke = KeyStroke.getKeyStroke(keystrokeAsString);
		JPanel panel = new JPanel();
		assertTrue(KeyStrokeInfoExtensions.getKeyStrokeInfos(panel).isEmpty());
		assertNull(KeyStrokeInfoExtensions.getKeyStrokeInfo(panel));

		panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, "action");
		List<KeyStrokeInfo> keyStrokeInfos = KeyStrokeInfoExtensions.getKeyStrokeInfos(panel);
		assertEquals(1, keyStrokeInfos.size());
		assertEquals(keyStroke, keyStrokeInfos.get(0).toKeyStroke());
		assertEquals(keyStroke, KeyStrokeInfoExtensions.getKeyStrokeInfo(panel).toKeyStroke());

		JMenuItem menuItem = new JMenuItem("Item");
		menuItem.setAccelerator(keyStroke);
		// for a menu item the accelerator is the first entry
		assertEquals(keyStroke, KeyStrokeInfoExtensions.getKeyStrokeInfo(menuItem).toKeyStroke());
	}

	static Stream<Arguments> nullRejectingCalls()
	{
		KeyStroke keyStroke = KeyStroke.getKeyStroke("ctrl S");
		return Stream.of(
			Arguments.of("KeyStrokeInfoExtensions.toKeyStroke", "keyStrokeInfo",
				(Executable)() -> KeyStrokeInfoExtensions.toKeyStroke(null)),
			Arguments.of("KeyStrokeInfoExtensions.set", "keyStrokeInfo",
				(Executable)() -> KeyStrokeInfoExtensions.set(null, keyStroke)),
			Arguments.of("KeyStrokeInfoExtensions.set", "keyStroke",
				(Executable)() -> KeyStrokeInfoExtensions.set(new KeyStrokeInfo(), null)),
			Arguments.of("KeyStrokeInfoExtensions.getKeyStrokeInfos", "jComponent",
				(Executable)() -> KeyStrokeInfoExtensions.getKeyStrokeInfos(null)),
			Arguments.of("KeyStrokeInfoExtensions.getKeyStrokeInfo", "jComponent",
				(Executable)() -> KeyStrokeInfoExtensions.getKeyStrokeInfo(null)),
			Arguments.of("KeyStrokeExtensions.toKeyStrokeInfo", "keyStroke",
				(Executable)() -> KeyStrokeExtensions.toKeyStrokeInfo(null)),
			Arguments.of("KeyStrokeInfo.toKeyStrokeInfo", "keyStroke",
				(Executable)() -> KeyStrokeInfo.toKeyStrokeInfo(null)),
			Arguments.of("KeyStrokeInfo.set", "keyStroke",
				(Executable)() -> new KeyStrokeInfo().set(null)));
	}

	/**
	 * Parameterized test that all conversion methods reject null arguments
	 */
	@ParameterizedTest(name = "[{index}] {0} throws a NullPointerException for a null {1}")
	@MethodSource("nullRejectingCalls")
	void nullArgumentsAreRejected(String label, String parameter, Executable call)
	{
		NullPointerException exception = assertThrows(NullPointerException.class, call);
		assertEquals(parameter + " is marked non-null but is null", exception.getMessage());
	}
}
