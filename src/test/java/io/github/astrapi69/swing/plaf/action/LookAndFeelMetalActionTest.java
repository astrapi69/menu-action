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
package io.github.astrapi69.swing.plaf.action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;

import org.junit.jupiter.api.Test;

import io.github.astrapi69.swing.plaf.LookAndFeels;

/**
 * The unit test class for the class {@link LookAndFeelMetalAction}
 */
class LookAndFeelMetalActionTest extends AbstractLookAndFeelActionTest
{

	@Test
	void constructors()
	{
		LookAndFeelMetalAction defaultAction = new LookAndFeelMetalAction();
		assertEquals(LookAndFeels.METAL, defaultAction.getLookAndFeel());
		assertEquals("METAL", defaultAction.getValue(Action.NAME));
		assertNull(defaultAction.getComponent());

		JLabel label = new JLabel();
		// the look and feel is resolved from the name, 'ocean' in any case selects the ocean theme
		assertEquals(LookAndFeels.METAL,
			new LookAndFeelMetalAction("Metal", label).getLookAndFeel());
		assertEquals(LookAndFeels.METAL, new LookAndFeelMetalAction(null, label).getLookAndFeel());
		assertEquals(LookAndFeels.OCEAN,
			new LookAndFeelMetalAction("Ocean", label).getLookAndFeel());
		assertEquals(LookAndFeels.OCEAN,
			new LookAndFeelMetalAction("Metal OCEAN theme", label).getLookAndFeel());

		LookAndFeelMetalAction action = new LookAndFeelMetalAction("Metal", label,
			LookAndFeels.OCEAN);
		assertEquals("Metal", action.getValue(Action.NAME));
		assertEquals(label, action.getComponent());
		assertEquals(LookAndFeels.OCEAN, action.getLookAndFeel());
	}

	@Test
	void actionPerformedWithDefaultMetalTheme()
	{
		JLabel label = new JLabel();
		applyAndExpect(new LookAndFeelMetalAction("Metal", label), LookAndFeels.METAL);
		assertInstanceOf(DefaultMetalTheme.class, MetalLookAndFeel.getCurrentTheme());
		assertFalse(MetalLookAndFeel.getCurrentTheme() instanceof OceanTheme);
		assertEquals("javax.swing.plaf.metal.MetalLabelUI", label.getUI().getClass().getName());
	}

	@Test
	void actionPerformedWithOceanTheme()
	{
		JLabel label = new JLabel();
		applyAndExpect(new LookAndFeelMetalAction("Metal Ocean", label), LookAndFeels.OCEAN);
		assertInstanceOf(OceanTheme.class, MetalLookAndFeel.getCurrentTheme());
	}
}
