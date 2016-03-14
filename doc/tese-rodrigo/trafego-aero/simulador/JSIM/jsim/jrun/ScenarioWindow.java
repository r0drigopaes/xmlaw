/******************************************************************
 * @(#) ScenarioWindow.java   1.3
 *
 * Copyright (c) 2000 John Miller
 * All Right Reserved
 *-----------------------------------------------------------------
 * Permission to use, copy, modify and distribute this software and
 * its documentation without fee is hereby granted provided that
 * this copyright notice appears in all copies.
 * WE MAKE NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY 
 * OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. WE SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY ANY USER AS A RESULT OFUSING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *-----------------------------------------------------------------
 *  
 * @version 1.3 (12 December 2000)
 * @author  Xueqin Huang, John Miller
 */

package jsim.jrun;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import jsim.util.*;
import jsim.jmessage.*;


/***************************************************************
 * The ScenarioWindow class provides a GUI for the user to run 
 * simulation and query the JSIM database.
 */
public class ScenarioWindow extends JFrame
			    implements ActionListener,
				       DBConnectionUIListener
{
    /**
     * The Trace facility
     */
    private final Trace	trc = new Trace ("jrun", "ScenarioWindow");

    /**
     * The width of the window
     */
    private final int WIDTH = 950;

    /**
     * The height of the window
     */
    private final int HEIGHT = 700;

    /**
     * The file seperator
     */
    private final String SLASH = System.getProperty ("file.separator");

    /**
     * The image directory
     */
    private final String IMAGE_BASE = System.getProperty ("user.home") + SLASH
                                + "JSIM" + SLASH + "icon" + SLASH;

    /**
     * The start simulation menu item
     */
    private final JMenuItem startItem = new JMenuItem ("Start Simulation");

    /**
     * The query menu item
     */
    private final JMenuItem queryItem = new JMenuItem ("Query Database");

    /**
     * The quit menu item
     */
    private final JMenuItem quitItem = new JMenuItem ("Quit");

    /**
     * The message area
     */
    private final JTextArea msgArea;

    /**
     * The message JScrollPane
     */
    private final JScrollPane msgPane;

    /**
     * The query area
     */
    private final JTextArea queryArea = new JTextArea ();

    /**
     * The execute query button
     */
    private final JButton execute = new JButton ("Excecute Query");

    /**
     * The scenario agent that launches this GUI
     */
    private final ScenarioAgent scenarioAgent;

    /**
     * The DBConnectionUI object owned by this class
     */
    private DBConnectionUI connUI;

    /**
     * The DBConnectionInfo object
     */
    private DBConnectionInfo connInfo = null;

    /**
     * The start simulation request flag
     */
    private boolean startRequested = false;


    /***************************************************************
     * Constructs a ScenarioWindow instance.
     * @param scenarioAgent	The scenario agent that launches this GUI
     * @param msgArea		The message area that will be updated by
     *				both the scenario agent and this class
     */
    public ScenarioWindow (ScenarioAgent scenarioAgent,
			   JTextArea msgArea)
    {
	super ("Scenario Control Window");

	this.scenarioAgent = scenarioAgent;
	this.msgArea = msgArea;
	msgArea.setEditable (false);
	msgPane = new JScrollPane (msgArea);
	msgPane.setBorder (BorderFactory.createTitledBorder ("Status Watch"));

	JMenuBar menuBar = new JMenuBar ();
	setJMenuBar (menuBar);
	JMenu menu = new JMenu ("Action");
	menuBar.add (menu);
	startItem.addActionListener (this);  menu.add (startItem);
	queryItem.addActionListener (this);  menu.add (queryItem);
	quitItem.addActionListener (this);   menu.add (quitItem);
	
	JLabel cover = new JLabel (new ImageIcon (IMAGE_BASE + "cover.gif"));
	JScrollPane coverPane = new JScrollPane (cover);
	setContentPane (coverPane);

	addWindowListener (new WindowAdapter () {
		public void windowClosing (WindowEvent e) {
			System.exit (0);
		}
	});

	setSize (WIDTH, HEIGHT);	
	setVisible (true);

    }; // ScenarioWindow constructor


    /***************************************************************
     * Handles an ActionEvent.
     * @param evt	The ActionEvent
     */
    public void actionPerformed (ActionEvent evt)
    {
	if (evt.getSource () == quitItem) {
		dispose ();
	} else if (evt.getSource () == startItem) {

		// set startRequested flag
		startRequested = true;

		// construct start simulation UI
		msgArea.setText ("Start simulation is requested...\n");
		setContentPane (msgPane);
		repaint ();
		setSize (WIDTH, HEIGHT);
		setVisible (true);

		// pop up DB connection UI
		connUI = new DBConnectionUI (this);
		connUI.addDBConnectionUIListener (this);
		connUI.pack ();
		connUI.setVisible (true);
	} else if (evt.getSource () == queryItem) {

		// construct query UI

		String imageFile = IMAGE_BASE + "queryguide.GIF";
		trc.show ("actionPerformed", "loading image: " + imageFile);
		JLabel guide = new JLabel (new ImageIcon (imageFile));
		JScrollPane guidePane = new JScrollPane (guide);
		guidePane.setBorder (BorderFactory.createTitledBorder ("Query Guide"));
		guidePane.setSize (600, 350);

		msgArea.setText ("Query JsimDB is requested...\n");
		msgPane.setSize (200, 350);
		JPanel content = new JPanel (new BorderLayout ());
		content.add (guidePane, BorderLayout.WEST);
		content.add (msgPane, BorderLayout.EAST);
		content.setBorder (BorderFactory.createEmptyBorder (20, 20, 20, 20));

		JScrollPane queryPane = new JScrollPane (queryArea);
		queryPane.setBorder (BorderFactory.createTitledBorder ("SQL Statement"));
		queryPane.setSize (800, 350);
		execute.addActionListener (this);
		execute.setBorder (BorderFactory.createEmptyBorder (0, 50, 50, 50));

		JPanel contentPane = new JPanel (new BorderLayout ());
		contentPane.add (content, BorderLayout.NORTH);
		contentPane.add (queryPane, BorderLayout.CENTER);
		contentPane.add (execute, BorderLayout.SOUTH);

		setContentPane (contentPane);
		repaint ();
		setSize (WIDTH, HEIGHT);
		setVisible (true);

		// pop up DB connection UI
		connUI = new DBConnectionUI (this);
		connUI.addDBConnectionUIListener (this);
		connUI.pack ();
		connUI.setVisible (true);
	} else if (evt.getSource () == execute) {		
		String queryStr = queryArea.getText ();
		msgArea.append ("Executing query:\n" + queryStr + "\n");
		queryItemClicked (queryStr);
	}; // if

    }; // actionPerformed


    /***************************************************************
     * Sends a message to the scenario agent after the user has clicked 
     * the start item on the menu list.
     */
    void startItemClicked ()
    {
        if (connInfo == null) {
            trc.show ("startItemClicked", "using default value for "
			+ "scenarioID: no database connection");
            msgArea.append ("Not connected to database. Using default value..");
            scenarioAgent.fireInstructEvent (null);
            return;
        } // if
              
        String queryStr = "SELECT seq FROM sequence";
        Query query = new Query ("Sequence query", queryStr);
        scenarioAgent.fireQueryEvent (query);
                
    }; // startItemClicked
     

    /***************************************************************
     * Sends a message to the scenario agent after the user has clicked 
     * the query item on the menu list.
     * @param queryStr		The query user has entered
     */
    void queryItemClicked (String queryStr)
    {
        if (connInfo == null) {
            trc.show ("queryItemClicked", "cannot connect to database: "
                                + "connInfo = " + connInfo);
            msgArea.append ("Can not create connection to the database.");
            return;
        } // if

	Query query = null;

	String cmd = queryStr.toLowerCase ();
	if (cmd.startsWith ("select")) {
            query = new Query ("Normal query", queryStr);
	} else {
	    query = new Query ("Update", queryStr);
	}; // if

        scenarioAgent.fireQueryEvent (query);
                     
    }; // queryItemClicked


    /***************************************************************
     * Handles a DBConnectionUIEvent.
     * @param evt	The DBConnectionUIEvent
     */
    public void notify (DBConnectionUIEvent evt)
    {
	connInfo = evt.getDBConnectionInfo ();
	scenarioAgent.setDBConnectionInfo (connInfo);

	trc.show ("notify", "startRequested: " + startRequested);
	if (startRequested) {
	    startRequested = false;
	    startItemClicked ();
	}; // if

	connUI.dispose ();

    }; // notify


    /***************************************************************
     * Main method for testing purposes only.
     */
    public static void main (String [] args)
    {
	new ScenarioWindow (null, new JTextArea (5, 30));

    }; // main

}; // class ScenarioWindow
