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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import io.github.astrapi69.swing.menu.enumeration.Anchor;
import io.github.astrapi69.swing.menu.enumeration.MenuType;
import io.github.astrapi69.swing.menu.model.MenuInfo;
import io.github.astrapi69.swing.menu.xml.MenuXmlReader;

/**
 * The unit test class for the class {@link MenuInfoExtensions}
 */
class MenuInfoExtensionsTest
{

	private static MenuInfo item(final String name)
	{
		return MenuInfo.builder().type(MenuType.MENU_ITEM).name(name).build();
	}

	@Test
	void findAndFlatten()
	{
		MenuInfo menuBar = MenuXmlReader.readResource("menubar.xml");
		Optional<MenuInfo> exit = MenuInfoExtensions.find(menuBar, "global.menu.file.exit");
		assertTrue(exit.isPresent());
		assertEquals("Exit", exit.get().getText());
		assertFalse(MenuInfoExtensions.find(menuBar, "nope").isPresent());
		assertFalse(MenuInfoExtensions.find(menuBar, null).isPresent());

		List<MenuInfo> all = MenuInfoExtensions.flatten(menuBar);
		assertEquals(menuBar, all.get(0));
		// 1 bar + 4 menus + 5 + 1 + 4 + 1 children
		assertEquals(16, all.size());
	}

	@Test
	void orderByAnchor()
	{
		MenuInfo first = item("first");
		first.setAnchor(Anchor.FIRST);
		MenuInfo afterB = item("afterB");
		afterB.setAnchor(Anchor.AFTER);
		afterB.setRelativeToMenuId("b");
		MenuInfo beforeA = item("beforeA");
		beforeA.setAnchor(Anchor.BEFORE);
		beforeA.setRelativeToMenuId("a");
		MenuInfo orphan = item("orphan");
		orphan.setAnchor(Anchor.BEFORE);
		orphan.setRelativeToMenuId("missing");
		MenuInfo last = item("last");
		last.setAnchor(Anchor.LAST);

		List<MenuInfo> ordered = MenuInfoExtensions.orderByAnchor(
			List.of(item("a"), afterB, item("b"), last, first, beforeA, orphan, item("c")));
		assertEquals(List.of("first", "beforeA", "a", "b", "afterB", "last", "c", "orphan"),
			ordered.stream().map(MenuInfo::getName).toList());
	}

	@Test
	void orderByAnchorResolvesChains()
	{
		MenuInfo b = item("b");
		b.setAnchor(Anchor.AFTER);
		b.setRelativeToMenuId("a");
		MenuInfo c = item("c");
		c.setAnchor(Anchor.AFTER);
		c.setRelativeToMenuId("b");
		MenuInfo d = item("d");
		d.setAnchor(Anchor.BEFORE);
		d.setRelativeToMenuId("c");
		MenuInfo a = item("a");
		a.setAnchor(Anchor.BEFORE);
		a.setRelativeToMenuId("z");
		MenuInfo cycle1 = item("cycle1");
		cycle1.setAnchor(Anchor.AFTER);
		cycle1.setRelativeToMenuId("cycle2");
		MenuInfo cycle2 = item("cycle2");
		cycle2.setAnchor(Anchor.AFTER);
		cycle2.setRelativeToMenuId("cycle1");

		// document order has the references before their targets
		List<MenuInfo> ordered = MenuInfoExtensions
			.orderByAnchor(List.of(cycle1, d, c, b, a, cycle2, item("z")));
		assertEquals(List.of("a", "b", "d", "c", "z", "cycle1", "cycle2"),
			ordered.stream().map(MenuInfo::getName).toList());
	}

	@Test
	void insertIndex()
	{
		List<String> siblings = List.of("file", "edit", "help");
		assertEquals(3, MenuInfoExtensions.insertIndex(siblings, item("plain")));
		MenuInfo first = item("first");
		first.setAnchor(Anchor.FIRST);
		assertEquals(0, MenuInfoExtensions.insertIndex(siblings, first));
		MenuInfo beforeEdit = item("beforeEdit");
		beforeEdit.setAnchor(Anchor.BEFORE);
		beforeEdit.setRelativeToMenuId("edit");
		assertEquals(1, MenuInfoExtensions.insertIndex(siblings, beforeEdit));
		MenuInfo afterEdit = item("afterEdit");
		afterEdit.setAnchor(Anchor.AFTER);
		afterEdit.setRelativeToMenuId("edit");
		assertEquals(2, MenuInfoExtensions.insertIndex(siblings, afterEdit));
		MenuInfo unknown = item("unknown");
		unknown.setAnchor(Anchor.AFTER);
		unknown.setRelativeToMenuId("nope");
		assertEquals(3, MenuInfoExtensions.insertIndex(siblings, unknown));
		MenuInfo last = item("last");
		last.setAnchor(Anchor.LAST);
		assertEquals(3, MenuInfoExtensions.insertIndex(siblings, last));
	}

	@Test
	void mergePluginContribution()
	{
		MenuInfo base = MenuXmlReader.readResource("menubar.xml");
		MenuInfo contribution = MenuXmlReader.readResource("plugin-contribution.xml");

		MenuInfo merged = MenuInfoExtensions.merge(base, contribution);

		List<String> menus = merged.getChildren().stream().map(MenuInfo::getName).toList();
		assertEquals(List.of("global.menu.file", "global.menu.edit", "global.menu.view",
			"plugin.menu", "global.menu.help"), menus);
		MenuInfo file = merged.getChildren().get(0);
		// the existing file menu keeps its attributes
		assertEquals("File", file.getText());
		List<String> fileItems = file.getChildren().stream().map(MenuInfo::getName).toList();
		assertEquals(4, fileItems.indexOf("plugin.file.export"));
		assertEquals(5, fileItems.indexOf("global.menu.file.exit"));
		assertEquals(6, file.getChildren().size());
	}
}
