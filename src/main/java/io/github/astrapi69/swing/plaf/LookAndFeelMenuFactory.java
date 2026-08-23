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

import java.awt.Component;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import io.github.astrapi69.swing.menu.build.ActionRegistry;
import io.github.astrapi69.swing.menu.enumeration.BaseMenuId;
import io.github.astrapi69.swing.menu.enumeration.MenuType;
import io.github.astrapi69.swing.menu.model.MenuInfo;
import lombok.NonNull;
import lombok.extern.java.Log;

/**
 * The class {@link LookAndFeelMenuFactory} creates the menu definition and the actions for a look
 * and feel menu from the look and feels that are installed in the {@link UIManager}. The menu
 * contains a radio button menu item per installed look and feel plus the metal ocean theme, the
 * current look and feel is selected. The ids of the well known look and feels are the
 * {@link BaseMenuId} keys, so existing resource bundles keep working
 *
 * <pre>
 * MenuInfo lookAndFeelMenu = LookAndFeelMenuFactory.newLookAndFeelMenuInfo();
 * ActionRegistry actions = LookAndFeelMenuFactory.registerActions(ActionRegistry.empty(), frame);
 * </pre>
 */
@Log
public final class LookAndFeelMenuFactory
{

	/** The group name of the look and feel radio button menu items */
	public static final String GROUP = "look.and.feel";

	private static final Map<String, String> KNOWN_IDS = Map.of(
		"com.sun.java.swing.plaf.gtk.GTKLookAndFeel", BaseMenuId.LOOK_AND_FEEL_GTK_KEY,
		"javax.swing.plaf.metal.MetalLookAndFeel", BaseMenuId.LOOK_AND_FEEL_METAL_KEY,
		"com.sun.java.swing.plaf.motif.MotifLookAndFeel", BaseMenuId.LOOK_AND_FEEL_MOTIF_KEY,
		"javax.swing.plaf.nimbus.NimbusLookAndFeel", BaseMenuId.LOOK_AND_FEEL_NIMBUS_KEY,
		"com.sun.java.swing.plaf.windows.WindowsLookAndFeel", BaseMenuId.LOOK_AND_FEEL_SYSTEM_KEY);

	private LookAndFeelMenuFactory()
	{
	}

	/**
	 * Creates the {@link MenuInfo} of the look and feel menu with a radio button menu item for
	 * every installed look and feel and the metal ocean theme
	 *
	 * @return the new {@link MenuInfo} object of type {@link MenuType#MENU}
	 */
	public static MenuInfo newLookAndFeelMenuInfo()
	{
		return newLookAndFeelMenuInfo("Look and Feel");
	}

	/**
	 * Creates the {@link MenuInfo} of the look and feel menu with the given text
	 *
	 * @param text
	 *            the text of the menu, may contain an ampersand mnemonic marker
	 * @return the new {@link MenuInfo} object of type {@link MenuType#MENU}
	 */
	public static MenuInfo newLookAndFeelMenuInfo(final String text)
	{
		MenuInfo menu = MenuInfo.builder().type(MenuType.MENU).name(BaseMenuId.LOOK_AND_FEEL_KEY)
			.text(text).build();
		String current = UIManager.getLookAndFeel() != null
			? UIManager.getLookAndFeel().getClass().getName()
			: null;
		for (Map.Entry<String, LookAndFeelInfo> entry : installed().entrySet())
		{
			boolean selected = entry.getValue().getClassName().equals(current) && !isOceanActive();
			menu.addChild(MenuInfo.builder().type(MenuType.RADIO_BUTTON_MENU_ITEM)
				.name(entry.getKey()).text(entry.getValue().getName()).group(GROUP)
				.selected(selected ? Boolean.TRUE : null).build());
		}
		menu.addChild(MenuInfo.builder().type(MenuType.RADIO_BUTTON_MENU_ITEM)
			.name(BaseMenuId.LOOK_AND_FEEL_OCEAN_KEY).text("Ocean").group(GROUP)
			.selected(isOceanActive() ? Boolean.TRUE : null).build());
		return menu;
	}

	/**
	 * Registers an action per installed look and feel and for the metal ocean theme in the given
	 * registry. The action sets the look and feel and updates the component tree of the given
	 * component
	 *
	 * @param registry
	 *            the {@link ActionRegistry} object
	 * @param component
	 *            the component whose tree is updated, usually the application frame
	 * @return the given {@link ActionRegistry} object
	 */
	public static ActionRegistry registerActions(final @NonNull ActionRegistry registry,
		final @NonNull Component component)
	{
		for (Map.Entry<String, LookAndFeelInfo> entry : installed().entrySet())
		{
			String className = entry.getValue().getClassName();
			registry.register(entry.getKey(), event -> setLookAndFeel(className, component));
		}
		registry.register(BaseMenuId.LOOK_AND_FEEL_OCEAN_KEY, event -> {
			try
			{
				LookAndFeels.setLookAndFeel(LookAndFeels.OCEAN, component);
			}
			catch (Exception e)
			{
				log.log(Level.WARNING, "Could not set the ocean look and feel", e);
			}
		});
		return registry;
	}

	/**
	 * Gets the ids of the menu items with the installed look and feels
	 *
	 * @return the list with the ids
	 */
	public static List<String> lookAndFeelIds()
	{
		List<String> ids = new ArrayList<>(installed().keySet());
		ids.add(BaseMenuId.LOOK_AND_FEEL_OCEAN_KEY);
		return ids;
	}

	private static Map<String, LookAndFeelInfo> installed()
	{
		Map<String, LookAndFeelInfo> result = new LinkedHashMap<>();
		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
		{
			String id = KNOWN_IDS.get(info.getClassName());
			if (id == null)
			{
				id = BaseMenuId.LOOK_AND_FEEL_KEY + "."
					+ info.getName().toLowerCase().replaceAll("[^a-z0-9]+", ".");
			}
			result.putIfAbsent(id, info);
		}
		return result;
	}

	private static boolean isOceanActive()
	{
		return UIManager.getLookAndFeel() instanceof javax.swing.plaf.metal.MetalLookAndFeel
			&& javax.swing.plaf.metal.MetalLookAndFeel
				.getCurrentTheme() instanceof javax.swing.plaf.metal.OceanTheme;
	}

	private static void setLookAndFeel(final String className, final Component component)
	{
		try
		{
			UIManager.setLookAndFeel(className);
			SwingUtilities.updateComponentTreeUI(component);
		}
		catch (Exception e)
		{
			log.log(Level.WARNING, "Could not set the look and feel " + className, e);
		}
	}
}
