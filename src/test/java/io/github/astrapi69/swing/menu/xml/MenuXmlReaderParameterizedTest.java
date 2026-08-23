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
package io.github.astrapi69.swing.menu.xml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringReader;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.astrapi69.swing.menu.model.MenuInfo;

/**
 * The parameterized unit test class for the class {@link MenuXmlReader}
 */
class MenuXmlReaderParameterizedTest
{

	/**
	 * The pattern of a validation error, see {@code line:column: message}; the line and column are
	 * {@code -1} when the underlying {@link javax.xml.validation.Validator} validates a
	 * {@link javax.xml.transform.dom.DOMSource} object, which does not carry real locator
	 * information
	 */
	private static final String ERROR_PATTERN = "-?\\d+:-?\\d+: .+";

	@ParameterizedTest(name = "[{index}] the invalid document {0} is rejected")
	@ValueSource(strings = { "<foo id=\"x\"/>", "<menus/>",
			"<menu id=\"m\"><unknown id=\"u\"/></menu>",
			"<item id=\"x\" accelerator=\"not a keystroke\"/>",
			"<item id=\"x\" accelerator=\"ctrl NOPE\"/>", "<item id=\"x\" anchor=\"MIDDLE\"/>",
			"<item id=\"x\" anchor=\"between\"/>", "<item id=\"x\" mnemonic=\"abc\"/>",
			"<item id=\"x\" mnemonic=\"12x\"/>", "<menu id=\"x\">", "<menu id=\"x\"></item>",
			"<menu id=\"x\"/><menu id=\"y\"/>", "not xml at all", "<menu id=x/>",
			"<!DOCTYPE menu><menu id=\"x\"/>",
			"<!DOCTYPE menu [<!ENTITY xxe SYSTEM \"file:///etc/passwd\">]><menu id=\"x\" text=\"&xxe;\"/>" })
	void invalidDocumentIsRejected(final String xml)
	{
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
			() -> MenuXmlReader.fromXml(xml));
		assertNotNull(exception.getMessage());
		assertFalse(exception.getMessage().isBlank());
	}

	@ParameterizedTest(name = "[{index}] the invalid document {0} is rejected with a message that contains ''{1}''")
	@CsvSource(delimiter = '|', value = { "<foo id=\"x\"/> | Unknown menu element <foo>",
			"<menu id=\"m\"><unknown/></menu> | Unknown menu element <unknown>",
			"<item id=\"x\" accelerator=\"not a keystroke\"/> | Invalid accelerator 'not a keystroke' in element <item id=\"x\">",
			"<item id=\"x\" anchor=\"MIDDLE\"/> | Invalid anchor 'MIDDLE' in element <item id=\"x\">",
			"<item anchor=\"MIDDLE\"/> | in element <item>",
			"<item id=\"x\" mnemonic=\"abc\"/> | Invalid mnemonic 'abc' in element <item id=\"x\">",
			"<menu id=\"x\"> | Invalid menu xml",
			"<!DOCTYPE menu><menu id=\"x\"/> | Invalid menu xml" })
	void invalidDocumentIsRejectedWithTheExpectedMessage(final String xml,
		final String expectedMessagePart)
	{
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
			() -> MenuXmlReader.fromXml(xml));
		assertTrue(exception.getMessage().contains(expectedMessagePart), "the message '"
			+ exception.getMessage() + "' does not contain '" + expectedMessagePart + "'");
	}

	@ParameterizedTest(name = "[{index}] the blank document ''{0}'' is rejected")
	@EmptySource
	@ValueSource(strings = { " ", "\n\t", "   \n   " })
	void blankDocumentIsRejected(final String xml)
	{
		assertThrows(IllegalArgumentException.class, () -> MenuXmlReader.fromXml(xml));
		assertThrows(IllegalArgumentException.class, () -> MenuXmlReader.readAll(xml));
	}

	static Stream<Arguments> nullArguments()
	{
		return Stream.of(Arguments.of("fromXml", (Executable)() -> MenuXmlReader.fromXml(null)),
			Arguments.of("readAll", (Executable)() -> MenuXmlReader.readAll((String)null)),
			Arguments.of("read(Reader)", (Executable)() -> MenuXmlReader.read((StringReader)null)),
			Arguments.of("readResource", (Executable)() -> MenuXmlReader.readResource(null)),
			Arguments.of("readValidated",
				(Executable)() -> MenuXmlReader.readValidated((String)null)),
			Arguments.of("validate", (Executable)() -> MenuXmlReader.validate((String)null)),
			Arguments.of("toMenuInfo", (Executable)() -> MenuXmlReader.toMenuInfo(null)));
	}

	@ParameterizedTest(name = "[{index}] {0} rejects a null argument")
	@MethodSource("nullArguments")
	void nullArgumentIsRejected(final String methodName, final Executable executable)
	{
		NullPointerException exception = assertThrows(NullPointerException.class, executable);
		assertNotNull(methodName);
		assertNotNull(exception);
	}

	@ParameterizedTest(name = "[{index}] a missing resource {0} is rejected")
	@ValueSource(strings = { "missing.xml", "/missing.xml", "does/not/exist.xml" })
	void missingResourceIsRejected(final String resource)
	{
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
			() -> MenuXmlReader.readResource(resource));
		assertTrue(exception.getMessage().contains("Menu xml resource not found"));
	}

	@ParameterizedTest(name = "[{index}] the document {0} is valid={1}")
	@CsvSource(delimiter = '|', value = { "<menu id=\"m\"/> | true",
			"<menubar id=\"b\"><menu id=\"m\" text=\"M\"><item id=\"i\"/></menu></menubar> | true",
			"<menubar id=\"b\"><menu id=\"m\"><separator/><checkbox id=\"c\" selected=\"true\"/></menu></menubar> | true",
			"<toolbar id=\"t\" floatable=\"false\" rollover=\"true\"><item id=\"i\"/><separator id=\"s\"/></toolbar> | true",
			"<popup id=\"p\"><radio id=\"r\" group=\"g\" model=\"m\" value=\"v\"/></popup> | true",
			"<tray id=\"tr\"><item id=\"i\" accelerator=\"ctrl S\"/></tray> | true",
			"<menus><menubar id=\"b\"/><popup id=\"p\"/><toolbar id=\"t\"/></menus> | true",
			"<menu id=\"m\" anchor=\"BEFORE\" relativeTo=\"other\"/> | true", "<menu/> | false",
			"<foo id=\"x\"/> | false", "<item id=\"i\"/> | false", "<separator id=\"s\"/> | false",
			"<menu id=\"m\"><foo/></menu> | false", "<menu id=\"m\" enabled=\"yes\"/> | false",
			"<menu id=\"m\" anchor=\"MIDDLE\"/> | false",
			"<toolbar id=\"t\"><menu id=\"m\"/></toolbar> | false",
			"<menubar id=\"b\" mnemonic=\"B\"/> | false", "<menu id=\"m\"> | false",
			"<menus><foo/></menus> | false" })
	void validateReportsTheExpectedResult(final String xml, final boolean valid)
	{
		List<String> errors = MenuXmlReader.validate(xml);
		if (valid)
		{
			assertTrue(errors.isEmpty(), "expected no error but got " + errors);
			if (xml.startsWith("<" + MenuXmlElements.MENUS))
			{
				// a document with several roots is read with readAll
				assertFalse(MenuXmlReader.readAll(xml).isEmpty());
			}
			else
			{
				assertNotNull(MenuXmlReader.readValidated(xml));
			}
		}
		else
		{
			assertFalse(errors.isEmpty(), "expected an error for " + xml);
			for (String error : errors)
			{
				// a malformed (not well formed) document can not be schema validated at all, the
				// parser failure is reported as a single "Invalid menu xml" message instead of the
				// usual line:column: message of a schema validation error
				assertTrue(error.matches(ERROR_PATTERN) || error.startsWith("Invalid menu xml"),
					"the error '" + error
						+ "' does not match the pattern line:column: message and is not a parse failure message");
			}
			IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> MenuXmlReader.readValidated(xml));
			assertTrue(exception.getMessage().startsWith("Invalid menu xml"));
		}
	}

	@ParameterizedTest(name = "[{index}] the resource {0} is valid")
	@CsvSource({ "menubar.xml, true", "popup.xml, true", "toolbar.xml, true",
			"plugin-contribution.xml, true", "menus.xml, false" })
	void resourceIsValid(final String resource, final boolean singleRoot)
	{
		List<String> errors = MenuXmlReader
			.validate(MenuXmlReaderParameterizedTest.class.getResourceAsStream("/" + resource));
		assertTrue(errors.isEmpty(), resource + ": " + errors);
		if (singleRoot)
		{
			assertNotNull(MenuXmlReader.readValidatedResource(resource));
		}
		else
		{
			assertFalse(MenuXmlReader
				.readAll(MenuXmlReaderParameterizedTest.class.getResourceAsStream("/" + resource))
				.isEmpty());
		}
	}

	/**
	 * Parameterized test that only the exact lexical values {@code true}, {@code false}, {@code 1}
	 * and {@code 0} (the lexical space of {@code xs:boolean}) are accepted for a boolean attribute
	 */
	@ParameterizedTest(name = "[{index}] the boolean attribute value \"{0}\" is read as {1}")
	@CsvSource({ "true, true", "1, true", "false, false", "0, false" })
	void booleanAttributeValuesAreReadStrictly(final String xmlValue, final boolean expected)
	{
		MenuInfo menuInfo = MenuXmlReader.fromXml("<item id=\"i\" enabled=\"" + xmlValue + "\"/>");
		assertEquals(expected, menuInfo.getEnabled());
	}

	/**
	 * Parameterized test that every boolean attribute value that is not one of the exact lexical
	 * values {@code true}, {@code false}, {@code 1} or {@code 0} is rejected, including values that
	 * only differ in case
	 */
	@ParameterizedTest(name = "[{index}] the boolean attribute value \"{0}\" is rejected")
	@ValueSource(strings = { "TRUE", "True", "FALSE", "yes", "notABoolean" })
	void nonLexicalBooleanAttributeValuesAreRejected(final String xmlValue)
	{
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
			() -> MenuXmlReader.fromXml("<item id=\"i\" enabled=\"" + xmlValue + "\"/>"));
		assertTrue(exception.getMessage().contains("Invalid boolean"));
	}

	@ParameterizedTest(name = "[{index}] the blank attribute value of {0} is read as null")
	@ValueSource(strings = { "mnemonic", "accelerator", "anchor" })
	void blankAttributeValuesAreReadAsNull(final String attributeName)
	{
		MenuInfo menuInfo = MenuXmlReader.fromXml("<item id=\"i\" " + attributeName + "=\"   \"/>");
		switch (attributeName)
		{
			case MenuXmlElements.ATTR_MNEMONIC -> assertNull(menuInfo.getMnemonic());
			case MenuXmlElements.ATTR_ACCELERATOR -> assertNull(menuInfo.getKeyStrokeInfo());
			default -> assertNull(menuInfo.getAnchor());
		}
		assertFalse(MenuXmlWriter.toXml(menuInfo).contains(attributeName + "="));
	}

	@ParameterizedTest(name = "[{index}] the document {0} has {1} root menu definitions")
	@CsvSource(delimiter = '|', value = { "<menu id=\"m\"/> | 1", "<menus/> | 0",
			"<menus><menu id=\"a\"/></menus> | 1",
			"<menus><menubar id=\"a\"/><popup id=\"b\"/><tray id=\"c\"/></menus> | 3",
			"<menubar id=\"b\"><menu id=\"m\"/></menubar> | 1" })
	void readAllCountsTheRootMenuDefinitions(final String xml, final int expectedSize)
	{
		assertEquals(expectedSize, MenuXmlReader.readAll(xml).size());
	}
}
