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
import java.awt.event.ActionListener;
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
import io.github.astrapi69.swing.menu.KeyStrokeExtensions;
import io.github.astrapi69.swing.menu.enumeration.BaseMenuId;
import io.github.astrapi69.swing.menu.enumeration.MenuType;
import io.github.astrapi69.swing.menu.model.KeyStrokeInfo;
import io.github.astrapi69.swing.menu.model.MenuInfo;
import io.github.astrapi69.swing.menu.model.MenuItemInfo;
import lombok.NonNull;

/**
 * The class {@link MenuItemInfoConverter} converts several menu components to {@link MenuItemInfo}
 * object and back
 */
public class MenuItemInfoConverter
{

	/**
	 * Factory method that creates a {@link MenuItemInfo} object that represents an {@link JMenuBar}
	 * object
	 *
	 * @return the new created {@link MenuItemInfo} object
	 */
	public static MenuInfo fromJMenuBar()
	{
		return MenuInfo.builder().ordinal(100).type(MenuType.MENU_BAR)
			.name(BaseMenuId.MENU_BAR.propertiesKey()).build();
	}

	/**
	 * Gets the list of {@link KeyStrokeInfo} objects from the given {@link JComponent} object
	 *
	 * @param jComponent
	 *            the {@link JComponent} object
	 * @return the list of {@link KeyStrokeInfo} objects
	 */
	@SuppressWarnings(value = "unchecked")
	public static List<KeyStrokeInfo> getKeyStrokeInfos(JComponent jComponent)
	{
		Object whenInFocusedWindow = jComponent.getClientProperty("_WhenInFocusedWindow");
		List<KeyStrokeInfo> keyStrokeInfos = new ArrayList<>();
		if (whenInFocusedWindow instanceof Hashtable)
		{
			Hashtable<KeyStroke, KeyStroke> hashtable = (Hashtable<KeyStroke, KeyStroke>)whenInFocusedWindow;
			Set<KeyStroke> keySet = hashtable.keySet();
			for (KeyStroke key : keySet)
			{
				keyStrokeInfos.add(KeyStrokeExtensions.toKeyStrokeInfo(key));
			}
		}
		return keyStrokeInfos;
	}

	/**
	 * Gets the {@link KeyStrokeInfo} object from the given {@link JComponent} object
	 * 
	 * @param jComponent
	 *            the {@link JComponent} object
	 * @return the {@link KeyStrokeInfo} object
	 */
	public static KeyStrokeInfo getKeyStrokeInfo(JComponent jComponent)
	{
		return ListExtensions.getFirst(getKeyStrokeInfos(jComponent));
	}

	/**
	 * Factory method that creates a {@link MenuItemInfo} object from the given {@link JMenu} object
	 *
	 * @param menu
	 *            the {@link JMenu} object
	 * @return the new created {@link MenuItemInfo} object
	 */
	public static MenuInfo fromJMenu(final @NonNull JMenu menu)
	{
		KeyStrokeInfo keyStrokeInfo = getKeyStrokeInfo(menu);
		return keyStrokeInfo != null
			? MenuInfo.builder().type(MenuType.MENU).name(menu.getName()).text(menu.getText())
				.mnemonic(menu.getMnemonic()).keyStrokeInfo(keyStrokeInfo).build()
			: MenuInfo.builder().type(MenuType.MENU).name(menu.getName()).text(menu.getText())
				.mnemonic(menu.getMnemonic()).build();
	}

