/******************************************************************
 * @(#) JsimBean.java     1.3
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

/******************************************************************
 * JsimBean is the parent class of all beans and agents in JSIM.
 */
public interface JsimBean extends Serializable
{
    /**************************************************************
     * Adds a JsimListener to an event object registration list.
     * @param  l	The target JsimListener to be added
     */
    void addJsimListener (JsimListener l);

    /**************************************************************
     * Removes a JsimListener from an event object registration list.
     * This is not used in Jini. It is for backward compatiability with
     * JavaBean events.
     * @param l		The target JsimListener to be removed
     */
//    void removeJsimListener (JsimListener l);

    /**************************************************************
     * Fires a JsimEvent.
     * @param  evt	An JsimEvent
     */
//    void fireJsimEvent (JsimEvent evt);

}; // interface JsimBean
