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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.junit.jupiter.api.Test;

import io.github.astrapi69.model.BaseModel;
import io.github.astrapi69.model.api.IModel;

/**
 * The unit test class for the class {@link StringBindingListener}
 */
class StringBindingListenerTest
{

	private static DocumentEvent newEvent(final Document document)
	{
		return new DocumentEvent()
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
				return document;
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
	}

	@Test
	void constructorRejectsNullModel()
	{
		assertThrows(NullPointerException.class, () -> new StringBindingListener(null));
	}

	@Test
	void getModelReturnsBoundModel()
	{
		IModel<String> model = BaseModel.of("initial");
		StringBindingListener listener = new StringBindingListener(model);
		assertSame(model, listener.getModel());
		assertEquals("initial", listener.getModel().getObject());
	}

	@Test
	void insertAndRemoveUpdateModel() throws Exception
	{
		IModel<String> model = BaseModel.of("");
		JTextField textField = new JTextField();
		Document document = textField.getDocument();
		document.addDocumentListener(new StringBindingListener(model));

		document.insertString(0, "abc", null);
		assertEquals("abc", model.getObject());

		document.insertString(3, "def", null);
		assertEquals("abcdef", model.getObject());

		document.remove(1, 2);
		assertEquals("adef", model.getObject());
		assertEquals("adef", textField.getText());

		document.remove(0, document.getLength());
		assertEquals("", model.getObject());

		textField.setText("via setText");
		assertEquals("via setText", model.getObject());
	}

	@Test
	void attributeChangeUpdatesModel() throws Exception
	{
		IModel<String> model = BaseModel.of("stale");
		DefaultStyledDocument document = new DefaultStyledDocument();
		document.insertString(0, "styled", null);
		document.addDocumentListener(new StringBindingListener(model));
		assertEquals("stale", model.getObject());

		SimpleAttributeSet bold = new SimpleAttributeSet();
		StyleConstants.setBold(bold, true);
		document.setCharacterAttributes(0, 6, bold, false);

		assertEquals("styled", model.getObject());
	}

	@Test
	void directListenerCallsReadWholeDocumentText() throws Exception
	{
		IModel<String> model = BaseModel.of();
		PlainDocument document = new PlainDocument();
		document.insertString(0, "direct", null);
		StringBindingListener listener = new StringBindingListener(model);
		DocumentEvent event = newEvent(document);

		listener.insertUpdate(event);
		assertEquals("direct", model.getObject());

		model.setObject(null);
		listener.removeUpdate(event);
		assertEquals("direct", model.getObject());

		model.setObject(null);
		listener.changedUpdate(event);
		assertEquals("direct", model.getObject());
	}

	@Test
	void badLocationIsLoggedAndModelIsUntouched()
	{
		IModel<String> model = BaseModel.of("untouched");
		PlainDocument document = new PlainDocument()
		{
			@Override
			public String getText(final int offset, final int length) throws BadLocationException
			{
				throw new BadLocationException("forced", offset);
			}
		};
		Logger logger = Logger.getLogger(StringBindingListener.class.getName());
		List<LogRecord> records = new ArrayList<>();
		Handler handler = new Handler()
		{
			@Override
			public void publish(final LogRecord record)
			{
				records.add(record);
			}

			@Override
			public void flush()
			{
			}

			@Override
			public void close()
			{
			}
		};
		logger.addHandler(handler);
		try
		{
			new StringBindingListener(model).insertUpdate(newEvent(document));
		}
		finally
		{
			logger.removeHandler(handler);
		}

		assertEquals("untouched", model.getObject());
		assertEquals(1, records.size());
		assertEquals(Level.SEVERE, records.get(0).getLevel());
		assertTrue(records.get(0).getMessage().contains("not a valid part of the document"));
		assertInstanceOf(BadLocationException.class, records.get(0).getThrown());
	}
}
