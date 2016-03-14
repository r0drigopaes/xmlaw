/******************************************************************
 * @(#) HelpMenu.java     1.3     98/4/16
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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


/******************************************************************
 * The help menu for the JMODEL designer.
 */

public class HelpMenu extends JMenu
                      implements ActionListener
{
    //////////////////////// Constants \\\\\\\\\\\\\\\\\\\\\\\\\\\\
    /**
     * General information on buttons
     */
    private static final String INFOButtons  = new String (

"A JMODEL design is drawn onto a canvas by clicking on a button.\n"    +
"The buttons are arranged on the left side of the designer frame.\n"   +
"For a description of what each button does select the appropriate.\n" +
"menu option.\n");

    /**
     * Information on Server button
     */
    private static final String INFOServer = new String (

"A Server is a node representing a server consisting or one or\n"     +
"more service units (tokens).  Click to create a new Server node.\n");

    /**
     * Information on Server button
     */
    private static final String INFOFacility = new String (

"A Facility is a node representing a service facility consisting\n"   +
"a server feed by a queue.  By clicking in the canvas a new Facility\n" +
"node will be created at that location.\n");

    /**
     * Information on Server button
     */
    private static final String INFOSource = new String (

"A Source is a node representing a producer of entities (e.g., bank\n" +
"customers).  Click to create a new Source node.\n");

    /**
     * Information on Server button
     */
    private static final String INFOSink = new String (

"A Sink is a node representing a consumer of entities (e.g., bank\n" +
"customers).  It also collects statistics on entities.  Click to\n"  +
"create a new Sink node.\n");

    /**
     * Information on Server button
     */
    private static final String INFOTransport = new String (

"A Transport is an edge over which entities travel from node to node.\n" +
"Position the mouse over the start node, press the mouse, move to the\n" +
"end node and release the mouse.\n");

    /**
     * Information on Server button
     */
    private static final String INFOMove = new String (

"The Move button allows nodes to be moved.  All incident edges and\n"  +
"held tokens will be moved as well.  Position the mouse over node\n"   +
"to be moved, press the mouse, move to desired position and release\n" +
"the mouse.\n");

    /**
     * Information on Server button
     */
    private static final String INFODelete = new String (

"The Delete button deletes a node.  All incident edges and held\n" +
"tokens will be deleted as well.  Click on the node to delete it.\n");

    /**
     * Information on Server button
     */
    private static final String INFOUpdate = new String (

"The Update button allows the paramater/properties of a node to be\n" +
"viewed or updated.  Click on the node to view/update it.\n");

    /**
     * Information on Server button
     */
    private static final String INFOGenerate = new String (

"The Generate button causes code to be generated for the model.\n" +
"Click anywhere on the canvas to produce code.\n");

	/**
	 * Unique identifier of serialized object
	 */
	private static final long serialVersionUID = 4079686079685958416L;

    /////////////////// Immutable Variables \\\\\\\\\\\\\\\\\\\\\\\
    private final JMenuItem explainButtons;
    private final JMenuItem explainServer;
    private final JMenuItem explainFacility;
    private final JMenuItem explainSource;
    private final JMenuItem explainSink;
    private final JMenuItem explainTransport;
    private final JMenuItem explainMove;
    private final JMenuItem explainDelete;
    private final JMenuItem explainUpdate;
    private final JMenuItem explainGenerate;


    /******************************************************************
     * Construct a help menu.
     */
    public HelpMenu (String title)
    {
        super (title);

        explainButtons   = new JMenuItem ("Buttons");

        explainServer    = new JMenuItem ("Server");
        explainFacility  = new JMenuItem ("Facility");
        explainSource    = new JMenuItem ("Source");
        explainSink      = new JMenuItem ("Sink");
        explainTransport = new JMenuItem ("Transport");

        explainMove      = new JMenuItem ("Move");
        explainDelete    = new JMenuItem ("Delete");
        explainUpdate    = new JMenuItem ("Update");
        explainGenerate  = new JMenuItem ("Generate");
        

        add (explainButtons);
        add (explainServer);
        add (explainFacility);
        add (explainSource);
        add (explainSink);
        add (explainTransport);
        add (explainMove);
        add (explainDelete);
        add (explainUpdate);
        add (explainGenerate);

        explainButtons.addActionListener (this);
        explainServer.addActionListener (this);
        explainFacility.addActionListener (this);
        explainSource.addActionListener (this);
        explainSink.addActionListener (this);
        explainTransport.addActionListener (this);
        explainMove.addActionListener (this);
        explainDelete.addActionListener (this);
        explainUpdate.addActionListener (this);
        explainGenerate.addActionListener (this);

    }; // helpMenu
    

    /******************************************************************
     * Handle the selection event.
     * @param  evt  button action event
     */
    public void actionPerformed (ActionEvent evt)
    {
        if (evt.getSource () == explainButtons) {
            HelpFrame helpInfo = new HelpFrame ("Buttons",   INFOButtons);

        } else if (evt.getSource () == explainServer) {
            HelpFrame helpInfo = new HelpFrame ("Server",    INFOServer);

        } else if (evt.getSource () == explainFacility) {
            HelpFrame helpInfo = new HelpFrame ("Facility",  INFOFacility);

        } else if (evt.getSource () == explainSource) {
            HelpFrame helpInfo = new HelpFrame ("Source",    INFOSource);

        } else if (evt.getSource () == explainSink) {
            HelpFrame helpInfo = new HelpFrame ("Sink",      INFOSink);

        } else if (evt.getSource () == explainTransport) {
            HelpFrame helpInfo = new HelpFrame ("Transport", INFOTransport);

        } else if (evt.getSource () == explainMove) {
            HelpFrame helpInfo = new HelpFrame ("Move",      INFOMove);

        } else if (evt.getSource () == explainDelete) {
            HelpFrame helpInfo = new HelpFrame ("Delete",    INFODelete);

        } else if (evt.getSource () == explainUpdate) {
            HelpFrame helpInfo = new HelpFrame ("Update",    INFOUpdate);

        } else if (evt.getSource () == explainGenerate) {
            HelpFrame helpInfo = new HelpFrame ("Generate",  INFOGenerate);

        }; // if

    }; // actionPerformed


}; // class

