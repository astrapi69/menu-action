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
import static org.junit.jupiter.api.Assertions.assertNull;

import javax.swing.Action;
import javax.swing.JLabel;

import org.junit.jupiter.api.Test;

import io.github.astrapi69.swing.plaf.LookAndFeels;

/**
 * The unit test class for the class {@link LookAndFeelNimbusAction}
 */
class LookAndFeelNimbusActionTest extends AbstractLookAndFeelActionTest
{

	@Test
	void constructors()
	{
		LookAndFeelNimbusAction defaultAction = new LookAndFeelNimbusAction();
		assertEquals(LookAndFeels.NIMBUS, defaultAction.getLookAndFeel());
		assertEquals("NIMBUS", defaultAction.getValue(Action.NAME));
		assertNull(defaultAction.getComponent());

		JLabel label = new JLabel();
		LookAndFeelNimbusAction action = new LookAndFeelNimbusAction("Nimbus", label);
		assertEquals("Nimbus", action.getValue(Action.NAME));
		assertEquals(label, action.getComponent());
		assertEquals(LookAndFeels.NIMBUS, action.getLookAndFeel());
	}

	@Test
	void actionPerformedInstallsNimbus()
	{
		JLabel label = new JLabel();
		applyAndExpect(new LookAndFeelNimbusAction("Nimbus", label), LookAndFeels.NIMBUS);
		assertEquals("javax.swing.plaf.synth.SynthLabelUI", label.getUI().getClass().getName());
	}
}
