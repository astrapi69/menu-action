package io.github.astrapi69.browser;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BrowserControlExtensionsTest
{

	/**
	 * Test display ur lon standard browser.
	 */
	@Test
	@Disabled
	public void testDisplayURLonStandardBrowser()
	{

		final String url = "http://jaulp.sourceforge.net";

		final Object obj = BrowserControlExtensions.displayURLonStandardBrowser(null, url);

		assertNotNull(obj);

	}
}
