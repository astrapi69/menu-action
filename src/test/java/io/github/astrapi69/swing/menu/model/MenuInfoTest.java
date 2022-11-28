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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import io.github.astrapi69.swing.menu.enumtype.MenuType;
import org.junit.jupiter.api.Test;

import io.github.astrapi69.design.pattern.visitor.Visitor;
import io.github.astrapi69.id.generate.LongIdGenerator;
import io.github.astrapi69.swing.action.ExitApplicationAction;
import io.github.astrapi69.swing.action.NoAction;
import io.github.astrapi69.swing.action.ToggleFullScreenAction;
import io.github.astrapi69.swing.menu.KeyStrokeInfo;
import io.github.astrapi69.swing.menu.MenuExtensions;
import io.github.astrapi69.swing.menu.builder.JMenuItemInfo;
import io.github.astrapi69.swing.menu.enumtype.BaseMenuId;
import io.github.astrapi69.throwable.RuntimeExceptionDecorator;
import io.github.astrapi69.tree.BaseTreeNode;
import io.github.astrapi69.tree.TreeIdNode;
import io.github.astrapi69.tree.convert.BaseTreeNodeTransformer;
import io.github.astrapi69.xstream.ObjectToXmlExtensions;
import io.github.astrapi69.xstream.XmlToObjectExtensions;

public class MenuInfoTest
{

	@Test
	public void test()
	{
		MenuInfo actual;
		MenuInfo expected;
		//
		actual = MenuInfo.builder().mnemonic(MenuExtensions.toMnemonic('E'))
			.keyStrokeInfo(
				KeyStrokeInfo.builder().keyCode(KeyEvent.VK_F4).modifiers(InputEvent.ALT_DOWN_MASK)
					.keystrokeAsString("alt pressed F4").onKeyRelease(false).build())
			.text("Exit").label("Exit").actionCommand(BaseMenuId.EXIT.propertiesKey())
			.actionId(BaseMenuId.EXIT.propertiesKey()).name(BaseMenuId.EXIT.propertiesKey())
			.build();
		String xml = RuntimeExceptionDecorator.decorate(() -> ObjectToXmlExtensions.toXml(actual));
		assertNotNull(xml);
		expected = RuntimeExceptionDecorator.decorate(() -> XmlToObjectExtensions.toObject(xml));
		assertEquals(actual, expected);
	}

