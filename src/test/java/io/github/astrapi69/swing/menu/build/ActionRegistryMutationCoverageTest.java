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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;

import org.junit.jupiter.api.Test;

/**
 * Additional unit tests for {@link ActionRegistry} written against the pitest mutation report to
 * exercise branches that {@link ActionRegistryTest} and {@link ActionRegistryParameterizedTest} did
 * not distinguish
 */
class ActionRegistryMutationCoverageTest
{

	private static ActionEvent newEvent(final String command)
	{
		return new ActionEvent(new JMenuItem(), ActionEvent.ACTION_PERFORMED, command);
	}

	static class PrivateFieldController
	{
		final List<String> calls = new ArrayList<>();

		@MenuAction("privateField")
		private final ActionListener listener = e -> calls
			.add("privateField:" + e.getActionCommand());
	}

	static class TooManyParametersController
	{
		@MenuAction("tooMany")
		void tooMany(final String a, final String b)
		{
		}
	}

	static class BaseWithOverride
	{
		@MenuAction("shared")
		void run()
		{
		}
	}

	static class SubWithOverride extends BaseWithOverride
	{
		final List<String> calls = new ArrayList<>();

		@Override
		@MenuAction("shared")
		void run()
		{
			calls.add("sub");
		}
	}

	/**
	 * A private field annotated with {@link MenuAction} is only readable because
	 * {@code toActionListener(Object, Field)} makes it accessible first; a package private field
	 * (as used by the other controllers in {@link ActionRegistryTest}) would already be readable
	 * without that call since the registry is in the same package, so it does not exercise this
	 * branch
	 */
	@Test
	void aPrivateFieldIsMadeAccessibleAndInvoked()
	{
		PrivateFieldController controller = new PrivateFieldController();
		ActionRegistry registry = ActionRegistry.ofHandlers(controller);

		registry.find("privateField").orElseThrow().actionPerformed(newEvent("x"));

		assertEquals(List.of("privateField:x"), controller.calls);
	}

	@Test
	void aMethodWithMoreThanOneParameterIsRejected()
	{
		assertThrows(IllegalArgumentException.class,
			() -> ActionRegistry.ofHandlers(new TooManyParametersController()));
	}

	/**
	 * The most derived class is walked first; a base class method already seen (by signature) in a
	 * subclass is skipped so it is not registered a second time, which would otherwise throw since
	 * both methods share the same action id
	 */
	@Test
	void anOverriddenMethodIsRegisteredOnlyOnceFromTheMostDerivedClass()
	{
		SubWithOverride controller = new SubWithOverride();

		ActionRegistry registry = ActionRegistry.ofHandlers(controller);

		assertEquals(1, registry.size());
		registry.find("shared").orElseThrow().actionPerformed(newEvent("x"));
		assertEquals(List.of("sub"), controller.calls);
	}
}
