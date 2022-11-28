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

import io.github.astrapi69.swing.menu.builder.JMenuItemInfo;
import io.github.astrapi69.swing.menu.enumtype.BaseMenuId;
import io.github.astrapi69.swing.menu.enumtype.MenuType;
import io.github.astrapi69.swing.menu.model.MenuInfo;
import io.github.astrapi69.throwable.RuntimeExceptionDecorator;
import io.github.astrapi69.tree.BaseTreeNode;
import io.github.astrapi69.tree.TreeIdNode;
import io.github.astrapi69.tree.convert.BaseTreeNodeTransformer;
import io.github.astrapi69.xstream.XmlToObjectExtensions;
import lombok.NonNull;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class MenuBarFactory
{


	public static JMenuBar buildMenuBar(final @NonNull BaseTreeNode<MenuInfo, Long> root,
		final Map<String, ActionListener> actionListenerMap)
	{
		final Map<String, JMenu> menuMap = new HashMap<>();
		final Map<String, JMenuItem> menuItemMap = new HashMap<>();
		final Map<String, JMenuBar> menuBarMap = new HashMap<>();
		visitAndAddToMap(root, actionListenerMap, menuMap, menuItemMap, menuBarMap);
		root.accept(menuInfoLongBaseTreeNode -> visitAndAddToMap(menuInfoLongBaseTreeNode,
			actionListenerMap, menuMap, menuItemMap, menuBarMap));
		root.accept(menuInfoLongBaseTreeNode -> visitAndAddToMenu(menuInfoLongBaseTreeNode, menuMap,
			menuItemMap, menuBarMap));
		return menuBarMap.get(BaseMenuId.MENU_BAR);
	}

	public static BaseTreeNode<MenuInfo, Long> buildRootTreeNode(final @NonNull String xml)
	{
		Map<Long, TreeIdNode<MenuInfo, Long>> treeIdNodeMap = RuntimeExceptionDecorator
			.decorate(() -> XmlToObjectExtensions.toObject(xml));
		return BaseTreeNodeTransformer.getRoot(treeIdNodeMap);
	}

	public static void visitAndAddToMenu(
		final @NonNull BaseTreeNode<MenuInfo, Long> menuInfoLongBaseTreeNode,
		final @NonNull Map<String, JMenu> menuMap,
		final @NonNull Map<String, JMenuItem> menuItemMap,
		final @NonNull Map<String, JMenuBar> menuBarMap)
	{
		final MenuInfo menuInfo = menuInfoLongBaseTreeNode.getValue();
		MenuType menuType = menuInfo.getType();
		final String actionId = menuInfo.getActionId();
		final BaseTreeNode<MenuInfo, Long> parent = menuInfoLongBaseTreeNode.getParent();
		switch (menuType)
		{
			case MENU_ITEM :
				final JMenuItem menuItem = menuItemMap.get(actionId);
				if (menuMap.containsKey(parent.getValue().getActionId()))
				{
					final JMenu menu = menuMap.get(parent.getValue().getActionId());
					menu.add(menuItem);
				}
				break;
			case MENU :
				final JMenu menu = menuMap.get(actionId);
				if (menuBarMap.containsKey(parent.getValue().getActionId()))
				{
					final JMenuBar menuBar = menuBarMap.get(parent.getValue().getActionId());
					menuBar.add(menu);
				}
				break;
		}
	}

	public static void visitAndAddToMap(
		final @NonNull BaseTreeNode<MenuInfo, Long> menuInfoLongBaseTreeNode,
		final @NonNull Map<String, ActionListener> actionListenerMap,
		final @NonNull Map<String, JMenu> menuMap,
		final @NonNull Map<String, JMenuItem> menuItemMap,
		final @NonNull Map<String, JMenuBar> menuBarMap)
	{
		final MenuInfo menuInfo = menuInfoLongBaseTreeNode.getValue();
		if (actionListenerMap.containsKey(menuInfo.getActionId()))
		{
			final JMenuItemInfo jMenuItemInfo = menuInfo
				.toJMenuItemInfo(actionListenerMap.get(menuInfo.getActionId()));
			actionListenerMap.remove(menuInfo.getActionId());
			MenuType menuType = menuInfo.getType();
			switch (menuType)
			{
				case MENU_BAR :
					final JMenuBar menuBar = jMenuItemInfo.toJMenuBar();
					menuBarMap.put(menuInfo.getActionId(), menuBar);
					break;
				case MENU_ITEM :
					final JMenuItem menuItem = jMenuItemInfo.toJMenuItem();
					menuItemMap.put(menuInfo.getActionId(), menuItem);
					break;
				case MENU :
					final JMenu menu = jMenuItemInfo.toJMenu();
					menuMap.put(menuInfo.getActionId(), menu);
					break;
			}
		}
	}
}
