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
