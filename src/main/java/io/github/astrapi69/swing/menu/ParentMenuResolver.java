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
package io.github.astrapi69.swing.menu;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToolBar;
import javax.swing.MenuElement;

import lombok.NonNull;

/**
 * The class {@link ParentMenuResolver} provides methods for resolve parent and root menus. A
 * {@link JMenuItem} inside a {@link JMenu} is technically a child of the {@link JPopupMenu} of the
 * menu, so the methods follow the invoker of the popup menu to the logical parent menu
 */
public final class ParentMenuResolver
{

	private ParentMenuResolver()
	{
	}

	/**
	 * Gets an optional of the menu type from the given {@link MenuElement} object
	 *
	 * @param menuElement
	 *            The {@link MenuElement} object
	 * @return the optional of the menu type from the given {@link MenuElement} object
	 */
	public static Optional<Class<?>> getMenuElementType(final @NonNull MenuElement menuElement)
	{
		Component component = menuElement.getComponent();
		return switch (component)
		{
			case JMenu ignored -> Optional.of(JMenu.class);
			case JMenuBar ignored -> Optional.of(JMenuBar.class);
			case JCheckBoxMenuItem ignored -> Optional.of(JCheckBoxMenuItem.class);
			case JRadioButtonMenuItem ignored -> Optional.of(JRadioButtonMenuItem.class);
			case JMenuItem ignored -> Optional.of(JMenuItem.class);
			case JPopupMenu ignored -> Optional.of(JPopupMenu.class);
			case null, default -> Optional.empty();
		};
	}

	/**
	 * Gets the direct child menu elements from the given parent {@link MenuElement} object
	 *
	 * @param parent
	 *            The parent {@link MenuElement} object
	 * @return a list with the direct child menu elements from the given parent {@link MenuElement}
	 *         object
	 */
	public static List<MenuElement> getChildMenuElements(final @NonNull MenuElement parent)
	{
		Component parentMenu = parent.getComponent();
		if (parentMenu instanceof JMenuBar jMenuBar)
		{
			return Arrays.asList(jMenuBar.getSubElements());
		}
		List<MenuElement> childMenuElements = new ArrayList<>();
		for (MenuElement menuElement : getAllMenuElements(parent))
		{
			if (menuElement.getComponent()instanceof JMenuItem jMenuItem
				&& jMenuItem.getParent()instanceof JPopupMenu jPopupMenu
				&& parentMenu.equals(jPopupMenu.getInvoker())
				&& !childMenuElements.contains(menuElement))
			{
				childMenuElements.add(menuElement);
			}
		}
		return childMenuElements;
	}

	/**
	 * Gets recursive all menu elements from the given parent {@link MenuElement} object
	 *
	 * @param parent
	 *            The parent {@link MenuElement} object
	 * @return a list with all menu elements from the given parent {@link MenuElement} object
	 */
	public static List<MenuElement> getAllMenuElements(final @NonNull MenuElement parent)
	{
		return getAllMenuElements(parent, false);
	}

	/**
	 * Gets recursive all menu elements from the given parent {@link MenuElement} object
	 *
	 * @param parent
	 *            The parent {@link MenuElement} object
	 * @param withoutPopupMenu
	 *            The flag if {@link JPopupMenu} objects shall be excluded
	 * @return a list with all menu elements from the given parent {@link MenuElement} object
	 */
	public static List<MenuElement> getAllMenuElements(final @NonNull MenuElement parent,
		boolean withoutPopupMenu)
	{
		return getAllMenuElements(parent, withoutPopupMenu, true);
	}

	/**
	 * Gets all menu elements from the given parent {@link MenuElement} object
	 *
	 * @param parent
	 *            The parent {@link MenuElement} object
	 * @param withoutPopupMenu
	 *            The flag if {@link JPopupMenu} objects shall be excluded
	 * @param recursive
	 *            The flag if the menu elements shall be resolved recursive
	 * @return a list with all menu elements from the given parent {@link MenuElement} object
	 */
	public static List<MenuElement> getAllMenuElements(final @NonNull MenuElement parent,
		boolean withoutPopupMenu, boolean recursive)
	{
		List<MenuElement> menuElements = new ArrayList<>();
		for (MenuElement menuElement : parent.getSubElements())
		{
			if (!(withoutPopupMenu && menuElement instanceof JPopupMenu))
			{
				menuElements.add(menuElement);
			}
			if (recursive)
			{
				menuElements.addAll(getAllMenuElements(menuElement, withoutPopupMenu, true));
			}
		}
		return menuElements;
	}

