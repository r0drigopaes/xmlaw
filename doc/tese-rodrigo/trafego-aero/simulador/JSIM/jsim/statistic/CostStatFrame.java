/******************************************************************
 * @(#) CostStatFrame.java     1.3     98/02/25
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

package jsim.statistic;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.logging.*;

import jsim.util.*;
import jsim.jmessage.*;

/******************************************************************
 * This class displays statistical outputs in a table.
 */

public class CostStatFrame extends    Frame
                       implements ActionListener
{
    //////////////////////// Constants \\\\\\\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Size of desirable string
     */
    private static final int      S_LENGTH = 13; 

    /**
     * Exit program after stat window is closed
     */
    private static final boolean  ONE_SHOT = false; 

    /////////////////// Immutable Variables \\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Tracing messages
     */
	private static Logger trc;
    // private final Trace  trc;


    /////////////////// Variables \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    /**
     * JSIM system property: use_xml
     */
    private boolean      use_xml = false;
       
    /**
     * JSIM system property: generate_xml_files
     */
    private boolean      generate_xml_files = false;

    /**
	 * Unique identifier of serialized object
	 */
	private static final long serialVersionUID = 4093820384721547549L;

    /**************************************************************
     * Construct a frame to display statistical results.
     * @param winTitle    title of statistics display window
     * @param dataVector  data vector
     */
    public CostStatFrame (String  winTitle,
                      Object  statReport)
    {
        super (winTitle);

		trc = Logger.getLogger(StatFrame.class.getName() );
		Trace.setLogConfig ( trc );
        // trc = new Trace ("StatFrame", winTitle);

        String jsimprop = System.getProperty ("jsim.use_xml", "false");
        if (jsimprop.toLowerCase ().equals ("true")) {
            use_xml = true;
        } // if
     
        jsimprop = System.getProperty ("jsim.generate_xml_files", "false");
        if (jsimprop.toLowerCase ().equals ("true")) {
            generate_xml_files = true;   
        } // if
    
        Panel panel1 = new Panel ();
        showStatResult (panel1, statReport);

        Panel  panel2      = new Panel ();
        Button closeButton = new Button ("Close");
        closeButton.addActionListener (this);
        panel2.add (closeButton);

        add ("Center", panel1);
        add ("South",  panel2);
        pack ();

    }; // StatFrame


    /**************************************************************
     * Show the statistical results.
     */
    public void showWin ()
    {
		trc.info ( "display statistical results" );
        // trc.show ("showWin", "display statistical results");
        // show ();  the show method was deprecated in jdk 1.5
		setVisible(true);
    }; // showWin


    /**************************************************************
     * Truncate str to S_LENGTH chars, if it is longer.
     * @param  str  the string need to be truncated
     */
    public String trunc (String str)
    {
        return (str.length () > S_LENGTH) ?
                str.substring (0, S_LENGTH) : str; 

    }; // trunc 


    /**************************************************************
     * Display a row of statistical information.
     * @param  sPanel     panel to display stat data
     * @param  data       row of stat data
     * @param  numCols    number of columns
     * @param  statName   Name of the individual stats
     */
    private void displayRow (Panel sPanel, Double [] data, 
				int numCols, String statName)
    {
        Button label;

        for (int c = 0; c < numCols; c++) {
            try {
		if (c == 0) {
		    label = new Button (statName);
		} else if (data [c - 1] == null) {
		    label = new Button ("");
		} else if (c == 1) {
		    Integer tmp = new Integer (data [c - 1].intValue ());
                    label = new Button (trunc (tmp.toString ()));
		} else {
		    label = new Button (trunc (data [c - 1].toString ()));
                }; // if
                sPanel.add (label);    
            } catch (ArrayIndexOutOfBoundsException ex) {
		ex.printStackTrace ();
	    };
        }; // for

    }; // displayRow


    /**************************************************************
     * Add statistical information to StatFrame.
     * Skip sVector.get (0) with indicates report type.
     * @param  sPanel   panel to display stat data
     * @param  sVector  all stat data for model
     */
    private void showStatResult (Panel sPanel, Object statReport)
    {
        final int   numCols  = Statistic.LABEL.length;
        int         numStats;                         // min # rows
        Button      label;
		CostReport  modelReport;
	    modelReport = (CostReport) statReport;

		numStats = modelReport.getNumOfNodes ();
		trc.info ( "numStats = " + 2 * numStats + " numCols = " + numCols);
		sPanel.setLayout (new GridLayout (0, numCols));
       
        /******************************************************
         * Add header labels to panel.
         */
        for (int c = 0; c < numCols; c++) {
                label = new Button (Statistic.LABEL [c]);
                label.setBackground (Color.pink);
                sPanel.add (label);
        }; // for

        /******************************************************
         * Add each stat to panel as a row in table.
         */
		String [] costStatName = modelReport.getCostStatName ();
		String [] costObsStatName = modelReport.getCostObsStatName ();
        Double [][] costStat = modelReport.getCostStat ();
        Double [][] costObsStat = modelReport.getCostObsStat ();
        for (int r = 0; r < numStats; r++) {

                displayRow (sPanel, costStat [r], numCols, costStatName [r]);

				if (costObsStat [r] != null)
				   displayRow (sPanel, costObsStat [r], numCols, costObsStatName [r]);

        }; // for

    }; // showStatResult


    /**************************************************************
     * Handle close event.
     * @param  evt   the input action event
     */
    public void actionPerformed (ActionEvent evt)
    {
		trc.info ( "close stat window" );
        // trc.show ("actionPerformed", "close stat window");
        dispose ();

        if (ONE_SHOT) {
            System.exit (0);
        }; // if

    }; // actionPerformed


}; // class

