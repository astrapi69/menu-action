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

import io.github.astrapi69.file.create.FileFactory;
import io.github.astrapi69.file.read.ReadFileExtensions;
import io.github.astrapi69.file.search.PathFinder;
import io.github.astrapi69.id.generate.LongIdGenerator;
import io.github.astrapi69.junit.jupiter.callback.before.test.IgnoreHeadlessExceptionExtension;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JMenuFactoryTest
{

	File xmlFile;
	String xml;

	@BeforeEach
	public void beforeEach()
	{
		String filename;
		filename = "app-menu.xml";
		xmlFile = FileFactory.newFileQuietly(PathFinder.getSrcTestResourcesDir(), filename);
		xml = RuntimeExceptionDecorator.decorate(() -> ReadFileExtensions.fromFile(xmlFile));
	}

	@ExtendWith(IgnoreHeadlessExceptionExtension.class)
	@Test
	public void testBuildMenuFromXml()
	{
		Map<String, ActionListener> actionListenerMap;
		BaseTreeNode<MenuInfo, Long> menuInfoLongBaseTreeNode;
		JMenu menu;

		menuInfoLongBaseTreeNode = MenuInfoTreeNodeConverter.toMenuInfoTreeNode(xml);
		assertNotNull(menuInfoLongBaseTreeNode);
		assertEquals(menuInfoLongBaseTreeNode.getId(), 0);

		actionListenerMap = new HashMap<>();

		actionListenerMap.put(BaseMenuId.TOGGLE_FULLSCREEN.propertiesKey(),
			new ToggleFullScreenAction("Fullscreen", new JFrame()));
		actionListenerMap.put(BaseMenuId.EXIT.propertiesKey(), new ExitApplicationAction("Exit"));
		actionListenerMap.put(BaseMenuId.FILE.propertiesKey(), new NoAction());

		menu = JMenuFactory.buildMenu(menuInfoLongBaseTreeNode, actionListenerMap);
		assertNotNull(menu);
	}


	@Test
	public void testBuildRootTreeNodeFromXmlForFileMenu()
	{
		BaseTreeNode<MenuInfo, Long> fileTreeNode;
		BaseTreeNode<MenuInfo, Long> toggleFullscreenTreeNode;
		BaseTreeNode<MenuInfo, Long> exitTreeNode;
		MenuInfo fileMenuInfo;
		MenuInfo toggleFullscreenMenuInfo;
		MenuInfo exitMenuInfo;
		LongIdGenerator idGenerator;
		String treeNodeAsXml;

		BaseTreeNode<MenuInfo, Long> menuInfoLongBaseTreeNode;

		idGenerator = LongIdGenerator.of(0L);

        fileMenuInfo = MenuInfo.builder().mnemonic(MenuExtensions.toMnemonic('F'))
                .keyStrokeInfo(KeyStrokeInfo.toKeyStrokeInfo(KeyStroke.getKeyStroke("alt pressed F")))
                .text("File").name(BaseMenuId.FILE.propertiesKey()).build();
        fileTreeNode = BaseTreeNode.<MenuInfo, Long>builder().id(idGenerator.getNextId())
                .value(fileMenuInfo).build();

        toggleFullscreenMenuInfo = MenuInfo.builder().type(MenuType.MENU_ITEM)
                .mnemonic(MenuExtensions.toMnemonic('T'))
                .keyStrokeInfo(KeyStrokeInfo.toKeyStrokeInfo(KeyStroke.getKeyStroke("alt pressed F11")))
                .text("Toggle Fullscreen").name(BaseMenuId.TOGGLE_FULLSCREEN.propertiesKey()).build();
        toggleFullscreenTreeNode = BaseTreeNode.<MenuInfo, Long>builder()
                .id(idGenerator.getNextId()).parent(fileTreeNode).value(toggleFullscreenMenuInfo)
                .leaf(true).build();

        exitMenuInfo = MenuInfo.builder().type(MenuType.MENU_ITEM)
                .mnemonic(MenuExtensions.toMnemonic('E'))
                .keyStrokeInfo(KeyStrokeInfo.toKeyStrokeInfo(KeyStroke.getKeyStroke("alt pressed F4")))
                .text("Exit").name(BaseMenuId.EXIT.propertiesKey()).build();
        exitTreeNode = BaseTreeNode.<MenuInfo, Long>builder().id(idGenerator.getNextId())
                .leaf(true).parent(fileTreeNode).value(exitMenuInfo).build();

		fileTreeNode.addChild(toggleFullscreenTreeNode);
		fileTreeNode.addChild(exitTreeNode);

		treeNodeAsXml = MenuInfoTreeNodeConverter.toXml(fileTreeNode);

		menuInfoLongBaseTreeNode = MenuInfoTreeNodeConverter.toMenuInfoTreeNode(treeNodeAsXml);
		assertNotNull(menuInfoLongBaseTreeNode);
		assertEquals(menuInfoLongBaseTreeNode, fileTreeNode);
	}


	@Test
	public void testBuildRootTreeNodeFromXmlForEditMenu()
	{
		BaseTreeNode<MenuInfo, Long> editTreeNode;
		MenuInfo editMenuInfo;
		LongIdGenerator idGenerator;
		String treeNodeAsXml;

		BaseTreeNode<MenuInfo, Long> menuInfoLongBaseTreeNode;

		idGenerator = LongIdGenerator.of(0L);

        editMenuInfo = MenuInfo.builder().mnemonic(MenuExtensions.toMnemonic('E'))
                .keyStrokeInfo(KeyStrokeInfo.toKeyStrokeInfo(KeyStroke.getKeyStroke("alt pressed E")))
                .text("Edit").name(BaseMenuId.EDIT.propertiesKey()).build();


        editTreeNode = BaseTreeNode.<MenuInfo, Long>builder().id(idGenerator.getNextId())
                .value(editMenuInfo).build();

		treeNodeAsXml = MenuInfoTreeNodeConverter.toXml(editTreeNode);

		menuInfoLongBaseTreeNode = MenuInfoTreeNodeConverter.toMenuInfoTreeNode(treeNodeAsXml);
		assertNotNull(menuInfoLongBaseTreeNode);
		assertEquals(menuInfoLongBaseTreeNode, editTreeNode);
	}

	@Test
	public void testBuildRootTreeNodeFromXmlForHelpMenu()
	{
		BaseTreeNode<MenuInfo, Long> helpTreeNode;
		BaseTreeNode<MenuInfo, Long> helpContentTreeNode;
		BaseTreeNode<MenuInfo, Long> donateTreeNode;
		BaseTreeNode<MenuInfo, Long> licenseTreeNode;
		BaseTreeNode<MenuInfo, Long> infoTreeNode;
		MenuInfo helpMenuInfo;
		MenuInfo helpContentMenuInfo;
        MenuInfo donateMenuInfo;
        MenuInfo licenseMenuInfo;
        MenuInfo infoMenuInfo;
        LongIdGenerator idGenerator;
        String treeNodeAsXml;

        BaseTreeNode<MenuInfo, Long> menuInfoLongBaseTreeNode;

        idGenerator = LongIdGenerator.of(0L);

        helpMenuInfo = MenuInfo.builder().mnemonic(MenuExtensions.toMnemonic('H')).text("Help")
                .name(BaseMenuId.HELP.propertiesKey()).build();
        helpTreeNode = BaseTreeNode.<MenuInfo, Long>builder().id(idGenerator.getNextId())
                .value(helpMenuInfo).build();

        helpContentMenuInfo = MenuInfo.builder().type(MenuType.MENU_ITEM)
                .mnemonic(MenuExtensions.toMnemonic('c'))
                .keyStrokeInfo(KeyStrokeInfo.toKeyStrokeInfo(KeyStroke.getKeyStroke("alt pressed H")))
                .text("Content").name(BaseMenuId.HELP_CONTENT.propertiesKey()).build();
        helpContentTreeNode = BaseTreeNode.<MenuInfo, Long>builder().id(idGenerator.getNextId())
                .value(helpContentMenuInfo).leaf(true).build();

        donateMenuInfo = MenuInfo.builder().type(MenuType.MENU_ITEM).text("Donate")
                .name(BaseMenuId.HELP_DONATE.propertiesKey()).build();
        donateTreeNode = BaseTreeNode.<MenuInfo, Long>builder().id(idGenerator.getNextId())
                .leaf(true).value(donateMenuInfo).build();

        licenseMenuInfo = MenuInfo.builder().type(MenuType.MENU_ITEM).text("Licence")
                .name(BaseMenuId.HELP_LICENSE.propertiesKey()).build();
        licenseTreeNode = BaseTreeNode.<MenuInfo, Long>builder().id(idGenerator.getNextId())
                .leaf(true).value(licenseMenuInfo).build();

        infoMenuInfo = MenuInfo.builder().type(MenuType.MENU_ITEM).text("Info")
                .name(BaseMenuId.HELP_INFO.propertiesKey()).build();
        infoTreeNode = BaseTreeNode.<MenuInfo, Long>builder().id(idGenerator.getNextId())
                .leaf(true).value(infoMenuInfo).build();

        helpTreeNode.addChild(helpContentTreeNode);
        helpTreeNode.addChild(donateTreeNode);
        helpTreeNode.addChild(licenseTreeNode);
        helpTreeNode.addChild(infoTreeNode);

        treeNodeAsXml = MenuInfoTreeNodeConverter.toXml(helpTreeNode);

        menuInfoLongBaseTreeNode = MenuInfoTreeNodeConverter.toMenuInfoTreeNode(treeNodeAsXml);
		assertNotNull(menuInfoLongBaseTreeNode);
		assertEquals(menuInfoLongBaseTreeNode, helpTreeNode);
	}
}
