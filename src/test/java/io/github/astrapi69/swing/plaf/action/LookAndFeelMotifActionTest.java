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
 * The unit test class for the class {@link LookAndFeelMotifAction}
 */
class LookAndFeelMotifActionTest extends AbstractLookAndFeelActionTest
{

	@Test
	void constructors()
	{
		LookAndFeelMotifAction defaultAction = new LookAndFeelMotifAction();
		assertEquals(LookAndFeels.MOTIF, defaultAction.getLookAndFeel());
		assertEquals("MOTIF", defaultAction.getValue(Action.NAME));
		assertNull(defaultAction.getComponent());

		JLabel label = new JLabel();
		LookAndFeelMotifAction action = new LookAndFeelMotifAction("CDE/Motif", label);
		assertEquals("CDE/Motif", action.getValue(Action.NAME));
		assertEquals(label, action.getComponent());
		assertEquals(LookAndFeels.MOTIF, action.getLookAndFeel());
	}

	@Test
	void actionPerformedInstallsMotifOrLogs()
	{
		JLabel label = new JLabel();
		applyTolerant(new LookAndFeelMotifAction("Motif", label));
		if (logRecords.isEmpty())
		{
			assertEquals(LookAndFeels.MOTIF.getLookAndFeelName(),
				UIManager.getLookAndFeel().getClass().getName());
			assertEquals("com.sun.java.swing.plaf.motif.MotifLabelUI",
				label.getUI().getClass().getName());
		}
	}
}
