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
package io.github.astrapi69.swing.menu.model.transform;

import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import io.github.astrapi69.collection.list.ListExtensions;
import io.github.astrapi69.swing.menu.enumeration.BaseMenuId;
import io.github.astrapi69.swing.menu.enumeration.MenuType;
import io.github.astrapi69.swing.menu.model.KeyStrokeInfo;
import io.github.astrapi69.swing.menu.model.MenuInfo;
import io.github.astrapi69.swing.menu.model.MenuItemInfo;
import lombok.NonNull;

public class MenuItemInfoConverter
{

	public static MenuInfo fromJMenuBar()
	{
		return MenuInfo.builder().ordinal(100).type(MenuType.MENU_BAR)
			.name(BaseMenuId.MENU_BAR.propertiesKey()).build();
	}

	@SuppressWarnings(value = "raw")
	public static List<KeyStrokeInfo> getKeyStrokeInfos(JComponent jComponent)
	{
		Object whenInFocusedWindow = jComponent.getClientProperty("_WhenInFocusedWindow");
		KeyStroke keyStroke;
		List<KeyStrokeInfo> keyStrokeInfos = new ArrayList<>();
		if (whenInFocusedWindow instanceof Hashtable)
		{
			Hashtable hashtable = (Hashtable)whenInFocusedWindow;
			Set keySet = hashtable.keySet();
			for (Object key : keySet)
			{
				if (key instanceof KeyStroke)
				{
					keyStroke = (KeyStroke)key;
					keyStrokeInfos.add(KeyStrokeInfo.toKeyStrokeInfo(keyStroke));
				}
			}
		}
		return keyStrokeInfos;
	}

	public static KeyStrokeInfo getKeyStrokeInfo(JComponent jComponent)
	{
		return ListExtensions.getFirst(getKeyStrokeInfos(jComponent));
	}

	public static MenuInfo fromJMenu(final @NonNull JMenu menu)
	{
		KeyStrokeInfo keyStrokeInfo = getKeyStrokeInfo(menu);
		return keyStrokeInfo != null
			? MenuInfo.builder().type(MenuType.MENU).name(menu.getName()).text(menu.getText())
				.mnemonic(menu.getMnemonic()).keyStrokeInfo(keyStrokeInfo).build()
			: MenuInfo.builder().type(MenuType.MENU).name(menu.getName()).text(menu.getText())
				.mnemonic(menu.getMnemonic()).build();
	}

	public static MenuInfo fromJMenuItem(final @NonNull JMenuItem menu)
	{
		KeyStrokeInfo keyStrokeInfo = getKeyStrokeInfo(menu);
		return keyStrokeInfo != null
			? MenuInfo.builder().type(MenuType.MENU_ITEM).name(menu.getName()).text(menu.getText())
				.mnemonic(menu.getMnemonic()).keyStrokeInfo(keyStrokeInfo).build()
			: MenuInfo.builder().type(MenuType.MENU_ITEM).name(menu.getName()).text(menu.getText())
				.mnemonic(menu.getMnemonic()).build();
	}

	public static MenuInfo fromJCheckBoxMenuItem(final @NonNull JCheckBoxMenuItem menu)
	{
		KeyStrokeInfo keyStrokeInfo = getKeyStrokeInfo(menu);
		return keyStrokeInfo != null
			? MenuInfo.builder().type(MenuType.CHECK_BOX_MENU_ITEM).name(menu.getName())
				.text(menu.getText()).mnemonic(menu.getMnemonic())
				.keyStrokeInfo(KeyStrokeInfo.toKeyStrokeInfo(menu.getAccelerator())).build()
			: MenuInfo.builder().type(MenuType.CHECK_BOX_MENU_ITEM).name(menu.getName())
				.text(menu.getText()).mnemonic(menu.getMnemonic()).build();
	}

