/******************************************************************
 * @(#) ModelMenu.java     1.3
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

import java.awt.*;
import java.awt.event.*;
import java.util.logging.*;

import jsim.coroutine.*;
import jsim.util.*;


/******************************************************************
 * The ModelMenu class defines the main menu for simulation models.
 */

public class ModelMenu extends    Menu
                       implements ActionListener
{
    /////////////////// Immutable Variables \\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Start simulation control.
     */
    private final MenuItem  star;

    /**
     * Stop simulation control.
     */
    private final MenuItem  stop;

    /**
     * Fast simulation control.
     */
    private final MenuItem  fast;

    /**
     * Slow simulation control.
     */
    private final MenuItem  slow;

    /**
     * Show simulation control.
     */
    private final MenuItem  show;
    
    /**
     * Show statistics control.
     */
    private final MenuItem  stat;

    /**
     * Containing model.
     */
    private final Model     env;

	/**
	 * Unique identifier of serialized object
	 */
	private static final long serialVersionUID = 4837285496859483938L;

    /*****************************************************************
     * Construct a control menu for the model.
     * @param  env  containing model.
     */
    ModelMenu (Model env)
    {
        super ("JSIM Controls");

        this.env = env;

        add (star = new MenuItem ("Start Simulation"));
        add (stop = new MenuItem ("Stop Simulation"));
        add (fast = new MenuItem ("Speed Up"));
        add (slow = new MenuItem ("Slow Down"));
        add (stat = new MenuItem ("Show Statistics"));
        add (show = new MenuItem ("Toggle Trace"));
        
        star.addActionListener (this);
        stop.addActionListener (this);
        fast.addActionListener (this);
        slow.addActionListener (this);
        show.addActionListener (this);
        stat.addActionListener (this);

    }; // ModelMenu


    /*****************************************************************
     * Handle menu events.
     * @param  evt  menu selection event
     */
    //public synchronized void actionPerformed (ActionEvent evt)
    public void actionPerformed (ActionEvent evt)
    {
        MenuItem selected = (MenuItem) evt.getSource ();

        if      (selected == star)  env.beginSim ();
        else if (selected == stop)  env.endSim ();
		//else if (selected == fast)  Coroutine.changeSpeed (1);
       // else if (selected == slow)  Coroutine.changeSpeed (-1);
        else if (selected == show) 	Trace.toggle ();
		// Add condition for showing statistics window	
    }; // actionPerformed

}; // class

