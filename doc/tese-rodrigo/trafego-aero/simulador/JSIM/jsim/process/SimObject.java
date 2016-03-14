/******************************************************************
 * @(#) SimObject.java     1.3
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
 * @author      John Miller, Rajesh Nair, Matt Perry, Greg Silver
 */

package jsim.process;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.logging.Logger;

import jsim.coroutine.*;
import jsim.queue.*;
import jsim.util.*;
import jsim.animator.*;
import jsim.variate.*;
import jsim.statistic.*;


/******************************************************************
 * The SimObject abstract class allows application specific entity
 * classes to be derived from it (e.g., Customer).  SimObject
 * factors out features common to all entities.
 */

public abstract class SimObject extends Coroutine
{
    //////////////////////// Constants \\\\\\\\\\\\\\\\\\\\\\\\\\\\
    /** Tracing messages
     */
    protected static Logger trc = Logger.getLogger(SimObject.class.getName());
//  public static final Trace trc = new Trace ("SimObject");
    
    /////////////////// Immutable Variables \\\\\\\\\\\\\\\\\\\\\\\
    /** Unique identifier for entity
     */
    protected final int    simId;
    
    /** Identifier of the entity's clone
     */
    protected int cloneId;

    /** Clone status of the entity
     */
    protected boolean cloneStatus;

    /** Status of entity waiting for join
     */
    protected boolean joinWait;

    /** Class of an entity
     * 'Temporary change from final variable to non-final'
     */
     protected int   simClassId;

    /** Entity's color for animation
     */
    public Color   eColor;

    /** Entity's creation time
     */
    public final double  eStartTime;
    
    /** Variate for branching
     */
    protected final Variate	branch = new Variate (0);
    
    //////////////////////// Variables \\\\\\\\\\\\\\\\\\\\\\\\\\\\
    /** Entity's priority
     */ 
    protected int           priority;
    
    /** Entity's current location (for animation)
     */
    private Point2D.Double  position;

    /** Entity's position in queue
     */
    private Q_Node          queuePos;

    /** Entity's index of token currently being used (if any)
     */
            int             tIndex;

    /** Time at which entity starts its current service
     */
            double          initialT;

    /** Duration of service time
     */
            double          serviceT;

    /** Whether the entity is currently preempted
     */
            boolean         preempted;

    /** Counter of lost entities (e.g., server busy)
     */
    private static int      lostEntities = 0;

    /** Entity counter
     */
    private static int      counter = 0;

    /** Cost statistics collected by all entities (sample or batch).
     */
//  protected static final BatchStat  simCost = new BatchStat ("Simulation" + " (cost)");	
    protected double		simCost = 0;
    
    /** Cost of the most recent service used by the entity
     */
    private double			serviceCost = 0;

    ///////////////////////// Shared Variables \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\ 
    
    /** Shared queue for communication with animator
     */
    private AnimationQueue sharedQueue;

    /** List of live entities
     */
    private java.util.LinkedList<Coroutine> liveEntity;        


    /**************************************************************
     * Construct an entity (SimObject).
     * @param  env  environment (containing model)
     */
    public SimObject ()
    {
        this (0);

    } // SimObject
    
    
    /**************************************************************
     * Construct an entity (SimObject).
     * @param  env  environment (containing model)
     */
    public SimObject (int simClassID)
    {
        super ("simobject_" + counter);

        simId = counter++;
        cloneId = Integer.MAX_VALUE;
        cloneStatus = false;
        joinWait = false;

        switch (simId % 3) {
        case 0:
            eColor = new Color (255, (31 * simId) % 224, 0);
            break;
        case 1:
            eColor = new Color (255, 0, (31 * simId) % 256);
            break;
        default:
            eColor = new Color (32 + (31 * simId) % 224, 0, 255);
            break;
        }; // switch

	simClassId = simClassID;
        eStartTime = getTime ();
        priority   = Coroutine.NORM_PRIORITY;
        preempted  = false;

		Trace.setLogConfig ( trc );

    } // SimObject
    

    /**************************************************************
     * Set the environment. (FIX)
     * @param env  The entity's environment
     */
    protected void setEnvironment (Model env) { env = null; }


    /**************************************************************
     * Get the environment. (FIX)
     * @return Model  The entity's environment
     */
    protected Model getEnvironment () { return null; }
    

    /**************************************************************
     * Set the priority of the entity.
     * @param  priority  New priority.
     */
    public void set_Priority (int priority)
    {
        this.priority = priority;
        //setPriority (priority);

    } // setProperties


    /**************************************************************
     * Get the priority of the entity.
     * @return  int  entity's current priority
     */
    public int get_Priority ()
    {
        return priority;

    } // get_Priority


