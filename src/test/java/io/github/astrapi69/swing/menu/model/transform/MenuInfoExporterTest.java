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
package io.github.astrapi69.swing.menu.model.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import org.junit.jupiter.api.Test;

import io.github.astrapi69.swing.menu.build.MenuBuilder;
import io.github.astrapi69.swing.menu.build.MissingActionPolicy;
import io.github.astrapi69.swing.menu.enumeration.MenuType;
import io.github.astrapi69.swing.menu.model.MenuInfo;
import io.github.astrapi69.swing.menu.xml.MenuXmlReader;
import io.github.astrapi69.swing.menu.xml.MenuXmlWriter;

/**
 * The unit test class for the class {@link MenuInfoExporter}
 */
class MenuInfoExporterTest
{

	static class ExitAction extends javax.swing.AbstractAction
	{
		ExitAction()
		{
			super("Exit");
		}

		@Override
		public void actionPerformed(java.awt.event.ActionEvent e)
		{
		}
	}

	@Test
	void exportOfBuiltMenuBarKeepsStructure()
	{
		MenuInfo original = MenuXmlReader.readResource("menubar.xml");
		MenuBuilder builder = new MenuBuilder().withMissingActionPolicy(MissingActionPolicy.IGNORE);
		JMenuBar menuBar = builder.buildMenuBar(original);

		MenuInfo exported = MenuInfoExporter.fromJMenuBar(menuBar);

		assertEquals(original.getName(), exported.getName());
		assertEquals(4, exported.getChildren().size());
		MenuInfo file = exported.getChildren().get(0);
		assertEquals("global.menu.file", file.getName());
		assertEquals("File", file.getText());
		assertEquals((int)'F', file.getMnemonic());
		assertEquals(
			java.util.Arrays.asList("global.menu.file.new", "global.menu.file.open", null,
				"global.menu.file.toggle.fullscreen", "global.menu.file.exit"),
			file.getChildren().stream().map(MenuInfo::getName).toList());
		assertEquals(MenuType.SEPARATOR, file.getChildren().get(2).getType());
		MenuInfo exit = file.getChildren().get(4);
		assertEquals(KeyStroke.getKeyStroke("alt F4"), exit.getKeyStrokeInfo().toKeyStroke());
		assertEquals("Exit the application", exit.getToolTip());
		assertEquals(Boolean.FALSE, exported.getChildren().get(1).getEnabled());
		MenuInfo view = exported.getChildren().get(2);
		assertEquals(MenuType.CHECK_BOX_MENU_ITEM, view.getChildren().get(0).getType());
		assertEquals(Boolean.TRUE, view.getChildren().get(0).getSelected());
		MenuInfo desktop = view.getChildren().get(2);
		assertEquals(MenuType.RADIO_BUTTON_MENU_ITEM, desktop.getType());
		assertNotNull(desktop.getGroup());
		assertEquals(desktop.getGroup(), view.getChildren().get(3).getGroup());

		// the export can be written and read again
		MenuInfo reread = MenuXmlReader.fromXml(MenuXmlWriter.toXml(exported));
		assertEquals(exported, reread);
	}

	@Test
	void exportOfProgrammaticMenuGeneratesIds()
	{
		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("File");
		file.setMnemonic('F');
		JMenuItem open = new JMenuItem("Open File...");
		open.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
		open.setActionCommand("open");
		file.add(open);
		file.addSeparator();
		JMenu sub = new JMenu("Recent");
		sub.add(new JMenuItem("a.txt"));
		file.add(sub);
		JCheckBoxMenuItem check = new JCheckBoxMenuItem("Auto save", true);
		check.setEnabled(false);
		file.add(check);
		ButtonGroup group = new ButtonGroup();
		JRadioButtonMenuItem r1 = new JRadioButtonMenuItem("One", true);
		JRadioButtonMenuItem r2 = new JRadioButtonMenuItem("Two");
		group.add(r1);
		group.add(r2);
		file.add(r1);
		file.add(r2);
		menuBar.add(file);

		MenuInfo exported = MenuInfoExporter.fromJMenuBar(menuBar);
		assertEquals("global.menu.bar", exported.getName());
		MenuInfo fileInfo = exported.getChildren().get(0);
		assertEquals("global.menu.bar.file", fileInfo.getName());
		assertEquals((int)'F', fileInfo.getMnemonic());
		MenuInfo openInfo = fileInfo.getChildren().get(0);
		assertEquals("global.menu.bar.file.open.file", openInfo.getName());
		assertEquals("open", openInfo.getActionCommand());
		assertEquals(KeyStroke.getKeyStroke("ctrl O"), openInfo.getKeyStrokeInfo().toKeyStroke());
		assertEquals(MenuType.SEPARATOR, fileInfo.getChildren().get(1).getType());
		MenuInfo subInfo = fileInfo.getChildren().get(2);
		assertEquals(MenuType.MENU, subInfo.getType());
		assertEquals("global.menu.bar.file.recent.a.txt", subInfo.getChildren().get(0).getName());
		MenuInfo checkInfo = fileInfo.getChildren().get(3);
		assertEquals(Boolean.TRUE, checkInfo.getSelected());
		assertEquals(Boolean.FALSE, checkInfo.getEnabled());
		assertNull(checkInfo.getGroup());
		assertEquals("group1", fileInfo.getChildren().get(4).getGroup());
		assertEquals("group1", fileInfo.getChildren().get(5).getGroup());
		assertNull(fileInfo.getChildren().get(5).getSelected());
		// the action command equal to the text is not exported
		assertNull(subInfo.getChildren().get(0).getActionCommand());
	}

