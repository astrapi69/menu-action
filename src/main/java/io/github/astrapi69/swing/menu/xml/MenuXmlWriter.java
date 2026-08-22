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
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import io.github.astrapi69.swing.menu.model.KeyStrokeInfo;
import io.github.astrapi69.swing.menu.model.MenuInfo;
import lombok.NonNull;

/**
 * The class {@link MenuXmlWriter} writes a {@link MenuInfo} tree to the xml format that
 * {@link MenuXmlReader} can read back
 */
public final class MenuXmlWriter
{

	private MenuXmlWriter()
	{
	}

	/**
	 * Writes the given {@link MenuInfo} tree to an xml string
	 *
	 * @param menuInfo
	 *            the root {@link MenuInfo} object
	 * @return the xml string
	 */
	public static String toXml(final @NonNull MenuInfo menuInfo)
	{
		StringWriter stringWriter = new StringWriter();
		write(menuInfo, stringWriter);
		return stringWriter.toString();
	}

	/**
	 * Writes the given {@link MenuInfo} trees wrapped in a {@code <menus>} element to an xml string
	 *
	 * @param menuInfos
	 *            the root {@link MenuInfo} objects
	 * @return the xml string
	 */
	public static String toXml(final @NonNull List<MenuInfo> menuInfos)
	{
		Document document = newDocument();
		Element root = document.createElement(MenuXmlElements.MENUS);
		document.appendChild(root);
		for (MenuInfo menuInfo : menuInfos)
		{
			root.appendChild(toElement(document, menuInfo));
		}
		StringWriter stringWriter = new StringWriter();
		transform(document, stringWriter);
		return stringWriter.toString();
	}

	/**
	 * Writes the given {@link MenuInfo} tree to the given xml file
	 *
	 * @param menuInfo
	 *            the root {@link MenuInfo} object
	 * @param path
	 *            the path of the xml file
	 */
	public static void write(final @NonNull MenuInfo menuInfo, final @NonNull Path path)
	{
		try (OutputStream outputStream = Files.newOutputStream(path))
		{
			write(menuInfo, outputStream);
		}
		catch (IOException e)
		{
			throw new UncheckedIOException("Could not write menu xml to " + path, e);
		}
	}

	/**
	 * Writes the given {@link MenuInfo} tree to the given {@link OutputStream} object. The stream
	 * is not closed
	 *
	 * @param menuInfo
	 *            the root {@link MenuInfo} object
	 * @param outputStream
	 *            the {@link OutputStream} object
	 */
	public static void write(final @NonNull MenuInfo menuInfo,
		final @NonNull OutputStream outputStream)
	{
		Document document = toDocument(menuInfo);
		try
		{
			newTransformer().transform(new DOMSource(document), new StreamResult(outputStream));
		}
		catch (TransformerException e)
		{
			throw new IllegalStateException("Could not write menu xml", e);
		}
	}

	/**
	 * Writes the given {@link MenuInfo} tree to the given {@link Writer} object. The writer is not
	 * closed
	 *
	 * @param menuInfo
	 *            the root {@link MenuInfo} object
	 * @param writer
	 *            the {@link Writer} object
	 */
	public static void write(final @NonNull MenuInfo menuInfo, final @NonNull Writer writer)
	{
		transform(toDocument(menuInfo), writer);
	}

	/**
	 * Converts the given {@link MenuInfo} tree to a xml {@link Document} object
	 *
	 * @param menuInfo
	 *            the root {@link MenuInfo} object
	 * @return the xml {@link Document} object
	 */
	public static Document toDocument(final @NonNull MenuInfo menuInfo)
	{
		Document document = newDocument();
		document.appendChild(toElement(document, menuInfo));
		return document;
	}

