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

/**
 * The service provider interface {@link ActionProvider} lets modules and plugins contribute actions
 * to an {@link ActionRegistry}. Implementations are discovered with the
 * {@link java.util.ServiceLoader}, so a plugin declares in its module descriptor
 *
 * <pre>
 * provides io.github.astrapi69.swing.menu.build.ActionProvider with my.plugin.MyPluginActions;
 * </pre>
 *
 * or on the classpath in the file
 * {@code META-INF/services/io.github.astrapi69.swing.menu.build.ActionProvider}. The application
 * collects the providers with {@link ActionRegistry#loadProviders(ActionContext)}
 */
public interface ActionProvider
{

	/**
	 * Registers the actions of this provider in the given {@link ActionRegistry}
	 *
	 * @param registry
	 *            the {@link ActionRegistry} to register the actions in
	 * @param context
	 *            the {@link ActionContext} with the application objects like the frame or the
	 *            models
	 */
	void registerActions(ActionRegistry registry, ActionContext context);
}
