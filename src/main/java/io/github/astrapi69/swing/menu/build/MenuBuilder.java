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

import java.awt.CheckboxMenuItem;
import java.awt.Component;
import java.awt.Container;
import java.awt.Menu;
import java.awt.MenuComponent;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Function;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import io.github.astrapi69.swing.menu.MenuExtensions;
import io.github.astrapi69.swing.menu.ParentMenuResolver;
import io.github.astrapi69.swing.menu.enumeration.Anchor;
import io.github.astrapi69.swing.menu.enumeration.MenuType;
import io.github.astrapi69.swing.menu.model.MenuInfo;
import io.github.astrapi69.swing.menu.model.MenuItemInfo;
import io.github.astrapi69.swing.menu.model.transform.MenuItemInfoConverter;
import lombok.Getter;
import lombok.NonNull;

/**
 * The class {@link MenuBuilder} builds swing menu components from a {@link MenuInfo} tree. The
 * actions of the menu items are resolved from an {@link ActionRegistry} and optional further
 * {@link ActionResolver} objects, texts can be resolved from a resource bundle and icons from a
 * custom icon resolver. All built components are registered by the name of their {@link MenuInfo}
 * and can be retrieved with {@link #getComponent(String)}
 *
 * <pre>
 * MenuInfo menuBarInfo = MenuXmlReader.readResource("menubar.xml");
 * ActionRegistry actions = ActionRegistry.empty().register("exit", e -&gt; System.exit(0));
 * MenuBuilder menuBuilder = new MenuBuilder(actions);
 * JMenuBar menuBar = menuBuilder.buildMenuBar(menuBarInfo);
 * </pre>
 */
public class MenuBuilder
{

	/** The {@link ActionRegistry} object */
	@Getter
	private final ActionRegistry actions;
	/** The additional {@link ActionResolver} objects that are asked after the registry */
	private final List<ActionResolver> actionResolvers = new ArrayList<>();
	/** The policy for menu items with an unresolved action */
	@Getter
	private MissingActionPolicy missingActionPolicy = MissingActionPolicy.FAIL;
	/** The resolver for the text keys */
	private Function<String, String> textResolver;
	/** The resolver for the icons */
	private Function<String, Icon> iconResolver = MenuItemInfoConverter::resolveIcon;
	/** The built components by name */
	private final Map<String, JComponent> components = new LinkedHashMap<>();
	/** The button groups by group name */
	private final Map<String, ButtonGroup> buttonGroups = new LinkedHashMap<>();
	/** The built awt menu components by name, for system tray menus */
	private final Map<String, MenuComponent> awtComponents = new LinkedHashMap<>();

	/**
	 * Creates a new {@link MenuBuilder} object with an empty {@link ActionRegistry}
	 */
	public MenuBuilder()
	{
		this(ActionRegistry.empty());
	}

	/**
	 * Creates a new {@link MenuBuilder} object with the given {@link ActionRegistry}
	 *
	 * @param actions
	 *            the {@link ActionRegistry} object
	 */
	public MenuBuilder(final @NonNull ActionRegistry actions)
	{
		this.actions = actions;
	}

	/**
	 * Sets the policy for menu items with an unresolved action
	 *
	 * @param missingActionPolicy
	 *            the {@link MissingActionPolicy}
	 * @return this {@link MenuBuilder} object for method chaining
	 */
	public MenuBuilder withMissingActionPolicy(
		final @NonNull MissingActionPolicy missingActionPolicy)
	{
		this.missingActionPolicy = missingActionPolicy;
		return this;
	}

	/**
	 * Adds an {@link ActionResolver} that is asked if the {@link ActionRegistry} can not resolve an
	 * action id. Resolvers are asked in the order they were added
	 *
	 * @param actionResolver
	 *            the {@link ActionResolver} object
	 * @return this {@link MenuBuilder} object for method chaining
	 */
	public MenuBuilder withActionResolver(final @NonNull ActionResolver actionResolver)
	{
		actionResolvers.add(actionResolver);
		return this;
	}

