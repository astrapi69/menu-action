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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.swing.JTree;

import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * The parameterized unit test class for the class {@link ActionRegistry}
 */
class ActionRegistryParameterizedTest
{

	private static ActionEvent newEvent(final String command)
	{
		return new ActionEvent(ActionRegistryParameterizedTest.class, ActionEvent.ACTION_PERFORMED,
			command);
	}

	/**
	 * The base class of all controller fixtures that record their invocations
	 */
	abstract static class RecordingController
	{
		final List<String> calls = new ArrayList<>();

		List<String> recorded()
		{
			return calls;
		}
	}

	/** the controller fixture with a {@link MenuAction} method without a parameter */
	static final class NoParameterController extends RecordingController
	{
		@MenuAction("exit")
		void exit()
		{
			calls.add("exit");
		}
	}

	/** the controller fixture with a {@link MenuAction} method with an {@link ActionEvent} */
	static final class EventParameterController extends RecordingController
	{
		@MenuAction("open")
		public void open(final ActionEvent event)
		{
			calls.add("open:" + event.getActionCommand());
		}
	}

	/**
	 * the invalid controller fixture with a {@link MenuAction} method with an {@link Object}
	 * parameter; a method parameter must be exactly {@link ActionEvent}, so this is rejected
	 */
	static final class ObjectParameterController extends RecordingController
	{
		@MenuAction("lenient")
		void lenient(final Object event)
		{
			calls.add("lenient:" + ((ActionEvent)event).getActionCommand());
		}
	}

	/** the controller fixture with a static {@link MenuAction} method */
	static final class StaticMethodController extends RecordingController
	{
		static final List<String> STATIC_CALLS = new ArrayList<>();

		StaticMethodController()
		{
			STATIC_CALLS.clear();
		}

		@MenuAction("staticAction")
		static void staticAction()
		{
			STATIC_CALLS.add("staticAction");
		}

		@Override
		List<String> recorded()
		{
			return STATIC_CALLS;
		}
	}

	/** the controller fixture with a {@link MenuAction} {@link ActionListener} field */
	static final class ActionListenerFieldController extends RecordingController
	{
		@MenuAction("field")
		final ActionListener fieldListener = event -> calls
			.add("field:" + event.getActionCommand());
	}

	/** the controller fixture with an inherited private {@link MenuAction} method */
	static class BaseInheritedController extends RecordingController
	{
		@MenuAction("inherited")
		private void inherited()
		{
			calls.add("inherited");
		}
	}

	/** the controller fixture that inherits the private {@link MenuAction} method */
	static final class InheritedController extends BaseInheritedController
	{
	}

	/** the controller fixture with a public {@link MenuAction} method */
	static final class PublicMethodController extends RecordingController
	{
		@MenuAction("save")
		public void save()
		{
			calls.add("save");
		}
	}

	/** the controller fixture with all kinds of {@link MenuAction} members */
	static final class MixedController extends BaseInheritedController
	{
		@MenuAction("field")
		final ActionListener fieldListener = event -> calls
			.add("field:" + event.getActionCommand());

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

		@MenuAction("staticAction")
		static void staticAction()
		{
			// a static member can not record into the instance list
		}

		void notAnAction()
		{
			calls.add("never");
		}
	}

	/** the controller fixture without any {@link MenuAction} member */
	static final class PlainController
	{
		public void doSomething()
		{
			// no operation
		}
	}

	/** the controller fixture whose actions fail */
	static final class FailingController extends RecordingController
	{
		@MenuAction("runtimeFailure")
		void runtimeFailure()
		{
			throw new IllegalArgumentException("boom");
		}

		@MenuAction("checkedFailure")
		void checkedFailure() throws Exception
		{
			throw new Exception("checked");
		}

		@MenuAction("errorFailure")
		void errorFailure()
		{
			throw new AssertionError("error");
		}
	}

	/** the invalid controller fixture with the same action id on two methods */
	static final class DuplicateIdController
	{
		@MenuAction("dup")
		void one()
		{
			// no operation
		}

		@MenuAction("dup")
		void two()
		{
			// no operation
		}
	}

	/** the base class of the invalid controller fixture with a duplicate in the hierarchy */
	static class DuplicateIdBaseController
	{
		@MenuAction("dup")
		void base()
		{
			// no operation
		}
	}

