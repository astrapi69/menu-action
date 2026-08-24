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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;
import javax.swing.plaf.metal.OceanTheme;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.astrapi69.swing.menu.build.ActionRegistry;
import io.github.astrapi69.swing.menu.build.MenuBuilder;
import io.github.astrapi69.swing.menu.enumeration.BaseMenuId;
import io.github.astrapi69.swing.menu.model.MenuInfo;

/**
 * Additional unit tests for {@link LookAndFeelMenuFactory} written against the pitest mutation
 * report to exercise branches that {@link LookAndFeelMenuFactoryTest} and
 * {@link LookAndFeelMenuFactoryParameterizedTest} did not distinguish
 */
class LookAndFeelMenuFactoryMutationCoverageTest
{

	private LookAndFeel previousLookAndFeel;

	private MetalTheme previousMetalTheme;

	private UIManager.LookAndFeelInfo[] previousInstalled;

	@BeforeEach
	void setUp()
	{
		previousLookAndFeel = UIManager.getLookAndFeel();
		previousMetalTheme = MetalLookAndFeel.getCurrentTheme();
		previousInstalled = UIManager.getInstalledLookAndFeels();
	}

	@AfterEach
	void tearDown() throws Exception
	{
		UIManager.setInstalledLookAndFeels(previousInstalled);
		if (previousMetalTheme != null)
		{
			MetalLookAndFeel.setCurrentTheme(previousMetalTheme);
		}
		if (previousLookAndFeel != null)
		{
			UIManager.setLookAndFeel(previousLookAndFeel);
		}
	}

	/**
	 * A look and feel that is not one of the well known ids gets an id derived from its class name;
	 * every look and feel installed on the machine running the tests happens to be well known, so
	 * this branch is otherwise never exercised
	 */
	@Test
	void unknownLookAndFeelsGetAnIdDerivedFromTheClassName()
	{
		List<UIManager.LookAndFeelInfo> extended = new ArrayList<>(List.of(previousInstalled));
		extended.add(
			new UIManager.LookAndFeelInfo("Third Party LAF", "com.example.ThirdPartyLookAndFeel"));
		UIManager.setInstalledLookAndFeels(extended.toArray(new UIManager.LookAndFeelInfo[0]));

		List<String> ids = LookAndFeelMenuFactory.lookAndFeelIds();

		assertTrue(
			ids.contains(BaseMenuId.LOOK_AND_FEEL_KEY + ".com.example.thirdpartylookandfeel"),
			ids::toString);
	}

	/**
	 * When the ocean theme is active, the metal radio button item itself must not also report
	 * itself as selected, otherwise both the metal and the ocean item would be selected at once
	 */
	@Test
	void oceanActiveSuppressesTheMetalItemsOwnSelection() throws Exception
	{
		LookAndFeels.setLookAndFeel(LookAndFeels.OCEAN);

		MenuInfo menuInfo = LookAndFeelMenuFactory.newLookAndFeelMenuInfo();

		long selected = menuInfo.getChildren().stream()
			.filter(child -> Boolean.TRUE.equals(child.getSelected())).count();
		assertEquals(1, selected);
		MenuInfo metalItem = menuInfo.getChildren().stream()
			.filter(child -> BaseMenuId.LOOK_AND_FEEL_METAL_KEY.equals(child.getName())).findFirst()
			.orElseThrow();
		assertNull(metalItem.getSelected());
		MenuInfo oceanItem = menuInfo.getChildren().stream()
			.filter(child -> BaseMenuId.LOOK_AND_FEEL_OCEAN_KEY.equals(child.getName())).findFirst()
			.orElseThrow();
		assertEquals(Boolean.TRUE, oceanItem.getSelected());
	}

	/**
	 * Clicking a non metal, non ocean item must actually activate that look and feel, not just
	 * select the radio button; {@code registerActions} registers a distinct action per id
	 */
	@Test
	void clickingANonMetalItemActuallyActivatesThatLookAndFeel() throws Exception
	{
		String nimbusClassName = Arrays.stream(UIManager.getInstalledLookAndFeels())
			.filter(info -> info.getName().toLowerCase(Locale.ROOT).contains("nimbus")).findFirst()
			.orElseThrow().getClassName();
		LookAndFeels.setLookAndFeel(LookAndFeels.METAL);

		MenuInfo menuInfo = LookAndFeelMenuFactory.newLookAndFeelMenuInfo();
		ActionRegistry registry = LookAndFeelMenuFactory.registerActions(ActionRegistry.empty(),
			new JLabel());
		MenuBuilder builder = new MenuBuilder(registry);
		JMenu menu = builder.buildMenu(menuInfo);

		builder.getComponent(BaseMenuId.LOOK_AND_FEEL_NIMBUS_KEY, JRadioButtonMenuItem.class)
			.orElseThrow().doClick();

		assertEquals(nimbusClassName, UIManager.getLookAndFeel().getClass().getName());
		assertTrue(menu.getItemCount() > 0);
	}

