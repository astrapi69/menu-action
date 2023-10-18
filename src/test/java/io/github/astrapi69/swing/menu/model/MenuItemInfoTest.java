package io.github.astrapi69.swing.menu.model;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import org.junit.jupiter.api.Test;

import io.github.astrapi69.swing.menu.MenuExtensions;
import io.github.astrapi69.swing.menu.enumeration.BaseMenuId;
import io.github.astrapi69.throwable.RuntimeExceptionDecorator;
import io.github.astrapi69.xstream.ObjectToXmlExtensions;
import io.github.astrapi69.xstream.XmlToObjectExtensions;

class MenuItemInfoTest
{

	@Test
	public void test()
	{
		MenuItemInfo actual;
		MenuItemInfo expected;

		actual = MenuItemInfo.builder().mnemonic(MenuExtensions.toMnemonic('E'))
			.keyStrokeInfo(
				KeyStrokeInfo.builder().keyCode(KeyEvent.VK_F4).modifiers(InputEvent.ALT_DOWN_MASK)
					.keystrokeAsString("alt pressed F4").onKeyRelease(false).build())
			.text("Exit").text("Exit").name(BaseMenuId.EXIT.propertiesKey())
			.actionCommand("foo-action-command").build();
		String xml = RuntimeExceptionDecorator.decorate(() -> ObjectToXmlExtensions.toXml(actual));
		assertNotNull(xml);
		expected = RuntimeExceptionDecorator.decorate(() -> XmlToObjectExtensions.toObject(xml));
		assertEquals(actual, expected);
	}
}