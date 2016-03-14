/******************************************************************
 * @(#) DiscreteProb.java     1.3     98/1/20
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
 * Discrete probability random variate generation.
 */

public class DiscreteProb extends Variate
{
    /////////////////// Immutable Variables \\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Cummulative probability (CDF)
     */
    private final double  cumProb [];

    /**
     * Data array
     */
    private final double  value [];


    /**************************************************************
     * Constructs a Discrete Probability random variate.
     * @param  cumProb  cummulative probability
     * @param  value    value array
     * @param  i        random number stream
     */
    public DiscreteProb (double cumProb [], double value [], int i)
    {
        super (i);
        this.cumProb = cumProb;
        this.value   = value;

    }; // DiscreteProb


    /**************************************************************
     * Generate a random number from Discrete Probability distribution.
     * @return  double  random number from Discrete Probability distribution
     */
    public double gen ()
    {
        double rand = super.gen ();
        for (int j = 0; j < cumProb.length; j++) {
            if (rand <= cumProb [j]) {
                return value [j];
            }; //if
        }; //for
        return value [cumProb.length - 1];

    }; // gen


}; // class