	/**
	 * Factory method that creates a {@link MenuItemInfo} object from the given {@link JMenuItem}
	 * object
	 *
	 * @param menu
	 *            the {@link JMenuItem} object
	 * @return the new created {@link MenuItemInfo} object
	 */
	public static MenuItemInfo fromJMenuItem(final @NonNull JMenuItem menu)
	{
		KeyStrokeInfo keyStrokeInfo = getKeyStrokeInfo(menu);
		return keyStrokeInfo != null
			? MenuItemInfo.builder().type(MenuType.MENU_ITEM).name(menu.getName())
				.text(menu.getText()).mnemonic(menu.getMnemonic()).keyStrokeInfo(keyStrokeInfo)
				.build()
			: MenuItemInfo.builder().type(MenuType.MENU_ITEM).name(menu.getName())
				.text(menu.getText()).mnemonic(menu.getMnemonic()).build();
	}

	/**
	 * Factory method that creates a {@link MenuItemInfo} object from the given
	 * {@link JCheckBoxMenuItem} object
	 *
	 * @param menu
	 *            the {@link JCheckBoxMenuItem} object
	 * @return the new created {@link MenuItemInfo} object
	 */
	public static MenuItemInfo fromJCheckBoxMenuItem(final @NonNull JCheckBoxMenuItem menu)
	{
		KeyStrokeInfo keyStrokeInfo = getKeyStrokeInfo(menu);
		return keyStrokeInfo != null
			? MenuItemInfo.builder().type(MenuType.CHECK_BOX_MENU_ITEM).name(menu.getName())
				.text(menu.getText()).mnemonic(menu.getMnemonic())
				.keyStrokeInfo(KeyStrokeExtensions.toKeyStrokeInfo(menu.getAccelerator())).build()
			: MenuItemInfo.builder().type(MenuType.CHECK_BOX_MENU_ITEM).name(menu.getName())
				.text(menu.getText()).mnemonic(menu.getMnemonic()).build();
	}

	/**
	 * Factory method that creates a {@link MenuItemInfo} object from the given
	 * {@link JRadioButtonMenuItem} object
	 *
	 * @param menu
	 *            the {@link JRadioButtonMenuItem} object
	 * @return the new created {@link MenuItemInfo} object
	 */
	public static MenuItemInfo fromJRadioButtonMenuItem(final @NonNull JRadioButtonMenuItem menu)
	{
		KeyStrokeInfo keyStrokeInfo = getKeyStrokeInfo(menu);
		return keyStrokeInfo != null
			? MenuItemInfo.builder().type(MenuType.RADIO_BUTTON_MENU_ITEM).name(menu.getName())
				.text(menu.getText()).mnemonic(menu.getMnemonic())
				.keyStrokeInfo(KeyStrokeExtensions.toKeyStrokeInfo(menu.getAccelerator())).build()
			: MenuItemInfo.builder().type(MenuType.RADIO_BUTTON_MENU_ITEM).name(menu.getName())
				.text(menu.getText()).mnemonic(menu.getMnemonic()).build();
	}

	/**
	 * Factory method that creates a {@link JCheckBoxMenuItem} object from the given
	 * {@link MenuItemInfo} object
	 *
	 * @param menuItemInfo
	 *            the {@link MenuItemInfo} object
	 * @return the new created {@link JCheckBoxMenuItem} object
	 */
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

	/**
	 * Factory method that creates a {@link JRadioButtonMenuItem} object from the given
	 * {@link MenuItemInfo} object
	 *
	 * @param menuItemInfo
	 *            the {@link MenuItemInfo} object
	 * @return the new created {@link JRadioButtonMenuItem} object
	 */
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

	/**
	 * Factory method that creates a {@link JMenuItem} object from the given {@link MenuItemInfo}
	 * object
	 *
	 * @param menuItemInfo
	 *            the {@link MenuItemInfo} object
	 * @return the new created {@link JMenuItem} object
	 */
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

