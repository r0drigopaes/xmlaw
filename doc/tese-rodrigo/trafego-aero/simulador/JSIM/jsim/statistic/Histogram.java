/******************************************************************
 * @(#) Histogram.java     1.3     98/2/7
 * 
 * Copyright (c) 2005, Yongfu Ge, John Miller
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
 * @author      Yongfu Ge, John Miller
 */

package jsim.statistic;

import java.awt.*;
import java.awt.event.*;


/******************************************************************
 * This class implements histograms for displaying data.
 */

public class Histogram extends    Frame
                       implements WindowListener
{
    //////////////////////// Constants \\\\\\\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Bar title space
     */
    private final static int    SPACING     = 20; 

    /**
     * Border space
     */
    private final static int    BORDER      = 30;

    /**
     * Bar color
     */
    private final static Color  BAR_COLOR   = Color.blue;

    /**
     * Label color
     */
    private final static Color  LABEL_COLOR = Color.black;

    /**
     * Frame background color
     */
    private final static Color  F_BG_COLOR  = new Color (160, 160, 160);

    /**
     * Frame foreground color
     */
    private final static Color  F_FG_COLOR  = new Color (100, 100, 100);

    /**
     * Frame top x coordinate
     */
    private final static int    F_TOP_X     = 100;

    /**
     * Frame top y coordinate
     */
    private final static int    F_TOP_Y     = 140;

    /**
     * Frame width
     */
    private final static int    F_WIDTH     = 700;

    /**
     * Frame height 
     */
    private final static int    F_HEIGHT    = 500;

    /**
     * Maximum label width
     */
    private static final int    MAX_LABEL_WIDTH = 7;

    /////////////////// Immutable Variables \\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Array of data values used as input for producing histogram
     */
    private final double []    value;

    /**
     * Number of data values
     */
    private final int          numValues;

    /**
     * Width of intervals in histogram
     */
    private final double       intervalWidth;

    /**
     * Array of histogram counters (# data values within intervals)
     */
    private final int []       histo;

    /**
     * Maximum histogram counter value
     */
    private final int          maxHisto;

    /**
     * Title string
     */
    private final String       title;

    /**
     * Title font
     */
    private final Font         titleFont;

    /**
     * Title font metrics
     */
    private final FontMetrics  titleFontMetrics;

    /**
     * Instance of Extreme to record minimum and maximum data values
     */
    private final Extreme      extreme;

    //////////////////////// Variables \\\\\\\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Number of intervals
     */
    private int  numIntervals = 0;

	/**
	 * Unique identifier of serialized object
	 */
	private static final long serialVersionUID = 5432126598786565456L;

    /************************************************************
     * Construct a histogram with two parameters.
     * @param value          an array of double values
     * @param intervalWidth  the width of interval for grouping
     */
    public Histogram (double [] value, double intervalWidth) 
    {
        this (value, intervalWidth, "Histogram");

    }; // Histogram


    /************************************************************
     * Construct a histogram with three parameters.
     * @param value          an array of double values
     * @param intervalWidth  the width of interval for grouping
     * @param title          the type of a random number generator
     */  
    public Histogram (double [] value, double intervalWidth, String title) 
    {
        super (title);

        this.value         = value;
        numValues          = value.length;
        this.intervalWidth = intervalWidth;
        this.title         = title;

        titleFont          = new Font ("Courier", Font.BOLD, 12);
        titleFontMetrics   = getFontMetrics (titleFont);
        extreme            = new Extreme ();    

        setBackground (F_BG_COLOR);
        setSize       (F_WIDTH, F_HEIGHT);
        setLocation   (F_TOP_X, F_TOP_Y);

        findExtremes ();   // of data values
     
        System.out.println ("------------------------------");
        System.out.println ("numValues     = " + numValues);
        System.out.println ("intervalWidth = " + intervalWidth);
        System.out.println ("extreme.max   = " + extreme.max);
        System.out.println ("extreme.min   = " + extreme.min);
        System.out.println ("------------------------------");
     
        /****************************************************************
         * Determine the number of intervals in histogram.
         */
        numIntervals = (int) ((extreme.max - extreme.min) / intervalWidth);
     
        // set the max intervals to be available pixels in height.
        int tmpI = F_HEIGHT - (2 * BORDER + SPACING + titleFont.getSize ()) - 10;
     
        System.out.println ("Original numIntervals = " + numIntervals);
     
        if (numIntervals <= 0 || numIntervals > tmpI) {
            numIntervals = tmpI;
            intervalWidth = (extreme.max - extreme.min) / (double) numIntervals;
            System.out.println ("Adjusted numIntervals = " + numIntervals);
        } // if
     
        /****************************************************************
         * Allocate histogram and initialize its counters to zero.
         */
        histo = new int [numIntervals + 1];
        for (int j = 0; j <= numIntervals; j++) {
            histo [j] = 0;
        }; // for

        /****************************************************************
         * Loop through data values incrementing histo counters.
         */
        int index;
        for (int i = 0; i < numValues; i++) {
            index = (int) ((value [i] - extreme.min) / intervalWidth);
            histo [index]++;
        }; // for

        maxHisto = findMax ();      // max counter in histogram

        addWindowListener (this);   // listener to close window

        // show (); the show method was deprecated as of jdk 1.5
		setVisible(true);

    }; // Histogram

 
    /************************************************************
     * Find the maximum value in the histogram array.
     * @return  int  maximum value of counter in histogram
     */  
    private int findMax ()
    { 
        int max = 0;
        for (int j = 0; j < numIntervals; j++) {
            if (histo [j] > max )  max = histo [j];
        }; //for 
        return max;
 
    }; // findMax
 

    /************************************************************
     * Find the extreme values in the value array.
     */
    private void findExtremes ()
    {
        for (int i = 0; i < numValues; i++) {
            if (value [i] > extreme.max)  extreme.max = value [i];
            if (value [i] < extreme.min)  extreme.min = value [i];
        }; //for

    }; // findExtremes


    /************************************************************
     * Draw the histogram in horizontal orientation
     * @param  g   the input graphics
     */
    public void paint (Graphics g) 
    {
        int    i;
        int    factor = 1;
        int    flag   = 0;    
        int    cx     = 0;
        int    cy     = 0;
        String trunc;
                 
        int barWidth = titleFont.getSize ();

        double scale = (double) (F_WIDTH - MAX_LABEL_WIDTH - SPACING - 2 * BORDER)
                       / (double) maxHisto;

        // Adjuat the bar width and/or the interval number  
        int tmp = barWidth * (numIntervals + 1) + 2 * BORDER + SPACING +
                          titleFont.getSize ();

        if (tmp < F_HEIGHT - 10) {
            barWidth = (F_HEIGHT  - 2 * BORDER - SPACING -
                        titleFont.getSize () ) / (numIntervals + 1);
        } else if (tmp > F_HEIGHT - 10) {
            factor = tmp / F_HEIGHT + 1;
            barWidth = (F_HEIGHT - 2 * BORDER - SPACING -
                        titleFont.getSize () ) / (numIntervals + 1);
        }; // if
        
        String tmpS = new String ("Range = " + Double.toString (extreme.min)
                          + " - " + Double.toString (extreme.max)  
                          + ", Max Weight = " + Integer.toString (maxHisto) );
                
        // draw the title centered at the bottom of the histigram
        g.setColor (LABEL_COLOR);
        String num = new String ("(n = " + Integer.toString (numValues) + ")");
        String newTitle1 = title.concat (num);
        
        g.setFont (titleFont);
        //display min and max values, and max weight
        i = titleFontMetrics.stringWidth (tmpS);
        g.drawString (tmpS, Math.max ((F_WIDTH - i) / 2, 0),
                      F_HEIGHT - titleFontMetrics.getDescent () - 10);
        i = titleFontMetrics.stringWidth (newTitle1);
        g.drawString (newTitle1, Math.max ((F_WIDTH - i) / 2, 0),
                      F_HEIGHT - titleFontMetrics.getDescent () - BORDER);
                    
        for (int ii = 0; ii <= numIntervals; ii++) {
    
            // set the Y coordinate
            cy = F_HEIGHT - barWidth * (ii + 1) - SPACING - BORDER
                            - titleFont.getSize ();
            // set the X coordinate to be the widest label
            cx = MAX_LABEL_WIDTH + 1;
            cx += Math.max ((F_WIDTH - (MAX_LABEL_WIDTH + 1 +
                  titleFontMetrics.stringWidth ("" + Integer.toString(maxHisto)) +
                                               (int) (maxHisto * scale))) / 2, 0);

             // draw the labels
             g.setColor (LABEL_COLOR);
             trunc = new String (Double.toString (extreme.min + ii * intervalWidth));
             if (flag == 0)                
                 g.drawString ( (trunc.length() > 6) ? trunc.substring (0, 6) : trunc,
                               cx - MAX_LABEL_WIDTH - 1,
                               cy + titleFontMetrics.getAscent () + barWidth / 2);
    

              // draw the bar in the current color
              g.setColor (BAR_COLOR);
              g.fillRect (cx, cy, (int) (histo[ii] * scale), barWidth);
                           
              g.setColor(LABEL_COLOR);

              // Draw the label of weight 
              if (flag == 0) {
                  g.drawString (" " + histo[ii],
                                cx + (int)(histo[ii] * scale) + 3,
                                cy + titleFontMetrics.getAscent());
              }; // if
                             
              flag++;
              flag = flag % factor;

        }; //for
        
        // draw  the last y-axis label
        g.setColor(LABEL_COLOR);                
        trunc = new String(Double.toString(extreme.max));
                
        g.drawString ((trunc.length() > 6) ? trunc.substring (0, 6) : trunc,     
                      cx - MAX_LABEL_WIDTH - 1,
                      cy + titleFontMetrics.getAscent() - barWidth/2);
                 
    }; // paint


    /************************************************************
     * Window handler
     * @param e  the input window event
     */ 
    public void windowClosing (WindowEvent e)
    {
        dispose ();

    }; // windowClosing


    /************************************************************
     * Window handler
     * @param e  the input window event
     */
    public void windowOpened (WindowEvent e) { };


    /************************************************************
     * Window handler
     * @param e  the input window event
     */ 
    public void windowClosed (WindowEvent e) { };


    /************************************************************
     * Window handler
     * @param e  the input window event
     */ 
    public void windowIconified (WindowEvent e) { };


    /************************************************************
     * Window handler
     * @param e  the input window event
     */ 
    public void windowDeiconified (WindowEvent e) { };


    /************************************************************
     * Window handler
     * @param e  the input window event
     */ 
    public void windowActivated (WindowEvent e) { };


    /************************************************************
     * Window handler
     * @param e  the input window event
     */ 
    public void windowDeactivated (WindowEvent e) { };


}; // class

