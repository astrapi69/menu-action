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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * The unit test class for the enum {@link LookAndFeels}
 */
class LookAndFeelsTest
{

	private LookAndFeel previous;

	@AfterEach
	void restore() throws Exception
	{
		if (previous != null)
		{
			UIManager.setLookAndFeel(previous);
		}
	}

	@Test
	void metalThemes() throws Exception
	{
		previous = UIManager.getLookAndFeel();

		LookAndFeels.setLookAndFeel(LookAndFeels.OCEAN);
		assertInstanceOf(MetalLookAndFeel.class, LookAndFeels.getCurrentLookAndFeel());
		assertInstanceOf(OceanTheme.class, MetalLookAndFeel.getCurrentTheme());

		LookAndFeels.setLookAndFeel(LookAndFeels.METAL);
		assertInstanceOf(MetalLookAndFeel.class, LookAndFeels.getCurrentLookAndFeel());
		assertInstanceOf(DefaultMetalTheme.class, MetalLookAndFeel.getCurrentTheme());
		assertFalse(MetalLookAndFeel.getCurrentTheme() instanceof OceanTheme);

		assertTrue(LookAndFeels.OCEAN.isMetalTheme());
		assertTrue(LookAndFeels.METAL.isMetalTheme());
		assertFalse(LookAndFeels.NIMBUS.isMetalTheme());
		assertNull(LookAndFeels.NIMBUS.newMetalTheme());
		assertEquals("javax.swing.plaf.metal.MetalLookAndFeel",
			LookAndFeels.OCEAN.getLookAndFeelName());
	}

	@Test
	void nimbus() throws Exception
	{
		previous = UIManager.getLookAndFeel();
		LookAndFeels.setLookAndFeel(LookAndFeels.NIMBUS);
		assertEquals(LookAndFeels.NIMBUS.getLookAndFeelName(),
			LookAndFeels.getCurrentLookAndFeel().getClass().getName());
	}
}
