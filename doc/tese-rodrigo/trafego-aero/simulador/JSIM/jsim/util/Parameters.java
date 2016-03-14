/******************************************************************
 * @(#) Parameters.java     1.3
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
 * @author      John Miller
 */

package jsim.util;


/******************************************************************
 * Contains constants used to represent parameters.
 */

public class Parameters
{
    /**
     * Type of node (Node.SERVER, ...).
     */
	public static final String [] OUT_COND_TYPE = 
		{
			"probability",
			"class Id",
			"cost"
		};

	public static final String [] OUT_COND_FIELD = 
		{
			"prob",
			"simClassId",
			"simCost"
		};

	public static final int PROBABILITY_TYPE = 0;
	public static final int CLASS_ID_TYPE = 1;
	public static final int COST_TYPE = 2;	

	public static final String [] DISTRIBUTION = {   "Bernoulli",
													 "Binomial",
													 "ChiSquare",
													 "Erlang",
													 "Exponential",
													 "Gamma",
													 "Geometric",
													 "HyperExponential",
													 "HyperGeometric",
													 "LogNormal",
													 "Normal",
													 "Triangular",
													 "Uniform",
													 "Weibull" };

													
}; // class
