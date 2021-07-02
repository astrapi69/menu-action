/**
 * The MIT License
 * <p>
 * Copyright (C) 2015 Asterios Raptis
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * <p>
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

import javax.swing.*;

import lombok.Getter;

/**
 * The abstract class {@link OpenBrowserAction} for open a browser
 */
@Getter
public abstract class OpenBrowserAction extends AbstractAction
{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The component. */
	private final Component component;

	/** The url */
	private final String url;

	/**
	 * Instantiates a new {@link OpenBrowserAction} object.
	 *
	 * @param name
	 *            the name
	 * @param component
	 *            the component
	 * @param url
	 *            the url
	 */
	public OpenBrowserAction(final String name, final Component component, final String url)
	{
		super(name);
		this.component = component;
		this.url = url;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void actionPerformed(final ActionEvent e)
	{
		onDisplayURLonStandardBrowser(component, url);
	}

	/**
	 * Abstract callback method to interact on file choose approve option.
	 *
	 * @param parentComponent
	 *            The parent component. Can be null.
	 * @param url
	 *            An url like "http://www.yahoo.com/"
	 */
	protected abstract void onDisplayURLonStandardBrowser(final Component parentComponent,
		final String url);

}
