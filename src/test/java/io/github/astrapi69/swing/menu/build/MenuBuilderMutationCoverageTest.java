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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.junit.jupiter.api.Test;

import io.github.astrapi69.model.BaseModel;
import io.github.astrapi69.model.api.IModel;
import io.github.astrapi69.swing.menu.enumeration.MenuType;
import io.github.astrapi69.swing.menu.model.MenuInfo;

/**
 * Additional unit tests for {@link MenuBuilder} written against the pitest mutation report to
 * exercise branches that {@link MenuBuilderTest} and {@link MenuBuilderModelBindingTest} did not
 * distinguish (both sides of a null check, both sides of a boolean flag)
 */
class MenuBuilderMutationCoverageTest
{

	/** Records whether revalidate/repaint were actually invoked on the component */
	private static class TrackingToolBar extends JToolBar
	{
		boolean revalidated;
		boolean repainted;

		@Override
		public void revalidate()
		{
			revalidated = true;
			super.revalidate();
		}

		@Override
		public void repaint()
		{
			repainted = true;
			super.repaint();
		}
	}

	@Test
	void menuBarLevelToolTipEnabledVisibleAccessible()
	{
		MenuBuilder withAll = new MenuBuilder();
		JMenuBar full = withAll
			.buildMenuBar(MenuInfo.builder().type(MenuType.MENU_BAR).name("bar").enabled(false)
				.visible(false).accessibleName("Bar").accessibleDescription("The bar").build());
		assertFalse(full.isEnabled());
		assertFalse(full.isVisible());
		assertEquals("Bar", full.getAccessibleContext().getAccessibleName());
		assertEquals("The bar", full.getAccessibleContext().getAccessibleDescription());

		MenuBuilder withNone = new MenuBuilder();
		JMenuBar bare = withNone
			.buildMenuBar(MenuInfo.builder().type(MenuType.MENU_BAR).name("bar").build());
		assertTrue(bare.isEnabled());
		assertTrue(bare.isVisible());
		assertNull(bare.getAccessibleContext().getAccessibleName());
	}

	@Test
	void popupMenuLevelNameToolTipEnabledAccessible()
	{
		MenuBuilder full = new MenuBuilder();
		JPopupMenu withAll = full
			.buildPopupMenu(MenuInfo.builder().type(MenuType.POPUP).name("p").toolTip("tip")
				.enabled(false).accessibleName("Popup").accessibleDescription("The popup").build());
		assertEquals("p", withAll.getName());
		assertEquals("tip", withAll.getToolTipText());
		assertFalse(withAll.isEnabled());
		assertEquals("Popup", withAll.getAccessibleContext().getAccessibleName());
		assertEquals("The popup", withAll.getAccessibleContext().getAccessibleDescription());

		MenuBuilder none = new MenuBuilder();
		JPopupMenu bare = none.buildPopupMenu(MenuInfo.builder().type(MenuType.POPUP).build());
		assertNull(bare.getName());
		assertNull(bare.getToolTipText());
		assertTrue(bare.isEnabled());
	}

	@Test
	void toolBarLevelNameEnabledVisible()
	{
		MenuBuilder full = new MenuBuilder();
		JToolBar withAll = full.buildToolBar(MenuInfo.builder().type(MenuType.TOOL_BAR).name("t")
			.enabled(false).visible(false).build());
		assertEquals("t", withAll.getName());
		assertFalse(withAll.isEnabled());
		assertFalse(withAll.isVisible());

		MenuBuilder none = new MenuBuilder();
		JToolBar bare = none.buildToolBar(MenuInfo.builder().type(MenuType.TOOL_BAR).build());
		assertNull(bare.getName());
		assertTrue(bare.isEnabled());
		assertTrue(bare.isVisible());
	}

	@Test
	void buildMenuComponentWithAndWithoutGroup()
	{
		MenuBuilder builder = new MenuBuilder().withMissingActionPolicy(MissingActionPolicy.IGNORE);

		JCheckBoxMenuItem grouped = (JCheckBoxMenuItem)builder.buildMenuComponent(
			MenuInfo.builder().type(MenuType.CHECK_BOX_MENU_ITEM).name("g").group("grp").build());
		assertTrue(builder.getButtonGroup("grp").isPresent());
		assertTrue(builder.getButtonGroup("grp").orElseThrow().getElements().hasMoreElements());

		JCheckBoxMenuItem ungrouped = (JCheckBoxMenuItem)builder.buildMenuComponent(
			MenuInfo.builder().type(MenuType.CHECK_BOX_MENU_ITEM).name("u").build());
		assertFalse(builder.getButtonGroup("u").isPresent());
		assertNotSameGroupMembership(grouped, ungrouped);
	}

