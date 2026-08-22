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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import org.junit.jupiter.api.Test;

import io.github.astrapi69.swing.menu.enumeration.MenuType;
import io.github.astrapi69.swing.menu.model.MenuInfo;
import io.github.astrapi69.swing.menu.xml.MenuXmlReader;

/**
 * The unit test class for the class {@link MenuBuilder}
 */
class MenuBuilderTest
{

	private static ActionRegistry newActions(final List<String> fired)
	{
		ActionRegistry actions = ActionRegistry.empty();
		for (String id : List.of("newFile", "openFile", "toggleFullscreen", "exit",
			"global.menu.edit.copy", "toggleStatusbar", "desktopMode", "panelMode", "info",
			"addNode", "deleteNode", "renameNode", "export", "pluginSettings"))
		{
			actions.register(id, e -> fired.add(e.getActionCommand()));
		}
		return actions;
	}

	@Test
	void buildMenuBarFromXml()
	{
		List<String> fired = new ArrayList<>();
		MenuBuilder builder = new MenuBuilder(newActions(fired))
			.withTextResolver(key -> "menu.edit.copy".equals(key) ? "Copy" : null);
		MenuInfo menuBarInfo = MenuXmlReader.readResource("menubar.xml");

		JMenuBar menuBar = builder.buildMenuBar(menuBarInfo);

		assertEquals("global.menu.bar", menuBar.getName());
		assertEquals(4, menuBar.getMenuCount());
		JMenu file = menuBar.getMenu(0);
		assertEquals("File", file.getText());
		assertEquals('F', file.getMnemonic());
		// 4 items + 1 separator
		assertEquals(5, file.getMenuComponentCount());
		assertInstanceOf(JSeparator.class, file.getMenuComponent(2));

		JMenuItem newItem = file.getItem(0);
		assertEquals("global.menu.file.new", newItem.getName());
		assertEquals("New", newItem.getText());
		assertEquals(KeyStroke.getKeyStroke("ctrl N"), newItem.getAccelerator());
		// action command defaults to the id
		assertEquals("global.menu.file.new", newItem.getActionCommand());
		newItem.doClick();
		assertEquals(List.of("global.menu.file.new"), fired);

		JMenuItem exit = file.getItem(4);
		assertEquals("Exit the application", exit.getToolTipText());

		JMenu edit = menuBar.getMenu(1);
		assertFalse(edit.isEnabled());
		assertEquals("Copy", edit.getItem(0).getText());

		JMenu view = menuBar.getMenu(2);
		JCheckBoxMenuItem statusbar = (JCheckBoxMenuItem)view.getItem(0);
		assertTrue(statusbar.isSelected());
		JRadioButtonMenuItem desktopMode = (JRadioButtonMenuItem)view.getItem(2);
		JRadioButtonMenuItem panelMode = (JRadioButtonMenuItem)view.getItem(3);
		assertTrue(desktopMode.isSelected());
		assertFalse(panelMode.isSelected());
		assertTrue(builder.getButtonGroup("view.mode").isPresent());
		panelMode.doClick();
		assertTrue(panelMode.isSelected());
		assertFalse(desktopMode.isSelected());

		assertEquals("global.menu.help", menuBar.getMenu(3).getName());

		assertTrue(builder.getComponent("global.menu.file.exit", JMenuItem.class).isPresent());
		assertTrue(builder.getComponent("global.menu.file", JMenu.class).isPresent());
		assertTrue(builder.getComponent("global.menu.bar", JMenuBar.class).isPresent());
		assertFalse(builder.getComponent("global.menu.bar", JMenu.class).isPresent());
		assertFalse(builder.getComponent("unknown").isPresent());
		Map<String, javax.swing.JComponent> components = builder.getComponents();
		assertTrue(components.containsKey("global.menu.view.mode.panel"));
	}

	@Test
	void buildPopupMenuAndToolBar()
	{
		List<String> fired = new ArrayList<>();
		MenuBuilder builder = new MenuBuilder(newActions(fired));

		JPopupMenu popup = builder.buildPopupMenu(MenuXmlReader.readResource("popup.xml"));
		assertEquals("tree.popup", popup.getName());
		// 2 items + separator + sub menu
		assertEquals(4, popup.getComponentCount());
		assertInstanceOf(JMenu.class, popup.getComponent(3));
		((JMenuItem)popup.getComponent(0)).doClick();
		assertEquals(List.of("tree.popup.add"), fired);

		JToolBar toolBar = builder.buildToolBar(MenuXmlReader.readResource("toolbar.xml"));
		assertEquals("global.tool.bar", toolBar.getName());
		assertEquals(4, toolBar.getComponentCount());
		Component newButton = toolBar.getComponent(0);
		assertInstanceOf(JButton.class, newButton);
		assertEquals("Create a new file", ((JButton)newButton).getToolTipText());
		assertInstanceOf(JToggleButton.class, toolBar.getComponent(3));
		assertTrue(((JToggleButton)toolBar.getComponent(3)).isSelected());
		((JButton)newButton).doClick();
		assertEquals(List.of("tree.popup.add", "global.tool.bar.new"), fired);
	}

