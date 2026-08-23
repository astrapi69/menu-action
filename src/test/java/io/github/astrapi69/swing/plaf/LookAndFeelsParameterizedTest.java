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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.util.stream.Stream;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;
import javax.swing.plaf.metal.OceanTheme;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * The parameterized unit test class for the class {@link LookAndFeels}
 */
class LookAndFeelsParameterizedTest
{

	/** The look and feel that was installed before a test method has changed it */
	private LookAndFeel previousLookAndFeel;

	/** The metal theme that was current before a test method has changed it */
	private MetalTheme previousMetalTheme;

	@BeforeEach
	void rememberCurrentLookAndFeel()
	{
		previousLookAndFeel = UIManager.getLookAndFeel();
		previousMetalTheme = MetalLookAndFeel.getCurrentTheme();
	}

	@AfterEach
	void restorePreviousLookAndFeel() throws Exception
	{
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
	 * Parameterized test for {@link LookAndFeels#getLookAndFeelName()},
	 * {@link LookAndFeels#isMetalTheme()} and {@link LookAndFeels#newMetalTheme()}. Only
	 * {@link LookAndFeels#METAL} and {@link LookAndFeels#OCEAN} provide a metal theme
	 */
	@ParameterizedTest(name = "[{index}] {0} has a look and feel name and a consistent metal theme")
	@EnumSource(LookAndFeels.class)
	void lookAndFeelNameAndMetalTheme(LookAndFeels lookAndFeels)
	{
		assertNotNull(lookAndFeels.getLookAndFeelName());
		assertFalse(lookAndFeels.getLookAndFeelName().isBlank());

		boolean expectedMetalTheme = lookAndFeels == LookAndFeels.METAL
			|| lookAndFeels == LookAndFeels.OCEAN;
		assertEquals(expectedMetalTheme, lookAndFeels.isMetalTheme());

		if (expectedMetalTheme)
		{
			MetalTheme metalTheme = lookAndFeels.newMetalTheme();
			assertNotNull(metalTheme);
			assertEquals(
				lookAndFeels == LookAndFeels.OCEAN ? OceanTheme.class : DefaultMetalTheme.class,
				metalTheme.getClass());
			assertNotSame(metalTheme, lookAndFeels.newMetalTheme());
			assertEquals("javax.swing.plaf.metal.MetalLookAndFeel",
				lookAndFeels.getLookAndFeelName());
		}
		else
		{
			assertNull(lookAndFeels.newMetalTheme());
		}
	}

	/**
	 * Parameterized test for the class names of the constants of the enum {@link LookAndFeels} that
	 * do not depend on the current platform
	 */
	@ParameterizedTest(name = "[{index}] the look and feel name of {0} is {1}")
	@CsvSource({ "GTK, com.sun.java.swing.plaf.gtk.GTKLookAndFeel",
			"METAL, javax.swing.plaf.metal.MetalLookAndFeel",
			"OCEAN, javax.swing.plaf.metal.MetalLookAndFeel",
			"MOTIF, com.sun.java.swing.plaf.motif.MotifLookAndFeel",
			"MULTI, javax.swing.plaf.multi.MultiLookAndFeel",
			"NIMBUS, javax.swing.plaf.nimbus.NimbusLookAndFeel",
			"SYNTH, javax.swing.plaf.synth.SynthLookAndFeel",
			"WINDOWS, com.sun.java.swing.plaf.windows.WindowsLookAndFeel",
			"WINDOWS_CLASSIC, com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel" })
	void lookAndFeelNameOfPlatformIndependentConstants(LookAndFeels lookAndFeels,
		String expectedLookAndFeelName)
	{
		assertEquals(expectedLookAndFeelName, lookAndFeels.getLookAndFeelName());
	}

	/**
	 * Parameterized test for the class names of the constants of the enum {@link LookAndFeels} that
	 * are resolved from the {@link UIManager}
	 */
	@ParameterizedTest(name = "[{index}] the look and feel name of {0} comes from the UIManager")
	@EnumSource(value = LookAndFeels.class, names = { "SYSTEM", "CROSSPLATFORM" })
	void lookAndFeelNameOfPlatformDependentConstants(LookAndFeels lookAndFeels)
	{
		String expectedLookAndFeelName = lookAndFeels == LookAndFeels.SYSTEM
			? UIManager.getSystemLookAndFeelClassName()
			: UIManager.getCrossPlatformLookAndFeelClassName();
		assertEquals(expectedLookAndFeelName, lookAndFeels.getLookAndFeelName());
	}

	/**
	 * Parameterized test for {@link LookAndFeels#valueOf(String)} and {@link LookAndFeels#name()}
	 */
	@ParameterizedTest(name = "[{index}] valueOf(''{0}'') resolves the same constant again")
	@EnumSource(LookAndFeels.class)
	void valueOfRoundTrip(LookAndFeels lookAndFeels)
	{
		assertSame(lookAndFeels, LookAndFeels.valueOf(lookAndFeels.name()));
	}

	/**
	 * Parameterized test for {@link LookAndFeels#setLookAndFeel(LookAndFeels)}. The constants that
	 * are not installed on this machine are skipped
	 */
	@ParameterizedTest(name = "[{index}] setLookAndFeel({0}) installs the look and feel")
	@EnumSource(LookAndFeels.class)
	void setLookAndFeelWithInstalledConstants(LookAndFeels lookAndFeels) throws Exception
	{
		assumeTrue(isInstalled(lookAndFeels),
			() -> lookAndFeels.getLookAndFeelName() + " is not installed on this machine");
		assumeTrue(canBeSet(lookAndFeels),
			() -> lookAndFeels.getLookAndFeelName() + " is not supported in this environment");

		LookAndFeels.setLookAndFeel(lookAndFeels);

		assertEquals(lookAndFeels.getLookAndFeelName(),
			LookAndFeels.getCurrentLookAndFeel().getClass().getName());
		assertSame(UIManager.getLookAndFeel(), LookAndFeels.getCurrentLookAndFeel());
		if (lookAndFeels.isMetalTheme())
		{
			assertEquals(lookAndFeels.newMetalTheme().getClass(),
				MetalLookAndFeel.getCurrentTheme().getClass());
		}
	}

	/**
	 * Parameterized test for {@link LookAndFeels#setLookAndFeel(LookAndFeels, Component)}. The
	 * given lightweight component is updated and the given look and feel is returned
	 */
	@ParameterizedTest(name = "[{index}] setLookAndFeel({0}, component) updates the component")
	@EnumSource(LookAndFeels.class)
	void setLookAndFeelWithComponent(LookAndFeels lookAndFeels) throws Exception
	{
		assumeTrue(isInstalled(lookAndFeels),
			() -> lookAndFeels.getLookAndFeelName() + " is not installed on this machine");
		assumeTrue(canBeSet(lookAndFeels),
			() -> lookAndFeels.getLookAndFeelName() + " is not supported in this environment");
		JLabel component = new JLabel("look and feel");

		assertSame(lookAndFeels, LookAndFeels.setLookAndFeel(lookAndFeels, component));

		assertEquals(lookAndFeels.getLookAndFeelName(),
			LookAndFeels.getCurrentLookAndFeel().getClass().getName());
		assertNotNull(component.getUI());
	}

	/**
	 * Parameterized test for {@link LookAndFeels#setLookAndFeel(LookAndFeels, Window)}. This test
	 * needs a {@link JFrame} object and is therefore skipped in a headless environment
	 */
	@ParameterizedTest(name = "[{index}] setLookAndFeel({0}, window) updates and packs the window")
	@EnumSource(value = LookAndFeels.class, names = { "METAL", "OCEAN", "NIMBUS" })
	void setLookAndFeelWithWindow(LookAndFeels lookAndFeels) throws Exception
	{
		assumeFalse(GraphicsEnvironment.isHeadless());
		assumeTrue(canBeSet(lookAndFeels),
			() -> lookAndFeels.getLookAndFeelName() + " is not supported in this environment");
		JFrame frame = new JFrame();
		try
		{
			frame.getContentPane().add(new JLabel("look and feel"));

			assertSame(lookAndFeels, LookAndFeels.setLookAndFeel(lookAndFeels, frame));

			assertEquals(lookAndFeels.getLookAndFeelName(),
				LookAndFeels.getCurrentLookAndFeel().getClass().getName());
			assertTrue(frame.getSize().width > 0);
		}
		finally
		{
			frame.dispose();
		}
	}

	/**
	 * Parameterized test for the null checks of the setLookAndFeel methods of the enum
	 * {@link LookAndFeels}
	 */
	@ParameterizedTest(name = "[{index}] {0} throws a NullPointerException")
	@MethodSource("nullArguments")
	void setLookAndFeelWithNullArgument(String description, String expectedMessagePart,
		Executable executable)
	{
		NullPointerException exception = assertThrows(NullPointerException.class, executable,
			description);
		assertNotNull(exception.getMessage());
		assertTrue(exception.getMessage().contains(expectedMessagePart), () -> "the message '"
			+ exception.getMessage() + "' should contain '" + expectedMessagePart + "'");
	}

	static Stream<Arguments> nullArguments()
	{
		JLabel component = new JLabel("look and feel");
		return Stream.of(
			Arguments.of("setLookAndFeel(null)", "lookAndFeels is marked non-null but is null",
				(Executable)() -> LookAndFeels.setLookAndFeel(null)),
			Arguments.of("setLookAndFeel(null, component)",
				"lookAndFeels is marked non-null but is null",
				(Executable)() -> LookAndFeels.setLookAndFeel(null, component)),
			Arguments.of("setLookAndFeel(NIMBUS, (Component)null)",
				"component is marked non-null but is null",
				(Executable)() -> LookAndFeels.setLookAndFeel(LookAndFeels.NIMBUS,
					(Component)null)),
			Arguments.of("setLookAndFeel(NIMBUS, (Window)null)",
				"window is marked non-null but is null",
				(Executable)() -> LookAndFeels.setLookAndFeel(LookAndFeels.NIMBUS, (Window)null)),
			Arguments.of("setLookAndFeel(null, (Window)null)",
				"lookAndFeels is marked non-null but is null",
				(Executable)() -> LookAndFeels.setLookAndFeel(null, (Window)null)));
	}

	private static boolean isInstalled(final LookAndFeels lookAndFeels)
	{
		for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
		{
			if (info.getClassName().equals(lookAndFeels.getLookAndFeelName()))
			{
				return true;
			}
		}
		return false;
	}

	private static boolean canBeSet(final LookAndFeels lookAndFeels)
	{
		LookAndFeel current = UIManager.getLookAndFeel();
		MetalTheme currentTheme = MetalLookAndFeel.getCurrentTheme();
		try
		{
			LookAndFeels.setLookAndFeel(lookAndFeels);
			return true;
		}
		catch (Exception exception)
		{
			return false;
		}
		finally
		{
			try
			{
				if (currentTheme != null)
				{
					MetalLookAndFeel.setCurrentTheme(currentTheme);
				}
				if (current != null)
				{
					UIManager.setLookAndFeel(current);
				}
			}
			catch (Exception exception)
			{
				// the restore of the previous look and feel is best effort only
			}
		}
	}
}
