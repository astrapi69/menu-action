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
package io.github.astrapi69.swing.listener.document;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.junit.jupiter.api.Test;

/**
 * The unit test class for the class {@link EnableButtonBehavior}
 */
class EnableButtonBehaviorTest
{

	private static boolean isRegistered(final AbstractDocument document,
		final EnableButtonBehavior behavior)
	{
		return Arrays.asList(document.getDocumentListeners()).contains(behavior);
	}

	@Test
	void constructorWithEnabledFalseDisablesButtonOnEmptyDocument()
	{
		JButton button = new JButton("ok");
		ButtonModel buttonModel = button.getModel();
		JTextField textField = new JTextField();
		AbstractDocument document = (AbstractDocument)textField.getDocument();
		assertTrue(button.isEnabled());

		EnableButtonBehavior behavior = new EnableButtonBehavior(buttonModel, document, false);

		assertFalse(buttonModel.isEnabled());
		assertFalse(button.isEnabled());
		assertFalse(behavior.isEnabled());
		assertSame(buttonModel, behavior.getButtonModel());
		assertSame(document, behavior.getDocument());
		assertTrue(isRegistered(document, behavior));
	}

	@Test
	void constructorWithEnabledFalseKeepsButtonEnabledOnFilledDocument()
	{
		JButton button = new JButton("ok");
		JTextField textField = new JTextField("preset");
		AbstractDocument document = (AbstractDocument)textField.getDocument();

		EnableButtonBehavior behavior = new EnableButtonBehavior(button.getModel(), document,
			false);

		// onChange was called and re-evaluated the state from the document length
		assertTrue(button.getModel().isEnabled());
		assertTrue(behavior.isEnabled());
	}

	@Test
	void constructorWithEnabledTrueDoesNotTouchButton()
	{
		JButton button = new JButton("ok");
		JTextField textField = new JTextField();
		AbstractDocument document = (AbstractDocument)textField.getDocument();

		EnableButtonBehavior behavior = new EnableButtonBehavior(button.getModel(), document, true);

		// document is empty but onChange is skipped, so the button stays enabled
		assertTrue(button.getModel().isEnabled());
		assertTrue(behavior.isEnabled());
		assertTrue(isRegistered(document, behavior));
	}

	@Test
	void insertAndRemoveToggleButtonState() throws Exception
	{
		JButton button = new JButton("ok");
		JTextField textField = new JTextField();
		AbstractDocument document = (AbstractDocument)textField.getDocument();
		EnableButtonBehavior behavior = new EnableButtonBehavior(button.getModel(), document,
			false);
		assertFalse(button.isEnabled());

		document.insertString(0, "abc", null);
		assertTrue(button.isEnabled());
		assertTrue(behavior.isEnabled());

		document.remove(0, 2);
		assertEquals("c", textField.getText());
		assertTrue(button.isEnabled());
		assertTrue(behavior.isEnabled());

		document.remove(0, 1);
		assertEquals("", textField.getText());
		assertFalse(button.isEnabled());
		assertFalse(behavior.isEnabled());

		textField.setText("again");
		assertTrue(button.isEnabled());

		textField.setText("");
		assertFalse(button.isEnabled());
	}

	@Test
	void changedUpdateReevaluatesState() throws Exception
	{
		JButton button = new JButton("ok");
		DefaultStyledDocument document = new DefaultStyledDocument();
		document.insertString(0, "styled", null);
		EnableButtonBehavior behavior = new EnableButtonBehavior(button.getModel(), document, true);
		// force an inconsistent state that only onChange() will correct
		button.getModel().setEnabled(false);
		behavior.setEnabled(false);

		SimpleAttributeSet bold = new SimpleAttributeSet();
		StyleConstants.setBold(bold, true);
		document.setCharacterAttributes(0, 6, bold, false);

		assertTrue(button.getModel().isEnabled());
		assertTrue(behavior.isEnabled());
	}

	@Test
	void listenerMethodsIgnoreTheEventAndCallOnChange()
	{
		JButton button = new JButton("ok");
		JTextField textField = new JTextField();
		int[] calls = { 0 };
		EnableButtonBehavior behavior = new EnableButtonBehavior(button.getModel(),
			textField.getDocument(), true)
		{
			@Override
			protected void onChange()
			{
				calls[0]++;
				super.onChange();
			}
		};
		assertEquals(0, calls[0]);

		behavior.insertUpdate(null);
		assertEquals(1, calls[0]);
		behavior.removeUpdate(null);
		assertEquals(2, calls[0]);
		behavior.changedUpdate(null);
		assertEquals(3, calls[0]);
		// the document is empty, so every call disables the button
		assertFalse(button.isEnabled());
		assertFalse(behavior.isEnabled());
	}

	@Test
	void builderRegistersListenerAndAppliesInitialState()
	{
		JButton button = new JButton("ok");
		JTextField textField = new JTextField();
		AbstractDocument document = (AbstractDocument)textField.getDocument();

		EnableButtonBehavior behavior = EnableButtonBehavior.builder()
			.buttonModel(button.getModel()).document(document).enabled(false).build();

		assertFalse(button.isEnabled());
		assertFalse(behavior.isEnabled());
		assertTrue(isRegistered(document, behavior));
		assertSame(button.getModel(), behavior.getButtonModel());
		assertSame(document, behavior.getDocument());
	}

	@Test
	void toBuilderCopiesState()
	{
		JButton button = new JButton("ok");
		JTextField textField = new JTextField("text");
		AbstractDocument document = (AbstractDocument)textField.getDocument();
		EnableButtonBehavior behavior = new EnableButtonBehavior(button.getModel(), document, true);

		EnableButtonBehavior copy = behavior.toBuilder().build();

		assertNotSame(behavior, copy);
		assertEquals(behavior, copy);
		assertEquals(behavior.hashCode(), copy.hashCode());
		assertSame(document, copy.getDocument());
		assertSame(button.getModel(), copy.getButtonModel());
		assertTrue(copy.isEnabled());
		assertTrue(isRegistered(document, copy));

		EnableButtonBehavior changed = behavior.toBuilder().enabled(false).build();
		// enabled=false triggers onChange which finds a non empty document
		assertTrue(changed.isEnabled());
		assertTrue(button.isEnabled());
	}

	@Test
	void equalsHashCodeAndToString()
	{
		JButton button = new JButton("ok");
		JTextField textField = new JTextField();
		EnableButtonBehavior first = new EnableButtonBehavior(button.getModel(),
			textField.getDocument(), true);
		EnableButtonBehavior second = new EnableButtonBehavior(button.getModel(),
			textField.getDocument(), true);
		EnableButtonBehavior other = new EnableButtonBehavior(new JButton().getModel(),
			new JTextField().getDocument(), true);

		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, other);
		assertNotEquals(first, null);

		second.setEnabled(false);
		assertNotEquals(first, second);
		assertFalse(second.isEnabled());

		String string = first.toString();
		assertTrue(string.startsWith("EnableButtonBehavior("));
		assertTrue(string.contains("enabled=true"));
	}
}
