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

import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JToolBar;

import org.junit.jupiter.api.Test;

import io.github.astrapi69.swing.menu.enumeration.MenuType;
import io.github.astrapi69.swing.menu.model.MenuInfo;

/**
 * Additional unit tests for {@link MenuInfoExporter} written against the pitest mutation report to
 * exercise branches that {@link MenuInfoExporterTest} and {@link MenuInfoExporterParameterizedTest}
 * did not distinguish (both sides of a null check, both sides of a boolean flag)
 */
class MenuInfoExporterMutationCoverageTest
{

	@Test
	void menuBarRootEnabledVisibleToolTipAndAccessible()
	{
		JMenuBar menuBar = new JMenuBar();
		menuBar.setEnabled(false);
		menuBar.setVisible(false);
		menuBar.setToolTipText("Bar tip");
		menuBar.getAccessibleContext().setAccessibleName("Bar name");
		menuBar.getAccessibleContext().setAccessibleDescription("Bar description");

		MenuInfo exported = MenuInfoExporter.fromJMenuBar(menuBar);

		assertEquals(Boolean.FALSE, exported.getEnabled());
		assertEquals(Boolean.FALSE, exported.getVisible());
		assertEquals("Bar tip", exported.getToolTip());
		assertEquals("Bar name", exported.getAccessibleName());
		assertEquals("Bar description", exported.getAccessibleDescription());

		JMenuBar defaultBar = new JMenuBar();
		MenuInfo defaultExported = MenuInfoExporter.fromJMenuBar(defaultBar);
		assertNull(defaultExported.getEnabled());
		assertNull(defaultExported.getVisible());
	}

	@Test
	void popupMenuRootEnabledAndAccessible()
	{
		JPopupMenu popup = new JPopupMenu("Tree");
		popup.setEnabled(false);
		popup.getAccessibleContext().setAccessibleName("Popup name");
		popup.getAccessibleContext().setAccessibleDescription("Popup description");

		MenuInfo exported = MenuInfoExporter.fromJPopupMenu(popup);

		assertEquals(Boolean.FALSE, exported.getEnabled());
		assertEquals("Popup name", exported.getAccessibleName());
		assertEquals("Popup description", exported.getAccessibleDescription());

		JPopupMenu defaultPopup = new JPopupMenu();
		assertNull(MenuInfoExporter.fromJPopupMenu(defaultPopup).getEnabled());
	}

	@Test
	void toolBarRootAccessible()
	{
		JToolBar toolBar = new JToolBar();
		toolBar.getAccessibleContext().setAccessibleName("Toolbar name");
		toolBar.getAccessibleContext().setAccessibleDescription("Toolbar description");

		MenuInfo exported = MenuInfoExporter.fromJToolBar(toolBar);

		assertEquals("Toolbar name", exported.getAccessibleName());
		assertEquals("Toolbar description", exported.getAccessibleDescription());
	}

	@Test
	void ampersandInTextIsEscapedOnExport()
	{
		JMenuItem item = new JMenuItem("Save & Exit");
		MenuInfo exported = MenuInfoExporter.fromComponent(item);
		assertEquals("Save && Exit", exported.getText());
	}

	@Test
	void emptyPopupLabelIsExportedAsNull()
	{
		JPopupMenu popup = new JPopupMenu();
		popup.setLabel("");
		assertNull(MenuInfoExporter.fromJPopupMenu(popup).getText());

		JPopupMenu noLabel = new JPopupMenu();
		assertNull(MenuInfoExporter.fromJPopupMenu(noLabel).getText());
	}

	@Test
	void iconPathIsExportedOnlyForANonBlankImageIconDescription()
	{
		BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

		JMenuItem withDescription = new JMenuItem("Save");
		withDescription.setIcon(new ImageIcon(image, "icons/save.png"));
		assertEquals("icons/save.png", MenuInfoExporter.fromComponent(withDescription).getIcon());

		JMenuItem blankDescription = new JMenuItem("Save");
		blankDescription.setIcon(new ImageIcon(image, "   "));
		assertNull(MenuInfoExporter.fromComponent(blankDescription).getIcon());

		JMenuItem nullDescription = new JMenuItem("Save");
		nullDescription.setIcon(new ImageIcon(image, null));
		assertNull(MenuInfoExporter.fromComponent(nullDescription).getIcon());

		JMenuItem customIcon = new JMenuItem("Save");
		customIcon.setIcon(new Icon()
		{
			@Override
			public void paintIcon(final Component c, final Graphics g, final int x, final int y)
			{
			}

			@Override
			public int getIconWidth()
			{
				return 1;
			}

			@Override
			public int getIconHeight()
			{
				return 1;
			}
		});
		assertNull(MenuInfoExporter.fromComponent(customIcon).getIcon());
	}

