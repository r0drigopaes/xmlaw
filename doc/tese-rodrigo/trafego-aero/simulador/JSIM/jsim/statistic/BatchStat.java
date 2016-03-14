/******************************************************************
 * @(#) BatchStat.java     1.3     98/11/28
 * 
 * Copyright (c) 2005, John Miller, Xuewei Xiang
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   
 * 1. Redistributions of source code must retain the above
 *    copyright notice, this list of conditions and the following
 *    disclaimer. 
 * 2. Redistributions in binary form must reproduce the above
 *    copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials
 *    provided with the distribution. 
 * 3. Neither the name of the University of Georgia nor the names
 *    of its contributors may be used to endorse or promote
 *    products derived from this software without specific prior
 *    written permission. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND 
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR 
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * @version     1.3, 28 Jan 2005
 * @author      John Miller, Xuewei Xiang
 */

package jsim.statistic;

import jsim.util.*;

import java.util.*;
import java.util.logging.Logger;

/******************************************************************
 * This class provides facilities for gathering and displaying
 * statistical summary results of batch runs.
 */

public class BatchStat extends SampleStat
{ 
    /////////////////////// Constants /////////////////////////////
    /**
     * Maximum number of batches
     */
    private final static int  MAX_BATCHES = 500;

    /**
     * Default size of a batch 
     */
    private final static int  DEFAULT_BATCH_SIZE = 10;

    ///////////////// Immutable Variables  ////////////////////////
    /**
     * Tracing messages
     */
	private static Logger trc;
    // private final Trace       trc;

    /**
     * Individual observations are also tallied
     */
    private final SampleStat  obs;

    ////////////////////// Variables  /////////////////////////////
    /**
     * Size of batch
     */
    private int        batchSize;

    /**
     * Old size of batch, in case batch size is double.
     */
    private int        oldBatchSize = 0;

    /**
     * Number of batch need to be collected by model manager
     */
    private int        numberOfBatch;

    /**
     * Array to store the batch mean values collected for Model Manager.
     */
    private double []  batchHolder;

    /**
     * Array to store the batch mean values locally for statistical
     * data output
     */
    private double []  localArray;

    /**
     * Cursor that indicates the index of next input in local Array 
     */
    private int        cursor = 0;

    /**
     * The current batch number
     */
    private int        nBatch = 0;

    /**
     * The number of observations collected in the current batch
     */
    private int        count = 0;

    /**
     * Sum of collected values within a batch  
     */
    private double     bSum = 0.0;

    /**
     * Flag to show if this BatchStat is the primary statistic
     */
    private boolean    primary = false;

	/**
	 * Unique identifier of serialized object
	 */
	private static final long serialVersionUID = 124573465970698857L;
    /**********************************************************
     * Constructs a batch statistic with name and default batch
     * size.
     * @param name         name of the batch statisitic
     */
    public BatchStat (String name)
    {
        this (name, DEFAULT_BATCH_SIZE);

    }; // BatchStat


    /**********************************************************
     * Constructs a batch statistic with name and batch size.
     * @param name         name of the batch statisitic
     * @param batchSize    size of a batch
     */
    public BatchStat (String name, int batchSize)
    {
        super (name);

		trc = Logger.getLogger(BatchStat.class.getName() );
		Trace.setLogConfig ( trc ); 
        // trc = new Trace ("BatchStat", name);
        obs = new SampleStat ("-" + name);

        this.batchSize = batchSize;
        numberOfBatch  = DEFAULT_BATCH_SIZE;

        batchHolder = new double [MAX_BATCHES];
        localArray  = new double [MAX_BATCHES];

    }; // BatchStat


    /**********************************************************
     * Get the batch size.
     * @return int  current batch size
     */
    public int getBatchSize ()
    {
        return batchSize;

    }; // getBatchSize


    /**********************************************************
     * Set the batch size.
     * @param batchSize  the batch size
     */
    public void setBatchSize (int batchSize)
    {
        this.batchSize = batchSize;

    }; // setBatchSize


    ///////////////////////////////////////////////////////////
    // Methods to process batch data.
    ///////////////////////////////////////////////////////////

    /**********************************************************
     * Save the value of the observation in bSum.
     * If a full batch has been collected, call process batch.
     * @param  value       value being tallied
     * @return boolean signal enough batch data has been collected
     *                 is true, otherwize continues to collect
     */  
    public boolean tally (double value) throws StatException
    {
        obs.tally (value);   // also tally individual observations

        bSum += value;
        count++;

        if (count >= batchSize) {

            double  mu  = bSum / (double) count;
            super.tally (mu);    // collect mean value of current batch
            batchHolder [nBatch] = mu;
            localArray [cursor]  = mu;
            bSum  = 0.0;
            count = 0;
            nBatch++;
            cursor++;
           
			trc.info ( " batch # = " + cursor + " out of " + numberOfBatch );
            // trc.show ("tally", " batch # = " + cursor + " out of " + numberOfBatch);

            // Enough batch mean data has been collected 
            if (nBatch >= numberOfBatch) {
                nBatch = 0;
                if (primary) {
                    System.out.println ("throws the StatException");
                    StatException e = new StatException (getBatchData ()); 
                    throw e; 
                    //return false;
               }; // if

            }; // if

        }; // if
 
        return true;

    }; // tally
 

    /**********************************************************
     * Get the array which stores "cursor" batch means collected with
     * previous batch size. Double the batch size and recalculate
     * batch mean for first "cursor"/2 batches with new batch size.
     * "cursor" stores the size of input array.
     * @param  meanArray  array which stores 16 batch means with
     *                    previous batch size.
     */
    private void doubleBatchSize (double [] meanArray)
    {
        resetStat (0.0);         // reset bMeanBatch to recollect data
                                 // with size of batch doubled.
        double val;
 
        for (int i = 0; i < cursor; i = i + 2) {
              val = (meanArray [i] + meanArray [i + 1]) / 2;
              super.tally (val);
              meanArray [i / 2] = val;    // Collect first eight batch means
        }; // for                         // from previous samples
 
        cursor = cursor / 2;              // reset the cursor 
 
        System.out.println ("cursor = " + cursor);

    }; // doubleBatchSize
 

    /**********************************************************
     * Set the batch size and number of batches.
     * In case the size of batch is doubled, adjust the local
     * data stores (localArray) accordingly.
     * @param  bSize     size of batches 
     * @param  nBatches  number of batches have been collected
     */  
    public void setBatchProperties (int bSize, int nBatches)
    {
        batchSize     = bSize; 
        numberOfBatch = nBatches;

        if (oldBatchSize == 0) {
           oldBatchSize = bSize;
        } else if (2 * oldBatchSize == bSize) {
           doubleBatchSize (localArray);
           oldBatchSize = bSize;
        } else {
           oldBatchSize = bSize;
        }; // if

    }; // setBatchProperties


    /**********************************************************
     * Get the vector which collects all the batch mean values.
     * @return  double []  batch mean vector
     */  
    public double [] getBatchData ()
    {
        return batchHolder;

    }; // getBatchData


    /**********************************************************
     * Get observational statistics for this batch stat variable.
     * @return  Vector  observational stat results
     */  
    public Vector getObsStats ()
    {
        return obs.getStats ();

    }; // getObstats


}; // class

