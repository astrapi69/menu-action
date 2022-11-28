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
package io.github.astrapi69.swing.menu.builder;

import java.awt.event.ActionListener;

import javax.swing.*;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

/**
 * The class {@link JMenuItemInfo} holds the fields for build {@link JMenuItem} and {@link JMenu}
 * objects
 */
@Data
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JMenuItemInfo
{
	String text;
	Integer mnemonic;
	KeyStroke keyStroke;
	ActionListener actionListener;
	String name;

	public JMenuItem toJMenuItem()
	{
		JMenuItem jMenuItem = new JMenuItem();
		setFields(jMenuItem);
		if (keyStroke != null)
		{
			jMenuItem.setAccelerator(keyStroke);
		}
		return jMenuItem;
	}

	public JMenu toJMenu()
	{
		JMenu jMenu = new JMenu();
		setFields(jMenu);
		return jMenu;
	}

	public JMenuBar toJMenuBar()
	{
		JMenuBar menuBar = new JMenuBar();
		if (name != null)
		{
			menuBar.setName(name);
		}
		return menuBar;
	}

	private void setFields(JMenuItem jMenuItem)
	{
		if (text != null)
		{
			jMenuItem.setText(text);
		}
		if (mnemonic != null)
		{
			jMenuItem.setMnemonic(mnemonic);
		}
		if (actionListener != null)
		{
			jMenuItem.addActionListener(actionListener);
		}
		if (name != null)
		{
			jMenuItem.setName(name);
		}
	}
}
