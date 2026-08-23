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
package io.github.astrapi69.browser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * The unit test class for the enum {@link OS}
 */
class OSTest
{

	/**
	 * Resolves the expected {@link OS} constant from the system property {@code os.name} with the
	 * same precedence as {@link OS#getOperatingSystem()}: windows, linux, mac and otherwise other
	 */
	private static OS expectedFromOsName()
	{
		String osName = System.getProperty("os.name");
		if (osName.contains("Windows") || osName.contains("windows"))
		{
			return OS.WINDOWS;
		}
		if (osName.contains("Linux") || osName.contains("linux"))
		{
			return OS.LINUX;
		}
		if (osName.contains("Mac") || osName.contains("mac") || osName.contains("Darwin")
			|| osName.contains("darwin"))
		{
			return OS.MAC;
		}
		return OS.OTHER;
	}

	@Test
	void getOperatingSystem()
	{
		OS expected = expectedFromOsName();
		assertSame(expected, OS.getOperatingSystem());
		assertSame(expected, OS.get());
		assertSame(OS.getOperatingSystem(), OS.get());
	}

	@Test
	void getOperatingSystemName()
	{
		String name = OS.getOperatingSystemName();
		assertNotNull(name);
		assertEquals(OS.get().getOs(), name);
		assertEquals(expectedFromOsName().getOs(), name);
	}

	@Test
	void isWindowsIsMacIsLinux()
	{
		OS expected = expectedFromOsName();
		assertEquals(expected == OS.WINDOWS, OS.isWindows());
		assertEquals(expected == OS.MAC, OS.isMac());
		assertEquals(expected == OS.LINUX, OS.isLinux());
		int matches = (OS.isWindows() ? 1 : 0) + (OS.isMac() ? 1 : 0) + (OS.isLinux() ? 1 : 0);
		if (expected == OS.OTHER)
		{
			assertEquals(0, matches);
		}
		else
		{
			assertEquals(1, matches);
		}
	}

	@Test
	void currentPlatformIsDetected()
	{
		// the build runs on windows, linux or mac, so the detection must never fall back to OTHER
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains("windows"))
		{
			assertTrue(OS.isWindows());
			assertFalse(OS.isLinux());
			assertFalse(OS.isMac());
		}
		else if (osName.contains("linux"))
		{
			assertTrue(OS.isLinux());
			assertFalse(OS.isWindows());
			assertFalse(OS.isMac());
		}
		else if (osName.contains("mac") || osName.contains("darwin"))
		{
			assertTrue(OS.isMac());
			assertFalse(OS.isWindows());
			assertFalse(OS.isLinux());
		}
	}

	@Test
	void getOs()
	{
		assertEquals("Linux", OS.LINUX.getOs());
		assertEquals("Mac OS", OS.MAC.getOs());
		assertEquals("Other", OS.OTHER.getOs());
		assertEquals("Unix", OS.UNIX.getOs());
		assertEquals("Windows", OS.WINDOWS.getOs());
	}

	@Test
	void valuesAndValueOf()
	{
		assertEquals(5, OS.values().length);
		for (OS os : OS.values())
		{
			assertSame(os, OS.valueOf(os.name()));
			assertNotNull(os.getOs());
			assertFalse(os.getOs().isEmpty());
		}
	}
}