	/**
	 * Clicking the metal item resets the metal theme to the default theme (the point of the
	 * dedicated {@code LookAndFeels.METAL} action, per its own comment); a previously activated
	 * ocean theme must not stay active
	 */
	@Test
	void clickingTheMetalItemResetsAPreviouslyActivatedOceanTheme() throws Exception
	{
		LookAndFeels.setLookAndFeel(LookAndFeels.OCEAN);

		MenuInfo menuInfo = LookAndFeelMenuFactory.newLookAndFeelMenuInfo();
		ActionRegistry registry = LookAndFeelMenuFactory.registerActions(ActionRegistry.empty(),
			new JLabel());
		MenuBuilder builder = new MenuBuilder(registry);
		builder.buildMenu(menuInfo);

		builder.getComponent(BaseMenuId.LOOK_AND_FEEL_METAL_KEY, JRadioButtonMenuItem.class)
			.orElseThrow().doClick();

		assertFalse(MetalLookAndFeel.getCurrentTheme() instanceof OceanTheme);
	}

	/**
	 * Distinguishes "the metal look and feel is active but the ocean theme is not" from "the ocean
	 * theme is active": only the metal item, not the ocean item, must be selected
	 */
	@Test
	void metalActiveWithoutOceanSelectsOnlyTheMetalItem() throws Exception
	{
		LookAndFeels.setLookAndFeel(LookAndFeels.METAL);

		MenuInfo menuInfo = LookAndFeelMenuFactory.newLookAndFeelMenuInfo();

		MenuInfo metalItem = menuInfo.getChildren().stream()
			.filter(child -> BaseMenuId.LOOK_AND_FEEL_METAL_KEY.equals(child.getName())).findFirst()
			.orElseThrow();
		assertEquals(Boolean.TRUE, metalItem.getSelected());
		MenuInfo oceanItem = menuInfo.getChildren().stream()
			.filter(child -> BaseMenuId.LOOK_AND_FEEL_OCEAN_KEY.equals(child.getName())).findFirst()
			.orElseThrow();
		assertNull(oceanItem.getSelected());
	}

	/**
	 * Two look and feels whose class names normalise to the same derived id (for instance a third
	 * party look and feel also named "Nimbus") must not both end up in the built menu, only the
	 * first one seen
	 */
	@Test
	void twoLookAndFeelsThatNormaliseToTheSameIdAreDeduplicated()
	{
		List<UIManager.LookAndFeelInfo> extended = new ArrayList<>(List.of(previousInstalled));
		extended.add(new UIManager.LookAndFeelInfo("Colliding One", "com.example.Colliding!!"));
		extended.add(new UIManager.LookAndFeelInfo("Colliding Two", "com.example.Colliding??"));
		UIManager.setInstalledLookAndFeels(extended.toArray(new UIManager.LookAndFeelInfo[0]));

		List<String> ids = LookAndFeelMenuFactory.lookAndFeelIds();

		long matches = ids.stream()
			.filter(id -> id.startsWith(BaseMenuId.LOOK_AND_FEEL_KEY + ".com.example.colliding"))
			.count();
		assertEquals(1, matches, ids::toString);
	}

	/**
	 * Activating a non metal look and feel (the branch that calls the {@code String} overload of
	 * {@code setLookAndFeel}, unlike the metal and ocean items which use the dedicated
	 * {@code LookAndFeels} overload) updates the component tree ui of the given component, not just
	 * the internal swing look and feel state
	 */
	@Test
	void activatingANonMetalLookAndFeelUpdatesTheComponentTreeUi() throws Exception
	{
		LookAndFeels.setLookAndFeel(LookAndFeels.METAL);
		TrackingLabel component = new TrackingLabel();
		MenuInfo menuInfo = LookAndFeelMenuFactory.newLookAndFeelMenuInfo();
		ActionRegistry registry = LookAndFeelMenuFactory.registerActions(ActionRegistry.empty(),
			component);
		MenuBuilder builder = new MenuBuilder(registry);
		builder.buildMenu(menuInfo);
		// the JLabel constructor itself already triggers one updateUI() call to install its
		// initial ui delegate, so that call must not be mistaken for the one under test
		component.uiUpdated = false;

		builder.getComponent(BaseMenuId.LOOK_AND_FEEL_NIMBUS_KEY, JRadioButtonMenuItem.class)
			.orElseThrow().doClick();

		assertTrue(component.uiUpdated);
	}

	private static class TrackingLabel extends JLabel
	{
		boolean uiUpdated;

		@Override
		public void updateUI()
		{
			super.updateUI();
			uiUpdated = true;
		}
	}
}
