/******************************************************************
 * @(#) Erlang.java     1.3     98/1/20
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
 * Erlang random variate generation.
 */

public class Erlang extends Variate
{
    /////////////////// Immutable Variables \\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Mean of exponential samples (Erlang mean = mu * k)
     */
    private final double  mu;

    /**
     * Number of stages (or Exponential samples)
     */
    private final int     k;

    /**
     * Array of parameters
     */
    private  final Double []  params = new Double [3];


    /**************************************************************
     * Constructs an Erlang random varaiate.
     * @param  mu  mean of exponential samples (Erlang mean = mu * k)
     * @param  k   number of stages (or Exponential samples)
     * @param  i   random nuber stream
     */
    public Erlang (double mu, int k, int i)
    { 
        super (i);
        if (mu < 0) {
            System.err.println ("Erlang argument error: " +
                        "mean is less than zero");
            System.exit (0);
        }; // if
        this.mu = mu;
        this.k  = k;

    }; // Erlang


    /**************************************************************
     * Constructs an Erlang random varaiate.
     * @param  mu  mean of exponential samples (Erlang mean = mu * k)
     * @param  k   number of stages (or Exponential samples)
     * @param  i   random nuber stream   
     */
    public Erlang (double mu, double k, int i)
    {  
        super (i);
        if (mu < 0) {
            System.err.println ("Erlang argument error: " +
                        "mean is less than zero");
            System.exit (0);
        }; // if
        this.mu = mu;
        this.k  = (int)k;

	params [0] = new Double (mu);
	params [1] = new Double (k);
	params [2] = new Double (i);

    }; // Erlang


    /**************************************************************
     * Get the parameters of the constuctor
     */
    public Double [] getParameters ()
    {
        return  params;
    }


    /**************************************************************
     * Generate a random number from the Erlang distribution.
     * @return  double  random number from Erlang distribution
     */
    public double gen ()
    {
        double product = 1.0;
        for (int j = 0; j < k; j++) {
            product *= super.gen ();
        }; // for
        return - mu * Math.log (product);

    }; // gen


}; // class