	@Test
	void actionIdFromActionNameIsNullWhenTheActionHasNoName()
	{
		Action noName = new AbstractAction()
		{
			@Override
			public void actionPerformed(final ActionEvent event)
			{
			}
		};
		JMenuItem item = new JMenuItem();
		item.setAction(noName);

		MenuInfo exported = MenuInfoExporter
			.withActionIds(MenuInfoExporter.actionIdFromActionName()).export(item);

		assertNull(exported.getActionId());
	}

	@Test
	void generatedNameFallsBackToActionNameThenIcon()
	{
		JMenu parent = new JMenu("Parent");
		parent.setName("parent");

		Action namedAction = new AbstractAction()
		{
			{
				putValue(Action.NAME, "Save Now");
			}

			@Override
			public void actionPerformed(final ActionEvent event)
			{
			}
		};
		JMenuItem fromActionName = new JMenuItem();
		fromActionName.setAction(namedAction);
		fromActionName.setText(null);
		parent.add(fromActionName);

		Action unnamedAction = new AbstractAction()
		{
			@Override
			public void actionPerformed(final ActionEvent event)
			{
			}
		};
		JMenuItem fromIcon = new JMenuItem();
		fromIcon.setAction(unnamedAction);
		fromIcon.setText(null);
		fromIcon.setIcon(new ImageIcon(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
			"icons/save-file.png"));
		parent.add(fromIcon);

		MenuInfo exported = MenuInfoExporter.fromComponent(parent);

		assertEquals("parent.save.now", exported.getChildren().get(0).getName());
		assertEquals("parent.icons.save.file.png", exported.getChildren().get(1).getName());
	}

	@Test
	void generatedNameCollisionSuffixesIncrementSequentially()
	{
		JMenu parent = new JMenu("Parent");
		parent.setName("parent");
		parent.add(new JMenuItem("Duplicate"));
		parent.add(new JMenuItem("Duplicate"));
		parent.add(new JMenuItem("Duplicate"));

		MenuInfo exported = MenuInfoExporter.fromComponent(parent);

		assertEquals(List.of("parent.duplicate", "parent.duplicate.2", "parent.duplicate.3"),
			exported.getChildren().stream().map(MenuInfo::getName).toList());
	}

	@Test
	void unsupportedChildComponentsAreSkippedInMenuAndPopup()
	{
		JMenu menu = new JMenu("Parent");
		menu.setName("parent");
		menu.add(new JMenuItem("Keep"));
		menu.add(new JLabel("ignored"));
		assertEquals(1, MenuInfoExporter.fromComponent(menu).getChildren().size());

		JPopupMenu popup = new JPopupMenu();
		popup.add(new JMenuItem("Keep"));
		popup.add(new JLabel("ignored"));
		assertEquals(1, MenuInfoExporter.fromJPopupMenu(popup).getChildren().size());
	}

	@Test
	void plainMenuItemNeverExportsSelectedOrGroupEvenWhenGrouped()
	{
		JMenuItem item = new JMenuItem("Item");
		new ButtonGroup().add(item);

		MenuInfo exported = MenuInfoExporter.fromComponent(item);

		assertNull(exported.getSelected());
		assertNull(exported.getGroup());
	}

	@Test
	void bothSeparatorKindsAreExportedAsSeparatorInAToolBar()
	{
		JToolBar toolBar = new JToolBar();
		toolBar.add(new JToolBar.Separator());
		toolBar.add(new JSeparator());
		toolBar.add(new JButton("Keep"));

		MenuInfo exported = MenuInfoExporter.fromJToolBar(toolBar);

		assertEquals(3, exported.getChildren().size());
		assertEquals(MenuType.SEPARATOR, exported.getChildren().get(0).getType());
		assertEquals(MenuType.SEPARATOR, exported.getChildren().get(1).getType());
		assertEquals(MenuType.MENU_ITEM, exported.getChildren().get(2).getType());
	}

	@Test
	void groupNameIsOnlyAssignedToActuallyGroupedButtons()
	{
		JMenu menu = new JMenu("Parent");
		menu.setName("parent");
		ButtonGroup group = new ButtonGroup();
		JRadioButtonMenuItem grouped = new JRadioButtonMenuItem("Grouped");
		group.add(grouped);
		JRadioButtonMenuItem ungrouped = new JRadioButtonMenuItem("Ungrouped");
		menu.add(grouped);
		menu.add(ungrouped);

		MenuInfo exported = MenuInfoExporter.fromComponent(menu);

		assertNotNull(exported.getChildren().get(0).getGroup());
		assertNull(exported.getChildren().get(1).getGroup());
	}
}
