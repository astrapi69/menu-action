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

import javax.swing.KeyStroke;

import io.github.astrapi69.swing.menu.KeyStrokeExtensions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

/**
 * The class {@link KeyStrokeInfo} is intended for store the information of a keystroke and restore
 * it back to a {@link KeyStroke} object
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KeyStrokeInfo
{

	/**
	 * the {@link Integer} object that specifies the numeric code for a keyboard key
	 */
	Integer keyCode;

	/**
	 * the {@link Integer} object that specifies the bitwise-ored combination of any modifiers
	 */
	Integer modifiers;

	/**
	 * the {@link Boolean} object that specifies if the KeyStroke should represent a key release in
	 * case of true.
	 */
	Boolean onKeyRelease;

	/**
	 * the {@link String} object that specifies the keystroke. <br/>
	 * For more information @see KeyStroke#getKeyStroke(String)
	 */
	String keystrokeAsString;

	/**
	 * Factory method that creates a {@link KeyStrokeInfo} object from the given {@link KeyStroke}
	 * object
	 *
	 * @param keyStroke
	 *            the {@link KeyStroke} object
	 * @return the new created {@link KeyStrokeInfo} object
	 */
	public static KeyStrokeInfo toKeyStrokeInfo(final @NonNull KeyStroke keyStroke)
	{
		return KeyStrokeExtensions.toKeyStrokeInfo(keyStroke);
	}

	/**
	 * Sets all values of this {@link KeyStrokeInfo} object from the given {@link KeyStroke} object
	 * 
	 * @param keyStroke
	 *            the {@link KeyStroke} object
	 */
	public KeyStrokeInfo set(final @NonNull KeyStroke keyStroke)
	{
		this.keyCode = keyStroke.getKeyCode();
		this.modifiers = keyStroke.getModifiers();
		this.onKeyRelease = keyStroke.isOnKeyRelease();
		this.keystrokeAsString = keyStroke.toString();
		return this;
	}

	/**
	 * Factory method that creates a {@link KeyStroke} object from this {@link KeyStrokeInfo} object
	 *
	 * @return the new created {@link KeyStroke} object
	 */
	public KeyStroke toKeyStroke()
	{
		KeyStroke keyStroke = null;
		if (keystrokeAsString != null && !keystrokeAsString.isEmpty())
		{
			keyStroke = KeyStroke.getKeyStroke(keystrokeAsString);
			if (keyStroke != null)
			{
				return keyStroke;
			}
		}
		if (keyCode != null)
		{

			if (onKeyRelease != null && modifiers != null)
			{
				keyStroke = KeyStroke.getKeyStroke(keyCode, modifiers, onKeyRelease);
				if (keyStroke != null)
				{
					return keyStroke;
				}
			}

			if (modifiers != null)
			{
				keyStroke = KeyStroke.getKeyStroke(keyCode, modifiers);
				if (keyStroke != null)
				{
					return keyStroke;
				}
			}
		}
		return keyStroke;
	}
}
