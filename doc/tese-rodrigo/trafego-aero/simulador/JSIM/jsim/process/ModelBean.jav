
/******************************************************************
 * @(#) ModelBean.java     1.3
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
 * @version     1.3 (13 December 2000)
 * @author      John Miller
 */

package jsim.process;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;

import jsim.statistic.*;
import jsim.variate.*;
import jsim.util.*;
import jsim.jmessage.*;

/******************************************************************
 * The ModelBean abstract class allows application specific simulation
 * models to be derived from it.  It generalizes all models.
 * Simulation models run in their own frame and multiple frames
 * (models) can be active simultaneously.
 */

public abstract class ModelBean extends    JPanel
                                implements JsimBean,
					   JsimListener
{
    //////////////////////// Constants \\\\\\\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Debug flag
     */
    private static final boolean  DEBUG = true;

    /**
     * Width of panel.
     */
    private static final int     WIDTH = 200;
       
    /**
     * Height of panel.
     */
    private static final int     HEIGHT = 50;


    /////////////////// Immutable  Variables \\\\\\\\\\\\\\\\\\\\\\
    /**
     * Vector of target listeners for (model) events.
     * These are model agents.
     */
    public final Vector  jsimListeners = new Vector ();

    /**
     * Tracing messages
     */
    protected final Trace  trc;


    //////////////////////// Variables \\\\\\\\\\\\\\\\\\\\\\\\\\\\
    /**
     * JSIM system property: use_xml
     */
    private boolean            use_xml = false;
       
    /**
     * JSIM system property: generate_xml_files
     */
    private boolean            generate_xml_files = false;

    /**
     * An instance of simulation model itself (wrapped in the model bean)
     */
    public Model    model;

    /**
     * Label/name of the model bean
     */
    private String  label;

    /**
     * Customized properties
     */
    protected Prop [] properties = null;

    /**************************************************************
     * Construct a ModelBean.
     * @param    modelName   the name of the model
     */
    public ModelBean (String modelName)
    {
        String jsimprop = System.getProperty ("jsim.use_xml", "false");
        if (jsimprop.toLowerCase ().equals ("true")) {
            use_xml = true;
        } // if

        jsimprop = System.getProperty ("jsim.generate_xml_files", "false");
        if (jsimprop.toLowerCase ().equals ("true")) {
            generate_xml_files = true;
        } // if

	trc = new Trace ("ModelBean", modelName);
	label = modelName;

	setLayout (new BorderLayout ());
        JLabel modelLabel = new JLabel (modelName, JLabel.CENTER);
        add (modelLabel, BorderLayout.CENTER);
        setBorder (BorderFactory.createEmptyBorder (10, 10, 10, 10));
        setSize (WIDTH, HEIGHT);
        setVisible (true); 
      
    }; // ModelBean


    ///////////////////////////////////////////////////////////////
    // Abstract methods must be provided in derived classes.
    // prepareModel, getProperties
    ///////////////////////////////////////////////////////////////

    /**************************************************************
     * Abstract method to prepare model bean for execution by
     * constructing an embedded model.
     */
    protected abstract void prepareModel ();

    /**************************************************************
     * Get the current model properties.
     * @return ModelProperties	The current model properties
     */
    ModelProperties getModelProperties ()
    {
        trc.show ("getModelProperties", "extracting model properties");
                
        String modelName = model.getTitle ();
        int    numOfNodes = properties.length;
        String [] nodeName = new String [numOfNodes];
        String [] distType = new String [numOfNodes];
        int []    nodeType = new int [numOfNodes];
        int []    nTokens = new int [numOfNodes];
        Integer [] stream = new Integer [numOfNodes];
        Double []  alpha = new Double [numOfNodes];
        Double []  beta = new Double [numOfNodes];
        Double []  gamma = new Double [numOfNodes];
                                    
        for (int i = 0; i < numOfNodes; i++) {
           nodeName [i] = properties [i].nName;
           distType [i] = VariateConverter.toString (properties [i].timeDist);
           nodeType [i] = properties [i].nType;
           nTokens [i] = properties [i].nTokens;
     
           alpha [i] = null;  beta [i] = null; gamma [i] = null;
           Double [] param = (properties [i].timeDist).getParameters ();
           if (param.length == 1) {
                stream [i] = new Integer (param [0].intValue ());   
           } else if (param.length == 2) {
                alpha [i] = param [0];
                stream [i] = new Integer (param [1].intValue ());
           } else if (param.length == 3) {
                alpha [i] = param [0];
                beta [i] = param [1];
                stream [i] = new Integer (param [2].intValue ());
           } else if (param.length == 4) {
                alpha [i] = param [0];
                beta [i] = param [1];   
                gamma [i] = param [2];
                stream [i] = new Integer (param [3].intValue ());
           } else {
                trc.show ("prepareReport", "can't handle variate with "
                           + "more than 4 parameters");
           } // if
        } // for   

        return new ModelProperties ("Inform", modelName, numOfNodes,
                                nodeName, distType, nodeType, nTokens,
                                stream, alpha, beta, gamma);
    }; // getModelProperties


    /**************************************************************
     * Set properties for embedded model.
     * @param  prop  new property list
     */
    public void setModelProperties (ModelProperties prop)
    { 
        trc.show ("setModelProperties", "setting model properties");
    
        Variate    timeDist;
        String []  nodeName = prop.getNodeName ();
        String []  distType = prop.getDistributionType ();
        int []     nodeType = prop.getNodeType ();
        int []     nTokens = prop.getNumOfTokens ();
        Integer [] stream = prop.getStream ();
        Double []  alpha = prop.getAlpha (); 
        Double []  beta = prop.getBeta ();
        Double []  gamma = prop.getGamma ();
        int        numOfNodes = prop.getNumOfNodes ();
     
        for (int i = 0; i < numOfNodes; i++) {
            timeDist = VariateConverter.toVariate (distType [i], stream [i],
                                         alpha [i], beta [i], gamma [i]);
	    properties [i].nType  = nodeType [i];
	    properties [i].nName  = nodeName [i];
	    properties [i].nTokens = nTokens [i];
	    properties [i].timeDist = timeDist;
        } // for

        if (numOfNodes > model.dynNode.length) {
            trc.tell ("setModelProperties", "wrong size for property list" +
                      "\nproperty list size: " + numOfNodes +
                      "\ndynNode size: " + model.dynNode.length);
            return ;
        }; // if

        for (int i = 0; i < numOfNodes; i++) {
            model.dynNode [i].setProps (properties [i]);
        }; // for

    }; // setModelProperties


    /**************************************************************
     * Start the simulation.
     */
    public void startSim ()
    {
	trc.show ("startSim", "Please start simulation...");
        prepareModel ();
	model.start ();

    }; // startSim


    /**************************************************************
     * Get the primary statisitic, the one used in the stopping rule.
     * @return  the primary statistic to monitor
     */
    public String showPrimaryStat ()
    {
        return model.getPrimaryStat ();
         
    }; // getPrimaryStat


    /**************************************************************
     * Set the primary statisitic, the one used in the stopping rule.
     * @param  statNodeName  the primary statistic to monitor
     */
    public void setPrimaryStat (String statNodeName)
    {
        model.setPrimaryStat (statNodeName);

    }; // setPrimaryStat


    /**************************************************************
     * Get the label for the model bean.
     */
    public String getLabel ()         
    { 
	return label;

    }; // getLabel


    /**************************************************************
     * Set the label for the model bean.
     */
    public void setLabel (String val) 
    { 
	label = val;

    }; // setLabel
     

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
    // InformEvent, ReportEvent, InjectEvent
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
     * Method for handling incoming Jsim events:
     * @param evt	The incoming JsimEvent
     */
    public void notify (JsimEvent evt)
    {
        trc.show("notify", "Event type: " + evt.getEventType ());

	Object handback = evt.getRegistrationObject ();
	if (evt.getID () == EventMap.INQUIRE_EVT) {

           /**************************************************************
            * Handle INQUIRE_EVT from model agent
            */
           prepareModel ();

	   // fire inform event
	   JsimEvent informEvt;
	   String    xmlStr;
	   String    SLASH = System.getProperty ("file.separator");
	   String    fname = System.getProperty ("user.home");
	   ModelProperties prop = getModelProperties ();
	   if (use_xml) {
        	try {
            	    Vector data = new Vector ();  data.add (prop);

		    if (generate_xml_files) {
			fname += SLASH + "JSIM" + SLASH + "jsim" + SLASH +
				"jmessage" + SLASH + "mproperties.xml";
            		xmlStr = XMLSerializer.serialize (data, fname);
		    } else {
			xmlStr = XMLSerializer.serialize (data);
		    }; // if

		    informEvt = new JsimEvent (this, EventMap.INFORM_EVT, xmlStr);
        	} catch (Exception e) {
            	    trc.show ("notify", e.getMessage ());
            	    e.printStackTrace ();
            	    return;
		}
           } else {
		informEvt = new JsimEvent (this, EventMap.INFORM_EVT, prop);
	   }; // if

           fireJsimEvent (informEvt);

	} else if (evt.getID () == EventMap.INFORM_EVT) {
	} else if (evt.getID () == EventMap.CHANGE_EVT) {

           /**************************************************************
            * Handle model property changeEvent by setting model properties
            */
           ModelProperties prop;
	   if (use_xml) {
              try {
                  Vector data = XMLSerializer.deserializeFromString ((String) handback);
                  prop = (ModelProperties) data.get (0);
              } catch (Exception e) {
                  trc.show ("notify", e.getMessage ());
                  e.printStackTrace ();
                  return;
              }
           } else {
	      prop = (ModelProperties) handback;
	   }; // if
	
           setModelProperties (prop);
	   JsimEvent changedEvt = new JsimEvent (this, EventMap.CHANGED_EVT, null);
	   fireJsimEvent (changedEvt);

        } else if (evt.getID () == EventMap.CHANGED_EVT) {
	} else if (evt.getID () == EventMap.SIMULATE_EVT) {

           /**************************************************************
            * Handle simulate events.  When a model agent
            * fires a simulate event, it sends the data with the event.
            * From the first element of the vector, model can decide
            * which output analysis method is required by model agent.
            * It can invoke the appropriate methods.
            */
	   Simulate actionData;
	   if (use_xml) {
              try {
                  Vector msg = XMLSerializer.deserializeFromString ((String) handback);
                  actionData = (Simulate) msg.get (0);
              } catch (Exception e) {
                  trc.show ("notify", e.getMessage ());
                  e.printStackTrace ();
                  return;
              }
	   } else {
	      actionData = (Simulate) handback;
	   }; // if

           String agentName = actionData.getAgentName ();
           trc.show ("notify", "Agent: " + agentName);
       
           if (agentName.equals ("BatchMeansAgent")) {
		processBatch (actionData);
           } else if (agentName.equals ("ReplicationAgent")) {
		processReplication (actionData);
           } else {
		trc.tell ("notify", "cannot recognize the model agent");
	   }; // if

	} else if (evt.getID () == EventMap.REPORT_EVT) {
        } else if (evt.getID () == EventMap.INJECT_EVT) {

           /**************************************************************
            * Handle (entity) inject events, start an entity in
            * every source node of the model.
            */

           // FIX: deserialize the message, extract entity info
           //      and process the message

//	   model.injectAnEntity ();

	} else if (evt.getID () == EventMap.INSTRUCT_EVT) {
        } else if (evt.getID () == EventMap.QUERY_EVT) {
        } else if (evt.getID () == EventMap.STORE_EVT) {
        } else if (evt.getID () == EventMap.RESULT_EVT) {
        } else if (evt.getID () == EventMap.INSTRUCT_EVT) {
        } else {
	   trc.tell ("notify", "Unknown event type!");
	}; // if

    }; // notify
      

    /**************************************************************
     * This method collects batch mean data in response to a simulate
     * event by a model agent.  When model agent fires a simulate
     * event, it sends instructions with the event:
     * 1. Start simulation and collect the several batches of simulation
     *    data. The batch size and number of batch needed is specified
     *    in the data sent with the SimulateEvent.
     * 2. Continue to collect simulation data.
     * 3. Stop the entire simulation.
     */
    public void processBatch (Simulate actionData)
    {
        String actionType = actionData.getActionType ();
        int batchSize = actionData.getBatchSize ().intValue ();
        int numOfBatches = actionData.getNumOfBatches ().intValue ();
            
        if (actionType.equals ("start")) {
            startSim ();
            model.setBatchProperties (batchSize, numOfBatches);
        } else if (actionType.equals ("continue")) {
            model.setBatchProperties (batchSize, numOfBatches);
        } else if (actionType.equals ("stop")) {
            model.endSim ();
        } else {
            trc.tell ("processBatch", "unrecognized actionType: " + actionType);
        }

/*
        if (DEBUG) {
            for (int i = 0; i < cData.size (); i++) {
                trc.show ("processBatch",
                          "vector cData [" + i + "] = " + cData.get (i));
            }; // for
        }; // if
 
        String  request  = (String)  cData.get (1);   // request from agent
        Integer batSize  = (Integer) cData.get (2);   // batch size
        Integer nBatches = (Integer) cData.get (3);   // number of batches

        int batchSize  = batSize.intValue ();
        int numBatches = nBatches.intValue ();

        if (request.equals ("start")) {               // start the simulation
            startSim ();
            model.setBatchProperties (batchSize, numBatches);

        } else if (request.equals ("continue")) {     // continue simulating
            model.setBatchProperties (batchSize, numBatches);

        } else if (request.equals ("stop")) {         // stop simulating
            model.endSim ();

        } else {
            trc.tell ("processBatch", "unrecognized request: " + request);

        }; // if
*/
    }; // processBatch

    /**************************************************************
     * This method collects batch mean data in response to a simulate
     * event by a model agent.  When model agent fires a simulate
     * event, it sends instructions with the event:
     * 1. Start simulation and collect the several batches of simulation
     *    data. The batch size and number of batch needed is specified  
     *    in the data sent with the SimulateEvent.
     * 2. Continue to collect simulation data.
     * 3. Stop the entire simulation.
     */
    public void processReplication (Simulate actionData)
    {   
	String request = actionData.getActionType ();
	int    batchSize = actionData.getReplicationSize ().intValue ();
	int    numBatches = 1;
	double transperiod = actionData.getTransientPeriod ().doubleValue ();
	if (request.toLowerCase ().equals ("true")) {
		startSim ();
		model.setBatchProperties (batchSize, numBatches);
	} else if (request.toLowerCase ().equals ("false")) {
		model.endSim ();
	} else {
		trc.tell ("processReplication", "unrecognized request: " + request);
	}; // if
/*

        if (DEBUG) {  
            for (int i = 0; i < cData.size (); i++) {
                trc.show ("processReplication", 
                          "vector cData [" + i + "] = " + cData.get (i));
            }; // for
        }; // if
     
        Boolean request  = (Boolean)  cData.get (1);  // request from agent
        Integer repSize  = (Integer) cData.get (2);   // replication size
        Double  transPeriod = (Double) cData.get (3); // transient period

        int batchSize  = repSize.intValue ();
        int numBatches = 1;

	System.out.println ("batchSize is " + batchSize);
	System.out.println ("Request is " + request.booleanValue ());    

        if (request.booleanValue () == true) {        // start the simulation
            startSim ();
            model.setBatchProperties (batchSize, numBatches);

        } else if (request.booleanValue () == false) { // stop simulating
            model.endSim ();

        } else {
            trc.tell ("processReplication", "unrecognized request: " + request);
        }; // if
*/        
    }; // processReplication

}; // class

