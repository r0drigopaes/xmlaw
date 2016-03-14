package br.pucrio.inf.les.law.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.util.Locale;

import br.pucrio.inf.les.law.XMLawTestCase;

public class LawResourceBundleTest extends XMLawTestCase {
	private Locale previousLocale;

	public void setUp() throws Exception {
		super.setUp();
		previousLocale = Locale.getDefault();
		Locale.setDefault(Locale.ENGLISH);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		Locale.setDefault(previousLocale);
		LawResourceBundle.destroy();
	}

	public void testDefaultResourceBundle() {
		String message = LawResourceBundle.getInstance().getString(
				"msg.no_parameter");
		assertEquals("Parameter [%s] was not informed", message);
	}

	public void testAnotherResourceBundle() {
		Locale.setDefault(new Locale("br"));
		String message = LawResourceBundle.getInstance().getString(
				"msg.no_parameter");
		assertEquals("Parametro [%s] não foi informado", message);
	}

	public void testNoAvailableResourceBundle() {
		// set to a non avaiable resource bundle, it must change to the default
		// one automatically
		Locale.setDefault(new Locale("xx"));
		testDefaultResourceBundle();
	}

	public void testInvalidResourceBundle() throws IOException {
		// create an invalid file
		File errorFile = new File(ConfigUtils.getConfigDir()
				+ "resources_err.properties");
		errorFile.createNewFile();
		FileOutputStream fout = new FileOutputStream(errorFile);
		FileLock lock = fout.getChannel().tryLock();

		// set to an invalid resource bundle, it must change to the default one
		// automatically
		Locale.setDefault(new Locale("err"));
		testDefaultResourceBundle();

		lock.release();
		fout.close();
		errorFile.delete();
	}

	public void testFormat() {
		String formated = LawResourceBundle.getInstance().format(
				"msg.no_parameter", "teste");
		assertEquals("Parameter [teste] was not informed", formated);
	}

	public void testMissingResourceFormat() {
		String formated = LawResourceBundle.getInstance().format(
				"no.existing.key");
		assertNull(formated);
	}

	public void testNullKeyParameter() {
		String formated = LawResourceBundle.getInstance().format(null);
		assertNull(formated);
	}

	public void testInvalidFormattArgs() {
		String formated = LawResourceBundle.getInstance().format(
				"msg.no_parameter");
		assertNull(formated);
	}
}
