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

import java.awt.AWTException;
import java.awt.Component;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import io.github.astrapi69.swing.menu.factory.JMenuBarFactory;
import io.github.astrapi69.swing.menu.factory.JMenuFactory;
import io.github.astrapi69.swing.menu.factory.JMenuItemFactory;
import io.github.astrapi69.swing.menu.factory.JPopupMenuFactory;
import io.github.astrapi69.swing.menu.factory.JToolBarFactory;
import io.github.astrapi69.swing.menu.model.PopupMenuInfo;
import lombok.NonNull;
import io.github.astrapi69.awt.system.SystemTrayFactory;
import io.github.astrapi69.swing.menu.model.JMenuItemInfo;

/**
 * A factory {@link MenuFactory} provides factory methods for create Menu and JToolbar objects
 *
 * @deprecated use instead the appropriate factory classes <br>
 *             <br>
 *             Note: will be removed in next major version
 */
@Deprecated
public class MenuFactory
{

	/**
	 * Factory method for create a {@link JToolBar} object
	 *
	 * @return the {@link JToolBar} object
	 * @deprecated use instead factory class <code>JToolBarFactory</code> <br>
	 *             <br>
	 *             Note: will be removed in next minor version
	 */
	@Deprecated
	public static JToolBar newJToolBar()
	{
		return JToolBarFactory.newJToolBar();
	}

	/**
	 * Factory method for create a <code>JMenu</code>.
	 *
	 * @param text
	 *            the text of the <code>JMenu</code>
	 * @return the new {@link JMenu} object
	 * @deprecated use instead factory class <code>JMenuFactory</code> <br>
	 *             <br>
	 *             Note: will be removed in next minor version
	 */
	@Deprecated
	public static JMenu newJMenu(final @NonNull String text)
	{
		return JMenuFactory.newJMenu(text);
	}

	/**
	 * Factory method for create a <code>JMenu</code>.
	 *
	 * @param menuItemInfo
	 *            the information for build a <code>JMenu</code>.
	 * @return the new {@link JMenu} object
	 * @deprecated use instead factory class <code>JMenuFactory</code> <br>
	 *             <br>
	 *             Note: will be removed in next minor version
	 */
	@Deprecated
	public static JMenu newJMenu(final @NonNull JMenuItemInfo menuItemInfo)
	{
		return JMenuFactory.newJMenu(menuItemInfo);
	}