	/**
	 * Gets the chain of the logical menu ancestors of the given {@link JMenuItem} object. The chain
	 * contains the parent menus from the nearest to the farthest and ends with the {@link JMenuBar}
	 * or {@link JToolBar} object if the menu is attached to one
	 *
	 * @param menu
	 *            The {@link JMenuItem} object
	 * @return the list with the logical menu ancestors, empty if the menu has no menu ancestor
	 */
	public static List<Container> getMenuAncestors(final @NonNull JMenuItem menu)
	{
		List<Container> ancestors = new ArrayList<>();
		Container container = menu.getParent();
		while (container != null)
		{
			if (container instanceof JPopupMenu popupMenu
				&& popupMenu.getInvoker()instanceof JMenu invoker)
			{
				container = invoker;
				ancestors.add(container);
			}
			else if (container instanceof JMenuBar || container instanceof JToolBar)
			{
				ancestors.add(container);
				break;
			}
			container = container.getParent();
		}
		return ancestors;
	}

	/**
	 * Gets an optional of the root {@link JMenu} object from the given {@link JMenuItem} object.
	 * The root menu is the top level menu that is attached to the {@link JMenuBar} or
	 * {@link JToolBar} object
	 *
	 * @param menu
	 *            The {@link JMenuItem} object
	 * @return an optional with the root menu or empty if not found
	 */
	public static Optional<Container> getRootJMenu(final @NonNull JMenuItem menu)
	{
		if (menu.getParent() == null)
		{
			return Optional.of(menu);
		}
		List<Container> ancestors = getMenuAncestors(menu);
		Container rootMenu = null;
		for (Container ancestor : ancestors)
		{
			if (ancestor instanceof JMenu)
			{
				rootMenu = ancestor;
			}
		}
		if (rootMenu != null)
		{
			return Optional.of(rootMenu);
		}
		return ancestors.isEmpty() ? Optional.empty() : Optional.of(menu);
	}

	/**
	 * Gets an optional of the root container from the given {@link JMenuItem} object. The root
	 * container is the {@link JMenuBar} or {@link JToolBar} object if attached, otherwise the top
	 * level {@link JMenu} object
	 *
	 * @param menu
	 *            The {@link JMenuItem} object
	 * @return an optional with the root container or empty if not found
	 */
	public static Optional<Container> getRoot(final @NonNull JMenuItem menu)
	{
		if (menu.getParent() == null)
		{
			return Optional.of(menu);
		}
		List<Container> ancestors = getMenuAncestors(menu);
		return ancestors.isEmpty()
			? Optional.empty()
			: Optional.of(ancestors.get(ancestors.size() - 1));
	}

	/**
	 * Gets an optional of the root container class from the given {@link JMenuItem} object
	 *
	 * @param menu
	 *            The {@link JMenuItem} object
	 * @return an optional with the root container class or empty if not found
	 */
	public static Optional<Class<?>> getRootType(final @NonNull JMenuItem menu)
	{
		if (menu.getParent() == null)
		{
			return Optional.of(menu instanceof JMenu ? JMenu.class : JMenuItem.class);
		}
		return getRoot(menu).map(ParentMenuResolver::toMenuClass);
	}

	/**
	 * Gets an optional of the root container class from the given {@link JMenu} object
	 *
	 * @param menu
	 *            The {@link JMenu} object
	 * @return an optional with the root container class or empty if not found
	 */
	public static Optional<Class<?>> getRootType(final @NonNull JMenu menu)
	{
		return getRootType((JMenuItem)menu);
	}

	/**
	 * Gets an optional of the parent container class from the given {@link JMenu} object
	 *
	 * @param menu
	 *            The {@link JMenu} object
	 * @return an optional with the parent container class or empty if not found
	 */
	public static Optional<Class<?>> getParentType(final @NonNull JMenu menu)
	{
		Container containerParent = menu.getParent();
		if (containerParent instanceof JPopupMenu popupMenu
			&& popupMenu.getInvoker() instanceof JMenu)
		{
			return Optional.of(JMenu.class);
		}
		if (containerParent instanceof JMenuBar)
		{
			return Optional.of(JMenuBar.class);
		}
		if (containerParent instanceof JToolBar)
		{
			return Optional.of(JToolBar.class);
		}
		return Optional.empty();
	}

	/**
	 * Gets an optional of the parent menu from the given {@link JMenuItem} object
	 *
	 * @param menu
	 *            The {@link JMenuItem} object
	 * @return an optional with the parent menu or empty if the parent is not a {@link JMenu} object
	 */
	public static Optional<JMenu> getParentMenu(final @NonNull JMenuItem menu)
	{
		if (menu.getParent()instanceof JPopupMenu popupMenu
			&& popupMenu.getInvoker()instanceof JMenu invoker)
		{
			return Optional.of(invoker);
		}
		return Optional.empty();
	}

	private static Class<?> toMenuClass(final Container container)
	{
		if (container instanceof JMenuBar)
		{
			return JMenuBar.class;
		}
		if (container instanceof JToolBar)
		{
			return JToolBar.class;
		}
		if (container instanceof JMenu)
		{
			return JMenu.class;
		}
		return container.getClass();
	}
}
