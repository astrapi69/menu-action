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
package io.github.astrapi69.swing.menu.model.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import io.github.astrapi69.swing.menu.model.MenuInfo;
import io.github.astrapi69.swing.menu.model.MenuItemInfo;

/**
 * Additional unit tests for {@link MenuItemInfoConverter} written against the pitest mutation
 * report to exercise branches that {@link MenuItemInfoConverterTest} and
 * {@link MenuItemInfoConverterParameterizedTest} did not distinguish. Several survivors reported by
 * pitest for {@code setFields} and {@code toJMenuBar} (the guards before {@code setName},
 * {@code setActionCommand}, {@code setIcon}, an unconditional {@code addActionListener} and the
 * accessible name/description setters) are equivalent mutants: forcing the setter to run with a
 * {@code null} argument produces the exact same observable state as never calling it, since every
 * one of those swing setters already treats {@code null} the same as "unset" (verified manually,
 * not re-asserted here since no test could ever kill them)
 */
class MenuItemInfoConverterMutationCoverageTest
{

	@Test
	void actionCommandIsExportedOnlyWhenItDiffersFromTheText()
	{
		JMenuItem defaultsToText = new JMenuItem("Open");
		MenuItemInfo defaultInfo = MenuItemInfoConverter.fromJMenuItem(defaultsToText);
		assertNull(defaultInfo.getActionCommand());

		JMenuItem explicitlySameAsText = new JMenuItem("Open");
		explicitlySameAsText.setActionCommand("Open");
		assertNull(MenuItemInfoConverter.fromJMenuItem(explicitlySameAsText).getActionCommand());

		JMenuItem different = new JMenuItem("Open");
		different.setActionCommand("open");
		assertEquals("open", MenuItemInfoConverter.fromJMenuItem(different).getActionCommand());

		JMenuItem noTextNoCommand = new JMenuItem();
		assertNull(MenuItemInfoConverter.fromJMenuItem(noTextNoCommand).getActionCommand());

		JMenu menu = new JMenu("File");
		menu.setActionCommand("file");
		MenuInfo menuInfo = MenuItemInfoConverter.fromJMenu(menu);
		assertEquals("file", menuInfo.getActionCommand());
	}

	@Test
	void resolveIconFallsBackToTheFileSystemAndKeepsTheOriginalPathAsDescription(
		@TempDir final Path tempDir) throws IOException
	{
		Path iconFile = tempDir.resolve("custom-icon.png");
		ImageIO.write(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), "png",
			iconFile.toFile());

		Icon icon = MenuItemInfoConverter.resolveIcon(iconFile.toString());

		assertNotNull(icon);
		assertEquals(iconFile.toString(), ((ImageIcon)icon).getDescription());
	}

	@Test
	void resolveIconClasspathDescriptionKeepsTheOriginalPathNotTheResolvedResource()
	{
		Icon withSlash = MenuItemInfoConverter.resolveIcon("/icons/dot.png");
		assertEquals("/icons/dot.png", ((ImageIcon)withSlash).getDescription());

		Icon withoutSlash = MenuItemInfoConverter.resolveIcon("icons/dot.png");
		assertEquals("icons/dot.png", ((ImageIcon)withoutSlash).getDescription());
	}
}
