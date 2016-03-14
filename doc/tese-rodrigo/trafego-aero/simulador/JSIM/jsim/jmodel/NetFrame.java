/******************************************************************
 * @(#) NetFrame.java     1.3     98/4/16
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

package jsim.jmodel;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.logging.*;

import jsim.util.*;


/******************************************************************
 * Overall frame for the JMODEL designer.  May be launched from an
 * applet or an application.  Assumes JSIM is installed in home
 * directory (change IMAGE_PATH if this is not the case).
 */

public class NetFrame extends JFrame
                      implements WindowListener,
                                 AdjustmentListener,
                                 ActionListener
{
    //////////////////////// Constants \\\\\\\\\\\\\\\\\\\\\\\\\\\\
    private final static String  F_TITLE    = "JMODEL Graphical Designer";
    private final static int     F_TOP_X    =  20; 
    private final static int     F_TOP_Y    =  40; 
    private final static int     F_WIDTH    = 980; 
    private final static int     F_HEIGHT   = 600; 
    private final static String  EXT        = ".gif";
    public final static String  SLASH      = System.getProperty ("file.separator");

    // **** Before Windows XP Change
    
    private final static String  IMAGE_BASE = new String (
                                               System.getProperty ("user.home") + SLASH +
                                               "JSIM" + SLASH );

    private final static String  IMAGE_PATH = new String (
                                               "jsim" + SLASH +
                                               "jmodel" + SLASH +
                                               "images" + SLASH );
    

    // **** After Windows XP Change
    /*
    private final static String  IMAGE_BASE = new String (
                                               //"c:" + SLASH +
                                               "JSIM" + SLASH );

    private final static String  IMAGE_PATH = new String (
                                               "jsim" + SLASH +
                                               "jmodel" + SLASH +
                                               "images" + SLASH );
    */
    // **** End of Windows XP change
    	 		 
    private final static String []  ACTION = { "Move",
                                               "Delete",
                                               "Update",
                                               "Generate" };

    /**
     * Action Types
     *
     * NOTE: jsim.util.Node defines several other Action Types
     *       which are used by NetFrame.
     */

            final static int  MOVE      = Node.TRANSPORT + 3;
            final static int  DELETE    = Node.TRANSPORT + 4;
            final static int  UPDATE    = Node.TRANSPORT + 5;
            final static int  GENERATE  = Node.TRANSPORT + 6;
 
    /////////////////// Immutable Variables \\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Drawing canvas for creating model.
     */
    private final NetCanvas   designCanvas;

    /**
     * Tracing messages.
     */
	private static Logger trc;
    // private final Trace       trc;

    //////////////////////// Variables \\\\\\\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Control buttons
     */
    private static JButton []  control;
    
    /**
     * Users current directory
     */
    public static String currDir;

	/**
	 * Unique identifier of serialized object
	 */
	private static final long serialVersionUID = 7112445388695435778L;

    /**************************************************************
     * Main method.
     * @param  args  command-line arguments
     */  
    public static void main (String [] args)
    {
	// **** Before Windows XP Change
	      
        String codeBase = new String(System.getProperty ("user.home") +
                                     System.getProperty ("file.separator") +
                                     "JSIM" +
                                     System.getProperty ("file.separator") );
				   
	currDir = args[0];			     
	
	
	// **** After Windows XP Change
        /*        
        String codeBase = new String(//"C:" +
                                     System.getProperty ("file.separator") +
                                     "JSIM" +
                                     System.getProperty ("file.separator") );
        */
	// **** End of Windows XP Change

         NetFrame designFrame = new NetFrame (codeBase);

    }; // main


    /*****************************************************************
     * Construct a JMODEL frame.
     */
    public NetFrame (String imageBase)
    {
        super (F_TITLE);
		 trc = Logger.getLogger(NetFrame.class.getName() ); 
		 Trace.setLogConfig ( trc );
        // trc = new Trace ("NetFrame", "JMODEL");

        /*************************************************************
         * Construct components of JMODEL frame.
         */
        designCanvas  = new NetCanvas (this);

        FrameMenuBar designMenuBar = new FrameMenuBar (designCanvas, this);
        JToolBar     designToolBar = new JToolBar ();

        /*************************************************************
         * Initialize the tool bar.
         */
        initToolBar (designToolBar, imageBase);
        // trc.show("NetFrame", "<test1>");

        /*************************************************************
         * Initialize properties of designFrame.
         */
        setJMenuBar   (designMenuBar);
        setForeground (Node.FORE_COLOR);
        setBackground (Node.BACK_COLOR);
        setSize       (F_WIDTH, F_HEIGHT);
        setLocation   (F_TOP_X, F_TOP_Y);
        // trc.show("NetFrame", "<test2>");

        /*************************************************************
         * Layout components.
         */
        Container cp = getContentPane ();
        cp.add ("North",  designToolBar);
        cp.add ("Center", designCanvas);
        // trc.show("NetFrame", "<test3>");

        addWindowListener (this);
        setVisible( true );
        setEnabled( true );
        // trc.show("NetFrame", "<test4>");

    }; // NetFrame


    /*****************************************************************
     * Put the control buttons into the tool bar after the tool bar
     * has been instantiated.
     * @param  designToolBar  desiger tool bar
     */
    private void initToolBar (JToolBar designToolBar, String imageBase)
    {
        int i, j, k;
        int n1 = Node.TYPE_NAME.length;
        int n2 = ACTION.length;

        String [] controlName = new String  [n1 + n2];

        control = new JButton [controlName.length];

        for (i = 0;  i < n1; i++)  controlName [i] = Node.TYPE_NAME [i];
        for (j = n1, k = 0; j < n1 + n2; j++, k++)  controlName [j] = ACTION [k];

        for (i = 0; i < controlName.length; i++) {

           if (imageBase == null) {
               imageBase = IMAGE_BASE;
           }; // if
 
           String cIconFile = new String (imageBase + IMAGE_PATH + controlName [i] + EXT); 

		   trc.info ( "Icon File Name = " + cIconFile );
           // trc.show ("initToolBar", "Icon File Name = " + cIconFile);

           // Create an icon for the button
           ImageIcon cIcon = new ImageIcon (cIconFile);
           control [i]     = new JButton   (null, cIcon);

           // Add an event listener
           control [i].addActionListener (this);

           // Add the button to the tool bar
           designToolBar.add (control [i]);
 
       }; // for

    }; // initToolBar


    /****************************************************************
     * Handle button pressed event.
     * @param  evt  button action event
     */
    public void actionPerformed (ActionEvent evt)
    {
        // Determine which button generated the event 
        Object pressed = evt.getSource ();
 
        for (int i = 0; i < control.length; i++) {
            if (pressed == control [i]) {
            // Set the action type of the canvas
                designCanvas.setActionType (i);
                return;
            }; // if
        }; // for
 
		trc.warning ( "could not recognize button" );
        // trc.tell ("actionPerformed", "could not recognize button");
 
    }; // actionPerformed


    /*****************************************************************
     * Handle event by adjusting the view of the design canvas based
     * on position of scrollbars.
     * @param  evt  scrollbar adjustment event
     */
    public void adjustmentValueChanged (AdjustmentEvent evt)
    { 
        designCanvas.repaint ();

    }; // adjustmentValueChanged

    /*****************************************************************
     * quit the Designer.
     */
    public void quit ()
    {
        //dispose();
        System.exit (0);
 
    }; // quit

    /*****************************************************************
     * Handle window closing event by exiting.
     * @param  evt  window closing event
     */
    public void windowClosing (WindowEvent evt)
    { 
        System.exit (0);

    }; // windowClosing


    /*****************************************************************
     * The rest of the WindowEvent handlers are not implemented.
     */
    public void windowClosed (WindowEvent evt) {};

    public void windowDeiconified (WindowEvent evt) {};

    public void windowIconified (WindowEvent evt) {};

    public void windowActivated (WindowEvent evt) {};

    public void windowDeactivated (WindowEvent evt) {};

    public void windowOpened (WindowEvent evt) {};

}; // class

