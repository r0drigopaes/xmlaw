/******************************************************************
 * @(#) Signal.java     1.3
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
 * @author      John Miller
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
 * The Signal class allows resource capacity to be altered via signals.
 * A Signal object can affect the behavior of servers, by alternatively,
 * increasing or decreasing the number of tokens (service units).
 */

public class Signal extends DynamicNode
                    implements Runnable
{
    /////////////////// Immutable Variables \\\\\\\\\\\\\\\\\\\\\\\
    /**
     * List of affected servers (currently limited to just one).
     */
    private final Server   serverList;

    /**
     * Coroutine for implementing Runnable interface.
     */
    private final Coroutine   runCoroutine;

    //////////////////////// Variables \\\\\\\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Priority of created entities.
     */
    private int      priority;

    /**
     * Whether the signal is on (green light) or off (red light).
     */
    private boolean  on       = true;

    /**
     * Whether the signal has been stopped.
     */
    private boolean  stopped  = false;
    
    /**
     * Id for use with animator
     */
     private int id;
     
    /**
     * shared queue for communication with animator
     */
     private AnimationQueue sharedQueue; 


    /************************************************************************
     * Constructs a Signal to control the operation of Servers.
     * This constructor has sufficient information to draw the node and its
     * outgoing edges.
     * @param  property    properties of node
     * @param  serverList  list of affected servers
     * @param  outgoing    outgoing transports
     */
    public Signal (Prop         props,
                   Server       serverList,
                   Transport [] outgoing)
    {
        super (props, outgoing);

        this.serverList = serverList;

        priority  = Coroutine.NORM_PRIORITY;
        runCoroutine = new Coroutine ("signal", this);
        //runCoroutine.setPriority (priority);

    }; // Signal
    
    
    /************************************************************************
     * Constructs a Signal to control the operation of Servers.
     * This constructor has sufficient information to draw the node and its
     * outgoing edges.
     * @param  property    properties of node
     * @param  serverList  list of affected servers
     * @param  outgoing    outgoing transports
     */
    public Signal (int 		id,
    		   Prop         props,
                   Server       serverList,
                   Transport [] outgoing,
                   AnimationQueue sharedQueue)
    {
        super (props, outgoing);
        
        this.id = id;
        this.sharedQueue = sharedQueue;

        this.serverList = serverList;

        priority  = Coroutine.NORM_PRIORITY;
        runCoroutine = new Coroutine ("signal", this);
        //runCoroutine.setPriority (priority);

    }; // Signal
    

    /************************************************************************
     * This method starts the Signal thread.
     */
    public void start ()
    {
        //env.clock.start (runCoroutine);

    }; // start


    /************************************************************************
     * This method stops the Signal thread.
     */
    public void stop ()
    {
        stopped = true;
		trc.info( "signal is stopped " + Coroutine.getTime () );
        // trc.show ("run", "signal is stopped", Coroutine.getTime ());
        runCoroutine.end();

    }; // stop


    /************************************************************************
     * This method returns the status of the Signal.
     * @return  boolean  whether this signal is stopped
     */
    public boolean getStopped ()
    {
        return stopped;

    }; // getStopped


    /************************************************************************
     * The run method of Signal.  This determines the lifetime of the 
     * Signal and the signaling cycles.
     */
    public void run ()
    {
		trc.info( "start the " + props.nName + " signal " + Coroutine.getTime () );
        // trc.show ("run", "start the " + props.nName + " signal", Coroutine.getTime ());

        for (int changes = 0; ! stopped && changes < props.nTokens; changes++) {

            on = ! on;    // change the signal

            if (on) {

                /************************************************************
                 * Add a token (service unit)
                 */
				trc.info( "signal " + props.nName + " turned on " +
							Coroutine.getTime () );
                //trc.show ("run", "signal " + props.nName + " turned on",
                //           Coroutine.getTime ());
                serverList.expand ();
                Object [] params = {new Integer(1)};
            	sharedQueue.enqueue (new AnimationMessage(serverList.getId(), params, Coroutine.getTime(), "adjusttokens"));

                //for (Enumeration s = serverList.elements (); 
                //     s.hasMoreElements ();
                //     ((Server) s.nextElement ()).expand ());

            } else {

                /************************************************************
                 * Remove a token (service unit)
                 */
				trc.info( "signal " + props.nName + " turned off " +
							Coroutine.getTime ());
                //trc.show ("run", "signal " + props.nName + " turned off",
                //           Coroutine.getTime ());
                serverList.contract ();
                Object [] params = {new Integer(-1)};
            	sharedQueue.enqueue (new AnimationMessage(serverList.getId(), params, Coroutine.getTime(), "adjusttokens"));


                //for (Enumeration s = serverList.elements (); 
                //     s.hasMoreElements ();
                //     ((Server) s.nextElement ()).contract ());

            }; // if

            double interChangeTime = props.timeDist.gen ();

            //trc.show ("run", "sleep for " + interChangeTime + " ms",
            //           Coroutine.getTime ());

            runCoroutine.pause (interChangeTime);    // sleep

        }; // for

        stop ();

    }; // run


}; // class

