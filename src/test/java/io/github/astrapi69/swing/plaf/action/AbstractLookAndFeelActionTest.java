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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import io.github.astrapi69.swing.plaf.LookAndFeels;

/**
 * The abstract base class for the unit tests of the look and feel actions. It saves and restores
 * the installed look and feel with the metal theme and captures the log records of the class
 * {@link LookAndFeelAction}
 */
abstract class AbstractLookAndFeelActionTest
{

	private static final Logger logger = Logger.getLogger(LookAndFeelAction.class.getName());

	/** The captured log records of the class {@link LookAndFeelAction} */
	final List<LogRecord> logRecords = new CopyOnWriteArrayList<>();

	private LookAndFeel previousLookAndFeel;
	private MetalTheme previousMetalTheme;
	private Handler handler;
	private Level previousLevel;
	private boolean previousUseParentHandlers;

	@BeforeEach
	void setUpLookAndFeel()
	{
		previousLookAndFeel = UIManager.getLookAndFeel();
		previousMetalTheme = MetalLookAndFeel.getCurrentTheme();
		previousLevel = logger.getLevel();
		previousUseParentHandlers = logger.getUseParentHandlers();
		handler = new Handler()
		{
			@Override
			public void publish(final LogRecord logRecord)
			{
				logRecords.add(logRecord);
			}

			@Override
			public void flush()
			{
			}

			@Override
			public void close()
			{
			}
		};
		logger.setLevel(Level.ALL);
		logger.setUseParentHandlers(false);
		logger.addHandler(handler);
	}

	@AfterEach
	void restoreLookAndFeel() throws Exception
	{
		logger.removeHandler(handler);
		logger.setLevel(previousLevel);
		logger.setUseParentHandlers(previousUseParentHandlers);
		MetalLookAndFeel.setCurrentTheme(previousMetalTheme);
		if (previousLookAndFeel != null)
		{
			UIManager.setLookAndFeel(previousLookAndFeel);
		}
	}

	/**
	 * Factory method for a new synthetic {@link ActionEvent} with the given source
	 *
	 * @param source
	 *            the source of the action event
	 * @return the new {@link ActionEvent} object
	 */
	static ActionEvent newActionEvent(final Object source)
	{
		return new ActionEvent(source, ActionEvent.ACTION_PERFORMED, "changeLookAndFeel");
	}

	/**
	 * Fires the given action and verifies that the expected look and feel is installed and that
	 * nothing was logged
	 *
	 * @param action
	 *            the action to fire
	 * @param expected
	 *            the expected look and feel
	 */
	void applyAndExpect(final LookAndFeelAction action, final LookAndFeels expected)
	{
		action.actionPerformed(newActionEvent(action));
		assertEquals(expected.getLookAndFeelName(),
			UIManager.getLookAndFeel().getClass().getName());
		assertTrue(logRecords.isEmpty());
	}

	/**
	 * Fires the given action and verifies that the look and feel is either installed or, if it is
	 * not available on this platform, that the failure is logged on the level {@link Level#INFO}
	 * and that no exception is thrown
	 *
	 * @param action
	 *            the action to fire
	 */
	void applyTolerant(final LookAndFeelAction action)
	{
		final LookAndFeels lookAndFeel = action.getLookAndFeel();
		assertDoesNotThrow(() -> action.actionPerformed(newActionEvent(action)));
		if (lookAndFeel.getLookAndFeelName()
			.equals(UIManager.getLookAndFeel().getClass().getName()))
		{
			assertTrue(logRecords.isEmpty());
		}
		else
		{
			assertEquals(1, logRecords.size());
			final LogRecord logRecord = logRecords.get(0);
			assertEquals(Level.INFO, logRecord.getLevel());
			assertNotNull(logRecord.getMessage());
			assertTrue(logRecord.getMessage().contains(lookAndFeel.getLookAndFeelName()));
			assertNotNull(logRecord.getThrown());
			assertTrue(logRecord.getThrown() instanceof UnsupportedLookAndFeelException
				|| logRecord.getThrown() instanceof ClassNotFoundException);
		}
	}
}
