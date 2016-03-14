/******************************************************************
 * @(#) Source.java     1.3
 * 
 * Copyright (c) 2005, John Miller, Rajesh Nair
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
 * @author      John Miller, Rajesh Nair, Greg Silver
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
 * The Source class allows generators (sources) of entities to be
 * created.   A Source object periodically creates entities
 * (SimObjects) of a given type.
 */

public class Source extends DynamicNode
                    implements Runnable
{
    /////////////////// Immutable Variables \\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Entity (SimObject) maker.
     */
    //private final EntityMaker  maker;
    private Class        entityType;

    /**
     * Coroutine for implementing Runnable interface.
     */
    private Coroutine       runCoroutine;

    //////////////////////// Variables \\\\\\\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Stopping rule 1:  entitiesCreated      < nTokens
     * Stopping rule 2:  Coroutine.getTime () < stopTime
     */
    private double   stopTime;

    /**
     * Priority of created entities.
     */
    private int      priority;

    /**
     * Number of entities created so far.
     */
    private int      entitiesCreated = 0;

    /**
     * Whether the source has been stopped.
     * 'Made non-static for testing'
     */
    private boolean  stopped         = false;

    /**
     * List of live entities.
     */
    private LinkedList<Coroutine> liveEntity;

    /**
     * The model environment.
     */
    private Model env;
    
    /**
     * The id of the Source
     * Needed for Animator
     */
     private int id = 0;
    
    ///////////////////////// Shared Variables \\\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Shared data structure for communication with animator
     */
    private AnimationQueue sharedQueue;

    /************************************************************************
     * Constructs a Source of entities.  This constructor has sufficient
     * information to draw the node and its outgoing edges.
     * @param  props      property of node
     * @param  stopTime   stop at this time
     * @param  maker      object to make client entities
     * @param  outgoing   outgoing edges
     * @param  queue	  shared Queue for communication with animator
     */
    public Source (Prop         props,
                   double       stopTime,
                   String       entityTName,
                   Transport [] outgoing,
                   AnimationQueue queue)
    {
        super (props, outgoing);

        this.stopTime   = stopTime;
        
        try {
            entityType = Class.forName (entityTName);
        } catch (ClassNotFoundException ex) {
            entityType = null;
			trc.warning( "can't find " + entityTName + " : " + ex );
            // trc.tell ("Source", "can't find " + entityTName + " : " + ex);
        }; // try

        if (props.nTokens <= 0 && stopTime <= 0.0) {
			trc.warning( "both nTokens and stopTime are zero" );
            // trc.tell ("Source", "both nTokens and stopTime are zero");
        } else if (props.nTokens <= 0) {
            props.nTokens = Integer.MAX_VALUE;
        } else if (stopTime <= 0.0) {
            stopTime = Double.MAX_VALUE;
        }; // if
        
        /**
         * Set the shared data structure
         */
        this.sharedQueue = queue;

        priority  = Coroutine.NORM_PRIORITY;
//        runCoroutine = new Coroutine ("source", this);
        //runCoroutine.setPriority (priority);

    }; // Source
    
    /************************************************************************
     * Constructor which uses id
     * Constructs a Source of entities.  This constructor has sufficient
     * information to draw the node and its outgoing edges.
     * @param  id	  id of the node
     * @param  props      property of node
     * @param  stopTime   stop at this time
     * @param  maker      object to make client entities
     * @param  outgoing   outgoing edges
     * @param  queue	  shared Queue for communication with animator
     */
    public Source (int 		id,
    		   Prop         props,
                   double       stopTime,
                   String       entityTName,
                   Transport [] outgoing,
                   AnimationQueue queue)
    {
        super (props, outgoing);

	this.id = id;
        this.stopTime   = stopTime;
        
        try {
            entityType = Class.forName (entityTName);
        } catch (ClassNotFoundException ex) {
            entityType = null;
			trc.warning ( "can't find " + entityTName + " : " + ex);
            // trc.tell ("Source", "can't find " + entityTName + " : " + ex);
        }; // try

        if (props.nTokens <= 0 && stopTime <= 0.0) {
			trc.warning( "both nTokens and stopTime are zero" );
            // trc.tell ("Source", "both nTokens and stopTime are zero");
        } else if (props.nTokens <= 0) {
            props.nTokens = Integer.MAX_VALUE;
        } else if (stopTime <= 0.0) {
            stopTime = Double.MAX_VALUE;
        }; // if
        
        /**
         * Set the shared data structure
         */
        this.sharedQueue = queue;

        priority  = Coroutine.NORM_PRIORITY;
//        runCoroutine = new Coroutine ("source", this);
        //runCoroutine.setPriority (priority);

    }; // Source


    /************************************************************************
     * Constructs a Source of entities.  This constructor has sufficient
     * information to draw the node and its outgoing edges.
     * @param  props      property of node
     * @param  maker      object to make client entities
     * @param  outgoing   outgoing edges
     */
    public Source (Prop         props,
                   String       entityTName,
                   Transport [] outgoing,
                   AnimationQueue queue)
    {
         this (props, Double.MAX_VALUE, entityTName, outgoing, queue);

    }; // Source
    
    /************************************************************************
     * Uses id for Animator
     * Constructs a Source of entities.  This constructor has sufficient
     * information to draw the node and its outgoing edges.
     * @param  id	  id of the node
     * @param  props      property of node
     * @param  maker      object to make client entities
     * @param  outgoing   outgoing edges
     */
    public Source (int 		id,
    		   Prop         props,
                   String       entityTName,
                   Transport [] outgoing,
                   AnimationQueue queue)
    {
         this (id, props, Double.MAX_VALUE, entityTName, outgoing, queue);

    }; // Source

 
    /************************************************************************
     * This method starts the Source thread.
     * @param  liveEntity  list of live entities
     */
    public void start (LinkedList<Coroutine> liveEntity, Model env)
    {
        this.liveEntity = liveEntity;
	stopped = false;
	entitiesCreated = 0;
	this.env = env;
        runCoroutine = new Coroutine ("source", this);
        //env.clock.start (runCoroutine);

    }; // start


    /************************************************************************
     * This method stops the Source thread.
     */
    public void stop ()
    {
		trc.info( "source is done " + Coroutine.getTime ());
        // trc.show ("run", "source is done", Coroutine.getTime ());
	    stopped = true;
        runCoroutine.end ();

    }; // stop


    /************************************************************************
     * This method returns the status of the Source.
     * @return  boolean  whether this source is stopped
     */
    public boolean getStopped ()
    {
        return stopped;

    }; // getStopped


    /************************************************************************
     * Create and start a new entity (SimObject).
     * This can be done internally in the run method, or
     * externally in response to a Java Beans event.
     */
    public void startEntity ()
    {
        SimObject entity = null;

        java.awt.Point startLoc = new java.awt.Point (props.location.x + Node.X_SOURCE [2],
                                                      props.location.y + Node.Y_SOURCE [2]);

        //SimObject entity = maker.makeEntity ();
        //SimObject entity = new SimObject (nodeGroup);  // for testing from main
        //entity.setPriority (priority);
        //entity.setPosition (startLoc);

        /********************************************************************
         * This call will determine the type of entity created by the Source. 
         */
        try {
            entity = (SimObject) entityType.newInstance ();
            entity.setQueueReference (sharedQueue); 
        } catch (Exception ex) {
			trc.warning( "unable to create entity of type "
						+ entityType.getName () + " : " + ex);
            //trc.tell ("startEntity", "unable to create entity of type "
            //                       + entityType.getName () + " : " + ex);
        }; // try

	/*
	if (props.nType == Node.LOADSOURCE)
		entity.set_simClassId (1);
	*/
	entity.setEnvironment (env);
        entity._start (liveEntity, priority, startLoc);
	
		trc.info( "start entity " + entity.name + " " + 
					Coroutine.getTime ());
        // trc.show ("startEntity", "start entity " + entity.name,
        //           Coroutine.getTime ());

    }; // startEntity


    /************************************************************************
     * Get the number of entities created by this source so far.
     * @return  int   number of entities created by this source
     */
    int getEntitiesCreated ()
    {
        return entitiesCreated;

    }; // getEntitiesCreated


    /************************************************************************
     * The run method of Source.  This determines the lifetime of the 
     * Source and the "create entity and sleep for some time" cycles.
     */
    public void run ()
    {
        double interArrivalTime;
        double lastTime;

		trc.info ( "will create " + props.nTokens + " entities until "
					+ stopTime + " " + Coroutine.getTime ());
        // trc.show ("run", "will create " + props.nTokens + " entities until "
        //         + stopTime, Coroutine.getTime ());

        for ( ; ! stopped &&
                (entitiesCreated < props.nTokens) &&
                (Coroutine.getTime () < stopTime);
                entitiesCreated++) {

			trc.info ( "start entity " + entitiesCreated + " " +
						Coroutine.getTime ());
            // trc.show ("run", "start entity " + entitiesCreated,
            //           Coroutine.getTime ());
	    /*****************************************
	     * Send message to Animator
	     */
	    Object [] params = {new Integer(1)};
            sharedQueue.enqueue (new AnimationMessage(this.id, params, Coroutine.getTime(), "adjustserviced"));
            startEntity ();

            interArrivalTime = props.timeDist.gen ();

            lastTime = Coroutine.getTime ();
			trc.info ( "sleeps " + interArrivalTime + "  from " + lastTime );
            // trc.show ("run", "sleeps " + interArrivalTime + "  from ",
            //           lastTime);

            runCoroutine.pause (interArrivalTime);    // sleep

            duration.tally (Coroutine.getTime () - lastTime);

        }; // for

        if (entitiesCreated >= props.nTokens)  stopped = true;

        stop ();

    }; // run


    /*****************************************************
     * Main method for testing purposes only.
     * Must change line in startEntity before calling.
     * @param  args  Command-line arguments.
     */  
    static void main (String [] args)
    {
        /*
	final int MAX = 1;

        Prop props = new Prop (Node.SOURCE, "source", 100,
                               new java.awt.Point (100, 100),
                               new Uniform (2000.0, 500.0, 1));

        Source src = new Source (props, 20000.0, null, null, null);

        src.trc.show ("main", "start the first coroutine");
        Coroutine.start ();
        src.trc.show ("main", "method is finished");
        */
    }; // main
 

}; // class

