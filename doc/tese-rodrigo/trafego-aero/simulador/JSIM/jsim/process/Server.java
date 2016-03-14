/******************************************************************
 * @(#) Server.java     1.3
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
import java.util.logging.Logger;

import jsim.coroutine.*;
import jsim.statistic.*;
import jsim.util.*;
import jsim.variate.*;
import jsim.animator.*;


/******************************************************************
 * The Server class allows groups of servers to be created.
 * A Server object consists of one or more tokens (service units).
 * To obtain service, an entity (client) must acquire a token.
 */

public class Server extends DynamicNode
{
    /**************************************************************
     * Inner class for token vector.
     * Implemented as a monitor (all methods are synchronized
     * and no IO in main flow).
     */
    protected class TokenVec
    {
        private static final int FACTOR    = 5;
        private        final int MAX_TOKENS;

        private int      numBusy;
        private int      numTokens;
        private Token [] token;

        TokenVec (int initTokens)
        {
            MAX_TOKENS = FACTOR * initTokens;
            numBusy    = 0;
            numTokens  = initTokens;
            token      = new Token [MAX_TOKENS];
            for (int i = 0; i < MAX_TOKENS; i++) {
                 token [i] = new Token ();
            }; // for

        }; // TokenVec

        //synchronized boolean isBusy ()
        boolean isBusy ()
        {
            return numBusy >= numTokens;
    
        }; // isBusy

        //synchronized int getNumBusy ()
        int getNumBusy ()
        {
            return numBusy;

        }; // getNumBusy

        //synchronized int getNumTokens ()
        int getNumTokens ()
        {
            return numTokens;

        }; // getNumTokens

        //synchronized Token getToken (int i)
        Token getToken (int i)
        {
            return token [i];

        }; // getToken

        //synchronized void setLocation (Point p, int i)
        void setLocation (java.awt.Point p, int i)
        {
             token [i].setLocation (p);

        }; // setLocation

        //private synchronized Point getLocation (int i)
        private java.awt.Point getLocation (int i)
        {
             return token [i].getLocation ();

        }; // getLocation

        //private synchronized boolean assign (SimObject client)
        private boolean assign (SimObject client)
        {
            int i = client.tIndex;
            if (0 <= i && i < numTokens) {
                Token tok = token [i];
                if ( ! tok.isBusy ()) {
                    tok.assign (client);
                    numBusy++;
                    return true;
                }; // if
            }; // if
            return false;

        }; // assign

        //private synchronized int findFree ()
        private int findFree ()
        {
            for (int i = 0; i < numTokens; i++) {
                if ( ! token [i].isBusy ()) {
                    return i;
                }; // if
            }; // for
            return -1;

        }; // findFree

        //synchronized boolean free (int i)
        boolean free (int i)
        {
            if (0 <= i && i < numTokens) {
                Token tok = token [i];
                if (tok.isBusy ()) {
                    token [i].free ();
                    numBusy--;
                }; // if
                return true;
            }; // if
            return false;
            
        }; // free

        //synchronized boolean acquires (SimObject entity)
        boolean acquires (SimObject entity)
        {
            if (numBusy < numTokens
               && (entity.tIndex = tokenV.findFree ()) >= 0) {
                tokenV.assign (entity);
                entity.setPosition (tokenV.getLocation (entity.tIndex));
                entity.initialT = Coroutine.getTime ();
                return true;
             } else {
                return false;
             } // if

        }; // acquire

        //synchronized boolean incTokens ()
        boolean incTokens ()
        {
            if (numTokens < MAX_TOKENS) {
                numTokens++;
                return true;
            }; // if
            return false;
                        
        }; // incTokens

        //synchronized boolean decTokens () 
        boolean decTokens () 
        {
            if (numTokens > 0) {
                free (numTokens - 1);
                numTokens--;
                return true;
            }; // if 
            return false;
                        
        }; // decTokens

    }; // inner class


    /////////////////// Immutable Variables \\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Token vector.
     */
    protected final TokenVec  tokenV;
    
    /**
     * id for animator package
     */
     private int id = -1;

    /**
     * shared data structure for animatior packagte
     */
     private AnimationQueue sharedQueue; 

