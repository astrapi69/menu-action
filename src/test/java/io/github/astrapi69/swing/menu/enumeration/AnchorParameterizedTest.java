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
package io.github.astrapi69.swing.menu.enumeration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * The parameterized unit test class for the class {@link Anchor}
 */
class AnchorParameterizedTest
{

	/**
	 * Parameterized test for {@link Anchor#valueOf(String)} and {@link Anchor#name()}
	 */
	@ParameterizedTest(name = "[{index}] valueOf(''{0}'') resolves the same constant again")
	@EnumSource(Anchor.class)
	void valueOfRoundTrip(Anchor anchor)
	{
		assertSame(anchor, Anchor.valueOf(anchor.name()));
		assertSame(anchor, Anchor.values()[anchor.ordinal()]);
		assertEquals(anchor.name(), anchor.toString());
	}

	/**
	 * Parameterized test for the names of the constants of the enum {@link Anchor}
	 */
	@ParameterizedTest(name = "[{index}] the constant with the name ''{0}'' has the ordinal {1}")
	@CsvSource({ "BEFORE, 0", "AFTER, 1", "FIRST, 2", "LAST, 3", "UNKNOWN, 4" })
	void nameAndOrdinal(String name, int expectedOrdinal)
	{
		Anchor anchor = Anchor.valueOf(name);

		assertEquals(expectedOrdinal, anchor.ordinal());
		assertEquals(name, anchor.name());
		assertEquals(Anchor.values().length, 5);
	}

	/**
	 * Parameterized test for {@link Anchor#valueOf(String)} with names of no enum constant
	 */
	@ParameterizedTest(name = "[{index}] valueOf(''{0}'') throws an IllegalArgumentException")
	@EmptySource
	@ValueSource(strings = { " ", "before", "Before", "BEFORE ", "NOT_AN_ANCHOR" })
	void valueOfWithUnknownName(String name)
	{
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
			() -> Anchor.valueOf(name));
		assertNotNull(exception.getMessage());
		assertTrue(exception.getMessage().contains("No enum constant"),
			() -> "the message '" + exception.getMessage() + "' should contain 'No enum constant'");
	}

	/**
	 * Parameterized test for {@link Anchor#valueOf(String)} with a null name
	 */
	@ParameterizedTest(name = "[{index}] valueOf(null) throws a NullPointerException")
	@NullSource
	void valueOfWithNullName(String name)
	{
		assertThrows(NullPointerException.class, () -> Anchor.valueOf(name));
	}
}