	private static void assertNotSameGroupMembership(final JCheckBoxMenuItem grouped,
		final JCheckBoxMenuItem ungrouped)
	{
		assertTrue(grouped.getModel()instanceof javax.swing.DefaultButtonModel model
			&& model.getGroup() != null);
		assertTrue(ungrouped.getModel()instanceof javax.swing.DefaultButtonModel model
			&& model.getGroup() == null);
	}

	@Test
	void buildToolBarComponentWithAndWithoutGroup()
	{
		MenuBuilder builder = new MenuBuilder().withMissingActionPolicy(MissingActionPolicy.IGNORE);
		JToggleButton grouped = (JToggleButton)builder.buildToolBarComponent(
			MenuInfo.builder().type(MenuType.CHECK_BOX_MENU_ITEM).name("g").group("tgrp").build());
		assertTrue(builder.getButtonGroup("tgrp").isPresent());
		assertTrue(((javax.swing.DefaultButtonModel)grouped.getModel()).getGroup() != null);

		JToggleButton ungrouped = (JToggleButton)builder.buildToolBarComponent(
			MenuInfo.builder().type(MenuType.CHECK_BOX_MENU_ITEM).name("u").build());
		assertNull(((javax.swing.DefaultButtonModel)ungrouped.getModel()).getGroup());
	}

	@Test
	void getComponentAndGetButtonGroupWithUnknownName()
	{
		MenuBuilder builder = new MenuBuilder();
		builder.buildMenu(MenuInfo.builder().type(MenuType.MENU).name("m").build());
		assertTrue(builder.getComponent("m").isPresent());
		assertFalse(builder.getComponent("does-not-exist").isPresent());
		assertFalse(builder.getComponent("m", javax.swing.JPopupMenu.class).isPresent());
		assertTrue(builder.getComponent("m", JMenu.class).isPresent());
		assertFalse(builder.getButtonGroup("does-not-exist").isPresent());
	}

	@Test
	void insertAndRemoveCallRevalidateAndRepaint()
	{
		MenuBuilder builder = new MenuBuilder().withMissingActionPolicy(MissingActionPolicy.IGNORE);
		TrackingToolBar toolBar = new TrackingToolBar();
		toolBar.setName("tb");
		java.lang.reflect.Field componentsField;
		try
		{
			componentsField = MenuBuilder.class.getDeclaredField("components");
			componentsField.setAccessible(true);
			@SuppressWarnings("unchecked")
			Map<String, javax.swing.JComponent> components = (Map<String, javax.swing.JComponent>)componentsField
				.get(builder);
			components.put("tb", toolBar);
		}
		catch (ReflectiveOperationException e)
		{
			throw new IllegalStateException(e);
		}

		toolBar.revalidated = false;
		toolBar.repainted = false;
		builder.insert("tb",
			MenuInfo.builder().type(MenuType.MENU_ITEM).name("i").text("I").build());
		assertTrue(toolBar.revalidated, "insert must call revalidate on the parent");
		assertTrue(toolBar.repainted, "insert must call repaint on the parent");

		toolBar.revalidated = false;
		toolBar.repainted = false;
		builder.remove("i");
		assertTrue(toolBar.revalidated, "remove must call revalidate on the parent");
		assertTrue(toolBar.repainted, "remove must call repaint on the parent");
	}

	@Test
	void removeForgetsEveryDescendantNotJustTheDirectMatch()
	{
		MenuBuilder builder = new MenuBuilder().withMissingActionPolicy(MissingActionPolicy.IGNORE);
		JMenuBar menuBar = builder
			.buildMenuBar(MenuInfo.builder().type(MenuType.MENU_BAR).name("bar").build()
				.addChild(MenuInfo.builder().type(MenuType.MENU).name("menu").build()
					.addChild(MenuInfo.builder().type(MenuType.MENU_ITEM).name("item").build())));
		assertTrue(builder.getComponent("menu").isPresent());
		assertTrue(builder.getComponent("item").isPresent());

		assertTrue(builder.remove("menu").isPresent());
		assertEquals(0, menuBar.getMenuCount());
		assertFalse(builder.getComponent("menu").isPresent());
		assertFalse(builder.getComponent("item").isPresent(),
			"removing a menu must also forget its items");

		assertFalse(builder.remove("menu").isPresent(), "removing twice returns empty");
	}

