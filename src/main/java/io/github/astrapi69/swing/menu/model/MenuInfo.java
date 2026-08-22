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
package io.github.astrapi69.swing.menu.model;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import io.github.astrapi69.swing.menu.enumeration.Anchor;
import io.github.astrapi69.swing.menu.enumeration.MenuType;
import io.github.astrapi69.swing.menu.model.transform.MenuItemInfoConverter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * The class {@link MenuInfo} is the declarative description of a menu component. A {@link MenuInfo}
 * object is a node of a menu tree: a menu bar contains menus, a menu contains menu items,
 * separators and sub menus. The tree can be loaded from and written to xml with the classes
 * {@link io.github.astrapi69.swing.menu.xml.MenuXmlReader} and
 * {@link io.github.astrapi69.swing.menu.xml.MenuXmlWriter} and built to swing components with the
 * class {@link io.github.astrapi69.swing.menu.build.MenuBuilder}
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MenuInfo
{
	/**
	 * The name of this menu component. The name is the unique id of this menu component and is used
	 * as the component name
	 */
	String name;

	/**
	 * The text of this menu component. The text is used as the label of this menu component
	 */
	String text;

	/**
	 * The optional resource bundle key for the text of this menu component. If a text resolver is
	 * configured in the menu builder the text is resolved from this key
	 */
	String textKey;

	/**
	 * The optional tool tip text of this menu component
	 */
	String toolTip;

	/**
	 * The keyboard mnemonic of this menu component
	 */
	Integer mnemonic;

	/**
	 * The {@link KeyStrokeInfo} that describes the accelerator of this menu component
	 */
	KeyStrokeInfo keyStrokeInfo;

	/**
	 * The {@link MenuType} describes the type of this menu component
	 */
	MenuType type;

	/**
	 * The anchor describes where to position this menu component relative to its siblings
	 */
	Anchor anchor;

	/**
	 * If the anchor value is set to {@link Anchor#BEFORE} or {@link Anchor#AFTER} than this value
	 * is the menu id that it will be relatively placed to
	 */
	String relativeToMenuId;

	/**
	 * The action command of this menu component
	 */
	String actionCommand;

	/**
	 * The id of the action of this menu component. The action is resolved from an
	 * {@link io.github.astrapi69.swing.menu.build.ActionRegistry}. If the action id is not set the
	 * name of this menu component is used as action id
	 */
	String actionId;

	/**
	 * The optional enabled state of this menu component. A null value means enabled
	 */
	Boolean enabled;

	/**
	 * The optional selected state of this menu component. Only relevant for check box and radio
	 * button menu items
	 */
	Boolean selected;

	/**
	 * The optional button group name of this menu component. Radio button menu items with the same
	 * group name are mutually exclusive
	 */
	String group;

	/**
	 * The optional icon of this menu component as classpath resource path or file path
	 */
	String icon;

	/**
	 * The child menu components of this menu component
	 */
	@Builder.Default
	List<MenuInfo> children = new ArrayList<>();

	/**
	 * Adds the given {@link MenuInfo} object as child of this {@link MenuInfo} object
	 *
	 * @param child
	 *            the child to add
	 * @return this {@link MenuInfo} object for method chaining
	 */
	public MenuInfo addChild(final @NonNull MenuInfo child)
	{
		if (children == null)
		{
			children = new ArrayList<>();
		}
		children.add(child);
		return this;
	}

	/**
	 * Checks if this {@link MenuInfo} object has children
	 *
	 * @return true if this {@link MenuInfo} object has children otherwise false
	 */
	public boolean hasChildren()
	{
		return children != null && !children.isEmpty();
	}

	/**
	 * Factory method that creates a {@link MenuItemInfo} object from this {@link MenuInfo} object
	 * and the given {@link ActionListener} object
	 *
	 * @param actionListener
	 *            the {@link ActionListener} object to set
	 * @return the new created {@link MenuItemInfo} object
	 */
	public MenuItemInfo toMenuItemInfo(ActionListener actionListener)
	{
		return MenuItemInfoConverter.toMenuItemInfo(this, actionListener);
	}
}
