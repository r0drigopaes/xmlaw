/******************************************************************
 * @(#) DBAccess.java   1.3
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

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import java.util.Vector;

import jsim.jmessage.*;


/***************************************************************
 * The DBAccess class encapsulates all basic database access operations.
 */
public class DBAccess
{
   /**
    * An object that knows how to create a database connection.
    */
   private final DBConnection dbc = new DBConnection ();

   /**
    * The Connection object
    */
   private final Connection conn;


   /***************************************************************
    * Constructs a DBConnection instance by a DBConnectionInfo object.
    * @param connInfo	The information needed to connect to the database.
    * @exception	java.lang.Exception
    */
   public DBAccess (DBConnectionInfo connInfo) throws Exception 
   {

	try {
		conn = dbc.getConnection (connInfo);
	} catch (Exception e) {
		System.out.println ("DBAccess: " + e.getMessage ());
		throw e;
	}

   }; // DBAccess constructor


   /***************************************************************
    * Constructs a DBAccess instance by a configuration file, username,
    * and password.
    * @param configFile		The configuration file containing the database
    *				URL, JDBC driver name, and connection framework
    * @param username		The user name
    * @param passwd		The password
    */
   public DBAccess (String configFile, String username, String passwd)
		throws Exception
   {
	try {
		conn = dbc.getConnection (configFile, "", "");
	} catch (Exception e) {
		System.out.println ("DBAccess: " + e.getMessage ());
		throw e;
	}

   }; // DBAccess constructor


   /***************************************************************
    * Constructs a DBAccess instance by a Connection object.
    * @param	Connection	The Connection object
    */
   public DBAccess (Connection conn) 
   {
	this.conn = conn;

   }; // DBAccess


   /***************************************************************
    * Returns the Connection object.
    * @return Connection	The Connection object
    */
   Connection getConnection () 
   {  
	return conn; 

   }; // Connection


   /***************************************************************
    * Closes the database connection.
    */
   void closeConnection () 
   {
	dbc.closeConnection ();

   }; // close 


   /***************************************************************
    * Updates a relational database by executing a SQL statement.
    * @param String	The SQL statement
    * @exception	java.lang.Exception
    */
   void updateRelationalData (String sqlStr) throws Exception 
    {
	// execute the update
	Statement st = null;
	try {
		st = conn.createStatement ();
		st.executeUpdate (sqlStr);
	} catch (Exception e) {
		throw e;
	} finally {
		if (st != null) st.close ();
	}

	System.out.println ("updateRelationalData: update success!");

   }; // updateRelationalData


   /***************************************************************
    * Updates an object or object-relational database by executing
    * a SQL statement.
    * @param obj	The object containing the data to be stored.
    * @exception        java.lang.Exception
    */
   void updateObject (Object obj) throws Exception 
   {
	Statement st = null;
	try {
		st = conn.createStatement ();
		new Updater ().update (this, obj);
	} catch (Exception e) {
		throw e;
	} finally {
		if (st != null) st.close ();
	}

	System.out.println ("updateObjects: update success!");

   }; // updateObject


   /***************************************************************
    * Gets current seq value, increments it by 1, and reflects the
    * increase back to the database.
    * @return	int	The current seq value
    * @exception        java.lang.Exception
    */
   int getSeq () throws Exception 
   {
	String queryStr = "select seq from sequence";
	try {
		ResultSet rs = select (queryStr);
		rs.next ();
		int curr = rs.getInt (1);
		rs.close ();
		int next = curr + 1;
		updateRelationalData ("update sequence set seq = " + next);
		return curr;
	} catch (Exception e) {
		throw e;
	} // try
	
   }; // getSeq


   /***************************************************************
    * Executes a SELECT statement and returns the ResultSet object.
    * @exception        java.lang.Exception
    */
   ResultSet select (String sqlStr) throws Exception 
   {
	// select from db
	Statement st = null;
	ResultSet rs = null;
	try {
		st = conn.createStatement ();
		rs = st.executeQuery (sqlStr);
	} catch (Exception e) {
		throw e;
	}

	System.out.println ("select: success!");
	return rs;

   }; // select


   /***************************************************************
    * Main method for debugging purposes only.
    */
   public static void main (String [] args)
   {
	DBAccess dbaccess = null;
	ResultSet rs = null;

	try {
	    dbaccess = new DBAccess ("default_config.xml", "", "");
	    rs = dbaccess.select ("select seq from sequence");
	    ResultSetMetaData meta = rs.getMetaData ();
	    int nCols = meta.getColumnCount ();
	    System.out.println ("Number of columns is " + nCols);
	    rs.next (); 
	    System.out.println ("test: seq = " + rs.getInt (1));
	} catch (Exception e) {
	    System.out.println ("main: " + e.getMessage ());
	    e.printStackTrace ();
	    return;
	} finally {
	    if (dbaccess != null)  dbaccess.closeConnection ();
	}

   }; // main


}; // class DBAccess
