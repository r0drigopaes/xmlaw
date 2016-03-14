/******************************************************************
 * @(#) HandleReportDialog.java   1.3
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

import java.util.Vector;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import jsim.util.Trace;
import jsim.jmessage.*;

/***************************************************************
 * The HandleReportDialog class provides a GUI for the user to choose
 * whether to display and/or store the simulation result.
 */
public class HandleReportDialog extends JFrame 
				implements ActionListener,
					   DBConnectionUIListener
{
    /**
     * The Trace facility
     */
    private final Trace    trc = new Trace ("jrun", "HandleReportDialog");

    /**
     * The display JCheckBox
     */
    private JCheckBox      display = new JCheckBox ("Display");

    /**
     * The store JCheckBox
     */
    private JCheckBox      store = new JCheckBox ("Store");

    /**
     * The OK button
     */
    private JButton        okButton = new JButton ("OK");

    /**
     * The cancel button
     */
    private JButton	   cancelButton = new JButton ("Cancel");

    /**
     * The model agent that uses this GUI
     */
    private ModelAgent     modelAgent;


    /***************************************************************
     * Constructs a HandelReportDialog instance.
     * @param modelAgent	The model agent who creates this GUI
     */
    public HandleReportDialog (ModelAgent modelAgent) 
    {
	super ("Handle Report Dialog");

	this.modelAgent = modelAgent;

	// panel for holding the question label

	JPanel panel0 = new JPanel ();
	panel0.setLayout (new BorderLayout ());
	panel0.add (new JLabel ("How do you wish to handle the simulation"
				+ " result?"), BorderLayout.WEST);
	panel0.setBorder (BorderFactory.createEmptyBorder (20, 20, 20, 20));	

	// panel for holding the checkboxes

	JPanel panel1 = new JPanel ();
	panel1.setLayout (new GridLayout (2, 1));
	panel1.add (display);  panel1.add (store);

	// panel for the content pane

	okButton.addActionListener (this);
	JPanel panel2 = new JPanel (new GridLayout (1, 2));
	panel2.add (okButton);
	panel2.add (cancelButton);
	panel2.setBorder (BorderFactory.createEmptyBorder (20, 20, 20, 20));

	JPanel panel3 = new JPanel ();
	panel3.setLayout (new BorderLayout ());
	panel3.add (panel0, BorderLayout.NORTH);
	panel3.add (panel1, BorderLayout.CENTER);
	panel3.add (panel2, BorderLayout.SOUTH);
	panel3.setBorder (BorderFactory.createEmptyBorder (20, 20, 20, 20));

	// set the content pane of the HandleReportDialog
	setContentPane (panel3);
	
        addWindowListener (new WindowAdapter () {
            public void WindowClosing (WindowEvent e) {
                System.exit (0);
            }});
	pack ();
	setVisible (true);

    }; // HandleReportDialog constructor


    /***************************************************************
     * Handles an ActionEvent.
     * @param evt	The ActionEvent
     */
    public void actionPerformed (ActionEvent evt)
    {
	if (evt.getSource () == cancelButton) {
		dispose ();
	} else {
		DBConnectionUI connUI = null;
		if (store.isSelected ()) {
			connUI = new DBConnectionUI (this);
			connUI.addDBConnectionUIListener (this);
			connUI.pack ();
			connUI.setVisible (true);
		}; // if

		if (display.isSelected ()) {
			modelAgent.displayReport ();
			if (connUI == null) dispose ();
		}; // if
	}; // if

    }; // actionPerformed


    /***************************************************************
     * Handles a DBConnectionUIEvent.
     * @param evt	The DBConnectionUIEvent
     */
    public void notify (DBConnectionUIEvent evt)
    {
	modelAgent.fireStoreEvent (evt.getDBConnectionInfo ());
	dispose ();

    }; // notify


    /***************************************************************
     * Main method for debugging purpose.
     */
    public static void main (String [] args)
    {
	new HandleReportDialog (new BatchMeansAgent ());

    }; // main

}; // class HandleReportDialog		

