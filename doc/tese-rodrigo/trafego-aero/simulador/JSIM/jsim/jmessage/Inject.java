/******************************************************************
 * @(#) Inject.java     1.3
 *
 * Copyright (c) 2000 John Miller
 * All Right Reserved
 *-----------------------------------------------------------------
 * Permission to use, copy, modify and distribute this software and
 * its documentation without fee is hereby granted provided that
 * this copyright notice appears in all copies.
 * WE MAKE NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY
 * OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. WE SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY ANY USER AS A RESULT OFUSING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *-----------------------------------------------------------------
 *
 * @version     1.3 (5 December 2000)
 * @author      Xueqin Huang, John Miller
 */

package jsim.jmessage;

/******************************************************************
 * This class contains data for the Inject event.
 */
public class Inject extends Message 
{
    /**
     * Time stamp
     */
    private double	timeStamp;

	/**
	 * Unique identifier of the serialized object
	 */
	private static final long serialVersionUID = 6577665443345419283L;

    /**************************************************************
     * Constructs an Inject message by actionType and timeStamp
     * @param  actionType	Type of the message
     * @param  timeStamp	Time stamp
     */
    public Inject (String actionType, double timeStamp) 
    {
	this.actionType = actionType;
        this.timeStamp = timeStamp;

    }; // Inject constructor

    /**************************************************************
     * Returns the time stamp
     * @return  double	The time stamp
     */
    public double getTimeStamp () 
    { 
	return timeStamp; 

    }; // getTimeStamp

}; // class Inject
