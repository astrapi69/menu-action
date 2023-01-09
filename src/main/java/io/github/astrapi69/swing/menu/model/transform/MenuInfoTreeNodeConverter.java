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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.github.astrapi69.collection.list.ListExtensions;
import io.github.astrapi69.gen.tree.BaseTreeNode;
import io.github.astrapi69.gen.tree.TreeIdNode;
import io.github.astrapi69.gen.tree.convert.BaseTreeNodeTransformer;
import io.github.astrapi69.gen.tree.visitor.MergeTreeNodesVisitor;
import io.github.astrapi69.id.generate.LongIdGenerator;
import io.github.astrapi69.swing.menu.model.MenuInfo;
import io.github.astrapi69.throwable.RuntimeExceptionDecorator;
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

	public static BaseTreeNode<MenuInfo, Long> mergeMenuInfoTreeNode(final @NonNull String... xmls)
	{
		List<BaseTreeNode<MenuInfo, Long>> treeNodes = new ArrayList<>();
		for (String xml : xmls)
		{
			Map<Long, TreeIdNode<MenuInfo, Long>> treeIdNodeMap = RuntimeExceptionDecorator
				.decorate(() -> XmlToObjectExtensions.toObject(xml));
			BaseTreeNode<MenuInfo, Long> root = BaseTreeNodeTransformer.getRoot(treeIdNodeMap);
			treeNodes.add(root);
		}

		BaseTreeNode<MenuInfo, Long> root = null;
		final Optional<BaseTreeNode<MenuInfo, Long>> menuInfoLongBaseTreeNode = ListExtensions
			.removeFirst(treeNodes);
		if (menuInfoLongBaseTreeNode.isPresent())
		{
			root = menuInfoLongBaseTreeNode.get();
			for (BaseTreeNode<MenuInfo, Long> treeNode : treeNodes)
			{
				MergeTreeNodesVisitor<MenuInfo, Long> visitor = new MergeTreeNodesVisitor<>(
					treeNode);
				root.accept(visitor);
			}
		}

		ReindexTreeNodeVisitor<MenuInfo, Long, BaseTreeNode<MenuInfo, Long>> reindexTreeNodeVisitor = new ReindexTreeNodeVisitor<>(
			LongIdGenerator.of(0L));
		root.accept(reindexTreeNodeVisitor);
		return root;
	}
}
