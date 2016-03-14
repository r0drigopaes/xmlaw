/******************************************************************
 * @(#) DBAgent.java   1.3
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

import java.awt.*;
import javax.swing.*;
import java.util.Vector;
import java.sql.*;

import jsim.util.Trace;
import jsim.jmessage.*;


/***************************************************************
 * The DBAgent class handles database access requests from the outside.
 */
public class DBAgent extends JPanel
		     implements JsimBean,
				JsimListener
{
    /**
     * Width of panel.
     */
    private static final int    WIDTH = 200;
 
    /**
     * Height of panel.
     */
    private static final int    HEIGHT = 50;

    /**
     * Vector for registration of JsimEvent listeners
     */
    private final Vector 	jsimListeners = new Vector ();

    /**
     * JSIM system property: use_xml
     */
    private boolean		use_xml = false;
       
    /**
     * JSIM system property: generate_xml_files
     */
    private boolean		generate_xml_files = false;

    /**
     * Tracing messages.
     */
    private final Trace		trc = new Trace ("jquery", "DBAgent");


   /***************************************************************
    * Constructs a DBAgent instance.
    */
    public DBAgent ()
    {
	setLayout (new BorderLayout ());
	add (new JLabel ("DBAgent", JLabel.CENTER),
			BorderLayout.CENTER);
	setBorder (BorderFactory.createEmptyBorder (10, 10, 10, 10));
	setSize (WIDTH, HEIGHT);
	setVisible (true);

        String jsimprop = System.getProperty ("jsim.use_xml", "false");   
        if (jsimprop.toLowerCase ().equals ("true")) {
            use_xml = true;
        } // if
     
        jsimprop = System.getProperty ("jsim.generate_xml_files", "false");
        if (jsimprop.toLowerCase ().equals ("true")) { 
            generate_xml_files = true;
        } // if
	
    }; // DBAgent


    /***************************************************************
     * Adds a JsimListener to the queryResultsListeners queue.
     * @param l the JsimListener
     */
    public synchronized void addJsimListener (JsimListener l)
    {
        jsimListeners.add (l);
     
    } // addJsimListener
       
    
    /***************************************************************
     * Removes a JsimListener from the queryListeners queue.
     * @param l the JsimListener to be removed
     */
    public synchronized void removeJsimListener (JsimListener l)
    {
        jsimListeners.remove (l);
            
    } // removeJsimListener


    /***************************************************************
     * Fires a JsimEvent.
     * @param evt	The JsimEvent
     */
    public void fireJsimEvent (JsimEvent evt)
    {
	JsimListener	target;
        Vector		allTargets;
     
        trc.show ("fireJsimEvent", "now");
        
        synchronized (this) {
            allTargets = (Vector) jsimListeners.clone ();
        }; // synchronized
    
        for (int i = 0; i < allTargets.size (); i++) {  
            target = (JsimListener) allTargets.get (i);
            target.notify (evt);
        }; // for
     
    }; // fireJsimEvent


    /***************************************************************
     * Selectively handles incoming events: QUERY_EVT, STORE_EVT.
     * @param evt	The incoming JsimEvent
     */
    public void notify (JsimEvent evt)
    {
        trc.show("notify", "Event type: " + evt.getEventType ());

        Object handback = evt.getRegistrationObject ();
        if (evt.getID () == EventMap.INQUIRE_EVT) {
        } else if (evt.getID () == EventMap.INFORM_EVT) {
        } else if (evt.getID () == EventMap.CHANGE_EVT) {
        } else if (evt.getID () == EventMap.CHANGED_EVT) {
        } else if (evt.getID () == EventMap.SIMULATE_EVT) {
        } else if (evt.getID () == EventMap.REPORT_EVT) {
        } else if (evt.getID () == EventMap.INJECT_EVT) {
        } else if (evt.getID () == EventMap.INSTRUCT_EVT) {
        } else if (evt.getID () == EventMap.QUERY_EVT) {

           /**************************************************************
            * Handle QUERY_EVT from scenario agent
            */
	   Vector       data;
	   Query	query;
	   ResultSet    rs = null;
	   Result	qrs = null;
	   DBAccess	dbaccess = null;
	   JsimEvent	resultEvt;
	   String       xmlStr;

	   try {
	       if (use_xml) {
	       	   data = XMLSerializer.deserializeFromString ((String) handback);
	       } else {
	           data = (Vector) handback;
	       }
	       query = (Query) data.get (0);
	       dbaccess = new DBAccess ((DBConnectionInfo) data.get (1));

	       String queryStr = query.getQueryString ();
	       String actionType = query.getActionType ();

	       if (actionType.toLowerCase ().equals ("sequence query")) {

		   // wrap current seq in a Result object
		   rs = dbaccess.select (queryStr);
		   qrs = new Result ("Sequence number", rs);
		   rs.close ();

		   // update the sequence table
		   rs = dbaccess.select (queryStr);
		   rs.next ();
		   int seq = rs.getInt (1);   
		   trc.show ("notify", "current seq value is " + seq);
		   seq += 1;
		   dbaccess.updateRelationalData ("update sequence set seq = " + seq);
		   rs.close ();

	       } else if (actionType.toLowerCase ().equals ("normal query")) {
	           rs = dbaccess.select (queryStr);
		   qrs = new Result ("Normal results", rs);
		   rs.close ();
	       } else if (actionType.toLowerCase ().equals ("update")) {
		   dbaccess.updateRelationalData (queryStr);
		   qrs = new Result ("Information", "Database updated!");
	       } else {
		       trc.tell ("notify", "unexpected query type");
		       return ;
	       }; // if

	   } catch (Exception e) {	
	       qrs = new Result ("Information", e.getMessage ());
               trc.tell ("notify", e.getMessage ());
               e.printStackTrace ();
           } finally {
               dbaccess.closeConnection ();
           }

	   try {
	       if (use_xml) {
	           data = new Vector ();   data.add (qrs);
		   if (generate_xml_files) {
			String SLASH = System.getProperty ("file.separator");
	   		String fname = System.getProperty ("user.home");
			fname += SLASH + "JSIM" + SLASH + "jsim" +
				SLASH + "jmessage" + SLASH + "result.xml";
	                xmlStr = XMLSerializer.serialize (data, fname);
		   } else {
			xmlStr = XMLSerializer.serialize (data);
		   }; // if
		   resultEvt = new JsimEvent (this, EventMap.RESULT_EVT, xmlStr);
	       } else {
	           resultEvt = new JsimEvent (this, EventMap.RESULT_EVT, qrs);
	       }; // if
	       fireJsimEvent (resultEvt);

	   } catch (Exception e) {	
               trc.tell ("notify", e.getMessage ());
               e.printStackTrace ();
	       return;
           }
        } else if (evt.getID () == EventMap.STORE_EVT) {

	   Vector   data;
	   Store    store; 
	   DBAccess dbaccess = null;

	   try {
	       if (use_xml) {
	          data = XMLSerializer.deserializeFromString ((String) handback);
	       } else {
		  data = (Vector) handback;
	       }; // if
		
	       store = (Store) data.get (0);
	       dbaccess = new DBAccess ((DBConnectionInfo) data.get (1));
	       dbaccess.updateObject (store);
	   } catch (Exception e) {
	       trc.tell ("notify (StoreEvent)", e.getMessage ());
	       e.printStackTrace ();
	       return;
	   } finally {
	       dbaccess.closeConnection ();
	   }; // try

        } else if (evt.getID () == EventMap.RESULT_EVT) {
        } else if (evt.getID () == EventMap.INSTRUCT_EVT) {
        } else {
           trc.tell ("notify", "Unknown event type!");  
        }; // if

    }; // notify

}; // class DBAgent
