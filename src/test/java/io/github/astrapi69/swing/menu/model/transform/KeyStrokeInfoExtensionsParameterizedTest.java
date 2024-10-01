package io.github.astrapi69.swing.menu.model.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import javax.swing.JComponent;
import javax.swing.KeyStroke;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import io.github.astrapi69.swing.menu.model.KeyStrokeInfo;

/**
 * The class {@link KeyStrokeInfoExtensionsParameterizedTest} provides parameterized tests for the
 * class {@link KeyStrokeInfoExtensions}
 */
public class KeyStrokeInfoExtensionsParameterizedTest
{

	/**
	 * Parameterized test for {@link KeyStrokeInfoExtensions#toKeyStroke(KeyStrokeInfo)}
	 */
	@ParameterizedTest
	@CsvFileSource(resources = "/keyStrokeInfos.csv", numLinesToSkip = 1)
	void testToKeyStrokeWithParameters(String keystrokeString, int keyCode, int modifiers)
	{
		KeyStrokeInfo keyStrokeInfo = new KeyStrokeInfo();
		keyStrokeInfo.setKeystrokeAsString(keystrokeString);
		keyStrokeInfo.setKeyCode(keyCode);
		keyStrokeInfo.setModifiers(modifiers);
		KeyStroke keyStroke = KeyStrokeInfoExtensions.toKeyStroke(keyStrokeInfo);
		assertNotNull(keyStroke);
	}
}
