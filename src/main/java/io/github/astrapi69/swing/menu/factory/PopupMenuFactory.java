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

import java.awt.PopupMenu;
import java.util.List;

import io.github.astrapi69.swing.menu.model.MenuItemInfo;

/**
 * A factory {@link PopupMenuFactory} provides factory methods for create PopupMenu objects
 */
public class PopupMenuFactory
{

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