	public static MenuInfo fromJRadioButtonMenuItem(final @NonNull JRadioButtonMenuItem menu)
	{
		KeyStrokeInfo keyStrokeInfo = getKeyStrokeInfo(menu);
		return keyStrokeInfo != null
			? MenuInfo.builder().type(MenuType.RADIO_BUTTON_MENU_ITEM).name(menu.getName())
				.text(menu.getText()).mnemonic(menu.getMnemonic())
				.keyStrokeInfo(KeyStrokeInfo.toKeyStrokeInfo(menu.getAccelerator())).build()
			: MenuInfo.builder().type(MenuType.RADIO_BUTTON_MENU_ITEM).name(menu.getName())
				.text(menu.getText()).mnemonic(menu.getMnemonic()).build();
	}

	public static JCheckBoxMenuItem toJCheckBoxMenuItem(MenuItemInfo menuItemInfo)
	{
		JCheckBoxMenuItem jMenuItem = new JCheckBoxMenuItem();
		setFields(menuItemInfo, jMenuItem);
		if (menuItemInfo.getKeyStrokeInfo() != null)
		{
			jMenuItem.setAccelerator(menuItemInfo.getKeyStrokeInfo().toKeyStroke());
		}
		return jMenuItem;
	}

	public static JRadioButtonMenuItem toJRadioButtonMenuItem(MenuItemInfo menuItemInfo)
	{
		JRadioButtonMenuItem jMenuItem = new JRadioButtonMenuItem();
		setFields(menuItemInfo, jMenuItem);
		if (menuItemInfo.getKeyStrokeInfo() != null)
		{
			jMenuItem.setAccelerator(menuItemInfo.getKeyStrokeInfo().toKeyStroke());
		}
		return jMenuItem;
	}

	public static JMenuItem toJMenuItem(MenuItemInfo menuItemInfo)
	{
		JMenuItem jMenuItem = new JMenuItem();
		setFields(menuItemInfo, jMenuItem);
		if (menuItemInfo.getKeyStrokeInfo() != null)
		{
			jMenuItem.setAccelerator(menuItemInfo.getKeyStrokeInfo().toKeyStroke());
		}
		return jMenuItem;
	}

	public static MenuItem toMenuItem(MenuItemInfo menuItemInfo)
	{
		MenuItem menuItem = new MenuItem();

		if (menuItemInfo.getText() != null)
		{
			menuItem.setLabel(menuItemInfo.getText());
		}
		if (menuItemInfo.getMnemonic() != null)
		{
			menuItem.setShortcut(new MenuShortcut(menuItemInfo.getMnemonic()));
		}
		if (menuItemInfo.getActionListener() != null)
		{
			menuItem.addActionListener(menuItemInfo.getActionListener());
		}
		if (menuItemInfo.getName() != null)
		{
			menuItem.setName(menuItemInfo.getName());
		}
		if (menuItemInfo.getActionCommand() != null)
		{
			menuItem.setActionCommand(menuItemInfo.getActionCommand());
		}
		return menuItem;
	}

	public static JMenu toJMenu(MenuItemInfo menuItemInfo)
	{
		JMenu jMenu = new JMenu();
		setFields(menuItemInfo, jMenu);
		return jMenu;
	}

	public static JMenuBar toJMenuBar(MenuItemInfo menuItemInfo)
	{
		JMenuBar menuBar = new JMenuBar();
		if (menuItemInfo.getName() != null)
		{
			menuBar.setName(menuItemInfo.getName());
		}
		return menuBar;
	}

	private static void setFields(MenuItemInfo menuItemInfo, JMenuItem menuItem)
	{
		if (menuItemInfo.getText() != null)
		{
			menuItem.setText(menuItemInfo.getText());
		}
		if (menuItemInfo.getMnemonic() != null)
		{
			menuItem.setMnemonic(menuItemInfo.getMnemonic());
		}
		if (menuItemInfo.getActionListener() != null)
		{
			menuItem.addActionListener(menuItemInfo.getActionListener());
		}
		if (menuItemInfo.getName() != null)
		{
			menuItem.setName(menuItemInfo.getName());
		}
		if (menuItemInfo.getActionCommand() != null)
		{
			menuItem.setActionCommand(menuItemInfo.getActionCommand());
		}
	}
}
