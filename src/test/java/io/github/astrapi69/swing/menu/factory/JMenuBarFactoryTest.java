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
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.KeyStroke;
import javax.swing.MenuElement;

import io.github.astrapi69.swing.menu.ParentMenuResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

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

/**
 * The unit test class for the class {@link JMenuBarFactory}
 */
public class JMenuBarFactoryTest
{

	File xmlFile;
	String xml;

	@BeforeEach
	public void beforeEach()
	{
		String filename;
		filename = "app-menubar.xml";
		xmlFile = FileFactory.newFileQuietly(PathFinder.getSrcTestResourcesDir(), filename);
		xml = RuntimeExceptionDecorator.decorate(() -> ReadFileExtensions.fromFile(xmlFile));
	}

	@ExtendWith(IgnoreHeadlessExceptionExtension.class)
	@Test
	public void testBuildMenubarFromXml() throws IOException
	{
		Map<String, ActionListener> actionListenerMap;
		BaseTreeNode<MenuInfo, Long> menuInfoLongBaseTreeNode;

		menuInfoLongBaseTreeNode = MenuInfoTreeNodeConverter.toMenuInfoTreeNode(xml);
		assertNotNull(menuInfoLongBaseTreeNode);
		assertEquals(menuInfoLongBaseTreeNode.getId(), 0);

		actionListenerMap = new HashMap<>();

		actionListenerMap.put(BaseMenuId.TOGGLE_FULLSCREEN.propertiesKey(),
			new ToggleFullScreenAction("Fullscreen", new JFrame()));
		actionListenerMap.put(BaseMenuId.EXIT.propertiesKey(), new ExitApplicationAction("Exit"));
		actionListenerMap.put(BaseMenuId.FILE.propertiesKey(), new NoAction());
		actionListenerMap.put(BaseMenuId.MENU_BAR.propertiesKey(), new NoAction());
		actionListenerMap.put(BaseMenuId.HELP.propertiesKey(), new NoAction());
		actionListenerMap.put(BaseMenuId.HELP_CONTENT.propertiesKey(), new NoAction());
		actionListenerMap.put(BaseMenuId.HELP_INFO.propertiesKey(), new NoAction());

		final JMenuBar menuBar = JMenuBarFactory.buildMenuBar(menuInfoLongBaseTreeNode,
			actionListenerMap);
		assertNotNull(menuBar);

		List<MenuElement> allMenuElements = ParentMenuResolver.getAllMenuElements(menuBar, true);
		assertNotNull(allMenuElements);

	}

	@Test
	public void testBuildRootTreeNodeFromXml() throws IOException
	{
		BaseTreeNode<MenuInfo, Long> menuBarTreeNode;
		BaseTreeNode<MenuInfo, Long> fileTreeNode;
		BaseTreeNode<MenuInfo, Long> toggleFullscreenTreeNode;
		BaseTreeNode<MenuInfo, Long> exitTreeNode;
		BaseTreeNode<MenuInfo, Long> helpTreeNode;
		BaseTreeNode<MenuInfo, Long> helpContentTreeNode;
		BaseTreeNode<MenuInfo, Long> infoTreeNode;
		MenuInfo helpContentMenuInfo;
		MenuInfo helpMenuInfo;
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
		helpContentMenuInfo = MenuInfo.builder().mnemonic(MenuExtensions.toMnemonic('C'))
			.keyStrokeInfo(
				KeyStrokeInfo.toKeyStrokeInfo(KeyStroke.getKeyStroke("ctrl alt pressed H")))
			.text("Help Content").name(BaseMenuId.HELP_CONTENT.propertiesKey()).build();
		helpContentTreeNode = BaseTreeNode.<MenuInfo, Long> builder().id(idGenerator.getNextId())
			.value(helpContentMenuInfo).build();

		infoMenuInfo = MenuInfo.builder().mnemonic(MenuExtensions.toMnemonic('I'))
			.keyStrokeInfo(KeyStrokeInfo.toKeyStrokeInfo(KeyStroke.getKeyStroke("ctrl pressed I")))
			.text("Info").name(BaseMenuId.HELP_INFO.propertiesKey()).build();
		infoTreeNode = BaseTreeNode.<MenuInfo, Long> builder().id(idGenerator.getNextId())
			.value(infoMenuInfo).build();

		menuBarTreeNode.addChild(fileTreeNode);
		menuBarTreeNode.addChild(helpTreeNode);
		fileTreeNode.addChild(toggleFullscreenTreeNode);
		fileTreeNode.addChild(exitTreeNode);
		helpTreeNode.addChild(helpContentTreeNode);
		helpTreeNode.addChild(infoTreeNode);

		menuInfoLongBaseTreeNode = MenuInfoTreeNodeConverter.toMenuInfoTreeNode(xml);
		assertNotNull(menuInfoLongBaseTreeNode);
		assertEquals(menuInfoLongBaseTreeNode, menuBarTreeNode);
	}

}