    /**************************************************************
     * Set the class type of an entity.
     * @param simClassId Class Id.
     */
    public void set_simClassId (int simClassId)
    {
        this.simClassId = simClassId;
    
    } // set_simClassId


    /**************************************************************
     * Get the class type of an entity.
     * @return simClassId the class Id of the entity.
     */
    public int get_simClassId () 
    {
        return this.simClassId;

    } // get_simClassId 


    /**************************************************************
     * Start the entity.  The run method MUST be coded in a derived
     * class (e.g., Customer, Client, etc.).
     * @param  liveEntity  Group of live entities.
     * @param  priority    New priority.
     * @param  startLoc    Starting position for entity.
     */
    void _start (java.util.LinkedList<Coroutine> liveEntity, int priority, Point startLoc)
    {
        this.priority = priority;
        this.position = new Point2D.Double (startLoc.x, startLoc.y);
        this.liveEntity = liveEntity;
        joinGroup (liveEntity);
        
        Object [] entityParameters = {new String ("Ellipse2D"), new Label(), new Double(startLoc.x), new Double (startLoc.y),
					new Double (8.0), new Double (8.0), new Double (0.0),
					new Double (0.0), new Double (0.0), new Double (0.0),
					new Integer(0), new Color (eColor.getRGB()) };
        
	//Object [] colorParameters = {new Color (eColor.getRGB())};
	
	sharedQueue.enqueue (new AnimationMessage( simId, entityParameters, getTime(), "create"));
	//sharedQueue.enqueue (new AnimationMessage (simId, colorParameters, 0.0, "setPaint"));
	
    } // init


   /**************************************************************
     * Delay the entity.
     * @param  sleepTime  duration of sleep
     */
    void _sleep (double sleepTime)
    {
        pause (sleepTime);
 
    } // _sleep
 

    /**************************************************************
     * Suspend the entity.
     */
    void _suspend (java.util.LinkedList<Coroutine> queue)
    {
    	Object [] endParameters = {new Integer(0)};
     	sharedQueue.enqueue (new AnimationMessage (simId, endParameters, getTime(), "enqueue"));
        waitIn (queue);
 
    } // _suspend


    /**************************************************************
     * Resume the entity.
     */  
    Coroutine _resume (java.util.LinkedList<Coroutine> queue)
    {
    	Object [] endParameters = {new Integer(0)};
     	sharedQueue.enqueue (new AnimationMessage (simId, endParameters, getTime(), "dequeue"));
        return kickOut (queue);
 
    } // _resume


    /**************************************************************
     * Stop the entity.  This is down by throwing an entity
     * exception to caught in the run method of the entity
     * (e.g. Customer.run).
     * @param   normal           true for normal termination
     * @throws  EntityException  causes entity termination
     */
    void _stop (boolean normal) throws EntityException
    {
        if ( ! normal) {
            if (! isClone()) {
                Object [] endParameters = {new Integer(0)};
     	        sharedQueue.enqueue (new AnimationMessage (simId, endParameters, getTime(), "INCLOST"));
                incLostEntities ();
            } // if
        }; // if
       
        throw new EntityException ();

    } // _stop 


    /**************************************************************
     * Terminate the entity.
     */
    public void _end ()
    {
     	Object [] endParameters = {new Integer(0)};
     	sharedQueue.enqueue (new AnimationMessage (simId, endParameters, getTime(), "destroy"));
    	
        end ();

    } // _end


    /**************************************************************
     * Please stop is a hack to stop the entity from outside its
     * control flow.  The jdk 1.1 "thread.stop" works fine, but is
     * now deprecated.  Still looking for a solution other than
     * having the entity continually check a termination condition.
     * Suggestions welcome.
     * @param  pe  preempted entity
     */
    void please_stop (SimObject pe)
    {
        //env.entityLost ();
        //env.liveEntity.remove (this);
        //stop ();                          // deprecated
        pe = null;

    } // please_stop


    ///////////////////// Getters & Updaters \\\\\\\\\\\\\\\\\\\\\\
    // These should be synchronized


    /**************************************************************
     * Increment the number of lost entities.
     */  
    public synchronized static void incLostEntities ()
    {
        lostEntities++;
 
    } // getLostEntities

 
    /**************************************************************
     * Get the number of lost entities.
     * @return  int  number of lost entities.
     */  
    synchronized static int getLostEntities ()
    {
        return lostEntities;
 
    } // getLostEntities


    /**************************************************************
     * Set the position of the entity.
     * @param  position  new position of entity
     */
    synchronized void setPosition (Point2D.Double position)
    {
    	Point2D.Double oldPosition = this.position;
        this.position = position;
        
        Object [] translateParameters = {new Double (position.getX() - oldPosition.getX()),
        				 new Double (position.getY() - oldPosition.getY()) };
        				 
        sharedQueue.enqueue (new AnimationMessage (simId, translateParameters, getTime(), "translate"));

    } // setPosition