	/**
	 * Sets the resolver for the text keys of the menu components
	 *
	 * @param textResolver
	 *            the resolver that maps a text key to the text
	 * @return this {@link MenuBuilder} object for method chaining
	 */
	public MenuBuilder withTextResolver(final Function<String, String> textResolver)
	{
		this.textResolver = textResolver;
		return this;
	}

	/**
	 * Sets the given {@link ResourceBundle} as resolver for the text keys of the menu components
	 *
	 * @param resourceBundle
	 *            the {@link ResourceBundle} object
	 * @return this {@link MenuBuilder} object for method chaining
	 */
	public MenuBuilder withResourceBundle(final @NonNull ResourceBundle resourceBundle)
	{
		return withTextResolver(
			key -> resourceBundle.containsKey(key) ? resourceBundle.getString(key) : null);
	}

	/**
	 * Sets the resolver for the icons of the menu components
	 *
	 * @param iconResolver
	 *            the resolver that maps an icon path to an {@link Icon} object
	 * @return this {@link MenuBuilder} object for method chaining
	 */
	public MenuBuilder withIconResolver(final @NonNull Function<String, Icon> iconResolver)
	{
		this.iconResolver = iconResolver;
		return this;
	}

	/**
	 * Gets the built component with the given name
	 *
	 * @param name
	 *            the name of the menu component
	 * @return an optional with the built component or empty if not built
	 */
	public Optional<JComponent> getComponent(final String name)
	{
		return name == null ? Optional.empty() : Optional.ofNullable(components.get(name));
	}

	/**
	 * Gets the built component with the given name and type
	 *
	 * @param <T>
	 *            the type of the component
	 * @param name
	 *            the name of the menu component
	 * @param type
	 *            the class of the component
	 * @return an optional with the built component or empty if not built or not of the type
	 */
	public <T extends JComponent> Optional<T> getComponent(final String name,
		final @NonNull Class<T> type)
	{
		return getComponent(name).filter(type::isInstance).map(type::cast);
	}

	/**
	 * Gets all built components by name
	 *
	 * @return an unmodifiable map with all built components by name
	 */
	public Map<String, JComponent> getComponents()
	{
		return Collections.unmodifiableMap(components);
	}

	/**
	 * Gets the {@link ButtonGroup} object with the given group name
	 *
	 * @param group
	 *            the group name
	 * @return an optional with the {@link ButtonGroup} object or empty if no such group was built
	 */
	public Optional<ButtonGroup> getButtonGroup(final String group)
	{
		return group == null ? Optional.empty() : Optional.ofNullable(buttonGroups.get(group));
	}

	/**
	 * Builds a {@link JMenuBar} object from the given {@link MenuInfo} tree
	 *
	 * @param menuBarInfo
	 *            the {@link MenuInfo} object of type {@link MenuType#MENU_BAR}
	 * @return the new {@link JMenuBar} object
	 */
	public JMenuBar buildMenuBar(final @NonNull MenuInfo menuBarInfo)
	{
		requireType(menuBarInfo, MenuType.MENU_BAR);
		JMenuBar menuBar = toMenuItemInfo(menuBarInfo, null).toJMenuBar();
		register(menuBarInfo, menuBar);
		for (MenuInfo child : MenuInfoExtensions.orderByAnchor(menuBarInfo.getChildren()))
		{
			if (child.getType() == MenuType.SEPARATOR)
			{
				continue;
			}
			menuBar.add(buildMenuComponent(child));
		}
		return menuBar;
	}

	/**
	 * Builds a {@link JMenu} object from the given {@link MenuInfo} tree
	 *
	 * @param menuInfo
	 *            the {@link MenuInfo} object of type {@link MenuType#MENU}
	 * @return the new {@link JMenu} object
	 */
	public JMenu buildMenu(final @NonNull MenuInfo menuInfo)
	{
		requireType(menuInfo, MenuType.MENU);
		JMenu menu = toMenuItemInfo(menuInfo, resolveAction(menuInfo, false)).toJMenu();
		register(menuInfo, menu);
		for (MenuInfo child : MenuInfoExtensions.orderByAnchor(menuInfo.getChildren()))
		{
			if (child.getType() == MenuType.SEPARATOR)
			{
				menu.addSeparator();
			}
			else
			{
				menu.add(buildMenuComponent(child));
			}
		}
		return menu;
	}

