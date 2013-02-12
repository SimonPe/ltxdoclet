package de.dclj.paul.ltxdoclet;

import java.util.MissingResourceException;

/**
 * @author Rigès De Witte, Simon Peeters,Barny Pieters,Laurens Van Damme
 * 
 *         wrapper around java.util.ResourceBundle to make its use more straight
 *         forward
 */
public class Translator {
	private static final String BUNDLENAME = "de.dclj.paul.ltxdoclet.locale";
	private static java.util.ResourceBundle bundle = null;

	private static java.util.ResourceBundle getBundle() {
		if (bundle == null)
			bundle = java.util.ResourceBundle.getBundle(BUNDLENAME);
		return bundle;
	}

	/**
	 * Set the language to use.
	 * 
	 * @param lang
	 *            language to use
	 * @throws MissingResourceException
	 */
	public static void setLocale(String lang) throws MissingResourceException {
		setLocale(new java.util.Locale(lang));
	}

	/**
	 * Set the language and region to use.
	 * 
	 * @param lang
	 *            language to use.
	 * @param region
	 *            region to use.
	 * @throws MissingResourceException
	 */
	public static void setLocale(String lang, String region)
			throws MissingResourceException {
		setLocale(new java.util.Locale(lang, region));
	}

	/**
	 * Set the language, region and vendor to use.
	 * 
	 * @param lang
	 *            language to use.
	 * @param region
	 *            region to use.
	 * @param vendor
	 *            vendor to use.
	 * @throws MissingResourceException
	 */
	public static void setLocale(String lang, String region, String vendor)
			throws MissingResourceException {
		setLocale(new java.util.Locale(lang, region, vendor));
	}

	/**
	 * Set the locale to use.
	 * 
	 * @param locale
	 *            locale to use.
	 * @throws MissingResourceException
	 */
	public static void setLocale(java.util.Locale locale)
			throws MissingResourceException {
		bundle = java.util.ResourceBundle.getBundle(BUNDLENAME, locale);
	}

	/**
	 * Get the translated version of a string.
	 * 
	 * @param key
	 *            key of a string to get.
	 * @return the translated string.
	 * @throws MissingResourceException
	 */
	public static String getString(String key) throws MissingResourceException {
		try {
			return getBundle().getString(key);
		} catch (MissingResourceException e) {
			return "!"+key+"!";
		}
	}

	/**
	 * Get an translated array of strings.
	 * 
	 * @param key
	 *            key to the array of strings.
	 * @return the translated array of strings.
	 * @throws MissingResourceException
	 */
	public static String[] getStringArray(String key)
			throws MissingResourceException {
		return getBundle().getStringArray(key);
	}

	/**
	 * Get an translated version of an object.
	 * 
	 * @param key
	 *            key to the object.
	 * @return the translated object.
	 * @throws MissingResourceException
	 */
	public static Object getObject(String key) throws MissingResourceException {
		return getBundle().getObject(key);
	}

	/**
	 * Checks if {@value needle} matches any string in a translated array
	 * referenced to by {@value key}. Check is case insensitive.
	 * 
	 * @param key
	 * @param needle
	 * @return Whether or not the string matches.
	 * @throws MissingResourceException
	 */
	public static boolean matchStringToArray(String key, String needle)
			throws MissingResourceException {
		return java.util.Arrays.asList(getStringArray(key)).contains(
				needle.toLowerCase());
	}
}
