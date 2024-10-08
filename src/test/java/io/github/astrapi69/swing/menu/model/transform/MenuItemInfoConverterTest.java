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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.astrapi69.awt.action.NoAction;
import io.github.astrapi69.file.create.FileFactory;
import io.github.astrapi69.file.read.ReadFileExtensions;
import io.github.astrapi69.file.search.PathFinder;
import io.github.astrapi69.swing.menu.MenuExtensions;
import io.github.astrapi69.swing.menu.enumeration.BaseMenuId;
import io.github.astrapi69.swing.menu.enumeration.MenuType;
import io.github.astrapi69.swing.menu.model.KeyStrokeInfo;
import io.github.astrapi69.swing.menu.model.MenuInfo;
import io.github.astrapi69.swing.menu.model.MenuItemInfo;
import io.github.astrapi69.throwable.RuntimeExceptionDecorator;

class MenuItemInfoConverterTest
{

	File xmlFile;
	String fileMenuXml;
	String editMenuXml;
	String helpMenuXml;

	@BeforeEach
	public void beforeEach()
	{
		String filename;
		filename = "app-file-menu.xml";
		xmlFile = FileFactory.newFileQuietly(PathFinder.getSrcTestResourcesDir(), filename);
		fileMenuXml = RuntimeExceptionDecorator
			.decorate(() -> ReadFileExtensions.fromFile(xmlFile));

		filename = "app-edit-menu.xml";
		xmlFile = FileFactory.newFileQuietly(PathFinder.getSrcTestResourcesDir(), filename);
		editMenuXml = RuntimeExceptionDecorator
			.decorate(() -> ReadFileExtensions.fromFile(xmlFile));

		filename = "app-help-menu.xml";
		xmlFile = FileFactory.newFileQuietly(PathFinder.getSrcTestResourcesDir(), filename);
		helpMenuXml = RuntimeExceptionDecorator
			.decorate(() -> ReadFileExtensions.fromFile(xmlFile));
	}

	@Test
	void fromJMenu()
	{
		MenuInfo editMenuInfo;
		MenuInfo menuInfo;
		MenuItemInfo menuItemInfo;
		JMenu menu;

		editMenuInfo = MenuInfo.builder().type(MenuType.MENU)
			.mnemonic(MenuExtensions.toMnemonic('E'))
			.keyStrokeInfo(KeyStrokeInfo.toKeyStrokeInfo(KeyStroke.getKeyStroke("alt pressed E")))
			.actionCommand("Edit").text("Edit").name(BaseMenuId.EDIT.propertiesKey()).build();

		menuItemInfo = MenuItemInfoConverter.toMenuItemInfo(editMenuInfo, new NoAction());

		menu = menuItemInfo.toJMenu();

		menuInfo = MenuItemInfoConverter.fromJMenu(menu);
		assertEquals(menuInfo, editMenuInfo);
	}

	@Test
	void testTtoMenuItemInfoWithActionClass()
	{
		MenuItemInfo helpContentMenuInfo;
		MenuItemInfo menuInfo;
		MenuItemInfo menuItemInfo;
		JMenuItem menu;


		helpContentMenuInfo = MenuItemInfo.builder().type(MenuType.MENU_ITEM)
			.actionCommand("io.github.astrapi69.awt.action.NoAction")
			.mnemonic(MenuExtensions.toMnemonic('C'))
			.keyStrokeInfo(
				KeyStrokeInfo.toKeyStrokeInfo(KeyStroke.getKeyStroke("ctrl alt pressed H")))
			.text("Help Content").name(BaseMenuId.HELP_CONTENT.propertiesKey()).build();

		menuItemInfo = MenuItemInfoConverter.toMenuItemInfo(helpContentMenuInfo,
			"io.github.astrapi69.awt.action.NoAction");
		assertNotNull(menuItemInfo);
	}

	@Test
	void fromJMenuItem()
	{
		MenuItemInfo helpContentMenuInfo;
		MenuItemInfo menuInfo;
		MenuItemInfo menuItemInfo;
		JMenuItem menu;


		helpContentMenuInfo = MenuItemInfo.builder().type(MenuType.MENU_ITEM)
			.mnemonic(MenuExtensions.toMnemonic('C'))
			.keyStrokeInfo(
				KeyStrokeInfo.toKeyStrokeInfo(KeyStroke.getKeyStroke("ctrl alt pressed H")))
			.actionCommand("Help Content").text("Help Content")
			.name(BaseMenuId.HELP_CONTENT.propertiesKey()).build();

		menuItemInfo = MenuItemInfoConverter.toMenuItemInfo(helpContentMenuInfo, new NoAction());

		menu = menuItemInfo.toJMenuItem();

		menuInfo = MenuItemInfoConverter.fromJMenuItem(menu);
		assertEquals(menuInfo, helpContentMenuInfo);
	}

	@Test
	void fromJCheckBoxMenuItem()
	{
		MenuItemInfo donateMenuInfo;
		MenuItemInfo menuInfo;
		MenuItemInfo menuItemInfo;
		JCheckBoxMenuItem menu;

		donateMenuInfo = MenuItemInfo.builder().type(MenuType.CHECK_BOX_MENU_ITEM)
			.mnemonic(MenuExtensions.toMnemonic('Y'))
			.keyStrokeInfo(KeyStrokeInfo.toKeyStrokeInfo(KeyStroke.getKeyStroke("ctrl pressed Y")))
			.actionCommand("Yes").text("Yes").name(BaseMenuId.HELP_DONATE.propertiesKey()).build();

		menuItemInfo = MenuItemInfoConverter.toMenuItemInfo(donateMenuInfo, new NoAction());

		menu = menuItemInfo.toJCheckBoxMenuItem();

		menuInfo = MenuItemInfoConverter.fromJCheckBoxMenuItem(menu);
		assertEquals(menuInfo, donateMenuInfo);
	}

	@Test
	void fromJRadioButtonMenuItem()
	{
		MenuItemInfo donateMenuInfo;
		MenuItemInfo menuInfo;
		MenuItemInfo menuItemInfo;
		JRadioButtonMenuItem menu;
		ActionListener actionListener;

		donateMenuInfo = MenuItemInfo.builder().type(MenuType.RADIO_BUTTON_MENU_ITEM)
			.mnemonic(MenuExtensions.toMnemonic('R'))
			.keyStrokeInfo(KeyStrokeInfo.toKeyStrokeInfo(KeyStroke.getKeyStroke("ctrl pressed R")))
			.actionCommand("Donate").text("Donate").name(BaseMenuId.HELP_DONATE.propertiesKey())
			.build();

		actionListener = new NoAction();
		menuItemInfo = MenuItemInfoConverter.toMenuItemInfo(donateMenuInfo, actionListener);

		menu = menuItemInfo.toJRadioButtonMenuItem();

		menuInfo = MenuItemInfoConverter.fromJRadioButtonMenuItem(menu);
		assertEquals(menuInfo, donateMenuInfo);
	}

}
