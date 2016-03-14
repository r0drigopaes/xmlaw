/******************************************************************
 * @(#) CostReport.java     1.3
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
 * @version     1.3 (9 July 2004)
 * @author      Greg Silver
 */

package jsim.jmessage;

import java.io.Serializable;
import java.util.Date;
import java.util.Vector;

import jsim.util.Trunc;

/******************************************************************
 * This class contains simulation result of a model. The result is sent
 * by the containing model bean to the controlling model agent.
 */
public class CostReport extends Message 
{
    /**
     * Serialization ID for instances of this class.
     */
    private static final long   serialVersionUID=6865934528700823025L;

    /**
     * Model name
     */
    private String		modelName;

    /**
     * Version number of the model
     */
//  private int                 version;     // version of the model, for future extension

    /**
     * Number of nodes
     */
    private int                 numOfNodes;

    /**
     * Statistic data needed by the ReplicationAgent
     */
    private double []           statData;    

    /**
     * Names of the statistic result
     */
    public String []		statLabel;

	/**
	 * Name of cost statistics
	 */
	public String []		costStatName;


	/**
	 * Name of cost observation statistics
	 */
	public String []		costObsStatName;

	/**
	 * Cost statistics
	 */
	public Double [][]		costStat;

	/**
	 * Cost observation statistics
	 */
	public Double [][]		costObsStat;

    /**************************************************************
     * Constructs an instance of FinalReport by message type, number of
     * nodes, names of statistics, time statistics, occupancy statistics,
     * time observation statistics, occupancy observation statistics, and
     * statistic data needed by the ReplicationAgent.
     * @param  actionType	The message type
     * @param  modelName	The model name
     * @param  statLabel	The names of the statistics
     * @param  tStat		The time statistics
     * @param  oStat		The occupancy statistics
     * @param  tObsStat		The time observation staticstics
     * @param  oObsStat		The occupancy observation statistics
     * @param  statData		The statistic data prepared by the
     * 				ModelBean for the ReplicationAgent in case
     *				the controlling agent is the ReplicationAgent
     */
    public CostReport (String 	actionType,
                             String 	modelName,
                             int        numOfNodes,
                             String []	statLabel,
							 Vector []	cStat,
                             Vector []	cObsStat,
							 double []  statData) 
    {
	this.actionType = actionType;
        this.modelName = modelName;
        this.numOfNodes = numOfNodes;
	this.statData = statData;
        this.statLabel = statLabel;
	this.costStatName = new String [numOfNodes];
	this.costObsStatName = new String [numOfNodes];
	this.costStat = new Double [numOfNodes][statLabel.length - 1];
	this.costObsStat = new Double [numOfNodes][statLabel.length - 1];

	for (int i = 0; i < numOfNodes; i++) {
	   costStatName [i] = (String) cStat [i].get (0);

		if (cObsStat [i] == null) {
			costObsStatName [i] = null;
		} 
		else {
			costObsStatName [i] = (String) cObsStat [i].get (0);
		}; // if
	}; // for
	
	for (int i = 0; i < numOfNodes; i++) {
	   for (int j = 1; j < statLabel.length; j++) {
	      costStat [i][j - 1] = Trunc.trunc ((Double) (cStat [i].get (j)));
	   }; // for

		if (cObsStat [i] == null) {
			costObsStat [i] = null;
		} 
		else {
			for (int j = 1; j < statLabel.length; j++) {
				costObsStat [i][j - 1] = Trunc.trunc ((Double) (cObsStat [i].get (j)));
			}; // for
		}; // if

	}; // for

    } // CostReport constructor

    /**************************************************************
     * Returns the model name.
     * @return  String		The model name
     */
    public String getModelName () 
    { 
	return modelName; 

    }; // getModelName

    /**************************************************************
     * Returns the number of nodes.
     * @return  int		The number of nodes
     */
    public int    getNumOfNodes () 
    { 
	return numOfNodes; 

    }; // getNumOfNodes

    /**************************************************************
     * Returns the statistic data.
     * @return  double []	The statistic data
     */
    public double [] getStatData ()   
    { 
	return statData; 

    }; // getStatData

    /**************************************************************
     * Returns the name of the statistics.
     * @return  String []	The name of the statistics
     */
    public String [] getStatLabel () 
    { 
	return statLabel; 

    }; // getStatLabel

	/**************************************************************
	 * Returns the names of the cost statistics.
	 * @return  String []	 The name of the cost statistics
	 */
	public String [] getCostStatName () 
	{ 
		return costStatName; 

	}; // getCostStatName


	/**************************************************************
	 * Returns the names of the cost observation statistics.
	 * @return  String []	The name of the cost observation statistics
	 */
	public String [] getCostObsStatName () 
	{ 
		return costObsStatName;

	}; // getCostObsStatName

	/**************************************************************
	 * Returns the cost statistics.
	 * @return  Double[][]	The cost statistics
	 */
	public Double [][] getCostStat () 
	{ 
		return costStat; 

	}; // getCostStat


	/**************************************************************
	 * Returns the cost observation statistics.
	 * @return  Double [][]	The cost observation statistics
	 */
	public Double [][] getCostObsStat () 
	{ 
		return costObsStat; 

	}; // getCostObsStat

    /**************************************************************
     * Returns an XML fragment representing the FinalReport
     * @return  String		The XML fragment representing this instance
     *				of the FinalReport
     */
    public String toString ()
    {
	String xmlStr = "";
	for (int i = 0; i < numOfNodes; i++) {
	   xmlStr += "\n        <Node outputID='" + i + "'>";

		xmlStr += "\n          <CostStat>";
		for (int j = 1; j < statLabel.length; j++) 
		{
			xmlStr += "\n            <" + statLabel [j].trim () + ">";
			xmlStr += costStat [i][j - 1] + "</" + statLabel [j].trim () + ">";
		}; // for
		xmlStr += "\n          </CostStat>";

		if (costObsStat [i] != null) 
		{
			xmlStr += "\n          <CostObsStat>";
			for (int j = 1; j < statLabel.length; j++) 
			{
				xmlStr += "\n            <" + statLabel [j].trim () + ">";
				xmlStr += costObsStat [i][j - 1] + "</" + statLabel [j].trim () + ">";
			}; // for
			xmlStr += "\n          </CostObsStat>";
		}; // if

	   xmlStr += "\n        </Node>";
	}; // for
		
	return xmlStr;

    }; // toString

}; // class FinalReport
