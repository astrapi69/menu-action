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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;

import org.junit.jupiter.api.Test;

import io.github.astrapi69.swing.plaf.LookAndFeels;

/**
 * The unit test class for the class {@link LookAndFeelAction}
 */
class LookAndFeelActionTest extends AbstractLookAndFeelActionTest
{

	@Test
	void defaultConstructor()
	{
		LookAndFeelAction action = new LookAndFeelAction();
		assertEquals(LookAndFeels.SYSTEM, action.getLookAndFeel());
		assertEquals("SYSTEM", action.getValue(Action.NAME));
		assertNull(action.getComponent());
	}

	@Test
	void constructorWithLookAndFeel()
	{
		LookAndFeelAction action = new LookAndFeelAction(LookAndFeels.NIMBUS);
		assertEquals(LookAndFeels.NIMBUS, action.getLookAndFeel());
		assertEquals("NIMBUS", action.getValue(Action.NAME));
		assertNull(action.getComponent());
	}

	@Test
	void constructorWithNameAndSetters()
	{
		JLabel label = new JLabel();
		LookAndFeelAction action = new LookAndFeelAction("Change look and feel");
		assertEquals("Change look and feel", action.getValue(Action.NAME));
		assertNull(action.getLookAndFeel());
		assertNull(action.getComponent());

		action.setLookAndFeel(LookAndFeels.OCEAN);
		action.setComponent(label);
		assertEquals(LookAndFeels.OCEAN, action.getLookAndFeel());
		assertEquals(label, action.getComponent());
	}

	@Test
	void constructorWithNameComponentAndLookAndFeel()
	{
		JLabel label = new JLabel();
		LookAndFeelAction action = new LookAndFeelAction("Nimbus", label, LookAndFeels.NIMBUS);
		assertEquals("Nimbus", action.getValue(Action.NAME));
		assertEquals(label, action.getComponent());
		assertEquals(LookAndFeels.NIMBUS, action.getLookAndFeel());
	}

	@Test
	void actionPerformedSetsLookAndFeelAndUpdatesComponent()
	{
		JLabel label = new JLabel("a label");
		LookAndFeelAction action = new LookAndFeelAction("Nimbus", label, LookAndFeels.NIMBUS);
		applyAndExpect(action, LookAndFeels.NIMBUS);
		assertEquals("javax.swing.plaf.synth.SynthLabelUI", label.getUI().getClass().getName());
	}

	@Test
	void actionPerformedLogsNotAvailableLookAndFeel()
	{
		// the windows look and feel is not available on other platforms and the failure must be
		// logged and not thrown
		applyTolerant(new LookAndFeelAction("Windows", new JLabel(), LookAndFeels.WINDOWS));
	}

	@Test
	void actionPerformedDelegatesToCallbackMethod()
	{
		List<ActionEvent> received = new ArrayList<>();
		LookAndFeelAction action = new LookAndFeelAction("callback")
		{
			@Override
			protected void onChangeOfLookAndFeel(final ActionEvent event)
			{
				received.add(event);
			}
		};
		ActionEvent event = newActionEvent(action);
		action.actionPerformed(event);
		assertEquals(List.of(event), received);
	}

	@Test
	void actionBoundToMenuItem()
	{
		JLabel label = new JLabel();
		LookAndFeelAction action = new LookAndFeelAction("Metal Ocean", label, LookAndFeels.OCEAN);
		JMenuItem menuItem = new JMenuItem(action);
		assertEquals("Metal Ocean", menuItem.getText());
		menuItem.doClick();
		assertInstanceOf(MetalLookAndFeel.class, UIManager.getLookAndFeel());
		assertInstanceOf(OceanTheme.class, MetalLookAndFeel.getCurrentTheme());
	}
}