	/**
	 * Builds a {@link JPopupMenu} object from the given {@link MenuInfo} tree
	 *
	 * @param popupInfo
	 *            the {@link MenuInfo} object of type {@link MenuType#POPUP}
	 * @return the new {@link JPopupMenu} object
	 */
	public JPopupMenu buildPopupMenu(final @NonNull MenuInfo popupInfo)
	{
		requireType(popupInfo, MenuType.POPUP);
		JPopupMenu popupMenu = new JPopupMenu(
			MenuExtensions.parseMnemonic(resolveText(popupInfo)).text());
		if (popupInfo.getName() != null)
		{
			popupMenu.setName(popupInfo.getName());
		}
		if (popupInfo.getToolTip() != null)
		{
			popupMenu.setToolTipText(popupInfo.getToolTip());
		}
		if (popupInfo.getEnabled() != null)
		{
			popupMenu.setEnabled(popupInfo.getEnabled());
		}
		register(popupInfo, popupMenu);
		for (MenuInfo child : MenuInfoExtensions.orderByAnchor(popupInfo.getChildren()))
		{
			if (child.getType() == MenuType.SEPARATOR)
			{
				popupMenu.addSeparator();
			}
			else
			{
				popupMenu.add(buildMenuComponent(child));
			}
		}
		return popupMenu;
	}

	/**
	 * Builds a {@link JToolBar} object from the given {@link MenuInfo} tree. Menu items become
	 * {@link JButton} objects, check box menu items become {@link JToggleButton} objects
	 *
	 * @param toolBarInfo
	 *            the {@link MenuInfo} object of type {@link MenuType#TOOL_BAR}
	 * @return the new {@link JToolBar} object
	 */
	public JToolBar buildToolBar(final @NonNull MenuInfo toolBarInfo)
	{
		requireType(toolBarInfo, MenuType.TOOL_BAR);
		JToolBar toolBar = new JToolBar(
			MenuExtensions.parseMnemonic(resolveText(toolBarInfo)).text());
		if (toolBarInfo.getName() != null)
		{
			toolBar.setName(toolBarInfo.getName());
		}
		if (toolBarInfo.getToolTip() != null)
		{
			toolBar.setToolTipText(toolBarInfo.getToolTip());
		}
		if (toolBarInfo.getEnabled() != null)
		{
			toolBar.setEnabled(toolBarInfo.getEnabled());
		}
		register(toolBarInfo, toolBar);
		for (MenuInfo child : MenuInfoExtensions.orderByAnchor(toolBarInfo.getChildren()))
		{
			toolBar.add(buildToolBarComponent(child));
		}
		return toolBar;
	}

	/**
	 * Builds the menu component for the given {@link MenuInfo} object. Depending on the type a
	 * {@link JMenu}, {@link JMenuItem}, {@link JCheckBoxMenuItem} or {@link JRadioButtonMenuItem}
	 * is created
	 *
	 * @param menuInfo
	 *            the {@link MenuInfo} object
	 * @return the new {@link JMenuItem} object
	 */
	public JMenuItem buildMenuComponent(final @NonNull MenuInfo menuInfo)
	{
		MenuType type = menuInfo.getType() != null ? menuInfo.getType() : MenuType.MENU_ITEM;
		return switch (type)
		{
			case MENU -> buildMenu(menuInfo);
			case CHECK_BOX_MENU_ITEM ->
			{
				JCheckBoxMenuItem checkBox = toMenuItemInfo(menuInfo, resolveAction(menuInfo, true))
					.toJCheckBoxMenuItem();
				addToButtonGroup(menuInfo, checkBox);
				register(menuInfo, checkBox);
				yield checkBox;
			}
			case RADIO_BUTTON_MENU_ITEM ->
			{
				JRadioButtonMenuItem radioButton = toMenuItemInfo(menuInfo,
					resolveAction(menuInfo, true)).toJRadioButtonMenuItem();
				addToButtonGroup(menuInfo, radioButton);
				register(menuInfo, radioButton);
				yield radioButton;
			}
			case MENU_ITEM ->
			{
				JMenuItem menuItem = toMenuItemInfo(menuInfo, resolveAction(menuInfo, true))
					.toJMenuItem();
				register(menuInfo, menuItem);
				yield menuItem;
			}
			default -> throw new IllegalArgumentException("The menu type " + type + " of the menu '"
				+ menuInfo.getName() + "' can not be built as menu component");
		};
	}