	/** the invalid controller fixture with the same action id in the class hierarchy */
	static final class DuplicateIdInHierarchyController extends DuplicateIdBaseController
	{
		@MenuAction("dup")
		void child()
		{
			// no operation
		}
	}

	/** the invalid controller fixture with the same action id on a method and a field */
	static final class DuplicateMethodAndFieldController
	{
		@MenuAction("dup")
		final ActionListener listener = event -> {
			// no operation
		};

		@MenuAction("dup")
		void dup()
		{
			// no operation
		}
	}

	/** the invalid controller fixture with a method with a wrong parameter type */
	static final class WrongMethodSignatureController
	{
		@MenuAction("wrong")
		void wrong(final String value)
		{
			// no operation
		}
	}

	/** the invalid controller fixture with a method with two parameters */
	static final class TwoParameterController
	{
		@MenuAction("two")
		void two(final ActionEvent event, final String value)
		{
			// no operation
		}
	}

	/** the invalid controller fixture with a field that is not an {@link ActionListener} */
	static final class WrongFieldTypeController
	{
		@MenuAction("wrong")
		final String wrong = "x";
	}

	/** the invalid controller fixture with an {@link ActionListener} field that is null */
	static final class NullFieldController
	{
		@MenuAction("nullField")
		ActionListener listener;
	}

	/** the invalid controller fixture with a blank action id */
	static final class BlankIdController
	{
		@MenuAction(" ")
		void blank()
		{
			// no operation
		}
	}

	/** the invalid controller fixture with an empty action id */
	static final class EmptyIdController
	{
		@MenuAction("")
		void empty()
		{
			// no operation
		}
	}

	/**
	 * The enum of the invalid controller fixtures, it lets the invalid controllers be referenced
	 * from a {@link CsvSource}
	 */
	enum InvalidControllerFixture
	{
		/** the same action id on two methods */
		DUPLICATE_ID(DuplicateIdController::new),
		/** the same action id in the class hierarchy */
		DUPLICATE_ID_IN_HIERARCHY(DuplicateIdInHierarchyController::new),
		/** the same action id on a method and a field */
		DUPLICATE_METHOD_AND_FIELD(DuplicateMethodAndFieldController::new),
		/** a method with a wrong parameter type */
		WRONG_METHOD_SIGNATURE(WrongMethodSignatureController::new),
		/** a method with two parameters */
		TWO_PARAMETERS(TwoParameterController::new),
		/** a method with an {@link Object} parameter instead of exactly {@link ActionEvent} */
		OBJECT_PARAMETER(ObjectParameterController::new),
		/** a field that is not an {@link ActionListener} */
		WRONG_FIELD_TYPE(WrongFieldTypeController::new),
		/** an {@link ActionListener} field that is null */
		NULL_FIELD(NullFieldController::new),
		/** a blank action id */
		BLANK_ID(BlankIdController::new),
		/** an empty action id */
		EMPTY_ID(EmptyIdController::new);

		private final Supplier<Object> factory;

		InvalidControllerFixture(final Supplier<Object> factory)
		{
			this.factory = factory;
		}

		Object controller()
		{
			return factory.get();
		}
	}

	static Stream<Arguments> registerHandlersRegistersTheAnnotatedMembers()
	{
		return Stream.of(
			Arguments.of("a method without a parameter",
				(Supplier<RecordingController>)NoParameterController::new, Set.of("exit"),
				List.of("exit"), "cmd", List.of("exit")),
			Arguments.of("a method with an ActionEvent parameter",
				(Supplier<RecordingController>)EventParameterController::new, Set.of("open"),
				List.of("open"), "file.txt", List.of("open:file.txt")),
			Arguments.of("a static method",
				(Supplier<RecordingController>)StaticMethodController::new, Set.of("staticAction"),
				List.of("staticAction"), "cmd", List.of("staticAction")),
			Arguments.of("an ActionListener field",
				(Supplier<RecordingController>)ActionListenerFieldController::new, Set.of("field"),
				List.of("field"), "f", List.of("field:f")),
			Arguments.of("an inherited private method",
				(Supplier<RecordingController>)InheritedController::new, Set.of("inherited"),
				List.of("inherited"), "cmd", List.of("inherited")),
			Arguments.of("a public method",
				(Supplier<RecordingController>)PublicMethodController::new, Set.of("save"),
				List.of("save"), "cmd", List.of("save")),
			Arguments.of("all kinds of members in one controller",
				(Supplier<RecordingController>)MixedController::new,
				Set.of("exit", "open", "field", "staticAction", "inherited"),
				List.of("exit", "open", "field", "inherited", "staticAction"), "cmd",
				List.of("exit", "open:cmd", "field:cmd", "inherited")),
			Arguments.of("an action can be invoked more than once",
				(Supplier<RecordingController>)NoParameterController::new, Set.of("exit"),
				List.of("exit", "exit", "exit"), "cmd", List.of("exit", "exit", "exit")));
	}

