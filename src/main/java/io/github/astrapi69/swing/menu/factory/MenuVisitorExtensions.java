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

import io.github.astrapi69.swing.menu.enumeration.MenuType;
import io.github.astrapi69.swing.menu.model.MenuInfo;
import io.github.astrapi69.swing.menu.model.MenuItemInfo;
import io.github.astrapi69.tree.BaseTreeNode;
import lombok.NonNull;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.*;

public class MenuVisitorExtensions
{


	public static void visitAndAddToMenu(
		final @NonNull BaseTreeNode<MenuInfo, Long> menuInfoLongBaseTreeNode,
		final @NonNull Map<String, JMenu> menuMap,
		final @NonNull Map<String, JMenuItem> menuItemMap,
		final @NonNull Map<String, JMenuBar> menuBarMap)
	{
		final MenuInfo menuInfo = menuInfoLongBaseTreeNode.getValue();
		MenuType menuType = menuInfo.getType();
		final String actionId = menuInfo.getName();
		final BaseTreeNode<MenuInfo, Long> parent = menuInfoLongBaseTreeNode.getParent();
		switch (menuType) {
			case MENU_ITEM:
				final JMenuItem menuItem = menuItemMap.get(actionId);
				addToMenu(menuInfoLongBaseTreeNode, menuMap, parent, menuItem);
				break;
			case MENU:
				final JMenu menu = menuMap.get(actionId);
				if (parent != null && menuBarMap.containsKey(parent.getValue().getName())) {
					final JMenuBar menuBar = menuBarMap.get(parent.getValue().getName());
					int length = menuBar.getSubElements().length;
					if (0 < length) {
						int indexOf = getSortedList(parent).indexOf(menuInfoLongBaseTreeNode);
						menuBar.add(menu, indexOf);
					} else {
						menuBar.add(menu);
					}
				}
				addToMenu(menuInfoLongBaseTreeNode, menuMap, parent, menu);
				break;
		}
	}

	private static void addToMenu(BaseTreeNode<MenuInfo, Long> menuInfoLongBaseTreeNode,
								  Map<String, JMenu> menuMap, BaseTreeNode<MenuInfo, Long> parent, JMenuItem menuItem) {
		if (parent != null && menuMap.containsKey(parent.getValue().getName())) {
			final JMenu menu = menuMap.get(parent.getValue().getName());
			int length = menu.getSubElements().length;
			if (0 < length) {
				int indexOf = getSortedList(parent).indexOf(menuInfoLongBaseTreeNode);
				menu.add(menuItem, indexOf);
			} else {
				menu.add(menuItem);
			}
		}
	}

	private static List<BaseTreeNode<MenuInfo, Long>> getSortedList(
			BaseTreeNode<MenuInfo, Long> parent) {
		Collection<BaseTreeNode<MenuInfo, Long>> children = parent.getChildren();
		List<BaseTreeNode<MenuInfo, Long>> list = new ArrayList<>(children);
		Collections.sort(list, (o1, o2) -> o1.getId().compareTo(o2.getId()));
		return list;
	}

	public static void visitAndAddToMap(
			final @NonNull BaseTreeNode<MenuInfo, Long> menuInfoLongBaseTreeNode,
			final @NonNull Map<String, ActionListener> actionListenerMap,
			final @NonNull Map<String, JMenu> menuMap,
			final @NonNull Map<String, JMenuItem> menuItemMap,
			final @NonNull Map<String, JMenuBar> menuBarMap) {
		final MenuInfo menuInfo = menuInfoLongBaseTreeNode.getValue();
		if (actionListenerMap.containsKey(menuInfo.getName()))
		{
			final MenuItemInfo menuItemInfo = menuInfo
				.toMenuItemInfo(actionListenerMap.get(menuInfo.getName()));
			actionListenerMap.remove(menuInfo.getName());
			MenuType menuType = menuInfo.getType();
			switch (menuType)
			{
				case MENU_BAR :
					final JMenuBar menuBar = menuItemInfo.toJMenuBar();
					menuBarMap.put(menuInfo.getName(), menuBar);
					break;
				case MENU_ITEM :
					final JMenuItem menuItem = menuItemInfo.toJMenuItem();
					menuItemMap.put(menuInfo.getName(), menuItem);
					break;
				case MENU :
					final JMenu menu = menuItemInfo.toJMenu();
					menuMap.put(menuInfo.getName(), menu);
					break;
			}
		}
	}
}
