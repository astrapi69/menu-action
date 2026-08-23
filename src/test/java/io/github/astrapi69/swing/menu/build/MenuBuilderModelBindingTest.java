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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.junit.jupiter.api.Test;

import io.github.astrapi69.model.BaseModel;
import io.github.astrapi69.model.api.IModel;
import io.github.astrapi69.swing.menu.enumeration.MenuType;
import io.github.astrapi69.swing.menu.model.MenuInfo;
import io.github.astrapi69.swing.menu.xml.MenuXmlReader;

/**
 * The unit test class for the model binding, the tool tip keys, the tool bar options and the
 * accessibility of the {@link MenuBuilder}
 */
class MenuBuilderModelBindingTest
{

	enum ViewMode
	{
		DESKTOP, PANEL
	}

	private static final String VIEW_MENU = "<menu id=\"view\" text=\"View\">"
		+ "<checkbox id=\"view.statusbar\" text=\"Statusbar\" model=\"statusbar\"/>"
		+ "<radio id=\"view.mode.desktop\" text=\"Desktop\" group=\"mode\" model=\"viewMode\" value=\"DESKTOP\"/>"
		+ "<radio id=\"view.mode.panel\" text=\"Panel\" group=\"mode\" model=\"viewMode\" value=\"PANEL\"/>"
		+ "</menu>";

	@Test
	void checkBoxAndRadioBinding()
	{
		IModel<Boolean> statusbar = BaseModel.of(Boolean.TRUE);
		IModel<ViewMode> viewMode = BaseModel.of(ViewMode.PANEL);
		MenuBuilder builder = new MenuBuilder().withMissingActionPolicy(MissingActionPolicy.IGNORE)
			.withModels(Map.of("statusbar", statusbar, "viewMode", viewMode));
		JMenu view = builder.buildMenu(MenuXmlReader.fromXml(VIEW_MENU));
		JCheckBoxMenuItem statusbarItem = (JCheckBoxMenuItem)view.getItem(0);
		JRadioButtonMenuItem desktop = (JRadioButtonMenuItem)view.getItem(1);
		JRadioButtonMenuItem panel = (JRadioButtonMenuItem)view.getItem(2);

		// initial state from the models
		assertTrue(statusbarItem.isSelected());
		assertFalse(desktop.isSelected());
		assertTrue(panel.isSelected());

		// the items write the models
		statusbarItem.doClick();
		assertEquals(Boolean.FALSE, statusbar.getObject());
		desktop.doClick();
		assertEquals(ViewMode.DESKTOP, viewMode.getObject());
		assertFalse(panel.isSelected());

		// the models are pulled into the items
		statusbar.setObject(Boolean.TRUE);
		viewMode.setObject(ViewMode.PANEL);
		assertFalse(statusbarItem.isSelected());
		builder.updateFromModels();
		assertTrue(statusbarItem.isSelected());
		assertTrue(panel.isSelected());
		assertFalse(desktop.isSelected());
	}

	@Test
	void modelsFromContextAndValueConversion()
	{
		IModel<Integer> level = BaseModel.of(2);
		ActionContext context = ActionContext.empty().put("level", level).put("other", "x");
		MenuBuilder builder = new MenuBuilder().withMissingActionPolicy(MissingActionPolicy.IGNORE)
			.withModels(context);
		JMenu menu = builder.buildMenu(MenuXmlReader.fromXml("<menu id=\"m\" text=\"M\">"
			+ "<radio id=\"one\" text=\"1\" group=\"g\" model=\"level\" value=\"1\"/>"
			+ "<radio id=\"two\" text=\"2\" group=\"g\" model=\"level\" value=\"2\"/></menu>"));
		assertTrue(menu.getItem(1).isSelected());
		menu.getItem(0).doClick();
		assertEquals(1, level.getObject());

		assertEquals(ViewMode.PANEL,
			MenuBuilder.convertValue(BaseModel.of(ViewMode.DESKTOP), "PANEL"));
		assertEquals(Boolean.TRUE, MenuBuilder.convertValue(BaseModel.of(Boolean.FALSE), "true"));
		assertEquals(7L, MenuBuilder.convertValue(BaseModel.of(1L), "7"));
		assertEquals(1.5d, MenuBuilder.convertValue(BaseModel.of(1d), "1.5"));
		assertEquals("x", MenuBuilder.convertValue(BaseModel.of("s"), "x"));
		assertEquals("x", MenuBuilder.convertValue(BaseModel.of(), "x"));
		assertNull(MenuBuilder.convertValue(BaseModel.of(1), null));

		MenuBuilder custom = new MenuBuilder().withMissingActionPolicy(MissingActionPolicy.IGNORE)
			.withModels(Map.of("level", level)).withValueConverter((model, value) -> 42);
		JMenu customMenu = custom.buildMenu(MenuXmlReader.fromXml("<menu id=\"m\" text=\"M\">"
			+ "<radio id=\"x\" text=\"x\" model=\"level\" value=\"ignored\"/></menu>"));
		customMenu.getItem(0).doClick();
		assertEquals(42, level.getObject());
	}

