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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JLabel;

import org.junit.jupiter.api.Test;

import io.github.astrapi69.swing.plaf.LookAndFeels;

/**
 * The unit test class for the class {@link LookAndFeelMultiAction}
 */
class LookAndFeelMultiActionTest extends AbstractLookAndFeelActionTest
{

	@Test
	void constructors()
	{
		LookAndFeelMultiAction defaultAction = new LookAndFeelMultiAction();
		assertEquals(LookAndFeels.MULTI, defaultAction.getLookAndFeel());
		assertEquals("MULTI", defaultAction.getValue(Action.NAME));
		assertNull(defaultAction.getComponent());

		JLabel label = new JLabel();
		LookAndFeelMultiAction action = new LookAndFeelMultiAction("Multi", label);
		assertEquals("Multi", action.getValue(Action.NAME));
		assertEquals(label, action.getComponent());
		assertEquals(LookAndFeels.MULTI, action.getLookAndFeel());
		assertEquals("javax.swing.plaf.multi.MultiLookAndFeel",
			action.getLookAndFeel().getLookAndFeelName());
	}

	@Test
	void actionPerformedDelegatesToCallbackMethod()
	{
		// installing the multi look and feel as the primary look and feel ends in an endless
		// recursion in MultiLookAndFeel.createUIs on the update of the component tree, so only
		// the delegation of actionPerformed to the callback method is verified here
		List<ActionEvent> received = new ArrayList<>();
		LookAndFeelMultiAction action = new LookAndFeelMultiAction("Multi", new JLabel())
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
		assertTrue(logRecords.isEmpty());
	}
}
