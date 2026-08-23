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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Container;
import java.awt.event.ActionListener;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;

import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * The parameterized unit test class for the class {@link ActionContext}
 */
class ActionContextParameterizedTest
{

	/** the first registered object, it is registered under its class name */
	static final JPanel PANEL = new JPanel();

	/** a second panel that is used to verify that a key is replaced */
	static final JPanel OTHER_PANEL = new JPanel();

	/** the object that is registered under the key 'label' */
	static final JLabel LABEL = new JLabel("l");

	/** the object that is registered under the key 'field' */
	static final JTextField FIELD = new JTextField("t");

	/**
	 * Creates the {@link ActionContext} object that all lookup cases work on. The panel is
	 * registered first, so it is the first match for all its super types
	 *
	 * @return the new {@link ActionContext} object
	 */
	private static ActionContext context()
	{
		return ActionContext.empty().put(PANEL).put("label", LABEL).put("field", FIELD);
	}

	static Stream<Arguments> lookupsReturnTheExpectedObject()
	{
		return Stream.of(
			Arguments.of("get(key) with the class name of a put(value) object",
				(Function<ActionContext, Optional<?>>)context -> context
					.get(JPanel.class.getName()),
				PANEL),
			Arguments.of("get(key) with an explicit key",
				(Function<ActionContext, Optional<?>>)context -> context.get("label"), LABEL),
			Arguments.of("get(key) with an unknown key",
				(Function<ActionContext, Optional<?>>)context -> context.get("nope"), null),
			Arguments.of("get(key) with a null key",
				(Function<ActionContext, Optional<?>>)context -> context.get((String)null), null),
			Arguments.of("get(key) with an empty key",
				(Function<ActionContext, Optional<?>>)context -> context.get(""), null),
			Arguments.of("get(key, type) with the exact type",
				(Function<ActionContext, Optional<?>>)context -> context.get("label", JLabel.class),
				LABEL),
			Arguments.of("get(key, type) with a super type",
				(Function<ActionContext, Optional<?>>)context -> context.get("label",
					JComponent.class),
				LABEL),
			Arguments.of("get(key, type) with a wrong type",
				(Function<ActionContext, Optional<?>>)context -> context.get("label", JPanel.class),
				null),
			Arguments.of("get(key, type) with an unknown key",
				(Function<ActionContext, Optional<?>>)context -> context.get("nope", JLabel.class),
				null),
			Arguments.of("get(key, type) with a null key",
				(Function<ActionContext, Optional<?>>)context -> context.get(null, JLabel.class),
				null),
			Arguments.of("get(type) with the exact type of the first object",
				(Function<ActionContext, Optional<?>>)context -> context.get(JPanel.class), PANEL),
			Arguments.of("get(type) with the exact type of a later object",
				(Function<ActionContext, Optional<?>>)context -> context.get(JTextField.class),
				FIELD),
			Arguments.of("get(type) with the exact type of the object with an explicit key",
				(Function<ActionContext, Optional<?>>)context -> context.get(JLabel.class), LABEL),
			Arguments.of("get(type) with a super type returns the first registered object",
				(Function<ActionContext, Optional<?>>)context -> context.get(JComponent.class),
				PANEL),
			Arguments.of("get(type) with an awt super type returns the first registered object",
				(Function<ActionContext, Optional<?>>)context -> context.get(Container.class),
				PANEL),
			Arguments.of("get(type) with Object returns the first registered object",
				(Function<ActionContext, Optional<?>>)context -> context.get(Object.class), PANEL),
			Arguments.of("get(type) with an unregistered type",
				(Function<ActionContext, Optional<?>>)context -> context.get(JTree.class), null),
			Arguments.of("get(type) with an unimplemented interface",
				(Function<ActionContext, Optional<?>>)context -> context.get(ActionListener.class),
				null));
	}

	/**
	 * Parameterized test for the combinations of {@link ActionContext#get(String)},
	 * {@link ActionContext#get(String, Class)} and {@link ActionContext#get(Class)}
	 */
	@ParameterizedTest(name = "[{index}] {0}")
	@MethodSource
	void lookupsReturnTheExpectedObject(final String caseName,
		final Function<ActionContext, Optional<?>> lookup, final Object expected)
	{
		Optional<?> actual = lookup.apply(context());
		if (expected == null)
		{
			assertFalse(actual.isPresent(), caseName);
		}
		else
		{
			assertSame(expected, actual.orElseThrow(), caseName);
		}
	}