    /**************************************************************
     * Construct a Server.  This constructor has sufficient information
     * to draw the node and its outgoing edges.
     * @param  property  properties of node
     * @param  location  position of node
     * @param  nodeGroup       containing model
     * @param  outgoing  outgoing transports
     */ 
    public Server (Prop         props,
                   Transport [] outgoing)
    {
        super (props, outgoing);
 
        tokenV   = new TokenVec (props.nTokens);
 
        int Xpos = props.location.x + Node.T_SERVER.x - Node.OUT_DIAMETER;
        int Ypos = props.location.y + Node.T_SERVER.y;
 
        for (int j = 0; j < props.nTokens; j++) {
            tokenV.setLocation (new java.awt.Point (Xpos, Ypos), j);
            Ypos += Node.OUT_DIAMETER;
        }; // for
 
    }; // Server

    /**************************************************************
     * Version of the Constructor for use with animator
     * Construct a Server.  This constructor has sufficient information
     * to draw the node and its outgoing edges.
     * @param  id	 id of the server
     * @param  property  properties of node
     * @param  location  position of node
     * @param  nodeGroup       containing model
     * @param  outgoing  outgoing transports
     * @param  sharedQueue  animation queue
     */ 
    public Server (int	    	id,
		   Prop         props,
                   Transport [] outgoing,
		   AnimationQueue sharedQueue)
    {
        super (props, outgoing);
	
	this.id = id;
	this.sharedQueue = sharedQueue; 
        tokenV   = new TokenVec (props.nTokens);
 
        int Xpos = props.location.x + Node.T_SERVER.x - Node.OUT_DIAMETER;
        int Ypos = props.location.y + Node.T_SERVER.y;
 
        for (int j = 0; j < props.nTokens; j++) {
            tokenV.setLocation (new java.awt.Point (Xpos, Ypos), j);
            Ypos += Node.OUT_DIAMETER;
        }; // for
 
    }; // Server
    
    /**********************************************************
     * Returns the id of the Server
     * @return id	id of this server
     */
     public int getId() 
     {
     	return this.id;
     }
     
    /**********************************************************
     * Sets the id of the Server
     * @param id	id for this server
     */
     public void setId(int id) 
     {
     	this.id = id;
     } 
 
 
    /**********************************************************
     * Request a token (service unit).  If no token is available
     * the entity is lost (and its thread is terminated).
     * @param  entity  the SimObject (entity) requesting service
     */
    public void request (SimObject entity)
    {
        if ( ! tokenV.acquires (entity)) {
			trc.info("entity " + entity.name + " lost because Server " + 
						props.nName + " is busy!!! " + Coroutine.getTime ());
            // trc.show ("request", "entity " + entity.name +
            //          " lost because Server " + props.nName + " is busy!!!",
            //          Coroutine.getTime ());
            HashMap<Integer, Coroutine> hJoinMap = entity.getEnvironment().getJoinEntity();
            Integer tempId = new Integer (entity.getCloneId());
            if (hJoinMap.containsKey(tempId)) {
               boolean onMap = entity.initiateJoin(hJoinMap, tempId);
            }
            else {
               if (tempId.intValue() != Integer.MAX_VALUE) {
                  HashMap<Integer, Coroutine> hLostMap = entity.getEnvironment().getLostJoinEntity();
                  tempId = new Integer (entity.getSimId());
                  hLostMap.put ( tempId, entity );
               } // if
            } // if
 
            entity._stop (false);
           
        }; // if

	
    }; // request



    /**************************************************************
     * An entity calls the use method to obtain service.
     * Must call request first.
     * @param  entity   entity obtaining service
     * @param  service  service time
     */
    public void use (SimObject entity, double service, double cost)
    {
        entity.serviceT = service;
        occupancy.accumulate ((double) (tokenV.getNumBusy () - 1), entity.initialT);


		trc.info( "entity " + entity.name + " uses " + entity.tIndex +
					" for " + entity.serviceT + " where " + tokenV.getNumBusy () +
					" of " + tokenV.getNumTokens () + " are busy " +
					Coroutine.getTime () ) ;
        // trc.show ("use", "entity " + entity.name + " uses " + entity.tIndex +
        //          " for " + entity.serviceT + " where " + tokenV.getNumBusy () +
        //          " of " + tokenV.getNumTokens () + " are busy",
        //          Coroutine.getTime ());

        entity._sleep (entity.serviceT);

        double finishT = Coroutine.getTime ();
        occupancy.accumulate ((double) tokenV.getNumBusy (), finishT);

        try {
             duration.tally (finishT - entity.initialT);
			 costs.tally (cost);
			 entity.tally (cost);
        } catch (StatException statEx) {
             //env.triggerModelReportEvent (statEx);
        } catch (EntityException entEx) {
        	//System.out.println("!!!!!!!!!!!!!!");
        }; // try

		trc.info( "entity " + entity.name + " releases " +
					entity.tIndex + " ; numBusy = " + tokenV.getNumBusy () +
					" " + Coroutine.getTime ());
		// trc.show ("use", "entity " + entity.name + " releases " +
        //           entity.tIndex + " ; numBusy = " + tokenV.getNumBusy (),
        //           Coroutine.getTime ());

        tokenV.free (entity.tIndex);
	// Temporary to test entity class
	if (entity.get_simClassId() == 1)
		entity._stop (false);

    }; // use


