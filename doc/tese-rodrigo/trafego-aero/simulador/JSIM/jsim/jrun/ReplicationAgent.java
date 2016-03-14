
/******************************************************************
 * @(#) ReplicationAgent.java     1.3
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

import java.beans.*;
import java.io.*;
import java.util.*;

import jsim.process.*;
import jsim.statistic.*;
import jsim.jmessage.*;

/******************************************************************
 * This model agent bean which analyzes the model output data using
 * independent replication algorithm
 */

public class ReplicationAgent extends ModelAgent
{
    //////////////////// Variables /////////////////////        
    /**
     * Size of replication
     */
    private int      replicationSize  = 10;
 
    /**
     * Number of replications
     */
    private int      numOfRepl        = 0;

    /**
     * Transient period
     */
    private double   transPeriod      = 500000.0;

    /**************************************************************
     * Constructs a ReplicationAgent with the a default name.
     */
    public ReplicationAgent ()
    {
        super ("ReplicationAgent");

    }; // ReplcationAgent

    /**************************************************************
     * Collect all the data necessary for starting a model simulation.
     * @return Simulate	The simulation control data
     */
    protected Simulate collectControlData ()
    {
	String actionType = "true";
	if (quit) actionType = "false";
	return new Simulate (actionType,
			     "ReplicationAgent", 
			     new Integer (replicationSize), 
			     new Integer (numOfRepl),
			     new Double (transPeriod));

    }; // collectControlData
 
    /**************************************************************
     * Collect information about the model agent to be stored in database.
     * @return Simulate The simulation control data
     */
    protected Simulate collectAgentData ()
    {
        return new Simulate   ( "agent data",
				"ReplicationAgent",
                                new Integer (replicationSize), 
				new Integer (numOfRepl),
				new Double (transPeriod));
    }; // collectAgentData


    /**************************************************************
     * Method to handle report events from executing models.
     */
    protected void handleReport (Message msg)
    {
       trc.show ("handleReport", "handling report from model...");

       started = true;   // model is running since it sent me this event
 
       String reportType = ((FinalReport) msg).getActionType ();
       trc.show ("handleReport", "report type is " + reportType);
       if (reportType.equals ("Interim")) {
           trc.tell ("handleReport", "unexpected report type");
           return;
       }; // if

       double [] data = ((FinalReport) msg).getStatData ();
       numOfRepl ++;
       trc.show ("handleReport", "Replication Agent " + data [0]);
       agentStat.tally (data [0]);
       if (!testPrecision ()) {
           quit = true;
           outputResultData ((FinalReport) msg);
       }  else {
           // FIX - increment stream
           trc.show ("handleReport", "firing simulate event...");
           fireSimulateEvent ();
       }; // if

       
/*
       BatchStat stat = (BatchStat) evt.evInfo.get (1);
       double [] data = stat.getBatchData ();
       numOfRepl ++;
       trc.show ("notify", "Replication Agent " + data [0]);
       agentStat.tally (data [0]);
       
       if (!testPrecision ()) {
           quit = true;
	   trc.show ("notify", "outputing result data..."); 
           outputResultData (evt.evInfo, false);
       }  else {
	   // FIX - increment stream
	   trc.show ("notify", "firing simulate event...");
           fireSimulateEvent ();
       }; // if
*/	  
    }; // handleReport


    /**************************************************************
     * Returns the replication size.
     * @return  int  the size of a replica
     */
    public int getReplicationSize ()
    {
	return replicationSize;

    }; // getReplicationSize


    /**************************************************************
     * Set the value of batch size.
     * @param  val  the batch size
     */
    public void setReplicationSize (int val)
    {
	replicationSize = val;

    }; // setReplicationSize


    /**************************************************************
     * Set the transient period.
     * @param  val  the length of transient period
     */
    public void setTransPeriod (double val)
    {
        transPeriod = val;

    }; // setTransPeriod


    /**************************************************************
     * Get the transient period
     * @return  double  the length of transient period
     */
    public double getTransPeriod ()
    {
        return transPeriod;

    }; // getTransPeriod


}; // class

