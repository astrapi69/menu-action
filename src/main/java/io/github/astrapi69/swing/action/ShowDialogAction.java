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

import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.*;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

/**
 * The abstract class {@link ShowDialogAction} can show a dialog
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class ShowDialogAction extends AbstractAction
{

	private static final long serialVersionUID = 1L;

	/** The owner Frame object */
	Frame owner;
	/** The title of the dialog */
	String title;

	/**
	 * Instantiates a new {@link ShowDialogAction} object
	 *
	 * @param name
	 *            the name
	 * @param owner
	 *            the owner
	 * @param title
	 *            the title
	 */
	public ShowDialogAction(final String name, final @NonNull Frame owner,
		final @NonNull String title)
	{
		super(name);
		this.owner = owner;
		this.title = title;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void actionPerformed(final ActionEvent actionEvent)
	{
		onShowDialog(actionEvent);
	}

	/**
	 * Callback method to interact on show the dialog
	 *
	 * @param actionEvent
	 *            the action event
	 */
	protected void onShowDialog(final ActionEvent actionEvent)
	{
		final JDialog info = newJDialog(owner, title);
		info.setVisible(true);
	}

	/**
	 * Abstract callback factory method to interact on creation of the dialog
	 *
	 * @param owner
	 *            the owner
	 * @param title
	 *            the title
	 */
	protected abstract JDialog newJDialog(final Frame owner, final String title);

}