    /**************************************************************
     * An entity calls the use method to obtain service.  The service
     * time is randomly generated.  Must call request first.
     * @param  entity  entity obtaining service
     */
    public void use (SimObject entity)
    {
        use (entity, props.timeDist.gen (), props.costDist.gen ());

    }; // use


    /**************************************************************
     * Preempt the service of the entity being served by token. 
     * @param   tokenNum   preempt the service of this token
     * @return  SimObject  the preempted entity
     */
    public SimObject preempt (Token pToken, SimObject pEntity)
    {
        //Token     pToken  = tokenV.getToken (tokenNum);
        //SimObject pEntity = pToken.getClient ();

        if (pEntity != null) {
            pToken.free ();
            pEntity.setState (4);
            
            Object [] endParameters = {new Integer(0)};
     	    sharedQueue.enqueue (new AnimationMessage (pEntity.getSimId(), endParameters, pEntity.getTime(), "destroy"));
     	   
     	    sharedQueue.enqueue (new AnimationMessage (pEntity.getSimId(), endParameters, pEntity.getTime(), "INCLOST"));
            pEntity.incLostEntities ();
            
            //pEntity.preempt ();   // remove from service
            return pEntity;
        }; // if

        return null;

    }; // preempt
     


    /**************************************************************
     * Add a service unit (token) to the server.
     */
    public void expand ()
    {
        int nTokens = tokenV.getNumTokens ();

        int Xpos = props.location.x + Node.T_SERVER.x - Node.OUT_DIAMETER;
        int Ypos = props.location.y + Node.T_SERVER.y +
                   nTokens * Node.OUT_DIAMETER;

        if (tokenV.incTokens ()) {
            tokenV.setLocation (new java.awt.Point (Xpos, Ypos), nTokens);
        } else {
			trc.warning( "can not increase the number of tokens");
            // trc.tell ("expand", "can not increase the number of tokens");
        }; // if

    }; // expand
     


    /**************************************************************
     * Remove a service unit (token) from the server.
     * Handle a possible preempted entity (a SimObject).
     */
    public void contract ()
    {
        int nTokens = tokenV.getNumTokens ();
        if (nTokens > 0) {

            Token     pToken  = tokenV.getToken (--nTokens);
            SimObject pEntity = pToken.getClient ();

            tokenV.decTokens ();

            if (pEntity != null) {

                //int freeIndex = tokenV.findFree ();
                //if (freeIndex >= 0) {
                //    Token freeToken = tokenV.getToken (freeIndex);
                //    freeToken.assign (pEntity);

                if ( ! tokenV.acquires (pEntity)) {
                    preempt (pToken, pEntity);              //pEntity);
                    
                    //pEntity._end();
                   // pEntity._end();
                    pEntity.preempted = true;
                    pEntity.serviceT -= Coroutine.getTime () - pEntity.initialT;
					trc.info( "preempted entity " + pEntity.name +
								" lost with " + pEntity.serviceT +
								" time remaining!!! " + Coroutine.getTime ());
					// trc.show ("contract", "preempted entity " + pEntity.name +
                    //          " lost with " + pEntity.serviceT +
                    //          " time remaining!!!", Coroutine.getTime ());
                    pEntity.please_stop (pEntity);
                }; // if

            }; // if

        } else {
			trc.warning( "there are no tokens to remove" );
            // trc.tell ("contract", "there are no tokens to remove");
        }; // if

    }; // contract
     


}; // class