	@Test
	public void testWithTreeNode()
	{
		BaseTreeNode<MenuInfo, Long> fileTreeNode;
		BaseTreeNode<MenuInfo, Long> toggleFullscreenTreeNode;
		BaseTreeNode<MenuInfo, Long> exitTreeNode;
		MenuInfo fileMenuInfo;
		MenuInfo toggleFullscreenMenuInfo;
		MenuInfo exitMenuInfo;
		LongIdGenerator idGenerator;

		idGenerator = LongIdGenerator.of(0L);

		fileMenuInfo = MenuInfo.builder().mnemonic(MenuExtensions.toMnemonic('F'))
			.keyStrokeInfo(
				KeyStrokeInfo.builder().keyCode(KeyEvent.VK_F11).modifiers(InputEvent.ALT_DOWN_MASK)
					.keystrokeAsString("alt pressed F").onKeyRelease(false).build())
			.text("File").actionCommand(BaseMenuId.FILE.propertiesKey())
			.actionId(BaseMenuId.FILE.propertiesKey()).label("File")
			.name(BaseMenuId.FILE.propertiesKey()).build();
		fileTreeNode = BaseTreeNode.<MenuInfo, Long> builder().id(idGenerator.getNextId())
			.value(fileMenuInfo).build();

		toggleFullscreenMenuInfo = MenuInfo.builder().type(MenuType.MENU_ITEM)
			.mnemonic(MenuExtensions.toMnemonic('T'))
			.keyStrokeInfo(
				KeyStrokeInfo.builder().keyCode(KeyEvent.VK_F11).modifiers(InputEvent.ALT_DOWN_MASK)
					.keystrokeAsString("alt pressed F11").onKeyRelease(false).build())
			.text("Toggle Fullscreen").label("Toggle Fullscreen")
			.actionCommand(BaseMenuId.TOGGLE_FULLSCREEN.propertiesKey())
			.actionId(BaseMenuId.TOGGLE_FULLSCREEN.propertiesKey())
			.name(BaseMenuId.TOGGLE_FULLSCREEN.propertiesKey()).build();
		toggleFullscreenTreeNode = BaseTreeNode.<MenuInfo, Long> builder()
			.id(idGenerator.getNextId()).parent(fileTreeNode).value(toggleFullscreenMenuInfo)
			.leaf(true).build();

		exitMenuInfo = MenuInfo.builder().type(MenuType.MENU_ITEM)
			.mnemonic(MenuExtensions.toMnemonic('E'))
			.keyStrokeInfo(
				KeyStrokeInfo.builder().keyCode(KeyEvent.VK_F4).modifiers(InputEvent.ALT_DOWN_MASK)
					.keystrokeAsString("alt pressed F4").onKeyRelease(false).build())
			.text("Exit").label("Exit").actionCommand(BaseMenuId.EXIT.propertiesKey())
			.actionId(BaseMenuId.EXIT.propertiesKey()).name(BaseMenuId.EXIT.propertiesKey())
			.build();
		exitTreeNode = BaseTreeNode.<MenuInfo, Long> builder().id(idGenerator.getNextId())
			.leaf(true).parent(fileTreeNode).value(exitMenuInfo).build();

		fileTreeNode.addChild(toggleFullscreenTreeNode);
		fileTreeNode.addChild(exitTreeNode);

		Map<Long, TreeIdNode<MenuInfo, Long>> treeIdNodeMap = BaseTreeNodeTransformer
			.toKeyMap(fileTreeNode);


		final String xml = RuntimeExceptionDecorator
			.decorate(() -> ObjectToXmlExtensions.toXml(treeIdNodeMap));
		assertNotNull(xml);

		Map<Long, TreeIdNode<MenuInfo, Long>> treeIdNodeMap2 = RuntimeExceptionDecorator
			.decorate(() -> XmlToObjectExtensions.toObject(xml));
		assertNotNull(treeIdNodeMap2);
		// TODO create a menu from the map...
		final BaseTreeNode<MenuInfo, Long> root = BaseTreeNodeTransformer.getRoot(treeIdNodeMap2);
		assertEquals(fileTreeNode, root);
		final Collection<BaseTreeNode<MenuInfo, Long>> baseTreeNodes = root.traverse();
		assertEquals(baseTreeNodes.size(), 3);
		final Map<String, ActionListener> actionListenerMap = new HashMap<>();

		actionListenerMap.put(BaseMenuId.TOGGLE_FULLSCREEN.propertiesKey(),
			new ToggleFullScreenAction("Fullscreen", new JFrame()));
		actionListenerMap.put(BaseMenuId.EXIT.propertiesKey(), new ExitApplicationAction("Exit"));
		actionListenerMap.put(BaseMenuId.FILE.propertiesKey(), new NoAction());

		final Map<String, JMenu> menuMap = new HashMap<>();
		final Map<String, JMenuItem> menuItemMap = new HashMap<>();

		visitAccept(root, actionListenerMap, menuMap, menuItemMap);
		root.accept(new Visitor<BaseTreeNode<MenuInfo, Long>>()
		{
			@Override
			public void visit(BaseTreeNode<MenuInfo, Long> menuInfoLongBaseTreeNode)
			{
				visitAccept(menuInfoLongBaseTreeNode, actionListenerMap, menuMap, menuItemMap);
			}
		});
		root.accept(new Visitor<BaseTreeNode<MenuInfo, Long>>()
		{
			@Override
			public void visit(BaseTreeNode<MenuInfo, Long> menuInfoLongBaseTreeNode)
			{
				final MenuInfo menuInfo = menuInfoLongBaseTreeNode.getValue();
				MenuType menuType = menuInfo.getType();
				final String actionId = menuInfo.getActionId();
				if (MenuType.MENU_ITEM.equals(menuType) && menuInfoLongBaseTreeNode.hasParent())
				{
					final JMenuItem menuItem = menuItemMap.get(actionId);
					final BaseTreeNode<MenuInfo, Long> parent = menuInfoLongBaseTreeNode
						.getParent();
					if (menuMap.containsKey(parent.getValue().getActionId()))
					{
						final JMenu menu = menuMap.get(parent.getValue().getActionId());
						menu.add(menuItem);
					}
				}

			}
		});
		assertEquals(menuMap.size(), 1);
		assertEquals(menuItemMap.size(), 2);
	}

	public void visitAccept(final BaseTreeNode<MenuInfo, Long> menuInfoLongBaseTreeNode,
		final Map<String, ActionListener> actionListenerMap, final Map<String, JMenu> menuMap,
		final Map<String, JMenuItem> menuItemMap)
	{
		final MenuInfo menuInfo = menuInfoLongBaseTreeNode.getValue();
		if (actionListenerMap.containsKey(menuInfo.getActionId()))
		{
			final JMenuItemInfo jMenuItemInfo = menuInfo
				.toJMenuItemInfo(actionListenerMap.get(menuInfo.getActionId()));
			actionListenerMap.remove(menuInfo.getActionId());
			MenuType menuType = menuInfo.getType();
			final boolean item = MenuType.MENU_ITEM.equals(menuType);
			if (item)
			{
				final JMenuItem menuItem = jMenuItemInfo.toJMenuItem();
				menuItemMap.put(menuInfo.getActionId(), menuItem);
			}
			else
			{
				final JMenu menu = jMenuItemInfo.toJMenu();
				menuMap.put(menuInfo.getActionId(), menu);
			}
		}
	}
}
