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
package io.github.astrapi69.swing.menu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.awt.event.KeyEvent;

import org.junit.jupiter.api.Test;

/**
 * The unit test class for the class {@link MenuExtensions}
 */
class MenuExtensionsTest
{

	@Test
	void parseMnemonic()
	{
		MenuExtensions.TextWithMnemonic parsed = MenuExtensions.parseMnemonic("&File");
		assertEquals("File", parsed.text());
		assertEquals(KeyEvent.VK_F, parsed.mnemonic());

		parsed = MenuExtensions.parseMnemonic("Save &As...");
		assertEquals("Save As...", parsed.text());
		assertEquals(KeyEvent.VK_A, parsed.mnemonic());

		parsed = MenuExtensions.parseMnemonic("Tom && &Jerry");
		assertEquals("Tom & Jerry", parsed.text());
		assertEquals(KeyEvent.VK_J, parsed.mnemonic());

		parsed = MenuExtensions.parseMnemonic("No marker");
		assertEquals("No marker", parsed.text());
		assertNull(parsed.mnemonic());

		parsed = MenuExtensions.parseMnemonic("trailing &");
		assertEquals("trailing &", parsed.text());
		assertNull(parsed.mnemonic());

		parsed = MenuExtensions.parseMnemonic("& space");
		assertEquals("& space", parsed.text());
		assertNull(parsed.mnemonic());

		parsed = MenuExtensions.parseMnemonic(null);
		assertNull(parsed.text());
		assertNull(parsed.mnemonic());
	}

	@Test
	void toMnemonic()
	{
		assertEquals(KeyEvent.VK_F, MenuExtensions.toMnemonic('f'));
		assertEquals(KeyEvent.VK_F, MenuExtensions.toMnemonic('F'));
		assertEquals((int)'1', MenuExtensions.toMnemonic('1'));
	}
}
