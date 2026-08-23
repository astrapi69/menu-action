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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import io.github.astrapi69.swing.menu.enumeration.MenuType;
import io.github.astrapi69.swing.menu.model.MenuInfo;

/**
 * The parameterized unit test class for the class {@link MenuBuilder}
 */
class MenuBuilderParameterizedTest
{

	/** The action id that is never registered in the {@link ActionRegistry} */
	private static final String MISSING_ACTION_ID = "missing.action";

	private static MenuBuilder newBuilder(final MissingActionPolicy missingActionPolicy)
	{
		return new MenuBuilder().withMissingActionPolicy(missingActionPolicy);
	}

	private static MenuInfo newMenuInfo(final MenuType type, final String name)
	{
		return MenuInfo.builder().type(type).name(name).text("Text").build();
	}

	private static Class<? extends JComponent> expectedMenuComponentClass(final MenuType type)
	{
		if (type == null)
		{
			return JMenuItem.class;
		}
		return switch (type)
		{
			case MENU -> JMenu.class;
			case MENU_ITEM -> JMenuItem.class;
			case CHECK_BOX_MENU_ITEM -> JCheckBoxMenuItem.class;
			case RADIO_BUTTON_MENU_ITEM -> JRadioButtonMenuItem.class;
			default -> null;
		};
	}

	private static Class<? extends JComponent> expectedRootClass(final MenuType type)
	{
		if (type == null)
		{
			return JMenuItem.class;
		}
		return switch (type)
		{
			case MENU_BAR -> JMenuBar.class;
			case POPUP -> JPopupMenu.class;
			case TOOL_BAR -> JToolBar.class;
			default -> expectedMenuComponentClass(type);
		};
	}

