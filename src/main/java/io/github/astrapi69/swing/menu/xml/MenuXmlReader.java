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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.swing.KeyStroke;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import io.github.astrapi69.swing.menu.KeyStrokeExtensions;
import io.github.astrapi69.swing.menu.MenuExtensions;
import io.github.astrapi69.swing.menu.enumeration.Anchor;
import io.github.astrapi69.swing.menu.enumeration.MenuType;
import io.github.astrapi69.swing.menu.model.MenuInfo;
import lombok.NonNull;

/**
 * The class {@link MenuXmlReader} reads menu definitions from xml into a {@link MenuInfo} tree. The
 * xml format is a plain hierarchical format, for instance:
 *
 * <pre>
 * &lt;menubar id="global.menu.bar"&gt;
 *   &lt;menu id="global.menu.file" text="File" mnemonic="F"&gt;
 *     &lt;item id="global.menu.file.exit" text="Exit" accelerator="alt F4" action="exit"/&gt;
 *   &lt;/menu&gt;
 * &lt;/menubar&gt;
 * </pre>
 *
 * The parser is configured against xml external entity attacks, doctype declarations are not
 * allowed
 */
public final class MenuXmlReader
{

	private MenuXmlReader()
	{
	}

	/**
	 * Reads the menu definition from the given xml string
	 *
	 * @param xml
	 *            the xml string
	 * @return the root {@link MenuInfo} object
	 */
	public static MenuInfo fromXml(final @NonNull String xml)
	{
		return read(new StringReader(xml));
	}

	/**
	 * Reads the menu definition from the given xml file
	 *
	 * @param path
	 *            the path of the xml file
	 * @return the root {@link MenuInfo} object
	 */
	public static MenuInfo read(final @NonNull Path path)
	{
		try (InputStream inputStream = Files.newInputStream(path))
		{
			return read(inputStream);
		}
		catch (IOException e)
		{
			throw new UncheckedIOException("Could not read menu xml from " + path, e);
		}
	}

	/**
	 * Reads the menu definition from the given classpath resource
	 *
	 * @param resource
	 *            the classpath resource path
	 * @return the root {@link MenuInfo} object
	 */
	public static MenuInfo readResource(final @NonNull String resource)
	{
		try (InputStream in = openResource(resource))
		{
			return read(in);
		}
		catch (IOException e)
		{
			throw new UncheckedIOException("Could not read menu xml resource " + resource, e);
		}
	}

	/**
	 * Reads the menu definition from the given {@link InputStream} object. The stream is not closed
	 *
	 * @param inputStream
	 *            the {@link InputStream} object
	 * @return the root {@link MenuInfo} object
	 */
	public static MenuInfo read(final @NonNull InputStream inputStream)
	{
		return toMenuInfo(parse(new InputSource(inputStream)).getDocumentElement());
	}

	/**
	 * Reads the menu definition from the given {@link Reader} object. The reader is not closed
	 *
	 * @param reader
	 *            the {@link Reader} object
	 * @return the root {@link MenuInfo} object
	 */
	public static MenuInfo read(final @NonNull Reader reader)
	{
		return toMenuInfo(parse(new InputSource(reader)).getDocumentElement());
	}

	/**
	 * Reads all menu definitions from the given xml string. If the root element is {@code <menus>}
	 * all its children are returned, otherwise a list with the single root
	 *
	 * @param xml
	 *            the xml string
	 * @return the list with the root {@link MenuInfo} objects
	 */
	public static List<MenuInfo> readAll(final @NonNull String xml)
	{
		return readAll(new StringReader(xml));
	}

	/**
	 * Reads all menu definitions from the given {@link Reader} object. If the root element is
	 * {@code <menus>} all its children are returned, otherwise a list with the single root
	 *
	 * @param reader
	 *            the {@link Reader} object
	 * @return the list with the root {@link MenuInfo} objects
	 */
	public static List<MenuInfo> readAll(final @NonNull Reader reader)
	{
		return readAll(parse(new InputSource(reader)).getDocumentElement());
	}

