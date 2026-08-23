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
package io.github.astrapi69.swing.plaf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import io.github.astrapi69.swing.menu.build.ActionRegistry;
import io.github.astrapi69.swing.menu.build.MenuBuilder;
import io.github.astrapi69.swing.menu.enumeration.BaseMenuId;
import io.github.astrapi69.swing.menu.enumeration.MenuType;
import io.github.astrapi69.swing.menu.model.MenuInfo;

/**
 * The unit test class for the class {@link LookAndFeelMenuFactory}
 */
class LookAndFeelMenuFactoryTest
{

	private LookAndFeel previous;

	@AfterEach
	void restore() throws Exception
	{
		if (previous != null)
		{
			UIManager.setLookAndFeel(previous);
		}
	}

	@Test
	void menuAndActions() throws Exception
	{
		previous = UIManager.getLookAndFeel();
		LookAndFeels.setLookAndFeel(LookAndFeels.NIMBUS);

		MenuInfo menuInfo = LookAndFeelMenuFactory.newLookAndFeelMenuInfo("&Look and Feel");
		assertEquals(BaseMenuId.LOOK_AND_FEEL_KEY, menuInfo.getName());
		List<String> ids = LookAndFeelMenuFactory.lookAndFeelIds();
		assertEquals(ids.size(), menuInfo.getChildren().size());
		assertTrue(ids.contains(BaseMenuId.LOOK_AND_FEEL_METAL_KEY));
		assertTrue(ids.contains(BaseMenuId.LOOK_AND_FEEL_NIMBUS_KEY));
		assertTrue(ids.contains(BaseMenuId.LOOK_AND_FEEL_OCEAN_KEY));
		for (MenuInfo child : menuInfo.getChildren())
		{
			assertEquals(MenuType.RADIO_BUTTON_MENU_ITEM, child.getType());
			assertEquals(LookAndFeelMenuFactory.GROUP, child.getGroup());
		}
		long selected = menuInfo.getChildren().stream()
			.filter(child -> Boolean.TRUE.equals(child.getSelected())).count();
		assertEquals(1, selected);

		JLabel component = new JLabel();
		ActionRegistry actions = LookAndFeelMenuFactory.registerActions(ActionRegistry.empty(),
			component);
		assertTrue(actions.ids().containsAll(ids));

		MenuBuilder builder = new MenuBuilder(actions);
		JMenu menu = builder.buildMenu(menuInfo);
		assertEquals("Look and Feel", menu.getText());
		assertEquals((int)'L', menu.getMnemonic());
		assertTrue(builder.getButtonGroup(LookAndFeelMenuFactory.GROUP).isPresent());

		builder.getComponent(BaseMenuId.LOOK_AND_FEEL_METAL_KEY, JRadioButtonMenuItem.class)
			.orElseThrow().doClick();
		assertInstanceOf(MetalLookAndFeel.class, UIManager.getLookAndFeel());

		builder.getComponent(BaseMenuId.LOOK_AND_FEEL_OCEAN_KEY, JRadioButtonMenuItem.class)
			.orElseThrow().doClick();
		assertInstanceOf(OceanTheme.class, MetalLookAndFeel.getCurrentTheme());
		MenuInfo afterOcean = LookAndFeelMenuFactory.newLookAndFeelMenuInfo();
		assertEquals(Boolean.TRUE,
			afterOcean.getChildren().stream()
				.filter(child -> BaseMenuId.LOOK_AND_FEEL_OCEAN_KEY.equals(child.getName()))
				.findFirst().orElseThrow().getSelected());
	}
}
