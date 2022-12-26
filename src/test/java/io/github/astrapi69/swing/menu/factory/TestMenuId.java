/**
 * The MIT License
 *
 * Copyright (C) 2021 Asterios Raptis
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
package io.github.astrapi69.swing.menu.factory;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum TestMenuId
{
	HELP_DIAGNOSTIC(TestMenuId.HELP_DIAGNOSTIC_KEY), HELP_DIAGNOSTIC_ACTIVITY(
		TestMenuId.HELP_DIAGNOSTIC_ACTIVITY_KEY), HELP_DIAGNOSTIC_PROFILE(
			TestMenuId.HELP_DIAGNOSTIC_PROFILE_KEY), HELP_DIAGNOSTIC_USAGE(
				TestMenuId.HELP_DIAGNOSTIC_USAGE_KEY);

	/** the properties key from the current menu */
	String propertiesKey;

	public static final String HELP_DIAGNOSTIC_KEY = "global.menu.help.diagnostic";
	public static final String HELP_DIAGNOSTIC_ACTIVITY_KEY = "global.menu.help.diagnostic.activity";
	public static final String HELP_DIAGNOSTIC_PROFILE_KEY = "global.menu.help.diagnostic.profile";
	public static final String HELP_DIAGNOSTIC_USAGE_KEY = "global.menu.help.diagnostic.usage";
}
