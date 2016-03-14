package br.pucrio.inf.les.law.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Hashtable;

import br.pucrio.inf.les.law.XMLawTestCase;
import br.pucrio.inf.les.law.execution.DescriptorTable;
import br.pucrio.inf.les.law.model.Id;

public abstract class XMLawParserScenario extends XMLawTestCase 
{

    protected DescriptorTable table;

    protected Hashtable<Id, Object> handlerCache;

    public void setUp() throws Exception
    {
    	super.setUp();
    	String fileSeparator = System.getProperty("file.separator");      

        File lawFile = new File(System.getProperty("user.dir")+fileSeparator+"laws"+fileSeparator+"TestLaw.xml");
        XMLawParser parser = new XMLawParser();
        try
        {
        	InputStream fileInput = new FileInputStream(lawFile);
        	table = parser.load(fileInput,lawFile.getAbsolutePath());
        	handlerCache = parser.getHandlerCache();
        }
        catch(FileNotFoundException fnfe)
        {
        	fail("could not find file " + lawFile.getAbsolutePath());
        }
        catch(Exception e)
        {
        	fail("Error parsing file " + lawFile.getAbsolutePath());
        }
    }

}