	@Test
	void missingModelPolicies()
	{
		String xml = "<checkbox id=\"c\" text=\"C\" model=\"missing\"/>";
		IllegalStateException exception = assertThrows(IllegalStateException.class,
			() -> new MenuBuilder(ActionRegistry.empty().register("x", e -> {
			})).withModelResolver(key -> java.util.Optional.empty())
				.buildMenuComponent(MenuInfo.builder().type(MenuType.CHECK_BOX_MENU_ITEM).name("c")
					.model("missing").actionId("x").build()));
		assertTrue(exception.getMessage().contains("missing"));
		JCheckBoxMenuItem unbound = (JCheckBoxMenuItem)new MenuBuilder()
			.withMissingActionPolicy(MissingActionPolicy.DISABLE).withModels(Map.of())
			.buildMenuComponent(MenuXmlReader.fromXml(xml));
		assertFalse(unbound.isSelected());
	}

	@Test
	void toolTipKeyToolBarOptionsAndAccessibility()
	{
		MenuBuilder builder = new MenuBuilder().withMissingActionPolicy(MissingActionPolicy.IGNORE)
			.withTextResolver(key -> switch (key)
			{
			case "tip.new" -> "Creates a new file";
			case "tip.bar" -> "Main tool bar";
			default -> null;
			})
			.withIconResolver(path -> new javax.swing.ImageIcon(new java.awt.image.BufferedImage(2,
				2, java.awt.image.BufferedImage.TYPE_INT_ARGB)));
		MenuInfo toolBarInfo = MenuXmlReader
			.fromXml("<toolbar id=\"tb\" text=\"Main\" toolTipKey=\"tip.bar\""
				+ " floatable=\"false\" rollover=\"true\" showText=\"false\" accessibleName=\"Main bar\""
				+ " accessibleDescription=\"The main tool bar\">"
				+ "<item id=\"tb.new\" text=\"New\" toolTipKey=\"tip.new\" icon=\"x.png\"/>"
				+ "<item id=\"tb.open\" text=\"Open\" icon=\"x.png\"/>"
				+ "<item id=\"tb.text\" text=\"Text only\"/>"
				+ "<item id=\"tb.forced\" text=\"Forced\" icon=\"x.png\" showText=\"true\" accessibleName=\"Forced button\"/>"
				+ "<checkbox id=\"tb.toggle\" text=\"Toggle\" icon=\"x.png\"/></toolbar>");
		JToolBar toolBar = builder.buildToolBar(toolBarInfo);
		assertFalse(toolBar.isFloatable());
		assertTrue(toolBar.isRollover());
		assertEquals("Main tool bar", toolBar.getToolTipText());
		assertEquals("Main bar", toolBar.getAccessibleContext().getAccessibleName());
		assertEquals("The main tool bar",
			toolBar.getAccessibleContext().getAccessibleDescription());

		JButton newButton = (JButton)toolBar.getComponent(0);
		assertNull(newButton.getText());
		assertEquals("Creates a new file", newButton.getToolTipText());
		JButton openButton = (JButton)toolBar.getComponent(1);
		assertNull(openButton.getText());
		// the text becomes the tool tip if there is none
		assertEquals("Open", openButton.getToolTipText());
		// no icon, so the text stays
		assertEquals("Text only", ((JButton)toolBar.getComponent(2)).getText());
		JButton forced = (JButton)toolBar.getComponent(3);
		assertEquals("Forced", forced.getText());
		assertEquals("Forced button", forced.getAccessibleContext().getAccessibleName());
		assertNull(((JToggleButton)toolBar.getComponent(4)).getText());

		JMenu menu = builder.buildMenu(MenuXmlReader.fromXml(
			"<menu id=\"m\" text=\"M\" accessibleName=\"Menu M\" accessibleDescription=\"Desc\">"
				+ "<item id=\"i\" text=\"I\" toolTipKey=\"tip.new\"/></menu>"));
		assertEquals("Menu M", menu.getAccessibleContext().getAccessibleName());
		assertEquals("Desc", menu.getAccessibleContext().getAccessibleDescription());
		assertEquals("Creates a new file", menu.getItem(0).getToolTipText());
	}
}
