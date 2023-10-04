package io.github.astrapi69.swing.menu.model;

import io.github.astrapi69.swing.menu.MenuExtensions;
import io.github.astrapi69.swing.menu.enumeration.BaseMenuId;
import io.github.astrapi69.throwable.RuntimeExceptionDecorator;
import io.github.astrapi69.jaxb.ObjectToXmlExtensions;
import io.github.astrapi69.jaxb.XmlToObjectExtensions;
import org.junit.jupiter.api.Test;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import static org.junit.jupiter.api.Assertions.*;

class MenuItemInfoTest
{

	@Test
	public void test()
	{
		MenuItemInfo actual;
		MenuItemInfo expected;
		MenuInfo menuInfo;
		//
		menuInfo = MenuInfo.builder().mnemonic(MenuExtensions.toMnemonic('E'))
			.keyStrokeInfo(
				KeyStrokeInfo.builder().keyCode(KeyEvent.VK_F4).modifiers(InputEvent.ALT_DOWN_MASK)
					.keystrokeAsString("alt pressed F4").onKeyRelease(false).build())
			.text("Exit").text("Exit").name(BaseMenuId.EXIT.propertiesKey()).build();
		actual = MenuItemInfo.builder()
			.menuInfo(menuInfo)
			.actionCommand("foo-action-command")
			.build();
		String xml = RuntimeExceptionDecorator.decorate(() -> ObjectToXmlExtensions.toXml(actual));
		assertNotNull(xml);
		System.out.println(xml);
		expected = RuntimeExceptionDecorator.decorate(() -> XmlToObjectExtensions.toObject(xml, actual.getClass()));
		assertEquals(actual, expected);
	}
}