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
package io.github.astrapi69.swing.menu.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.awt.event.ActionListener;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.astrapi69.file.create.FileFactory;
import io.github.astrapi69.file.read.ReadFileExtensions;
import io.github.astrapi69.file.search.PathFinder;
import io.github.astrapi69.id.generate.LongIdGenerator;
import io.github.astrapi69.swing.action.ExitApplicationAction;
import io.github.astrapi69.swing.action.NoAction;
import io.github.astrapi69.swing.action.ToggleFullScreenAction;
import io.github.astrapi69.swing.menu.MenuExtensions;
import io.github.astrapi69.swing.menu.enumeration.BaseMenuId;
import io.github.astrapi69.swing.menu.enumeration.MenuType;
import io.github.astrapi69.swing.menu.model.KeyStrokeInfo;
import io.github.astrapi69.swing.menu.model.MenuInfo;
import io.github.astrapi69.swing.menu.model.transform.MenuInfoTreeNodeConverter;
import io.github.astrapi69.throwable.RuntimeExceptionDecorator;
import io.github.astrapi69.tree.BaseTreeNode;
import io.github.astrapi69.window.adapter.CloseWindow;

/**
 * The unit test class for the class {@link JMenuBarFactory}
 */
public class JMenuBarFactoryTest2
{

	File xmlFile;
	String xml;

	public static void main(String[] args)
	{
		JFrame frame;
		String filename;
		filename = "app-tree-menubar.xml";
		frame = new JFrame("Test Menu with xml");
		File xmlFile = FileFactory.newFileQuietly(PathFinder.getSrcTestResourcesDir(), filename);
		String xml = RuntimeExceptionDecorator.decorate(() -> ReadFileExtensions.fromFile(xmlFile));

		Map<String, ActionListener> actionListenerMap;
		BaseTreeNode<MenuInfo, Long> menuInfoLongBaseTreeNode;

		menuInfoLongBaseTreeNode = MenuInfoTreeNodeConverter.toMenuInfoTreeNode(xml);
		actionListenerMap = new LinkedHashMap<>();

		actionListenerMap.put(BaseMenuId.TOGGLE_FULLSCREEN.propertiesKey(),
			new ToggleFullScreenAction("Fullscreen", frame));
		actionListenerMap.put(BaseMenuId.EXIT.propertiesKey(), new ExitApplicationAction("Exit"));
		actionListenerMap.put(BaseMenuId.FILE.propertiesKey(), new NoAction());
		actionListenerMap.put(BaseMenuId.MENU_BAR.propertiesKey(), new NoAction());
		actionListenerMap.put(BaseMenuId.HELP.propertiesKey(), new NoAction());
		actionListenerMap.put(BaseMenuId.HELP_CONTENT.propertiesKey(), new NoAction());
		actionListenerMap.put(BaseMenuId.HELP_DONATE.propertiesKey(), new NoAction());
		actionListenerMap.put(TestMenuId.HELP_DIAGNOSTIC.propertiesKey(), new NoAction());
		actionListenerMap.put(TestMenuId.HELP_DIAGNOSTIC_ACTIVITY.propertiesKey(), new NoAction());
		actionListenerMap.put(TestMenuId.HELP_DIAGNOSTIC_PROFILE.propertiesKey(), new NoAction());
		actionListenerMap.put(TestMenuId.HELP_DIAGNOSTIC_USAGE.propertiesKey(), new NoAction());
		actionListenerMap.put(BaseMenuId.HELP_LICENSE.propertiesKey(), new NoAction());
		actionListenerMap.put(BaseMenuId.HELP_INFO.propertiesKey(), new NoAction());

		final JMenuBar menuBar = JMenuBarFactory.buildMenuBar(menuInfoLongBaseTreeNode,
			actionListenerMap);


		frame.setJMenuBar(menuBar);
		frame.addWindowListener(new CloseWindow());

		frame.setSize(400, 200);
		frame.setVisible(true);
	}

	@BeforeEach
	public void beforeEach()
	{
		String filename;
		filename = "app-tree-menubar.xml";
		xmlFile = FileFactory.newFileQuietly(PathFinder.getSrcTestResourcesDir(), filename);
		xml = RuntimeExceptionDecorator.decorate(() -> ReadFileExtensions.fromFile(xmlFile));
	}


