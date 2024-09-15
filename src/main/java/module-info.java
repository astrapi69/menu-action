/**
 * The MIT License
 *
 * Copyright (C) 2021 Asterios Raptis
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
module io.github.astrapisixtynine.menu.action
{
	requires static lombok;
	requires java.desktop;
	requires java.logging;
	requires io.github.astrapisixtynine.throwable;
	requires io.github.astrapisixtynine.gen.tree;
	requires io.github.astrapisixtynine.silly.collection;
	requires io.github.astrapisixtynine.jobj.core;
	requires id.generate;
	requires model.data;
	requires jobj.reflect.main;

	exports io.github.astrapi69.browser;
	exports io.github.astrapi69.swing.action;
	exports io.github.astrapi69.swing.listener;
	exports io.github.astrapi69.swing.listener.document;
	exports io.github.astrapi69.swing.listener.item;
	exports io.github.astrapi69.swing.listener.mouse;
	exports io.github.astrapi69.swing.menu;
	exports io.github.astrapi69.swing.menu.enumeration;
	exports io.github.astrapi69.swing.menu.factory;
	exports io.github.astrapi69.swing.menu.model;
	exports io.github.astrapi69.swing.menu.model.transform;
	exports io.github.astrapi69.swing.menu.popup.listener;
	exports io.github.astrapi69.swing.plaf;
	exports io.github.astrapi69.swing.plaf.action;

}
