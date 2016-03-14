/******************************************************************
 * @(#) TestHisto.java     1.3     98/2/7
 * 
 * Copyright (c) 2005, John Miller, Yongfu Ge
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
 * @author      John Miller, Yongfu Ge
 */


package jsim.statistic;

import java.applet.*;
import java.awt.*;


/******************************************************************
 * Test applet for Histogram class and the distributions in the
 * variate package.
 */

public class TestHisto extends Applet
{

	/**
	 * Unique identifier of serialized object
	 */
	private static final long serialVersionUID = 3198605940302020403L;

    /**************************************************************
     * Initialize the histogram control frame.
     */
    public void init ()
    {
        setBackground (Color.black);

        DistHistograms his = new DistHistograms ("Histograms for Distributions");

    }; // init


    /**************************************************************
     * Display for applet.
     * @param  g  the input graphic
     */
    public void paint (Graphics g)
    {
        g.setColor (Color.yellow);
        g.drawString ("If the graph looks bad,", 10, 20 );
        g.drawString ("please adjust the interval", 10, 35);   

    }; // paint


}; // class

