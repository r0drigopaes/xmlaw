
/******************************************************************
 * @(#) DBConnectionUIListener.java   1.3
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
 * @author  John Miller, Xueqin Huang
 */
 
package jsim.jrun;

import java.util.*;


/******************************************************************
 * Listener interface specification for DBConnectionUIEvents.
 */
public interface DBConnectionUIListener extends EventListener
{

    /**********************************************************
     * Method to handle DBConnectionUIEvent 
     */
    public void notify (DBConnectionUIEvent evt);

}; // interface

