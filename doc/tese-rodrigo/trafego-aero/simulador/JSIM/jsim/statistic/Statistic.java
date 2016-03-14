/******************************************************************
 * @(#) Statistic.java     1.3     98/1/20
 * 
 * Copyright (c) 2005, John Miller
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
 * @author      John Miller, Zhiwei Zhang
 */

package jsim.statistic;

import java.io.*;
import java.util.logging.*;

import java.util.*;
import jsim.util.*;


/******************************************************************
 * This class provides facilities for gathering and displaying statistical
 * summary results of simulation runs.
 */

public abstract class Statistic implements Serializable
{
    ///////////////////////// Constants \\\\\\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Largest double value in Java
     */  
    protected final static double  MAX_DOUBLE = Double.MAX_VALUE;
 
    /**
     * Flag indicating whether to monitor (print) values as they are collected
     */  
    protected final static boolean MONITOR    = false;

    /**
     * Default confidence level (.95 or 95%)
     */  
    protected final static double  CONF_LEVEL = 0.95;

    /**  
     * Standard label set for statistical results.
     */
    public final static String [] LABEL = { "StatName ",
                                               "NoSamples",
                                               "MinValue ",
                                               "MaxValue ",
                                               "MeanValue",
                                               "Deviation",
                                               "Interval ",
                                               "Precision"};
 
    //////////////////// Immutable Variables \\\\\\\\\\\\\\\\\\\\\\
    /**
     * Name of the statistic being collected
     */
    protected final String  name;

    /**
     * Tracing messages
     */
	protected static Logger trc;
    // private   final Trace   trc;

    ///////////////////////// Variables \\\\\\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Whether this is the primary statistic for a model
     */
    protected boolean primary;

    /**
     * Number of observations
     */
    protected double  nobs;

    /**
     * Time of last observation
     */
    protected double  lastTime;

    /** 
     * Minimum value of sample data
     */
    protected double  minValue;

    /**
     * Maximum value of sample data
     */
    protected double  maxValue;

    /**
     * Sum of sample data
     */
    protected double  sum;

    /**
     * Sum of squares
     */
    protected double  sumSquares;

    /**
     *  Sum of Square value between two consective observations
     *  collected for batch statistics
     */
    protected double  sumSquBatch;

    /*************************************************************
     * Constructs a statistical object.
     * @param  name  name of statistic
     */
    public Statistic (String name)
    { 
        this.name = name;
		trc = Logger.getLogger(Statistic.class.getName() );
		Trace.setLogConfig ( trc );
        // trc  = new Trace  ("Statistic", name);
        nobs = lastTime = maxValue = sum = sumSquares = sumSquBatch = 0.0; 
        minValue = MAX_DOUBLE; 

    }; // Statistic


    /*************************************************************
     * Set the primary stat indicator.
     * @param  value  whether its primary
     */
    public void setPrimary (boolean value)
    {
        primary = value;

    }; // setPrimary


    /*************************************************************
     * This method returns the minimum value collected.
     * @return  double  the minimum value
     */
    public double min ()
    { 
        return (nobs > 0.0) ? minValue : 0.0;

    }; // min


    /*************************************************************
     * This method returns the maximum value collected.
     * @return  double  the maximum value
     */
    public double max ()
    { 
        return (nobs > 0.0) ? maxValue : 0.0;

    }; // max


    /*************************************************************
      * This method returns the mean of the collected values.
     * @return  double  the mean value 
     */
    public double mean ()
    {
        return (nobs > 0.0) ? (sum / nobs) : 0.0;

    }; // mean


    /*************************************************************
     * This method returns the variance of the collected values.
     * @return  double  the variance
     */
    public double variance ()
    { 
        return (nobs > 0.0) ?
            Math.abs ((sumSquares - (sum * sum) / nobs) / nobs)  :  0.0; 

    }; // variance


    /*************************************************************
     * This method returns the standard deviation of the collected
     * values.
     * @return  double  the standard deviation
     */
    public double stdDev ()
    { 
        return Math.sqrt (variance ()); 

    }; // stdDev


    /*************************************************************
     * This method returns root mean squares of collected data.
     * @return  double  root mean squares
     */
    public double rms ()
    {
        return Math.sqrt (sumSquares);

    }; // rms