	@Test
	void exportOfPopupAndToolBar()
	{
		JPopupMenu popup = new JPopupMenu("Tree");
		popup.setName("tree.popup");
		popup.add(new JMenuItem("Add"));
		popup.addSeparator();
		popup.add(new JMenu("More"));
		MenuInfo popupInfo = MenuInfoExporter.fromJPopupMenu(popup);
		assertEquals(MenuType.POPUP, popupInfo.getType());
		assertEquals("tree.popup", popupInfo.getName());
		assertEquals("Tree", popupInfo.getText());
		assertEquals(3, popupInfo.getChildren().size());
		assertEquals("tree.popup.add", popupInfo.getChildren().get(0).getName());

		JToolBar toolBar = new JToolBar();
		toolBar.add(new JButton("New"));
		toolBar.addSeparator();
		toolBar.add(new JToggleButton("Status", true));
		toolBar.add(new JLabel("ignored"));
		MenuInfo toolBarInfo = MenuInfoExporter.fromJToolBar(toolBar);
		assertEquals(MenuType.TOOL_BAR, toolBarInfo.getType());
		assertEquals(3, toolBarInfo.getChildren().size());
		assertEquals(MenuType.MENU_ITEM, toolBarInfo.getChildren().get(0).getType());
		assertEquals(MenuType.SEPARATOR, toolBarInfo.getChildren().get(1).getType());
		assertEquals(MenuType.CHECK_BOX_MENU_ITEM, toolBarInfo.getChildren().get(2).getType());
		assertEquals(Boolean.TRUE, toolBarInfo.getChildren().get(2).getSelected());

		assertEquals(MenuType.TOOL_BAR, MenuInfoExporter.fromComponent(toolBar).getType());
		assertEquals(MenuType.POPUP, MenuInfoExporter.fromComponent(popup).getType());
		assertEquals(MenuType.MENU_ITEM,
			MenuInfoExporter.fromComponent(new JMenuItem("x")).getType());
		assertNull(MenuInfoExporter.fromComponent(new JLabel("x")));
	}

	@Test
	void actionIdStrategiesAndToolBarOptions()
	{
		ExitAction exit = new ExitAction();
		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem exitItem = new JMenuItem(exit);
		exitItem.getAccessibleContext().setAccessibleName("Exit the application");
		exitItem.getAccessibleContext().setAccessibleDescription("Closes all windows");
		file.add(exitItem);
		file.add(new JMenuItem("Plain"));
		menuBar.add(file);

		MenuInfo byClass = MenuInfoExporter
			.withActionIds(MenuInfoExporter.actionIdFromActionClass()).export(menuBar);
		MenuInfo exitInfo = byClass.getChildren().get(0).getChildren().get(0);
		assertEquals("exitAction", exitInfo.getActionId());
		assertEquals("Exit the application", exitInfo.getAccessibleName());
		assertEquals("Closes all windows", exitInfo.getAccessibleDescription());
		assertNull(byClass.getChildren().get(0).getChildren().get(1).getActionId());
		assertNull(byClass.getChildren().get(0).getChildren().get(1).getAccessibleName());

		MenuInfo byName = MenuInfoExporter.withActionIds(MenuInfoExporter.actionIdFromActionName())
			.export(menuBar);
		assertEquals("Exit", byName.getChildren().get(0).getChildren().get(0).getActionId());
		assertNull(MenuInfoExporter.fromJMenuBar(menuBar).getChildren().get(0).getChildren().get(0)
			.getActionId());

		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.setRollover(true);
		toolBar.add(new JButton("x"));
		MenuInfo toolBarInfo = MenuInfoExporter.withActionIds(button -> "tb").export(toolBar);
		assertEquals(Boolean.FALSE, toolBarInfo.getFloatable());
		assertEquals(Boolean.TRUE, toolBarInfo.getRollover());
		assertEquals("tb", toolBarInfo.getChildren().get(0).getActionId());
		assertEquals(MenuType.MENU_ITEM,
			MenuInfoExporter.withActionIds(button -> null).export(new JMenuItem("i")).getType());
	}
}