	/**
	 * Parameterized test for {@link ActionRegistry#registerHandlers(Object)} with the valid member
	 * kinds
	 */
	@ParameterizedTest(name = "[{index}] registerHandlers registers {0}")
	@MethodSource
	void registerHandlersRegistersTheAnnotatedMembers(final String caseName,
		final Supplier<RecordingController> factory, final Set<String> expectedIds,
		final List<String> invokedActionIds, final String eventCommand,
		final List<String> expectedCalls)
	{
		RecordingController controller = factory.get();
		ActionRegistry registry = ActionRegistry.ofHandlers(controller);
		assertEquals(expectedIds, registry.ids(), caseName);
		assertEquals(expectedIds.size(), registry.size(), caseName);
		for (String actionId : invokedActionIds)
		{
			assertTrue(registry.contains(actionId), caseName + " must contain " + actionId);
			registry.find(actionId).orElseThrow().actionPerformed(newEvent(eventCommand));
		}
		assertEquals(expectedCalls, controller.recorded(), caseName);
	}

	static Stream<Arguments> controllersWithoutAnnotatedMembersRegisterNothing()
	{
		return Stream.of(Arguments.of("a plain object", new Object()),
			Arguments.of("a class without annotated members", new PlainController()),
			Arguments.of("a string", "not a controller"), Arguments.of("a lambda", (Runnable)() -> {
				// no operation
			}));
	}

	/**
	 * Parameterized test for {@link ActionRegistry#registerHandlers(Object)} with controllers
	 * without {@link MenuAction} annotated members
	 */
	@ParameterizedTest(name = "[{index}] {0} registers no action")
	@MethodSource
	void controllersWithoutAnnotatedMembersRegisterNothing(final String caseName,
		final Object controller)
	{
		ActionRegistry registry = ActionRegistry.ofHandlers(controller);
		assertEquals(0, registry.size(), caseName);
		assertEquals(Set.of(), registry.ids(), caseName);
	}

	/**
	 * Parameterized test for the invalid controllers of
	 * {@link ActionRegistry#registerHandlers(Object)}
	 */
	@ParameterizedTest(name = "[{index}] {0} fails with {1}")
	@CsvSource({ "DUPLICATE_ID, java.lang.IllegalStateException, is registered twice in",
			"DUPLICATE_ID_IN_HIERARCHY, java.lang.IllegalStateException, is registered twice in",
			"DUPLICATE_METHOD_AND_FIELD, java.lang.IllegalStateException, is registered twice in",
			"WRONG_METHOD_SIGNATURE, java.lang.IllegalArgumentException, must have no parameter or a single ActionEvent parameter",
			"TWO_PARAMETERS, java.lang.IllegalArgumentException, must have no parameter or a single ActionEvent parameter",
			"OBJECT_PARAMETER, java.lang.IllegalArgumentException, must have no parameter or a single ActionEvent parameter",
			"WRONG_FIELD_TYPE, java.lang.IllegalArgumentException, must be of the type ActionListener",
			"NULL_FIELD, java.lang.IllegalStateException, is null",
			"BLANK_ID, java.lang.IllegalArgumentException, is empty",
			"EMPTY_ID, java.lang.IllegalArgumentException, is empty" })
	void registerHandlersRejectsInvalidControllers(final InvalidControllerFixture fixture,
		final Class<? extends RuntimeException> expectedType, final String expectedMessageFragment)
	{
		Object controller = fixture.controller();
		RuntimeException exception = assertThrows(expectedType,
			() -> ActionRegistry.ofHandlers(controller));
		assertTrue(exception.getMessage().contains(expectedMessageFragment), "the message '"
			+ exception.getMessage() + "' must contain '" + expectedMessageFragment + "'");
	}

