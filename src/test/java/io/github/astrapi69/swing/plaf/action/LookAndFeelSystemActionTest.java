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
import javax.swing.UIManager;

import org.junit.jupiter.api.Test;

import io.github.astrapi69.swing.plaf.LookAndFeels;

/**
 * The unit test class for the class {@link LookAndFeelSystemAction}
 */
class LookAndFeelSystemActionTest extends AbstractLookAndFeelActionTest
{

	@Test
	void constructors()
	{
		LookAndFeelSystemAction defaultAction = new LookAndFeelSystemAction();
		assertEquals(LookAndFeels.SYSTEM, defaultAction.getLookAndFeel());
		assertEquals("SYSTEM", defaultAction.getValue(Action.NAME));
		assertNull(defaultAction.getComponent());
		assertEquals(UIManager.getSystemLookAndFeelClassName(),
			defaultAction.getLookAndFeel().getLookAndFeelName());

		JLabel label = new JLabel();
		LookAndFeelSystemAction action = new LookAndFeelSystemAction("System", label);
		assertEquals("System", action.getValue(Action.NAME));
		assertEquals(label, action.getComponent());
		assertEquals(LookAndFeels.SYSTEM, action.getLookAndFeel());
	}

	@Test
	void actionPerformedInstallsSystemLookAndFeel()
	{
		JLabel label = new JLabel();
		applyAndExpect(new LookAndFeelSystemAction("System", label), LookAndFeels.SYSTEM);
		assertEquals(UIManager.getSystemLookAndFeelClassName(),
			UIManager.getLookAndFeel().getClass().getName());
	}
}
