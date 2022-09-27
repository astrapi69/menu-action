package io.github.astrapi69.awt.system;

import io.github.astrapi69.swing.menu.MenuFactory;
import lombok.NonNull;

import java.awt.*;

/**
 * The class {@link SystemTrayFactory} provides factory methods for create system tray Menu
 */
public class SystemTrayFactory
{

	public static SystemTray newSystemTray(final @NonNull TrayIcon trayIcon,
		final @NonNull PopupMenu popup) throws AWTException
	{
		if (!SystemTray.isSupported())
		{
			throw new RuntimeException("SystemTray is not supported");
		}
		final SystemTray systemTray = SystemTray.getSystemTray();
		trayIcon.setPopupMenu(popup);
		systemTray.add(trayIcon);
		return systemTray;
	}
}
