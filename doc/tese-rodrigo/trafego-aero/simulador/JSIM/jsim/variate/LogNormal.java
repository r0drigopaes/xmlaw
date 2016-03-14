
/******************************************************************
 * @(#) LogNormal.java     1.0     98/1/20
 *
 * Copyright (c) 1998 John Miller, Zhiwei Zhang
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
 * @version     1.0, 20 Jan 1998
 * @author      Zhiwei Zhang, John Miller
 */

package jsim.variate;


/******************************************************************
 * Log Normal random variate generation.
 */

public class LogNormal extends Variate
{
    /////////////////// Immutable Variables \\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Base Normal distribution
     */
    private final Normal normal;

    /**
     * Array of parameters
     */
    private  final Double []  params = new Double [3];


    /**************************************************************
     * Constructs a Log Normal random variate.
     * @param  mu     mean
     * @param  sigma  standard deviation
     * @param  i      random number stream
     */
    public LogNormal (double mu, double sigma, int i)
    { 
        super (i);
        double variance = Math.log ((sigma * sigma) / (mu * mu) + 1.0);
        double mean     = Math.log (mu) - 0.5 * variance;
        normal = new Normal (mean, Math.sqrt (variance), i);

        params [0] = new Double (mu);
        params [1] = new Double (sigma);
        params [2] = new Double (i);

    }; // LogNormal


    /**************************************************************
     * Get the parameters of the constuctor
     */
    public Double [] getParameters ()
    {  
        return  params;
    }


    /**************************************************************
     * Generate a random number from Log Normal distribution.
     * @return  double  random number from LogNormal distribution
     */
    public double gen ()
    {
        return Math.exp (normal.gen ());

    }; // gen
    

}; // class

