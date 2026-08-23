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
package io.github.astrapi69.swing.menu.model.transform;

import java.awt.Component;
import java.util.IdentityHashMap;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import io.github.astrapi69.swing.menu.KeyStrokeExtensions;
import io.github.astrapi69.swing.menu.enumeration.BaseMenuId;
import io.github.astrapi69.swing.menu.enumeration.MenuType;
import io.github.astrapi69.swing.menu.model.MenuInfo;
import lombok.NonNull;

/**
 * The class {@link MenuInfoExporter} exports existing swing menu components to a {@link MenuInfo}
 * tree, for instance to migrate a programmatically built menu to the xml format with
 * {@link io.github.astrapi69.swing.menu.xml.MenuXmlWriter}. Components without a name get an id
 * that is derived from the id of the parent and the text. Action ids are not exported, so the
 * exported items use their id as action id
 */
public final class MenuInfoExporter
{

	private final Map<ButtonGroup, String> groupNames = new IdentityHashMap<>();

	private MenuInfoExporter()
	{
	}

	/**
	 * Exports the given {@link JMenuBar} object with all menus and items
	 *
	 * @param menuBar
	 *            the {@link JMenuBar} object
	 * @return the {@link MenuInfo} tree
	 */
	public static MenuInfo fromJMenuBar(final @NonNull JMenuBar menuBar)
	{
		MenuInfoExporter exporter = new MenuInfoExporter();
		MenuInfo menuBarInfo = MenuItemInfoConverter.fromJMenuBar(menuBar);
		if (Boolean.FALSE.equals(visibleOrNull(menuBar)))
		{
			menuBarInfo.setVisible(false);
		}
		for (int i = 0; i < menuBar.getMenuCount(); i++)
		{
			JMenu menu = menuBar.getMenu(i);
			if (menu != null)
			{
				menuBarInfo.addChild(exporter.fromJMenu(menu, menuBarInfo.getName()));
			}
		}
		return menuBarInfo;
	}

	/**
	 * Exports the given {@link JMenu} object with all sub menus and items
	 *
	 * @param menu
	 *            the {@link JMenu} object
	 * @return the {@link MenuInfo} tree
	 */
	public static MenuInfo fromJMenu(final @NonNull JMenu menu)
	{
		return new MenuInfoExporter().fromJMenu(menu, null);
	}

	/**
	 * Exports the given {@link JPopupMenu} object with all sub menus and items
	 *
	 * @param popupMenu
	 *            the {@link JPopupMenu} object
	 * @return the {@link MenuInfo} tree
	 */
	public static MenuInfo fromJPopupMenu(final @NonNull JPopupMenu popupMenu)
	{
		MenuInfoExporter exporter = new MenuInfoExporter();
		MenuInfo popupInfo = MenuInfo.builder().type(MenuType.POPUP)
			.name(popupMenu.getName() != null ? popupMenu.getName() : "popup")
			.text(emptyToNull(popupMenu.getLabel())).toolTip(popupMenu.getToolTipText())
			.enabled(popupMenu.isEnabled() ? null : Boolean.FALSE).build();
		for (Component component : popupMenu.getComponents())
		{
			MenuInfo child = exporter.fromComponent(component, popupInfo.getName());
			if (child != null)
			{
				popupInfo.addChild(child);
			}
		}
		return popupInfo;
	}

	/**
	 * Exports the given {@link JToolBar} object with all buttons
	 *
	 * @param toolBar
	 *            the {@link JToolBar} object
	 * @return the {@link MenuInfo} tree
	 */
	public static MenuInfo fromJToolBar(final @NonNull JToolBar toolBar)
	{
		MenuInfoExporter exporter = new MenuInfoExporter();
		MenuInfo toolBarInfo = MenuInfo.builder().type(MenuType.TOOL_BAR)
			.name(
				toolBar.getName() != null ? toolBar.getName() : BaseMenuId.TOOL_BAR.propertiesKey())
			.toolTip(toolBar.getToolTipText()).enabled(toolBar.isEnabled() ? null : Boolean.FALSE)
			.build();
		for (Component component : toolBar.getComponents())
		{
			MenuInfo child;
			if (component instanceof JToolBar.Separator || component instanceof JSeparator)
			{
				child = MenuInfo.builder().type(MenuType.SEPARATOR).build();
			}
			else if (component instanceof JToggleButton toggleButton)
			{
				child = exporter.fromButton(toggleButton, MenuType.CHECK_BOX_MENU_ITEM,
					toolBarInfo.getName());
			}
			else if (component instanceof AbstractButton button)
			{
				child = exporter.fromButton(button, MenuType.MENU_ITEM, toolBarInfo.getName());
			}
			else
			{
				continue;
			}
			toolBarInfo.addChild(child);
		}
		return toolBarInfo;
	}

