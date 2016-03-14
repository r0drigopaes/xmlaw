/******************************************************************
 * @(#) DesignAgent.java	1.3	98/4/16
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

package jsim.jmodel;

import java.applet.*;
import java.awt.*;
import java.io.*;
import javax.swing.*;


/******************************************************************
 * An applet class that initializes and activates the JMODEL
 * designer.
 */

public class DesignAgent extends JApplet
                    implements Serializable
{
    /////////////////// Immutable Variables \\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Main JMODEL design frame that contains a menubar, a toolbox,
     * a drawing canvas and two scollbars.
     */
    private static NetFrame  designFrame;


	/**
	 * Unique identifier of serialized object
	 */
	private static final long serialVersionUID = 4958473264557689606L;

    /**************************************************************
     * Initialize the applet.
     */
    public void init ()
    {
        designFrame = new NetFrame (getCodeBase().getFile ());  
        designFrame.setVisible (true);
        designFrame.setEnabled (true);

    }; // init


    /**************************************************************
     * Called to stop the applet.  It is called when the applet's
     * document is no longer on the screen. 
     */
    public void stop ()
    {
        designFrame.setEnabled (false);
	designFrame.setVisible (false);

    }; // stop 


    /**************************************************************
     * Main method.
     * @param  args  command-line arguments
     */
    public static void main (String [] args)
    {
        String codeBase = new String(System.getProperty ("user.home") +
                                     System.getProperty ("file.separator") +
                                     "JSIM" +
                                     System.getProperty ("file.separator") );
        designFrame = new NetFrame (codeBase);  

    }; // main
   

}; // class

