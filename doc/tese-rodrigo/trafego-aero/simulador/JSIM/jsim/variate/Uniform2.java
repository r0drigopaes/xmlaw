/******************************************************************
 * @(#) Uniform2.java     1.3     98/1/20
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
 * Uniform random variate generation in which variates are constructed
 * by specifying the lower and upper bounds.
 */

public class Uniform2 extends Variate
{
    /////////////////// Immutable Variables \\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Minimum possible value
     */
    private final double  min;

    /**
     * Size of range of values
     */
    private final double  range;

    /**
     * Array of parameters
     */
    private  final Double []  params = new Double [3];


    /**************************************************************
     * Constructs a Unifrom random variate.
     * @param  min  minimum value
     * @param  max  maximum value
     * @param  i    stream
     */
    public Uniform2 (double min, double max, int i)
    { 
        super (i);
        if (max < min) {
            System.err.println ("Uniform2 argument error: Max value is less than min value");
            System.exit (0);
        }; //if
        this.min = min;
        range = max - min;

        params [0] = new Double (min);
        params [1] = new Double (max);
        params [2] = new Double (i);

    }; // Uniform2


    /**************************************************************
     * Get the parameters of the constuctor
     */
    public Double [] getParameters ()
    {
        return  params;
    }


    /**************************************************************
     * Generate a random number from the Uniform distribution.
     * @return  double  random number from Uniform2 distribution
     */
    public double gen ()
    {
        return min + range * super.gen ();

    }; // gen


}; // class

