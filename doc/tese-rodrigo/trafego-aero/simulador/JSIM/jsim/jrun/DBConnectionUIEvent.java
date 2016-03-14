
/******************************************************************
 * @(#) DBConnectionUIEvent.java   1.3
 *
 * Copyright (c) 1999 John Miller
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
 * @version 1.3 (13 December 2000)
 * @author  Xueqin Huang, John Miller
 */
 
package jsim.jrun;

import java.util.*;
import jsim.jmessage.*;
 
/******************************************************************
 * A DBConnectionUIEvent is used to inform listeners of database
 * connection information. 
 */

public class DBConnectionUIEvent extends EventObject
{
    /**
     * The event information
     */
    private DBConnectionInfo  evInfo;


    /**************************************************************
     * Construct a DBConnectionUIEvent using a passed-in vector of data.
     * @param  evInfo  detailed information carried with event
     */  
    public DBConnectionUIEvent (String type, DBConnectionInfo evInfo)
    {
        super ("DBConnectionUIEvent");
	this.evInfo = evInfo;

    }; // DBConnectionUIEvent


    /**************************************************************
     * Returns the DBConnectionInfo object.
     * @return DBConnectionInfo	The DBConnectionInfo object
     */
    public DBConnectionInfo getDBConnectionInfo () 
    { 
	return evInfo; 

    }; // getDBConnectionInfo

}; // class 

