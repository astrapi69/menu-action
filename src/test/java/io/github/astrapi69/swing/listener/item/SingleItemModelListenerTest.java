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
package io.github.astrapi69.swing.listener.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.awt.event.ItemEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import org.junit.jupiter.api.Test;

import io.github.astrapi69.model.BaseModel;
import io.github.astrapi69.model.api.IModel;

/**
 * The unit test class for the class {@link SingleItemModelListener}
 */
class SingleItemModelListenerTest
{

	@Test
	void comboBoxSelectionIsStoredInTheModel()
	{
		final IModel<String> model = BaseModel.of("a");
		final SingleItemModelListener<String> listener = new SingleItemModelListener<>(model);
		assertSame(model, listener.getModel());

		final JComboBox<String> source = new JComboBox<>(
			new DefaultComboBoxModel<>(new String[] { "a", "b", "c" }));
		source.addItemListener(listener);

		source.setSelectedItem("c");
		assertEquals("c", model.getObject());

		// clearing the selection stores null
		source.setSelectedItem(null);
		assertNull(model.getObject());
	}

	@Test
	void firstOfManySelectedObjectsIsStoredInTheModel()
	{
		final IModel<String> model = BaseModel.of();
		final SingleItemModelListener<String> listener = new SingleItemModelListener<>(model);

		listener.itemStateChanged(ItemBindListenerTest.newItemEvent(
			ItemBindListenerTest.itemSelectable(new Object[] { "b", "a" }), "b",
			ItemEvent.SELECTED));
		assertEquals("b", model.getObject());

		// an empty selection resolves to null
		listener.itemStateChanged(ItemBindListenerTest.newItemEvent(
			ItemBindListenerTest.itemSelectable(new Object[0]), null, ItemEvent.DESELECTED));
		assertNull(model.getObject());
	}

	@Test
	void deselectedCheckBoxStoresNullInTheModel()
	{
		final IModel<String> model = BaseModel.of();
		final JCheckBox checkBox = new JCheckBox("check");
		checkBox.addItemListener(new SingleItemModelListener<>(model));

		checkBox.setSelected(true);
		assertEquals("check", model.getObject());

		// a deselected check box returns null as selected objects
		checkBox.setSelected(false);
		assertNull(model.getObject());
	}
}
