package io.github.astrapi69.swing.menu;

import org.junit.jupiter.api.Test;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KeyStrokeInfoTest
{
	@Test
	public void test()
	{
		KeyStroke actual;
		KeyStroke expected;
		KeyStrokeInfo keyStrokeInfo;

		keyStrokeInfo = KeyStrokeInfo.builder()
				.keystrokeAsString("alt pressed F4")
				.build();
		actual = keyStrokeInfo.toKeyStroke();
		expected = KeyStroke.getKeyStroke("alt pressed F4");
		assertEquals(actual, expected);

		keyStrokeInfo = KeyStrokeInfo.toKeyStrokeInfo(KeyStroke.getKeyStroke("alt pressed F5"));

		actual = keyStrokeInfo.toKeyStroke();
		expected = KeyStroke.getKeyStroke("alt pressed F5");
		assertEquals(actual, expected);


		keyStrokeInfo.set(KeyStroke.getKeyStroke("ctrl pressed D"));

		actual = keyStrokeInfo.toKeyStroke();
		expected = KeyStroke.getKeyStroke("ctrl pressed D");
		assertEquals(actual, expected);

	}
}
