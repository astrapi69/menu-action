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
package io.github.astrapi69.swing.menu.model;

import java.awt.event.ActionListener;

import io.github.astrapi69.swing.menu.enumtype.BaseMenuId;
import io.github.astrapi69.swing.menu.enumtype.MenuType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import javax.swing.*;

/**
 * The class {@link MenuInfo} is intended for store the information of a menu and on need to restore
 * the menu
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MenuInfo
{
	/**
	 * The name of the menu component. The name is used as the id and the action command of the menu
	 * component
	 */
	String name;

	/**
	 * The text of the menu component. The text is used as the label of the menu component
	 */
	String text;
	Integer mnemonic;
	KeyStrokeInfo keyStrokeInfo;
	@Builder.Default
	MenuType type = MenuType.MENU;

	public MenuItemInfo toMenuItemInfo(ActionListener actionListener)
	{
		return this.keyStrokeInfo != null
			? MenuItemInfo.builder().actionListener(actionListener).text(this.text)
				.mnemonic(this.mnemonic).keyStroke(this.keyStrokeInfo.toKeyStroke()).name(this.name)
				.build()
			: MenuItemInfo.builder().actionListener(actionListener).text(this.text)
				.mnemonic(this.mnemonic).name(this.name).build();
	}
}