	/**
	 * Exports the given menu component. Supported are {@link JMenuBar}, {@link JMenu},
	 * {@link JPopupMenu}, {@link JToolBar}, {@link JMenuItem} and its subclasses and separators
	 *
	 * @param component
	 *            the component
	 * @return the {@link MenuInfo} tree or null if the component is not a menu component
	 */
	public static MenuInfo fromComponent(final @NonNull Component component)
	{
		return switch (component)
		{
			case JMenuBar menuBar -> fromJMenuBar(menuBar);
			case JToolBar toolBar -> fromJToolBar(toolBar);
			case JPopupMenu popupMenu -> fromJPopupMenu(popupMenu);
			default -> new MenuInfoExporter().fromComponent(component, null);
		};
	}

	private MenuInfo fromComponent(final Component component, final String parentName)
	{
		return switch (component)
		{
			case JMenu menu -> fromJMenu(menu, parentName);
			case JCheckBoxMenuItem item -> fromButton(item, MenuType.CHECK_BOX_MENU_ITEM,
				parentName);
			case JRadioButtonMenuItem item -> fromButton(item, MenuType.RADIO_BUTTON_MENU_ITEM,
				parentName);
			case JMenuItem item -> fromButton(item, MenuType.MENU_ITEM, parentName);
			case JSeparator ignored -> MenuInfo.builder().type(MenuType.SEPARATOR).build();
			case null, default -> null;
		};
	}

	private MenuInfo fromJMenu(final JMenu menu, final String parentName)
	{
		MenuInfo menuInfo = fromButton(menu, MenuType.MENU, parentName);
		for (Component component : menu.getMenuComponents())
		{
			MenuInfo child = fromComponent(component, menuInfo.getName());
			if (child != null)
			{
				menuInfo.addChild(child);
			}
		}
		return menuInfo;
	}

	private MenuInfo fromButton(final AbstractButton button, final MenuType type,
		final String parentName)
	{
		String text = emptyToNull(button.getText());
		String name = button.getName() != null ? button.getName() : generateName(parentName, text);
		String actionCommand = button.getActionCommand();
		MenuInfo.MenuInfoBuilder builder = MenuInfo.builder().type(type).name(name).text(text)
			.toolTip(button.getToolTipText())
			.mnemonic(button.getMnemonic() != 0 ? button.getMnemonic() : null)
			.actionCommand(
				actionCommand != null && !actionCommand.equals(text) ? actionCommand : null)
			.enabled(button.isEnabled() ? null : Boolean.FALSE)
			.visible(button.isVisible() ? null : Boolean.FALSE).icon(iconPath(button.getIcon()));
		if (type == MenuType.CHECK_BOX_MENU_ITEM || type == MenuType.RADIO_BUTTON_MENU_ITEM)
		{
			builder.selected(button.isSelected() ? Boolean.TRUE : null).group(groupName(button));
		}
		if (button instanceof JMenuItem menuItem && !(button instanceof JMenu)
			&& menuItem.getAccelerator() != null)
		{
			builder.keyStrokeInfo(KeyStrokeExtensions.toKeyStrokeInfo(menuItem.getAccelerator()));
		}
		return builder.build();
	}

	private String groupName(final AbstractButton button)
	{
		ButtonModel model = button.getModel();
		if (model instanceof DefaultButtonModel defaultModel && defaultModel.getGroup() != null)
		{
			return groupNames.computeIfAbsent(defaultModel.getGroup(),
				group -> "group" + (groupNames.size() + 1));
		}
		return null;
	}

	private static String generateName(final String parentName, final String text)
	{
		String slug = text != null
			? text.toLowerCase().replaceAll("[^a-z0-9]+", ".").replaceAll("^\\.|\\.$", "")
			: "item";
		if (slug.isEmpty())
		{
			slug = "item";
		}
		return parentName != null ? parentName + "." + slug : slug;
	}

	private static String iconPath(final Icon icon)
	{
		if (icon instanceof ImageIcon imageIcon && imageIcon.getDescription() != null
			&& !imageIcon.getDescription().isBlank())
		{
			return imageIcon.getDescription();
		}
		return null;
	}

	private static String emptyToNull(final String value)
	{
		return value == null || value.isEmpty() ? null : value;
	}

	private static Boolean visibleOrNull(final Component component)
	{
		return component.isVisible() ? null : Boolean.FALSE;
	}
}
