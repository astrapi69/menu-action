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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * The parameterized unit test class for the class {@link BaseMenuId}
 */
class BaseMenuIdParameterizedTest
{

	/** The prefix that every properties key of the enum {@link BaseMenuId} has */
	private static final String GLOBAL_PREFIX = "global.";

	/**
	 * Parameterized test for {@link BaseMenuId#propertiesKey()},
	 * {@link BaseMenuId#getBaseMenuIdsAsMap()} and {@link BaseMenuId#getBaseMenuIdKeys()}
	 */
	@ParameterizedTest(name = "[{index}] the properties key of {0} is a global menu id key")
	@EnumSource(BaseMenuId.class)
	void propertiesKeyIsAGlobalMenuIdKey(BaseMenuId baseMenuId)
	{
		String propertiesKey = baseMenuId.propertiesKey();

		assertNotNull(propertiesKey);
		assertFalse(propertiesKey.isBlank());
		assertTrue(propertiesKey.startsWith(GLOBAL_PREFIX), () -> "the properties key '"
			+ propertiesKey + "' should start with '" + GLOBAL_PREFIX + "'");

		Map<String, Boolean> baseMenuIdsAsMap = BaseMenuId.getBaseMenuIdsAsMap();
		Set<String> baseMenuIdKeys = BaseMenuId.getBaseMenuIdKeys();
		assertTrue(baseMenuIdsAsMap.containsKey(propertiesKey));
		assertEquals(Boolean.TRUE, baseMenuIdsAsMap.get(propertiesKey));
		assertTrue(baseMenuIdKeys.contains(propertiesKey));
		assertEquals(BaseMenuId.values().length, baseMenuIdsAsMap.size());
		assertEquals(BaseMenuId.values().length, baseMenuIdKeys.size());
	}

	/**
	 * Parameterized test that the public key constants of the enum {@link BaseMenuId} match the
	 * properties keys of the enum constants
	 */
	@ParameterizedTest(name = "[{index}] the properties key of {0} is ''{2}''")
	@MethodSource("baseMenuIdsWithKeys")
	void keyConstantsMatchTheEnumConstants(BaseMenuId baseMenuId, String keyConstant,
		String expectedKey)
	{
		assertEquals(expectedKey, keyConstant);
		assertEquals(expectedKey, baseMenuId.propertiesKey());
		assertEquals(keyConstant, baseMenuId.propertiesKey());
	}

	static Stream<Arguments> baseMenuIdsWithKeys()
	{
		return Stream.of(Arguments.of(BaseMenuId.EDIT, BaseMenuId.EDIT_KEY, "global.menu.edit"),
			Arguments.of(BaseMenuId.FILE, BaseMenuId.FILE_KEY, "global.menu.file"),
			Arguments.of(BaseMenuId.HELP, BaseMenuId.HELP_KEY, "global.menu.help"),
			Arguments.of(BaseMenuId.HELP_CONTENT, BaseMenuId.HELP_CONTENT_KEY,
				"global.menu.help.content"),
			Arguments.of(BaseMenuId.HELP_DONATE, BaseMenuId.HELP_DONATE_KEY,
				"global.menu.help.donate"),
			Arguments.of(BaseMenuId.HELP_LICENSE, BaseMenuId.HELP_LICENSE_KEY,
				"global.menu.help.license"),
			Arguments.of(BaseMenuId.HELP_INFO, BaseMenuId.HELP_INFO_KEY, "global.menu.help.info"),
			Arguments.of(BaseMenuId.LOOK_AND_FEEL, BaseMenuId.LOOK_AND_FEEL_KEY,
				"global.menu.look.and.feel"),
			Arguments.of(BaseMenuId.LOOK_AND_FEEL_GTK, BaseMenuId.LOOK_AND_FEEL_GTK_KEY,
				"global.menu.look.and.feel.gtk"),
			Arguments.of(BaseMenuId.LOOK_AND_FEEL_METAL, BaseMenuId.LOOK_AND_FEEL_METAL_KEY,
				"global.menu.look.and.feel.metal"),
			Arguments.of(BaseMenuId.LOOK_AND_FEEL_OCEAN, BaseMenuId.LOOK_AND_FEEL_OCEAN_KEY,
				"global.menu.look.and.feel.ocean"),
			Arguments.of(BaseMenuId.LOOK_AND_FEEL_MOTIF, BaseMenuId.LOOK_AND_FEEL_MOTIF_KEY,
				"global.menu.look.and.feel.motif"),
			Arguments.of(BaseMenuId.LOOK_AND_FEEL_NIMBUS, BaseMenuId.LOOK_AND_FEEL_NIMBUS_KEY,
				"global.menu.look.and.feel.nimbus"),
			Arguments.of(BaseMenuId.LOOK_AND_FEEL_SYSTEM, BaseMenuId.LOOK_AND_FEEL_SYSTEM_KEY,
				"global.menu.look.and.feel.system"),
			Arguments.of(BaseMenuId.TOGGLE_FULLSCREEN, BaseMenuId.TOGGLE_FULLSCREEN_KEY,
				"global.menu.file.toggle.fullscreen"),
			Arguments.of(BaseMenuId.EXIT, BaseMenuId.EXIT_KEY, "global.menu.file.exit"),
			Arguments.of(BaseMenuId.MENU_BAR, BaseMenuId.MENU_BAR_KEY, "global.menu.bar"),
			Arguments.of(BaseMenuId.TOOL_BAR, BaseMenuId.TOOL_BAR_KEY, "global.tool.bar"));
	}

	/**
	 * Parameterized test for {@link BaseMenuId#valueOf(String)} and {@link BaseMenuId#name()}
	 */
	@ParameterizedTest(name = "[{index}] valueOf(''{0}'') resolves the same constant again")
	@EnumSource(BaseMenuId.class)
	void valueOfRoundTrip(BaseMenuId baseMenuId)
	{
		assertSame(baseMenuId, BaseMenuId.valueOf(baseMenuId.name()));
		assertSame(baseMenuId, BaseMenuId.values()[baseMenuId.ordinal()]);
	}

	/**
	 * Parameterized test for {@link BaseMenuId#valueOf(String)} with names of no enum constant
	 */
	@ParameterizedTest(name = "[{index}] valueOf(''{0}'') throws an IllegalArgumentException")
	@EmptySource
	@ValueSource(strings = { " ", "edit", "global.menu.edit", "NOT_A_BASE_MENU_ID" })
	void valueOfWithUnknownName(String name)
	{
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
			() -> BaseMenuId.valueOf(name));
		assertNotNull(exception.getMessage());
		assertTrue(exception.getMessage().contains("No enum constant"),
			() -> "the message '" + exception.getMessage() + "' should contain 'No enum constant'");
	}

	/**
	 * Parameterized test for {@link BaseMenuId#valueOf(String)} with a null name
	 */
	@ParameterizedTest(name = "[{index}] valueOf(null) throws a NullPointerException")
	@NullSource
	void valueOfWithNullName(String name)
	{
		assertThrows(NullPointerException.class, () -> BaseMenuId.valueOf(name));
	}
}
