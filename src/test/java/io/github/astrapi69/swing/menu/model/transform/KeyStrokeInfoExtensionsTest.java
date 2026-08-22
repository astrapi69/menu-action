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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.junit.jupiter.api.Test;

import io.github.astrapi69.swing.menu.model.KeyStrokeInfo;

/**
 * The unit test class for the class {@link KeyStrokeInfoExtensions}
 */
class KeyStrokeInfoExtensionsTest
{

	@Test
	void set()
	{
		KeyStrokeInfo keyStrokeInfo = new KeyStrokeInfo();
		KeyStroke keyStroke = KeyStroke.getKeyStroke("ctrl A");
		KeyStrokeInfoExtensions.set(keyStrokeInfo, keyStroke);
		assertEquals(keyStroke.getKeyCode(), keyStrokeInfo.getKeyCode());
		assertEquals(keyStroke.getModifiers(), keyStrokeInfo.getModifiers());
		assertEquals(keyStroke.isOnKeyRelease(), keyStrokeInfo.getOnKeyRelease());
		assertEquals(keyStroke.toString(), keyStrokeInfo.getKeystrokeAsString());
	}

	@Test
	void toKeyStroke()
	{
		KeyStroke expected = KeyStroke.getKeyStroke("ctrl A");
		KeyStrokeInfo keyStrokeInfo = new KeyStrokeInfo();
		assertNull(KeyStrokeInfoExtensions.toKeyStroke(keyStrokeInfo));

		keyStrokeInfo.setKeyCode(expected.getKeyCode());
		assertEquals(KeyStroke.getKeyStroke(expected.getKeyCode(), 0),
			KeyStrokeInfoExtensions.toKeyStroke(keyStrokeInfo));

		keyStrokeInfo.setModifiers(expected.getModifiers());
		assertEquals(expected, KeyStrokeInfoExtensions.toKeyStroke(keyStrokeInfo));

		keyStrokeInfo.setOnKeyRelease(true);
		assertTrue(KeyStrokeInfoExtensions.toKeyStroke(keyStrokeInfo).isOnKeyRelease());

		// the keystroke string has priority
		keyStrokeInfo.setKeystrokeAsString("alt F4");
		assertEquals(KeyStroke.getKeyStroke("alt F4"),
			KeyStrokeInfoExtensions.toKeyStroke(keyStrokeInfo));

		// an invalid keystroke string falls back to the key code
		keyStrokeInfo.setKeystrokeAsString("not a keystroke");
		assertNotNull(KeyStrokeInfoExtensions.toKeyStroke(keyStrokeInfo));
	}

	@Test
	void getKeyStrokeInfos()
	{
		JComponent component = new JPanel();
		assertTrue(KeyStrokeInfoExtensions.getKeyStrokeInfos(component).isEmpty());
		assertNull(KeyStrokeInfoExtensions.getKeyStrokeInfo(component));

		KeyStroke ctrlS = KeyStroke.getKeyStroke("ctrl S");
		component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ctrlS, "save");
		component.getActionMap().put("save", new AbstractAction()
		{
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e)
			{
			}
		});
		List<KeyStrokeInfo> keyStrokeInfos = KeyStrokeInfoExtensions.getKeyStrokeInfos(component);
		assertEquals(1, keyStrokeInfos.size());
		assertEquals(ctrlS, keyStrokeInfos.get(0).toKeyStroke());

		JMenuItem menuItem = new JMenuItem("Open");
		menuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
		assertEquals(KeyStroke.getKeyStroke("ctrl O"),
			KeyStrokeInfoExtensions.getKeyStrokeInfo(menuItem).toKeyStroke());
	}
}