	/**
	 * Builds the root component for the given {@link MenuInfo} tree depending on its type
	 *
	 * @param menuInfo
	 *            the root {@link MenuInfo} object
	 * @return the new {@link JComponent} object, a {@link JMenuBar}, {@link JMenu},
	 *         {@link JPopupMenu}, {@link JToolBar} or {@link JMenuItem}
	 */
	public JComponent build(final @NonNull MenuInfo menuInfo)
	{
		MenuType type = menuInfo.getType() != null ? menuInfo.getType() : MenuType.MENU_ITEM;
		return switch (type)
		{
			case MENU_BAR -> buildMenuBar(menuInfo);
			case POPUP -> buildPopupMenu(menuInfo);
			case TOOL_BAR -> buildToolBar(menuInfo);
			default -> buildMenuComponent(menuInfo);
		};
	}

	/**
	 * Builds an awt {@link PopupMenu} object from the given {@link MenuInfo} tree, for instance for
	 * a {@link java.awt.TrayIcon}. Menus become {@link Menu} objects, menu items become
	 * {@link MenuItem} objects, check box and radio button menu items become
	 * {@link CheckboxMenuItem} objects whose item events are delivered to the action listener as
	 * action events. The accelerator key code of an item becomes a {@link MenuShortcut}. The built
	 * awt components are available with {@link #getAwtComponent(String)}
	 *
	 * @param popupInfo
	 *            the {@link MenuInfo} object of type {@link MenuType#SYSTEM_TRAY} or
	 *            {@link MenuType#POPUP}
	 * @return the new {@link PopupMenu} object
	 */
	public PopupMenu buildAwtPopupMenu(final @NonNull MenuInfo popupInfo)
	{
		if (popupInfo.getType() != MenuType.SYSTEM_TRAY && popupInfo.getType() != MenuType.POPUP)
		{
			throw new IllegalArgumentException(
				"Expected a menu of type SYSTEM_TRAY or POPUP but the menu '" + popupInfo.getName()
					+ "' has the type " + popupInfo.getType());
		}
		PopupMenu popupMenu = new PopupMenu(
			MenuExtensions.parseMnemonic(resolveText(popupInfo)).text());
		setAwtFields(popupInfo, popupMenu);
		addAwtChildren(popupInfo, popupMenu);
		return popupMenu;
	}

	/**
	 * Gets the built awt menu component with the given name
	 *
	 * @param name
	 *            the name of the menu component
	 * @return an optional with the built {@link MenuComponent} or empty if not built
	 */
	public Optional<MenuComponent> getAwtComponent(final String name)
	{
		return name == null ? Optional.empty() : Optional.ofNullable(awtComponents.get(name));
	}