	@Test
	public void testBuildRootTreeNodeFromXml()
	{
		BaseTreeNode<MenuInfo, Long> menuBarTreeNode;
		BaseTreeNode<MenuInfo, Long> fileTreeNode;
		BaseTreeNode<MenuInfo, Long> toggleFullscreenTreeNode;
		BaseTreeNode<MenuInfo, Long> exitTreeNode;
		BaseTreeNode<MenuInfo, Long> helpTreeNode;
		BaseTreeNode<MenuInfo, Long> helpContentTreeNode;
		BaseTreeNode<MenuInfo, Long> donateTreeNode;
		BaseTreeNode<MenuInfo, Long> diagnosticTreeNode;
		BaseTreeNode<MenuInfo, Long> diagnosticActivityTreeNode;
		BaseTreeNode<MenuInfo, Long> diagnosticProfileTreeNode;
		BaseTreeNode<MenuInfo, Long> diagnosticUsageTreeNode;
		BaseTreeNode<MenuInfo, Long> licenseTreeNode;
		BaseTreeNode<MenuInfo, Long> infoTreeNode;
		MenuInfo helpContentMenuInfo;
		MenuInfo helpMenuInfo;
		MenuInfo donateMenuInfo;
		MenuInfo diagnosticMenuInfo;
		MenuInfo diagnosticActivityMenuInfo;
		MenuInfo diagnosticProfileMenuInfo;
		MenuInfo diagnosticUsageMenuInfo;
		MenuInfo licenseMenuInfo;
		MenuInfo infoMenuInfo;
		MenuInfo menuBarInfo;
		MenuInfo fileMenuInfo;
		MenuInfo toggleFullscreenMenuInfo;
		MenuInfo exitMenuInfo;
		LongIdGenerator idGenerator;

		BaseTreeNode<MenuInfo, Long> menuInfoLongBaseTreeNode;

		idGenerator = LongIdGenerator.of(0L);

		menuBarInfo = MenuInfoTreeNodeConverter.fromJMenuBar();

		menuBarTreeNode = BaseTreeNode.<MenuInfo, Long> builder().id(idGenerator.getNextId())
			.value(menuBarInfo).build();

		fileMenuInfo = MenuInfo.builder().mnemonic(MenuExtensions.toMnemonic('F'))
			.keyStrokeInfo(KeyStrokeInfo.toKeyStrokeInfo(KeyStroke.getKeyStroke("alt pressed F")))
			.text("File").name(BaseMenuId.FILE.propertiesKey()).build();
		fileTreeNode = BaseTreeNode.<MenuInfo, Long> builder().id(idGenerator.getNextId())
			.value(fileMenuInfo).build();

		toggleFullscreenMenuInfo = MenuInfo.builder().type(MenuType.MENU_ITEM)
			.mnemonic(MenuExtensions.toMnemonic('T'))
			.keyStrokeInfo(KeyStrokeInfo.toKeyStrokeInfo(KeyStroke.getKeyStroke("alt pressed F11")))
			.text("Toggle Fullscreen").name(BaseMenuId.TOGGLE_FULLSCREEN.propertiesKey()).build();
		toggleFullscreenTreeNode = BaseTreeNode.<MenuInfo, Long> builder()
			.id(idGenerator.getNextId()).parent(fileTreeNode).value(toggleFullscreenMenuInfo)
			.leaf(true).build();

		exitMenuInfo = MenuInfo.builder().type(MenuType.MENU_ITEM)
			.mnemonic(MenuExtensions.toMnemonic('E'))
			.keyStrokeInfo(KeyStrokeInfo.toKeyStrokeInfo(KeyStroke.getKeyStroke("alt pressed F4")))
			.text("Exit").name(BaseMenuId.EXIT.propertiesKey()).build();
		exitTreeNode = BaseTreeNode.<MenuInfo, Long> builder().id(idGenerator.getNextId())
			.leaf(true).parent(fileTreeNode).value(exitMenuInfo).build();

		helpMenuInfo = MenuInfo.builder().mnemonic(MenuExtensions.toMnemonic('H'))
			.keyStrokeInfo(KeyStrokeInfo.toKeyStrokeInfo(KeyStroke.getKeyStroke("alt pressed H")))
			.text("Help").name(BaseMenuId.HELP.propertiesKey()).build();
		helpTreeNode = BaseTreeNode.<MenuInfo, Long> builder().id(idGenerator.getNextId())
			.value(helpMenuInfo).build();
		helpContentMenuInfo = MenuInfo.builder().type(MenuType.MENU_ITEM)
			.mnemonic(MenuExtensions.toMnemonic('C'))
			.keyStrokeInfo(
				KeyStrokeInfo.toKeyStrokeInfo(KeyStroke.getKeyStroke("ctrl alt pressed H")))
			.text("Help Content").name(BaseMenuId.HELP_CONTENT.propertiesKey()).build();
		helpContentTreeNode = BaseTreeNode.<MenuInfo, Long> builder().id(idGenerator.getNextId())
			.leaf(true).value(helpContentMenuInfo).build();

		donateMenuInfo = MenuInfo.builder().type(MenuType.MENU_ITEM)
			.mnemonic(MenuExtensions.toMnemonic('L'))
			.keyStrokeInfo(KeyStrokeInfo.toKeyStrokeInfo(KeyStroke.getKeyStroke("ctrl pressed L")))
			.text("Donate").name(BaseMenuId.HELP_DONATE.propertiesKey()).build();
		donateTreeNode = BaseTreeNode.<MenuInfo, Long> builder().id(idGenerator.getNextId())
			.leaf(true).value(donateMenuInfo).build();

		diagnosticMenuInfo = MenuInfo.builder().mnemonic(MenuExtensions.toMnemonic('G'))
			.text("Diagnostic >").name(TestMenuId.HELP_DIAGNOSTIC.propertiesKey()).build();
		diagnosticTreeNode = BaseTreeNode.<MenuInfo, Long> builder().id(idGenerator.getNextId())
			.value(diagnosticMenuInfo).build();

		diagnosticActivityMenuInfo = MenuInfo.builder().type(MenuType.CHECK_BOX_MENU_ITEM)
			.mnemonic(MenuExtensions.toMnemonic('A'))
			.keyStrokeInfo(KeyStrokeInfo.toKeyStrokeInfo(KeyStroke.getKeyStroke("ctrl pressed A")))
			.text("Activity").name(TestMenuId.HELP_DIAGNOSTIC_ACTIVITY.propertiesKey()).build();
		diagnosticActivityTreeNode = BaseTreeNode.<MenuInfo, Long> builder()
			.id(idGenerator.getNextId()).leaf(true).value(diagnosticActivityMenuInfo).build();

		diagnosticProfileMenuInfo = MenuInfo.builder().type(MenuType.RADIO_BUTTON_MENU_ITEM)
			.mnemonic(MenuExtensions.toMnemonic('P'))
			.keyStrokeInfo(KeyStrokeInfo.toKeyStrokeInfo(KeyStroke.getKeyStroke("ctrl pressed P")))
			.text("Profile").name(TestMenuId.HELP_DIAGNOSTIC_PROFILE.propertiesKey()).build();
		diagnosticProfileTreeNode = BaseTreeNode.<MenuInfo, Long> builder()
			.id(idGenerator.getNextId()).leaf(true).value(diagnosticProfileMenuInfo).build();

		diagnosticUsageMenuInfo = MenuInfo.builder().type(MenuType.MENU_ITEM)
			.mnemonic(MenuExtensions.toMnemonic('U'))
			.keyStrokeInfo(KeyStrokeInfo.toKeyStrokeInfo(KeyStroke.getKeyStroke("ctrl pressed U")))
			.text("Usage").name(TestMenuId.HELP_DIAGNOSTIC_USAGE.propertiesKey()).build();
		diagnosticUsageTreeNode = BaseTreeNode.<MenuInfo, Long> builder()
			.id(idGenerator.getNextId()).leaf(true).value(diagnosticUsageMenuInfo).build();

		licenseMenuInfo = MenuInfo.builder().type(MenuType.MENU_ITEM)
			.mnemonic(MenuExtensions.toMnemonic('L'))
			.keyStrokeInfo(KeyStrokeInfo.toKeyStrokeInfo(KeyStroke.getKeyStroke("ctrl pressed L")))
			.text("Licence").name(BaseMenuId.HELP_LICENSE.propertiesKey()).build();
		licenseTreeNode = BaseTreeNode.<MenuInfo, Long> builder().id(idGenerator.getNextId())
			.value(licenseMenuInfo).build();

		infoMenuInfo = MenuInfo.builder().type(MenuType.MENU_ITEM)
			.mnemonic(MenuExtensions.toMnemonic('I'))
			.keyStrokeInfo(KeyStrokeInfo.toKeyStrokeInfo(KeyStroke.getKeyStroke("ctrl pressed I")))
			.text("Info").name(BaseMenuId.HELP_INFO.propertiesKey()).build();
		infoTreeNode = BaseTreeNode.<MenuInfo, Long> builder().id(idGenerator.getNextId())
			.value(infoMenuInfo).build();

		menuBarTreeNode.addChild(fileTreeNode);
		menuBarTreeNode.addChild(helpTreeNode);
		fileTreeNode.addChild(toggleFullscreenTreeNode);
		fileTreeNode.addChild(exitTreeNode);
		helpTreeNode.addChild(helpContentTreeNode);
		helpTreeNode.addChild(donateTreeNode);
		helpTreeNode.addChild(diagnosticTreeNode);
		helpTreeNode.addChild(licenseTreeNode);
		helpTreeNode.addChild(infoTreeNode);
		diagnosticTreeNode.addChild(diagnosticActivityTreeNode);
		diagnosticTreeNode.addChild(diagnosticProfileTreeNode);
		diagnosticTreeNode.addChild(diagnosticUsageTreeNode);

		String toXml = MenuInfoTreeNodeConverter.toXml(menuBarTreeNode);

		menuInfoLongBaseTreeNode = MenuInfoTreeNodeConverter.toMenuInfoTreeNode(xml);
		assertNotNull(menuInfoLongBaseTreeNode);
		assertEquals(menuInfoLongBaseTreeNode, menuBarTreeNode);
	}

}
