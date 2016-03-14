/******************************************************************
 * @(#) TransportDialog.java     1.3	7/19/04
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
import jsim.util.*;


/******************************************************************
 * Dialog box for updating a node's parameters.
 */

public class TransportDialog extends Dialog
	implements ActionListener
{
	//////////////////////// Constants \\\\\\\\\\\\\\\\\\\\\\\\\\\\
	private static final String  TITLE     = "Set Parameters";
	private static final int     NUM_LINES = 8;
	/*
	private static final String [] OUT_COND_TYPE = {"probability",
												    "class Id",
												    "cost"
												    };
												
	private static final int PROBABILITY_TYPE = 0;
	private static final int CLASS_ID_TYPE = 1;
	private static final int COST_TYPE = 2;
	*/
	/////////////////// Immutable Variables \\\\\\\\\\\\\\\\\\\\\\\
	private final Choice  distChoiceV;

	/**
	 * Alpha parameter value
	 */
	private final TextField  alphaV;

	/**
	 * Beta parameter value
	 */
	private final TextField  betaV;

	/**
	 * Random number stream value
	 */
	private final TextField  streamV;

	/**
	 * Random number stream value
	 */
	private final TextField  outConditionV;

	/**
	 * Type of the output condition
	 */
	//private final TextField  outCondTypeV;
    
	/**
	 * Apply button
	 */
	private final Button     applyButton;

	/**
	 * Cancel button
	 */
	private final Button     cancelButton;

	/**
	 * OutCondType button
	 */
	private final Choice	outCondChoiceV;

	/**
	 * QCurve whose parameters are being updated
	 */
	private final QCurve       onQCurve;

	/**
	 * Unique identifier of serialized object
	 */
	private static final long serialVersionUID = 5175968472652209384L;

	/**********************************************************************
	 * Construct a dialog box to adjust the parameter for a node.
	 * @param  inFrame  containing frame
	 * @param  onQCurve   node to be updated
	 */
	public TransportDialog (Frame inFrame, QCurve onQCurve)
	{
		super (inFrame, TITLE, true);

		this.onQCurve = onQCurve;

		setLayout (new GridLayout (NUM_LINES, 2));

		distChoiceV	  = new Choice ();
		for (int i = 0; i < Parameters.DISTRIBUTION.length; i++)
			distChoiceV.add (Parameters.DISTRIBUTION [i]);
		distChoiceV.select (onQCurve.distribution);
		alphaV        = new TextField (onQCurve.alpha);
		betaV         = new TextField (onQCurve.beta);
		streamV       = new TextField (onQCurve.stream);
		outConditionV = new TextField (onQCurve.getOutCondition ());
		outCondChoiceV = new Choice ();
		for (int i = 0; i < Parameters.OUT_COND_TYPE.length; i++)
			outCondChoiceV.add (Parameters.OUT_COND_TYPE [i]);
		outCondChoiceV.select (onQCurve.getOutCondType ());
		
		applyButton   = new Button ("Apply");
		cancelButton  = new Button ("Cancel");
		
		add (new Label ("time distribution")); add (distChoiceV);
		add (new Label ("time alpha"));        add (alphaV);
		add (new Label ("time beta"));         add (betaV);
		add (new Label ("time stream"));       add (streamV);
		add (new Label ("out cond. (priority " + (onQCurve.getEdgeIndex ()+ 1) + ")"));       add (outConditionV);
		add (new Label ("out cond. type"));	   add (outCondChoiceV);

		add (applyButton);  add (cancelButton);

		applyButton.addActionListener (this);
		cancelButton.addActionListener (this);

		pack ();

	}; // NodeDialog

	/**********************************************************************
	 * Handle button events.
	 * Apply:  update the nodes parameters and then dismiss dialog box.
	 * Cancel: dismiss dialog box.
	 * @param  evt  button action event
	 */
	public void actionPerformed (ActionEvent evt)
	{
		if (evt.getSource () == applyButton) 
		{

			onQCurve.distribution = distChoiceV.getSelectedItem ();
			onQCurve.alpha        = alphaV.getText ();
			onQCurve.beta         = betaV.getText ();
			onQCurve.stream       = streamV.getText ();
			onQCurve.setOutCondition (outConditionV.getText ());
			onQCurve.setOutCondType (outCondChoiceV.getSelectedIndex ());

		}; // if

		dispose ();

	}; // actionPerformed


}; // class

