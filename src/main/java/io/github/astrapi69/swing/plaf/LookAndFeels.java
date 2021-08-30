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
package io.github.astrapi69.swing.plaf;

import java.awt.*;

import javax.swing.*;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

/**
 * The enum class {@link LookAndFeels} provides constants with the fully qualified Names of look and
 * feel classes.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public enum LookAndFeels
{

	/** The GTK look and feel class */
	GTK(LookAndFeels.LOOK_AND_FEEL_GTK),

	/** The METAL look and feel class */
	METAL(LookAndFeels.LOOK_AND_FEEL_METAL),

	/** The OCEAN look and feel class */
	OCEAN(LookAndFeels.LOOK_AND_FEEL_METAL_OCEAN),

	/** The MOTIF look and feel class */
	MOTIF(LookAndFeels.LOOK_AND_FEEL_MOTIF),

	/** The MULTI look and feel class */
	MULTI(LookAndFeels.LOOK_AND_FEEL_MULTI),

	/** The NIMBUS look and feel class */
	NIMBUS(LookAndFeels.LOOK_AND_FEEL_NIMBUS),

	/** The SYNTH look and feel class */
	SYNTH(LookAndFeels.LOOK_AND_FEEL_SYNTH),

	/** The SYSTEM look and feel class */
	SYSTEM(UIManager.getSystemLookAndFeelClassName()),

	/** The CROSSPLATFORM look and feel class */
	CROSSPLATFORM(UIManager.getCrossPlatformLookAndFeelClassName()),

	/** The WINDOWS_CLASSIC look and feel class */
	WINDOWS_CLASSIC(LookAndFeels.LOOK_AND_FEEL_WINDOWS_CLASSIC),

	/** The WINDOWS look and feel class */
	WINDOWS(LookAndFeels.LOOK_AND_FEEL_WINDOWS);

	private static final String LOOK_AND_FEEL_GTK = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
	private static final String LOOK_AND_FEEL_METAL = "javax.swing.plaf.metal.MetalLookAndFeel";
	private static final String LOOK_AND_FEEL_METAL_OCEAN = "javax.swing.plaf.metal.OceanTheme";
	private static final String LOOK_AND_FEEL_MOTIF = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
	private static final String LOOK_AND_FEEL_MULTI = "javax.swing.plaf.multi.MultiLookAndFeel";

	private static final String LOOK_AND_FEEL_NIMBUS = "javax.swing.plaf.nimbus.NimbusLookAndFeel";
	private static final String LOOK_AND_FEEL_SYNTH = "javax.swing.plaf.synth.SynthLookAndFeel";
	private static final String LOOK_AND_FEEL_WINDOWS = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
	private static final String LOOK_AND_FEEL_WINDOWS_CLASSIC = "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel";
	/** The look and feel name. */
	@Getter
	String lookAndFeelName;

	/**
	 * Sets the given {@link LookAndFeels} to the given {@link Component} and returns given
	 * {@link LookAndFeels}
	 *
	 * @param lookAndFeels
	 *            the look and feels
	 * @param component
	 *            the component
	 * @return the look and feels
	 * @exception ClassNotFoundException
	 *                if the <code>LookAndFeel</code> class could not be found
	 * @exception InstantiationException
	 *                if a new instance of the class couldn't be created
	 * @exception IllegalAccessException
	 *                if the class or initializer isn't accessible
	 * @exception UnsupportedLookAndFeelException
	 *                if <code>lnf.isSupportedLookAndFeel()</code> is false
	 * @throws ClassCastException
	 *             if {@code className} does not identify a class that extends {@code LookAndFeel}
	 */
	public static LookAndFeels setLookAndFeel(final @NonNull LookAndFeels lookAndFeels,
		final @NonNull Component component) throws ClassNotFoundException, InstantiationException,
		IllegalAccessException, UnsupportedLookAndFeelException
	{
		setLookAndFeel(lookAndFeels);
		SwingUtilities.updateComponentTreeUI(component);
		return lookAndFeels;
	}


	/**
	 * Sets the given {@link LookAndFeels} to the given {@link Window} and returns given
	 * {@link LookAndFeels}
	 *
	 * @param lookAndFeels
	 *            the look and feels
	 * @param window
	 *            the window
	 * @return the look and feels
	 * @exception ClassNotFoundException
	 *                if the <code>LookAndFeel</code> class could not be found
	 * @exception InstantiationException
	 *                if a new instance of the class couldn't be created
	 * @exception IllegalAccessException
	 *                if the class or initializer isn't accessible
	 * @exception UnsupportedLookAndFeelException
	 *                if <code>lnf.isSupportedLookAndFeel()</code> is false
	 * @throws ClassCastException
	 *             if {@code className} does not identify a class that extends {@code LookAndFeel}
	 */
	public static LookAndFeels setLookAndFeel(final @NonNull LookAndFeels lookAndFeels,
		final @NonNull Window window) throws ClassNotFoundException, InstantiationException,
		IllegalAccessException, UnsupportedLookAndFeelException
	{
		UIManager.setLookAndFeel(lookAndFeels.getLookAndFeelName());
		SwingUtilities.updateComponentTreeUI(window);
		window.pack();
		return lookAndFeels;
	}

	/**
	 * Sets the given {@link LookAndFeels} to the {@link UIManager}
	 *
	 * @param lookAndFeels
	 *            the look and feels
	 * @exception ClassNotFoundException
	 *                if the <code>LookAndFeel</code> class could not be found
	 * @exception InstantiationException
	 *                if a new instance of the class couldn't be created
	 * @exception IllegalAccessException
	 *                if the class or initializer isn't accessible
	 * @exception UnsupportedLookAndFeelException
	 *                if <code>lnf.isSupportedLookAndFeel()</code> is false
	 * @throws ClassCastException
	 *             if {@code className} does not identify a class that extends {@code LookAndFeel}
	 */
	public static void setLookAndFeel(final @NonNull LookAndFeels lookAndFeels)
		throws ClassNotFoundException, InstantiationException, IllegalAccessException,
		UnsupportedLookAndFeelException
	{
		UIManager.setLookAndFeel(lookAndFeels.getLookAndFeelName());
	}

	/**
	 * Returns the current look and feel or <code>null</code> if not set
	 *
	 * @return the current look and feel, or <code>null</code> if not set
	 */
	public static LookAndFeel getCurrentLookAndFeel() {
		return UIManager.getLookAndFeel();
	}

}
