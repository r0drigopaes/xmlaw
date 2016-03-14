/******************************************************************
 * @(#) XMLSerializer.java   1.3
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

package jsim.jmessage;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.net.*;

import org.xml.sax.*;

//import fr.dyade.koala.xml.koml.*;
//import fr.dyade.koala.serialization.GeneratorOutputStream;
//import fr.dyade.koala.serialization.ObjectOutputHandler;

/***************************************************************
 * The XMLSerializer class contains static methods to
 * serialize/deserialize Java objects to and from an XML string or file.
 * Note Koala relies on the Java serialization mechanism to work. All
 * target Java objects must be serializable. This class also has a method
 * to write an XML string into a file.
 */
public class XMLSerializer 
{

    /***************************************************************
     * Serializes a vector of Java objects into an XML string.
     * @param v		The vector of Java objects to be serialized.
     * @return String	The resulting XML string
     * @exception	java.io.IOException, java.lang.ClassNotFoundException
     */
    public static String serialize (Vector v)
		throws IOException, ClassNotFoundException 
    {
/*******
        ByteArrayOutputStream baos = new ByteArrayOutputStream ();
        KOMLSerializer ob = new KOMLSerializer (baos, false);
        for (int i = 0; i < v.size (); i++) {
                ob.addObject (v.get (i));
        }
        ob.close();
        String xmlStr = baos.toString ();
        return xmlStr;
*******/

        return v.toString ();

    }; // serialize


    /***************************************************************
     * Serializes a vector of Java objects into an XML file and returns
     * the XML string representation.
     * @param v		The vector of Java objects to be serialized
     * @param fname	The name of the target XML file
     * @return String	The resulting XML string
     * @exception       java.io.IOException, java.lang.ClassNotFoundException
     */
    public static String serialize (Vector v, String fname) 
		throws IOException, ClassNotFoundException 
    {
/*******
        KOMLSerializer ob = new KOMLSerializer (fname, false);
        for (int i = 0; i < v.size (); i++) {
                ob.addObject (v.get (i));
        } // for
        ob.close ();

	String xmlStr = "", line = null;
	BufferedReader br = new BufferedReader (new FileReader (fname));
	while ((line = br.readLine ()) != null)  xmlStr += line;
	br.close ();
	return xmlStr;
*******/

        return v.toString ();

    }; // serialize


    /***************************************************************
     * Deserializes from an XML string back into Java objects.
     * @param xmlStr	The XML string
     * @return		The resulting vector of Java objects
     * @exception       java.io.IOException,
     *			java.io.EOFException,
     *			java.lang.ClassNotFoundException
     */
    public static Vector deserializeFromString (String xmlStr)
              throws ClassNotFoundException, IOException, EOFException 
    {
        Vector v = new Vector ();
/*******
        ByteArrayInputStream bais = new ByteArrayInputStream (xmlStr.getBytes ());
        KOMLDeserializer koml = new KOMLDeserializer  (bais, false);

        Object obj = null;
        try {
                while (true) {
                        obj = koml.readObject ();
                        v.add (obj);
                } 
        } catch (IOException e) {
                throw e;
        } finally {
                koml.close();
                return v;
        } // try
*******/

        return v;

    }; // deserializeFromString


    /***************************************************************
     * Deserializes from an XML file into Java objects.
     * @param fname	The XML file name
     * @return Vector	The resulting vector of Java objects
     * @exception	java.io.IOException,
     *			java.io.EOFException,
     *			java.lang.ClassNotFoundException
     */
    public static Vector deserializeFromFile (String fname)
             throws ClassNotFoundException, IOException, EOFException 
    {
        Vector v = new Vector ();
/*******
        KOMLDeserializer koml = new KOMLDeserializer  (fname, false);
    
        Object obj = null;
        try {
                while (true) {
                        obj = koml.readObject ();
                        v.add (obj);
                }
        } catch (IOException e) {
                throw e;
        } finally {
                koml.close();
                return v;
        } // try
*******/

        return v;

    }; // deserialize
        

    /***************************************************************
     * Writes the XML string into a file. 
     * @param xmlString  The XML string to be written to a file
     * @param fname      The name of the target XML file
     */
    public static void generateXML (String xmlString, String fname)
    {
        FileOutputStream fos;
        byte [] b = xmlString.getBytes ();   
    
        try {
            fos = new FileOutputStream (fname);
            fos.write (b);  fos.flush ();  fos.close ();
        } catch (Exception e) {
            System.out.println (e.getMessage ());
            e.printStackTrace ();
        } // try
        
    }; // generateXML


    /***************************************************************
     * Meets the Koala XML serialization requirements
     * @param  in       The XML serialization GeneratorInputStream 
     *			provided by KOML
     * @exception       java.io.IOException
     */
    public static void readObjectStore (ObjectInputStream in)
		throws IOException, ClassNotFoundException {
		
/*******
	fr.dyade.koala.serialization.GeneratorInputStream kin = (fr.dyade.koala.serialization.GeneratorInputStream) in;
*******/
        ObjectInputStream kin = in;

        kin.readObject ();
        kin.readObject ();
        kin.readObject ();
        kin.readObject ();

    }; // readObject


   /***************************************************************
    * Meets the Koml XML serialization requirements.
    * @param in         The ObjectInputStream
    * @exception        java.io.IOException, java.lang.ClassNotFoundException
    */
   public static void readObjectResult (ObjectInputStream in)
		throws IOException, ClassNotFoundException
   {
/*******
	fr.dyade.koala.serialization.GeneratorInputStream kin = (fr.dyade.koala.serialization.GeneratorInputStream) in;   
*******/
        ObjectInputStream kin = in;

	kin.readObject ();
	int nCs = kin.readInt ();
	int nRs = kin.readInt ();
	for (int i = 0; i < nCs; i++)
	   kin.readObject ();
	for (int i = 0; i < nRs; i++)
	   for (int j = 0; j < nCs; j++)
		kin.readObject ();
	kin.readObject ();

    }; // readObject
   

    /***************************************************************
     * Meets the Koala XML serialization requirements
     * @param  in	The XML serialization GeneratorInputStream provided by KOML
     * @exception	java.io.IOException, java.lang.ClassNotFoundException
     */
    public static void readObjectModel (ObjectInputStream in)
		throws IOException, ClassNotFoundException
    {
/*******
	fr.dyade.koala.serialization.GeneratorInputStream kin = (fr.dyade.koala.serialization.GeneratorInputStream) in;   
*******/
        ObjectInputStream kin = in;

        kin.readObject ();
        int nNodes = (int) kin.readInt ();
	for (int i = 0; i < nNodes; i++) {
		kin.readObject ();
		kin.readObject ();
	}; // for

    }; // readObject

   
    /***************************************************************
     * Main method for testing purposes only.
     */
    public static void main (String[] args) {
    
        Vector v = new Vector ();
        v.add (new Date ());
      
        try {
                String xmlStr = serialize (v);
                System.out.println ("Serializing into string:\n" + xmlStr);
                v = deserializeFromString (xmlStr);
                System.out.println ("Deserializing from String: " + v);
                xmlStr = serialize (v, "test.xml");
                System.out.println ("Serializing into file: test.xml" + 
				    "\n\txml string is:\n" + xmlStr);
                v = deserializeFromFile ("test.xml");
                System.out.println ("Deserializing from file: " + v);
        } catch (Exception e) {
                e.printStackTrace();
        }; // try

    }; // main

}; // class XMLSerializer

