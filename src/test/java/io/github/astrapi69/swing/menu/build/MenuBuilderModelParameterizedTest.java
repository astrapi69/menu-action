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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import io.github.astrapi69.model.BaseModel;
import io.github.astrapi69.model.api.IModel;
import io.github.astrapi69.swing.menu.enumeration.MenuType;
import io.github.astrapi69.swing.menu.model.MenuInfo;

/**
 * The parameterized unit test class for the value conversion and the model binding of the class
 * {@link MenuBuilder}
 */
class MenuBuilderModelParameterizedTest
{

	/** The enum for the model values of the radio button menu items */
	enum ViewMode
	{
		DESKTOP, PANEL
	}

	private static IModel<?> newModel(final String modelObjectType)
	{
		return switch (modelObjectType)
		{
			case "ENUM" -> BaseModel.of(ViewMode.DESKTOP);
			case "BOOLEAN" -> BaseModel.of(Boolean.TRUE);
			case "INTEGER" -> BaseModel.of(Integer.valueOf(2));
			case "LONG" -> BaseModel.of(Long.valueOf(2L));
			case "DOUBLE" -> BaseModel.of(Double.valueOf(2d));
			case "STRING" -> BaseModel.of("s");
			case "NULL_OBJECT" -> BaseModel.of();
			default -> throw new IllegalArgumentException(modelObjectType);
		};
	}

	private static MenuBuilder newBuilder(final Map<String, ? extends IModel<?>> models)
	{
		return new MenuBuilder().withMissingActionPolicy(MissingActionPolicy.IGNORE)
			.withModels(models);
	}

	/**
	 * Parameterized test for {@link MenuBuilder#convertValue(IModel, String)}
	 */
	@ParameterizedTest(name = "convertValue of a {0} model with the value {1} gives the {2} {3}")
	@CsvSource(nullValues = "NULL", value = { "ENUM,        PANEL, ViewMode, PANEL",
			"BOOLEAN,     true,  Boolean,  true", "BOOLEAN,     false, Boolean,  false",
			"BOOLEAN,     yes,   Boolean,  false", "INTEGER,     7,     Integer,  7",
			"LONG,        7,     Long,     7", "DOUBLE,      1.5,   Double,   1.5",
			"STRING,      x,     String,   x", "NULL_OBJECT, x,     String,   x",
			"INTEGER,     NULL,  NULL,     NULL" })
	void convertValueWithEveryModelObjectType(final String modelObjectType, final String value,
		final String expectedType, final String expectedValue)
	{
		IModel<?> model = newModel(modelObjectType);

		Object converted = MenuBuilder.convertValue(model, value);

		if (expectedType == null)
		{
			assertNull(converted);
			return;
		}
		assertEquals(expectedType, converted.getClass().getSimpleName());
		assertEquals(expectedValue, String.valueOf(converted));
	}

	/**
	 * Parameterized test for {@link MenuBuilder#convertValue(IModel, String)} with values that can
	 * not be converted to the type of the model object
	 */
	@ParameterizedTest(name = "convertValue of a {0} model with the invalid value {1} throws {2}")
	@CsvSource({ "ENUM,    NOPE, IllegalArgumentException, No enum constant",
			"INTEGER, x,    NumberFormatException,    For input string",
			"LONG,    x,    NumberFormatException,    For input string",
			"DOUBLE,  x,    NumberFormatException,    For input string" })
	void convertValueWithInvalidValues(final String modelObjectType, final String value,
		final String expectedExceptionType, final String expectedMessagePart)
	{
		IModel<?> model = newModel(modelObjectType);

		RuntimeException exception = assertThrows(RuntimeException.class,
			() -> MenuBuilder.convertValue(model, value));

		assertEquals(expectedExceptionType, exception.getClass().getSimpleName());
		assertTrue(exception.getMessage().contains(expectedMessagePart), exception.getMessage());
	}

	private static JCheckBoxMenuItem buildCheckBox(final MenuBuilder builder)
	{
		return (JCheckBoxMenuItem)builder
			.buildMenuComponent(MenuInfo.builder().type(MenuType.CHECK_BOX_MENU_ITEM)
				.name("view.statusbar").text("Statusbar").model("statusbar").build());
	}

	/**
	 * Parameterized test for the model binding of a check box menu item
	 */
	@ParameterizedTest(name = "a check box with the model value {0} and {1} clicks")
	@CsvSource(nullValues = "NULL", value = { "true,  0, true,  true", "true,  1, false, false",
			"true,  2, true,  true", "true,  3, false, false", "false, 0, false, false",
			"false, 1, true,  true", "false, 2, false, false", "NULL,  0, false, NULL",
			"NULL,  1, true,  true" })
	void checkBoxModelBinding(final Boolean modelValue, final int clicks,
		final boolean expectedSelected, final Boolean expectedModelValue)
	{
		IModel<Object> statusbar = BaseModel.of();
		statusbar.setObject(modelValue);
		JCheckBoxMenuItem checkBox = buildCheckBox(newBuilder(Map.of("statusbar", statusbar)));

		for (int i = 0; i < clicks; i++)
		{
			checkBox.doClick();
		}

		assertEquals(expectedSelected, checkBox.isSelected());
		assertEquals(expectedModelValue, statusbar.getObject());
	}

