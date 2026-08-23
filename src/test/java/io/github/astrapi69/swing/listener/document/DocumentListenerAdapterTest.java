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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.junit.jupiter.api.Test;

/**
 * The unit test class for the class {@link DocumentListenerAdapter}
 */
class DocumentListenerAdapterTest
{

	/**
	 * A concrete {@link DocumentListenerAdapter} that records every event it receives
	 */
	private static class RecordingAdapter extends DocumentListenerAdapter
	{
		final List<DocumentEvent> events = new ArrayList<>();

		@Override
		public void onDocumentChanged(final DocumentEvent e)
		{
			events.add(e);
		}
	}

	@Test
	void insertAndRemoveDelegateToOnDocumentChanged() throws Exception
	{
		JTextField textField = new JTextField();
		Document document = textField.getDocument();
		RecordingAdapter adapter = new RecordingAdapter();
		document.addDocumentListener(adapter);

		document.insertString(0, "foo", null);

		assertEquals(1, adapter.events.size());
		DocumentEvent insert = adapter.events.get(0);
		assertEquals(DocumentEvent.EventType.INSERT, insert.getType());
		assertEquals(0, insert.getOffset());
		assertEquals(3, insert.getLength());
		assertSame(document, insert.getDocument());

		document.remove(1, 2);

		assertEquals(2, adapter.events.size());
		DocumentEvent remove = adapter.events.get(1);
		assertEquals(DocumentEvent.EventType.REMOVE, remove.getType());
		assertEquals(1, remove.getOffset());
		assertEquals(2, remove.getLength());
		assertEquals("f", textField.getText());
	}

	@Test
	void attributeChangeDelegatesToOnDocumentChanged() throws Exception
	{
		DefaultStyledDocument document = new DefaultStyledDocument();
		document.insertString(0, "styled", null);
		RecordingAdapter adapter = new RecordingAdapter();
		document.addDocumentListener(adapter);

		SimpleAttributeSet bold = new SimpleAttributeSet();
		StyleConstants.setBold(bold, true);
		document.setCharacterAttributes(0, 6, bold, false);

		assertEquals(1, adapter.events.size());
		assertEquals(DocumentEvent.EventType.CHANGE, adapter.events.get(0).getType());
	}

	@Test
	void directCallsPassTheSameEventInstance()
	{
		RecordingAdapter adapter = new RecordingAdapter();
		DocumentEvent event = new DocumentEvent()
		{
			@Override
			public int getOffset()
			{
				return 0;
			}

			@Override
			public int getLength()
			{
				return 0;
			}

			@Override
			public Document getDocument()
			{
				return null;
			}

			@Override
			public EventType getType()
			{
				return EventType.CHANGE;
			}

			@Override
			public ElementChange getChange(final Element elem)
			{
				return null;
			}
		};

		adapter.changedUpdate(event);
		adapter.insertUpdate(event);
		adapter.removeUpdate(event);

		assertEquals(3, adapter.events.size());
		assertTrue(adapter.events.stream().allMatch(e -> e == event));
	}
}
