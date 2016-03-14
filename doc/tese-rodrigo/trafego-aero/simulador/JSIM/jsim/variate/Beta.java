/******************************************************************
 * @(#) Beta.java     1.3     98/1/20
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
 * Beta random variate generation.
 */

public class Beta extends Variate
{
    /////////////////// Immutable Variables \\\\\\\\\\\\\\\\\\\\\\\
    /**
     * First Gamma random variable
     */
    private final Gamma  gamma1;

    /**
     * Second Gamma random variable
     */
    private final Gamma  gamma2;

    /**
     * Array of parameters
     */
    private  final Double []  params = new Double [3];


    /**************************************************************
     * Constructs a Beta random variate.
     * Beta = Gamma1 / (Gamma1 + Gamma2)
     * @param  alpha  shape parameter for Gamma1
     * @param  beta   shape parameter for Gamma2
     * @param  i      random number stream
     */
    public Beta (double alpha, double beta, int i)
    {
        super (i);
        if (alpha <= 0.0 || beta <= 0.0) {
            System.err.println ("Beta argument error: " +
                        "arguments are less than zero");
            System.exit (0);
        }; // if
        gamma1 = new Gamma (1.0, alpha, i);
        gamma2 = new Gamma (1.0, beta, i);

	params [0] = new Double (alpha);
	params [1] = new Double (beta);
	params [2] = new Double (i);

     }; // Beta


    /**************************************************************
     * Get the parameters of the constuctor
     */
    public Double [] getParameters ()
    {
        return  params;
    }


    /**************************************************************
     * Generate a random number from a Beta distribution.
     * @return  double  random number from Beta distribution
     */
    public double gen ()
    {
        double g1 = gamma1.gen ();
        double g2 = gamma2.gen ();
        return g1 / (g1 + g2);

    }; // gen


}; // class

