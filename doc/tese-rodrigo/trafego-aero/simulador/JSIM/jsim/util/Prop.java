/******************************************************************
 * @(#) Prop.java     1.3
 * 
 * Copyright (c) 2005, John Miller
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
 * @author      John Miller, Greg Silver
 */

package jsim.util;

import java.awt.*;

import jsim.variate.*;


/******************************************************************
 * Structure to record properties of dynamic nodes.
 */

public class Prop
{
    /**
     * Type of node (Node.SERVER, ...).
     */
    public int      nType;

    /**
     * Name of node.
     */
    public String   nName;

    /**
     * Number of tokens (number of service units, etc.).
     */
    public int      nTokens;

    /**
     * Location of node on canvas.
     */
    public Point    location;

    /**
     * Time distribution (service, arrival, etc.).
     */
    public Variate  timeDist;

    /**
     * Cost of service from resource.
     */
    // public double cost;

	/**
	 * Cost distribution 
	 */
	public Variate costDist;

    /**************************************************************
     * Construct a property set.
     * @param  nType     type of node
     * @param  nName     name of node
     * @param  nTokens   number of tokens
     * @param  timeDist  time distribution
     * @param  cost      cost of service
     */
    public Prop (int     nType,
                 String  nName,
                 int     nTokens,
                 Point   location,
                 Variate timeDist,
                 // double  cost,
				 Variate costDist)
    {
        this.nType    = nType;
        this.nName    = nName;
        this.nTokens  = nTokens;
        this.location = location;
        this.timeDist = timeDist;
        // this.cost     = cost;
		this.costDist = costDist;

    }; // Prop

}; // class