	/**
	 * Factory method for create a <code>JMenu</code>.
	 *
	 * @param text
	 *            the text of the <code>JMenu</code>
	 * @param mnemonic
	 *            the keyboard mnemonic for the <code>JMenu</code>
	 * @return the new {@link JMenu} object
	 * @deprecated use instead factory class <code>JMenuFactory</code> <br>
	 *             <br>
	 *             Note: will be removed in next minor version
	 */
	@Deprecated
	public static JMenu newJMenu(final @NonNull String text, final int mnemonic)
	{
		return JMenuFactory.newJMenu(text, mnemonic);
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
	 * @deprecated use instead factory class <code>JMenuFactory</code> <br>
	 *             <br>
	 *             Note: will be removed in next minor version
	 */
	@Deprecated
	public static JMenu newJMenu(final @NonNull String text, final int mnemonic,
		final @NonNull ActionListener actionListener)
	{
		return JMenuFactory.newJMenu(text, mnemonic, actionListener);
	}

	/**
	 * Factory method for create a <code>JMenu</code>.
	 *
	 * @param text
	 *            the text of the <code>JMenu</code>
	 * @param mnemonic
	 *            the keyboard mnemonic for the <code>JMenu</code>
	 * @return the new {@link JMenu} object
	 * @deprecated use instead factory class <code>JMenuFactory</code> <br>
	 *             <br>
	 *             Note: will be removed in next minor version
	 */
	@Deprecated
	public static JMenu newJMenu(final @NonNull String text, final char mnemonic)
	{
		return JMenuFactory.newJMenu(text, mnemonic);
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
	 * @deprecated use instead factory class <code>JMenuFactory</code> <br>
	 *             <br>
	 *             Note: will be removed in next minor version
	 */
	@Deprecated
	public static JMenu newJMenu(final @NonNull String text, final char mnemonic,
		final @NonNull ActionListener actionListener)
	{
		return JMenuFactory.newJMenu(text, mnemonic, actionListener);
	}

	/**
	 * Factory method for create a <code>SystemTray</code> from the given {@link TrayIcon} object
	 * and the given {@link PopupMenu} object
	 *
	 * @param trayIcon
	 *            the tray icon of the <code>SystemTray</code>
	 * @param popupMenu
	 *            the tray popup menu of the <code>SystemTray</code>
	 * @return the new {@link SystemTray} object
	 * @throws AWTException
	 *             is thrown if the desktop system tray is missing
	 * @deprecated use instead factory class <code>SystemTrayFactory</code> <br>
	 *             <br>
	 *             Note: will be removed in next minor version
	 */
	@Deprecated
	public static SystemTray newSystemTray(final @NonNull TrayIcon trayIcon,
		final @NonNull PopupMenu popupMenu) throws AWTException
	{
		return SystemTrayFactory.newSystemTray(trayIcon, popupMenu);
	}

	/**
	 * Factory method for create a {@link JMenuBar} object
	 *
	 * @return the {@link JMenuBar} object
	 * @deprecated use instead factory class <code>JMenuBarFactory</code> <br>
	 *             <br>
	 *             Note: will be removed in next minor version
	 */
	@Deprecated
	public static JMenuBar newJMenuBar()
	{
		return JMenuBarFactory.newJMenuBar();
	}

	/**
	 * Factory method for create a <code>JMenuItem</code>.
	 *
	 * @param menuItemInfo
	 *            the information for build a <code>JMenuItem</code>.
	 * @return the new {@link JMenuItem}
	 * @deprecated use instead factory class <code>JMenuItemFactory</code> <br>
	 *             <br>
	 *             Note: will be removed in next minor version
	 */
	@Deprecated
	public static JMenuItem newJMenuItem(final @NonNull JMenuItemInfo menuItemInfo)
	{
		return JMenuItemFactory.newJMenuItem(menuItemInfo);
	}

	/**
	 * Factory method for create a <code>JMenuItem</code>.
	 *
	 * @param text
	 *            the text of the <code>JMenuItem</code>
	 * @return the new {@link JMenuItem}
	 * @deprecated use instead factory class <code>JMenuItemFactory</code> <br>
	 *             <br>
	 *             Note: will be removed in next minor version
	 */
	@Deprecated
	public static JMenuItem newJMenuItem(final @NonNull String text)
	{
		return JMenuItemFactory.newJMenuItem(text);
	}

	/**
	 * Factory method for create a <code>JMenuItem</code>.
	 *
	 * @param text
	 *            the text of the <code>JMenuItem</code>
	 * @return the new {@link JMenuItem}
	 * @deprecated use instead factory class <code>JMenuItemFactory</code> <br>
	 *             <br>
	 *             Note: will be removed in next minor version
	 */
	@Deprecated
	public static JMenuItem newJMenuItem(final @NonNull String text,
		final @NonNull ActionListener actionListener)
	{
		return JMenuItemFactory.newJMenuItem(text, actionListener);
	}

	/**
	 * Factory method for create a <code>JMenuItem</code>.
	 *
	 * @param text
	 *            the text of the <code>JMenuItem</code>
	 * @param mnemonic
	 *            the keyboard mnemonic for the <code>JMenuItem</code>
	 * @return the new {@link JMenuItem}
	 * @deprecated use instead factory class <code>JMenuItemFactory</code> <br>
	 *             <br>
	 *             Note: will be removed in next minor version
	 */
	@Deprecated
	public static JMenuItem newJMenuItem(final @NonNull String text, final int mnemonic)
	{
		return JMenuItemFactory.newJMenuItem(text, mnemonic);
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
	 * @deprecated use instead factory class <code>JMenuItemFactory</code> <br>
	 *             <br>
	 *             Note: will be removed in next minor version
	 */
	@Deprecated
	public static JMenuItem newJMenuItem(final @NonNull String text, final int mnemonic,
		final char accelerator)
	{
		return JMenuItemFactory.newJMenuItem(text, mnemonic, accelerator);
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
	 * @deprecated use instead factory class <code>JMenuItemFactory</code> <br>
	 *             <br>
	 *             Note: will be removed in next minor version
	 */
	@Deprecated
	public static JMenuItem newJMenuItem(final @NonNull String text, final int mnemonic,
		final char accelerator, final @NonNull ActionListener actionListener)
	{
		return JMenuItemFactory.newJMenuItem(text, mnemonic, accelerator, actionListener);
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
	 * @deprecated use instead factory class <code>JMenuItemFactory</code> <br>
	 *             <br>
	 *             Note: will be removed in next minor version
	 */
	@Deprecated
	public static JMenuItem newJMenuItem(final @NonNull String text, final int mnemonic,
		final KeyStroke keyStroke, final @NonNull ActionListener actionListener)
	{
		return JMenuItemFactory.newJMenuItem(text, mnemonic, keyStroke, actionListener);
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
	 * @deprecated use instead factory class <code>JMenuItemFactory</code> <br>
	 *             <br>
	 *             Note: will be removed in next minor version
	 */
	@Deprecated
	public static JMenuItem newJMenuItem(final @NonNull String text, final int mnemonic,
		final KeyStroke keyStroke, final ActionListener actionListener, String name)
	{
		return JMenuItemFactory.newJMenuItem(text, mnemonic, keyStroke, actionListener, name);
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
	 * @deprecated use instead factory class <code>JMenuItemFactory</code> <br>
	 *             <br>
	 *             Note: will be removed in next minor version
	 */
	@Deprecated
	public static JMenuItem newJMenuItem(final @NonNull String text, final char mnemonic,
		final char accelerator)
	{
		return JMenuItemFactory.newJMenuItem(text, mnemonic, accelerator);
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
	 * @deprecated use instead factory class <code>JMenuItemFactory</code> <br>
	 *             <br>
	 *             Note: will be removed in next minor version
	 */
	@Deprecated
	public static JMenuItem newJMenuItem(final @NonNull String text, final char mnemonic,
		final char accelerator, final @NonNull ActionListener actionListener)
	{
		return JMenuItemFactory.newJMenuItem(text, mnemonic, accelerator, actionListener);
	}

	/**
	 * Factory method for create a <code>JPopupMenu</code>.
	 *
	 * @return the new {@link JPopupMenu}.
	 * @deprecated use instead factory class <code>JPopupMenuFactory</code> <br>
	 *             <br>
	 *             Note: will be removed in next minor version
	 */
	@Deprecated
	public static JPopupMenu newJPopupMenu()
	{
		return JPopupMenuFactory.newJPopupMenu();
	}

	/**
	 * Factory method for create a <code>JPopupMenu</code> that will be add a
	 * <code>MouseListener</code> to the given <code>Component</code> and an array of
	 * <code>JMenuItem</code> that will be added to the popup.
	 *
	 * @param component
	 *            the component
	 * @param items
	 *            the <code>JMenuItem</code>s
	 * @return the new {@link JPopupMenu}.
	 * @deprecated use instead factory class <code>JPopupMenuFactory</code> <br>
	 *             <br>
	 *             Note: will be removed in next minor version
	 */
	@Deprecated
	public static JPopupMenu newJPopupMenu(final Component component, final JMenuItem... items)
	{
		return JPopupMenuFactory.newJPopupMenu(component, items);
	}

	/**
	 * Factory method for create a <code>JPopupMenu</code> with the specified title.
	 *
	 * @param label
	 *            the string that a UI may use to display as a title for the popup menu.
	 * @return the new {@link JPopupMenu}.
	 * @deprecated use instead factory class <code>JPopupMenuFactory</code> <br>
	 *             <br>
	 *             Note: will be removed in next minor version
	 */
	@Deprecated
	public static JPopupMenu newJPopupMenu(final String label)
	{
		return JPopupMenuFactory.newJPopupMenu(label);
	}

	/**
	 * Factory method for create a <code>JPopupMenu</code> that will be add a
	 * <code>MouseListener</code> to the given <code>Component</code> and an array of
	 * <code>JMenuItem</code> that will be added to the popup.
	 *
	 * @param label
	 *            the label
	 * @param component
	 *            the component
	 * @param items
	 *            the <code>JMenuItem</code>s
	 * @return the new {@link JPopupMenu}.
	 * @deprecated use instead factory class <code>JPopupMenuFactory</code> <br>
	 *             <br>
	 *             Note: will be removed in next minor version
	 */
	@Deprecated
	public static JPopupMenu newJPopupMenu(final String label, final Component component,
		final JMenuItem... items)
	{
		return JPopupMenuFactory.newJPopupMenu(label, component, items);
	}

	/**
	 * Factory method for create a {@link PopupMenu} object.
	 *
	 * @param menuItemBeans
	 *            the menu item beans
	 * @return the new {@link PopupMenu}.
	 * @deprecated use instead factory class <code>JPopupMenuFactory</code> <br>
	 *             <br>
	 *             Note: will be removed in next minor version
	 */
	@Deprecated
	public static PopupMenu newPopupMenu(final List<PopupMenuInfo> menuItemBeans)
	{
		return JPopupMenuFactory.newPopupMenu(menuItemBeans);
	}

	/**
	 * Factory method for create a {@link TrayIcon} object.
	 *
	 * @param imgFilename
	 *            the img filename
	 * @param appName
	 *            the app name
	 * @param systemTrayPopupMenu
	 *            the system tray popup menu
	 * @param actionListeners
	 *            the action listeners
	 * @return the new {@link TrayIcon}.
	 * @deprecated use instead factory class <code>SystemTrayFactory</code> <br>
	 *             <br>
	 *             Note: will be removed in next minor version
	 */
	@Deprecated
	public static TrayIcon newTrayIcon(final String imgFilename, final String appName,
		final PopupMenu systemTrayPopupMenu, final Map<String, ActionListener> actionListeners)
	{
		return SystemTrayFactory.newTrayIcon(imgFilename, appName, systemTrayPopupMenu,
			actionListeners);
	}

}
