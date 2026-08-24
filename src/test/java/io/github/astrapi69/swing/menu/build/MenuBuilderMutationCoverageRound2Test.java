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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToggleButton;

import org.junit.jupiter.api.Test;

import io.github.astrapi69.model.BaseModel;
import io.github.astrapi69.model.api.IModel;
import io.github.astrapi69.swing.menu.enumeration.MenuType;
import io.github.astrapi69.swing.menu.model.MenuInfo;

/**
 * A second batch of unit tests for {@link MenuBuilder} written against the pitest mutation report,
 * covering survivors that {@link MenuBuilderMutationCoverageTest} did not yet address
 */
class MenuBuilderMutationCoverageRound2Test
{

	@Test
	void buildMenuBarSkipsSeparatorChildren()
	{
		MenuBuilder builder = new MenuBuilder().withMissingActionPolicy(MissingActionPolicy.IGNORE);
		MenuInfo menuBarInfo = MenuInfo.builder().type(MenuType.MENU_BAR).name("bar").build()
			.addChild(MenuInfo.builder().type(MenuType.SEPARATOR).build())
			.addChild(MenuInfo.builder().type(MenuType.MENU).name("m").text("M").build());

		JMenuBar menuBar = builder.buildMenuBar(menuBarInfo);

		assertEquals(1, menuBar.getMenuCount());
		assertEquals("M", menuBar.getMenu(0).getText());
	}

	@Test
	void aModelBoundCheckBoxAndRadioNeverRequireAnActionEvenUnderTheFailPolicy()
	{
		IModel<Boolean> statusbar = BaseModel.of(Boolean.TRUE);
		IModel<String> mode = BaseModel.of("A");
		MenuBuilder builder = new MenuBuilder().withMissingActionPolicy(MissingActionPolicy.FAIL)
			.withModels(Map.of("statusbar", statusbar, "mode", mode));

		// a model bound check box or radio button needs no action, even under the FAIL policy
		JCheckBoxMenuItem checkBox = (JCheckBoxMenuItem)builder.buildMenuComponent(MenuInfo
			.builder().type(MenuType.CHECK_BOX_MENU_ITEM).name("c").model("statusbar").build());
		JRadioButtonMenuItem radio = (JRadioButtonMenuItem)builder
			.buildMenuComponent(MenuInfo.builder().type(MenuType.RADIO_BUTTON_MENU_ITEM).name("r")
				.model("mode").value("A").build());
		assertTrue(checkBox.isSelected());
		assertTrue(radio.isSelected());

		// without a model the very same policy does require an action
		assertThrows(IllegalStateException.class, () -> builder.buildMenuComponent(
			MenuInfo.builder().type(MenuType.CHECK_BOX_MENU_ITEM).name("d").build()));
		assertThrows(IllegalStateException.class, () -> builder.buildMenuComponent(
			MenuInfo.builder().type(MenuType.RADIO_BUTTON_MENU_ITEM).name("s").build()));
	}

	@Test
	void aModelBoundToggleButtonInAToolBarNeverRequiresAnActionEvenUnderTheFailPolicy()
	{
		IModel<Boolean> statusbar = BaseModel.of(Boolean.TRUE);
		MenuBuilder builder = new MenuBuilder().withMissingActionPolicy(MissingActionPolicy.FAIL)
			.withModels(Map.of("statusbar", statusbar));

		JToggleButton toggle = (JToggleButton)builder.buildToolBarComponent(MenuInfo.builder()
			.type(MenuType.CHECK_BOX_MENU_ITEM).name("c").model("statusbar").build());
		assertTrue(toggle.isSelected());

		assertThrows(IllegalStateException.class, () -> builder.buildToolBarComponent(
			MenuInfo.builder().type(MenuType.CHECK_BOX_MENU_ITEM).name("d").build()));
	}

	@Test
	void aModelBoundItemIsNeverForceDisabledByTheDisablePolicyEvenWithoutAnAction()
	{
		IModel<Boolean> statusbar = BaseModel.of(Boolean.TRUE);
		MenuBuilder builder = new MenuBuilder().withMissingActionPolicy(MissingActionPolicy.DISABLE)
			.withModels(Map.of("statusbar", statusbar));

		JCheckBoxMenuItem modelBound = (JCheckBoxMenuItem)builder.buildMenuComponent(MenuInfo
			.builder().type(MenuType.CHECK_BOX_MENU_ITEM).name("c").model("statusbar").build());
		assertTrue(modelBound.isEnabled());

		JMenuItem withoutModel = builder
			.buildMenuComponent(MenuInfo.builder().type(MenuType.MENU_ITEM).name("d").build());
		assertFalse(withoutModel.isEnabled());
	}

