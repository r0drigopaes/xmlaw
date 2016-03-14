/******************************************************************
 * @(#) F_Distribution.java     1.3     98/1/20
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
 * F-Distribution random variate generation.
 */

public class F_Distribution extends Variate
{
    /////////////////// Immutable Variables \\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Degrees of freedom for numerator Chi-Square
     */
    private final int       m;

    /**
     * Degrees of freedom for denominator Chi-Square
     */
    private final int       n;

    /**
     * First ChiSquare random variate
     */
    private final ChiSquare chi1;

    /**
     * Second ChiSquare random variate
     */
    private final ChiSquare chi2;

    /**
     * Array of parameters
     */
    private  final Double []  params = new Double [3];


    /**************************************************************
     * Constructs an F-Distribution random variate.
     * @param m    degrees of freedom for numerator Chi-Square
     * @param n    degrees of freedom for denominator Chi-Square
     * @param i    random number stream
     */
    public F_Distribution (int n, int m, int i)
    { 
        super (i);
        this.m = m;
        this.n = n;
        chi1   = new ChiSquare (m, i);
        chi2   = new ChiSquare (n, i);

        params [0] = new Double (n);
        params [1] = new Double (m);
        params [2] = new Double (i);

    }; // F_Distribution

    /**************************************************************
     * Constructs an F-Distribution random variate.
     * @param m    degrees of freedom for numerator Chi-Square
     * @param n    degrees of freedom for denominator Chi-Square
     * @param i    random number stream
     */
    public F_Distribution (double n, double m, int i)
    {  
        super (i);
        this.m = (int)m;
        this.n = (int)n;
        chi1   = new ChiSquare (m, i);
        chi2   = new ChiSquare (n, i);
    
        params [0] = new Double (n);
        params [1] = new Double (m);
        params [2] = new Double (i);  
       
    }; // F_Distribution


    /**************************************************************
     * Get the parameters of the constuctor
     */
    public Double [] getParameters ()
    {
        return  params;
    }


    /**************************************************************
     * Generate a random number from the F-Distribution.
     * @return double
     */
    public double gen ()
    {
        double c1 = chi1.gen ();
        double c2 = chi2.gen ();
        return (m * c1) / (n * c2);

    }; // gen


}; // class