	/**
	 * Reads all menu definitions from the given {@link InputStream} object. If the root element is
	 * {@code <menus>} all its children are returned, otherwise a list with the single root
	 *
	 * @param inputStream
	 *            the {@link InputStream} object
	 * @return the list with the root {@link MenuInfo} objects
	 */
	public static List<MenuInfo> readAll(final @NonNull InputStream inputStream)
	{
		return readAll(parse(new InputSource(inputStream)).getDocumentElement());
	}

	private static List<MenuInfo> readAll(final Element root)
	{
		List<MenuInfo> menuInfos = new ArrayList<>();
		if (MenuXmlElements.MENUS.equals(root.getTagName()))
		{
			for (Element child : childElements(root))
			{
				menuInfos.add(toMenuInfo(child));
			}
		}
		else
		{
			menuInfos.add(toMenuInfo(root));
		}
		return menuInfos;
	}

	/**
	 * Converts the given xml element to a {@link MenuInfo} object including all children
	 *
	 * @param element
	 *            the xml element
	 * @return the {@link MenuInfo} object
	 */
	public static MenuInfo toMenuInfo(final @NonNull Element element)
	{
		String tagName = element.getTagName();
		MenuType type = MenuXmlElements.toMenuType(tagName)
			.orElseThrow(() -> new IllegalArgumentException("Unknown menu element <" + tagName
				+ ">, expected one of menubar, menu, item, checkbox, radio, separator, popup, toolbar, tray"));
		MenuExtensions.TextWithMnemonic text = MenuExtensions
			.parseMnemonic(attribute(element, MenuXmlElements.ATTR_TEXT));
		Integer mnemonic = toMnemonic(attribute(element, MenuXmlElements.ATTR_MNEMONIC), element);
		MenuInfo menuInfo = MenuInfo.builder().type(type)
			.name(attribute(element, MenuXmlElements.ATTR_ID)).text(text.text())
			.textKey(attribute(element, MenuXmlElements.ATTR_TEXT_KEY))
			.toolTip(attribute(element, MenuXmlElements.ATTR_TOOL_TIP))
			.mnemonic(mnemonic != null ? mnemonic : text.mnemonic())
			.keyStrokeInfo(
				toKeyStrokeInfo(attribute(element, MenuXmlElements.ATTR_ACCELERATOR), element))
			.actionId(attribute(element, MenuXmlElements.ATTR_ACTION))
			.actionCommand(attribute(element, MenuXmlElements.ATTR_ACTION_COMMAND))
			.enabled(toBoolean(attribute(element, MenuXmlElements.ATTR_ENABLED)))
			.visible(toBoolean(attribute(element, MenuXmlElements.ATTR_VISIBLE)))
			.selected(toBoolean(attribute(element, MenuXmlElements.ATTR_SELECTED)))
			.group(attribute(element, MenuXmlElements.ATTR_GROUP))
			.icon(attribute(element, MenuXmlElements.ATTR_ICON))
			.anchor(toAnchor(attribute(element, MenuXmlElements.ATTR_ANCHOR), element))
			.relativeToMenuId(attribute(element, MenuXmlElements.ATTR_RELATIVE_TO))
			.toolTipKey(attribute(element, MenuXmlElements.ATTR_TOOL_TIP_KEY))
			.model(attribute(element, MenuXmlElements.ATTR_MODEL))
			.value(attribute(element, MenuXmlElements.ATTR_VALUE))
			.showText(toBoolean(attribute(element, MenuXmlElements.ATTR_SHOW_TEXT)))
			.floatable(toBoolean(attribute(element, MenuXmlElements.ATTR_FLOATABLE)))
			.rollover(toBoolean(attribute(element, MenuXmlElements.ATTR_ROLLOVER)))
			.accessibleName(attribute(element, MenuXmlElements.ATTR_ACCESSIBLE_NAME))
			.accessibleDescription(attribute(element, MenuXmlElements.ATTR_ACCESSIBLE_DESCRIPTION))
			.build();
		for (Element child : childElements(element))
		{
			menuInfo.addChild(toMenuInfo(child));
		}
		return menuInfo;
	}

