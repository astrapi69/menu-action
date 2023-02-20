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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import io.github.astrapi69.collection.list.ListExtensions;
import io.github.astrapi69.gen.tree.BaseTreeNode;
import io.github.astrapi69.gen.tree.TreeIdNode;
import io.github.astrapi69.gen.tree.convert.BaseTreeNodeTransformer;
import io.github.astrapi69.gen.tree.handler.IBaseTreeNodeHandlerExtensions;
import io.github.astrapi69.id.generate.LongIdGenerator;
import io.github.astrapi69.swing.menu.model.MenuInfo;
import io.github.astrapi69.throwable.RuntimeExceptionDecorator;
import io.github.astrapi69.xstream.ObjectToXmlExtensions;
import io.github.astrapi69.xstream.XmlToObjectExtensions;
import lombok.NonNull;

/**
 * The class {@link MenuInfoTreeNodeConverter} converts xml representations of {@link MenuInfo}
 * objects to a single root {@link BaseTreeNode} object and back
 */
public class MenuInfoTreeNodeConverter
{

	/**
	 * Factory method that creates a {@link BaseTreeNode} object from the given xml {@link String}
	 * object that represents a {@link BaseTreeNode} object of {@link MenuInfo} objects
	 *
	 * @param xml
	 *            the xml representations of {@link MenuInfo} objects
	 * @return the new created {@link BaseTreeNode} object
	 */
	public static BaseTreeNode<MenuInfo, Long> toMenuInfoTreeNode(final @NonNull String xml)
	{
		Map<Long, TreeIdNode<MenuInfo, Long>> treeIdNodeMap = RuntimeExceptionDecorator
			.decorate(() -> XmlToObjectExtensions.toObject(xml));
		return BaseTreeNodeTransformer.getRoot(treeIdNodeMap);
	}

	/**
	 * Converts the given {@link BaseTreeNode} object to a xml {@link String} object that represents
	 * a {@link BaseTreeNode} object of {@link MenuInfo} objects
	 * 
	 * @param root
	 *            the {@link BaseTreeNode} object
	 * @return the xml {@link String} object
	 */
	public static String toXml(final @NonNull BaseTreeNode<MenuInfo, Long> root)
	{
		Map<Long, TreeIdNode<MenuInfo, Long>> treeIdNodeMap = BaseTreeNodeTransformer
			.toKeyMap(root);
		return RuntimeExceptionDecorator.decorate(() -> ObjectToXmlExtensions.toXml(treeIdNodeMap));
	}

	/**
	 * Merges several xml {@link String} objects that represents individual menu items to a single
	 * {@link BaseTreeNode} object of {@link MenuInfo} objects
	 * 
	 * @param xmls
	 *            the xml {@link String} objects
	 * @return the new created {@link BaseTreeNode} object
	 */
	public static BaseTreeNode<MenuInfo, Long> mergeMenuInfoTreeNode(final @NonNull String... xmls)
	{
		List<BaseTreeNode<MenuInfo, Long>> treeNodes = toBaseTreeNodes(xmls);

		BaseTreeNode<MenuInfo, Long> root = mergeTreeNodes(treeNodes);

		List<BaseTreeNode<MenuInfo, Long>> orderedList = new ArrayList<>(root.traverse());
		orderedList.sort(new BaseTreeNodeByMenuInfoOrdinalComparator());
		LongIdGenerator idGenerator = LongIdGenerator.of(0L);
		for (BaseTreeNode<MenuInfo, Long> treeNode : orderedList)
		{
			treeNode.setId(idGenerator.getNextId());
		}
		return root;
	}

	private static List<BaseTreeNode<MenuInfo, Long>> toBaseTreeNodes(final @NonNull String[] xmls)
	{
		return Arrays.stream(xmls).map(MenuInfoTreeNodeConverter::toMenuInfoTreeNode)
			.sorted(new BaseTreeNodeByMenuInfoOrdinalComparator()).collect(Collectors.toList());
	}

	private static <T, K> BaseTreeNode<T, K> mergeTreeNodes(
		final @NonNull List<BaseTreeNode<T, K>> treeNodes)
	{
		return mergeTreeNodes(ListExtensions.removeFirstElement(treeNodes), treeNodes);
	}

	private static <T, K> BaseTreeNode<T, K> mergeTreeNodes(
		final @NonNull Optional<BaseTreeNode<T, K>> firstTreeNode,
		final @NonNull List<BaseTreeNode<T, K>> treeNodes)
	{

		BaseTreeNode<T, K> root = null;
		if (firstTreeNode.isPresent())
		{
			root = IBaseTreeNodeHandlerExtensions.mergeTreeNodes(firstTreeNode.get(), treeNodes);
		}
		return root;
	}

}