	/**
	 * Converts the given {@link MenuInfo} object including all children to a xml {@link Element}
	 * object
	 *
	 * @param document
	 *            the owner {@link Document} object
	 * @param menuInfo
	 *            the {@link MenuInfo} object
	 * @return the xml {@link Element} object
	 */
	public static Element toElement(final @NonNull Document document,
		final @NonNull MenuInfo menuInfo)
	{
		String elementName = MenuXmlElements.toElementName(menuInfo.getType())
			.orElseThrow(() -> new IllegalArgumentException("The menu type " + menuInfo.getType()
				+ " of the menu '" + menuInfo.getName() + "' can not be represented in xml"));
		Element element = document.createElement(elementName);
		setAttribute(element, MenuXmlElements.ATTR_ID, menuInfo.getName());
		setAttribute(element, MenuXmlElements.ATTR_TEXT, menuInfo.getText());
		setAttribute(element, MenuXmlElements.ATTR_TEXT_KEY, menuInfo.getTextKey());
		setAttribute(element, MenuXmlElements.ATTR_TOOL_TIP, menuInfo.getToolTip());
		setAttribute(element, MenuXmlElements.ATTR_MNEMONIC,
			toMnemonicString(menuInfo.getMnemonic()));
		setAttribute(element, MenuXmlElements.ATTR_ACCELERATOR,
			toAcceleratorString(menuInfo.getKeyStrokeInfo()));
		setAttribute(element, MenuXmlElements.ATTR_ACTION, menuInfo.getActionId());
		setAttribute(element, MenuXmlElements.ATTR_ACTION_COMMAND, menuInfo.getActionCommand());
		setAttribute(element, MenuXmlElements.ATTR_ENABLED, toString(menuInfo.getEnabled()));
		setAttribute(element, MenuXmlElements.ATTR_SELECTED, toString(menuInfo.getSelected()));
		setAttribute(element, MenuXmlElements.ATTR_GROUP, menuInfo.getGroup());
		setAttribute(element, MenuXmlElements.ATTR_ICON, menuInfo.getIcon());
		setAttribute(element, MenuXmlElements.ATTR_ANCHOR,
			menuInfo.getAnchor() != null ? menuInfo.getAnchor().name() : null);
		setAttribute(element, MenuXmlElements.ATTR_RELATIVE_TO, menuInfo.getRelativeToMenuId());
		if (menuInfo.hasChildren())
		{
			for (MenuInfo child : menuInfo.getChildren())
			{
				element.appendChild(toElement(document, child));
			}
		}
		return element;
	}

	private static void transform(final Document document, final Writer writer)
	{
		try
		{
			newTransformer().transform(new DOMSource(document), new StreamResult(writer));
		}
		catch (TransformerException e)
		{
			throw new IllegalStateException("Could not write menu xml", e);
		}
	}

	private static Document newDocument()
	{
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			return factory.newDocumentBuilder().newDocument();
		}
		catch (ParserConfigurationException e)
		{
			throw new IllegalStateException("Could not create xml document", e);
		}
	}

	private static Transformer newTransformer() throws TransformerException
	{
		TransformerFactory factory = TransformerFactory.newInstance();
		factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		Transformer transformer = factory.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.name());
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		return transformer;
	}

	private static void setAttribute(final Element element, final String name, final String value)
	{
		if (value != null)
		{
			element.setAttribute(name, value);
		}
	}

	private static String toString(final Boolean value)
	{
		return value == null ? null : value.toString();
	}

	private static String toMnemonicString(final Integer mnemonic)
	{
		if (mnemonic == null)
		{
			return null;
		}
		int value = mnemonic;
		if ((value >= 'A' && value <= 'Z') || (value >= '0' && value <= '9'))
		{
			return String.valueOf((char)value);
		}
		return String.valueOf(value);
	}

	private static String toAcceleratorString(final KeyStrokeInfo keyStrokeInfo)
	{
		if (keyStrokeInfo == null)
		{
			return null;
		}
		if (keyStrokeInfo.getKeystrokeAsString() != null
			&& !keyStrokeInfo.getKeystrokeAsString().isBlank())
		{
			return keyStrokeInfo.getKeystrokeAsString();
		}
		javax.swing.KeyStroke keyStroke = keyStrokeInfo.toKeyStroke();
		return keyStroke != null ? keyStroke.toString() : null;
	}
}