	@Test
	void radioButtonModelBindingWithoutAValueNeverSelectsOrWrites()
	{
		IModel<String> mode = BaseModel.of("A");
		MenuBuilder builder = new MenuBuilder().withMissingActionPolicy(MissingActionPolicy.IGNORE)
			.withModels(Map.of("mode", mode));

		JRadioButtonMenuItem radio = (JRadioButtonMenuItem)builder.buildMenuComponent(MenuInfo
			.builder().type(MenuType.RADIO_BUTTON_MENU_ITEM).name("r").model("mode").build());

		assertFalse(radio.isSelected());
		radio.doClick();
		assertEquals("A", mode.getObject(),
			"no value on the info means the model is never written");
	}

	@Test
	void removeOfAComponentWithoutAParentStillForgetsItAndReturnsIt()
	{
		MenuBuilder builder = new MenuBuilder().withMissingActionPolicy(MissingActionPolicy.IGNORE);
		builder.buildMenuComponent(MenuInfo.builder().type(MenuType.MENU_ITEM).name("standalone")
			.text("Standalone").build());
		assertTrue(builder.getComponent("standalone").isPresent());

		assertTrue(builder.remove("standalone").isPresent());

		assertFalse(builder.getComponent("standalone").isPresent());
	}

	@Test
	void removeForgetsModelBindingsAndButtonGroupMembershipOfEveryDescendant()
	{
		IModel<Boolean> model = BaseModel.of(Boolean.TRUE);
		MenuBuilder builder = new MenuBuilder().withMissingActionPolicy(MissingActionPolicy.IGNORE)
			.withModels(Map.of("statusbar", model));
		JMenu menu = builder.buildMenu(MenuInfo.builder().type(MenuType.MENU).name("menu").build()
			.addChild(MenuInfo.builder().type(MenuType.CHECK_BOX_MENU_ITEM).name("bound")
				.model("statusbar").build())
			.addChild(MenuInfo.builder().type(MenuType.RADIO_BUTTON_MENU_ITEM).name("grouped")
				.group("g").build()));
		assertTrue(builder.getButtonGroup("g").orElseThrow().getButtonCount() > 0);

		builder.remove("menu");

		// the model change must no longer reach the removed (and now stale) check box
		JCheckBoxMenuItem boundItem = (JCheckBoxMenuItem)menu.getItem(0);
		model.setObject(Boolean.FALSE);
		builder.updateFromModels();
		assertTrue(boundItem.isSelected(),
			"a removed component is no longer updated from its model");
		assertEquals(0, builder.getButtonGroup("g").orElseThrow().getButtonCount(),
			"a removed component is forgotten by its button group");
	}

	@Test
	void removingAToolBarForgetsAButtonThatIsARealSwingDescendantOfIt()
	{
		MenuBuilder builder = new MenuBuilder().withMissingActionPolicy(MissingActionPolicy.IGNORE);
		// the button becomes a real swing container child of the tool bar (toolBar.add(button)),
		// unlike a menu item whose swing parent is the popup menu of its logical parent menu; this
		// is the only remove() scenario that reaches the removed component through
		// SwingUtilities.isDescendingFrom rather than through the ParentMenuResolver ancestor chain
		builder.buildToolBar(MenuInfo.builder().type(MenuType.TOOL_BAR).name("tb").build()
			.addChild(MenuInfo.builder().type(MenuType.MENU_ITEM).name("btn").text("Btn").build()));
		assertTrue(builder.getComponent("btn").isPresent());

		builder.remove("tb");

		assertFalse(builder.getComponent("btn").isPresent());
	}

	@Test
	void isItemTreatsAMissingTypeAsAnItem()
	{
		MenuBuilder builder = new MenuBuilder()
			.withMissingActionPolicy(MissingActionPolicy.DISABLE);

		JMenuItem withoutType = builder.buildMenuComponent(MenuInfo.builder().name("x").build());

		assertFalse(withoutType.isEnabled(),
			"a menu info without an explicit type is treated as a plain item, so it is force disabled");
	}
}
