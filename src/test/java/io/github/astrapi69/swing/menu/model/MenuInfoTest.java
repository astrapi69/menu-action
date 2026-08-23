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
package io.github.astrapi69.swing.menu.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.junit.jupiter.api.Test;

import io.github.astrapi69.swing.menu.MenuExtensions;
import io.github.astrapi69.swing.menu.enumeration.Anchor;
import io.github.astrapi69.swing.menu.enumeration.BaseMenuId;
import io.github.astrapi69.swing.menu.enumeration.MenuType;
import io.github.astrapi69.swing.menu.xml.MenuXmlReader;
import io.github.astrapi69.swing.menu.xml.MenuXmlWriter;

/**
 * The unit test class for the class {@link MenuInfo}
 */
class MenuInfoTest
{

	private static MenuInfo newFullMenuInfo()
	{
		return MenuInfo.builder().name("id").text("Text").textKey("text.key").toolTip("tip")
			.toolTipKey("tip.key").mnemonic(MenuExtensions.toMnemonic('T'))
			.keyStrokeInfo(KeyStrokeInfo.builder().keystrokeAsString("ctrl pressed T").build())
			.type(MenuType.CHECK_BOX_MENU_ITEM).anchor(Anchor.AFTER).relativeToMenuId("other")
			.actionCommand("cmd").actionId("action.id").enabled(true).visible(true).selected(true)
			.group("group").icon("icon.png").model("model.key").value("model-value").showText(true)
			.floatable(false).rollover(true).accessibleName("acc-name")
			.accessibleDescription("acc-desc").build();
	}

	@Test
	void xmlRoundTrip()
	{
		MenuInfo expected = MenuInfo.builder().type(MenuType.MENU_ITEM)
			.mnemonic(MenuExtensions.toMnemonic('E'))
			.keyStrokeInfo(KeyStrokeInfo.toKeyStrokeInfo(KeyStroke.getKeyStroke("alt F4")))
			.text("Exit").name(BaseMenuId.EXIT.propertiesKey()).build();
		String xml = MenuXmlWriter.toXml(expected);
		assertNotNull(xml);
		assertEquals(expected, MenuXmlReader.fromXml(xml));
	}

	@Test
	void childrenAndConversion()
	{
		MenuInfo menu = MenuInfo.builder().type(MenuType.MENU).name("m").build();
		assertFalse(menu.hasChildren());
		menu.setChildren(null);
		assertFalse(menu.hasChildren());
		menu.addChild(MenuInfo.builder().type(MenuType.MENU_ITEM).name("i").text("I").build());
		assertTrue(menu.hasChildren());

		boolean[] clicked = { false };
		MenuItemInfo itemInfo = menu.getChildren().get(0).toMenuItemInfo(e -> clicked[0] = true);
		JMenuItem item = itemInfo.toJMenuItem();
		assertEquals("i", item.getName());
		item.doClick();
		assertTrue(clicked[0]);

		MenuInfo copy = menu.toBuilder().build();
		assertEquals(menu, copy);
	}

	@Test
	void equalsAndHashCodeWithAllFields()
	{
		MenuInfo first = newFullMenuInfo();
		MenuInfo second = newFullMenuInfo();
		assertEquals(first, first);
		assertEquals(first, second);
		assertEquals(second, first);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, null);
		assertNotEquals(first, "MenuInfo");

		assertNotEquals(first, first.toBuilder().visible(false).build());
		assertNotEquals(first, first.toBuilder().visible(null).build());
		assertNotEquals(first, first.toBuilder().accessibleName("other-name").build());
		assertNotEquals(first, first.toBuilder().accessibleDescription("other-desc").build());
		assertNotEquals(first, first.toBuilder().toolTipKey("other.key").build());
		assertNotEquals(first, first.toBuilder().model("other.model").build());
		assertNotEquals(first, first.toBuilder().value("other-value").build());
		assertNotEquals(first, first.toBuilder().showText(false).build());
		assertNotEquals(first, first.toBuilder().floatable(true).build());
		assertNotEquals(first, first.toBuilder().rollover(false).build());
		assertNotEquals(first, first.toBuilder().textKey("other.text.key").build());
		assertNotEquals(first, first.toBuilder().group("other-group").build());
		assertNotEquals(first, first.toBuilder().icon("other.png").build());
		assertNotEquals(first, first.toBuilder().actionId("other.action").build());
		assertNotEquals(first, first.toBuilder().selected(false).build());
		assertNotEquals(first, first.toBuilder().enabled(false).build());
		assertNotEquals(first,
			first.toBuilder().children(List.of(MenuInfo.builder().name("child").build())).build());
	}

	@Test
	void settersOfNewFields()
	{
		MenuInfo info = new MenuInfo();
		info.setVisible(false);
		info.setToolTipKey("tip.key");
		info.setModel("model.key");
		info.setValue("model-value");
		info.setShowText(true);
		info.setFloatable(false);
		info.setRollover(true);
		info.setAccessibleName("acc-name");
		info.setAccessibleDescription("acc-desc");
		assertEquals(Boolean.FALSE, info.getVisible());
		assertEquals("tip.key", info.getToolTipKey());
		assertEquals("model.key", info.getModel());
		assertEquals("model-value", info.getValue());
		assertEquals(Boolean.TRUE, info.getShowText());
		assertEquals(Boolean.FALSE, info.getFloatable());
		assertEquals(Boolean.TRUE, info.getRollover());
		assertEquals("acc-name", info.getAccessibleName());
		assertEquals("acc-desc", info.getAccessibleDescription());
	}

	@Test
	void toStringWithAllFields()
	{
		String string = newFullMenuInfo().toString();
		assertTrue(string.startsWith("MenuInfo("), string);
		assertTrue(string.contains("name=id"), string);
		assertTrue(string.contains("text=Text"), string);
		assertTrue(string.contains("textKey=text.key"), string);
		assertTrue(string.contains("toolTip=tip"), string);
		assertTrue(string.contains("toolTipKey=tip.key"), string);
		assertTrue(string.contains("type=CHECK_BOX_MENU_ITEM"), string);
		assertTrue(string.contains("anchor=AFTER"), string);
		assertTrue(string.contains("relativeToMenuId=other"), string);
		assertTrue(string.contains("actionCommand=cmd"), string);
		assertTrue(string.contains("actionId=action.id"), string);
		assertTrue(string.contains("enabled=true"), string);
		assertTrue(string.contains("visible=true"), string);
		assertTrue(string.contains("selected=true"), string);
		assertTrue(string.contains("group=group"), string);
		assertTrue(string.contains("icon=icon.png"), string);
		assertTrue(string.contains("model=model.key"), string);
		assertTrue(string.contains("value=model-value"), string);
		assertTrue(string.contains("showText=true"), string);
		assertTrue(string.contains("floatable=false"), string);
		assertTrue(string.contains("rollover=true"), string);
		assertTrue(string.contains("accessibleName=acc-name"), string);
		assertTrue(string.contains("accessibleDescription=acc-desc"), string);
	}

	@Test
	void toMenuItemInfoCopiesNewFields()
	{
		MenuInfo menuInfo = MenuInfo.builder().name("i").visible(true).accessibleName("acc-name")
			.accessibleDescription("acc-desc").build();
		MenuItemInfo itemInfo = menuInfo.toMenuItemInfo(e -> {
		});
		assertEquals(Boolean.TRUE, itemInfo.getVisible());
		assertEquals("acc-name", itemInfo.getAccessibleName());
		assertEquals("acc-desc", itemInfo.getAccessibleDescription());
	}
}