    /*************************************************************
     * Return the number of samples.
     * @return  int  number of samples 
     */
    public int samples ()
    {
        return (int) nobs;

    }; // samples


    /*************************************************************
     * Return the confidence interval half-width for level (e.g., .95).
     * @param   level   confidence level
     * @return  double  confidence interval half-width
     */
    public double confidence (double level)
    {
        double df = nobs - 1.0;      // degrees of freedom
        if (df <= 0.0) {
			trc.warning ( "must have at least 2 observations" );
            // trc.tell ("confidence", "must have at least 2 observations");
            return 0.0;
        }; // if

        double p = (1.0 - level) / 2.0;            // e.g., .95 -> .025
        double t = InverseT.tValue (p, df);
		trc.info ( "p = " + p + " df = " + df + " t = " + t );
        // trc.show ("confidence", "p = " + p + " df = " + df + " t = " + t);
        if (t == MAX_DOUBLE) {
			trc.info ( "essentially infinite interval" );
            // trc.tell ("confidence", "essentially infinite interval");
            return t;
        }; // if

        return (t * stdDev ()) / Math.sqrt (df);

    }; // confidence


    /*************************************************************
     * Return the confidence interval half-width for default
     * confidence level.
     * @return  double  confidence interval half-width
     */
    public double confidence ()
    {
        return confidence (CONF_LEVEL);

    }; // confidene


    /*************************************************************
     * Return the relative precision for the given confidence level.
     * @param   level   confidence level
     * @return  double  relative precision
     */
    public double precision (double level)
    {
        return (nobs > 1) ? confidence (level) / mean () : 0.0;

    }; // precision


    /*************************************************************
     * Return the relative precision for the default confidence level.
     * @return  double  relative precision
     */
    public double precision ()
    {
        return precision (CONF_LEVEL);

    }; // precision


    /*************************************************************
     * Reset the statistical counter.
     * @param  time  time of reset
     */
    public void resetStat (double time)
    { 
        lastTime = time; 
        nobs     = 0.0; 
        minValue = MAX_DOUBLE;  
        maxValue = sum = sumSquares = sumSquBatch = 0.0;

    }; // resetStat


    /*************************************************************
     * Return the sum of square value between the consective obs
     * used for batch mean statistics. 
     * @return  double  sum of square of batch means 
     */
    public double getSumSquBatch ()
    {
        return sumSquBatch;

    }; // getSumSquBatch


    /*************************************************************
     * Get statistical value vector for this stat variable.
     * @return  Vector  vector of value for this statistic
     */
    public Vector getStats ()
    {
        Vector<Object> valueVec = new Vector<Object> (LABEL.length);

		valueVec.add (name);
		valueVec.add (new Double (nobs));
		valueVec.add (new Double (min ()));
		valueVec.add (new Double (max ()));
		valueVec.add (new Double (mean ()));
		valueVec.add (new Double (stdDev ()));
		valueVec.add (new Double (confidence ()));
		valueVec.add (new Double (precision ()));

        return valueVec;

    }; // getStats


    /*************************************************************
     * Print column labels.
     */
    public static void printLabel ()
    { 
        System.out.println ( LABEL [0] + "   " +
                             LABEL [1] + "   " +
                             LABEL [2] + "   " +
                             LABEL [3] + "   " +
                             LABEL [4] + "   " +
                             LABEL [5] + "   " +
                             LABEL [6] + "   " +
                             LABEL [7] );

    }; // printLabel


    /*************************************************************
     * Print statistical information.
     */
    public void printStat ()
    { 
        Format.print (System.out, "%12d", (int) nobs);
        Format.print (System.out, "%12.3f", min ());
        Format.print (System.out, "%12.3f", max ());
        Format.print (System.out, "%12.3f", mean ());
        Format.print (System.out, "%12.3f", stdDev ());
        Format.print (System.out, "%12.3f", confidence ());
        Format.print (System.out, "%12.3f", precision ());
        Format.print (System.out, "   %s\n", name );

    }; // printStat


    /*************************************************************
     * Get the name of this statistic.
     * @return  String  the name of this statistic
     */
    public String getName ()
    {
        return name;

    }; // getName


}; // class