	/**
	 * Parameterized test that an invalid controller names its class in the exception message and
	 * leaves the {@link ActionRegistry} object unchanged
	 */
	@ParameterizedTest(name = "[{index}] the failure of {0} names the controller class")
	@EnumSource(InvalidControllerFixture.class)
	void theExceptionOfAnInvalidControllerNamesTheControllerClass(
		final InvalidControllerFixture fixture)
	{
		Object controller = fixture.controller();
		ActionRegistry registry = ActionRegistry.empty().register("keep", event -> {
			// no operation
		});
		RuntimeException exception = assertThrows(RuntimeException.class,
			() -> registry.registerHandlers(controller));
		assertTrue(exception.getMessage().startsWith("The "), exception.getMessage());
		assertTrue(exception.getMessage().contains(controller.getClass().getName()),
			exception.getMessage());
		assertEquals(Set.of("keep"), registry.ids());
	}

	/**
	 * Parameterized test that the failures of the invoked actions are propagated
	 */
	@ParameterizedTest(name = "[{index}] the action ''{0}'' throws {1}")
	@CsvSource({ "runtimeFailure, java.lang.IllegalArgumentException, boom, false",
			"checkedFailure, java.lang.IllegalStateException, checked, true",
			"errorFailure, java.lang.AssertionError, error, false" })
	void actionFailuresArePropagated(final String actionId,
		final Class<? extends Throwable> expectedType, final String expectedMessage,
		final boolean fromCause)
	{
		ActionRegistry registry = ActionRegistry.ofHandlers(new FailingController());
		ActionListener actionListener = registry.find(actionId).orElseThrow();
		Throwable thrown = assertThrows(expectedType,
			() -> actionListener.actionPerformed(newEvent("x")));
		assertEquals(expectedMessage,
			fromCause ? thrown.getCause().getMessage() : thrown.getMessage());
	}

	/**
	 * Parameterized test for {@link ActionRegistry#find(String)},
	 * {@link ActionRegistry#contains(String)} and {@link ActionRegistry#resolve(String)}
	 */
	@ParameterizedTest(name = "[{index}] the registry with exit and open knows the id ''{0}''")
	@NullSource
	@EmptySource
	@ValueSource(strings = { "exit", "open", "nope", "EXIT", "exit ", " " })
	void findContainsAndResolveAgree(final String actionId)
	{
		ActionListener exit = event -> {
			// no operation
		};
		ActionListener open = event -> {
			// no operation
		};
		ActionRegistry registry = ActionRegistry.empty().register("exit", exit).register("open",
			open);
		boolean expected = "exit".equals(actionId) || "open".equals(actionId);
		assertEquals(expected, registry.contains(actionId));
		assertEquals(expected, registry.find(actionId).isPresent());
		assertEquals(expected, registry.resolve(actionId).isPresent());
		assertEquals(registry.find(actionId), registry.resolve(actionId));
		if (expected)
		{
			assertSame("exit".equals(actionId) ? exit : open,
				registry.find(actionId).orElseThrow());
		}
	}

	static Stream<Arguments> registriesAreBuiltFromTheGivenSources()
	{
		ActionListener exit = event -> {
			// no operation
		};
		ActionListener open = event -> {
			// no operation
		};
		ActionListener save = event -> {
			// no operation
		};
		Map<String, ActionListener> map = new LinkedHashMap<>();
		map.put("open", open);
		map.put("exit", exit);
		return Stream.of(Arguments.of("empty registers nothing", ActionRegistry.empty(), List.of()),
			Arguments.of("of keeps the order of the given map", ActionRegistry.of(map),
				List.of("open", "exit")),
			Arguments.of("of with an empty map registers nothing", ActionRegistry.of(Map.of()),
				List.of()),
			Arguments.of("register appends the new ids",
				ActionRegistry.empty().register("exit", exit).register("open", open),
				List.of("exit", "open")),
			Arguments.of("register of an existing id keeps the order",
				ActionRegistry.empty().register("exit", exit).register("open", open)
					.register("exit", save),
				List.of("exit", "open")),
			Arguments.of("registerAll adds the entries of the other registry",
				ActionRegistry.empty().register("exit", exit)
					.registerAll(ActionRegistry.of(Map.of("save", save))),
				List.of("exit", "save")),
			Arguments.of("registerAll of an empty registry changes nothing",
				ActionRegistry.empty().register("exit", exit).registerAll(ActionRegistry.empty()),
				List.of("exit")),
			Arguments.of("ofHandlers without a controller registers nothing",
				ActionRegistry.ofHandlers(), List.of()),
			Arguments.of(
				"ofHandlers merges the ids of the controllers in order", ActionRegistry
					.ofHandlers(new NoParameterController(), new PublicMethodController()),
				List.of("exit", "save")));
	}

