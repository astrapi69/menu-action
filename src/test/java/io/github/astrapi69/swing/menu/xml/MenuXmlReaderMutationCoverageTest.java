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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Additional unit tests for {@link MenuXmlReader} written against the pitest mutation report to
 * exercise branches that {@link MenuXmlReaderTest} and {@link MenuXmlReaderParameterizedTest} did
 * not distinguish. The hardening calls in {@code menuSchema()} and {@code parse()} (the various
 * {@code setFeature}/{@code setProperty} calls that lock down external DTDs, external entities and
 * doctypes) are deliberate defense in depth:
 * {@link #doctypeIsRejectedRegardlessOfWhichGuardCatchesIt} documents that several of them
 * independently block the same attack payload, which is exactly why pitest can remove any single
 * one of those calls without a test noticing (the other guards still catch the same payload); those
 * survivors are expected and not chased further here
 */
class MenuXmlReaderMutationCoverageTest
{

	@Test
	void openResourceFallsBackToTheDeclaringClassLoaderWhenNoContextClassLoaderIsSet()
	{
		ClassLoader original = Thread.currentThread().getContextClassLoader();
		try
		{
			Thread.currentThread().setContextClassLoader(null);
			assertNotNull(MenuXmlReader.readResource("popup.xml"));
		}
		finally
		{
			Thread.currentThread().setContextClassLoader(original);
		}
	}

	@Test
	void validateOfAPathReportsTheErrorsOfAnInvalidFile(@TempDir final Path tempDir)
		throws IOException
	{
		Path invalid = tempDir.resolve("invalid.xml");
		Files.writeString(invalid, "<menu id=\"m\"><foo/></menu>", StandardCharsets.UTF_8);
		Path valid = tempDir.resolve("valid.xml");
		Files.writeString(valid, "<menu id=\"m\"><item id=\"i\"/></menu>", StandardCharsets.UTF_8);

		List<String> invalidErrors = MenuXmlReader.validate(invalid);
		assertFalse(invalidErrors.isEmpty());
		assertTrue(invalidErrors.stream().anyMatch(error -> error.contains("foo")));
		assertTrue(MenuXmlReader.validate(valid).isEmpty());
	}

	@Test
	void validateOfAnInputStreamReportsTheErrorsOfInvalidXml()
	{
		InputStream invalid = new ByteArrayInputStream(
			"<menu id=\"m\"><foo/></menu>".getBytes(StandardCharsets.UTF_8));
		List<String> invalidErrors = MenuXmlReader.validate(invalid);
		assertFalse(invalidErrors.isEmpty());
		assertTrue(invalidErrors.stream().anyMatch(error -> error.contains("foo")));

		InputStream valid = new ByteArrayInputStream(
			"<menu id=\"m\"><item id=\"i\"/></menu>".getBytes(StandardCharsets.UTF_8));
		assertTrue(MenuXmlReader.validate(valid).isEmpty());
	}

	@Test
	void readValidatedTruncatesTheSourceDescriptionOnlyWhenLongerThanEightyCharacters()
	{
		String invalidBody = "<menu><foo/></menu>";

		IllegalArgumentException shortException = assertThrows(IllegalArgumentException.class,
			() -> MenuXmlReader.readValidated(invalidBody));
		assertTrue(shortException.getMessage().contains(invalidBody));
		assertFalse(shortException.getMessage().contains("..."));

		String exactlyEighty = padWithLeadingComment(invalidBody, 80);
		IllegalArgumentException exactException = assertThrows(IllegalArgumentException.class,
			() -> MenuXmlReader.readValidated(exactlyEighty));
		assertTrue(exactException.getMessage().contains(exactlyEighty));
		assertFalse(exactException.getMessage().contains("..."));

		String overEighty = padWithLeadingComment(invalidBody, 81);
		IllegalArgumentException overException = assertThrows(IllegalArgumentException.class,
			() -> MenuXmlReader.readValidated(overEighty));
		assertTrue(overException.getMessage().contains(overEighty.substring(0, 80) + "..."));
		assertFalse(overException.getMessage().contains(overEighty));
	}

	/**
	 * Prepends an xml comment to the given xml so the result has exactly the given length
	 */
	private static String padWithLeadingComment(final String xml, final int targetLength)
	{
		int fillerLength = targetLength - xml.length() - "<!--  -->".length();
		String comment = "<!-- " + "x".repeat(fillerLength) + " -->";
		String padded = comment + xml;
		assertEquals(targetLength, padded.length());
		return padded;
	}

	@Test
	void doctypeIsRejectedRegardlessOfWhichGuardCatchesIt()
	{
		String withExternalEntity = "<!DOCTYPE menu [<!ENTITY xxe SYSTEM \"file:///etc/passwd\">]>"
			+ "<menu id=\"x\" text=\"&xxe;\"/>";
		assertThrows(IllegalArgumentException.class,
			() -> MenuXmlReader.fromXml(withExternalEntity));

		String plainDoctype = "<!DOCTYPE menu><menu id=\"x\"/>";
		assertThrows(IllegalArgumentException.class, () -> MenuXmlReader.fromXml(plainDoctype));
	}
}