	@Test
	void bindModelNonRadioWritesOnEverySelectionChange()
	{
		IModel<Boolean> model = BaseModel.of(Boolean.FALSE);
		MenuBuilder builder = new MenuBuilder().withMissingActionPolicy(MissingActionPolicy.IGNORE)
			.withModels(Map.of("m", model));
		JCheckBoxMenuItem item = (JCheckBoxMenuItem)builder.buildMenuComponent(
			MenuInfo.builder().type(MenuType.CHECK_BOX_MENU_ITEM).name("c").model("m").build());
		item.doClick();
		assertEquals(Boolean.TRUE, model.getObject());
		item.doClick();
		assertEquals(Boolean.FALSE, model.getObject());
	}

	@Test
	void bindModelRadioDeselectionDoesNotWrite()
	{
		IModel<String> model = BaseModel.of("A");
		MenuBuilder builder = new MenuBuilder().withMissingActionPolicy(MissingActionPolicy.IGNORE)
			.withModels(Map.of("m", model));
		JMenu menu = builder.buildMenu(MenuInfo.builder().type(MenuType.MENU).name("menu").build()
			.addChild(MenuInfo.builder().type(MenuType.RADIO_BUTTON_MENU_ITEM).name("a").group("g")
				.model("m").value("A").build())
			.addChild(MenuInfo.builder().type(MenuType.RADIO_BUTTON_MENU_ITEM).name("b").group("g")
				.model("m").value("B").build()));
		menu.getItem(1).doClick();
		assertEquals("B", model.getObject());
	}

	@Test
	void applyAccessibleSetsBothFieldsIndependently()
	{
		MenuBuilder onlyName = new MenuBuilder();
		JMenu menuWithName = onlyName.buildMenu(
			MenuInfo.builder().type(MenuType.MENU).name("m").accessibleName("Name only").build());
		assertEquals("Name only", menuWithName.getAccessibleContext().getAccessibleName());
		assertNull(menuWithName.getAccessibleContext().getAccessibleDescription());

		MenuBuilder onlyDescription = new MenuBuilder();
		JMenu menuWithDescription = onlyDescription.buildMenu(MenuInfo.builder().type(MenuType.MENU)
			.name("m").accessibleDescription("Description only").build());
		// JMenu falls back to its (empty) text for the accessible name when none is set explicitly
		assertEquals("", menuWithDescription.getAccessibleContext().getAccessibleName());
		assertEquals("Description only",
			menuWithDescription.getAccessibleContext().getAccessibleDescription());
	}

	@Test
	void isItemDistinguishesMenuFromItemTypes()
	{
		// a MENU without a registered action is never force-disabled by the DISABLE policy,
		// only real items (menu item, check box, radio button) are
		MenuBuilder builder = new MenuBuilder()
			.withMissingActionPolicy(MissingActionPolicy.DISABLE);
		JMenu menu = builder.buildMenu(MenuInfo.builder().type(MenuType.MENU).name("m").build());
		assertTrue(menu.isEnabled());

		JMenuItem item = builder
			.buildMenuComponent(MenuInfo.builder().type(MenuType.MENU_ITEM).name("i").build());
		assertFalse(item.isEnabled());
	}

	@Test
	void registerIgnoresAnUnnamedComponent()
	{
		MenuBuilder builder = new MenuBuilder().withMissingActionPolicy(MissingActionPolicy.IGNORE);
		builder.buildMenuComponent(MenuInfo.builder().type(MenuType.MENU_ITEM).build());
		// an unnamed item can not be looked up, but building it must not throw
		assertEquals(0, builder.getComponents().size());
	}

	@Test
	void resolveToolTipFallsBackToPlainToolTipWithoutResolver()
	{
		MenuBuilder builder = new MenuBuilder().withMissingActionPolicy(MissingActionPolicy.IGNORE);
		JMenuItem item = builder.buildMenuComponent(MenuInfo.builder().type(MenuType.MENU_ITEM)
			.name("i").toolTip("plain").toolTipKey("key.without.resolver").build());
		assertEquals("plain", item.getToolTipText());
	}

	@Test
	void toMenuItemInfoCopiesAccessibleFields()
	{
		MenuBuilder builder = new MenuBuilder().withMissingActionPolicy(MissingActionPolicy.IGNORE);
		JMenuItem item = builder.buildMenuComponent(MenuInfo.builder().type(MenuType.MENU_ITEM)
			.name("i").accessibleName("AN").accessibleDescription("AD").build());
		assertEquals("AN", item.getAccessibleContext().getAccessibleName());
		assertEquals("AD", item.getAccessibleContext().getAccessibleDescription());
	}
}
