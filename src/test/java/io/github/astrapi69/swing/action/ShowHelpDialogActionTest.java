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
package io.github.astrapi69.swing.action;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import io.github.astrapi69.swing.menu.enumeration.BaseMenuId;

/**
 * The unit test class for the class {@link ShowHelpDialogAction}
 */
class ShowHelpDialogActionTest
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

	private static ActionEvent newEvent()
	{
		return new ActionEvent(new JButton(), ActionEvent.ACTION_PERFORMED, "help");
	}

	@Test
	void defaultConstructorUsesHelpContentMenuId()
	{
		ShowHelpDialogAction action = new ShowHelpDialogAction();

		assertEquals(BaseMenuId.HELP_CONTENT.propertiesKey(), action.getValue(Action.NAME));
		assertEquals("global.menu.help.content", action.getValue(Action.NAME));
		assertNull(action.getHelpWindow());
		assertNull(action.getLookAndFeelsClassName());
	}

	@Test
	void constructorRequiresHelpWindow()
	{
		assertThrows(NullPointerException.class, () -> new ShowHelpDialogAction("Help", null,
			UIManager.getCrossPlatformLookAndFeelClassName()));
	}

	@Test
	void actionPerformedWithoutHelpWindowThrows()
	{
		ShowHelpDialogAction action = new ShowHelpDialogAction();

		assertThrows(NullPointerException.class, () -> action.actionPerformed(newEvent()));
	}

	@Test
	void actionPerformedSetsLookAndFeelAndUpdatesHelpWindow() throws Exception
	{
		assumeFalse(GraphicsEnvironment.isHeadless());
		previous = UIManager.getLookAndFeel();
		Frame helpWindow = new Frame("help");
		try
		{
			String metal = UIManager.getCrossPlatformLookAndFeelClassName();
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
			ShowHelpDialogAction action = new ShowHelpDialogAction("Help", helpWindow, metal);
			assertEquals("Help", action.getValue(Action.NAME));
			assertSame(helpWindow, action.getHelpWindow());
			assertEquals(metal, action.getLookAndFeelsClassName());

			action.actionPerformed(newEvent());

			assertEquals(metal, UIManager.getLookAndFeel().getClass().getName());

			assertThrows(NullPointerException.class,
				() -> new ShowHelpDialogAction("Help", helpWindow, null));
		}
		finally
		{
			helpWindow.dispose();
		}
	}

	@Test
	void actionPerformedWithInvalidLookAndFeelClassNameOnlyLogs()
	{
		assumeFalse(GraphicsEnvironment.isHeadless());
		previous = UIManager.getLookAndFeel();
		Frame helpWindow = new Frame("help");
		try
		{
			ShowHelpDialogAction action = new ShowHelpDialogAction("Help", helpWindow,
				"does.not.Exist");

			assertDoesNotThrow(() -> action.actionPerformed(newEvent()));
			assertSame(previous, UIManager.getLookAndFeel());
		}
		finally
		{
			helpWindow.dispose();
		}
	}
}
