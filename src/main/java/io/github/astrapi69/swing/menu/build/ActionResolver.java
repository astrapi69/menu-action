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

import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;

import lombok.NonNull;

/**
 * The functional interface {@link ActionResolver} resolves an action id to an
 * {@link ActionListener} object. The {@link ActionRegistry} is the default implementation, the
 * {@link MenuBuilder} can be extended with further resolvers
 */
@FunctionalInterface
public interface ActionResolver
{

	/**
	 * Resolves the {@link ActionListener} object for the given action id
	 *
	 * @param actionId
	 *            the action id
	 * @return an optional with the {@link ActionListener} object or empty if this resolver can not
	 *         resolve the action id
	 */
	Optional<ActionListener> resolve(String actionId);

	/**
	 * Factory method that creates an {@link ActionResolver} that asks the given resolvers in order
	 * and returns the first result
	 *
	 * @param resolvers
	 *            the resolvers
	 * @return the new composite {@link ActionResolver}
	 */
	static ActionResolver of(final @NonNull ActionResolver... resolvers)
	{
		List<ActionResolver> list = List.of(resolvers);
		return actionId -> {
			for (ActionResolver resolver : list)
			{
				Optional<ActionListener> actionListener = resolver.resolve(actionId);
				if (actionListener.isPresent())
				{
					return actionListener;
				}
			}
			return Optional.empty();
		};
	}
}
