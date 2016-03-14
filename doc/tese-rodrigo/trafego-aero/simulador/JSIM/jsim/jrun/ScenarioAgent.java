
/******************************************************************
 * @(#) ScenarioAgent.java     1.3
 *
 * Copyright (c) 1999 Xuewei Xiang,  John Miller
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
 * The scenario agent gets the properties from model and model agent and
 * display them to a GUI. After user's modifying these properties, the
 * scenario agent sends the modified properties back to model and model
 * agent and start the simulation.  
 *
 * @version     1.3 (13 December 2000)
 * @author      Xuewei Xiang, John Miller
 */

package jsim.jrun;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

import jsim.event.*;
import jsim.util.*;
import jsim.jmessage.*;

/******************************************************************
 * This is a class which generalizes a scenario bean. This bean has
 * a display window which display properties of model and model agent.
 * User can modify these properties. When user finishes and hits the
 * 'Run' button, the scenario event will be fired.
 */

public class ScenarioAgent extends JPanel
                           implements JsimBean,
                                      ActionListener,
                                      JsimListener
{
    //////////////////////// Constants \\\\\\\\\\\\\\\\\\\\\\\\\\\\
    /**  
     * Background color.
     */  
    private static final Color   BACK_COLOR = Color.orange;
 
    /**  
     * Width of panel.
     */  
    private static final int     WIDTH = 200;
 
    /**  
     * Height of panel.
     */  
    private static final int     HEIGHT = 100;

    /**
     * The name of the agent
     */
    private static final String  NAME = "ScenarioAgent";

    /////////////////// Immutable Variables \\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Vector for registration of JsimEvent listeners
     */
    private final Vector         jsimListeners = new Vector ();

    /**
     * Tracing messages. 
     */  
    private final Trace          trc;

    /**
     * Output message area
     */
    private final JTextArea      msgArea = new JTextArea ();

    /////////////////// Variables \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    /** 
     * JSIM system property: use_xml
     */
    private boolean              use_xml = false;

    /**
     * JSIM system property: generate_xml_files
     */
    private boolean              generate_xml_files = false;

    /**
     * Database Connection information
     */
    private DBConnectionInfo     connInfo;


    /***************************************************************
     * Constructs a scenario agent with a default name.
     */
    public ScenarioAgent ()
    {
        trc = new Trace  ("jrun", "ScenarioAgent");

        String jsimprop = System.getProperty ("jsim.use_xml", "false");
        if (jsimprop.toLowerCase ().equals ("true")) {
            use_xml = true;
        } // if
       
        jsimprop = System.getProperty ("jsim.generate_xml_files", "false");
        if (jsimprop.toLowerCase ().equals ("true")) {
            generate_xml_files = true;
        } // if

        JPanel panel = new JPanel (new BorderLayout ());
        panel.setBackground (BACK_COLOR);
	panel.add (new JLabel (NAME, JLabel.CENTER), BorderLayout.CENTER);

	JPanel panel2 = new JPanel (new BorderLayout ());
	JButton startBtn = new JButton ("Start");
        startBtn.addActionListener (this);
        panel2.add (startBtn, BorderLayout.CENTER);

        setLayout (new BorderLayout ());
	add (panel, BorderLayout.NORTH);
        add (panel2, BorderLayout.CENTER);
        setBorder (BorderFactory.createEmptyBorder (5, 5, 5, 5));

        setBackground (BACK_COLOR);
        setSize (WIDTH, HEIGHT);
        setVisible (true);

    }; // ScenarioAgent


    ///////////////////////////////////////////////////////////////
    // Methods for registration of target Jsim event listeners.
    ///////////////////////////////////////////////////////////////   
     
    /**************************************************************
     * Add a JsimEvent listener.
     * @param  t  target listener to add
     */
    public synchronized void addJsimListener (JsimListener t)
    {
        jsimListeners.add (t);

    }; // addJsimListener

    
    /**************************************************************
     * Remove a JsimEvent listener.
     * @param  t  target listener to remove
     */
    public synchronized void removeJsimListener (JsimListener t)
    {  
        jsimListeners.remove (t);

    }; // removeJsimListener


    ///////////////////////////////////////////////////////////////
    // Methods for firing Jsim events:
    // QUERY_EVT, INSTRUCT_EVT
    ///////////////////////////////////////////////////////////////

    /**************************************************************
     * Create a jsim event and broadcast it to the listeners.
     * @param  evt An instance of JsimEvent
     */
    public void fireJsimEvent (JsimEvent evt)
    {
        JsimListener    target;
        Vector          allTargets;

        trc.show ("fireJsimEvent", "Event type: " + evt.getEventType ());

        synchronized (this) {
            allTargets = (Vector) jsimListeners.clone ();
        }; // synchronized
   
        for (int i = 0; i < allTargets.size (); i++) {
            target = (JsimListener) allTargets.elementAt (i);
            target.notify (evt);
        }; // for
 
    }; // fireJsimEvent


    /**************************************************************
     * Fire a QUERY_EVT.
     * @param	query	The query to be sent as the QueryEvent data
     */
    void fireQueryEvent (Query query)
    {
        JsimEvent evt;   
        String    xmlStr;
	Vector    data = new Vector ();
	data.add (query);   data.add (connInfo);

	if (use_xml) {
	   try {
		if (generate_xml_files) {
		   String SLASH = System.getProperty ("file.separator");
                   String fname = System.getProperty ("user.home");
                   fname += SLASH + "JSIM" + SLASH +  "jsim" + SLASH +
                              "jmessage" + SLASH + "query.xml";
	           xmlStr = XMLSerializer.serialize (data, fname);
		} else {
		   xmlStr = XMLSerializer.serialize (data);
		}; // if

	   	evt = new JsimEvent (this, EventMap.QUERY_EVT, xmlStr);
	   } catch (Exception e) {
	        trc.tell ("fireQueryEvent ", e.getMessage ());
	        e.printStackTrace ();
	        return;
	   }
	} else {
	   evt = new JsimEvent (this, EventMap.QUERY_EVT, data);
	}; // if

	fireJsimEvent (evt);

    }; // fireQueryEvent


    /**************************************************************
     * Fire an INSTRUCT_EVT.
     * @param scenarioID	The scenarioID to be used
     */
    void fireInstructEvent (Integer scenarioID)
    {
	JsimEvent evt;
	String    xmlStr;
	Instruct  instruct = new Instruct ("Instruct", scenarioID);

        if (use_xml) {
            try {
                Vector data = new Vector ();  data.add (instruct);

		if (generate_xml_files) {
		    String SLASH = System.getProperty ("file.separator");
	            String fname = System.getProperty ("user.home");
	            fname += SLASH + "JSIM" + SLASH +  "jsim" + SLASH
                            + "jmessage" + SLASH + "instruct.xml";
                    xmlStr = XMLSerializer.serialize (data, fname);
		} else {
		    xmlStr = XMLSerializer.serialize (data);
		}; // if

                evt = new JsimEvent (this, EventMap.INSTRUCT_EVT, xmlStr);
            } catch (Exception e) {
                trc.tell ("fireInstructEvent ", e.getMessage ());
                e.printStackTrace ();
                return;
            }
        } else {
                evt = new JsimEvent (this, EventMap.INSTRUCT_EVT, instruct);
        }; // if
        
        fireJsimEvent (evt);

    }; // fireInstructEvent

    ///////////////////////////////////////////////////////////////////
    // Methods for handling incoming awt/jsim events.
    // ScenarioIDEvent
    ///////////////////////////////////////////////////////////////////
    /***************************************************************
     * Handle the events if the buttons on the Scenario panel are hit
     * @param  evt   The action event fired by a button.
     */
    public void actionPerformed (ActionEvent evt)
    {
	new ScenarioWindow (this, msgArea);

    }; // actionPerformed
 

    /***************************************************************
     * Handle incoming JsimEvents.
     * @param evt	The JsimEvent
     */
    public void notify (JsimEvent evt)
    {
        trc.show("notify", "Event type: " + evt.getEventType ());
    
        Object handback = evt.getRegistrationObject ();
        if (evt.getID () == EventMap.INQUIRE_EVT) {
        } else if (evt.getID () == EventMap.INFORM_EVT) {
        } else if (evt.getID () == EventMap.CHANGE_EVT) {
        } else if (evt.getID () == EventMap.SIMULATE_EVT) {
        } else if (evt.getID () == EventMap.REPORT_EVT) {
        } else if (evt.getID () == EventMap.INJECT_EVT) {
        } else if (evt.getID () == EventMap.INSTRUCT_EVT) {
        } else if (evt.getID () == EventMap.QUERY_EVT) {
        } else if (evt.getID () == EventMap.STORE_EVT) {
        } else if (evt.getID () == EventMap.RESULT_EVT) {

             /***************************************************************
              * Handle ScenarioIDEvent from the database agent
              * @param evt ScenarioIDEvent containing a unique ID for this
              *            scenario
              */
	     Result    result;
	     if (use_xml) {        
                 try {
                     Vector data = XMLSerializer.deserializeFromString ((String) handback);
                     result = (Result) data.get (0);
                 } catch (Exception e) {
                     trc.tell ("notify ", e.getMessage ());
                     e.printStackTrace ();
                     return;
		 }
	     } else {
		 result = (Result) handback;
	     }; // if

             if (result.getActionType ().toLowerCase ().equals ("sequence number")) {
                 Object [][] value = result.getValue ();
            	 Integer scenarioID = (Integer) value [0][0];
		 if (scenarioID == null) {  
		     trc.show ("notify ", "scenarioID is: null");
	         } else {
		     trc.show ("notify ", "scenarioID is: "
				+ scenarioID.toString ());
		 }; // if

		 fireInstructEvent (scenarioID);
            } else if (result.getActionType ().toLowerCase ().equals ("normal results")) {

		 if (use_xml) {
		     String SLASH = System.getProperty ("file.separator");
		     String fname = System.getProperty ("user.home");
		     fname += SLASH + "JSIM" + SLASH +  "jsim" + SLASH +
                              "jmessage" + SLASH + "result.xml";
		     try {
			 String xmlString = result.toString ();
		         XMLSerializer.generateXML (xmlString, fname);
		         Runtime.getRuntime ().exec ("java ui.TreeViewer " + fname);
		     } catch (Exception e) {
		         trc.tell ("notify ", e.getMessage ());
		         e.printStackTrace ();
		     }
		 } else {
		     String resultStr = result.toString ();
		     msgArea.append (resultStr);
		 }; // if
	    } else if (result.getActionType ().toLowerCase ().equals ("information")) {
		 msgArea.setText (result.getInfo ());
	    }; // if

        } else if (evt.getID () == EventMap.INSTRUCT_EVT) {
        } else {
           trc.tell ("notify", "Unknown event type!");
        }; // if

    }; // notify


    /***************************************************************
     * Set up the database connection information.
     * @param connInfo	The incoming DBConnectionInfo
     */
    public void setDBConnectionInfo (DBConnectionInfo connInfo)
    {
        this.connInfo = connInfo;

    }; // setDBConnectionInfo

}; // class

