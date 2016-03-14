
/******************************************************************
 * @(#) JsimEvent.java   1.3
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
 * @version 1.3 (5 December 2000)
 * @author  John Miller, Xueqin Huang
 */
 
package jsim.jmessage;

import java.io.*;
import java.util.*;
 
 
/***************************************************************
 * This class provides general events for Jsim.  If the event
 * does not have special requirements, it can be an instance
 * of this class; otherwise, a class should be derived from
 * this one.
 */

public class JsimEvent extends EventObject
                       implements Serializable
{

    /**
     * The event source.
     */
    private Object source;

    /**
     * The event identifier.
     */
    private long eventID;

    /**
     * The event sequence number.
     */
    //private long seqNum;

    /**
     * The handback/message object.
     */
    private Object handback;

	/**
	 * Unique identifier of the serialized object
	 */
	private static final long serialVersionUID = 3249586723092239495L;

    /***************************************************************
     * Construct a JsimEvent.
     * @param  source		The source of this event
     * @param  eventID		The global ID of this event
     * @param  seqNum		The sequence number (needed in distributed enrionment
     * @param  handback		The event registration object carrying event data
     */
    public JsimEvent (Object  source,
                      long    eventID,
                      //long  seqNum,
                      Object  handback)
    {
         super (source);
         this.source   = source;
         this.eventID  = eventID;
         this.handback = handback;

    }; // JsimEvent

    /***************************************************************
     * Returns the event ID.
     * @return long		The event ID
     */
    public  long getID () 
    { 
	return eventID; 

    }; // getID

    /***************************************************************
     * Returns the name of the event type (rather than just the ID).
     * @return  String		Name/type of the event
     */
    public  String getEventType () 
    {
	 if (eventID == EventMap.INJECT_EVT) {
		  return "INJECT_EVT";
	 } else if (eventID == EventMap.INQUIRE_EVT) {
		  return "INQUIRE_EVT";
	 } else if (eventID == EventMap.INFORM_EVT) {
   		  return "INFORM_EVT";
	 } else if (eventID == EventMap.CHANGE_EVT) {
		  return "CHANGE_EVT";
	 } else if (eventID == EventMap.CHANGED_EVT) {
		  return "CHANGED_EVT";
	 } else if (eventID == EventMap.SIMULATE_EVT) {
		  return "SIMULATE_EVT";
	 } else if (eventID == EventMap.REPORT_EVT) {
		  return "REPORT_EVT";
	 } else if (eventID == EventMap.STORE_EVT) {
		  return "STORE_EVT";
	 } else if (eventID == EventMap.INSTRUCT_EVT) {
		  return "INSTRUCT_EVT";
	 } else if (eventID == EventMap.QUERY_EVT) {
		  return "QUERY_EVT";
	 } else if (eventID == EventMap.RESULT_EVT) {
		  return "RESULT_EVT";
	 } else {
		  return "Unknown event type!";
	 } // if

    }; // getEventType

    /***************************************************************
     * Returns the event registration object.
     * @return  Object		The event registration object
     */
    public Object getRegistrationObject () 
    {
	 return handback;

    }; // getRegistrationObject

}; // class
