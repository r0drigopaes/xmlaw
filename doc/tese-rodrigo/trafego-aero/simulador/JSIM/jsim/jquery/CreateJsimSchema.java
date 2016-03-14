/******************************************************************
 * @(#) CreateJsimSchema.java   1.3
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

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;

import java.io.FileWriter;
import java.io.File;

/***************************************************************
 * The CreateJsimScheam class creates a schema for the JSIM database
 * and generates SQL scripts for the schema.
 */
public class CreateJsimSchema 
{
   /**
    * The default configuration file for connecting to the database.
    */
   private final String DEFAULT_CONFIG_FILE = "create_jsimschema_config.xml";

   /**
    * The total number of tables to be created
    */
   private final int    NTABLES = 3;

   /**
    * The names of each table
    */
   private final String [] tableName = new String [NTABLES];

   /**
    * The create statement for creating each table
    */
   private final String [] sqlStr = new String [NTABLES];

   /**
    * An object that can create a database connection
    */
   private final DBConnection dbc = new DBConnection ();

   /**
    * The Connection object
    */
   private Connection conn = null;


  /***************************************************************
   * Constructs a CreateJsimSchema instance by preparing the create table
   * statements.
   * @param username	The user name for the database
   * @param passwd	The password for the database
   */
   public CreateJsimSchema (String username, String passwd) 
   {
	try {
	    conn = dbc.getConnection (DEFAULT_CONFIG_FILE, username, passwd);
	} catch (Exception e) {
	    System.out.println ("CreateJsimSchema: " + e.getMessage ());
	    dbc.closeConnection ();
	    System.exit (0);
	}

	tableName [0] = new String ("sequence");
	tableName [1] = new String ("federate");
	tableName [2] = new String ("modelnode");

	sqlStr [0] = new String ("CREATE TABLE " + tableName [0] + " (" +
				 "\nseq		int\n)");
	sqlStr [1] = new String ("CREATE TABLE " + tableName [1] + " (" +
			"\nFederateID	INTEGER PRIMARY KEY," +
			"\nScenarioID	INTEGER," +
			"\nAgentData	SERIALIZE(jsim.jmessage.Simulate)," +
			"\nModelName	VARCHAR(20)\n)");
	sqlStr [2] = new String ("CREATE TABLE " + tableName [2] + " (" +
			"\nFederateID	INTEGER REFERENCES federate(FederateID)," +
			"\nProps	SERIALIZE(jsim.jmessage.NodeProps)," +
			"\nStats	SERIALIZE(jsim.jmessage.NodeStats)\n)");

   }; // CreateJsimDB constructor


   /***************************************************************
    * Generates SQL scripts for creating the tables.
    * @exception	java.lang.Exception
    */
   void generateSqlScripts () throws Exception 
   {
	FileWriter fw = null;
	String currdir = System.getProperty ("user.dir");
	
	System.out.println ("Generating sql scripts...");

	try {
		File f = new File (currdir, "jsim.sql");
		fw = new FileWriter (f);
		for (int i = 0; i < NTABLES; i++) {
			fw.write (sqlStr [i], 0, sqlStr [i].length ());
			fw.write (new String (";\n\n"), 0, 3);
		} // for
	} catch (Exception e) {
		System.out.println ("generateSqlScripts: " + e.getMessage ());
	} finally {
		if (fw != null)  fw.close ();
	}

   }; // generateSqlScripts


   /***************************************************************
    * Drops the schema.
    * @exception        java.lang.Exception
    */
   void dropSchema () throws Exception
   {
        Statement st = null;

	System.out.println ("Dropping JsimDB schema...");

	try {
           st = conn.createStatement ();
           st.executeUpdate ("drop table sequence");
	   st.executeUpdate ("drop table modelnode");
	   st.executeUpdate ("drop table federate");
	   conn.commit ();
        } catch (Exception e) {
	   System.out.println ("dropSchema: " + e.getMessage ());
	   return;
        } finally {
           if (st != null) st.close ();
        }

   }; // dropSchema


   /***************************************************************
    * Creates the schema for the JSIM database.
    * @exception	java.lang.Exception
    */
   void createSchema () throws Exception 
   {
	Statement st = null;

	System.out.println ("Creating JsimDB schema...");

	try {
		st = conn.createStatement ();
		for (int i = 0; i < NTABLES; i++)
			st.executeUpdate (sqlStr [i]);
		conn.commit ();
	} catch (Exception e) {
		System.out.println ("createSchema: " + e.getMessage ());
		return;
	} finally {
		if (st != null) st.close ();
	}

   }; // createSchema


   /***************************************************************
    * Initializes the sequence table by setting the initial seq to 0.
    * @exception        java.lang.Exception
    */
   void initSequence () throws Exception
   {
	Statement st = null;

	System.out.println ("Initializing sequence table...");

	try {
	    st = conn.createStatement ();
	    String s = "insert into sequence values (0)";
	    st.executeUpdate (s);
	    conn.commit ();
  	} catch (Exception e) {
	    System.out.println ("initSequence: " + e.getMessage ());
	    return;
	} finally {
	    if (st != null)  st.close ();
	}

   }; // initSequence


   /***************************************************************
    * Closes the database connection.
    */
   void closeConnection ()
   {
	dbc.closeConnection ();

   }; // closeConnection


   /***************************************************************
    * Calls the CreateJsimSchema methods to create the tables.
    * @param	args	The commandline arguments, where the username
    *			password are obtained. If no arguments supplies,
    *			default username and password are empty string.
    */
   public static void main (String [] args) 
   {
	String username = "", passwd = "";
	if (args.length == 2) {
		username = args [0];
		passwd = args [1];
	} // if

	try {
	    CreateJsimSchema createSchema = new CreateJsimSchema (username, passwd);
	    createSchema.dropSchema ();
	    createSchema.generateSqlScripts ();
	    createSchema.createSchema ();
	    createSchema.initSequence ();
	    createSchema.closeConnection ();
	} catch (Throwable t) {
	    t.printStackTrace ();
	}

   }; // main

}; // class CreateJsimSchema