	/**
	 * Parameterized test for {@link MenuBuilder#buildMenuComponent(MenuInfo)} with every
	 * {@link MenuType} and with the null type
	 */
	@ParameterizedTest(name = "buildMenuComponent with the type {0}")
	@NullSource
	@EnumSource(MenuType.class)
	void buildMenuComponentWithEveryMenuType(final MenuType type)
	{
		MenuBuilder builder = newBuilder(MissingActionPolicy.IGNORE);
		MenuInfo menuInfo = newMenuInfo(type, "component");
		Class<? extends JComponent> expected = expectedMenuComponentClass(type);
		if (expected == null)
		{
			IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> builder.buildMenuComponent(menuInfo));
			assertEquals(
				"The menu type " + type
					+ " of the menu 'component' can not be built as menu component",
				exception.getMessage());
			assertTrue(builder.getComponent("component").isEmpty());
			return;
		}
		JMenuItem component = builder.buildMenuComponent(menuInfo);
		assertEquals(expected, component.getClass());
		assertEquals("Text", component.getText());
		assertEquals("component", component.getName());
		assertSame(component, builder.getComponent("component").orElseThrow());
	}

	/**
	 * Parameterized test for {@link MenuBuilder#build(MenuInfo)} with every {@link MenuType} and
	 * with the null type
	 */
	@ParameterizedTest(name = "build with the type {0}")
	@NullSource
	@EnumSource(MenuType.class)
	void buildWithEveryMenuType(final MenuType type)
	{
		MenuBuilder builder = newBuilder(MissingActionPolicy.IGNORE);
		MenuInfo menuInfo = newMenuInfo(type, "root");
		Class<? extends JComponent> expected = expectedRootClass(type);
		if (expected == null)
		{
			IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> builder.build(menuInfo));
			assertEquals(
				"The menu type " + type + " of the menu 'root' can not be built as menu component",
				exception.getMessage());
			return;
		}
		JComponent component = builder.build(menuInfo);
		assertEquals(expected, component.getClass());
		assertEquals("root", component.getName());
		assertSame(component, builder.getComponent("root").orElseThrow());
	}

	private static Stream<Arguments> rootBuildersWithWrongTypes()
	{
		List<Arguments> rootBuilders = List.of(
			Arguments.of("buildMenuBar", MenuType.MENU_BAR,
				(BiFunction<MenuBuilder, MenuInfo, JComponent>)MenuBuilder::buildMenuBar),
			Arguments.of("buildMenu", MenuType.MENU,
				(BiFunction<MenuBuilder, MenuInfo, JComponent>)MenuBuilder::buildMenu),
			Arguments.of("buildPopupMenu", MenuType.POPUP,
				(BiFunction<MenuBuilder, MenuInfo, JComponent>)MenuBuilder::buildPopupMenu),
			Arguments.of("buildToolBar", MenuType.TOOL_BAR,
				(BiFunction<MenuBuilder, MenuInfo, JComponent>)MenuBuilder::buildToolBar));
		List<MenuType> types = new ArrayList<>(Arrays.asList(MenuType.values()));
		types.add(null);
		return rootBuilders.stream()
			.flatMap(rootBuilder -> types.stream().filter(type -> type != rootBuilder.get()[1])
				.map(type -> Arguments.of(rootBuilder.get()[0], rootBuilder.get()[1], type,
					rootBuilder.get()[2])));
	}

	/**
	 * Parameterized test for the type check of all root build methods of the class
	 * {@link MenuBuilder}
	 */
	@ParameterizedTest(name = "{0} rejects a menu of the type {2}")
	@MethodSource("rootBuildersWithWrongTypes")
	void rootBuildersRejectTheWrongMenuType(final String methodName, final MenuType expectedType,
		final MenuType wrongType, final BiFunction<MenuBuilder, MenuInfo, JComponent> rootBuilder)
	{
		MenuBuilder builder = newBuilder(MissingActionPolicy.IGNORE);
		MenuInfo menuInfo = newMenuInfo(wrongType, "root");
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
			() -> rootBuilder.apply(builder, menuInfo));
		assertEquals("Expected a menu of type " + expectedType
			+ " but the menu 'root' has the type " + wrongType, exception.getMessage());
	}

	/**
	 * Parameterized test for the {@link MissingActionPolicy} of a menu item without a registered
	 * action
	 */
	@ParameterizedTest(name = "the policy {0} for a menu item without a registered action")
	@EnumSource(MissingActionPolicy.class)
	void missingActionPolicyForMenuItem(final MissingActionPolicy policy)
	{
		MenuBuilder builder = newBuilder(policy);
		assertEquals(policy, builder.getMissingActionPolicy());
		MenuInfo item = MenuInfo.builder().type(MenuType.MENU_ITEM).name("file.exit").text("Exit")
			.actionId(MISSING_ACTION_ID).build();
		if (policy == MissingActionPolicy.FAIL)
		{
			IllegalStateException exception = assertThrows(IllegalStateException.class,
				() -> builder.buildMenuComponent(item));
			assertEquals("No action registered for the id '" + MISSING_ACTION_ID
				+ "' of the menu 'file.exit'", exception.getMessage());
			return;
		}
		JMenuItem menuItem = builder.buildMenuComponent(item);
		assertEquals(JMenuItem.class, menuItem.getClass());
		assertEquals(policy != MissingActionPolicy.DISABLE, menuItem.isEnabled());
		assertEquals(0, menuItem.getActionListeners().length);
	}

	/**
	 * Parameterized test for the {@link MissingActionPolicy} of a tool bar button without a
	 * registered action
	 */
	@ParameterizedTest(name = "the policy {0} for a tool bar button without a registered action")
	@EnumSource(MissingActionPolicy.class)
	void missingActionPolicyForToolBarButton(final MissingActionPolicy policy)
	{
		MenuBuilder builder = newBuilder(policy);
		MenuInfo item = MenuInfo.builder().type(MenuType.MENU_ITEM).name("tool.bar.exit")
			.text("Exit").actionId(MISSING_ACTION_ID).build();
		MenuInfo toggle = MenuInfo.builder().type(MenuType.CHECK_BOX_MENU_ITEM)
			.name("tool.bar.toggle").text("Toggle").actionId(MISSING_ACTION_ID).build();
		if (policy == MissingActionPolicy.FAIL)
		{
			IllegalStateException exception = assertThrows(IllegalStateException.class,
				() -> builder.buildToolBarComponent(item));
			assertEquals("No action registered for the id '" + MISSING_ACTION_ID
				+ "' of the menu 'tool.bar.exit'", exception.getMessage());
			assertThrows(IllegalStateException.class, () -> builder.buildToolBarComponent(toggle));
			return;
		}
		JComponent button = builder.buildToolBarComponent(item);
		JComponent toggleButton = builder.buildToolBarComponent(toggle);
		assertEquals(JButton.class, button.getClass());
		assertEquals(JToggleButton.class, toggleButton.getClass());
		assertEquals(policy != MissingActionPolicy.DISABLE, button.isEnabled());
		assertEquals(policy != MissingActionPolicy.DISABLE, toggleButton.isEnabled());
		assertEquals(0, ((JButton)button).getActionListeners().length);
	}

	/**
	 * Parameterized test for the {@link MissingActionPolicy} of a check box menu item and of a tool
	 * bar toggle button with a model key that can not be resolved
	 */
	@ParameterizedTest(name = "the policy {0} for a check box with an unresolvable model")
	@EnumSource(MissingActionPolicy.class)
	void missingActionPolicyForUnresolvableModel(final MissingActionPolicy policy)
	{
		ActionRegistry actions = ActionRegistry.empty().register("toggle", event -> {
		});
		MenuBuilder builder = new MenuBuilder(actions).withMissingActionPolicy(policy)
			.withModelResolver(key -> Optional.empty());
		MenuInfo checkBoxInfo = MenuInfo.builder().type(MenuType.CHECK_BOX_MENU_ITEM)
			.name("view.statusbar").text("Statusbar").actionId("toggle").model("missing.model")
			.build();
		if (policy == MissingActionPolicy.FAIL)
		{
			IllegalStateException exception = assertThrows(IllegalStateException.class,
				() -> builder.buildMenuComponent(checkBoxInfo));
			assertEquals(
				"No model registered for the key 'missing.model' of the menu 'view.statusbar'",
				exception.getMessage());
			assertThrows(IllegalStateException.class,
				() -> builder.buildToolBarComponent(checkBoxInfo));
			return;
		}
		JMenuItem checkBox = builder.buildMenuComponent(checkBoxInfo);
		JComponent toggleButton = builder.buildToolBarComponent(checkBoxInfo);
		assertEquals(JCheckBoxMenuItem.class, checkBox.getClass());
		assertEquals(JToggleButton.class, toggleButton.getClass());
		// the action is registered, so the enabled state depends only on the unresolvable model:
		// DISABLE explicitly disables the item, every other policy leaves it enabled
		assertEquals(policy != MissingActionPolicy.DISABLE, checkBox.isEnabled());
		assertEquals(policy != MissingActionPolicy.DISABLE, toggleButton.isEnabled());
		assertFalse(checkBox.isSelected());
		assertFalse(((JToggleButton)toggleButton).isSelected());
	}

	/**
	 * Parameterized test that shows that a menu never needs an action, whatever the
	 * {@link MissingActionPolicy} is
	 */
	@ParameterizedTest(name = "a menu without an action is built with the policy {0}")
	@EnumSource(MissingActionPolicy.class)
	void menuWithoutActionIsBuiltWithEveryPolicy(final MissingActionPolicy policy)
	{
		MenuInfo menuInfo = MenuInfo.builder().type(MenuType.MENU).name("file").text("File")
			.actionId(MISSING_ACTION_ID).build()
			.addChild(MenuInfo.builder().type(MenuType.SEPARATOR).build());
		JMenu menu = newBuilder(policy).buildMenu(menuInfo);
		assertEquals("File", menu.getText());
		assertTrue(menu.isEnabled());
		assertEquals(1, menu.getMenuComponentCount());
	}
}
