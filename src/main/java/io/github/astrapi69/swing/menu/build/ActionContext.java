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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import lombok.NonNull;

/**
 * The class {@link ActionContext} carries the objects that an {@link ActionProvider} needs to
 * create its actions, for instance the application frame or the application model. Objects are
 * registered by key and can additionally be looked up by type
 */
public final class ActionContext
{

	private final Map<String, Object> objects = new LinkedHashMap<>();

	/**
	 * Factory method that creates a new empty {@link ActionContext} object
	 *
	 * @return the new {@link ActionContext} object
	 */
	public static ActionContext empty()
	{
		return new ActionContext();
	}

	/**
	 * Registers the given object under the given key
	 *
	 * @param key
	 *            the key
	 * @param value
	 *            the object
	 * @return this {@link ActionContext} object for method chaining
	 */
	public ActionContext put(final @NonNull String key, final @NonNull Object value)
	{
		objects.put(key, value);
		return this;
	}

	/**
	 * Registers the given object under the name of its class
	 *
	 * @param value
	 *            the object
	 * @return this {@link ActionContext} object for method chaining
	 */
	public ActionContext put(final @NonNull Object value)
	{
		return put(value.getClass().getName(), value);
	}

	/**
	 * Gets the object with the given key
	 *
	 * @param key
	 *            the key
	 * @return an optional with the object or empty if not registered
	 */
	public Optional<Object> get(final String key)
	{
		return key == null ? Optional.empty() : Optional.ofNullable(objects.get(key));
	}

	/**
	 * Gets the object with the given key as the given type
	 *
	 * @param <T>
	 *            the type of the object
	 * @param key
	 *            the key
	 * @param type
	 *            the class of the object
	 * @return an optional with the object or empty if not registered or not of the type
	 */
	public <T> Optional<T> get(final String key, final @NonNull Class<T> type)
	{
		return get(key).filter(type::isInstance).map(type::cast);
	}

	/**
	 * Gets the first registered object that is an instance of the given type
	 *
	 * @param <T>
	 *            the type of the object
	 * @param type
	 *            the class of the object
	 * @return an optional with the object or empty if no object of the type is registered
	 */
	public <T> Optional<T> get(final @NonNull Class<T> type)
	{
		return objects.values().stream().filter(type::isInstance).map(type::cast).findFirst();
	}

	/**
	 * Gets the first registered object that is an instance of the given type or throws an
	 * {@link IllegalStateException}
	 *
	 * @param <T>
	 *            the type of the object
	 * @param type
	 *            the class of the object
	 * @return the object
	 */
	public <T> T require(final @NonNull Class<T> type)
	{
		return get(type).orElseThrow(() -> new IllegalStateException(
			"No object of type " + type.getName() + " registered in the action context"));
	}

	/**
	 * Gets the number of registered objects
	 *
	 * @return the number of registered objects
	 */
	public int size()
	{
		return objects.size();
	}
}
