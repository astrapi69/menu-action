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

import java.awt.*;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.*;

import io.github.astrapi69.swing.menu.model.MenuItemInfo;
import io.github.astrapi69.swing.menu.popup.listener.PopupListener;

/**
 * A factory {@link JPopupMenuFactory} provides factory methods for create JPopupMenu objects
 */
public class JPopupMenuFactory
{


	/**
	 * Factory method for create a <code>JPopupMenu</code>.
	 *
	 * @return the new {@link JPopupMenu}.
	 */
	public static JPopupMenu newJPopupMenu()
	{
		return newJPopupMenu("");
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
	 */
	public static JPopupMenu newJPopupMenu(final Component component, final JMenuItem... items)
	{
		return newJPopupMenu("", component, items);
	}

	/**
	 * Factory method for create a <code>JPopupMenu</code> with the specified title.
	 *
	 * @param label
	 *            the string that a UI may use to display as a title for the popup menu.
	 * @return the new {@link JPopupMenu}.
	 */
	public static JPopupMenu newJPopupMenu(final String label)
	{
		return new JPopupMenu(label);
	}

	/**
	 * Factory method for create a new {@link JPopupMenu} object that will be added to the given
	 * <code>Component</code> and an array of <code>JMenuItem</code> that will be added to the popup
	 *
	 * @param label
	 *            the label
	 * @param component
	 *            the component
	 * @param items
	 *            the <code>JMenuItem</code>s
	 * @return the new {@link JPopupMenu}.
	 */
	public static JPopupMenu newJPopupMenu(final String label, final Component component,
		final JMenuItem... items)
	{
		return newJPopupMenu(component, label, items);
	}

	/**
	 * Factory method for create a new {@link JPopupMenu} object that will be added to the given
	 * <code>Component</code> and an array of <code>JMenuItem</code> that will be added to the popup
	 *
	 * @param component
	 *            the component to add the <code>JPopupMenu</code>
	 * @param label
	 *            the label from the <code>JPopupMenu</code>
	 * @param items
	 *            the <code>JMenuItem</code>s
	 * @return the new {@link JPopupMenu} object
	 */
	public static JPopupMenu newJPopupMenu(final Component component, final String label,
		final JMenuItem... items)
	{
		// Create the popup menu.
		final JPopupMenu popup = newJPopupMenu(label);
		for (final JMenuItem menuItem : items)
		{
			popup.add(menuItem);
		}
		// Add listener to the component so the popup menu can come up.
		final MouseListener popupListener = new PopupListener(popup);
		component.addMouseListener(popupListener);
		return popup;
	}

	/**
	 * Factory method for create a new {@link JPopupMenu} object that will be added to the given
	 * <code>Component</code> and an array of <code>JMenuItem</code> that will be added to the
	 * popup.
	 *
	 * @param component
	 *            the component to add the <code>JPopupMenu</code>
	 * @param label
	 *            the label from the <code>JPopupMenu</code>
	 * @param items
	 *            the <code>JMenuItem</code>s
	 * @return the new {@link JPopupMenu} object
	 */
	public static JPopupMenu newJPopupMenu(final Component component, final String label,
		final MenuItemInfo... items)
	{
		// Create the popup menu.
		final JPopupMenu popup = newJPopupMenu(label);
		for (final MenuItemInfo menuItem : items)
		{
			popup.add(menuItem.toJMenuItem());
		}
		// Add listener to the component so the popup menu can come up.
		final MouseListener popupListener = new PopupListener(popup);
		component.addMouseListener(popupListener);
		return popup;
	}

	/**
	 * Factory method for create a {@link PopupMenu} object.
	 *
	 * @param popupMenuInfos
	 *            the list with the popup menu infos
	 * @return the new {@link PopupMenu}
	 */
	public static PopupMenu newPopupMenu(final List<MenuItemInfo> popupMenuInfos)
	{
		final PopupMenu popupMenu = new PopupMenu();
		for (final MenuItemInfo menuItemBean : popupMenuInfos)
		{
			popupMenu.add(menuItemBean.toMenuItem());
		}
		return popupMenu;
	}
}