    /**************************************************************
     * Set the position of the entity.
     * @param  position  new position of entity
     */
    synchronized void setPosition (Point position)
    {
    	Point2D.Double oldPosition = this.position;
        this.position = new Point2D.Double (position.x, position.y);
        
        Object [] translateParameters = {new Double (position.getX() - oldPosition.getX()),
        				 new Double (position.getY() - oldPosition.getY()) };
        				 
        sharedQueue.enqueue (new AnimationMessage (simId, translateParameters, getTime(), "translate"));


    } // setPosition


    /**************************************************************
     * Get the position of the entity.
     * @param  Point2D.Double  current position of entity
     */
    synchronized Point2D.Double getPosition ()
    {
        return position;

    } // getPosition


    /**************************************************************
     * Get the integer-valued position of the entity.
     * @param  Point  current position of entity
     */
    synchronized Point getIntPosition ()
    {
        return new Point ((int) (position.x), (int) (position.y));

    } // getIntPosition


    /**************************************************************
     * Get the list containing the live entities.
     * @return java.util.LinkedList LinkedList of live entities
     */
     public java.util.LinkedList<Coroutine> getLiveEntity ()
     {
        return liveEntity;

     } // getLiveEntities


    /**************************************************************
     * Set the queue position of entity.
     * @param  node  queue node
     */
    synchronized void setQueuePos (Q_Node node)
    {
        queuePos = node;

    } // setQueuePos


    /**************************************************************
     * Get the queue position of entity.
     * @param  Q_Node  current queue node
     */
    synchronized Q_Node getQueuePos ()
    {
        return queuePos;

    } // getQueuePos


    /**************************************************************
     * Set the sharedQueue of the SimObject
     * @param queue	the shared queue to reference
     */
     synchronized void setQueueReference (AnimationQueue queue) 
     {
     	this.sharedQueue = queue;
     
     } // setQueueReference
     

     /**************************************************************
      * Return the id of the SimObject
      */   
     public int getSimId() 
     {
         return simId;
            
     } // getSimId


     /**************************************************************
      * Set the color of the entity
      * @param eColor the color of the entity
      */
     public void setEColor ( Color eColor ) 
     {
         this.eColor = eColor;

     } // setEColor
 
    /**************************************************************
     * Return the color of the SimObject
     * @return Color color of the SimObject
     */
    public Color getEColor () 
    {
        return eColor;

    } // getEColor


    /*************************************************************
     * Set the clone Id of the entity
     * @param cloneId clone Id of the entity
     */
    public void setCloneId ( int cloneId)
    {
        this.cloneId = cloneId;

    } // setCloneId      	


    /**************************************************************
     * Return the cloneId of the SimObject
     */   
    public int getCloneId() 
    {
        return cloneId;
            
    } // getCloneId


    /*************************************************************
     * Set the clone status of the entity
     * @param cloneStatus clone Id of the entity
     */
    public void setCloneStatus ( boolean cloneStatus)
    {
        this.cloneStatus = cloneStatus;

    } // setCloneStatus      	


    /**************************************************************
     * Return the clone status of the SimObject
     */   
    public boolean isClone () 
    {
        return cloneStatus;
            
    } // isClone


    /*************************************************************
     * Set the join wait status of the entity
     * @param joinWait join wait status of the entity
     */
    public void setJoinWait ( boolean joinWait)
    {
        this.joinWait = joinWait;

    } // setJoinWait      	


    /**************************************************************
     * Return the join wait status of the SimObject
     */   
    public boolean waitingOnJoin () 
    {
        return joinWait;
            
    } // waitingOnJoin

    /**************************************************************
     * Tally the cost statistics for entities
     */   
    public void tally (double cost) 
    {
        simCost += cost;
        serviceCost = cost;
/********
        try {
            simCost.tally (cost);
            trc.info ( "&&&&&&&&&&&&&&&&&& tallying simulation cost: " + cost );
        } catch (StatException statEx) {
            env.triggerModelReportEvent (statEx);
        } catch (EntityException entEx) {
            System.out.println("!!!!!!!!!!!!!!");
        } // try
*******/     
    } // tally

/*******
    public static Statistic [] getModelStats ()
    {
       Statistic [] statArray = {simCost};
          return statArray;
		
    } // getModelStats
*******/


    /**************************************************************
     * 
     */   
    public double getCost () 
    {
        return simCost;

    } // getCost


    /**************************************************************
     * 
     */   
    public void setCost (double cost) 
    {
        simCost = cost;

    } // setCost


    /**************************************************************
     * 
     */   
    public double getServiceCost ()
    {
        return serviceCost;

    } // getServiceCost


} // SimObject class

