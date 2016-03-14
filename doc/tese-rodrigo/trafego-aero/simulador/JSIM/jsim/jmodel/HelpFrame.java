/******************************************************************
 * @(#) HelpFrame.java     1.3     98/4/16
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

import java.awt.* ;
import java.awt.event.* ;


/******************************************************************
 * JMODEL help frame explains how to use the designer.
 */

public class HelpFrame extends Frame
                       implements ActionListener
{
    //////////////////////// Constants \\\\\\\\\\\\\\\\\\\\\\\\\\\\

    /////////////////// Immutable Variables \\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Text area to display help information
     */
    private final TextArea  display;

    /**
     * Bottom panel
     */
    private final Panel     bottom;

    /**
     * Left label
     */
    private final Label     fillLabelLeft;

    /**
     * Right label
     */
    private final Label     fillLabelRight;

    /**
     * Dismiss button
     */
    private final Button    dismissButton;

	/**
	 * Unique identifier of serialized object
	 */
	private static final long serialVersionUID = 2742958109486094832L;

    /**************************************************************
     * Construct a help frame.
     * @param  title        help topic
     * @param  explanation  help info
     */
    public HelpFrame (String title, String explanation)
    {
        super (title);
        setLayout (new BorderLayout ());

        display = new TextArea (explanation);
        display.setEditable (false);

        bottom = new Panel ();
        bottom.setLayout (new GridLayout (1, 3));
        fillLabelLeft  = new Label ();
        dismissButton  = new Button ("Dismiss");
        fillLabelRight = new Label ();
        bottom.add (fillLabelLeft);
        bottom.add (dismissButton);
        bottom.add (fillLabelRight);

        add ("Center", display);
        add ("South",  bottom);

        dismissButton.addActionListener (this);

        pack ();
        setEnabled (true);
        setVisible (true);

    }; // HelpFrame
       

    /**************************************************************
     * Handle button event.
     * @param  evt  button action event
     */
    public void actionPerformed (ActionEvent evt)
    {
        if (evt.getSource () == dismissButton) {
            this.setVisible (false);
        }; // if

    }; // actionPerformed


}; // class

