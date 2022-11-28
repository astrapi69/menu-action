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
package io.github.astrapi69.awt.system;

import io.github.astrapi69.swing.menu.MenuFactory;
import lombok.NonNull;

import java.awt.*;

/**
 * The class {@link SystemTrayFactory} provides factory methods for create system tray Menu
 */
public class SystemTrayFactory
{

	/**
	 * Factory method for create a <code>SystemTray</code> from the given {@link TrayIcon} object
	 * and the given {@link PopupMenu} object
	 *
	 * @param trayIcon
	 *            the tray icon of the <code>SystemTray</code>
	 * @param popupMenu
	 *            the tray popup menu of the <code>SystemTray</code>
	 * @return the new {@link SystemTray} object
	 * @throws AWTException
	 *             is thrown if the desktop system tray is missing
	 */
	public static SystemTray newSystemTray(final @NonNull TrayIcon trayIcon,
										   final @NonNull PopupMenu popupMenu) throws AWTException
	{
		if (!SystemTray.isSupported())
		{
			throw new RuntimeException("SystemTray is not supported");
		}
		final SystemTray systemTray = SystemTray.getSystemTray();
		trayIcon.setPopupMenu(popupMenu);
		systemTray.add(trayIcon);
		return systemTray;
	}

}
