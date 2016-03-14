/******************************************************************
 * @(#) HyperGeometric.java     1.3     98/1/20
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
 * Generate a random number from the hypergeometric distribution with
 * population m, number of drawn items n and probability of success p.
 */

public class HyperGeometric extends Variate
{
    /////////////////// Immutable Variables \\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Number of items drawn from population
     */
    private final int  n;

    //////////////////////// Variables \\\\\\\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Size of population
     */
    private int     m;

    /**
     * Expected number of "good" in population
     */
    private double  g;

    /**
     * Expected number of complement of "good" in population
     */
    private double  c;

    /**
     * Array of parameters
     */
    private  final Double []  params = new Double [4];


    /**************************************************************
     * Constructs a Hyper-Geometric random variate.
     * @param  m  size of population
     * @param  n  number of items drawn from population
     * @param  p  probability of success
     * @param  i  stream
     */
    public HyperGeometric (int m, int n, double p, int i)
    {
        super (i);
        if (m < n) {
            System.err.println ("HyperGeometric argument error: " +
                      "population size is less than the number to be drawn");
            System.exit (0);
        }; // if
        this.m = m;
        this.n = n;
        g = p * m;
        c = m - g;

        params [0] = new Double (m);
        params [1] = new Double (n);
        params [2] = new Double (p);
        params [3] = new Double (i);

    }; // HyperGeometric


    /**************************************************************
     * Constructs a Hyper-Geometric random variate.
     * @param  m  size of population
     * @param  n  number of items drawn from population
     * @param  p  probability of success
     * @param  i  stream
     */
    public HyperGeometric (double m, double n, double p, int i)
    {
        super (i);
        if (m < n) {
            System.err.println ("HyperGeometric argument error: " +
                      "population size is less than the number to bedrawn");
            System.exit (0);
        }; // if
        this.m = (int)m;
        this.n = (int)n;
        g = p * m;
        c = m - g;
     
        params [0] = new Double (m);
        params [1] = new Double (n);
        params [2] = new Double (p);
        params [3] = new Double (i);

    }; // HyperGeometric


    /**************************************************************
     * Get the parameters of the constuctor
     */
    public Double [] getParameters ()
    {  
        return  params;
    }


    /**************************************************************
     * Generate a random number from the hypergeometric distribution.
     * @return  double  random number from HyperGeometric distribution
     */
    public double gen ()
    {
        int k = 0;
        for (int j = 0; j < n; j++) {
            if (super.gen () <= g / m) {
                k++;
                g--;
            } else {
                c--;
            }; // if
            m--;
        }; // for
        return (double) k;            

    }; // gen


}; // class

