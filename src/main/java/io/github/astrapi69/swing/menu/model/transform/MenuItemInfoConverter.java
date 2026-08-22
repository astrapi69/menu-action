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

import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

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
public final class MenuItemInfoConverter
{

	private MenuItemInfoConverter()
	{
	}

	/**
	 * Factory method that creates a {@link MenuInfo} object that represents an {@link JMenuBar}
	 * object
	 *
	 * @return the new created {@link MenuInfo} object
	 */
	public static MenuInfo fromJMenuBar()
	{
		return MenuInfo.builder().type(MenuType.MENU_BAR).name(BaseMenuId.MENU_BAR.propertiesKey())
			.build();
	}

	/**
	 * Factory method that creates a {@link MenuInfo} object from the given {@link JMenuBar} object
	 *
	 * @param menuBar
	 *            the {@link JMenuBar} object
	 * @return the new created {@link MenuInfo} object
	 */
	public static MenuInfo fromJMenuBar(final @NonNull JMenuBar menuBar)
	{
		return MenuInfo.builder().type(MenuType.MENU_BAR)
			.name(
				menuBar.getName() != null ? menuBar.getName() : BaseMenuId.MENU_BAR.propertiesKey())
			.build();
	}

	/**
	 * Factory method that creates a {@link MenuInfo} object from the given {@link JMenu} object
	 *
	 * @param menu
	 *            the {@link JMenu} object
	 * @return the new created {@link MenuInfo} object
	 */
	public static MenuInfo fromJMenu(final @NonNull JMenu menu)
	{
		return fromAbstractButton(menu, MenuType.MENU);
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
		return toMenuItemInfo(fromAbstractButton(menu, MenuType.MENU_ITEM), null);
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
		return toMenuItemInfo(fromAbstractButton(menu, MenuType.CHECK_BOX_MENU_ITEM), null);
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
		return toMenuItemInfo(fromAbstractButton(menu, MenuType.RADIO_BUTTON_MENU_ITEM), null);
	}

	private static MenuInfo fromAbstractButton(final JMenuItem menu, final MenuType type)
	{
		KeyStrokeInfo keyStrokeInfo = menu.getAccelerator() != null
			? KeyStrokeExtensions.toKeyStrokeInfo(menu.getAccelerator())
			: null;
		return MenuInfo.builder().type(type).name(menu.getName()).text(menu.getText())
			.toolTip(menu.getToolTipText()).actionCommand(menu.getActionCommand())
			.mnemonic(menu.getMnemonic()).keyStrokeInfo(keyStrokeInfo).build();
	}

