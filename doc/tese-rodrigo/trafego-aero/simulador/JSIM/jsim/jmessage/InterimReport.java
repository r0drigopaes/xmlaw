/******************************************************************
 * @(#) InterimReport.java     1.3
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

import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.util.Date;
import java.util.Vector;

/******************************************************************
 * This class contains interim report data for the Report event.
 */
public class InterimReport extends Message 
{
    /**
     * Statistical data
     */
    private double []		statData;

	/**
	 * Unique identifier of the serialized object
	 */
	private static final long serialVersionUID = 8584732343129393948L;

    /**************************************************************
     * Constructs an InterimReport message by actionType and statData.
     * @param  actionType	Type of message
     * @param  statData		Statistical data
     */
    public InterimReport (String    actionType,
                          double [] statData) 
    {
	this.actionType = actionType;
        this.statData = statData;

    }; // InterimReport constructor

    /**************************************************************
     * Returns the statData.
     * @return  double []	The statistical data
     */
    public double [] getStatData () 
    { 
	return statData; 

    }; // getStatData

}; // class InterimReport
