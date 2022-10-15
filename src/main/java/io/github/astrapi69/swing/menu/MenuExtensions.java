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

import java.awt.event.ActionListener;
import java.awt.event.InputEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import lombok.NonNull;

/**
 * The class {@link MenuExtensions}.
 */
public final class MenuExtensions
{

	/**
	 * Converts the given char mnemonic to an int mnemonic
	 *
	 * @param charMnemonic
	 *            The char mnemonic to convert
	 * @return the converted mnemonic as int
	 */
	public static int toMnemonic(char charMnemonic)
	{
		int mnemonic = charMnemonic;
		if (mnemonic >= 'a' && mnemonic <= 'z')
		{
			mnemonic -= ('a' - 'A');
		}
		return mnemonic;
	}

	/**
	 * Sets the accelerator for the given menuitem and the given key char.
	 *
	 * @param jmi
	 *            The JMenuItem.
	 * @param keyChar
	 *            the key char
	 */
	public static void setAccelerator(final JMenuItem jmi, final char keyChar)
	{
		jmi.setAccelerator(KeyStroke.getKeyStroke(keyChar));
	}

	/**
	 * Sets the mnemonic for the given menu from the given key char.
	 *
	 * @param menu
	 *            The JMenu
	 * @param keyChar
	 *            the key char
	 */
	public static void setMnemonic(final JMenu menu, final char keyChar)
	{
		menu.setMnemonic(keyChar);
	}

	/**
	 * Sets the mnemonic for the given menu from the given key char.
	 *
	 * @param menu
	 *            The JMenu
	 * @param mnemonic
	 *            the key code which represents the mnemonic
	 */
	public static void setMnemonic(final JMenu menu, final int mnemonic)
	{
		menu.setMnemonic(mnemonic);
	}

	/**
	 * Sets the mnemonic for the given JMenuItem from the given key char.
	 *
	 * @param menuItem
	 *            The JMenuItem
	 * @param keyChar
	 *            the key char
	 */
	public static void setMnemonic(final JMenuItem menuItem, final char keyChar)
	{
		menuItem.setMnemonic(keyChar);
	}

	/**
	 * Sets the mnemonic for the given JMenuItem from the given key char.
	 *
	 * @param menuItem
	 *            The JMenuItem
	 * @param mnemonic
	 *            the key code which represents the mnemonic
	 */
	public static void setMnemonic(final JMenuItem menuItem, final int mnemonic)
	{
		menuItem.setMnemonic(mnemonic);
	}

	/**
	 * Sets the accelerator for the given menuitem and the given key char and the given modifiers.
	 *
	 * @param jmi
	 *            The JMenuItem.
	 * @param keyChar
	 *            the key char
	 * @param modifiers
	 *            the modifiers
	 */
	public static void setAccelerator(final JMenuItem jmi, final Character keyChar,
		final int modifiers)
	{
		setAccelerator(jmi, KeyStroke.getKeyStroke(keyChar, modifiers));
	}

	/**
	 * Sets the accelerator for the given menuitem and the given key code and the given modifiers.
	 *
	 * @param jmi
	 *            The JMenuItem.
	 * @param keyCode
	 *            the key code
	 * @param modifiers
	 *            the modifiers
	 */
	public static void setAccelerator(final JMenuItem jmi, final int keyCode, final int modifiers)
	{
		setAccelerator(jmi, KeyStroke.getKeyStroke(keyCode, modifiers));
	}

	/**
	 * Sets the accelerator for the given menuitem and the given key code and the given modifiers.
	 *
	 * @param jmi
	 *            The JMenuItem
	 * @param keyStroke
	 *            The keystroke
	 */
	public static void setAccelerator(final JMenuItem jmi, final KeyStroke keyStroke)
	{
		jmi.setAccelerator(keyStroke);
	}

	/**
	 * Sets the accelerator for the given menuitem and the given keyCode and the given modifiers.
	 *
	 * @param jmi
	 *            The JMenuItem.
	 * @param keyCode
	 *            the key code
	 * @param modifiers
	 *            the modifiers
	 * @param onKeyRelease
	 *            true if the KeyStroke should represent a key release, false otherwise.
	 */
	public static void setAccelerator(final JMenuItem jmi, final int keyCode, final int modifiers,
		final boolean onKeyRelease)
	{
		setAccelerator(jmi, KeyStroke.getKeyStroke(keyCode, modifiers, onKeyRelease));
	}

