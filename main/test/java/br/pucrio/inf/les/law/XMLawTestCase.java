package br.pucrio.inf.les.law;

import junit.framework.TestCase;

public abstract class XMLawTestCase extends TestCase 
{
	public void setUp() throws Exception
	{
		 // setting user directory
        String fileSeparator = System.getProperty("file.separator");
        String previousUserDir = System.getProperty("user.dir");
        if (previousUserDir.indexOf("target" + fileSeparator + "test-classes") == -1) {
            String userDir = previousUserDir + fileSeparator + "target"
                    + fileSeparator + "test-classes";

            System.setProperty("user.dir", userDir);

            System.setProperty("law.conf.dir", userDir + fileSeparator + "conf");
        }
	}

}
