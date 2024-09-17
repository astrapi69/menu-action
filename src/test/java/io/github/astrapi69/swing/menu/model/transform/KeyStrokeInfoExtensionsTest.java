package io.github.astrapi69.swing.menu.model.transform;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import javax.swing.JComponent;
import javax.swing.KeyStroke;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import io.github.astrapi69.swing.menu.model.KeyStrokeInfo;

/**
 * The class {@link KeyStrokeInfoExtensionsTest} provides tests for the class
 * {@link KeyStrokeInfoExtensions}
 */
public class KeyStrokeInfoExtensionsTest
{

	private KeyStrokeInfo keyStrokeInfo;
	private JComponent jComponent;

	@BeforeEach
	void setUp()
	{
		keyStrokeInfo = new KeyStrokeInfo();
		// mock or initialize JComponent
		jComponent = new JComponent()
		{
		};
	}

	/**
	 * Test for {@link KeyStrokeInfoExtensions#set(KeyStrokeInfo, KeyStroke)}
	 */
	@Test
	void testSetKeyStrokeInfo()
	{
		KeyStroke keyStroke = KeyStroke.getKeyStroke("ctrl A");
		KeyStrokeInfoExtensions.set(keyStrokeInfo, keyStroke);
		assertEquals(keyStroke.getKeyCode(), keyStrokeInfo.getKeyCode());
		assertEquals(keyStroke.getModifiers(), keyStrokeInfo.getModifiers());
		assertEquals(keyStroke.isOnKeyRelease(), keyStrokeInfo.getOnKeyRelease());
		assertEquals(keyStroke.toString(), keyStrokeInfo.getKeystrokeAsString());
	}

	/**
	 * Test for {@link KeyStrokeInfoExtensions#toKeyStroke(KeyStrokeInfo)}
	 */
	@Test
	void testToKeyStroke()
	{
		keyStrokeInfo.setKeyCode(KeyStroke.getKeyStroke("ctrl A").getKeyCode());
		keyStrokeInfo.setModifiers(KeyStroke.getKeyStroke("ctrl A").getModifiers());
		KeyStroke keyStroke = KeyStrokeInfoExtensions.toKeyStroke(keyStrokeInfo);
		assertNotNull(keyStroke);
	}

	/**
	 * Test for {@link KeyStrokeInfoExtensions#getKeyStrokeInfos(JComponent)}
	 */
	@Test
	void testGetKeyStrokeInfos()
	{
		List<KeyStrokeInfo> keyStrokeInfos = KeyStrokeInfoExtensions.getKeyStrokeInfos(jComponent);
		assertNotNull(keyStrokeInfos);
	}

}