	private void addAwtChildren(final MenuInfo parentInfo, final Menu parent)
	{
		for (MenuInfo child : MenuInfoExtensions.orderByAnchor(parentInfo.getChildren()))
		{
			MenuType type = child.getType() != null ? child.getType() : MenuType.MENU_ITEM;
			switch (type)
			{
				case SEPARATOR -> parent.addSeparator();
				case MENU -> {
					Menu menu = new Menu(MenuExtensions.parseMnemonic(resolveText(child)).text());
					setAwtFields(child, menu);
					addAwtChildren(child, menu);
					parent.add(menu);
				}
				case CHECK_BOX_MENU_ITEM, RADIO_BUTTON_MENU_ITEM -> {
					CheckboxMenuItem item = new CheckboxMenuItem(
						MenuExtensions.parseMnemonic(resolveText(child)).text(),
						Boolean.TRUE.equals(child.getSelected()));
					setAwtFields(child, item);
					ActionListener actionListener = resolveAction(child, true);
					if (actionListener != null)
					{
						String actionCommand = child.getActionCommand() != null
							? child.getActionCommand()
							: child.getName();
						item.addItemListener(event -> actionListener.actionPerformed(
							new ActionEvent(item, ActionEvent.ACTION_PERFORMED, actionCommand)));
					}
					else if (missingActionPolicy == MissingActionPolicy.DISABLE)
					{
						item.setEnabled(false);
					}
					parent.add(item);
				}
				default -> {
					MenuItem item = new MenuItem(
						MenuExtensions.parseMnemonic(resolveText(child)).text());
					setAwtFields(child, item);
					ActionListener actionListener = resolveAction(child, true);
					if (actionListener != null)
					{
						item.addActionListener(actionListener);
					}
					else if (missingActionPolicy == MissingActionPolicy.DISABLE)
					{
						item.setEnabled(false);
					}
					parent.add(item);
				}
			}
		}
	}

	private void setAwtFields(final MenuInfo menuInfo, final MenuItem item)
	{
		if (menuInfo.getName() != null)
		{
			item.setName(menuInfo.getName());
			awtComponents.put(menuInfo.getName(), item);
		}
		if (menuInfo.getEnabled() != null)
		{
			item.setEnabled(menuInfo.getEnabled());
		}
		String actionCommand = menuInfo.getActionCommand() != null
			? menuInfo.getActionCommand()
			: menuInfo.getName();
		if (actionCommand != null)
		{
			item.setActionCommand(actionCommand);
		}
		if (menuInfo.getKeyStrokeInfo() != null && menuInfo.getKeyStrokeInfo().getKeyCode() != null
			&& !(item instanceof Menu))
		{
			int modifiers = menuInfo.getKeyStrokeInfo().getModifiers() != null
				? menuInfo.getKeyStrokeInfo().getModifiers()
				: 0;
			boolean shift = (modifiers & InputEvent.SHIFT_DOWN_MASK) != 0;
			item.setShortcut(new MenuShortcut(menuInfo.getKeyStrokeInfo().getKeyCode(), shift));
		}
	}

	/**
	 * Builds the tool bar component for the given {@link MenuInfo} object. Menu items become
	 * {@link JButton} objects, check box and radio button menu items become {@link JToggleButton}
	 * objects and separators become {@link JToolBar.Separator} objects
	 *
	 * @param menuInfo
	 *            the {@link MenuInfo} object
	 * @return the new {@link JComponent} object
	 */
	public JComponent buildToolBarComponent(final @NonNull MenuInfo menuInfo)
	{
		MenuType type = menuInfo.getType() != null ? menuInfo.getType() : MenuType.MENU_ITEM;
		switch (type)
		{
			case SEPARATOR :
				return new JToolBar.Separator();
			case CHECK_BOX_MENU_ITEM, RADIO_BUTTON_MENU_ITEM :
				JToggleButton toggleButton = new JToggleButton();
				MenuItemInfoConverter.setFields(
					toMenuItemInfo(menuInfo, resolveAction(menuInfo, true)), toggleButton);
				addToButtonGroup(menuInfo, toggleButton);
				register(menuInfo, toggleButton);
				return toggleButton;
			default :
				JButton button = new JButton();
				MenuItemInfoConverter
					.setFields(toMenuItemInfo(menuInfo, resolveAction(menuInfo, true)), button);
				register(menuInfo, button);
				return button;
		}
	}

