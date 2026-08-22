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

import java.awt.Component;

import io.github.astrapi69.swing.plaf.LookAndFeels;

/**
 * The class {@link LookAndFeelMetalAction} sets the metal look and feel. If the name of the action
 * contains 'ocean' the ocean theme is used, otherwise the default metal theme
 */
public class LookAndFeelMetalAction extends LookAndFeelAction
{

	private static final String OCEAN_THEME_NAME = "ocean";

	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new {@link LookAndFeelMetalAction} object with the default metal theme
	 */
	public LookAndFeelMetalAction()
	{
		super(LookAndFeels.METAL);
	}

	/**
	 * Instantiates a new {@link LookAndFeelMetalAction} object
	 *
	 * @param name
	 *            the name of the action. If the name contains 'ocean' the ocean theme is used
	 * @param component
	 *            the component to update
	 */
	public LookAndFeelMetalAction(final String name, final Component component)
	{
		super(name, component, toMetalLookAndFeel(name));
	}

	/**
	 * Instantiates a new {@link LookAndFeelMetalAction} object with the given metal look and feel
	 *
	 * @param name
	 *            the name of the action
	 * @param component
	 *            the component to update
	 * @param lookAndFeel
	 *            the metal look and feel, {@link LookAndFeels#METAL} or {@link LookAndFeels#OCEAN}
	 */
	public LookAndFeelMetalAction(final String name, final Component component,
		final LookAndFeels lookAndFeel)
	{
		super(name, component, lookAndFeel);
	}

	private static LookAndFeels toMetalLookAndFeel(final String name)
	{
		return name != null && name.toLowerCase().contains(OCEAN_THEME_NAME)
			? LookAndFeels.OCEAN
			: LookAndFeels.METAL;
	}
}
