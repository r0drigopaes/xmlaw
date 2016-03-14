/******************************************************************
 * @(#) Facility.java     1.3
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


package jsim.process;

import java.util.*;
import java.lang.*;
import java.util.logging.Logger;

import jsim.coroutine.*;
//import jsim.queue.*;
import jsim.statistic.*;
import jsim.variate.*;
import jsim.util.*;
import jsim.animator.*;

 
/******************************************************************
 * The Facility class allows service facilities to be created.
 * A Facility object consists of a set of tokens (service units)
 * feed by a common queue.
 */

public class Facility extends Server
{
    /////////////////// Immutable Variables \\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Duration statistics for time in queue
     */
    private /*final*/ BatchStat  qDuration;

    /**
     * Occupancy statistics for number in queue.
     */
    private /*final*/ TimeStat   qOccupancy;
     
    /**
     * shared data structure for animatior packagte
     */
     private AnimationQueue sharedQueue;

    /**************************************************************
     * Waiting line for entities waiting to use the facility.
     * Implemented as a monitor (all methods are synchronized
     * and no IO in main flow).
     */
    protected class Line
    {
//      private jsim.queue.Queue waitQueue;
        private LinkedList<Coroutine> waitQueue;

//      Line (jsim.queue.Queue waitQueue)
        Line (LinkedList<Coroutine> waitQueue)
        {
            this.waitQueue = waitQueue;

        }; // Line

        //synchronized boolean waits (SimObject entity, double startTime)
        boolean waits (SimObject entity, double startTime)
        {
            if (tokenV.isBusy ()) {
                qOccupancy.accumulate ((double) queueLength (), startTime);
//              try {
/*******************
                    if (waitQueue instanceof PriorityQueue) {
                        entity.setQueuePos (((PriorityQueue) waitQueue).addAt
                                           (entity, entity.get_Priority ()));
                    } else { 
                        entity.setQueuePos (waitQueue.addAt (entity));
                    }; // if 
*******************/
            /*****************************************
	 	     * Send message to Animator
	 	     */
	 	     Object [] params = {new Integer (1)};
         	     sharedQueue.enqueue (new AnimationMessage(getId(), params, Coroutine.getTime(), "adjustserviced"));
                    
                    entity._suspend (waitQueue);
                    
                    return true;
/*******************
                } catch (FullQueueException e) {
                    trc.show ("waits", "entity " + entity.name +
                              " lost because Facility queue is full!!!",
                              Coroutine.getTime ());
                    entity._stop (false);
                } // try
*******************/				
            } // if
            return false;

        }
         // waits

        //synchronized SimObject kick ()
        SimObject kick ()
        {
            if ( ! waitQueue.isEmpty ()) {
                
				/*****************************************
	 			* Send message to Animator
	 			*/
	 			Object [] params = {new Integer (-1)};
				sharedQueue.enqueue (new AnimationMessage(getId(), params, Coroutine.getTime(), "adjustserviced"));
				SimObject entity = (SimObject)waitQueue.getFirst(); 
				return (SimObject) entity._resume (waitQueue); 
                   
            } // if
            return null;

        } // kick

        //synchronized SimObject release (SimObject entity)
        SimObject release (SimObject entity)
        {
            tokenV.free (entity.tIndex);
            if ( ! waitQueue.isEmpty ()) {
                qOccupancy.accumulate ((double) queueLength (), Coroutine.getTime ());
                
                /*****************************************
			 	 * Send message to Animator
	 			 */
	 			Object [] params = {new Integer (-1)};
         		sharedQueue.enqueue (new AnimationMessage(getId(), params, Coroutine.getTime(), "adjustserviced"));
                         
                SimObject nextEntity = (SimObject) entity._resume (waitQueue);
                          
                    
                if ( ! tokenV.acquires (nextEntity) ) {
					trc.warning ( "entity " + nextEntity.name +
									" can't acquire a token");
                    // trc.tell ("release", "entity " + nextEntity.name +
                    //          " can't acquire a token");
                } // if
                return nextEntity;
            } // if 
            return null;

        } // release

        //synchronized int lineLength ()
        int lineLength ()
        {
            return waitQueue.size ();

        }; // lineLength

    }; // inner class


    /**
     * Queue for waiting entities.
     */
    private /*final*/ Line  waitLine;


    /**************************************************************
     * Construct a Facility.  This constructor has sufficient
     * information to draw the node and its outgoing edges.
     * @param  property   properties of node
     * @param  location   position of node
     * @param  waitQueue  where entities wait
     * @param  outgoing   outgoing transports
     */
    public Facility (Prop                  props,
                     LinkedList<Coroutine> waitQueue,
                     Transport []          outgoing)
    {
        super (props, outgoing);

        waitLine   = new Line (waitQueue);
        qDuration  = new BatchStat (props.nName + "Q (dur)");
        qOccupancy = new TimeStat  (props.nName + "Q (occ)");

        int deltaX = Node.T_FACILITY.x - Node.T_SERVER.x;

        for (int i = 0; i < props.nTokens; i++) {
            Token tok = tokenV.getToken (i);
            java.awt.Point loc = tok.getLocation ();
            loc.x += deltaX;
            tok.setLocation (loc);
        }; // for

    }; // Facility
    
