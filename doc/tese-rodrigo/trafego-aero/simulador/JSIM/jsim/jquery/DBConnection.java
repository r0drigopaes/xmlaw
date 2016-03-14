/******************************************************************
 * @(#) DBConnection.java   1.3
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

import jsim.jmessage.*;

/***************************************************************
 * The DBConnection class abstracts the establishment of a database
 * connection to the Cloudscape database. If a different database system
 * is used, slight changes need to be made to this class (especially
 * the connection framework part).
 */
public class DBConnection {

   /**
    * The Connection object
    */
   private Connection conn = null;

   /**
    * The connection framework: "embedded" or "rmijdbcclient"
    */
   private String framework = null;

   /**
    * The JDBC driver class name
    */
   private String driver = null;

   /**
    * The databse URL
    */
   private String url = null;

   /**
    * The user name
    */
   private String username = null;

   /**
    * The password
    */
   private String passwd = null;


   /***************************************************************
    * Default connection attributes.
    */
   private void useDefaults () 
   {

/****   If using embedded connection mode
	framework = "embedded";
	driver = "COM.cloudscape.core.JDBCDriver";
	url = "jdbc:cloudscape:JsimDB;";
****/

	// uses RmiJdbc connection mode
	framework = "rmijdbcclient";
	driver = "COM.cloudscape.core.RmiJdbcDriver";
	url = "jdbc:cloudscape:rmi://localhost:1099/JsimDB;";
	username = "";
	passwd = "";	

   }; // useDefaults


   /***************************************************************
    * Configures the connection attributes.
    * @param framework	The connection framework
    * @param driver	The JDBC driver class name
    * @param url	The database URL
    * @param username	The user name
    * @param passwd	The password
    */
   private void configure (String framework, String driver, 
		String url, String username, String passwd)
   {
//	useDefaults ();
	this.framework = framework;
	this.driver = driver;
	this.url = url;
	this.username = username;
	this.passwd = passwd;

   }; // configure


   /***************************************************************
    * Returns a Connection object.
    * @param configFile		The configuration file
    * @param username		The user name
    * @param password		The password
    * @return Connection	The newly created Connection object
    * @exception 		java.lang.Exception
    */
   Connection getConnection (String configFile,
			     String username,
			     String password) 
		throws Exception 
   {
	// configure the connection
	ParseConfig parser = new ParseConfig ();
	parser.parse (configFile);
	configure (parser.getFramework (), parser.getDriverClassName (), 
		   parser.getUrl (), username, passwd);
	return getConnection ();

   }; // getConnection


   /***************************************************************
    * Returns a Connection object created using a DBConnectionInfo object.
    * @param connInfo		A DBConnectionInfo object
    * @return Connection        The newly created Connection object
    * @exception                java.lang.Exception
    */
   Connection getConnection (DBConnectionInfo connInfo)
		throws Exception
   {
	configure (connInfo.getFramework (), connInfo.getDriverClassName (),
		   connInfo.getUrl (), connInfo.getUserName (),
		   connInfo.getPassword ());
	return getConnection ();

   }; // getConnection


   /***************************************************************
    * Returns the current Connection object.
    * @return			The current Connection object
    * @exception                java.lang.Exception
    */
   private Connection getConnection ()  throws Exception
   {
	if ((username != null && username.length () > 0) &&
	    (passwd != null && passwd.length () > 0)) {
	    System.out.println ("Username and password will be validated.");
	    url += ";user=" + username + ";password=" + passwd;
	} // if

	try {
		Class.forName (driver).newInstance ();
                System.out.println ("Loaded the " + framework + " JDBC driver");
		conn = DriverManager.getConnection (url);
		conn.setAutoCommit (false);

	} catch (Exception e) {
		System.out.println ("Cannot connect to database: " + url);
		throw e;
	}

	System.out.println ("Connection opened successfully!");
	return conn;

   }; // getConnection


   /****************************************************************
    * Closes connection to Cloudscape if in embedded mode, else leaves the
    * the connectin open for other clients.
    */ 
   void closeConnection () 
   {
	if (conn == null)  return;

	try {
		conn.commit ();
		conn.close ();

		if (framework.toLowerCase ().equals ("embedded")) {
			DriverManager.getConnection
				("jdbc:cloudscape:;shutdown=true");
		}
	} catch (SQLException e) {
		System.out.println ("Database shut down success!");
	} finally {
		framework = null;
		driver = null;
		url = null;
		username = null;
		passwd = null;
		conn = null;
	}

   }; // closeConnection


   /****************************************************************
    * Prints out the connection attribute values for debugging purposes.
    */
   void printConfig () 
   {

	System.out.println ("DBConnection:" +
   			"\nframework = " + framework +
			"\ndriver = " + driver +
			"\nurl = " + url +
			"\nusername = " + username);
  
   }; // printConfig 


   /****************************************************************
    * Main method for debugging purposes.
    */
   public static void main (String [] args) 
   {
	try {
		DBConnection dbc = new DBConnection ();
		Connection conn = dbc.getConnection ("default_config.xml", "", "");
		Statement st = conn.createStatement ();
		ResultSet rs = st.executeQuery ("select seq from sequence");
		ResultSetMetaData meta = rs.getMetaData ();
		int nCols = meta.getColumnCount ();
		System.out.println ("Number of columns is " + nCols);
		dbc.printConfig ();
		dbc.closeConnection ();
	} catch (Exception e) { 
		e.printStackTrace ();
	}

   }; // main

}; // class DBConnection
