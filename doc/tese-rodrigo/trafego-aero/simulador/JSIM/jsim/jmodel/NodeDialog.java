/******************************************************************
 * @(#) NodeDialog.java     1.3     98/4/16
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

public class NodeDialog extends Dialog
                        implements ActionListener
{
    //////////////////////// Constants \\\\\\\\\\\\\\\\\\\\\\\\\\\\
    private static final String  TITLE     = "Set Parameters";
    private static final int     NUM_LINES = 13;

    /////////////////// Immutable Variables \\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Node name value
     */
    private final TextField  nodeNameV;

    /**
     * Type of queue value
     */
    private final TextField  queueTypeV;

    /**
     * Number of tokens value
     */
    private final TextField  numTokensV;

    /**
     * Random variate distribution value
     */
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
	 * Random variate distribution value for cost
	 */
	private final Choice  costDistChoiceV;

	/**
	 * Alpha parameter value for cost
	 */
	private final TextField  costAlphaV;

	/**
	 * Beta parameter value for cost
	 */
	private final TextField  costBetaV;

	/**
	 * Random number stream value for cost
	 */
	private final TextField  costStreamV;

	/**
     * Outgoing probability values
     */
    // private final TextField  outProbV;

	/**
	 * Cost of using the resource
	 */
	// private final TextField  costV;
    
    /**
     * Apply button
     */
    private final Button     applyButton;

    /**
     * Cancel button
     */
    private final Button     cancelButton;

    /**
     * Node whose parameters are being updated
     */
    private final Node       onNode;

	/**
	 * Unique identifier of serialized object
	 */
	private static final long serialVersionUID = 5175968472652209384L;

    /**********************************************************************
     * Construct a dialog box to adjust the parameter for a node.
     * @param  inFrame  containing frame
     * @param  onNode   node to be updated
     */
    public NodeDialog (Frame inFrame, Node onNode)
    {
        super (inFrame, TITLE, true);

        this.onNode = onNode;

        setLayout (new GridLayout (NUM_LINES, 2));

        nodeNameV     = new TextField (onNode.nodeName);
        queueTypeV    = new TextField (onNode.queueType);
        numTokensV    = new TextField (onNode.numTokens);
        distChoiceV = new Choice ();
		for (int i = 0; i < Parameters.DISTRIBUTION.length; i++)
			distChoiceV.add (Parameters.DISTRIBUTION [i]);
		distChoiceV.select (onNode.distribution);
        alphaV        = new TextField (onNode.alpha);
        betaV         = new TextField (onNode.beta);
        streamV       = new TextField (onNode.stream);
		costDistChoiceV = new Choice ();
		for (int i = 0; i < Parameters.DISTRIBUTION.length; i++)
			costDistChoiceV.add (Parameters.DISTRIBUTION [i]);
		costDistChoiceV.select (onNode.costDistribution);
		costAlphaV    = new TextField (onNode.costAlpha);
		costBetaV     = new TextField (onNode.costBeta);
		costStreamV   = new TextField (onNode.costStream);
		/*
        outProbV      = new TextField
                        (packProb (onNode.outCondition, onNode.numOutEdges));
	    */
		// costV		  = new TextField (onNode.cost);

        applyButton   = new Button ("Apply");
        cancelButton  = new Button ("Cancel");

        add (new Label ("nodeName"));     add (nodeNameV);
        add (new Label ("queueType"));    add (queueTypeV);
        add (new Label ("numTokens"));    add (numTokensV);
        add (new Label ("time distribution")); add (distChoiceV);
        add (new Label ("time alpha"));        add (alphaV);
        add (new Label ("time beta"));         add (betaV);
        add (new Label ("time stream"));       add (streamV);
		add (new Label ("cost distribution")); add (costDistChoiceV);
		add (new Label ("cost alpha"));        add (costAlphaV);
		add (new Label ("cost beta"));         add (costBetaV);
		add (new Label ("cost stream"));       add (costStreamV);
        // add (new Label ("outCondition")); add (outProbV);
		// add (new Label ("cost"));		  add (costV);

        add (applyButton);  add (cancelButton);

        applyButton.addActionListener (this);
        cancelButton.addActionListener (this);

        pack ();

    }; // NodeDialog


    /**********************************************************************
     * Pack the vector of probabilities into one string.
     * @param   probVector  probability vector
     * @param   size        number of active elements
     * @return  String      packed string
     */
    public String packProb (String [] probVector, int size)
    {
        String s = new String ();

        for (int i = 0; i < size; i++) {
            s += probVector [i];
            s += "; ";
        }; // for 

        return s;
 
    }; // packProb


    /**********************************************************************
     * Unpack the vector of probabilities from one string.
     * @param   prob        resultant probability vector
     * @param   probString  packed string
     * @param   size        number of active elements
     */
    public void unpackProb (String [] prob, String probString, int size)
    {
        int       start = 0;
        int       end   = 0;
        int       sum   = 0;

        for (int i = 0; i < size; i++) {
            probString  = probString.substring (start);
            end         = probString.indexOf (';');
            prob [i]    = probString.substring (0, end);
            start       = end + 2;
            System.out.println ("prob [" + i + "] = " + prob [i]); 
        }; // for 

    }; // unpackProb


    /**********************************************************************
     * Handle button events.
     *     Apply:  update the nodes parameters and then dismiss dialog box.
     *     Cancel: dismiss dialog box.
     * @param  evt  button action event
     */
    public void actionPerformed (ActionEvent evt)
    {
        if (evt.getSource () == applyButton) {

            onNode.nodeName     = nodeNameV.getText ();
            onNode.queueType    = queueTypeV.getText ();
            onNode.numTokens    = numTokensV.getText ();
            onNode.distribution = distChoiceV.getSelectedItem ();
            onNode.alpha        = alphaV.getText ();
            onNode.beta         = betaV.getText ();
            onNode.stream       = streamV.getText ();
			onNode.costDistribution = costDistChoiceV.getSelectedItem ();
			onNode.costAlpha        = costAlphaV.getText ();
			onNode.costBeta         = costBetaV.getText ();
			onNode.costStream       = costStreamV.getText ();
			// onNode.cost			= costV.getText ();
			/*
            unpackProb (onNode.outCondition, outProbV.getText (),
                        onNode.numOutEdges);
			*/

        }; // if

        dispose ();

    }; // actionPerformed


}; // class