    /**************************************************************
     * Construct a Facility.  This constructor has sufficient
     * information to draw the node and its outgoing edges.
     * @param  property   properties of node
     * @param  location   position of node
     * @param  waitQueue  where entities wait
     * @param  outgoing   outgoing transports
     */
    public Facility (int 					id,
    				 Prop					props,
                     LinkedList<Coroutine>	waitQueue,
                     Transport []			outgoing,
                     AnimationQueue			queue)
    {
        super (props, outgoing);

		setId(id);
		this.sharedQueue = queue;
        waitLine   = new Line (waitQueue);
        qDuration  = new BatchStat (props.nName + "Q (dur)");
        qOccupancy = new TimeStat  (props.nName + "Q (occ)");

        int deltaX = Node.T_FACILITY.x - Node.T_SERVER.x;

        for (int i = 0; i < props.nTokens; i++) {
            Token tok = tokenV.getToken (i);
            java.awt.Point loc = tok.getLocation ();
            loc.x += deltaX;
            tok.setLocation (loc);
        }; // for

    }; // Facility


    /**************************************************************
     * Get all statistics for this node.
     * @return  Statistic []  statistics for this node
     */
    public Statistic [] getNodeStats ()
    {
       Statistic [] statArray = {qDuration, qOccupancy, duration, occupancy};
       return statArray;

    }; // getNodeStats


    /**************************************************************
     * Get the length of the facility's wait queue.
     * @return  int  the queue length
     */
    public int queueLength ()
    {
        return waitLine.lineLength ();

    }; // queueLength


    /**************************************************************
     * Request a token (service unit).  If no token is available
     * enqueue the requesting entity in the wait queue.
     * If the queue has finite capacity, the entity may be lost.
     * @param   entity   the SimObject (entity) requesting service
     */
    public void request (SimObject entity)
    {
        double startTime = Coroutine.getTime ();
		trc.info ( "service for entity "  + entity.name + " " 
					+ startTime);
        //trc.show ("request", "service for entity "  + entity.name,
        //           startTime);

        if (waitLine.waits (entity, startTime)) {
			trc.info( "entity " + entity.name + " leaves queue " + 
						Coroutine.getTime ());
            // trc.show ("request", "entity " + entity.name + " leaves queue",
            //           Coroutine.getTime ());
        } else if ( ! tokenV.acquires (entity) ) {
			trc.warning ( "entity "+ entity.name + " can't acquire a token");
            // trc.tell ("request", "entity "+ entity.name +
            //          " can't acquire a token");
        }; // if

		trc.info ( "entity " + entity.name + " acquires a token " 
					+ Coroutine.getTime ());
        // trc.show ("request", "entity " + entity.name + " acquires a token",
        //           Coroutine.getTime ());
        qDuration.tally (Coroutine.getTime () - startTime);
        
        
    }; // request


    /**************************************************************
     * This method causes the SimObject entity to use the server.
     * @param  entity   the SimObject trying to use a server
     * @param  service  service time
     */
    public void use (SimObject entity, double service, double cost)
    {
        entity.serviceT = service;

        occupancy.accumulate ((double) (tokenV.getNumBusy () - 1),
                               entity.initialT);

		trc.info ( "entity " + entity.name + " uses " + entity.tIndex +
					" for " + entity.serviceT + " where " + tokenV.getNumBusy () +
					" of " + tokenV.getNumTokens () + " are busy " +
					Coroutine.getTime ());
        // trc.show ("use", "entity " + entity.name + " uses " + entity.tIndex +
        //          " for " + entity.serviceT + " where " + tokenV.getNumBusy () +
        //          " of " + tokenV.getNumTokens () + " are busy",
        //          Coroutine.getTime ());

        for ( ; ; ) {
            entity._sleep (entity.serviceT);
            if (entity.preempted) {
                entity.preempted = false;
                request (entity);
            } else {
                break;
            }; // if
        }; // for

        double finishT = Coroutine.getTime (); 
        occupancy.accumulate ((double) tokenV.getNumBusy (), finishT);
        duration.tally (finishT - entity.initialT);
		costs.tally (cost);
		entity.tally (cost);

        //entity.yield ();
		trc.info ( "entity " + entity.name + " releases " +
					entity.tIndex + " ; numBusy = " + tokenV.getNumBusy () + " "
					 + Coroutine.getTime ());
        // trc.show ("use", "entity " + entity.name + " releases " +
        //           entity.tIndex + " ; numBusy = " + tokenV.getNumBusy (),
        //           Coroutine.getTime ());

        SimObject nextEntity = waitLine.release (entity);
        if (nextEntity != null) {
			trc.info ( "kick " + nextEntity.name + " out of queue " 
						+ Coroutine.getTime ());
            // trc.show ("use", "kick " + nextEntity.name + " out of queue",
            //           Coroutine.getTime ());
        }; // if
 
    }; // use


