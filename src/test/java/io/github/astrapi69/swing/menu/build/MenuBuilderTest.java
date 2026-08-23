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
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
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
	void actionResolverChain()
	{
		List<String> fired = new ArrayList<>();
		ActionRegistry actions = ActionRegistry.empty().register("fromRegistry",
			e -> fired.add("registry"));
		ActionResolver first = id -> "fromFirst".equals(id)
			? java.util.Optional.of((ActionListener)e -> fired.add("first"))
			: java.util.Optional.empty();
		ActionResolver second = ActionResolver.of(id -> java.util.Optional.empty(),
			id -> "fromSecond".equals(id)
				? java.util.Optional.of((ActionListener)e -> fired.add("second"))
				: java.util.Optional.empty());
		MenuBuilder builder = new MenuBuilder(actions).withActionResolver(first)
			.withActionResolver(second);
		for (String id : List.of("fromRegistry", "fromFirst", "fromSecond"))
		{
			builder.buildMenuComponent(MenuInfo.builder().type(MenuType.MENU_ITEM).name(id).build())
				.doClick();
		}
		assertEquals(List.of("registry", "first", "second"), fired);
		assertThrows(IllegalStateException.class, () -> builder.buildMenuComponent(
			MenuInfo.builder().type(MenuType.MENU_ITEM).name("unknown").build()));
	}

	private static MenuInfo anchored(final MenuInfo menuInfo,
		final io.github.astrapi69.swing.menu.enumeration.Anchor anchor, final String relativeTo)
	{
		menuInfo.setAnchor(anchor);
		menuInfo.setRelativeToMenuId(relativeTo);
		return menuInfo;
	}

	@Test
	void insertAtRuntime()
	{
		List<String> fired = new ArrayList<>();
		MenuBuilder builder = new MenuBuilder(newActions(fired))
			.withMissingActionPolicy(MissingActionPolicy.IGNORE);
		JMenuBar menuBar = builder.buildMenuBar(MenuXmlReader.readResource("menubar.xml"));

		// a plugin menu between File and Edit
		MenuInfo pluginMenu = anchored(
			MenuInfo.builder().type(MenuType.MENU).name("plugin.menu").text("Plugin").build(),
			io.github.astrapi69.swing.menu.enumeration.Anchor.AFTER, "global.menu.file")
				.addChild(MenuInfo.builder().type(MenuType.MENU_ITEM).name("plugin.menu.settings")
					.text("Settings").actionId("pluginSettings").build());
		JComponent inserted = builder.insert("global.menu.bar", pluginMenu);
		assertInstanceOf(JMenu.class, inserted);
		assertEquals(5, menuBar.getMenuCount());
		assertEquals("plugin.menu", menuBar.getMenu(1).getName());
		assertTrue(builder.getComponent("plugin.menu.settings", JMenuItem.class).isPresent());

		// an item before Exit and a separator before it
		JMenu file = builder.getComponent("global.menu.file", JMenu.class).orElseThrow();
		int before = file.getMenuComponentCount();
		builder.insert("global.menu.file",
			anchored(
				MenuInfo.builder().type(MenuType.MENU_ITEM).name("plugin.file.export")
					.text("Export...").actionId("export").build(),
				io.github.astrapi69.swing.menu.enumeration.Anchor.BEFORE, "global.menu.file.exit"));
		builder.insert("global.menu.file",
			anchored(MenuInfo.builder().type(MenuType.SEPARATOR).build(),
				io.github.astrapi69.swing.menu.enumeration.Anchor.BEFORE, "plugin.file.export"));
		assertEquals(before + 2, file.getMenuComponentCount());
		assertInstanceOf(JSeparator.class, file.getMenuComponent(before - 1));
		assertEquals("plugin.file.export", file.getMenuComponent(before).getName());
		assertEquals("global.menu.file.exit", file.getMenuComponent(before + 1).getName());
		builder.getComponent("plugin.file.export", JMenuItem.class).orElseThrow().doClick();
		assertEquals(List.of("plugin.file.export"), fired);

		// FIRST, unknown relativeTo appends, LAST appends
		builder.insert("global.menu.file",
			anchored(
				MenuInfo.builder().type(MenuType.MENU_ITEM).name("first").text("First").build(),
				io.github.astrapi69.swing.menu.enumeration.Anchor.FIRST, null));
		assertEquals("first", file.getMenuComponent(0).getName());
		builder.insert("global.menu.file",
			anchored(
				MenuInfo.builder().type(MenuType.MENU_ITEM).name("orphan").text("Orphan").build(),
				io.github.astrapi69.swing.menu.enumeration.Anchor.AFTER, "nope"));
		assertEquals("orphan", file.getMenuComponent(file.getMenuComponentCount() - 1).getName());

		// popup and tool bar
		JPopupMenu popup = builder.buildPopupMenu(MenuXmlReader.readResource("popup.xml"));
		builder.insert("tree.popup",
			anchored(
				MenuInfo.builder().type(MenuType.MENU_ITEM).name("tree.popup.copy").text("Copy")
					.build(),
				io.github.astrapi69.swing.menu.enumeration.Anchor.AFTER, "tree.popup.add"));
		builder.insert("tree.popup", anchored(MenuInfo.builder().type(MenuType.SEPARATOR).build(),
			io.github.astrapi69.swing.menu.enumeration.Anchor.FIRST, null));
		assertInstanceOf(JSeparator.class, popup.getComponent(0));
		assertEquals("tree.popup.copy", popup.getComponent(2).getName());

		JToolBar toolBar = builder.buildToolBar(MenuXmlReader.readResource("toolbar.xml"));
		builder.insert("global.tool.bar",
			anchored(
				MenuInfo.builder().type(MenuType.MENU_ITEM).name("global.tool.bar.export")
					.text("Export").actionId("export").build(),
				io.github.astrapi69.swing.menu.enumeration.Anchor.BEFORE, "global.tool.bar.open"));
		assertEquals("global.tool.bar.export", toolBar.getComponent(1).getName());
		assertInstanceOf(JButton.class, toolBar.getComponent(1));

		// invalid parents
		assertThrows(IllegalArgumentException.class,
			() -> builder.insert("unknown.parent", pluginMenu));
		assertThrows(IllegalArgumentException.class,
			() -> builder.insert("global.menu.file.exit", pluginMenu));
	}

	@Test
	void removeAtRuntime()
	{
		MenuBuilder builder = new MenuBuilder().withMissingActionPolicy(MissingActionPolicy.IGNORE);
		JMenuBar menuBar = builder.buildMenuBar(MenuXmlReader.readResource("menubar.xml"));
		assertTrue(builder.getComponent("global.menu.file.exit").isPresent());

		assertTrue(builder.remove("global.menu.file.exit").isPresent());
		JMenu file = builder.getComponent("global.menu.file", JMenu.class).orElseThrow();
		assertEquals(4, file.getMenuComponentCount());
		assertFalse(builder.getComponent("global.menu.file.exit").isPresent());

		// removing a menu forgets its items as well
		assertTrue(builder.remove("global.menu.view").isPresent());
		assertEquals(3, menuBar.getMenuCount());
		assertFalse(builder.getComponent("global.menu.view").isPresent());
		assertFalse(builder.getComponent("global.menu.view.statusbar").isPresent());
		assertFalse(builder.getComponent("global.menu.view.mode.panel").isPresent());
		assertTrue(builder.getComponent("global.menu.help.info").isPresent());

		assertFalse(builder.remove("nope").isPresent());
	}

	@Test
	void swingActionsAreBoundWithSetAction()
	{
		javax.swing.AbstractAction save = new javax.swing.AbstractAction("Save from action")
		{
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e)
			{
			}
		};
		ActionRegistry actions = ActionRegistry.empty().register("save", save);
		MenuBuilder builder = new MenuBuilder(actions);
		JMenuItem withText = builder.buildMenuComponent(
			MenuInfo.builder().type(MenuType.MENU_ITEM).name("save").text("Save").build());
		JMenuItem withoutText = builder.buildMenuComponent(
			MenuInfo.builder().type(MenuType.MENU_ITEM).name("save.2").actionId("save").build());
		JToolBar toolBar = builder.buildToolBar(MenuInfo.builder().type(MenuType.TOOL_BAR)
			.name("tb").build().addChild(MenuInfo.builder().type(MenuType.MENU_ITEM).name("tb.save")
				.actionId("save").text("Save").build()));
		JButton button = (JButton)toolBar.getComponent(0);

		// the explicit text wins, the action text is the fallback
		assertEquals("Save", withText.getText());
		assertEquals("Save from action", withoutText.getText());
		assertEquals(save, withText.getAction());
		assertEquals(1, withText.getActionListeners().length);

		// the enabled state of the action is propagated to all bound components
		save.setEnabled(false);
		assertFalse(withText.isEnabled());
		assertFalse(withoutText.isEnabled());
		assertFalse(button.isEnabled());
		save.setEnabled(true);
		assertTrue(withText.isEnabled());
		assertTrue(button.isEnabled());
	}

	@Test
	void visibleAndAmpersandMnemonicFromResolvedText()
	{
		MenuBuilder builder = new MenuBuilder().withMissingActionPolicy(MissingActionPolicy.IGNORE)
			.withTextResolver(key -> "menu.file".equals(key) ? "&Datei" : null);
		JMenu menu = builder
			.buildMenu(MenuInfo.builder().type(MenuType.MENU).name("m").textKey("menu.file").build()
				.addChild(MenuInfo.builder().type(MenuType.MENU_ITEM).name("hidden").text("&Hidden")
					.visible(false).build())
				.addChild(MenuInfo.builder().type(MenuType.MENU_ITEM).name("explicit").text("&Text")
					.mnemonic((int)'X').build()));
		assertEquals("Datei", menu.getText());
		assertEquals((int)'D', menu.getMnemonic());
		assertFalse(menu.getItem(0).isVisible());
		assertEquals("Hidden", menu.getItem(0).getText());
		assertEquals((int)'H', menu.getItem(0).getMnemonic());
		assertEquals((int)'X', menu.getItem(1).getMnemonic());
	}

	@Test
	void awtPopupMenuForTray()
	{
		org.junit.jupiter.api.Assumptions.assumeFalse(java.awt.GraphicsEnvironment.isHeadless());
		List<String> fired = new ArrayList<>();
		ActionRegistry actions = ActionRegistry.empty()
			.register("show", e -> fired.add("show:" + e.getActionCommand()))
			.register("mute", e -> fired.add("mute:" + e.getActionCommand()));
		MenuBuilder builder = new MenuBuilder(actions)
			.withMissingActionPolicy(MissingActionPolicy.DISABLE);
		MenuInfo trayInfo = MenuXmlReader.fromXml("<tray id=\"tray\" text=\"App\">"
			+ "<item id=\"tray.show\" text=\"&amp;Show\" action=\"show\" accelerator=\"shift ctrl S\"/>"
			+ "<checkbox id=\"tray.mute\" text=\"Mute\" action=\"mute\" selected=\"true\"/>"
			+ "<separator/><menu id=\"tray.more\" text=\"More\"><item id=\"tray.more.x\" text=\"X\"/></menu>"
			+ "<item id=\"tray.quit\" text=\"Quit\"/></tray>");
		java.awt.PopupMenu popup = builder.buildAwtPopupMenu(trayInfo);
		assertEquals("App", popup.getLabel());
		assertEquals(5, popup.getItemCount());
		java.awt.MenuItem show = popup.getItem(0);
		assertEquals("Show", show.getLabel());
		assertNotNull(show.getShortcut());
		assertTrue(show.getShortcut().usesShiftModifier());
		java.awt.CheckboxMenuItem mute = (java.awt.CheckboxMenuItem)popup.getItem(1);
		assertTrue(mute.getState());
		assertEquals("-", popup.getItem(2).getLabel());
		assertInstanceOf(java.awt.Menu.class, popup.getItem(3));
		assertFalse(popup.getItem(4).isEnabled());
		assertTrue(builder.getAwtComponent("tray.more.x").isPresent());
		assertFalse(builder.getAwtComponent("nope").isPresent());
		assertThrows(IllegalArgumentException.class, () -> builder
			.buildAwtPopupMenu(MenuInfo.builder().type(MenuType.MENU_BAR).name("b").build()));
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