	/**
	 * Parameterized test for the factory and registration methods of {@link ActionRegistry}
	 */
	@ParameterizedTest(name = "[{index}] {0}")
	@MethodSource
	void registriesAreBuiltFromTheGivenSources(final String caseName, final ActionRegistry registry,
		final List<String> expectedIds)
	{
		assertEquals(expectedIds, List.copyOf(registry.ids()), caseName);
		assertEquals(expectedIds.size(), registry.size(), caseName);
		assertThrows(UnsupportedOperationException.class, () -> registry.ids().add("x"), caseName);
	}

	static Stream<Arguments> theLastRegistrationOfAnIdWins()
	{
		ActionListener first = event -> {
			// no operation
		};
		ActionListener second = event -> {
			// no operation
		};
		return Stream.of(
			Arguments.of("register twice",
				ActionRegistry.empty().register("exit", first).register("exit", second), second),
			Arguments.of("register and then registerAll",
				ActionRegistry.empty().register("exit", first)
					.registerAll(ActionRegistry.empty().register("exit", second)),
				second),
			Arguments.of("registerAll and then register",
				ActionRegistry.empty().registerAll(ActionRegistry.empty().register("exit", first))
					.register("exit", second),
				second),
			Arguments.of("of and then register",
				ActionRegistry.of(Map.of("exit", first)).register("exit", second), second),
			Arguments.of("only one registration", ActionRegistry.empty().register("exit", second),
				second));
	}

	/**
	 * Parameterized test that the last registration of an action id wins
	 */
	@ParameterizedTest(name = "[{index}] {0}: the last registration of the id exit wins")
	@MethodSource
	void theLastRegistrationOfAnIdWins(final String caseName, final ActionRegistry registry,
		final ActionListener expected)
	{
		assertEquals(1, registry.size(), caseName);
		assertSame(expected, registry.find("exit").orElseThrow(), caseName);
		assertSame(expected, registry.resolve("exit").orElseThrow(), caseName);
	}

	private static ActionProvider provider(final String... actionIds)
	{
		return (registry, context) -> {
			for (String actionId : actionIds)
			{
				registry.register(actionId, event -> {
					// no operation
				});
			}
		};
	}

	static Stream<Arguments> registerProvidersRegistersTheActionsOfEveryProvider()
	{
		ActionProvider fromContext = (registry, context) -> registry
			.register("show:" + context.require(String.class), event -> {
				// no operation
			});
		return Stream.of(
			Arguments.of("no provider registers nothing", List.of(), ActionContext.empty(),
				List.of()),
			Arguments.of("a provider that registers nothing", List.of(provider()),
				ActionContext.empty(), List.of()),
			Arguments.of("a single provider registers its actions", List.of(provider("a", "b")),
				ActionContext.empty(), List.of("a", "b")),
			Arguments.of("several providers register in order",
				List.of(provider("a"), provider("b"), provider("c")), ActionContext.empty(),
				List.of("a", "b", "c")),
			Arguments.of("the last provider wins for the same id",
				List.of(provider("a"), provider("a")), ActionContext.empty(), List.of("a")),
			Arguments.of("a provider can read the context", List.of(fromContext),
				ActionContext.empty().put("title", "Demo"), List.of("show:Demo")));
	}

	/**
	 * Parameterized test for {@link ActionRegistry#registerProviders(Iterable, ActionContext)}
	 */
	@ParameterizedTest(name = "[{index}] {0}")
	@MethodSource
	void registerProvidersRegistersTheActionsOfEveryProvider(final String caseName,
		final List<ActionProvider> providers, final ActionContext context,
		final List<String> expectedIds)
	{
		ActionRegistry registry = ActionRegistry.empty().registerProviders(providers, context);
		assertEquals(expectedIds, List.copyOf(registry.ids()), caseName);
		assertEquals(expectedIds.size(), registry.size(), caseName);
	}