    /**************************************************************
     * This method causes the SimObject entity to use the server.
     * The service time is randomly generated.
     * @param  entity  the SimObject trying to use a server
     */
    public void use (SimObject entity)
    {
        use (entity, props.timeDist.gen (), props.costDist.gen ());

    }; // use

 
    /**************************************************************
     * Preempt the service of the entity being served by token.
     * See if there is an entity in the queue to use the new token.
     * @param   tokenNum   preempt the service of this token
     * @return  SimObject  the preempted entity
     */
    public SimObject preempt (int tokenNum)
    {
    	Token     pToken  = tokenV.getToken (tokenNum);
        SimObject pEntity = pToken.getClient ();
        pEntity = super.preempt (pToken, pEntity);

        if (pEntity != null) {
            SimObject nextEntity = waitLine.kick ();
            if (nextEntity != null) {
				trc.info ( "kick " + nextEntity.name +
							" out of queue " + Coroutine.getTime ());
                // trc.show ("preemt", "kick " + nextEntity.name +
                //          " out of queue", Coroutine.getTime ());
            }; // if
        }; // if

        return pEntity;

    }; // preempt
       


    /**************************************************************
     * Preempt the service of the entity being served by token
     * and push it back unto the queue.
     * Not yet tested.
     * @param   tokenNum   preempt the service of this token
     */ 
    public void pushBack (int tokenNum)
    {
        SimObject pEntity = preempt (tokenNum);

        request (pEntity);
 
    }; // pushBack
      


    /**************************************************************
     * Add a service unit (token) to the facility.  
     * See if there is an entity in the queue to use the new token.
     */ 
    public void expand ()
    {
        /******
        int nTokens = tokenV.getNumTokens ();

        int Xpos = location.x + Node.T_FACILITY.x - Node.OUT_DIAMETER;
        int Ypos = location.y + Node.T_FACILITY.y +
                   nTokens * Node.OUT_DIAMETER;

        if (tokenV.incTokens ()) {
            tokenV.setLocation (new java.awt.Point (Xpos, Ypos), props.nTokens);
        } else {
            trc.tell ("expand", "can not increase the number of tokens");
        }; // if

        trc.show ("expand", "***** increase number of tokens to " +
                   tokenV.getNumTokens () + " where numBusy = " +
                   tokenV.getNumBusy (), Coroutine.getTime ());
	*******/
	super.expand();
	
        SimObject nextEntity = waitLine.kick ();
        if (nextEntity != null) {
			trc.info ( "kick " + nextEntity.name + " out of queue " 
						+ Coroutine.getTime ());
            // trc.show ("expand", "kick " + nextEntity.name + " out of queue",
            //           Coroutine.getTime ());
        }; // if

    }; // expand
      


    /**************************************************************
     * Remove a service unit (token) from the facility.
     * Handle a possible preempted entity (a SimObject).
     */
    public void contract ()
    {
        int nTokens = tokenV.getNumTokens ();
        if (nTokens > 0) {
 
            Token     pToken  = tokenV.getToken (--nTokens);
            SimObject pEntity = pToken.getClient ();
 
            tokenV.decTokens ();

			trc.info ( "***** reduce number of tokens to " +
						nTokens + " where numBusy = " + tokenV.getNumBusy () +
						" ***** handle entity " + pEntity + " " 
						+ Coroutine.getTime ());
            // trc.show ("contract", "***** reduce number of tokens to " +
            //           nTokens + " where numBusy = " + tokenV.getNumBusy () +
            //          " ***** handle entity " + pEntity, Coroutine.getTime ());
 
            if (pEntity != null) {
 
                //int freeIndex = tokenV.findFree ();
                //if (freeIndex >= 0) {
                //    Token freeToken = tokenV.getToken (freeIndex);
                //    freeToken.assign (pEntity);
 
                if ( ! tokenV.acquires (pEntity)) {
                    pEntity.preempt ();
                    pEntity.preempted = true;
                    pEntity.serviceT -= Coroutine.getTime () - pEntity.initialT;
					trc.info ( "preempted entity " + pEntity.name +
								" pushed back with " + pEntity.serviceT +
								" time remaining!!! " + Coroutine.getTime ());
                    // trc.show ("contract", "preempted entity " + pEntity.name +
                    //          " pushed back with " + pEntity.serviceT +
                    //          " time remaining!!!", Coroutine.getTime ());
                  /*********
                   * BUG
                   */
                  //  pEntity._resume ();
                }; // if
 
            }; // if
 
        } else {
			trc.warning ( "there are no tokens to remove");
            // trc.tell ("contract", "there are no tokens to remove");
        }; // if

    }; // contract


}; // class

