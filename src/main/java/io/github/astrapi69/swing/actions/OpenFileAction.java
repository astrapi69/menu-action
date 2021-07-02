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
package io.github.astrapi69.swing.actions;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Objects;

import javax.swing.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

/**
 * The class {@link OpenFileAction} can open a file with a file chooser
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class OpenFileAction extends AbstractAction
{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The parent component. */
	Component parent;

	/** The file chooser. */
	JFileChooser fileChooser;

	/**
	 * Instantiates a new {@link OpenFileAction} object.
	 *
	 * @param name
	 *            the name
	 * @param parent
	 *            the parent
	 */
	public OpenFileAction(final String name, Component parent)
	{
		super(name);
		Objects.requireNonNull(parent);
		this.parent = parent;
		this.fileChooser = newJFileChooser();
	}

	/**
	 * Factory method for create the {@link JFileChooser} object. Derived class can overwrite
	 * this method for provide they own {@link JFileChooser} object, i.e. with initial path for the
	 * {@link JFileChooser} object.
	 */
	protected JFileChooser newJFileChooser()
	{
		return new JFileChooser();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void actionPerformed(ActionEvent actionEvent)
	{
		onFileChoose(fileChooser, actionEvent);
	}

	/**
	 * Abstract callback method to interact on file choose approve option.
	 *
	 * @param file
	 *            the file
	 * @param actionEvent
	 *            the action event
	 */
	protected abstract void onApproveOption(File file, ActionEvent actionEvent);

	/**
	 * Abstract callback method to interact on cancel from file choose.
	 *
	 * @param actionEvent
	 *            the action event
	 */
	protected abstract void onCancel(ActionEvent actionEvent);

	/**
	 * Callback method to interact on file choose.
	 *
	 * @param fileChooser
	 *            the file chooser
	 * @param actionEvent
	 *            the action event
	 */
	protected void onFileChoose(JFileChooser fileChooser, ActionEvent actionEvent)
	{
		final int returnVal = fileChooser.showOpenDialog(parent);

		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			final File selectedFile = fileChooser.getSelectedFile();
			onApproveOption(selectedFile, actionEvent);
		}
		else
		{
			onCancel(actionEvent);
		}
	}

}
