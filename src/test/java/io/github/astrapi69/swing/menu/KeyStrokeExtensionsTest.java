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
package io.github.astrapi69.swing.menu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.junit.jupiter.api.Test;

import io.github.astrapi69.swing.menu.model.KeyStrokeInfo;

/**
 * The unit test class for the class {@link KeyStrokeExtensions}
 */
class KeyStrokeExtensionsTest
{

	/**
	 * A {@link JPanel} that exposes the protected key binding processing for firing synthetic key
	 * events without a display
	 */
	private static class KeyBindingPanel extends JPanel
	{
		private static final long serialVersionUID = 1L;

		boolean fire(final KeyStroke keyStroke, final int condition)
		{
			KeyEvent keyEvent = new KeyEvent(this, KeyEvent.KEY_PRESSED, 0L,
				keyStroke.getModifiers(), keyStroke.getKeyCode(), KeyEvent.CHAR_UNDEFINED);
			return processKeyBinding(keyStroke, keyEvent, condition, true);
		}
	}

	private static Action recording(final List<Object> firedSources)
	{
		return new AbstractAction()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(final java.awt.event.ActionEvent e)
			{
				firedSources.add(e.getSource());
			}
		};
	}

	@Test
	void addShortcutToComponentWithDefaultCondition()
	{
		List<Object> firedSources = new ArrayList<>();
		KeyBindingPanel panel = new KeyBindingPanel();
		KeyStroke ctrlS = KeyStroke.getKeyStroke("ctrl S");
		Action action = recording(firedSources);

		KeyStrokeExtensions.addShortcutToComponent(panel, ctrlS, "save", action);

		// the keystroke is bound in the input map for WHEN_IN_FOCUSED_WINDOW
		assertEquals("save", panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).get(ctrlS));
		assertNull(panel.getInputMap(JComponent.WHEN_FOCUSED).get(ctrlS));
		// the action command is bound to the action in the action map
		assertSame(action, panel.getActionMap().get("save"));
		assertSame(action, panel.getActionForKeyStroke(ctrlS));
		assertEquals(JComponent.WHEN_IN_FOCUSED_WINDOW, panel.getConditionForKeyStroke(ctrlS));
		// a synthetic key event fires the action
		assertTrue(panel.fire(ctrlS, JComponent.WHEN_IN_FOCUSED_WINDOW));
		assertEquals(List.of(panel), firedSources);
	}

	@Test
	void addShortcutToComponentWithGivenCondition()
	{
		List<Object> firedSources = new ArrayList<>();
		KeyBindingPanel panel = new KeyBindingPanel();
		KeyStroke ctrlO = KeyStroke.getKeyStroke("ctrl O");
		Action action = recording(firedSources);

		KeyStrokeExtensions.addShortcutToComponent(panel, ctrlO, JComponent.WHEN_FOCUSED, "open",
			action);

		assertEquals("open", panel.getInputMap(JComponent.WHEN_FOCUSED).get(ctrlO));
		assertNull(panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).get(ctrlO));
		assertSame(action, panel.getActionMap().get("open"));
		assertEquals(JComponent.WHEN_FOCUSED, panel.getConditionForKeyStroke(ctrlO));
		// the shortcut is not registered for another condition
		assertFalse(panel.fire(ctrlO, JComponent.WHEN_IN_FOCUSED_WINDOW));
		assertTrue(firedSources.isEmpty());
		assertTrue(panel.fire(ctrlO, JComponent.WHEN_FOCUSED));
		assertEquals(List.of(panel), firedSources);
	}

	@Test
	void toKeyStrokeInfo()
	{
		KeyStroke ctrlS = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK);

		KeyStrokeInfo info = KeyStrokeExtensions.toKeyStrokeInfo(ctrlS);

		assertEquals(KeyEvent.VK_S, info.getKeyCode());
		assertEquals(ctrlS.getModifiers(), info.getModifiers());
		assertFalse(info.getOnKeyRelease());
		assertEquals("ctrl pressed S", info.getKeystrokeAsString());
		// the info restores the same keystroke
		assertEquals(ctrlS, info.toKeyStroke());

		KeyStroke released = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK, true);
		KeyStrokeInfo releasedInfo = KeyStrokeExtensions.toKeyStrokeInfo(released);
		assertTrue(releasedInfo.getOnKeyRelease());
		assertEquals("ctrl released S", releasedInfo.getKeystrokeAsString());
		assertEquals(released, releasedInfo.toKeyStroke());

		assertThrows(NullPointerException.class, () -> KeyStrokeExtensions.toKeyStrokeInfo(null));
	}

	@Test
	void getKeyStrokeWithInvalidString()
	{
		assertNull(KeyStrokeExtensions.getKeyStroke("not a keystroke"));
		assertNull(KeyStrokeExtensions.getKeyStroke(null));
	}

	@Test
	void getKeyStroke()
	{
		String actual;
		String expected;
		KeyStroke keyStroke;
		KeyStroke otherKeyStroke;
		// new scenario ...
		expected = "ctrl pressed Z";
		keyStroke = KeyStrokeExtensions.getKeyStroke(expected);
		actual = keyStroke.toString();
		assertEquals(expected, actual);
		// new scenario ...
		expected = "ctrl pressed D";
		keyStroke = KeyStrokeExtensions.getKeyStroke(expected);
		actual = keyStroke.toString();
		assertEquals(expected, actual);
		// new scenario ...
		expected = "ctrl pressed S";
		keyStroke = KeyStrokeExtensions.getKeyStroke(expected);
		actual = keyStroke.toString();
		assertEquals(expected, actual);
		// new scenario ...
		expected = "ctrl pressed K";
		keyStroke = KeyStrokeExtensions.getKeyStroke(expected);
		actual = keyStroke.toString();
		assertEquals(expected, actual);
		// new scenario ...
		expected = "ctrl pressed P";
		keyStroke = KeyStrokeExtensions.getKeyStroke(expected);
		actual = keyStroke.toString();
		assertEquals(expected, actual);
		// new scenario ...
		expected = "ctrl pressed Y";
		keyStroke = KeyStrokeExtensions.getKeyStroke(expected);
		actual = keyStroke.toString();
		assertEquals(expected, actual);
		// new scenario ...
		expected = "ctrl pressed N";
		keyStroke = KeyStrokeExtensions.getKeyStroke(expected);
		actual = keyStroke.toString();
		assertEquals(expected, actual);
		// new scenario ...
		expected = "ctrl pressed C";
		keyStroke = KeyStrokeExtensions.getKeyStroke(expected);
		actual = keyStroke.toString();
		assertEquals(expected, actual);
		// new scenario ...
		expected = "ctrl pressed I";
		keyStroke = KeyStrokeExtensions.getKeyStroke(expected);
		actual = keyStroke.toString();
		assertEquals(expected, actual);
		// new scenario ...
		expected = "alt pressed F11";
		keyStroke = KeyStrokeExtensions.getKeyStroke(expected);
		actual = keyStroke.toString();
		assertEquals(expected, actual);
		// new scenario ...
		expected = "alt pressed F4";
		keyStroke = KeyStrokeExtensions.getKeyStroke(expected);
		actual = keyStroke.toString();
		assertEquals(expected, actual);
		otherKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK);
		assertEquals(keyStroke, otherKeyStroke);
		// new scenario ...
		expected = "alt pressed L";
		keyStroke = KeyStrokeExtensions.getKeyStroke(expected);
		actual = keyStroke.toString();
		assertEquals(expected, actual);
		// new scenario ...
		expected = "ctrl alt pressed D";
		keyStroke = KeyStrokeExtensions.getKeyStroke(expected);
		actual = keyStroke.toString();
		assertEquals(expected, actual);
		otherKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_D,
			InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK);
		assertEquals(keyStroke, otherKeyStroke);
	}
}
