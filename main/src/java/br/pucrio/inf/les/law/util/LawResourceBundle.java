package br.pucrio.inf.les.law.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Formatter;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Implements a Resource Bundle for the Law platform as a singleton. All the
 * messages can be mapped into the file resourses_(Locale).properties. Where the
 * Locale option means the default locale set by the virtual machine when it
 * starts. If the resource file does not exists for the locale set or it is
 * invalid the default one (en) will be considered.
 * 
 * @author lfrodrigues
 * 
 */
public final class LawResourceBundle extends PropertyResourceBundle {
	private static final Log LOG = LogFactory.getLog(LawResourceBundle.class);

	private static LawResourceBundle lawResourceBundle;

	private static final File DEFAULT_RESOURCE_BUNDLE = new File(ConfigUtils
			.getConfigDir()
			+ "resources_en.properties");

	private LawResourceBundle(File file) throws IOException {
		super(new FileInputStream(file));
		if (file.equals(DEFAULT_RESOURCE_BUNDLE)
				&& DEFAULT_RESOURCE_BUNDLE.exists() == false) {
			LOG.error("The default resource bundle file does not exist. "
					+ DEFAULT_RESOURCE_BUNDLE);
		}
	}

	/**
	 * Returns the instance of this LawResourceBundle singleton
	 * 
	 * @return - the singleton instance
	 */
	public static LawResourceBundle getInstance() {
		if (lawResourceBundle == null) {
			String locale = Locale.getDefault().getLanguage();

			LOG.info(ConfigUtils.getConfigDir()
					+ "resources_" + locale + ".properties");
			
			File lawResourceBundleFile = new File(ConfigUtils.getConfigDir()
					+ "resources_" + locale + ".properties");
			if (lawResourceBundleFile.exists() == false) {
				LOG.info("Unable to find resource file for default locale ("
						+ locale + "). Loading default (en) resource");
				lawResourceBundleFile = DEFAULT_RESOURCE_BUNDLE;
			}

			try {
				lawResourceBundle = new LawResourceBundle(lawResourceBundleFile);
			} catch (IOException ioe) {
				LOG.error("Error loading the resource file "
						+ lawResourceBundleFile + ".");
				if (lawResourceBundleFile.equals(DEFAULT_RESOURCE_BUNDLE) == false) {
					try {
						lawResourceBundle = new LawResourceBundle(
								DEFAULT_RESOURCE_BUNDLE);
					} catch (IOException ioe2) {
						LOG.error("Error loading default resource file", ioe2);
					}
				}
			}
		}
		LOG.info("Saindo");
		return lawResourceBundle;
	}

	/**
	 * Destroy the singleton instance
	 * 
	 */
	public static void destroy() {
		lawResourceBundle = null;
	}

	/**
	 * Format a message mapped by the key arg according to the
	 * {@link java.util.Formatter Formatter}
	 * 
	 * @param key -
	 *            the resource key
	 * @param args -
	 *            the content to be replaced by the formatter
	 * @return - the message formatted or null if a null key is informed, if
	 *         there is no message mapped to the given key, if message mapped is
	 *         not an String or if occur an error during the format process,
	 *         such as no enough arguments to be replaced
	 */
	public String format(String key, Object... args) {
		String msg = null;
		try {
			msg = lawResourceBundle.getString(key);
		} catch (MissingResourceException mre) {
			LOG.warn("No resource found for " + key, mre);
			return null;
		} catch (ClassCastException cce) {
			LOG.error("The resource mapped by the key " + key
					+ " is not a String", cce);
			return null;
		} catch (NullPointerException npe) {
			LOG.warn("Null key argument for retrieving msg", npe);
			return null;
		}

		String formated = null;
		try {
			formated = new Formatter().format(msg, args).toString();
		} catch (Throwable t) {
			LOG.warn("Error formatting message " + msg
					+ " with following arguments " + args + ".");
		}

		return formated;
	}
}