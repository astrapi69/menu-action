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

import java.util.ArrayList;
import java.util.List;

import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import io.github.astrapi69.swing.menu.KeyStrokeExtensions;
import io.github.astrapi69.swing.menu.model.KeyStrokeInfo;
import lombok.NonNull;

/**
 * The class {@link KeyStrokeInfoExtensions} provides utility methods for handling
 * {@link KeyStrokeInfo} and {@link KeyStroke} objects
 */
public final class KeyStrokeInfoExtensions
{

	/**
	 * Private constructor to prevent instantiation
	 */
	private KeyStrokeInfoExtensions()
	{
	}

	/**
	 * Sets all values of this {@link KeyStrokeInfo} object from the given {@link KeyStroke} object
	 *
	 * @param keyStrokeInfo
	 *            the {@link KeyStrokeInfo} object to be set
	 * @param keyStroke
	 *            the {@link KeyStroke} object from which values will be copied
	 */
	public static void set(@NonNull final KeyStrokeInfo keyStrokeInfo,
		final @NonNull KeyStroke keyStroke)
	{
		keyStrokeInfo.setKeyCode(keyStroke.getKeyCode());
		keyStrokeInfo.setModifiers(keyStroke.getModifiers());
		keyStrokeInfo.setOnKeyRelease(keyStroke.isOnKeyRelease());
		keyStrokeInfo.setKeystrokeAsString(keyStroke.toString());
	}

	/**
	 * Converter method that creates a {@link KeyStroke} object from the given {@link KeyStrokeInfo}
	 * object. The keystroke string has priority, then the key code with the modifiers
	 *
	 * @param keyStrokeInfo
	 *            the {@link KeyStrokeInfo} object from which the {@link KeyStroke} is created
	 * @return the new created {@link KeyStroke} object or null if the keyStrokeInfo is invalid
	 */
	public static KeyStroke toKeyStroke(@NonNull final KeyStrokeInfo keyStrokeInfo)
	{
		KeyStroke keyStroke = null;
		if (keyStrokeInfo.getKeystrokeAsString() != null
			&& !keyStrokeInfo.getKeystrokeAsString().isEmpty())
		{
			keyStroke = KeyStroke.getKeyStroke(keyStrokeInfo.getKeystrokeAsString());
			if (keyStroke != null)
			{
				return keyStroke;
			}
		}
		if (keyStrokeInfo.getKeyCode() != null)
		{
			int modifiers = keyStrokeInfo.getModifiers() != null ? keyStrokeInfo.getModifiers() : 0;
			boolean onKeyRelease = keyStrokeInfo.getOnKeyRelease() != null
				&& keyStrokeInfo.getOnKeyRelease();
			keyStroke = KeyStroke.getKeyStroke(keyStrokeInfo.getKeyCode(), modifiers, onKeyRelease);
		}
		return keyStroke;
	}

	/**
	 * Gets the list of {@link KeyStrokeInfo} objects from the given {@link JComponent} object. For
	 * a {@link JMenuItem} the accelerator is the first entry. Then all keystrokes of the input map
	 * with the condition {@link JComponent#WHEN_IN_FOCUSED_WINDOW} follow
	 *
	 * @param jComponent
	 *            the {@link JComponent} object from which {@link KeyStrokeInfo} objects will be
	 *            retrieved
	 * @return the list of {@link KeyStrokeInfo} objects
	 */
	public static List<KeyStrokeInfo> getKeyStrokeInfos(final @NonNull JComponent jComponent)
	{
		List<KeyStrokeInfo> keyStrokeInfos = new ArrayList<>();
		if (jComponent instanceof JMenuItem menuItem && menuItem.getAccelerator() != null)
		{
			keyStrokeInfos.add(KeyStrokeExtensions.toKeyStrokeInfo(menuItem.getAccelerator()));
		}
		InputMap inputMap = jComponent.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		KeyStroke[] keyStrokes = inputMap != null ? inputMap.allKeys() : null;
		if (keyStrokes != null)
		{
			for (KeyStroke keyStroke : keyStrokes)
			{
				keyStrokeInfos.add(KeyStrokeExtensions.toKeyStrokeInfo(keyStroke));
			}
		}
		return keyStrokeInfos;
	}

	/**
	 * Gets the first {@link KeyStrokeInfo} object from the given {@link JComponent} object
	 *
	 * @param jComponent
	 *            the {@link JComponent} object from which the first {@link KeyStrokeInfo} object
	 *            will be retrieved
	 * @return the {@link KeyStrokeInfo} object or null if no {@link KeyStrokeInfo} is found
	 */
	public static KeyStrokeInfo getKeyStrokeInfo(final @NonNull JComponent jComponent)
	{
		List<KeyStrokeInfo> keyStrokeInfos = getKeyStrokeInfos(jComponent);
		return keyStrokeInfos.isEmpty() ? null : keyStrokeInfos.get(0);
	}
}
