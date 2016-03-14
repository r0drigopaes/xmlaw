/******************************************************************
 * @(#) FinalReport.java     1.3
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
 * @version     1.3 (5 December 2000)
 * @author      Xueqin Huang, John Miller
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
public class FinalReport extends Message 
{
    /** Serialization ID for instances of this class.
     */
    private static final long  serialVersionUID = 6865934528700823025L;

    /** Model name
     */
    private String      modelName;

//  /** Version number of the model
//   */
//  private int         version;     // version of the model, for future extension

    /** Number of nodes
     */
    private int         numOfNodes;

    /** Statistic data needed by the ReplicationAgent
     */
    private double []   statData;    

    /** Names of the statistic result
     */
    public String []    statLabel;

    /** Name of time statistics
     */
    public String []    timeStatName;

    /** Name of occupancy statistics
     */
    public String []    occuStatName;

    /** Name of cost statistics
     */
    public String []    costStatName;

    /** Name of time observation statistics
     */
    public String []    timeObsStatName;

    /** Name of the occupancy statistics
     */
    public String []    occuObsStatName;

    /** Name of cost observation statistics
     */
    public String []    costObsStatName;

    /** Time statistics
     */
    public Double [][]  timeStat;

    /** Occupancy statistics
     */
    public Double [][]  occuStat;

    /** Cost statistics
     */
    public Double [][]  costStat;

    /** Time observation statistics
     */
    public Double [][]  timeObsStat;

    /** Occupancy observation statistics
     */
    public Double [][]  occuObsStat;

    /** Cost observation statistics
     */
    public Double [][]  costObsStat;


    /**************************************************************
     * Constructs an instance of FinalReport by message type, number of
     * nodes, names of statistics, time statistics, occupancy statistics,
     * time observation statistics, occupancy observation statistics, and
     * statistic data needed by the ReplicationAgent.
     * @param  actionType  The message type
     * @param  modelName   The model name
     * @param  statLabel   The names of the statistics
     * @param  tStat       The time statistics
     * @param  oStat       The occupancy statistics
     * @param  tObsStat    The time observation staticstics
     * @param  oObsStat    The occupancy observation statistics
     * @param  statData    The statistic data prepared by the
     *         ModelBean for the ReplicationAgent in case
     *         the controlling agent is the ReplicationAgent
     */
    public FinalReport (String     actionType,
                        String     modelName,
                        int        numOfNodes,
                        String []  statLabel,
                        Vector []  tStat,
                        Vector []  oStat,
                        Vector []  cStat,
                        Vector []  tObsStat,
                        Vector []  oObsStat,
                        Vector []  cObsStat,
                        double []  statData) 
    {
        this.actionType = actionType;
        this.modelName = modelName;
        this.numOfNodes = numOfNodes;
        this.statData = statData;
        this.statLabel = statLabel;
        this.timeStatName = new String [numOfNodes];
        this.occuStatName = new String [numOfNodes];
        this.costStatName = new String [numOfNodes];
        this.timeObsStatName = new String [numOfNodes];
        this.occuObsStatName = new String [numOfNodes];
        this.costObsStatName = new String [numOfNodes];
        this.timeStat = new Double [numOfNodes][statLabel.length - 1];
        this.occuStat = new Double [numOfNodes][statLabel.length - 1];
        this.costStat = new Double [numOfNodes][statLabel.length - 1];
        this.timeObsStat = new Double [numOfNodes][statLabel.length - 1];
        this.occuObsStat = new Double [numOfNodes][statLabel.length - 1];
        this.costObsStat = new Double [numOfNodes][statLabel.length - 1];

        for (int i = 0; i < numOfNodes; i++) {
            timeStatName [i] = (String) tStat [i].get (0);
            occuStatName [i] = (String) oStat [i].get (0);
            costStatName [i] = (String) cStat [i].get (0);

            if (tObsStat [i] == null) {
                timeObsStatName [i] = null;
            } else {
                timeObsStatName [i] = (String) tObsStat [i].get (0);
            } // if

            if (oObsStat [i] == null) {
                occuObsStatName [i] = null;
            } else {
                occuObsStatName [i] = (String) oObsStat [i].get (0);
            } // if
 
            if (cObsStat [i] == null) {
                costObsStatName [i] = null;
            } else {
                costObsStatName [i] = (String) cObsStat [i].get (0);
            } // if
        } // for
    
        for (int i = 0; i < numOfNodes; i++) {
            for (int j = 1; j < statLabel.length; j++) {
                timeStat [i][j - 1] = Trunc.trunc ((Double) (tStat [i].get (j)));
                occuStat [i][j - 1] = Trunc.trunc ((Double) (oStat [i].get (j)));
                costStat [i][j - 1] = Trunc.trunc ((Double) (cStat [i].get (j)));
            } // for

            if (tObsStat [i] == null) {
                timeObsStat [i] = null;
            } else {
                for (int j = 1; j < statLabel.length; j++) {
                    timeObsStat [i][j - 1] = Trunc.trunc ((Double) (tObsStat [i].get (j)));
                } // for
            } // if

            if (oObsStat [i] == null) {
                occuObsStat [i] = null;
            } else {
                for (int j = 1; j < statLabel.length; j++) {
                    occuObsStat [i][j - 1] = Trunc.trunc ((Double) (oObsStat [i].get (j)));
                } // for
            } // if

           if (cObsStat [i] == null) {
               costObsStat [i] = null;
           } else {
               for (int j = 1; j < statLabel.length; j++) {
                   costObsStat [i][j - 1] = Trunc.trunc ((Double) (cObsStat [i].get (j)));
               } // for
           } // if
        } // for

    } // FinalReport constructor


    /**************************************************************
     * Returns the model name.
     * @return  String  The model name
     */
    public String getModelName () 
    { 
        return modelName; 

    } // getModelName


    /**************************************************************
     * Returns the number of nodes.
     * @return  int  The number of nodes
     */
    public int getNumOfNodes () 
    { 
        return numOfNodes; 

    } // getNumOfNodes


    /**************************************************************
     * Returns the statistic data.
     * @return  double []  The statistic data
     */
    public double [] getStatData ()   
    { 
        return statData; 

    } // getStatData


    /**************************************************************
     * Returns the name of the statistics.
     * @return  String []  The name of the statistics
     */
    public String [] getStatLabel () 
    { 
        return statLabel; 

    } // getStatLabel


    /**************************************************************
     * Returns the names of the time statistics.
     * @return  String []  The name of the time statistics
     */
    public String [] getTimeStatName () 
    { 
        return timeStatName; 

    } // getTimeStatName


    /**************************************************************
     * Returns the names of the occupancy statistics.
     * @return  String []  The name of the occupancy statistics
     */
    public String [] getOccuStatName () 
    { 
        return occuStatName; 

    } // getOccuStatName


    /**************************************************************
    * Returns the names of the cost statistics.
    * @return  String []  The name of the cost statistics
    */
    public String [] getCostStatName () 
    { 
        return costStatName; 

    } // getCostStatName

    /**************************************************************
     * Returns the names of the time observation statistics.
     * @return  String []  The name of the time observation statistics
     */
    public String [] getTimeObsStatName () 
    { 
        return timeObsStatName;

    } // getTimeObsStatName


    /**************************************************************
     * Returns the names of the occupancy observation statistics.
     * @return  String []  The name of the occupancy observation statistics
     */
    public String [] getOccuObsStatName () 
    { 
        return occuObsStatName; 

    } // getOccuObsStatName


    /**************************************************************
    * Returns the names of the cost observation statistics.
    * @return  String []  The name of the cost observation statistics
    */
    public String [] getCostObsStatName () 
    { 
        return costObsStatName;

    } // getCostObsStatName


    /**************************************************************
     * Returns the time statistics.
     * @return  Double [][]  The time statistics
     */
    public Double [][] getTimeStat () 
    { 
        return timeStat; 

    } // getTimeStat


    /**************************************************************
     * Returns the occupancy statistics.
     * @return  Double [][]  The occupancy statistics
     */
    public Double [][] getOccuStat () 
    { 
        return occuStat; 

    } // getOccuStat


    /**************************************************************
    * Returns the cost statistics.
    * @return  Double [][]  The cost statistics
    */
    public Double [][] getCostStat () 
    { 
        return costStat; 

    } // getCostStat


    /**************************************************************
     * Returns the time observation statistics.
     * @return  Double [][]  The time observation statistics
     */
    public Double [][] getTimeObsStat () 
    { 
        return timeObsStat; 

    } // getTimeObsStat


    /**************************************************************
     * Returns the occupancy observation statistics.
     * @return  Double [][]  The occupancy observation statistics
     */
    public Double [][] getOccuObsStat () 
    { 
        return occuObsStat;
 
    } // getOccuObsStat


    /**************************************************************
    * Returns the cost observation statistics.
    * @return  Double [][]  The cost observation statistics
    */
    public Double [][] getCostObsStat () 
    { 
        return costObsStat; 

    } // getCostObsStat


    /**************************************************************
     * Returns an XML fragment representing the FinalReport
     * @return  String  The XML fragment representing this instance
     *          of the FinalReport
     */
    public String toString ()
    {
        String xmlStr = "";

        for (int i = 0; i < numOfNodes; i++) {
            xmlStr += "\n        <Node outputID='" + i + "'>";
            xmlStr += "\n          <TimeStat>";
            for (int j = 1; j < statLabel.length; j++) {
                xmlStr += "\n            <" + statLabel [j].trim () + ">";
                xmlStr += timeStat [i][j - 1] + "</" + statLabel [j].trim () + ">";
            } // for
            xmlStr += "\n          </TimeStat>";

            if (timeObsStat [i] != null) {
               xmlStr += "\n          <TimeObsStat>";
               for (int j = 1; j < statLabel.length; j++) {
                  xmlStr += "\n            <" + statLabel [j].trim () + ">";
                  xmlStr += timeObsStat [i][j - 1] + "</" + statLabel [j].trim () + ">";
               } // for
               xmlStr += "\n          </TimeObsStat>";
            } // if

           xmlStr += "\n          <OccupancyStat>";
           for (int j = 1; j < statLabel.length; j++) {
               xmlStr += "\n            <" + statLabel [j].trim () + ">";
               xmlStr += occuStat [i][j - 1] + "</" + statLabel [j].trim () + ">";
           } // for
           xmlStr += "\n          </OccupancyStat>";

           if (occuObsStat [i] != null) {
               xmlStr += "\n          <OccupancyObsStat>";
               for (int j = 1; j < statLabel.length; j++) {
                   xmlStr += "\n            <" + statLabel [j].trim () + ">";
                   xmlStr += occuObsStat [i][j - 1] + "</" + statLabel [j].trim () + ">";
               } // for
               xmlStr += "\n          </OccupancyObsStat>";
           } // if

           xmlStr += "\n          <CostStat>";
           for (int j = 1; j < statLabel.length; j++) {
               xmlStr += "\n            <" + statLabel [j].trim () + ">";
               xmlStr += costStat [i][j - 1] + "</" + statLabel [j].trim () + ">";
           } // for
           xmlStr += "\n          </CostStat>";

           if (costObsStat [i] != null) {
               xmlStr += "\n          <CostObsStat>";
               for (int j = 1; j < statLabel.length; j++) {
                   xmlStr += "\n            <" + statLabel [j].trim () + ">";
                   xmlStr += costObsStat [i][j - 1] + "</" + statLabel [j].trim () + ">";
               } // for
               xmlStr += "\n          </CostObsStat>";
           } // if

           xmlStr += "\n        </Node>";
        } // for
    
        return xmlStr;

    } // toString


} // FinalReport class

