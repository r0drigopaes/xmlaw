
/******************************************************************
 * @(#) ModelAgent.java     1.3
 *
 * Copyright (c) 1998 John Miller, Xuewei Xiang
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
import jsim.jmessage.*;
import jsim.statistic.*;
import jsim.util.*;

/******************************************************************
 * The base class for all model agents.
 */

public abstract class ModelAgent extends JPanel
                                 implements JsimBean,
                                            ActionListener,
					    JsimListener
{
    //////////////////////// Constants \\\\\\\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Button for editing model properties.
     */
    private static final JButton     editBtn = new JButton ("Load/Edit");

    /**
     * Button for starting simulation.
     */
    private   static final JButton   startBtn = new JButton ("Start");

    /**
     * Background color.
     */  
    private   static final Color    BACK_COLOR = Color.red;

    /**
     * Width of panel.
     */  
    private   static final int      WIDTH = 200;

    /**
     * Height of panel.
     */  
    private   static final int      HEIGHT = 100;

    /**
     * Default relative precision.
     */
    protected static final double   DEFAULT_PRECISION = 0.1;

    /**
     * Default confidence level.
     */
    protected static final double   DEFAULT_CONFIDENCE = 0.95;

    /** 
     * Debug flag.
     */ 
    protected static final boolean  DEBUG = true;

    /////////////////// Immutable Variables \\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Target listeners for jsim events.
     */
    private   final Vector	jsimListeners = new Vector ();

    /**
     * Tracing messages.
     */  
    protected final Trace	trc;

    //////////////////////// Variables \\\\\\\\\\\\\\\\\\\\\\\\\\\\
    /**
     * JSIM system property: use_xml
     */
    protected boolean           use_xml = false;

    /**
     * JSIM system property: generate_xml_files
     */
    protected boolean           generate_xml_files = false;

    /**
     * Name of the model agent.
     */  
    protected String		agentName;

    /**
     * Statistic maintained locally by the model agent.
     * The data comes from report events in which a model sends the
     * agent its primary statistic.
     */  
    protected SampleStat	agentStat;
 
    /**
     * Number of batch/run means collected.
     */  
    protected int		numMeans = 0;

    /**
     * Relative precision of statistical results.
     */  
    protected double		relativePrecision = DEFAULT_PRECISION;
 
    /**
     * Confidence level of statistical results.
     */  
    protected double		confidenceLevel   = DEFAULT_CONFIDENCE;

    /**
     * Whether the model has been started.
     */  
    protected boolean		started = false;

    /**
     * Whether it is time for the model to quit.
     */  
    protected boolean		quit = false;

    /**
     * Cached copy of current modelProperties
     */
    private ModelProperties	propCache = null;

    /**
     * A unique ID for the scenario/federation that the model agent 
     * participates in
     */
    private Integer		scenarioID = null;
 
    /**
     * Report from model
     */
    private FinalReport		modelReport;


    /******************************************************************
     * Constructs a model agent with the a default name.
     */
    protected ModelAgent ()
    {
        this ("ModelAgent");

    }; // ModelAgent


    /******************************************************************
     * Constructs a model agent with the specified name.
     * @param agentName  the name of the agent
     */
    protected ModelAgent (String agentName)
    {
        trc = new Trace  ("ModelAgent", agentName);

        String jsimprop = System.getProperty ("jsim.use_xml", "false");
        if (jsimprop.toLowerCase ().equals ("true")) {
            use_xml = true;
        } // if
      
        jsimprop = System.getProperty ("jsim.generate_xml_files", "false");
        if (jsimprop.toLowerCase ().equals ("true")) {
            generate_xml_files = true;
        } // if

        this.agentName = agentName;
        agentStat      = new SampleStat (agentName);

        JPanel panel = new JPanel ();
        panel.setLayout (new BorderLayout ());
        panel.add (new JLabel (agentName, JLabel.CENTER),
                    BorderLayout.CENTER);

        JPanel panel2 = new JPanel ();
        startBtn.addActionListener (this);
        panel2.add (startBtn);
        
        setLayout (new BorderLayout ());
        add (panel, BorderLayout.NORTH);
        add (panel2, BorderLayout.CENTER);
        setBorder (BorderFactory.createEmptyBorder (5, 5, 5, 5));
        setSize (WIDTH, HEIGHT);
        setVisible (true);

    }; // ModelAgent


    /******************************************************************
     * Output the simulation result data (e.g., to screen or database).
     * @param  report    Model report containing information about
     *                   the model, its input node properties and output
     *                   statistical results
     */
    protected void outputResultData (FinalReport report)
    {
        trc.show ("outputResultData", "output simulation results");

	modelReport = report;
	new HandleReportDialog (this);

    }; // outputResultData


    /******************************************************************
     * Launches a StatFrame to display the simulation result.
     */
    void displayReport ()
    {
	StatFrame statTable;
        String    winTitle  = new String ("Statistical Results Window");

	if (use_xml) {
	    try {
		Vector data = new Vector ();  data.add (modelReport);
		String xmlStr = XMLSerializer.serialize (data);
		statTable = new StatFrame (winTitle, xmlStr);
	    } catch (Exception e) {
		trc.tell ("displayReport", e.getMessage ());
		e.printStackTrace ();
		return;
	    }
	} else {
            statTable = new StatFrame (winTitle, modelReport);
	}; // if

        statTable.showWin ();

    }; // displayReport


    /******************************************************************
     * Collect all the control data necessary for starting/controlling
     * a simulation model.
     * This abstract methods must be implemented in each child class.
     * @return Simulate		The simulation control data
     */
    protected abstract Simulate collectControlData ();


    /******************************************************************
     * Collect information about the model agent
     * @return Simulate		The simulation control data
     */
    protected abstract Simulate collectAgentData ();


    ///////////////////////////////////////////////////////////////////
    // Methods for registering/deregistering jsim event listeners.
    // InquireListener, ModelPropertyChangeListener,
    // SimulateListener, StoreListener
    ///////////////////////////////////////////////////////////////////

    /******************************************************************
     * Adds a JsimListener.
     * @param  t  target listener to add
     */
    public synchronized void addJsimListener (JsimListener t)
    {
        jsimListeners.add (t);
    
    }; // addJsimListener

    /******************************************************************
     * Removes a JsimListener.
     * @param  t  target listener to remove
     */
    public synchronized void removeJsimListener (JsimListener t)
    {
        jsimListeners.remove (t);

    }; // removeJsimListener


    ///////////////////////////////////////////////////////////////////
    // Methods for handling incoming events awt/Jsim.
    // ActionEvent, InformEvent, ReportEvent
    ///////////////////////////////////////////////////////////////////

    /*****************************************************************
     * Handles mouse click event
     */
    public void mouseClicked(MouseEvent evt) {
    }


    /******************************************************************
     * Handles button clicking event.
     * @param  evt  awt action event
     */
    public void actionPerformed (ActionEvent evt)
    {
        trc.show ("actionPerformed", "handle button pressed event");

        if (evt.getSource () == startBtn) {
  	    started = false;
	    quit = false;
            fireInquireEvent ();
        }; // if

    }; // actionPerformed


    /******************************************************************
     * Handles incoming JsimEvents
     * @param  evt	The JsimEvent
     */
    public void notify (JsimEvent evt)
    {
        trc.show ("notify", "Event type: " + evt.getEventType ());
  
	Object handback = evt.getRegistrationObject ();
        if (evt.getID () == EventMap.INQUIRE_EVT) {
        } else if (evt.getID () == EventMap.INFORM_EVT) {
           
           /**************************************************************
            * Handle InformEvent from model
            */

           ModelProperties prop;
           if (use_xml) {
	      try {
                  Vector data = XMLSerializer.deserializeFromString ((String) handback);
                  prop = (ModelProperties) (data.get (0));
              } catch (Exception e) {
                  trc.tell ("notify (InformEvent)", e.getMessage ());
                  e.printStackTrace ();
                  return;
              }
	   } else {
	      prop = (ModelProperties) handback;
	   }; // if

           if (prop != null) {
		 propCache = prop;
		 new PropertyDialog (this, prop);
           } else {
		 trc.show ("notify", "model prop is null");
	   }; // if

        } else if (evt.getID () == EventMap.CHANGE_EVT) {
        } else if (evt.getID () == EventMap.CHANGED_EVT) {
	   fireSimulateEvent ();
        } else if (evt.getID () == EventMap.SIMULATE_EVT) {
        } else if (evt.getID () == EventMap.REPORT_EVT) {
	   Message msg = null;
	   if (use_xml) {
                try {
                    Vector v = XMLSerializer.deserializeFromString ((String) handback);
                    msg = (Message) v.get (0);
                } catch (Exception e) {
                    trc.tell ("notify", e.getMessage ());
                    e.printStackTrace ();
                }
           } else {
                msg = (Message) handback;
	   }; // if
	   handleReport (msg);
        } else if (evt.getID () == EventMap.INJECT_EVT) {
        } else if (evt.getID () == EventMap.QUERY_EVT) {
        } else if (evt.getID () == EventMap.STORE_EVT) {
        } else if (evt.getID () == EventMap.RESULT_EVT) {
        } else if (evt.getID () == EventMap.INSTRUCT_EVT) {

           /******************************************************************
            * Handle instruct events from a model
            */
	   Instruct instruct;
	   if (use_xml) {
               try {
                   Vector data = XMLSerializer.deserializeFromString ((String) handback);
                   instruct = (Instruct) data.get (0);
               } catch (Exception e) {   
                   trc.tell ("notify", e.getMessage ());
                   e.printStackTrace ();
                   return;
               }
	   } else {
	       instruct = (Instruct) handback;
	   }; // if

	   scenarioID = instruct.getScenarioID ();
           started = false;
           quit = false;
           fireInquireEvent ();

        } else {
           trc.tell ("notify", "Unknown event type!");
        }; // if

    }; // notify


    /******************************************************************
     * Method to handle report events from a model.
     * Must be handled by derived class.
     */
    protected abstract void handleReport (Message msg);


    ///////////////////////////////////////////////////////////////////
    // Methods for communication between beans (fire events).
    // fireInquireEvent, fireChangeEvent, fireSimulateEvent,
    // fireStoreEvent
    ///////////////////////////////////////////////////////////////////

    /**************************************************************
     * Creates a jsim event and broadcast it to the listeners.
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


    /******************************************************************
     * Fires inquire event (model agent to model).
     * Inquire the model for its properties.
     */
    public void fireInquireEvent ()

    {
	JsimEvent evt = new JsimEvent (this, EventMap.INQUIRE_EVT, null);
	fireJsimEvent (evt);

    }; // fireInquireEvent


    /******************************************************************
     * Fire model property change event (model agent to model).
     * Propagate changes made to model properties to the target model.
     * @param prop  ModelProperties containing input parameters associated
     *              with the nodes of the model
     */
    protected void fireChangeEvent (ModelProperties prop)
    {
	JsimEvent   evt;
	String	    SLASH = System.getProperty ("file.separator");
	String      fname = System.getProperty ("user.home");
	String      xmlStr;

	if (use_xml) {
           try {
                Vector data = new Vector ();  data.add (prop);
                if (generate_xml_files) {
		    fname += SLASH + "JSIM" + SLASH + "jsim" + SLASH +
				"jmessage" + SLASH + "change.xml";
		    xmlStr = XMLSerializer.serialize (data, fname);
		} else {
		    xmlStr = XMLSerializer.serialize (data);
		}; // if
           } catch (Exception e) {
                trc.tell ("fireChangeEvent", e.getMessage ());
                e.printStackTrace ();
                return;
           }
	   evt = new JsimEvent (this, EventMap.CHANGE_EVT, xmlStr);
	} else {
	   evt = new JsimEvent (this, EventMap.CHANGE_EVT, prop);
	}; // if

        propCache = prop;
	fireJsimEvent (evt);
     
    }; // fireChangeEvent

    /******************************************************************
     * Fire the model action event (model agent to model).
     * This method has the same effect as pressing the button.
     * Get the data vector and prepare a SimulateEvent.
     */      
    protected void fireSimulateEvent ()
    {
	JsimEvent evt;
	Simulate  controlData = collectControlData ();
	String    SLASH = System.getProperty ("file.separator");
	String    fname = System.getProperty ("user.home");
	String    xmlStr;

	if (use_xml) {
             try {
		 Vector data = new Vector ();  data.add (controlData);
		 if (generate_xml_files) {
			fname += SLASH + "JSIM" + SLASH + "jsim" + SLASH +
					"jmessage" + SLASH + "simulate.xml";
			xmlStr = XMLSerializer.serialize (data, fname);
		 } else {
			xmlStr = XMLSerializer.serialize (data);
		 }; // if
                 evt = new JsimEvent (this, EventMap.SIMULATE_EVT, xmlStr);
             } catch (Exception e) {
                 trc.tell ("fireSimulateEvent ", e.getMessage ());
                 e.printStackTrace ();
                 return ;
             }
	} else {
	     evt = new JsimEvent (this, EventMap.SIMULATE_EVT, controlData);
	}; // if

	fireJsimEvent (evt);

    }; // fireSimulateEvent

    /**********************************************************
     * Fire StoreEvent to the database agent to store model report
     */
    protected void fireStoreEvent (DBConnectionInfo connInfo)
    {
	if (connInfo == null) {
            trc.show ("fireStoreEvent", "report will not be stored: "
			+ "unable to connect to database");
	    return;
	} // if

	JsimEvent  evt;
	String     xmlStr;
	String	   SLASH = System.getProperty ("file.separator");
	String     fname = System.getProperty ("user.home");

        Store      store = new Store ("Store", scenarioID,
                                  collectAgentData (),
                                  propCache, modelReport);
	String     sdata = store.toString ();
	System.out.println (sdata);

	Vector     data = new Vector ();
	data.add (store);  data.add (connInfo);
	
	if (use_xml) {
           try {
	       if (generate_xml_files) {
		   fname += SLASH + "JSIM" + SLASH + "jsim" + SLASH +
                                "jmessage" + SLASH + "store.xml";
                   xmlStr = XMLSerializer.serialize (data, fname);
	       } else {
		   xmlStr = XMLSerializer.serialize (data);
	       }; // if
               evt = new JsimEvent (this, EventMap.STORE_EVT, xmlStr);
           } catch (Exception e) {
               trc.tell ("fireStoreEvent", e.getMessage ());   
               e.printStackTrace ();
               return;
           }

	} else {
	       evt = new JsimEvent (this, EventMap.STORE_EVT, data);
	}; // if

	fireJsimEvent (evt);

    }; // fireStoreEvent


    /**********************************************************
     * The stopping rule method returns true if the simulation
     * should continue, and false otherwise.
     * @return  boolean  whether simulation should stop
     */
    protected boolean testPrecision ()
    {
         double precision = relativePrecision / (1.0 + relativePrecision);
 
        /******************************************************
         * Check the relative precision
         */
        double relPrecision = agentStat.confidence () / agentStat.mean ();

        trc.show ("testPrecision", " precision = " + precision +
                                   " relPrecision = " + relPrecision);

        if (numMeans < 2) {
            return false;
        } else { 
            return relPrecision <= precision;
        } // if

    }; // testPrecision


    ///////////////////////////////////////////////////////////////////
    // Methods for getting and setting properties of model agent beans.
    // agentName, relativePrecision, confidenceLevel
    ///////////////////////////////////////////////////////////////////

    /******************************************************************
     * Get the name of the model agent.
     * @return  String  agent name
     */
    public String getAgentName ()
    {
        return agentName;

    }; // getAgentName


    /******************************************************************
     * Set the name of the model agent.
     * @param  String  agent name
     */
    public void setAgentName (String agentName)
    {
        this.agentName = agentName;

    }; // setAgentName


    /******************************************************************
     * Get the value of relative precision.
     * @return  relativePrecision  desired level of relative precision
     */
    public double getRelativePrecision ()
    {
        return relativePrecision;

    }; // getRelativePrecision


    /******************************************************************
     * Set the value of relative precision.
     * @param  relativePrecision  desired level of relative precision
     */
    public void setRelativePrecision (double relativePrecision)
    {
        this.relativePrecision = relativePrecision;

    }; // setRelativePrecision


    /******************************************************************
     * Get the value of confidence level
     * @return  confidenceLevel  desired level of confidence
     */
    public double getConfidenceLevel ()
    { 
        return confidenceLevel;
 
    }; // getConfidenceLevel
 
 
    /******************************************************************
     * Set the value of confidence level.
     * @param  confidenceLevel  desired level of confidence
     */
    public void setConfidenceLevel (double confidenceLevel)
    {
        this.confidenceLevel = confidenceLevel;
 
    }; // setConfidenceLevel


}; // class

