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
package io.github.astrapi69.swing.menu.enumeration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * The unit test class for the enum {@link BaseMenuId}
 */
class BaseMenuIdTest
{

	@Test
	void propertiesKeyOfEveryConstant()
	{
		assertEquals(BaseMenuId.EDIT_KEY, BaseMenuId.EDIT.propertiesKey());
		assertEquals(BaseMenuId.FILE_KEY, BaseMenuId.FILE.propertiesKey());
		assertEquals(BaseMenuId.HELP_KEY, BaseMenuId.HELP.propertiesKey());
		assertEquals(BaseMenuId.HELP_CONTENT_KEY, BaseMenuId.HELP_CONTENT.propertiesKey());
		assertEquals(BaseMenuId.HELP_DONATE_KEY, BaseMenuId.HELP_DONATE.propertiesKey());
		assertEquals(BaseMenuId.HELP_LICENSE_KEY, BaseMenuId.HELP_LICENSE.propertiesKey());
		assertEquals(BaseMenuId.HELP_INFO_KEY, BaseMenuId.HELP_INFO.propertiesKey());
		assertEquals(BaseMenuId.LOOK_AND_FEEL_KEY, BaseMenuId.LOOK_AND_FEEL.propertiesKey());
		assertEquals(BaseMenuId.LOOK_AND_FEEL_GTK_KEY,
			BaseMenuId.LOOK_AND_FEEL_GTK.propertiesKey());
		assertEquals(BaseMenuId.LOOK_AND_FEEL_METAL_KEY,
			BaseMenuId.LOOK_AND_FEEL_METAL.propertiesKey());
		assertEquals(BaseMenuId.LOOK_AND_FEEL_OCEAN_KEY,
			BaseMenuId.LOOK_AND_FEEL_OCEAN.propertiesKey());
		assertEquals(BaseMenuId.LOOK_AND_FEEL_MOTIF_KEY,
			BaseMenuId.LOOK_AND_FEEL_MOTIF.propertiesKey());
		assertEquals(BaseMenuId.LOOK_AND_FEEL_NIMBUS_KEY,
			BaseMenuId.LOOK_AND_FEEL_NIMBUS.propertiesKey());
		assertEquals(BaseMenuId.LOOK_AND_FEEL_SYSTEM_KEY,
			BaseMenuId.LOOK_AND_FEEL_SYSTEM.propertiesKey());
		assertEquals(BaseMenuId.TOGGLE_FULLSCREEN_KEY,
			BaseMenuId.TOGGLE_FULLSCREEN.propertiesKey());
		assertEquals(BaseMenuId.EXIT_KEY, BaseMenuId.EXIT.propertiesKey());
		assertEquals(BaseMenuId.MENU_BAR_KEY, BaseMenuId.MENU_BAR.propertiesKey());
		assertEquals(BaseMenuId.TOOL_BAR_KEY, BaseMenuId.TOOL_BAR.propertiesKey());
		assertEquals(18, BaseMenuId.values().length);
	}

	@Test
	void keyConstants()
	{
		assertEquals("global.tool.bar", BaseMenuId.TOOL_BAR_KEY);
		assertEquals("global.menu.bar", BaseMenuId.MENU_BAR_KEY);
		assertEquals("global.menu.edit", BaseMenuId.EDIT_KEY);
		assertEquals("global.menu.look.and.feel", BaseMenuId.LOOK_AND_FEEL_KEY);
		assertEquals("global.menu.look.and.feel.gtk", BaseMenuId.LOOK_AND_FEEL_GTK_KEY);
		assertEquals("global.menu.look.and.feel.metal", BaseMenuId.LOOK_AND_FEEL_METAL_KEY);
		assertEquals("global.menu.look.and.feel.ocean", BaseMenuId.LOOK_AND_FEEL_OCEAN_KEY);
		assertEquals("global.menu.look.and.feel.motif", BaseMenuId.LOOK_AND_FEEL_MOTIF_KEY);
		assertEquals("global.menu.look.and.feel.nimbus", BaseMenuId.LOOK_AND_FEEL_NIMBUS_KEY);
		assertEquals("global.menu.look.and.feel.system", BaseMenuId.LOOK_AND_FEEL_SYSTEM_KEY);
		assertEquals("global.menu.file", BaseMenuId.FILE_KEY);
		assertEquals("global.menu.help", BaseMenuId.HELP_KEY);
		assertEquals("global.menu.help.content", BaseMenuId.HELP_CONTENT_KEY);
		assertEquals("global.menu.help.donate", BaseMenuId.HELP_DONATE_KEY);
		assertEquals("global.menu.help.license", BaseMenuId.HELP_LICENSE_KEY);
		assertEquals("global.menu.help.info", BaseMenuId.HELP_INFO_KEY);
		assertEquals("global.menu.file.toggle.fullscreen", BaseMenuId.TOGGLE_FULLSCREEN_KEY);
		assertEquals("global.menu.file.exit", BaseMenuId.EXIT_KEY);
	}

	@Test
	void getBaseMenuIdsAsMap()
	{
		Map<String, Boolean> map = BaseMenuId.getBaseMenuIdsAsMap();
		assertEquals(BaseMenuId.values().length, map.size());
		List<String> expectedOrder = new ArrayList<>();
		for (BaseMenuId baseMenuId : BaseMenuId.values())
		{
			assertTrue(map.containsKey(baseMenuId.propertiesKey()));
			assertEquals(Boolean.TRUE, map.get(baseMenuId.propertiesKey()));
			expectedOrder.add(baseMenuId.propertiesKey());
		}
		// the map keeps the declaration order of the constants
		assertEquals(expectedOrder, new ArrayList<>(map.keySet()));
		// every call creates a new mutable map
		Map<String, Boolean> other = BaseMenuId.getBaseMenuIdsAsMap();
		assertEquals(map, other);
		other.put("custom", false);
		assertEquals(BaseMenuId.values().length, map.size());
	}

	@Test
	void getBaseMenuIdKeys()
	{
		Set<String> keys = BaseMenuId.getBaseMenuIdKeys();
		assertEquals(BaseMenuId.values().length, keys.size());
		List<String> expectedOrder = new ArrayList<>();
		for (BaseMenuId baseMenuId : BaseMenuId.values())
		{
			assertTrue(keys.contains(baseMenuId.propertiesKey()));
			expectedOrder.add(baseMenuId.propertiesKey());
		}
		// the set keeps the declaration order of the constants
		assertEquals(expectedOrder, new ArrayList<>(keys));
		assertEquals(BaseMenuId.getBaseMenuIdsAsMap().keySet(), keys);
		assertTrue(keys.contains(BaseMenuId.EXIT_KEY));
		assertTrue(keys.contains(BaseMenuId.TOOL_BAR_KEY));
		// every call creates a new mutable set
		Set<String> other = BaseMenuId.getBaseMenuIdKeys();
		other.add("custom");
		assertEquals(BaseMenuId.values().length, keys.size());
	}

	@Test
	void keysAreUniqueAndGlobal()
	{
		Set<String> keys = BaseMenuId.getBaseMenuIdKeys();
		for (String key : keys)
		{
			assertTrue(key.startsWith("global."), key);
		}
		for (BaseMenuId baseMenuId : BaseMenuId.values())
		{
			assertSame(baseMenuId, BaseMenuId.valueOf(baseMenuId.name()));
		}
	}
}
