/**
 * The MIT License
 *
 * Copyright (C) 2026 Asterios Raptis
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
package io.github.astrapi69.swing.menu.build;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import io.github.astrapi69.swing.menu.enumeration.Anchor;
import io.github.astrapi69.swing.menu.model.MenuInfo;
import lombok.NonNull;

/**
 * The class {@link MenuInfoExtensions} provides utility methods for {@link MenuInfo} trees like
 * search, flatten, ordering by anchors and merging of menu contributions
 */
public final class MenuInfoExtensions
{

	private MenuInfoExtensions()
	{
	}

	/**
	 * Finds the {@link MenuInfo} object with the given name in the given tree
	 *
	 * @param root
	 *            the root of the tree
	 * @param name
	 *            the name to search
	 * @return an optional with the found {@link MenuInfo} object or empty if not found
	 */
	public static Optional<MenuInfo> find(final @NonNull MenuInfo root, final String name)
	{
		if (name == null)
		{
			return Optional.empty();
		}
		if (name.equals(root.getName()))
		{
			return Optional.of(root);
		}
		if (root.hasChildren())
		{
			for (MenuInfo child : root.getChildren())
			{
				Optional<MenuInfo> found = find(child, name);
				if (found.isPresent())
				{
					return found;
				}
			}
		}
		return Optional.empty();
	}

	/**
	 * Flattens the given tree to a list in depth first order, the root is the first element
	 *
	 * @param root
	 *            the root of the tree
	 * @return the list with all {@link MenuInfo} objects of the tree
	 */
	public static List<MenuInfo> flatten(final @NonNull MenuInfo root)
	{
		List<MenuInfo> result = new ArrayList<>();
		collect(root, result);
		return result;
	}

	private static void collect(final MenuInfo menuInfo, final List<MenuInfo> result)
	{
		result.add(menuInfo);
		if (menuInfo.hasChildren())
		{
			for (MenuInfo child : menuInfo.getChildren())
			{
				collect(child, result);
			}
		}
	}

	/**
	 * Orders the given children by their anchors. Children without anchor or with the anchor
	 * {@link Anchor#LAST} keep their order, children with the anchor {@link Anchor#FIRST} are moved
	 * to the front and children with the anchor {@link Anchor#BEFORE} or {@link Anchor#AFTER} are
	 * placed relative to the child with the name of their relative menu id. Relative children may
	 * reference other relative children in any order; the placement is repeated until all
	 * references are resolved. A child whose relative child does not exist or is part of a cycle is
	 * appended
	 *
	 * @param children
	 *            the children to order
	 * @return the new ordered list
	 */
	public static List<MenuInfo> orderByAnchor(final @NonNull List<MenuInfo> children)
	{
		List<MenuInfo> ordered = new ArrayList<>();
		List<MenuInfo> pending = new ArrayList<>();
		int firstIndex = 0;
		for (MenuInfo child : children)
		{
			Anchor anchor = child.getAnchor();
			if (anchor == Anchor.FIRST)
			{
				ordered.add(firstIndex++, child);
			}
			else if ((anchor == Anchor.BEFORE || anchor == Anchor.AFTER)
				&& child.getRelativeToMenuId() != null)
			{
				pending.add(child);
			}
			else
			{
				ordered.add(child);
			}
		}
		while (!pending.isEmpty())
		{
			List<MenuInfo> unresolved = new ArrayList<>();
			for (MenuInfo child : pending)
			{
				int index = indexOf(ordered, child.getRelativeToMenuId());
				if (index < 0)
				{
					unresolved.add(child);
				}
				else
				{
					ordered.add(child.getAnchor() == Anchor.BEFORE ? index : index + 1, child);
				}
			}
			if (unresolved.size() == pending.size())
			{
				ordered.addAll(unresolved);
				break;
			}
			pending = unresolved;
		}
		return ordered;
	}

	/**
	 * Calculates the index where the given child has to be inserted in the given list of siblings
	 * according to its anchor. {@link Anchor#FIRST} gives 0, {@link Anchor#BEFORE} and
	 * {@link Anchor#AFTER} the index relative to the sibling with the relative menu id and
	 * everything else or an unknown sibling the size of the list
	 *
	 * @param siblingNames
	 *            the names of the existing siblings in their order
	 * @param child
	 *            the child to insert
	 * @return the index to insert the child
	 */
	public static int insertIndex(final @NonNull List<String> siblingNames,
		final @NonNull MenuInfo child)
	{
		Anchor anchor = child.getAnchor();
		if (anchor == Anchor.FIRST)
		{
			return 0;
		}
		if ((anchor == Anchor.BEFORE || anchor == Anchor.AFTER)
			&& child.getRelativeToMenuId() != null)
		{
			int index = siblingNames.indexOf(child.getRelativeToMenuId());
			if (index >= 0)
			{
				return anchor == Anchor.BEFORE ? index : index + 1;
			}
		}
		return siblingNames.size();
	}

	/**
	 * Gets the index of the child with the given name in the given list
	 *
	 * @param children
	 *            the children
	 * @param name
	 *            the name to search
	 * @return the index or -1 if not found
	 */
	public static int indexOf(final @NonNull List<MenuInfo> children, final String name)
	{
		for (int i = 0; i < children.size(); i++)
		{
			if (Objects.equals(children.get(i).getName(), name))
			{
				return i;
			}
		}
		return -1;
	}

	/**
	 * Merges the given contribution tree into the given base tree. This is intended for plugins
	 * that contribute menu items to an existing menu. The contribution tree mirrors the path to the
	 * target: every child of the contribution that exists in the base by name is merged
	 * recursively, every child that does not exist in the base is added to the base parent
	 * respecting its anchor. Existing nodes keep their attributes. The base tree is modified
	 *
	 * @param base
	 *            the base tree
	 * @param contribution
	 *            the contribution tree
	 * @return the base tree
	 */
	public static MenuInfo merge(final @NonNull MenuInfo base, final @NonNull MenuInfo contribution)
	{
		if (contribution.hasChildren())
		{
			for (MenuInfo contributed : contribution.getChildren())
			{
				int index = base.hasChildren()
					? indexOf(base.getChildren(), contributed.getName())
					: -1;
				if (index >= 0 && contributed.getName() != null)
				{
					merge(base.getChildren().get(index), contributed);
				}
				else
				{
					base.addChild(contributed);
				}
			}
			base.setChildren(orderByAnchor(base.getChildren()));
		}
		return base;
	}
}
