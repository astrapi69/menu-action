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
package io.github.astrapi69.swing.menu.factory;

import java.awt.event.ActionListener;
import java.awt.event.InputEvent;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import io.github.astrapi69.swing.menu.KeyStrokeExtensions;
import io.github.astrapi69.swing.menu.MenuExtensions;
import io.github.astrapi69.swing.menu.model.MenuItemInfo;
import lombok.NonNull;

/**
 * A factory {@link JMenuItemFactory} provides factory methods for create JMenuItem objects
 */
public class JMenuItemFactory
{

	/**
	 * Factory method for create a <code>JMenuItem</code>.
	 *
	 * @param menuItemInfo
	 *            the information for build a <code>JMenuItem</code>.
	 * @return the new {@link JMenuItem}
	 */
	public static JMenuItem newJMenuItem(final @NonNull MenuItemInfo menuItemInfo)
	{
		return menuItemInfo.toJMenuItem();
	}

	/**
	 * Factory method for create a <code>JMenuItem</code>.
	 *
	 * @param text
	 *            the text of the <code>JMenuItem</code>
	 * @return the new {@link JMenuItem}
	 */
	public static JMenuItem newJMenuItem(final @NonNull String text)
	{
		return MenuItemInfo.builder().text(text).build().toJMenuItem();
	}

	/**
	 * Factory method for create a <code>JMenuItem</code>.
	 *
	 * @param text
	 *            the text of the <code>JMenuItem</code>
	 * @return the new {@link JMenuItem}
	 */
	public static JMenuItem newJMenuItem(final @NonNull String text,
		final @NonNull ActionListener actionListener)
	{
		return MenuItemInfo.builder().text(text).actionListener(actionListener).build()
			.toJMenuItem();
	}

	/**
	 * Factory method for create a <code>JMenuItem</code>.
	 *
	 * @param text
	 *            the text of the <code>JMenuItem</code>
	 * @param mnemonic
	 *            the keyboard mnemonic for the <code>JMenuItem</code>
	 * @return the new {@link JMenuItem}
	 */
	public static JMenuItem newJMenuItem(final @NonNull String text, final int mnemonic)
	{
		return MenuItemInfo.builder().text(text).mnemonic(mnemonic).build().toJMenuItem();
	}

	/**
	 * Factory method for create a <code>JMenuItem</code>.
	 *
	 * @param text
	 *            the text of the <code>JMenuItem</code>
	 * @param mnemonic
	 *            the keyboard mnemonic for the <code>JMenuItem</code>
	 * @param accelerator
	 *            The character that have to push together with the CTRL.
	 * @return the new {@link JMenuItem}
	 */
	public static JMenuItem newJMenuItem(final @NonNull String text, final int mnemonic,
		final char accelerator)
	{
		return MenuItemInfo.builder().text(text).mnemonic(mnemonic)
			.keyStrokeInfo(KeyStrokeExtensions
				.toKeyStrokeInfo(KeyStroke.getKeyStroke(accelerator, InputEvent.CTRL_DOWN_MASK)))
			.build().toJMenuItem();
	}

	/**
	 * Factory method for create a <code>JMenuItem</code>.
	 *
	 * @param text
	 *            the text of the <code>JMenuItem</code>
	 * @param mnemonic
	 *            the keyboard mnemonic for the <code>JMenuItem</code>
	 * @param accelerator
	 *            The character that have to push together with the CTRL.
	 * @param actionListener
	 *            The action listener for the <code>JMenuItem</code>
	 * @return the new {@link JMenuItem}
	 */
	public static JMenuItem newJMenuItem(final @NonNull String text, final int mnemonic,
		final char accelerator, final @NonNull ActionListener actionListener)
	{
		return MenuItemInfo.builder().text(text).mnemonic(mnemonic)
			.keyStrokeInfo(KeyStrokeExtensions
				.toKeyStrokeInfo(KeyStroke.getKeyStroke(accelerator, InputEvent.CTRL_DOWN_MASK)))
			.actionListener(actionListener).build().toJMenuItem();
	}

	/**
	 * Factory method for create a <code>JMenuItem</code>.
	 *
	 * @param text
	 *            the text of the <code>JMenuItem</code>
	 * @param mnemonic
	 *            the keyboard mnemonic for the <code>JMenuItem</code>
	 * @param keyStroke
	 *            The keystroke
	 * @param actionListener
	 *            The action listener for the <code>JMenuItem</code>
	 * @return the new {@link JMenuItem}
	 */
	public static JMenuItem newJMenuItem(final @NonNull String text, final int mnemonic,
		final KeyStroke keyStroke, final @NonNull ActionListener actionListener)
	{
		return MenuItemInfo.builder().text(text).mnemonic(mnemonic)
			.keyStrokeInfo(KeyStrokeExtensions.toKeyStrokeInfo(keyStroke))
			.actionListener(actionListener).build().toJMenuItem();
	}

	/**
	 * Factory method for create a <code>JMenuItem</code>.
	 *
	 * @param text
	 *            the text of the <code>JMenuItem</code>
	 * @param mnemonic
	 *            the keyboard mnemonic for the <code>JMenuItem</code>
	 * @param keyStroke
	 *            The keystroke
	 * @param actionListener
	 *            The action listener for the <code>JMenuItem</code>
	 * @return the new {@link JMenuItem}
	 */
	public static JMenuItem newJMenuItem(final @NonNull String text, final int mnemonic,
		final KeyStroke keyStroke, final ActionListener actionListener, String name)
	{
		return MenuItemInfo.builder().text(text).mnemonic(mnemonic).name(name)
			.keyStrokeInfo(KeyStrokeExtensions.toKeyStrokeInfo(keyStroke))
			.actionListener(actionListener).build().toJMenuItem();
	}

	/**
	 * Factory method for create a <code>JMenuItem</code>.
	 *
	 * @param text
	 *            the text of the <code>JMenuItem</code>
	 * @param mnemonic
	 *            the keyboard mnemonic for the <code>JMenuItem</code>
	 * @param accelerator
	 *            The character that have to push together with the CTRL.
	 * @return the new {@link JMenuItem}
	 */
	public static JMenuItem newJMenuItem(final @NonNull String text, final char mnemonic,
		final char accelerator)
	{
		return MenuItemInfo.builder().text(text).mnemonic(MenuExtensions.toMnemonic(mnemonic))
			.keyStrokeInfo(KeyStrokeExtensions
				.toKeyStrokeInfo(KeyStroke.getKeyStroke(accelerator, InputEvent.CTRL_DOWN_MASK)))
			.build().toJMenuItem();
	}

	/**
	 * Factory method for create a <code>JMenuItem</code>.
	 *
	 * @param text
	 *            the text of the <code>JMenuItem</code>
	 * @param mnemonic
	 *            the keyboard mnemonic for the <code>JMenuItem</code>
	 * @param accelerator
	 *            The character that have to push together with the CTRL.
	 * @param actionListener
	 *            The action listener for the <code>JMenuItem</code>
	 * @return the new {@link JMenuItem}
	 */
	public static JMenuItem newJMenuItem(final @NonNull String text, final char mnemonic,
		final char accelerator, final @NonNull ActionListener actionListener)
	{
		return MenuItemInfo.builder().text(text).mnemonic(MenuExtensions.toMnemonic(mnemonic))
			.keyStrokeInfo(KeyStrokeExtensions
				.toKeyStrokeInfo(KeyStroke.getKeyStroke(accelerator, InputEvent.CTRL_DOWN_MASK)))
			.actionListener(actionListener).build().toJMenuItem();
	}

}