	/**
	 * Parameterized test that the failure of an {@link ActionProvider} is propagated and leaves the
	 * {@link ActionRegistry} object unchanged
	 */
	@ParameterizedTest(name = "[{index}] a provider that requires {0} fails")
	@ValueSource(classes = { JTree.class, Integer.class, Runnable.class })
	void providerFailuresArePropagated(final Class<?> missingType)
	{
		ActionProvider failing = (registry, context) -> {
			context.require(missingType);
			registry.register("never", event -> {
				// no operation
			});
		};
		ActionRegistry registry = ActionRegistry.empty();
		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> registry
			.registerProviders(List.of(failing), ActionContext.empty().put("title", "Demo")));
		assertTrue(exception.getMessage().contains(missingType.getName()), exception.getMessage());
		assertEquals(0, registry.size());
		assertFalse(registry.contains("never"));
	}

	static Stream<Arguments> loadProvidersFindsNoServiceProvider()
	{
		return Stream.of(Arguments.of("the module layer of the registry", (ClassLoader)null),
			Arguments.of("the class loader of this test",
				ActionRegistryParameterizedTest.class.getClassLoader()),
			Arguments.of("the platform class loader", ClassLoader.getPlatformClassLoader()),
			Arguments.of("the system class loader", ClassLoader.getSystemClassLoader()));
	}

	/**
	 * Parameterized test for {@link ActionRegistry#loadProviders(ActionContext)} and
	 * {@link ActionRegistry#loadProviders(ClassLoader, ActionContext)}, no {@link ActionProvider}
	 * service is declared in this build
	 */
	@ParameterizedTest(name = "[{index}] loadProviders with {0} finds no service provider")
	@MethodSource
	void loadProvidersFindsNoServiceProvider(final String caseName, final ClassLoader classLoader)
	{
		ActionContext context = ActionContext.empty().put("title", "Demo");
		ActionRegistry registry = classLoader == null
			? ActionRegistry.empty().loadProviders(context)
			: ActionRegistry.empty().loadProviders(classLoader, context);
		assertEquals(0, registry.size(), caseName);
		assertEquals(Set.of(), registry.ids(), caseName);
	}

	static Stream<Arguments> nullArgumentsAreRejected()
	{
		ActionListener actionListener = event -> {
			// no operation
		};
		Map<String, ActionListener> nullValue = new HashMap<>();
		nullValue.put("exit", null);
		Map<String, ActionListener> nullKey = new HashMap<>();
		nullKey.put(null, actionListener);
		return Stream.of(
			Arguments.of("of with a null map", (Executable)() -> ActionRegistry.of(null),
				"actions is marked non-null but is null"),
			Arguments.of("of with a map with a null value",
				(Executable)() -> ActionRegistry.of(nullValue),
				"actionListener is marked non-null but is null"),
			Arguments.of("of with a map with a null key",
				(Executable)() -> ActionRegistry.of(nullKey),
				"actionId is marked non-null but is null"),
			Arguments.of("ofHandlers with a null array",
				(Executable)() -> ActionRegistry.ofHandlers((Object[])null),
				"controllers is marked non-null but is null"),
			Arguments.of("ofHandlers with a null controller",
				(Executable)() -> ActionRegistry.ofHandlers((Object)null),
				"controller is marked non-null but is null"),
			Arguments.of("register with a null action id",
				(Executable)() -> ActionRegistry.empty().register(null, actionListener),
				"actionId is marked non-null but is null"),
			Arguments.of("register with a null action listener",
				(Executable)() -> ActionRegistry.empty().register("exit", null),
				"actionListener is marked non-null but is null"),
			Arguments.of("registerAll with a null registry",
				(Executable)() -> ActionRegistry.empty().registerAll(null),
				"other is marked non-null but is null"),
			Arguments.of("registerHandlers with a null controller",
				(Executable)() -> ActionRegistry.empty().registerHandlers(null),
				"controller is marked non-null but is null"),
			Arguments.of("registerProviders with null providers",
				(Executable)() -> ActionRegistry.empty().registerProviders(null,
					ActionContext.empty()),
				"providers is marked non-null but is null"),
			Arguments.of("registerProviders with a null context",
				(Executable)() -> ActionRegistry.empty().registerProviders(List.of(), null),
				"context is marked non-null but is null"),
			Arguments.of("loadProviders with a null context",
				(Executable)() -> ActionRegistry.empty().loadProviders(null),
				"context is marked non-null but is null"),
			Arguments.of("loadProviders with a null class loader",
				(Executable)() -> ActionRegistry.empty().loadProviders(null, ActionContext.empty()),
				"classLoader is marked non-null but is null"));
	}

	/**
	 * Parameterized test for the null argument cases of the {@link ActionRegistry} methods
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
