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

import java.awt.ItemSelectable;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import org.junit.jupiter.api.Test;

/**
 * The unit test class for the class {@link ItemBindListener}
 */
class ItemBindListenerTest
{

	static ItemSelectable itemSelectable(final Object[] selectedObjects)
	{
		return new ItemSelectable()
		{
			@Override
			public Object[] getSelectedObjects()
			{
				return selectedObjects;
			}

			@Override
			public void addItemListener(final ItemListener listener)
			{
			}

			@Override
			public void removeItemListener(final ItemListener listener)
			{
			}
		};
	}

	static ItemEvent newItemEvent(final ItemSelectable source, final Object item,
		final int stateChange)
	{
		return new ItemEvent(source, ItemEvent.ITEM_STATE_CHANGED, item, stateChange);
	}

	@Test
	void comboBoxSelectionIsBoundToTheTargetModel()
	{
		final JComboBox<String> source = new JComboBox<>(
			new DefaultComboBoxModel<>(new String[] { "a", "b", "c" }));
		final DefaultComboBoxModel<String> target = new DefaultComboBoxModel<>(
			new String[] { "a", "b", "c" });
		source.addItemListener(new ItemBindListener<>(target));
		assertEquals("a", target.getSelectedItem());

		source.setSelectedItem("b");
		assertEquals("b", target.getSelectedItem());

		source.setSelectedIndex(2);
		assertEquals("c", target.getSelectedItem());

		// clearing the selection of the source clears the selection of the target
		source.setSelectedItem(null);
		assertNull(target.getSelectedItem());
	}

	@Test
	void firstSelectedObjectIsTakenFromASyntheticItemEvent()
	{
		final DefaultComboBoxModel<String> target = new DefaultComboBoxModel<>(
			new String[] { "a", "b" });
		final ItemBindListener<String> listener = new ItemBindListener<>(target);

		listener.itemStateChanged(
			newItemEvent(itemSelectable(new Object[] { "b", "a" }), "b", ItemEvent.SELECTED));
		assertEquals("b", target.getSelectedItem());

		// an empty selection resolves to null
		listener.itemStateChanged(
			newItemEvent(itemSelectable(new Object[0]), null, ItemEvent.DESELECTED));
		assertNull(target.getSelectedItem());

		// a null selection as from a deselected AbstractButton resolves to null as well
		listener.itemStateChanged(
			newItemEvent(itemSelectable(new Object[] { "a" }), "a", ItemEvent.SELECTED));
		assertEquals("a", target.getSelectedItem());
		listener.itemStateChanged(newItemEvent(itemSelectable(null), null, ItemEvent.DESELECTED));
		assertNull(target.getSelectedItem());
	}

	@Test
	void checkBoxSelectionIsBoundToTheTargetModel()
	{
		final JCheckBox checkBox = new JCheckBox("check");
		final DefaultComboBoxModel<String> target = new DefaultComboBoxModel<>();
		checkBox.addItemListener(new ItemBindListener<>(target));

		checkBox.setSelected(true);
		assertEquals("check", target.getSelectedItem());

		// a deselected check box returns null as selected objects
		checkBox.setSelected(false);
		assertNull(target.getSelectedItem());
	}
}