	static Stream<Arguments> putStoresTheObjectUnderTheExpectedKey()
	{
		return Stream.of(
			Arguments.of("put(value) uses the class name as key", ActionContext.empty().put(PANEL),
				1, JPanel.class.getName(), PANEL),
			Arguments.of("put(key, value) uses the given key",
				ActionContext.empty().put("panel", PANEL), 1, "panel", PANEL),
			Arguments.of("put(key, value) with the same key replaces the object",
				ActionContext.empty().put("x", PANEL).put("x", LABEL), 1, "x", LABEL),
			Arguments.of("put(value) with the same class replaces the object",
				ActionContext.empty().put(PANEL).put(OTHER_PANEL), 1, JPanel.class.getName(),
				OTHER_PANEL),
			Arguments.of("put(key, value) and put(value) of the same object keep both entries",
				ActionContext.empty().put("panel", PANEL).put(PANEL), 2, "panel", PANEL),
			Arguments.of("put(value) after put(key, value) is reachable by the class name",
				ActionContext.empty().put("panel", PANEL).put(PANEL), 2, JPanel.class.getName(),
				PANEL),
			Arguments.of("put(key, value) accepts an empty key",
				ActionContext.empty().put("", LABEL), 1, "", LABEL),
			Arguments.of("put(key, value) accepts a key that is not a class name",
				ActionContext.empty().put("the.frame.title", FIELD), 1, "the.frame.title", FIELD));
	}

	/**
	 * Parameterized test for the combinations of {@link ActionContext#put(String, Object)} and
	 * {@link ActionContext#put(Object)}
	 */
	@ParameterizedTest(name = "[{index}] {0}")
	@MethodSource
	void putStoresTheObjectUnderTheExpectedKey(final String caseName, final ActionContext context,
		final int expectedSize, final String key, final Object expectedValue)
	{
		assertEquals(expectedSize, context.size(), caseName);
		assertSame(expectedValue, context.get(key).orElseThrow(), caseName);
	}

	static Stream<Arguments> requireReturnsTheFirstObjectOfTheType()
	{
		return Stream.of(Arguments.of(JPanel.class, PANEL), Arguments.of(JLabel.class, LABEL),
			Arguments.of(JTextField.class, FIELD), Arguments.of(JComponent.class, PANEL),
			Arguments.of(Container.class, PANEL), Arguments.of(Object.class, PANEL));
	}

	/**
	 * Parameterized test for {@link ActionContext#require(Class)} with registered types
	 */
	@ParameterizedTest(name = "[{index}] require({0}) returns the first registered object")
	@MethodSource
	void requireReturnsTheFirstObjectOfTheType(final Class<?> type, final Object expected)
	{
		ActionContext context = context();
		assertSame(expected, context.require(type));
		assertSame(expected, context.get(type).orElseThrow());
	}

	/**
	 * Parameterized test for the negative cases of {@link ActionContext#require(Class)}
	 */
	@ParameterizedTest(name = "[{index}] require({0}) fails because no object of the type exists")
	@ValueSource(classes = { JTree.class, ActionListener.class, Integer.class, Runnable.class })
	void requireFailsForUnregisteredTypes(final Class<?> type)
	{
		ActionContext context = context();
		IllegalStateException exception = assertThrows(IllegalStateException.class,
			() -> context.require(type));
		assertEquals("No object of type " + type.getName() + " registered in the action context",
			exception.getMessage());
		assertFalse(context.get(type).isPresent());
	}

	/**
	 * Parameterized test that an empty {@link ActionContext} object resolves no key
	 */
	@ParameterizedTest(name = "[{index}] an empty action context does not know the key ''{0}''")
	@NullSource
	@EmptySource
	@ValueSource(strings = { "label", "javax.swing.JPanel", "nope" })
	void anEmptyContextResolvesNothing(final String key)
	{
		ActionContext context = ActionContext.empty();
		assertEquals(0, context.size());
		assertFalse(context.get(key).isPresent());
		assertFalse(context.get(key, JLabel.class).isPresent());
	}

	static Stream<Arguments> nullArgumentsAreRejected()
	{
		ActionContext context = context();
		return Stream.of(
			Arguments.of("put with a null key", (Executable)() -> context.put(null, PANEL),
				"key is marked non-null but is null"),
			Arguments.of("put with a null value", (Executable)() -> context.put("panel", null),
				"value is marked non-null but is null"),
			Arguments.of("put with a null object", (Executable)() -> context.put((Object)null),
				"value is marked non-null but is null"),
			Arguments.of("get with a null type", (Executable)() -> context.get("label", null),
				"type is marked non-null but is null"),
			Arguments.of("get with a null class", (Executable)() -> context.get((Class<?>)null),
				"type is marked non-null but is null"),
			Arguments.of("require with a null type", (Executable)() -> context.require(null),
				"type is marked non-null but is null"));
	}

	/**
	 * Parameterized test for the null argument cases of the {@link ActionContext} methods
	 */
	@ParameterizedTest(name = "[{index}] {0} is rejected")
	@MethodSource
	void nullArgumentsAreRejected(final String caseName, final Executable executable,
		final String expectedMessageFragment)
	{
		NullPointerException exception = assertThrows(NullPointerException.class, executable,
			caseName);
		assertTrue(exception.getMessage().contains(expectedMessageFragment), "the message '"
			+ exception.getMessage() + "' must contain '" + expectedMessageFragment + "'");
	}
}
