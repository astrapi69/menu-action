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
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

import io.github.astrapi69.swing.menu.enumeration.BaseMenuId;
import io.github.astrapi69.swing.menu.model.MenuInfo;
import io.github.astrapi69.tree.BaseTreeNode;
import lombok.NonNull;

/**
 * A factory {@link JMenuBarFactory} provides factory methods for create JMenuBar objects
 */
public class JMenuBarFactory
{

	/**
	 * Factory method for create a {@link JMenuBar} object
	 *
	 * @return the {@link JMenuBar} object
	 */
	public static JMenuBar newJMenuBar()
	{
		return new JMenuBar();
	}

	public static JMenuBar buildMenuBar(final @NonNull BaseTreeNode<MenuInfo, Long> root,
		final @NonNull Map<String, ActionListener> actionListenerMap)
	{
		final Map<String, JMenu> menuMap = new HashMap<>();
		final Map<String, JMenuItem> menuItemMap = new HashMap<>();
		final Map<String, JMenuBar> menuBarMap = new HashMap<>();
		MenuVisitorExtensions.visitAndAddToMap(root, actionListenerMap, menuMap, menuItemMap,
			menuBarMap);
		root.accept(menuInfoLongBaseTreeNode -> MenuVisitorExtensions.visitAndAddToMap(
			menuInfoLongBaseTreeNode, actionListenerMap, menuMap, menuItemMap, menuBarMap));
		root.accept(menuInfoLongBaseTreeNode -> MenuVisitorExtensions
			.visitAndAddToMenu(menuInfoLongBaseTreeNode, menuMap, menuItemMap, menuBarMap));
		return menuBarMap.get(BaseMenuId.MENU_BAR.propertiesKey());
	}

}
