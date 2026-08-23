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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.PopupMenu;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.junit.jupiter.api.Test;

import io.github.astrapi69.swing.menu.model.MenuItemInfo;
import io.github.astrapi69.swing.menu.popup.listener.PopupListener;

/**
 * The unit test class for the class {@link JPopupMenuFactory}
 */
class JPopupMenuFactoryTest
{

	private static PopupListener popupListenerOf(final Component component)
	{
		PopupListener found = null;
		for (MouseListener listener : component.getMouseListeners())
		{
			if (listener instanceof PopupListener popupListener)
			{
				found = popupListener;
			}
		}
		return found;
	}

	private static MouseEvent popupTrigger(final Component component)
	{
		return new MouseEvent(component, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(),
			InputEvent.BUTTON3_DOWN_MASK, 5, 7, 1, true, MouseEvent.BUTTON3);
	}

	private static void assertPopupBoundTo(final Component component, final JPopupMenu popup)
	{
		PopupListener listener = popupListenerOf(component);
		assertInstanceOf(PopupListener.class, listener);
		assertNull(popup.getInvoker());
		try
		{
			// the component is not showing so the popup can not be displayed, but the listener
			// passes the component as the invoker to the popup before that
			listener.mousePressed(popupTrigger(component));
		}
		catch (RuntimeException expected)
		{
			// IllegalComponentStateException or HeadlessException
		}
		assertSame(component, popup.getInvoker());
	}

	@Test
	void newJPopupMenu()
	{
		JPopupMenu popup = JPopupMenuFactory.newJPopupMenu();

		assertEquals("", popup.getLabel());
		assertEquals(0, popup.getComponentCount());
		assertNotSame(popup, JPopupMenuFactory.newJPopupMenu());
	}

	@Test
	void newJPopupMenuWithLabel()
	{
		JPopupMenu popup = JPopupMenuFactory.newJPopupMenu("Edit");

		assertEquals("Edit", popup.getLabel());
		assertEquals(0, popup.getComponentCount());
		assertNull(JPopupMenuFactory.newJPopupMenu((String)null).getLabel());
	}

	@Test
	void newJPopupMenuWithComponentAndItems()
	{
		JLabel label = new JLabel("tree");
		assertEquals(0, label.getMouseListeners().length);
		JMenuItem add = JMenuItemFactory.newJMenuItem("Add");
		JMenuItem delete = JMenuItemFactory.newJMenuItem("Delete");

		JPopupMenu popup = JPopupMenuFactory.newJPopupMenu(label, add, delete);

		assertEquals("", popup.getLabel());
		assertEquals(2, popup.getComponentCount());
		assertSame(add, popup.getComponent(0));
		assertSame(delete, popup.getComponent(1));
		assertEquals(1, label.getMouseListeners().length);
		assertPopupBoundTo(label, popup);
	}

	@Test
	void newJPopupMenuWithLabelComponentAndItems()
	{
		JLabel label = new JLabel("tree");
		JMenuItem add = JMenuItemFactory.newJMenuItem("Add");

		JPopupMenu popup = JPopupMenuFactory.newJPopupMenu("Tree", label, add);

		assertEquals("Tree", popup.getLabel());
		assertEquals(1, popup.getComponentCount());
		assertSame(add, popup.getComponent(0));
		assertPopupBoundTo(label, popup);
	}

	@Test
	void newJPopupMenuWithComponentLabelAndItems()
	{
		JLabel label = new JLabel("tree");

		JPopupMenu popup = JPopupMenuFactory.newJPopupMenu(label, "Tree", new JMenuItem[0]);

		assertEquals("Tree", popup.getLabel());
		assertEquals(0, popup.getComponentCount());
		assertPopupBoundTo(label, popup);
		// every call adds a new listener
		JPopupMenuFactory.newJPopupMenu(label, "Other", JMenuItemFactory.newJMenuItem("Rename"));
		assertEquals(2, label.getMouseListeners().length);
		assertThrows(NullPointerException.class,
			() -> JPopupMenuFactory.newJPopupMenu((Component)null, "Tree", new JMenuItem[0]));
	}

	@Test
	void newJPopupMenuWithComponentLabelAndMenuItemInfos()
	{
		List<String> fired = new ArrayList<>();
		JLabel label = new JLabel("tree");
		MenuItemInfo add = MenuItemInfo.builder().name("tree.add").text("Add")
			.mnemonic(KeyEvent.VK_A).actionListener(e -> fired.add(e.getActionCommand())).build();
		MenuItemInfo delete = MenuItemInfo.builder().name("tree.delete").text("Delete")
			.enabled(false).build();

		JPopupMenu popup = JPopupMenuFactory.newJPopupMenu(label, "Tree", add, delete);

		assertEquals("Tree", popup.getLabel());
		assertEquals(2, popup.getComponentCount());
		JMenuItem addItem = (JMenuItem)popup.getComponent(0);
		assertEquals("tree.add", addItem.getName());
		assertEquals("Add", addItem.getText());
		assertEquals(KeyEvent.VK_A, addItem.getMnemonic());
		addItem.doClick();
		assertEquals(List.of("Add"), fired);
		JMenuItem deleteItem = (JMenuItem)popup.getComponent(1);
		assertEquals("tree.delete", deleteItem.getName());
		assertFalse(deleteItem.isEnabled());
		assertPopupBoundTo(label, popup);

		JPopupMenu empty = JPopupMenuFactory.newJPopupMenu(new JLabel(), "Empty",
			new MenuItemInfo[0]);
		assertEquals(0, empty.getComponentCount());
	}

	@Test
	void newPopupMenu()
	{
		// the awt PopupMenu is a heavyweight component and needs a display
		assumeFalse(GraphicsEnvironment.isHeadless());
		List<MenuItemInfo> infos = List.of(
			MenuItemInfo.builder().name("tray.show").text("Show").mnemonic(KeyEvent.VK_S).build(),
			MenuItemInfo.builder().name("tray.exit").text("Exit").enabled(false).build());

		PopupMenu popupMenu = JPopupMenuFactory.newPopupMenu(infos);

		assertEquals(2, popupMenu.getItemCount());
		assertEquals("Show", popupMenu.getItem(0).getLabel());
		assertEquals("tray.show", popupMenu.getItem(0).getName());
		assertEquals(KeyEvent.VK_S, popupMenu.getItem(0).getShortcut().getKey());
		assertEquals("Exit", popupMenu.getItem(1).getLabel());
		assertFalse(popupMenu.getItem(1).isEnabled());
		assertEquals(0, JPopupMenuFactory.newPopupMenu(List.of()).getItemCount());
	}
}
