/******************************************************************
 * @(#) FileDialog.java     1.3     98/4/16
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


/******************************************************************
 * Dialog box for entering a file name for the current design model.
 */

public class FileDialog extends Dialog
                        implements ActionListener
{
    //////////////////////// Constants \\\\\\\\\\\\\\\\\\\\\\\\\\\\
    private static final String  TITLE     = "Enter File Name";
    private static final int     NUM_LINES = 2;

    /////////////////// Immutable Variables \\\\\\\\\\\\\\\\\\\\\\\
    /**
     * File name label
     */
    private final Label      fileName;

    /**
     * File name value
     */
    private final TextField  fileNameV;

    /**
     * Apply button
     */
    private final Button     applyButton;

    /**
     * Cancel button
     */
    private final Button     cancelButton;

    /**
     * Associated file menu
     */
    private final FileMenu   inMenu;

	/**
	 * Unique identifier of serialized object
	 */
	private static final long serialVersionUID = 3223345456699009768L;

    /**********************************************************************
     * Construct a dialog box to update the file name of current model.
     * @param  inFrame  associated frame
     * @param  inMenu   associated file menu
     */
    public FileDialog (Frame inFrame, FileMenu inMenu)
    {
        super (inFrame, TITLE, true);

        this.inMenu = inMenu;

        setLayout (new GridLayout (NUM_LINES, 2));

        fileName  = new Label     ("fileName");
        fileNameV = new TextField (inMenu.getFileName ());

        applyButton   = new Button ("Apply");
        cancelButton  = new Button ("Cancel");

        add (fileName);     add (fileNameV);
        add (applyButton);  add (cancelButton);

        applyButton.addActionListener  (this);
        cancelButton.addActionListener (this);

        pack ();

    }; // FileDialog


    /**********************************************************************
     * Handle button events.
     *     Apply:  update the file name and then dismiss dialog box.
     *     Cancel: dismiss dialog box.
     * @param  evt  button action event
     */
    public void actionPerformed (ActionEvent evt)
    {
        if (evt.getSource () == applyButton) {
            inMenu.setFileName (fileNameV.getText ());
        }; // if

        dispose ();

    }; // actionPerformed


}; // class