	@Test
	void buildDispatchesByType()
	{
		MenuBuilder builder = new MenuBuilder().withMissingActionPolicy(MissingActionPolicy.IGNORE);
		assertInstanceOf(JMenuBar.class, builder.build(MenuXmlReader.readResource("menubar.xml")));
		assertInstanceOf(JPopupMenu.class, builder.build(MenuXmlReader.readResource("popup.xml")));
		assertInstanceOf(JToolBar.class, builder.build(MenuXmlReader.readResource("toolbar.xml")));
		assertInstanceOf(JMenu.class,
			builder.build(MenuInfo.builder().type(MenuType.MENU).name("m").build()));
		assertInstanceOf(JMenuItem.class,
			builder.build(MenuInfo.builder().type(MenuType.MENU_ITEM).name("i").build()));
	}

	@Test
	void missingActionPolicies()
	{
		MenuInfo item = MenuInfo.builder().type(MenuType.MENU_ITEM).name("foo").text("Foo").build();

		IllegalStateException exception = assertThrows(IllegalStateException.class,
			() -> new MenuBuilder().buildMenuComponent(item));
		assertTrue(exception.getMessage().contains("'foo'"));

		JMenuItem disabled = new MenuBuilder().withMissingActionPolicy(MissingActionPolicy.DISABLE)
			.buildMenuComponent(item);
		assertFalse(disabled.isEnabled());

		JMenuItem ignored = new MenuBuilder().withMissingActionPolicy(MissingActionPolicy.IGNORE)
			.buildMenuComponent(item);
		assertTrue(ignored.isEnabled());
		assertEquals(0, ignored.getActionListeners().length);

		// a menu without action is fine with the FAIL policy
		JMenu menu = new MenuBuilder()
			.buildMenu(MenuInfo.builder().type(MenuType.MENU).name("bar").text("Bar").build());
		assertEquals("Bar", menu.getText());
	}

	@Test
	void actionIdFallsBackToName()
	{
		List<String> fired = new ArrayList<>();
		ActionRegistry actions = ActionRegistry.empty().register("foo",
			e -> fired.add(e.getActionCommand()));
		JMenuItem item = new MenuBuilder(actions).buildMenuComponent(MenuInfo.builder()
			.type(MenuType.MENU_ITEM).name("foo").actionCommand("custom").build());
		item.doClick();
		assertEquals(List.of("custom"), fired);
	}

	@Test
	void anchorsAreApplied()
	{
		MenuInfo menu = MenuInfo.builder().type(MenuType.MENU).name("m").build()
			.addChild(MenuInfo.builder().type(MenuType.MENU_ITEM).name("b").build())
			.addChild(MenuInfo.builder().type(MenuType.MENU_ITEM).name("c").build())
			.addChild(MenuInfo.builder().type(MenuType.MENU_ITEM).name("a")
				.anchor(io.github.astrapi69.swing.menu.enumeration.Anchor.FIRST).build())
			.addChild(MenuInfo.builder().type(MenuType.MENU_ITEM).name("bb")
				.anchor(io.github.astrapi69.swing.menu.enumeration.Anchor.AFTER)
				.relativeToMenuId("b").build());
		JMenu built = new MenuBuilder().withMissingActionPolicy(MissingActionPolicy.IGNORE)
			.buildMenu(menu);
		assertEquals("a", built.getItem(0).getName());
		assertEquals("b", built.getItem(1).getName());
		assertEquals("bb", built.getItem(2).getName());
		assertEquals("c", built.getItem(3).getName());
	}

	@Test
	void resourceBundleAndIconResolver()
	{
		java.util.ResourceBundle bundle = new java.util.ListResourceBundle()
		{
			@Override
			protected Object[][] getContents()
			{
				return new Object[][] { { "menu.file", "Datei" } };
			}
		};
		MenuBuilder builder = new MenuBuilder().withResourceBundle(bundle)
			.withIconResolver(path -> null).withMissingActionPolicy(MissingActionPolicy.IGNORE);
		JMenu menu = builder.buildMenu(
			MenuInfo.builder().type(MenuType.MENU).name("m").textKey("menu.file").build());
		assertEquals("Datei", menu.getText());
		JMenu unresolved = builder.buildMenu(
			MenuInfo.builder().type(MenuType.MENU).name("m2").textKey("menu.unknown").build());
		assertEquals("menu.unknown", unresolved.getText());
		JMenuItem withIcon = builder.buildMenuComponent(MenuInfo.builder().type(MenuType.MENU_ITEM)
			.name("i").text("I").icon("icons/missing.png").build());
		assertNull(withIcon.getIcon());
		assertNotNull(withIcon.getText());
	}

	@Test
	void wrongRootTypeIsRejected()
	{
		MenuInfo popup = MenuInfo.builder().type(MenuType.POPUP).name("p").build();
		assertThrows(IllegalArgumentException.class, () -> new MenuBuilder().buildMenuBar(popup));
		assertThrows(IllegalArgumentException.class, () -> new MenuBuilder().buildToolBar(popup));
		assertThrows(IllegalArgumentException.class, () -> new MenuBuilder().buildMenu(popup));
		assertThrows(IllegalArgumentException.class,
			() -> new MenuBuilder().buildMenuComponent(popup));
	}
}
