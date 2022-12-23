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

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.*;

import lombok.NonNull;

/**
 * The class {@link ParentMenuResolver} provides methods for resolve parent and root menus
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
		if (component instanceof JMenu)
		{
			return Optional.of(JMenu.class);
		}
		else if (component instanceof JMenuBar)
		{
			return Optional.of(JMenuBar.class);
		}
		else if (component instanceof JMenuItem)
		{
			return Optional.of(JMenuItem.class);
		}
		else if (component instanceof JCheckBoxMenuItem)
		{
			return Optional.of(JCheckBoxMenuItem.class);
		}
		else if (component instanceof JRadioButtonMenuItem)
		{
			return Optional.of(JRadioButtonMenuItem.class);
		}
		return Optional.of(JPopupMenu.class);
	}

	/**
	 * Gets recursive all menu elements from the given parent {@link MenuElement} object
	 *
	 * @param parent
	 *            The parent {@link MenuElement} object
	 * @return a list with all menu elements from the given parent {@link MenuElement} object
	 */
	public static List<MenuElement> getChildMenuElements(final @NonNull MenuElement parent)
	{
		Component parentMenu = parent.getComponent();
		List<MenuElement> allMenuElements = ParentMenuResolver.getAllMenuElements(parent);
		List<MenuElement> childMenuElements = new ArrayList<>();
		for (MenuElement menuElement : allMenuElements)
		{
			Component component = menuElement.getComponent();
			if (component instanceof JMenuItem)
			{
				JMenuItem jMenuItem = (JMenuItem)component;
				Container parent1 = jMenuItem.getParent();
				if (parent1 instanceof JPopupMenu)
				{
					JPopupMenu jPopupMenu = (JPopupMenu)parent1;
					Component invoker = jPopupMenu.getInvoker();
					if (invoker.equals(parentMenu))
					{
						childMenuElements.add(menuElement);
					}
				}
			}
			if (component instanceof JMenu)
			{
				JMenu jMenuItem = (JMenu)component;
				Container parent1 = jMenuItem.getParent();
				if (parent1 instanceof JPopupMenu)
				{
					JPopupMenu jPopupMenu = (JPopupMenu)parent1;
					Component invoker = jPopupMenu.getInvoker();
					if (invoker.equals(parentMenu))
					{
						childMenuElements.add(menuElement);
					}
				}
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
	 *            The flag if {@link JPopupMenu} objects shall be accepted
	 * @return a list with all menu elements from the given parent {@link MenuElement} object
	 */
	public static List<MenuElement> getAllMenuElements(final @NonNull MenuElement parent,
		boolean withoutPopupMenu)
	{
		return getAllMenuElements(parent, withoutPopupMenu, true);
	}

	/**
	 * Gets recursive all menu elements from the given parent {@link MenuElement} object
	 *
	 * @param parent
	 *            The parent {@link MenuElement} object
	 * @param withoutPopupMenu
	 *            The flag if {@link JPopupMenu} objects shall be accepted
	 * @param recursive
	 *            The flag if only the child {@link MenuElement} object will be found
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
	 * Gets an optional of the root container from the given {@link JMenuItem} object
	 *
	 * @param menu
	 *            The {@link JMenuItem} object
	 * @return an optional with the root container or empty if not found
	 */
	public static Optional<Container> getRootJMenu(final @NonNull JMenuItem menu)
	{
		Optional<Container> current = Optional.empty();
		Container previous = menu;
		Container containerParent = previous.getParent();
		boolean iterate = containerParent != null;
		if (!iterate)
		{
			current = Optional.of(menu);
		}
		while (iterate)
		{
			if (containerParent instanceof JPopupMenu)
			{
				JPopupMenu popupMenu = (JPopupMenu)containerParent;
				Component invoker = popupMenu.getInvoker();
				if (invoker instanceof JMenu)
				{
					containerParent = (JMenu)invoker;
				}
			}
			else if (containerParent instanceof JMenuBar)
			{
				current = Optional.of(previous);
				break;
			}
			else if (containerParent instanceof JToolBar)
			{
				current = Optional.of(previous);
				break;
			}
			if (containerParent.getParent() != null)
			{
				previous = containerParent;
				containerParent = containerParent.getParent();
			}
			else
			{
				if (containerParent instanceof JMenu)
				{
					current = Optional.of(containerParent);
				}
				iterate = false;
			}
		}
		return current;
	}

	/**
	 * Gets an optional of the root container from the given {@link JMenuItem} object
	 *
	 * @param menu
	 *            The {@link JMenuItem} object
	 * @return an optional with the root container or empty if not found
	 */
	public static Optional<Container> getRoot(final @NonNull JMenuItem menu)
	{
		Optional<Container> current = Optional.empty();
		Container containerParent = menu.getParent();
		boolean iterate = containerParent != null;
		if (!iterate)
		{
			current = Optional.of(menu);
		}
		while (iterate)
		{
			if (containerParent instanceof JPopupMenu)
			{
				JPopupMenu popupMenu = (JPopupMenu)containerParent;
				Component invoker = popupMenu.getInvoker();
				if (invoker instanceof JMenu)
				{
					containerParent = (JMenu)invoker;
					current = Optional.of(containerParent);
				}
			}
			else if (containerParent instanceof JMenuBar)
			{
				current = Optional.of(containerParent);
				break;
			}
			else if (containerParent instanceof JToolBar)
			{
				current = Optional.of(containerParent);
				break;
			}
			if (containerParent.getParent() != null)
			{
				containerParent = containerParent.getParent();
			}
			else
			{
				iterate = false;
			}
		}
		return current;
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
		Container containerParent = menu.getParent();
		boolean iterate = containerParent != null;
		if (!iterate)
		{
			return Optional.of(JMenuItem.class);
		}
		return getCurrentRootType(containerParent);
	}

	private static Optional<Class<?>> getCurrentRootType(Container containerParent)
	{
		Optional<Class<?>> current = Optional.empty();
		boolean iterate = true;
		while (iterate)
		{
			if (containerParent instanceof JPopupMenu)
			{
				JPopupMenu popupMenu = (JPopupMenu)containerParent;
				Component invoker = popupMenu.getInvoker();
				if (invoker instanceof JMenu)
				{
					containerParent = (JMenu)invoker;
					current = Optional.of(JMenu.class);
				}
			}
			else if (containerParent instanceof JMenuBar)
			{
				current = Optional.of(JMenuBar.class);
				break;
			}
			else if (containerParent instanceof JToolBar)
			{
				current = Optional.of(JToolBar.class);
				break;
			}
			if (containerParent.getParent() != null)
			{
				containerParent = containerParent.getParent();
			}
			else
			{
				iterate = false;
			}
		}
		return current;
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
		Container containerParent = menu.getParent();
		boolean hasNoParent = containerParent == null;
		if (hasNoParent)
		{
			return Optional.of(JMenu.class);
		}
		return getCurrentRootType(containerParent);
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
		if (containerParent instanceof JPopupMenu)
		{
			JPopupMenu popupMenu = (JPopupMenu)containerParent;
			Component invoker = popupMenu.getInvoker();
			if (invoker instanceof JMenu)
			{
				return Optional.of(JMenu.class);
			}
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
	 * Gets an optional of the parent object from the given {@link JMenu} object
	 *
	 * @param menu
	 *            The {@link JMenu} object
	 * @return an optional with the parent object or empty if the parent is not a {@link JMenu}
	 *         object
	 */
	public static Optional<JMenu> getParentMenu(final @NonNull JMenuItem menu)
	{
		Container containerParent = menu.getParent();
		if (containerParent instanceof JPopupMenu)
		{
			JPopupMenu popupMenu = (JPopupMenu)containerParent;
			Component invoker = popupMenu.getInvoker();
			if (invoker instanceof JMenu)
			{
				return Optional.of((JMenu)invoker);
			}
		}
		return Optional.empty();
	}

}
