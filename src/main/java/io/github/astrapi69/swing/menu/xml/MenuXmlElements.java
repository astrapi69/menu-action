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
package io.github.astrapi69.swing.menu.xml;

import java.util.Map;
import java.util.Optional;

import io.github.astrapi69.swing.menu.enumeration.MenuType;

/**
 * The class {@link MenuXmlElements} holds the element and attribute names of the menu xml format
 * and the mapping between element names and {@link MenuType} values
 */
public final class MenuXmlElements
{

	/** The root element that can hold several menu definitions */
	public static final String MENUS = "menus";
	/** The element name for a menu bar */
	public static final String MENU_BAR = "menubar";
	/** The element name for a menu */
	public static final String MENU = "menu";
	/** The element name for a menu item */
	public static final String ITEM = "item";
	/** The element name for a check box menu item */
	public static final String CHECKBOX = "checkbox";
	/** The element name for a radio button menu item */
	public static final String RADIO = "radio";
	/** The element name for a separator */
	public static final String SEPARATOR = "separator";
	/** The element name for a popup menu */
	public static final String POPUP = "popup";
	/** The element name for a tool bar */
	public static final String TOOL_BAR = "toolbar";
	/** The element name for a system tray popup menu */
	public static final String TRAY = "tray";

	/** The attribute name for the id of a menu component */
	public static final String ATTR_ID = "id";
	/** The attribute name for the text of a menu component */
	public static final String ATTR_TEXT = "text";
	/** The attribute name for the resource bundle key of the text */
	public static final String ATTR_TEXT_KEY = "textKey";
	/** The attribute name for the tool tip text */
	public static final String ATTR_TOOL_TIP = "toolTip";
	/** The attribute name for the mnemonic */
	public static final String ATTR_MNEMONIC = "mnemonic";
	/** The attribute name for the accelerator keystroke */
	public static final String ATTR_ACCELERATOR = "accelerator";
	/** The attribute name for the action id */
	public static final String ATTR_ACTION = "action";
	/** The attribute name for the action command */
	public static final String ATTR_ACTION_COMMAND = "actionCommand";
	/** The attribute name for the enabled state */
	public static final String ATTR_ENABLED = "enabled";
	/** The attribute name for the visible state */
	public static final String ATTR_VISIBLE = "visible";
	/** The attribute name for the selected state */
	public static final String ATTR_SELECTED = "selected";
	/** The attribute name for the button group */
	public static final String ATTR_GROUP = "group";
	/** The attribute name for the icon */
	public static final String ATTR_ICON = "icon";
	/** The attribute name for the anchor */
	public static final String ATTR_ANCHOR = "anchor";
	/** The attribute name for the relative menu id of the anchor */
	public static final String ATTR_RELATIVE_TO = "relativeTo";

	private static final Map<String, MenuType> ELEMENT_TO_TYPE = Map.of(MENU_BAR, MenuType.MENU_BAR,
		MENU, MenuType.MENU, ITEM, MenuType.MENU_ITEM, CHECKBOX, MenuType.CHECK_BOX_MENU_ITEM,
		RADIO, MenuType.RADIO_BUTTON_MENU_ITEM, SEPARATOR, MenuType.SEPARATOR, POPUP,
		MenuType.POPUP, TOOL_BAR, MenuType.TOOL_BAR, TRAY, MenuType.SYSTEM_TRAY);

	private MenuXmlElements()
	{
	}

	/**
	 * Resolves the {@link MenuType} from the given element name
	 *
	 * @param elementName
	 *            the element name
	 * @return an optional with the {@link MenuType} or empty if the element name is unknown
	 */
	public static Optional<MenuType> toMenuType(final String elementName)
	{
		return Optional.ofNullable(ELEMENT_TO_TYPE.get(elementName));
	}

	/**
	 * Resolves the element name from the given {@link MenuType}
	 *
	 * @param menuType
	 *            the {@link MenuType}
	 * @return an optional with the element name or empty if the type can not be represented in xml
	 */
	public static Optional<String> toElementName(final MenuType menuType)
	{
		if (menuType == null)
		{
			return Optional.empty();
		}
		return switch (menuType)
		{
			case MENU_BAR -> Optional.of(MENU_BAR);
			case MENU -> Optional.of(MENU);
			case MENU_ITEM -> Optional.of(ITEM);
			case CHECK_BOX_MENU_ITEM -> Optional.of(CHECKBOX);
			case RADIO_BUTTON_MENU_ITEM -> Optional.of(RADIO);
			case SEPARATOR -> Optional.of(SEPARATOR);
			case POPUP -> Optional.of(POPUP);
			case TOOL_BAR -> Optional.of(TOOL_BAR);
			case SYSTEM_TRAY -> Optional.of(TRAY);
			default -> Optional.empty();
		};
	}
}