	/**
	 * Validates the given xml string against the menu schema {@code menu.xsd}
	 *
	 * @param xml
	 *            the xml string
	 * @return the list with the validation errors, empty if the xml is valid
	 */
	public static List<String> validate(final @NonNull String xml)
	{
		return validate(new StreamSource(new StringReader(xml)));
	}

	/**
	 * Validates the given xml file against the menu schema {@code menu.xsd}
	 *
	 * @param path
	 *            the path of the xml file
	 * @return the list with the validation errors, empty if the xml is valid
	 */
	public static List<String> validate(final @NonNull Path path)
	{
		return validate(new StreamSource(path.toFile()));
	}

	/**
	 * Validates the given xml stream against the menu schema {@code menu.xsd}. The stream is not
	 * closed
	 *
	 * @param inputStream
	 *            the {@link InputStream} object
	 * @return the list with the validation errors, empty if the xml is valid
	 */
	public static List<String> validate(final @NonNull InputStream inputStream)
	{
		return validate(new StreamSource(inputStream));
	}

	/**
	 * Validates the given xml source against the menu schema {@code menu.xsd}
	 *
	 * @param source
	 *            the xml {@link Source} object
	 * @return the list with the validation errors in the form {@code line:column: message}, empty
	 *         if the xml is valid
	 */
	public static List<String> validate(final @NonNull Source source)
	{
		List<String> errors = new ArrayList<>();
		try
		{
			Validator validator = menuSchema().newValidator();
			validator.setErrorHandler(new ErrorHandler()
			{
				@Override
				public void warning(final SAXParseException exception)
				{
				}

				@Override
				public void error(final SAXParseException exception)
				{
					errors.add(describe(exception));
				}

				@Override
				public void fatalError(final SAXParseException exception)
				{
					errors.add(describe(exception));
				}
			});
			validator.validate(source);
		}
		catch (SAXException e)
		{
			if (errors.isEmpty())
			{
				errors.add(e.getMessage());
			}
		}
		catch (IOException e)
		{
			throw new UncheckedIOException("Could not read menu xml for validation", e);
		}
		return errors;
	}

	/**
	 * Validates the given xml string against the menu schema and reads it if it is valid
	 *
	 * @param xml
	 *            the xml string
	 * @return the root {@link MenuInfo} object
	 * @throws IllegalArgumentException
	 *             with all validation errors if the xml is not valid
	 */
	public static MenuInfo readValidated(final @NonNull String xml)
	{
		requireValid(validate(xml), xml.length() > 80 ? xml.substring(0, 80) + "..." : xml);
		return fromXml(xml);
	}

	/**
	 * Validates the given xml file against the menu schema and reads it if it is valid
	 *
	 * @param path
	 *            the path of the xml file
	 * @return the root {@link MenuInfo} object
	 * @throws IllegalArgumentException
	 *             with all validation errors if the xml is not valid
	 */
	public static MenuInfo readValidated(final @NonNull Path path)
	{
		requireValid(validate(path), path.toString());
		return read(path);
	}

	/**
	 * Validates the given classpath resource against the menu schema and reads it if it is valid
	 *
	 * @param resource
	 *            the classpath resource path
	 * @return the root {@link MenuInfo} object
	 * @throws IllegalArgumentException
	 *             with all validation errors if the xml is not valid
	 */
	public static MenuInfo readValidatedResource(final @NonNull String resource)
	{
		try (InputStream inputStream = openResource(resource))
		{
			requireValid(validate(inputStream), resource);
		}
		catch (IOException e)
		{
			throw new UncheckedIOException("Could not read menu xml resource " + resource, e);
		}
		return readResource(resource);
	}

	private static void requireValid(final List<String> errors, final String source)
	{
		if (!errors.isEmpty())
		{
			throw new IllegalArgumentException(
				"Invalid menu xml " + source + ":\n" + String.join("\n", errors));
		}
	}