	/**
	 * Factory method that creates a {@link MenuItem} object from the given {@link MenuItemInfo}
	 * object
	 *
	 * @param menuItemInfo
	 *            the {@link MenuItemInfo} object
	 * @return the new created {@link MenuItem} object
	 */
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
		setFields(menuItemInfo, menuItem);
		return menuItem;
	}

	/**
	 * Factory method that creates a {@link JMenu} object from the given {@link MenuItemInfo} object
	 *
	 * @param menuItemInfo
	 *            the {@link MenuItemInfo} object
	 * @return the new created {@link JMenu} object
	 */
	public static JMenu toJMenu(MenuItemInfo menuItemInfo)
	{
		JMenu jMenu = new JMenu();
		setFields(menuItemInfo, jMenu);
		return jMenu;
	}

	/**
	 * Factory method that creates a {@link JMenuBar} object from the given {@link MenuItemInfo}
	 * object
	 *
	 * @param menuItemInfo
	 *            the {@link MenuItemInfo} object
	 * @return the new created {@link JMenuBar} object
	 */
	public static JMenuBar toJMenuBar(MenuItemInfo menuItemInfo)
	{
		JMenuBar menuBar = new JMenuBar();
		if (menuItemInfo.getName() != null)
		{
			menuBar.setName(menuItemInfo.getName());
		}
		return menuBar;
	}

	private static void setFields(MenuItemInfo menuItemInfo, JMenuItem jMenuItem)
	{
		if (menuItemInfo.getText() != null)
		{
			jMenuItem.setText(menuItemInfo.getText());
		}
		if (menuItemInfo.getMnemonic() != null)
		{
			jMenuItem.setMnemonic(menuItemInfo.getMnemonic());
		}
		if (menuItemInfo.getActionListener() != null)
		{
			jMenuItem.addActionListener(menuItemInfo.getActionListener());
		}
		if (menuItemInfo.getName() != null)
		{
			jMenuItem.setName(menuItemInfo.getName());
		}
		if (menuItemInfo.getActionCommand() != null)
		{
			jMenuItem.setActionCommand(menuItemInfo.getActionCommand());
		}
	}

	private static void setFields(MenuItemInfo menuItemInfo, MenuItem menuItem)
	{
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


	/**
	 * Factory method that creates a {@link MenuItemInfo} object from this {@link MenuItemInfo}
	 * object and the given {@link ActionListener} object
	 *
	 * @param menuInfo
	 *            the {@link MenuItemInfo} object to use
	 * @param actionListener
	 *            the {@link ActionListener} object to set
	 * @return the new created {@link MenuItemInfo} object
	 */
	public static MenuItemInfo toMenuItemInfo(MenuInfo menuInfo, ActionListener actionListener)
	{
		return MenuItemInfo.builder().actionListener(actionListener).name(menuInfo.getName())
			.text(menuInfo.getText()).mnemonic(menuInfo.getMnemonic())
			.ordinal(menuInfo.getOrdinal()).keyStrokeInfo(menuInfo.getKeyStrokeInfo())
			.type(menuInfo.getType()).anchor(menuInfo.getAnchor())
			.actionCommand(menuInfo.getActionCommand())
			.relativeToMenuId(menuInfo.getRelativeToMenuId()).build();
	}

	/**
	 * Factory method that creates a {@link MenuItemInfo} object from this {@link MenuItemInfo}
	 * object and the given {@link ActionListener} object
	 *
	 * @param menuInfo
	 *            the {@link MenuItemInfo} object to use
	 * @param actionListener
	 *            the {@link ActionListener} object to set
	 * @return the new created {@link MenuItemInfo} object
	 */
	public static MenuItemInfo toMenuItemInfo(MenuItemInfo menuInfo, ActionListener actionListener)
	{
		return MenuItemInfo.builder().actionListener(actionListener).name(menuInfo.getName())
			.text(menuInfo.getText()).mnemonic(menuInfo.getMnemonic())
			.ordinal(menuInfo.getOrdinal()).keyStrokeInfo(menuInfo.getKeyStrokeInfo())
			.type(menuInfo.getType()).anchor(menuInfo.getAnchor())
			.relativeToMenuId(menuInfo.getRelativeToMenuId()).build();
	}
}
