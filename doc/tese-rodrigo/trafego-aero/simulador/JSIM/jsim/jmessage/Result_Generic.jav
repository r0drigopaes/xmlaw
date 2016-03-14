/******************************************************************
 * @(#) Result.java   1.3
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
import java.sql.*;
import java.util.*;

/***************************************************************
 * The Result class is a JSIM message type representing the query
 * result returned by the DBAgent. It may contain a simple message
 * indicating if the query is successful or not. Or it may encapsulate
 * a Java ResultSet object. In the former case, the action type would
 * be "Information". In the latter case, the action type would be
 * either "Sequence number" or "Normal result".
 */
public class Result extends Message 
{
   /**
    * The number of columns
    */
   private int			nCols = 0;

   /**
    * The number of rows
    */
   private int			nRows = 0;

   /**
    * The column labels
    */
   private String []		colLabel = null;

   /**
    * The value
    */
   private Object [][]		value = null;

   /**
    * The information or message from the DBAgent
    */
   private String		info = null;

	/**
	 * Unique identifier of the serialized object
	 */
	private static final long serialVersionUID = 1392845080249194813L;

   /***************************************************************
    * Constructs a Result instance by action type and information.
    * @param actionType	The type of result
    * @param info		The message
    */
   public Result (String actionType, String info)
   {
	this.actionType = actionType;
	this.info = info;

   }; // Result constructor


   /***************************************************************
    * Constructs a Result instance by action type and a ResultSet object.
    * @param actionType		The result type
    * @param rs			The ResultSet object
    */
   public Result (String actionType, ResultSet rs) throws SQLException 
   {
	System.out.println ("Result constructor: extracting result meta data");

	this.actionType = actionType;
	ResultSetMetaData meta = rs.getMetaData ();
	nCols = meta.getColumnCount ();
	colLabel = new String [nCols];
	// Vector [] temp = new Vector [nCols];
	ArrayList<Vector> temp = new ArrayList<Vector> (nCols);
	for (int i = 0; i < nCols; i++) {
		colLabel [i] = meta.getColumnLabel (i + 1);
		// temp [i] = new Vector ();
		temp.add(i, new Vector<?> ());
	}; // for

	System.out.println ("Result constructor: extracting result data");
	while (rs.next ()) {
		nRows++;
		for (int i = 0; i < nCols; i++) 
		{
			// temp [i].add (rs.getObject (i + 1));
			temp.get(i).add(rs.getObject (i + 1));	
		}
	}; // while

	value = new Object [nRows][nCols];
	for (int i = 0; i < nRows; i++)
	   for (int j = 0; j < nCols; j++) {
		// value [i][j] = temp [j].get (i);
		   value [i][j] = temp.get (j).get (i);
	}; // for

   }; // Result constructor


   /***************************************************************
    * Returns the number of columns.
    * @return int	The number of columns
    */
   public int		getColumnCount ()  
   { 
	return nCols;

   }; // getColumnCount


   /***************************************************************
    * Returns the number of rows.
    * @return int       The number of rows
    */
   public int		getRowCount () 
   { 
	return nRows; 

   }; // getRowCount


   /***************************************************************
    * Returns the number of rows.   
    * @return String [][]	The column labels
    */
   public String []	getColumnLabels () 
   { 
	return colLabel; 

   }; // getColumnLabels


   /***************************************************************
    * Returns the value array.
    * @return Object [][]	The array of value (rows:columns)
    */
   public Object [][]	getValue ()  
   { 
	return value; 

   }; // getValue


   /***************************************************************
    * Returns the information/message.
    * @return info	The information/message
    */
   public String        getInfo () 
   { 
	return info; 

   }; // getInfo


   /***************************************************************
    * Meets the Java serialization requirements.
    * @param out	The ObjectOutputStream
    * @exception 	java.io.IOException
    */
   public void writeObject (ObjectOutputStream out)
		throws IOException 
   {
	out.writeObject (actionType);
	out.writeInt (nCols);
	out.writeInt (nRows);
	for (int i = 0; i < nCols; i++)
		out.writeObject (colLabel [i]);
	for (int i = 0; i < nRows; i++)
	   for (int j = 0; j < nCols; j++)
		out.writeObject (value [i][j]);
	out.writeObject (info);

   }; // writeObject


   /***************************************************************
    * Meets the Java serialization requirements.
    * @param in		The ObjectInputStream
    * @exception	java.io.IOException, java.lang.ClassNotFoundException
    */
   public void readObject (ObjectInputStream in)
		throws IOException, ClassNotFoundException 
   {
	actionType = (String) in.readObject ();
	nCols = in.readInt ();
	nRows = in.readInt ();
	colLabel = new String [nCols];
	value = new Object [nCols][nRows];
	for (int i = 0; i < nCols; i++)
	   colLabel [i] = (String) in.readObject ();
	for (int i = 0; i < nRows; i++)
	   for (int j = 0; j < nCols; j++)
		value [i][j] = in.readObject ();
	info = (String) in.readObject ();

   }; // readObject


   /***************************************************************
    * Meets the Koml XML serialization requirements.
    * @param in         The ObjectInputStream
    * @exception        java.io.IOException, java.lang.ClassNotFoundException
   public static void readObject (ObjectInputStream in)
		throws IOException, ClassNotFoundException
   {
	XMLSerializer.readObjectResult (in);

   }; // readObject
    */


   /*************************************************************** 
    * Returns an XML fragment representing this instance of Result.
    * @return String	The XML fragment
    */
   public String toString ()
   {
	if (actionType.toLowerCase ().equals ("information"))  return new String ("");

	String xmlStr = "";

	xmlStr += "<QueryResult>";
	for (int i = 0; i < nRows; i++) {
	   xmlStr += "\n  <Row id='" + i + "'>";
	   for (int j = 0; j < nCols; j++) {
	      xmlStr += "\n    <" + colLabel [j] + ">";
	      xmlStr += value [i][j].toString ();
	      if (value [i][j].toString ().length () > 20) {
	          xmlStr += "\n    </" + colLabel [j] + ">";
	      } else {
		  xmlStr += "</" + colLabel [j] + ">";
	      }; // if
	   }; // for
	   xmlStr += "\n  </Row>";
	}; // for
	xmlStr += "\n</QueryResult>";
	
	return xmlStr;

   }; // toString


   /***************************************************************
    * Prints out the information or query result for debugging purposes.
    */
   public void print () 
   {
	if (actionType.toLowerCase ().equals ("information")) {
	    System.out.println (info);
	    return;
	}; // if

	System.out.println ("nCols: " + nCols + " | nRows: " + nRows);
	System.out.println ("--------------------------------------");
	for (int i = 0; i < nCols; i++)
		System.out.print (colLabel [i] + "\t");

	System.out.print ("\n--------------------------------------");
	for (int i = 0; i < nRows; i++) {
	   System.out.println ();
	   for (int j = 0; j < nCols; j++) {
	   	System.out.print (value [i][j].toString () + "\t");
	   } // for
	} // for

   }; // print

}; // class Result
