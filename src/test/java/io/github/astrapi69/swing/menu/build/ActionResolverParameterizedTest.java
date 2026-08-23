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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * The parameterized unit test class for the class {@link ActionResolver}
 */
class ActionResolverParameterizedTest
{

	/**
	 * The {@link ActionListener} implementation that carries the tag of the resolver that created
	 * it, so the winner of a resolver chain can be identified
	 */
	static final class TaggedActionListener implements ActionListener
	{
		private final String tag;

		TaggedActionListener(final String tag)
		{
			this.tag = tag;
		}

		String tag()
		{
			return tag;
		}

		@Override
		public void actionPerformed(final ActionEvent event)
		{
			// no operation, this listener is only used to identify the resolver
		}

		@Override
		public String toString()
		{
			return tag;
		}
	}

	private static ActionResolver resolverWith(final String tag, final String... actionIds)
	{
		ActionRegistry registry = ActionRegistry.empty();
		for (String actionId : actionIds)
		{
			registry.register(actionId, new TaggedActionListener(tag + ":" + actionId));
		}
		return registry;
	}

	private static ActionResolver emptyResolver()
	{
		return actionId -> Optional.empty();
	}

	private static ActionResolver counting(final AtomicInteger calls, final String tag,
		final String... actionIds)
	{
		ActionResolver delegate = resolverWith(tag, actionIds);
		return actionId -> {
			calls.incrementAndGet();
			return delegate.resolve(actionId);
		};
	}

	private static String tagOf(final Optional<ActionListener> resolved)
	{
		return resolved.map(actionListener -> ((TaggedActionListener)actionListener).tag())
			.orElse(null);
	}

	static Stream<Arguments> ofAsksTheResolversInOrder()
	{
		ActionResolver first = resolverWith("first", "exit", "open");
		ActionResolver second = resolverWith("second", "exit", "save");
		ActionResolver third = resolverWith("third", "print");
		return Stream.of(
			Arguments.of("an empty chain resolves nothing", ActionResolver.of(), "exit", null),
			Arguments.of("a chain of empty resolvers resolves nothing",
				ActionResolver.of(emptyResolver(), emptyResolver(), emptyResolver()), "exit", null),
			Arguments.of("a single resolver answers alone", ActionResolver.of(second), "save",
				"second:save"),
			Arguments.of("the first resolver wins if both know the id",
				ActionResolver.of(first, second), "exit", "first:exit"),
			Arguments.of("the reversed chain lets the other resolver win",
				ActionResolver.of(second, first), "exit", "second:exit"),
			Arguments.of("the second resolver wins if the first is empty",
				ActionResolver.of(emptyResolver(), second), "save", "second:save"),
			Arguments.of("the second resolver wins if the first does not know the id",
				ActionResolver.of(first, second), "save", "second:save"),
			Arguments.of("the third resolver wins if the first two do not know the id",
				ActionResolver.of(first, second, third), "print", "third:print"),
			Arguments.of("an unknown id is resolved by no resolver of the chain",
				ActionResolver.of(first, second, third), "nope", null),
			Arguments.of("a null id is resolved by no resolver of the chain",
				ActionResolver.of(first, second, third), null, null),
			Arguments.of("an empty id is resolved by no resolver of the chain",
				ActionResolver.of(first, second, third), "", null));
	}

	/**
	 * Parameterized test for {@link ActionResolver#of(ActionResolver...)}
	 */
	@ParameterizedTest(name = "[{index}] {0}")
	@MethodSource
	void ofAsksTheResolversInOrder(final String caseName, final ActionResolver resolver,
		final String actionId, final String expectedTag)
	{
		Optional<ActionListener> resolved = resolver.resolve(actionId);
		assertNotNull(resolved, caseName);
		assertEquals(expectedTag != null, resolved.isPresent(), caseName);
		assertEquals(expectedTag, tagOf(resolved), caseName);
	}

	/**
	 * Parameterized test for {@link ActionResolver#of(ActionResolver...)} with a fixed chain of two
	 * resolvers and varying action ids
	 */
	@ParameterizedTest(name = "[{index}] the chain resolves the action id ''{0}''")
	@NullSource
	@EmptySource
	@ValueSource(strings = { "exit", "open", "save", "nope", "EXIT", " exit" })
	void chainResolvesOnlyTheKnownIds(final String actionId)
	{
		ActionResolver chain = ActionResolver.of(resolverWith("first", "exit", "open"),
			resolverWith("second", "open", "save"));
		String expectedTag = switch (actionId == null ? "" : actionId)
		{
			case "exit" -> "first:exit";
			case "open" -> "first:open";
			case "save" -> "second:save";
			default -> null;
		};
		Optional<ActionListener> resolved = chain.resolve(actionId);
		assertEquals(expectedTag != null, resolved.isPresent());
		assertEquals(expectedTag, tagOf(resolved));
	}

	/**
	 * Parameterized test that the chain of {@link ActionResolver#of(ActionResolver...)} stops at
	 * the first resolver with a result
	 */
	@ParameterizedTest(name = "[{index}] resolving ''{0}'' asks {1} of the three resolvers")
	@CsvSource({ "exit, 1", "save, 2", "print, 3", "nope, 3", ", 3" })
	void theChainStopsAtTheFirstResolverWithAResult(final String actionId, final int expectedCalls)
	{
		AtomicInteger calls = new AtomicInteger();
		ActionResolver chain = ActionResolver.of(counting(calls, "first", "exit"),
			counting(calls, "second", "save"), counting(calls, "third", "print"));
		chain.resolve(actionId);
		assertEquals(expectedCalls, calls.get());
	}

	/**
	 * Parameterized test that a resolver of the chain that returns null instead of an empty
	 * optional lets the chain fail
	 */
	@ParameterizedTest(name = "[{index}] a resolver that returns null fails for the id ''{0}''")
	@NullSource
	@EmptySource
	@ValueSource(strings = { "exit" })
	void aResolverThatReturnsNullFailsTheChain(final String actionId)
	{
		ActionResolver chain = ActionResolver.of(resolverWith("first", "other"), id -> null);
		assertThrows(NullPointerException.class, () -> chain.resolve(actionId));
	}

	static Stream<Arguments> ofRejectsNullResolvers()
	{
		ActionResolver valid = resolverWith("valid", "exit");
		return Stream.of(
			Arguments.of("a null array of resolvers",
				(Executable)() -> ActionResolver.of((ActionResolver[])null),
				"resolvers is marked non-null but is null"),
			Arguments.of("a null resolver as only element",
				(Executable)() -> ActionResolver.of(new ActionResolver[] { null }), null),
			Arguments.of("a null resolver at the end of the chain",
				(Executable)() -> ActionResolver.of(valid, null), null),
			Arguments.of("a null resolver in the middle of the chain",
				(Executable)() -> ActionResolver.of(valid, null, valid), null));
	}

	/**
	 * Parameterized test for the negative cases of {@link ActionResolver#of(ActionResolver...)}
	 */
	@ParameterizedTest(name = "[{index}] ActionResolver.of rejects {0}")
	@MethodSource
	void ofRejectsNullResolvers(final String caseName, final Executable executable,
		final String expectedMessageFragment)
	{
		NullPointerException exception = assertThrows(NullPointerException.class, executable,
			caseName);
		if (expectedMessageFragment != null)
		{
			assertTrue(exception.getMessage().contains(expectedMessageFragment), "the message '"
				+ exception.getMessage() + "' must contain '" + expectedMessageFragment + "'");
		}
	}
}
