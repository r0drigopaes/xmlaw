/******************************************************************
 * @(#) Split.java     1.3
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
 * The Split class is not functional yet.
 */

public class Split extends DynamicNode
{
    /**
     * Id for use with animator package
     */
     private int id = -1;
     
     /**
      * Shared data structure for use with animation package
      */
      private AnimationQueue sharedQueue;


     /**
      * Type for use in creating a new entity
      */
      private Class entityType;

    /**************************************************************
     * Construct a Split.  This constructor has sufficient information
     * to draw the node.
     * @param  outgoing   outgoing transports
     */
    public Split (double x1, double y1,
                  double x2, double y2,
                  String entityTName,
                  Transport [] outgoing)
    {
        super (null, null);
        try {
            this.entityType = Class.forName (entityTName);
        } catch (ClassNotFoundException ex) { 
            this.entityType = null;
			trc.warning ( "can't find " + entityTName + " : " + ex );
            // trc.tell ("Split", "can't find " + entityTName + " : " + ex);
        } // try       

    }; // Split
    
    /**************************************************************
     * Construct a Split.  This constructor has sufficient information
     * to draw the node.
     * Version of the constructor to use with animator package
     * @param  outgoing   outgoing transports (should be null)
     * @param  id	  id of node
     * @param  queue	  shared queue for use with animator package
     */
    public Split (int 	      id,
		 Prop props,
                 String entityTName, 		 
                 Transport [] outgoing,
                 AnimationQueue queue)
    {
        super (props, null);
        this.id = id;
        this.sharedQueue = queue;
        try {
            this.entityType = Class.forName (entityTName);
        } catch (ClassNotFoundException ex) { 
            this.entityType = null;
			trc.warning ( "can't find " + entityTName + " : " + ex);
            // trc.tell ("Split", "can't find " + entityTName + " : " + ex);
        } // try       

    }; // Split


    /**************************************************************
     * This method is called by SimObjects when they are done with
     * their business and wish to leave the application scenario.
     * It also computes lifetime statistics for SimObjects.
     * @param  SimObject   the SimObject that wishes to be captured.
     */
    public void use (SimObject entity)
    {
            Object [] params = {new Integer(1)};
            sharedQueue.enqueue (new AnimationMessage(this.id, params, Coroutine.getTime(), "adjustserviced"));
			trc.info ( "split entity " + entity.name + " " + Coroutine.getTime ());

			try 
			{
				duration.tally (Coroutine.getTime () - entity.eStartTime);
			} 
			catch (StatException statEx) 
			{}; // try

			cloneEntity (entity);
        //	trc.show ("use", "split entity " + entity.name,
        //				Coroutine.getTime ());
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

    }; // use 


    /************************************************************************
     * Create and start a new entity (SimObject).
     * This can be done internally in the run method, or
     * externally in response to a Java Beans event.
     */
    public void cloneEntity (SimObject entity)
    {
        SimObject eClone = null;
            
        java.awt.Point startLoc = new java.awt.Point (props.location.x,
                                                      props.location.y);
     
        //SimObject entity = maker.makeEntity ();
        //SimObject entity = new SimObject (nodeGroup);  // for testing from main
        //entity.setPriority (priority);
        //entity.setPosition (startLoc);
     
        /********************************************************************
         * This call will determine the type of entity created by the Source.
         */
        try {
            eClone = ( SimObject ) entityType.newInstance ();
            eClone.setQueueReference ( sharedQueue );
            eClone.setCloneId ( entity.getSimId () );
            eClone.setEColor ( entity.getEColor () );
            eClone.setCloneStatus (true);
            entity.setCloneId ( eClone.getSimId () ); 
        } catch (Exception ex) {
			trc.warning ( "unable to create entity of type "
							+ entityType.getName () + " : " + ex );
            // trc.tell ("cloneEntity", "unable to create entity of type "
            //                       + entityType.getName () + " : " + ex);
        }; // try
        
        eClone.setEnvironment (entity.getEnvironment ());
		trc.info ( "start entity " + eClone.name + " " + Coroutine.getTime ());        
        eClone._start (entity.getLiveEntity (), entity.get_Priority (), startLoc);

        // trc.show ("cloneEntity", "start entity " + entity.name,
        //           Coroutine.getTime ());
            
    }; // cloneEntity

}; // class