	/**
	 * Factory method that creates a {@link JCheckBoxMenuItem} object from the given
	 * {@link MenuItemInfo} object
	 *
	 * @param menuItemInfo
	 *            the {@link MenuItemInfo} object
	 * @return the new created {@link JCheckBoxMenuItem} object
	 */
	public static JCheckBoxMenuItem toJCheckBoxMenuItem(final @NonNull MenuItemInfo menuItemInfo)
	{
		JCheckBoxMenuItem jMenuItem = new JCheckBoxMenuItem();
		setFields(menuItemInfo, jMenuItem);
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
	public static JRadioButtonMenuItem toJRadioButtonMenuItem(
		final @NonNull MenuItemInfo menuItemInfo)
	{
		JRadioButtonMenuItem jMenuItem = new JRadioButtonMenuItem();
		setFields(menuItemInfo, jMenuItem);
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
	public static JMenuItem toJMenuItem(final @NonNull MenuItemInfo menuItemInfo)
	{
		JMenuItem jMenuItem = new JMenuItem();
		setFields(menuItemInfo, jMenuItem);
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
	public static MenuItem toMenuItem(final @NonNull MenuItemInfo menuItemInfo)
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
		if (menuItemInfo.getEnabled() != null)
		{
			menuItem.setEnabled(menuItemInfo.getEnabled());
		}
		return menuItem;
	}

	/**
	 * Factory method that creates a {@link JMenu} object from the given {@link MenuItemInfo} object
	 *
	 * @param menuItemInfo
	 *            the {@link MenuItemInfo} object
	 * @return the new created {@link JMenu} object
	 */
	public static JMenu toJMenu(final @NonNull MenuItemInfo menuItemInfo)
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
	public static JMenuBar toJMenuBar(final @NonNull MenuItemInfo menuItemInfo)
	{
		JMenuBar menuBar = new JMenuBar();
		if (menuItemInfo.getName() != null)
		{
			menuBar.setName(menuItemInfo.getName());
		}
		if (menuItemInfo.getToolTip() != null)
		{
			menuBar.setToolTipText(menuItemInfo.getToolTip());
		}
		if (menuItemInfo.getEnabled() != null)
		{
			menuBar.setEnabled(menuItemInfo.getEnabled());
		}
		return menuBar;
	}

	/**
	 * Sets all fields from the given {@link MenuItemInfo} object to the given
	 * {@link AbstractButton} object
	 *
	 * @param menuItemInfo
	 *            the {@link MenuItemInfo} object
	 * @param button
	 *            the {@link AbstractButton} object to set the fields to
	 */
	public static void setFields(final @NonNull MenuItemInfo menuItemInfo,
		final @NonNull AbstractButton button)
	{
		if (menuItemInfo.getText() != null)
		{
			button.setText(menuItemInfo.getText());
		}
		if (menuItemInfo.getToolTip() != null)
		{
			button.setToolTipText(menuItemInfo.getToolTip());
		}
		if (menuItemInfo.getMnemonic() != null)
		{
			button.setMnemonic(menuItemInfo.getMnemonic());
		}
		if (menuItemInfo.getActionListener() != null)
		{
			button.addActionListener(menuItemInfo.getActionListener());
		}
		if (menuItemInfo.getName() != null)
		{
			button.setName(menuItemInfo.getName());
		}
		if (menuItemInfo.getActionCommand() != null)
		{
			button.setActionCommand(menuItemInfo.getActionCommand());
		}
		if (menuItemInfo.getIcon() != null)
		{
			button.setIcon(menuItemInfo.getIcon());
		}
		if (menuItemInfo.getEnabled() != null)
		{
			button.setEnabled(menuItemInfo.getEnabled());
		}
		if (menuItemInfo.getSelected() != null)
		{
			button.setSelected(menuItemInfo.getSelected());
		}
		if (button instanceof JMenuItem jMenuItem && !(button instanceof JMenu)
			&& menuItemInfo.getKeyStrokeInfo() != null)
		{
			jMenuItem.setAccelerator(menuItemInfo.getKeyStrokeInfo().toKeyStroke());
		}
	}

	/**
	 * Factory method that creates a {@link MenuItemInfo} object from the given {@link MenuInfo}
	 * object and the given {@link ActionListener} object. The icon of the {@link MenuInfo} object
	 * is resolved with {@link #resolveIcon(String)}
	 *
	 * @param menuInfo
	 *            the {@link MenuInfo} object to use
	 * @param actionListener
	 *            the {@link ActionListener} object to set
	 * @return the new created {@link MenuItemInfo} object
	 */
	public static MenuItemInfo toMenuItemInfo(final @NonNull MenuInfo menuInfo,
		final ActionListener actionListener)
	{
		return toMenuItemInfo(menuInfo, actionListener, resolveIcon(menuInfo.getIcon()));
	}

	/**
	 * Factory method that creates a {@link MenuItemInfo} object from the given {@link MenuInfo}
	 * object, the given {@link ActionListener} object and the given {@link Icon} object
	 *
	 * @param menuInfo
	 *            the {@link MenuInfo} object to use
	 * @param actionListener
	 *            the {@link ActionListener} object to set
	 * @param icon
	 *            the {@link Icon} object to set
	 * @return the new created {@link MenuItemInfo} object
	 */
	public static MenuItemInfo toMenuItemInfo(final @NonNull MenuInfo menuInfo,
		final ActionListener actionListener, final Icon icon)
	{
		return MenuItemInfo.builder().actionListener(actionListener).name(menuInfo.getName())
			.text(menuInfo.getText()).toolTip(menuInfo.getToolTip())
			.mnemonic(menuInfo.getMnemonic()).keyStrokeInfo(menuInfo.getKeyStrokeInfo())
			.type(menuInfo.getType()).anchor(menuInfo.getAnchor())
			.relativeToMenuId(menuInfo.getRelativeToMenuId())
			.actionCommand(menuInfo.getActionCommand()).enabled(menuInfo.getEnabled())
			.selected(menuInfo.getSelected()).icon(icon).build();
	}

	/**
	 * Factory method that creates a {@link MenuItemInfo} object from the given {@link MenuItemInfo}
	 * object and the given {@link ActionListener} object
	 *
	 * @param menuItemInfo
	 *            the {@link MenuItemInfo} object to use
	 * @param actionListener
	 *            the {@link ActionListener} object to set
	 * @return the new created {@link MenuItemInfo} object
	 */
	public static MenuItemInfo toMenuItemInfo(final @NonNull MenuItemInfo menuItemInfo,
		final ActionListener actionListener)
	{
		return MenuItemInfo.builder().actionListener(actionListener).name(menuItemInfo.getName())
			.text(menuItemInfo.getText()).toolTip(menuItemInfo.getToolTip())
			.mnemonic(menuItemInfo.getMnemonic()).keyStrokeInfo(menuItemInfo.getKeyStrokeInfo())
			.type(menuItemInfo.getType()).anchor(menuItemInfo.getAnchor())
			.relativeToMenuId(menuItemInfo.getRelativeToMenuId())
			.actionCommand(menuItemInfo.getActionCommand()).enabled(menuItemInfo.getEnabled())
			.selected(menuItemInfo.getSelected()).icon(menuItemInfo.getIcon()).build();
	}

	/**
	 * Resolves the given icon path to an {@link Icon} object. The path is first looked up as
	 * classpath resource and then as file. If the path is null or can not be resolved null is
	 * returned
	 *
	 * @param iconPath
	 *            the classpath resource path or the file path of the icon
	 * @return the {@link Icon} object or null if the icon can not be resolved
	 */
	public static Icon resolveIcon(final String iconPath)
	{
		if (iconPath == null || iconPath.isBlank())
		{
			return null;
		}
		String resource = iconPath.startsWith("/") ? iconPath.substring(1) : iconPath;
		URL url = Thread.currentThread().getContextClassLoader().getResource(resource);
		if (url == null)
		{
			url = MenuItemInfoConverter.class.getClassLoader().getResource(resource);
		}
		if (url != null)
		{
			return new ImageIcon(url);
		}
		File file = new File(iconPath);
		if (file.isFile())
		{
			return new ImageIcon(file.getAbsolutePath());
		}
		return null;
	}
}
