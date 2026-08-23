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
package io.github.astrapi69.swing.menu.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import org.junit.jupiter.api.Test;

/**
 * The unit test class for the class {@link KeyStrokeInfo}
 */
class KeyStrokeInfoTest
{

	private static KeyStrokeInfo newAltF4()
	{
		return KeyStrokeInfo.builder().keyCode(KeyEvent.VK_F4).modifiers(InputEvent.ALT_DOWN_MASK)
			.onKeyRelease(false).keystrokeAsString("alt pressed F4").build();
	}

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

	@Test
	void builderAndGetters()
	{
		KeyStrokeInfo info = newAltF4();
		assertEquals(KeyEvent.VK_F4, info.getKeyCode());
		assertEquals(InputEvent.ALT_DOWN_MASK, info.getModifiers());
		assertEquals(Boolean.FALSE, info.getOnKeyRelease());
		assertEquals("alt pressed F4", info.getKeystrokeAsString());

		KeyStrokeInfo empty = KeyStrokeInfo.builder().build();
		assertNull(empty.getKeyCode());
		assertNull(empty.getModifiers());
		assertNull(empty.getOnKeyRelease());
		assertNull(empty.getKeystrokeAsString());
	}

	@Test
	void constructorsAndSetters()
	{
		KeyStrokeInfo info = new KeyStrokeInfo();
		assertNull(info.getKeyCode());
		assertNull(info.toKeyStroke());
		info.setKeyCode(KeyEvent.VK_F4);
		info.setModifiers(InputEvent.ALT_DOWN_MASK);
		info.setOnKeyRelease(false);
		info.setKeystrokeAsString("alt pressed F4");
		assertEquals(newAltF4(), info);

		KeyStrokeInfo allArgs = new KeyStrokeInfo(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK, false,
			"alt pressed F4");
		assertEquals(newAltF4(), allArgs);
	}

	@Test
	void toBuilder()
	{
		KeyStrokeInfo original = newAltF4();
		KeyStrokeInfo copy = original.toBuilder().build();
		assertEquals(original, copy);
		assertNotSame(original, copy);

		KeyStrokeInfo changed = original.toBuilder().keyCode(KeyEvent.VK_F5)
			.keystrokeAsString("alt pressed F5").build();
		assertNotEquals(original, changed);
		assertEquals(KeyEvent.VK_F5, changed.getKeyCode());
		assertEquals("alt pressed F5", changed.getKeystrokeAsString());
		assertEquals(InputEvent.ALT_DOWN_MASK, changed.getModifiers());
		// the original is untouched
		assertEquals(KeyEvent.VK_F4, original.getKeyCode());
		assertEquals("alt pressed F4", original.getKeystrokeAsString());
	}

	@Test
	void setCopiesAllValuesAndReturnsThis()
	{
		KeyStrokeInfo info = new KeyStrokeInfo();
		KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_S,
			InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK, true);
		KeyStrokeInfo result = info.set(keyStroke);
		assertSame(info, result);
		assertEquals(KeyEvent.VK_S, info.getKeyCode());
		// the keystroke reports the extended and the legacy modifier masks
		assertEquals(keyStroke.getModifiers(), info.getModifiers());
		assertTrue((info.getModifiers() & (InputEvent.CTRL_DOWN_MASK
			| InputEvent.SHIFT_DOWN_MASK)) == (InputEvent.CTRL_DOWN_MASK
				| InputEvent.SHIFT_DOWN_MASK));
		assertEquals(Boolean.TRUE, info.getOnKeyRelease());
		assertEquals(keyStroke.toString(), info.getKeystrokeAsString());
		assertEquals(keyStroke, info.toKeyStroke());
		// set is the same as the factory method
		assertEquals(KeyStrokeInfo.toKeyStrokeInfo(keyStroke), info);
	}

	@Test
	void nullArguments()
	{
		assertThrows(NullPointerException.class, () -> new KeyStrokeInfo().set(null));
		assertThrows(NullPointerException.class, () -> KeyStrokeInfo.toKeyStrokeInfo(null));
	}

	@Test
	void toKeyStrokeFromKeyCode()
	{
		// without keystroke string the key code and the modifiers are used
		KeyStrokeInfo info = KeyStrokeInfo.builder().keyCode(KeyEvent.VK_S)
			.modifiers(InputEvent.CTRL_DOWN_MASK).build();
		KeyStroke keyStroke = info.toKeyStroke();
		assertEquals(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), keyStroke);
		assertFalse(keyStroke.isOnKeyRelease());

		// null modifiers means no modifiers
		info = KeyStrokeInfo.builder().keyCode(KeyEvent.VK_F1).build();
		assertEquals(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), info.toKeyStroke());

		// on key release is respected
		info = KeyStrokeInfo.builder().keyCode(KeyEvent.VK_F1).onKeyRelease(true).build();
		assertTrue(info.toKeyStroke().isOnKeyRelease());
		assertEquals(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0, true), info.toKeyStroke());
	}

	@Test
	void toKeyStrokeStringHasPriority()
	{
		KeyStrokeInfo info = KeyStrokeInfo.builder().keyCode(KeyEvent.VK_S)
			.modifiers(InputEvent.CTRL_DOWN_MASK).keystrokeAsString("alt pressed F4").build();
		assertEquals(KeyStroke.getKeyStroke("alt pressed F4"), info.toKeyStroke());
	}

	@Test
	void toKeyStrokeInvalidStringFallsBackToKeyCode()
	{
		KeyStrokeInfo info = KeyStrokeInfo.builder().keyCode(KeyEvent.VK_S)
			.modifiers(InputEvent.CTRL_DOWN_MASK).keystrokeAsString("not a keystroke").build();
		assertEquals(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK),
			info.toKeyStroke());

		// an invalid string without key code can not be converted
		info = KeyStrokeInfo.builder().keystrokeAsString("not a keystroke").build();
		assertNull(info.toKeyStroke());
		info = KeyStrokeInfo.builder().keystrokeAsString("").build();
		assertNull(info.toKeyStroke());
		assertNull(KeyStrokeInfo.builder().build().toKeyStroke());
	}

	@Test
	void equalsAndHashCode()
	{
		KeyStrokeInfo first = newAltF4();
		KeyStrokeInfo second = newAltF4();
		assertEquals(first, first);
		assertEquals(first, second);
		assertEquals(second, first);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, null);
		assertNotEquals(first, "alt pressed F4");

		assertNotEquals(first, first.toBuilder().keyCode(KeyEvent.VK_F5).build());
		assertNotEquals(first, first.toBuilder().modifiers(InputEvent.CTRL_DOWN_MASK).build());
		assertNotEquals(first, first.toBuilder().onKeyRelease(true).build());
		assertNotEquals(first, first.toBuilder().keystrokeAsString("alt released F4").build());
		assertNotEquals(first, first.toBuilder().keyCode(null).build());
		assertEquals(new KeyStrokeInfo(), KeyStrokeInfo.builder().build());
		assertEquals(new KeyStrokeInfo().hashCode(), KeyStrokeInfo.builder().build().hashCode());
	}

	@Test
	void toStringContainsAllFields()
	{
		String string = newAltF4().toString();
		assertTrue(string.startsWith("KeyStrokeInfo("), string);
		assertTrue(string.contains("keyCode=" + KeyEvent.VK_F4), string);
		assertTrue(string.contains("modifiers=" + InputEvent.ALT_DOWN_MASK), string);
		assertTrue(string.contains("onKeyRelease=false"), string);
		assertTrue(string.contains("keystrokeAsString=alt pressed F4"), string);
	}
}
