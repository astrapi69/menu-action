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
package io.github.astrapi69.swing.menu.build;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.junit.jupiter.api.Test;

/**
 * The unit test class for the class {@link ActionContext}
 */
class ActionContextTest
{

	@Test
	void putAndGet()
	{
		JPanel panel = new JPanel();
		JLabel label = new JLabel("l");
		ActionContext context = ActionContext.empty().put(panel).put("label", label);
		assertEquals(2, context.size());
		assertEquals(panel, context.get(JPanel.class.getName()).orElseThrow());
		assertEquals(label, context.get("label", JLabel.class).orElseThrow());
		assertFalse(context.get("label", JPanel.class).isPresent());
		assertFalse(context.get((String)null).isPresent());
		assertEquals(panel, context.get(JPanel.class).orElseThrow());
		assertEquals(panel, context.require(java.awt.Container.class));
		assertTrue(context.get(javax.swing.JComponent.class).isPresent());
		assertThrows(IllegalStateException.class, () -> context.require(javax.swing.JTree.class));
	}
}
