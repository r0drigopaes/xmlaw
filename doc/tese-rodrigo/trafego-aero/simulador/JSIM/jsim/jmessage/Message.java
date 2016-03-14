
/******************************************************************
 * @(#) Message.java   1.3
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
 * @version 1.3 (12 December 2000)
 * @author  Xueqin Huang, John Miller
 */

package jsim.jmessage;

import java.io.Serializable;

/***************************************************************
 * Message is the parent class of all JSIM messages.
 */
public abstract class Message implements Serializable 
{
    /**
     * Type of message
     */
    protected String actionType;

    /***************************************************************
     * Returns the action type.
     * @return String	The action type.
     */
    public String getActionType () 
    { 
	return actionType;
 
    }; // getActionType

}; // abstract class Message
