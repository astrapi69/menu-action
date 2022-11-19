/**
 * The MIT License
 *
 * Copyright (C) 2021 Asterios Raptis
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
package io.github.astrapi69.swing.menu.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import org.junit.jupiter.api.Test;

import io.github.astrapi69.swing.menu.BaseMenuId;
import io.github.astrapi69.swing.menu.KeyStrokeInfo;
import io.github.astrapi69.swing.menu.MenuExtensions;
import io.github.astrapi69.throwable.RuntimeExceptionDecorator;
import io.github.astrapi69.xml.jackson.ObjectToXmlExtensions;
import io.github.astrapi69.xml.jackson.XmlToObjectExtensions;

public class MenuInfoTest
{

	@Test
	public void test()
	{
		MenuInfo actual;
		MenuInfo expected;
		//
		actual = MenuInfo.builder().mnemonic(MenuExtensions.toMnemonic('E'))
			.keyStrokeInfo(
				KeyStrokeInfo.builder().keyCode(KeyEvent.VK_F4).modifiers(InputEvent.ALT_DOWN_MASK)
					.keystrokeAsString("alt pressed F4").onKeyRelease(false).build())
			.text("Exit").label("Exit").actionCommand(BaseMenuId.EXIT.propertiesKey())
			.actionId(BaseMenuId.EXIT.propertiesKey()).name(BaseMenuId.EXIT.propertiesKey())
			.build();
		String xml = RuntimeExceptionDecorator.decorate(() -> ObjectToXmlExtensions.toXml(actual));
		assertNotNull(xml);
		expected = RuntimeExceptionDecorator
			.decorate(() -> XmlToObjectExtensions.toObject(xml, MenuInfo.class));
		assertEquals(actual, expected);
	}
}
