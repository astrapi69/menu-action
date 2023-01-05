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

import java.awt.MenuItem;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import io.github.astrapi69.swing.menu.model.transform.MenuItemInfoConverter;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

/**
 * The class {@link MenuItemInfo} holds the fields for build {@link JMenu}, {@link JMenuBar},
 * {@link JMenuItem}, {@link MenuItem}, {@link JCheckBoxMenuItem} and {@link JRadioButtonMenuItem}
 * objects
 */
@Data
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MenuItemInfo
{
	String text;
	String actionCommand;
	Integer mnemonic;
	KeyStroke keyStroke;
	ActionListener actionListener;
	String name;

	public JCheckBoxMenuItem toJCheckBoxMenuItem()
	{
		return MenuItemInfoConverter.toJCheckBoxMenuItem(this);
	}

	public JRadioButtonMenuItem toJRadioButtonMenuItem()
	{
		return MenuItemInfoConverter.toJRadioButtonMenuItem(this);
	}

	public JMenuItem toJMenuItem()
	{
		return MenuItemInfoConverter.toJMenuItem(this);
	}

	public MenuItem toMenuItem()
	{
		return MenuItemInfoConverter.toMenuItem(this);
	}

	public JMenu toJMenu()
	{
		return MenuItemInfoConverter.toJMenu(this);
	}

	public JMenuBar toJMenuBar()
	{
		return MenuItemInfoConverter.toJMenuBar(this);
	}

}
