/******************************************************************
 * @(#) SampleStat.java     1.3     98/1/20
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

package jsim.statistic;


/******************************************************************
 * This class provides facilities for gathering and displaying sample 
 * statistical summary results of simulation runs.
 */

public class SampleStat extends Statistic
{
        /**********************************************************
         * Added by Xuewei Xiang, 11/17/98
         * valuable to store previous value of obs.
         */
        double   oldMu = 0.0;


	/**
	 * Unique identifier of serialized object
	 */
	private static final long serialVersionUID = 6777463523726212309L;
	/**********************************************************
	 * Constructs a Sample Statistic with the given name
	 * @param name		the name of this Sample Statistic
	 */
	public SampleStat (String name)
        {
		super (name);

	}; // SampleStat

	/**********************************************************
	 * Tally statistical information on sample data.
         * modified by Xuewei Xiang, 11/17/98
	 * @param  val		value to collect
	 * @return SampleStat	the object itself
	 */
	public boolean tally (double val)
        {


                if ( nobs == 0)
                     oldMu = val;
                else{

                     sumSquBatch += (val - oldMu) * (val - oldMu);
                     oldMu = val;
                }

 
		if (MONITOR) {
			System.out.println ("value = " + val);
		}; // if		
		++nobs;
		if (val < minValue) {
			minValue = val;
		}; // if
		if (val > maxValue) {
			maxValue = val;
		}; // if    
		if (MONITOR) {
			System.out.println ("max val = " + maxValue);
			System.out.println ("min val = " + minValue);
		}; // if

		sum += val;
		sumSquares += val * val;

		return true;

	}; // tally


}; // class

