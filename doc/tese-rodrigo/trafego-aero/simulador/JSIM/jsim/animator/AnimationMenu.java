/******************************************************************
 * @(#) AnimatorMenu.java     1.3
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

package jsim.animator;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import jsim.process.*;
import jsim.util.*;


/******************************************************************
 * The ModelMenu class defines the main menu for simulation models.
 */

public class AnimationMenu extends    Menu
                       implements ActionListener
{
    /////////////////// Immutable Variables \\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Start simulation control.
     */
    private final MenuItem  star;

    /**
     * Stop animation control.
     */
    private final MenuItem  stop;
    
    /**
     * Start animation control.
     */
    private final MenuItem  starA;

    /**
     * Fast simulation control.
     */
    private final MenuItem  fast;

    /**
     * Slow simulation control.
     */
    private final MenuItem  slow;

    /**
     * Show statistics control.
     */
    private final MenuItem  stat;

    /**
     * Show simulation control.
     */
    private final MenuItem  show;

    /**
     * Containing AnimationImp.
     */
    private final AnimationImp     env;
    
     /**
     * Containing Model.
     */
    private final Model     mod;

	/**
	 * Unique identifier of the serialized object
	 */
	private static final long serialVersionUID = 4003928821349032823L;

    /*****************************************************************
     * Construct a control menu for the Animator
     * @param  env  containing AnimationImp
     * @param  mod  containing model
     */
    public AnimationMenu (AnimationImp env, Model mod)
    {
        super ("JSIM Controls");

        this.env = env;
	this.mod = mod;

        add (star = new MenuItem ("Start Simulation"));
        add (starA = new MenuItem ("Start Animation"));
        add (stop = new MenuItem ("Stop Animation"));
        add (fast = new MenuItem ("Speed Up"));
        add (slow = new MenuItem ("Slow Down"));
        add (stat = new MenuItem ("Show Statistics"));
        add (show = new MenuItem ("Toggle Trace"));

        star.addActionListener (this);
        starA.addActionListener (this);
        stop.addActionListener (this);
        fast.addActionListener (this);
        slow.addActionListener (this);
        stat.addActionListener (this);
        show.addActionListener (this);

    }; // ModelMenu


    /*****************************************************************
     * Handle menu events.
     * @param  evt  menu selection event
     */
    //public synchronized void actionPerformed (ActionEvent evt)
    public void actionPerformed (ActionEvent evt)
    {
        MenuItem selected = (MenuItem) evt.getSource ();

        if      (selected == star)  mod.beginSim ();
        else if (selected == starA)  env.beginSim ();
        else if (selected == stop)  env.endSim ();
        else if (selected == fast)  env.changeSpeed (-1);
        else if (selected == slow)  env.changeSpeed (1);
        else if (selected == stat)  mod.showStats ();
        else if (selected == show)  Trace.toggle ();

    }; // actionPerformed


}; // class

