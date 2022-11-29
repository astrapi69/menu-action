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

import io.github.astrapi69.swing.menu.enumtype.MenuType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

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
	String actionCommand;
	String label;
	String text;
	Integer mnemonic;
	KeyStrokeInfo keyStrokeInfo;
	String name;
	String actionId;
	@Builder.Default
	MenuType type = MenuType.MENU;

	public JMenuItemInfo toJMenuItemInfo(ActionListener actionListener)
	{
		return this.keyStrokeInfo != null
			? JMenuItemInfo.builder().actionListener(actionListener).text(this.text)
				.mnemonic(this.mnemonic).keyStroke(this.keyStrokeInfo.toKeyStroke()).name(this.name)
				.build()
			: JMenuItemInfo.builder().actionListener(actionListener).text(this.text)
				.mnemonic(this.mnemonic).name(this.name).build();
	}
}
