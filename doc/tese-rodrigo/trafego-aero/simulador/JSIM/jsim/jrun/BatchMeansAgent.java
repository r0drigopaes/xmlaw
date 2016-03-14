
/******************************************************************
 * @(#) BatchMeansAgent.java     1.3
 *
 * Copyright (c) 2000 John Miller, Xuewei Xiang
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

import java.io.*;
import java.util.*;

import jsim.event.*;
import jsim.statistic.*;
import jsim.jmessage.*;

/******************************************************************
 * This model agent uses the ABATCH Batch Means algorithm to
 * control the execution and analyze the output data of a model.
 */

public class BatchMeansAgent extends ModelAgent
                             implements Serializable
{
    ///////////////////////// Constants ///////////////////////////
    /**
     * Initial (and minimum) batch size.
     */
    private static final int  INIT_BATCH_SIZE  = 4;

    /**
     * Initial number of batches.
     */
    private static final int  INIT_NUM_BATCHES = 8;

    /**
     * Step size for increasing the number of batches.
     */
    private static final int  STEP_SIZE = 2;

    ///////////////////////// Variables ///////////////////////////
    /**
     * Current batch size.
     */
    private int        batchSize = INIT_BATCH_SIZE;

    /**
     * Current target number to batches to produce.
     */
    private int        numBatches = INIT_NUM_BATCHES;

    /**
     * Half the maximum number of batches.
     */
    private int        halfSize = INIT_NUM_BATCHES;

    /**
     * Current batch number.
     */
    private int        nBatch = 0;

    /**
     * Array which stores collected batch means (used for doubling
     * batch size).
     */
    private double []  meanArray = new double [2 * INIT_NUM_BATCHES];
 

    /**************************************************************
     * Construct a BatchMeansAgent with the default name.
     */
    public BatchMeansAgent ()
    {
        super ("BatchMeansAgent");

    }; // BatchMeansAgent

    /**************************************************************
     * Collect all the data necessary for starting/controlling a model.
     * @return Simulate The simulation control data to be sent to ModelBean
     */
    protected Simulate collectControlData () 
    {
        String actionType;
        if (!started) actionType = "start";
        else if (quit) actionType = "stop";
        else actionType = "continue";
        Simulate actionData = new Simulate (actionType,
					    "BatchMeansAgent",
                                            new Integer (batchSize), 
					    new Integer (numBatches),
					    new Double (relativePrecision),
 					    new Double (confidenceLevel));
	return actionData;

    }; // collectControlData


    /**************************************************************
     * Collect information about the model agent to be stored in database.
     * @return Simulate	The simulation control data to be stored in database
     */
    protected Simulate collectAgentData () 
    {
        return new Simulate   ( "agent data",
				"BatchMeansAgent",
				new Integer (batchSize),
                                new Integer (numBatches), 
                                new Double (relativePrecision),
                                new Double (confidenceLevel));
    }; // collectAgentData


    /**************************************************************
     * Method to handle report events from executing models.
     * @param  evt  a report event from model
     */
    protected void handleReport (Message msg)
    {
        started = true;   // model is running since it sent me this event

        String reportType = msg.getActionType ();

        /**********************************************************
         * Handle an interim report from the model.
         */
        if (reportType.equals ("Interim")) {

            double [] data = ((InterimReport) msg).getStatData ();
 
            for (int i = 0; i < numBatches; i++) {
 
                if (DEBUG) {
                    trc.show ("notify", "data [" + i + "] = " + data [i]);
                }; // if
 
                agentStat.tally (data [i]);
                meanArray [nBatch++] = data [i];
    
            }; // for
     
            if (testCorrelation ()) {
                quit = true;
            } else {
                numBatches += STEP_SIZE;
            }; // if
 
            fireSimulateEvent ();

        /**********************************************************
         * Handle a final report from the model.
         */
        } else {
            outputResultData ((FinalReport) msg);
        }; // if
 
    }; // handleReport


    /**********************************************************
     * Double the batch size by averaging every two batch means.
     * This combines two batches into one (halving the number of
     * batches).
     * Check if these new batch means satisfy the independence
     * condition, and if so, stop the simulation.
     * @return  boolean  whether simulation should stop
     */
    private boolean doubleBatchSize ()
    {
        double average;

        agentStat.resetStat (0.0);   // reset agentStat to recollect data
                                     // with size of batch doubled.
        if (DEBUG) {
            trc.show ("doubleBatchSize", "batchSize = " + batchSize);

            for (int i = 0; i < 2 * halfSize; i += 1) {
                trc.show ("doubleBatchSize", "meanArray [" + i + "] = " + meanArray [i]);
            }; // for

        }; // if
       
        for (int i = 0; i < 2 * halfSize; i += 2) {
            average = (meanArray [i] + meanArray [i + 1]) / 2;

            if (DEBUG) {
                trc.show ("doubleBatchSize", "meanArray [" + i + "] = " + average);
            }; // if

            agentStat.tally (average);
            meanArray [i / 2] = average;    // Collect first eight batch means
        }; // for                           // from previous samples

        double var = getVariance (nBatch);
        double cl  = 1 - agentStat.getSumSquBatch () /
                    (2 * (double) nBatch  * agentStat.variance ());
 
        double test = cl / Math.sqrt (var);
        double t    = - InverseT.tValue (confidenceLevel, nBatch);
 
        trc.show ("doubleBatchSize", "t value = " + t);
        trc.show ("doubleBatchSize",
                  " nBatch  = " + nBatch +
                  " sumSquBatch = " + agentStat.getSumSquBatch () +
                  " test = " + test);

        return Math.abs (test) <= t && testPrecision ();

    }; // doubleBatchSize


    /**********************************************************
     * Get the variance from the given number of batches.
     * @param   nBatch    number of batches have been collected
     * @return  variance  variance for normal distribution
     */
    private double getVariance (int nBatch)
    {
        return ((double) nBatch - 2) / ((double) (nBatch * nBatch) - 1);

    }; // getVariance


    /**********************************************************
     * New batch has been collected, this function tries to
     * test if the batches collected so far are independent
     * from each other. It tests the H0: there is no correlationship
     * among batches.
     * It uses the following value cl which is N (0, (l-2)/(l*l -1 ))
     * distributed.
     * cl = 1 - sum((Yi,b - Yi+1,b)*(Yi,b - Yi+1,b))/
     *                          (2 * sum((Yi,b - Xt) * (Yi,b - Xt)))
     * Where:
     *        Yi,b    -- batch mean of the ith batch,
     *        Yi+1,b  -- batch mean of the (i+1)th batch,
     *        Xt      -- grand mean of total samples.
     *        l       -- total number of batch.
     * cl value is used to have a t-test, if H0 is accepted, the simulation
     * stops, else the simulation continues.
     * @return  boolean  whether simulation should stop
     */
    private boolean testCorrelation ()
    {
        double cl = 1 - agentStat.getSumSquBatch () /
              (2 * (double) nBatch  * agentStat.variance ());
 
        double var = getVariance (nBatch);
 
        double test = cl / Math.sqrt (var);

        double t = - InverseT.tValue (confidenceLevel, nBatch);
 
        trc.show ("testCorrelation", "t value = " + t);
        trc.show ("testCorrelation",
                  " nBatch = " + nBatch +
                  " sumSquBatch = " + agentStat.getSumSquBatch () +
                  " test = " + test);
 
        if ( Math.abs (test) > t || ! testPrecision () ) {

            if (nBatch >= 2 * halfSize) {
                nBatch     = halfSize;
                batchSize *= 2;
                return doubleBatchSize ();
            } else {
                return false;
            } // if

        } else {
            return true;
        } // if
 
    }; // testCorrelation
   

    /**********************************************************
     * Main method for basic testing batch mean agent bean.
     * @param  args  command-line arguments
     */
    public static void main (String [] arg)
    {
        BatchMeansAgent ma = new BatchMeansAgent ();

        ma.fireSimulateEvent ();         // test fire event

    }; // main


}; // class


