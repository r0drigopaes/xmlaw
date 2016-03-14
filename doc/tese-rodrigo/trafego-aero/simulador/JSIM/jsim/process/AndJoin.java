/******************************************************************
 * @(#) DynamicNode.java     1.3
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
 * @author      Greg Silver
 */


package jsim.process;

import java.util.*;
import java.util.logging.Logger;

import jsim.coroutine.*;
import jsim.statistic.*;
import jsim.util.*;
import jsim.animator.*;


/******************************************************************
 * The Sink class allows consumers (sinks) of entities to be created.
 * A Sink object destroys entities and collects statistics at the
 * same time.  It is the counterpart of Source which creates entities.
 */

public class AndJoin extends DynamicNode
{
    //////////////////////// Constants \\\\\\\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Flag indicating whether to trigger an entity injection event.
     */
    private static final boolean TRIGGER = true;

    //////////////////////// Variables \\\\\\\\\\\\\\\\\\\\\\\\\\\\
    /** 
     * Counter maintaining number of entities consumed by this Sink.
     */
    private int entitiesConsumed = 0;
    
    /**
     * Id for use with animator package
     */
     private int id = -1;
     
     /**
      * Shared data structure for use with animation package
      */
      private AnimationQueue sharedQueue;

     /**
      * Data structure where cloned entities wait to join the original.
      */
      private HashMap<Integer, Coroutine> hmap;


    /**************************************************************
     * Construct an AndJoin.  This constructor has sufficient information
     * to draw the node (there are no outgoing edges).
     * @param  props      properties of node
     * @param  outgoing   outgoing transports (should be null)
     */
    public AndJoin (Prop         props,
                    Transport [] outgoing)
    {
        super (props, null);

    }; // Sink
    
    /**************************************************************
     * Construct a AndJoin.  This constructor has sufficient information
     * to draw the node (there are no outgoing edges).
     * Version of the constructor to use with animator package
     * @param  props      properties of node
     * @param  hmap       hashmap where cloned entities wait
     * @param  outgoing   outgoing transports (should be null)
     * @param  id	  id of node
     * @param  queue	  shared queue for use with animator package
     */
    public AndJoin (int          id,
    		    Prop         props,
                    Transport [] outgoing,
                    AnimationQueue queue)
    {
        super (props, null);
        this.id = id;
        this.sharedQueue = queue;

    }; // AndJoin



    /**************************************************************
     * This method is called by SimObjects when they are done with
     * their business and wish to leave the application scenario.
     * It also computes lifetime statistics for SimObjects.
     * @param  SimObject   the SimObject that wishes to be captured.
     */
    public void use (SimObject entity)
    {
        /*****************************************
	 * Send message to Animator
	 */
	    Object [] params = {new Integer(1)};
            sharedQueue.enqueue (new AnimationMessage(this.id, params, Coroutine.getTime(), "adjustserviced"));
			trc.info ( "join cloned entity " + entity.name + " " + Coroutine.getTime ());
            // trc.show ("JBug: use", "join cloned entity " + entity.name, Coroutine.getTime ());
			/************************************
             * Get the hash map containing entities waiting to be joined from the model
             */
            hmap = entity.getEnvironment ().getJoinEntity ();
            Integer tempId = new Integer (entity.getCloneId());

            if (hmap.containsKey(tempId)) {
				try 
				{
					duration.tally (Coroutine.getTime () - entity.eStartTime);
				} 
				catch (StatException statEx) 
				{
					trc.info ( "Stat exception " + statEx );
				}; // try
               SimObject tempEntity = (SimObject) hmap.get(tempId);
               if (tempEntity.isClone()) 
			   {
                  //tempEntity.setJoinWait (false);
				  entity.setCost ( entity.getCost () + tempEntity.getServiceCost ());
                  boolean onMap = tempEntity.initiateJoin(hmap, tempId);
                  tempEntity._stop(false);
               }
               else {
                  if (entity.isClone()) {
                     //tempEntity.setJoinWait (false);
				     tempEntity.setCost ( tempEntity.getCost () + entity.getServiceCost ());
                     boolean onMap = tempEntity.initiateJoin(hmap, tempId);
                     entity._stop(false);
                  }
                  else {
					 trc.warning ( "error on join, entity has no clone " + entity.name + " " + Coroutine.getTime());
                     // trc.show ("JBug: use", "error on join, entity has no clone " + entity.name, Coroutine.getTime());
                  } // if
               } // if
            } 
            else {
               HashMap<Integer, Coroutine> hLostMap = entity.getEnvironment ().getLostJoinEntity ();
               if (! hLostMap.containsKey (tempId)) {
                   Object o = entity.waitForJoin(hmap, new Integer(entity.getSimId()), entity);
               } 
               else {
                     SimObject tempEntity = (SimObject) hLostMap.remove(tempId);
				     entity._stop(false);
               } // if 
               //hmap.put(new Integer(entity.getSimId()), entity);
               //entity.setJoinWait(true);
               //System.out.println("We put " + entity.getSimId() + " on the hashmap");
               //for ( ; ; ) {
               //    entity._sleep(500000);
               //    System.out.println("In wait loop " + entity.waitingOnJoin() + " entity " + entity.name);
               //    if ( ! entity.waitingOnJoin() )
               //       break;
               //} 
            } // if
 	

/*******
        try {
            duration.tally (Coroutine.getTime () - entity.eStartTime);
        } catch (StatException statEx) {
            //env.triggerModelReportEvent (statEx);
        }; // try
*******/
/*******
        if (nTokens > 0) {
            if (timeDist.gen () > 0.0) {
                trc.show ("use", "fire an entity event", Coroutine.getTime ());
                //env.triggerEntityEvent ();   // FIX
                //env.fireEntityEvent ();   // FIX
            }; // if
        }; // if
*******/

//        entity._stop (true);

    }; // use 


}; // class

