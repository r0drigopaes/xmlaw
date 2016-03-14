/******************************************************************
 * @(#) Store.java     1.3
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
 * @author      Xueqin Huang, John Miller
 */

package jsim.jmessage;

import java.io.*;

/******************************************************************
 * The Store class represents all the data that need to be store in
 * the database. It contains a scenario ID, the agent data, and the
 * model input and output data. A Store object is sent from a model
 * agent to the DBAgent after the model agent has received a report
 * from the model bean it controls.
 */
public class Store extends Message 
{
    /**
     * Serialization ID for all instances of this class
     */
    private static final long	serialVersionUID = -6292887518843050290L;

    /**
     * The ID of the scenario that the federate participates in
     */
    public Integer		scenarioID;

    /**
     * The model agent control data
     */
    public Simulate		agentData;

    /**
     * The model input and output data
     */
    public ModelData		modelData;


    /******************************************************************
     * Constructs a Store instance.
     * @param actionType	The action type
     * @param scenarioID	The scenario ID
     * @param agentData		The simulation control data from the model agent
     * @param modelProperties	The input model properties
     * @param report		The simulation result report from the model bean
     */
    public Store (String		actionType,
		  Integer		scenarioID,
		  Simulate		agentData,
		  ModelProperties	modelProperties,
		  FinalReport		report)
    {
	this.actionType = actionType;
	this.scenarioID = scenarioID;
	this.agentData = agentData;

	String modelName = modelProperties.getModelName ();
	int numNodes = modelProperties.getNumOfNodes ();
	String [] nodeName = modelProperties.getNodeName ();
	int    [] nodeType = modelProperties.getNodeType ();
	int    [] numTokens = modelProperties.getNumOfTokens ();
	String [] distType = modelProperties.getDistributionType ();
	Double [] alpha = modelProperties.getAlpha ();
	Double [] beta = modelProperties.getBeta ();
	Double [] gamma = modelProperties.getGamma ();
	Integer [] stream = modelProperties.getStream ();
	NodeProps [] props = new NodeProps [numNodes];

	Double [][] timeStat = report.getTimeStat ();
	Double [][] timeObsStat = report.getTimeObsStat ();
	Double [][] occuStat = report.getOccuStat ();
	Double [][] occuObsStat = report.getOccuObsStat ();
	NodeStats [] stats = new NodeStats [numNodes];
	for (int i = 0; i < numNodes; i++) {
		props [i] = new NodeProps (nodeName [i], nodeType [i],
				numTokens [i], distType [i], alpha [i],
				beta [i], gamma [i], stream [i]);
		stats [i] = new NodeStats (timeStat [i], timeObsStat [i],
					occuStat [i], occuObsStat [i]);
	}; // for

	modelData = new ModelData (modelName, props, stats);

    }; // Store constructor


    /**************************************************************
     * Returns the scenario ID.
     * @return Integer		The scenario ID
     */
    public Integer		getScenarioID () 
    {
	return scenarioID; 

    }; // getScenarioID


    /**************************************************************
     * Returns the control data from the model agent.
     * @return Simulate		The simulation control data
     */
    public Simulate 		getAgentData () 
    { 
	return agentData; 

    }; // getAgentData


    /**************************************************************
     * Returns the model input and output data.
     * @return ModelData	The model input and output data
     */
    public ModelData		getModelData () 
    { 
	return modelData; 

    }; // getModelData


    /**************************************************************
     * Returns an XML fragemnt representing this instance of Store
     * @return String		The XML fragment
     */
    public String toString ()
    {
	String xmlStr = "";

	xmlStr += "\n      <ScenarioID>" + scenarioID + "</ScenarioID>";
	xmlStr += "\n      <Agent>" + agentData.toString ();
	xmlStr += "\n      </Agent>";
	xmlStr += "\n      <Model>" + modelData.toString ();
	xmlStr += "\n      </Model>";

	return xmlStr;
	
    }; // toString


    /***************************************************************
     * Meets the Java serialization requirements.
     * @param  out      The ObjectOutputStream
     * @exception java.io.IOException
     */
    public void writeObject (ObjectOutputStream out)
		throws IOException {

	out.writeObject (actionType);
        out.writeObject (scenarioID);
	out.writeObject (agentData);
	out.writeObject (modelData);

    }; // writeObject


    /***************************************************************
     * Meets the Java serialization requirements.
     * @param  in       The ObjectInputStream
     * @exception       java.io.Exception
     */
    public void readObject (ObjectInputStream in)
		throws IOException, ClassNotFoundException {

	actionType = (String) in.readObject ();
	scenarioID = (Integer) in.readObject ();
	agentData = (Simulate) in.readObject ();
	modelData = (ModelData) in.readObject ();

    }; // readObject


    /***************************************************************
     * Meets the Koala XML serialization requirements
     * @param  in       The XML serialization GeneratorInputStream 
     *			provided by KOML
     * @exception       java.io.IOException
    public static void readObject (ObjectInputStream in)
		throws IOException, ClassNotFoundException {

        XMLSerializer.readObjectStore (in);
   
    }; // readObject
     */

}; // class Store
