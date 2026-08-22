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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import lombok.NonNull;

/**
 * The class {@link ActionRegistry} maps action ids to {@link ActionListener} objects. The
 * {@link MenuBuilder} resolves the actions of the menu items from the registry by the action id of
 * the menu item or, if not set, by the name of the menu item
 */
public final class ActionRegistry
{

	private final Map<String, ActionListener> actions = new LinkedHashMap<>();

	/**
	 * Factory method that creates a new empty {@link ActionRegistry} object
	 *
	 * @return the new {@link ActionRegistry} object
	 */
	public static ActionRegistry empty()
	{
		return new ActionRegistry();
	}

	/**
	 * Factory method that creates a new {@link ActionRegistry} object from the given map
	 *
	 * @param actions
	 *            the map with the action ids and the {@link ActionListener} objects
	 * @return the new {@link ActionRegistry} object
	 */
	public static ActionRegistry of(final @NonNull Map<String, ? extends ActionListener> actions)
	{
		ActionRegistry registry = new ActionRegistry();
		actions.forEach(registry::register);
		return registry;
	}

	/**
	 * Registers the given {@link ActionListener} object under the given action id. An existing
	 * registration with the same id is replaced
	 *
	 * @param actionId
	 *            the action id
	 * @param actionListener
	 *            the {@link ActionListener} object
	 * @return this {@link ActionRegistry} object for method chaining
	 */
	public ActionRegistry register(final @NonNull String actionId,
		final @NonNull ActionListener actionListener)
	{
		actions.put(actionId, actionListener);
		return this;
	}

	/**
	 * Registers all entries of the given {@link ActionRegistry} object
	 *
	 * @param other
	 *            the other {@link ActionRegistry} object
	 * @return this {@link ActionRegistry} object for method chaining
	 */
	public ActionRegistry registerAll(final @NonNull ActionRegistry other)
	{
		actions.putAll(other.actions);
		return this;
	}

	/**
	 * Finds the {@link ActionListener} object for the given action id
	 *
	 * @param actionId
	 *            the action id
	 * @return an optional with the {@link ActionListener} object or empty if not registered
	 */
	public Optional<ActionListener> find(final String actionId)
	{
		return actionId == null ? Optional.empty() : Optional.ofNullable(actions.get(actionId));
	}

	/**
	 * Checks if an action is registered under the given action id
	 *
	 * @param actionId
	 *            the action id
	 * @return true if an action is registered under the given action id otherwise false
	 */
	public boolean contains(final String actionId)
	{
		return actionId != null && actions.containsKey(actionId);
	}

	/**
	 * Gets all registered action ids
	 *
	 * @return an unmodifiable set with all registered action ids
	 */
	public Set<String> ids()
	{
		return Collections.unmodifiableSet(actions.keySet());
	}

	/**
	 * Gets the number of registered actions
	 *
	 * @return the number of registered actions
	 */
	public int size()
	{
		return actions.size();
	}
}
