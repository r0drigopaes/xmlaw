/******************************************************************
 * @(#) ParseConfig.java   1.3
 *
 * Copyright (c) 2000 John Miller
 * All Right Reserved
 *-----------------------------------------------------------------
 * Permission to use, copy, modify and distribute this software and
 * its documentation without fee is hereby granted provided that
 * this copyright notice appears in all copies.
 * WE MAKE NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY 
 * OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. WE SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY ANY USER AS A RESULT OFUSING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *-----------------------------------------------------------------
 *  
 * @version 1.3 (12 December 2000)
 * @author  Xueqin Huang, John Miller
 */

package jsim.jquery;

import java.io.File;
import java.io.StringReader;

import org.xml.sax.*;
import org.xml.sax.helpers.*;


/***************************************************************
 * The ParseConfig class parses an XML formatted configuration file
 * and extracts the values of the database connection attributes.
 */
public class ParseConfig extends DefaultHandler 
{
   /**
    * The default XML parser name
    */
   private final String DEFAULT_PARSER_NAME = "org.apache.xerces.parsers.SAXParser";

   /**
    * Whether or not the XML file should be validated
    */
   private boolean      setValidation = true;

   /**
    * Whether namespaces should be checked
    */
   private boolean      setNameSpaces = false;

   /**
    * Whether an XML schema will be used
    */
   private boolean      setSchemaSupport = false;

   /**
    * The framework attribute of the connection
    */
   private String       framework = null;

   /**
    * The JDBC driver class name
    */
   private String       driverClassName = null;

   /**
    * The database URL
    */
   private String       url = null;

   /**
    * A global buffer for storing temporary element values
    */
   private char []	buffer = new char [100];

   /**
    * The length of characters stored in the global buffer
    */
   private int		charLen = 0;


   /***************************************************************
    * Parses the configuration file.
    * @param configFile		The configuration file
    * @exception        	org.xml.sax.SAXParseException,
    *				org.xml.sax.SAXException,
    *				java.lang.Exception
    */
   void parse (String configFile) 
		throws SAXParseException, SAXException, Exception 
   {
	String parserName = DEFAULT_PARSER_NAME;

	try {
	    XMLReader parser = (XMLReader) Class.forName
					(parserName).newInstance ();
	    parser.setFeature ("http://xml.org/sax/features/validation",
					setValidation);
	    parser.setFeature ("http://xml.org/sax/features/namespaces", 
					setNameSpaces);
	    parser.setFeature ("http://apache.org/xml/features/validation/schema",
					setSchemaSupport);

	    parser.setContentHandler (this);
	    parser.setErrorHandler (this);
	    String uri = "file:" + new File (configFile).getAbsolutePath ();
	    System.out.println ("config file is " + uri);
	    parser.parse (uri);
	} catch (SAXParseException err) {
	    System.out.println ("Parsing error: " 
				+ "line " + err.getLineNumber ()
				+ ", uri " + err.getSystemId ()
				+ "\n   " + err.getMessage ());
	    
	    throw err;
	} catch (SAXException e) {
	    e.printStackTrace (); throw e;
	} catch (Exception e) {
	    e.printStackTrace (); throw e;
	}

    }; // parse


    /***************************************************************
     * Returns the framework attribute.
     * @return String		The framework
     */
    String getFramework ()  
    { 
	return framework; 

    }; // getFramework


    /***************************************************************
     * Returns the driver class name.
     * @return String		The driver class name
     */
    String getDriverClassName ()  
    { 
	return driverClassName; 

    }; // getDriverClassName


    /***************************************************************
     * Returns the database URL.
     * @param String		The database URL
     */
    String getUrl ()  
    { 
	return url; 

    }; // getUrl

                        
    /***************************************************************
     * Handles error: treats validation errors as fatal.
     * @param e		The SAXParseException that throws the error
     * @exception	org.xml.sax.SAXParseException
     */
    public void error (SAXParseException e) throws SAXParseException 
    {
	framework = null;
	driverClassName = null;
	url = null;
	System.out.println ("Validation error: " + e.getMessage ());
	e.printStackTrace ();
	throw e;

    }; // error


    /***************************************************************
     * Handles warning.
     * @param err	The SAXParseException that throws the warning
     * @exception       org.xml.sax.SAXParseException
     */
    public void warning (SAXParseException err) throws SAXParseException 
    {
	System.out.println ("Warning: " 
			+ "line " + err.getLineNumber ()
			+ ", uri " + err.getSystemId ()
			+ "\n   " + err.getMessage ());
    }; // warning


    /***************************************************************
     * Handles characters.
     * @param ch	The characters from the XML document
     * @param start	The start position in the character array
     * @param length	The number of characters to read from the array
     */
    public void characters (char [] ch, int start, int length)
    {
	System.out.println ("Just received: " + new String (ch, start, length));

	int i = start, j = 0;
	while (i < start + length) buffer [j++] = ch [i++];
	charLen = length;
    };
 

    /***************************************************************
     * Handles end of an element.
     * @param uri	The Namespace URI, or the empty string if the element has
     *			no Namespace URI or if Namespace processing is not being
     *			performed.
     * @param localname	The local name (without prefix), or the empty string if
     *			Namespace processing is not being performed.
     * @param rawname	The qualified XML 1.0 name (with prefix), or the empty
     *			string if qualified names are not available.
     * @exception	org.xml.sax.SAXException
     */
    public void endElement (String uri, String localname, String rawname)
			throws SAXException 
    {
	System.out.println ("End of element: " + rawname);

	if (rawname.toLowerCase ().equals ("framework")) {
		framework = new String (buffer, 0, charLen);
	} else if (rawname.toLowerCase ().equals ("driverclassname")) {
		driverClassName = new String (buffer, 0, charLen);
	} else if (rawname.toLowerCase ().equals ("url")) {
		url = new String (buffer, 0, charLen);
	} // if

   }; // endElement

}; // class ParseConfig