	private static String describe(final SAXParseException exception)
	{
		return exception.getLineNumber() + ":" + exception.getColumnNumber() + ": "
			+ exception.getMessage();
	}

	private static Schema menuSchema()
	{
		try
		{
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
			URL schemaUrl = MenuXmlReader.class.getResource("/menu.xsd");
			if (schemaUrl == null)
			{
				throw new IllegalStateException(
					"The menu schema menu.xsd is missing on the classpath");
			}
			return factory.newSchema(schemaUrl);
		}
		catch (SAXException e)
		{
			throw new IllegalStateException("The menu schema menu.xsd is invalid", e);
		}
	}

	private static InputStream openResource(final String resource)
	{
		String name = resource.startsWith("/") ? resource.substring(1) : resource;
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream inputStream = classLoader != null
			? classLoader.getResourceAsStream(name)
			: null;
		if (inputStream == null)
		{
			inputStream = MenuXmlReader.class.getClassLoader().getResourceAsStream(name);
		}
		if (inputStream == null)
		{
			throw new IllegalArgumentException("Menu xml resource not found: " + resource);
		}
		return inputStream;
	}

	private static Document parse(final InputSource inputSource)
	{
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
			factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			factory.setXIncludeAware(false);
			factory.setExpandEntityReferences(false);
			factory.setNamespaceAware(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.parse(inputSource);
		}
		catch (ParserConfigurationException | SAXException e)
		{
			throw new IllegalArgumentException("Invalid menu xml: " + e.getMessage(), e);
		}
		catch (IOException e)
		{
			throw new UncheckedIOException("Could not read menu xml", e);
		}
	}

	private static List<Element> childElements(final Element element)
	{
		List<Element> elements = new ArrayList<>();
		NodeList childNodes = element.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++)
		{
			Node node = childNodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				elements.add((Element)node);
			}
		}
		return elements;
	}

	private static String attribute(final Element element, final String name)
	{
		return element.hasAttribute(name) ? element.getAttribute(name) : null;
	}

	private static Boolean toBoolean(final String value)
	{
		return value == null ? null : Boolean.valueOf(value.trim());
	}

	private static Integer toMnemonic(final String value, final Element element)
	{
		if (value == null || value.isBlank())
		{
			return null;
		}
		String trimmed = value.trim();
		if (trimmed.length() == 1)
		{
			return MenuExtensions.toMnemonic(trimmed.charAt(0));
		}
		try
		{
			return Integer.valueOf(trimmed);
		}
		catch (NumberFormatException e)
		{
			throw new IllegalArgumentException("Invalid mnemonic '" + value + "' in element "
				+ describe(element) + ", expected a single character or a key code", e);
		}
	}

	private static io.github.astrapi69.swing.menu.model.KeyStrokeInfo toKeyStrokeInfo(
		final String value, final Element element)
	{
		if (value == null || value.isBlank())
		{
			return null;
		}
		KeyStroke keyStroke = KeyStroke.getKeyStroke(value.trim());
		if (keyStroke == null)
		{
			throw new IllegalArgumentException("Invalid accelerator '" + value + "' in element "
				+ describe(element) + ", expected a keystroke like 'ctrl S' or 'alt F4'");
		}
		return KeyStrokeExtensions.toKeyStrokeInfo(keyStroke);
	}

	private static Anchor toAnchor(final String value, final Element element)
	{
		if (value == null || value.isBlank())
		{
			return null;
		}
		try
		{
			return Anchor.valueOf(value.trim().toUpperCase());
		}
		catch (IllegalArgumentException e)
		{
			throw new IllegalArgumentException("Invalid anchor '" + value + "' in element "
				+ describe(element) + ", expected one of BEFORE, AFTER, FIRST, LAST", e);
		}
	}

	private static String describe(final Element element)
	{
		String id = attribute(element, MenuXmlElements.ATTR_ID);
		return "<" + element.getTagName() + (id != null ? " id=\"" + id + "\"" : "") + ">";
	}
}
