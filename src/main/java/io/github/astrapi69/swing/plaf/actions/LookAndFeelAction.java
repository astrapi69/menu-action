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
package io.github.astrapi69.swing.plaf.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.UnsupportedLookAndFeelException;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.java.Log;
import io.github.astrapi69.swing.plaf.LookAndFeels;

/**
 * The base class {@link LookAndFeelAction} for change a look and feel from the application
 */
@Log
@Getter(AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LookAndFeelAction extends AbstractAction
{

	private static final long serialVersionUID = 1L;
	/** The parent component. */
	Component component;
	/** The look and feels enum value */
	LookAndFeels lookAndFeel;

	/**
	 * Instantiates a new {@link LookAndFeelAction} object
	 *
	 * @param name
	 *            the name
	 * @param component
	 *            the component
	 * @param lookAndFeel
	 *            the look and feels enum value
	 */
	public LookAndFeelAction(final String name, final Component component,
		final LookAndFeels lookAndFeel)
	{
		super(name);
		this.component = component;
		this.lookAndFeel = lookAndFeel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void actionPerformed(final ActionEvent event)
	{
		onChangeOfLookAndFeel(event);
	}

	/**
	 * Callback method to interact on change of look and feel
	 *
	 * @param event
	 *            the action event
	 */
	protected void onChangeOfLookAndFeel(final ActionEvent event)
	{
		try
		{
			LookAndFeels.setLookAndFeel(this.lookAndFeel, this.component);
		}
		catch (UnsupportedLookAndFeelException e)
		{
			log.log(Level.INFO,
				"Look and Feel not supported " + this.lookAndFeel.getLookAndFeelName(), e);
		}
		catch (ClassNotFoundException e)
		{
			log.log(Level.INFO,
				"Look and Feel class not found " + this.lookAndFeel.getLookAndFeelName(), e);
		}
		catch (InstantiationException e)
		{
			log.log(Level.INFO,
				"Look and Feel could not instantiated " + this.lookAndFeel.getLookAndFeelName(), e);
		}
		catch (IllegalAccessException e)
		{
			log.log(Level.INFO, "Illegal access exception is thrown for the Look and Feel "
				+ this.lookAndFeel.getLookAndFeelName(), e);
		}
	}

}
