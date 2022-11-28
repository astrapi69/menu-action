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

import javax.swing.KeyStroke;

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
 * The class {@link KeyStrokeInfo}
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
	Character keyChar;
	Integer keyCode;
	Integer modifiers;
	Boolean onKeyRelease;
	String keystrokeAsString;

	public static KeyStrokeInfo toKeyStrokeInfo(final @NonNull KeyStroke keyStroke)
	{
		return KeyStrokeInfo.builder().keyCode(keyStroke.getKeyCode())
			.keyChar(keyStroke.getKeyChar()).modifiers(keyStroke.getModifiers())
			.onKeyRelease(keyStroke.isOnKeyRelease()).keystrokeAsString(keyStroke.toString())
			.build();
	}

	public void set(final @NonNull KeyStroke keyStroke)
	{
		this.keyCode = keyStroke.getKeyCode();
		this.keyChar = keyStroke.getKeyChar();
		this.modifiers = keyStroke.getModifiers();
		this.onKeyRelease = keyStroke.isOnKeyRelease();
		this.keystrokeAsString = keyStroke.toString();
	}

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
				keyStroke = KeyStroke.getKeyStroke(keyCode.intValue(), modifiers.intValue(),
					onKeyRelease);
				if (keyStroke != null)
				{
					return keyStroke;
				}
			}

			if (modifiers != null)
			{
				keyStroke = KeyStroke.getKeyStroke(keyCode.intValue(), modifiers.intValue());
				if (keyStroke != null)
				{
					return keyStroke;
				}
			}
		}

		if (keyChar != null)
		{

			if (modifiers != null)
			{
				keyStroke = KeyStroke.getKeyStroke(keyChar, modifiers.intValue());
				if (keyStroke != null)
				{
					return keyStroke;
				}
			}

			keyStroke = KeyStroke.getKeyStroke(keyChar);
			if (keyStroke != null)
			{
				return keyStroke;
			}
		}
		return keyStroke;
	}
}