	/**
	 * Parameterized test for {@link MenuBuilder#updateFromModels()} with a check box menu item
	 */
	@ParameterizedTest(name = "updateFromModels with the model value {0} that changed to {1}")
	@CsvSource(nullValues = "NULL", value = { "true,  false, true,  false, false",
			"false, true,  false, true,  true", "true,  true,  true,  true,  true",
			"true,  NULL,  true,  false, NULL", "false, NULL,  false, false, NULL" })
	void checkBoxUpdateFromModels(final Boolean modelValue, final Boolean newModelValue,
		final boolean expectedSelectedBeforeUpdate, final boolean expectedSelectedAfterUpdate,
		final Boolean expectedModelValueAfterUpdate)
	{
		IModel<Object> statusbar = BaseModel.of();
		statusbar.setObject(modelValue);
		MenuBuilder builder = newBuilder(Map.of("statusbar", statusbar));
		JCheckBoxMenuItem checkBox = buildCheckBox(builder);

		statusbar.setObject(newModelValue);

		// the check box is only updated on demand
		assertEquals(expectedSelectedBeforeUpdate, checkBox.isSelected());
		builder.updateFromModels();
		assertEquals(expectedSelectedAfterUpdate, checkBox.isSelected());
		// updateFromModels() only pulls the model into the button, it never writes back, so the
		// model object stays exactly what it was set to
		assertEquals(expectedModelValueAfterUpdate, statusbar.getObject());
	}

	private static JMenu buildRadioGroup(final MenuBuilder builder)
	{
		return builder
			.buildMenu(MenuInfo.builder().type(MenuType.MENU).name("view").text("View").build()
				.addChild(MenuInfo.builder().type(MenuType.RADIO_BUTTON_MENU_ITEM)
					.name("view.mode.desktop").text("Desktop").group("view.mode").model("viewMode")
					.value("DESKTOP").build())
				.addChild(MenuInfo.builder().type(MenuType.RADIO_BUTTON_MENU_ITEM)
					.name("view.mode.panel").text("Panel").group("view.mode").model("viewMode")
					.value("PANEL").build()));
	}

	private static int selectedIndex(final JMenu menu)
	{
		for (int i = 0; i < menu.getItemCount(); i++)
		{
			if (menu.getItem(i).isSelected())
			{
				return i;
			}
		}
		return -1;
	}

	/**
	 * Parameterized test for the model binding of a radio button group
	 */
	@ParameterizedTest(name = "a radio group with the model value {0} and a click on the item {1}")
	@CsvSource(nullValues = "NULL", value = { "DESKTOP, NULL, 0,  DESKTOP, ViewMode",
			"PANEL,   NULL, 1,  PANEL,   ViewMode", "DESKTOP, 1,    1,  PANEL,   ViewMode",
			"PANEL,   0,    0,  DESKTOP, ViewMode", "DESKTOP, 0,    0,  DESKTOP, ViewMode",
			"NULL,    NULL, -1, NULL,    NULL", "NULL,    0,    0,  DESKTOP, String" })
	void radioGroupModelBinding(final String modelValue, final Integer clickedIndex,
		final int expectedSelectedIndex, final String expectedModelValue,
		final String expectedModelType)
	{
		IModel<Object> viewMode = BaseModel.of();
		if (modelValue != null)
		{
			viewMode.setObject(ViewMode.valueOf(modelValue));
		}
		MenuBuilder builder = newBuilder(Map.of("viewMode", viewMode));
		JMenu menu = buildRadioGroup(builder);
		assertEquals(2, builder.getButtonGroup("view.mode").orElseThrow().getButtonCount());
		assertTrue(builder.getButtonGroup("unknown.group").isEmpty());

		if (clickedIndex != null)
		{
			menu.getItem(clickedIndex).doClick();
		}

		assertEquals(expectedSelectedIndex, selectedIndex(menu));
		assertEquals(JRadioButtonMenuItem.class, menu.getItem(0).getClass());
		Object modelObject = viewMode.getObject();
		if (expectedModelValue == null)
		{
			assertNull(modelObject);
			return;
		}
		assertEquals(expectedModelValue, String.valueOf(modelObject));
		assertEquals(expectedModelType, modelObject.getClass().getSimpleName());
	}

	/**
	 * Parameterized test for {@link MenuBuilder#updateFromModels()} with a radio button group
	 */
	@ParameterizedTest(name = "updateFromModels with the model value {0} selects the item {1}")
	@CsvSource(nullValues = "NULL", value = { "DESKTOP, 0", "PANEL, 1", "NULL, 0" })
	void radioGroupUpdateFromModels(final String newModelValue, final int expectedSelectedIndex)
	{
		IModel<Object> viewMode = BaseModel.of();
		viewMode.setObject(ViewMode.DESKTOP);
		MenuBuilder builder = newBuilder(Map.of("viewMode", viewMode));
		JMenu menu = buildRadioGroup(builder);
		assertEquals(0, selectedIndex(menu));

		viewMode.setObject(newModelValue == null ? null : ViewMode.valueOf(newModelValue));
		builder.updateFromModels();

		// a button of a button group can not be deselected, so the selection of the group stays
		// on the last selected item if the model object is null
		assertEquals(expectedSelectedIndex, selectedIndex(menu));
	}
}
