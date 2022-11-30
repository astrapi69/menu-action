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

import javax.swing.JMenu;

import io.github.astrapi69.swing.menu.MenuExtensions;
import lombok.NonNull;
import io.github.astrapi69.swing.menu.model.MenuItemInfo;

/**
 * A factory {@link JMenuFactory} provides factory methods for create JMenu objects
 */
public class JMenuFactory
{

	/**
	 * Factory method for create a <code>JMenu</code>.
	 *
	 * @param text
	 *            the text of the <code>JMenu</code>
	 * @return the new {@link JMenu} object
	 */
	public static JMenu newJMenu(final @NonNull String text)
	{
		return MenuItemInfo.builder().text(text).build().toJMenu();
	}

	/**
	 * Factory method for create a <code>JMenu</code>.
	 *
	 * @param menuItemInfo
	 *            the information for build a <code>JMenu</code>.
	 * @return the new {@link JMenu} object
	 */
	public static JMenu newJMenu(final @NonNull MenuItemInfo menuItemInfo)
	{
		return menuItemInfo.toJMenu();
	}

	/**
	 * Factory method for create a <code>JMenu</code>.
	 *
	 * @param text
	 *            the text of the <code>JMenu</code>
	 * @param mnemonic
	 *            the keyboard mnemonic for the <code>JMenu</code>
	 * @return the new {@link JMenu} object
	 */
	public static JMenu newJMenu(final @NonNull String text, final int mnemonic)
	{
		return MenuItemInfo.builder().text(text).mnemonic(mnemonic).build().toJMenu();
	}

	/**
	 * Factory method for create a <code>JMenu</code>.
	 *
	 * @param text
	 *            the text of the <code>JMenu</code>
	 * @param mnemonic
	 *            the keyboard mnemonic for the <code>JMenu</code>
	 * @param actionListener
	 *            The action listener for the <code>JMenu</code>
	 * @return the new {@link JMenu} object
	 */
	public static JMenu newJMenu(final @NonNull String text, final int mnemonic,
		final @NonNull ActionListener actionListener)
	{
		return MenuItemInfo.builder().text(text).mnemonic(mnemonic).actionListener(actionListener)
			.build().toJMenu();
	}

	/**
	 * Factory method for create a <code>JMenu</code>.
	 *
	 * @param text
	 *            the text of the <code>JMenu</code>
	 * @param mnemonic
	 *            the keyboard mnemonic for the <code>JMenu</code>
	 * @return the new {@link JMenu} object
	 */
	public static JMenu newJMenu(final @NonNull String text, final char mnemonic)
	{
		return MenuItemInfo.builder().text(text).mnemonic(MenuExtensions.toMnemonic(mnemonic))
			.build().toJMenu();
	}

	/**
	 * Factory method for create a <code>JMenu</code>.
	 *
	 * @param text
	 *            the text of the <code>JMenu</code>
	 * @param mnemonic
	 *            the keyboard mnemonic for the <code>JMenu</code>
	 * @param actionListener
	 *            The action listener for the <code>JMenu</code>
	 * @return the new {@link JMenu} object
	 */
	public static JMenu newJMenu(final @NonNull String text, final char mnemonic,
		final @NonNull ActionListener actionListener)
	{
		return MenuItemInfo.builder().text(text).mnemonic(MenuExtensions.toMnemonic(mnemonic))
			.actionListener(actionListener).build().toJMenu();
	}

}
