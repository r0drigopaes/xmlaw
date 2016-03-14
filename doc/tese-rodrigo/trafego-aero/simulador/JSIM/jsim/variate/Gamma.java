/******************************************************************
 * @(#) Gamma.java     1.3     98/1/20
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
 * Gamma random variate generation.
 */

public class Gamma extends Variate
{
    /////////////////// Immutable Variables \\\\\\\\\\\\\\\\\\\\\\\
    /**
     *
     */
    private final double  beta;

    /**
     *
     */
    private final double  alpha;

    /**
     *
     */
    private final int     a;

    /**
     *
     */
    private final double  b;

    /**
     *
     */
    private final Erlang  e1;

    /**
     *
     */
    private final Erlang  e2;

    /**
     * Array of parameters
     */
    private  final Double []  params = new Double [3];


    /***************************************************************
     * Constructs a Gamma random variate where
     * mean     = alpha * beta and
     * variance = alpha * beta ^ 2.
     * @param beta   scale parameter  (is this backwards?)
     * @param alpha  shape parameter
     * @param i      random number stream
     */
    public Gamma (double beta, double alpha, int i)
    { 
        super (i);
        if (beta < 0.0 || alpha < 0.0) {
            System.err.println ("Auguments must be greater than zero");
            System.exit (0);
        }; //if
        this.beta  = beta;
        this.alpha = alpha;
        a = (int) alpha;                  // integer part
        b = alpha - (double) a;           // fractional part
        e1 = new Erlang (beta, a, i);
        e2 = new Erlang (beta, a + 1, i);

        params [0] = new Double (alpha);
        params [1] = new Double (beta);
        params [2] = new Double (i);

    }; // Gamma


    /**************************************************************
     * Get the parameters of the constuctor
     */
    public Double [] getParameters ()
    {  
        return  params;
    }


    /***************************************************************
     * Generates a random number from the Gamma distribution
         * @return double
     */
    public double gen ()
    {
        double x = 0.0;
        double y = 0.0;
        if (alpha < 1.0) {                       // 0 < alpha < 1
            do {
                x = Math.pow (super.gen (), 1.0 / alpha);
                y = Math.pow (super.gen (), 1.0 / (1.0 - alpha));
            } while (x + y > 1);
            return (x / (x + y)) * (- Math.log (super.gen ())) * beta;

        } else  if (alpha < 5.0) {               // 1 <= alpha < 5
            do {
                x = alpha / (double) a;
                double prod = 1.0;
                for (int i = 0; i < a; i++)  prod *= super.gen ();
                x *= - Math.log (prod);
            } while (super.gen () > Math.pow (x/alpha, b) * Math.exp (-b*x/(alpha-1.0)));
            return x * beta;

        } else {                                 // alpha >= 5
            if (super.gen () >= b) {
                return e1.gen ();
            } else {
                return e2.gen ();
            } // if
        } // if

    }; // gen


}; // class

