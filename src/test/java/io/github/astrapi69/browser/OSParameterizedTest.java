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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * The parameterized unit test class for the class {@link OS}
 */
class OSParameterizedTest
{

	/** The name of the system property that the class {@link OS} reads */
	private static final String OS_NAME = "os.name";

	/** The value of the system property 'os.name' of the machine that runs the tests */
	private String realOsName;

	@BeforeEach
	void rememberRealOsName()
	{
		realOsName = System.getProperty(OS_NAME);
	}

	@AfterEach
	void restoreRealOsName()
	{
		if (realOsName != null)
		{
			System.setProperty(OS_NAME, realOsName);
		}
		else
		{
			System.clearProperty(OS_NAME);
		}
	}

	/**
	 * Parameterized test for {@link OS#getOperatingSystem()} and {@link OS#get()} with the values
	 * that the system property 'os.name' can have
	 */
	@ParameterizedTest(name = "[{index}] the os.name ''{0}'' is detected as {1}")
	@CsvSource({ "Windows 10, WINDOWS", "Windows 11, WINDOWS", "Windows Server 2022, WINDOWS",
			"windows, WINDOWS", "Linux, LINUX", "linux, LINUX", "GNU/Linux, LINUX", "mac, MAC",
			"darwin, MAC", "Mac OS X, MAC", "Mac OS, MAC", "Darwin, MAC", "SunOS, OTHER",
			"AIX, OTHER", "FreeBSD, OTHER", "HP-UX, OTHER" })
	void getOperatingSystem(String osName, OS expected)
	{
		System.setProperty(OS_NAME, osName);

		assertSame(expected, OS.getOperatingSystem());
		assertSame(expected, OS.get());
		assertEquals(expected.getOs(), OS.getOperatingSystemName());
	}

	/**
	 * Parameterized test for {@link OS#getOperatingSystem()} with a blank system property 'os.name'
	 */
	@ParameterizedTest(name = "[{index}] the blank os.name ''{0}'' is detected as OTHER")
	@EmptySource
	@ValueSource(strings = { " ", "   ", "\t" })
	void getOperatingSystemWithBlankOsName(String osName)
	{
		System.setProperty(OS_NAME, osName);

		assertSame(OS.OTHER, OS.getOperatingSystem());
		assertEquals("Other", OS.getOperatingSystemName());
	}

	/**
	 * Parameterized test for {@link OS#isWindows()}, {@link OS#isLinux()} and {@link OS#isMac()}
	 */
	@ParameterizedTest(name = "[{index}] with the os.name ''{0}'' isWindows={1}, isLinux={2}, isMac={3}")
	@CsvSource({ "Windows 10, true, false, false", "Linux, false, true, false",
			"mac, false, false, true", "darwin, false, false, true", "Mac OS X, false, false, true",
			"SunOS, false, false, false" })
	void isWindowsIsLinuxAndIsMac(String osName, boolean expectedWindows, boolean expectedLinux,
		boolean expectedMac)
	{
		System.setProperty(OS_NAME, osName);

		assertEquals(expectedWindows, OS.isWindows());
		assertEquals(expectedLinux, OS.isLinux());
		assertEquals(expectedMac, OS.isMac());
	}

	/**
	 * Parameterized test for {@link OS#getOperatingSystem()} with a missing system property
	 * 'os.name'
	 */
	@ParameterizedTest(name = "[{index}] a missing os.name lets {0} return the OTHER/false default without throwing")
	@ValueSource(strings = { "getOperatingSystem", "get", "getOperatingSystemName", "isWindows",
			"isLinux", "isMac" })
	void getOperatingSystemWithoutOsName(String method)
	{
		System.clearProperty(OS_NAME);

		Object result = invoke(method);

		switch (method)
		{
			case "getOperatingSystem", "get" -> assertSame(OS.OTHER, result);
			case "getOperatingSystemName" -> assertEquals("Other", result);
			case "isWindows", "isLinux", "isMac" -> assertEquals(Boolean.FALSE, result);
			default -> throw new IllegalArgumentException("Unknown method " + method);
		}
	}

	/**
	 * Parameterized test for {@link OS#getOs()} of all constants of the enum {@link OS}
	 */
	@ParameterizedTest(name = "[{index}] the constant {0} provides the operating system name")
	@EnumSource(OS.class)
	void getOs(OS os)
	{
		assertNotNull(os.getOs());
		assertFalse(os.getOs().isBlank());
		assertSame(os, OS.valueOf(os.name()));
		assertSame(os, OS.values()[os.ordinal()]);
	}

	/**
	 * Parameterized test for the operating system names of the constants of the enum {@link OS}
	 */
	@ParameterizedTest(name = "[{index}] the operating system name of {0} is ''{1}''")
	@CsvSource({ "LINUX, Linux", "MAC, Mac OS", "OTHER, Other", "UNIX, Unix", "WINDOWS, Windows" })
	void getOsOfConstant(OS os, String expectedOs)
	{
		assertEquals(expectedOs, os.getOs());
	}

	/**
	 * Parameterized test for {@link OS#valueOf(String)} with names of no enum constant
	 */
	@ParameterizedTest(name = "[{index}] valueOf(''{0}'') throws an IllegalArgumentException")
	@EmptySource
	@ValueSource(strings = { " ", "linux", "Linux", "WINDOWS ", "NOT_AN_OS" })
	void valueOfWithUnknownName(String name)
	{
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
			() -> OS.valueOf(name));
		assertNotNull(exception.getMessage());
		assertTrue(exception.getMessage().contains("No enum constant"),
			() -> "the message '" + exception.getMessage() + "' should contain 'No enum constant'");
	}

	/**
	 * Parameterized test for {@link OS#valueOf(String)} with a null name
	 */
	@ParameterizedTest(name = "[{index}] valueOf(null) throws a NullPointerException")
	@NullSource
	void valueOfWithNullName(String name)
	{
		assertThrows(NullPointerException.class, () -> OS.valueOf(name));
	}

	private static Object invoke(final String method)
	{
		return switch (method)
		{
			case "getOperatingSystem" -> OS.getOperatingSystem();
			case "get" -> OS.get();
			case "getOperatingSystemName" -> OS.getOperatingSystemName();
			case "isWindows" -> OS.isWindows();
			case "isLinux" -> OS.isLinux();
			case "isMac" -> OS.isMac();
			default -> throw new IllegalArgumentException("Unknown method " + method);
		};
	}
}
