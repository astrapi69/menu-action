package io.github.astrapi69.swing.menu.model;

import io.github.astrapi69.swing.menu.BaseMenuId;
import io.github.astrapi69.swing.menu.KeyStrokeInfo;
import io.github.astrapi69.swing.menu.MenuExtensions;
import io.github.astrapi69.throwable.RuntimeExceptionDecorator;
import io.github.astrapi69.xml.jackson.ObjectToXmlExtensions;
import io.github.astrapi69.xml.jackson.XmlToObjectExtensions;
import org.junit.jupiter.api.Test;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MenuInfoTest {

    @Test
    public void test() {
        MenuInfo actual;
        MenuInfo expected;
        //
        actual = MenuInfo.builder()
                .mnemonic(MenuExtensions.toMnemonic('E'))
                .keyStrokeInfo(KeyStrokeInfo.builder().keyCode(KeyEvent.VK_F4)
                        .modifiers(InputEvent.ALT_DOWN_MASK)
                        .keystrokeAsString("alt pressed F4")
                        .onKeyRelease(false)
                        .build())
                .text("Exit")
                .label("Exit")
                .actionCommand(BaseMenuId.EXIT.propertiesKey())
                .actionId(BaseMenuId.EXIT.propertiesKey())
                .name(BaseMenuId.EXIT.propertiesKey())
                .build();
        String xml = RuntimeExceptionDecorator.decorate(()-> ObjectToXmlExtensions.toXml(actual));
        assertNotNull(xml);
        expected = RuntimeExceptionDecorator.decorate(()-> XmlToObjectExtensions.toObject(xml, MenuInfo.class));
        assertEquals(actual, expected);
    }
}
