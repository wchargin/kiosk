package org.lcmmun.kiosk.resources;

import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class CreditsMessages {
	private static final String BUNDLE_NAME = "org.lcmmun.kiosk.resources.credits"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private CreditsMessages() {
	}
	
	public static Enumeration<String> getKeys() {
		return RESOURCE_BUNDLE.getKeys();
	}
	
	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
