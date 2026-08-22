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
package io.github.astrapi69.swing.plaf;

import java.awt.Component;
import java.awt.Window;
import java.util.function.Supplier;

import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;
import javax.swing.plaf.metal.OceanTheme;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

/**
 * The enum {@link LookAndFeels} holds the class names of the look and feels and provides methods
 * for set them. The metal themes {@link #METAL} and {@link #OCEAN} set the metal theme before the
 * metal look and feel is installed
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum LookAndFeels
{
	/** The gtk look and feel */
	GTK(LookAndFeels.LOOK_AND_FEEL_GTK),
	/** The metal look and feel with the default metal theme */
	METAL(LookAndFeels.LOOK_AND_FEEL_METAL, DefaultMetalTheme::new),
	/** The metal look and feel with the ocean theme */
	OCEAN(LookAndFeels.LOOK_AND_FEEL_METAL, OceanTheme::new),
	/** The motif look and feel */
	MOTIF(LookAndFeels.LOOK_AND_FEEL_MOTIF),
	/** The multi look and feel */
	MULTI(LookAndFeels.LOOK_AND_FEEL_MULTI),
	/** The nimbus look and feel */
	NIMBUS(LookAndFeels.LOOK_AND_FEEL_NIMBUS),
	/** The synth look and feel */
	SYNTH(LookAndFeels.LOOK_AND_FEEL_SYNTH),
	/** The system look and feel of the current platform */
	SYSTEM(UIManager.getSystemLookAndFeelClassName()),
	/** The cross platform look and feel */
	CROSSPLATFORM(UIManager.getCrossPlatformLookAndFeelClassName()),
	/** The windows classic look and feel */
	WINDOWS_CLASSIC(LookAndFeels.LOOK_AND_FEEL_WINDOWS_CLASSIC),
	/** The windows look and feel */
	WINDOWS(LookAndFeels.LOOK_AND_FEEL_WINDOWS);

	private static final String LOOK_AND_FEEL_GTK = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
	private static final String LOOK_AND_FEEL_METAL = "javax.swing.plaf.metal.MetalLookAndFeel";
	private static final String LOOK_AND_FEEL_MOTIF = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
	private static final String LOOK_AND_FEEL_MULTI = "javax.swing.plaf.multi.MultiLookAndFeel";
	private static final String LOOK_AND_FEEL_NIMBUS = "javax.swing.plaf.nimbus.NimbusLookAndFeel";
	private static final String LOOK_AND_FEEL_SYNTH = "javax.swing.plaf.synth.SynthLookAndFeel";
	private static final String LOOK_AND_FEEL_WINDOWS = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
	private static final String LOOK_AND_FEEL_WINDOWS_CLASSIC = "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel";

	/** The class name of the look and feel */
	@Getter
	String lookAndFeelName;

	/** The optional supplier of the metal theme, only set for the metal look and feels */
	Supplier<MetalTheme> metalTheme;

	LookAndFeels(final String lookAndFeelName)
	{
		this(lookAndFeelName, null);
	}

	LookAndFeels(final String lookAndFeelName, final Supplier<MetalTheme> metalTheme)
	{
		this.lookAndFeelName = lookAndFeelName;
		this.metalTheme = metalTheme;
	}

	/**
	 * Checks if this look and feel is a metal look and feel with a theme
	 *
	 * @return true if this look and feel is a metal look and feel with a theme otherwise false
	 */
	public boolean isMetalTheme()
	{
		return metalTheme != null;
	}

	/**
	 * Creates a new instance of the metal theme of this look and feel
	 *
	 * @return the new {@link MetalTheme} object or null if this look and feel is not a metal look
	 *         and feel
	 */
	public MetalTheme newMetalTheme()
	{
		return metalTheme != null ? metalTheme.get() : null;
	}

	/**
	 * Sets the given look and feel and updates the ui of the given component
	 *
	 * @param lookAndFeels
	 *            the look and feel to set
	 * @param component
	 *            the component to update
	 * @return the given look and feel
	 * @throws ClassNotFoundException
	 *             if the look and feel class could not be found
	 * @throws InstantiationException
	 *             if a new instance of the class couldn't be created
	 * @throws IllegalAccessException
	 *             if the class or initializer isn't accessible
	 * @throws UnsupportedLookAndFeelException
	 *             if the look and feel is not supported on the current platform
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
	 * Sets the given look and feel, updates the ui of the given window and packs it
	 *
	 * @param lookAndFeels
	 *            the look and feel to set
	 * @param window
	 *            the window to update
	 * @return the given look and feel
	 * @throws ClassNotFoundException
	 *             if the look and feel class could not be found
	 * @throws InstantiationException
	 *             if a new instance of the class couldn't be created
	 * @throws IllegalAccessException
	 *             if the class or initializer isn't accessible
	 * @throws UnsupportedLookAndFeelException
	 *             if the look and feel is not supported on the current platform
	 */
	public static LookAndFeels setLookAndFeel(final @NonNull LookAndFeels lookAndFeels,
		final @NonNull Window window) throws ClassNotFoundException, InstantiationException,
		IllegalAccessException, UnsupportedLookAndFeelException
	{
		setLookAndFeel(lookAndFeels);
		SwingUtilities.updateComponentTreeUI(window);
		window.pack();
		return lookAndFeels;
	}

	/**
	 * Sets the given look and feel. For the metal look and feels the metal theme is set first
	 *
	 * @param lookAndFeels
	 *            the look and feel to set
	 * @throws ClassNotFoundException
	 *             if the look and feel class could not be found
	 * @throws InstantiationException
	 *             if a new instance of the class couldn't be created
	 * @throws IllegalAccessException
	 *             if the class or initializer isn't accessible
	 * @throws UnsupportedLookAndFeelException
	 *             if the look and feel is not supported on the current platform
	 */
	public static void setLookAndFeel(final @NonNull LookAndFeels lookAndFeels)
		throws ClassNotFoundException, InstantiationException, IllegalAccessException,
		UnsupportedLookAndFeelException
	{
		if (lookAndFeels.isMetalTheme())
		{
			MetalLookAndFeel.setCurrentTheme(lookAndFeels.newMetalTheme());
			UIManager.setLookAndFeel(new MetalLookAndFeel());
			return;
		}
		UIManager.setLookAndFeel(lookAndFeels.getLookAndFeelName());
	}

	/**
	 * Gets the current look and feel
	 *
	 * @return the current {@link LookAndFeel} object
	 */
	public static LookAndFeel getCurrentLookAndFeel()
	{
		return UIManager.getLookAndFeel();
	}
}
