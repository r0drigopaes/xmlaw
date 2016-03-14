/******************************************************************
 * @(#) FederateInserter.java   1.3
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

import java.sql.*;
import java.io.IOException;

import jsim.jmessage.*;

/***************************************************************
 * The FederateInserter class inserts an Store object
 * into the database.
 */
public class FederateInserter 
{
    /***************************************************************
     * Inserts an object into the database using an DBAccess object.
     * @param dbaccess		The DBAccess object
     * @param data		The data to be stored
     * @exception        java.lang.Exception
     */
     public static void insert (DBAccess dbaccess, Object data)
		throws Exception 
     {	
	Connection conn = dbaccess.getConnection ();
	Integer fedid = new Integer (dbaccess.getSeq ());
	Integer sid = ((Store) data).getScenarioID ();
	Simulate agentData = ((Store) data).getAgentData ();
	ModelData modelData = ((Store) data).getModelData ();
	String    mName = modelData.getModelName ();
	NodeProps [] props = modelData.getNodeProps ();
	NodeStats [] stats = modelData.getOutputStats ();

	// prepare the statement once
	String insertStr = "INSERT INTO federate VALUES (?, ?, ?, ?)";
	PreparedStatement ps = conn.prepareStatement (insertStr);
	if (ps == null) {
	    System.out.println ("FederateInserter: cannot create prepared statement");
	    return;
	}; // if

	// set the parameter values  
	ps.setObject (1, fedid);
	ps.setObject (2, sid);
	ps.setObject (3, agentData);
	ps.setObject (4, mName);
	ps.executeUpdate ();
	ps.close ();

	// insert into the modelnodes table
	insertStr = "INSERT INTO  modelnode VALUES (?, ?, ?)";
	ps = conn.prepareStatement (insertStr);
	for (int i = 0; i < props.length; i++) {
	   ps.setObject (1, fedid);
	   ps.setObject (2, props [i]);
	   ps.setObject (3, stats [i]);
	   ps.executeUpdate ();
	}; // for
	ps.close ();

    }; // insert


    /***************************************************************
     * Main method for debugging purposes.
     */
    public static void main (String [] args) 
    {
	DBConnection dbc = new DBConnection ();
	Connection conn = null;

	try {
		conn = dbc.getConnection ("default_config.xml", "", "");
		DBAccess dbaccess = new DBAccess (conn);
		FederateInserter.insert (dbaccess, null);
	} catch (Exception e) {
		System.out.println ("FederateInserter: " + e.getMessage ());
		e.printStackTrace ();
	} finally {
		dbc.closeConnection ();
	}

   }; // main method for debugging purpuse only

}; // class FederateInserter
