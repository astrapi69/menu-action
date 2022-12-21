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

import java.util.Map;

import javax.swing.*;

import io.github.astrapi69.swing.menu.enumeration.BaseMenuId;
import io.github.astrapi69.swing.menu.enumeration.MenuType;
import io.github.astrapi69.swing.menu.model.KeyStrokeInfo;
import io.github.astrapi69.swing.menu.model.MenuInfo;
import io.github.astrapi69.throwable.RuntimeExceptionDecorator;
import io.github.astrapi69.tree.BaseTreeNode;
import io.github.astrapi69.tree.TreeIdNode;
import io.github.astrapi69.tree.convert.BaseTreeNodeTransformer;
import io.github.astrapi69.xstream.ObjectToXmlExtensions;
import io.github.astrapi69.xstream.XmlToObjectExtensions;
import lombok.NonNull;

public class MenuInfoTreeNodeConverter
{

	public static BaseTreeNode<MenuInfo, Long> toMenuInfoTreeNode(final @NonNull String xml)
	{
		Map<Long, TreeIdNode<MenuInfo, Long>> treeIdNodeMap = RuntimeExceptionDecorator
			.decorate(() -> XmlToObjectExtensions.toObject(xml));
		return BaseTreeNodeTransformer.getRoot(treeIdNodeMap);
	}

	public static String toXml(final @NonNull BaseTreeNode<MenuInfo, Long> root)
	{
		Map<Long, TreeIdNode<MenuInfo, Long>> treeIdNodeMap = BaseTreeNodeTransformer
			.toKeyMap(root);
		final String xml = RuntimeExceptionDecorator
			.decorate(() -> ObjectToXmlExtensions.toXml(treeIdNodeMap));
		return xml;
	}

	public static MenuInfo fromJMenuBar()
	{
		return MenuInfo.builder().type(MenuType.MENU_BAR).name(BaseMenuId.MENU_BAR.propertiesKey())
			.build();
	}

	public static MenuInfo fromJMenu(final @NonNull JMenu menu)
	{
		return menu.getAccelerator() != null
			? MenuInfo.builder().type(MenuType.MENU).name(menu.getName()).text(menu.getText())
				.mnemonic(menu.getMnemonic())
				.keyStrokeInfo(KeyStrokeInfo.toKeyStrokeInfo(menu.getAccelerator())).build()
			: MenuInfo.builder().type(MenuType.MENU).name(menu.getName()).text(menu.getText())
				.mnemonic(menu.getMnemonic()).build();
	}

	public static MenuInfo fromJMenuItem(final @NonNull JMenuItem menu)
	{
		return MenuInfo.builder().type(MenuType.MENU_ITEM).name(menu.getName()).text(menu.getText())
			.mnemonic(menu.getMnemonic())
			.keyStrokeInfo(KeyStrokeInfo.toKeyStrokeInfo(menu.getAccelerator())).build();
	}
}
