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
package io.github.astrapi69.browser;

import java.awt.Component;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import lombok.NonNull;
import lombok.extern.java.Log;

/**
 * The class {@link BrowserControlExtensions} opens urls in the default browser of the operating
 * system. The {@link Desktop} api is used if supported, otherwise the platform command
 * {@code xdg-open}, {@code open} or {@code rundll32} is executed as fallback
 */
@Log
public final class BrowserControlExtensions
{

	private BrowserControlExtensions()
	{
	}

	/**
	 * Opens the given url in the default browser of the operating system
	 *
	 * @param url
	 *            the url to open
	 * @return true if the browser could be started otherwise false
	 */
	public static boolean displayURLonStandardBrowser(final @NonNull String url)
	{
		URI uri;
		try
		{
			uri = new URI(url);
		}
		catch (URISyntaxException e)
		{
			log.log(Level.WARNING, "Invalid url: " + url, e);
			return false;
		}
		return browse(uri);
	}

	/**
	 * Opens the given url in the default browser of the operating system and shows a message dialog
	 * on the given parent component if the browser could not be started
	 *
	 * @param parentComponent
	 *            the parent component for the message dialog, can be null
	 * @param url
	 *            the url to open
	 * @return true if the browser could be started otherwise false
	 */
	public static boolean displayURLonStandardBrowser(final Component parentComponent,
		final @NonNull String url)
	{
		boolean opened = displayURLonStandardBrowser(url);
		if (!opened)
		{
			JOptionPane.showMessageDialog(parentComponent,
				"Could not open the default web browser for the url\n" + url);
		}
		return opened;
	}

	/**
	 * Opens the given {@link URI} in the default browser of the operating system
	 *
	 * @param uri
	 *            the {@link URI} to open
	 * @return true if the browser could be started otherwise false
	 */
	public static boolean browse(final @NonNull URI uri)
	{
		if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE))
		{
			try
			{
				Desktop.getDesktop().browse(uri);
				return true;
			}
			catch (IOException | RuntimeException e)
			{
				log.log(Level.FINE, "Desktop.browse failed for " + uri + ", trying fallback", e);
			}
		}
		return browseWithPlatformCommand(uri);
	}

	private static boolean browseWithPlatformCommand(final URI uri)
	{
		String[] command = switch (OS.get())
		{
			case WINDOWS -> new String[] { "rundll32", "url.dll,FileProtocolHandler",
					uri.toString() };
			case MAC -> new String[] { "open", uri.toString() };
			default -> new String[] { "xdg-open", uri.toString() };
		};
		try
		{
			new ProcessBuilder(command).start();
			return true;
		}
		catch (IOException e)
		{
			log.log(Level.WARNING, "Could not open the browser with " + command[0], e);
			return false;
		}
	}
}