	/**
	 * Sets the accelerator for the given menuitem and the given parsable keystroke string.
	 *
	 * @param jmi
	 *            The JMenuItem.
	 * @param parsableKeystrokeString
	 *            the parsable keystroke string
	 */
	public static void setAccelerator(final JMenuItem jmi, final String parsableKeystrokeString)
	{
		setAccelerator(jmi, KeyStroke.getKeyStroke(parsableKeystrokeString));
	}

	/**
	 * Sets the accelerator for the given menuitem and the given character with the ALT. The
	 * accelerator are combined with the given character and the ALT.
	 *
	 * @param jmi
	 *            The JMenuItem.
	 * @param accelerator
	 *            The character that have to push together with the ALT.
	 */
	public static void setAltAccelerator(final JMenuItem jmi, final char accelerator)
	{
		setAccelerator(jmi, accelerator, InputEvent.ALT_MASK);
	}

	/**
	 * Sets the accelerator for the given menuitem and the given character with the CTRL. The
	 * accelerator are combined with the given character and the CTRL.
	 *
	 * @param jmi
	 *            The JMenuItem.
	 * @param accelerator
	 *            The character that have to push together with the CTRL.
	 */
	public static void setCtrlAccelerator(final JMenuItem jmi, final char accelerator)
	{
		String keystrokeAsString = "ctrl pressed " + accelerator;
		setAccelerator(jmi, accelerator, InputEvent.CTRL_MASK);
	}

	/**
	 * Adds the given <code>ActionListener</code> to the given <code>JMenuItem</code>
	 *
	 * @param menuItem
	 *            The <code>JMenuItem</code> object
	 * @param actionListener
	 *            The <code>ActionListener</code> object
	 */
	public static void addActionListener(final @NonNull JMenuItem menuItem,
		final @NonNull ActionListener actionListener)
	{
		menuItem.addActionListener(actionListener);
	}

	/**
	 * Adds the given <code>JMenuItem</code> to the given <code>JMenu</code>
	 *
	 * @param menu
	 *            The <code>JMenu</code> object
	 * @param menuItem
	 *            The <code>JMenuItem</code> object
	 * @return the <code>JMenuItem</code> added
	 */
	public static JMenuItem addMenuItem(final @NonNull JMenu menu,
		final @NonNull JMenuItem menuItem)
	{
		return menu.add(menuItem);
	}

	/**
	 * Adds the given <code>ActionListener</code> to the given <code>JMenuItem</code> and finally
	 * the given <code>JMenuItem</code> is added to the given <code>JMenu</code>
	 *
	 * @param menu
	 *            The <code>JMenu</code> object
	 * @param menuItem
	 *            The <code>JMenuItem</code> object
	 * @param actionListener
	 *            The <code>ActionListener</code> object
	 */
	public static JMenuItem addMenuItem(final @NonNull JMenu menu,
		final @NonNull JMenuItem menuItem, final @NonNull ActionListener actionListener)
	{
		JMenuItem jMenuItem = addMenuItem(menu, menuItem);
		addActionListener(jMenuItem, actionListener);
		return jMenuItem;
	}

	/**
	 * Adds the given <code>ActionListener</code> to the given <code>JMenuItem</code> and finally
	 * the given <code>JMenuItem</code> is added to the given <code>JMenu</code>
	 *
	 * @param menu
	 *            The <code>JMenu</code> object
	 * @param menuItem
	 *            The <code>JMenuItem</code> object
	 * @param actionListener
	 *            The <code>ActionListener</code> object
	 */
	public static JMenuItem addMenuItem(final @NonNull JMenu menu,
		final @NonNull JMenuItem menuItem, final @NonNull KeyStroke keyStroke,
		final @NonNull ActionListener actionListener)
	{
		JMenuItem jMenuItem = addMenuItem(menu, menuItem);
		jMenuItem.setAccelerator(keyStroke);
		addActionListener(jMenuItem, actionListener);
		return jMenuItem;
	}


}
