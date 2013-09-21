package org.lcmmun.kiosk.resources.flags;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;

/**
 * This utility class aims to guess, given the name of a delegate, the correct
 * flag to be associated with that delegate.
 * 
 * @author William Chargin
 * 
 */
public final class FileNameGuesser {

	public static class Result {

		/**
		 * Whether the result is confident. If it is, the flag should be placed
		 * automatically. If it isn't, the user should be asked if it is
		 * correct.
		 */
		public final boolean confident;

		/**
		 * The icon.
		 */
		public final ImageIcon icon;

		/**
		 * Creates the result with the given information.
		 * 
		 * @param confident
		 *            whether the result is confident
		 * @param icon
		 *            the icon
		 */
		private Result(boolean confident, ImageIcon icon) {
			super();
			this.confident = confident;
			this.icon = icon;
		}
	}

	/**
	 * The extension to look for.
	 */
	private static final String DESIRED_EXTENSION = "png"; //$NON-NLS-1$

	/**
	 * The directory containing the images.
	 */
	private static final String DIRECTORY = "/org/lcmmun/kiosk/resources/flags/"; //$NON-NLS-1$

	/**
	 * The map of primary names to lists of aliases.
	 */
	public static final Map<String, ? extends List<String>> imageNames;
	static {
		// This list will become #imageNames.
		LinkedHashMap<String, List<String>> tempMap = new LinkedHashMap<String, List<String>>();

		// Read the list of flags from a file.
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				FileNameGuesser.class.getResourceAsStream(DIRECTORY
						+ "_flags.txt"))); //$NON-NLS-1$

		try {
			String line;
			while ((line = reader.readLine()) != null) {
				final String firstPartName = line.substring(0, line
						.contains(Character.toString('|')) ? line.indexOf('|')
						: line.length());
				final String fileName = firstPartName.substring(0,
						firstPartName.lastIndexOf('.'));
				ArrayList<String> list = new ArrayList<String>();
				String[] parts = line.split(Pattern.quote(Character
						.toString('|')));
				if (parts.length > 1) {
					for (int i = 1; i < parts.length; i++) {
						String part = parts[i].trim();
						if (!part.isEmpty()) {
							list.add(part);
						}
					}
				}
				tempMap.put(fileName, Collections.unmodifiableList(list));
			}
		} catch (IOException ie) {
			// Do nothing.
		}

		imageNames = Collections.unmodifiableMap(tempMap);
	}

	/**
	 * Gets the image icon from the given name. The path must be
	 * {@value #DIRECTORY}, and the extension must be
	 * {@value #DESIRED_EXTENSION}.
	 * 
	 * @param name
	 *            the file name, excluding extension.
	 * @return
	 */
	public static ImageIcon getIconFromName(String name) {
		String path = DIRECTORY + name + '.' + DESIRED_EXTENSION;
		return new ImageIcon(FileNameGuesser.class.getResource(path));
	}

	public static Result guess(String delegateName) {

		// 0. Ignore case. delegateName = delegateName.toLowerCase();

		// 1. Check for exact matches.
		for (String name : imageNames.keySet()) {
			if (name.equalsIgnoreCase(delegateName)) {
				return new Result(true, getIconFromName(name));
			} else {
				for (String alias : imageNames.get(name)) {
					if (alias.equalsIgnoreCase(delegateName)) {
						return new Result(true, getIconFromName(name));
					}
				}
			}
		}

		// 2. Check for matches, excluding non-word characters.
		{
			String spacelessDelegateName = delegateName.replaceAll("\\W", ""); //$NON-NLS-1$ //$NON-NLS-2$
			for (String name : imageNames.keySet()) {
				if (name.replace("\\W", "").equalsIgnoreCase( //$NON-NLS-1$ //$NON-NLS-2$
						spacelessDelegateName)) {
					return new Result(true, getIconFromName(name));
				} else {
					for (String alias : imageNames.get(name)) {
						if (alias.replace("\\W", "").equalsIgnoreCase( //$NON-NLS-1$ //$NON-NLS-2$
								spacelessDelegateName)) {
							return new Result(true, getIconFromName(name));
						}
					}
				}
			}
		}

		return null;
	}

	/**
	 * No instantiation required.
	 */
	private FileNameGuesser() {
	}
}
