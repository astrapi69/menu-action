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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;

import lombok.NonNull;

/**
 * The class {@link ActionRegistry} maps action ids to {@link ActionListener} objects. The
 * {@link MenuBuilder} resolves the actions of the menu items from the registry by the action id of
 * the menu item or, if not set, by the name of the menu item. Actions can be registered explicitly,
 * declaratively from controller objects with {@link MenuAction} annotated methods and fields or
 * from {@link ActionProvider} services
 */
public final class ActionRegistry implements ActionResolver
{

	private final Map<String, ActionListener> actions = new LinkedHashMap<>();

	/**
	 * Private constructor, use the factory methods {@link #empty()} and {@link #of(Map)}
	 */
	private ActionRegistry()
	{
	}

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
	 * Factory method that creates a new {@link ActionRegistry} object from the {@link MenuAction}
	 * annotated methods and fields of the given controller objects
	 *
	 * @param controllers
	 *            the controller objects
	 * @return the new {@link ActionRegistry} object
	 */
	public static ActionRegistry ofHandlers(final @NonNull Object... controllers)
	{
		ActionRegistry registry = new ActionRegistry();
		for (Object controller : controllers)
		{
			registry.registerHandlers(controller);
		}
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
	 * Registers all {@link MenuAction} annotated methods and fields of the given controller object
	 * including the inherited ones. A method must have no parameter or a single {@link ActionEvent}
	 * parameter, a field must be of the type {@link ActionListener} and not null. Non public
	 * members are made accessible, which requires in a named module that the package of the
	 * controller is opened to this module. An action id that is already registered by another
	 * member of the same controller causes an {@link IllegalStateException}
	 *
	 * @param controller
	 *            the controller object
	 * @return this {@link ActionRegistry} object for method chaining
	 */
	public ActionRegistry registerHandlers(final @NonNull Object controller)
	{
		Map<String, ActionListener> handlers = new LinkedHashMap<>();
		Set<String> overriddenMethodSignatures = new java.util.HashSet<>();
		for (Class<?> type = controller.getClass(); type != null
			&& type != Object.class; type = type.getSuperclass())
		{
			for (Method method : type.getDeclaredMethods())
			{
				String signature = method.getName()
					+ java.util.Arrays.toString(method.getParameterTypes());
				if (method.isBridge() || method.isSynthetic()
					|| !overriddenMethodSignatures.add(signature))
				{
					// a bridge or synthetic method carries a copy of the annotation of the method
					// it delegates to; a signature seen in a more derived class is the override
					// of this one and already registered
					continue;
				}
				MenuAction menuAction = method.getAnnotation(MenuAction.class);
				if (menuAction != null)
				{
					putHandler(handlers, menuAction.value(), toActionListener(controller, method),
						controller, method.getName());
				}
			}
			for (Field field : type.getDeclaredFields())
			{
				if (field.isSynthetic())
				{
					continue;
				}
				MenuAction menuAction = field.getAnnotation(MenuAction.class);
				if (menuAction != null)
				{
					putHandler(handlers, menuAction.value(), toActionListener(controller, field),
						controller, field.getName());
				}
			}
		}
		actions.putAll(handlers);
		return this;
	}

	/**
	 * Loads all {@link ActionProvider} services with the {@link ServiceLoader} of the class loader
	 * of this class and lets them register their actions. Unlike the plain
	 * {@link ServiceLoader#load(Class)} this does not depend on the context class loader of the
	 * calling thread
	 *
	 * @param context
	 *            the {@link ActionContext} that is passed to the providers
	 * @return this {@link ActionRegistry} object for method chaining
	 */
	public ActionRegistry loadProviders(final @NonNull ActionContext context)
	{
		return registerProviders(
			ServiceLoader.load(ActionProvider.class, ActionRegistry.class.getClassLoader()),
			context);
	}

	/**
	 * Loads all {@link ActionProvider} services with the {@link ServiceLoader} of the given class
	 * loader, for instance the class loader of a plugin, and lets them register their actions
	 *
	 * @param classLoader
	 *            the class loader
	 * @param context
	 *            the {@link ActionContext} that is passed to the providers
	 * @return this {@link ActionRegistry} object for method chaining
	 */
	public ActionRegistry loadProviders(final @NonNull ClassLoader classLoader,
		final @NonNull ActionContext context)
	{
		return registerProviders(ServiceLoader.load(ActionProvider.class, classLoader), context);
	}

	/**
	 * Lets the given {@link ActionProvider} objects register their actions
	 *
	 * @param providers
	 *            the {@link ActionProvider} objects
	 * @param context
	 *            the {@link ActionContext} that is passed to the providers
	 * @return this {@link ActionRegistry} object for method chaining
	 */
	public ActionRegistry registerProviders(final @NonNull Iterable<ActionProvider> providers,
		final @NonNull ActionContext context)
	{
		for (ActionProvider provider : providers)
		{
			provider.registerActions(this, context);
		}
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
	 * {@inheritDoc}
	 */
	@Override
	public Optional<ActionListener> resolve(final String actionId)
	{
		return find(actionId);
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

	private static void putHandler(final Map<String, ActionListener> handlers,
		final String actionId, final ActionListener actionListener, final Object controller,
		final String memberName)
	{
		if (actionId == null || actionId.isBlank())
		{
			throw new IllegalArgumentException("The @MenuAction id of " + memberName + " in "
				+ controller.getClass().getName() + " is empty");
		}
		if (handlers.containsKey(actionId))
		{
			throw new IllegalStateException("The action id '" + actionId
				+ "' is registered twice in " + controller.getClass().getName());
		}
		handlers.put(actionId, actionListener);
	}

	private static ActionListener toActionListener(final Object controller, final Method method)
	{
		Class<?>[] parameterTypes = method.getParameterTypes();
		boolean withEvent = parameterTypes.length == 1 && parameterTypes[0] == ActionEvent.class;
		if (parameterTypes.length != 0 && !withEvent)
		{
			throw new IllegalArgumentException("The @MenuAction method " + method.getName() + " in "
				+ controller.getClass().getName()
				+ " must have no parameter or a single ActionEvent parameter");
		}
		makeAccessible(controller, method);
		Object target = Modifier.isStatic(method.getModifiers()) ? null : controller;
		return event -> {
			try
			{
				if (withEvent)
				{
					method.invoke(target, event);
				}
				else
				{
					method.invoke(target);
				}
			}
			catch (IllegalAccessException e)
			{
				throw new IllegalStateException("Can not invoke the @MenuAction method "
					+ method.getName() + " in " + controller.getClass().getName(), e);
			}
			catch (InvocationTargetException e)
			{
				Throwable cause = e.getCause();
				if (cause instanceof RuntimeException runtimeException)
				{
					throw runtimeException;
				}
				if (cause instanceof Error error)
				{
					throw error;
				}
				throw new IllegalStateException("The @MenuAction method " + method.getName()
					+ " in " + controller.getClass().getName() + " failed", cause);
			}
		};
	}

	private static ActionListener toActionListener(final Object controller, final Field field)
	{
		if (!ActionListener.class.isAssignableFrom(field.getType()))
		{
			throw new IllegalArgumentException("The @MenuAction field " + field.getName() + " in "
				+ controller.getClass().getName() + " must be of the type ActionListener");
		}
		makeAccessible(controller, field);
		try
		{
			Object target = Modifier.isStatic(field.getModifiers()) ? null : controller;
			Object value = field.get(target);
			if (value == null)
			{
				throw new IllegalStateException("The @MenuAction field " + field.getName() + " in "
					+ controller.getClass().getName() + " is null");
			}
			return (ActionListener)value;
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalStateException("Can not read the @MenuAction field " + field.getName()
				+ " in " + controller.getClass().getName(), e);
		}
	}

	private static void makeAccessible(final Object controller,
		final java.lang.reflect.AccessibleObject member)
	{
		try
		{
			member.setAccessible(true);
		}
		catch (RuntimeException e)
		{
			throw new IllegalStateException("The @MenuAction member in "
				+ controller.getClass().getName()
				+ " is not accessible, make it public or open the package to the module io.github.astrapisixtynine.menu.action",
				e);
		}
	}
}
