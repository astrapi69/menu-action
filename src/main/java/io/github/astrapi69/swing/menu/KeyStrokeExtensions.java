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

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ComponentInputMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ActionMapUIResource;

import io.github.astrapi69.swing.menu.model.KeyStrokeInfo;
import lombok.NonNull;

/**
 * The class {@link KeyStrokeExtensions}.
 */
public final class KeyStrokeExtensions
{

	/**
	 * Private constructor to prevent instantiation
	 */
	private KeyStrokeExtensions()
	{
	}

	/**
	 * Adds the shortcut to component.
	 *
	 * @param component
	 *            the component
	 * @param keystroke
	 *            the keystroke
	 * @param whenInFocus
	 *            the whe in focus
	 * @param actionCommand
	 *            the action command
	 * @param action
	 *            the action
	 */
	public static void addShortcutToComponent(final JComponent component, final KeyStroke keystroke,
		final int whenInFocus, final String actionCommand, final Action action)
	{
		final InputMap keyMap = new ComponentInputMap(component);
		keyMap.put(keystroke, actionCommand);
		final ActionMap actionMap = new ActionMapUIResource();
		actionMap.put(actionCommand, action);
		SwingUtilities.replaceUIActionMap(component, actionMap);
		SwingUtilities.replaceUIInputMap(component, whenInFocus, keyMap);
	}

	/**
	 * Adds the shortcut to component.
	 *
	 * @param component
	 *            the component
	 * @param keystroke
	 *            the keystroke
	 * @param actionCommand
	 *            the action command
	 * @param action
	 *            the action
	 */
	public static void addShortcutToComponent(final JComponent component, final KeyStroke keystroke,
		final String actionCommand, final Action action)
	{
		addShortcutToComponent(component, keystroke, JComponent.WHEN_IN_FOCUSED_WINDOW,
			actionCommand, action);
	}

	/**
	 * Gets the given string that represents a keystroke.
	 * 
	 * @param keystrokeAsString
	 *            the keystroke as string object
	 * @return the corresponding {@link KeyStroke} object
	 */
	public static KeyStroke getKeyStroke(String keystrokeAsString)
	{
		return KeyStroke.getKeyStroke(keystrokeAsString);
	}

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
		return KeyStrokeInfo.builder().keyCode(keyStroke.getKeyCode())
			.modifiers(keyStroke.getModifiers()).onKeyRelease(keyStroke.isOnKeyRelease())
			.keystrokeAsString(keyStroke.toString()).build();
	}

}
