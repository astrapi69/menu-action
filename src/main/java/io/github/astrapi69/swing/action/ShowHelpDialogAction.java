/**
 * The MIT License
 *
 * Copyright (C) 2021 Asterios Raptis
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

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import io.github.astrapi69.swing.menu.enumeration.BaseMenuId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.java.Log;

/**
 * The class {@link ShowHelpDialogAction} shows the help window of an application
 */
@Log
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShowHelpDialogAction extends AbstractAction
{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The help window */
	Window helpWindow;
	/** The look and feels class name */
	String lookAndFeelsClassName;

	/**
	 * Instantiates a new {@link ShowHelpDialogAction}
	 *
	 */
	public ShowHelpDialogAction()
	{
		super(BaseMenuId.HELP_CONTENT.propertiesKey());
	}

	/**
	 * Instantiates a new {@link ShowHelpDialogAction}
	 *
	 * @param name
	 *            the name
	 * @param helpWindow
	 *            the help window
	 * @param lookAndFeelsClassName
	 *            the look and feels class name
	 */
	public ShowHelpDialogAction(final String name, final @NonNull Window helpWindow,
		final @NonNull String lookAndFeelsClassName)
	{
		super(name);
		this.helpWindow = helpWindow;
		this.lookAndFeelsClassName = lookAndFeelsClassName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void actionPerformed(final ActionEvent actionEvent)
	{
		onShowHelpDialog(actionEvent);
	}

	/**
	 * Callback method to interact on show the help dialog
	 *
	 * @param actionEvent
	 *            the action event
	 */
	protected void onShowHelpDialog(final ActionEvent actionEvent)
	{
		helpWindow.setLocationRelativeTo(null);
		try
		{
			UIManager.setLookAndFeel(this.lookAndFeelsClassName);
		}
		catch (final Exception ex)
		{
			log.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
		}
		SwingUtilities.updateComponentTreeUI(helpWindow);
	}
}