	/**
	 * Builds the given {@link MenuInfo} object and inserts it into the already built parent with
	 * the given name. The position is calculated from the anchor of the child: {@link Anchor#FIRST}
	 * at the front, {@link Anchor#BEFORE} or {@link Anchor#AFTER} relative to the sibling with
	 * the relative menu id, otherwise at the end. The parent can be a {@link JMenuBar},
	 * {@link JMenu}, {@link JPopupMenu} or {@link JToolBar}. This is intended for plugins that
	 * contribute menus after the application menu was built
	 *
	 * @param parentName
	 *            the name of the built parent component
	 * @param child
	 *            the {@link MenuInfo} object to build and insert
	 * @return the new inserted {@link JComponent} object
	 */
	public JComponent insert(final @NonNull String parentName, final @NonNull MenuInfo child)
	{
		JComponent parent = getComponent(parentName)
			.orElseThrow(() -> new IllegalArgumentException("No built component with the name '"
				+ parentName + "', build the parent menu first"));
		List<String> siblingNames = new ArrayList<>();
		for (Component sibling : childComponents(parent))
		{
			siblingNames.add(sibling.getName());
		}
		int index = MenuInfoExtensions.insertIndex(siblingNames, child);
		JComponent component;
		switch (parent)
		{
			case JMenuBar menuBar ->
			{
				component = buildMenuComponent(child);
				menuBar.add(component, index);
			}
			case JMenu menu ->
			{
				if (child.getType() == MenuType.SEPARATOR)
				{
					menu.insertSeparator(index);
					component = (JComponent)menu.getMenuComponent(index);
				}
				else
				{
					component = buildMenuComponent(child);
					menu.insert((JMenuItem)component, index);
				}
			}
			case JPopupMenu popupMenu ->
			{
				component = child.getType() == MenuType.SEPARATOR
					? new JPopupMenu.Separator()
					: buildMenuComponent(child);
				popupMenu.insert(component, index);
			}
			case JToolBar toolBar ->
			{
				component = buildToolBarComponent(child);
				toolBar.add(component, index);
			}
			default -> throw new IllegalArgumentException("The component '" + parentName
				+ "' of the type " + parent.getClass().getName()
				+ " can not take menu children, expected a JMenuBar, JMenu, JPopupMenu or JToolBar");
		}
		parent.revalidate();
		parent.repaint();
		return component;
	}

	/**
	 * Removes the built component with the given name from its parent and forgets it and all its
	 * descendants in the component lookup
	 *
	 * @param name
	 *            the name of the built component
	 * @return an optional with the removed component or empty if no component with the name was
	 *         built
	 */
	public Optional<JComponent> remove(final @NonNull String name)
	{
		JComponent component = components.get(name);
		if (component == null)
		{
			return Optional.empty();
		}
		Container parent = component.getParent();
		if (parent != null)
		{
			parent.remove(component);
			parent.revalidate();
			parent.repaint();
		}
		components.entrySet().removeIf(entry -> isSameOrDescendant(entry.getValue(), component));
		return Optional.of(component);
	}

	private static List<Component> childComponents(final JComponent parent)
	{
		if (parent instanceof JMenu menu)
		{
			return List.of(menu.getMenuComponents());
		}
		return List.of(parent.getComponents());
	}

	private static boolean isSameOrDescendant(final JComponent candidate, final JComponent removed)
	{
		if (candidate == removed || SwingUtilities.isDescendingFrom(candidate, removed))
		{
			return true;
		}
		return candidate instanceof JMenuItem menuItem
			&& ParentMenuResolver.getMenuAncestors(menuItem).contains(removed);
	}

	/**
	 * Resolves the text of the given {@link MenuInfo} object. If a text resolver is set and the
	 * {@link MenuInfo} object has a text key the text is resolved from the key, otherwise the text
	 * of the {@link MenuInfo} object is returned and if that is null the text key
	 *
	 * @param menuInfo
	 *            the {@link MenuInfo} object
	 * @return the resolved text or null
	 */
	protected String resolveText(final MenuInfo menuInfo)
	{
		if (menuInfo.getTextKey() != null && textResolver != null)
		{
			String resolved = textResolver.apply(menuInfo.getTextKey());
			if (resolved != null)
			{
				return resolved;
			}
		}
		return menuInfo.getText() != null ? menuInfo.getText() : menuInfo.getTextKey();
	}

