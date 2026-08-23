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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JMenuItem;

import org.junit.jupiter.api.Test;

/**
 * The unit test class for the class {@link ActionRegistry}
 */
class ActionRegistryTest
{

	private static ActionEvent newEvent(final String command)
	{
		return new ActionEvent(new JMenuItem(), ActionEvent.ACTION_PERFORMED, command);
	}

	static class BaseController
	{
		final List<String> calls = new ArrayList<>();

		@MenuAction("inherited")
		private void inherited()
		{
			calls.add("inherited");
		}
	}

	static class MainController extends BaseController
	{
		@MenuAction("field")
		final ActionListener fieldListener = e -> calls.add("field:" + e.getActionCommand());

		@MenuAction("exit")
		void exit()
		{
			calls.add("exit");
		}

		@MenuAction("open")
		public void open(final ActionEvent event)
		{
			calls.add("open:" + event.getActionCommand());
		}

		@MenuAction("static")
		static void staticAction()
		{
		}

		void notAnAction()
		{
			calls.add("never");
		}
	}

	static class DuplicateController
	{
		@MenuAction("dup")
		void one()
		{
		}

		@MenuAction("dup")
		void two()
		{
		}
	}

	static class WrongSignatureController
	{
		@MenuAction("wrong")
		void wrong(final String value)
		{
		}
	}

	static class WrongFieldController
	{
		@MenuAction("wrong")
		String wrong = "x";
	}

	static class NullFieldController
	{
		@MenuAction("nullField")
		ActionListener listener;
	}

	static class EmptyIdController
	{
		@MenuAction(" ")
		void empty()
		{
		}
	}

	static class FailingController
	{
		@MenuAction("fails")
		void fails()
		{
			throw new IllegalArgumentException("boom");
		}

		@MenuAction("checked")
		void checked() throws Exception
		{
			throw new Exception("checked");
		}
	}

	@Test
	void registerAndFind()
	{
		ActionListener exit = e -> {
		};
		ActionListener open = e -> {
		};
		ActionRegistry registry = ActionRegistry.of(Map.of("open", open)).register("exit", exit);
		assertEquals(2, registry.size());
		assertTrue(registry.contains("exit"));
		assertFalse(registry.contains("nope"));
		assertFalse(registry.contains(null));
		assertEquals(exit, registry.find("exit").orElseThrow());
		assertEquals(exit, registry.resolve("exit").orElseThrow());
		assertFalse(registry.find("nope").isPresent());
		assertFalse(registry.find(null).isPresent());
		assertTrue(registry.ids().containsAll(Set.of("open", "exit")));
		assertThrows(UnsupportedOperationException.class, () -> registry.ids().add("x"));

		ActionRegistry other = ActionRegistry.empty().register("save", e -> {
		});
		registry.registerAll(other);
		assertEquals(3, registry.size());
	}

	@Test
	void registerHandlers()
	{
		MainController controller = new MainController();
		ActionRegistry registry = ActionRegistry.ofHandlers(controller);
		assertEquals(Set.of("exit", "open", "field", "static", "inherited"), registry.ids());

		registry.find("exit").orElseThrow().actionPerformed(newEvent("ignored"));
		registry.find("open").orElseThrow().actionPerformed(newEvent("file.txt"));
		registry.find("field").orElseThrow().actionPerformed(newEvent("f"));
		registry.find("inherited").orElseThrow().actionPerformed(newEvent("i"));
		registry.find("static").orElseThrow().actionPerformed(newEvent("s"));
		assertEquals(List.of("exit", "open:file.txt", "field:f", "inherited"), controller.calls);
	}

	@Test
	void registerHandlersRejectsInvalidControllers()
	{
		assertThrows(IllegalStateException.class,
			() -> ActionRegistry.ofHandlers(new DuplicateController()));
		assertThrows(IllegalArgumentException.class,
			() -> ActionRegistry.ofHandlers(new WrongSignatureController()));
		assertThrows(IllegalArgumentException.class,
			() -> ActionRegistry.ofHandlers(new WrongFieldController()));
		assertThrows(IllegalStateException.class,
			() -> ActionRegistry.ofHandlers(new NullFieldController()));
		assertThrows(IllegalArgumentException.class,
			() -> ActionRegistry.ofHandlers(new EmptyIdController()));
	}

	@Test
	void handlerExceptionsArePropagated()
	{
		ActionRegistry registry = ActionRegistry.ofHandlers(new FailingController());
		IllegalArgumentException runtime = assertThrows(IllegalArgumentException.class,
			() -> registry.find("fails").orElseThrow().actionPerformed(newEvent("x")));
		assertEquals("boom", runtime.getMessage());
		IllegalStateException checked = assertThrows(IllegalStateException.class,
			() -> registry.find("checked").orElseThrow().actionPerformed(newEvent("x")));
		assertEquals("checked", checked.getCause().getMessage());
	}

	@Test
	void providers()
	{
		ActionContext context = ActionContext.empty().put("frameTitle", "Demo");
		ActionProvider provider = (registry, ctx) -> registry.register("fromProvider",
			e -> ctx.get("frameTitle", String.class).orElseThrow());
		ActionRegistry registry = ActionRegistry.empty().registerProviders(List.of(provider),
			context);
		assertTrue(registry.contains("fromProvider"));

		// no provider is registered as service in this build, the call must not fail
		ActionRegistry loaded = ActionRegistry.empty().loadProviders(context);
		assertEquals(0, loaded.size());
		ActionRegistry loadedWithClassLoader = ActionRegistry.empty()
			.loadProviders(ActionRegistryTest.class.getClassLoader(), context);
		assertEquals(0, loadedWithClassLoader.size());
	}
}
