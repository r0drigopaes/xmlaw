/******************************************************************
 * @(#) Binomial.java     1.3     98/1/20
 * 
 * Copyright (c) 2005, John Miller, Zhiwei Zhang
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

package jsim.variate;


/******************************************************************
 * Binomial random variate generation.
 */

public class Binomial extends Variate
{
    /////////////////// Immutable Variables \\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Number of trials
     */
    private final int    n;

    /**
     * Probability of success
     */
    private final double p;

    /**
     * Array of parameters
     */
    private  final Double []  params = new Double [3];


    /***************************************************************
     * Constructs a Binomial random variate.
     * @param  n  number of trials
     * @param  p  probability of success
     */
    public Binomial (int n, double p, int i)
    {
        super (i);
        if (p <= 0.0 || p >= 1.0) {
            System.err.println ("Binomial argument error: " +
                        "Probability values is not in (0, 1)");
            System.exit (0);
        }; //if
        this.n = n;
        this.p = p;

	params [0] = new Double (n);
	params [1] = new Double (p);
	params [2] = new Double (i);

     }; // Binomial

    /***************************************************************
     * Constructs a Binomial random variate.
     * @param  n  number of trials  (passed in as double, 
     *            will get truncated to int)
     * @param  p  probability of success
     */
    public Binomial (double n, double p, int i)
    {
        super (i);
        if (p <= 0.0 || p >= 1.0) {
            System.err.println ("Binomial argument error: " +
                        "Probability values is not in (0, 1)");
            System.exit (0);
        }; //if
        this.n = (int)n;
        this.p = p;
       
        params [0] = new Double (n);
        params [1] = new Double (p);
        params [2] = new Double (i);
       
     }; // Binomial


    /**************************************************************
     * Get the parameters of the constuctor
     */
    public Double [] getParameters ()
    {
        return  params;
    }


    /***************************************************************
     * Generate a random number from binomial distribution.
     * @return  double  random number from binomial distribution
     */
    public double gen ()
    {
        int count = 0;
        for (int j = 0; j < n; j++) {
            if (super.gen () <= p )  count++;
        }; // for
        return (double) count;

    }; // gen


}; // class