	/**
	 * Resolves the {@link ActionListener} object for the given {@link MenuInfo} object from the
	 * {@link ActionRegistry} by the action id or, if not set, by the name. If the action can not be
	 * resolved and the action is required the {@link MissingActionPolicy} is applied
	 *
	 * @param menuInfo
	 *            the {@link MenuInfo} object
	 * @param required
	 *            the flag if the action is required
	 * @return the resolved {@link ActionListener} object or null
	 */
	protected ActionListener resolveAction(final MenuInfo menuInfo, final boolean required)
	{
		String actionId = menuInfo.getActionId() != null
			? menuInfo.getActionId()
			: menuInfo.getName();
		Optional<ActionListener> actionListener = actions.find(actionId);
		for (int i = 0; actionListener.isEmpty() && i < actionResolvers.size(); i++)
		{
			actionListener = actionResolvers.get(i).resolve(actionId);
		}
		if (actionListener.isEmpty() && required && missingActionPolicy == MissingActionPolicy.FAIL)
		{
			throw new IllegalStateException("No action registered for the id '" + actionId
				+ "' of the menu '" + menuInfo.getName() + "'");
		}
		return actionListener.orElse(null);
	}

	private MenuItemInfo toMenuItemInfo(final MenuInfo menuInfo,
		final ActionListener actionListener)
	{
		Boolean enabled = menuInfo.getEnabled();
		if (actionListener == null && missingActionPolicy == MissingActionPolicy.DISABLE
			&& isItem(menuInfo))
		{
			enabled = Boolean.FALSE;
		}
		Icon icon = menuInfo.getIcon() != null && iconResolver != null
			? iconResolver.apply(menuInfo.getIcon())
			: null;
		String actionCommand = menuInfo.getActionCommand() != null
			? menuInfo.getActionCommand()
			: menuInfo.getName();
		MenuExtensions.TextWithMnemonic text = MenuExtensions.parseMnemonic(resolveText(menuInfo));
		Integer mnemonic = menuInfo.getMnemonic() != null
			? menuInfo.getMnemonic()
			: text.mnemonic();
		return MenuItemInfo.builder().name(menuInfo.getName()).text(text.text())
			.toolTip(menuInfo.getToolTip()).mnemonic(mnemonic)
			.keyStrokeInfo(menuInfo.getKeyStrokeInfo()).type(menuInfo.getType())
			.anchor(menuInfo.getAnchor()).relativeToMenuId(menuInfo.getRelativeToMenuId())
			.actionCommand(actionCommand).actionListener(actionListener).enabled(enabled)
			.visible(menuInfo.getVisible()).selected(menuInfo.getSelected()).icon(icon).build();
	}

	private static boolean isItem(final MenuInfo menuInfo)
	{
		MenuType type = menuInfo.getType();
		return type == null || type == MenuType.MENU_ITEM || type == MenuType.CHECK_BOX_MENU_ITEM
			|| type == MenuType.RADIO_BUTTON_MENU_ITEM;
	}

	private void addToButtonGroup(final MenuInfo menuInfo, final AbstractButton button)
	{
		if (menuInfo.getGroup() != null)
		{
			buttonGroups.computeIfAbsent(menuInfo.getGroup(), key -> new ButtonGroup()).add(button);
		}
	}

	private void register(final MenuInfo menuInfo, final JComponent component)
	{
		if (menuInfo.getName() != null)
		{
			components.put(menuInfo.getName(), component);
		}
	}

	private static void requireType(final MenuInfo menuInfo, final MenuType expected)
	{
		if (menuInfo.getType() != expected)
		{
			throw new IllegalArgumentException("Expected a menu of type " + expected
				+ " but the menu '" + menuInfo.getName() + "' has the type " + menuInfo.getType());
		}
	}
}
