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
package io.github.astrapi69.swing.listener.item;

import java.awt.ItemSelectable;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import io.github.astrapi69.model.api.IModel;
import lombok.Getter;

/**
 * The listener interface {@link ItemModelListener} receives itemBind events.
 *
 * @param <T>
 *            the generic type
 */
@Getter
public class ItemModelListener<T> implements ItemListener
{

	/** The model. */
	private final IModel<T[]> model;

	/**
	 * Instantiates a new {@link ItemModelListener}.
	 *
	 * @param model
	 *            the model
	 */
	public ItemModelListener(final IModel<T[]> model)
	{
		this.model = model;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void itemStateChanged(final ItemEvent e)
	{
		final ItemSelectable is = e.getItemSelectable();
		final T[] selected = (T[])is.getSelectedObjects();
		model.setObject(selected);
	}

}
